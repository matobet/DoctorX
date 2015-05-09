package com.github.matobet.doctor.document;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import com.github.matobet.doctor.BaseMongoService;
import com.github.matobet.doctor.exception.ApplicationException;
import com.github.matobet.doctor.exception.BadRequestException;
import com.github.matobet.doctor.exception.NotFoundException;
import com.github.matobet.doctor.push.PushService;
import io.vertx.ext.mongo.FindOptions;

public class DocumentServiceImpl extends BaseMongoService implements DocumentService {

    private PushService pushService;

    public DocumentServiceImpl(Vertx vertx) {
        super(vertx);

        pushService = PushService.createProxy(vertx);
    }

    @Override
    public void getAll(String name, Handler<AsyncResult<JsonArray>> resultHandler) {
        mongoClient.findWithOptions(name,
                new JsonObject(),
                new FindOptions().setFields(new JsonObject().put("_id", 0)), res -> {
                    resultHandler.handle(Future.succeededFuture(new JsonArray(res.result())));
                });
    }

    @Override
    public void getOne(String name, String id, Handler<AsyncResult<JsonObject>> resultHandler) {
        mongoClient.findWithOptions(name,
                new JsonObject().put("id", id),
                new FindOptions().setFields(new JsonObject().put("_id", 0)), res -> {
                    if (res.result().size() == 0) {
                        resultHandler.handle(Future.failedFuture(new NotFoundException()));
                    } else {
                        resultHandler.handle(Future.succeededFuture(res.result().get(0)));
                    }
                });
    }

    @Override
    public void createOne(String name, JsonObject document, Handler<AsyncResult<Void>> resultHandler) {
        withExceptions(resultHandler, () -> {
            checkId(document);
            String id = document.getString("id");
            getOne(name, id, result -> {
                if (result.succeeded()) {
                    replaceOne(name, id, document, resultHandler);
                } else {
                    pushService.pushCreate(name, id);
                    mongoClient.insert(name, document, (Handler) resultHandler);
                }
            });
        });
    }

    @Override
    public void replaceAll(String name, JsonArray objects, Handler<AsyncResult<Void>> resultHandler) {

    }

    @Override
    public void replaceOne(String name, String id, JsonObject document, Handler<AsyncResult<Void>> resultHandler) {
        withExceptions(resultHandler, () -> {
            checkId(document);
            checkIdMatches(document, id);

            getOne(name, id, result -> {
                if (result.failed()) {
                    createOne(name, document, resultHandler);
                } else {
                    mongoClient.update(name, new JsonObject().put("id", id), document, resultHandler);
                }
            });
        });
    }

    @Override
    public void deleteAll(String name, Handler<AsyncResult<Void>> resultHandler) {
        mongoClient.findWithOptions(name,
                new JsonObject(),
                new FindOptions().setFields(new JsonObject().put("id", 1).put("_id", 0)), result -> {
                    result.result()
                            .stream()
                            .map(document -> document.getString("id"))
                            .forEach(id -> pushService.pushDelete(name, id));

                    mongoClient.dropCollection(name, resultHandler);
                });
    }

    @Override
    public void deleteOne(String name, String id, Handler<AsyncResult<Void>> resultHandler) {
        getOne(name, id, result -> {
            if (result.failed()) {
                resultHandler.handle(Future.failedFuture(new NotFoundException("Cannot delete non-existing document.")));
            } else {
                pushService.pushDelete(name, id);
                mongoClient.remove(name, new JsonObject().put("id", id), resultHandler);
            }
        });
    }

    private <T> void withExceptions(Handler<AsyncResult<T>> resultHandler, Runnable code) {
        try {
            code.run();
        } catch (ApplicationException e) {
            resultHandler.handle(Future.failedFuture(e));
        }
    }

    private void checkId(JsonObject document) {
        Object id = document.getValue("id");
        if (id == null || !(id instanceof String)) {
            throw new BadRequestException("Document needs to contain the 'id' attribute of type 'string'.");
        }
    }

    private void checkIdMatches(JsonObject document, String id) {
        if (!document.getString("id").equals(id)) {
            throw new BadRequestException("Document id must match last segment of document URL.");
        }
    }
}

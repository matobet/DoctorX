package com.github.matobet.doctor;

import com.github.matobet.doctor.exception.ApplicationException;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import com.github.matobet.doctor.document.DocumentService;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestVerticle extends AbstractVerticle {

    private DocumentService documentService;

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.get("/entities/:name").handler(this::handleGetAll);
        router.get("/entities/:name/:id").handler(this::handleGet);
        router.post("/entities/:name").handler(this::handleCreate);
        router.put("/entities/:name").handler(this::handleReplaceAll);
        router.put("/entities/:name/:id").handler(this::handleReplace);
        router.delete("/entities/:name").handler(this::handleDeleteAll);
        router.delete("/entities/:name/:id").handler(this::handleDelete);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

        System.out.println("Deployed DoctorX Rest Api");

        documentService = DocumentService.createProxy(vertx);
    }

    private void handleGetAll(RoutingContext context) {
        String name = context.request().getParam("name");

        documentService.getAll(name, wrapResult(context));
    }

    private void handleGet(RoutingContext context) {
        String name = context.request().getParam("name");
        String id = context.request().getParam("id");

        documentService.getOne(name, id, wrapResult(context));
    }

    private void handleCreate(RoutingContext context) {
        String name = context.request().getParam("name");

        documentService.createOne(name, context.getBodyAsJson(), wrapResult(context));
    }

    private void handleReplaceAll(RoutingContext context) {
        String name = context.request().getParam("name");

        documentService.replaceAll(name, new JsonArray(context.getBodyAsString()), wrapResult(context));
    }

    private void handleReplace(RoutingContext context) {
        String name = context.request().getParam("name");
        String id = context.request().getParam("id");

        documentService.replaceOne(name, id, context.getBodyAsJson(), wrapResult(context));
    }

    private void handleDeleteAll(RoutingContext context) {
        String name = context.request().getParam("name");

        documentService.deleteAll(name, wrapResult(context));
    }

    private void handleDelete(RoutingContext context) {
        String name = context.request().getParam("name");
        String id = context.request().getParam("id");

        documentService.deleteOne(name, id, wrapResult(context));

    }

    private <T> Handler<AsyncResult<T>> wrapResult(RoutingContext context) {
        return result -> {
            returnResult(context, result);
        };
    }

    private <T> void returnResult(RoutingContext context, AsyncResult<T> result) {
        if (result.failed()) {
            int statusCode = getStatusCode(result);
            context.response()
                    .setStatusCode(statusCode)
                    .end(result.cause().getMessage());
        } else {
            T value = result.result();
            boolean isObject = value instanceof JsonObject;
            boolean isArray = value instanceof JsonArray;
            if (isObject || isArray) {
                String output = isObject ? ((JsonObject) value).encode() : ((JsonArray) value).encode();
                context.response().putHeader("Content-Type", "application/json").end(output);
            } else {
                context.response().end("OK");
            }
        }
    }

    private <T> int getStatusCode(AsyncResult<T> result) {
        if (result.cause() instanceof ApplicationException) {
            return ((ApplicationException) result.cause()).getStatusCode();
        } else {
            return 500;
        }
    }
}

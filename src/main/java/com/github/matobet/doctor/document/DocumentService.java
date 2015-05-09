package com.github.matobet.doctor.document;

import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

@ProxyGen
public interface DocumentService {

    String ADDRESS = "document";

    static DocumentService create(Vertx vertx) {
        return new DocumentServiceImpl(vertx);
    }

    static void deploy(Vertx vertx) {
        ProxyHelper.registerService(DocumentService.class, vertx, create(vertx), ADDRESS);
    }

    static DocumentService createProxy(Vertx vertx) {
        return ProxyHelper.createProxy(DocumentService.class, vertx, ADDRESS);
    }

    void getAll(String name, Handler<AsyncResult<JsonArray>> resultHandler);

    void getOne(String name, String id, Handler<AsyncResult<JsonObject>> resultHandler);

    void createOne(String name, JsonObject document, Handler<AsyncResult<Void>> resultHandler);

    void replaceAll(String name, JsonArray objects, Handler<AsyncResult<Void>> resultHandler);

    void replaceOne(String name, String id, JsonObject document, Handler<AsyncResult<Void>> resultHandler);

    void deleteAll(String name, Handler<AsyncResult<Void>> resultHandler);

    void deleteOne(String name, String id, Handler<AsyncResult<Void>> resultHandler);

    @ProxyClose
    void close();
}

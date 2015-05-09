package com.github.matobet.doctor.session;

import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ProxyHelper;

@ProxyGen
public interface SessionService {

    String ADDRESS = "session";

    static SessionService create(Vertx vertx) {
        return new SessionServiceImpl(vertx);
    }

    static void deploy(Vertx vertx) {
        ProxyHelper.registerService(SessionService.class, vertx, create(vertx), ADDRESS);
    }

    static SessionService createProxy(Vertx vertx, String address) {
        return ProxyHelper.createProxy(SessionService.class, vertx, address);
    }

    void getAll(Handler<AsyncResult<JsonArray>> resultHandler);

    void getOne(Handler<AsyncResult<JsonObject>> resultHandler);

    @ProxyClose
    void close();
}

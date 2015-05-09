package com.github.matobet.doctor.session;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonArray;
import com.github.matobet.doctor.BaseMongoService;
import io.vertx.core.json.JsonObject;

public class SessionServiceImpl extends BaseMongoService implements SessionService {

    public SessionServiceImpl(Vertx vertx) {
        super(vertx);
    }

    @Override
    public void getAll(Handler<AsyncResult<JsonArray>> resultHandler) {

    }

    @Override
    public void getOne(Handler<AsyncResult<JsonObject>> resultHandler) {

    }
}

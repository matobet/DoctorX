package com.github.matobet.doctor;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;

public abstract class BaseMongoService {

    protected final MongoClient mongoClient;

    public BaseMongoService(Vertx vertx) {
        mongoClient = MongoClient.createShared(vertx, new JsonObject()
                .put("db_name", "doctor"));
    }

    public void close() {
        mongoClient.close();
    }
}

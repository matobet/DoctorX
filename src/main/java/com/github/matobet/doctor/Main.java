package com.github.matobet.doctor;

import io.vertx.core.Vertx;
import com.github.matobet.doctor.document.DocumentService;
import com.github.matobet.doctor.push.PushService;

public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();

        vertx.deployVerticle(new RestVerticle());

        DocumentService.deploy(vertx);
        PushService.deploy(vertx);
    }
}

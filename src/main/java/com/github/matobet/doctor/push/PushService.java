package com.github.matobet.doctor.push;

import io.vertx.codegen.annotations.ProxyClose;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.core.Vertx;
import io.vertx.serviceproxy.ProxyHelper;

import java.util.List;

@ProxyGen
public interface PushService {

    String ADDRESS = "push";

    static PushService create() {
        return new PushServiceImpl();
    }

    static void deploy(Vertx vertx) {
        ProxyHelper.registerService(PushService.class, vertx, create(), ADDRESS);
    }

    static PushService createProxy(Vertx vertx) {
        return ProxyHelper.createProxy(PushService.class, vertx, ADDRESS);
    }

    void pushCreate(String name, String id);

    void pushUpdate(String name, String id, List<String> fields);

    void pushDelete(String name, String id);

    @ProxyClose
    void close();
}

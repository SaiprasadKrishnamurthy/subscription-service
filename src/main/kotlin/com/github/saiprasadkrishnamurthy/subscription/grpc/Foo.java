package com.github.saiprasadkrishnamurthy.subscription.grpc;

import net.jodah.expiringmap.ExpiringMap;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Foo {
    static final ExecutorService e = Executors.newSingleThreadExecutor();

    public static void main(String[] args) throws Exception {
        final ExpiringMap<String, String> m = ExpiringMap.builder().variableExpiration().build();
        m.addExpirationListener((k, v) -> System.out.println(" Expiring " + k));
        m.put("1", "one", 3, TimeUnit.SECONDS);
        e.submit(() -> {
            for (; ; ) {
                System.out.println("[Event Loop]" + m.get("1"));
                Thread.sleep(1000);
            }
        });
        for (int i = 0; i < 2000; i++) {
            m.compute("1", (k, v) -> {
                if (v == null) {
                    return "one";
                } else {
                    return v + "+";
                }
            });
            Thread.sleep(1000);
        }
    }
}

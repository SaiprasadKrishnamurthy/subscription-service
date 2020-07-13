package com.github.saiprasadkrishnamurthy.subscription.grpc;

import com.github.saiprasadkrishnamurthy.subscription.*;
import com.github.saiprasadkrishnamurthy.subscription.SubscriptionRequest;
import com.github.saiprasadkrishnamurthy.subscription.SubscriptionScheme;
import io.grpc.CallCredentials;
import io.grpc.ConnectivityState;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.netty.shaded.io.grpc.netty.GrpcSslContexts;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.client.security.CallCredentialsHelper;

import java.io.File;


public class GrpcClient1 {
    public static void main(String[] args) throws Exception {
//        ManagedChannel channel = ManagedChannelBuilder.forAddress("localhost", 6565)
//                .usePlaintext()
//                .build();

        ManagedChannel channel = NettyChannelBuilder.forAddress("localhost", 6565)
                .sslContext(GrpcSslContexts.forClient().trustManager(new File("ca.crt")).build())
                .build();

        SubscriptionServiceGrpc.SubscriptionServiceStub helloServiceStub = SubscriptionServiceGrpc
                .newStub(channel)
                .withCallCredentials(CallCredentialsHelper.basicAuth("sai", "sai123"));

        long start = System.currentTimeMillis();
        SubscriptionRequest rq = SubscriptionRequest.newBuilder().setId("Sai")
                .setSubscriptionScheme(SubscriptionScheme.newBuilder()
                        .setSubscriptionTimeInSeconds(10).build()).setTopic("foo").build();
        helloServiceStub.subscribe(rq,
                new StreamObserver<>() {
                    @Override
                    public void onNext(SubscriptionResponse helloResponse) {
                        System.out.println(" --- " + helloResponse.getDataJson());
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        System.out.println(" ***** On Error **** "+throwable);
                    }

                    @Override
                    public void onCompleted() {
                        long end = System.currentTimeMillis();
                        System.out.println((end - start) / 1000);
                        System.exit(0);
                    }
                });
        Thread.sleep(10000000);
    }
}
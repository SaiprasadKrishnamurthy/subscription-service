package com.github.saiprasadkrishnamurthy.subscription.repository

import com.github.saiprasadkrishnamurthy.subscription.*
import com.github.saiprasadkrishnamurthy.subscription.model.Subscription
import io.grpc.stub.StreamObserver
import net.jodah.expiringmap.ExpirationPolicy
import net.jodah.expiringmap.ExpiringMap
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit


@Repository
class StreamObserverRepository(@Value("\${observersSize}") private val observersSize: Int) {
    private val map = ExpiringMap.builder()
            .maxSize(observersSize)
            .build<String, ExpiringMap<String, StreamObserver<SubscriptionResponse>>>()

    fun save(subscription: Subscription, streamObserver: StreamObserver<SubscriptionResponse>) {
        if (map.containsKey(subscription.topic)) {
            if (subscription.subscriptionScheme.subscriptionTimeInSeconds != null) {
                map[subscription.topic]!!.put(subscription.id, streamObserver, ExpirationPolicy.ACCESSED, subscription.subscriptionScheme.subscriptionTimeInSeconds, TimeUnit.SECONDS)
            } else {
                map[subscription.topic]!![subscription.id] = streamObserver
            }
        } else {
            val exm = ExpiringMap.builder()
                    .variableExpiration()
                    .build<String, StreamObserver<SubscriptionResponse>>()
            if (subscription.subscriptionScheme.subscriptionTimeInSeconds != null) {
                exm.put(subscription.id, streamObserver, ExpirationPolicy.ACCESSED, subscription.subscriptionScheme.subscriptionTimeInSeconds, TimeUnit.SECONDS)
            } else {
                exm[subscription.id] = streamObserver
            }
            exm.addExpirationListener { _, o ->
                o.onCompleted()
            }
            map[subscription.topic] = exm
        }
    }

    fun findObserversForTopic(topic: String): List<StreamObserver<SubscriptionResponse>> = map[topic]?.values?.toList()
            ?: listOf()

    fun deleteSubscribersForTopic(topic: String) {
        map[topic]?.values?.toList()?.forEach { it.onCompleted() }
        map.remove(topic)
    }
}
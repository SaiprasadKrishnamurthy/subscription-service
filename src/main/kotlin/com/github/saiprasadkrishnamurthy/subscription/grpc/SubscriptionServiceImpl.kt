package com.github.saiprasadkrishnamurthy.subscription.grpc

import com.github.saiprasadkrishnamurthy.subscription.*
import com.github.saiprasadkrishnamurthy.subscription.SubscriptionRequest
import com.github.saiprasadkrishnamurthy.subscription.model.Subscription
import com.github.saiprasadkrishnamurthy.subscription.model.SubscriptionScheme
import com.github.saiprasadkrishnamurthy.subscription.repository.StreamObserverRepository
import io.grpc.stub.StreamObserver
import net.devh.boot.grpc.server.service.GrpcService
import org.springframework.security.access.annotation.Secured
import java.util.*

@GrpcService
class SubscriptionServiceImpl(private val observerRepository: StreamObserverRepository) : SubscriptionServiceGrpc.SubscriptionServiceImplBase() {

    @Secured("ROLE_ADMIN")
    override fun subscribe(request: SubscriptionRequest, responseObserver: StreamObserver<SubscriptionResponse>) {
        observerRepository.save(Subscription(
                id = UUID.randomUUID().toString(),
                topic = request.topic,
                subscriptionScheme = SubscriptionScheme(subscriptionTimeInSeconds = request.subscriptionScheme.subscriptionTimeInSeconds,
                        numberOfMessages = request.subscriptionScheme.numberOfMessages)), responseObserver)
    }
}
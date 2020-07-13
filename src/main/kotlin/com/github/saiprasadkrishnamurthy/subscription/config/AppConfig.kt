package com.github.saiprasadkrishnamurthy.subscription.config

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.github.saiprasadkrishnamurthy.subscription.SubscriptionResponse
import com.github.saiprasadkrishnamurthy.subscription.repository.StreamObserverRepository
import io.grpc.Status
import io.grpc.StatusRuntimeException
import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*


@Configuration
class AppConfig(private val observerRepository: StreamObserverRepository) {


    @Scheduled(initialDelay = 5000, fixedDelay = 1)
    fun start() {
        observerRepository.findObserversForTopic("foo")
                .forEach {
                    try {
                        it.onNext(SubscriptionResponse.newBuilder().setDataJson(jacksonObjectMapper().writeValueAsString(mapOf("key" to UUID.randomUUID().toString()))).build())
                    } catch (ex: StatusRuntimeException) {
                        if (ex.status != Status.CANCELLED)
                            it.onCompleted()
                    } catch (ex: Exception) {
                        it.onError(Status.INTERNAL.withCause(ex).asRuntimeException())
                    }
                }
    }


}
package com.github.saiprasadkrishnamurthy.subscription

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@SpringBootApplication
class SubscriptionServiceApplication

fun main(args: Array<String>) {
	runApplication<SubscriptionServiceApplication>(*args)
}

package com.github.saiprasadkrishnamurthy.subscription.model

import java.util.*

data class Subscription(val id: String = UUID.randomUUID().toString(), val topic: String, val subscriptionScheme: SubscriptionScheme)

data class SubscriptionScheme(val subscriptionTimeInSeconds: Long?, val numberOfMessages: Long?)
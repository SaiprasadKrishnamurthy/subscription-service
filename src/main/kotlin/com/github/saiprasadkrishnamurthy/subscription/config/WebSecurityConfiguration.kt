package com.github.saiprasadkrishnamurthy.subscription.config

import net.devh.boot.grpc.server.security.authentication.BasicGrpcAuthenticationReader
import net.devh.boot.grpc.server.security.authentication.CompositeGrpcAuthenticationReader
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authentication.dao.DaoAuthenticationProvider
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.NoOpPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import java.util.*


/**
 * @author Sai.
 */
@EnableGlobalMethodSecurity(securedEnabled = true, proxyTargetClass = true)
class WebSecurityConfiguration(@Value("\${api.auth.username}") private val user: String,
                               @Value("\${api.auth.password}") private val password: String) {

    @Bean
    fun authenticationManager(daoAuthenticationProvider: DaoAuthenticationProvider): AuthenticationManager {
        val providers = ArrayList<AuthenticationProvider>()
        providers.add(daoAuthenticationProvider)
        return ProviderManager(providers);
    }

    @Bean
    fun authenticationReader(): GrpcAuthenticationReader {
        val readers = ArrayList<GrpcAuthenticationReader>()
        readers.add(BasicGrpcAuthenticationReader())
        return CompositeGrpcAuthenticationReader(readers);
    }

    @Bean
    fun daoAuthenticationProvider(passwordEncoder: PasswordEncoder, userDetailsService: UserDetailsService): DaoAuthenticationProvider {
        val daoAuthenticationProvider = DaoAuthenticationProvider()
        daoAuthenticationProvider.setPasswordEncoder(passwordEncoder)
        daoAuthenticationProvider.setUserDetailsService(userDetailsService)
        return daoAuthenticationProvider
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return NoOpPasswordEncoder.getInstance()
    }

    @Bean
    fun userdetailsService(): UserDetailsService {
        return InMemoryUserDetailsManager(User("sai", "sai123", listOf(SimpleGrantedAuthority("ROLE_ADMIN"))))
    }
}
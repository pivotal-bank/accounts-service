package io.pivotal.accounts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;

/**
 * Microservice to manage user accounts.
 * <p>
 * Spring Boot application to provide a service to manage user accounts.
 * The application registers with a registry service - Eureka.
 *
 * @author David Ferreira Pinto
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableCircuitBreaker
public class AccountsApplication {

    public static void main(String[] args) {
        SpringApplication.run(AccountsApplication.class, args);
    }

    static {
        HostnameVerifier allHostsValid = (name, sslSession) -> true;
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
    }
}

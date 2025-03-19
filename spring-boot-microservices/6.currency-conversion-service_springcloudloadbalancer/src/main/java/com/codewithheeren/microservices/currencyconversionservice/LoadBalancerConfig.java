package com.codewithheeren.microservices.currencyconversionservice;

import java.util.Arrays;
import java.util.List;

import org.springframework.cloud.client.DefaultServiceInstance;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.loadbalancer.core.ServiceInstanceListSupplier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import reactor.core.publisher.Flux;
/**
 * Manual load balancer configuration.
 * 
 * CurrencyConversionService (Microservice1) is client class which is consuming CurrencyExcehangeService (Microservice 2).
 * CurrencyConversionService having a proxy service implemented feign client to simplifies REST APIs call of existing CurrencyExcehangeService.
 * CurrencyConversionService getting rates from CurrencyExcehangeService and then calculating total conversion amount basis on quantity.
 * @author Heeren
 * @version 1.0
 */
@Configuration
public class LoadBalancerConfig {
    
    @Bean
    public ServiceInstanceListSupplier serviceInstanceListSupplier() {
        return new ServiceInstanceListSupplier() {
            @Override
            public String getServiceId() {
                return "currency-exchange-service";
            }

            @Override
            public Flux<List<ServiceInstance>> get() {
                List<ServiceInstance> instances = Arrays.asList(
                        new DefaultServiceInstance("currency-exchange-1", "currency-exchange-service", "localhost", 8000, false),
                        new DefaultServiceInstance("currency-exchange-2", "currency-exchange-service", "localhost", 8200, false)
                );
                return Flux.just(instances);
            }
        };
    }
}

package com.codewithheeren.microservices.currencyexchangeservice;

import java.math.BigDecimal;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class ExchangeValueDataLoader implements CommandLineRunner {

    private final ExchangeValueRepository repository;

    public ExchangeValueDataLoader(ExchangeValueRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) throws Exception {
        repository.save(new ExchangeValue(10001L, "USD", "INR", BigDecimal.valueOf(65)));
        repository.save(new ExchangeValue(10002L, "EUR", "INR", BigDecimal.valueOf(75)));
        repository.save(new ExchangeValue(10003L, "AUD", "INR", BigDecimal.valueOf(25)));

        System.out.println("Exchange values inserted successfully!");
    }
}

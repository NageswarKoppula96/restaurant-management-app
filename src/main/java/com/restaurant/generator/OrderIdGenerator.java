package com.restaurant.generator;

import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.io.Serializable;
import java.util.Random;

public class OrderIdGenerator implements IdentifierGenerator {
    private static final String PREFIX = "ORD";
    private static final int RANDOM_NUMBER_LENGTH = 5;
    private static final Random RANDOM = new Random();

    @Override
    public Serializable generate(SharedSessionContractImplementor session, Object object) {
        return PREFIX + generateRandomNumber();
    }

    private String generateRandomNumber() {
        // Generate a random number with RANDOM_NUMBER_LENGTH digits
        int min = (int) Math.pow(10, RANDOM_NUMBER_LENGTH - 1);
        int max = (int) Math.pow(10, RANDOM_NUMBER_LENGTH) - 1;
        int randomNumber = RANDOM.nextInt(max - min + 1) + min;
        return String.format("%0" + RANDOM_NUMBER_LENGTH + "d", randomNumber);
    }
}

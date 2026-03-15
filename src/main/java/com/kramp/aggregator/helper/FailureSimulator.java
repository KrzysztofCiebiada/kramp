package com.kramp.aggregator.helper;

import java.util.Random;

public class FailureSimulator {

    private static final Random RANDOM = new Random();

    public static void simulateLatency(int millis) {

        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static void simulateFailure(double reliability) {

        if (RANDOM.nextDouble() > reliability) {
            throw new RuntimeException("Upstream service failed");
        }
    }
}

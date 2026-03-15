package com.kramp.aggregator.helper;

import java.util.List;
import java.util.Random;

public class RandomHelper {
    private static final Random RANDOM = new Random();
    public static String randomFrom(List<String> values) {
        return values.get(RANDOM.nextInt(values.size()));
    }
}

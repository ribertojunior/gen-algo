package org.example.service;

import org.apache.commons.lang3.RandomUtils;
import org.example.model.Solution;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import static org.example.Constants.DECIMAL_PLACE;

public record SolutionListFactory(int index, double seed) implements Callable<List<Solution>> {
    public static final int SIZE = 100;
    private static int signal = 1;

    @Override
    public List<Solution> call() {
        ArrayList<Solution> pop = new ArrayList<>();
        if (seed != 0) {
            for (int i = 0; i < SIZE; i++) {
                pop.add(createSolution());
                signal = -1 * signal;
            }
        } else {
            for (int i = 0; i < SIZE; i++) {
                pop.add(createSolution(seed));
                signal = -1 * signal;
            }
        }
        return pop;
    }

    private static Solution createSolution() {
        return new Solution(Math.floor(RandomUtils.secure().randomDouble(0, 1000) * DECIMAL_PLACE * signal) / DECIMAL_PLACE, null);
    }

    private static Solution createSolution(double seed) {
        return new Solution(Math.floor(RandomUtils.secure().randomDouble(0, 1) * seed * DECIMAL_PLACE * signal) / DECIMAL_PLACE, null);
    }
}

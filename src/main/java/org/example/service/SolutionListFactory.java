package org.example.service;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.model.Solution;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;

import static org.example.Constants.DECIMAL_PLACE;
import static org.example.Constants.MUTATION_RATE;

public record SolutionListFactory(int index, Pair<Solution, Solution> seed) implements Callable<List<Solution>> {
    public static final int SIZE = 100;
    private static int signal = 1;

    @Override
    public List<Solution> call() {
        HashMap<Double, Solution> popMap = new HashMap<>();
        if (seed == null) {
            for (int i = 0; i < SIZE; i++) {
                Solution s;
                do {
                    s = createSolution();
                } while (popMap.containsKey(SolutionService.round(s.getX())));
                popMap.put(SolutionService.round(s.getX()), s);
                signal = -1 * signal;
            }
        } else {
            for (int i = 0; i < SIZE; i++) {
                Solution s;
                do {
                    s = createSolution(seed);
                } while (popMap.containsKey(SolutionService.round(s.getX())));
                popMap.put(SolutionService.round(s.getX()), s);
                signal = -1 * signal;
            }
        }
        return popMap.values().stream().toList();
    }

    private static Solution crossover(Solution parentA, Solution parentB) {
        double x = (parentA.getX() + parentB.getX()) / 2;
        x = Math.floor(x * DECIMAL_PLACE) / DECIMAL_PLACE;
        return new Solution(x, null);
    }

    private static void mutation(Solution child) {
        double random = Math.random();
        if (random < MUTATION_RATE) {
            child.setX(child.getX() + 100 * random);
        }
        if (random > 0.99) {
            child.setX(child.getX() * -1);
        }
    }

    private static Solution createSolution() {
        return new Solution(Math.floor(RandomUtils.secure().randomDouble(0, 1000) * DECIMAL_PLACE * signal) / DECIMAL_PLACE, null);
    }

    private static Solution createSolution(Pair<Solution, Solution> seed) {
        Solution child = crossover(seed.getLeft(), seed.getRight());
        mutation(child);
        return child;
    }
}

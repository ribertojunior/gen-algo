package org.example.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.example.model.Population;
import org.example.model.Quadratic;
import org.example.model.Solution;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static org.example.Constants.DECIMAL_PLACE;
import static org.example.service.SolutionListFactory.SIZE;
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SolutionService {

    public static Population generateMultiThread(int size) {
        return new Population(generateListMultiThread(size, null));
    }

    public static Population generateMultiThread(int size, Population elite) {
        return new Population(generateListMultiThread(size, elite));
    }

    public static List<Solution> generateListMultiThread(int size) {
        return generateListMultiThread(size, null);
    }

    public static List<Solution> generateListMultiThread(int size, Population seed) {
        if (size % 100 != 0) {
            System.out.println("Error size is not a multiple of 100");
            throw new RuntimeException("Error size is not a multiple of 100");
        }
        LinkedList<Solution> seedQueue = seed != null && ObjectUtils.isNotEmpty(seed.getPopulation()) ? new LinkedList<>(seed.getPopulation()) : null;
        int threadPollSize = size / SIZE;
        ArrayList<SolutionListFactory> factories = new ArrayList<>();
        for (int i = 0; i < threadPollSize; i++) {
            Pair<Solution, Solution> solutionPair = getSolutionPair(seedQueue);
            factories.add(new SolutionListFactory(i, solutionPair));
            requeueSolutions(seedQueue, solutionPair);
        }
        ArrayList<Solution> solutions = new ArrayList<>();
        List<Future<List<Solution>>> futures;
        try (ExecutorService cachedPool = Executors.newCachedThreadPool()) {
            futures = cachedPool.invokeAll(factories);
        }catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            for (Future<List<Solution>> future : futures){
                solutions.addAll(future.get());
            }
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return solutions;
    }

    private static void requeueSolutions(LinkedList<Solution> seedQueue, Pair<Solution, Solution> solutionPair) {
        if (seedQueue != null) {
            seedQueue.add(solutionPair.getLeft());
            seedQueue.add(solutionPair.getRight());
        }
    }

    private static Pair<Solution, Solution> getSolutionPair(LinkedList<Solution> seedQueue) {
        return seedQueue != null ? Pair.of(seedQueue.remove(), seedQueue.remove()) : null;
    }

    public static void fitness(Population pop, Quadratic equation) {
        for (Solution sol : pop.getPopulation()) {
            sol.setFitness(equation.evaluate(sol.getX()));
        }
    }

    public static boolean hasTwoSolutions(List<Solution> pop) {
        Map<Double, Set<Double>> isZero = new HashMap<>();
        for (Solution sol : pop) {
            if (sol.getFitness() == 0 && !containsSolution(isZero, sol)) {
                Set<Double> setX = isZero.get(sol.getFitness());
                setX = setX != null ? setX : new HashSet<>();
                setX.add(sol.getX());
                isZero.put(sol.getFitness(), setX);
                if (setX.size() == 2) {
                    break;
                }
            }
        }
        boolean hasTwo = false;
        for (Map.Entry<Double, Set<Double>> entry : isZero.entrySet()) {
            Set<Double> solutionSet = entry.getValue();
            if (solutionSet.size() > 1) {
                hasTwo = true;
                System.out.println("Solution found: " + solutionSet.stream().map(s -> String.format("%.5f ",s)).collect(Collectors.joining()));
                break;
            }
        }
        return hasTwo;
    }

    private static boolean containsSolution(Map<Double, Set<Double>> isZero, Solution sol) {
        return isZero.containsKey(sol.getFitness()) && isZero.get(sol.getFitness()).contains(round(sol.getX()));
    }

    public static double round(double x) {
        if (x >= 0) return Math.floor(x * DECIMAL_PLACE) / DECIMAL_PLACE;
        else return Math.ceil(x * DECIMAL_PLACE) / DECIMAL_PLACE;
    }
}

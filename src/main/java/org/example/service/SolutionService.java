package org.example.service;

import org.apache.commons.lang3.RandomUtils;
import org.example.model.Population;
import org.example.model.Quadratic;
import org.example.model.Solution;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SolutionService {


    public static Solution generate() {
        return new Solution(Math.floor(RandomUtils.secure().randomDouble(0, 1000) * 10000 ) / 10000, null);
    }

    public Population generate(int size) {
        return new Population(generateList(size));
    }

    public Population generateMultiThread(int size) {
        return new Population(generateListMultiThread(size, 0));
    }

    public Population generateMultiThread(int size, double seed) {
        return new Population(generateListMultiThread(size, seed));
    }

    public Population generate(int size, Population elite) {
        return new Population(generateList(size, elite));
    }

    public List<Solution> generateListMultiThread(int size, double seed) {
        if (size % 10 != 0) {
            System.out.println("Error size is not a multiple of 10");
            throw new RuntimeException("Error size is not a multiple of 10");
        }
        int threadPollSize = size / SolutionListFactory.SIZE;
        ArrayList<SolutionListFactory> factories = new ArrayList<>();
        for (int i = 0; i < threadPollSize; i++) {
            factories.add( new SolutionListFactory(i, seed));
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

    public List<Solution> generateList(int size) {
        ArrayList<Solution> pop = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pop.add(generate());
            if (i % 1000 == 0) {
                int pb = (int) ((i / (float) size) * 100);
                System.out.print( "=".repeat(pb)+ " " + pb + "%\r");
            }
        }
        return pop;
    }

    public List<Solution> generateList(int size, Population elite) {
        ArrayList<Solution> pop = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            pop.add(generate());
        }
        return pop;
    }

    public void fitness(Population pop, Quadratic equation) {
        for (Solution sol : pop.getPopulation()) {
            sol.setFitness(equation.evaluate(sol.getX()));
        }
    }

    public boolean hasTwoSolutions(List<Solution> pop) {
        Map<Double, Set<Double>> isZero = new HashMap<>();
        for (Solution sol : pop) {
            if (sol.getFitness() == 0 && !containsSolution(isZero, sol)) {
                Set<Double> setX = isZero.get(sol.getFitness());
                setX = setX != null ? setX : new HashSet<>();
                setX.add(sol.getX());
                isZero.put(sol.getFitness(), setX);
            }
        }
        boolean hasTwo = false;
        for (Map.Entry<Double, Set<Double>> entry : isZero.entrySet()) {
            Set<Double> solutionSet = entry.getValue();
            if (solutionSet.size() > 1) {
                hasTwo = true;
                break;
            }
        }
        return hasTwo;
    }

    private boolean containsSolution(Map<Double, Set<Double>> isZero, Solution sol) {
        return isZero.containsKey(sol.getFitness()) && isZero.get(sol.getFitness()).contains(sol.getX());
    }
}

package org.example;

import org.example.model.Population;
import org.example.model.Quadratic;
import org.example.model.Solution;
import org.example.service.SolutionService;

import java.util.ArrayList;
import java.util.Collections;


public class Main {
    public static void main(String[] args) {
        int eliteSize = 100;
        int populationSize = 1_000_000;
        int iteration = 0;
        Population elite = new Population(new ArrayList<>());
        Quadratic equation = new Quadratic(1, 1, 0);
        while (!SolutionService.hasTwoSolutions(elite.getPopulation())) {
            // create population
            int newPopSize = elite.getPopulation().isEmpty() ? populationSize : populationSize - eliteSize;
            Population population = SolutionService.generateMultiThread(newPopSize, elite);
            population.getPopulation().addAll(elite.getPopulation());


            // evaluate fitness
            SolutionService.fitness(population, equation);
            Collections.sort(population.getPopulation());

            // create elite
            elite.setPopulation(new ArrayList<>());
            for (int i = 0, j = 0; i < eliteSize && j < population.getPopulation().size(); j++) {
                Solution solution = population.getPopulation().get(j);
                if (solution.getFitness() >= 0 && solution.getFitness() < 1000) {
                    elite.getPopulation().add(i, solution);
                    i++;
                }
            }
            System.out.print("Iteration " + iteration++ + " Elite 10: " + printElite(elite, 10) + "\r");
        }
    }

    private static String printElite(Population elite, int limit) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < limit; i++) {
            sb.append(elite.getPopulation().get(i)).append(" ");
        }
        return sb.toString();
    }
}
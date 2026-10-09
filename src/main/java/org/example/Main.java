package org.example;

import org.example.model.Population;
import org.example.model.Quadratic;
import org.example.model.Solution;
import org.example.service.SolutionService;

import java.util.ArrayList;
import java.util.Collections;


public class Main {
    public static void main(String[] args) {
        int eliteSize = 10;
        int populationSize = 1_000_000;
        int iteration = 0;
        SolutionService service = new SolutionService();
        Population elite = new Population(new ArrayList<>());
        Quadratic equation = new Quadratic(1, 1, 0);
        while (!service.hasTwoSolutions(elite.getPopulation())) {
            // create population
            int newPopSize = elite.getPopulation().isEmpty() ? populationSize : populationSize - eliteSize;
            // System.out.print("Generating new population with size " + newPopSize + " and elite size " + elite.getPopulation().size() + "\r");
            Population population = service.generateMultiThread(newPopSize);
            population.getPopulation().addAll(elite.getPopulation());


            // evaluate fitness
            service.fitness(population, equation);
            Collections.sort(population.getPopulation());
            elite.setPopulation(new ArrayList<>());
            for (int i = 0, j = 0; i < eliteSize && j < population.getPopulation().size(); j++) {
                Solution solution = population.getPopulation().get(j);
                if (solution.getFitness() >= 0 && solution.getFitness() < 1000) {
                    elite.getPopulation().add(solution);
                    i++;
                }
            }
            System.out.print("Iteration " + iteration++ + " Elite: " + elite + "\r");
        }
    }
}
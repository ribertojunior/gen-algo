package org.example.service;


import org.example.model.Population;
import org.example.model.Quadratic;
import org.example.model.Solution;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SolutionServiceTest {

    @Test
    void givenSize_whenGenerateListIsCalled_thenAValidListIsCreated() {
        // given
        int size = 100;

        // when
        List<Solution> solutions = SolutionService.generateListMultiThread(size);

        // then
        assert size == solutions.size();
    }

    @Test
    void givenRandomGeneratedPopulation_whenFitnessIsCalculated_thenSolutionsAreUpdated() {
        // given
        Population population = SolutionService.generateMultiThread(100);
        assert population.getPopulation().stream().allMatch(solution -> null == solution.getFitness());

        // when
        SolutionService.fitness(population, new Quadratic(1,5,1));

        // then
        assert population.getPopulation().stream().allMatch(solution -> null != solution.getFitness());
    }

    @Test
    void givenPopulationWithNoValidSolution_whenHasTwoSolutionIsCalled_thenItReturnsFalse() {
        // given
        ArrayList<Solution> solutions = new ArrayList<>();
        solutions.add(new Solution(15, null));
        solutions.add(new Solution(10, null));
        Population population = new Population(solutions);

        // when
        SolutionService.fitness(population, new Quadratic(1, 1, 0));

        // then
        assert !SolutionService.hasTwoSolutions(population.getPopulation());
    }

    @Test
    void givenPopulationWithOneValidSolution_whenHasTwoSolutionIsCalled_thenItReturnsFalse() {
        // given
        ArrayList<Solution> solutions = new ArrayList<>();
        solutions.add(new Solution(-1, null));
        solutions.add(new Solution(10, null));
        Population population = new Population(solutions);

        // when
        SolutionService.fitness(population, new Quadratic(1, 1, 0));

        // then
        assert !SolutionService.hasTwoSolutions(population.getPopulation());
    }

    @Test
    void givenPopulationWithTwoValidSolutions_whenHasTwoSolutionIsCalled_thenItReturnsTrue() {
        // given
        ArrayList<Solution> solutions = new ArrayList<>();
        solutions.add(new Solution(-1, null));
        solutions.add(new Solution(0, null));
        Population population = new Population(solutions);

        // when
        SolutionService.fitness(population, new Quadratic(1, 1, 0));

        // then
        assert SolutionService.hasTwoSolutions(population.getPopulation());
    }

    @Test
    void givenValidSize_whenMultiThreadPopulationGenerationIsCalled_thenAValidPopulationIsReturned() {
        // given
        int size = 500;

        // when
        List<Solution> solutions = SolutionService.generateListMultiThread(size);

        // then
        assert size == solutions.size();
    }

    @Test
    void givenInvalidSize_whenMultiThreadPopulationGenerationIsCalled_thenErrorIsThrown() {
        // given
        int size = 501;

        // when
        // then
        assertThrows(RuntimeException.class, () -> SolutionService.generateListMultiThread(size));
    }

    @Test
    void givenElite_whenPopulationGenerationIsCalled_thenAValidPopulationIsReturned() {
        // given
        int size = 100;
        Solution a = new Solution(1, 1D);
        Solution b = new Solution(2, -1D);
        Population elite = new Population(List.of(a, b));

        // when
        List<Solution> solutions = SolutionService.generateListMultiThread(size, elite);

        // then
        assert size == solutions.size();
        assert solutions.stream().allMatch(s -> s != null && s.getX() != 0 && s.getFitness() == null);
    }

}
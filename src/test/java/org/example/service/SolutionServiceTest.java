package org.example.service;


import org.example.model.Population;
import org.example.model.Quadratic;
import org.example.model.Solution;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;

class SolutionServiceTest {

    public final SolutionService service = new SolutionService();

    @Test
    void givenSize_whenGenerateListIsCalled_thenAValidListIsCreated() {
        // given
        int size = 12;

        // when
        List<Solution> solutions = service.generateList(size);

        // then
        assert size == solutions.size();
    }

    @Test
    void givenRandomGeneratedPopulation_whenFitnessIsCalculated_thenSolutionsAreUpdated() {
        // given
        Population population = service.generate(100);
        assert population.getPopulation().stream().allMatch(solution -> null == solution.getFitness());

        // when
        service.fitness(population, new Quadratic(1,5,1));

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
        service.fitness(population, new Quadratic(1, 1, 0));

        // then
        assert !service.hasTwoSolutions(population.getPopulation());
    }

    @Test
    void givenPopulationWithOneValidSolution_whenHasTwoSolutionIsCalled_thenItReturnsFalse() {
        // given
        ArrayList<Solution> solutions = new ArrayList<>();
        solutions.add(new Solution(-1, null));
        solutions.add(new Solution(10, null));
        Population population = new Population(solutions);

        // when
        service.fitness(population, new Quadratic(1, 1, 0));

        // then
        assert !service.hasTwoSolutions(population.getPopulation());
    }

    @Test
    void givenPopulationWithTwoValidSolutions_whenHasTwoSolutionIsCalled_thenItReturnsTrue() {
        // given
        ArrayList<Solution> solutions = new ArrayList<>();
        solutions.add(new Solution(-1, null));
        solutions.add(new Solution(0, null));
        Population population = new Population(solutions);

        // when
        service.fitness(population, new Quadratic(1, 1, 0));

        // then
        assert service.hasTwoSolutions(population.getPopulation());
    }

    @Test
    void givenValidSize_whenMultiThreadPopulationGenerationIsCalled_thenAValidPopulationIsReturned() {
        // given
        int size = 500;

        // when
        List<Solution> solutions = service.generateListMultiThread(size);

        // then
        assert size == solutions.size();
    }

    @Test
    void givenInvalidSize_whenMultiThreadPopulationGenerationIsCalled_thenErrorIsThrown() {
        // given
        int size = 501;

        // when
        // then
        assertThrows(RuntimeException.class, () -> service.generateListMultiThread(size));
    }

}
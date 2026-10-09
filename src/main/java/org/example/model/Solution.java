package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Solution implements Comparable<Solution> {
    private double x;
    private Double fitness;

    @Override
    public int compareTo(Solution sol) {
        return Double.compare(getFitness(), sol.getFitness());
    }
}

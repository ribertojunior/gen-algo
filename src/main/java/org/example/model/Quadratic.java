package org.example.model;

import static org.example.Constants.DECIMAL_PLACE;

public record Quadratic(double a, double b, double c) {
    public boolean isSolution(double x) {
        return evaluate(x) == 0;
    }

    public double evaluate(double x) {
        double value = Math.pow(x, 2) * a + x * b + c;
        if (value >= 0) {
            value = Math.floor(value * DECIMAL_PLACE) / DECIMAL_PLACE;
        } else {
            value = Math.ceil(value * DECIMAL_PLACE) / DECIMAL_PLACE ;
        }

        return value;
    }
}

package org.example.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public final class Population {
    private List<Solution> population;

    @Override
    public String toString() {
        if (population == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (Solution sol : population) {
            sb.append(sol).append("\n");
        }
        return sb.toString();
    }

}

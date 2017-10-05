package com.blazebit.validation.constraint;

public enum PopulationMode {
    NONE, ALL, FIRST, LAST;

    public boolean includes(PopulationMode mode) {
        return this == mode || this == ALL;
    }
}
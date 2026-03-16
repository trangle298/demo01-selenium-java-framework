package config.enums;

import java.util.Arrays;

public enum RunOn {

    LOCAL("local"),
    GRID("grid"),
    PERFECTO("perfecto"),
    AWS("aws"),
    INVALID("Invalid run mode");

    private final String name;

    RunOn(String name) {
        this.name = name;
    }

    public static RunOn fromName(String name) {
        return Arrays.stream(RunOn.values())
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(INVALID);
    }

    public String getName() {
        return name;
    }
}

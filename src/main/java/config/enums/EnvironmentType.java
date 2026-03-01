package config.enums;

import java.util.Arrays;

public enum EnvironmentType {
    QA("qa"),
    DEV("dev"),
    STAGING("staging"),
    PRODUCTION("production"),
    PERF("perf"),
    TRAINING("training"),
    HOTFIX("hotfix"),
    INVALID("invalid");

    private final String name;

    EnvironmentType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public static EnvironmentType fromName(String name) {
        return Arrays.stream(EnvironmentType.values())
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(EnvironmentType.INVALID);
    }
}

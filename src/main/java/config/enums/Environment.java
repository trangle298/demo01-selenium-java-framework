package config.enums;

import java.util.Arrays;

public enum Environment {
    QA("qa"),
    QA1("qa1"),
    QA2("qa2"),
    DEV("dev"),
    APPSTEAM1("appsteam1"),
    APPSTEAM2("appsteam2"),
    DATATEAM1("datateam1"),
    DATATEAM2("datateam2"),
    SKYTEAM1("skyteam1"),
    SKYTEAM2("skyteam2"),
    STAGE("stage"),
    STAGE1("stage1"),
    STAGE2("stage2"),
    LARGEPERF1("largeperf1"),
    LARGEPERF2("largeperf2"),
    MEDIUMPERF1("mediumperf1"),
    MEDIUMPERF2("mediumperf2"),
    HOTFIX("hotfix"),
    TRAINING("training"),
    PRODUCTION("production"),
    INVALID("invalid");

    private final String name;

    Environment(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public EnvironmentType getEnvironmentType() {
        if (name.startsWith("qa"))
            return EnvironmentType.QA;

        if (name.startsWith("dev") || name.startsWith("appsteam") || name.startsWith("skyteam"))
            return EnvironmentType.DEV;

        if (name.startsWith("stage"))
            return EnvironmentType.STAGING;

        if (name.contains("perf"))
            return EnvironmentType.PERF;

        if (name.startsWith("production"))
            return EnvironmentType.PRODUCTION;

        if (name.startsWith("training"))
            return EnvironmentType.TRAINING;

        if (name.startsWith("hotfix"))
            return EnvironmentType.HOTFIX;

        return EnvironmentType.INVALID;
    }

    public static Environment fromName(String name) {
        return Arrays.stream(Environment.values())
                .filter(x -> x.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(Environment.INVALID);
    }
}

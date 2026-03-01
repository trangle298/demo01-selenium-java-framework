package config.enums;

import java.util.Arrays;

public enum Browser {
    CHROME("chrome"),
    FIREFOX("firefox"),
    EDGE("edge"),
    SAFARI("safari");

    private final String name;

    Browser(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static Browser fromName(String name) {
        return Arrays.stream(values())
                .filter(b -> b.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported browser: " + name));
    }
}

package config.enums;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    private static final Logger LOG = LogManager.getLogger(Browser.class);

    public static Browser fromName(String name) {
        if (name == null || name.trim().isEmpty()) {
            LOG.warn("Browser name is null or empty, defaulting to CHROME");
            return CHROME;
        }
        return Arrays.stream(values())
                .filter(b -> b.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported browser: " + name));
    }
}

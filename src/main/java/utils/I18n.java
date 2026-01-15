package utils;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * Small helper to centralize ResourceBundle loading with UTF-8 support.
 */
public final class I18n {
    private I18n() {}

    public static ResourceBundle getBundle(String baseName, Locale locale) {
        return ResourceBundle.getBundle(baseName, locale, new UTF8Control());
    }

    public static ResourceBundle getBundle(String baseName) {
        return getBundle(baseName, Locale.getDefault());
    }
}


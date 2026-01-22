package helpers.providers;

import model.ui.MovieDropdownFields;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import utils.I18n;

import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * Centralized message helper for loading localized strings from message bundles.
 * Provides type-safe methods for all expected message keys used in tests.
 * This eliminates magic strings and provides autocomplete/refactoring support.
 */
public class MessagesProvider {

    private static final Logger LOG = LogManager.getLogger(MessagesProvider.class);
    private static final Locale DEFAULT_LOCALE = Locale.forLanguageTag("vi-VN");
    private static final ResourceBundle BUNDLE = I18n.getBundle("messages_vi", DEFAULT_LOCALE);

    private MessagesProvider() {
        // utility class, no instantiation
    }

    // --------------------------
    // Auth Validation Messages
    // --------------------------
    public static String getRequiredFieldError() {
        return getString("auth.validation.required");
    }

    public static String getPasswordMinLengthError() {
        return getString("auth.validation.password.minLength");
    }

    public static String getNameContainsNumberError() {
        return getString("auth.validation.name.hasNumber");
    }

    public static String getPasswordMismatchError() {
        return getString("auth.validation.confirmPassword.mismatch");
    }

    // --------------------------
    // Login Messages
    // --------------------------
    public static String getLoginSuccessMessage() {
        return getString("login.success.alert");
    }

    public static String getLoginErrorMessage() {
        return getString("login.error.alert");
    }

    // --------------------------
    // Register Messages
    // --------------------------
    public static String getRegisterSuccessMessage() {
        return getString("register.success.alert");
    }

    public static String getRegisterExistingUsernameError() {
        return getString("register.error.existUsername");
    }

    public static String getRegisterExistingEmailError() {
        return getString("register.error.existEmail");
    }

    // --------------------------
    // Account Update Messages
    // --------------------------
    public static String getAccountUpdateSuccessMessage() {
        return getString("account.success.alert");
    }

    public static String getAccountUpdateExistingEmailError() {
        return getString("account.error.existEmail");
    }

    public static String getAccountPhoneNrRequiredError() {
        return getString("account.validation.phone.required");
    }

    public static String getPhoneInvalidError() {
        return getString("account.validation.phone.invalid");
    }

    // --------------------------
    // Booking Messages
    // --------------------------
    public static String getBookingSuccessMessage() { return getString("booking.success.alert"); }

    public static String getNoSeatSelectedError() { return getString("booking.error.noSeatSelected"); }

    public static String getUnauthenticatedBookingError() { return getString("booking.error.unauthenticated"); }

    // --------------------------
    // Dropdown Filters Error Messages
    // --------------------------
    public static String getMissingFilterError(MovieDropdownFields fieldName) {
        return getString("filter.missing." + fieldName.name().toLowerCase());
    }

    // --------------------------
    // Generic getString (fallback for dynamic keys if needed)
    // --------------------------
    /**
     * Get a message by key. Prefer using the specific typed methods above.
     * @param key the message key
     * @return the localized message, or the key itself if not found
     */
    public static String getString(String key) {
        try {
            return BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            // Return key as fallback so tests show what was expected
            LOG.info("Can not find message for key '{}'. Returning key as fallback.", key);
            return key;
        }
    }
}


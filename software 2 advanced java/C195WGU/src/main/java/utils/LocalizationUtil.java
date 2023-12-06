package utils;

import java.time.ZoneId;
import java.util.Locale;
import java.util.ResourceBundle;

public class LocalizationUtil {
    private static Locale currentLocale;

    public static void setupLocale() {
        currentLocale = Locale.getDefault();
    }

    public static boolean isFrench() {
        return currentLocale.getLanguage().equals("fr");
    }

    public static String getLocalizedString(String key) {
        ResourceBundle resourceBundle = ResourceBundle.getBundle("i18n.Bundle", currentLocale);
        return resourceBundle.getString(key);
    }

    public static String getUserLocation() {
        // Return the user's location based on their system's default ZoneId.
        // For simplicity, this example will return the ZoneId directly.
        return ZoneId.systemDefault().toString();
    }
}

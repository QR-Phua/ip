package alphaone.util;

/**
 * Application-wide constants shared across model, parser and storage layers.
 */
public final class Constants {
    /** Separator token used when serialising tasks and contacts to the storage files. */
    public static final String STORAGE_SEPARATOR = "!@!";

    /** Date-time pattern expected for event inputs (e.g. 2025-01-31 1400). */
    public static final String INPUT_DATETIME_PATTERN = "yyyy-MM-dd HHmm";

    private Constants() {
        // Utility class — not instantiable.
    }
}


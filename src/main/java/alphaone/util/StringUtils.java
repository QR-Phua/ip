package alphaone.util;

/**
 * Utility class providing common string manipulation helpers.
 */
public final class StringUtils {

    private StringUtils() {
        // Utility class — not instantiable.
    }

    /**
     * Removes a single trailing newline character from the end of the given StringBuilder,
     * if one is present.
     *
     * @param builder the StringBuilder to trim in place.
     */
    public static void trimTrailingNewline(StringBuilder builder) {
        int lastIndex = builder.length() - 1;
        if (lastIndex >= 0 && builder.charAt(lastIndex) == '\n') {
            builder.deleteCharAt(lastIndex);
        }
    }
}


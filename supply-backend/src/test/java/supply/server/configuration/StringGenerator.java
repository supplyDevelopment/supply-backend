package supply.server.configuration;

import java.security.SecureRandom;

public class StringGenerator {

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String LETTERS_AND_DIGITS = LETTERS + DIGITS;

    private final SecureRandom random = new SecureRandom();

    public String generate(int length, boolean includeLetters, boolean includeDigits) {
        if (!includeLetters && !includeDigits) {
            throw new IllegalArgumentException("At least one of letters or digits must be included!");
        }

        String source = (includeLetters ? LETTERS : "") + (includeDigits ? DIGITS : "");

        StringBuilder result = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            result.append(source.charAt(random.nextInt(source.length())));
        }

        return result.toString();
    }

}

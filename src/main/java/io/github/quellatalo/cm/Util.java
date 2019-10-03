package io.github.quellatalo.cm;

class Util {
    private static final String INVALID_INDEX_EXCEPTION = "Starting index should not be negative.";

    public static int getIndexOfNonWhitespace(String source, int startIndex) {
        if (startIndex < 0) throw new IndexOutOfBoundsException(INVALID_INDEX_EXCEPTION);
        if (source != null)
            for (int i = startIndex; i < source.length(); i++)
                if (!Character.isWhitespace(source.charAt(i))) return i;
        return -1;
    }

    public static int getIndexOfNonWhitespace(String source) {
        return getIndexOfNonWhitespace(source, 0);
    }

}

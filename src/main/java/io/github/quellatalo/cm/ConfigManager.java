package io.github.quellatalo.cm;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static io.github.quellatalo.cm.Util.getIndexOfNonWhitespace;

/**
 * Manages a properties file.
 */
public class ConfigManager {
    public static final String COMMENT = "#";
    public static final String DEFAULT_SEPARATOR = "=";
    private static final String INVALID_KEY_EXCEPTION = "A key should not have Separator nor NewLine character in it, and should not start with Comment.";
    private static final String LINE_ENTRY_EXCEPTION = "An entry should not contain a NewLine character.";
    private String separator = DEFAULT_SEPARATOR;
    private HashMap<String, String> properties = new HashMap<>();
    private List<LineEntry> lineEntries = new ArrayList<>();
    private Charset charset;
    private String filePath;
    private String newLine;

    /**
     * Creates an instance of ConfigManager, create the config file if not exist.
     *
     * @param filePath Path to working file.
     * @param charset  Charset setting.
     * @param newLine  Custom NewLine string.
     */
    public ConfigManager(String filePath, Charset charset, String newLine) {
        this.filePath = filePath;
        this.newLine = newLine;
        this.charset = charset;
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates an instance of ConfigManager, create the config file if not exist.
     *
     * @param filePath Path to working file.
     * @param charset  Charset setting.
     */
    public ConfigManager(String filePath, Charset charset) {
        this(filePath, charset, System.lineSeparator());
    }

    /**
     * Creates an instance of ConfigManager, create the config file if not exist.
     *
     * @param filePath Path to working file.
     * @param newLine  Custom NewLine string.
     */
    public ConfigManager(String filePath, String newLine) {
        this(filePath, Charset.defaultCharset(), newLine);
    }

    /**
     * Creates an instance of ConfigManager, create the config file if not exist.
     *
     * @param filePath Path to working file.
     */
    public ConfigManager(String filePath) {
        this(filePath, Charset.defaultCharset(), System.lineSeparator());
    }

    private static IllegalArgumentException newInvalidKeyException() {
        return new IllegalArgumentException(INVALID_KEY_EXCEPTION);
    }

    private static IllegalArgumentException newLineEntryException() {
        return new IllegalArgumentException(LINE_ENTRY_EXCEPTION);
    }

    public static boolean isPropertyLine(String line) {
        String trim = line.trim();
        return trim.length() > 0 && !trim.startsWith(COMMENT);
    }

    /**
     * Gets the key-value separator.
     *
     * @return The character that used to separate key and value.
     */
    public String getSeparator() {
        return separator;
    }

    /**
     * Sets the key-value separator.
     *
     * @param separator The character that used to separate key and value.
     */
    public void setSeparator(String separator) {
        this.separator = separator;
    }

    /**
     * Gets the file's charset.
     *
     * @return The charset which the file is using,
     */
    public Charset getCharsets() {
        return charset;
    }

    /**
     * Sets the file's charset.
     *
     * @param charset The charset which the file will use.
     */
    public void setCharset(Charset charset) {
        this.charset = charset;
    }

    /**
     * Gets the file path.
     *
     * @return The path to the file to be saved or loaded.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets file path.
     *
     * @param filePath The path to the file to be saved or loaded.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Gets the string which represents a new line in this file.
     *
     * @return The string which represents a new line in this file.
     */
    public String getNewLine() {
        return newLine;
    }

    /**
     * Sets the string which represents a new line in this file.
     *
     * @param newLine The string which represents a new line in this file.
     */
    public void setNewLine(String newLine) {
        this.newLine = newLine;
    }

    /**
     * Gets one line based on given index.
     *
     * @param entryIndex Index of the line.
     * @return Whole line as String.
     */
    public String getLineEntry(int entryIndex) {
        String rs;
        if (lineEntries.get(entryIndex).isProperty()) {
            String key = lineEntries.get(entryIndex).getKey();
            rs = lineEntries.get(entryIndex).getEntry() + properties.getOrDefault(key, "");
        } else {
            rs = lineEntries.get(entryIndex).getEntry();
        }
        return rs;
    }

    /**
     * Modifies one line based on given index.
     *
     * @param entryIndex Index of the line.
     * @param text       String to be replaced.
     */
    public void modifyLineEntry(int entryIndex, String text) {
        checkLineEntryValid(text);
        String trim = text.trim();
        if (trim.length() == 0 || trim.startsWith(COMMENT)) {
            lineEntries.get(entryIndex).setEntry(text);
        } else {
            String entryText, v = "";
            String[] prop = text.split(separator, 2);
            if (prop.length > 1) {
                v = prop[1].trim();
                int valueIndex = getIndexOfNonWhitespace(prop[1], 0);
                entryText = prop[0] + separator + prop[1].substring(0, valueIndex == -1 ? 0 : valueIndex);
            } else {
                entryText = prop[0];
            }
            lineEntries.get(entryIndex).setEntry(entryText);
            properties.put(prop[0].trim(), v);
        }
    }

    /**
     * Gets number of lines.
     *
     * @return Number of lines as int.
     */
    public int lineCount() {
        return lineEntries.size();
    }

    /**
     * Adds a new empty line.
     */
    public void addLineEntry() {
        addLineEntry(lineEntries.size(), "");
    }

    /**
     * Adds a new line.
     *
     * @param text The value of the new line.
     */
    public void addLineEntry(String text) {
        addLineEntry(lineEntries.size(), text);
    }

    /**
     * Inserts a new empty line at a specified index.
     *
     * @param index The index to be added at.
     */
    public void addLineEntry(int index) {
        addLineEntry(index, "");
    }

    /**
     * Inserts a new line at a specified index.
     *
     * @param index The index to be added at.
     * @param text  The value of the new line.
     */
    public void addLineEntry(int index, String text) {
        checkLineEntryValid(text);
        String trim = text.trim();
        if (trim.length() == 0 || trim.startsWith(COMMENT)) {
            lineEntries.add(index, new LineEntry(this, text));
        } else {
            String entryText, value = "";
            String[] prop = text.split(separator, 2);
            if (prop.length > 1) {
                value = prop[1].trim();
                int valueIndex = getIndexOfNonWhitespace(prop[1], 0);
                entryText = prop[0] + separator + prop[1].substring(0, valueIndex == -1 ? 0 : valueIndex);
            } else {
                entryText = prop[0];
            }
            lineEntries.add(index, new LineEntry(this, entryText));
            properties.put(prop[0].trim(), value);
        }
    }

    /**
     * Removes the line at the specified zero-based index.
     *
     * @param index The zero-based index of the line to be removed.
     */
    public void removeLineEntryAt(int index) {
        lineEntries.remove(index);
    }

    /**
     * Gets the line index of a particular property.
     *
     * @param key The property to look for.
     * @return The index of the property.
     */
    public int getPropertyEntryIndex(String key) {
        checkKeyValid(key);
        key = key.trim();
        boolean found = false;
        int i;
        for (i = 0; i < lineEntries.size(); i++) {
            if (lineEntries.get(i).isProperty()) {
                if (lineEntries.get(i).getKey().equals(key)) {
                    found = true;
                    break;
                }
            }
        }
        if (!found) i = -1;
        return i;
    }

    /**
     * Gets the value of a property.
     *
     * @param key Property key.
     * @return Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key..
     */
    public String getProperty(String key) {
        return properties.get(key);
    }

    /**
     * Sets a property.
     *
     * @param key   Property key.
     * @param value Property Value.
     */
    public void setProperty(String key, Object value) {
        setProperty(key, value.toString());
    }

    /**
     * Sets a property.
     *
     * @param key   Property key.
     * @param value Property Value.
     */
    public void setProperty(String key, String value) {
        checkKeyValid(key);
        checkLineEntryValid(value);
        properties.put(key.trim(), value.trim());
        if (getPropertyEntryIndex(key) == -1) {
            lineEntries.add(new LineEntry(this, key + separator));
        }
    }

    /**
     * Clears all data.
     */
    public void clearAll() {
        lineEntries.clear();
        properties.clear();
    }

    /**
     * Loads data from existing file.
     *
     * @throws IOException Cannot read from file.
     */
    public void load() throws IOException {
        clearAll();
        InputStream is = new FileInputStream(filePath);
        BufferedReader br = new BufferedReader(new InputStreamReader(is, charset));
        List<String> lines = br.lines().collect(Collectors.toList());
        br.close();
        is.close();
        for (String line : lines) {
            addLineEntry(line);
        }
    }

    /**
     * Gets the to be written data of the file.
     *
     * @return Full data as String.
     */
    public String getFullText() {
        StringBuilder sb = new StringBuilder();
        for (LineEntry entry : lineEntries) {
            if (entry.isProperty()) {
                String key = entry.getKey();
                sb.append(entry.getEntry()).append(properties.getOrDefault(key, "")).append(newLine);
            } else {
                sb.append(entry.getEntry()).append(newLine);
            }
        }
        return sb.toString();
    }

    /**
     * Gets the to be written data of the file.
     *
     * @return List of lines in order.
     */
    public List<String> getAllLines() {
        List<String> lines = new ArrayList<>();
        for (LineEntry line : lineEntries) {
            if (line.isProperty()) {
                String key = line.getKey();
                lines.add(line.getEntry() + properties.getOrDefault(key, ""));
            } else {
                lines.add(line.getEntry());
            }
        }
        return lines;
    }

    /**
     * Saves to file.
     *
     * @throws IOException File not accessible.
     */
    public void save() throws IOException {
        File file = new File(filePath);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file), charset);
        writer.write(getFullText());
        writer.close();
    }

    /**
     * Checking whether a property exists.
     *
     * @param key Property key.
     * @return Is exist in boolean.
     */
    public boolean hasProperty(String key) {
        return properties.containsKey(key);
    }

    private void checkKeyValid(String key) {
        if (key.contains(separator) || key.trim().startsWith(COMMENT) || key.contains(newLine))
            throw newInvalidKeyException();
    }

    private void checkLineEntryValid(String text) {
        if (text.contains(newLine)) throw newLineEntryException();
    }
}

package io.github.quellatalo.cm;

class LineEntry {
    private String entry;
    private ConfigManager manager;

    public LineEntry(ConfigManager manager) {
        this(manager, "");
    }

    public LineEntry(ConfigManager manager, String text) {
        this.manager = manager;
        entry = text;
    }

    public String getEntry() {
        return entry;
    }

    public void setEntry(String entry) {
        this.entry = entry;
    }

    public ConfigManager getManager() {
        return manager;
    }

    public void setManager(ConfigManager manager) {
        this.manager = manager;
    }

    public String getKey() {
        String s = entry.trim();
        if (s.endsWith(manager.getSeparator())) {
            s = s.substring(0, s.length() - 1).trim();
        }
        return s;
    }

    public boolean isProperty() {
        return ConfigManager.isPropertyLine(entry);
    }
}

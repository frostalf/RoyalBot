package org.royaldev.royalbot.configuration;


import java.util.Map;
import org.apache.commons.lang3.Validate;

/**
 * This is a {@link Configuration} implementation that does not save or load
 * from any source, and stores all values in memory only.
 * This is useful for temporary Configurations for providing defaults.
 */
public class MemoryConfiguration extends MemorySection implements Configuration {
    protected Configuration defaults;

    /**
     * Creates an empty {@link MemoryConfiguration} with no default values.
     */
    public MemoryConfiguration() {
    }

    @Override
    public void addDefault(String path, Object value) {
        Validate.notNull(path, "Path may not be null");
        if (defaults == null) {
            defaults = new MemoryConfiguration();
        }
        defaults.set(path, value);
    }

    @Override
    public void addDefaults(Map<String, Object> defaults) {
        Validate.notNull(defaults, "Defaults may not be null");
        for (Map.Entry<String, Object> entry : defaults.entrySet()) {
            addDefault(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Configuration getDefaults() {
        return defaults;
    }

    @Override
    public ConfigurationSection getParent() {
        return null;
    }
}

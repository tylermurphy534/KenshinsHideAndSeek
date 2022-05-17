/*
 * This file is part of Kenshins Hide and Seek
 *
 * Copyright (c) 2021 Tyler Murphy.
 *
 * Kenshins Hide and Seek free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * he Free Software Foundation version 3.
 *
 * Kenshins Hide and Seek is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package net.tylermurphy.hideAndSeek.configuration;

import net.tylermurphy.hideAndSeek.Main;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class ConfigManager {

    private final File file;
    private YamlConfiguration config,defaultConfig;
    private String defaultFilename;

    public static ConfigManager create(String filename) {
        return new ConfigManager(filename, filename);
    }

    public static ConfigManager create(String filename, String defaultFilename) {
        return new ConfigManager(filename, defaultFilename);
    }

    private ConfigManager(String filename, String defaultFilename) {

        this.defaultFilename = defaultFilename;
        this.file = new File(Main.getInstance().getDataFolder(), filename);

        File folder = Main.getInstance().getDataFolder();
        if (!folder.exists()) {
            if (!folder.mkdirs()) {
                throw new RuntimeException("Failed to make directory: " + file.getPath());
            }
        }

        if (!file.exists()) {
            try{
                InputStream input = Main.getInstance().getResource(defaultFilename);
                if (input == null) {
                    throw new RuntimeException("Could not create input stream for "+defaultFilename);
                }
                java.nio.file.Files.copy(input, file.toPath());
                input.close();
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        FileInputStream fileInputStream;
        try {
            fileInputStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not create input stream for "+file.getPath());
        }
        InputStreamReader reader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8);
        this.config = new YamlConfiguration();
        try {
            this.config.load(reader);
        } catch(InvalidConfigurationException e) {
            throw new RuntimeException("Invalid configuration in config file: "+file.getPath());
        } catch(IOException e) {
            throw new RuntimeException("Could not access file: "+file.getPath());
        }

        InputStream input = this.getClass().getClassLoader().getResourceAsStream(defaultFilename);
        if (input == null) {
            throw new RuntimeException("Could not create input stream for "+defaultFilename);
        }
        InputStreamReader default_reader = new InputStreamReader(input, StandardCharsets.UTF_8);
        this.defaultConfig = new YamlConfiguration();
        try {
            this.defaultConfig.load(default_reader);
        } catch(InvalidConfigurationException e) {
            throw new RuntimeException("Invalid configuration in config file: "+file.getPath());
        } catch(IOException e) {
            throw new RuntimeException("Could not access file: "+file.getPath());
        }

        try{
            fileInputStream.close();
            default_reader.close();
        } catch (IOException e) {
            throw new RuntimeException("Unable to finalize loading of config files.");
        }
    }

    public boolean contains(String path) {
        return config.contains(path);
    }

    public double getDouble(String path) {
        if (!config.contains(path)) {
            return defaultConfig.getDouble(path);
        } else {
            return config.getDouble(path);
        }
    }

    public int getInt(String path) {
        if (!config.contains(path)) {
            return defaultConfig.getInt(path);
        } else {
            return config.getInt(path);
        }
    }

    public int getDefaultInt(String path) {
        return defaultConfig.getInt(path);
    }

    public float getFloat(String path) {
        if (!config.contains(path)) {
            return (float) defaultConfig.getDouble(path);
        } else {
            return (float) config.getDouble(path);
        }
    }

    public String getString(String path) {
        String value = config.getString(path);
        if (value == null) {
            return defaultConfig.getString(path);
        } else {
            return value;
        }
    }

    public String getString(String path, String oldPath) {
        String value = config.getString(path);
        if (value == null) {
            String oldValue = config.getString(oldPath);
            if (oldValue == null) {
                return defaultConfig.getString(path);
            } else {
                return oldValue;
            }
        } else {
            return value;
        }
    }

    public List<String> getStringList(String path) {
        List<String> value = config.getStringList(path);
        if (value == null) {
            return defaultConfig.getStringList(path);
        } else {
            return value;
        }
    }

    public void reset(String path) {
        config.set(path, defaultConfig.get(path));
    }

    public void resetFile(String newDefaultFilename) {
        this.defaultFilename = newDefaultFilename;

        InputStream input = Main.getInstance().getResource(defaultFilename);
        if (input == null) {
            throw new RuntimeException("Could not create input stream for "+defaultFilename);
        }
        InputStreamReader reader = new InputStreamReader(input);
        this.config = YamlConfiguration.loadConfiguration(reader);
        this.defaultConfig = YamlConfiguration.loadConfiguration(reader);

    }

    public boolean getBoolean(String path) {
        if (!config.contains(path)) {
            return defaultConfig.getBoolean(path);
        } else {
            return config.getBoolean(path);
        }
    }

    public ConfigurationSection getConfigurationSection(String path) {
        ConfigurationSection section = config.getConfigurationSection(path);
        if (section == null) {
            return defaultConfig.getConfigurationSection(path);
        } else {
            return section;
        }
    }

    public ConfigurationSection getDefaultConfigurationSection(String path) {
        return defaultConfig.getConfigurationSection(path);
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public void saveConfig() {
        try {
            InputStream is = Main.getInstance().getResource(defaultFilename);
            if (is == null) {
                throw new RuntimeException("Could not create input stream for "+defaultFilename);
            }
            StringBuilder textBuilder = new StringBuilder(new String("".getBytes(), StandardCharsets.UTF_8));
            Reader reader = new BufferedReader(new InputStreamReader(is, Charset.forName(StandardCharsets.UTF_8.name())));
            int c;
            while((c = reader.read()) != -1) {
                textBuilder.append((char) c);
            }
            String yamlString = new String(textBuilder.toString().getBytes(), StandardCharsets.UTF_8);
            Map<String, Object> temp = config.getValues(true);
            for(Map.Entry<String, Object> entry: temp.entrySet()) {
                if (entry.getValue() instanceof Integer || entry.getValue() instanceof Double || entry.getValue() instanceof String || entry.getValue() instanceof Boolean || entry.getValue() instanceof List) {
                    String[] parts = entry.getKey().split("\\.");
                    int index = 0;
                    int i = 0;
                    for(String part : parts) {
                        if (i == 0) {
                            index = yamlString.indexOf(part+":", index);
                        } else {
                            index = yamlString.indexOf(" " + part+":", index);
                            index++;
                        }
                        i++;
                        if (index == -1) break;
                    }
                    if (index < 10)  continue;
                    int start = yamlString.indexOf(' ', index);
                    int end = yamlString.indexOf('\n', index);
                    if (end == -1) end = yamlString.length();
                    StringBuilder replace = new StringBuilder(new String("".getBytes(), StandardCharsets.UTF_8));
                    if (entry.getValue() instanceof List) {
                        if (((List<?>) entry.getValue()).isEmpty()) {
                            replace.append("[]");
                        } else {
                            replace.append("[");
                            for (Object o : (List<?>) entry.getValue()) {
                                replace.append(o.toString()).append(", ");
                            }
                            replace = new StringBuilder(replace.substring(0, replace.length() - 2));
                            replace.append("]");
                        }
                    } else {
                        replace.append(entry.getValue());
                    }
                    if (entry.getValue() instanceof String) {
                        replace.append("\"");
                        replace.reverse();
                        replace.append("\"");
                        replace.reverse();
                    }
                    StringBuilder builder = new StringBuilder(yamlString);
                    builder.replace(start+1, end, replace.toString());
                    yamlString = builder.toString();
                }
            }
            Writer fileWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            fileWriter.write(yamlString);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

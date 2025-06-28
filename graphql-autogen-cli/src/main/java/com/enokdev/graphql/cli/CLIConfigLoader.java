package com.enokdev.graphql.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loader for CLI configuration files (JSON/YAML).
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class CLIConfigLoader {

    public CLIConfig loadConfig(Path configFile) throws Exception {
        if (!Files.exists(configFile)) {
            throw new IllegalArgumentException("Configuration file not found: " + configFile);
        }

        String fileName = configFile.getFileName().toString().toLowerCase();
        ObjectMapper mapper;

        if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            mapper = new ObjectMapper(new YAMLFactory());
        } else if (fileName.endsWith(".json")) {
            mapper = new ObjectMapper();
        } else {
            throw new IllegalArgumentException(
                "Unsupported configuration file format. Use .json, .yml, or .yaml"
            );
        }

        return mapper.readValue(configFile.toFile(), CLIConfig.class);
    }
}

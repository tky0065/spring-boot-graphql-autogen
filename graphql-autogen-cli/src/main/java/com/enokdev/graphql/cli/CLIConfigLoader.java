package com.enokdev.graphql.cli;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Loader for CLI configuration files (JSON only).
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
        ObjectMapper mapper = new ObjectMapper();

        if (fileName.endsWith(".json")) {
            return mapper.readValue(configFile.toFile(), CLIConfig.class);
        } else if (fileName.endsWith(".yml") || fileName.endsWith(".yaml")) {
            throw new IllegalArgumentException(
                "YAML configuration files are not currently supported. " +
                "Please use JSON format (.json) or add jackson-dataformat-yaml dependency."
            );
        } else {
            throw new IllegalArgumentException(
                "Unsupported configuration file format. Use .json format."
            );
        }
    }
}

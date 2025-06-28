package com.enokdev.graphql.gradle;

import org.gradle.api.DefaultTask;
import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.Property;
import org.gradle.api.tasks.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Gradle task to clean generated GraphQL schema files.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public abstract class CleanSchemaTask extends DefaultTask {

    /**
     * Directory containing the generated schema files.
     */
    @InputDirectory
    @Optional
    public abstract DirectoryProperty getOutputDirectory();

    /**
     * Whether to clean only schema files or the entire directory.
     */
    @Input
    public abstract Property<Boolean> getCleanOnlySchemaFiles();

    /**
     * Skip schema cleaning.
     */
    @Input
    public abstract Property<Boolean> getSkipClean();

    @TaskAction
    public void cleanSchema() {
        if (getSkipClean().get()) {
            getLogger().info("GraphQL schema cleaning skipped");
            return;
        }

        if (!getOutputDirectory().isPresent() || !getOutputDirectory().get().getAsFile().exists()) {
            getLogger().info("Output directory does not exist, nothing to clean");
            return;
        }

        Path outputPath = getOutputDirectory().get().getAsFile().toPath();
        getLogger().info("Cleaning GraphQL generated files from: {}", outputPath.toAbsolutePath());

        try {
            if (getCleanOnlySchemaFiles().get()) {
                cleanSchemaFiles(outputPath);
            } else {
                cleanEntireDirectory(outputPath);
            }
            getLogger().info("GraphQL schema cleaning completed successfully");
        } catch (Exception e) {
            throw new TaskExecutionException(this, 
                new RuntimeException("Failed to clean GraphQL files: " + e.getMessage(), e));
        }
    }

    private void cleanSchemaFiles(Path outputPath) throws IOException {
        int deletedFiles = 0;

        try (Stream<Path> files = Files.walk(outputPath)) {
            deletedFiles = (int) files
                .filter(Files::isRegularFile)
                .filter(this::isSchemaFile)
                .mapToInt(path -> {
                    try {
                        Files.delete(path);
                        getLogger().debug("Deleted schema file: {}", path);
                        return 1;
                    } catch (IOException e) {
                        getLogger().warn("Failed to delete file: {} - {}", path, e.getMessage());
                        return 0;
                    }
                })
                .sum();
        }

        getLogger().info("Deleted {} schema files", deletedFiles);

        // Clean empty directories
        cleanEmptyDirectories(outputPath);
    }

    private void cleanEntireDirectory(Path outputPath) throws IOException {
        int deletedFiles = 0;

        try (Stream<Path> files = Files.walk(outputPath)) {
            deletedFiles = (int) files
                .sorted((a, b) -> b.compareTo(a)) // Delete files before directories
                .filter(path -> !path.equals(outputPath)) // Don't delete the root directory
                .mapToInt(path -> {
                    try {
                        Files.delete(path);
                        getLogger().debug("Deleted: {}", path);
                        return 1;
                    } catch (IOException e) {
                        getLogger().warn("Failed to delete: {} - {}", path, e.getMessage());
                        return 0;
                    }
                })
                .sum();
        }

        getLogger().info("Deleted {} files and directories", deletedFiles);
    }

    private boolean isSchemaFile(Path path) {
        String fileName = path.getFileName().toString().toLowerCase();
        return fileName.endsWith(".graphqls") ||
               fileName.endsWith(".graphql") ||
               fileName.endsWith(".gql");
    }

    private void cleanEmptyDirectories(Path rootPath) throws IOException {
        try (Stream<Path> paths = Files.walk(rootPath)) {
            paths.filter(Files::isDirectory)
                 .filter(path -> !path.equals(rootPath))
                 .sorted((a, b) -> b.compareTo(a)) // Process deepest directories first
                 .forEach(this::deleteIfEmpty);
        }
    }

    private void deleteIfEmpty(Path directory) {
        try {
            if (isDirectoryEmpty(directory)) {
                Files.delete(directory);
                getLogger().debug("Deleted empty directory: {}", directory);
            }
        } catch (IOException e) {
            getLogger().debug("Could not delete directory: {} - {}", directory, e.getMessage());
        }
    }

    private boolean isDirectoryEmpty(Path directory) throws IOException {
        try (Stream<Path> entries = Files.list(directory)) {
            return entries.findFirst().isEmpty();
        }
    }
}

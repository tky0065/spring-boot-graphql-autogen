package com.enokdev.graphql.gradle;

import org.gradle.api.file.DirectoryProperty;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

/**
 * Extension for configuring the GraphQL AutoGen Gradle plugin.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public abstract class GraphQLAutoGenExtension {

    /**
     * Base packages to scan for GraphQL annotations.
     * 
     * @return the base packages property
     */
    public abstract ListProperty<String> getBasePackages();

    /**
     * Packages to exclude from scanning.
     * 
     * @return the exclude packages property
     */
    public abstract ListProperty<String> getExcludePackages();

    /**
     * Output directory for the generated schema file.
     * 
     * @return the output directory property
     */
    public abstract DirectoryProperty getOutputDirectory();

    /**
     * Name of the generated schema file.
     * 
     * @return the schema file name property
     */
    public abstract Property<String> getSchemaFileName();

    /**
     * Whether to generate input types automatically.
     * 
     * @return the generate inputs property
     */
    public abstract Property<Boolean> getGenerateInputs();

    /**
     * Naming strategy for generated types.
     * Valid values: CAMEL_CASE, PASCAL_CASE, SNAKE_CASE
     * 
     * @return the naming strategy property
     */
    public abstract Property<String> getNamingStrategy();

    /**
     * Whether to fail build on schema generation errors.
     * 
     * @return the fail on error property
     */
    public abstract Property<Boolean> getFailOnError();

    /**
     * Skip schema generation.
     * 
     * @return the skip property
     */
    public abstract Property<Boolean> getSkip();

    /**
     * Whether to include inherited fields.
     * 
     * @return the include inherited fields property
     */
    public abstract Property<Boolean> getIncludeInheritedFields();

    /**
     * Maximum scanning depth for packages.
     * 
     * @return the max scan depth property
     */
    public abstract Property<Integer> getMaxScanDepth();

    /**
     * Convenience method to set base packages.
     * 
     * @param packages the packages to scan
     */
    public void basePackages(String... packages) {
        getBasePackages().set(java.util.List.of(packages));
    }

    /**
     * Convenience method to add base packages.
     * 
     * @param packages the packages to add
     */
    public void addBasePackages(String... packages) {
        getBasePackages().addAll(packages);
    }

    /**
     * Convenience method to set exclude packages.
     * 
     * @param packages the packages to exclude
     */
    public void excludePackages(String... packages) {
        getExcludePackages().set(java.util.List.of(packages));
    }

    /**
     * Convenience method to add exclude packages.
     * 
     * @param packages the packages to add to exclusion list
     */
    public void addExcludePackages(String... packages) {
        getExcludePackages().addAll(packages);
    }

    /**
     * Convenience method to set output directory from string path.
     * 
     * @param path the output directory path
     */
    public void outputDirectory(String path) {
        getOutputDirectory().set(new java.io.File(path));
    }

    /**
     * Convenience method to set schema file name.
     * 
     * @param fileName the schema file name
     */
    public void schemaFileName(String fileName) {
        getSchemaFileName().set(fileName);
    }

    /**
     * Convenience method to set generate inputs flag.
     * 
     * @param generate whether to generate inputs
     */
    public void generateInputs(boolean generate) {
        getGenerateInputs().set(generate);
    }

    /**
     * Convenience method to set naming strategy.
     * 
     * @param strategy the naming strategy
     */
    public void namingStrategy(String strategy) {
        getNamingStrategy().set(strategy);
    }

    /**
     * Convenience method to set fail on error flag.
     * 
     * @param fail whether to fail on error
     */
    public void failOnError(boolean fail) {
        getFailOnError().set(fail);
    }

    /**
     * Convenience method to set skip flag.
     * 
     * @param skip whether to skip generation
     */
    public void skip(boolean skip) {
        getSkip().set(skip);
    }

    /**
     * Convenience method to set include inherited fields flag.
     * 
     * @param include whether to include inherited fields
     */
    public void includeInheritedFields(boolean include) {
        getIncludeInheritedFields().set(include);
    }

    /**
     * Convenience method to set max scan depth.
     * 
     * @param depth the maximum scan depth
     */
    public void maxScanDepth(int depth) {
        getMaxScanDepth().set(depth);
    }
}

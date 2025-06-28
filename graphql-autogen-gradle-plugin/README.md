# GraphQL AutoGen Gradle Plugin

Gradle plugin for generating GraphQL schemas from Java classes using annotations.

## Features

- 🚀 **Automatic Schema Generation**: Generate complete GraphQL schemas from JPA entities and Spring controllers
- 🎯 **Task-Based**: Multiple Gradle tasks for different phases of development
- ⚙️ **Highly Configurable**: Extensive configuration through Gradle DSL
- 🔄 **Build Integration**: Seamless integration with Gradle build lifecycle
- ✅ **Validation**: Built-in schema validation and verification
- 🧹 **Clean Support**: Automatic cleanup of generated files

## Quick Start

### 1. Apply the Plugin

Using the plugins DSL (Gradle 2.1+):
```gradle
plugins {
    id 'java'
    id 'com.enokdev.graphql.autogen' version '1.0.0-SNAPSHOT'
}
```

Using legacy plugin application:
```gradle
buildscript {
    dependencies {
        classpath 'com.enokdev.graphql:graphql-autogen-gradle-plugin:1.0.0-SNAPSHOT'
    }
}

apply plugin: 'com.enokdev.graphql.autogen'
```

### 2. Configure the Plugin

```gradle
graphqlAutoGen {
    basePackages 'com.example.domain', 'com.example.controllers'
    generateInputs true
    namingStrategy 'PASCAL_CASE'
    schemaFileName 'api-schema.graphqls'
}
```

### 3. Annotate Your Classes

```java
@Entity
@GraphQLType
public class Book {
    @Id
    @GraphQLId
    private Long id;
    
    @GraphQLField(description = "The title of the book")
    private String title;
    
    @GraphQLField
    private String author;
}

@GraphQLController
public class BookController {
    
    @GraphQLQuery
    public List<Book> getAllBooks() {
        // Implementation
    }
    
    @GraphQLMutation
    public Book createBook(@GraphQLArgument CreateBookInput input) {
        // Implementation
    }
}
```

### 4. Run Gradle Tasks

```bash
# Generate schema
./gradlew generateGraphQLSchema

# Validate schema
./gradlew validateGraphQLSchema

# Clean generated files
./gradlew cleanGraphQLSchema

# Build with validation
./gradlew build
```

## Tasks

### `generateGraphQLSchema`

Generates GraphQL schema from annotated Java classes.

**Type**: Generation task  
**Depends on**: `compileJava` (if configured)

```gradle
tasks.named('generateGraphQLSchema') {
    dependsOn 'compileJava'
    
    doFirst {
        logger.info("Starting schema generation...")
    }
}
```

### `validateGraphQLSchema`

Validates the generated GraphQL schema for syntax and structural issues.

**Type**: Verification task  
**Depends on**: `generateGraphQLSchema`

```gradle
tasks.named('validateGraphQLSchema') {
    minTypes = 5
    validateNotEmpty = true
    failOnValidationError = true
}
```

### `cleanGraphQLSchema`

Cleans generated GraphQL schema files.

**Type**: Delete task  
**Lifecycle**: Runs during `clean`

```gradle
tasks.named('cleanGraphQLSchema') {
    cleanOnlySchemaFiles = true
}
```

## Configuration

### Basic Configuration

```gradle
graphqlAutoGen {
    // Required: Base packages to scan
    basePackages 'com.example.entities', 'com.example.controllers'
    
    // Optional: Output configuration
    outputDirectory = file("${buildDir}/graphql")
    schemaFileName = 'api-schema.graphqls'
    
    // Optional: Generation options
    generateInputs = true
    namingStrategy = 'PASCAL_CASE'
    includeInheritedFields = true
    
    // Optional: Scanning options
    maxScanDepth = 10
    excludePackages 'com.example.internal'
    
    // Optional: Build behavior
    failOnError = true
    skip = false
}
```

### Extension Properties

| Property | Type | Default | Description |
|----------|------|---------|-------------|
| `basePackages` | `List<String>` | `[project.group]` | Base packages to scan |
| `excludePackages` | `List<String>` | `[]` | Packages to exclude |
| `outputDirectory` | `Directory` | `${buildDir}/generated/sources/graphql` | Output directory |
| `schemaFileName` | `String` | `"schema.graphqls"` | Schema file name |
| `generateInputs` | `Boolean` | `true` | Generate input types |
| `namingStrategy` | `String` | `"PASCAL_CASE"` | Naming strategy |
| `failOnError` | `Boolean` | `true` | Fail build on errors |
| `skip` | `Boolean` | `false` | Skip generation |
| `includeInheritedFields` | `Boolean` | `true` | Include inherited fields |
| `maxScanDepth` | `Integer` | `10` | Maximum scan depth |

### Naming Strategies

- `PASCAL_CASE`: `BookType`, `CreateBookInput`
- `CAMEL_CASE`: `bookType`, `createBookInput`  
- `SNAKE_CASE`: `book_type`, `create_book_input`

### Advanced Configuration

```gradle
graphqlAutoGen {
    // Multiple base packages
    basePackages(
        'com.example.entities',
        'com.example.controllers',
        'com.example.dto'
    )
    
    // Alternative syntax
    basePackages = ['com.example.entities', 'com.example.controllers']
    
    // Add packages dynamically
    addBasePackages('com.example.services')
    
    // Exclude packages
    excludePackages(
        'com.example.internal',
        'com.example.test'
    )
    
    // Custom output directory
    outputDirectory = layout.buildDirectory.dir('custom/graphql')
    
    // Custom schema file name
    schemaFileName = "api-v${version}.graphqls"
}
```

## Build Integration

### Lifecycle Integration

```gradle
// Generate schema before compilation
tasks.named('compileJava') {
    dependsOn 'generateGraphQLSchema'
}

// Validate schema during check phase
tasks.named('check') {
    dependsOn 'validateGraphQLSchema'
}

// Clean schema files during clean
tasks.named('clean') {
    dependsOn 'cleanGraphQLSchema'
}

// Custom integration
tasks.register('buildWithSchema') {
    group = 'build'
    description = 'Build project with fresh schema generation'
    
    dependsOn 'cleanGraphQLSchema', 'generateGraphQLSchema', 'build'
    
    tasks.findByName('generateGraphQLSchema').mustRunAfter('cleanGraphQLSchema')
    tasks.findByName('build').mustRunAfter('generateGraphQLSchema')
}
```

### Task Configuration

```gradle
// Configure generate task
tasks.named('generateGraphQLSchema') {
    // Set task properties
    group = 'GraphQL AutoGen'
    description = 'Generate GraphQL schema from Java classes'
    
    // Configure inputs and outputs for up-to-date checking
    inputs.files(fileTree('src/main/java') {
        include '**/*.java'
    })
    outputs.file("${buildDir}/generated/sources/graphql/schema.graphqls")
    
    // Custom logic
    doFirst {
        logger.info("Scanning for GraphQL annotations...")
    }
    
    doLast {
        def schemaFile = outputs.files.singleFile
        logger.info("Generated schema: ${schemaFile.absolutePath}")
        logger.info("Schema size: ${schemaFile.length()} bytes")
    }
}

// Configure validate task
tasks.named('validateGraphQLSchema') {
    // Custom validation settings
    minTypes = project.findProperty('graphql.minTypes')?.toInteger() ?: 1
    validateNotEmpty = true
    
    // Conditional execution
    onlyIf {
        file("${buildDir}/generated/sources/graphql").exists()
    }
}
```

### Multi-Project Setup

```gradle
// In root build.gradle
subprojects {
    apply plugin: 'com.enokdev.graphql.autogen'
    
    graphqlAutoGen {
        basePackages "${project.group}.${project.name}"
        outputDirectory = file("${rootProject.buildDir}/schemas/${project.name}")
        schemaFileName = "${project.name}-schema.graphqls"
    }
}

// Aggregate schemas
tasks.register('aggregateSchemas') {
    group = 'GraphQL AutoGen'
    description = 'Aggregate all subproject schemas'
    
    dependsOn subprojects.collect { "${it.path}:generateGraphQLSchema" }
    
    doLast {
        def aggregatedSchema = file("${buildDir}/schemas/aggregated-schema.graphqls")
        aggregatedSchema.text = ""
        
        subprojects.each { subproject ->
            def schemaFile = file("${buildDir}/schemas/${subproject.name}/${subproject.name}-schema.graphqls")
            if (schemaFile.exists()) {
                aggregatedSchema << "# From ${subproject.name}\n"
                aggregatedSchema << schemaFile.text
                aggregatedSchema << "\n\n"
            }
        }
        
        logger.info("Aggregated schema written to: ${aggregatedSchema.absolutePath}")
    }
}
```

## IDE Integration

### IntelliJ IDEA

The plugin provides IDE support through Gradle's tooling API:

```gradle
// Enable IDE integration
apply plugin: 'idea'

idea {
    module {
        // Include generated sources
        sourceDirs += file("${buildDir}/generated/sources/graphql")
        generatedSourceDirs += file("${buildDir}/generated/sources/graphql")
    }
}
```

### VS Code

For VS Code with Gradle extension:

```json
// .vscode/settings.json
{
    "gradle.nestedProjects": true,
    "java.compile.nullAnalysis.mode": "automatic",
    "java.configuration.updateBuildConfiguration": "interactive"
}
```

## Command Line Usage

```bash
# Basic generation
./gradlew generateGraphQLSchema

# With custom properties
./gradlew generateGraphQLSchema -Pgraphql.namingStrategy=CAMEL_CASE

# Parallel execution
./gradlew --parallel generateGraphQLSchema validateGraphQLSchema

# Debug mode
./gradlew generateGraphQLSchema --debug

# Clean and regenerate
./gradlew clean generateGraphQLSchema

# Show task dependencies
./gradlew generateGraphQLSchema --dry-run
```

## Examples

### Basic Spring Boot Application

```gradle
plugins {
    id 'java'
    id 'org.springframework.boot' version '3.3.1'
    id 'com.enokdev.graphql.autogen' version '1.0.0-SNAPSHOT'
}

graphqlAutoGen {
    basePackages 'com.example'
    generateInputs true
    namingStrategy 'PASCAL_CASE'
}

dependencies {
    implementation 'com.enokdev.graphql:graphql-autogen-spring-boot-starter:1.0.0-SNAPSHOT'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    runtimeOnly 'com.h2database:h2'
}
```

### Multi-Module Project

```gradle
// root/build.gradle
allprojects {
    apply plugin: 'com.enokdev.graphql.autogen'
    
    graphqlAutoGen {
        basePackages "${rootProject.group}.${project.name}"
        failOnError = false // Don't fail entire build
    }
}

// api/build.gradle
graphqlAutoGen {
    basePackages 'com.example.api'
    schemaFileName = 'api-schema.graphqls'
    generateInputs = true
}

// core/build.gradle
graphqlAutoGen {
    basePackages 'com.example.core'
    schemaFileName = 'core-schema.graphqls'
    generateInputs = false // Only types, no inputs
}
```

## Troubleshooting

### Common Issues

**Plugin not found**:
```gradle
// Make sure plugin is published to a repository you have access to
repositories {
    gradlePluginPortal()
    mavenCentral()
    mavenLocal() // For SNAPSHOT versions
}
```

**Schema not generated**:
```gradle
// Check dependencies and classpath
tasks.named('generateGraphQLSchema') {
    doFirst {
        logger.info("Classpath: ${classpath.asPath}")
        logger.info("Base packages: ${basePackages.get()}")
    }
}
```

**Build cache issues**:
```bash
# Clear Gradle cache
./gradlew clean --build-cache
rm -rf ~/.gradle/caches/
```

### Debug Logging

```gradle
// Enable debug logging
logging.captureStandardOutput LogLevel.DEBUG

tasks.named('generateGraphQLSchema') {
    logging.level = LogLevel.DEBUG
}
```

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass: `./gradlew test`
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0.

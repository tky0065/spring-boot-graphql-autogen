# GraphQL AutoGen Maven Plugin

Maven plugin for generating GraphQL schemas from Java classes using annotations.

## Features

- 🚀 **Automatic Schema Generation**: Generate complete GraphQL schemas from JPA entities and Spring controllers
- 🎯 **Goal-Oriented**: Multiple Maven goals for different phases of development
- ⚙️ **Highly Configurable**: Extensive configuration options for customization
- 🔄 **Build Integration**: Seamless integration with Maven build lifecycle
- ✅ **Validation**: Built-in schema validation and verification
- 🧹 **Clean Support**: Automatic cleanup of generated files

## Quick Start

### 1. Add the Plugin to Your `pom.xml`

```xml
<build>
    <plugins>
        <plugin>
            <groupId>com.enokdev.graphql</groupId>
            <artifactId>graphql-autogen-maven-plugin</artifactId>
            <version>1.0.0-SNAPSHOT</version>
            <executions>
                <execution>
                    <id>generate-schema</id>
                    <phase>generate-sources</phase>
                    <goals>
                        <goal>generate-schema</goal>
                    </goals>
                </execution>
            </executions>
        </plugin>
    </plugins>
</build>
```

### 2. Annotate Your Classes

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

### 3. Run Maven

```bash
mvn generate-sources
```

Your GraphQL schema will be generated in `target/generated-sources/graphql/schema.graphqls`.

## Goals

### `generate-schema`

Generates GraphQL schema from annotated Java classes.

**Default Phase**: `generate-sources`

**Parameters**:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `basePackages` | `List<String>` | `${project.groupId}` | Base packages to scan |
| `outputDirectory` | `File` | `${project.build.directory}/generated-sources/graphql` | Output directory |
| `schemaFileName` | `String` | `schema.graphqls` | Schema file name |
| `generateInputs` | `boolean` | `true` | Generate input types |
| `namingStrategy` | `String` | `PASCAL_CASE` | Naming strategy |
| `failOnError` | `boolean` | `true` | Fail build on errors |
| `skip` | `boolean` | `false` | Skip generation |

**Example**:

```xml
<plugin>
    <groupId>com.enokdev.graphql</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <configuration>
        <basePackages>
            <basePackage>com.example.domain</basePackage>
            <basePackage>com.example.controllers</basePackage>
        </basePackages>
        <generateInputs>true</generateInputs>
        <namingStrategy>CAMEL_CASE</namingStrategy>
        <schemaFileName>api-schema.graphqls</schemaFileName>
    </configuration>
    <executions>
        <execution>
            <goals>
                <goal>generate-schema</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### `validate-schema`

Validates the generated GraphQL schema for syntax and structural issues.

**Default Phase**: `verify`

**Parameters**:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `schemaDirectory` | `File` | `${project.build.directory}/generated-sources/graphql` | Schema directory |
| `schemaFileName` | `String` | `schema.graphqls` | Schema file name |
| `failOnValidationError` | `boolean` | `true` | Fail build on validation errors |
| `skipValidation` | `boolean` | `false` | Skip validation |
| `validateNotEmpty` | `boolean` | `true` | Validate schema is not empty |
| `minTypes` | `int` | `1` | Minimum number of types expected |

**Example**:

```xml
<execution>
    <id>validate-schema</id>
    <phase>verify</phase>
    <goals>
        <goal>validate-schema</goal>
    </goals>
    <configuration>
        <minTypes>5</minTypes>
        <failOnValidationError>true</failOnValidationError>
    </configuration>
</execution>
```

### `clean-schema`

Cleans generated GraphQL schema files.

**Default Phase**: `clean`

**Parameters**:

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `outputDirectory` | `File` | `${project.build.directory}/generated-sources/graphql` | Directory to clean |
| `cleanOnlySchemaFiles` | `boolean` | `true` | Clean only .graphqls files |
| `skipClean` | `boolean` | `false` | Skip cleaning |

**Example**:

```xml
<execution>
    <id>clean-schema</id>
    <phase>clean</phase>
    <goals>
        <goal>clean-schema</goal>
    </goals>
</execution>
```

## Configuration Options

### Naming Strategies

- `PASCAL_CASE`: `BookType`, `CreateBookInput`
- `CAMEL_CASE`: `bookType`, `createBookInput`  
- `SNAKE_CASE`: `book_type`, `create_book_input`

### Complete Configuration Example

```xml
<plugin>
    <groupId>com.enokdev.graphql</groupId>
    <artifactId>graphql-autogen-maven-plugin</artifactId>
    <version>1.0.0-SNAPSHOT</version>
    <configuration>
        <!-- Base packages to scan -->
        <basePackages>
            <basePackage>com.example.entities</basePackage>
            <basePackage>com.example.controllers</basePackage>
            <basePackage>com.example.dto</basePackage>
        </basePackages>
        
        <!-- Packages to exclude -->
        <excludePackages>
            <excludePackage>com.example.internal</excludePackage>
        </excludePackages>
        
        <!-- Output configuration -->
        <outputDirectory>${project.build.directory}/graphql</outputDirectory>
        <schemaFileName>api-schema.graphqls</schemaFileName>
        
        <!-- Generation options -->
        <generateInputs>true</generateInputs>
        <includeInheritedFields>true</includeInheritedFields>
        <namingStrategy>PASCAL_CASE</namingStrategy>
        
        <!-- Build behavior -->
        <failOnError>true</failOnError>
        <skip>false</skip>
        <maxScanDepth>10</maxScanDepth>
    </configuration>
    
    <executions>
        <!-- Generate schema during compile -->
        <execution>
            <id>generate</id>
            <phase>generate-sources</phase>
            <goals>
                <goal>generate-schema</goal>
            </goals>
        </execution>
        
        <!-- Validate schema during verify -->
        <execution>
            <id>validate</id>
            <phase>verify</phase>
            <goals>
                <goal>validate-schema</goal>
            </goals>
            <configuration>
                <minTypes>3</minTypes>
                <validateNotEmpty>true</validateNotEmpty>
            </configuration>
        </execution>
        
        <!-- Clean during clean phase -->
        <execution>
            <id>clean</id>
            <phase>clean</phase>
            <goals>
                <goal>clean-schema</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## Command Line Usage

You can also run the plugin goals directly from the command line:

```bash
# Generate schema
mvn graphql-autogen:generate-schema

# Validate schema  
mvn graphql-autogen:validate-schema

# Clean generated files
mvn graphql-autogen:clean-schema

# With custom configuration
mvn graphql-autogen:generate-schema -Dgraphql.autogen.basePackages=com.example -Dgraphql.autogen.generateInputs=false
```

## IDE Integration

The plugin integrates well with IDEs:

- **IntelliJ IDEA**: Auto-completion for plugin configuration
- **Eclipse**: Maven integration with build lifecycle
- **VS Code**: Maven extension support

## Troubleshooting

### Common Issues

**Schema not generated**:
- Check that base packages contain annotated classes
- Verify classpath includes all necessary dependencies
- Enable debug logging: `mvn -X graphql-autogen:generate-schema`

**Validation errors**:
- Ensure generated schema has valid GraphQL syntax
- Check for duplicate type definitions
- Verify all referenced types are defined

**Build performance**:
- Limit base packages to necessary ones only
- Use `excludePackages` to skip irrelevant packages
- Consider `maxScanDepth` for large codebases

### Debug Logging

Enable detailed logging to troubleshoot issues:

```xml
<configuration>
    <!-- Plugin configuration -->
</configuration>
```

Run with debug flag:
```bash
mvn -X graphql-autogen:generate-schema
```

## Examples

See the `graphql-autogen-examples` module for complete working examples:

- **Simple Library**: Basic CRUD operations
- **E-commerce**: Complex relationships and pagination
- **Blog API**: Advanced querying and filtering

## Contributing

1. Fork the repository
2. Create a feature branch
3. Add tests for new functionality
4. Ensure all tests pass
5. Submit a pull request

## License

This project is licensed under the Apache License 2.0.

package com.enokdev.graphql.cli;

import com.enokdev.graphql.autogen.cli.CLILogger;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Callable;

/**
 * Command to initialize a new GraphQL AutoGen project.
 * Creates necessary configuration files and directory structure.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Command(
    name = "init",
    description = "Initialize a new GraphQL AutoGen project",
    mixinStandardHelpOptions = true
)
public class InitCommand implements Callable<Integer> {

    @Parameters(
        index = "0",
        description = "Project directory (default: current directory)",
        defaultValue = "."
    )
    private String projectDirectory;

    @Option(
        names = {"-t", "--type"},
        description = "Project type: maven, gradle, spring-boot (default: spring-boot)",
        defaultValue = "spring-boot"
    )
    private String projectType;

    @Option(
        names = {"-p", "--package"},
        description = "Base package name (e.g., com.example.myapp)",
        required = true
    )
    private String basePackage;

    @Option(
        names = {"-n", "--name"},
        description = "Project name",
        defaultValue = "graphql-autogen-project"
    )
    private String projectName;

    @Option(
        names = {"--force"},
        description = "Overwrite existing files"
    )
    private boolean force;

    private final CLILogger logger = new CLILogger();

    @Override
    public Integer call() throws Exception {
        try {
            Path projectPath = Paths.get(projectDirectory).toAbsolutePath();
            
            logger.info("Initializing GraphQL AutoGen project...");
            logger.info("Project directory: " + projectPath);
            logger.info("Project type: " + projectType);
            logger.info("Base package: " + basePackage);
            
            createProjectStructure(projectPath);
            createConfigurationFiles(projectPath);
            createExampleFiles(projectPath);
            
            logger.success("Project initialized successfully!");
            logger.info("Next steps:");
            logger.info("1. cd " + projectPath.getFileName());
            logger.info("2. Add your entities with @GraphQLType annotations");
            logger.info("3. Run: graphql-autogen generate");
            
            return 0;
            
        } catch (Exception e) {
            logger.error("Failed to initialize project: " + e.getMessage());
            if (logger.isVerbose()) {
                e.printStackTrace();
            }
            return 1;
        }
    }

    private void createProjectStructure(Path projectPath) throws IOException {
        // Create main directory structure
        createDirectory(projectPath);
        createDirectory(projectPath.resolve("src/main/java"));
        createDirectory(projectPath.resolve("src/main/resources"));
        createDirectory(projectPath.resolve("src/test/java"));
        createDirectory(projectPath.resolve("src/main/resources/graphql"));
        
        // Create package directories
        String[] packageParts = basePackage.split("\\.");
        Path packagePath = projectPath.resolve("src/main/java");
        for (String part : packageParts) {
            packagePath = packagePath.resolve(part);
            createDirectory(packagePath);
        }
        
        createDirectory(packagePath.resolve("model"));
        createDirectory(packagePath.resolve("controller"));
        createDirectory(packagePath.resolve("dto"));
        
        logger.info("Created project directory structure");
    }

    private void createConfigurationFiles(Path projectPath) throws IOException {
        switch (projectType.toLowerCase()) {
            case "maven" -> createMavenFiles(projectPath);
            case "gradle" -> createGradleFiles(projectPath);
            case "spring-boot" -> createSpringBootFiles(projectPath);
            default -> throw new IllegalArgumentException("Unsupported project type: " + projectType);
        }
        
        // Create GraphQL AutoGen configuration
        createGraphQLConfig(projectPath);
        
        logger.info("Created configuration files");
    }

    private void createMavenFiles(Path projectPath) throws IOException {
        String pomXml = String.format("""
            <?xml version="1.0" encoding="UTF-8"?>
            <project xmlns="http://maven.apache.org/POM/4.0.0"
                     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                     xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
                                         http://maven.apache.org/xsd/maven-4.0.0.xsd">
                <modelVersion>4.0.0</modelVersion>
                
                <groupId>%s</groupId>
                <artifactId>%s</artifactId>
                <version>1.0.0-SNAPSHOT</version>
                <packaging>jar</packaging>
                
                <name>%s</name>
                <description>GraphQL AutoGen project</description>
                
                <properties>
                    <maven.compiler.source>21</maven.compiler.source>
                    <maven.compiler.target>21</maven.compiler.target>
                    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
                    <spring.boot.version>3.3.1</spring.boot.version>
                    <graphql.autogen.version>1.0.0-SNAPSHOT</graphql.autogen.version>
                </properties>
                
                <dependencies>
                    <dependency>
                        <groupId>com.enokdev</groupId>
                        <artifactId>graphql-autogen-spring-boot-starter</artifactId>
                        <version>${graphql.autogen.version}</version>
                    </dependency>
                    
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-web</artifactId>
                        <version>${spring.boot.version}</version>
                    </dependency>
                    
                    <dependency>
                        <groupId>org.springframework.boot</groupId>
                        <artifactId>spring-boot-starter-data-jpa</artifactId>
                        <version>${spring.boot.version}</version>
                    </dependency>
                </dependencies>
                
                <build>
                    <plugins>
                        <plugin>
                            <groupId>com.enokdev</groupId>
                            <artifactId>graphql-autogen-maven-plugin</artifactId>
                            <version>${graphql.autogen.version}</version>
                            <executions>
                                <execution>
                                    <goals>
                                        <goal>generate-schema</goal>
                                    </goals>
                                    <configuration>
                                        <basePackages>
                                            <basePackage>%s</basePackage>
                                        </basePackages>
                                    </configuration>
                                </execution>
                            </executions>
                        </plugin>
                    </plugins>
                </build>
            </project>
            """, basePackage, projectName, projectName, basePackage);
            
        writeFile(projectPath.resolve("pom.xml"), pomXml);
    }

    private void createGradleFiles(Path projectPath) throws IOException {
        String buildGradle = String.format("""
            plugins {
                id 'java'
                id 'org.springframework.boot' version '3.3.1'
                id 'io.spring.dependency-management' version '1.1.0'
                id 'com.enokdev.graphql-autogen' version '1.0.0-SNAPSHOT'
            }
            
            group = '%s'
            version = '1.0.0-SNAPSHOT'
            sourceCompatibility = '21'
            
            repositories {
                mavenCentral()
                mavenLocal()
            }
            
            dependencies {
                implementation 'com.enokdev:graphql-autogen-spring-boot-starter:1.0.0-SNAPSHOT'
                implementation 'org.springframework.boot:spring-boot-starter-web'
                implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
                
                testImplementation 'org.springframework.boot:spring-boot-starter-test'
            }
            
            graphqlAutogen {
                basePackages = ['%s']
                generateInputs = true
                schemaLocation = 'src/main/resources/graphql/'
            }
            
            test {
                useJUnitPlatform()
            }
            """, basePackage, basePackage);
            
        writeFile(projectPath.resolve("build.gradle"), buildGradle);
    }

    private void createSpringBootFiles(Path projectPath) throws IOException {
        createMavenFiles(projectPath);
        
        // Create application.yml
        String applicationYml = String.format("""
            spring:
              application:
                name: %s
              
              graphql:
                autogen:
                  enabled: true
                  base-packages:
                    - %s
                  generate-inputs: true
                  generate-data-loaders: true
                  generate-pagination: true
                  schema-location: classpath:graphql/
                  naming-strategy: CAMEL_CASE
            
            logging:
              level:
                com.enokdev.graphql.autogen: DEBUG
            """, projectName, basePackage);
            
        writeFile(projectPath.resolve("src/main/resources/application.yml"), applicationYml);
    }

    private void createGraphQLConfig(Path projectPath) throws IOException {
        String config = String.format("""
            # GraphQL AutoGen Configuration
            # See: https://docs.graphql-autogen.com/configuration
            
            # Base packages to scan for GraphQL annotations
            basePackages:
              - %s.model
              - %s.dto
              - %s.controller
            
            # Generation settings
            generateInputs: true
            generateDataLoaders: true
            generatePagination: true
            generateInterfaces: true
            generateUnions: true
            
            # Output settings
            outputDirectory: src/main/resources/graphql
            schemaFileName: schema.graphqls
            
            # Naming strategy: DEFAULT, CAMEL_CASE, SNAKE_CASE, KEBAB_CASE
            namingStrategy: CAMEL_CASE
            
            # Type mappings
            typeMappings:
              java.time.LocalDateTime: DateTime
              java.time.LocalDate: Date
              java.time.LocalTime: Time
              java.math.BigDecimal: Decimal
              java.util.UUID: ID
            """, basePackage, basePackage, basePackage);
            
        writeFile(projectPath.resolve("graphql-autogen.yml"), config);
    }

    private void createExampleFiles(Path projectPath) throws IOException {
        String[] packageParts = basePackage.split("\\.");
        Path basePath = projectPath.resolve("src/main/java");
        for (String part : packageParts) {
            basePath = basePath.resolve(part);
        }
        
        // Create example entity
        String exampleEntity = String.format("""
            package %s.model;
            
            import com.enokdev.graphql.autogen.annotation.*;
            import jakarta.persistence.*;
            
            /**
             * Example book entity demonstrating GraphQL AutoGen annotations.
             */
            @Entity
            @GraphQLType(name = "Book", description = "A book in the library")
            public class Book {
                
                @Id
                @GeneratedValue(strategy = GenerationType.IDENTITY)
                @GraphQLId
                private Long id;
                
                /**
                 * The title of the book.
                 */
                @GraphQLField(description = "Book title")
                private String title;
                
                /**
                 * The ISBN of the book.
                 */
                @GraphQLField(description = "ISBN identifier")
                private String isbn;
                
                @ManyToOne
                @GraphQLField(description = "Book author")
                @GraphQLDataLoader(batchSize = 100)
                private Author author;
                
                // Constructors
                public Book() {}
                
                public Book(String title, String isbn) {
                    this.title = title;
                    this.isbn = isbn;
                }
                
                // Getters and setters
                public Long getId() { return id; }
                public void setId(Long id) { this.id = id; }
                
                public String getTitle() { return title; }
                public void setTitle(String title) { this.title = title; }
                
                public String getIsbn() { return isbn; }
                public void setIsbn(String isbn) { this.isbn = isbn; }
                
                public Author getAuthor() { return author; }
                public void setAuthor(Author author) { this.author = author; }
            }
            """, basePackage);
            
        writeFile(basePath.resolve("model/Book.java"), exampleEntity);
        
        // Create example DTO
        String exampleDto = String.format("""
            package %s.dto;
            
            import com.enokdev.graphql.autogen.annotation.*;
            
            /**
             * Input for creating a new book.
             */
            @GraphQLInput(name = "CreateBookInput", description = "Input for creating a book")
            public class CreateBookInput {
                
                @GraphQLInputField(name = "title", required = true, description = "Book title")
                private String title;
                
                @GraphQLInputField(name = "isbn", required = false, description = "ISBN identifier")
                private String isbn;
                
                @GraphQLInputField(name = "authorId", required = true, description = "Author ID")
                private Long authorId;
                
                // Constructors
                public CreateBookInput() {}
                
                // Getters and setters
                public String getTitle() { return title; }
                public void setTitle(String title) { this.title = title; }
                
                public String getIsbn() { return isbn; }
                public void setIsbn(String isbn) { this.isbn = isbn; }
                
                public Long getAuthorId() { return authorId; }
                public void setAuthorId(Long authorId) { this.authorId = authorId; }
            }
            """, basePackage);
            
        writeFile(basePath.resolve("dto/CreateBookInput.java"), exampleDto);
        
        logger.info("Created example files");
    }

    private void createDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path);
        }
    }

    private void writeFile(Path path, String content) throws IOException {
        if (Files.exists(path) && !force) {
            logger.warn("File already exists, skipping: " + path.getFileName());
            return;
        }
        
        createDirectory(path.getParent());
        Files.writeString(path, content);
        logger.debug("Created file: " + path);
    }
}

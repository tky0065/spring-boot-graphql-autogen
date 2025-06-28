package com.enokdev.graphql.cli;

import picocli.CommandLine;

import java.io.PrintWriter;

/**
 * Custom exception handler for the GraphQL AutoGen CLI.
 * Provides user-friendly error messages and proper exit codes.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class CLIExceptionHandler implements CommandLine.IExecutionExceptionHandler {

    private final CLILogger logger = new CLILogger();

    @Override
    public int handleExecutionException(
            Exception ex,
            CommandLine commandLine,
            CommandLine.ParseResult parseResult) {
        
        // Get the command that failed
        Object command = commandLine.getCommand();
        String commandName = getCommandName(command);
        
        // Handle different exception types
        if (ex instanceof IllegalArgumentException) {
            logger.error("Invalid argument: " + ex.getMessage());
            commandLine.usage(commandLine.getErr());
            return 2; // Invalid argument
        }
        
        if (ex instanceof java.nio.file.NoSuchFileException) {
            logger.error("File not found: " + ex.getMessage());
            return 3; // File not found
        }
        
        if (ex instanceof java.nio.file.AccessDeniedException) {
            logger.error("Access denied: " + ex.getMessage());
            logger.info("Try running with appropriate permissions");
            return 4; // Permission denied
        }
        
        if (ex instanceof java.io.IOException) {
            logger.error("I/O error: " + ex.getMessage());
            return 5; // I/O error
        }
        
        if (ex instanceof SecurityException) {
            logger.error("Security error: " + ex.getMessage());
            return 6; // Security error
        }
        
        if (ex instanceof RuntimeException && ex.getCause() instanceof ClassNotFoundException) {
            logger.error("Class not found: " + ex.getCause().getMessage());
            logger.info("Make sure all required dependencies are on the classpath");
            return 7; // Classpath error
        }
        
        // Generic error handling
        logger.error("Command '" + commandName + "' failed: " + ex.getMessage());
        
        // Show stack trace in verbose mode
        if (isVerboseMode(command)) {
            logger.error("Stack trace:");
            ex.printStackTrace(new PrintWriter(commandLine.getErr()));
        } else {
            logger.info("Use --verbose flag for detailed error information");
        }
        
        return 1; // Generic error
    }
    
    private String getCommandName(Object command) {
        if (command == null) {
            return "unknown";
        }
        
        CommandLine.Command annotation = command.getClass().getAnnotation(CommandLine.Command.class);
        if (annotation != null && !annotation.name().isEmpty()) {
            return annotation.name();
        }
        
        return command.getClass().getSimpleName();
    }
    
    private boolean isVerboseMode(Object command) {
        try {
            if (command instanceof GraphQLAutoGenCLI cli) {
                return cli.isVerbose();
            }
            
            // Try to get verbose flag via reflection for subcommands
            var field = command.getClass().getDeclaredField("verbose");
            field.setAccessible(true);
            return (Boolean) field.get(command);
        } catch (Exception e) {
            return false;
        }
    }
}

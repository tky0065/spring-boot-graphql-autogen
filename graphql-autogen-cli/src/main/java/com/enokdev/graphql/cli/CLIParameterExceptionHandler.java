package com.enokdev.graphql.cli;

import picocli.CommandLine;

/**
 * Custom parameter exception handler for the GraphQL AutoGen CLI.
 * Handles parameter parsing errors and provides helpful messages.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class CLIParameterExceptionHandler implements CommandLine.IParameterExceptionHandler {

    private final CLILogger logger = new CLILogger();

    @Override
    public int handleParseException(
            CommandLine.ParameterException ex,
            String[] args) {
        
        CommandLine commandLine = ex.getCommandLine();
        
        // Handle specific parameter errors
        if (ex instanceof CommandLine.MissingParameterException) {
            logger.error("Missing required parameter: " + ex.getMessage());
        } else if (ex instanceof CommandLine.UnmatchedArgumentException) {
            logger.error("Unknown parameter: " + ex.getMessage());
            logger.info("Use --help to see available options");
        } else if (ex instanceof CommandLine.ParameterException) {
            logger.error("Parameter error: " + ex.getMessage());
        }
        
        // Show usage information
        commandLine.usage(commandLine.getErr());
        
        return 2; // Parameter error exit code
    }
}

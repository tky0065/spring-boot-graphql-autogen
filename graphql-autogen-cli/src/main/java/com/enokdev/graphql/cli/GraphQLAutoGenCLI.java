package com.enokdev.graphql.cli;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

/**
 * Main CLI entry point for GraphQL AutoGen.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Command(
    name = "graphql-autogen",
    description = "Generate GraphQL schemas from Java classes",
    version = "GraphQL AutoGen CLI 1.0.0-SNAPSHOT",
    mixinStandardHelpOptions = true,
    subcommands = {
        GenerateCommand.class,
        ValidateCommand.class,
        InfoCommand.class,
        InitCommand.class
    }
)
public class GraphQLAutoGenCLI implements Callable<Integer> {

    @Option(
        names = {"-v", "--verbose"}, 
        description = "Enable verbose output"
    )
    private boolean verbose;

    @Option(
        names = {"-q", "--quiet"}, 
        description = "Enable quiet mode (minimal output)"
    )
    private boolean quiet;

    @Option(
        names = {"--no-color"}, 
        description = "Disable colored output"
    )
    private boolean noColor;

    public static void main(String[] args) {
        // Enable ANSI colors on Windows
        org.fusesource.jansi.AnsiConsole.systemInstall();
        
        int exitCode = new CommandLine(new GraphQLAutoGenCLI())
            .setExecutionExceptionHandler(new CLIExceptionHandler())
            .setParameterExceptionHandler(new CLIParameterExceptionHandler())
            .execute(args);
            
        org.fusesource.jansi.AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        // Default behavior: show help when no subcommand is specified
        new CommandLine(this).usage(System.out);
        return 0;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public boolean isQuiet() {
        return quiet;
    }

    public boolean isNoColor() {
        return noColor;
    }
}

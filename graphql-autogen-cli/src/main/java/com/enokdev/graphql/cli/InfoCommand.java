package com.enokdev.graphql.cli;

import com.enokdev.graphql.autogen.cli.CLILogger;
import picocli.CommandLine.Command;
import picocli.CommandLine.ParentCommand;

import java.util.concurrent.Callable;

/**
 * CLI command to display system information.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
@Command(
    name = "info",
    description = "Display system and project information",
    aliases = {"i"}
)
public class InfoCommand implements Callable<Integer> {

    @ParentCommand
    private GraphQLAutoGenCLI parent;

    @Override
    public Integer call() throws Exception {
        CLILogger logger = new CLILogger();
        logger.setVerbose(parent.isVerbose());
        
        logger.info("📦 GraphQL AutoGen System Information");
        logger.printSeparator();
        
        // Version information
        logger.info("📦 GraphQL AutoGen CLI: 1.0.0-SNAPSHOT");
        logger.info("☕ Java Version: " + System.getProperty("java.version"));
        logger.info("🏠 Java Home: " + System.getProperty("java.home"));
        logger.info("💻 OS: " + System.getProperty("os.name") + " " + System.getProperty("os.version"));
        logger.info("💻 Architecture: " + System.getProperty("os.arch"));
        
        // Memory information
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        
        logger.info("📊 Memory Information:");
        logger.info("  Max Memory: " + formatBytes(maxMemory));
        logger.info("  Total Memory: " + formatBytes(totalMemory));
        logger.info("  Used Memory: " + formatBytes(usedMemory));
        logger.info("  Free Memory: " + formatBytes(freeMemory));
        
        // Classpath information
        String classPath = System.getProperty("java.class.path");
        if (classPath != null && parent.isVerbose()) {
            logger.info("📂 Classpath Entries:");
            String[] entries = classPath.split(System.getProperty("path.separator"));
            for (int i = 0; i < Math.min(entries.length, 10); i++) {
                logger.debug("  " + entries[i]);
            }
            if (entries.length > 10) {
                logger.debug("  ... and " + (entries.length - 10) + " more entries");
            }
        }
        
        return 0;
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024));
        return String.format("%.1f GB", bytes / (1024.0 * 1024 * 1024));
    }
}

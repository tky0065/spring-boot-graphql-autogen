package com.enokdev.graphql.autogen.cli;

import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Simple logger for CLI applications.
 * 
 * @author GraphQL AutoGen Team
 * @since 1.0.0
 */
public class CLILogger {
    
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");
    
    private boolean verbose = false;
    private final PrintStream out = System.out;
    private final PrintStream err = System.err;
    
    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }
    
    public boolean isVerbose() {
        return verbose;
    }
    
    public void info(String message) {
        log("INFO", message, out);
    }
    
    public void warn(String message) {
        log("WARN", message, out);
    }
    
    public void error(String message) {
        log("ERROR", message, err);
    }
    
    public void debug(String message) {
        if (verbose) {
            log("DEBUG", message, out);
        }
    }
    
    public void success(String message) {
        log("SUCCESS", message, out);
    }
    
    private void log(String level, String message, PrintStream stream) {
        String timestamp = LocalDateTime.now().format(TIME_FORMAT);
        String logMessage = String.format("[%s] %s - %s", timestamp, level, message);
        stream.println(logMessage);
    }
    
    public void printSeparator() {
        out.println("----------------------------------------");
    }
    
    public void printHeader(String title) {
        printSeparator();
        out.println("  " + title);
        printSeparator();
    }
}

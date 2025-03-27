package com.curlbaby;

public class UIManager {
    // ANSI color codes
    private static final String RESET = "\033[0m";
    private static final String BOLD_CYAN = "\033[1;36m";
    private static final String BOLD_GREEN = "\033[1;32m";
    private static final String BOLD_YELLOW = "\033[1;33m";
    private static final String BOLD_RED = "\033[1;31m";
    private static final String BOLD_BLUE = "\033[1;34m";
    private static final String CYAN = "\033[0;36m";
    
    public void printWelcomeScreen() {
        System.out.println(BOLD_CYAN);
        System.out.println("┌───────────────────────────────────────────────────┐");
        System.out.println("│                                                   │");
        System.out.println("│                 🌐  Curl Baby  🌐                 │");
        System.out.println("│                                                   │");
        System.out.println("│          A stylish HTTP client for devs           │");
        System.out.println("│                                                   │");
        System.out.println("│        Simple. Elegant. Gets the job done.        │");
        System.out.println("│                                                   │");
        System.out.println("└───────────────────────────────────────────────────┘");
        System.out.println(RESET);
        
        System.out.println(BOLD_GREEN + "Type 'help' for available commands or 'exit' to quit" + RESET);
    }
    
    public void printPrompt() {
        System.out.print("\n" + BOLD_CYAN + "> " + RESET);
    }
    
    public void printHelp() {
        System.out.println("\n" + BOLD_YELLOW + "📚 Available Commands:" + RESET);
        System.out.println("  " + BOLD_CYAN + "get <url>" + RESET + " - Execute a GET request to the specified URL");
        System.out.println("  " + BOLD_CYAN + "help" + RESET + "      - Show this help message");
        System.out.println("  " + BOLD_CYAN + "exit" + RESET + "      - Exit the application");
    }
    
    public void printExitMessage() {
        System.out.println("\n" + BOLD_GREEN + "✓ Thank you for using Curl Baby. Goodbye!" + RESET);
    }
    
    public void printError(String message) {
        System.out.println(BOLD_RED + "✗ " + message + RESET);
    }
    
    public void printRequestInfo(String url) {
        System.out.println("\n" + BOLD_BLUE + "🔄 Executing GET request to " + url + RESET);
    }
    
    public void printStatusInfo(int status, String message) {
        System.out.println(BOLD_BLUE + "📊 Status: " + status + " " + message + RESET);
    }
    
    public void printHeadersSection() {
        System.out.println("\n" + BOLD_YELLOW + "📋 Headers:" + RESET);
    }
    
    public void printHeader(String key, String value) {
        System.out.println("  " + CYAN + key + ":" + RESET + " " + value);
    }
    
    public void printResponseBodySection() {
        System.out.println("\n" + BOLD_YELLOW + "📄 Response Body:" + RESET);
    }
}
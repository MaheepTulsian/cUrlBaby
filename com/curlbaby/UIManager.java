package com.curlbaby;
import java.io.File;
import com.github.lalyos.jfiglet.FigletFont;

public class UIManager {
    private static final String RESET = "\033[0m";
    private static final String BOLD_CYAN = "\033[1;36m";
    private static final String BOLD_GREEN = "\033[1;32m";
    private static final String BOLD_YELLOW = "\033[1;33m";
    private static final String BOLD_RED = "\033[1;31m";
    private static final String BOLD_BLUE = "\033[1;34m";
    private static final String BOLD_PURPLE = "\033[1;35m";
    private static final String CYAN = "\033[0;36m";
    private static final String GREEN = "\033[0;32m";
    private static final String YELLOW = "\033[0;33m";
    private static final String PINK = "\033[1;35m"; // Using bold magenta as a more compatible pink
    
    public String getReset() { return RESET; }
    public String getBoldYellow() { return BOLD_YELLOW; }

        
    
    public void printWelcomeScreen() {
        try {
            String banner = FigletFont.convertOneLine(new File("./curlbaby/fonts/slant.flf"), "curlbaby");
            String logo = FigletFont.convertOneLine("{0_0}");
            System.out.println(logo);
            // System.out.println(PINK + "     ___" + RESET);
            // System.out.println(PINK + "   ((((\\\\\\" + RESET);
            // System.out.println(PINK + "    9_9 3))" + RESET);
            // System.out.println(PINK + "    \\=  ((" + RESET);
            // System.out.println(PINK + "    __) (__" + RESET);
            // System.out.println(PINK + "  ,'  \\_/  `." + RESET);
            // System.out.println(PINK + " /    :|:    \\" + RESET);
            System.out.println(banner);
            // Command hints
            System.out.println(BOLD_GREEN + "Type" + BOLD_YELLOW +" 'help' " + BOLD_GREEN + "for available commands or" + BOLD_RED + "'exit'"+ BOLD_GREEN + "to quit" + RESET);
            System.out.println(BOLD_CYAN + "Quick Start: Try " + BOLD_YELLOW + "get your_api.com/todos/1" + RESET);
            System.out.println();
        } catch (Exception e) {
            System.out.println("==== curlbaby ====");
            System.out.println("(Failed to load figlet font)");
            e.printStackTrace();
        }
    }
    
    public void printPrompt() {
        System.out.print(BOLD_CYAN + "> " + RESET);
    }
    
    public void printInputPrompt(String message) {
        System.out.print(CYAN + message + " " + RESET);
    }
    
    public void printHelp() {
        System.out.println("\n" + BOLD_YELLOW + "📚 Available Commands:" + RESET);
        System.out.println("  " + BOLD_CYAN + "get <url>" + RESET + " - Execute a GET request to the specified URL");
        System.out.println("  " + BOLD_CYAN + "post <url>" + RESET + " - Execute a POST request with interactive body editor");
        System.out.println("  " + BOLD_CYAN + "put <url>" + RESET + " - Execute a PUT request with interactive body editor");
        System.out.println("  " + BOLD_CYAN + "delete <url>" + RESET + " - Execute a DELETE request to the specified URL");
        System.out.println("  " + BOLD_CYAN + "history" + RESET + " - Show command history");
        System.out.println("  " + BOLD_CYAN + "history clear" + RESET + " - Clear command history");
        System.out.println("  " + BOLD_CYAN + "help" + RESET + " - Show this help message");
        System.out.println("  " + BOLD_CYAN + "exit" + RESET + " - Exit the application");
    }
    
    public void printExitMessage() {
        System.out.println("\n" + BOLD_GREEN + "✓ Thank you for using cUrlBaby. Goodbye!" + RESET);
        System.out.println(PINK + "  See you next time!" + RESET);
    }
    
    public void printError(String message) {
        System.out.println(BOLD_RED + "✗ " + message + RESET);
    }
    
    public void printWarning(String message) {
        System.out.println(YELLOW + "⚠ " + message + RESET);
    }
    
    public void printInfo(String message) {
        System.out.println(CYAN + "ℹ " + message + RESET);
    }
    
    public void printSuccess(String message) {
        System.out.println(GREEN + "✓ " + message + RESET);
    }
    
    public void printRequestInfo(String url, String type) {
        System.out.println("\n" + BOLD_BLUE + "🔄 Executing " + type.toUpperCase() + " request to " + url + RESET);
    }
    
    public void printStatusInfo(int status, String message) {
        String statusPrefix = BOLD_GREEN;
        if (status >= 400) {
            statusPrefix = BOLD_RED;
        } else if (status >= 300) {
            statusPrefix = BOLD_YELLOW;
        }
        
        System.out.println(statusPrefix + "📊 Status: " + status + " " + message + RESET);
    }
    
    public void printHeadersSection() {
        System.out.println("\n" + BOLD_YELLOW + "📋 Response Headers:" + RESET);
    }
    
    public void printHeader(String key, String value) {
        System.out.println("  " + CYAN + key + ":" + RESET + " " + value);
    }
    
    public void printResponseBodySection() {
        System.out.println("\n" + BOLD_YELLOW + "📄 Response Body:" + RESET);
    }
    
    public void printRequestDetailsSection() {
        System.out.println("\n" + BOLD_PURPLE + "🔍 Request Details:" + RESET);
    }
    
    public void printRequestDetail(String key, String value) {
        System.out.println("  " + CYAN + key + ":" + RESET + " " + value);
    }
    
    public void printRequestBodySection() {
        System.out.println("\n" + BOLD_PURPLE + "📝 Request Body:" + RESET);
    }
}
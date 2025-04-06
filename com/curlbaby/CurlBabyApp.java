package com.curlbaby;

public class CurlBabyApp {
    private static final UIManager uiManager = new UIManager();
    private static final HttpRequestHandler requestHandler = new HttpRequestHandler(uiManager);
    private static final CommandProcessor commandProcessor = new CommandProcessor(uiManager, requestHandler);
    private static final CommandHistory commandHistory = new CommandHistory();
    private static final ConsoleReader consoleReader = new ConsoleReader(uiManager);
    
    public static void main(String[] args) {
        uiManager.printWelcomeScreen();
        
        // Check if we're running in an environment that supports arrow keys
        boolean supportsArrowKeys = isUnixTerminal();
        
        if (supportsArrowKeys) {
            uiManager.printInfo("📌 Use up/down arrow keys to navigate command history");
            uiManager.printInfo("📌 Use left/right arrow keys to edit current command");
        } else {
            uiManager.printWarning("Your terminal may not support arrow keys for command history navigation");
            uiManager.printInfo("Basic input mode will be used instead");
        }
        
        // Main application loop
        while (true) {
            String input;
            
            if (supportsArrowKeys) {
                // Use enhanced console reader with arrow key support
                input = consoleReader.readLine().trim();
            } else {
                // Fallback to simple prompt and Scanner for non-Unix terminals
                uiManager.printPrompt();
                input = new java.util.Scanner(System.in).nextLine().trim();
                // Still save commands to history even without navigation
                if (!input.isEmpty()) {
                    commandHistory.addCommand(input);
                }
            }
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] commandParts = input.split("\\s+", 2);
            String command = commandParts[0].toLowerCase();
            String argument = commandParts.length > 1 ? commandParts[1] : "";
            
            commandProcessor.processCommand(command, argument);
        }
    }
    
    /**
     * Check if we're running in a Unix-like terminal that would support arrow keys
     */
    private static boolean isUnixTerminal() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("nux") || osName.contains("mac");
    }
}
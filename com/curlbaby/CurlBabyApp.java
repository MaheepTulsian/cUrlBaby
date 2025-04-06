package com.curlbaby;

import java.util.Scanner;

public class CurlBabyApp {
    private static final UIManager uiManager = new UIManager();
    private static final HttpRequestHandler requestHandler = new HttpRequestHandler(uiManager);
    private static final CommandProcessor commandProcessor = new CommandProcessor(uiManager, requestHandler);
    private static final ConsoleReader consoleReader = new ConsoleReader(uiManager);
    
    public static void main(String[] args) {
        uiManager.printWelcomeScreen();
        
        boolean supportsArrowKeys = isUnixTerminal();
        
        if (supportsArrowKeys) {
            uiManager.printInfo("📌 Use up/down arrow keys to navigate command history");
            uiManager.printInfo("📌 Use left/right arrow keys to edit current command");
        } else {
            uiManager.printWarning("Your terminal may not support arrow keys for command history navigation");
            uiManager.printInfo("Basic input mode will be used instead");
        }
        
        while (true) {
            String input;
            
            if (supportsArrowKeys) {
                input = consoleReader.readLine().trim();
            } else {
                uiManager.printPrompt();
                Scanner scanner = new Scanner(System.in);
                input = scanner.nextLine().trim();
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
    
    private static boolean isUnixTerminal() {
        String osName = System.getProperty("os.name").toLowerCase();
        return osName.contains("nix") || osName.contains("nux") || osName.contains("mac");
    }
}
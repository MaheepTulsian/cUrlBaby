package com.curlbaby;

import java.util.Scanner;

public class CurlBabyApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final UIManager uiManager = new UIManager();
    private static final HttpRequestHandler requestHandler = new HttpRequestHandler();
    private static final CommandProcessor commandProcessor = new CommandProcessor(uiManager, requestHandler);
    
    public static void main(String[] args) {
        uiManager.printWelcomeScreen();
        
        while (true) {
            uiManager.printPrompt();
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] commandParts = input.split("\\s+", 2);
            String command = commandParts[0].toLowerCase();
            String argument = commandParts.length > 1 ? commandParts[1] : "";
            
            commandProcessor.processCommand(command, argument);
        }
    }
}
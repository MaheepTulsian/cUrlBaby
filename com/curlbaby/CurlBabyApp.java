package com.curlbaby;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CurlBabyApp {
    private static final UIManager uiManager = new UIManager();
    private static final HttpRequestHandler requestHandler = new HttpRequestHandler(uiManager);
    private static final CommandProcessor commandProcessor = new CommandProcessor(uiManager, requestHandler);
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<String> commandHistory = new ArrayList<>();
    private static int historyIndex = 0;
    private static final String HISTORY_FILE = System.getProperty("user.home") + "/.curlbaby_history";
    
    public static void main(String[] args) {
        uiManager.printWelcomeScreen();
        loadCommandHistory();
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            saveCommandHistory();
        }));
        
        while (true) {
            uiManager.printPrompt();
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            // Add command to history if it's not a duplicate of the last command
            if (commandHistory.isEmpty() || !commandHistory.get(commandHistory.size() - 1).equals(input)) {
                commandHistory.add(input);
                historyIndex = commandHistory.size();
            }
            
            String[] commandParts = input.split("\\s+", 2);
            String command = commandParts[0].toLowerCase();
            String argument = commandParts.length > 1 ? commandParts[1] : "";
            
            if (command.equals("history")) {
                if (argument.equals("clear")) {
                    commandHistory.clear();
                    historyIndex = 0;
                    uiManager.printSuccess("Command history cleared");
                } else if (argument.isEmpty()) {
                    printHistory();
                }
                continue;
            }
            
            commandProcessor.processCommand(command, argument);
        }
    }
    
    private static void loadCommandHistory() {
        File historyFile = new File(HISTORY_FILE);
        if (historyFile.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (!line.trim().isEmpty()) {
                        commandHistory.add(line);
                    }
                }
                historyIndex = commandHistory.size();
            } catch (IOException e) {
                uiManager.printWarning("Could not load command history: " + e.getMessage());
            }
        }
    }
    
    private static void saveCommandHistory() {
        try {
            File historyFile = new File(HISTORY_FILE);
            if (!historyFile.exists()) {
                historyFile.createNewFile();
            }
            
            // Keep only the last 100 commands
            List<String> historyToSave = commandHistory;
            if (commandHistory.size() > 100) {
                historyToSave = commandHistory.subList(commandHistory.size() - 100, commandHistory.size());
            }
            
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(historyFile))) {
                for (String cmd : historyToSave) {
                    writer.write(cmd);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error saving command history: " + e.getMessage());
        }
    }
    
    private static void printHistory() {
        if (commandHistory.isEmpty()) {
            uiManager.printInfo("Command history is empty");
            return;
        }
        
        System.out.println("\n" + uiManager.getBoldYellow() + "Command History:" + uiManager.getReset());
        for (int i = 0; i < commandHistory.size(); i++) {
            System.out.printf("  %3d  %s\n", i + 1, commandHistory.get(i));
        }
    }
}
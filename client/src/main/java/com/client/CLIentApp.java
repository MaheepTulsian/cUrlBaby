package com.client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

public class CLIentApp {
    public static void main(String[] args) {
        printWelcomeScreen();
        
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.print("\n\033[1;36m>\033[0m ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            String[] commandParts = input.split("\\s+", 2);
            String command = commandParts[0].toLowerCase();
            
            switch (command) {
                case "exit":
                    System.out.println("\n\033[1;32m✓ Thank you for using SimpleHTTP CLI. Goodbye!\033[0m");
                    System.exit(0);
                    break;
                case "help":
                    printHelp();
                    break;
                case "get":
                    if (commandParts.length < 2) {
                        System.out.println("\033[1;31m✗ Usage: get <url>\033[0m");
                    } else {
                        executeGetRequest(commandParts[1]);
                    }
                    break;
                default:
                    System.out.println("\033[1;31m✗ Unknown command: " + command + "\033[0m");
                    System.out.println("Type 'help' for available commands");
            }
        }
    }
    
    private static void printWelcomeScreen() {
        System.out.println("\033[1;36m");
        System.out.println("┌───────────────────────────────────────────┐");
        System.out.println("│                                           │");
        System.out.println("│           🌐 SimpleHTTP CLI 🌐            │");
        System.out.println("│                                           │");
        System.out.println("│       A lightweight HTTP GET client       │");
        System.out.println("│                                           │");
        System.out.println("└───────────────────────────────────────────┘");
        System.out.println("\033[0m");
        System.out.println("\033[1;32mType 'help' for available commands or 'exit' to quit\033[0m");
    }
    
    private static void printHelp() {
        System.out.println("\n\033[1;33m📚 Available Commands:\033[0m");
        System.out.println("  \033[1;36mget <url>\033[0m - Execute a GET request to the specified URL");
        System.out.println("  \033[1;36mhelp\033[0m      - Show this help message");
        System.out.println("  \033[1;36mexit\033[0m      - Exit the application");
    }
    
    private static void executeGetRequest(String urlString) {
        HttpURLConnection connection = null;
        try {
            // Normalize URL (add http:// if missing)
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "http://" + urlString;
            }
            
            System.out.println("\n\033[1;34m🔄 Executing GET request to " + urlString + "\033[0m");
            
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            
            int status = connection.getResponseCode();
            System.out.println("\033[1;34m📊 Status: " + status + " " + connection.getResponseMessage() + "\033[0m");
            
            // Print headers
            System.out.println("\n\033[1;33m📋 Headers:\033[0m");
            connection.getHeaderFields().forEach((key, values) -> {
                if (key != null) {
                    System.out.println("  \033[0;36m" + key + ":\033[0m " + String.join(", ", values));
                }
            });
            
            // Read and print response
            BufferedReader reader;
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            
            System.out.println("\n\033[1;33m📄 Response Body:\033[0m");
            String line;
            StringBuilder responseContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();
            
            // Pretty print the response if it's JSON
            String response = responseContent.toString();
            if (response.trim().startsWith("{") || response.trim().startsWith("[")) {
                try {
                    // Simple indentation for JSON (very basic)
                    String formatted = formatJson(response);
                    System.out.println(formatted);
                } catch (Exception e) {
                    System.out.println(response);
                }
            } else {
                System.out.println(response);
            }
            
        } catch (IOException e) {
            System.out.println("\033[1;31m✗ Error: " + e.getMessage() + "\033[0m");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    private static String formatJson(String json) {
        // Very simple JSON formatter
        int indentLevel = 0;
        StringBuilder result = new StringBuilder();
        boolean inQuotes = false;
        
        for (char c : json.toCharArray()) {
            if (c == '"' && (result.length() == 0 || result.charAt(result.length() - 1) != '\\')) {
                inQuotes = !inQuotes;
                result.append(c);
            } else if (!inQuotes && (c == '{' || c == '[')) {
                indentLevel++;
                result.append(c).append("\n").append("  ".repeat(indentLevel));
            } else if (!inQuotes && (c == '}' || c == ']')) {
                indentLevel--;
                result.append("\n").append("  ".repeat(indentLevel)).append(c);
            } else if (!inQuotes && c == ',') {
                result.append(c).append("\n").append("  ".repeat(indentLevel));
            } else if (!inQuotes && c == ':') {
                result.append(c).append(" ");
            } else {
                result.append(c);
            }
        }
        
        return result.toString();
    }
}
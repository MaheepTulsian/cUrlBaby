package com.curlbaby;

public class CommandProcessor {
    private final UIManager uiManager;
    private final HttpRequestHandler requestHandler;
    private final ApiCollectionManager apiCollectionManager;
    private final ApiCollectionCommands apiCollectionCommands;
    private final EnvironmentManager environmentManager;
    private final EnvironmentCommands environmentCommands;
    
    public CommandProcessor(UIManager uiManager, HttpRequestHandler requestHandler) {
        this.uiManager = uiManager;
        this.requestHandler = requestHandler;
        this.apiCollectionManager = new ApiCollectionManager(uiManager);
        this.apiCollectionCommands = new ApiCollectionCommands(apiCollectionManager, uiManager, requestHandler);
        this.environmentManager = new EnvironmentManager(uiManager);
        this.environmentCommands = new EnvironmentCommands(environmentManager, uiManager);
    }
    
    public void processCommand(String command, String argument) {
        // First try to substitute environment variables in the argument
        if (environmentManager.getActiveEnvironment() != null) {
            argument = environmentManager.substituteVariables(argument);
        }
        
        switch (command) {
            case "exit":
                uiManager.printExitMessage();
                // Close database connections before exit
                apiCollectionManager.close();
                environmentManager.close();
                System.exit(0);
                break;
            case "help":
                printHelp();
                break;
            case "get":
                if (argument.isEmpty()) {
                    uiManager.printError("Usage: get <url>");
                } else {
                    requestHandler.executeGetRequest(argument);
                }
                break;
            case "post": 
                if (argument.isEmpty()) {
                    uiManager.printError("Usage: post <url>");
                } else {
                    requestHandler.executePostRequest(argument);
                }
                break;
            case "put":
                if (argument.isEmpty()) {
                    uiManager.printError("Usage: put <url>");
                } else {
                    requestHandler.executePutRequest(argument);
                }
                break;
            case "delete":
                if (argument.isEmpty()) {
                    uiManager.printError("Usage: delete <url>");
                } else {
                    requestHandler.executeDeleteRequest(argument);
                }
                break;
            // API collection commands
            case "group":
            case "api":
            case "run":
                apiCollectionCommands.handleCommand(command, argument);
                break;
            // Environment commands
            case "env":
                environmentCommands.handleCommand(argument);
                break;
            default:
                uiManager.printError("Unknown command: " + command);
                System.out.println("Type 'help' for available commands");
        }
    }
    
    private void printHelp() {
        uiManager.printInfo("\nBasic Commands:");
        uiManager.printInfo("  help - Display this help message");
        uiManager.printInfo("  exit - Exit the application");
        
        uiManager.printInfo("\nRequest Commands:");
        uiManager.printInfo("  get <url> - Execute a GET request to the specified URL");
        uiManager.printInfo("  post <url> - Execute a POST request to the specified URL");
        uiManager.printInfo("  put <url> - Execute a PUT request to the specified URL");
        uiManager.printInfo("  delete <url> - Execute a DELETE request to the specified URL");
        
        uiManager.printInfo("\nAPI Collection Commands:");
        uiManager.printInfo("  group - Manage API groups");
        uiManager.printInfo("    group create <name> - Create a new API group");
        uiManager.printInfo("    group list - List all API groups");
        uiManager.printInfo("    group show <id|name> - Show details of a specific group");
        uiManager.printInfo("    group rename <id> <new_name> - Rename a group");
        uiManager.printInfo("    group delete <id> - Delete a group");
        
        uiManager.printInfo("\n  api - Manage API requests");
        uiManager.printInfo("    api save <group_id|group_name> <name> - Save current or new API request to a group");
        uiManager.printInfo("    api list <group_id|group_name> - List all APIs in a group");
        uiManager.printInfo("    api show <id> - Show details of a specific API request");
        uiManager.printInfo("    api delete <id> - Delete an API request");
        
        uiManager.printInfo("\n  run <id> - Execute a saved API request");
        
        uiManager.printInfo("\nEnvironment Commands:");
        uiManager.printInfo("  env - Show active environment");
        uiManager.printInfo("  env create <name> - Create a new environment");
        uiManager.printInfo("  env list - List all environments");
        uiManager.printInfo("  env use <name> - Set active environment");
        uiManager.printInfo("  env show <id|name> - Show environment and its variables");
        uiManager.printInfo("  env delete <id> - Delete an environment");
        uiManager.printInfo("  env set <var> <value> - Set variable in active environment");
        uiManager.printInfo("  env set <env> <var> <value> - Set variable in specified environment");
        uiManager.printInfo("  env unset <var> - Unset variable in active environment");
        uiManager.printInfo("  env unset <env> <var> - Unset variable in specified environment");
        
        uiManager.printInfo("\nHistory Commands:");
        uiManager.printInfo("  history - Display command history");
        uiManager.printInfo("  history clear - Clear command history");
        
        uiManager.printInfo("\nEnvironment Variables:");
        uiManager.printInfo("  Use {{variable_name}} in requests to substitute environment variables");
        uiManager.printInfo("  Example: get api.example.com/users/{{user_id}}");
    }
}
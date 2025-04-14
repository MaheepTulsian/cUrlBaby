package com.curlbaby;

import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnvironmentCommands {
    private final EnvironmentManager environmentManager;
    private final UIManager uiManager;
    private final Scanner scanner;
    
    public EnvironmentCommands(EnvironmentManager environmentManager, UIManager uiManager) {
        this.environmentManager = environmentManager;
        this.uiManager = uiManager;
        this.scanner = new Scanner(System.in);
    }
    
    public void handleCommand(String argument) {
        if (argument.isEmpty()) {
            showActiveEnvironment();
            return;
        }
        
        String[] parts = argument.split("\\s+", 2);
        String subCommand = parts[0].toLowerCase();
        String subArgument = parts.length > 1 ? parts[1] : "";
        
        switch (subCommand) {
            case "create":
                createEnvironment(subArgument);
                break;
            case "list":
                listEnvironments();
                break;
            case "use":
                useEnvironment(subArgument);
                break;
            case "show":
                showEnvironment(subArgument);
                break;
            case "delete":
                deleteEnvironment(subArgument);
                break;
            case "set":
                setVariable(subArgument);
                break;
            case "unset":
                unsetVariable(subArgument);
                break;
            default:
                uiManager.printError("Unknown environment command: " + subCommand);
                printHelp();
        }
    }
    
    private void printHelp() {
        uiManager.printInfo("Environment Commands:");
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
    }
    
    private void showActiveEnvironment() {
        String active = environmentManager.getActiveEnvironment();
        if (active == null) {
            uiManager.printInfo("No active environment set. Use 'env use <name>' to set one.");
            return;
        }
        
        uiManager.printInfo("Active Environment: " + active);
        Map<String, String> variables = environmentManager.getActiveVariables();
        
        if (variables.isEmpty()) {
            uiManager.printInfo("No variables defined.");
        } else {
            uiManager.printInfo("Variables:");
            for (Map.Entry<String, String> var : variables.entrySet()) {
                System.out.printf("  %s: %s\n", var.getKey(), var.getValue());
            }
        }
        
        uiManager.printInfo("\nTip: Use variables in requests like {{variable_name}}");
    }
    
    private void createEnvironment(String argument) {
        if (argument.isEmpty()) {
            uiManager.printError("Environment name is required");
            return;
        }
        
        String[] parts = argument.split("\\s+", 2);
        String name = parts[0];
        String description = parts.length > 1 ? parts[1] : "";
        
        if (description.isEmpty()) {
            uiManager.printInputPrompt("Enter environment description (optional):");
            description = scanner.nextLine().trim();
        }
        
        if (environmentManager.createEnvironment(name, description)) {
            uiManager.printSuccess("Environment created: " + name);
            
            // If this is the first environment, set it as active
            List<Map<String, Object>> envs = environmentManager.getAllEnvironments();
            if (envs.size() == 1) {
                environmentManager.setActiveEnvironment(name);
            }
        }
    }
    
    private void listEnvironments() {
        List<Map<String, Object>> environments = environmentManager.getAllEnvironments();
        
        if (environments.isEmpty()) {
            uiManager.printInfo("No environments found. Create one using 'env create <name>'");
            return;
        }
        
        uiManager.printInfo("Environments:");
        for (Map<String, Object> env : environments) {
            int id = (int) env.get("id");
            String name = (String) env.get("name");
            String description = (String) env.get("description");
            boolean isActive = (boolean) env.get("is_active");
            
            System.out.printf("  %d. %s%s%s\n", 
                    id, 
                    name, 
                    (isActive ? " (ACTIVE)" : ""),
                    (description != null && !description.isEmpty() ? " - " + description : ""));
        }
    }
    
    private void useEnvironment(String argument) {
        if (argument.isEmpty()) {
            uiManager.printError("Environment name is required");
            return;
        }
        
        environmentManager.setActiveEnvironment(argument);
    }
    
    private void showEnvironment(String argument) {
        if (argument.isEmpty()) {
            uiManager.printError("Environment ID or name is required");
            return;
        }
        
        Map<String, Object> env;
        int envId;
        
        try {
            envId = Integer.parseInt(argument);
            env = environmentManager.getEnvironmentById(envId);
        } catch (NumberFormatException e) {
            // Treat as a name instead
            Integer id = environmentManager.getEnvironmentIdByName(argument);
            if (id == null) {
                uiManager.printError("Environment not found: " + argument);
                return;
            }
            envId = id;
            env = environmentManager.getEnvironmentById(envId);
        }
        
        if (env == null) {
            uiManager.printError("Environment not found");
            return;
        }
        
        String name = (String) env.get("name");
        String description = (String) env.get("description");
        boolean isActive = (boolean) env.get("is_active");
        
        uiManager.printInfo("Environment Details:");
        System.out.println("  ID: " + env.get("id"));
        System.out.println("  Name: " + name);
        if (description != null && !description.isEmpty()) {
            System.out.println("  Description: " + description);
        }
        System.out.println("  Status: " + (isActive ? "Active" : "Inactive"));
        
        // List variables in this environment
        List<Map<String, Object>> variables = environmentManager.getVariablesByEnvironmentId(envId);
        if (variables.isEmpty()) {
            uiManager.printInfo("No variables defined in this environment");
        } else {
            uiManager.printInfo("Variables:");
            for (Map<String, Object> var : variables) {
                System.out.printf("  %s: %s\n", var.get("name"), var.get("value"));
            }
        }
    }
    
    private void deleteEnvironment(String argument) {
        if (argument.isEmpty()) {
            uiManager.printError("Environment ID is required");
            return;
        }
        
        try {
            int envId = Integer.parseInt(argument);
            
            // Confirm deletion
            uiManager.printWarning("This will delete the environment and all its variables.");
            uiManager.printInputPrompt("Are you sure? (y/n):");
            String confirm = scanner.nextLine().trim().toLowerCase();
            
            if (confirm.equals("y") || confirm.equals("yes")) {
                if (environmentManager.deleteEnvironment(envId)) {
                    uiManager.printSuccess("Environment deleted");
                } else {
                    uiManager.printError("Failed to delete environment. Environment might not exist.");
                }
            } else {
                uiManager.printInfo("Deletion cancelled");
            }
        } catch (NumberFormatException e) {
            uiManager.printError("Invalid environment ID: " + argument);
        }
    }
    
    private void setVariable(String argument) {
        if (argument.isEmpty()) {
            uiManager.printError("Variable name and value are required");
            return;
        }
        
        // Check if we're setting in active environment or specific environment
        Pattern specificPattern = Pattern.compile("([^\\s]+)\\s+([^\\s]+)\\s+(.+)");
        Pattern activePattern = Pattern.compile("([^\\s]+)\\s+(.+)");
        
        Matcher specificMatcher = specificPattern.matcher(argument);
        Matcher activeMatcher = activePattern.matcher(argument);
        
        if (specificMatcher.matches()) {
            // Set in specific environment: env set <env> <var> <value>
            String envName = specificMatcher.group(1);
            String varName = specificMatcher.group(2);
            String varValue = specificMatcher.group(3);
            
            if (environmentManager.setVariable(envName, varName, varValue)) {
                uiManager.printSuccess(String.format("Set %s = %s in environment '%s'", 
                        varName, varValue, envName));
            }
        } else if (activeMatcher.matches()) {
            // Set in active environment: env set <var> <value>
            String activeEnv = environmentManager.getActiveEnvironment();
            if (activeEnv == null) {
                uiManager.printError("No active environment. Use 'env use <name>' to set one.");
                return;
            }
            
            String varName = activeMatcher.group(1);
            String varValue = activeMatcher.group(2);
            
            if (environmentManager.setVariable(activeEnv, varName, varValue)) {
                uiManager.printSuccess(String.format("Set %s = %s in active environment", 
                        varName, varValue));
            }
        } else {
            uiManager.printError("Usage: env set <var> <value> or env set <env> <var> <value>");
        }
    }
    
    private void unsetVariable(String argument) {
        if (argument.isEmpty()) {
            uiManager.printError("Variable name is required");
            return;
        }
        
        // Check if we're unsetting in active environment or specific environment
        Pattern pattern = Pattern.compile("([^\\s]+)\\s+([^\\s]+)");
        Matcher matcher = pattern.matcher(argument);
        
        if (matcher.matches()) {
            // Unset in specific environment: env unset <env> <var>
            String envName = matcher.group(1);
            String varName = matcher.group(2);
            
            if (environmentManager.deleteVariable(envName, varName)) {
                uiManager.printSuccess(String.format("Removed variable '%s' from environment '%s'", 
                        varName, envName));
            } else {
                uiManager.printError(String.format("Variable '%s' not found in environment '%s'", 
                        varName, envName));
            }
        } else {
            // Unset in active environment: env unset <var>
            String activeEnv = environmentManager.getActiveEnvironment();
            if (activeEnv == null) {
                uiManager.printError("No active environment. Use 'env use <name>' to set one.");
                return;
            }
            
            if (environmentManager.deleteVariable(activeEnv, argument)) {
                uiManager.printSuccess(String.format("Removed variable '%s' from active environment", 
                        argument));
            } else {
                uiManager.printError(String.format("Variable '%s' not found in active environment", 
                        argument));
            }
        }
    }
}
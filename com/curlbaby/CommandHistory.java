package com.curlbaby;

import java.util.ArrayList;
import java.util.List;

public class CommandHistory {
    private final List<String> history;
    private int currentIndex;
    
    public CommandHistory() {
        this.history = new ArrayList<>();
        this.currentIndex = 0;
    }
    
    public void addCommand(String command) {
        if (!command.trim().isEmpty()) {
            // Only add if it's not a duplicate of the most recent command
            if (history.isEmpty() || !history.get(history.size() - 1).equals(command)) {
                history.add(command);
            }
            // Set current index to point just after the last command
            currentIndex = history.size();
        }
    }
    
    public String getPreviousCommand() {
        if (history.isEmpty()) {
            return "";
        }
        
        // If we're at the end of the history, we're not looking at any command yet
        if (currentIndex == history.size()) {
            currentIndex--;
        } else if (currentIndex > 0) {
            // Move to the previous command only if we're not at the beginning
            currentIndex--;
        }
        
        // Return the command at the current index
        return history.get(currentIndex);
    }
    
    public String getNextCommand() {
        if (history.isEmpty() || currentIndex >= history.size() - 1) {
            // If at the end or beyond the last command, move to end and return empty
            currentIndex = history.size();
            return "";
        }
        
        // Move to the next command and return it
        currentIndex++;
        return history.get(currentIndex);
    }
    
    // Used to reset history navigation after adding a new command
    public void resetNavigation() {
        currentIndex = history.size();
    }
    
    public void clear() {
        history.clear();
        currentIndex = 0;
    }
    
    public int size() {
        return history.size();
    }
    
    public List<String> getAll() {
        return new ArrayList<>(history);
    }
}
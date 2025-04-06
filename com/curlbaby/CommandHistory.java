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
            if (history.isEmpty() || !history.get(history.size() - 1).equals(command)) {
                history.add(command);
            }
            currentIndex = history.size();
        }
    }
    
    public String getPreviousCommand() {
        if (history.isEmpty() || currentIndex <= 0) {
            return "";
        }
        
        currentIndex--;
        return history.get(currentIndex);
    }
    
    public String getNextCommand() {
        if (history.isEmpty() || currentIndex >= history.size()) {
            return "";
        }
        
        currentIndex++;
        if (currentIndex == history.size()) {
            return "";
        }
        return history.get(currentIndex);
    }
    
    public int size() {
        return history.size();
    }
}
# cUrlBaby 🍼

> **API Testing from the Command Line** - Making HTTP requests as simple as child's play!

A lightweight, Java-based command-line HTTP client that brings Postman-like functionality to your terminal. Perfect for developers who prefer CLI tools, DevOps automation, and CI/CD pipeline integration.

## 🚀 Features

- **HTTP Methods**: Full support for GET, POST, PUT, DELETE requests
- **Interactive JSON Editor**: Built-in editor with syntax highlighting and formatting
- **API Collections**: Organize and save requests in groups for easy reuse
- **Command History**: Navigate through previous commands with arrow keys
- **Response Formatting**: Automatic JSON prettification and colored output
- **Request Management**: Save, modify, and execute requests with a single command
- **Terminal-First Design**: Built for CLI lovers and automation workflows

## 🎯 Who Is This For?

- **CLI Developers** who prefer terminal-based tools over GUI applications
- **DevOps Engineers** integrating API tests into CI/CD pipelines
- **Backend Developers** testing APIs during development
- **System Administrators** working in headless environments
- **Automation Engineers** scripting API interactions

## 📦 Installation

### Prerequisites
- Java 8 or higher (JRE or JDK)
- Terminal with ANSI color support (most modern terminals)

### Quick Start
```bash
# Clone the repository
git clone https://github.com/yourusername/curlbaby.git

# Navigate to directory
cd curlbaby

# Make executable and run
chmod +x curlbaby.sh
./curlbaby.sh
```

That's it! cUrlBaby will compile itself and launch.

## 🏃‍♂️ Quick Examples

### Basic GET Request
```bash
> get jsonplaceholder.typicode.com/users/1
```

### POST with JSON Body
```bash
> post api.example.com/users
# Follow interactive prompts or use 'json' for the editor
```

### Save and Reuse Requests
```bash
> group create MyAPIs "My API collection"
> api save MyAPIs LoginRequest
> run 1  # Execute saved request by ID
```

## 📚 Documentation

### Basic Commands

| Command | Description | Example |
|---------|-------------|---------|
| `get <url>` | Execute GET request | `get api.github.com/user` |
| `post <url>` | Execute POST request | `post api.example.com/data` |
| `put <url>` | Execute PUT request | `put api.example.com/users/1` |
| `delete <url>` | Execute DELETE request | `delete api.example.com/users/1` |
| `help` | Show available commands | `help` |
| `exit` | Quit application | `exit` |

### API Collection Management

| Command | Description | Example |
|---------|-------------|---------|
| `group create <name>` | Create new API group | `group create AuthAPI` |
| `group list` | List all groups | `group list` |
| `group show <id\|name>` | Show group details | `group show AuthAPI` |
| `api save <group> <name>` | Save request to group | `api save AuthAPI Login` |
| `api list <group>` | List requests in group | `api list AuthAPI` |
| `run <id>` | Execute saved request | `run 5` |

### JSON Editor Commands

When editing request bodies, cUrlBaby provides a powerful JSON editor:

| Command | Description |
|---------|-------------|
| `:h` | Show help |
| `:p` | Preview current JSON |
| `:l` | List lines with numbers |
| `:e <line>` | Edit specific line |
| `:f` | Format/prettify JSON |
| `:s` | Save and exit |
| `:paste` | Multi-line paste mode |

## 🏗️ Architecture Overview

cUrlBaby follows a modular architecture designed for maintainability and extensibility:

```
com.curlbaby/
├── CurlBabyApp.java          # Main application entry point
├── CommandProcessor.java     # Command parsing and routing
├── HttpRequestHandler.java   # HTTP client implementation
├── UIManager.java           # Terminal UI and colors
├── ApiCollectionManager.java # Database operations for collections
├── ApiCollectionCommands.java # Collection command handlers
├── JsonFormatter.java       # JSON prettification
├── SimpleJsonEditor.java    # Interactive JSON editor
├── ConsoleReader.java       # Terminal input handling
├── CommandHistory.java      # In-memory command history
└── CommandHistoryDatabase.java # Persistent command history
```

### Key Components

1. **CurlBabyApp**: Main entry point that initializes components and handles the main application loop
2. **CommandProcessor**: Routes user input to appropriate handlers
3. **HttpRequestHandler**: Manages HTTP requests using Java's built-in HTTP client
4. **UIManager**: Handles all terminal output with colors and formatting
5. **ApiCollectionManager**: SQLite-based persistence for saved requests and groups
6. **JsonFormatter**: Custom JSON parser and prettifier
7. **SimpleJsonEditor**: Interactive text editor for JSON content

## 🛠️ Development Setup

### Building from Source

cUrlBaby uses a simple build system via the `curlbaby.sh` script:

```bash
# The script automatically:
# 1. Creates necessary directories
# 2. Compiles Java source files
# 3. Sets up classpath with dependencies
# 4. Launches the application
./curlbaby.sh
```

### Project Structure

```
curlbaby/
├── curlbaby.sh              # Build and launch script
├── .gitignore              # Git ignore rules
├── com/curlbaby/           # Java source files
├── curlbaby/
│   ├── lib/                # JAR dependencies
│   └── target/classes/     # Compiled Java classes
└── backup/                 # Backup dependencies
```

### Dependencies

- **SQLite JDBC**: For API collection persistence
- **Java Standard Library**: HTTP client, JSON handling, terminal I/O

## 🤝 Contributing

We welcome contributions! Here's how to get started:

### Setting Up Development Environment

1. **Fork and Clone**
   ```bash
   git clone https://github.com/yourusername/curlbaby.git
   cd curlbaby
   ```

2. **Understand the Codebase**
   - Start with `CurlBabyApp.java` to understand the main flow
   - Look at `CommandProcessor.java` to see how commands are routed
   - Examine `HttpRequestHandler.java` for HTTP functionality

3. **Test Your Changes**
   ```bash
   ./curlbaby.sh
   # Test various commands to ensure everything works
   ```

### Code Style Guidelines

- **Java Conventions**: Follow standard Java naming conventions
- **Modular Design**: Keep classes focused on single responsibilities
- **Error Handling**: Always handle exceptions gracefully with user-friendly messages
- **Terminal UX**: Use `UIManager` for all output to maintain consistent formatting

### Common Contribution Areas

1. **New HTTP Methods**: Add support for PATCH, OPTIONS, HEAD
2. **Authentication**: Implement OAuth, API key management
3. **Import/Export**: Add Postman collection import/export
4. **Response Processing**: Add response validation, testing frameworks
5. **Performance**: Optimize startup time, memory usage

### Adding a New Command

Example: Adding a `PATCH` command

1. **Add to CommandProcessor**:
   ```java
   case "patch":
       if (argument.isEmpty()) {
           uiManager.printError("Usage: patch <url>");
       } else {
           requestHandler.executePatchRequest(argument);
       }
       break;
   ```

2. **Implement in HttpRequestHandler**:
   ```java
   public void executePatchRequest(String urlString) {
       Request request = new Request("PATCH", urlString);
       // Add body and headers handling similar to POST/PUT
       executeRequest(request);
   }
   ```

3. **Update Help Text** in `CommandProcessor.printHelp()`

### Database Schema

cUrlBaby uses SQLite for persistence:

```sql
-- API Groups
CREATE TABLE api_groups (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT UNIQUE NOT NULL,
    description TEXT
);

-- API Requests  
CREATE TABLE api_requests (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_id INTEGER NOT NULL,
    name TEXT NOT NULL,
    method TEXT NOT NULL,
    url TEXT NOT NULL,
    headers TEXT,  -- JSON string
    body TEXT,
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY(group_id) REFERENCES api_groups(id) ON DELETE CASCADE
);
```

## 🐛 Troubleshooting

### Common Issues

1. **Java Not Found**
   - Install Java 8+ and ensure it's in your PATH
   - Test with `java -version`

2. **Compilation Errors**
   - Ensure all `.java` files are in the correct package structure
   - Check that SQLite JDBC driver is in `curlbaby/lib/`

3. **Terminal Colors Not Working**
   - Use a terminal that supports ANSI escape codes
   - Try Windows Terminal, iTerm2, or modern Linux terminals

4. **Database Issues**
   - Database files are stored in `~/.curlbaby/`
   - Delete the directory to reset all data

### Getting Help

- **Issues**: Report bugs and feature requests on GitHub Issues
- **Discussions**: Join GitHub Discussions for questions and ideas
- **Code Review**: All PRs are reviewed for quality and consistency

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

## 🎉 Acknowledgments

- Inspired by Postman's API testing workflow
- Built for the command-line loving developer community
- Designed with automation and CI/CD integration in mind

---

**Happy API Testing!** 🚀

Made with ❤️ for developers who live in the terminal.
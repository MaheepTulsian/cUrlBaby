# Curl Baby

A lightweight HTTP GET client with a colorful CLI interface.

## Features

- Simple GET request execution
- Beautiful command-line interface with colors
- Response headers display
- JSON response formatting
- Error handling

## Installation

1. Clone this repository:
   ```
   git clone https://github.com/yourusername/curlbaby.git
   cd curlbaby
   ```

2. Make the script executable:
   ```
   chmod +x curlbaby.sh
   ```

## Directory Structure

```
curlbaby/
├── curlbaby.sh
├── README.md
└── src/
    └── main/
        └── java/
            └── com/
                └── curlbaby/
                    └── CurlBabyApp.java
```

## Usage

Run the application:

```
./curlbaby.sh
```

### Available Commands

- `get <url>` - Execute a GET request to the specified URL
  - Example: `get api.example.com/users`
  - Note: `http://` will be added automatically if not included in the URL
- `help` - Display available commands
- `exit` - Exit the application

## Example

```
> get jsonplaceholder.typicode.com/users/1

🔄 Executing GET request to http://jsonplaceholder.typicode.com/users/1

📊 Status: 200 OK

📋 Headers:
  Cache-Control: max-age=43200
  Content-Type: application/json; charset=utf-8
  ... (more headers)

📄 Response Body:
{
  "id": 1,
  "name": "Leanne Graham",
  "username": "Bret",
  "email": "Sincere@april.biz",
  ...
}
```

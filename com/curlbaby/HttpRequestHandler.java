package com.curlbaby;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HttpRequestHandler {
    private final UIManager uiManager;
    private final JsonFormatter jsonFormatter;
    private final Scanner scanner;
    
    public HttpRequestHandler(UIManager uiManager) {
        this.uiManager = uiManager;
        this.jsonFormatter = new JsonFormatter();
        this.scanner = new Scanner(System.in);
    }
    
    public void executeGetRequest(String urlString) {
        Request request = new Request("GET", urlString);
        executeRequest(request);
    }

    public void executePostRequest(String urlString) {
        Request request = new Request("POST", urlString);
         
        uiManager.printInputPrompt("Content-Type (default: application/json):");
        String contentType = scanner.nextLine().trim();
        if (contentType.isEmpty()) {
            contentType = "application/json";
        }
        request.addHeader("Content-Type", contentType);
         
        uiManager.printInputPrompt("Request body (enter 'json' for JSON editor, or type directly):");
        String bodyInput = scanner.nextLine().trim();
        
        if (bodyInput.equalsIgnoreCase("json")) {
            SimpleJsonEditor editor = new SimpleJsonEditor(uiManager, scanner, jsonFormatter);
            String jsonBody = editor.edit();
            request.setBody(jsonBody);
        } else if (!bodyInput.isEmpty()) {
            request.setBody(bodyInput);
        }
        
        boolean addingHeaders = true;
        while (addingHeaders) {
            uiManager.printInputPrompt("Add header? (y/n):");
            String addHeader = scanner.nextLine().trim().toLowerCase();
            if (addHeader.equals("y")) {
                uiManager.printInputPrompt("Header name:");
                String headerName = scanner.nextLine().trim();
                uiManager.printInputPrompt("Header value:");
                String headerValue = scanner.nextLine().trim();
                request.addHeader(headerName, headerValue);
            } else {
                addingHeaders = false;
            }
        }
        
        executeRequest(request);
    }
    
    public void executePutRequest(String urlString) {
        Request request = new Request("PUT", urlString);
         
        uiManager.printInfo("PUT request follows the same flow as POST");
        
        uiManager.printInputPrompt("Content-Type (default: application/json):");
        String contentType = scanner.nextLine().trim();
        if (contentType.isEmpty()) {
            contentType = "application/json";
        }
        request.addHeader("Content-Type", contentType);
        
        
        uiManager.printInputPrompt("Request body (enter 'json' for JSON editor, or type directly):");
        String bodyInput = scanner.nextLine().trim();
        
        if (bodyInput.equalsIgnoreCase("json")) {
            SimpleJsonEditor editor = new SimpleJsonEditor(uiManager, scanner, jsonFormatter);
            String jsonBody = editor.edit();
            request.setBody(jsonBody);
        } else if (!bodyInput.isEmpty()) {
            request.setBody(bodyInput);
        }
        
        boolean addingHeaders = true;
        while (addingHeaders) {
            uiManager.printInputPrompt("Add header? (y/n):");
            String addHeader = scanner.nextLine().trim().toLowerCase();
            if (addHeader.equals("y")) {
                uiManager.printInputPrompt("Header name:");
                String headerName = scanner.nextLine().trim();
                uiManager.printInputPrompt("Header value:");
                String headerValue = scanner.nextLine().trim();
                request.addHeader(headerName, headerValue);
            } else {
                addingHeaders = false;
            }
        }
        
        executeRequest(request);
    }
    
    public void executeDeleteRequest(String urlString) {
        Request request = new Request("DELETE", urlString);
        executeRequest(request);
    }
    
    private void executeRequest(Request request) {
        HttpURLConnection connection = null;
        try {
            String urlString = request.getUrl();
            if (!urlString.startsWith("http://") && !urlString.startsWith("https://")) {
                urlString = "http://" + urlString;
                request.setUrl(urlString);
            }
            
            uiManager.printRequestInfo(urlString, request.getMethod().toLowerCase());
            
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(request.getMethod());
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
             
            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
             
            if (request.getMethod().equals("POST") || request.getMethod().equals("PUT")) {
                connection.setDoOutput(true);
                if (request.getBody() != null && !request.getBody().isEmpty()) {
                    try (OutputStream os = connection.getOutputStream()) {
                        byte[] input = request.getBody().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }
                }
            }
            
            int status = connection.getResponseCode();
            uiManager.printStatusInfo(status, connection.getResponseMessage());
             
            uiManager.printRequestDetailsSection();
            uiManager.printRequestDetail("Method", request.getMethod());
            uiManager.printRequestDetail("URL", request.getUrl());
             
            for (Map.Entry<String, String> header : request.getHeaders().entrySet()) {
                uiManager.printRequestDetail("Header", header.getKey() + ": " + header.getValue());
            }
             
            if (request.getBody() != null && !request.getBody().isEmpty()) {
                uiManager.printRequestBodySection();
                if (request.getBody().trim().startsWith("{") || request.getBody().trim().startsWith("[")) {
                    try {
                        System.out.println(jsonFormatter.formatJson(request.getBody()));
                    } catch (Exception e) {
                        System.out.println(request.getBody());
                    }
                } else {
                    System.out.println(request.getBody());
                }
            }
             
            uiManager.printHeadersSection();
            connection.getHeaderFields().forEach((key, values) -> {
                if (key != null) {
                    uiManager.printHeader(key, String.join(", ", values));
                }
            });
             
            BufferedReader reader;
            if (status > 299) {
                reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            } else {
                reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
            
            uiManager.printResponseBodySection();
            String line;
            StringBuilder responseContent = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                responseContent.append(line);
            }
            reader.close();
            
            String response = responseContent.toString();
            if (response.trim().startsWith("{") || response.trim().startsWith("[")) {
                try {
                    String formatted = jsonFormatter.formatJson(response);
                    System.out.println(formatted);
                } catch (Exception e) {
                    System.out.println(response);
                }
            } else {
                System.out.println(response);
            }
            
        } catch (IOException e) {
            uiManager.printError("Error: " + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    
    public static class Request {
        private String method;
        private String url;
        private Map<String, String> headers;
        private String body;
        
        public Request(String method, String url) {
            this.method = method;
            this.url = url;
            this.headers = new HashMap<>();
            this.body = null;
        }
        
        public String getMethod() {
            return method;
        }
        
        public String getUrl() {
            return url;
        }
        
        public void setUrl(String url) {
            this.url = url;
        }
        
        public Map<String, String> getHeaders() {
            return headers;
        }
        
        public void addHeader(String name, String value) {
            headers.put(name, value);
        }
        
        public String getBody() {
            return body;
        }
        
        public void setBody(String body) {
            this.body = body;
        }
    }
}
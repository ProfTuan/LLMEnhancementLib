/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.semantic.llmenrichment;

import java.io.IOException;
import java.io.InputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import java.util.Scanner;

/**
 *
 * @author mac
 */
public class LLMManagement {
    
    static private LLMManagement INSTANCE = null;
    
    private LLMManagement(){
        
    }
    
    
    static public LLMManagement getInstance(){
        
        if(INSTANCE == null){
            INSTANCE = new LLMManagement();
        }
        
        return INSTANCE;
        
    }
    
    public void downloadFile(String fileURL, String saveDir) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.ALWAYS) // Automatically follow redirects
                .build();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(fileURL))
                .build();

        HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());

        int statusCode = response.statusCode();
        if (statusCode == 200) {
            long contentLength = Long.parseLong(response.headers().firstValue("Content-Length").orElse("0"));
            try (InputStream inputStream = response.body();
                 FileOutputStream outputStream = new FileOutputStream(saveDir)) {

                byte[] buffer = new byte[4096];
                long totalBytesRead = 0;
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                    totalBytesRead += bytesRead;

                    // Calculate and display progress
                    int progress = (int) (totalBytesRead * 100 / contentLength);
                    System.out.print("\r" + "Downloaded " + progress + "% [" + progressBar(progress) + "]");
                }
                System.out.println("\nDownload complete.");
            }
        } else {
            System.out.println("Failed to download file. HTTP status code: " + statusCode);
        }
    }

    private String progressBar(int progress) {
        int totalBars = 50; // The length of the progress bar
        int bars = (progress * totalBars) / 100;
        return "=".repeat(bars) + " ".repeat(totalBars - bars);
    }
    
    /*
    public static void main(String[] args) {
        String fileURL = "https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q2_K.gguf";
        String saveDir = "D:/hugging_scope/modelscope/codellama-7b.Q2_K.gguf";
        try {
            downloadFile(fileURL, saveDir);
            System.out.println(saveDir);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    */
    
    public static void main(String[] args) {
        
        LLMManagement llmmanagement = new LLMManagement();
        
        // List of URLs to present to the user
        String[] urls = {
            "https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q2_K.gguf",
            "https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q4_K.gguf",
            "https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q5_K.gguf"
        };

        // Display the list of options to the user
        System.out.println("Please choose a model to download:");
        for (int i = 0; i < urls.length; i++) {
            System.out.println((i + 1) + ": " + urls[i]);
        }

        // Read user input for URL choice
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the number corresponding to the model you want to download: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        // Validate input
        if (choice < 1 || choice > urls.length) {
            System.out.println("Invalid choice. Exiting.");
            return;
        }

        // Set fileURL based on user choice
        String fileURL = urls[choice - 1];

        // Extract the file name from the URL
        String fileName = fileURL.substring(fileURL.lastIndexOf('/') + 1);

        // Prompt the user to enter the save directory
        System.out.print("Enter the directory where you want to save the file: ");
        String saveDir = scanner.nextLine();

        // Combine save directory with file name
        saveDir = saveDir + "/" + fileName;

        // Download the file
        try {
            llmmanagement.downloadFile(fileURL, saveDir);
            System.out.println("Model downloading from: " + fileURL);
            System.out.println("Model downloaded to: " + saveDir);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        scanner.close();
    }
    
}

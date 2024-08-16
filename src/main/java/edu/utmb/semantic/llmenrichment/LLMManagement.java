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

/**
 *
 * @author mac
 */
public class LLMManagement {
    public static void downloadFile(String fileURL, String saveDir) throws IOException, InterruptedException {
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

    private static String progressBar(int progress) {
        int totalBars = 50; // The length of the progress bar
        int bars = (progress * totalBars) / 100;
        return "=".repeat(bars) + " ".repeat(totalBars - bars);
    }
    
    public static void main(String[] args) {
        String fileURL = "https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q2_K.gguf";
        String saveDir = "D:/hugging_scope/modelscope/codellama-7b.Q2_K.gguf";
        try {
            downloadFile(fileURL, saveDir);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
}

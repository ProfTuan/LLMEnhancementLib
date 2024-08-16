/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Singleton.java to edit this template
 */
package edu.utmb.semantic.llmenrichment.util;

import com.opencsv.CSVWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author tuan
 */
public class Reporter {
    
    private static Reporter INSTANCE = null;
    
    private Reporter() {
    }
    
    public static Reporter getInstance() {
        
        if(INSTANCE == null){
            INSTANCE = new Reporter();
        }
        
        return INSTANCE;
        
    }
    
     public void writeCsv(String filePath, List<String[]> data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            writer.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    } 
    
    
}

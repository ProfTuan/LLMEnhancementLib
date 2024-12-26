/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.semantic.llmenrichment.util;

import de.kherud.llama.args.MiroStat;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author mac
 */
public class LLMConfiguration {
    
    private final String propertyFile = "llm.properties";
    
    private final String modelsFile = "models.csv";
    
    private static LLMConfiguration INSTANCE = null;
    
    private String configPath;
    
    private String rootPath;
    
    private Properties property = new Properties();
    
    private LLMConfiguration(){
        
        
        //File resource = new ClassPathResource("data/employees.dat").getFile();
        InputStream i_stream = ClassLoader.getSystemClassLoader().getResourceAsStream(propertyFile);
        
        
        
        //rootPath = Thread.currentThread().getContextClassLoader().getResource("").getPath();
        
        //configPath = rootPath + propertyFile;
        
        try {
            property.load(i_stream);
            //property.load(new FileInputStream(configPath));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(LLMConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(LLMConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public static LLMConfiguration getInstance(){
        
        if(INSTANCE == null){
            INSTANCE = new LLMConfiguration();
        }
        
        return INSTANCE;
        
    }
    
    public Map<String, String> collectLLMList() {
        Map<String, String> collect_llm_list = new HashMap<String, String>();

        InputStream i_stream = ClassLoader.getSystemClassLoader().getResourceAsStream(modelsFile);

        BufferedReader reader = new BufferedReader(new InputStreamReader(i_stream));
        boolean header = true;
        String line_read;
        try {

            while ((line_read = reader.readLine()) != null) {

                if (header == true) {
                    header = false;
                } else {
                    String[] split = line_read.split(",");
                    String name = split[0];
                    String url = split[1];

                    collect_llm_list.put(name, url);
                }

            }

        } catch (IOException ex) {
            Logger.getLogger(LLMConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }

    
        
        return collect_llm_list;

    }
    
    
    public int getNumThreads(){
        
        String value = property.getProperty("threads", "16");
        
        int threads = Integer.parseInt(value);
        
        return threads;
        
    }
    
    public int getLayers(){
        
        String value = property.getProperty("layers", "43");
        
        int layers = Integer.parseInt(value);
        
        return layers;
    }
    
   public float getTemperature(){
       
       String value = property.getProperty("temperature", "0.7f");
       
       float temperature = Float.parseFloat(value);
       
       return temperature;
       
   }
    
   public boolean getShouldPenalize(){
       String value = property.getProperty("should_penalize", "true");
       
       boolean penalize = Boolean.parseBoolean(value);
       
       return penalize;
   }
   
   public MiroStat getMiroStatType(){
       
       String value = property.getProperty("mirostat_v");
       
       int version = Integer.parseInt(value);
       
       if(version == 1){
           
           return MiroStat.V1;
       }
       else if (version ==2){
           return MiroStat.V2;
       }
       else
           return null;
       
   }
   
   public int predictNumber(){
       String value = property.getProperty("predict", "30");
       int predict = Integer.parseInt(value);
       
       return predict;
   }
   
   public String getModelFilePath(){
       
       String value = property.getProperty("file_path", "");
       
       return value;
   }
    
}

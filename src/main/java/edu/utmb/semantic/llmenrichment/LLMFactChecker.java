/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.semantic.llmenrichment;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import de.kherud.llama.InferenceParameters;
import de.kherud.llama.LlamaModel;
import de.kherud.llama.LlamaOutput;
import de.kherud.llama.ModelParameters;
import de.kherud.llama.args.MiroStat;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvException;
import edu.utmb.semantic.llmenrichment.util.Reporter;
import java.util.ArrayList;
import java.util.List;
/**
 *
 * @author mac
 */
public class LLMFactChecker {
    private ModelParameters modelParams = null;
    private InferenceParameters inferParams = null;
    
    private Reporter llmReporter = null;
    
    private String model_path ="";
    private int thread_number;
    private int gpu_layers;
    
    public LLMFactChecker (){
        
    }    
    
    public void setModelParameters(String modelPath, int thread_number, int gpu_layers){
        
        this.model_path = modelPath;
        this.thread_number = thread_number;
        this.gpu_layers = gpu_layers;
    }
    
    public void setInferenceParamters(float temp, boolean penalize, MiroStat ms, String stop_string, int num_predict){
        
    }
    
    public void factchecking(String sourcepath, String targetpath, String modelpath) throws IOException {
        llmReporter = Reporter.getInstance();
        List<String[]> records = llmReporter.readcsv(sourcepath);
        
        ModelParameters modelParams = new ModelParameters()
            .setModelFilePath(modelpath)
            .setNThreads(16)
            .setNGpuLayers(43);
      
        List<String[]> outdata = new ArrayList<>();
        
        try (LlamaModel model = new LlamaModel(modelParams)) {
            System.out.println("Fact checking Hootation's natural language translation: inference...");    
            
            String system = "\nYou are a helpful assistant. ";
            String question = "Evaluate the accuracy of the ontology axiom's natural language translation.";        
            for(String[] input: records) { 
                if (input[0].trim().equals("Axiom Type") || input[0].trim().length()<2){
                    outdata.add(input);
                    continue;
                }
                
                String axiom_type = "The axiom type is: " + input[0] + ". ";
                String axiom = "The axiom is: " + input[1] + ". ";
                String trans = "The axiom's natural language translation is: " + input[2] + ". ";
                String prompt = system + "\nUser: " + question + axiom_type + axiom + 
                                "Is the translation accurate? (Only answer Yes, No, or Don't know):";                                                
                                  
                System.out.println("prompt:  " + prompt);
                
                InferenceParameters inferParams = new InferenceParameters(prompt)
                    .setTemperature(0.7f)
                    .setPenalizeNl(true)
                    .setMiroStat(MiroStat.V2)
                    .setStopStrings("User:")
                    .setNPredict(30);
                
                String data = "";
                for (LlamaOutput output : model.generate(inferParams)) {                    
                    data += output;
                }
                System.out.println("Fact checking:   "+data);
                
                String[] temp = new String[input.length+1];                
                System.arraycopy(input, 0, temp, 0, input.length);
                int len = data.indexOf(".", 0);
                if (len != -1)                    
                    data = data.substring(0, len);                
                data = data.replace("\n", " ");
                temp[temp.length-1] = data;                
                outdata.add(temp);
                llmReporter.writeCsv(targetpath, outdata);    
            } 
            llmReporter.writeCsv(targetpath, outdata);
        }
    }
    
    public static void main(String... args){
        //System.setProperty("de.kherud.llama.lib.path", "D:/AAAAA_pythonProject/amith/java-llama.cpp/src/main/resources/de/kherud/llama/Windows/x86_64");
        //System.out.println(System.getProperty("de.kherud.llama.lib.path"));
        // System.exit(0);
        LLMFactChecker infer = new LLMFactChecker();
        
        
        
        String sourcepath = "D:/netbean_project/LLMEnrichment/data/People Axioms 11_18.csv";
        //String sourcepath = "D:/netbean_project/LLMEnrichment/data/a.csv";
        //List<String[]> records = infer.readcsv(sourcepath);
        
        String respath = "D:/netbean_project/LLMEnrichment/result/t.csv";
        //infer.writeCsv(respath, records);
        
        
        
        try{
            //TODO: We need a class that can download and import a selected model
            String modelpath = "D:/hugging_scope/modelscope/Meta-Llama-3-8B-Instruct-Q6_K.gguf";
            infer.factchecking(sourcepath, respath, modelpath);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
    
}

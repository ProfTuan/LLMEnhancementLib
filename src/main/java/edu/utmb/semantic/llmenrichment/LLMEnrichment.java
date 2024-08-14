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

@SuppressWarnings("InfiniteLoopStatement")
public class LLMEnrichment 
{
    
    private ModelParameters modelParams = null;
    private InferenceParameters inferParams = null;
    
    private Reporter llmReporter = null;
    
    private String model_path ="";
    private int thread_number;
    private int gpu_layers;
    
    public LLMEnrichment (){
        
    }    
    
    public List<String[]> readcsv(String filepath) {        
        List<String[]> records = null;
        try (CSVReader reader = new CSVReader(new InputStreamReader(new FileInputStream(filepath), StandardCharsets.UTF_8))) {
            records = reader.readAll();            
            for (String[] record : records) {                
                System.out.println("Record: " + record.length);
                for (String field : record) {
                    System.out.print(field + " ");
                }
                System.out.println();
            }
        } catch (IOException | CsvException e) {
            e.printStackTrace();
        }
        return records;
    }
    
    public void setModelParameters(String modelPath, int thread_number, int gpu_layers){
        
        this.model_path = modelPath;
        this.thread_number = thread_number;
        this.gpu_layers = gpu_layers;
    }
    
    public void setInferenceParamters(float temp, boolean penalize, MiroStat ms, String stop_string, int num_predict){
        
    }
    // TODO: we need to move this to the Reporter
    /*
    public void writeCsv(String filePath, List<String[]> data) {
        try (CSVWriter writer = new CSVWriter(new FileWriter(filePath, StandardCharsets.UTF_8))) {
            writer.writeAll(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
       
    public void inference(String sourcepath, String targetpath, String modelpath) throws IOException {
        List<String[]> records = readcsv(sourcepath);
        llmReporter = Reporter.getInstance();
        
        modelParams = new ModelParameters()
            .setModelFilePath(modelpath)
            .setNThreads(16)
            .setNGpuLayers(43);
      
        List<String[]> outdata = new ArrayList<>();
        
        try (LlamaModel model = new LlamaModel(modelParams)) {
            System.out.println("inference...");    
            
            String system = "\nYou are a helpful assistant. ";
            String question = "Please translate the ontology axiom using natural language. ";        
            for(String[] input: records) { 
                if (input[0].trim().equals("Axiom Type") || input[0].trim().length()<2){
                    outdata.add(input);
                    continue;
                }
                
                String axiom_type = "The axiom type is: " + input[0] + ". ";
                String axiom = "The axiom you need to translate is: " + input[1] + ". ";
                String prompt = system + "\nUser: " + question + axiom_type + axiom + 
                                "Your translation for this axiom is (Just state your translation in one sentence. Do not add any other statements):";                                                
                                  
                System.out.println("prompt:  " + prompt);
                
                inferParams = new InferenceParameters(prompt)
                    .setTemperature(0.7f)
                    .setPenalizeNl(true)
                    .setMiroStat(MiroStat.V2)
                    .setStopStrings("User:")
                    .setNPredict(30);
                
                String data = "";
                for (LlamaOutput output : model.generate(inferParams)) {                    
                    data += output;
                }
                System.out.println("###trans:   "+data);
                
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
        LLMEnrichment infer = new LLMEnrichment();
        
        
        
        String sourcepath = "D:/netbean_project/LLMEnrichment/data/People Axioms 11_18.csv";
        //String sourcepath = "D:/netbean_project/LLMEnrichment/data/a.csv";
        //List<String[]> records = infer.readcsv(sourcepath);
        
        String respath = "D:/netbean_project/LLMEnrichment/result/t.csv";
        //infer.writeCsv(respath, records);
        
        try{
            
            //TODO: We need a class that can download and import a selected model
            String modelpath = "C:/Users/xubin/.cache/modelscope/Meta-Llama-3-8B-Instruct-Q6_K.gguf";
            infer.inference(sourcepath, respath, modelpath);
        }
        catch(IOException e){
            e.printStackTrace();
        }
        
        
        
        
        
        
        
    }
}

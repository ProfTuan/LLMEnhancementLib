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

//@SuppressWarnings("InfiniteLoopStatement")
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
    
    /*
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
    */
    
    public void setModelParameters(String modelPath, int thread_number, int gpu_layers){
        
        this.model_path = modelPath;
        this.thread_number = thread_number;
        this.gpu_layers = gpu_layers;
    }
    
    public void setInferenceParamters(float temp, boolean penalize, MiroStat ms, String stop_string, int num_predict){
        
    }
    
    public void downloadAndSetModelPath(String fileURL, String saveDir) throws IOException, InterruptedException {
        // Use LLMManagement to download the model
        LLMManagement.downloadFile(fileURL, saveDir);
        // Set the downloaded model's path as the model_path
        this.model_path = saveDir;
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
        llmReporter = Reporter.getInstance();
        List<String[]> records = llmReporter.readcsv(sourcepath);
        
        
        modelParams = new ModelParameters()
            .setModelFilePath(modelpath)
            //.setModelUrl("https://huggingface.co/TheBloke/CodeLlama-7B-GGUF/resolve/main/codellama-7b.Q2_K.gguf")
            //.setHuggingFaceRepository("TheBloke/CapybaraHermes-2.5-Mistral-7B-GGUF")
            //.setHuggingFaceFile("capybarahermes-2.5-mistral-7b.Q2_K.gguf")
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
    
    public void factchecking(String sourcepath, String targetpath, String modelpath) throws IOException {
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
    
    
    public static void main(String[] args){
        //System.setProperty("de.kherud.llama.lib.path", "D:/AAAAA_pythonProject/amith/java-llama.cpp/src/main/resources/de/kherud/llama/Windows/x86_64");
        //System.out.println(System.getProperty("de.kherud.llama.lib.path"));
        // System.exit(0);
        System.out.println("Arguments length: " + args.length);
        if (args.length < 1) {
            System.out.println("Usage: java LLMEnrichment <modelSaveDir>");
            //return;
        }
        String saveDir = args[0];

        LLMEnrichment infer = new LLMEnrichment();
        String sourcepath = "D:/netbean_project/LLMEnrichment/data/People Axioms 11_18.csv";
        String respath = "D:/netbean_project/LLMEnrichment/result/t.csv";
        //infer.writeCsv(respath, records);
        
        try{
            //TODO: We need a class that can download and import a selected model
            //String modelpath = "D:/hugging_scope/modelscope/codellama-7b.Q2_K.gguf";
            infer.inference(sourcepath, respath, saveDir);
        }
        catch(IOException e){
            e.printStackTrace();
        }
    }
}

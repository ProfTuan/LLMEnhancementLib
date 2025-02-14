/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.semantic.llmenrichment;

import edu.utmb.semantic.llmenrichment.model.NLAxiomData;
import java.util.Set;
import javax.swing.JTextArea;

/**
 *
 * @author tuan
 */
public class LLMAdapter {
    
    private static LLMAdapter INSTANCE = null;
    
    private LLMEnrichment llm_enrichment;
    private LLMFactChecker llm_fact_checker;
    private LLMManagement llm_management;
    
    
    public synchronized static LLMAdapter getInstance(){
        
        if(INSTANCE == null){
            
            INSTANCE = new LLMAdapter();
            
        }
        
        
        return INSTANCE;
    }
    
    
    private LLMAdapter(){
        
        init();
             
    }
    
    private void init(){
        llm_enrichment = new LLMEnrichment();
        llm_fact_checker = new LLMFactChecker();
    }
    
    public void retrieveLLMModel(String file_url, String save_dir, JTextArea panelOutput){
        llm_management = LLMManagement.getInstance();
        
        llm_management.downloadFile(file_url, save_dir, panelOutput);
    }
    
    public String excecuteFactChecking(String nl_string, String axiom_type){
        llm_management = LLMManagement.getInstance();
        
        return llm_fact_checker.checkSentenceAccuracy(nl_string, axiom_type);
    }
    
    public void executeFactChecking(Set<NLAxiomData> records){
        
        llm_management = LLMManagement.getInstance();
        //set up parameter
        
        //execute
        llm_fact_checker.checkSentenceAccuracy(records);
        
    }
    
    public void initializeLLMModelEnrichement(){
        llm_enrichment.initModelParameters();
    }
    
    public void initializeLLMModelFactChecker(){
        llm_fact_checker.initModelParameters();
    }
    
    public String executeLLMEnhancement(String nl_string, String axiom_type){
        
        llm_management = LLMManagement.getInstance();
        
        return llm_enrichment.translateAxiom(nl_string, axiom_type);
    }
    
    public void executeLLMEnhancement(Set<NLAxiomData> records){
        
        llm_management = LLMManagement.getInstance();
        //set up parameters
        
        //execute
        llm_enrichment.translateAxioms(records);
    }
    
    public static void main(String[] args) {
        
    }
}

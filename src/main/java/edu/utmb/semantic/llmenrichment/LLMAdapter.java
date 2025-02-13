/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.semantic.llmenrichment;

import edu.utmb.semantic.llmenrichment.model.NLAxiomData;
import java.util.Set;

/**
 *
 * @author tuan
 */
public class LLMAdapter {
    
    private LLMEnrichment llm_enrichment;
    private LLMFactChecker llm_fact_checker;
    private LLMManagement llm_management;
    
    
    public LLMAdapter(){
        
        llm_enrichment = new LLMEnrichment();
        llm_fact_checker = new LLMFactChecker();
             
    }
    
    public void retrieveLLMModel(){
        
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

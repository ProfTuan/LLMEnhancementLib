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
    
    public void executeFactChecking(Set<NLAxiomData> records){
        
        
        //set up parameter
        
        //execute
        llm_fact_checker.checkSentenceAccuracy(records);
        
    }
    
    public void executeLLMEnhancement(Set<NLAxiomData> records){
        //set up parameters
        
        //execute
        llm_enrichment.translateAxioms(records);
    }
    
    public static void main(String[] args) {
        
    }
}

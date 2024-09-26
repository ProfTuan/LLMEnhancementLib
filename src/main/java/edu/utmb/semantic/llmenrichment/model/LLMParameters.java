/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.utmb.semantic.llmenrichment.model;

import de.kherud.llama.args.MiroStat;

/**
 *
 * @author mac
 */
public class LLMParameters extends LLMInferenceParameters {
   
    private static LLMParameters INSTANCE = null;
    
    //inference parameters
    private LLMInferenceParameters inference_parameters;
    private LLMModelParameters model_parameters;
    
    private LLMParameters(){
        
        inference_parameters = new LLMInferenceParameters();
        model_parameters = new LLMModelParameters();
        
    }
    
    public static LLMParameters getInstance(){
        if (INSTANCE == null){
            INSTANCE = new LLMParameters();
        }
        
        return INSTANCE;
    }
    
    
    
    
}

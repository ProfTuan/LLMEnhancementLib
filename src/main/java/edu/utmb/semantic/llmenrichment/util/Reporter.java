/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Singleton.java to edit this template
 */
package edu.utmb.semantic.llmenrichment.util;

/**
 *
 * @author tuan
 */
public class Reporter {
    
    private Reporter() {
    }
    
    public static Reporter getInstance() {
        return ReporterHolder.INSTANCE;
    }
    
    private static class ReporterHolder {

        private static final Reporter INSTANCE = new Reporter();
    }
}

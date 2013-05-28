/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.entities;

import org.neuroph.core.NeuralNetwork;

/**
 * Entidade da classe OCR
 * @author Bruno, Mívian e Washington
 */
public class OCR {
    NeuralNetwork rede;
    
    //CONSTRUTORES
    public OCR() {
    }//end construtor
        
    public OCR(NeuralNetwork rede) {
        this.rede = rede;
    }//end construtor

    //MÉTODOS
    
    
    //GETTERS AND SETTERS
    public NeuralNetwork getRede() {
        return rede;
    }

    public void setRede(NeuralNetwork rede) {
        this.rede = rede;
    }
           
}

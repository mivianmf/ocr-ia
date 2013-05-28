/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.controllers;

import ocr.entities.OCR;
import ocr.gui.Janela;

/**
 * Controlador da classe OCR
 * @author Bruno, Mívian e Washington
 */
public class OCR_Controller {
    static Janela janela;
    static OCR ocr;
    
    /**
     * Método principal, controla todo o fluxo de dados
     * @param args
     */
    public static void main(String []args){        
        //Criação dos objetos
        janela = new Janela();
        ocr = new OCR();
        
        mostrarJanela();
    }

    private static void mostrarJanela() {
        janela.setLocationRelativeTo(null);//Centraliza
        janela.setTitle("OCR");//Define o título
        janela.setVisible(true);//Faz ficar visível
    }//end mostrarJanela
}

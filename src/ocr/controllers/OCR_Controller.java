/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.controllers;

import org.opencv.core.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.util.ArrayList;
import javax.swing.JFrame;
import ocr.entities.OCR;
import ocr.gui.Janela;
import ocr.interfaces.Drawer_Observable;
import ocr.interfaces.Drawer_Observer;
import org.opencv.highgui.Highgui;

/**
 * Controlador da classe OCR
 *
 * @author Bruno, Mívian e Washington
 */
public class OCR_Controller implements Drawer_Observer {

    static Janela janela;
    static OCR ocr;
    //static BufferedImage imagem;
    static Mat imagem;

    /**
     * Método principal, controla todo o fluxo de dados
     *
     * @param args
     */
    public static void main(String[] args) {
        //janela = new Janela();
        //janela.setTitle("OCR");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        imagem = Highgui.imread("sorriso1.jpg");
        //imagem = abrirImagem("sorriso1.jpg");
        //ArrayList<CvRect> listaRetangulos = segmentar(imagem);
        
        
    }

    private static void mostrarJanela() {
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setSize(200, 200);//Define o tamanho
        janela.setLocationRelativeTo(null);//Centraliza
        janela.setTitle("OCR");//Define o título
        janela.setVisible(true);//Faz ficar visível
    }//end mostrarJanela

    @Override
    public void atualizar(Drawer_Observable observavel) {
        //TODO: Trocar Image para awt.Image
        //this.ocr.adicionarImagemAoTreino(((Janela)observavel).getImagem(),
        //                                  ((Janela)observavel).getClasse());
    }
}

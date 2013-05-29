/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.controllers;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import ocr.entities.OCR;
import ocr.gui.Janela;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.image.Image;
import org.neuroph.imgrec.image.ImageJ2SE;

/**
 * Controlador da classe OCR
 *
 * @author Bruno, Mívian e Washington
 */
public class OCR_Controller {

    static Janela janela;
    static OCR ocr;
    static Image imagem;

    /**
     * Método principal, controla todo o fluxo de dados
     *
     * @param args
     */
    public static void main(String[] args) {
        try {
            //Criação dos objetos
            janela = new Janela();
            
            imagem = new ImageJ2SE(ImageIO.read(new File("sorriso1.jpg")));

            ocr = new OCR(imagem.getWidth() * imagem.getHeight());

            ocr.adicionarImagemAoTreino(imagem, 0, "Sorriso1");
            
            Image imagem2 = new ImageJ2SE(ImageIO.read(new File("sorriso2.jpg")));
            
            //ocr.adicionarImagemAoTreino(imagem2, 0, "Sorriso2");
                        
            Image imagem3 = new ImageJ2SE(ImageIO.read(new File("triste1.jpg")));
            
            ocr.adicionarImagemAoTreino(imagem3, 1, "Tristeza1");
            
            Image imagem4 = new ImageJ2SE(ImageIO.read(new File("triste2.jpg")));
            
            //ocr.adicionarImagemAoTreino(imagem4, 1, "Tristeza2");
            
            ocr.treinarRede();
            ocr.adicionarImagemAoTreino(imagem2, 0, "Sorriso2");
            //ocr.salvarRede("rede.nnet");
            //ocr = new OCR();
            //ocr.carregarRede("rede.nnet");
            
            System.out.println("A classe da imagem é: "+ ocr.reconhecer(imagem));
            
            mostrarJanela();
        } catch (IOException ex) {
            Logger.getLogger(OCR_Controller.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static BufferedImage paraCinza(BufferedImage img) {
        ColorConvertOp colorConvert = new ColorConvertOp(
                ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
        colorConvert.filter(img, img);
        return img;
    }

    private static void mostrarJanela() {
        janela.setLocationRelativeTo(null);//Centraliza
        janela.setTitle("OCR");//Define o título
        janela.setVisible(true);//Faz ficar visível
    }//end mostrarJanela
}

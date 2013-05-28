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
            
            imagem = new ImageJ2SE(paraCinza(ImageIO.read(new File("teste.bmp"))));
            
            ocr = new OCR(imagem.getWidth() * imagem.getHeight());
            
            mostrarJanela();

            ocr.adicionarImagemAoTreino(imagem, 1, "Teste");
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

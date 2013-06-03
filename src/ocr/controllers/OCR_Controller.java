/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.controllers;

import com.googlecode.javacpp.BytePointer;
import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.JFrame;
import ocr.entities.ImageTools;
import ocr.entities.OCR;
import ocr.gui.Janela;
import ocr.interfaces.Drawer_Observable;
import ocr.interfaces.Drawer_Observer;

/**
 * Controlador da classe OCR
 *
 * @author Bruno, Mívian e Washington
 */
public class OCR_Controller implements Drawer_Observer {

    static Janela janela;
    static OCR ocr;
    static BufferedImage imagem;
    static IplImage imagemIpl, imagemTeste, imagemTesteRedimensionada;
    static IplImage[] imagensTreino, imagensTreinoRedimensionadas;
    static int quantTreino, quantClasses;

    /**
     * Método principal, controla todo o fluxo de dados
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        //janela = new Janela();
        //janela.setTitle("OCR");
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        quantTreino = 8;
        quantClasses = 2;
        //imagem = Highgui.imread("sorriso1.jpg");
        imagensTreino = new IplImage[quantTreino];
        imagensTreinoRedimensionadas = new IplImage[quantTreino];

        ocr = new OCR(quantTreino * quantClasses - 1);

        for (int i = 0; i < quantClasses; i++) {
            for (int j = 0; j < quantTreino; j++) {

                imagemIpl = ImageTools.abrirImagem("src/ocr/images/"+(i+1)+"_" + (j + 1) + ".png");

                imagemIpl = ImageTools.converterImgEscalaCinza(imagemIpl);

                ArrayList<CvRect> retangulos = segmentar(imagemIpl);

                if (retangulos.get(0).height() != 0 && retangulos.get(0).width() != 0) {
                    //imagemIpl.copyTo(imagem, 0, false, retanguloAtual);
                    //cvSetImageROI(imagemIpl, retangulos.get(i));
                    cvSetImageROI(imagemIpl, retangulos.get(0));
                    imagensTreino[j] = cvCreateImage(cvGetSize(imagemIpl), imagemIpl.depth(), imagemIpl.nChannels());
                    cvCopy(imagemIpl, imagensTreino[j]);
                    cvResetImageROI(imagemIpl);
                    imagensTreinoRedimensionadas[j] = IplImage.create(50, 75, imagensTreino[j].depth(), imagensTreino[j].nChannels());
                    cvResize(imagensTreino[j], imagensTreinoRedimensionadas[j], CV_INTER_CUBIC);

                    ocr.adicionarImagemAoTreino(imagensTreinoRedimensionadas[j], i);

                    //imagem = resizedImage.getBufferedImage();
                    //ImageTools.mostrarImagem(imagem);
                }//end if
            }//end for  
        }//end for
        ocr.treinarRede();

        //Reconhecer
        imagemIpl = ImageTools.abrirImagem("src/ocr/images/2_9.png");//TODO: Trocar imagem a ser reconhecida

        imagemIpl = ImageTools.converterImgEscalaCinza(imagemIpl);

        ArrayList<CvRect> retangulos = segmentar(imagemIpl);

        if (retangulos.get(0).height() != 0 && retangulos.get(0).width() != 0) {
            //imagemIpl.copyTo(imagem, 0, false, retanguloAtual);
            //cvSetImageROI(imagemIpl, retangulos.get(i));
            cvSetImageROI(imagemIpl, retangulos.get(0));
            imagemTeste = cvCreateImage(cvGetSize(imagemIpl), imagemIpl.depth(), imagemIpl.nChannels());
            cvCopy(imagemIpl, imagemTeste);
            cvResetImageROI(imagemIpl);
            imagemTesteRedimensionada = IplImage.create(50, 75, imagemTeste.depth(), imagemTeste.nChannels());
            cvResize(imagemTeste, imagemTesteRedimensionada, CV_INTER_CUBIC);

            System.out.println("Saída da rede = " + (ocr.reconhecer(imagemTesteRedimensionada) + 1));

            //imagem = resizedImage.getBufferedImage();
            //ImageTools.mostrarImagem(imagem);
        }//end if


        /*
         for (int i = 0; i < imagem.getHeight(); i++) {
         for (int j = 0; j < imagem.getWidth(); j++) {
         int argb = imagem.getRGB(j, i);
         int r = (argb >> 16) & 0xff;
         int g = (argb >> 8) & 0xff;
         int b = (argb) & 0xff;

         int tomDeCinza = (int) (r + g + b) / 3;

         System.out.print("\t" + tomDeCinza);
         }
         System.out.println("");
         }*/

        //imagem = abrirImagem("sorriso1.jpg");
        //ArrayList<CvRect> listaRetangulos = segmentar(imagem);


    }

    public static IplImage preprocessar(IplImage imagem) {
        IplImage temp = cvCreateImage(cvSize(imagem.width(), imagem.height()),
                IPL_DEPTH_8U, 1);
        opencv_imgproc.IplConvKernel element = new opencv_imgproc.IplConvKernel();
        IplImage cinza = ImageTools.converterImgEscalaCinza(imagem);
        cinza = ImageTools.filtroSmooth(cinza);
        cinza = ImageTools.limiarizar(cinza);
        cvMorphologyEx(cinza, cinza, temp, element, CV_MOP_OPEN, 2);
        return cinza;
    }

    public static ArrayList<opencv_core.CvRect> segmentar(IplImage cinza) {
        ArrayList<opencv_core.CvRect> lista = new ArrayList<>();
        opencv_core.CvSeq contornos = ImageTools.getContornos(cinza);
        opencv_core.CvMemStorage m = cvCreateMemStorage(0);
        opencv_core.CvSeq contourLow = cvApproxPoly(contornos,
                Loader.sizeof(opencv_core.CvContour.class), m, CV_POLY_APPROX_DP, 1, 1);
        opencv_core.CvFont font = new opencv_core.CvFont(CV_FONT_HERSHEY_PLAIN, 1, 1);
        while (contourLow != null) {

            // Elimina retangulos com largura maior que altura
            // Pois padrao do numero e altura > que largura
            opencv_core.CvRect retangulo = cvBoundingRect(contourLow, 0);
            if (retangulo.width() >= retangulo.height()) {
                contourLow = contourLow.h_next();
                continue;
            }

            // Elimina retangulos com tamanho muito pequeno
            // Pois estes provavelmente são ruidos
            if (retangulo.width() <= 10 || retangulo.height() <= 10) {
                contourLow = contourLow.h_next();
                continue;
            }

            lista.add(retangulo);
            contourLow = contourLow.h_next();
        }
        return lista;
    }

    private static void mostrarJanela() {
        janela.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        janela.setSize(200, 200);//Define o tamanho
        janela.setLocationRelativeTo(null);//Centraliza
        janela.setTitle("OCR");//Define o título
        janela.setVisible(true);//Faz ficar visível
    }//end mostrarJanela

    public static BufferedImage getGrayscale(BufferedImage bufferedImage) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        BufferedImage image = op.filter(bufferedImage, null);
        return image;
    }

    @Override
    public void atualizar(Drawer_Observable observavel) {
        //TODO: Trocar Image para awt.Image
        //this.ocr.adicionarImagemAoTreino(((Janela)observavel).getImagem(),
        //                                  ((Janela)observavel).getClasse());
    }
}

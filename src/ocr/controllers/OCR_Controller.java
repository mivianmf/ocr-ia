/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.controllers;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.cpp.opencv_core;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvRect;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import com.googlecode.javacv.cpp.opencv_imgproc;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import ocr.entities.ImageTools;
import ocr.entities.OCR;
import ocr.gui.Janela;
import ocr.interfaces.BotaoReconhecer_Observable;
import ocr.interfaces.BotaoReconhecer_Observer;
import ocr.interfaces.BotaoTreinar_Observable;
import ocr.interfaces.BotaoTreinar_Observer;
import ocr.interfaces.Drawer_Observable;
import ocr.interfaces.Drawer_Observer;
import ocr.interfaces.NeuralNet_Observable;
import ocr.interfaces.NeuralNet_Observer;
import org.neuroph.util.TransferFunctionType;

/**
 * Controlador da classe OCR
 *
 * @author Bruno, Mívian e Washington
 */
public class OCR_Controller implements Drawer_Observer, NeuralNet_Observable,
        NeuralNet_Observer, BotaoReconhecer_Observer, BotaoTreinar_Observer {

    public static final int SIZE_X_NUMBERS = 50;
    public static final int SIZE_Y_NUMBERS = 75;
    private Janela janela;
    private OCR ocr;
    private IplImage imagemTreino, imagemTreinoRedimensionada,
            imagemTeste, imagemTesteRedimensionada;
    private int quantTreino;
    public ArrayList<NeuralNet_Observer> observadores = new ArrayList<>();

    public OCR_Controller() {
        this.janela = new Janela();
        this.ocr = new OCR(TransferFunctionType.TANH);
        this.janela.setTitle("OCR");

        //Adicionando os observadores
        this.janela.adicionarObservador(this);
        this.janela.adicionarObservadorTreinar(this);
        this.janela.adicionarObservadorReconhecer(this);
        this.ocr.adicionarObservadorRede(this);
        this.adicionarObservadorRede(janela);
    }

    /**
     * Método principal, controla todo o fluxo de dados
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        new OCR_Controller();
    }

    //Adiciona uma imagem do painel ao treino
    private void adicionarAoTreino(BufferedImage imagemDoPainel, Integer classe) {
        IplImage novaImagemTreino = cvCreateImage(cvSize(imagemDoPainel.getWidth(),
                imagemDoPainel.getHeight()), IPL_DEPTH_8U, 1);
        novaImagemTreino.copyFrom(imagemDoPainel);
        
        novaImagemTreino = preprocessar(novaImagemTreino);
        
        ArrayList<CvRect> retangulos = segmentar(novaImagemTreino);

        //ImageTools.mostrarImagem(novaImagemTreino.getBufferedImage());
        //if (retangulos.size() == 1) {
        cvSetImageROI(novaImagemTreino, retangulos.get(0));//Define região de interesse
        imagemTreino = cvCreateImage(cvGetSize(novaImagemTreino),
                novaImagemTreino.depth(), novaImagemTreino.nChannels());//Cria uma nova imagem de treino
        cvCopy(novaImagemTreino, imagemTreino);//Copia os dados para a nova imagem
        cvResetImageROI(novaImagemTreino);//Reseta a região de interesse

        imagemTreinoRedimensionada = IplImage.create(SIZE_X_NUMBERS, SIZE_Y_NUMBERS,
                imagemTreino.depth(), imagemTreino.nChannels());//Cria a imagem para redimensionamento
        cvResize(imagemTreino, imagemTreinoRedimensionada, CV_INTER_CUBIC);//Redimensiona
        this.ocr.adicionarImagemAoTreino(imagemTreinoRedimensionada, classe);//Adiciona ao treinamento

        //ImageTools.mostrarImagem(imagemTreinoRedimensionada.getBufferedImage());
        if (classe == 1) {
            this.janela.incrementarContadorClasse1();
        } else {
            this.janela.incrementarContadorClasse2();
        }
        this.quantTreino++;//Incrementa a quantidade de imagens
        JOptionPane.showConfirmDialog(null, "Adicionada com sucesso!", "Adição",
                JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        this.janela.limpar();
        /*}//end if
         else {
         JOptionPane.showConfirmDialog(null, "Certifique-se que o painel contenha um desenho contínuo");
         }//end else*/
    }

    private void reconhecer(BufferedImage imagemDoPainel) {
        IplImage novaImagemTeste = cvCreateImage(cvSize(imagemDoPainel.getWidth(),
                imagemDoPainel.getHeight()), IPL_DEPTH_8U, 1);
        novaImagemTeste.copyFrom(imagemDoPainel);

        novaImagemTeste = preprocessar(novaImagemTeste);
        
        ArrayList<CvRect> retangulos = segmentar(novaImagemTeste);

        //if (retangulos.size() == 1) {
        cvSetImageROI(novaImagemTeste, retangulos.get(0));//Define região de interesse
        imagemTeste = cvCreateImage(cvGetSize(novaImagemTeste),
                novaImagemTeste.depth(), novaImagemTeste.nChannels());//Cria uma nova imagem de treino
        cvCopy(novaImagemTeste, imagemTeste);//Copia os dados para a nova imagem
        cvResetImageROI(novaImagemTeste);//Reseta a região de interesse

        imagemTesteRedimensionada = IplImage.create(SIZE_X_NUMBERS, SIZE_Y_NUMBERS,
                imagemTeste.depth(), imagemTeste.nChannels());//Cria a imagem para redimensionamento
        cvResize(imagemTeste, imagemTesteRedimensionada, CV_INTER_CUBIC);//Redimensiona
        Integer classe = (int) this.ocr.reconhecer(imagemTesteRedimensionada);
        //ImageTools.mostrarImagem(imagemTesteRedimensionada.getBufferedImage());
        if (classe == 1) {
            JOptionPane.showConfirmDialog(null, "A imagem pertence à classe 1!", "Resultado",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        }//end if
        else {
            JOptionPane.showConfirmDialog(null, "A imagem pertence à classe 2!", "Resultado",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE);
        }//end else
        this.janela.limpar();
    }

    public IplImage preprocessar(IplImage imagem) {
        IplImage temp = cvCreateImage(cvSize(imagem.width(), imagem.height()),
                IPL_DEPTH_8U, 1);
        opencv_imgproc.IplConvKernel element = new opencv_imgproc.IplConvKernel();
        IplImage cinza = imagem;//ImageTools.converterImgEscalaCinza(imagem);
        cinza = ImageTools.filtroSmooth(cinza);
        cinza = ImageTools.limiarizar(cinza);
        cvMorphologyEx(cinza, cinza, temp, element, CV_MOP_OPEN, 2);
        return cinza;
    }

    public ArrayList<opencv_core.CvRect> segmentar(IplImage cinza) {
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

    public BufferedImage getGrayscale(BufferedImage bufferedImage) {
        ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);
        ColorConvertOp op = new ColorConvertOp(cs, null);
        BufferedImage image = op.filter(bufferedImage, null);
        return image;
    }

    @Override
    public void atualizar(Drawer_Observable observavel) {
        BufferedImage imagemDoPainel = ((Janela) observavel).getImagem();
        Integer classe = ((Janela) observavel).getClasse();
        this.adicionarAoTreino(imagemDoPainel, classe);
    }

    @Override
    public void adicionarObservadorRede(NeuralNet_Observer observador) {
        this.observadores.add(observador);
    }

    @Override
    public void removerObservadorRede(NeuralNet_Observer observador) {
        this.observadores.remove(observador);
    }

    @Override
    public void notificarRede() {
        for (NeuralNet_Observer neuralNet_Observer : observadores) {
            neuralNet_Observer.atualizar(this.ocr);
        }
    }

    @Override
    public void atualizar(NeuralNet_Observable observavel) {
        notificarRede();
    }

    @Override
    public void atualizar(BotaoTreinar_Observable observavel) {
        //TODO: Fazer janela de definição de parâmetros
        this.ocr.treinarRede();
    }

    @Override
    public void atualizar(BotaoReconhecer_Observable observavel) {
        BufferedImage imagemDoPainel = ((Janela) observavel).getImagem();
        this.reconhecer(imagemDoPainel);
    }
}

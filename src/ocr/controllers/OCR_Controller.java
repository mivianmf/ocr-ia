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
import javax.swing.JOptionPane;
import ocr.entities.ImageTools;
import ocr.entities.OCR;
import ocr.gui.Janela;
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
        NeuralNet_Observer {

    public static final int SIZE_X_NUMBERS = 50;
    public static final int SIZE_Y_NUMBERS = 75;
    private Janela janela;
    private OCR ocr;
    private IplImage imagemTreino, imagemTreinoRedimensionada;
    private int quantTreino;
    public ArrayList<NeuralNet_Observer> observadores;

    public OCR_Controller() {
        this.janela = new Janela();
        this.ocr = new OCR(TransferFunctionType.SIGMOID);
        janela.setTitle("OCR");
        janela.adicionarObservador(this);
    }

    public void rodar() {
        //System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        /*quantTreino = 3;
         quantClasses = 2;
         //imagem = Highgui.imread("sorriso1.jpg");
         imagensTreino = new IplImage[quantTreino];
         imagensTreinoRedimensionadas = new IplImage[quantTreino];

         ocr = new OCR(quantTreino * quantClasses - 1);

         for (int i = 0; i < quantClasses; i++) {
         for (int j = 0; j < quantTreino; j++) {
         String nomeArquivo = "src/ocr/images/" + (i + 1) + "_" + (j + 1) + ".png"; 
         imagemIpl = ImageTools.abrirImagem(nomeArquivo);

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
         cvResize(imagensTreino[j], imagensTreinoRedimensionadas[j], CV_INTER_AREA);

         ocr.adicionarImagemAoTreino(imagensTreinoRedimensionadas[j], i - 1);
                    
         cvSaveImage(nomeArquivo, imagensTreinoRedimensionadas[j]);
         //imagem = resizedImage.getBufferedImage();
         //ImageTools.mostrarImagem(imagem);
         }//end if
         }//end for  
         }//end for
         ocr.treinarRede();

         //Reconhecer
         imagemIpl = ImageTools.abrirImagem("src/ocr/images/1_3.png");//TODO: Trocar imagem a ser reconhecida

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
         cvResize(imagemTeste, imagemTesteRedimensionada, CV_INTER_AREA);

         System.out.println("Saída da rede = " + (ocr.reconhecer(imagemTesteRedimensionada)));

         imagem = imagemTeste.getBufferedImage();
                       
         ImageTools.mostrarImagem(imagem);
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

    /**
     * Método principal, controla todo o fluxo de dados
     *
     * @param args
     */
    public static void main(String[] args) throws IOException {
        new OCR_Controller().rodar();
    }

    //Adiciona uma imagem do painel ao treino
    private void adicionarAoTreino(BufferedImage imagemDoPainel, Integer classe) {
        IplImage novaImagemTreino = cvCreateImage(cvSize(imagemDoPainel.getWidth(),
                imagemDoPainel.getHeight()), IPL_DEPTH_8U, 1);
        novaImagemTreino.copyFrom(imagemDoPainel);

        ArrayList<CvRect> retangulos = segmentar(novaImagemTreino);

        //if (retangulos.size() == 1) {
        cvSetImageROI(novaImagemTreino, retangulos.get(0));//Define região de interesse
        imagemTreino = cvCreateImage(cvGetSize(novaImagemTreino),
                novaImagemTreino.depth(), novaImagemTreino.nChannels());//Cria uma nova imagem de treino
        cvCopy(novaImagemTreino, imagemTreino);//Copia os dados para a nova imagem
        cvResetImageROI(novaImagemTreino);//Reseta a região de interesse

        imagemTreinoRedimensionada = IplImage.create(SIZE_X_NUMBERS, SIZE_Y_NUMBERS,
                imagemTreino.depth(), imagemTreino.nChannels());//Cria a imagem para redimensionamento
        cvResize(imagemTreino, imagemTreinoRedimensionada, CV_INTER_AREA);//Redimensiona
        this.ocr.adicionarImagemAoTreino(imagemTreinoRedimensionada, classe);//Adiciona ao treinamento
        if (classe == 1) {
            this.janela.incrementarContadorNumero();
        } else {
            this.janela.incrementarContadorNaoNumero();
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

    public IplImage preprocessar(IplImage imagem) {
        IplImage temp = cvCreateImage(cvSize(imagem.width(), imagem.height()),
                IPL_DEPTH_8U, 1);
        opencv_imgproc.IplConvKernel element = new opencv_imgproc.IplConvKernel();
        IplImage cinza = ImageTools.converterImgEscalaCinza(imagem);
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
    public void adicionarObservador(NeuralNet_Observer observador) {
        this.observadores.add(observador);
    }

    @Override
    public void removerObservador(NeuralNet_Observer observador) {
        this.observadores.remove(observador);
    }

    @Override
    public void notificar() {
        for (NeuralNet_Observer neuralNet_Observer : observadores) {
            neuralNet_Observer.atualizar(this.ocr);
        }
    }

    @Override
    public void atualizar(NeuralNet_Observable observavel) {
        notificar();
    }
}

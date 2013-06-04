package ocr.entities;

import com.googlecode.javacpp.*;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvContour;
import com.googlecode.javacv.cpp.opencv_core.CvMemStorage;
import com.googlecode.javacv.cpp.opencv_core.CvSeq;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.*;
import static com.googlecode.javacv.cpp.opencv_imgproc.*;
import com.googlecode.javacv.cpp.opencv_imgproc.CvHistogram;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ImageTools {

    public static int getLimiar(int[] histVetorizado) {
        // Zack, G. W., Rogers, W. E. and Latt, S. A., 1977,
        // Automatic Measurement of Sister Chromatid Exchange Frequency,
        // Journal of Histochemistry and Cytochemistry 25 (7), pp. 741-753

        // encontra o primeiro ponto com valor > 0 da esquerda -> direita
        int min = 0, dmax = 0, max = 0, min2 = 0;
        for (int i = 0; i < histVetorizado.length; i++) {
            if (histVetorizado[i] > 0) {
                min = i;
                break;
            }
        }
        if (min > 0) {
            min--; // alinha a posi��o para antes do valor m�nimo
        }
        // encontra o primeiro ponto com valor > 0 da direita -> esquerda
        for (int i = 255; i >= 0; i--) {
            if (histVetorizado[i] > 0) {
                min2 = i;
                break;
            }
        }
        if (min2 < 255) {
            min2++; // alinha a posi��o para antes do valor m�nimo
        }
        // encontra o maior valor do hist(dmax)e a posi��o dele (max)
        for (int i = 0; i < 256; i++) {
            if (histVetorizado[i] > dmax) {
                max = i;
                dmax = histVetorizado[i];
            }
        }
        // encontra o lado maior entre o max e o min(min <- max -> min2);
        // se o maior lado for max -> min2, o histograma � revertido
        if ((max - min) < (min2 - max)) {
            int left = 0;
            int right = 255;
            while (left < right) {
                int temp = histVetorizado[left];
                histVetorizado[left] = histVetorizado[right];
                histVetorizado[right] = temp;
                left++;
                right--;
            }
            min = 255 - min2;
            max = 255 - max;
        }

        if (min == max) {
            return min;
        }

        // descreve uma linha por nx * x + ny * y - d = 0
        double nx, ny, d;
        nx = histVetorizado[max]; // maior valor ->eixo y
        ny = min - max; // delta das posi��es -> eixo x
        d = Math.sqrt(nx * nx + ny * ny);
        nx /= d;
        ny /= d;
        d = nx * min + ny * histVetorizado[min];

        // find split point
        int split = min;
        double splitDistance = 0;
        for (int i = min + 1; i <= max; i++) {
            double newDistance = nx * i + ny * histVetorizado[i] - d;
            if (newDistance > splitDistance) {
                split = i;
                splitDistance = newDistance;
            }
        }
        split--;

        return split;
    }

    public static IplImage converterImgEscalaCinza(IplImage imgSrc) {
        IplImage cinza = cvCreateImage(cvSize(imgSrc.width(), imgSrc.height()),
                IPL_DEPTH_8U, 1);
        cvCvtColor(imgSrc, cinza, CV_RGB2GRAY);
        return cinza;
    }

    public static CvHistogram getHistograma(IplImage imgSrc) {
        CvHistogram histograma = new CvHistogram();
        int[] tonsCinza = new int[]{256};
        float[][] range = new float[][]{new float[]{(float) 0, (float) 255}};

        histograma = cvCreateHist(1, tonsCinza, CV_HIST_ARRAY, range, 1);
        cvCalcHist(new IplImage[]{imgSrc}, histograma, 0, null);
        return histograma;
    }

    public static int[] converterHistToArray(CvHistogram histograma) {
        int[] histoArr = new int[256];
        for (int i = 0; i < 255; i++) {
            histoArr[i] = (int) cvGetReal1D(histograma.bins(), i);
        }
        return histoArr;
    }

    public static IplImage desenharHistograma(CvHistogram histograma,
            CvSize tamanho) {
        IplImage imgHistograma = cvCreateImage(
                cvSize(tamanho.width(), tamanho.height()), IPL_DEPTH_8U, 1);
        cvSet(imgHistograma, cvScalarAll(255));
        float w_scale;
        float[] max_val = new float[]{0};

        cvGetMinMaxHistValue(histograma, null, max_val, null, null);
        cvScale(histograma.bins(), histograma.bins(),
                (double) ((imgHistograma.height()) / max_val[0]), 0);
        w_scale = ((float) imgHistograma.width()) / 256;
        for (int i = 0; i < 256; i++) {
            cvRectangle(
                    imgHistograma,
                    cvPoint((int) (i * w_scale), imgHistograma.height()),
                    cvPoint((int) ((i + 1) * w_scale), imgHistograma.height()
                    - ((int) (cvGetReal1D(histograma.bins(), i)))),
                    cvScalar(0, 0, 0, 0), -1, 8, 0);
        }

        return imgHistograma;
    }

    public static IplImage equalizarImgCinza(IplImage imgSrc) {
        IplImage imgEqualizada = cvCreateImage(
                cvSize(imgSrc.width(), imgSrc.height()), IPL_DEPTH_8U, 1);
        cvEqualizeHist(imgSrc, imgEqualizada);
        return imgEqualizada;
    }

    public static IplImage filtroSmooth(IplImage imgSrc) {
        IplImage imgDst = cvCreateImage(cvSize(imgSrc.width(), imgSrc.height()),
                IPL_DEPTH_8U, 1);
        cvCopy(imgSrc, imgDst);
        cvSmooth(imgDst, imgDst, CV_GAUSSIAN, 3, 0, 0, 0);
        return imgDst;
    }

    public static IplImage limiarizar(IplImage imgSrc) {
        IplImage imgDst = cvCreateImage(cvSize(imgSrc.width(), imgSrc.height()),
                IPL_DEPTH_8U, 1);
        cvCopy(imgSrc, imgDst);
        //cvThreshold(imgDst, imgDst, CV_THRESH_OTSU, 255, CV_THRESH_BINARY_INV);
        cvThreshold(imgDst, imgDst, getLimiar(converterHistToArray(getHistograma(imgDst))), 255, CV_THRESH_BINARY_INV);
        return imgDst;
    }

    public static CvSeq getContornos(IplImage imgSrc) {
        IplImage imgDst = cvCreateImage(cvSize(imgSrc.width(), imgSrc.height()),
                IPL_DEPTH_8U, 1);
        cvCopy(imgSrc, imgDst);
        CvMemStorage m = cvCreateMemStorage(0);
        CvSeq contornos = new CvSeq();
        cvFindContours(imgDst, m, contornos, Loader.sizeof(CvContour.class),
                CV_RETR_EXTERNAL, CV_CHAIN_APPROX_SIMPLE, cvPoint(0, 0));
        return contornos;
    }

    public static IplImage abrirImagem(String nomeArq) {
        IplImage img = cvLoadImage(nomeArq);
        return img;
    }

    public static BufferedImage getImageBuffer(IplImage img) {
        BufferedImage imagem = img.getBufferedImage();
        return imagem;
    }

    public static void mostrarImagem(BufferedImage imagem) {
        //janela do programa      
        JFrame frame = new JFrame("Carregar Imagem");  
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        //container onde serão adicionados todos componentes  
        Container container = frame.getContentPane();  
  
        //carrega a imagem passando o nome da mesma  
        ImageIcon img = new ImageIcon(imagem);  
          
        //pega a altura e largura  
        int altura = img.getIconHeight();  
        int largura = img.getIconWidth();  
          
        //adiciona a imagem em um label  
        JLabel label = new JLabel(img);  
        //adiciona a altura e largura em outro label  
        JLabel label2 = new JLabel("Altura: "+altura+"      Largura: "+largura);  
  
        //cria o JPanel para adicionar os labels  
        JPanel panel = new JPanel();  
        panel.add(label, BorderLayout.NORTH);  
        panel.add(label2, BorderLayout.SOUTH);  
  
        //adiciona o panel no container  
        container.add(panel, BorderLayout.CENTER);  
          
        frame.pack();  
        frame.setVisible(true);    

    }
}
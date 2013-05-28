/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.entities;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.imgrec.image.Image;

/**
 * Entidade da classe OCR
 *
 * @author Bruno, Mívian e Washington
 */
public class OCR {

    private NeuralNetwork rede;
    private Map<Image, Integer> treino;
    private DataSet conjunto;

    //CONSTRUTORES
    public OCR(Integer numPixels) {
        this.rede = new NeuralNetwork();
        this.treino = new HashMap<>();
        this.conjunto = new DataSet(numPixels);
    }//end construtor

    public OCR(NeuralNetwork rede, Map<Image, Integer> treino,
            Integer numPixels) {
        this.rede = rede;
        this.treino = treino;
    }//end construtor

    //MÉTODOS
    /**
     * Treina a rede neural
     */
    public void treinarRede() {
        rede.learn(this.conjunto);
    }//end treinarRede

    /**
     * Adiciona uma imagem ao conjunto de treino
     *
     * @param imagem
     * @param classe
     */
    public void adicionarImagemAoTreino(Image imagem, Integer classe) {
        int[] pixels;
        double[] entrada;
        double[] saidaEsperada;

        this.treino.put(imagem, classe);//Adiciona a imagem no vetor de treino

        //Obtém o vetor de pixels da imagem
        pixels = imagem.getPixels(
                0,//Offset
                1,//Stride
                0,//x
                0,//y
                imagem.getWidth(),//Largura
                imagem.getHeight());//Altura

        //Realizando conversão para Double
        entrada = new double[pixels.length];
        saidaEsperada = new double[1];

        for (int i = 0; i < pixels.length; i++) {
            entrada[i] = (double) pixels[i];
        }//end for        
        saidaEsperada[0] = (double) classe;

        //Adicionando no conjunto de treinamento
        this.conjunto.addRow(entrada, saidaEsperada);
    }//end adicionarImagemAoTreino

    /**
     * Adiciona uma imagem rotulada ao conjunto de treino
     *
     * @param imagem
     * @param classe
     */
    public void adicionarImagemAoTreino(Image imagem, Integer classe,
            String rotulo) {
        double[] entrada;
        double[] saidaEsperada;
        DataSetRow dados;
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        this.treino.put(imagem, classe);//Adiciona a imagem no vetor de treino

        //Realizando conversão para Double
        entrada = new double[largura * altura];
        saidaEsperada = new double[1];

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                entrada[i] = (double) imagem.getPixel(i, j);
            }
        }//end for
        
        saidaEsperada[0] = (double) classe;

        //Define os dados (pixels) e o rótulo
        dados = new DataSetRow(entrada, saidaEsperada);
        dados.setLabel(rotulo);

        //Adicionando no conjunto de treinamento
        this.conjunto.addRow(dados);
    }//end adicionarImagemAoTreino

    /**
     * Retorna a classe da imagem removida
     *
     * @param imagem
     * @return classe
     */
    public Integer removerImagemDoTreino(Image imagem, String rotulo) {
        int indice = -1;
        List<DataSetRow> imagens = this.conjunto.getRows();

        for (int i = 0; i < imagens.size(); i++) {
            if (imagens.get(i).getLabel().equalsIgnoreCase(rotulo)) {
                indice = i;
                i = imagens.size();
            }//end if
        }//end for

        //Remove imagem do conjunto de treino
        this.conjunto.removeRowAt(indice);

        return this.treino.remove(imagem);
    }//end removerImagemDoTreino

    /**
     * Retorna a classe obtida do reconhecimento
     *
     * @param imagem
     * @return
     */
    public Integer reconhecer(Image imagem) {
        double[] entrada;
        double[] saidaObtida;
        int largura = imagem.getWidth();
        int altura = imagem.getHeight();

        //Realizando conversão para Double
        entrada = new double[largura * altura];

        for (int i = 0; i < largura; i++) {
            for (int j = 0; j < altura; j++) {
                entrada[i] = (double) imagem.getPixel(i, j);
            }
        }//end for

        //Define uma nova entrada na rede neural
        this.rede.setInput(entrada);

        //Calcula a rede neural com a entrada dada
        this.rede.calculate();

        //Obtém a saída da rede neural
        saidaObtida = this.rede.getOutput();

        return (int) saidaObtida[0];
    }//end reconhecer

    //GETTERS AND SETTERS
    public NeuralNetwork getRede() {
        return rede;
    }

    public void setRede(NeuralNetwork rede) {
        this.rede = rede;
    }

    public Map<Image, Integer> getTreino() {
        return treino;
    }

    public void setTreino(Map<Image, Integer> treino) {
        this.treino = treino;
    }
}//end class

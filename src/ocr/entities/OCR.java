/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.entities;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.core.learning.LearningRule;
import org.neuroph.imgrec.FractionRgbData;
import org.neuroph.imgrec.image.Image;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.learning.BackPropagation;
import org.neuroph.nnet.learning.MomentumBackpropagation;
import org.neuroph.util.TransferFunctionType;

/**
 * Entidade da classe OCR
 *
 * @author Bruno, Mívian e Washington
 */
public class OCR implements LearningEventListener {

    private NeuralNetwork rede;
    private Map<Image, Integer> treino;
    private DataSet conjunto;

    //CONSTRUTORES
    public OCR() {        
    }//end construtor
    
    public OCR(Integer numPixels) {
        this.rede = new MultiLayerPerceptron(
                TransferFunctionType.TANH,
                numPixels,
                numPixels + 1,
                1);
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
        // enable batch if using MomentumBackpropagation
        if( this.rede.getLearningRule() instanceof MomentumBackpropagation ) {
            ((MomentumBackpropagation)this.rede.getLearningRule()).setBatchMode(true);
            //((MomentumBackpropagation)this.rede.getLearningRule()).setLearningRate(0.1);
            //((MomentumBackpropagation)this.rede.getLearningRule()).setMaxError(0.001);
        }//end if

        LearningRule learningRule = this.rede.getLearningRule();
        learningRule.addListener(this);
        
        //Realiza o aprendizado
        System.out.println("Treinando a rede...");
        this.rede.learn(this.conjunto, learningRule);
        
         //Testa os perceptrons
        System.out.println("Testando a rede...");
        this.testarRede();
    }//end treinarRede

    /**
     * Adiciona uma imagem ao conjunto de treino
     * @param imagem
     * @param classe
     */
    public void adicionarImagemAoTreino(Image imagem, Integer classe) {
        double[] entrada;//Entrada da rede
        double[] saidaEsperada;//Saída esperada
        FractionRgbData ajudante;//Ajudante para conversão para preto e branco

        this.treino.put(imagem, classe);//Adiciona a imagem no vetor de treino

        ajudante = new FractionRgbData(imagem);

        entrada = FractionRgbData.convertRgbInputToBinaryBlackAndWhite(
                ajudante.getFlattenedRgbValues());

        saidaEsperada = new double[1];
        saidaEsperada[0] = (double) classe.intValue();

        //Adicionando no conjunto de treinamento
        this.conjunto.addRow(entrada, saidaEsperada);
    }//end adicionarImagemAoTreino

    /**
     * Adiciona uma imagem rotulada ao conjunto de treino
     * @param imagem
     * @param classe
     */
    public void adicionarImagemAoTreino(Image imagem, Integer classe,
            String rotulo) {
        double[] entrada;//Entrada da rede
        double[] saidaEsperada;//Saída esperada
        DataSetRow dados;//Dados de entrada da rede
        FractionRgbData ajudante;//Ajudante para conversão para preto e branco

        this.treino.put(imagem, classe);//Adiciona a imagem no vetor de treino

        ajudante = new FractionRgbData(imagem);

        entrada = FractionRgbData.convertRgbInputToBinaryBlackAndWhite(
                ajudante.getFlattenedRgbValues());

        saidaEsperada = new double[1];
        saidaEsperada[0] = (double) classe.intValue();

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
    public double reconhecer(Image imagem) {
        double[] entrada;//Entrada da rede
        double[] saidaObtida;//Saída obtida pela rede
        FractionRgbData ajudante;//Ajudante para conversão para preto e branco

        ajudante = new FractionRgbData(imagem);

        entrada = FractionRgbData.convertRgbInputToBinaryBlackAndWhite(
                ajudante.getFlattenedRgbValues());

        //Define uma nova entrada na rede neural
        this.rede.setInput(entrada);

        //Calcula a rede neural com a entrada dada
        this.rede.calculate();

        //Obtém a saída da rede neural
        saidaObtida = this.rede.getOutput();

        return Math.round(saidaObtida[0]);
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

     /**
     * Prints network output for the each element from the specified training set.
     * @param neuralNet neural network
     * @param trainingSet training set
     */
    public void testarRede() {
        for(DataSetRow testSetRow : this.conjunto.getRows()) {
            this.rede.setInput(testSetRow.getInput());
            this.rede.calculate();
            double[] networkOutput = this.rede.getOutput();

            System.out.print("Entrada: " + Arrays.toString( testSetRow.getInput() ) );
            System.out.println(" Saída: " + Arrays.toString( networkOutput) );
        }
    }
    
    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation)event.getSource();
        System.out.println(bp.getCurrentIteration() + ". iteração : "+
                bp.getTotalNetworkError());
    }   

    /**
     * Salva a rede neural treinada
     * @param nomeArquivo
     */
    public void salvarRede(String nomeArquivo) {
        this.rede.save(nomeArquivo);
    }

    /**
     * Carrega a rede neural salva
     * @param nomeArquivo
     */
    public void carregarRede(String nomeArquivo) {
        this.rede = MultiLayerPerceptron.load(nomeArquivo);
    }
}//end class

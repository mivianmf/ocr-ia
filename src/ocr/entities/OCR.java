/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.entities;

import com.googlecode.javacv.cpp.opencv_core.IplImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import ocr.controllers.DimensionReducer;
import ocr.interfaces.NeuralNet_Observable;
import ocr.interfaces.NeuralNet_Observer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.events.LearningEvent;
import org.neuroph.core.events.LearningEventListener;
import org.neuroph.core.learning.DataSet;
import org.neuroph.core.learning.DataSetRow;
import org.neuroph.core.learning.LearningRule;
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
public class OCR implements LearningEventListener, NeuralNet_Observable {

    private NeuralNetwork rede;
    private Map<IplImage, Integer> treino;
    private DataSet conjunto;
    private DimensionReducer redutor;
    private TransferFunctionType funcaoAtivacao;
    public ArrayList<NeuralNet_Observer> observadores;
    private String estadoAtual;

    //CONSTRUTORES
    public OCR(TransferFunctionType funcaoAtivacao) {
        this.funcaoAtivacao = funcaoAtivacao;
        this.treino = new HashMap<>();
    }//end construtor

    public OCR(NeuralNetwork rede, Map<IplImage, Integer> treino,
            Integer quantValores) {
        this.rede = rede;
        this.treino = treino;
    }//end construtor

    //MÉTODOS
    /**
     * Treina a rede neural
     */
    public void treinarRede() {
        double[][] entradas = new double[this.treino.size()][this.conjunto.size()];
        double[][] saidasEsperadas = new double[this.treino.size()][1];
        Integer[] saidasTemp = new Integer[this.treino.size()];
        IplImage[] imagensEntrada = new IplImage[this.treino.size()];
        this.treino.values().toArray(saidasTemp);

        //Redução de dimensionalidade
        redutor = new DimensionReducer();

        //Converte treino para vetor de IplImage e começa a redução
        this.treino.keySet().toArray(imagensEntrada);
        redutor.reduce(imagensEntrada, this.treino.size());
        
        this.conjunto = new DataSet(this.treino.size() - 1, 1);
        this.rede = new MultiLayerPerceptron(
                this.funcaoAtivacao,
                this.conjunto.getInputSize(),
                this.conjunto.getInputSize() + 1,
                1);
        
        for (int i = 0; i < this.treino.size(); i++) {
            entradas[i] = new double[this.conjunto.getInputSize()];
            for (int j = 0; j < entradas[i].length; j++) {
                entradas[i][j] = redutor.projectedTrainNumberMat.get(i, j);
            }//end for

            saidasEsperadas[i] = new double[1];
            saidasEsperadas[i][0] = (double) saidasTemp[i].intValue();

            //Adicionando no conjunto de treinamento
            this.conjunto.addRow(entradas[i], saidasEsperadas[i]);
        }//end for

        // enable batch if using MomentumBackpropagation
        if (this.rede.getLearningRule() instanceof MomentumBackpropagation) {
            ((MomentumBackpropagation) this.rede.getLearningRule()).setBatchMode(true);
            //((MomentumBackpropagation)this.rede.getLearningRule()).setLearningRate(0.1);
            ((MomentumBackpropagation)this.rede.getLearningRule()).setMaxError(0.01);
            ((MomentumBackpropagation)this.rede.getLearningRule()).setMaxIterations(10000);
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
     *
     * @param imagem
     * @param classe
     */
    public void adicionarImagemAoTreino(IplImage imagem, Integer classe) {
        this.treino.put(imagem, classe);//Adiciona a imagem no vetor de treino
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
    public double reconhecer(IplImage imagem) {
        double[] entrada = new double[this.conjunto.getInputSize()];
        double[] saidaObtida;

        //Redução de dimensionalidade        
        //Converte treino para vetor de IplImage e começa a redução
        float[] entradasTemp = redutor.recognize(imagem);

        for (int i = 0; i < entradasTemp.length; i++) {
            entrada[i] = (double) entradasTemp[i];
        }//end fors
        
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

    public Map<IplImage, Integer> getTreino() {
        return treino;
    }

    public void setTreino(Map<IplImage, Integer> treino) {
        this.treino = treino;
    }

    /**
     * Prints network output for the each element from the specified training
     * set.
     *
     * @param neuralNet neural network
     * @param trainingSet training set
     */
    public void testarRede() {
        for (DataSetRow testSetRow : this.conjunto.getRows()) {
            this.rede.setInput(testSetRow.getInput());
            this.rede.calculate();
            double[] networkOutput = this.rede.getOutput();

            System.out.print("Saída Esperada: " + testSetRow.getDesiredOutput()[0]);//Arrays.toString(testSetRow.getInput()));
            System.out.print(" Saída: " + Arrays.toString(networkOutput));
            System.out.println(" Arredondada: " + Math.round(networkOutput[0]));
        }
    }

    @Override
    public void handleLearningEvent(LearningEvent event) {
        BackPropagation bp = (BackPropagation) event.getSource();
        estadoAtual += (bp.getCurrentIteration() + ". iteração : "
                + bp.getTotalNetworkError());
        notificar();
    }

    /**
     * Salva a rede neural treinada
     *
     * @param nomeArquivo
     */
    public void salvarRede(String nomeArquivo) {
        this.rede.save(nomeArquivo);
    }

    /**
     * Carrega a rede neural salva
     *
     * @param nomeArquivo
     */
    public void carregarRede(String nomeArquivo) {
        this.rede = MultiLayerPerceptron.load(nomeArquivo);
    }

    public String getEstadoAtual() {
        return estadoAtual;
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
            neuralNet_Observer.atualizar(this);
        }
    }
}//end class

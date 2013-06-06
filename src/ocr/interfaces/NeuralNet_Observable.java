/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.interfaces;

/**
 *
 * @author Bruno, Mívian e Washington
 */
public interface NeuralNet_Observable {
    public void adicionarObservadorRede(NeuralNet_Observer observador);
    public void removerObservadorRede(NeuralNet_Observer observador);
    public void notificarRede();    
}

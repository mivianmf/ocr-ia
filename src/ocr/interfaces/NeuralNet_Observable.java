/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.interfaces;

/**
 *
 * @author WashingtonLuis
 */
public interface NeuralNet_Observable {
    public void adicionarObservador(NeuralNet_Observer observador);
    public void removerObservador(NeuralNet_Observer observador);
    public void notificar();    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.interfaces;

/**
 *
 * @author WashingtonLuis
 */
public interface BotaoReconhecer_Observable {
    public void adicionarObservadorReconhecer(BotaoReconhecer_Observer observador);
    public void removerObservadorReconhecer(BotaoReconhecer_Observer observador);
    public void notificarReconhecer();        
}

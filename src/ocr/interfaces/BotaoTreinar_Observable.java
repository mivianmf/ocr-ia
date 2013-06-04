/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.interfaces;

/**
 *
 * @author WashingtonLuis
 */
public interface BotaoTreinar_Observable {
    public void adicionarObservadorTreinar(BotaoTreinar_Observer observador);
    public void removerObservadorTreinar(BotaoTreinar_Observer observador);
    public void notificarTreinar();       
}

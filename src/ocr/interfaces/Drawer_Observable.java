/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.interfaces;

/**
 *
 * @author 407456
 */
public interface Drawer_Observable {
    public void adicionarObservador(Drawer_Observer observador);
    public void removerObservador(Drawer_Observer observador);
    public void notificarDesenho();
}

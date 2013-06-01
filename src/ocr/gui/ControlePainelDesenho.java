/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Panel;
import javax.swing.JButton;

/**
 *
 * @author Rubico
 */
public class ControlePainelDesenho extends Panel{
   private JButton salvar;
   private JButton limpar;
   private JButton adicionarTreinamento;
   
   
   public ControlePainelDesenho ( ){       
       
       this.setLayout (new BorderLayout ( ));
       
       this.salvar = new JButton ("Salvar Imagem");
       this.limpar = new JButton ("Limpar");
       this.adicionarTreinamento = new JButton ("Adicionar Treinamento");
       
       this.add(adicionarTreinamento, BorderLayout.WEST);
       this.add(salvar, BorderLayout.CENTER);
       this.add(limpar, BorderLayout.EAST);
   }
}

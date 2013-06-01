/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JButton;
import ocr.controllers.ControleDesenhoObservador;
import ocr.controllers.ImagemGetter;

/**
 *
 * @author Rubico
 */
public class ControlePainelDesenho extends Panel{
   private JButton salvar;
   private JButton limpar;
   private JButton adicionarTreinamento;
   
   private ImagemGetter imageGetter = null;
   private ArrayList<ControleDesenhoObservador> observadores = new ArrayList<>();
   //private ArrayList<ImagesContainer> containers = new ArrayList<>();
   
   
   public ControlePainelDesenho ( ){       
       
       this.setLayout (new BorderLayout ( ));
       
       this.salvar = new JButton ("Salvar Imagem");
       salvar.addActionListener (new BotaoSalvarAcao());
       
       this.limpar = new JButton ("Limpar");
       limpar.addActionListener(new BotaoLimparObservador());
       
       this.adicionarTreinamento = new JButton ("Adicionar Treinamento");
       
       this.add(adicionarTreinamento, BorderLayout.WEST);
       this.add(salvar, BorderLayout.CENTER);
       this.add(limpar, BorderLayout.EAST);
   }
   
   public void setImagemGetter(ImagemGetter imageGetter) {
        this.imageGetter = imageGetter;
   }
   
   public void addControleDesenhoObservador(ControleDesenhoObservador observer) {
        observadores.add(observer);
   }

   public void removeControleDesenhoObservador(ControleDesenhoObservador observer) {
        observadores.remove(observer);
   }
   
   class BotaoSalvarAcao implements ActionListener{
       @Override
       public void actionPerformed (ActionEvent e){
           if (imageGetter != null){
               try {
                   ImageIO.write((RenderedImage)imageGetter.getImagem(), "PNG", new File("mapa.PNG"));
               } catch (IOException ex) {
                   Logger.getLogger(ControlePainelDesenho.class.getName()).log(Level.SEVERE, null, ex);
               }
           }
       }
   }
   
   class BotaoLimparObservador implements ActionListener {

       @Override
        public void actionPerformed(ActionEvent e) {
            if (imageGetter != null) {
                for (ControleDesenhoObservador o : observadores) {
                    o.limpar();
                }
            }
        }
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.BorderLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import ocr.interfaces.ControleDesenhoObservador;
import ocr.interfaces.Drawer_Observable;
import ocr.interfaces.Drawer_Observer;
import ocr.interfaces.ImagemGetter;

/**
 *
 * @author Rubico
 */
public class ControlePainelDesenho extends Panel implements Drawer_Observable {
    
    private JButton salvar;
    private JButton limpar;
    private JButton treinarRede;
    private JButton reconhecer;
    private JButton adicionarTreinamento;
    private ImagemGetter imageGetter = null;
    private ArrayList<Drawer_Observer> observadores = new ArrayList<>();
    private ArrayList<ControleDesenhoObservador> paineisObservadores = new ArrayList<>();
    boolean treinar;

    public ControlePainelDesenho() {
        this.setLayout(new BorderLayout());

        this.salvar = new JButton("Salvar Imagem");
        this.salvar.addActionListener(new BotaoSalvarAcao());
        this.salvar.setMnemonic(KeyEvent.VK_ENTER);

        this.limpar = new JButton("Limpar");
        this.limpar.addActionListener(new BotaoLimparObservador());

        this.treinarRede = new JButton("Treinar Rede");
        this.treinarRede.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                treinar = true;
                notificar();
                treinar = false;
            }
        });

        this.reconhecer = new JButton("Reconhecer");
        //this.limpar.addActionListener(new BotaoLimparObservador());

        this.adicionarTreinamento = new JButton("Adicionar");
        this.adicionarTreinamento.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                imageGetter.getImagem();
                notificar();
            }
        });

        this.add(adicionarTreinamento, BorderLayout.LINE_START);
        this.add(limpar, BorderLayout.LINE_END);
        this.add(salvar, BorderLayout.CENTER);
        this.add(treinarRede, BorderLayout.AFTER_LAST_LINE);
        this.add(reconhecer, BorderLayout.BEFORE_FIRST_LINE);
    }

    public void setImagemGetter(ImagemGetter imageGetter) {
        this.imageGetter = imageGetter;
    }

    @Override
    public void adicionarObservador(Drawer_Observer observador) {
        this.observadores.add(observador);
    }

    public void adicionarPainelObservador(ControleDesenhoObservador observador) {
        this.paineisObservadores.add(observador);
    }

    @Override
    public void removerObservador(Drawer_Observer observador) {
        this.observadores.remove(observador);
    }

    @Override
    public void notificar() {
        for (Drawer_Observer drawer_Observer : observadores) {
            drawer_Observer.atualizar(this);
        }//end for
    }

    class BotaoSalvarAcao implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            //NO ACTION
            /*String nome = (Janela.nome.getText());
             int cont = Integer.parseInt(Janela.contadorNumero.getText());

             if (imageGetter != null) {
             try {

             ImageIO.write((RenderedImage) imageGetter.getImagem(),
             "PNG", new File("src\\ocr\\images\\" + nome + "_" + cont + ".PNG"));
             cont++;
             Janela.contadorNumero.setText("" + cont);

             for (ControleDesenhoObservador o : paineisObservadores) {
             o.limpar();
             }
             } catch (IOException ex) {
             Logger.getLogger(ControlePainelDesenho.class.getName()).log(Level.SEVERE, null, ex);
             }*/
        }
    }

    class BotaoLimparObservador implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            for (ControleDesenhoObservador o : paineisObservadores) {
                o.limpar();
            }
        }
    }
}
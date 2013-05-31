/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import ocr.interfaces.Drawer_Observable;
import ocr.interfaces.Drawer_Observer;

/**
 *
 * @author 407456
 */
public class Janela extends JFrame implements Drawer_Observable{
    private ArrayList<Drawer_Observer> observadores;
    private PainelDesenho painel;
    private JButton botaoAdicionar, botaoLimpar;
    private Container container;
    private Image imagem;
    private Panel esquerdaPanel, direitaPanel;
    private Integer classe;
    
    public Janela(){
        initComponents();        
    }
    
    public void initComponents(){
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        
        this.setBounds(maxBounds);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);        
        container = this.getContentPane();
        container.setLayout (null);
        
        esquerdaPanel = new Panel ( );
        esquerdaPanel.setLayout (new BorderLayout( ));
        esquerdaPanel.setBounds(0, 0, maxBounds.getSize().width/2, maxBounds.getSize().height - 60);
        esquerdaPanel.setBackground(Color.red);
        container.add(esquerdaPanel);
        
        direitaPanel = new Panel ( );
        direitaPanel.setLayout (new BorderLayout( ));
        direitaPanel.setBounds (maxBounds.getSize( ).width/2, 0, maxBounds.getSize().width/2 - 16, maxBounds.getSize().height - 38);
        direitaPanel.setBackground(Color.blue);
        container.add(direitaPanel);
        
        this.painel = new PainelDesenho();
        this.botaoAdicionar = new JButton("Adicionar ao Treino");
        this.botaoLimpar = new JButton("Limpar Tela");
        
        //this.botaoAdicionar.setBounds(250, 250, 200, 50);
        //this.botaoLimpar.setBounds(250, 350, 200, 50);
        
        this.botaoAdicionar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String resposta = JOptionPane.showInputDialog("Informe a classe da imagem:");
                Integer classeDaImagem = Integer.parseInt(resposta);
                imagem = null;//TODO: IMAGEM SERÁ DEFINIDA AQUI
                classe = classeDaImagem;//Classe informada
                notificar();
            }
        });
        
        this.botaoLimpar.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                painel.limpar();
            }
        });
        
        direitaPanel.add(botaoAdicionar, BorderLayout.SOUTH);
        direitaPanel.add(botaoLimpar, BorderLayout.EAST);
        direitaPanel.add(painel, BorderLayout.CENTER);
        
        this.setVisible(true);
    }//end init

    public Image getImagem() {
        return imagem;
    }

    public Integer getClasse() {
        return classe;
    }
       
    @Override
    public void adicionarObservador(Drawer_Observer observador) {
        observadores.add(observador);
    }

    @Override
    public void removerObservador(Drawer_Observer observador) {
        observadores.remove(observador);
    }

    @Override
    public void notificar() {
        for (Drawer_Observer drawer_Observer : observadores) {
            drawer_Observer.atualizar(this);
        }
    }
    
}

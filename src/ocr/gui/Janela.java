/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.Image;
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
    private Painel painel;
    private JButton botaoAdicionar, botaoLimpar;
    private Image imagem;
    private Integer classe;
    
    public Janela(){
        initComponents();        
    }
    
    public void initComponents(){
        this.setSize(800,600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);
        
        this.painel = new Painel();
        this.botaoAdicionar = new JButton("Adicionar ao Treino");
        this.botaoLimpar = new JButton("Limpar Tela");
        
        this.botaoAdicionar.setBounds(250, 250, 200, 50);
        this.botaoLimpar.setBounds(250, 350, 200, 50);
        
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
        
        this.add(botaoAdicionar);
        this.add(botaoLimpar);
        this.add(painel);
        
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

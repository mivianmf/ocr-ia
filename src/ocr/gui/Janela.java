/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
public class Janela extends JFrame implements Drawer_Observable {

    private ArrayList<Drawer_Observer> observadores;
    private Painel painel;
    private JButton botaoAdicionar, botaoLimpar;
    private Image imagem;
    private Integer classe;

    public Janela() {
        initComponents();
    }

    public void initComponents() {
        this.setSize(800, 600);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(null);

        this.painel = new Painel();
        this.botaoAdicionar = new JButton("Adicionar ao Treino");
        this.botaoLimpar = new JButton("Limpar Tela");
        this.observadores = new ArrayList<>();

        this.botaoAdicionar.setBounds(250, 250, 200, 50);
        this.botaoLimpar.setBounds(250, 350, 200, 50);

        this.botaoAdicionar.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String resposta = JOptionPane.showInputDialog("Informe a classe da imagem:");
                Integer classeDaImagem = Integer.parseInt(resposta);
                
                //Pega a imagem presente no painel e define na imagem da classe
                gerarImagemDoPainel();
                
                //Classe informada
                classe = classeDaImagem;
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

    public void gerarImagemDoPainel() {        
        //Cria imagem buferizada
        BufferedImage bi = new BufferedImage(this.painel.getWidth(),
                this.painel.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        
        //Pega os graficos da imagem
        Graphics2D g = bi.createGraphics();
        
        //Pinta o painel nos gráficos da imagem
        this.painel.paint(g);
        
        //Define nova imagem
        this.imagem = bi;
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

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Panel;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import ocr.interfaces.Drawer_Observable;
import ocr.interfaces.Drawer_Observer;

/**
 *
 * @author 407456
 */
public class Janela extends JFrame implements Drawer_Observable {

    //Gambiarra para criar as imagens
    static JTextField nome;
    static JTextField contador;
            
    private ArrayList<Drawer_Observer> observadores;
    private PainelDesenho painelDesenho;
    //private JButton botaoAdicionar, botaoLimpar;
    private ControlePainelDesenho controlePainelDesenho;
    private Container container;
    private Image imagem;
    private Panel esquerdaPanel, direitaPanel;
    private Integer classe;

    public Janela() {
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
        esquerdaPanel.setBounds(0, 0, maxBounds.getSize().width/2, maxBounds.getSize().height - 38);
        esquerdaPanel.setBackground(Color.red);
        container.add(esquerdaPanel);
        
        direitaPanel = new Panel ( );
        direitaPanel.setLayout (new BorderLayout( ));
        direitaPanel.setBounds (maxBounds.getSize( ).width/2, 0, maxBounds.getSize().width/2 - 16, maxBounds.getSize().height - 38);
        direitaPanel.setBackground(Color.blue);
        container.add(direitaPanel);
        
        //GAMBIARRA MASTER BLASTER PARA A CRIACAO DAS IMAGENS
        esquerdaPanel.setLayout(null);
        
        JLabel lnome = new JLabel("Número que está sendo desenhado");
        lnome.setBounds (10, 10, 250, 25);
        esquerdaPanel.add(lnome);
        
        JLabel lcontador = new JLabel ("Contador de quantas imagens");
        lcontador.setBounds (10, 50, 250, 25);
        esquerdaPanel.add(lcontador);
        
        nome = new JTextField( );
        nome.setBounds (10, 30, 100, 25);
        esquerdaPanel.add(nome);
        
        contador = new JTextField( );
        contador.setBounds (10, 70, 50, 25);
        contador.setText("1");
        esquerdaPanel.add(contador);
        //FIM DA GAMBIARRA #################################################
        
        this.painelDesenho = new PainelDesenho();
        this.direitaPanel.add(painelDesenho, BorderLayout.CENTER);
        
        this.controlePainelDesenho = new ControlePainelDesenho();
        this.controlePainelDesenho.addControleDesenhoObservador(painelDesenho);
        this.controlePainelDesenho.setImagemGetter(painelDesenho);
        
        this.direitaPanel.add(controlePainelDesenho, BorderLayout.SOUTH);
        
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
        BufferedImage bi = new BufferedImage(this.painelDesenho.getWidth(),
                this.painelDesenho.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        
        //Pega os graficos da imagem
        Graphics2D g = bi.createGraphics();
        
        //Pinta o painel nos gráficos da imagem
        this.painelDesenho.paint(g);
        
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

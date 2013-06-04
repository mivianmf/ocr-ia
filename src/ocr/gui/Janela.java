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
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import ocr.entities.OCR;
import ocr.interfaces.BotaoReconhecer_Observable;
import ocr.interfaces.BotaoReconhecer_Observer;
import ocr.interfaces.BotaoTreinar_Observable;
import ocr.interfaces.BotaoTreinar_Observer;
import ocr.interfaces.Drawer_Observable;
import ocr.interfaces.Drawer_Observer;
import ocr.interfaces.NeuralNet_Observable;
import ocr.interfaces.NeuralNet_Observer;

/**
 *
 * @author 407456
 */
public class Janela extends JFrame implements Drawer_Observable, Drawer_Observer,
        NeuralNet_Observer, BotaoReconhecer_Observable, BotaoReconhecer_Observer,
        BotaoTreinar_Observable, BotaoTreinar_Observer {

    private JTextField nome;
    private JTextField contadorNumero;
    private JTextField contadorNaoNumero;
    private ArrayList<Drawer_Observer> observadores = new ArrayList<>();
    private ArrayList<BotaoReconhecer_Observer> observadoresReconhecer = new ArrayList<>();
    private ArrayList<BotaoTreinar_Observer> observadoresTreinar = new ArrayList<>();
    private PainelDesenho painelDesenho;
    private ControlePainelDesenho controlePainelDesenho;
    private Container container;
    private Panel esquerdaPanel, direitaPanel;
    private Integer classe;
    private JRadioButton numero;
    private JRadioButton naoNumero;
    private ButtonGroup grupo;
    private JTextArea area;
    private BufferedImage imagem;

    public Janela() {
        initComponents();
    }

    public void initComponents() {
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        Rectangle bounds = new Rectangle(0, 0, 800, 600);

        this.setBounds(bounds);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        //this.setBounds(maxBounds);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        container = this.getContentPane();
        container.setLayout(null);

        esquerdaPanel = new Panel();
        esquerdaPanel.setLayout(new BorderLayout());
        esquerdaPanel.setBounds(0, 0, bounds.getSize().width / 2, bounds.getSize().height - 38);
        esquerdaPanel.setBackground(Color.red);
        container.add(esquerdaPanel);

        direitaPanel = new Panel();
        direitaPanel.setLayout(new BorderLayout());
        direitaPanel.setBounds(bounds.getSize().width / 2 + 5, 10,
                bounds.getSize().width / 2 - 30, bounds.getSize().height - 45);
        direitaPanel.setBackground(Color.blue);
        container.add(direitaPanel);

        //Criação das Imagens
        esquerdaPanel.setLayout(null);

        JLabel lnome = new JLabel("Escolha a classe a ser desenhada:");
        lnome.setBounds(10, 10, 250, 25);
        esquerdaPanel.add(lnome);

        JLabel lcontador = new JLabel("Contador de imagens por classe:");
        lcontador.setBounds(10, 55, 250, 25);
        esquerdaPanel.add(lcontador);

        JLabel lcontadorNum = new JLabel("Número");
        lcontadorNum.setBounds(30, 70, 60, 25);
        esquerdaPanel.add(lcontadorNum);

        JLabel lcontadorNaoNum = new JLabel("Não-número");
        lcontadorNaoNum.setBounds(100, 70, 100, 25);
        esquerdaPanel.add(lcontadorNaoNum);

        grupo = new ButtonGroup();
        numero = new JRadioButton("Número");
        naoNumero = new JRadioButton("Não-número");
        area = new JTextArea("Feedback");
        area.setBounds(10, 130, bounds.getSize().width / 2 - 25, 400);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setEditable(false);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scroll.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.add(scroll);

        numero.setBounds(10, 30, 100, 25);
        numero.setBackground(Color.red);
        numero.setSelected(true);
        naoNumero.setBounds(120, 30, 100, 25);
        naoNumero.setBackground(Color.red);
        grupo.add(numero);
        grupo.add(naoNumero);

        esquerdaPanel.add(numero);
        esquerdaPanel.add(naoNumero);
        esquerdaPanel.add(area);

        contadorNumero = new JTextField();
        contadorNumero.setBounds(37, 92, 30, 25);
        contadorNumero.setText("0");
        contadorNumero.setEditable(false);
        esquerdaPanel.add(contadorNumero);

        contadorNaoNumero = new JTextField();
        contadorNaoNumero.setBounds(118, 92, 30, 25);
        contadorNaoNumero.setText("0");
        contadorNaoNumero.setEditable(false);
        esquerdaPanel.add(contadorNaoNumero);

        this.painelDesenho = new PainelDesenho();
        this.direitaPanel.add(painelDesenho, BorderLayout.CENTER);

        //Adicionando os observadores
        this.controlePainelDesenho = new ControlePainelDesenho();
        this.controlePainelDesenho.adicionarObservador(this);
        this.controlePainelDesenho.adicionarPainelObservador(this.painelDesenho);
        this.controlePainelDesenho.adicionarObservadorReconhecer(this);
        this.controlePainelDesenho.adicionarObservadorTreinar(this);
        this.controlePainelDesenho.setImagemGetter(painelDesenho);

        this.direitaPanel.add(controlePainelDesenho, BorderLayout.SOUTH);

        this.setVisible(true);
    }//end init

    /**
     * Limpa o campo de desenho
     */
    public void limpar() {
        this.painelDesenho.limpar();
    }

    /**
     * Retorna a imagem do painel de desenho
     *
     * @return
     */
    public BufferedImage getImagem() {
        return this.painelDesenho.getImagem();
    }

    /**
     * Retorna a classe
     *
     * @return
     */
    public Integer getClasse() {
        return this.classe;
    }

    public void incrementarContadorNumero() {
        this.contadorNumero.setText("" + (Integer.parseInt(this.contadorNumero.getText()) + 1));
    }

    public void incrementarContadorNaoNumero() {
        this.contadorNaoNumero.setText("" + (Integer.parseInt(this.contadorNaoNumero.getText()) + 1));
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
    public void notificarDesenho() {
        for (Drawer_Observer drawer_Observer : observadores) {
            drawer_Observer.atualizar(this);
        }
    }

    @Override
    public void atualizar(Drawer_Observable observavel) {
        if (observavel instanceof ControlePainelDesenho) {
            this.imagem = this.painelDesenho.getImagem();
            if (this.numero.isSelected()) {//Se classe é número então 1, senão 0
                this.classe = 1;
            } else {
                this.classe = -1;
            }//end else
            notificarDesenho();
        }//end if
    }

    @Override
    public void atualizar(NeuralNet_Observable observavel) {
        this.area.setText(((OCR) observavel).getEstadoAtual());
        //repaint();
    }

    @Override
    public void adicionarObservadorReconhecer(BotaoReconhecer_Observer observador) {
        this.observadoresReconhecer.add(observador);
    }

    @Override
    public void removerObservadorReconhecer(BotaoReconhecer_Observer observador) {
        this.observadoresReconhecer.remove(observador);
    }

    @Override
    public void adicionarObservadorTreinar(BotaoTreinar_Observer observador) {
        this.observadoresTreinar.add(observador);
    }

    @Override
    public void removerObservadorTreinar(BotaoTreinar_Observer observador) {
        this.observadoresTreinar.remove(observador);
    }

    @Override
    public void notificarReconhecer() {
        for (BotaoReconhecer_Observer observador : observadoresReconhecer) {
            observador.atualizar(this);
        }
    }

    @Override
    public void notificarTreinar() {
        for (BotaoTreinar_Observer observador : observadoresTreinar) {
            observador.atualizar(this);
        }
    }

    @Override
    public void atualizar(BotaoReconhecer_Observable observavel) {
        this.imagem = this.painelDesenho.getImagem();
        notificarReconhecer();
    }

    @Override
    public void atualizar(BotaoTreinar_Observable observavel) {
        notificarTreinar();
    }
}

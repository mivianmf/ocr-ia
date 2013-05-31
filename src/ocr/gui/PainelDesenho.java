/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JPanel;

/**
 *
 * @author 407456
 */
public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener{
    private Image imagem;
    private boolean[][] pixels;
    public static final int TAMANHO_X = 200;
    public static final int TAMANHO_Y = 300;
    

    public PainelDesenho() {
        this.initComponents();
    }
    
    public void initComponents(){
        this.imagem = null;
        this.pixels = new boolean[TAMANHO_X][TAMANHO_Y];
        //this.setBounds(10, 200, TAMANHO_X, TAMANHO_Y);
        this.setBackground(Color.green);
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.BLACK);
        
        for (int i = 0; i < TAMANHO_X; i++) {
             for (int j = 0; j < TAMANHO_Y; j++) {
                if(this.pixels[i][j]){
                    g.fillOval(i - 5, j - 5, 10, 10);
                }
            }
        }
    }     
    
    public Image getImagem() {
        return this.imagem;
    }
    
    public void limpar(){
        this.pixels = new boolean[TAMANHO_X][TAMANHO_Y];
        this.repaint();
    }
       
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        this.pixels[e.getX()][e.getY()] = true;
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
}

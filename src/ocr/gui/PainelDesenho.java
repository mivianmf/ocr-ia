/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 *
 * @author 407456
 */
public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener{
    private Image imagem;
    private int[] pixels;
    public static final int TAMANHO_X = 100;
    public static final int TAMANHO_Y = 100;
    

    public PainelDesenho() {
        this.initComponents();
    }
    
    public void initComponents(){
        this.imagem = null;
        this.pixels = new int[TAMANHO_X * TAMANHO_Y];
        
        for (int i=0; i  < pixels.length; i++){
            pixels[i] = 0xFFFFFFFF;
        }
        
        //this.setBounds(10, 200, TAMANHO_X, TAMANHO_Y);
        this.setBackground(Color.green);
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }

    /*@Override
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
    }*/
    
    private double getFieldWidth() {
        return (double) getWidth() / TAMANHO_X;
    }

    private double getFieldHeight() {
        return (double) getHeight() / TAMANHO_Y;
    }
    
    private void paintGrid(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int intY = 0;
        for (double y = 0; intY < TAMANHO_Y; y += getFieldHeight()) {
            //System.out.println(y);
            int intX = 0;
            for (double x = 0; intX < TAMANHO_X; x += getFieldWidth()) {
                Rectangle2D rec = new Rectangle2D.Double(x, y, getFieldWidth(), getFieldHeight());

                g2.setColor(new Color(pixels[intX + intY * TAMANHO_X]));
                g2.fill(rec);
                //g2.setColor(Color.black);
                //g2.draw(rec);
                intX++;
            }
            intY++;
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight());
        paintGrid(g);
    }
    
    public Image getImagem() {
        return this.imagem;
    }
    
    public void limpar(){
        this.pixels = new int[TAMANHO_X * TAMANHO_Y];
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
        double x = getFieldWidth();
        double y = getFieldHeight();

        if (x != 0 && y != 0) {
            try {
                System.out.println("X = "+e.getX());
                System.out.println("Y = "+e.getY());
                x = Math.round(e.getX() / x) ;// TAMANHO_X;
                y = Math.round(e.getY() / y) ;// TAMANHO_Y;
                pixels[(int)x + (int)y * TAMANHO_X] = 0x00000000;
                //System.out.println("rysuje: " + x + " * " + y + " = " + x + y * IMAGE_RES_X);
                repaint();
            } catch (ArrayIndexOutOfBoundsException ex) {
                //System.out.println(ex.getMessage());
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;
import ocr.controllers.ControleDesenhoObservador;
import ocr.controllers.ImagemGetter;

/**
 *
 * @author 407456
 */
public class PainelDesenho extends JPanel implements MouseListener, MouseMotionListener,
                                                     ImagemGetter, ControleDesenhoObservador{
    private BufferedImage imagem;
    private boolean[] pixels;
    private int TAMANHO_X;
    private int TAMANHO_Y;
    

    public PainelDesenho() {
        this.initComponents();
    }
    
    public void initComponents(){
        this.imagem = null;
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    private void paintGrid(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g.setColor(new Color(0x00000000));
        
        for (double y = 0; y < TAMANHO_Y; y++) {
            for (double x = 0; x < TAMANHO_X; x++) {
                if (pixels [(int)x + (int)y * TAMANHO_X]){
                    
                    g.fillOval((int)x, (int)y,
                               10, 10);
                }
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        
        if(this.pixels == null){
            this.TAMANHO_X = this.getWidth();
            this.TAMANHO_Y = this.getHeight();
            this.pixels = new boolean[getWidth() * getHeight()];
            
            for (int i=0; i < pixels.length; i++){
                pixels[i] = false;
            }
        }
        g.setColor(new Color(0xFFFFFFFF));
        g.clearRect(0, 0, this.TAMANHO_X, this.TAMANHO_Y);
        paintGrid(g);        
    }
    
    @Override
    public BufferedImage getImagem() {
        int width = this.TAMANHO_X;
        int height = this.TAMANHO_Y;
        
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = image.createGraphics();
        this.paint(graphics);
        
        return image;
    }
    
    public void limpar(){
        for (int i=0; i < pixels.length; i++){
            pixels[i] = false;
        }
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
        //double x = getFieldWidth();
        //double y = getFieldHeight();

        //if (x != 0 && y != 0) {
            try {
                int x = e.getX() ;// TAMANHO_X;
                int y = e.getY() ;// TAMANHO_Y;
                pixels[x + y * TAMANHO_X] = true;
                repaint();
            } catch (ArrayIndexOutOfBoundsException ex) {
                //System.out.println(ex.getMessage());
            }
        //}
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}

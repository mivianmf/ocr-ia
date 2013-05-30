/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.awt.image.*;
import javax.swing.*;
import ocr.controllers.*;

/**
 *
 * @author Pawel
 */
public class DrawingPanel extends JPanel
        implements SelectImageObserver, MouseMotionListener, MouseListener, DrawingControlObserver, ImageGetter {

    public final static int IMAGE_RES_X = 20;
    public final static int IMAGE_RES_Y = 20;
    private int[] pixels = new int[IMAGE_RES_X * IMAGE_RES_Y];
    private Color color = Color.black;

    public DrawingPanel() {
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (Exception ex) {
        }

        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xFFFFFFFF;
        }

        addMouseMotionListener(this);
        addMouseListener(this);
    }

    private double getFieldWidth() {
        return (double) getWidth() / IMAGE_RES_X;
    }

    private double getFieldHeight() {
        return (double) getHeight() / IMAGE_RES_Y;
    }

    @Override
    public void clear() {
        for (int i = 0; i < pixels.length; i++) {
            pixels[i] = 0xFFFFFFFF; // biały
        }
        repaint();
    }

    @Override
    public void changeSelectedImage(Image img) {
        PixelGrabber pg = new PixelGrabber(img, 0, 0, IMAGE_RES_X, IMAGE_RES_Y, pixels, 0, IMAGE_RES_X);
        try {
            pg.grabPixels();
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex.getMessage());
        }
        repaint();
    }

    private void paintGrid(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        int intY = 0;
        for (double y = 0; intY < IMAGE_RES_Y; y += getFieldHeight()) {
            //System.out.println(y);
            int intX = 0;
            for (double x = 0; intX < IMAGE_RES_X; x += getFieldWidth()) {
                Rectangle2D rec = new Rectangle2D.Double(x, y, getFieldWidth(), getFieldHeight());

                g2.setColor(new Color(pixels[intX + intY * IMAGE_RES_X]));
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

    @Override
    public void setColor(Color color) {
        this.color = color;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        int x = (int) getFieldWidth();
        int y = (int) getFieldHeight();

        if (x != 0 && y != 0) {
            try {
                x = e.getX() / x;
                y = e.getY() / y;
                pixels[x + y * IMAGE_RES_X] = color.getRGB();
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

    @Override
    public void mouseClicked(MouseEvent e) {
        mouseDragged(e);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mouseDragged(e);
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
    public Image getImage() {
        MemoryImageSource memImg = new MemoryImageSource(IMAGE_RES_X, IMAGE_RES_Y, pixels.clone(), 0, IMAGE_RES_X);
        return createImage(memImg).getScaledInstance(10, 10, 1);
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr.gui;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import ocr.controllers.*;

/**
 *
 * @author Pawel
 */
public class ImagesPanel extends JPanel
        implements MouseListener, ImagesContainer {

    public final static int MAX_IMAGES_COUNT = 10;

    private Image selectedImage = null;

    private ArrayList<Image> goodImages = new ArrayList<>();
    private ArrayList<Image> badImages = new ArrayList<>();

    private ArrayList<SelectImageObserver> observers = new ArrayList<>();


    public Image getSelectedImage() {
        return selectedImage;
    }

    public ImagesPanel() {
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (Exception ex) {}
        setPreferredSize(new Dimension(100, 800));
        setLayout(new GridLayout(10, 2, 5, 5));
    }

    private void refreshImages() {
        removeAll();
        for (int i = 0; i < MAX_IMAGES_COUNT; i++) {
            if (i < goodImages.size()) {
                JLabel label = new JLabel(new ImageIcon(goodImages.get(i).getScaledInstance(40, 40, 1)));
                label.addMouseListener(this);
                label.setName("g" + Integer.toString(i));
                add(label);
            }
            else {
                add(new JLabel(""));
            }
            if (i < badImages.size()) {
                JLabel label = new JLabel(new ImageIcon(badImages.get(i).getScaledInstance(40, 40, 1)));
                label.setName("b" + Integer.toString(i));
                label.addMouseListener(this);
                add(label);
            }
            else {
                add(new JLabel(""));
            }
        }
        validate();
    }

    public void addGoodImage(Image img) {
        if (goodImages.size() < MAX_IMAGES_COUNT) {
            goodImages.add(img.getScaledInstance(10, 10, 1));
            refreshImages();
        }
    }

    public void addBadImage(Image img) {
        if (badImages.size() < MAX_IMAGES_COUNT) {
            badImages.add(img.getScaledInstance(10, 10, 1));
            refreshImages();
        }
    }

    public void removeGoodImage(int index) {
        goodImages.remove(index);
        refreshImages();
    }

    public void removeBadImage(int index) {
        badImages.remove(index);
        refreshImages();
    }

    public void clear() {
        goodImages.clear();
        badImages.clear();
        refreshImages();
    }

    public void mouseClicked(MouseEvent e) {
        if (e.getComponent() instanceof JLabel) {
            JLabel label = (JLabel)(e.getComponent());
            String labelName = label.getName();
            if (labelName.startsWith("g")) {
                selectedImage = goodImages.get(Integer.parseInt(labelName.substring(1)));
            }
            else {
                selectedImage = badImages.get(Integer.parseInt(labelName.substring(1)));
            }
            //System.out.println(selectedImage.getWidth(this));
            for (SelectImageObserver o : observers) {
                o.changeSelectedImage(selectedImage);
            }
        }
    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}

    public void addSelectImageObserver(SelectImageObserver observer) {
        observers.add(observer);
    }

    public void removeSelectImageObserver(SelectImageObserver observer) {
        observers.remove(observer);
    }

    public ArrayList<Image> getGoodImages() {
        return goodImages;
    }

    public ArrayList<Image> getBadImages() {
        return badImages;
    }

}

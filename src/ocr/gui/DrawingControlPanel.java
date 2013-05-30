/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ocr.gui;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import ocr.controllers.*;

/**
 *
 * @author Pawel
 */
public class DrawingControlPanel extends JPanel {

    private ArrayList<DrawingControlObserver> observers = new ArrayList<>();
    private JSlider colorSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, 0);
    private JButton addToGoodButton = new JButton("Add bitmap to the left group");
    private JButton addToBadButton = new JButton("Add bitmap to the right group");
    private JButton clearButton = new JButton("Clear");
    private ArrayList<ImagesContainer> containers = new ArrayList<>();
    private ImageGetter imageGetter = null;

    public void setImageGetter(ImageGetter imageGetter) {
        this.imageGetter = imageGetter;
    }

    public void addImagesContainer(ImagesContainer c) {
        containers.add(c);
    }

    public void removeImagesContainer(ImagesContainer c) {
        containers.remove(c);
    }

    public void addDrawingControlObserver(DrawingControlObserver observer) {
        observers.add(observer);
    }

    public void removeDrawingControlObserver(DrawingControlObserver observer) {
        observers.remove(observer);
    }

    public DrawingControlPanel() {
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (Exception ex) {
        }

        setLayout(new BorderLayout());

        colorSlider.setToolTipText("choose from grayscale");
        colorSlider.addChangeListener(new ColorSliderObserver());
        add(colorSlider, BorderLayout.NORTH);

        addToGoodButton.addActionListener(new AddToGoodButtonObserver());
        add(addToGoodButton, BorderLayout.WEST);

        addToBadButton.addActionListener(new AddToBadButtonObserver());
        add(addToBadButton, BorderLayout.EAST);

        clearButton.addActionListener(new ClearButtonObserver());
        add(clearButton, BorderLayout.CENTER);

    }

    class ColorSliderObserver implements ChangeListener {

        public void stateChanged(ChangeEvent e) {
            for (DrawingControlObserver o : observers) {
                int c = colorSlider.getValue();
                o.setColor(new Color(c, c, c));
            }
        }
    }

    class AddToGoodButtonObserver implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (imageGetter != null) {
                for (ImagesContainer c : containers) {
                    c.addGoodImage(imageGetter.getImage());
                }
            }
        }
    }

    class AddToBadButtonObserver implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (imageGetter != null) {
                for (ImagesContainer c : containers) {
                    c.addBadImage(imageGetter.getImage());
                }
            }
        }
    }

    class ClearButtonObserver implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (imageGetter != null) {
                for (DrawingControlObserver o : observers) {
                    o.clear();
                }
            }
        }
    }
}

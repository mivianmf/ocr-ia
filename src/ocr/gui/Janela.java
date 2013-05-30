package ocr.gui;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Toolkit;
import javax.swing.*;

/**
 *
 * @author Pawel
 */
public class Janela  extends JFrame {


    private ImagesPanel imagesPanel = new ImagesPanel();
    private DrawingPanel drawingPanel = new DrawingPanel();
    private DrawingControlPanel drawingControlPanel = new DrawingControlPanel();

    private MainMenu mainMenu = new MainMenu();

    private static Janela mainFrame = null;

    private JPanel esquerda, direita;
    
    private Janela() {
        super("Image Recognition");
        init();
    }

    public static Janela getMainFrame() {
        if (mainFrame == null) {
            mainFrame = new Janela();
        }
        return mainFrame;
    }

    private void init() {
        
        //this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (Exception ex) {}
        this.getContentPane().setLayout(null);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);

        mainMenu.setMainFrame(this);
        setJMenuBar(mainMenu);
   
        Rectangle maxBounds = GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
        this.setBounds(maxBounds);
        
        esquerda = new JPanel ();
        esquerda.setLocation (0,0);
        esquerda.setSize((int)maxBounds.width/2,
                         (int)maxBounds.height - 60);
        //esquerda.setBackground(Color.red);
        
        direita = new JPanel();
        direita.setLocation (((int)maxBounds.width/2), 0);
        direita.setSize((int)maxBounds.width/2 - 20,
                         (int)maxBounds.height - 60);
        //direita.setBackground(Color.blue);
        direita.setLayout(new BorderLayout( ));
        
        this.add(esquerda);
        this.add(direita);
        
        direita.add(imagesPanel, BorderLayout.EAST);

        direita.add(drawingPanel, BorderLayout.CENTER);

        drawingControlPanel.addDrawingControlObserver(drawingPanel);
        drawingControlPanel.addImagesContainer(imagesPanel);
        drawingControlPanel.setImageGetter(drawingPanel);
        direita.add(drawingControlPanel, BorderLayout.SOUTH);

        imagesPanel.addSelectImageObserver(drawingPanel);

        setVisible(true);

    }

    public void clearAll() {
        //netControlPanel.reset();
    }

    public void saveNet() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            //netControlPanel.saveNet(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }


    public void loadNet() {
        JFileChooser fileChooser = new JFileChooser();
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            //netControlPanel.loadNet(fileChooser.getSelectedFile().getAbsolutePath());
        }
    }
}

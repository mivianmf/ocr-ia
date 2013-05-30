package ocr.gui;

import com.sun.java.swing.plaf.windows.WindowsLookAndFeel;
import java.awt.BorderLayout;
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

        try {
            UIManager.setLookAndFeel(new WindowsLookAndFeel());
        } catch (Exception ex) {}
        getContentPane().setLayout(new BorderLayout());

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setSize(777, 666);

        mainMenu.setMainFrame(this);
        setJMenuBar(mainMenu);

         
        add(imagesPanel, BorderLayout.EAST);

        add(drawingPanel, BorderLayout.CENTER);

        drawingControlPanel.addDrawingControlObserver(drawingPanel);
        drawingControlPanel.addImagesContainer(imagesPanel);
        drawingControlPanel.setImageGetter(drawingPanel);
        add(drawingControlPanel, BorderLayout.SOUTH);

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

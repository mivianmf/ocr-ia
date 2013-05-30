package ocr.gui;


import java.awt.event.*;
import javax.swing.*;

/**
 *
 * @author Pawel
 */
public class MainMenu extends JMenuBar {

    private JMenu fileMenu = new JMenu();
    private JMenuItem saveNet = new JMenuItem();
    private JMenuItem clearAll = new JMenuItem();
    private JMenuItem loadNet = new JMenuItem();

    private Janela mainFrame = null;


    public void setMainFrame(Janela mainFrame) {
        this.mainFrame = mainFrame;
    }

    public MainMenu() {

        fileMenu.setText("File");

        clearAll.setText("Clear everything");
        clearAll.addActionListener(new ClearAllClickedObserver());
        fileMenu.add(clearAll);

        saveNet.setText("Save net");
        saveNet.addActionListener(new SaveNetClickedObserver());
        fileMenu.add(saveNet);

        loadNet.setText("Load net");
        loadNet.addActionListener(new LoadNetClickedObserver());
        fileMenu.add(loadNet);

        add(fileMenu);

    }

    class ClearAllClickedObserver implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (mainFrame != null) {
                mainFrame.clearAll();
            }
        }

    }


    class SaveNetClickedObserver implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (mainFrame != null) {
                mainFrame.saveNet();
            }
        }

    }

    class LoadNetClickedObserver implements ActionListener {

        public void actionPerformed(ActionEvent e) {
            if (mainFrame != null) {
                mainFrame.loadNet();
            }
        }

    }

}

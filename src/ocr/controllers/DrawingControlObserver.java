package ocr.controllers;


import java.awt.*;

/**
 *
 * @author Pawel
 */
public interface DrawingControlObserver {


    void setColor(Color color);

    void clear();

    void changeSelectedImage(Image img);


}

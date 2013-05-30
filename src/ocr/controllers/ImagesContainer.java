/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package ocr.controllers;


import java.awt.*;
import java.util.ArrayList;

/**
 *
 * @author Pawel
 */
public interface ImagesContainer {

    void addGoodImage(Image img);

    void addBadImage(Image img);

    void removeGoodImage(int index);

    void removeBadImage(int index);

    Image getSelectedImage();

    void clear();

    void addSelectImageObserver(SelectImageObserver observer);

    void removeSelectImageObserver(SelectImageObserver observer);

    ArrayList<Image> getGoodImages();

    ArrayList<Image> getBadImages();



}

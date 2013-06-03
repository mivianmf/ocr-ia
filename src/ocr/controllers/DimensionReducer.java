/*
 * NumberRecognition.java
 *
 * Created on Dec 7, 2011, 1:27:25 PM
 *
 * Description: Recognizes numbers.
 *
 * Copyright (C) Dec 7, 2011, Stephen L. Reed, Texai.org.
 *
 * This file is a translation from the OpenCV example http://www.shervinemami.info/numberRecognition.html, ported
 * to Java using the JavaCV library.  Notable changes are the addition of the Apache Log4J framework and the
 * installation of image files in a data directory child of the working directory. Some of the code has
 * been expanded to make debugging easier.  Expected results are 100% recognition of the lower3.txt test
 * image index set against the all10.txt training image index set.  See http://en.wikipedia.org/wiki/Eigennumber
 * for a technical explanation of the algorithm.
 *
 * stephenreed@yahoo.com
 *
 * NumberRecognition is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version (subject to the "Classpath" exception
 * as provided in the LICENSE.txt file that accompanied this code).
 *
 * NumberRecognition is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with JavaCV.  If not, see .
 *
 */
package ocr.controllers;

import com.googlecode.javacpp.FloatPointer;
import com.googlecode.javacpp.PointerPointer;
import static com.googlecode.javacv.cpp.opencv_core.*;
import com.googlecode.javacv.cpp.opencv_core.CvMat;
import com.googlecode.javacv.cpp.opencv_core.CvSize;
import com.googlecode.javacv.cpp.opencv_core.CvTermCriteria;
import com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_legacy.*;

/**
 * Recognizes numbers.
 *
 * @author reed
 */
public class DimensionReducer {

    /**
     * the number of training numbers
     */
    private int nTrainNumbers = 0;
    /**
     * the training number image array
     */
    IplImage[] trainingNumberImgArr;
    /**
     * the test number image array
     */
    //IplImage[] testNumberImgArr;
    /**
     * the number number array
     */
    CvMat numberNumTruthMat;
    /**
     * the number of numbers
     */
    int nNumbers;
    /**
     * the number of eigenvalues
     */
    int nEigens = 0;
    /**
     * eigenvectors
     */
    IplImage[] eigenVectArr;
    /**
     * eigenvalues
     */
    CvMat eigenValMat;
    /**
     * the average image
     */
    IplImage pAvgTrainImg;
    /**
     * the projected training numbers
     */
    public CvMat projectedTrainNumberMat;

    /**
     * Constructs a new NumberRecognition instance.
     */
    public DimensionReducer() {
    }

    /**
     * Trains from the data in the given training text index file, and store the
     * trained data into the file 'data/numberdata.xml'.
     *
     * @param trainingFileName the given training text index file
     */
    public void reduce(IplImage[] trainingNumberImgArr, int nNumbers) {
        int i;

        this.nNumbers = nNumbers;
        this.trainingNumberImgArr = trainingNumberImgArr;
        nTrainNumbers = this.trainingNumberImgArr.length;

        // do Principal Component Analysis on the training numbers
        doPCA();

        System.out.println("projecting the training numbers onto the PCA subspace");
        // project the training numbers onto the PCA subspace
        projectedTrainNumberMat = cvCreateMat(
                nTrainNumbers, // rows
                nEigens, // cols
                CV_32FC1); // type, 32-bit float, 1 channel

        // initialize the training number matrix - for ease of debugging
        for (int i1 = 0; i1 < nTrainNumbers; i1++) {
            for (int j1 = 0; j1 < nEigens; j1++) {
                projectedTrainNumberMat.put(i1, j1, 0.0);
            }
        }

        System.out.println("created projectedTrainNumberMat with " + nTrainNumbers + " (nTrainNumbers) rows and " + nEigens + " (nEigens) columns");
        if (nTrainNumbers < 5) {
            System.out.println("projectedTrainNumberMat contents:\n FAIL");// + oneChannelCvMatToString(projectedTrainNumberMat));
        }

        final FloatPointer floatPointer = new FloatPointer(nEigens);
        for (i = 0; i < nTrainNumbers; i++) {
            cvEigenDecomposite(
                    this.trainingNumberImgArr[i], // obj
                    nEigens, // nEigObjs
                    new PointerPointer(eigenVectArr), // eigInput (Pointer)
                    0, // ioFlags
                    null, // userData (Pointer)
                    pAvgTrainImg, // avg
                    floatPointer); // coeffs (FloatPointer)

            if (nTrainNumbers < 5) {
                System.out.println("floatPointer: " + floatPointer.toString());
            }
            for (int j1 = 0; j1 < nEigens; j1++) {
                projectedTrainNumberMat.put(i, j1, floatPointer.get(j1));
            }
        }
        if (nTrainNumbers < 5) {
            System.out.println("projectedTrainNumberMat after cvEigenDecomposite:\n" + projectedTrainNumberMat);
        }

        // store the recognition data as an xml file
        //storeTrainingData();

        // Save all the eigenvectors as numbers, so that they can be checked.
        //storeEigennumberImages();
    }

    /**
     * Does the Principal Component Analysis, finding the average image and the
     * eigennumbers that represent any image in the given dataset.
     */
    private void doPCA() {
        int i;
        CvTermCriteria calcLimit;
        CvSize numberImgSize = new CvSize();

        // set the number of eigenvalues to use
        nEigens = nTrainNumbers - 1;

        System.out.println("allocating numbers for principal component analysis, using " + nEigens + (nEigens == 1 ? " eigenvalue" : " eigenvalues"));

        // allocate the eigenvector numbers
        numberImgSize.width(trainingNumberImgArr[0].width());
        numberImgSize.height(trainingNumberImgArr[0].height());
        eigenVectArr = new IplImage[nEigens];
        for (i = 0; i < nEigens; i++) {
            eigenVectArr[i] = cvCreateImage(
                    numberImgSize, // size
                    IPL_DEPTH_32F, // depth
                    1); // channels
        }

        // allocate the eigenvalue array
        eigenValMat = cvCreateMat(
                1, // rows
                nEigens, // cols
                CV_32FC1); // type, 32-bit float, 1 channel

        // allocate the averaged image
        pAvgTrainImg = cvCreateImage(
                numberImgSize, // size
                IPL_DEPTH_32F, // depth
                1); // channels

        // set the PCA termination criterion
        calcLimit = cvTermCriteria(
                CV_TERMCRIT_ITER, // type
                nEigens, // max_iter
                1); // epsilon

        System.out.println("computing average image, eigenvalues and eigenvectors");
        // compute average image, eigenvalues, and eigenvectors
        cvCalcEigenObjects(
                nTrainNumbers, // nObjects
                new PointerPointer(trainingNumberImgArr), // input
                new PointerPointer(eigenVectArr), // output
                CV_EIGOBJ_NO_CALLBACK, // ioFlags
                0, // ioBufSize
                null, // userData
                calcLimit,
                pAvgTrainImg, // avg
                eigenValMat.data_fl()); // eigVals

        System.out.println("normalizing the eigenvectors");
        cvNormalize(
                eigenValMat, // src (CvArr)
                eigenValMat, // dst (CvArr)
                1, // a
                0, // b
                CV_L1, // norm_type
                null); // mask
    }

    /**
     * Recognizes the number in each of the test numbers given, and compares the
     * results with the truth.
     *
     * @param szFileTest the index file of test numbers
     */
    public float[] recognize(IplImage imagem) {
        float[] projectedTestNumber;
        // project the test image onto the PCA subspace
        projectedTestNumber = new float[nEigens];
        cvEigenDecomposite(
                imagem, // obj
                nEigens, // nEigObjs
                new PointerPointer(eigenVectArr), // eigInput (Pointer)
                0, // ioFlags
                null, // userData
                pAvgTrainImg, // avg
                projectedTestNumber);  // coeffs
        return projectedTestNumber;
    }
    /*public void recognizeFileList(final String szFileTest) {
     System.out.println("===========================================");
     System.out.println("recognizing numbers indexed from " + szFileTest);
     int i = 0;
     int nTestNumbers = 0;         // the number of test numbers
     CvMat trainNumberNumMat;  // the number numbers during training
     float[] projectedTestNumber;
     String answer;
     int nCorrect = 0;
     int nWrong = 0;
     double timeNumberRecognizeStart;
     double tallyNumberRecognizeTime;
     float confidence = 0.0f;

     // load test numbers and ground truth for number number
     testNumberImgArr = loadNumberImgArray(szFileTest);
     nTestNumbers = testNumberImgArr.length;

     System.out.println(nTestNumbers + " test numbers loaded");

     // load the saved training data
     trainNumberNumMat = loadTrainingData();
     if (trainNumberNumMat == null) {
     return;
     }

     // project the test numbers onto the PCA subspace
     projectedTestNumber = new float[nEigens];
     timeNumberRecognizeStart = (double) cvGetTickCount();        // Record the timing.

     for (i = 0; i < nTestNumbers; i++) {
     int iNearest;
     int nearest;
     int truth;

     // project the test image onto the PCA subspace
     cvEigenDecomposite(
     testNumberImgArr[i], // obj
     nEigens, // nEigObjs
     new PointerPointer(eigenVectArr), // eigInput (Pointer)
     0, // ioFlags
     null, // userData
     pAvgTrainImg, // avg
     projectedTestNumber);  // coeffs

     //System.out.println("projectedTestNumber\n" + floatArrayToString(projectedTestNumber));

     final FloatPointer pConfidence = new FloatPointer(confidence);
     iNearest = findNearestNeighbor(projectedTestNumber, new FloatPointer(pConfidence));
     confidence = pConfidence.get();
     truth = numberNumTruthMat.data_i().get(i);
     nearest = trainNumberNumMat.data_i().get(iNearest);

     if (nearest == truth) {
     answer = "Correct";
     nCorrect++;
     } else {
     answer = "WRONG!";
     nWrong++;
     }
     System.out.println("nearest = " + nearest + ", Truth = " + truth + " (" + answer + "). Confidence = " + confidence);
     }
     tallyNumberRecognizeTime = (double) cvGetTickCount() - timeNumberRecognizeStart;
     if (nCorrect + nWrong > 0) {
     System.out.println("TOTAL ACCURACY: " + (nCorrect * 100 / (nCorrect + nWrong)) + "% out of " + (nCorrect + nWrong) + " tests.");
     System.out.println("TOTAL TIME: " + (tallyNumberRecognizeTime / (cvGetTickFrequency() * 1000.0 * (nCorrect + nWrong))) + " ms average.");
     }
     }

     /**
     * Reads the names & image filenames of people from a text file, and loads
     * all those numbers listed.
     *
     * @param filename the training file name
     * @return the number image array
     */
    /*private IplImage[] loadNumberImgArray(final String filename) {
     IplImage[] numberImgArr;
     BufferedReader imgListFile;
     String imgFilename;
     int iNumber = 0;
     int nNumbers = 0;
     int i;
     try {
     // open the input file
     imgListFile = new BufferedReader(new FileReader(filename));

     // count the number of numbers
     while (true) {
     final String line = imgListFile.readLine();
     if (line == null || line.isEmpty()) {
     break;
     }
     nNumbers++;
     }
     System.out.println("nNumbers: " + nNumbers);
     imgListFile = new BufferedReader(new FileReader(filename));

     // allocate the number-image array and number number matrix
     numberImgArr = new IplImage[nNumbers];
     numberNumTruthMat = cvCreateMat(
     1, // rows
     nNumbers, // cols
     CV_32SC1); // type, 32-bit unsigned, one channel

     // initialize the number number matrix - for ease of debugging
     for (int j1 = 0; j1 < nNumbers; j1++) {
     numberNumTruthMat.put(0, j1, 0);
     }

     numberNames.clear();        // Make sure it starts as empty.
     nNumbers = 0;

     // store the number numbers in an array
     for (iNumber = 0; iNumber < nNumbers; iNumber++) {
     String numberName;
     String sNumberName;
     int numberNumber;

     // read number number (beginning with 1), their name and the image filename.
     final String line = imgListFile.readLine();
     if (line.isEmpty()) {
     break;
     }
     final String[] tokens = line.split(" ");
     numberNumber = Integer.parseInt(tokens[0]);
     numberName = tokens[1];
     imgFilename = tokens[2];
     sNumberName = numberName;
     System.out.println("Got " + iNumber + " " + numberNumber + " " + numberName + " " + imgFilename);

     // Check if a new number is being loaded.
     if (numberNumber > nNumbers) {
     // Allocate memory for the extra number (or possibly multiple), using this new number's name.
     numberNames.add(sNumberName);
     nNumbers = numberNumber;
     System.out.println("Got new number " + sNumberName + " -> nNumbers = " + nNumbers + " [" + numberNames.size() + "]");
     }

     // Keep the data
     numberNumTruthMat.put(
     0, // i
     iNumber, // j
     numberNumber); // v

     // load the number image
     numberImgArr[iNumber] = cvLoadImage(
     imgFilename, // filename
     CV_LOAD_IMAGE_GRAYSCALE); // isColor

     if (numberImgArr[iNumber] == null) {
     throw new RuntimeException("Can't load image from " + imgFilename);
     }
     }

     imgListFile.close();

     } catch (IOException ex) {
     throw new RuntimeException(ex);
     }

     System.out.println("Data loaded from '" + filename + "': (" + nNumbers + " numbers of " + nNumbers + " people).");
     final StringBuilder stringBuilder = new StringBuilder();
     stringBuilder.append("People: ");
     if (nNumbers > 0) {
     stringBuilder.append("<").append(numberNames.get(0)).append(">");
     }
     for (i = 1; i < nNumbers && i < numberNames.size(); i++) {
     stringBuilder.append(", <").append(numberNames.get(i)).append(">");
     }
     System.out.println(stringBuilder.toString());

     return numberImgArr;
     }

    
     /**
     * Stores the training data to the file 'data/numberdata.xml'.
     */
    /*private void storeTrainingData() {
     CvFileStorage fileStorage;
     int i;

     System.out.println("writing data/numberdata.xml");

     // create a file-storage internumber
     fileStorage = cvOpenFileStorage(
     "data/numberdata.xml", // filename
     null, // memstorage
     CV_STORAGE_WRITE, // flags
     null); // encoding

     // Store the number names. Added by Shervin.
     cvWriteInt(
     fileStorage, // fs
     "nNumbers", // name
     nNumbers); // value

     for (i = 0; i < nNumbers; i++) {
     String varname = "numberName_" + (i + 1);
     cvWriteString(
     fileStorage, // fs
     varname, // name
     numberNames.get(i), // string
     0); // quote
     }

     // store all the data
     cvWriteInt(
     fileStorage, // fs
     "nEigens", // name
     nEigens); // value

     cvWriteInt(
     fileStorage, // fs
     "nTrainNumbers", // name
     nTrainNumbers); // value

     cvWrite(
     fileStorage, // fs
     "trainNumberNumMat", // name
     numberNumTruthMat, // value
     cvAttrList()); // attributes

     cvWrite(
     fileStorage, // fs
     "eigenValMat", // name
     eigenValMat, // value
     cvAttrList()); // attributes

     cvWrite(
     fileStorage, // fs
     "projectedTrainNumberMat", // name
     projectedTrainNumberMat,
     cvAttrList()); // value

     cvWrite(fileStorage, // fs
     "avgTrainImg", // name
     pAvgTrainImg, // value
     cvAttrList()); // attributes

     for (i = 0; i < nEigens; i++) {
     String varname = "eigenVect_" + i;
     cvWrite(
     fileStorage, // fs
     varname, // name
     eigenVectArr[i], // value
     cvAttrList()); // attributes
     }

     // release the file-storage internumber
     cvReleaseFileStorage(fileStorage);
     }

     /**
     * Opens the training data from the file 'data/numberdata.xml'.
     *
     * @param pTrainNumberNumMat
     * @return the number numbers during training, or null if not successful
     */
    /* private CvMat loadTrainingData() {
     System.out.println("loading training data");
     CvMat pTrainNumberNumMat = null; // the number numbers during training
     CvFileStorage fileStorage;
     int i;

     // create a file-storage internumber
     fileStorage = cvOpenFileStorage(
     "data/numberdata.xml", // filename
     null, // memstorage
     CV_STORAGE_READ, // flags
     null); // encoding
     if (fileStorage == null) {
     System.out.println("Can't open training database file 'data/numberdata.xml'.");
     return null;
     }

     // Load the number names.
     numberNames.clear();        // Make sure it starts as empty.
     nNumbers = cvReadIntByName(
     fileStorage, // fs
     null, // map
     "nNumbers", // name
     0); // default_value
     if (nNumbers == 0) {
     System.out.println("No people found in the training database 'data/numberdata.xml'.");
     return null;
     } else {
     System.out.println(nNumbers + " numbers read from the training database");
     }

     // Load each number's name.
     for (i = 0; i < nNumbers; i++) {
     String sNumberName;
     String varname = "numberName_" + (i + 1);
     sNumberName = cvReadStringByName(
     fileStorage, // fs
     null, // map
     varname,
     "");
     numberNames.add(sNumberName);
     }
     System.out.println("number names: " + numberNames);

     // Load the data
     nEigens = cvReadIntByName(
     fileStorage, // fs
     null, // map
     "nEigens",
     0); // default_value
     nTrainNumbers = cvReadIntByName(
     fileStorage,
     null, // map
     "nTrainNumbers",
     0); // default_value
     Pointer pointer = cvReadByName(
     fileStorage, // fs
     null, // map
     "trainNumberNumMat", // name
     cvAttrList()); // attributes
     pTrainNumberNumMat = new CvMat(pointer);

     pointer = cvReadByName(
     fileStorage, // fs
     null, // map
     "eigenValMat", // nmae
     cvAttrList()); // attributes
     eigenValMat = new CvMat(pointer);

     pointer = cvReadByName(
     fileStorage, // fs
     null, // map
     "projectedTrainNumberMat", // name
     cvAttrList()); // attributes
     projectedTrainNumberMat = new CvMat(pointer);

     pointer = cvReadByName(
     fileStorage,
     null, // map
     "avgTrainImg",
     cvAttrList()); // attributes
     pAvgTrainImg = new IplImage(pointer);

     eigenVectArr = new IplImage[nTrainNumbers];
     for (i = 0; i < nEigens; i++) {
     String varname = "eigenVect_" + i;
     pointer = cvReadByName(
     fileStorage,
     null, // map
     varname,
     cvAttrList()); // attributes
     eigenVectArr[i] = new IplImage(pointer);
     }

     // release the file-storage internumber
     cvReleaseFileStorage(fileStorage);

     System.out.println("Training data loaded (" + nTrainNumbers + " training numbers of " + nNumbers + " people)");
     final StringBuilder stringBuilder = new StringBuilder();
     stringBuilder.append("People: ");
     if (nNumbers > 0) {
     stringBuilder.append("<").append(numberNames.get(0)).append(">");
     }
     for (i = 1; i < nNumbers; i++) {
     stringBuilder.append(", <").append(numberNames.get(i)).append(">");
     }
     System.out.println(stringBuilder.toString());

     return pTrainNumberNumMat;
     }

     /**
     * Saves all the eigenvectors as numbers, so that they can be checked.
     */
    /*private void storeEigennumberImages() {
     // Store the average image to a file
     System.out.println("Saving the image of the average number as 'data/out_averageImage.bmp'");
     cvSaveImage("data/out_averageImage.bmp", pAvgTrainImg);

     // Create a large image made of many eigennumber numbers.
     // Must also convert each eigennumber image to a normal 8-bit UCHAR image instead of a 32-bit float image.
     System.out.println("Saving the " + nEigens + " eigenvector numbers as 'data/out_eigennumbers.bmp'");

     if (nEigens > 0) {
     // Put all the eigennumbers next to each other.
     int COLUMNS = 8;        // Put upto 8 numbers on a row.
     int nCols = Math.min(nEigens, COLUMNS);
     int nRows = 1 + (nEigens / COLUMNS);        // Put the rest on new rows.
     int w = eigenVectArr[0].width();
     int h = eigenVectArr[0].height();
     CvSize size = cvSize(nCols * w, nRows * h);
     final IplImage bigImg = cvCreateImage(
     size,
     IPL_DEPTH_8U, // depth, 8-bit Greyscale UCHAR image
     1);        // channels
     for (int i = 0; i < nEigens; i++) {
     // Get the eigennumber image.
     IplImage byteImg = convertFloatImageToUcharImage(eigenVectArr[i]);
     // Paste it into the correct position.
     int x = w * (i % COLUMNS);
     int y = h * (i / COLUMNS);
     CvRect ROI = cvRect(x, y, w, h);
     cvSetImageROI(
     bigImg, // image
     ROI); // rect
     cvCopy(
     byteImg, // src
     bigImg, // dst
     null); // mask
     cvResetImageROI(bigImg);
     cvReleaseImage(byteImg);
     }
     cvSaveImage(
     "data/out_eigennumbers.bmp", // filename
     bigImg); // image
     cvReleaseImage(bigImg);
     }
     }

     /**
     * Converts the given float image to an unsigned character image.
     *
     * @param srcImg the given float image
     * @return the unsigned character image
     */
    /*private IplImage convertFloatImageToUcharImage(IplImage srcImg) {
     IplImage dstImg;
     if ((srcImg != null) && (srcImg.width() > 0 && srcImg.height() > 0)) {
     // Spread the 32bit floating point pixels to fit within 8bit pixel range.
     CvPoint minloc = new CvPoint();
     CvPoint maxloc = new CvPoint();
     double[] minVal = new double[1];
     double[] maxVal = new double[1];
     cvMinMaxLoc(srcImg, minVal, maxVal, minloc, maxloc, null);
     // Deal with NaN and extreme values, since the DFT seems to give some NaN results.
     if (minVal[0] < -1e30) {
     minVal[0] = -1e30;
     }
     if (maxVal[0] > 1e30) {
     maxVal[0] = 1e30;
     }
     if (maxVal[0] - minVal[0] == 0.0f) {
     maxVal[0] = minVal[0] + 0.001;  // remove potential divide by zero errors.
     }                        // Convert the format
     dstImg = cvCreateImage(cvSize(srcImg.width(), srcImg.height()), 8, 1);
     cvConvertScale(srcImg, dstImg, 255.0 / (maxVal[0] - minVal[0]), -minVal[0] * 255.0 / (maxVal[0] - minVal[0]));
     return dstImg;
     }
     return null;
     }

     /**
     * Find the most likely number based on a detection. Returns the index, and
     * stores the confidence value into pConfidence.
     *
     * @param projectedTestNumber the projected test number
     * @param pConfidencePointer a pointer containing the confidence value
     * @param iTestNumber the test number index
     * @return the index
     */
    /* private int findNearestNeighbor(float projectedTestNumber[], FloatPointer pConfidencePointer) {
     double leastDistSq = Double.MAX_VALUE;
     int i = 0;
     int iTrain = 0;
     int iNearest = 0;

     System.out.println("................");
     System.out.println("find nearest neighbor from " + nTrainNumbers + " training numbers");
     for (iTrain = 0; iTrain < nTrainNumbers; iTrain++) {
     //System.out.println("considering training number " + (iTrain + 1));
     double distSq = 0;

     for (i = 0; i < nEigens; i++) {
     //LOGGER.debug("  projected test number distance from eigennumber " + (i + 1) + " is " + projectedTestNumber[i]);

     float projectedTrainNumberDistance = (float) projectedTrainNumberMat.get(iTrain, i);
     float d_i = projectedTestNumber[i] - projectedTrainNumberDistance;
     distSq += d_i * d_i; // / eigenValMat.data_fl().get(i);  // Mahalanobis distance (might give better results than Eucalidean distance)
     //          if (iTrain < 5) {
     //            System.out.println("    ** projected training number " + (iTrain + 1) + " distance from eigennumber " + (i + 1) + " is " + projectedTrainNumberDistance);
     //            System.out.println("    distance between them " + d_i);
     //            System.out.println("    distance squared " + distSq);
     //          }
     }

     if (distSq < leastDistSq) {
     leastDistSq = distSq;
     iNearest = iTrain;
     System.out.println("  training number " + (iTrain + 1) + " is the new best match, least squared distance: " + leastDistSq);
     }
     }

     // Return the confidence level based on the Euclidean distance,
     // so that similar numbers should give a confidence between 0.5 to 1.0,
     // and very different numbers should give a confidence between 0.0 to 0.5.
     float pConfidence = (float) (1.0f - Math.sqrt(leastDistSq / (float) (nTrainNumbers * nEigens)) / 255.0f);
     pConfidencePointer.put(pConfidence);

     System.out.println("training number " + (iNearest + 1) + " is the final best match, confidence " + pConfidence);
     return iNearest;
     }

     /**
     * Returns a string representation of the given float array.
     *
     * @param floatArray the given float array
     * @return a string representation of the given float array
     */
    /* private String floatArrayToString(final float[] floatArray) {
     final StringBuilder stringBuilder = new StringBuilder();
     boolean isFirst = true;
     stringBuilder.append('[');
     for (int i = 0; i < floatArray.length; i++) {
     if (isFirst) {
     isFirst = false;
     } else {
     stringBuilder.append(", ");
     }
     stringBuilder.append(floatArray[i]);
     }
     stringBuilder.append(']');

     return stringBuilder.toString();
     }

     /**
     * Returns a string representation of the given float pointer.
     *
     * @param floatPointer the given float pointer
     * @return a string representation of the given float pointer
     */
    /*private String floatPointerToString(final FloatPointer floatPointer) {
     final StringBuilder stringBuilder = new StringBuilder();
     boolean isFirst = true;
     stringBuilder.append('[');
     for (int i = 0; i < floatPointer.capacity(); i++) {
     if (isFirst) {
     isFirst = false;
     } else {
     stringBuilder.append(", ");
     }
     stringBuilder.append(floatPointer.get(i));
     }
     stringBuilder.append(']');

     return stringBuilder.toString();
     }

     /**
     * Returns a string representation of the given one-channel CvMat object.
     *
     * @param cvMat the given CvMat object
     * @return a string representation of the given CvMat object
     */
    /*public String oneChannelCvMatToString(final CvMat cvMat) {
     //Preconditions
     if (cvMat.channels() != 1) {
     throw new RuntimeException("illegal argument - CvMat must have one channel");
     }

     final int type = cvMat.maskedType();
     StringBuilder s = new StringBuilder("[ ");
     for (int i = 0; i < cvMat.rows(); i++) {
     for (int j = 0; j < cvMat.cols(); j++) {
     if (type == CV_32FC1 || type == CV_32SC1) {
     s.append(cvMat.get(i, j));
     } else {
     throw new RuntimeException("illegal argument - CvMat must have one channel and type of float or signed integer");
     }
     if (j < cvMat.cols() - 1) {
     s.append(", ");
     }
     }
     if (i < cvMat.rows() - 1) {
     s.append("\n  ");
     }
     }
     s.append(" ]");
     return s.toString();
     }

     /**
     * Executes this application.
     *
     * @param args the command line arguments
     */
    /*public static void main(final String[] args) {

     final NumberRecognition numberRecognition = new NumberRecognition();
     //numberRecognition.learn("data/some-training-numbers.txt");
     numberRecognition.learn("data/all10.txt");
     //numberRecognition.recognizeFileList("data/some-test-numbers.txt");
     numberRecognition.recognizeFileList("data/lower3.txt");
     }*/
}
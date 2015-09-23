import java.util.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to determine which regions are words.
 * <p>
 * Dilates the given image of the characters of the chart.
 * Labels and finds the borders of the dilated image
 * Gets the bounding boxes of the dilated image's regions
 * Compares the bounding boxes with the original image of the characters
 * Checks each bounding box and find which character regions are in
 * that bounding box.
 * Character regions that occupy the same bounding box form a word.
 * Each bounding box forms a word.
 * Post processing: put words that have touching or 1 pixel apart
 * bounding boxes into one word.
 * <p>
 * Which words form the title, x-axis title, y-axis title, legend, x-axis labels,
 * and y-axis labels?
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class WordFinder {

  private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
	private int bPixValue;      //background label
	private int bBoxColor; 			//the color of the bounding box in a final image
	private int noOfWords; 

	private int[][] inputImage;  	//input image
	private int[][] inputLabelImage;  		//labelled input image
	private int[][] wordImage;  	//word image
	private int[][] bBoxImage;  	//bounding box image
	private Hashtable words;			//all the words

	private PointPixel[] boundingBoxes; //bounding box corners for all words

	/**
	 * Constructor.
	 *
	 * @param inImage The image of the regions which are found to be characters
	 * @param imageRows The number of rows
	 * @param imageColumns The number of columns
	 * @param bPix The label of the background pixels
	 */
	public WordFinder(int[][] inImage, int imageRows, int imageColumns, int bPix) {
		inputLabelImage = inImage;
		inputImage = new int[imageRows][imageColumns];
		wordImage = new int[imageRows][imageColumns];
		bBoxImage = new int[imageRows][imageColumns];
		for (int i = 0; i < imageRows; i++) {
			for (int j = 0; j < imageColumns; j++) {
				//inputImage[i][j] = bPix;
				inputImage[i][j] = 255;
				if (inImage[i][j] != bPix) {
					inputImage[i][j] = 0;
				}
				wordImage[i][j] = inputImage[i][j];
				bBoxImage[i][j] = 0;
			}
		}
		imageHeight = imageRows;
		imageWidth = imageColumns;
		//bPixValue = bPix; 
                bPixValue = 255;
		//System.out.println("Background is "+bPixValue);
		bBoxColor = 200;
		words = new Hashtable();
	}

	/**
	 * Finds which regions make up words.
	 * The image is first dilated. The dilated image is labelled and the 
	 * bounding box of each region is found. A word is created for each
	 * region. The number of characters in a region is found. The angle of
	 * the axis of the word is found. The words are combined, if possible.
	 *
	 * @param none
	 */
	public void findWords(Region[] allRegions) {
		Dilator aDilator = new Dilator();
		int[][] dilatedImage = aDilator.dilate(inputImage, imageHeight, imageWidth, bPixValue);
//just for tight chart like Amex.PGM; didn't work
		//int[][] dilatedImage = aDilator.dilate2(inputImage, imageHeight, imageWidth, bPixValue);
			
		//Further dilation: Did not work well.
		//dilatedImage = aDilator.dilate(dImage, imageHeight, imageWidth, bPixValue);
// DLC here is where image was dilated a second time
// not used for Amex example
		//dilatedImage = aDilator.dilate2(dilatedImage, imageHeight, imageWidth, bPixValue);
// end DLC
		//dilate();

		//Label -find the regions of- the dilated image
		LabelImage alabeler = new LabelImage(dilatedImage, imageHeight, imageWidth, bPixValue);	
		Vector labelledImageInfo = alabeler.applyLabelling();
		//The number of regions = number of words
		noOfWords = ((Integer)labelledImageInfo.get(0)).intValue();
		int[][] labelImage = (int [][])labelledImageInfo.get(2);
//System.out.println("There are "+noOfWords+" words.");

		//Find the bounding boxes of all the regions in the labelled image of
		//the dilated image.
		BoundingBoxFinder boxFinder = new BoundingBoxFinder(labelImage, imageHeight, imageWidth, 0, noOfWords);
		boxFinder.findBoundingBoxes();
		boundingBoxes = boxFinder.getBoundingBoxes();

		//Create a word for each region in the labelled image of the dilated image.
		PointPixel upperLeft, lowerRight;
                //DLC
                PointPixel newUpperLeft, newLowerRight;
                //DLC
		Word aWord;
		for (int i = 2; i < noOfWords*2; i+=2) {
			upperLeft = boundingBoxes[i];
			lowerRight = boundingBoxes[i+1];
			//System.out.println("\n"+i+"th word corners: "+upperLeft+" and "+lowerRight);
//DLC  compensate for dilation(s) [offsets for dilate() method]
                        newUpperLeft = new PointPixel(upperLeft.getRow() + 1,upperLeft.getColumn() + 1);
                        newLowerRight = new PointPixel(lowerRight.getRow() - 1,lowerRight.getColumn() - 1);
			//aWord = new Word(upperLeft, lowerRight, i);
			aWord = new Word(newUpperLeft, newLowerRight, i);
//end DLC
			//The word object knows only about its own area:
			setArea(aWord);
			aWord.findCharacterCount();
			double angle = aWord.calculateAngle();
			//System.out.println("Axis is from "+aWord.getAxisBeginPoint()+" to "+aWord.getAxisEndPoint());

                        aWord.readWord(allRegions,inputLabelImage);
			//System.out.println(aWord.getText());

			//Save the new word in the hashtable "words"
			words.put(new Integer(i), aWord);
		}
		//System.out.println("\nThere are "+words.size()+" words before combining\n");

		//drawBoxes();
		     //drawWords();
		combineWords();
		     drawWords();
		//System.out.println("\nThere are "+words.size()+" words after combining\n");

		//drawWords();
		drawAxes();
		drawBoxes();
	}

	/**
	 * Saves the region that the word occupies in the word object.
	 *
	 * @param aWord
	 */
	private void setArea(Word aWord) {
		PointPixel upperLeft = aWord.getUpperLeft();
		PointPixel lowerRight = aWord.getLowerRight();
		int h = lowerRight.getRow()-upperLeft.getRow()+1;
		int w = lowerRight.getColumn()-upperLeft.getColumn()+1;
		int[][] wordArea = new int[h][w];
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
				wordArea[i-upperLeft.getRow()][j-upperLeft.getColumn()] = inputLabelImage[i][j];	
			}
		}
		aWord.setArea(h, w, wordArea, upperLeft.getRow(), upperLeft.getColumn(), 0);
	}


	/**
	 * Combines the previously found words.
	 * For each bounding box, checks if there is another one touching it or 1 pixel
	 * away from it.
	 *
	 * @param none
	 */
	private void combineWords() {
		PointPixel upperLeft, lowerRight;
		Word aWord;
		int wordNo, wordNo2, row, column, refSize;
		for (int i = 2; i < noOfWords*2; i+=2) {
			aWord = (Word)words.get(new Integer(i));
			if (aWord == null) {
				continue;
		}
			upperLeft = aWord.getUpperLeft();
			lowerRight = aWord.getLowerRight();
			wordNo = i;
			System.out.println(wordNo+"th word corners: "+upperLeft+" and "+lowerRight);
System.out.println("word is " + aWord.getText());
			//Check the upper and lower sides 
                        ;refSize = (lowerRight.getColumn() - upperLeft.getColumn())/4;
// for Burns but no change
          refSize = (int)(aWord.getFontSize()/2.5) + 3 ;
System.out.println("refSize = "+refSize);
          //refSize = (int)(aWord.getFontSize()/2.5) + 1 ;
                        if (refSize < 2) refSize = 2;
			for (int j = upperLeft.getColumn()-refSize; j <= lowerRight.getColumn()+refSize; j++) {
				//Check the upper side
				for (row = upperLeft.getRow()- refSize; row <= upperLeft.getRow(); row++) { 
					if (row >= 0 && row < imageHeight && j >=0 && j < imageWidth) {
						wordNo2 = bBoxImage[row][j];
						if (wordNo2 != wordNo && wordNo2 != 0) {
							//System.out.println("Will try to merge words "+wordNo+" and "+wordNo2);
							mergeWords(wordNo, wordNo2);
						}
					}
				}
				//Check the lower side 
				for (row = lowerRight.getRow(); row <= lowerRight.getRow()+refSize; row++) { 
					if (row >= 0 && row < imageHeight && j >=0 && j < imageWidth) {
						wordNo2 = bBoxImage[row][j];
						if (wordNo2 != wordNo && wordNo2 != 0) {
							//System.out.println("Will try to merge words "+wordNo+" and "+wordNo2);
							mergeWords(wordNo, wordNo2);
						}
					}
				}
			}
			//Check the left and right sides 
                        ;refSize = (lowerRight.getRow() - upperLeft.getRow())/4;
                        if (refSize < 2) refSize = 2;
			for (int j = upperLeft.getRow(); j <= lowerRight.getRow(); j++) {
				//Check the right side 
				for (column = upperLeft.getColumn()- refSize; column <= upperLeft.getColumn(); column++) {
					if (column >= 0 && column < imageWidth && j >=0 && j < imageHeight) {
						wordNo2 = bBoxImage[j][column];
						if (wordNo2 != wordNo && wordNo2 != 0) {
							//System.out.println("Will try to merge words "+wordNo+" and "+wordNo2);
							mergeWords(wordNo, wordNo2);
						}
					}
				}
				//Check the left side 
				for (column = lowerRight.getColumn(); column <= lowerRight.getColumn()+refSize; column++) {
					if (column >= 0 && column < imageWidth && j >=0 && j < imageHeight) {
						wordNo2 = bBoxImage[j][column];
						if (wordNo2 != wordNo && wordNo2 != 0) {
							System.out.println("Will try to merge words "+wordNo+" and "+wordNo2);
							mergeWords(wordNo, wordNo2);
						}
					}
				}
			}
		}
	}

	/**
	 * Checks the two given words, and if possible, merges them into one word.
	 *
	 * @param wordNo1 The first word
	 * @param wordNo2 The second word
	 */
	private void mergeWords(int wordNo1, int wordNo2) {
		double angle;
		PointPixel upperLeft, lowerRight, upperLeft2, lowerRight2;
		PointPixel newUpperLeft, newlowerRight;
		Word word1 = (Word)words.get(new Integer(wordNo1));
		Word word2 = (Word)words.get(new Integer(wordNo2));
		if (word1 == null || word2 == null) {
			//System.out.println("One of the words "+wordNo1+" and "+wordNo2+" is null");
			return;
		}
System.out.println("trying to merge "+word1.getText()+" with "+word2.getText());
		//Get the bounding box corners for both words:
		upperLeft = word1.getUpperLeft();
		lowerRight = word1.getLowerRight();
		upperLeft2 = word2.getUpperLeft();
		lowerRight2 = word2.getLowerRight();
		if (upperLeft.equals(upperLeft2) && lowerRight.equals(lowerRight2)) {
			//Already the same bounding box
			//System.out.println("Words "+wordNo1+" and "+wordNo2+" already have the same bounding box");
			return;
		}
		if (Math.abs(word1.getAngle() - word2.getAngle()) > 4) {
			//Not the same orientation
			//System.out.println("Words "+wordNo1+" and "+wordNo2+" have different angles: "+word1.getAngle()+", "+word2.getAngle());
			return;
		}
		LineFitter aFitter = new LineFitter();
		if (Math.abs(word2.getAngle()- word1.getAngle()) > 10) {
			//They do not extend each other
			//System.out.println("Words "+wordNo1+" and "+wordNo2+" do not extend each other: "+word1.getAngle()+", "+word2.getAngle());
			return;
		}

		//Use the axes information
		PointPixel center1 = new PointPixel((word1.getAxisBeginPoint().getRow() + word1.getAxisEndPoint().getRow())/2, (word1.getAxisBeginPoint().getColumn() + word1.getAxisEndPoint().getColumn())/2);
		PointPixel center2 = new PointPixel((word2.getAxisBeginPoint().getRow() + word2.getAxisEndPoint().getRow())/2, (word2.getAxisBeginPoint().getColumn() + word2.getAxisEndPoint().getColumn())/2);
		angle = aFitter.getAngle(center1, center2);
		if (Math.abs(angle - word1.getAngle()) > 10 || Math.abs(angle - word2.getAngle()) > 10) {
			//They do not extend each other
			//System.out.println("Words "+wordNo1+" and "+wordNo2+" do not extend each other: "+word1.getAngle()+", "+word2.getAngle()+", angle between their axes is "+angle);
			return;
		}
		//Passed all the tests at this point, so merge the words.
		//Update the bounding box corners
		//System.out.println("Merging words "+wordNo1+" and "+wordNo2);
		if (upperLeft.getRow() <= upperLeft2.getRow() && upperLeft.getColumn() <= upperLeft2.getColumn()) {
			newUpperLeft = new PointPixel(upperLeft.getRow(), upperLeft.getColumn());
			word1.setUpperLeft(newUpperLeft);
			word2.setUpperLeft(newUpperLeft);
		}
		else if (upperLeft.getRow() <= upperLeft2.getRow() && upperLeft.getColumn() > upperLeft2.getColumn()) {
			newUpperLeft = new PointPixel(upperLeft.getRow(), upperLeft2.getColumn());
			word1.setUpperLeft(newUpperLeft);
			word2.setUpperLeft(newUpperLeft);
		}
		else if (upperLeft2.getRow() <= upperLeft.getRow() && upperLeft2.getColumn() <= upperLeft.getColumn()) {
			newUpperLeft = new PointPixel(upperLeft2.getRow(), upperLeft2.getColumn());
			word1.setUpperLeft(newUpperLeft);
			word2.setUpperLeft(newUpperLeft);
		}
		else if (upperLeft2.getRow() <= upperLeft.getRow() && upperLeft2.getColumn() > upperLeft.getColumn()) {
			newUpperLeft = new PointPixel(upperLeft2.getRow(), upperLeft.getColumn());
			word1.setUpperLeft(newUpperLeft);
			word2.setUpperLeft(newUpperLeft);
		}
		if (lowerRight.getRow() >= lowerRight2.getRow() && lowerRight.getColumn() <= lowerRight2.getColumn()) {
			newlowerRight = new PointPixel(lowerRight.getRow(), lowerRight2.getColumn());
			word1.setLowerRight(newlowerRight);
			word2.setLowerRight(newlowerRight);
		}
		else if (lowerRight.getRow() >= lowerRight2.getRow() && lowerRight.getColumn() > lowerRight2.getColumn()) {
			newlowerRight = new PointPixel(lowerRight.getRow(), lowerRight.getColumn());
			word1.setLowerRight(newlowerRight);
			word2.setLowerRight(newlowerRight);
		}
		else if (lowerRight.getRow() < lowerRight2.getRow() && lowerRight.getColumn() <= lowerRight2.getColumn()) {
			newlowerRight = new PointPixel(lowerRight2.getRow(), lowerRight2.getColumn());
			word1.setLowerRight(newlowerRight);
			word2.setLowerRight(newlowerRight);
		}
		else if (lowerRight.getRow() < lowerRight2.getRow() && lowerRight.getColumn() > lowerRight2.getColumn()) {
			newlowerRight = new PointPixel(lowerRight2.getRow(), lowerRight.getColumn());
			word1.setLowerRight(newlowerRight);
			word2.setLowerRight(newlowerRight);
		}
		//Remove one of the words, keep the other one -keep word1:
		words.remove(new Integer(wordNo2));
		//Set the properties of word1 -the merged word- again:
		setArea(word1);
		word1.findCharacterCount();
		angle = word1.calculateAngle();
		//System.out.println("Merged word: Axis is from "+word1.getAxisBeginPoint()+" to "+word1.getAxisEndPoint());
	}

 	/**
	 * Draws on the bBoxImage (the bounding boxes for each word).
	 * The color of the bounding box of each word is the word's key in the 
	 * hashtable "words". 
	 *
	 * @param none
	 */
	private void drawBoxes() {
		int i;
		PointPixel upperLeft;
		PointPixel lowerRight;
		Word aWord;
		Integer aKey;
		LineFitter aFitter = new LineFitter();
		Enumeration enumWords = words.keys();
		while (enumWords.hasMoreElements()) {
			aKey = (Integer)enumWords.nextElement();
			aWord = (Word)words.get(aKey);
			upperLeft = aWord.getUpperLeft();
			lowerRight = aWord.getLowerRight();
			i = aKey.intValue();
			//Draw the four sides of the bounding box:
			aFitter.drawLine(upperLeft.getRow(), upperLeft.getColumn(), upperLeft.getRow(), lowerRight.getColumn(), bBoxImage, i);
			aFitter.drawLine(upperLeft.getRow(), lowerRight.getColumn(), lowerRight.getRow(), lowerRight.getColumn(), bBoxImage, i);
			aFitter.drawLine(lowerRight.getRow(), upperLeft.getColumn(), lowerRight.getRow(), lowerRight.getColumn(), bBoxImage, i);
			aFitter.drawLine(upperLeft.getRow(), upperLeft.getColumn(), lowerRight.getRow(), upperLeft.getColumn(), bBoxImage, i);
		}
	}

 	/**
	 * Draws the bounding boxes of each word on the wordImage. 
	 * 
	 * @param none
	 */
	private void drawWords() {
		PointPixel upperLeft;
		PointPixel lowerRight;
		Word aWord;
		LineFitter aFitter = new LineFitter();
		Enumeration enumWords = words.elements();
		while (enumWords.hasMoreElements()) {
			aWord = (Word)enumWords.nextElement();
			upperLeft = aWord.getUpperLeft();
			lowerRight = aWord.getLowerRight();
			aFitter.drawLine(upperLeft.getRow(), upperLeft.getColumn(), upperLeft.getRow(), lowerRight.getColumn(), wordImage, bBoxColor);
			aFitter.drawLine(upperLeft.getRow(), lowerRight.getColumn(), lowerRight.getRow(), lowerRight.getColumn(), wordImage, bBoxColor);
			aFitter.drawLine(lowerRight.getRow(), upperLeft.getColumn(), lowerRight.getRow(), lowerRight.getColumn(), wordImage, bBoxColor);
			aFitter.drawLine(upperLeft.getRow(), upperLeft.getColumn(), lowerRight.getRow(), upperLeft.getColumn(), wordImage, bBoxColor);
		}
	}

 	/**
	 * Draws the axis line of each word on the wordImage. 
	 *
	 * @param none
	 */
	private void drawAxes() {
		PointPixel axisB;
		PointPixel axisE;
		Word aWord;
		LineFitter aFitter = new LineFitter();
		Enumeration enumWords = words.elements();
		while (enumWords.hasMoreElements()) {
			aWord = (Word)enumWords.nextElement();
			axisB = aWord.getAxisBeginPoint();
			axisE = aWord.getAxisEndPoint();
			aFitter.drawLine(axisB.getRow(), axisB.getColumn(), axisE.getRow(), axisE.getColumn(), wordImage, bBoxColor);
		}
	}

 
 	/**
	 * Returns the word image in a 2d array
	 * 
	 * @param none
	 * @return The 2d array representation of the word image
	 */
	public int[][] getWordImage() {
		return wordImage;
	}
 
 	/**
	 * Returns the set of words found
	 * 
	 * @param none
	 * @return The collection of the words
	 */
	public Collection getWords() {
		return words.values();
	}

 	/**
	 * Returns the hash table of words found
	 * 
	 * @param none
	 * @return The hash table of the words
	 */
	public Hashtable getWordsHashtable() {
		return words;
	}
}


import java.util.*;
import java.lang.Math;
import java.lang.*;

/**
 * A class to label the image. 
 * All pixels with value = bPix are considered as background and  
 * they are all labelled with bPixLabel which is set to 0.
 * Other regions are labelled with consecutive integers.
 * 
 * @author Chart Reading project 
 * @version 1.0
 */

public class LabelImage{
	final static int [] rPos = {-1, -1, -1, 0, 1, 1, 1, 0}; 
	final static int [] cPos = {1, 0, -1, -1, -1, 0, 1, 1};
	//Storing the image as 2D array
	private int[][] inputImage;
	private int[][] pixelLabel; 
	private int[] labelsArray;
	private int noOfLabels;
	private int imageHeight;
	private int imageWidth;
 	private int bPix; //value of the background pixel
 	private int bPixLabel; //label of the background pixels (set to 0)


  /**
   * Constructor.
	 *
   * @param inImage2D The 2-D representation of the image
	 * @param imageRows The number of columns
	 * @param imageColumns The number of rows
	 * @param bP The color value of the background 
   */
	public LabelImage(int[][] inImage2D, int imageRows, int imageColumns, int bP) {
		inputImage = inImage2D;  
		imageHeight = imageRows;
		imageWidth = imageColumns;
		pixelLabel = new int[imageRows][imageColumns];
		bPix = bP;
		noOfLabels = 1;  //the background
   	bPixLabel = 0;
	}

  /**
   * Labels the image using the code of p.65 in Computer Vision by
   * by Shapiro and Stockman. The background pixels are labelled with zero.
	 *
   * @param none
	 * @return A Vector of number of labels as an Integer, the 1d integer array
	 * of color values for each label number and the 2d integer array of the labelled 
	 * image.
   */
	public Vector applyLabelling() {
		int rows = imageHeight;
		int columns = imageWidth;

		//System.out.println("In applyLabelling of LabelImage.");
		int label = 1; 
 		int minLabel;
 		int[] priorNeigLabel = new int[4];
		int[] parentArray = new int[rows*columns];
		Vector labelVector = new Vector(3);
		parentArray[0] = -1; //this is a root, it is there for the background pixels

		int j = 0;
		int noOfbPixels = 0;
		for (int r = 0; r < rows; r++) {
			for (int c = 0; c < columns; c++) {
				pixelLabel[r][c] = -1; //Initialize
			}
			for (int c = 0; c < columns; c++) {
				if (inputImage[r][c] == bPix) {
					pixelLabel[r][c] = bPixLabel;
					continue;
				}	

				j = 0;

//check 4 neighbors that are already processed, record the ones that are in the same region
				for (int i = 0; i < 4; i++) {
					if ((r+rPos[i]) >= 0 && (c+cPos[i]) >= 0 && (r+rPos[i]) < imageHeight && (c+cPos[i]) < imageWidth) {
						if (Math.abs(inputImage[r][c] - inputImage[r+rPos[i]][c+cPos[i]]) < 8) {
					 		priorNeigLabel[j] = pixelLabel[r+rPos[i]][c+cPos[i]];
							j++;
						}
					}
				}
				
				//System.out.println("There are " + j + "prior neighbors in the same region.");
				if (j == 0) { //there are no prior neighbors in the same region
					pixelLabel[r][c] = label;
					noOfLabels++;
					parentArray[label] = -1; //this label is a root
					label++;
					//System.out.println("Labeled ("+r+", "+c+") with " + label);
				}
				else {  //Find the minimum label of the neighbors
					minLabel = priorNeigLabel[0];
					for (int i = 1; i < j; i++) {
						if (priorNeigLabel[i] < minLabel)
							minLabel = priorNeigLabel[i];
					}
					//System.out.println("min label is " + minLabel);
					pixelLabel[r][c] = minLabel;
					//System.out.println("Labeled ("+r+", "+c+") with " + minLabel);
				}
				for (int i = 0; i < j; i++) {
					if (pixelLabel[r][c] != priorNeigLabel[i]) {
						union(pixelLabel[r][c], priorNeigLabel[i], parentArray);
						//noOfLabels--;
					}
				}	
			}
		}
	//System.out.println("End of first pass. There are " + noOfLabels+ " labels.");

	//Second pass replaces the first pass labels with equivalence class labels
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				if (pixelLabel[r][c] != bPixLabel) { //0 is the background label
					pixelLabel[r][c] = findParent(pixelLabel[r][c], parentArray);
				}
			}
		}
		//System.out.println("No of label is " + label);
		int[] labelsMap = new int[label];
		noOfLabels = 0;
		for (int i = 0; i < label; i++) {
			if (parentArray[i] == -1) { //a root
				labelsMap[i] = noOfLabels;
				noOfLabels++;
			}
 		}
		//System.out.println("End of second pass. There are " + noOfLabels+ " labels.");

   	labelsArray = new int[noOfLabels];

		//Third pass replaces the second pass labels with consecutive numbered labels
		for(int r = 0; r < rows; r++) {
			for(int c = 0; c < columns; c++) {
				pixelLabel[r][c] = labelsMap[pixelLabel[r][c]];
				labelsArray[pixelLabel[r][c]] = inputImage[r][c];
				//pixelLabel[r][c] = ((int)(255/noOfLabels))*pixelLabel[r][c];
			}
		}
		//System.out.println("End of third pass. There are " + noOfLabels+ " labels.");

		//System.out.println("There are " + noOfLabels + " objects.");

		labelVector.add(new Integer(noOfLabels)); //Count of Blobs
		labelVector.add(labelsArray);//Array holding the mapping of sequential labels to pixel labels
		//Note that label 0 in labelsMap will point to some label x in pixelLabel
		labelVector.add(pixelLabel);//Pixel Labels
		return labelVector;
	}

  /**
   * Finds the roots of label and neigLabel in the parent array
   * and makes label the parent of neigLabel, 
	 * if neigLabel is the background label, makes neigLabel the parent of label. 
	 *
   * @param label
	 * @param neigLabel
	 * @param parentArray 
   */
	private void union(int label, int neigLabel, int[] parentArray) {
//System.out.println("In union method.");
		int j = label;
		int k = neigLabel;
		while (parentArray[j] != -1)
			j = parentArray[j];	
		while (parentArray[k] != -1) 
			k = parentArray[k];	
		if (j != k) {
			if (k == bPixLabel)
				parentArray[j] = k;
			else
				parentArray[k] = j;
		}	
	}

  /**
	 * This method finds the root of label in the parent array
	 * All roots have -1 as their value in the parentArray
	 *
   * @param label
	 * @param parentArray 
   */
	private int findParent(int label, int[] parentArray) {
		int j = label;
		while (parentArray[j] != -1)
			j = parentArray[j];	
		return j;
	}

}


import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to determine the bounding boxes of the regions.
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class BoundingBoxFinder{

	private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
	private int bPixLabel;      //background label
	private int noOfLabels;

	private int[][] inputImage;     //input image
	private PointPixel[] boundingBoxCorners;

	/**
	 * Constructor.
	 *
	 * @param inImage The labelled or the bordered image
	 * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
	 * @param bPix The label of the background pixel regions
	 * @param noOfBlobs The number of regions in the image
	 */
	public BoundingBoxFinder(int[][] inImage, int imageRows, int imageColumns, int bPix, int noOfBlobs) {
		inputImage = inImage;
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPixLabel= bPix;
		noOfLabels = noOfBlobs;
		boundingBoxCorners = new PointPixel[noOfLabels*2];
	}

	/**
	 * Finds the bounding box of all the regions.
	 * Records two points -the left upper corner and the right lower corner of
	 * each region.
	 *
	 * @param none
	 */
	public void findBoundingBoxes() {
		//System.out.println("In findBoundingBoxes method 2.");
		int[] minRow = new int[noOfLabels];
		int[] minColumn = new int[noOfLabels];
		int[] maxRow = new int[noOfLabels];
		int[] maxColumn = new int[noOfLabels];
		int[] count = new int[noOfLabels];
		int labelNo;
		PointPixel aPoint;
		for (int i = 0; i < noOfLabels; i++) {
			count[i] = 0;
		}
		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {
				labelNo = inputImage[i][j];
				if (labelNo != bPixLabel) {
					aPoint = new PointPixel(i, j);
					if (count[labelNo] == 0) { //this is the first point encountered
						minRow[labelNo] = i;
						minColumn[labelNo] = j;
						maxRow[labelNo] = i;
						maxColumn[labelNo] = j;
					}
					if (i > maxRow[labelNo]) maxRow[labelNo] = i;
					if (i < minRow[labelNo]) minRow[labelNo] = i;
					if (j > maxColumn[labelNo]) maxColumn[labelNo] = j;
					if (j < minColumn[labelNo]) minColumn[labelNo] = j;
					count[labelNo]++;
				}
			}
		}
		//System.out.println("Found min/max row/columns.");
		PointPixel upperLeft;
		PointPixel lowerRight;
		for (int i = 2; i < noOfLabels*2; i+=2) {
			upperLeft = new PointPixel(minRow[(int)(i/2)], minColumn[(int)(i/2)]);
			lowerRight = new PointPixel(maxRow[(int)(i/2)], maxColumn[(int)(i/2)]);
			boundingBoxCorners[i] = upperLeft;
			boundingBoxCorners[i+1] = lowerRight;
		}
		//System.out.println("\nFound bounding boxes.");
	}


	/**
	 * Returns the array of bounding box corners; the upper left
	 * and the lower right corner of the bounding box of each region.
	 *
	 * @param none
	 * @return The <code>PointPixel</code> array of the corners for all regions.
	 *
	 */
	public PointPixel[] getBoundingBoxes() {
		return boundingBoxCorners;
	}

}


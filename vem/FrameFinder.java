import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to find out if the image has a frame of an image.
 * If there is a frame, the region of the frame is determined.
 *
 * @author Chart Reading project
 * @version 1.0
 */

public class FrameFinder {

	private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
 	private int bPixLabel; 			//background label

	private int[][] borderImage;  //Labelled image
	private int noOfLabels;
	private Region[] allRegions;

	private PointPixel upperLeft;
	private PointPixel lowerRight;

 /**
	* Constructor
	*
 	* @param inImage The input image whose frame is to be found
	* @param labelCount The number of regions in the image
	* @param imageRows The number of rows 
	* @param imageColumns The number of columns
	* @param regions The Region array of all regions in the image
	* @param bPix The label of the background pixel
	*/
	public FrameFinder(int[][] inImage, int labelCount, int imageRows, int imageColumns, Region[] regions, int bPix) {
		borderImage = inImage;
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPixLabel = bPix;
		noOfLabels = labelCount;
		allRegions = regions;
	}


  /**
   * Finds which regions are frame. Checks the regions at the upper left
	 * corner of the image in a square area of sides 1/10th the height of 
	 * the image.
	 *
   * @param none
	 * @return True (there is a frame) or false (no frame)
	 */
	public boolean findFrameRegions() {
		int count = 0;
		int checkHeight = imageHeight - imageHeight/10 ;
		int checkWidth = imageWidth - imageWidth/10;
		int label;
		for (int i = 0; i < imageHeight/10; i++) {
			label = borderImage[i][i];
			if (label != bPixLabel) {
				if (isFrame(label, checkHeight, checkWidth)) {
					allRegions[label].setIsFrame(true);
					setFrameArea(label);
					count++;
				}
			}
		}	
		/*********
		for (int i = 1; i < noOfLabels; i++) {
			if (isFrame(i, checkHeight, checkWidth)) {
				allRegions[i].setIsFrame(true);
				setFrameArea(i);
				count++;
			}
		}
		*********/
		//System.out.println("Found "+count+" frame regions out of "+noOfLabels);
		if (count > 0) {
			//System.out.println("Frame corners are "+upperLeft+" and "+lowerRight);
			return true;
		}
		return false;
	}


  /**
   * Finds out if the region given by its label is the frame of the image. 
	 *
   * @param label The label of the region that is checked if it is the frame
	 * @param checkHeight The minimum height of a frame region
	 * @param checkWidth The minimum width of a frame region 
   */
	private boolean isFrame(int label, int checkHeight, int checkWidth) {
		int area, mass;
		double density;
		double[] centroid;
		double[] variance;
		double[] varianceRect;
		PointPixel upLeft = allRegions[label].getUpperLeft(); 
		PointPixel lowRight = allRegions[label].getLowerRight();
		int boxHeight = lowRight.getRow() - upLeft.getRow() + 1;
		int boxWidth = lowRight.getColumn() - upLeft.getColumn() + 1;
		if (label != bPixLabel && boxHeight > checkHeight && boxWidth > checkWidth) {
			centroid = findCentroid(label, upLeft, lowRight);
			variance = findVariance(label, upLeft, lowRight);
			if (isRectangle(centroid, variance, upLeft, lowRight)) {
				//This is a frame 
				//System.out.println("Region "+label+" is a frame");
				return true;
			}
		}
		return false;
	}


  /**
   * Updates the frame area of the image when a region that is more likely
	 * to be the frame is found.
	 *
   * @param label The label of the region that is the frame
   */
	private void setFrameArea(int label) {
		if (upperLeft == null) {
			upperLeft = allRegions[label].getUpperLeft();
		}
		else {
			PointPixel newUpperLeft = allRegions[label].getUpperLeft();
			int changed = 0;
			int row = upperLeft.getRow();
			int column = upperLeft.getColumn();
			if (newUpperLeft.getRow() > upperLeft.getRow()) {
				row = newUpperLeft.getRow();
				changed = 1;
			}
			if (newUpperLeft.getColumn() > upperLeft.getColumn()) {
				column = newUpperLeft.getColumn();
				changed = 1;
			}
			if (changed == 1) {
				upperLeft = new PointPixel(row, column);
			}
		}
		if (lowerRight == null) {
			lowerRight = allRegions[label].getLowerRight();
		}
		else {
			PointPixel newLowerRight = allRegions[label].getLowerRight();
			int changed = 0;
			int row = lowerRight.getRow();
			int column = lowerRight.getColumn();
			if (newLowerRight.getRow() < lowerRight.getRow()) {
				row = newLowerRight.getRow();
				changed = 1;
			}
			if (newLowerRight.getColumn() < lowerRight.getColumn()) {
				column = newLowerRight.getColumn();
				changed = 1;
			}
			if (changed == 1) {
				lowerRight = new PointPixel(row, column);
			}
		}
	}


  /**
   * Gives the centroid of the pixels with label 'label' in the given area. 
	 *
   * @param label The label of the region whose centroid is to be calculated
	 * @param upperLeft The upper left corner of the region
	 * @param lowerRight The lower right corner of the region
	 * @return Array of doubles; first element is the row number of the centroid, second is the column number of the centroid.
   */
	private double[] findCentroid(int label, PointPixel upperLeft, PointPixel lowerRight) {
		int count = 0;
		int sumRow = 0;
		int sumColumn = 0;
		double[] cent = new double[2];
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
				if (borderImage[i][j] == label) {
					count++;
					sumRow += i;
					sumColumn += j;
				}
			}
		}			
		cent[0] = (double)sumRow/(double)count;
		cent[1] = (double)sumColumn/(double)count;
		return cent;	
	}

  /**
   * Gives the variances of the pixels with label 'label' in the given area. 
	 *
   * @param label The label of the region whose variance is to be calculated
	 * @param upperLeft The upper left corner of the region
	 * @param lowerRight The lower right corner of the region
	 * @return Array of doubles; first element is the row variance, second is the column variance
   */
	private double[] findVariance(int label, PointPixel upperLeft, PointPixel lowerRight) {
		int count = 0;
		double sumRow = 0;
		double sumColumn = 0;
		double[] variance = new double[2];
		double[] cent = findCentroid(label, upperLeft, lowerRight);
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
				if (borderImage[i][j] == label) {
					count++;
					sumRow += (i-cent[0]) * (i-cent[0]);
					sumColumn += (j-cent[1]) * (j-cent[1]);
				}
			}
		}			
		variance[0] = sumRow/(double)count;
		variance[1] = sumColumn/(double)count;
		return variance;	
	}

  /**
   * Finds out if the region with the given centroid, variance and 
	 * bounding box area is a rectangle with all its pixels on the boundary. 
	 *
   * @param centroid The centroid of the region
	 * @param variance The row and column variance of the region
	 * @param upperLeft The upper left corner of the region
	 * @param lowerRight The lower right corner of the region
   */
	private boolean isRectangle(double[] centroid, double[] variance, PointPixel upperLeft, PointPixel lowerRight) {
		double[] cent =  new double[2];
		cent[0] = (double)(lowerRight.getRow() + upperLeft.getRow())/2;
		cent[1] = (double)(lowerRight.getColumn() + upperLeft.getColumn())/2;
		if (Math.abs(centroid[0] - cent[0]) < 0.001 && Math.abs(centroid[1] - cent[1]) < 0.001) {
			double[] varianceRect = findVarianceRectangle(cent, upperLeft, lowerRight);
			//System.out.println("Rectangle variance="+varianceRect[0]+", "+varianceRect[1]);
			if (Math.abs(variance[0] - varianceRect[0]) < 0.001 && Math.abs(variance[1] - varianceRect[1]) < 0.001) {
				return true;
			}
			return false;
		}
		return false;
	}

  /**
   * Finds out the variances of the rectangle in the given area
	 * with the given centroid. 
	 * The pixels of the rectangle are all on the boundary.
	 *
   * @param cent The centroid point; the row and the column
	 * @param upperLeft The upper left corner of the area
	 * @param lowerRight The lower right corner of the area
	 * @return Array of doubles; first element is the row variance, second is the column variance 
   */
	private double[] findVarianceRectangle(double[] cent, PointPixel upperLeft, PointPixel lowerRight) {
		int count = 0;
		double sumRow = 0;
		double sumColumn = 0;
		double[] variance = new double[2];
		//System.out.println("Rectangle centroid: "+cent[0]+", "+cent[1]);
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
				if (i == upperLeft.getRow() || i == lowerRight.getRow()) {
					count++;
					sumRow += (i-cent[0]) * (i-cent[0]);
					sumColumn += (j-cent[1]) * (j-cent[1]);
				}
				else if (j == upperLeft.getColumn() || j == lowerRight.getColumn()) {
					count++;
					sumRow += (i-cent[0]) * (i-cent[0]);
					sumColumn += (j-cent[1]) * (j-cent[1]);
				}
			}
		}			
		variance[0] = sumRow/(double)count;
		variance[1] = sumColumn/(double)count;
		return variance;	
	}

  /**
	 * Returns the upper left bounding box corner point.
	 *
   * @param none
	 * @return The upper left corner of the bounding box
	 */
	public PointPixel getUpperLeft() {
		return upperLeft;
	}

  /**
	 * Returns the lower right bounding box corner point.
	 *
   * @param none
	 * @return The lower right corner of the bounding box
	 */
	public PointPixel getLowerRight() {
		return lowerRight;
	}

}

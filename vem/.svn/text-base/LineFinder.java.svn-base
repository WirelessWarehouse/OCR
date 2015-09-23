import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to recognize discontinuous lines and combine them.
 * The lines that tend to extend each other but are not connected
 * are stored in one VirtualLine object.
 * All the VirtualLines in the image are stored in a Hashtable 
 * according to the angle of the line from the positive row axis 
 * (negative y axis) and the distance of the line from the origin
 * (the origin is where row and column numbers are both zero; 
 * the upper left corner of the image).
 * 
 * @author Chart Reading project
 * @version 1.0
 */

public class LineFinder {

	private int imageHeight;    //number of rows in the input image
	private int imageWidth;    	//number of columns in the input image
	private int noOfLabels;
	private int bPixLabel;

	private int angles;
	private int distances;

	private int[][] borderImage;
	private Region[] allRegions;
	private Hashtable allLines;

	/**
	 * Constructor.
	 *
   * @param none
	 */
	public LineFinder() {
	}

	/**
	 * Constructor.
	 * 
   * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
	 * @param aLines The Hashtable of all the VirtualLines in the image
	 */
	public LineFinder(int imageRows, int imageColumns, Hashtable aLines) {
		imageHeight = imageRows;
		imageWidth = imageColumns;
		allLines = aLines;
	}


	/**
	 * Constructor.
	 * 
   * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
   * @param labelCount The number of regions in the image
	 * @param bLabel The label of the background pixels
	 * @param bImage The 2d array reprentation of the border image
	 * @param allR The Region array for all the regions in the image
	 */
	public LineFinder(int imageRows, int imageColumns, int labelCount, int bLabel, int[][] bImage, Region[] allR) {
		imageHeight = imageRows;
		imageWidth = imageColumns;
		noOfLabels = labelCount;
		allRegions = allR;
		//Angle between 0 and 180
		//Distance to origin between 0 and sqrt(imageHeight^2+imageWidth^2)
		double maxSize = Math.sqrt(imageHeight*imageHeight+imageWidth*imageWidth);
		angles = 181;
		distances = (int)maxSize + 1;
		allLines = new Hashtable();
		/******
		allLines = new VirtualLine[angles][distances];
		for (int i = 0; i < angles; i++) {
			for (int j = 0; j < distances; j++) {
				allLines[i][j] = new VirtualLine();
			}	
		}
		**********/
		bPixLabel = bLabel;
		borderImage = bImage;
	}

  /**
	 * Creates VirtualLines. Goes through all the regions in the image
	 * and all the primitives in a region. All primitives are stored
	 * as part of a VirtualLine. Primitives from different regions can
	 * be part of the same VirtualLine.
	 * After all VirtualLines are created, addOnePixelRegions is called
	 * to add any possible one pixel regions to each VirtualLine.
	 *
   * @param none
   */
	public void findLines() {
		//System.out.println("In findLines of LineFinder.");
		Integer key;
		Region aRegion;
		Collection prims;
		Primitive aPrim;
		VirtualLine aLine;
		for (int i = 0; i < noOfLabels; i++) {
			aRegion = allRegions[i];
			prims = aRegion.getPrimitiveList();
			Iterator itr = prims.iterator();
			while (itr.hasNext()) {
				aPrim = (Primitive)itr.next();
				if (aPrim.getSize() > 1) {
					storeLine(aPrim);
				}
			}
		}
		Collection lines = allLines.values();
		Iterator itr = lines.iterator();
		while (itr.hasNext()) {
			aLine = (VirtualLine)itr.next();
			if (aLine.getNoOfPrims() > 0) {
				addOnePixelRegions(aLine);
			}
			if (aLine.getNoOfPrims() > 1) {
				//System.out.println(aLine.getNoOfPrims()+" primitives for angle="+i+", distance to origin="+j);
				//System.out.println(aLine);
			}
		}
		/********
		for (int i = 0; i < angles; i++) {
			for (int j = 0; j < distances; j++) {
				if (allLines[i][j].getNoOfPrims() > 0) {
					addOnePixelRegions(allLines[i][j]);
				}
				if (allLines[i][j].getNoOfPrims() > 1) {
					//System.out.println(allLines[i][j].getNoOfPrims()+" primitives for angle="+i+", distance to origin="+j);
					//System.out.println(allLines[i][j]);
				}
			}
		}
		***********/
	}


  /**
	 * Stores the given primitive in its corresponding VirtualLine according
	 * to its angle and distance from the origin.
	 * 
   * @param aPrim The primitive to be stored
	 */	
	private void storeLine(Primitive aPrim) {
//System.out.println("in storeLine\n");
		int label = aPrim.getParent();
		int tag = aPrim.getTagNo();
		int orientation = aPrim.getLineOrientation();
		double slope = aPrim.getSlope();
		double intercept = aPrim.getIntercept();
		double angle = aPrim.getAngle();
		int dist = aPrim.getDistance();
		Integer key;
		VirtualLine aLine;
		//LineFitter aFitter = new LineFitter();
		//double dist = Math.sqrt(aFitter.squareDistPointToLine(0, 0, slope, intercept, orientation));
		if (angle >= 0 && angle < angles && dist >= 0 && dist < distances) {
			key = generateKey(angle, dist);
			aLine = (VirtualLine)allLines.get(key);
			if (aLine != null) {
				aLine.addPrimitive(aPrim);
			}
			else {
				aLine = new VirtualLine();
				aLine.addPrimitive(aPrim);
				allLines.put(key, aLine); 
			}
			//allLines[(int)Math.rint(angle)][dist].addPrimitive(aPrim);
		}
		else {
			//System.out.println("Angle="+angle+", distance to origin="+dist);
		}
	}

	/**
	 * Generates a key for the hashtable based on the angle and the distance 
	 * of the primitive.
	 *
	 * @param ang The angle with respect to the positive row axis (negative y axis)
	 * @param dist The distance from the origin
	 * @return The key
	 */
	public Integer generateKey(double ang, int dist) {
		int key = dist*1000 + (int)Math.rint(ang);
		return new Integer(key);
	}

	/**
	 * Adds one pixel regions to the given VirtualLine.
	 * Checks the virtual line to see if a one pixel region is on the 
	 * axis of the line.
	 * Regions that are only 1 pixel cannot have angle and distance, so
	 * they are added to a VirtualLine using this method.
	 *
	 * @param aLine The VirtualLine to which one pixel regions are to be added
	 */
	private void addOnePixelRegions(VirtualLine aLine) {
		int orientation = aLine.getOrientation();
		double slope = aLine.getSlope();
		double intercept = aLine.getIntercept();
		PointPixel beginPoint = aLine.getBeginPoint();
		PointPixel endPoint = aLine.getEndPoint();
		int length = (int)(aLine.getLength()*0.2);
		if (orientation == Primitive.VERTICAL) { //slope = column/row
			for (int r = beginPoint.getRow() - length; r < endPoint.getRow() + length; r++) {
				int c = (int)((double)r*slope + intercept);
				if (r >= 0 && r < imageHeight && c >= 0 && c < imageWidth) {
					int label = borderImage[r][c];
					if (label != bPixLabel && allRegions[label].getNumPixels() == 1) {
						aLine.addPrimitive(allRegions[label].getPrimitive(0));
					}   
				}
			}
		}
		else { //slope = row/column
			for (int c = beginPoint.getColumn() - length; c < endPoint.getColumn() + length; c++) {
				int r = (int)((double)c*slope + intercept);
				if (r >= 0 && r < imageHeight && c >= 0 && c < imageWidth) {
					int label = borderImage[r][c];
					if (label != bPixLabel && allRegions[label].getNumPixels() == 1) {
						aLine.addPrimitive(allRegions[label].getPrimitive(0));
					}   
				}
			}
		}
	}


  /**
	 * Creates the image of one line. Other than the pixels of the line
	 * which are black, everything is background (white).
	 *
	 * @param ang The angle with respect to the positive row axis (negative y axis)
	 * @param dist The distance from the origin
	 * @return The 2d array representation of the VirtualLine image
   */
  public int[][] makeLineImage(double angle, int dist) {
		int[][] bImage = new int[imageHeight][imageWidth];
		Integer key = generateKey(angle, dist);
		//LinkedList prims = allLines[(int)angle][dist].getPrimitives();
		//System.out.println(allLines[(int)angle][dist]);
		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {
				bImage[i][j] = 255;
			}
		}
		VirtualLine aLine = (VirtualLine)allLines.get(key);
		if (aLine != null) {
			int row, column;
			Point aPoint;
			LinkedList points;
			Primitive aPrim;
			ListIterator lItr2;
			LinkedList prims = aLine.getPrimitives();
			ListIterator lItr = prims.listIterator(0);
			while (lItr.hasNext()) {
				aPrim = (Primitive)lItr.next();
				points = aPrim.getAllPoints();
				lItr2 = points.listIterator(0);
				while (lItr2.hasNext()) {
					aPoint = (Point)lItr2.next();
					row =	(int)aPoint.getX();
					column =	(int)aPoint.getY();
					bImage[row][column] = 0;
				}
			}
		}
		return bImage;
	}

  /**
	 * Returns the hashtable of the VirtualLines of the image.
	 *
   * @param none 
	 * @return The hastable of VirtualLines.
	 */
	public Hashtable getAllLines() {
		return allLines;
	}

}

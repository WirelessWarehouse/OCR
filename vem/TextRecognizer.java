import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to determine which regions are characters.
 *
 * @author Chart Reading project
 * @version 1.0
 */

public class TextRecognizer {

	private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
 	private int bPixLabel; 			//background label

	private int[][] borderImage;  //Labelled image
	private int noOfLabels;				//number or regions in the image
	private int noOfCharacters;
	private Region[] allRegions;
	//PointPixel[] boundingBoxCorners;
	LinkedList allDashedLines;

 /**
	* Constructor. Initializes a linked list for the regions which are dashed lines.
	*
	* @param inImage The border image
	* @param labelCount The number of regions in the image
	* @param imageRows The number of rows
	* @param imageColumns The number of columns
	* @param regions The Region array of all the regions
	* @param bPix The background pixels' label (zero)
	*/
	public TextRecognizer(int[][] inImage, int labelCount, int imageRows, int imageColumns, Region[] regions, int bPix) {
		borderImage = new int[imageRows][imageColumns];
		for (int i=0; i<imageRows; i++) {
			for (int j=0; j<imageColumns; j++) {
				//System.out.println(inImage[i][j]);
				borderImage[i][j] = inImage[i][j];
				//System.out.print(inPixImage[i][j]);
			}
		}	
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPixLabel = bPix;
		noOfLabels = labelCount;
		noOfCharacters = 0;
		//boundingBoxCorners = new PointPixel[noOfLabels*2];
		allRegions = regions;
		allDashedLines = new LinkedList();
		//borderedImage = new int[imageRows][imageColumns];
		//pixelData = new PixelDatabase(imageRows, imageColumns);
	}


  /**
   * Finds which objects (regions) are character. The size of each region is checked
	 * against a value that is set to 0.05*the length of the longer side of the image.
	 * In the first pass, all regions are checked with respect to their size.
	 * <p>
	 * In the second pass, if a region was found to be both character and dashed line
	 * in the first pass, it is set to a character only.
	 * If the region was found to be neither and if it is a vertical or horizontal 
	 * line, then the region around it is checked. If there are characters around
	 * that region, that region is also set to be a character.
	 * <p>
	 * In the third pass, the dashed lines are checked. If a dashed line is 
	 * composed of dashes that are all characters, or only one dash is not a character
	 * those regions are no longer dashed lines and the dashed line is removed from 
	 * the dashed lines list. If there is one dash that was not found to be a 
	 * character, it is checked again to see if 
	 * there are characters around it. If there are, only then it is set to be 
	 * a character. 
	 * <p>
	 * Each dash is a separate region.
	 *
   * @param none
   */
	public void findTextRegions() {
		//Go through the bounding boxes, find area, mass, density. 
		//Based on the area and the density, decide whether the region is text or not.
		PointPixel upperLeft, lowerRight;
		int boxHeight, boxWidth, orientation;
		double checkSize = imageHeight*0.05;
		if (imageHeight < imageWidth) {
			checkSize = imageWidth*0.05;
		}
    //an attempt to liberalize the size of characters  DLC
                checkSize = 4*checkSize;
    // end DLC
		//System.out.println("Checking lengths against "+checkSize);
		noOfCharacters = 0;
		for (int i = 1; i < noOfLabels; i++) {
			if (isText(i, checkSize)) {
				upperLeft = allRegions[i].getUpperLeft();
				lowerRight = allRegions[i].getLowerRight();
				boxHeight = lowerRight.getRow() - upperLeft.getRow() + 1;
				boxWidth = lowerRight.getColumn() - upperLeft.getColumn() + 1;
				orientation = Primitive.VERTICAL;
				if (boxWidth > boxHeight) {
					orientation = Primitive.HORIZONTAL;
				}
				allRegions[i].setOrientation(orientation);
				allRegions[i].setIsCharacter(true);
				noOfCharacters++;
			}
		}
		//Second pass
		for (int i = 1; i < noOfLabels; i++) {
			if (allRegions[i].getIsCharacter() && allRegions[i].getIsDashedLine()) {
				allRegions[i].setIsDashedLine(false);
			}
			if (!allRegions[i].getIsCharacter() && !allRegions[i].getIsDashedLine() && i != bPixLabel) {
				upperLeft = allRegions[i].getUpperLeft();
				lowerRight = allRegions[i].getLowerRight();
				boxHeight = lowerRight.getRow() - upperLeft.getRow() + 1;
				boxWidth = lowerRight.getColumn() - upperLeft.getColumn() + 1;
				if (boxHeight < checkSize && boxWidth < checkSize) {
					if (isVerticalLine(i, upperLeft, lowerRight) || isHorizontalLine(i, upperLeft, lowerRight)) {
						//System.out.println("Region "+i+" is a vertical or horizontal line.");
						if (isTextAround(i, upperLeft, lowerRight, checkSize)) {
							//System.out.println("Region "+i+" is a character (vertical or horizontal line)");
							orientation = Primitive.VERTICAL;
							if (boxWidth > boxHeight) {
								orientation = Primitive.HORIZONTAL;
							}
							allRegions[i].setOrientation(orientation);
							allRegions[i].setIsCharacter(true);
							noOfCharacters++;
						}	
					}
				}
				else {
				}
			}
		}

		int count = 0;
		int characterCount;
		VirtualLine aLine;
		LinkedList aList;
		Region aRegion;
		ListIterator lItr2;
		ListIterator lItr = allDashedLines.listIterator(0);
		while (lItr.hasNext()) {
			count++;
			characterCount = 0;
			aLine = (VirtualLine)lItr.next();
			//System.out.print("\n"+count+"th dashed line has "+aList.size()+" dashes: Regions ");
			aList = aLine.getRegions();
			lItr2 = aList.listIterator(0);
			while (lItr2.hasNext()) {
				aRegion = (Region)lItr2.next();
				if (aRegion.getIsCharacter()) {
					aRegion.setIsDashedLine(false);
					characterCount++;
				}
				//System.out.print(label+", ");
			}
			if ((aList.size() - characterCount) <= 1) {
				lItr2 = aList.listIterator(0);
				while (lItr2.hasNext()) {
					aRegion = (Region)lItr2.next();
					aRegion.setIsDashedLine(false);
					if (!aRegion.getIsCharacter()) {
						upperLeft = aRegion.getUpperLeft();
						lowerRight = aRegion.getLowerRight();
						boxHeight = lowerRight.getRow() - upperLeft.getRow() + 1;
						boxWidth = lowerRight.getColumn() - upperLeft.getColumn() + 1;
						if (isTextAround(aRegion.getRegion(), upperLeft, lowerRight, checkSize)) {
							orientation = Primitive.VERTICAL;
							if (boxWidth > boxHeight) {
								orientation = Primitive.HORIZONTAL;
							}
							aRegion.setOrientation(orientation);
							aRegion.setIsCharacter(true);
							noOfCharacters++;
						}
					}	
				}
				aLine.setIsDashedLine(false);
				lItr.remove();
			}
			/*********
			else if (aList.size() == 2) {
				Region[] regions = new Region[2];
				regions[0] = (Region)aList.getFirst();
				regions[1] = (Region)aList.getLast();
				int i = 0;
				if (regions[1].getNumPixels() > regions[0].getNumPixels()) {
					i = 1;
				}
				aRegion = regions[i];
				upperLeft = aRegion.getUpperLeft();
				lowerRight = aRegion.getLowerRight();
				boxHeight = lowerRight.getRow() - upperLeft.getRow() + 1;
				boxWidth = lowerRight.getColumn() - upperLeft.getColumn() + 1;
				if (isTextAround(aRegion.getRegion(), upperLeft, lowerRight, checkSize)) {
					orientation = Primitive.VERTICAL;
					if (boxWidth > boxHeight) {
						orientation = Primitive.HORIZONTAL;
					}
					aRegion.setOrientation(orientation);
					aRegion.setIsCharacter(true);
					aRegion.setIsDashedLine(false);
					noOfCharacters++;
					if (i == 0) aRegion = regions[1];
					if (i == 1) aRegion = regions[0];
					aRegion.setOrientation(orientation);
					aRegion.setIsCharacter(true);
					aRegion.setIsDashedLine(false);
					noOfCharacters++;
				}
			}
			*********/
		}
		//System.out.println("\nFound "+noOfCharacters+" character regions out of "+noOfLabels);
		//System.out.println("\nFound "+allDashedLines.size()+" dashed lines\n");
		lItr = allDashedLines.listIterator(0);
		while (lItr.hasNext()) {
			aLine = (VirtualLine)lItr.next();
			//System.out.print(aLine);
		}
	}


  /**
   * Finds if the given region is text. 
	 * Sometimes the characters are connected.
	 * The size check is done to take care of two characters that are 
	 * next to each other and are connected (they are only one region).
	 *
	 * If the region passes the size check, and it is either a vertical or
	 * horizontal line, it is checked to see if it is part of a dashed line.
	 * The dashed lines are saved as VirtualLines.
	 *
	 * If the region passes the size check, and it is not a vertical or horizontal line,
	 * and its density is greater than or equal to 0.145 and the region is not a 
	 * rectangle, then the region is set to be a character.
	 *	
   * @param label The label of the region to be checked
	 * @param checkSize The size that the size of the region should be checked against. The size of the region needs to be smaller than checkSize in both height and width in order to be qualified to be a character region.
   * 
   */
	private boolean isText(int label, double checkSize) {
		int area, mass;
		double density;
		double[] centroid;
		double[] variance;
		double[] varianceRect;
		PointPixel upperLeft = allRegions[label].getUpperLeft();
		PointPixel lowerRight = allRegions[label].getLowerRight();
		int boxHeight = lowerRight.getRow() - upperLeft.getRow() + 1;
		int boxWidth = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		//if (label != bPixLabel && ((boxHeight < checkSize && boxWidth < 2*checkSize) || (boxWidth < checkSize && boxHeight < 2*checkSize))) {
		if (label != bPixLabel && ((boxHeight < checkSize
					    && boxWidth < 2*checkSize
					    && boxWidth < 2*boxHeight)
					   || (boxWidth < checkSize
					       && boxHeight < 2*checkSize
					       && boxHeight < 2*boxWidth))) {
			if (isVerticalLine(label, upperLeft, lowerRight) || isHorizontalLine(label, upperLeft, lowerRight)) {
				Region aRegion = allRegions[label];
				if (!aRegion.getIsDashedLine()) {
					if (aRegion.getNumPixels() > 1) {
						VirtualLine aLine = new VirtualLine();
						//LinkedList dashedLines = new LinkedList();
						//System.out.println("Begin dashed line with region "+label);
						if (isDashedLine(label, aLine)) {
							//System.out.println("Region "+label+" is part of a dashed line");
							aLine.setIsDashedLine(true);
							allDashedLines.add(aLine);
						}
					}
				}
				else {
					//System.out.println("Region "+label+" is part of a dashed line");
				}
			}
			else { 
				//System.out.println("Region "+label+": upperLeft="+upperLeft+", lowerRight="+lowerRight);
				//System.out.println("Region "+label+": height="+boxHeight+", width="+boxWidth);
				area = boxHeight*boxWidth;
				mass = findPixelCount(label, upperLeft, lowerRight);
				density = (double)mass/(double)area;
				centroid = findCentroid(label, upperLeft, lowerRight);
				variance = findVariance(label, upperLeft, lowerRight);
				//System.out.println("R "+label+": a="+area+", m="+mass+", d="+density+" C="+centroid[0]+", "+centroid[1]+" V="+variance[0]+", "+variance[1]);
				//System.out.println("Centroid: ("+centroid[0]+", "+centroid[1]+"). Variance: ("+variance[0]+", "+variance[1]+")");
				if (density >= 0.145 && !(isRectangle(variance, upperLeft, lowerRight))) {
					//This is a character
					//System.out.println("Region "+label+" is a character, "+upperLeft+", "+lowerRight);
					return true;
				}
			}
		}	
		return false;
	}



  /**
   * Finds the centroid of the pixels of the given label in the given area. 
	 *
   * @param label The label of the region
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @return An array of size two of double values; first one is the row number of the centroid, the second one is the column number of the centroid
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
   * Finds the variances of the pixels of the given label in the given area. 
	 *
   * @param label The label of the region
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @return An array of size two of double values; first one is the row variance, the second one is the column variance
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
   * Finds the variances of the pixels of the rectangle defined by the two
	 * given points; the upper left corner and the lower right corner. 
	 *
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @return An array of size two of double values; first one is the row variance, the second one is the column variance
   */
	private double[] findVarianceRectangle(PointPixel upperLeft, PointPixel lowerRight) {
		int count = 0;
		double sumRow = 0;
		double sumColumn = 0;
		double[] variance = new double[2];
		double[] cent =  new double[2];
		cent[0] = (double)(lowerRight.getRow() - upperLeft.getRow())/2 + (double)upperLeft.getRow();
		cent[1] = (double)(lowerRight.getColumn() - upperLeft.getColumn())/2 + (double)upperLeft.getColumn();
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
   * Gives the number of pixels of the given label in the given region. 
	 *
   * @param label The label of the region
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @return The number of pixels with the same label in the given area.
   */
	private int findPixelCount(int label, PointPixel upperLeft, PointPixel lowerRight) {
		int count = 0;
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
				if (borderImage[i][j] == label) {
					count++;
				}
			}
		}			
		return count;	
	}

  /**
   * Finds out if the region is a vertical line of
	 * one pixel or two pixels wide. 
	 *
   * @param label The label of the region
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @return True or false
   */
	private boolean isVerticalLine(int label, PointPixel upperLeft, PointPixel lowerRight) {
		int count = findPixelCount(label, upperLeft, lowerRight);
		int height = lowerRight.getRow() - upperLeft.getRow() + 1;
		int width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		if (width == 1 && height == count) {
			return true;
		}
		if (count == 2*height && width == 2) {
			return true;
		}
		/*
		if (count == 2*height+2*width-4 && (height >= 2*width || width == 2)) {
			return true;
		}
		*/
		return false;
	}

  /**
   * Finds out if the region is a horizontal line of
	 * one or two pixels wide. 
	 *
   * @param label The label of the region
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @return True or false
   */
	private boolean isHorizontalLine(int label, PointPixel upperLeft, PointPixel lowerRight) {
		int count = findPixelCount(label, upperLeft, lowerRight);
		int height = lowerRight.getRow() - upperLeft.getRow() + 1;
		int width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		if (height == 1 && width == count) {
			return true;
		}
		if (count == 2*width && height == 2) {
			return true;
		}
		/*
		if (count == 2*height+2*width-4 && (width >= 2*height || height == 2)) {
			return true;
		}
		*/
		return false;
	}



  /**
   * Finds out if there is another text region around this region 
	 * with the given label. 
	 *
   * @param label The label of the region
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @param checkSize Defines the size of the area that is to checked arount the given region
	 * @return True or false
   */
	private boolean isTextAround(int label, PointPixel upperLeft, PointPixel lowerRight, double checkSize) {
		//System.out.println("Region "+label+": is near a text region?");
		int height = lowerRight.getRow() - upperLeft.getRow() + 1;
		int width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		int row = upperLeft.getRow();
		int column = upperLeft.getColumn();
		int newLabel;
		//Check the region around 
		int orientation = Primitive.VERTICAL;
		if (width > height) {
			orientation = Primitive.HORIZONTAL;
		}
		else if (width == height) {
			orientation = -1;
		}	
		int size = height;
		if (width > height) {
			size = width;
		}
		//System.out.println("Looking around region "+label+", size="+(size*4)+", row="+row+", column="+column);
		//System.out.println("Looking around region "+label+", size="+(int)(checkSize/2)+", row="+row+", column="+column);
		for (int j = 0; j < (int)(checkSize); j++) {
			for (int i = column; i < column + width; i++) { 
				if (row >= 0 && row < imageHeight && i >= 0 && i < imageWidth) { 
					newLabel = borderImage[row][i];
					if (newLabel != label && newLabel != bPixLabel) {
						//A new region is encountered
						//System.out.println("New region "+newLabel+" at ("+row+", "+i+")");
						if (allRegions[newLabel].getIsCharacter()) {
							//aRegion = allRegions[label];
							//anotherRegion = allRegions[newLabel];
							//if (aRegion.getLowerRight().getRow() == anotherRegion.getLowerRight().getRow() || 
							//		aRegion.getUpperLeft().getRow() == anotherRegion.getUpperLeft().getRow() ||
							//		aRegion.getLowerRight().getColumn() == anotherRegion.getLowerRight().getColumn() ||
							//		aRegion.getUpperLeft().getColumn() == anotherRegion.getUpperLeft().getColumn()) {

							//if (orientation == allRegions[newLabel].getOrientation() || orientation == -1) {
								//System.out.println("Region "+newLabel+" is around region "+label); 
								return true;
							//}
						}	
					}	
				}
			}
			for (int i = row; i < row + height; i++) { 
				if (i >= 0 && i < imageHeight && (column + width - 1) >= 0 && (column + width - 1) < imageWidth) { 
					newLabel = borderImage[i][column + width - 1];
					if (newLabel != label && newLabel != bPixLabel) {
						//A new region is encountered
						//System.out.println("New region "+newLabel+" at ("+i+", "+(column+width-1)+")");
						if (allRegions[newLabel].getIsCharacter()) {
							//if (orientation == allRegions[newLabel].getOrientation() || orientation == -1) {
								//System.out.println("Region "+newLabel+" is around region "+label); 
								return true;
							//}
						}
					}
				}
			}
			for (int i = column + width - 1; i >= column; i--) { //row = row + length - 1;
				if ((row + height - 1) >= 0 && (row  + height - 1) < imageHeight && i >= 0 && i < imageWidth) { 
					newLabel = borderImage[row + height - 1][i];
					if (newLabel != label && newLabel != bPixLabel) {
						//A new region is encountered
						//System.out.println("New region "+newLabel+" at ("+(row+height-1)+", "+i+")");
						if (allRegions[newLabel].getIsCharacter()) {
							//if (orientation == allRegions[newLabel].getOrientation() || orientation == -1) {
								//System.out.println("Region "+newLabel+" is around region "+label); 
								return true;
							//}
						}
					}
				}
			}
			for (int i = row + height - 1; i >= row; i--) { 
				if (i >= 0 && i < imageHeight && column >= 0 && column < imageWidth) { 
					newLabel = borderImage[i][column];
					if (newLabel != label && newLabel != bPixLabel) {
						//A new region is encountered
						//System.out.println("New region "+newLabel+" at ("+i+", "+column+")");
						if (allRegions[newLabel].getIsCharacter()) {
							//if (orientation == allRegions[newLabel].getOrientation() || orientation == -1) {
								//System.out.println("Region "+newLabel+" is around region "+label); 
								return true;
							//}
						}
					}
				}
			}
			row--;
			column--;
			width += 2;
			height += 2;
		//System.out.println("row="+row+", column="+column+", width="+width+", height="+height);
		}
		return false;
	}


  /**
   * Finds out if the region of the given label is part of a dashed line. 
	 * Fits a line -needs to be a straight line.
	 * Checks the endpoints, tries to find another line that is
	 * aligned with this one.
	 * 
	 * Is the line extendible?
	 * Starts from its endpoints, moves in the same slope outward.
	 * Moves 4 times the length/width of this region.
	 * Checks if another region with the same slope, intercept, angle, length  
	 * is encountered. 
	 *
   * @param label The label of the region to be checked
	 * @param dashedLines The dashed line that this region will be added to if it is a dash itself
	 * @return True or false
   */
	private boolean isDashedLine(int label, VirtualLine dashedLines) {
		//System.out.println("Region "+label+": is it part of a dashed line?");
		Region aRegion = allRegions[label];
		if (aRegion.getIsDashedLine()) {
			//System.out.println("Region "+label+" is a dashed line");
			return true;
		}
		if (aRegion.getNumPixels() == 1) {
			aRegion.setIsDashedLine(true);
			dashedLines.addRegion(aRegion);
			//System.out.println("Region "+label+" is a dashed line");
			return true;
		}
		if (aRegion.getIsCharacter()) {
			return false;
		}
		int row, column, newLabel, newLineOrientation;
		double newSlope, newIntercept, newAngle;
		Vector newFitVector;
		PointPixel upperLeft = aRegion.getUpperLeft();
		PointPixel lowerRight = aRegion.getLowerRight();
		int height = lowerRight.getRow() - upperLeft.getRow() + 1;
		int width = lowerRight.getColumn() - upperLeft.getColumn() + 1;

		Vector fitVector = aRegion.fitLine();
		int lineOrientation = ((Integer)fitVector.get(0)).intValue();
		double slope = ((Double)fitVector.get(1)).doubleValue();
		double intercept = ((Double)fitVector.get(2)).doubleValue();
		double angle = ((Double)fitVector.get(3)).doubleValue();

		LinkedList listPoints = allRegions[label].getPixelList();
		PointPixel firstPoint = new PointPixel(listPoints.getFirst());
		PointPixel lastPoint = new PointPixel(listPoints.getLast());
		int rowFirstPoint = firstPoint.getRow();
		int columnFirstPoint = firstPoint.getColumn();
		int rowLastPoint = lastPoint.getRow();
		int columnLastPoint = lastPoint.getColumn();
		int otherLines = 0;
		int otherLabel = -1;
		boolean isDashed = false;
		if (lineOrientation == Primitive.HORIZONTAL) {
			//System.out.println("Region "+label+": width="+width+", height="+height);
			for (int i = columnFirstPoint; i > columnFirstPoint - 4*width; i--) {
				row = (int)(slope * (double)i + intercept);
				if (row >= 0 && row < imageHeight && i >= 0 && i < imageWidth) { 
					newLabel = borderImage[row][i];
					if (newLabel != label && newLabel != bPixLabel) {
						if (extendDashedLine(label, height, lineOrientation, slope, intercept, newLabel, dashedLines)) {
							Region anotherRegion = allRegions[newLabel];
							if (anotherRegion.getIsDashedLine()) {
								isDashed = true;
							}
							else {
								anotherRegion.setIsDashedLine(true);
							}
							dashedLines.addRegion(anotherRegion);
							otherLines++;
							otherLabel = newLabel;
							//System.out.println("Region "+newLabel+" extends region "+label);
							break;
						}
					}
				}
			}
			for (int i = columnLastPoint; i < columnLastPoint + 4*width; i++) {
				row = (int)(slope * (double)i + intercept);
				if (row >= 0 && row < imageHeight && i >= 0 && i < imageWidth) { 
					newLabel = borderImage[row][i];
					if (newLabel != label && newLabel != bPixLabel) {
						if (extendDashedLine(label, height, lineOrientation, slope, intercept, newLabel, dashedLines)) {
							Region anotherRegion = allRegions[newLabel];
							if (anotherRegion.getIsDashedLine()) {
								isDashed = true;
							}
							else {
								anotherRegion.setIsDashedLine(true);
							}
							dashedLines.addRegion(anotherRegion);
							otherLines++;
							otherLabel = newLabel;
							//System.out.println("Region "+newLabel+" extends region "+label);
							break;
						}
					}
				}
			}
		}
		if (lineOrientation == Primitive.VERTICAL) {
			//System.out.println("Region "+label+": width="+width+", height="+height);
			for (int i = rowFirstPoint; i > rowFirstPoint - 4*height; i--) {
				column = (int)(slope * (double)i + intercept);
				if (i >= 0 && i < imageHeight && column >= 0 && column < imageWidth) { 
					newLabel = borderImage[i][column];
					if (newLabel != label && newLabel != bPixLabel) {
						if (extendDashedLine(label, width, lineOrientation, slope, intercept, newLabel, dashedLines)) {
							Region anotherRegion = allRegions[newLabel];
							if (anotherRegion.getIsDashedLine()) {
								isDashed = true;
							}
							else {
								anotherRegion.setIsDashedLine(true);
							}
							dashedLines.addRegion(anotherRegion);
							otherLines++;
							otherLabel = newLabel;
							//System.out.println("Region "+newLabel+" extends region "+label);
							break;
						}
					}
				}
			}
			for (int i = rowLastPoint; i < rowLastPoint + 4*height; i++) {
				column = (int)(slope * (double)i + intercept);
				if (i >= 0 && i < imageHeight && column >= 0 && column < imageWidth) { 
					newLabel = borderImage[i][column];
					if (newLabel != label && newLabel != bPixLabel) {
						if (extendDashedLine(label, width, lineOrientation, slope, intercept, newLabel, dashedLines)) {
							Region anotherRegion = allRegions[newLabel];
							if (anotherRegion.getIsDashedLine()) {
								isDashed = true;
							}
							else {
								anotherRegion.setIsDashedLine(true);
							}
							dashedLines.addRegion(anotherRegion);
							otherLines++;
							otherLabel = newLabel;
							//System.out.println("Region "+newLabel+" extends region "+label);
							break;
						}
					}
				}
			}
		}
		//System.out.println("There are "+otherLines+" lines extending region "+label);
		if (otherLines > 1 || isDashed) {
		//if (aRegion.getIsDashedLine()) {
			aRegion.setIsDashedLine(true);
			LineFitter aLineFitter = new LineFitter();
			int distance = (int)(Math.rint((Math.sqrt(aLineFitter.squareDistPointToLine(0, 0, slope, intercept, lineOrientation)))));
			dashedLines.addRegion(aRegion, lineOrientation, angle, distance);
			//System.out.println("There are "+otherLines+" lines extending region "+label+", it is a dashed line");
			return true;
		}
		if (otherLines == 1) {
			Region otherRegion = allRegions[otherLabel];
			otherRegion.setIsDashedLine(false);
			//System.out.println("Region "+otherLabel+" is not dashed line");
			return false;
		}
		return false;
	}

  /**
   * Tries to extend the region of the given label (dashed line)
	 * by the region of the newLabel.
	 *
   * @param label The label of the region that is to be extended
	 * @param lineOrientation The orientation of the region that is to be extended
	 * @param slope The slope of the region that is to be extended
	 * @param intercept The intercept of the region that is to be extended
	 * @param newLabel The label of the region that can extend the first region
	 * @param dashedLines The dashed line list to which the region of newLabel will be added if it is a dash extending the region of label
	 */
	private boolean extendDashedLine(int label, int thickness, int lineOrientation, double slope, double intercept, int newLabel, VirtualLine dashedLines) {
		Vector newFitVector;
		int newLineOrientation;
		double newSlope, newIntercept, newAngle;
		Region aRegion = allRegions[label];
		Region anotherRegion = allRegions[newLabel];
		PointPixel upperLeft = anotherRegion.getUpperLeft();
		PointPixel lowerRight = anotherRegion.getLowerRight();
		int height = lowerRight.getRow() - upperLeft.getRow() + 1;
		int width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		if (anotherRegion.getNumPixels() == 1) {
			//System.out.println("Region "+newLabel+" extends region "+label);
			return true;
		}
		if (!anotherRegion.getIsCharacter()) {
			newFitVector = anotherRegion.fitLine();
			newLineOrientation = ((Integer)newFitVector.get(0)).intValue();
			newSlope = ((Double)newFitVector.get(1)).doubleValue();
			newIntercept = ((Double)newFitVector.get(2)).doubleValue();
			newAngle = ((Double)newFitVector.get(3)).doubleValue();
			if (lineOrientation == newLineOrientation && Math.abs(slope - newSlope) <= 1 && Math.abs(intercept - newIntercept) <= 1) {
				if ((newLineOrientation == Primitive.HORIZONTAL && thickness == height) || (newLineOrientation == Primitive.VERTICAL && thickness == width)) {
					//System.out.println(label+": s="+slope+", i="+intercept);
					//System.out.println(newLabel+": s="+newSlope+", i="+newIntercept);
					//This new region is extending the old one
					//Potential dashed line
					//System.out.println("Region "+newLabel+" extends region "+label+", region "+label+" is dashed");
					LineFitter aLineFitter = new LineFitter();
					int distance = (int)(Math.rint((Math.sqrt(aLineFitter.squareDistPointToLine(0, 0, newSlope, newIntercept, newLineOrientation)))));
					//System.out.println("Region "+newLabel+" extends region "+label);
					return true;
				}
				//else {
					//System.out.println("Region "+newLabel+" is different");
				//}
			}
		}
		return false;
	}	


  /**
   * Finds out if the region with the given variance and the
	 * bounding box corners is a rectangle with all its pixels on the boundary. 
	 *
   * @param variance The variance of the area
	 * @param upperLeft The upper left corner of the area
	 * @param lowerRight The lower right corner of the area
   */
	private boolean isRectangle(double[] variance, PointPixel upperLeft, PointPixel lowerRight) {
		double[] varianceRect = findVarianceRectangle(upperLeft, lowerRight);
		//System.out.println("Rectangle variance="+varianceRect[0]+", "+varianceRect[1]);
		if (Math.abs(variance[0] - varianceRect[0]) < 0.001 && Math.abs(variance[1] - varianceRect[1]) < 0.001) {
			return true;
		}
		return false;
	}


  /**
   * Returns the number of character regions. 
	 *
   * @param none
	 * @return The number of character regions
   */
  public int getNoOfCharacters() {
    return noOfCharacters;
  }

  /**
   * Returns the number of dashed line regions. 
	 *
   * @param none
   * @return The number of dashed line regions
   */ 
  public int getNoOfDashedLines() {
    return allDashedLines.size();
  }

}

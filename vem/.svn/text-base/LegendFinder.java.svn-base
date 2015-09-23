import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find the legend in the image.
 * <p>
 * Searches through the rectangles in the image
 * to find a large rectangle that holds two or more
 * rectangles and text inside.
 */

public class LegendFinder {

  private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
	private int bPixLabel;			//label of the background
	private int noOfLabels;
	private int[][] borderImage;
  private	Region[] allRegions;
	private LinkedList allRectangles;

	/**
	 * Constructor.
	 *
	 * @param none
	 */
	public LegendFinder() {
	}

	/**
	 * Constructor.
	 *
	 * @param imageRows The number of rows in the image
	 * @param imageColumns The number or columns in the image
	 * @param bImage The 2d array representation of the bordered image
	 * @param bPixL The label of the background pixel regions
	 * @param labelCount The number of regions in the image
	 * @param regions The <code>Region</code> array of all the regions in the image
	 * @param rectangles The linked list of all the rectangles in the image
	 */
	public LegendFinder(int imageRows, int imageColumns, int[][] bImage, int bPixL, int labelCount, Region[] regions, LinkedList rectangles) {
		imageHeight = imageRows;
	 	imageWidth = imageColumns;
		noOfLabels = labelCount;
		allRegions = regions; 
		allRectangles = rectangles;
		borderImage = bImage;
		bPixLabel = bPixL;
	}

	/**
	 * Finds the legend in the image by searching through all rectangles.
	 * The size of a legend rectangle needs to be lower than a threshold.
	 * The threshold is set to be 5 or 10 percent of the length of a side
	 * of the image. If such a rectangle is found, it is checked whether
	 * there is text next to that rectangle to qualify it as a legend rectangle.
	 *
	 * @param none
	 */
	public void findLegend() {
		System.out.println("In findLegend of LegendFinder.");
		int count = 0;
		double checksize = imageHeight*0.05;
		if (imageWidth > imageHeight) {
			checksize = imageWidth*0.1;
		}
		int height, width;
		PointPixel upperLeft, lowerRight;
		Rectangle aRectangle;
System.out.println("checksize= "+checksize);
		ListIterator lItr = allRectangles.listIterator(0); 
		while (lItr.hasNext()) {
			aRectangle = (Rectangle)lItr.next();
			upperLeft = aRectangle.getUpperLeft();
			lowerRight = aRectangle.getLowerRight();
			height = lowerRight.getRow() - upperLeft.getRow() + 1;
			width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
System.out.println("height= "+height+" width= "+width);
			if (height <= checksize && width <= checksize) {
				//Check the area on the right side of this rectangle 
				//to find any text
				if (findText(lowerRight, height, width)) {
                                        // wrong; not here
					//aRectangle.setIsLegend(true);
					count++;
				}
			}
		}
		System.out.println("Found "+count+" legend rectangles out of "+allRectangles.size()+" rectangles");
	}


	/**
	 * Checks the region to the right of the legend rectangle to find
	 * if there is any text around it.
	 * Goes 2 heights up and 2 widths to the right.
	 *
	 * @param lowRight The lower right corner of the legend rectangle
	 * @param height The height of the legend rectangle
	 * @param width The width of the legend rectangle
	 * @return True if there is text to the right of the area and false if there is no text
	 */
	private boolean findText(PointPixel lowRight, int height, int width) {
                System.out.println("In findText");
		int label;
		int row = lowRight.getRow()-height;
		int column = lowRight.getColumn()+2*width;
System.out.println("row= "+row+" column= "+column);
		if (row < 0) {
			row = 0;
		}
		if (column > imageWidth-1) {
			column = imageWidth - 1;
		}
		PointPixel upperLeft = new PointPixel(row, lowRight.getColumn()); 
		PointPixel lowerRight = new PointPixel(lowRight.getRow(), column); 
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
			  label = borderImage[i][j];
				if (label != bPixLabel && allRegions[label].getIsCharacter()) {
					//There is a character region next to this rectangle.
					return true;
				}
			}
		}
		return false;
	}



	/**
	 * Creates a 2d array representation of an image that has the legend 
	 * rectangle pixels as black and all others as white. 
	 *
	 * @param rectangles The linked list of legend rectangles  
	 * @return The 2d array representation of the legend image 	
	 */
	public int[][] makeLegendImage(LinkedList rectangles) {
		LinkedList alist, primlist, pointsList;
		Primitive aPrim;
		Rectangle aRectangle;
		Point aPoint;
		ListIterator lItr, lItr2, lItr3;
		int[][] legendImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				legendImage[i][j] = 255;
			}
		}
		lItr = rectangles.listIterator(0);
		while(lItr.hasNext()) {
			aRectangle = (Rectangle)lItr.next();
			if (aRectangle.getIsLegend()) {
				primlist = aRectangle.getSides();
				lItr2 = primlist.listIterator(0);
				while(lItr2.hasNext()) {
					aPrim = (Primitive)lItr2.next();
					pointsList = aPrim.getAllPoints();
					lItr3 = pointsList.listIterator(0);
					while (lItr3.hasNext()) {
						aPoint = (Point)lItr3.next();
						legendImage[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
					}
				}
			}
		}
		return legendImage;
	}

}					
				

import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find the gridlines in the image.
 * <p> 
 * Get the incline angles of the axes, 
 * Get the distances of the tick marks,
 * Search inside the axes area.
 * <p> 
 * Find all regions that have the same incline angle
 * or are perpendicular to the incline angle
 * and same distance as one of the ticks
 * <p> 
 * There are regions that are thick lines or filled areas
 * Select the thin line regions only.
 * <p> 
 * There are regions that are the intersection of
 * dashed gridlines and therefore not purely
 * horizontal or vertical!
 * 
 * @author Chart Reading project
 * @version 1.0
 */

public class GridlineFinder {

  private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
	private int diagonal;
	//private int noOfLabels;			//number of regions in the input image
  //private	Region[] allRegions;
	//private VirtualLine[][] allLines;
	private Hashtable allLines;
	private Axis horizontalAxis;
	private Axis verticalAxis;
	private LinkedList allGridlines;
	private LinkedList horGridlines;
	private LinkedList verGridlines;
	private int[][] gridlineImage;

	private PointPixel origin;
	private PointPixel upperLeft;
	private PointPixel lowerRight;

	/**
	 * Constructor. 
	 *
	 * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
	 */
	public GridlineFinder(int imageRows, int imageColumns) {
		imageHeight = imageRows;
	 	imageWidth = imageColumns;
		diagonal = 1 + (int)(Math.sqrt(imageRows*imageRows+imageColumns*imageColumns));
	}

	/**
	 * Constructor. 
	 *
	 * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
	 * @param lines The hash table of the VirtualLines in the image
	 * @param hAxis The horizontal axis
	 * @param vAxis The vertical axis
	 * @param orig The point where the axes meet 
	 */
	public GridlineFinder(int imageRows, int imageColumns, Hashtable lines, Axis hAxis, Axis vAxis, PointPixel orig) {
		imageHeight = imageRows;
	 	imageWidth = imageColumns;
		allLines = lines;
		diagonal = 1 + (int)(Math.sqrt(imageRows*imageRows+imageColumns*imageColumns));
		horizontalAxis = hAxis;
		verticalAxis = vAxis;
		origin = orig;
		gridlineImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				gridlineImage[i][j] = 255;
			}
		}
	}

	/**
	 * Finds the gridlines inside the area of the axes
	 * (finds the regions/primitives that classify as gridlines).
	 * Checks the hash table of VirtualLines 
	 * for the entries that have the same angle as the axes
	 * and whose distance is between the minimum and the maximum
	 * distance of the axes from the origin of the image.
	 *
	 * @param none
	 */
	public void findGridlines() {
		//System.out.println("In findGridline of GridlineFinder.");
		findAxesArea();
		allGridlines = new LinkedList();
		horGridlines = new LinkedList();
		verGridlines = new LinkedList();
		int inclineAngleH = (int)Math.rint(horizontalAxis.getInclineAngle());
		int inclineAngleV = (int)Math.rint(verticalAxis.getInclineAngle());
		//System.out.println("Axes area points are upperLeft="+upperLeft+", lowerRight="+lowerRight);
		int height = lowerRight.getRow() - upperLeft.getRow() + 1;
		int width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		int beginDistV = origin.getColumn() + 2; 
		int endDistV = lowerRight.getColumn(); 
		int beginDistH = upperLeft.getRow();
		int endDistH = origin.getRow() - 2;
		int distanceV = origin.getColumn();
		int distanceH = origin.getRow();
		LinkedList ticksV = verticalAxis.getTickList();
		LinkedList ticksH = horizontalAxis.getTickList();
		//int count = checkLines(inclineAngleV, beginDistV, endDistV, height, Primitive.VERTICAL);
		//count += checkLines(inclineAngleH, beginDistH, endDistH, width, Primitive.HORIZONTAL);
		int count = checkLines(inclineAngleV, distanceV, ticksH, height, Primitive.VERTICAL);
		count += checkLines(inclineAngleH, distanceH, ticksV, width, Primitive.HORIZONTAL);
		//System.out.println("Found "+count+" gridlines"); 
		ListIterator lItr = allGridlines.listIterator(0);
		//while (lItr.hasNext()) {
			//System.out.print((VirtualLine)lItr.next());
		//}
	}

	/**
	 * Finds the virtual lines inside the area of the axes that 
	 * align with the tick marks. The lines have the same angle
	 * as the axis. A gridline is saved as a VirtualLine.
	 * It is checked if the gridline can be extended further.
	 * <p>
	 * length and index are currently not used. 
	 *
	 * @param angle The angle that the VirtualLine needs to have
	 * @param distanceAxis The distance of the axis from the origin of the image
	 * @param ticks The linked list of the tick marks of the axis
	 * @param length The length of the axis
	 * @param index The orientation of the line; horizontal or vertical
	 */
	private int checkLines(int angle, int distanceAxis, LinkedList ticks, int length, int index) {
		int i;
		int count = 0;
		int added = 0;
		int beginInside = 0;
		int endInside = 0;
		double tAngle;
		Integer key;
		PointPixel beginP, endP, beginPointG, endPointG;
		VirtualLine aLine;
		VirtualLine aGridline = new VirtualLine();
		Primitive aTick, aPrim;
		LinkedList prims, points;
		ListIterator lItr2, lItr3;
		Point aPoint;
		LineFinder aFinder = new LineFinder();
		ListIterator lItr = ticks.listIterator(0);
		while (lItr.hasNext()) {
		//for (int i = beginDist; i <= endDist; i++) {
			aTick = (Primitive)lItr.next();
			i = aTick.getDistance();
			tAngle = aTick.getAngle();
			//System.out.println("Tick angle = " + tAngle+", distance = "+i);
			key = aFinder.generateKey((double)angle, i);
			aLine = (VirtualLine)allLines.get(key);
			if (aLine != null) {
				//if (i != distanceAxis && allLines[angle][i].getNoOfPrims() > 1) {
				if (i != distanceAxis && aLine.getNoOfPrims() > 1) {
					//aLine = allLines[angle][i];
					beginP = aLine.getBeginPoint();
					endP = aLine.getEndPoint();
					//Region aRegion = allRegions[i];
					//if (!aRegion.getIsThickLine() && !aRegion.getIsFilledArea())
					//if (aLine.getLength() >= 0.9*length) {

					//Check if the line is inside the axes area
					//Go through the primitives of aLine and select the ones that are inside
					//the axes area.
					prims = aLine.getPrimitives();
					lItr2 = prims.listIterator(0);
					added = 0;
					while (lItr2.hasNext()) {
						aPrim = (Primitive)lItr2.next();
						beginP = aPrim.getBeginPoint();
						endP = aPrim.getEndPoint();
						beginInside = 0;
						endInside = 0;
						if (beginP.getRow() >= upperLeft.getRow() && beginP.getRow() <= lowerRight.getRow() && beginP.getColumn() >= upperLeft.getColumn() && beginP.getColumn() <= lowerRight.getColumn()) { 
							beginInside = 1;
						}
						if (endP.getRow() >= upperLeft.getRow() && endP.getRow() <= lowerRight.getRow() && endP.getColumn() >= upperLeft.getColumn() && endP.getColumn() <= lowerRight.getColumn()) {
							endInside = 1;
						}
						if (beginInside == 1 && endInside == 1) {
							//Both the beginning point and the end point of the primitive is inside
							//the axes area.
							aPrim.setPartOfGridline(1);
							added++;
							if (added == 1) {
								aGridline = new VirtualLine();
							}
							aGridline.addPrimitive(aPrim);
							points = aPrim.getAllPoints();
							lItr3 = points.listIterator(0);
							while (lItr3.hasNext()) {
								aPoint = (Point)lItr3.next();
								gridlineImage[(int)aPoint.getX()][(int)aPoint.getY()] = 200;
							}
						/**************
						if (added == 1) {
							if (aPrim.getLineOrientation() == Primitive.HORIZONTAL) {
								if (aPrim.getBeginPoint().getColumn() < aPrim.getEndPoint().getColumn()) {
									beginPointG = new PointPixel(aPrim.getBeginPoint().getRow(), aPrim.getBeginPoint().getColumn());
									endPointG = new PointPixel(aPrim.getEndPoint().getRow(), aPrim.getEndPoint().getColumn());
								}
								else {
									endPointG = new PointPixel(aPrim.getBeginPoint().getRow(), aPrim.getBeginPoint().getColumn());
									beginPointG = new PointPixel(aPrim.getEndPoint().getRow(), aPrim.getEndPoint().getColumn());
								}
							}
							else {
								if (aPrim.getBeginPoint().getRow() > aPrim.getEndPoint().getRow()) {
									beginPointG = new PointPixel(aPrim.getBeginPoint().getRow(), aPrim.getBeginPoint().getColumn());
									endPointG = new PointPixel(aPrim.getEndPoint().getRow(), aPrim.getEndPoint().getColumn());
								}
								else {
									endPointG = new PointPixel(aPrim.getBeginPoint().getRow(), aPrim.getBeginPoint().getColumn());
									beginPointG = new PointPixel(aPrim.getEndPoint().getRow(), aPrim.getEndPoint().getColumn());
								}
							}
						}
						else {
							setEndPoints(beginPointG, endPointG, aPrim);
						}
						if (index == Primitive.VERTICAL) {
							verGridlines.add(aPrim);
						}
						else {
							horGridlines.add(aPrim);
						}
						allGridlines.add(aPrim);
						count++;
						*****************/
						}
					} //end of primitives in a line
					if (added > 0) {
						//Check if the gridline can be extended.
						extendLine(aGridline);
						allGridlines.add(aGridline);
						count++;
					}
				}
			}
		} //end of ticks
		return count;
	}


	/**
	 * Extends the given VirtualLine.
	 * <p>
	 * Gets the angle and the distance of the given VirtualLine. 
	 * Finds out if there are other lines with angles and distances close to this one
	 * and inside the axes area. The primitives of the other lines
	 * extending this one are added to this line and also made part of a gridline.
	 * 
	 * @param aLine The VirtualLine to be extended 
	 */
	private void extendLine(VirtualLine aLine) {
		int checkAngle;
		int extendsLine = 0;
		int dist = aLine.getDistance();
		int angle = (int)Math.rint(aLine.getAngle());
		Integer key;
		VirtualLine anotherLine;
		PointPixel beginP, endP;
		LinkedList prims, points;
		ListIterator lItr2, lItr3;
		Point aPoint;
		Primitive aPrim;
		LineFinder aFinder = new LineFinder();
		for (int i = angle-2; i <= angle+2; i++) {
			for (int j = dist-2; j <= dist+2; j++) {
				checkAngle = i;
				if (i < 0) {
					checkAngle += 180;
				}
				//System.out.println("Checking lines with angle="+checkAngle+", distance="+j);
				key = aFinder.generateKey((double)checkAngle, j);
				anotherLine = (VirtualLine)allLines.get(key);
				if (anotherLine != null) {
					if ((!(checkAngle == angle && j == dist)) && aLine.getNoOfPrims() > 0) {
						prims = anotherLine.getPrimitives();
						lItr2 = prims.listIterator(0);
						while (lItr2.hasNext()) {
							aPrim = (Primitive)lItr2.next();
							beginP = aPrim.getBeginPoint();
							endP = aPrim.getEndPoint();
							extendsLine = 0;
							//System.out.println("Primitive from "+beginP+" to "+endP);
							//Check if the primitive is inside the axes area
							if ((beginP.getRow() >= upperLeft.getRow() && beginP.getRow() <= lowerRight.getRow() && endP.getRow() >= upperLeft.getRow() && endP.getRow() <= lowerRight.getRow()) && (beginP.getColumn() >= upperLeft.getColumn() && beginP.getColumn() <= lowerRight.getColumn() && endP.getColumn() >= upperLeft.getColumn() && endP.getColumn() <= lowerRight.getColumn())) {
								if (aPrim.getLineOrientation() == Primitive.VERTICAL) { //vertical
									//aPrim should not overlap with a part of aLine.
									if (gridlineImage[beginP.getRow()][dist] == 255 && gridlineImage[endP.getRow()][dist] == 255) {
										extendsLine = 1;
									}
								}
								else { //horizontal
									if (gridlineImage[dist][beginP.getColumn()] == 255 && gridlineImage[dist][endP.getColumn()] == 255) {
										extendsLine = 1;
									}
								}
							}
							if (extendsLine == 1) {
								//System.out.println("Primitive from "+beginP+" to "+endP+", angle="+checkAngle+", distance="+j+" extends gridline");
								aPrim.setPartOfGridline(1);
								aLine.addPrimitive(aPrim);
								points = aPrim.getAllPoints();
								lItr3 = points.listIterator(0);
								while (lItr3.hasNext()) {
									aPoint = (Point)lItr3.next();
									gridlineImage[(int)aPoint.getX()][(int)aPoint.getY()] = 200;
								}
							}
						}
					}
				}
			}
		}
	}



	/**
	 * Sets the two endpoints of the given primitive.
	 *
	 * @param beginPoint The first point which can be a new end point 
	 * @param endPoint The second point which can be a new end point 
	 * @param aPrim The primitive whose end points are checked 
	 */
	private void setEndPoints(PointPixel beginPoint, PointPixel endPoint, Primitive aPrim) {
		int minIndex = 0;
		int maxIndex = 0;;
		PointPixel[] list = new PointPixel[4];
		list[0] = beginPoint;
		list[1] = endPoint;
		list[2] = aPrim.getBeginPoint();
		list[3] = aPrim.getEndPoint();
		PointPixel minPoint = list[0];
		PointPixel maxPoint = list[0];
		if (aPrim.getLineOrientation() == Primitive.HORIZONTAL) {
			//The minimum column point is the begin point
			//The maximum column point is the end point
			for (int i = 1; i < 4; i++) {
				if (list[i].getColumn() < minPoint.getColumn()) {
					minPoint = list[i];
					minIndex = i;
				}
				if (list[i].getColumn() > maxPoint.getColumn()) {
					maxPoint = list[i];
					maxIndex = i;
				}
			}
		}
		else {
			//The minimum row point is the begin point
			//The maximum row point is the end point
			for (int i = 1; i < 4; i++) {
				if (list[i].getRow() < minPoint.getRow()) {
					minPoint = list[i];
					minIndex = i;
				}
				if (list[i].getRow() > maxPoint.getRow()) {
					maxPoint = list[i];
					maxIndex = i;
				}
			}
		}
		if (minIndex == 2 || minIndex == 3) {
			beginPoint.setRow(minPoint.getRow());
			beginPoint.setColumn(minPoint.getColumn());
		}
		if (maxIndex == 2 || maxIndex == 3) {
			endPoint.setRow(maxPoint.getRow());
			endPoint.setColumn(maxPoint.getColumn());
		}
	}


	/**
	 * Finds the axes area. Sets the upper left and the lower right
	 * corner points of the axes area.
	 *
	 * @param none
	 */
	private void findAxesArea() {
		PointPixel[] points = new PointPixel[4];
		points[0] = horizontalAxis.getBeginPoint();
		points[1] = horizontalAxis.getEndPoint();
		points[2] = verticalAxis.getBeginPoint();
		points[3] = verticalAxis.getEndPoint();
		int minRow = points[0].getRow();
		int maxRow = points[0].getRow();
		int minColumn = points[0].getColumn();
		int maxColumn = points[0].getColumn();
		for (int i = 1; i < 4; i++) {
			if (points[i].getRow() < minRow) {
				minRow = points[i].getRow();
			}
			if (points[i].getRow() > maxRow) {
				maxRow = points[i].getRow();
			}
			if (points[i].getColumn() < minColumn) {
				minColumn = points[i].getColumn();
			}
			if (points[i].getColumn() > maxColumn) {
				maxColumn = points[i].getColumn();
			}
		}	
		upperLeft = new PointPixel(minRow-1, minColumn-1);
		lowerRight = new PointPixel(maxRow+1, maxColumn+1);
		//System.out.println("Axes area points are upperLeft="+upperLeft+", lowerRight="+lowerRight);
	}


	/**
	 * Creates a 2d array representation of an image that has the Gridline pixels
	 * as black and all others as white 
	 *
	 * @param lines The VirtualLines that are the gridlines in the image 
	 * @return The 2d array representation of the gridline image
	 */
	public int[][] makeGridlineImage(LinkedList lines) {
		int[][] gridlineImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				gridlineImage[i][j] = 255;
			}
		}
		LinkedList points, prims;
		Point aPoint;
		Primitive aPrim;
		VirtualLine aLine;
		ListIterator lItr, lItr3, lItr2;
		lItr = lines.listIterator(0);
		while (lItr.hasNext()) {
			aLine = (VirtualLine)lItr.next();
			prims = aLine.getPrimitives();
			lItr3 = prims.listIterator(0);
			while (lItr3.hasNext()) {
				aPrim = (Primitive)lItr3.next();
				points = aPrim.getAllPoints();
				lItr2 = points.listIterator(0);
				while (lItr2.hasNext()) {
					aPoint = (Point)lItr2.next();
					gridlineImage[(int)aPoint.getX()][(int)aPoint.getY()] = 200;
				}
			}
		}
		return gridlineImage;
	}
			
	/**
	 * Returns the linked list of the gridlines (each gridline is a VirtualLine).
	 *
	 * @param none
	 * @return The linked list of the gridlines 
	 */
	public LinkedList getGridlines() {
		return allGridlines;
	}
			
	/*****************
	METHODS BELOW HERE ARE NOT USED

	 * @param none
	 * 	
	 * This method creates a 2d array that has the Gridline pixels
	 * as black and all others as white 
	 *
	public int[][] makeGridlineImage(LinkedList prims) {
		int[][] gridlineImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				gridlineImage[i][j] = 255;
			}
		}
		LinkedList points;
		Point aPoint;
		Primitive aPrim;
		ListIterator lItr, lItr2;
		lItr = prims.listIterator(0);
		while (lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			points = aPrim.getAllPoints();
			lItr2 = points.listIterator(0);
			while (lItr2.hasNext()) {
				aPoint = (Point)lItr2.next();
				gridlineImage[(int)aPoint.getX()][(int)aPoint.getY()] = 200;
			}
		}
		return gridlineImage;
	}
				

	**
	 * @param none
	 * 
	 * This method finds the gridlines inside the area of the axes
	 * (finds the regions that classify as gridlines)
	 *
	public void findGridlinesOld() {
		//System.out.println("In findGridline of GridlineFinder.");
		double inclineAngleH = horizontalAxis.getInclineAngle();
		double inclineAngleV = verticalAxis.getInclineAngle();
		findAxesArea();
		Region aRegion;
		PointPixel regionUpperLeft, regionLowerRight;
		int count = 0;
		for (int i = 1; i < noOfLabels; i++) {
			aRegion = allRegions[i];
			if (aRegion.getNumPixels() < 1) {
				continue;
			}
			regionUpperLeft = aRegion.getUpperLeft();
			regionLowerRight = aRegion.getLowerRight();
			//Check if the region is "thin"
			if (!aRegion.getIsThickLine() && !aRegion.getIsFilledArea() && (Math.abs(regionUpperLeft.getColumn() - regionLowerRight.getColumn()) < 4 || Math.abs(regionUpperLeft.getRow() - regionLowerRight.getRow()) < 4)) {
				//Check if the region is inside the axes area
				if (regionUpperLeft.getRow() > upperLeft.getRow() && regionUpperLeft.getColumn() > upperLeft.getColumn() && regionLowerRight.getRow() < lowerRight.getRow() && regionLowerRight.getColumn() < lowerRight.getColumn()) { //aRegion is inside the axes area
					Vector fitVector = aRegion.fitLine();
					int lineOrientation = ((Integer)fitVector.get(0)).intValue();
					double slope = ((Double)fitVector.get(1)).doubleValue();
					double intercept = ((Double)fitVector.get(2)).doubleValue();
					double angle = ((Double)fitVector.get(3)).doubleValue();
					if (lineOrientation == Primitive.HORIZONTAL && Math.abs(inclineAngleH - angle) < 2) {
						aRegion.setIsGridline(true);
						count++;
						//System.out.println("Region "+i+" is a gridline");
					}
					else if (lineOrientation == Primitive.VERTICAL && Math.abs(inclineAngleV - angle) < 2) {
						aRegion.setIsGridline(true);
						//System.out.println("Region "+i+" is a gridline");
						count++;
					}
				}
			}
		}
		//System.out.println("Found "+count+" gridline regions out of "+noOfLabels+" regions");
	}
	****************/
}					
				

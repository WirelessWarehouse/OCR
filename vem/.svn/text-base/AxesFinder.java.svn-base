import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find the horizontal and vertical axes of the image.
 * <p>
 * allLines is a hash table. Each entry is a VirtualLine.
 * The keys are generated based on the angle and the distance of the line.
 * <p>
 * Find the vertical line (angle = 0) whose distance to the origin is the smallest.
 * Find the horizontal line (angle = 90) whose distance to the origin is the longest.
 * The length of both should be close to the number of pixels they have and
 * the total length should be long enough to be an axis.
 * <p> 
 * Check to see if the beginning points of the two lines are close enough to be axes.
 * Once the two axis lines are found, add the tick marks.
 *
 * @author Chart Reading project
 * @version 1.0
 */

public class AxesFinder {

  private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
	private int minHAxisLength;
	private int minVAxisLength;
	private int diagonal;

  private	Region[] allRegions;
  private PixelDatabase allPixels;
	private Hashtable allLines;

	private Axis horizontalAxis;
	private Axis verticalAxis;

 /**
	* Constructor.
	*
	* @param none
	*/
	public AxesFinder() {
	}

 /**
	* Constructor. Minimum horizontal axis length is set to be 0.4 times
	* the number of columns of the image. Minimum vertical axis length
	* is set to be 0.4 times the number of rows of the image.
	*
	* @param imageRows The number of rows in the image
	* @param imageColumns The number of columns in the image
	* @param regions The Region array of all the regions in the image
	* @param pixels The database for all the border pixels of the image
	* @param lines The hash table of VirtualLines in the image
	*/
	public AxesFinder(int imageRows, int imageColumns, Region[] regions, PixelDatabase pixels, Hashtable lines) {
		imageHeight = imageRows;
	 	imageWidth = imageColumns;
		allLines = lines;
		minHAxisLength = (int)(0.4*imageColumns);
		minVAxisLength = (int)(0.4*imageRows);
//DLC
//minHAxisLength = 0;
//minVAxisLength = 0;
//end DLC
		diagonal = 1 + (int)(Math.sqrt(imageRows*imageRows+imageColumns*imageColumns));
		allPixels = pixels;	
		allRegions = regions; 
		horizontalAxis = new Axis(Axis.HORIZONTAL_AXIS);
		verticalAxis = new Axis(Axis.VERTICAL_AXIS);
	}

	/**
   * Finds the VirtualLine that is at an angle of 0 degrees from the 
	 * positive row axis (negative y axis) that is longer than the minimum
	 * vertical axis length and that is the VirtualLine that is closest to 
	 * the origin.
	 * <p>
   * Finds the VirtualLine that is at an angle of 90 degrees from the 
	 * positive row axis (negative y axis) that is longer than the minimum
	 * horizontal axis length and that is the VirtualLine that is furthest from 
	 * the origin.
	 * <p>
	 * There will be cases where the axes angles will not be exactly 0 and 90.
	 * <p>
   * The length of both should be close to the number of pixels they have and
   * the total length should be long enough to be an axis.
	 * <p>
	 * All the primitives on the two VirtualLines are set to be part of an axis.
	 * The tick marks are added to the axes. 
	 * <p>
	 * There are cases where the axes is extendible with another line
	 * whose distance to the origin is close but still different.
	 * <p>
	 * Once the axes lines are found, see if they are extendible.
	 * Go to the end points, see the region of the endpoint if
	 * there is another virtual line or primitive that begins/ends there
	 * with similar angle and similar distance to the origin
	 * Add to the axes lines.
	 *
	 * @param none
	 */
	public boolean findAxes() {
		//System.out.println("In findAxes of AxesFinder.");
		boolean foundV = false;
		boolean foundH = false;
		Integer key;
		VirtualLine aLine;
		LineFinder aFinder = new LineFinder();
		key = aFinder.generateKey(0, 0);
		VirtualLine vLine = (VirtualLine)allLines.get(key);
		key = aFinder.generateKey(90, 0);
		VirtualLine hLine = (VirtualLine)allLines.get(key);
		for (int i = 1; i < diagonal; i++) {
			//for (int ang = 0; ang < 4; ang++) {
			key = aFinder.generateKey(0, i);
			aLine = (VirtualLine)allLines.get(key);
			if (aLine != null) {
				if (aLine.getNoOfPrims() > 0) {
					if (aLine.getLength() >= minVAxisLength && (double)aLine.getSize()/(double)aLine.getLength() > 0.80) {
						vLine = aLine;
						foundV = true;
//DLC
//System.out.println("foundV");
					}
				}
			}
			//}
			/************
			for (int ang = 180; ang > 176; ang-) {
				if (allLines[ang][i].getNoOfPrims() > 0) {
					aLine = allLines[0][i];
					if (aLine.getLength() >= minVAxisLength && (double)aLine.getSize()/(double)aLine.getLength() > 0.80) {
						vLine = aLine;
						foundV = true;
					}
				}
			}
			**************/
			if (foundV)
				break;
		}
		for (int i = diagonal-1; i > 0; i--) {
			//for (int ang = 87; ang < 94; ang++) {
			key = aFinder.generateKey(90, i);
			aLine = (VirtualLine)allLines.get(key);
			if (aLine != null) {
				//if (allLines[90][i].getNoOfPrims() > 0) {
				//aLine = allLines[90][i];
				if (aLine.getNoOfPrims() > 0) {
//System.out.println("minlen= " + minHAxisLength + " linelen= " + aLine.getLength() + " linesize= " + aLine.getSize());
//System.out.println("beginPoint= " + aLine.getBeginPoint() + " endpoint " + aLine.getEndPoint());
					if (aLine.getLength() >= minHAxisLength && (double)aLine.getSize()/(double)aLine.getLength() > 0.80) {
						hLine = aLine;
						foundH = true;
//DLC
//System.out.println("foundH "+minHAxisLength);
//System.out.println(aLine);
					}
				}
			}
			//}
			if (foundH)
				break;
		}
		Axis aVAxis = new Axis(Axis.VERTICAL_AXIS);
		Axis aHAxis = new Axis(Axis.HORIZONTAL_AXIS);
		if (foundV) {
			//Go through all the primitives in vLine
			int count = 0;
			Primitive aPrim;
			LinkedList prims = vLine.getPrimitives();
			ListIterator lItr = prims.listIterator(0);
			while (lItr.hasNext()) {
				aPrim = (Primitive)lItr.next();
				if (count == 0) {
					aVAxis.setRegionNo(aPrim.getParent());
					aVAxis.setColor(allRegions[aPrim.getParent()].getColor());
				}
				count++;
				aPrim.setPartOfAxis(1);
				aVAxis.addLinePrimitive(aPrim);
				addTicks(aVAxis, aPrim);
				PointPixel bPoint = vLine.getBeginPoint();
				PointPixel ePoint = vLine.getEndPoint();
			}
			aVAxis.setBeginPoint(vLine.getBeginPoint());
			aVAxis.setEndPoint(vLine.getEndPoint());
			extendLine(aVAxis, vLine.getBeginPrim(), vLine.getBeginPoint());
			extendLine(aVAxis, vLine.getEndPrim(), vLine.getEndPoint());
		}
		if (foundH) {
			//Go through all the primitives in hLine
			int count = 0;
			Primitive aPrim;
			LinkedList prims = hLine.getPrimitives();
			ListIterator lItr = prims.listIterator(0);
			while (lItr.hasNext()) {
				aPrim = (Primitive)lItr.next();
				if (count == 0) {
					aHAxis.setRegionNo(aPrim.getParent());
					aHAxis.setColor(allRegions[aPrim.getParent()].getColor());
				}
				aPrim.setPartOfAxis(1);
				aHAxis.addLinePrimitive(aPrim);
				addTicks(aHAxis, aPrim);
			}
//DLC
//System.out.println("count = " + count);
			aHAxis.setBeginPoint(hLine.getBeginPoint());
			aHAxis.setEndPoint(hLine.getEndPoint());
//DLC
//System.out.println("begin " + aHAxis.getBeginPoint());
//System.out.println("end " + aHAxis.getEndPoint());
			extendLine(aHAxis, hLine.getBeginPrim(), hLine.getBeginPoint());
			extendLine(aHAxis, hLine.getEndPrim(), hLine.getEndPoint());
		}
                else { // look for virtual horizontal axis
                }

System.out.println("about to call checkAxes from findAxes");
		boolean isAxis = checkAxes(aVAxis, aHAxis);
// /* DLC
		if (isAxis) {
// */
			System.out.println("Found axes");
			horizontalAxis = aHAxis;
			verticalAxis = aVAxis;
			horizontalAxis.setBoundingBox();
System.out.println("verticalAxis before bounding "+verticalAxis);
			verticalAxis.setBoundingBox();
System.out.println("verticalAxis after bounding "+verticalAxis);
			return true;
// /* DLC
		}
		System.out.println("No axes");
		return false;
// */
	}


	/**
	 * Adds the tickmarks connected to the given primitive of the given axis.
	 * All points of the primitive are checked to find primitives that are 
	 * short and perpendicular to it. The size of a tick mark needs to 
	 * be smaller than a threshold value, which is set to be 10. (30 now)
	 * These primitives are added to the axis as ticks.
	 *
	 * @param anAxis
	 * @param aPrim 
	 */
	private void addTicks(Axis anAxis, Primitive aPrim) {
		//System.out.println("In addTicks");
		LinkedList primList;
		ListIterator plItr;
		Primitive anotherPrim;
		PixelData entry;
		PointPixel aPoint;
		int tag2, labelNo, position;
		int tag = aPrim.getTagNo();
		int label = aPrim.getParent();
		Region aRegion = allRegions[label];
		LinkedList alist = aPrim.getAllPoints();
		ListIterator lItr = alist.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			primList = allPixels.getPixelDataWithNeigbors(aPoint.getRow(), aPoint.getColumn());
			plItr = primList.listIterator(0);
			while (plItr.hasNext()) {
				//System.out.println("Getting an entry");
				entry = (PixelData)plItr.next();
				tag2 = entry.getTag();
				labelNo = entry.getLabel();
				position = entry.getPosition();
				//System.out.println("Entry: "+entry);
				if (labelNo == label && tag2 != tag && position != entry.INTERIORPOINT) { 
					anotherPrim = aRegion.getPrimitive(tag2);
					if (anotherPrim.isPerpendicularTo(aPrim) && anotherPrim.getSize() <= 30) { //Possible tick mark
						//System.out.println("Region "+label+": Primitive "+tag2+" is short and perpendicular to primitive "+tag+". Adding "+tag2+" to "+tag);
						anAxis.addTick(anotherPrim);
						//anotherPrim.setPartOfAxis(1);
						anotherPrim.setPartOfTick(1);
					}
				}
			}
		}
	}

	/**
	 * The primitive in the given axis can be extended by another primitive
	 * that is not identified as part of an axis. The primitives that
	 * begin or end around the begin or end point of the given primitive
	 * are found and added to the axis if parallel to the given primitive.
	 * Tickmarks of the new primitive are added to the axis, too.
	 * extendLine is called recursively if more primitives can further extend
	 * the given primitive.
	 * 
	 * @param anAxis The axis the given primitive belongs to 
	 * @param aPrim The primitive to be extended
	 * @param aPoint The point at which the primitive is to be extended
	 */
	private void extendLine(Axis anAxis, Primitive aPrim, PointPixel aPoint) {
		int tag2, labelNo, index;
		int row = aPoint.getRow();
		int column = aPoint.getColumn();
		int tag = aPrim.getTagNo();
		int label = aPrim.getParent();
		PointPixel searchPoint;
		PixelData entry;
		LinkedList alist;
		ListIterator lItr;
		//System.out.println("Looking around "+aPoint);
		alist = (LinkedList)allPixels.getPixelDataWithNeigbors24(row, column);
		lItr = alist.listIterator(0);
		while (lItr.hasNext()) {
			//System.out.println("Getting an entry");
			entry = (PixelData)lItr.next();
			tag2 = entry.getTag();
			labelNo = entry.getLabel();
			index = entry.getPosition();
			//System.out.println("Entry: "+entry);
			//if (labelNo == label && tag2 != tag && index != entry.INTERIORPOINT) {
			if (tag2 != tag && index != entry.INTERIORPOINT) {
				//This point starts or ends a different primitive in the same region
				Primitive anotherPrim = allRegions[label].getPrimitive(tag2);
				//if (anotherPrim.isParallelTo(aPrim)) { //Add anotherPrim to the axis line
				if (anotherPrim.isPartOfAxis() == 0 && anotherPrim.isPartOfTick() == 0 && anotherPrim.isParallelTo(aPrim)) { //Add anotherPrim to the axis line
					//System.out.println("Adding primitive "+tag2+" to axis of primitive "+tag);
					anAxis.addLinePrimitive(anotherPrim);
					anotherPrim.setPartOfAxis(1);
					addTicks(anAxis, anotherPrim);
					if (index == PixelData.STARTPOINT) {
						searchPoint = anotherPrim.getEndPoint();
						extendLine(anAxis, anotherPrim, searchPoint);
					}
					else if (index == PixelData.ENDPOINT) {
						searchPoint = anotherPrim.getBeginPoint();
						extendLine(anAxis, anotherPrim, searchPoint);
					}
				}
			}
		} //end of traversing chains at one location
	}


	/**
	 * Checks if the two given axes meet and
	 * if the horizontal axis is at the bottom of the image and
	 * the vertical axis is at the left side.
	 * The end points of the two axes need to be in the lower left corner 
	 * are of the image and the distance between them need to be smaller
	 * than a threshold which is set to be 10.
	 *
	 * @param vAxis The vertical axis
	 * @param hAxis The horizontal axis
	 * @return True if the two axes are close enough, false if they are not
	 */
	private boolean checkAxes(Axis vAxis, Axis hAxis) {
		System.out.println("In checkAxes of AxesFinder");
		System.out.println("Horizontal axis: "+hAxis);
		System.out.println("Vertical axis: "+vAxis);
		if (hAxis.getSize() > 0) {
			PointPixel beginH = hAxis.getBeginPoint();
			PointPixel endH = hAxis.getEndPoint();
			PointPixel hPoint = beginH;
			if (endH.getColumn() < beginH.getColumn()) {
				hPoint = endH;
			}
                        if (vAxis.getSize() == 0) {  // create virtual axis
System.out.println("creating virtual vertical axis");
                           VirtualLine aLine;
                           LineFinder aFinder = new LineFinder();
                           int rb = hPoint.getRow();
                           int re = rb;
                           for (int i = 1; i < diagonal;i++){
                               int key = aFinder.generateKey(90, i);
                               aLine = (VirtualLine)allLines.get(key);
                               if (aLine != null) {
                                  rb = aLine.getBeginPoint().getRow();
                                  break;
                               }
                           }
                           int c = hPoint.getColumn() + 2;
                           vAxis.setBeginPoint(new PointPixel(rb,c));
                           vAxis.setEndPoint(new PointPixel(re,c));
                           vAxis.setLength();
                        }
			PointPixel beginV = vAxis.getBeginPoint();
			PointPixel endV = vAxis.getEndPoint();
			PointPixel vPoint = beginV;
			if (endV.getRow() > beginV.getRow()) {
				vPoint = endV;
			}
			//if (hPoint.getRow() > imageHeight*0.25 && hPoint.getColumn() < imageWidth*0.25 && vPoint.getRow() > imageHeight*0.25 && vPoint.getColumn() < imageWidth*0.25) {
			if (hPoint.getRow() > imageHeight*0.3 && hPoint.getColumn() < imageWidth*0.3 && vPoint.getRow() > imageHeight*0.3 && vPoint.getColumn() < imageWidth*0.3) {
				LineFitter aFitter = new LineFitter();
				double dist = aFitter.squareDistPointToPoint(hPoint, vPoint);
				//System.out.println("The square of the distance between axes is "+dist);
				if (dist <= 2000) { //dist is the square of the distance 
                                //above test must be refined; it puts a limit
                                // on the lengths of ticks at the origin of
                                // graph region.
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Creates a 2d array representation of an image that has the axes pixels
	 * as black and all others as white. 
	 *
	 * @param h The horizontal axis
	 * @param v The vertical axis
	 * @return The 2d array representation of an image of the axes
	 */
	public int[][] makeAxesImage(Axis h, Axis v) {
		LinkedList alist, primlist;
		Primitive aPrim;
		Point aPoint;
		ListIterator lItr, lItr2;
		int[][] axesImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				axesImage[i][j] = 255;
			}
		}
		alist = h.getPrimitiveList();
		lItr = alist.listIterator();
		while(lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			primlist = aPrim.getAllPoints();
			lItr2 = primlist.listIterator();
			while(lItr2.hasNext()) {
				aPoint = (Point)lItr2.next();
				axesImage[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
			}
		}
		alist = h.getTickList();
		lItr = alist.listIterator();
		while(lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			primlist = aPrim.getAllPoints();
			lItr2 = primlist.listIterator();
			while(lItr2.hasNext()) {
				aPoint = (Point)lItr2.next();
				axesImage[(int)aPoint.getX()][(int)aPoint.getY()] = 90;
			}
		}
		alist = v.getPrimitiveList();
		lItr = alist.listIterator();
		while(lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			primlist = aPrim.getAllPoints();
			lItr2 = primlist.listIterator();
			while(lItr2.hasNext()) {
				aPoint = (Point)lItr2.next();
				axesImage[(int)aPoint.getX()][(int)aPoint.getY()] = 150;
			}
		}
		alist = v.getTickList();
		lItr = alist.listIterator();
		while(lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			primlist = aPrim.getAllPoints();
			lItr2 = primlist.listIterator();
			while(lItr2.hasNext()) {
				aPoint = (Point)lItr2.next();
				axesImage[(int)aPoint.getX()][(int)aPoint.getY()] = 200;
			}
		}
		return axesImage;
	}
				
	/**
	 * Returns the horizontal axis found.
	 *
	 * @param none
	 * @return The horizontal axis
	 */
	public Axis getHorizontalAxis() {
		return horizontalAxis;
	}

	/**
	 * Returns the vertical axis found.
	 *
	 * @param none
	 * @return The vertical axis
	 */
	public Axis getVerticalAxis() {
System.out.println("getVerticalAxis = "+verticalAxis);
		return verticalAxis;
	}
}

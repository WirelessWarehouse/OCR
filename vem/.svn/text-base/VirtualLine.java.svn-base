import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.lang.Math;

/**
 * A class to hold information for the virtual lines of the image.
 * All lines that have the same angle and the distance from the origin
 * are considered part of the same virtual line.
 * A virtual line can be composed of primitives or regions.
 *
 * @author Chart Reading project
 * @version 1.0
 */

public class VirtualLine {
	private LinkedList primitives;
	private LinkedList regions;
	private PointPixel beginPoint;
	private PointPixel endPoint;
	private Primitive beginPrim;
	private Primitive endPrim;
	
	private int orientation;
	private double angle;
	private double slope;
	private double intercept;
	private int distance;
	private int noOfPixels;
	private int length;

	private boolean primitivesLine;
	private boolean regionsLine;
	private boolean isGridline;
	private boolean isDashedLine;

	/**
	 * Constructor.
	 *
	 * @param none
	 */
	public VirtualLine() {
		primitives = new LinkedList();
		regions = new LinkedList();
		noOfPixels = 0;
		isGridline = false;
		isDashedLine = false;
	}

	/**
	 * Constructor.
	 *
	 * @param aPrim A primitive that is part of the virtual line 
	 */
	public VirtualLine(Primitive aPrim) {
		primitives = new LinkedList();
		beginPoint = aPrim.getBeginPoint();
		endPoint = aPrim.getEndPoint();
		angle = aPrim.getAngle();
		orientation = aPrim.getLineOrientation();
		slope = aPrim.getSlope();
		intercept = aPrim.getIntercept();
		primitives.add(aPrim);
		noOfPixels = aPrim.getSize();
		distance = aPrim.getDistance();
		isGridline = false;
		isDashedLine = false;
		primitivesLine = true;
		regionsLine = false;
		beginPrim = aPrim;
		endPrim = aPrim;
	}

	/**
	 * Constructor.
	 *
	 * @param aRegion A region that is part of the virtual line 
	 * @param orient The orientation of the pixels of the region when a straight line is fitted; vertical or horizontal
	 * @param ang The angle of the pixels of the region when a straight line is fitted
	 */
	public VirtualLine(Region aRegion, int orient, double ang, int dist) {
		regions = new LinkedList();
		//beginPoint = aRegion.getBeginPoint();
		//endPoint = aRegion.getEndPoint();
		angle = ang;
		orientation = orient;
		distance = dist; 
		regions.add(aRegion);
		noOfPixels = aRegion.getNumPixels();
		isGridline = false;
		isDashedLine = false;
		primitivesLine = false;
		regionsLine = true;
	}

	/**
	 * Adds a primitive to the virtual line.
	 * 
	 * @param aPrim The primitive to be added 
	 */
	public void addPrimitive(Primitive aPrim) {
		if (getNoOfPrims() == 0) {
			primitivesLine = true;
			regionsLine = false;
			angle = aPrim.getAngle();
			orientation = aPrim.getLineOrientation();
			slope = aPrim.getSlope();
			intercept = aPrim.getIntercept();
			noOfPixels += aPrim.getSize();
			distance = aPrim.getDistance();
			beginPoint = aPrim.getBeginPoint();
			endPoint = aPrim.getEndPoint();
			if (orientation == Primitive.HORIZONTAL && endPoint.getColumn() < beginPoint.getColumn()) {
				beginPoint = aPrim.getEndPoint();
				endPoint = aPrim.getBeginPoint();
			}
			else if (orientation == Primitive.VERTICAL && endPoint.getRow() < beginPoint.getRow()) {
				beginPoint = aPrim.getEndPoint();
				endPoint = aPrim.getBeginPoint();
			}
			primitives.add(aPrim);
			beginPrim = aPrim;
			endPrim = aPrim;
		}
		else {
			setEndPoints(aPrim);
			noOfPixels += aPrim.getSize();
			angle = (angle + aPrim.getAngle())/2;
			slope = (slope + aPrim.getSlope())/2;
			intercept = (intercept + aPrim.getIntercept())/2;
			primitives.add(aPrim);
		}
	}

	/**
	 * Adds a region to the virtual line.
	 * 
	 * @param aRegion The region to be added 
	 * @param orient The orientation of the pixels of the region when a straight line is fitted; vertical or horizontal
	 * @param ang The angle of the pixels of the region when a straight line is fitted
	 * @param dist The distance of the pixels of the region from the origin when a straight line is fitted
	 */
	public void addRegion(Region aRegion, int orient, double ang, int dist) {
		if (regions.size() == 0) {
			primitivesLine = false;
			regionsLine = true;
		}
		angle = ang;
		orientation = orient;
		distance = dist; 
		regions.add(aRegion);
		noOfPixels += aRegion.getNumPixels();
	}

	/**
	 * Adds a region to the virtual line.
	 * 
	 * @param aRegion The region to be added 
	 */
	public void addRegion(Region aRegion) {
		if (regions.size() == 0) {
			primitivesLine = false;
			regionsLine = true;
		}
		regions.add(aRegion);
		noOfPixels += aRegion.getNumPixels();
	}


	/**
	 * Sets the end points of the virtual line when the 
	 * given primitive is added to it.
	 * 	 
	 * @param aPrim The primitive added to the virtual line that can change the end points of the virtual line 
	 */
	private void setEndPoints(Primitive aPrim) {
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
			beginPoint = minPoint;
			beginPrim = aPrim;
		}
		if (maxIndex == 2 || maxIndex == 3) {
			endPoint = maxPoint;
			endPrim = aPrim;
		}
	}


	/**
	 * Returns the begin point of the virtual line.
	 *
	 * @param none
	 * @return The begin point of the virtual line
	 */
	public PointPixel getBeginPoint() {
		return beginPoint;
	}

	/**
	 * Returns the end point of the virtual line.
	 *
	 * @param none
	 * @return The end point of the virtual line
	 */
	public PointPixel getEndPoint() {
		return endPoint;
	}

	/**
	 * Returns the first primitive that is at the beginning 
	 * point of the virtual line.
	 *
	 * @param none
	 * @return The primitive that is the beginning primitive of the virtual line
	 */
	public Primitive getBeginPrim() {
		return beginPrim;
	}

	/**
	 * Returns the last primitive that is at the end 
	 * point of the virtual line.
	 *
	 * @param none
	 * @return The primitive that is the ending primitive of the virtual line
	 */
	public Primitive getEndPrim() {
		return endPrim;
	}

	/**
	 * Sets the angle of the virtual line
	 * with respect to the positive row axis.
	 *
	 * @param a The angle of the virtual line
	 */
	public void setAngle(double a) {
		angle = a;
	}

	/**
	 * Returns the angle of the virtual line
	 * with respect to the positive row axis.
	 *
	 * @param none
	 * @return The angle of the virtual line
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Returns the distance of the virtual line from the origin.
	 *
	 * @param none
	 * @return The distance of the virtual line from the origin
	 */
	public int getDistance() {
		return distance;
	}

	/**
	 * Returns the slope of the virtual line.
	 *
	 * @param none
	 * @return The slope of the virtual line
	 */
	public double getSlope() {
		return slope;
	}

	/**
	 * Returns the row/column intercept of the virtual line.
	 *
	 * @param none
	 * @return The row/column intercept of the virtual line
	 */
	public double getIntercept() {
		return intercept;
	}

	/**
	 * Returns the list of primitives that make up the virtual line.
	 *
	 * @param none
	 * @return The linked list of primitives of the virtual line
	 */
	public LinkedList getPrimitives() {
		return primitives;
	}

	/**
	 * Returns the list of regions that make up the virtual line.
	 *
	 * @param none
	 * @return The linked list of regions of the virtual line
	 */
	public LinkedList getRegions() {
		return regions;
	}

	/**
	 * Returns the orientation of the virtual line; vertical or horizontal.
	 *
	 * @param none
	 * @return The orientation of the virtual line
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Sets the orientation of the virtual line; vertical or horizontal.
	 *
	 * @param o The orientation of the virtual line
	 */
	public void setOrientation(int o) {
		orientation = o;
	}

	/**
	 * Returns the number of primitives that make up the virtual line.
	 *
	 * @param none
	 * @return The number of primitives of the virtual line
	 */
	public int getNoOfPrims() {
		return primitives.size();
	}

	/**
	 * Returns the number of regions that make up the virtual line.
	 *
	 * @param none
	 * @return The number of regions of the virtual line
	 */
	public int getNoOfRegions() {
		return regions.size();
	}

	/**
	 * Returns the number of pixels that make up the virtual line.
	 *
	 * @param none
	 * @return The number of pixels of the virtual line
	 */
	public int getSize() {
		return noOfPixels;
	}

	/**
	 * Returns whether the virtual line is a gridline or not.
	 *
	 * @param none
	 * @return True if the virtual line is a gridline and false otherwise
	 */
	public boolean getIsGridline() {
		return isGridline;
	}

	/**
	 * Sets whether the virtual line is a gridline or not.
	 *
	 * @param b True if the virtual line is a gridline and false otherwise
	 */
	public void setIsGridline(boolean b) {
		isGridline = b;
	}

	/**
	 * Returns whether the virtual line is a dashed line or not.
	 *
	 * @param none
	 * @return True if the virtual line is a dashed line and false otherwise
	 */
	public boolean getIsDashedLine() {
		return isDashedLine;
	}

	/**
	 * Sets whether the virtual line is a dashed line or not.
	 *
	 * @param b True if the virtual line is a dashed line and false otherwise
	 */
	public void setIsDashedLine(boolean b) {
		isDashedLine = b;
	}

	/**
	 * Returns the total length of the virtual line
	 * from its beginning point to its end point.
	 *
	 * @param none
	 * @return The length of the virtual line
	 */
	public int getLength() {
		double dist = Math.sqrt((beginPoint.getRow()-endPoint.getRow())*(beginPoint.getRow()-endPoint.getRow()) + (beginPoint.getColumn()-endPoint.getColumn())*(beginPoint.getColumn()-endPoint.getColumn()));
		return (int)dist;
	}

	/**
	 * Returns a string holding information about the virtual line 
	 * to be printed on the screen.
 	 *
	 * @param none
	 * @return The string of information for the virtual line
	 */
	public String toString() {
	 	String message = new String("Line, ");
		if (orientation == Primitive.HORIZONTAL) {
			message = message + ("Horizontal, ");
		}
		else {
			message = message + ("Vertical, ");
		}
		if (primitivesLine) {
			message = message + (getNoOfPrims()+" primitives, "+noOfPixels+" pixels from "+getBeginPoint()+" to "+getEndPoint()+" length="+getLength()+", angle="+angle+", distance to origin="+distance+"\n");
		}
		if (regionsLine) {
			message = message + (getNoOfRegions()+" regions, "+noOfPixels+" pixels, angle="+angle+", distance to origin="+distance+"\n");
		}
		return message;
	}

}

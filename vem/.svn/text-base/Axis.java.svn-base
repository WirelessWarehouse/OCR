import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * A class to hold information for an axis.
 * 
 * @author Chart Reading project
 * @version 1.0
 */
public class Axis {
	public final static int NOT_AXIS=0;
	public final static int HORIZONTAL_AXIS=1;
	public final static int VERTICAL_AXIS=2;
	public final static int INCLINE_AXIS=3;

	private LinkedList linePrimitives;
	private int length;
	private LinkedList ticks;
	private int noOfTicks;
	private PointPixel endPoint1;
	private PointPixel endPoint2;

	private int orientation;
	private int height;
	private int width;
	private int regionLabelNo;
	private int color;
	private double inclineAngle; // w.r.t positive row direction (downward) from (0,0), i.e. top-left corner
	private PointPixel lowerRight;
	private PointPixel upperLeft;
	//private LinkedList endArrows;

	/**
	 * Constructor.
	 *
	 * @param orient The orientation of the axis; HORIZONTAL_AXIS or VERTICAL_AXIS
	 */
	public Axis(int orient) {
		linePrimitives = new LinkedList();
		ticks = new LinkedList();
		endPoint1 = null;
		endPoint2 = null;
		inclineAngle = 0;
		length = 0;
		noOfTicks = 0;
		orientation = orient;
		regionLabelNo = -1;
		color = -1;
	}

	/**
	 * Constructor.
	 *
	 * @param p A primitive that is part of the axis and that is to be added to the primitive list of the axis
	 */
	public Axis(Primitive p) {
		/***
		if( p.getType != Primitive.LINE_SEGMENT ) {
			throw Exception();
		}
		***/
		linePrimitives = new LinkedList();
		ticks = new LinkedList();
		endPoint1 = p.getBeginPoint();
		endPoint2 = p.getEndPoint();
		inclineAngle = p.getAngle();
		linePrimitives.add(p);
		length = p.getSize();
		noOfTicks = 0;
		regionLabelNo = p.getParent();
		color = -1;
	}

	/**
	 * Constructor.
	 *
	 * @param a Another axis that the current axis will be copied from
	 */
	public Axis(Axis a) {
		endPoint1 = a.getBeginPoint();
		endPoint2 = a.getEndPoint();
		linePrimitives = new LinkedList();
		ticks = new LinkedList();
		ListIterator lItr = (a.getPrimitiveList()).listIterator(0);
		if (lItr != null) {
			while(lItr.hasNext()) {
				Primitive tmpPrim = (Primitive)lItr.next();
				linePrimitives.add(tmpPrim);
				length = length + tmpPrim.getSize();
			}
		}
		ListIterator lItr2 = (a.getTickList()).listIterator(0);
		if( lItr != null ) {
			while (lItr.hasNext())
				ticks.add((Primitive)lItr.next());
		}
		inclineAngle = a.getInclineAngle();
		noOfTicks = a.getNoOfTicks();
		color = -1;
	}

	/**
	 * Constructor. If the two given primitives are parallel to 
	 * each other, only one is added to the primitive list of the axis.
	 * If they are perpendicular, the second primitive is added
	 * to the primitive list of the tick marks of the current axis.
	 * The incline angle of the axis is set to the incline angle of the 
	 * first or the second primitive or the average angle of the two
	 * primitives.
	 *
	 * @param p1 A primitive that is to be added to the axis
	 * @param p2 Another primitive that is to be added to the axis
	 */
	public Axis(Primitive p1, Primitive p2) {
		/***
		if( p2.getType != Primitive.LINE || p2.getType != Primitive.LINE ) {
			throw Exception();
		}
		if(Math.abs(p1.getAngle - p2.getAngle) >= 3 ) {
			throw Exception();
		}
		***/
		linePrimitives = new LinkedList();
		ticks = new LinkedList();
		color = -1;
		//How are the primitive related
		//1. Two primitives at 180 deg
		//2. Two primitives parallel--Not accepted
		//3. Two primitives at 90 deg
		if( Math.abs(p1.getAngle() - p2.getAngle()) <= 2 ) {
			linePrimitives.add(p1);
			setEndPoints(p1, p2);
			length  = length + p1.getSize() + p2.getSize();
			inclineAngle = (p1.getAngle() + p2.getAngle())/2;
		}
		else {
			if( p1.getSize() >= p2.getSize() ) {
				linePrimitives.add(p1);
				length  = length + p1.getSize();
				ticks.add(p2);
				noOfTicks = 1;
				inclineAngle = p1.getAngle();
			}
			else {
				linePrimitives.add(p2);
				length  = length + p2.getSize();
				ticks.add(p2);
				noOfTicks = 1;
				inclineAngle = p2.getAngle();
			}
		}
	}

	/**
	 * Adds a primitive to the current axis. The length and 
	 * the angle of the axis is updated.
	 *
	 * @param p1 A primitive that is to be added to the axis
	 * @return The modified <code>Axis</code>
	 */
	public Axis addLinePrimitive(Primitive p) {
		/***
		if( p2.getType != Primitve.LINE || p2.getType != Primitive.LINE ) {
			throw Exception();
		}
		if( Math.abs( p1.getAngle - p2.getAngle ) >= 3 ) {
			throw Exception();
		}
		***/
		if (linePrimitives.size() == 0) {
			endPoint1 = p.getBeginPoint();
			endPoint2 = p.getEndPoint();
			length = p.getSize();
			inclineAngle = p.getAngle();
			linePrimitives.add(p);
		}
		else {
			setEndPoints(this, p);
			length  = length + p.getSize();
			inclineAngle = (inclineAngle + p.getAngle())/2;
		}
		return this;
	}

	/**
	 * Adds a primitive to the current axis at the position given
	 * in the parameter. The length and 
	 * the angle of the axis is updated.
	 *
	 * @param p A primitive that is to be added to the axis
	 * @param index The position in the primitive list of the axis that the given primitive is to be added at
	 * @return The modified <code>Axis</code>
	 */
	public Axis addLinePrimitive(Primitive p, int index) {
		/***
		if( p2.getType != Primitive.LINE || p2.getType != Primitive.LINE ) {
			throw Exception();
		}
		if( Math.abs( p1.getAngle - p2.getAngle ) >= 3 ) {
			throw Exception();
		}
		***/
		setEndPoints(this, p);
		length = length + p.getSize();
		inclineAngle = (inclineAngle + p.getAngle())/2;
		return this;
	}

	/**
	 * Adds a tick mark to the current axis.
	 *
	 * @param p A primitive that is to be added to the axis as a tick mark
	 * @return The modified <code>Axis</code>
	 */
	public Axis addTick(Primitive p) {
		// if Tick is not at a certain angle throw an exception
		if (!ticks.contains(p)) {
			ticks.add(p);
			noOfTicks++;
		}
		return this;
	}

	/**
	 * Adds a tick mark to the current axis at the position given
	 * in the parameter. 
	 *
	 * @param p A primitive that is to be added to the axis as a tick mark
	 * @param index The position in the tick mark primitive list of the axis that the given primitive is to be added at
	 * @return The modified <code>Axis</code>
	 */
	public Axis addTick(Primitive p, int index) {
		ticks.add(index, p);
		noOfTicks++;
		return this;
	}

	/**
	 * Sets the bounding box of the axis together with its tick marks.
	 * The bounding box is stored as the upper left corner and the 
	 * lower right corner.
	 *
	 * @param none
	 */
	public void setBoundingBox() {
		int row, column;
		int minRow = -1;
		int maxRow = -1;
		int minColumn = -1;
		int maxColumn = -1;
		int count = 0;
		Primitive aPrim;
		LinkedList pointsList;
		PointPixel aPoint;
		ListIterator lItr, lItr2;
		for (int k = 0; k <= 1; k++) {
			if (k == 0) {	
				lItr = linePrimitives.listIterator(0);
			}
			else {
				lItr = ticks.listIterator(0);
			}
			while (lItr.hasNext()) {
				aPrim = (Primitive)lItr.next();
				pointsList = aPrim.getAllPoints();
				lItr2 = pointsList.listIterator(0);
				while (lItr2.hasNext()) {
					count++;
					aPoint = new PointPixel(lItr2.next());
					row = aPoint.getRow();
					column = aPoint.getColumn();
					if (count == 1) {
						minRow = row;
						maxRow = row;
						minColumn = column;
						maxColumn = column;
					}
					else {
						if (row < minRow) {
							minRow = row;
						}
						if (row > maxRow) {
							maxRow = row;
						}
						if (column < minColumn) {
							minColumn = column;
						}
						if (column > maxColumn) {
							maxColumn = column;
						}
					}
				}
			}
		}
                if (linePrimitives.size() > 0 || ticks.size() > 0) {
		    upperLeft = new PointPixel(minRow, minColumn);
		    lowerRight = new PointPixel(maxRow, maxColumn);}
                else { //virtual axis
                    upperLeft = endPoint1;
                    lowerRight = endPoint2;}
		height = lowerRight.getRow() - upperLeft.getRow() + 1;
		width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		/****
		orientation = VERTICAL;
		if (width > height) {
			orientation = HORIZONTAL;
		}
		****/
		if (width <= height) {
			//System.out.println("Bounding box: "+upperLeft+", "+lowerRight+", height="+height+", width="+width+", orientation=VERTICAL");
		}
		else {
			//System.out.println("Bounding box: "+upperLeft+", "+lowerRight+", height="+height+", width="+width+", orientation=HORIZONTAL");
		}
	}


	/**
	 * Adds the two given primitives to the primitive list
	 * of the axis. One primitive follows the other one in order. The
	 * begin and end points are modified accordingly.
	 *
	 * @param p1 The first primitive to be added to the primitive list of the axis 
	 * @param p2 The second primitive to be added to the primitive list of the axis 
	 */
	private void setEndPoints(Primitive p1, Primitive p2) {
		PointPixel p1e1, p1e2;
		PointPixel p2e1, p2e2;
		double p1e1_p2e1;
		double p1e1_p2e2;
		double p1e2_p2e1;
		double p1e2_p2e2;
		double minVal;
		p1e1 = p1.getBeginPoint();
		p1e2 = p1.getEndPoint();
		p2e1 = p2.getBeginPoint();
		p2e2 = p2.getEndPoint();
		//Square Distance p1e1 and p2e1
		p1e1_p2e1 = (p1e1.getRow() - p2e1.getRow())*(p1e1.getRow() - p2e1.getRow()) + (p1e1.getColumn() - p2e1.getColumn())*(p1e1.getColumn() - p2e1.getColumn());
		//Square Distance p1e1 and p2e2
		p1e1_p2e2 = (p1e1.getRow() - p2e2.getRow())*(p1e1.getRow() - p2e2.getRow()) + (p1e1.getColumn() - p2e2.getColumn())*(p1e1.getColumn() - p2e2.getColumn());
		//Square Distance p1e2 and p2e1
		p1e2_p2e1 = (p1e2.getRow() - p2e1.getRow())*(p1e2.getRow() - p2e1.getRow()) + (p1e2.getColumn() - p2e1.getColumn())*(p1e2.getColumn() - p2e1.getColumn());
		//Square Distance p1e2 and p2e2
		p1e2_p2e2 = (p1e2.getRow() - p2e2.getRow())*(p1e2.getRow() - p2e2.getRow()) + (p1e2.getColumn() - p2e2.getColumn())*(p1e2.getColumn() - p2e2.getColumn());
		minVal = Math.min(Math.min(p1e1_p2e1, p1e1_p2e2 ), Math.min(p1e2_p2e1, p1e2_p2e2));
		if( minVal == p1e1_p2e1 ) {
			endPoint1 = p1e2;				
			endPoint2 = p2e2;				
			//Rearrange Primitive 1
			linePrimitives.add(p1.invPrimitive());
			linePrimitives.add(p2);
		}
		else if(minVal == p1e1_p2e2) {
			endPoint1 = p1e2;				
			endPoint2 = p2e1;				
			//Rearrange Primitive 1 and 2
			linePrimitives.add(p1.invPrimitive());
			linePrimitives.add(p2.invPrimitive());
		}
		else if(minVal == p1e2_p2e1) {
			endPoint1 = p1e1;				
			endPoint2 = p2e2;				
			linePrimitives.add(p1);
			linePrimitives.add(p2);
		}
		else {
			//p1e2_p2e2:
			endPoint1 = p1e1;				
			endPoint2 = p2e1;				
			//Rearrange Primitive 2
			linePrimitives.add(p1);
			linePrimitives.add(p2.invPrimitive());
		}
	}

	/**
	 * Adds the given primitive to the primitive list
	 * of the axis according to the endpoints of the given axis.
	 *
	 * @param la The axis according to which the given primitive will be added to the current axis
	 * @param p The primitive to be added to the primitive list of the axis 
	 */
	private void setEndPoints(Axis la, Primitive p) {
		PointPixel p1e1, p1e2;
		PointPixel p2e1, p2e2;
		double p1e1_p2e1;
		double p1e1_p2e2;
		double p1e2_p2e1;
		double p1e2_p2e2;
		double minVal;
		p1e1 = la.getBeginPoint();
		p1e2 = la.getEndPoint();
		p2e1 = p.getBeginPoint();
		p2e2 = p.getEndPoint();
		//Square Distance p1e1 and p2e1
		p1e1_p2e1 = (p1e1.getRow() - p2e1.getRow())*(p1e1.getRow() - p2e1.getRow()) + (p1e1.getColumn() - p2e1.getColumn())*(p1e1.getColumn() - p2e1.getColumn());
		//Square Distance p1e1 and p2e2
		p1e1_p2e2 = (p1e1.getRow() - p2e2.getRow())*(p1e1.getRow() - p2e2.getRow()) + (p1e1.getColumn() - p2e2.getColumn())*(p1e1.getColumn() - p2e2.getColumn());
		//Square Distance p1e2 and p2e1
		p1e2_p2e1 = (p1e2.getRow() - p2e1.getRow())*(p1e2.getRow() - p2e1.getRow()) + (p1e2.getColumn() - p2e1.getColumn())*(p1e2.getColumn() - p2e1.getColumn());
		//Square Distance p1e2 and p2e2
		p1e2_p2e2 = (p1e2.getRow() - p2e2.getRow())*(p1e2.getRow() - p2e2.getRow()) + (p1e2.getColumn() - p2e2.getColumn())*(p1e2.getColumn() - p2e2.getColumn());
		minVal = Math.min(Math.min(p1e1_p2e1, p1e1_p2e2 ), Math.min(p1e2_p2e1, p1e2_p2e2));
		if (minVal == p1e1_p2e1) {
				endPoint1 = p2e2;
				//Rearrange Primitive 2
				linePrimitives.add(0, p.invPrimitive());
		}
		else if (minVal == p1e1_p2e2) {
			endPoint1 = p2e1;				
			linePrimitives.add(0, p);
		}
		else if (minVal == p1e2_p2e1) {
			endPoint2 = p2e2;				
			linePrimitives.add(p);
		}
		else {
			//p1e2_p2e2:
			endPoint2 = p2e1;				
			//Rearrange Primitive 2
			linePrimitives.add(p.invPrimitive());
		}
	}

	/**
	 * Returns the begin point pixel of the axis.
	 *
	 * @param none
	 * @return The begin point of the axis
	 */
	public PointPixel getBeginPoint() {
		return endPoint1;
	}

	/**
	 * Returns the end point pixel of the axis.
	 *
	 * @param none
	 * @return The end point of the axis
	 */
	public PointPixel getEndPoint() {
		return endPoint2;
	}

	/**
	 * Sets the begin point pixel of the axis.
	 *
	 * @param p The begin point of the axis 
	 */
	public void setBeginPoint(PointPixel p) {
		endPoint1 = p;
	}

	/**
	 * Sets the end point pixel of the axis.
	 *
	 * @param p The end point of the axis 
	 */
	public void setEndPoint(PointPixel p) {
		endPoint2 = p;
	}

	/**
	 * Returns the lower right corner point of the bounding box of the axis.
	 *
	 * @param none
	 * @return The lower right corner point of the axis
	 */
	public PointPixel getLowerRight() {
		return lowerRight;
	}

        public void setLowerRight(PointPixel p) {
                lowerRight = p;}

	/**
	 * Returns the upper left corner point of the bounding box of the axis.
	 *
	 * @param none
	 * @return The upper left corner point of the axis
	 */
	public PointPixel getUpperLeft() {
		return upperLeft;
	}

        public void setUpperLeft(PointPixel p) {
               upperLeft = p;}

	/**
	 * Returns the incline angle of the axis.
	 *
	 * @param none
	 * @return The incline angle of the axis
	 */
	public double getInclineAngle() {
		return inclineAngle;
	}

	/**
	 * Returns the list of primitives that make up the axis line.
	 *
	 * @param none
	 * @return The linked list of primitives for the current axis
	 */
	public LinkedList getPrimitiveList() {
		return linePrimitives;
	}

	/**
	 * Returns the list of primitives that make up the tick marks of the axis.
	 *
	 * @param none
	 * @return The linked list of primitives for the tick marks of the current axis
	 */
	public LinkedList getTickList() {
		return ticks;
	}

/*
*  sets ticks to a LinkedList; used to store revised tick list
*  after removing duplicate ticks
*/
        public void setTickList(LinkedList newTicks) {
            ticks = newTicks;}

	/**
	 * Returns the number of tick marks of the current axis.
	 *
	 * @param none
	 * @return The number of tick marks
	 */
	public int getNoOfTicks() {
		return noOfTicks;
	}

	/**
	 * Returns a new <code>Axis</code> the same as the current one.
	 *
	 * @param none
	 * @return The new <code>Axis</code>
	 */
	public Axis makeCopy() {
		Axis l = new Axis(this);
		return l;
	}

	/**
	 * Returns the number of pixels of the axis line.
	 *
	 * @param none
	 * @return The number of pixels of the axis line.
	 */
	public int getSize() {
		return length;
	}

	/**
	 * Returns the length of the axis line from one end point to 
	 * the other end point.
	 *
	 * @param none
	 * @return The length of the axis line.
	 */
	public int getLength() {
		if (getSize() > 0) {
			double dist = Math.sqrt((endPoint1.getRow()-endPoint2.getRow())*(endPoint1.getRow()-endPoint2.getRow()) + (endPoint1.getColumn()-endPoint2.getColumn())*(endPoint1.getColumn()-endPoint2.getColumn()));
			return (int)dist;
		}
		return 0;
	}

	/**
	 * Sets the orientation of the axis; vertical or horizontal.
	 *
	 * @param o The orientation of the axis; VERTICAL_AXIS or HORIZONTAL_AXIS
	 */
	public void setOrientation(int o) {
		//Check for the boundary condition
		orientation = o;
	}

	/**
	 * Returns the orientation of the axis; vertical or horizontal.
	 *
	 * @param none
	 * @return The orientation of the axis; VERTICAL_AXIS or HORIZONTAL_AXIS
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Returns the label number of the region the axis is in.
	 *
	 * @param none
	 * @return The label of the axis's region
	 */
	public int getRegionNo() {
		return regionLabelNo;
	}

	/**
	 * Sets the label number of the region the axis is in.
	 *
	 * @param a The label of the axis's region 
	 */
	public void setRegionNo(int a) {
		regionLabelNo = a;
	}

	/**
	 * Sets the color value of the axis in the image.
	 *
	 * @param a The color value of the axis
	 */
	public void setColor(int a) {
		color = a;
	}

	/**
	 * Returns a string holding information about the axis for
	 * printing on the screen.
	 *
	 * @param none
	 * @return The string for the axis information
	 */
	public String toString() {
		String message = new String("Axis in region "+regionLabelNo+": ");
		if (orientation == HORIZONTAL_AXIS) {
			message = message + ("Horizontal, ");
		}
		else if (orientation == VERTICAL_AXIS) {
			message = message + ("Vertical, ");
		}
		else if (orientation == INCLINE_AXIS) {
			message = message + ("Inclined, ");
		}
		message = message + (length+" pixels from "+getBeginPoint()+" to "+getEndPoint()+" with "+noOfTicks+" ticks, length = " + getLength() + ", incline angle =  "+inclineAngle+", color = "+color+"\n");	
		return message;
	}

    public void setLength() {
      if (endPoint1.getRow() == endPoint2.getRow())
         length = endPoint2.getColumn() - endPoint1.getColumn();
      else
         length = endPoint2.getRow() - endPoint1.getRow();}

}

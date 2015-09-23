import java.util.LinkedList;
import java.util.*;
import java.awt.Point;

/**
 * A class to hold information for a wedge in the chart image.
 *
 * @author Chart Reading project
 * @version 1.0
 */

public class Wedge{

	private Primitive wedgeCurve;	
	private LinkedList wedgeSides;
	private LinkedList wedgeCorners;
	private Primitive side1;
	private Primitive side2;
	private Primitive side3;

	private PointPixel joinPointOfSides;
	private double angleSubtendedAtJoinPoint;
	private double area;
	private int orientation;
	private int height;
	private int width;
	private int regionLabelNo;
	private int color;
	private final int VERTICAL = 0;
	private final int HORIZONTAL = 1;
	private boolean isFilledArea;
	private boolean isData;
	private PointPixel upperLeft;
	private PointPixel lowerRight;

	/**
	 * Constructor.
	 * If only the primitives of the sides are given, 
	 * the corner points are assigned as follows: 
	 *  <p>
	 *  		    		  * Point 1, begin point of primitive 1 <br>
	 *    		 		 	 | | <br>
	 * Primitive 3  |   | Primitive 1 <br>
	 *  				 	 |     | <br>
	 * 		 			  *-------* Point 2, begin point of primitive 2 <br>
	 *  				  Point 3, begin point of primitive 3 
	 *
	 * @param sides The linked list of the primitives that make up the sides of the wedge
	 * @param isFilled True if the wedge is a filled region and false otherwise
	 *
	 */
	public Wedge(LinkedList sides, boolean isFilled) {
		color = -1;
		isFilledArea = isFilled;
		isData = false;
		wedgeSides = new LinkedList();
		wedgeCorners = new LinkedList();
		ListIterator lItr = sides.listIterator(0);
		Primitive aPrim;
		PointPixel aPoint;
		while (lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			wedgeSides.add(aPrim);
			aPoint = aPrim.getBeginPoint();
			wedgeCorners.add(aPoint);
			regionLabelNo = aPrim.getParent();
		}
	}

	/**
	 * Sets the initial properties of the wedge and 
	 * determines whether the object is a wedge.
	 * 
	 * @param none
	 * @return True if the object is a wedge and false otherwise
	 */
	public boolean setWedge() {
		setBoundingBox();
		boolean isWedge = setCenter();
		if (isWedge) {
			setAngle();
		}
		return isWedge;
	}

	/**
	 * Constructor.
	 * First and second corner belong to first primitive,
	 * Second and third corner belong to second primitive,
	 * Third and first corner belong to third primitive.
	 * Or if there are two primitives: 
	 * First and second corner belong to first primitive,
	 * Second and first corner belong to second primitive.
	 * There can be more than 3 primitives,
	 * one side can be made up of more than one primitive.
	 *
	 * @param sides The linked list of the primitives that make up the sides of the wedge
	 * @param corners The linked list of the corner points of the wedge
	 * @param isFilled True if the wedge is a filled region and false otherwise
	 */
	public Wedge(LinkedList sides, LinkedList corners, boolean isFilled) {
		color = -1;
		isFilledArea = isFilled;
		isData = false;
		wedgeSides = new LinkedList();
		ListIterator lItr = sides.listIterator(0);
		Primitive aPrim;
		while (lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			wedgeSides.add(aPrim);
			regionLabelNo = aPrim.getParent();
		}
		wedgeCorners = new LinkedList();
		lItr = corners.listIterator(0);
		PointPixel aPoint;
		while (lItr.hasNext()) {
			aPoint =  new PointPixel(lItr.next());
			wedgeCorners.add(aPoint);
		}
	}

	/**
	 * Sets the bounding box of the wedge. The
	 * bounding box is stored as the upper left corner point
	 * and the lower right corner point.
	 * The height and the width of the wedge are set
	 * as the height and the width of the bounding box.
	 *
	 * @param none
	 */
	private void setBoundingBox() {
		int row, column;
		int minRow = -1;
		int maxRow = -1;
		int minColumn = -1;
		int maxColumn = -1;
		int count = 0;
		Primitive aPrim;
		LinkedList pointsList;
		PointPixel aPoint;
		ListIterator lItr2;
		ListIterator lItr = wedgeSides.listIterator(0);
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
		upperLeft = new PointPixel(minRow, minColumn);
		lowerRight = new PointPixel(maxRow, maxColumn);
		height = lowerRight.getRow() - upperLeft.getRow() + 1;
		width = lowerRight.getColumn() - upperLeft.getColumn() + 1;
		orientation = VERTICAL;
		if (width > height) {
			orientation = HORIZONTAL;
		}
		if (orientation == VERTICAL) {
			//System.out.println("Bounding box: "+upperLeft+", "+lowerRight+", height="+height+", width="+width+", orientation=VERTICAL");
		}
		else {
			//System.out.println("Bounding box: "+upperLeft+", "+lowerRight+", height="+height+", width="+width+", orientation=HORIZONTAL");
		}
	}


	/**
	 * Given the primitive list and the corner list,
	 * finds the center point and the angle the wedge spans.
	 * <p>
	 * If there are 3 primitives and 3 corners,
	 * checks the line segments of the primitives given by the corners.
	 * Either 2 lines and a curve or two lines and a shorter line.
	 * The angle is between the two lines, on the same side as the third one
	 * <p>
	 * If there are 2 primitives and 2 corners,
	 * tries to break the two segments defined by the corner points into
	 * smaller line segments.
	 * The one that is broken into more line segments is the curved part.
	 * The other one is the line part; can be one line or two lines
	 * If one line, angle is 180 degrees, otherwise angle is between its
	 * two segments.
	 * <p>
	 * An alternative: try to fit lines, 
	 * the one with more error should be the curved part.
	 * Then, how to calculate the angle - gets the point farther from the line
	 * defined by its endpoints; that should be the center. Calculate the angle
	 * between the two parts of that line divided at the center.
	 *
	 * @param none
	 * @return True if the center is set and the object is a wedge and false otherwise
	 */
	private boolean setCenter() {
		boolean isWedge = false;
		if (wedgeSides.size() == 3) {
			isWedge = setCenter(wedgeSides);
		}
		else if (wedgeSides.size() == 2) {
			LinkedList sidesList = new LinkedList();
			LinkedList pointsList;
			LinkedList[] breakPointsList = new LinkedList[2];
			LineFitter aLineFitter = new LineFitter();
			//System.out.println("Wedge has 2 sides");
			Primitive aPrim;
			for (int i = 0; i < 2; i++) {
				aPrim = (Primitive)wedgeSides.get(i);
				pointsList = aPrim.getAllPoints();
				breakPointsList[i] = new LinkedList();
				aLineFitter.splitSegment(pointsList, breakPointsList[i]);
				//System.out.println("Primitive "+aPrim.getTagNo()+" is split at "+breakPointsList[i].size()+" points" );
				/*****
				ListIterator lItr2 = breakPointsList.listIterator();
				while (lItr2.hasNext()) {
					//System.out.println(new PointPixel(pointsList.get(((Integer)lItr2.next()).intValue())));
				}
				****/
			}
			if (breakPointsList[0].size() > 0 || breakPointsList[1].size() > 0) {
			PointPixel breakPoint;
			if (breakPointsList[0].size() < breakPointsList[1].size()) {
				//breakPoint = ((Integer)breakPointsList[0].getFirst()).intValue();
				breakPoint = (PointPixel)breakPointsList[0].getFirst();
				sidesList.add((Primitive)wedgeSides.get(1));
				aPrim = (Primitive)wedgeSides.get(0);
				pointsList = aPrim.getAllPoints();
			}
			else {
				//breakPoint = ((Integer)breakPointsList[1].getFirst()).intValue();
				breakPoint = (PointPixel)breakPointsList[1].getFirst();
				sidesList.add((Primitive)wedgeSides.get(0));
				aPrim = (Primitive)wedgeSides.get(1);
				pointsList = aPrim.getAllPoints();
			}
			LinkedList firstList = new LinkedList();
			LinkedList secondList = new LinkedList();
			Point aPoint;
			int count = -1;
			int breakCount = pointsList.size();
			ListIterator lItr = pointsList.listIterator(0);
			while (lItr.hasNext()) {
				count++;
				aPoint = (Point)lItr.next();
				if ((int)aPoint.getX() == breakPoint.getRow() && (int)aPoint.getY() == breakPoint.getColumn()) {
					breakCount = count;
				}
				if (count < breakCount) {
					firstList.add(aPoint);
				}
				else {
					secondList.add(aPoint);
				}
			}
			//System.out.println("New primitive 1");
			Primitive firstPrim = new Primitive(firstList, -1);
			firstPrim.fitLine();
			//System.out.println("New primitive 2");
			Primitive secondPrim = new Primitive(secondList, -1);
			secondPrim.fitLine();
			sidesList.add(firstPrim);
			sidesList.add(secondPrim);
			isWedge = setCenter(sidesList);
			}
			else {
				isWedge = false;
			}
		}	
		return isWedge;
	}


	/**
	 * Given the primitive list of the sides of the wedge,
	 * finds the center point of the wedge.
	 * The primitive list needs to have exactly three elements,
	 * for the three sides of the wedge.
	 * 
	 * @param allSides The linked list of the side primitives of the wedge
	 * @return True if the given primitives form a wedge and the center point is found and false otherwise
	 */
	private boolean setCenter(LinkedList allSides) {
		int sides = 0;
		boolean isWedge = false;
		LinkedList aList;
		//Primitive side1, side2, side3, aPrim;
		Primitive aPrim;
		LineFitter aLineFitter = new LineFitter();
		if (allSides.size() == 3) {
			side1 = (Primitive)allSides.getFirst();
			side2 = side1;
			side3 = side1;
			ListIterator lItr = allSides.listIterator(0);
			while (lItr.hasNext()) {
				aPrim = (Primitive)lItr.next();
				if (sides == 0 && aPrim.isStraightLine()) {
					side1 = aPrim;
					sides++;
				}
				else if (sides == 1 && aPrim.isStraightLine()) {
					side2 = aPrim;
					sides++;
				}
				else if (sides == 2 && aPrim.isStraightLine()) {
					side3 = aPrim;
					if (side1.getSize() < side2.getSize() && side1.getSize() < aPrim.getSize()) {
						side3 = side1;
						side1 = aPrim;
					}
					else if (side2.getSize() < side1.getSize() && side2.getSize() < aPrim.getSize()) {
						side3 = side2;
						side2 = aPrim;
					}
					sides++;
				}
				else if (!aPrim.isStraightLine()) {
					side3 = aPrim;
				}
			}	
			//Find the intersection point of side1 and side2
			joinPointOfSides = aLineFitter.getIntersectionPoint(side1.getLineOrientation(), side1.getSlope(), side1.getIntercept(), side2.getLineOrientation(), side2.getSlope(), side2.getIntercept());
			//System.out.println("Wedge center="+joinPointOfSides);
			isWedge = true;
		}
		return isWedge;
	}


	/**
	 * Calculates the angle that the wedge spans.
	 * Angle of a side is from the row axis -the vertical axis, going counter clockwise.
	 * Angle can be the difference in angles of the two primitives, 180 - diff, 180 + diff or 360 - diff.
	 * Center is known, the corners are known. 
	 * Finds two vectors from the center and calculate the angle. 
	 * How do you know if the wedge angle is that angle or it is 360-that angle?
	 * Given the angle and the length of the two sides, the length of the
	 * third side needs to be around 2*pi*r*angle/360 
	 * where r is the average length of the two sides.
	 * Given the angle and the length of the two sides, the area of the
	 * wedge needs to be around pi*r*r*angle/360 
	 * where r is the average length of the two sides.
	 * Selects the angle that gives the minimum difference between area and the calculated area of the wedge.
	 * The above does not work -pixel count of the area and the calculated are are very different. Gives wrong results.
	 * <p>
	 * Alternative: Finds two vectors; joinPoint to beginPoint of side3
	 * and joinPoint to endPoint of side3.
	 * Calculate the angle between these two vectors: <br>
	 * dotProduct = |vector1|*|vector2|*cos(theta) <br>
	 * The wedge angle can be theta or 360-theta!
	 * The half angle is calculated; the angle between vector1 and vector3
	 * which is the vector from the middle point of side3 to the joinPoint.
	 * The half angle is compared with the wedge angle to determine
	 * whether it is theta or 360-theta.
	 *
	 * @param none
	 */
	private void setAngle() {
		double vector1Row = (side3.getBeginPoint()).getRow() - joinPointOfSides.getRow();
		double vector1Column = (side3.getBeginPoint()).getColumn() - joinPointOfSides.getColumn();
		double vector2Row = (side3.getEndPoint()).getRow() - joinPointOfSides.getRow();
		double vector2Column = (side3.getEndPoint()).getColumn() - joinPointOfSides.getColumn();
		double vector1Length = Math.sqrt((vector1Row*vector1Row)+(vector1Column*vector1Column));
		double vector2Length = Math.sqrt((vector2Row*vector2Row)+(vector2Column*vector2Column));
		double dotProduct = vector1Row*vector2Row+vector1Column*vector2Column;
		double cosAngle = dotProduct / (vector1Length*vector2Length);
		double angle = (Math.acos(cosAngle))*180/Math.PI;
		LinkedList side3Points = side3.getAllPoints();
		PointPixel middleSide3 = new PointPixel(side3Points.get(side3Points.size()/2));
		//System.out.println("Middle point of the arc is "+middleSide3);
		double vector3Row = middleSide3.getRow() - joinPointOfSides.getRow();
		double vector3Column = middleSide3.getColumn() - joinPointOfSides.getColumn();
		double vector3Length = Math.sqrt((vector3Row*vector3Row)+(vector3Column*vector3Column));
		double dotProduct2 = vector1Row*vector3Row+vector1Column*vector3Column;
		double cosHalfAngle = dotProduct2 / (vector1Length*vector3Length);
		double halfAngle = (Math.acos(cosHalfAngle))*180/Math.PI;
		double minDiff = Math.abs(halfAngle*2 - angle);
		angleSubtendedAtJoinPoint = angle;
		if (Math.abs(halfAngle*2 - (360-angle)) < minDiff) {
			angleSubtendedAtJoinPoint = 360 - angle;
		}
		//System.out.println("Angle is "+angle+" or "+(360-angle)+", half angle is "+halfAngle);
		//System.out.println("Wedge angle="+angleSubtendedAtJoinPoint+"o");

		/******
		double diffAngle, radius, length3, diff, minDiff, areaCalculated;
		double[] angles = new double[4];
		diffAngle = Math.abs(side1.getAngle() - side2.getAngle());
		angleSubtendedAtJoinPoint = diffAngle;
		angles[1] = 180 - diffAngle;
		angles[2] = 180 + diffAngle;
		angles[3] = 360 - diffAngle;
		radius = (side1.getLength() + side2.getLength()) / 2;
		//length3 =  side3.getLength();
		//radius = (side1.getSize() + side2.getSize()) / 2;
		//length3 =  side3.getSize();
		//minDiff = Math.abs((2*Math.PI*radius*diffAngle)/360 - length3);
		areaCalculated = (Math.PI*radius*radius*diffAngle)/360;
		minDiff = Math.abs(areaCalculated - area);
		for (int i = 1; i <= 3; i++) {
			diff = Math.abs((Math.PI*radius*radius*angles[i])/360 - area);
			if (diff < minDiff) {
				angleSubtendedAtJoinPoint = angles[i];
			}
		}
		areaCalculated = (Math.PI*radius*radius*angleSubtendedAtJoinPoint)/360;
		//System.out.println("Wedge angle="+angleSubtendedAtJoinPoint+"o, Wedge center="+joinPointOfSides+" Wedge area="+area+", Calculated area="+areaCalculated+", radius="+radius);
		******/
	}


	
	/**
	 * Constructor.
	 * 
	 * @param c The primitive defining the curved side of the wedge
	 * @param s1 The primitive defining one straight side of the wedge
	 * @param s2 The second primitive defining one straight side of the wedge
	 * @param p The point at the intersection of the two straight sides of the wedge 
	 * @param angle The angle that the wedge spans
	 */
	public Wedge(Primitive c, Primitive s1, Primitive s2, PointPixel p, double angle) {
		wedgeCurve = c;
		wedgeSides = new LinkedList();
		wedgeSides.add(s1);
		wedgeSides.add(s2);
		joinPointOfSides=p;
		angleSubtendedAtJoinPoint = angle;
	}

	/**
	 * Constructor.
	 * 
	 * @param c The primitive defining the curved side of the wedge
	 * @param s The primitive defining one straight side of the wedge
	 * @param p The point at the intersection of the two straight sides of the wedge 
	 * @param angle The angle that the wedge spans
	 */
	public Wedge(Primitive c, Primitive s, PointPixel p, double angle ) {
		wedgeCurve = c;
		wedgeSides = new LinkedList();
		wedgeSides.add(s);
		joinPointOfSides=p;
		angleSubtendedAtJoinPoint = angle;
	}

	/**
	 * Returns the curved side of the wedge.
	 * 
	 * @param none
	 * @return The primitive of the curved side of the wedge
	 */
	public Primitive getCurveEdge() {
		return wedgeCurve;
	}

	/**
	 * Returns the list of the primitives that define the sides of the wedge.
	 *
	 * @param none
	 * @return The linked list of the side primitives of the wedge
	 */
	public LinkedList getSides() {
		return wedgeSides;
	}

	/**
	 * Returns the label number of the region the wedge is in.
	 *
	 * @param none
	 * @return The label number of the wedge's region
	 */
	public int getRegionNo() {
		return regionLabelNo;
	}

	/**
	 * Sets the label number of the region the wedge is in.
	 *
	 * @param a The label number of the wedge's region
	 */
	public void setRegionLabelNo(int a) {
		regionLabelNo = a;
	}

	/**
	 * Sets whether the wedge is a filled area or not
	 *
	 * @param c True if the wedge is a filled area and false otherwise
	 */
	 public void setIsFilledArea(boolean c) {
		 isFilledArea = c;
	 }

	/**
	 * Returns whether the wedge is a filled area or not
	 *
	 * @param none
	 * @return True if the wedge is a filled area and false otherwise
	 */
	 public boolean getIsFilledArea() {
		 return isFilledArea;
	 }


	/**
	 * Sets whether the wedge is a part of a pie chart holding some data or not.
	 *
	 * @param c True if the wedge holds data for a pie chart and false otherwise
	 */
	 public void setIsData(boolean c) {
		 isData = c;
	 }

	/**
	 * Returns whether the wedge is a part of a pie chart holding some data or not.
	 *
	 * @param none
	 * @return True if the wedge holds data for a pie chart and false otherwise
	 */
	 public boolean getIsData() {
		 return isData;
	 }


	/**
	 * Returns the center point of the wedge where the two
	 * straight sides intersect.
	 *
	 * @param none
	 * @return The center point of the wedge
	 */
	 public PointPixel getCenter() {
		return joinPointOfSides;
	 }


	/**
	 * Returns the angle the wedge spans.
	 * The angle of the wedge is the angle between the 
	 * two straight sides of the wedge.
	 *
	 * @param none
	 * @return The angle of the wedge
	 */
	 public double getAngle() {
		return angleSubtendedAtJoinPoint;
	 }


	/**
	 * Returns the area covered by the wedge.
	 *
	 * @param none
	 * @return The area of the wedge
	 */
	public double getArea() {
		return area;
	}


	/**
	 * Sets the area covered by the wedge.
	 *
	 * @param a The area of the wedge
	 */
	public void setArea(double a) {
		area = a;
		//System.out.println("Wedge area="+area);
	}

	/**
	 * Returns the color value of the wedge.
	 * This is the color of the filled region if the wedge is filled and
	 * the color of the border line of the wedge if it is not filled.
	 *
	 * @param none
	 * @return The color value of the wedge
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Sets the color value of the wedge.
	 * This is the color of the filled region if the wedge is filled and
	 * the color of the border line of the wedge if it is not filled.
	 *
	 * @param c The color value of the wedge
	 */
	public void setColor(int c) {
		color = c;
	}

	/**
	 * Returns the upper left corner point of 
	 * the bounding box of the wedge.
	 *
	 * @param none
	 * @return The upper left corner point of the bounding box of the wedge
	 */
	 public PointPixel getUpperLeft() {
		return upperLeft;
	 }

	/**
	 * Returns the lower right corner point of 
	 * the bounding box of the wedge.
	 *
	 * @param none
	 * @return The lower right corner point of the bounding box of the wedge
	 */
	 public PointPixel getLowerRight() {
		return lowerRight;
	 }


	/**
	 * Returns a string holding information about the wedge
	 * for printing on the screen.
	 * 
	 * @param none
	 * @return The string holding the wedge information
	 */
	public String toString() {
		String message = ("Wedge in region "+regionLabelNo+": ");	
		if (isFilledArea) {
			message = message + ("Filled, ");
		}
		if (orientation == VERTICAL) {
			message = message + ("Vertical, ");
		}
		else if (orientation == HORIZONTAL) {
			message = message + ("Horizontal, ");
		}
		message = message + ("Angle="+angleSubtendedAtJoinPoint+"o, Center="+joinPointOfSides+", Area="+area+" pixels, Corners are ");	
		PointPixel aPoint;
		ListIterator lItr = wedgeCorners.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			message = (message + aPoint + " ");
		}
		message = (message + ", Color = "+color+"\n");
		return message;
	}
}

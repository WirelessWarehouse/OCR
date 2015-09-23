import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.lang.Math;
import java.awt.geom.*;

/**
 * A class with methods to fit straight line to 
 * a set of points, to calculate distances, etc.
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class LineFitter {

	private final int NOCHANGE = 0;
	private final int DECREASING = -1;
	private final int INCREASING = 1;

	private final int LEFT = 0;
	private final int RIGHT = 1;

/*
	LINE_SEGMENT = 2;
	ARC_SEGMENT = 3;
	CURVE_SEGMENT = 4;
	HORIZONTAL = 0;
	VERTICAL = 1;
*/

	/**
	 * Constructor.
	 * 
	 * @param none
	 */
	public LineFitter() {
	}

	/**
	 * Fits a line or a circle to a list of points, 
	 * determines which fit is better.
	 * 
	 * @param points The linked list of points to which a line or a circle will be fit 
	 * @return A vector that consists of the parameters of the line or the circle
	 */
	public Vector fitsLineOrArc(LinkedList points) {
		Point firstPoint = (Point)points.getFirst();
		Point lastPoint = (Point)points.getLast();
		Vector lineVector = fitStraightLine(points, firstPoint, lastPoint);
		if (((Boolean)lineVector.get(1)).booleanValue() == true)
			return lineVector;
		else {
			Vector circleVector = fitCircularArc(points, firstPoint, lastPoint);
			if (((Boolean)circleVector.get(1)).booleanValue() == true)
				return circleVector;
			}
		return lineVector;  //Fits a curve actually; doesn't fit a line or arc!
		}

	/**
	 * Fits a straight line to the list of points given. 
   * The line is not forced to pass through the endpoints.
	 * <p>
	 * X axis is positive direction of row, 
	 * Y axis is positive direction of columns. 
   *   --------> +ve column ie y-axis <br>
   *   | <br>
   *   | <br>
   *   | <br>
   *   | <br>
   *   V <br>
   *   +ve rows ie x-axis 
	 * <p>
   * Slope with positive direction of r axis.
   * Define a line: columns = m * rows + b. 
	 * If vertical: column = b (m=0). 
	 * If horizontal: row = b and m is infinite.
   * Here, if more vertical than horizontal: columns = m * rows + b, if vertical m=0.
   * If more horizontal than vertical: rows = m * columns + b, if horizontal m=0.
	 * Least squares error : e = sum(j=1 to n) (slope*xj + intercept -yj)^2.
	 * de/dslope = 0 and de/dintercept = 0 gives two equations:
	 * <p>
	 * |sum(xj^2)  sum(xj)| |slope    | = |sum(xj*yj)| <br>
	 * |sum(xj)    sum(1) | |intercept| = |sum(yj)   |
	 * <p>
	 * where all the sums are from 1 to n where n is the number of points. <br>
	 * sumx2*slope + sumx*intercept = sumxy <br>
	 * sumx*slope + noOfPoints*intercept = sumy <br>
	 * Solve for slope and intercept. <br>
	 * slope = (sumy - noOfPoints*intercept) / sumx <br>
	 * sumx2* ((sumy - noOfPoints*intercept) / sumx) + sumx*intercept = sumxy <br>
	 * sumx2*sumy/sumx - sumx2*noOfPoints*intercept/sumx +sumx*intercept = sumxy <br>
	 * intercept* (sumx - sumx2*noOfPoints/sumx) = sumxy - sumx2*sumy/sumx <br>
	 * intercept = (sumxy*sumx - sumx2*sumy) / (sumx^2 - sumx2*noOfPoints) <br>
	 * intercept = (sumx2*sumy - sumxy*sumx) / (sumx2*noOfPoints - sumx^2) <br>
	 * The best fit equation is: y = slope*x + intercept.
	 *
	 * @param points The linked list of points to which a line or a circle will be fit 
	 * @return A vector that consists of the parameters of the line (line orientation, slope and the row or column intercept)
   */
	public Vector fitStraightLineLSE(LinkedList points) {
		int x;
		int y;
		boolean exChange_x_y = false;
		Point p1 = (Point)points.getFirst();
		Point p2 = (Point)points.getLast();
		Point aPoint;
		double sumx = 0;
		double sumy = 0;
		double sumx2 = 0;
		double sumy2 = 0;
		double sumxy = 0;
		int noOfPoints = points.size();
		int x1 = (int)p1.getX();
		int y1 = (int)p1.getY();
		int x2 = (int)p2.getX();
		int y2 = (int)p2.getY();
		double slope, intercept, det;
		int lineOrientation = Primitive.VERTICAL;
		Vector line = new Vector(3);

		//Here, if more vertical than horizontal: columns = m * rows + b, if vertical m=0
		//If more horizontal than vertical: rows = m * columns + b, if horizontal m=0

		if (Math.abs(y1-y2) > Math.abs(x1-x2)) { //more horizontal than vertical. slope = x(rows)/y(columns) 
			exChange_x_y = true;
			lineOrientation = Primitive.HORIZONTAL;
			//slope = (float)(x2 - x1)/(float)(y2 - y1); //if horizontal, slope = 0
			//axisIntercept = x1 - slope * y1; 
		}
		else {	//more vertical than horizontal. slope = y(columns)/x(rows)
			//slope = (float)(y2 - y1)/(float)(x2 - x1); //if vertical, slope = 0
			//axisIntercept = y1 - slope * x1; 
		}

		ListIterator lItr = points.listIterator(0);
		while(lItr.hasNext()) {
			aPoint = (Point)lItr.next();
			x = (int)aPoint.getX();
			y = (int)aPoint.getY();
			sumx += x;
			sumy += y;
			sumxy += x*y;
			sumx2 += x*x;
			sumy2 += y*y;
		}
		//sumx2*slope + sumx*intercept = sumxy
		//sumx*slope + noOfPoints*intercept = sumy
		if (exChange_x_y) {
	  	//intercept = (sumy2*sumx - sumxy*sumy) / (sumy2*noOfPoints - sumy^2)
	  	//slope = (sumx - noOfPoints*intercept) / sumy
			det = (sumy2 * noOfPoints) - (sumy*sumy);
			intercept = (sumy2*sumx - sumxy*sumy) / det;
		}
		else {
	  	//intercept = (sumx2*sumy - sumxy*sumx) / (sumx2*noOfPoints - sumx^2)
	  	//slope = (sumy - noOfPoints*intercept) / sumx
			det = (sumx2 * noOfPoints) - (sumx*sumx);
			intercept = (sumx2*sumy - sumxy*sumx) / det;
		}
		slope = (sumxy*noOfPoints - sumx*sumy) / det;
		//System.out.println(noOfPoints+"points, Det: "+det+", slope: "+slope+", int: "+intercept);
		line.add(new Integer(lineOrientation));
		line.add(new Double(slope));
		line.add(new Double(intercept));
		return line;
	}

	/**
	 * Returns the properties of the straight line between two points
	 *
	 * @param p1 One endpoint of the straight line
	 * @param p2 The other endpoint of the straight line
	 * @return A vector that consists of the parameters of the line (line orientation, slope and the row or column intercept)
	 */ 
	public Vector getLine(PointPixel p1, PointPixel p2) {
		return getLine(new Point(p1.getRow(), p1.getColumn()), new Point(p2.getRow(), p2.getColumn()));

	}

	/**
	 * Returns the properties of the straight line between two points
	 *
	 * @param p1 One endpoint of the straight line
	 * @param p2 The other endpoint of the straight line
	 * @return A vector that consists of the parameters of the line (line orientation, slope and the row or column intercept)
	 */ 
	public Vector getLine(Point p1, Point p2) {
		double x1 = p1.getX();
		double y1 = p1.getY();
		double x2 = p2.getX();
		double y2 = p2.getY();
		double slope, intercept;
		int lineOrientation = Primitive.VERTICAL;
		Vector line = new Vector(3);

		//If more vertical than horizontal: columns = m * rows + b, if vertical m=0
		//If more horizontal than vertical: rows = m * columns + b, if horizontal m=0

		if (Math.abs(y1-y2) > Math.abs(x1-x2)) { //more horizontal than vertical. slope = x(rows)/y(columns) 
			lineOrientation = Primitive.HORIZONTAL;
			slope = (x2 - x1)/(y2 - y1); //if horizontal, slope = 0
			intercept = x1 - slope * y1; 
		}
		else {	//more vertical than horizontal. slope = y(columns)/x(rows)
			slope = (y2 - y1)/(x2 - x1); //if vertical, slope = 0
			intercept = y1 - slope * x1; 
		}
		line.add(new Integer(lineOrientation));
		line.add(new Double(slope));
		line.add(new Double(intercept));
		return line;
	}


	/**
	 * Calculates the angle of a line given the line orientation and the slope.
	 * The angle is from the row axis -the vertical axis, going counter clockwise.
	 *
	 * @param lineOrientation The orientation of the line (vertical or horizontal)
	 * @param slope The slope of the line
	 * @return The angle the line makes with the positive row axis
	 */ 
	public double getAngle(int lineOrientation, double slope) {
		double angle;
		if (lineOrientation == Primitive.HORIZONTAL) {
			angle = (90 - Math.toDegrees(Math.atan(slope)));
		}
		else {  //more vertical than horizontal. slope = y(columns)/x(rows)
			angle = Math.toDegrees(Math.atan(slope));
			if (angle < 0) 
				angle = 180 + angle;
		}
		return angle;
	}
			
	/**
	 * Calculates the angle of a line given the two end points of the line.
	 * The angle is from the row axis -the vertical axis, going counter clockwise.
	 *
	 * @param p1 One endpoint of the straight line
	 * @param p2 The other endpoint of the straight line
	 * @return The angle the line makes with the positive row axis
	 */ 
	public double getAngle(PointPixel p1, PointPixel p2) {
		Vector line = getLine(new Point(p1.getRow(), p1.getColumn()), new Point(p2.getRow(), p2.getColumn()));
		int lineOr = ((Integer)line.get(0)).intValue();
		double slope = ((Double)line.get(1)).doubleValue();
		return getAngle(lineOr, slope);
	}
			

	/**
	 * Calculates the orientation of a line given the two end points of the line.
	 * The angle is from the row axis -the vertical axis, going counter clockwise.
	 *
	 * @param p1 One endpoint of the straight line
	 * @param p2 The other endpoint of the straight line
	 * @return The orientation of the line; vertical or horizontal
	 */ 
	public int getOrientation(PointPixel p1, PointPixel p2) {
		Vector line = getLine(new Point(p1.getRow(), p1.getColumn()), new Point(p2.getRow(), p2.getColumn()));
		int lineOr = ((Integer)line.get(0)).intValue();
		return lineOr;
	}
			


	/**
	 * Calculates the error between the fitted straight line
	 * and the actual points.
	 *
	 * @param points The linked list of points 
	 * @param orientation The orientation of the fitted straight line (vertical or horizontal)
	 * @param slope The slope of the fitted straight line
	 * @param intercept The row or column intercept of the fitted straight line
	 * @return The position of the highest error point in the given linked list of points
	 */ 
	public int highestErrorPoint(LinkedList points, int orientation, double slope, double intercept) {
		double row, column, estColumn, estRow, error;
		double highestErrorRow = -1;
		double highestErrorColumn = -1;
		double cumError = 0;
		double maxError = 0;
		int count = -1;
		int highestErrorCount = -1;
		ListIterator lItr = points.listIterator(0);
		while(lItr.hasNext()) {
			count++;
			Point aPoint = (Point)lItr.next();
			row = aPoint.getX();
			column = aPoint.getY();

			if (orientation == Primitive.VERTICAL) {  //more vertical than horizontal. slope = y(column)/x(rows)
				estColumn = row*slope + intercept; 
				error = (column - estColumn)*(column - estColumn);
				if (count != 0 && count != points.size()-1 && error > maxError) {
					maxError = error;
					highestErrorRow= row;
					highestErrorColumn = column;
					highestErrorCount = count;
				}
			}
			else if (orientation == Primitive.HORIZONTAL) {  //more horizontal than vertical. slope = x(rows)/y(columns)
				estRow = column*slope + intercept; 
				error = (row - estRow)*(row - estRow);
				if (count != 0 && count != points.size()-1 && error > maxError) {
					maxError = error;
					highestErrorRow = row;
					highestErrorColumn = column;
					highestErrorCount = count;
				}
			}
		}
		return highestErrorCount;
	}


	/**
	 * Determines whether the given linked list of points for which 
	 * a straight line is fit form a straight line.
	 * The best fit line parameters are also given.
	 * The cumulative error is calculated. If the cumulative error
	 * is less than twice the number of points, than the points
	 * are a line. If the cumulative error is higher, the points
	 * do not form a line.
	 *
	 * @param points The linked list of points 
	 * @param orientation The orientation of the fitted straight line (vertical or horizontal)
	 * @param slope The slope of the fitted straight line
	 * @param intercept The row or column intercept of the fitted straight line
	 * @return True if the list of points form a straight line and false otherwise
	 */ 
	public boolean isLine(LinkedList points, int orientation, double slope, double intercept) {
		double row, column, estColumn, estRow, error;
		double cumError = 0;
		ListIterator lItr = points.listIterator(0);
		while(lItr.hasNext()) {
			Point aPoint = (Point)lItr.next();
			row = aPoint.getX();
			column = aPoint.getY();

			if (orientation == Primitive.VERTICAL) {  //more vertical than horizontal. slope = y(column)/x(rows)
				estColumn = row*slope + intercept; 
				error = (column - estColumn)*(column - estColumn);
				cumError += error;
			}
			else if (orientation == Primitive.HORIZONTAL) {  //more horizontal than vertical. slope = x(rows)/y(columns)
				estRow = column*slope + intercept; 
				error = (row - estRow)*(row - estRow);
				cumError += error;
			}
		}
		if ((int)(cumError) < (2 * points.size())) {
			return true;
		}
		return false;
	}


	/**
	 * Fits a straight line to the list of points given. 
   * The line is calculated based on the endpoints, so it passes through the endpoints.
	 * X axis is positive direction of row.
	 * Y axis is positive direction of columns.
	 * <p>
	 *   --------> +ve column ie y-axis <br>
   *   | <br>
   *   | <br>
   *   | <br>
   *   | <br>
   *   V <br>
   *   +ve rows ie x-axis
	 * <p>
   * Slope with positive direction of r axis.
   * Define a line: columns = m * rows + b. <br>
	 * If vertical: column = b (m=0). <br>
	 * If horizontal: row = b and m is infinite. <br>
   * Here, if more vertical than horizontal: columns = m * rows + b, if vertical m=0. <br>
   * If more horizontal than vertical: rows = m * columns + b, if horizontal m=0.
	 *
	 * @param points The linked list of points to which a straight line is fit
	 * @param p1 One endpoint of the points (and of the straight line)
	 * @param p2 The other endpoint of the points (and of the straight line)
	 * @return A vector that consists of the parameters of the line (line orientation, slope and the row or column intercept)
   */
	public Vector fitStraightLine(LinkedList points, Point p1, Point p2) {
		int x1, y1, x2, y2;
		int cur_x;
		int cur_y;
		boolean exChange_x_y = false;
		boolean is_line = false;
		float slope = 0, est_y, est_x; 
		float axisIntercept = 0;
		double angle_x = 0;
		float cum_sqd = 0;
		Vector line = new Vector(6);
		int lineOrientation = Primitive.VERTICAL;

		x1 = (int)p1.getX();
		y1 = (int)p1.getY();
		x2 = (int)p2.getX();
		y2 = (int)p2.getY();

		//Here, if more vertical than horizontal: columns = m * rows + b, if vertical m=0
		//If more horizontal than vertical: rows = m * columns + b, if horizontal m=0

		if (Math.abs(y1-y2) > Math.abs(x1-x2)) { //more horizontal than vertical. slope = x(rows)/y(columns) 
			exChange_x_y = true;
			slope = (float)(x2 - x1)/(float)(y2 - y1); //if horizontal, slope = 0
			axisIntercept = x1 - slope * y1; 
		}
		else {	//more vertical than horizontal. slope = y(columns)/x(rows)
			slope = (float)(y2 - y1)/(float)(x2 - x1); //if vertical, slope = 0
			axisIntercept = y1 - slope * x1; 
		}

		ListIterator lItr = points.listIterator(0);
		while(lItr.hasNext()) {
			Point cur_p = (Point)lItr.next();
			est_y = 0;
			cur_x = (int) cur_p.getX();
			cur_y = (int) cur_p.getY();

			if (exChange_x_y) {  //more horizontal than vertical. slope = x(rows)/y(columns)
				est_x = calcCoordLine(cur_y, slope, y1, x1);
				cum_sqd = cum_sqd + (est_x - cur_x) * (est_x - cur_x);
				//System.out.println("Est: " + est_x + "," + cur_y + " Cur: " + cur_x + "," + cur_y );
			}
			else {  //more vertical than horizontal. slope = y(columns)/x(rows)
				est_y = calcCoordLine(cur_x, slope, x1, y1); 
				cum_sqd = cum_sqd + (est_y - cur_y)*(est_y - cur_y);
				//System.out.println("Est: " + cur_x + "," + est_y + " Cur: " + cur_x + "," + cur_y );
			}

		}

		//System.out.println( "Line Sum of square of diff =: " + cum_sqd + " , Threshold =: " + (points.size()));
		if ((int)(cum_sqd ) < (2 * points.size()))
			is_line = true;

		if (exChange_x_y) {  //more horizontal than vertical. slope = x(rows)/y(columns)
			lineOrientation = Primitive.HORIZONTAL;
			if(slope < 0)
				angle_x = (90 + Math.toDegrees(Math.atan(slope)));
			else	
				angle_x = (90 - Math.toDegrees(Math.atan(slope)));
		}
		else {  //more vertical than horizontal. slope = y(columns)/x(rows)
			lineOrientation = Primitive.VERTICAL;
			angle_x = Math.toDegrees(Math.atan(slope));
		}
			
		//Define a line: columns = m * rows + b If vertical: column = b (m=0) If horizontal: row = b and m is infinite
		//Here, if more vertical than horizontal: columns = m * rows + b, if vertical m=0
		//If more horizontal than vertical: rows = m * columns + b, if horizontal m=0
		//System.out.println("Line with orientation: "+lineOrientation+", slope: "+slope+", intercept: "+axisIntercept);
		line.add(new Integer(Primitive.LINE_SEGMENT));
		line.add(new Boolean(is_line));
		line.add(new Integer(lineOrientation));
		line.add(new Double(slope));
		line.add(new Double(axisIntercept));
		line.add(new Double(angle_x));
		return line;
	}

	/** 
	 * Calculates the estimated value given the line equation.  
	 * m is the slope, x1 and y1 are coordinates of a point on the line,
	 * x1 is a given coordinate, and y1 is returned.
	 *
	 * @param x The x coordinate of a point for which the y coordinate is returned
	 * @param m The slope of the line
	 * @param x1 The x coordinate of a point on the line
	 * @param y1 The y coordinate of the same point on the line
	 * @return The y coordinate of the point whose x coordinate is given
	 */ 
	private float calcCoordLine(int x, float m, int x1, int y1) {
		return (m * (x - x1) + y1);
	}

  /** 
	 * Fits a circular arc to the list of points given.
	 * The method is not used in the current version, it
	 * was used in the first version of this project.
	 * What might be given : <br>
	 * <ol>
	 * <li>
	 * 1. Circle or near complete circle
	 * <li>
	 * 2. Arc of a circle of reasonable length
	 * <li>
	 * 3. A small arc of a circle, almost a straight line 
	 * </ol>
   *
	 * Can use any standard Circle Fitting Algorithm available.
	 * But this is the one used here:
	 * <ol>
	 * <li>
	 * 1. Given the end-points(x1,y1) and (x2,y2), find the coordinates of the 
	 *	  midpoint(x3, y3) and its length from one of the end points, l.
	 * <li>
	 * 2. Also find the coordinates of a point on the circle(x4, y4), such that 
	 *	   the distance of this point from the endpoints is approx. equal.
	 * <li>
	 * 3. The line drawn from the point found in step 1 to the point found
	 *	   in step 2 will pass thru the center of the circle.( if circle )
	 * <li>
	 * 4. Calc the distance between the two points, d.
	 * <li>
	 * 5. Calculate the radius and the approximate center.
	 *    If r is the radius, then we have the equation.
   *    r^2 = l^2 + (r-d)^2 .. Pythagoras theorem.
	 *    Solving, r = (d^2 + l^2)/ 2 * d.
	 * <li>
	 * 6. Having found r, find coordinates of center.
	 *   If  (x_c,y_c) is the center of the circle.
	 *   then center should lie on the line passing thru (x3,y3) and (x4,y4).
	 *   i.e y - y3 = (y3 - y4)/(x3 - x4)*( x - x3 ).
	 *   which gives y_c - y3 = (y3 - y4)/(x3 - x4) * (x_c - x3);   EQN 1
	 *   Also, (x_c - x1)^2 + (yc - y1)^2 = r^2                     EQN 2
	 *   Also, (x_c - x2)^2 + (yc - y2)^2 = r^2                     EQN 3
	 *   Solving EQN 1 ,EQN 2 and EQN 3 will give x_c and y_c
	 * </ol>
	 * 	Having found the center and radius, calculate
	 *	Summation((x_i - x_c )^2 + (y_i - y_c)^2 - r^2 )^2.
	 *	This summation should be less than a predefined threshold.
	 *  x_i and y_i are the points that are trying to fit as an arc.
	 * 
	 * @param points The linked list of points to which a circle or an arc will be fit
	 * @param p1 One end point of the linked list of points
	 * @param p2 The other end point of the linked list of points
	 * @return A vector consisting of the properties of the circle or the arc
	 */
	public Vector fitCircularArc(LinkedList points, Point p1, Point p2) {
		double x1,y1,x2,y2,x3,y3,x4,y4,x5,y5,x6,y6; 
		double x_c=0, y_c=0;
		double l, d, r=0; 
		Point cur_p;
		double cur_x, cur_y;
		double l1, l2;
		double cur_diff=0.0, prev_diff=0.0;
		double area_diff=0.0, sum_diff=0.0;
		double angle_x=0.0;
		double cosTheta=0;
		boolean is_circle=false;
		boolean alt_endpoint=false;;
		double saved_x2=0, saved_y2=0;
		Vector circle = new Vector(5);
		int fwd_index = 0;
		ListIterator lItrf=null;

		if (points.size() > 5) {
			x1 = (int) p1.getX();
			y1 = (int) p1.getY();
			x2 = (int) p2.getX();
			y2 = (int) p2.getY();

			if ((int)Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) ) < 6) {
				// End points are too close. Change one of the  endPoints 
				Point alt_p = (Point)points.get((int)(points.size()/2));
				saved_x2 = x2;
				saved_y2 = y2;
				x2 = (int) alt_p.getX(); 
				y2 = (int) alt_p.getY();
				alt_endpoint = true;
			}
			x3 = (x1 + x2)/2;
			y3 = (y1 + y2)/2;
			x4 = 0;		//Initialize
			y4 = 0;		//Initialize

			fwd_index = (int)(points.size()/3);
			lItrf = points.listIterator(fwd_index);
			while(lItrf.hasNext()) {
				cur_p = (Point)lItrf.next();
				cur_x = cur_p.getX();
				cur_y = cur_p.getY();

				l1 = Math.sqrt((x1 - cur_x) * (x1 - cur_x) + (y1 - cur_y) * (y1 - cur_y));
				l2 = Math.sqrt((x2 - cur_x) * (x2 - cur_x) + (y2 - cur_y) * (y2 - cur_y));
			
				cur_diff = Math.abs(l1 - l2);
				//System.out.println("CUR DIFF: " + cur_diff );

				//if (Math.abs(cur_diff - prev_diff) >= 0.5)
				if (Math.abs(cur_diff) >= 1.0) {
					//prev_diff = cur_diff;
					continue;
				}
				else {
					//Am I selecting the correct point
					x4 = cur_x;
					y4 = cur_y;
					lItrf=null;
					cur_p = null;
					cur_x = cur_y = 0;
					cur_diff = 0.0;
					break;
				}
			}

			double y3My4, x3Mx4, x3Mx2, y3My2;
			double y6My5, x4Mx2, x4Mx1, y4My2, y4My1;
			double x4Px1, x4Px2, y4Py1, y4Py2;

			//Find the radius
		 	x3Mx4 = x3 - x4;
		 	y3My4 = y3 - y4;
		 	x3Mx2 = x3 - x2;
		 	y3My2 = y3 - y2;
			d = Math.sqrt(x3Mx4 * x3Mx4 + y3My4 * y3My4);
			l = Math.sqrt(x3Mx2  * x3Mx2 + y3My2 * y3My2);
			//System.out.println("Circle: " + "L: " + l + " D: " + d );
			r = (d*d + l*l)/(2*d);

			//Find the center
			x5 = (x4 + x1)/2;
			y5 = (y4 + y1)/2;
			x6 = (x4 + x2)/2; 
			y6 = (y4 + y2)/2;
			y6My5 = y6 - y5;
			x4Mx2 = x4 - x2;
			x4Mx1 = x4 - x1;
			y4My2 = y4 - y2;
			y4My1 = y4 - y1;

			if (y4My2 == 0 && y4My1 == 0) {
				//Parallel lines
			}
			else {
				if (y4My1 == 0) {
					y_c = y5;
					if(x4Mx2 == 0)
						x_c = x6;
					else
						x_c = ((y6My5 * y4My2)/x4Mx2) + x6;
				}
				else if (y4My2 == 0) {
					y_c = y6;
					if(x4Mx1 == 0)
						x_c = x5;
					else
						x_c = (-1 * (y6My5 * y4My1)/x4Mx1) + x5;
				}
				else {
					x_c = (y6My5 + x6 * (x4Mx2/y4My2) - x5 * (x4Mx1/y4My1))/((x4Mx2/y4My2) - (x4Mx1/y4My1));
					y_c = -1 * (x4Mx1/y4My1) * (x_c - x5) + y5;
				}

		 		//No we have the approx center and the radius
				lItrf = points.listIterator(0);

				while(lItrf.hasNext()) {
					cur_p = (Point)lItrf.next();
					cur_x = (int)cur_p.getX();
					cur_y = (int)cur_p.getY();

					cur_diff = Math.sqrt((cur_x - x_c )*(cur_x - x_c ) + (cur_y - y_c)*(cur_y - y_c)) - r;
					//System.out.println("DIff: " + cur_diff );
					area_diff = cur_diff * cur_diff;
					sum_diff = sum_diff + area_diff;
				}


				//System.out.println("Circle: " + "Center: " + x_c + "," + y_c + " Radius: " + r );
				//System.out.println( "Circle Sum of square of diff =: " + sum_diff + " , Threshold =: " + (3 * points.size()));
				if ((int)(sum_diff ) < (5 * points.size()))
					is_circle = true;

				//Find the angle subtended by the arc at the centre
				//Which angle---obtuse or the acute
				//Using the cosine law
				//Cos A = (b^2 + c^2 - a^2) / 2bc
				if (alt_endpoint) {
					cosTheta = 1 - (((x1 - saved_x2) * (x1 - saved_x2) + (y1 - saved_y2) * (y1 - saved_y2))/(2*r*r)); 
				}
				else {
					cosTheta = 1 - (((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))/(2*r*r)); 
				}
				angle_x = Math.toDegrees(Math.acos(cosTheta));
				if (points.size() > (int) (2 * r)) {
					angle_x =  360 - angle_x;
				}
			}
		}

		//System.out.println("Circle: " + "Center: " + x_c + "," + y_c + " Radius: " + r + " COSTHT: " + cosTheta + " Angle: " + angle_x);
		circle.add(new Integer(Primitive.ARC_SEGMENT));
		circle.add(new Boolean(is_circle));
		circle.add(new Point((int)(x_c + 0.5), (int)(y_c + 0.5)));
		circle.add(new Double(r));
		circle.add(new Double(angle_x));
		return (circle);
	}

	/** 
	 * Splits a curved line into straight line segments.
	 * Checks if the given list of points form a straight line. If yes, does nothing.
	 * If not a straight line,
	 * finds the point at which the error is highest 
	 * -except the endpoints.
	 * Splits the chain into two at that point.
	 * Does the same for the two new chains. 
	 * The method is called recursively.
	 *
	 * @param points The linked list of points that are going to be split
	 * @param breakPoints The points at which the linked list of points is split at
	 */
	public void splitSegment(LinkedList points, LinkedList breakPoints) {
		Vector lineParam = getLine((Point)points.getFirst(), (Point)points.getLast());
		int type = ((Integer)lineParam.get(0)).intValue();
		double slope = ((Double)lineParam.get(1)).doubleValue();
		double intercept = ((Double)lineParam.get(2)).doubleValue();
		if (isLine(points, type, slope, intercept)) {
			PointPixel firstPoint = new PointPixel(points.getFirst());
			PointPixel lastPoint = new PointPixel(points.getLast());
			//System.out.println("Straight line from "+firstPoint+" to "+lastPoint);
		}
		else { //Find the highest error point, except the endpoints
			int breakCount = highestErrorPoint(points, type, slope, intercept);
			if (breakCount > 0) {
				PointPixel splitPoint = new PointPixel(points.get(breakCount));
				breakPoints.add(splitPoint);
				//breakPoints.add(new Integer(breakCount));
				//System.out.println("Split at point "+splitPoint);
				LinkedList firstList = new LinkedList();
				LinkedList secondList = new LinkedList();
				Point aPoint;
				int count = -1;
				ListIterator lItr = points.listIterator(0);
				while (lItr.hasNext()) {
					count++;
					aPoint = (Point)lItr.next();
					if (count < breakCount) {
						firstList.add(aPoint);
					}
//DLC
                                        else if (count == breakCount)
                                          {
                                          firstList.add(aPoint);
                                          secondList.add(aPoint);
                                          }
					else {
						secondList.add(aPoint);
					}
				}
				splitSegment(firstList, breakPoints);
				splitSegment(secondList, breakPoints);
			}
		}
	}


	/** 
	 * Splits a curved line into straight line segments.
	 * 
	 * @param points The linked list of points that are going to be split
	 */
	public void splitSegment2(LinkedList points) {
		int row, column;
		double est1 = 0;
		double est2 = 0;
		double sideLength2, dist2;
		double previousDist2 = 0;
		int trend = NOCHANGE;
		int previousTrend = NOCHANGE;
		int side = RIGHT;
		int previousSide = RIGHT;
		Point aPoint;
		PointPixel firstPixel = new PointPixel(points.getFirst());
		PointPixel lastPixel = new PointPixel(points.getLast());
		//Fit straight line to the primitive. Compute the distances of all the points in the chain to that straight line. When you find a local maximum, break the chain at that point.
		Vector lineParam = fitStraightLineLSE(points);
		//System.out.println("Got param");
		//Go through the whole chain, calculate the distance of each point to the best fit line.  
		int type = ((Integer)lineParam.get(0)).intValue();
		double slope = ((Double)lineParam.get(1)).doubleValue();
		double intercept = ((Double)lineParam.get(2)).doubleValue();
		//System.out.println("Chain is type: "+type+", from "+firstPixel+", to "+lastPixel +", slope="+slope+", int="+intercept);
		int count = 0;
		int newCount = 0;
		ListIterator lItr = points.listIterator(0);
		while (lItr.hasNext()) {
			count++;
			newCount++;
			aPoint = (Point)lItr.next();
			row = (int)aPoint.getX();
			column = (int)aPoint.getY();
			if (type == Primitive.VERTICAL) {  //slope = columns/rows
				est1 = row;
				est2 = (slope * row) + intercept;
				if (column >= est2) {
					side = RIGHT;
				}
				else if (column < est2) {
					side = LEFT;
				}
			}
			else if (type == Primitive.HORIZONTAL) {  //slope = rows/columns
				est2 = column;
				est1 = (slope * column) + intercept;
				if (row <= est1) {
					side = RIGHT;
				}
				else if (row > est1) {
					side = LEFT;
				}
			}
			sideLength2 = squareDistPointToPoint(row, column, est1, est2);  //square of the length
			dist2 = sideLength2 / (1 + (slope*slope));  //distance from (row, column) to the estimated straight line
			if (newCount > 1) {
				//if (Math.abs(dist2 - previousDist2) < 0.0001) {
				if (dist2 == previousDist2) {
					trend = NOCHANGE;
				}
				else if (dist2 > previousDist2) {
					trend = INCREASING;
				}
				else if (dist2 < previousDist2) {
					trend = DECREASING;
				}
			}
			if (side != previousSide && trend == DECREASING && (previousTrend == DECREASING || newCount == 2)) {
				trend = INCREASING;
			}
			//System.out.println("("+row+", "+column+"): side="+side+", trend="+trend+", count="+newCount);
			if (newCount > 2) {
				if ((side == previousSide && previousTrend != NOCHANGE && trend == NOCHANGE) || (side != previousSide && trend == NOCHANGE)) {
					aPoint = (Point)points.get(count-2);
					//aChain = breakChain(aChain, count-3); //index element stays in the old chain
					//System.out.println("Case 1: Trend change: "+previousTrend+" to "+trend+" Side change: "+previousSide+" to "+side+". New chain begin point: ("+(int)aPoint.getX() +", "+(int)aPoint.getY()+")");
					//lItr = points.listIterator(count);
					newCount = 2;
					//System.out.println("Continue with the new chain.");
				}
				else if ((side == previousSide && trend != previousTrend && trend != NOCHANGE) || ((side != previousSide) && (previousTrend == NOCHANGE || previousTrend == INCREASING))) {
					aPoint = (Point)points.get(count-1);
					//aChain = breakChain(aChain, count-2); //index element stays in the old chain
					//System.out.println("Case 2: Trend change: "+previousTrend+" to "+trend+" Side change: "+previousSide+" to "+side+". New chain begin point: ("+(int)aPoint.getX()+", "+(int)aPoint.getY()+")");
					//lItr = points.listIterator(count);
					newCount = 1;
					//System.out.println("Continue with the new chain.");
				}
			}
			previousSide = side;
			previousDist2 = dist2;
			previousTrend = trend;
			//System.out.println("Next point.");
		}
		//System.out.println("Splitting is done.");
	}	


	/** 
	 * Calculates the mid point of the straight line passing through
	 * the two points given.
	 *
	 * @param p1 One endpoint of the straight line 
	 * @param p2 The other endpoint of the straight line 
	 * @return The midpoint of the straight line
	 */
	public Point calcMidPoint(Point p1, Point p2) {
		Point midPoint=null;
		int x_mid;
		int y_mid;
		x_mid = (int)((p1.getX() + p2.getX())/2 + 0.5);
		y_mid = (int)((p1.getY() + p2.getY())/2 + 0.5);
		midPoint = new Point(x_mid, y_mid);
		return midPoint;
	}

	/** 
	 * Calculates the distance from the first point to the mid points 
	 * straight line passing through the next two points.
	 *
	 * @param p The point from which the distance to the mid point of the straight line is calculated
	 * @param p1 One end point defining the straight line
	 * @param p2 The other end point defining the straight line
	 * @return The distance from the given point to the straight line defined by the two given points
	 */
	public double distFromLine(Point p, Point p1, Point p2 ) {
		double distance;
		Point midPoint = calcMidPoint( p1, p2 );
		distance = Math.abs(p.distance(midPoint));
		return distance;
	}

	/** 
	 * Calculates the square of the distance between a point and a line. 
	 * distance^2 = a^2 / (1 + slope^2) <br>
	 * where a is the distance from the point (r1, c1). 
	 * to (slope*c1+intercept, c1) in the horizontal case and
	 * to (r1, slope*r1+intercept) in the vertical case.
	 *
	 * @param p1 The point from which the distance to the straight line is calculated 
	 * @param slope The slope of the straight line
	 * @param intercept The row or column intercept of the straight line
	 * @param orientation The orientation of the straight line (vertical or horizontal)
	 * @return The square of the distance from the given point to the given straight line
	 */
	public double squareDistPointToLine(Point p1, double slope, double intercept, int orientation) { 
		int row1 = (int)p1.getX();
		int column1 = (int)p1.getY();
		double distance = squareDistPointToLine(row1, column1, slope, intercept, orientation);
		return distance;
	}

	/** 
	 * Calculates the square of the distance between a point and a line. 
	 * distance^2 = a^2 / (1 + slope^2) <br>
	 * where a is the distance from the point (r1, c1). 
	 * to (slope*c1+intercept, c1) in the horizontal case and
	 * to (r1, slope*r1+intercept) in the vertical case.
	 *
	 * @param row The row number of the point from which the distance to the straight line is calculated 
	 * @param column The column number of the point from which the distance to the straight line is calculated 
	 * @param slope The slope of the straight line
	 * @param intercept The row or column intercept of the straight line
	 * @param orientation The orientation of the straight line (vertical or horizontal)
	 * @return The square of the distance from the given point to the given straight line
	 */
	public double squareDistPointToLine(int row, int column, double slope, double intercept, int orientation) { 
		//System.out.println("Orientation: "+orientation+": y = "+slope+"*x + "+intercept);
		double row1 = (double)row;
		double column1 = (double)column;
		double a = 0;
		if (orientation == Primitive.HORIZONTAL) { //slope = row/column
			a = squareDistPointToPoint(row1, column1, slope*column1+intercept, column1);
		}
		else if (orientation == Primitive.VERTICAL) { //slope = column/row
			a = squareDistPointToPoint(row1, column1, row1, slope*row1+intercept);
		}
		double distance = a / (1 + slope*slope);
		return distance;
	}

	/** 
	 * Calculates the square of the distance between a point and a line. 
	 * distance^2 = a^2 / (1 + slope^2) <br>
	 * where a is the distance from the point (r1, c1). 
	 * to (slope*c1+intercept, c1) in the horizontal case and
	 * to (r1, slope*r1+intercept) in the vertical case.
	 *
	 * @param r The row number of the point from which the distance to the straight line is calculated 
	 * @param c The column number of the point from which the distance to the straight line is calculated 
	 * @param pBegin One end point defining the straight line
	 * @param pEnd The other end point defining the straight line
	 * @return The square of the distance from the given point to the given straight line
	 */
	public double squareDistPointToLine(int r, int c, Point pBegin, Point pEnd) { 
		double row1 = (double)r;
		double column1 = (double)c;
		double rBegin = pBegin.getX();
		double cBegin = pBegin.getY();
		double rEnd = pEnd.getX();
		double cEnd = pEnd.getY();
		double a = 0;
		int lineOrientation = Primitive.VERTICAL;
		double slope = 0;
		double axisIntercept = 0;
		if (Math.abs(cBegin-cEnd) > Math.abs(rBegin-rEnd)) { //more horizontal than vertical. slope = rows/columns 
			lineOrientation = Primitive.HORIZONTAL;
			slope = (rEnd - rBegin)/(cEnd - cBegin); //if horizontal, slope = 0
			axisIntercept = rBegin - slope * cBegin; 
		}
		else {	//more vertical than horizontal. slope = columns/rows
			slope = (cEnd - cBegin) / (rEnd - rBegin); //if vertical, slope = 0
			axisIntercept = cBegin - slope * rBegin; 
		}
	  double distance = squareDistPointToLine((int)row1, (int)column1, slope, axisIntercept, lineOrientation);
		return distance;
	}

	/** 
	 * Calculates the square of the distance between a point and a line. 
	 * distance^2 = a^2 / (1 + slope^2) <br>
	 * where a is the distance from the point (r1, c1). 
	 * to (slope*c1+intercept, c1) in the horizontal case and
	 * to (r1, slope*r1+intercept) in the vertical case.
	 * The line is from p1 to p2.
	 *
	 * @param r The row number of the point from which the distance to the straight line is calculated 
	 * @param c The column number of the point from which the distance to the straight line is calculated 
	 * @param pBegin One end point defining the straight line
	 * @param pEnd The other end point defining the straight line
	 * @return The square of the distance from the given point to the given straight line
	 */
	public double squareDistPointToLine(int r, int c, PointPixel pBegin, PointPixel pEnd) { 
		Point pB = new Point(pBegin.getRow(), pBegin.getColumn());
		Point pE = new Point(pEnd.getRow(), pEnd.getColumn());
		double distance = squareDistPointToLine(r, c, pB, pE);
		return distance;
	}

	/** 
	 * Calculates the square of the distance between two points.
	 * 
	 * @param p1 The point from which the distance to the other point will be calculated
	 * @param p2 The other point
	 * @return The square of the distance between the two given points
	 */
	public double squareDistPointToPoint(PointPixel p1, PointPixel p2) { 
		double distance = ((p1.getRow() - p2.getRow()) * (p1.getRow() - p2.getRow())) + ((p1.getColumn() - p2.getColumn()) * (p1.getColumn() - p2.getColumn()));
		return distance;
	}

	/** 
	 * Calculates the square of the distance between two points.
	 * 
	 * @param p1 The point from which the distance to the other point will be calculated
	 * @param p2 The other point
	 * @return The square of the distance between the two given points
	 */
	public double squareDistPointToPoint(Point p1, Point p2) { 
		double distance = ((p1.getX() - p2.getX()) * (p1.getX() - p2.getX())) + ((p1.getY() - p2.getY()) * (p1.getY() - p2.getY()));
		return distance;
	}

	/** 
	 * Calculates the square of the distance between two points.
	 * 
	 * @param x1 The x coordinate of the first point
	 * @param y1 The y coordinate of the first point
	 * @param x2 The x coordinate of the second point
	 * @param y2 The y coordinate of the second point
	 * @return The square of the distance between the two given points
	 */
	public double squareDistPointToPoint(double x1, double y1, double x2, double y2) { 
		double distance = ((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2));
		return distance;
	}

	/** 
	 * Calculates the intersection point of two straight lines.
	 * The first line is defined by the first two points and the other line is
	 * defined by the last two PointPixels in the argument list.
	 *
	 * @param line1p1 A point on the first line
	 * @param line1p2 Another point on the first line
	 * @param line2p1 A point on the second line
	 * @param line2p2 Another point on the second line
	 * @return The point at which the given two lines intersect
	 */
	public PointPixel getIntersectionPoint(PointPixel line1p1, PointPixel line1p2, PointPixel line2p1, PointPixel line2p2) { 
		boolean isLine1Vertical=false;
		boolean isLine2Vertical=false;
		double line1r1 = line1p1.getRow();
		double line1c1 = line1p1.getColumn();
		double line1r2 = line1p2.getRow();
		double line1c2 = line1p2.getColumn();
		double line2r1 = line2p1.getRow();
		double line2c1 = line2p1.getColumn();
		double line2r2 = line2p2.getRow();
		double line2c2 = line2p2.getColumn();
		double slope1, slope2, intercept1, intercept2;
		double cIntersection = -1;
		double rIntersection = -1;

		if (line1c1 == line1c2)
			isLine1Vertical = true;
		if (line2c1 == line2c2)
			isLine2Vertical = true;

		if (isLine1Vertical && isLine2Vertical) { //Both lines are vertical
			if (line1c1 == line2c1) { //They are the same line 
				cIntersection = line1c1;
				rIntersection = line1r1;
			}
		}
		else if (isLine1Vertical && !isLine2Vertical) {
			slope2 = (line2r2 - line2r1) / (line2c2 - line2c1);
			intercept2 = (line2r1*line2c2 - line2r2*line2c1) / (line2c2 - line2c1);
			cIntersection = line1c1;
			rIntersection = slope2*cIntersection + intercept2;
		}
		else if (!isLine1Vertical && isLine2Vertical) {
			slope1 = (line1r2 - line1r1) / (line1c2 - line1c1);
			intercept1 = (line1r1*line1c2 - line1r2*line1c1) / (line1c2 - line1c1);
			cIntersection = line2c1;
			rIntersection = slope1*cIntersection + intercept1;
		}
		else { //Neither of the 2 lines is vertical
			slope1 = (line1r2 - line1r1) / (line1c2 - line1c1);
			intercept1 = (line1r1*line1c2 - line1r2*line1c1) / (line1c2 - line1c1);
			slope2 = (line2r2 - line2r1) / (line2c2 - line2c1);
			intercept2 = (line2r1*line2c2 - line2r2*line2c1) / (line2c2 - line2c1);
			if (slope1 != slope2) {
				cIntersection = (intercept2 - intercept1) / (slope1 -slope2);
				rIntersection = slope1*cIntersection + intercept1;
			}
		}
		PointPixel intersectionPoint = new PointPixel((int)rIntersection, (int)cIntersection);
		return intersectionPoint;
	}

	/** 
	 * Calculates the intersection point of two lines. 
	 * Lines are defined by the orientation, slope and intercept. 
	 * if (orientation == Primitive.HORIZONTAL), slope = row/column.
	 * if (orientation == Primitive.VERTICAL), slope = column/row.
	 * If the orientations are the same: <br>
	 * y = m1*x + b1, y = m2*x + b2, m1*x + b1 = m2*x + b2, x*(m1-m2) = b2-b1<br>
	 * x = (b2 - b1) / (m1 - m2) <p>
	 * if m1 == m2, they are parallel, they do not intersect<p>
	 * If the orientations are different: <br>
	 * y = m1*x + b1, x = m2*y + b2, x = m2*m1*x + m2*b1 + b2, x*(1-m1*m2) = m2*b1+b2<br>
	 * x = (m2 * b1 + b2) / (1 - m1 * m2) <br>
	 * x = m1*y + b1, y = m2*x + b2, y = m2*m1*y + m2*b1 + b2, y*(1-m1*m2) = m2*b1+b2<br>
	 * y = (m2 * b1 + b2) / (1 - m1 * m2) <br>
	 * if m1*m2==1, they are parallel, they do not intersect.
	 *
	 * @param line1o The orientation of the first line
	 * @param line1s The slope of the first line
	 * @param line1i The intercept of the first line
	 * @param line2o The orientation of the second line
	 * @param line2s The slope of the second line
	 * @param line2i The intercept of the second line
	 * @return The point at which the given two lines intersect
	 */
	public PointPixel getIntersectionPoint(int line1o, double line1s, double line1i, int line2o, double line2s, double line2i) {
		double cIntersection = -1;
		double rIntersection = -1;

		if (line1o == line2o) {
			if (line1s == line2s) {
			}
			else if (line1o == Primitive.HORIZONTAL) {
				cIntersection = (line2i - line1i) / (line1s - line2s);
				rIntersection = line1s * cIntersection + line1i;
			}
			else {
				rIntersection = (line2i - line1i) / (line1s - line2s);
				cIntersection = line1s * rIntersection + line1i;
			}
		}
		else {
			if ((line1s*line2s) == 1) {
			}
			else if (line1o == Primitive.HORIZONTAL) {
				cIntersection = (line2s*line1i + line2i) / (1 - line1s*line2s);
				rIntersection = line1s * cIntersection + line1i;
			}
			else {
				rIntersection = (line2s*line1i + line2i) / (1 - line1s*line2s);
				cIntersection = line1s * rIntersection + line1i;
			}
		}
		PointPixel intersectionPoint = new PointPixel((int)rIntersection, (int)cIntersection);
		return intersectionPoint;
	}

  /**
	 * Determines whether two points are on the same side of a straight line.
	 * Are points firstP and secondP on the same side of the line
	 * defined by beginPixel - endPixel?
	 * Finds the intersection point of line beginPixel-endPixel with
	 * line firstP-secondP.
	 * If they are not on the same side, distBetween = dist1 + dist2
	 * If they are on the same side, distBetween = |dist1 - dist2| 
	 * where dist1 is the distance between the firstP and the intersection point 
	 * and dist2 is the distance between the secondP and the intersection point.
	 * distBetween is the distance between the two points (firstP and secondP).
	 * <p>
	 * This is the old approach: <br>
	 * If the intersection point is contained in the line,
	 * beginPixel-endPixel, firstP and secondP are on different sides.
	 * Otherwise, the intersection point is at an extension of the line
	 * beginPixel-endPixel and firstP and secondP are on the same side.
	 *
	 * @param beginPixel One end point of the straight line
	 * @param endPixel The other end point of the straight line
	 * @param firstP One point that is on the same side of the straight line as secondP or not
	 * @param secondP The other point that is on the same side of the straight line as firstP or not
	 * @return True if firstP and secondP are on the same side of the straight line and false otherwise
	 */
	public boolean isOnSameSide(PointPixel beginPixel, PointPixel endPixel, PointPixel firstP, PointPixel secondP) {
		PointPixel intPoint = getIntersectionPoint(beginPixel, endPixel, firstP, secondP);
		//System.out.println("Intersection point: "+intPoint);
		if (intPoint.getRow() == -1 && intPoint.getColumn() == -1) { //They do not intersect
			//System.out.println("Points "+firstP+" and "+secondP+" are on the same side of the line between "+beginPixel+" and "+endPixel);
			return true;
		}
		double distBetween = Math.sqrt(squareDistPointToPoint(firstP, secondP));
		double dist1 = Math.sqrt(squareDistPointToPoint(firstP, intPoint));
		double dist2 = Math.sqrt(squareDistPointToPoint(secondP, intPoint));
		//If they are not on the same side, distBetween = dist1 + dist2
		//If they are on the same side, distBetween = |dist1 - dist2| 
		if (Math.abs(distBetween - (Math.abs(dist1 - dist2))) < 2) {
			//System.out.println("Points "+firstP+" and "+secondP+" are on the same side of the line between "+beginPixel+" and "+endPixel);
			return true;
		}
		//System.out.println("Points "+firstP+" and "+secondP+" are on different sides of the line between "+beginPixel+" and "+endPixel);
		return false;
		/****
		if (intPoint.getRow() <= beginPixel.getRow() && intPoint.getRow() <= endPixel.getRow()) {
			if ((intPoint.getColumn() <= beginPixel.getColumn() && intPoint.getColumn() <= endPixel.getColumn()) || (intPoint.getColumn() >= beginPixel.getColumn() && intPoint.getColumn() >= endPixel.getColumn())) {
				return true;
			}
		}
		else if (intPoint.getRow() >= beginPixel.getRow() && intPoint.getRow() >= endPixel.getRow()) {
			if ((intPoint.getColumn() <= beginPixel.getColumn() && intPoint.getColumn() <= endPixel.getColumn()) || (intPoint.getColumn() >= beginPixel.getColumn() && intPoint.getColumn() >= endPixel.getColumn())) { 
				return true;
			}
		}
		//if ((intPoint.getRow() == beginPixel.getRow() && intPoint.getColumn() == beginPixel.getColumn()) || (intPoint.getRow() == endPixel.getRow() && intPoint.getColumn() == endPixel.getColumn())) {
			//return true;
		//}
		*****/
	}


	/** 
	 * Calculates the intersection point between two lines.
	 * The inputs are a sequence of points.
	 * <ol> 
	 * <li>
	 * 1. If they are parallel and the distance between the two lines is not zero, then
	 *   they do not intersect.
	 * <li>
	 * 2. If they are parallel and the distance between the two lines is zero, then they
	 *   they lie on the same line.
	 * <li>
	 * 3. Else the two are not parallel and they intersect.
	 * </ol>
	 *
	 * @param line1 The first line
	 * @param line2 The second line
	 * @return The intersection point of the two lines
	 */
	public Point getIntersectionPoint(Line2D.Double line1, Line2D.Double line2) {
		Point2D p1Line1, p2Line1, p1Line2, p2Line2;
		Point p_intxn=null;
		double x1Line1,x2Line1, y1Line1, y2Line1;
		double x1Line2,x2Line2, y1Line2, y2Line2;
		double slopeLine1, slopeLine2;
		double x_intxn, y_intxn;
		boolean isLine1Vertical=false;
		boolean isLine2Vertical=false;
		double dist_p1Line1_p1Line2;
		double dist_p1Line1_p2Line2; 
		double dist_p2Line1_p1Line2;
		double dist_p2Line1_p2Line2;
		double minVal;

		//Coordinates of the First Line
		p1Line1 = line1.getP1();
		x1Line1 = line1.getX1();
		y1Line1 = line1.getY1();
		p2Line1 = line1.getP2();
		x2Line1 = line1.getX2();
		y2Line1 = line1.getY2();

		//Coordinates of the Second Line
		p1Line2 = line2.getP1();
		x1Line2 = line2.getX1();
		y1Line2 = line2.getY1();
		p2Line2 = line2.getP2();
		x2Line2 = line2.getX2();
		y2Line2 = line2.getY2();

		//Find the slope of both lines with respect to the positive direction of the
		//x-Axis
		if (Math.abs(x1Line1-x2Line1) == 0.00)
			isLine1Vertical = true;
		if (Math.abs(x1Line2-x2Line2) == 0.00)
			isLine2Vertical = true;

		if (isLine1Vertical && isLine2Vertical) { //Both Lines are vertical
			if (Math.abs(x1Line1 - x1Line2 ) == 0.00) { 
				//The distance between the 2 lines is 0;
				x_intxn = x1Line1;

				dist_p1Line1_p1Line2 = p1Line1.distance(p1Line2);
				dist_p1Line1_p2Line2 = p1Line1.distance(p2Line2);
				dist_p2Line1_p1Line2 = p2Line1.distance(p1Line2);
				dist_p2Line1_p2Line2 = p2Line1.distance(p2Line2);

				minVal = Math.min( 
										 Math.min(dist_p1Line1_p1Line2, dist_p1Line1_p2Line2), 
										 Math.min(dist_p2Line1_p1Line2, dist_p2Line1_p1Line2)
										);
				if (minVal == dist_p1Line1_p1Line2)
					y_intxn = (y1Line1 + y1Line2) / 2;
				else if (minVal == dist_p1Line1_p2Line2)
					y_intxn = (y1Line1 + y2Line2) / 2;
				else if (minVal == dist_p2Line1_p1Line2)
					y_intxn = (y2Line1 + y1Line2) / 2;
				else
					y_intxn = (y2Line1 + y2Line2)/ 2;

				p_intxn = new Point((int)x_intxn, (int) y_intxn);
			}
		}
		else if (isLine1Vertical && !isLine2Vertical) { //Line 1 is vertical and line 2 is not
			x_intxn = x1Line1;
			y_intxn = ((y2Line2 - y1Line2) / (x2Line2 - x1Line2)) * (x_intxn - x1Line2) + y1Line2;

			p_intxn = new Point((int) x_intxn, (int) y_intxn);
		}
		else if (!isLine1Vertical && isLine2Vertical) { //Line 2 is vertical and line 1 is not
			x_intxn = x1Line2;
			y_intxn = ((y2Line1 - y1Line1) / (x2Line1 - x1Line1)) * (x_intxn - x1Line1) + y1Line1;
			p_intxn = new Point((int) x_intxn, (int) y_intxn);
		}
		else { //Neither of the 2 lines is vertical
			//Are the 2 lines parallel
			//First find the slope 
			slopeLine1 = (y2Line1 - y1Line1) / (x2Line1 - x1Line1);
			slopeLine2 = (y2Line2 - y1Line2) / (x2Line2 - x1Line2);

			if (Math.abs(slopeLine1 - slopeLine2) == 0) {//The lines are parallel
				//Are the 2 line segments on the same straight line
				double slopeLine1Line2 = (y2Line2 - y1Line1) / (x2Line2 - x1Line1);
				//Use an approximation
				if (Math.abs(Math.abs(slopeLine1Line2 - slopeLine1) - 0) < 0.005) {
					dist_p1Line1_p1Line2 = p1Line1.distance(p1Line2);
					dist_p1Line1_p2Line2 = p1Line1.distance(p2Line2);
					dist_p2Line1_p1Line2 = p2Line1.distance(p1Line2);
					dist_p2Line1_p2Line2 = p2Line1.distance(p2Line2);

					minVal = Math.min( 
										 Math.min(dist_p1Line1_p1Line2, dist_p1Line1_p2Line2), 
										 Math.min(dist_p2Line1_p1Line2, dist_p2Line1_p1Line2)
											);
					if (minVal == dist_p1Line1_p1Line2) {
						x_intxn = (x1Line1 + x1Line2) / 2;
						y_intxn = (y1Line1 + y1Line2) / 2;
					}
					else if (minVal == dist_p1Line1_p2Line2) {
						x_intxn = (x1Line1 + x2Line2) / 2;
						y_intxn = (y1Line1 + y2Line2) / 2;
					}
					else if (minVal == dist_p2Line1_p1Line2) {
						x_intxn = (x2Line1 + x1Line2) / 2;
						y_intxn = (y2Line1 + y1Line2) / 2;
					}
					else {
						x_intxn = (x2Line1 + x2Line2) / 2;
						y_intxn = (y2Line1 + y2Line2) / 2;
					}

					p_intxn = new Point((int) x_intxn, (int) y_intxn);
				}
			}
			else {
				//Find the intersection of the two lines by solving the equation of 
				//the type y' - p1_y1 = m_1 ( x' - p1_x1 )
				//         y' - p2_y1 = m_2 ( x' - p2_x1 )
				// and find the value of (x', y')

				//System.out.println("Lines not parallel");
				x_intxn = ((y1Line2 - y1Line1) - ((slopeLine2 * x1Line2) - (slopeLine1 * x1Line1))) / (slopeLine1 - slopeLine2);
				y_intxn = (slopeLine1 * (x_intxn - x1Line1)) + y1Line1;
				p_intxn = new Point((int) x_intxn, (int) y_intxn);
			}
		}
		return( p_intxn );
	}

	/** 
	 * Determines the list of points that form a straight line between the
	 * two given points.
	 * 
	 * @param fromPoint The point from which a line will be determined  
	 * @param toPoint The point to which a line will be determined
	 * @return The linked list of points on the straight line between the two given points
	 */ 
	public LinkedList drawLinePoints(PointPixel fromPoint, PointPixel toPoint) {
		int row, column;
		LinkedList pointsP = new LinkedList();
		pointsP.add(new Point(fromPoint.getRow(), fromPoint.getColumn()));
		pointsP.add(new Point(toPoint.getRow(), toPoint.getColumn()));
		LinkedList points = new LinkedList();
		points.add(fromPoint);
		points.add(toPoint);

		Vector lineInfo = fitStraightLineLSE(pointsP);
		int lineOrientation = ((Integer)lineInfo.get(0)).intValue();
		double slope = ((Double)lineInfo.get(1)).doubleValue();
		double intercept = ((Double)lineInfo.get(2)).doubleValue();
		//System.out.println("LineOrient="+lineOrientation+", slope="+slope+", intercept="+intercept);
		if (lineOrientation == Primitive.HORIZONTAL) { //rows = m*columns + b
			for (int i = fromPoint.getColumn() + 1; i < toPoint.getColumn(); i++) {
				row = (int)(slope*(double)i+intercept);
				points.add(new PointPixel(row, i));
			}
			for (int i = fromPoint.getColumn() - 1; i > toPoint.getColumn(); i--) {
				row = (int)(slope*(double)i+intercept);
				points.add(new PointPixel(row, i));
			}
		}
		else if (lineOrientation == Primitive.VERTICAL) { //columns = m*rows + b
			for (int i = fromPoint.getRow() + 1; i < toPoint.getRow(); i++) {       
				column = (int)(slope*(double)i+intercept);
				points.add(new PointPixel(i, column));
			}  
			for (int i = fromPoint.getRow() - 1; i > toPoint.getRow(); i--) {
				column = (int)(slope*(double)i+intercept);
				points.add(new PointPixel(i, column));
			}  
		}  
		return points;
	}

	/** 
	 * Determines the list of points that form a straight line between the
	 * two given points.
	 * 
	 * @param fromPoint The point from which a line will be determined  
	 * @param toPoint The point to which a line will be determined
	 * @param anImage The 2d array representation of an image on which the line between the two given points will be drawn
	 * @param value The color value of the line that will be drawn
	 */ 
	public void drawLine(PointPixel fromPoint, PointPixel toPoint, int[][] anImage, int value) {
		int row, column;
		LinkedList points = new LinkedList();
		points.add(fromPoint);
		points.add(toPoint);

		Vector lineInfo = fitStraightLineLSE(points);
		int lineOrientation = ((Integer)lineInfo.get(0)).intValue();
		double slope = ((Double)lineInfo.get(1)).doubleValue();
		double intercept = ((Double)lineInfo.get(2)).doubleValue();
		if (lineOrientation == Primitive.HORIZONTAL) { //rows = m*columns + b
			for (int i = fromPoint.getColumn() + 1; i < toPoint.getColumn(); i++) {
				row = (int)(slope*(double)i+intercept);
				anImage[row][i] = value;
			}
			for (int i = fromPoint.getColumn() - 1; i > toPoint.getColumn(); i--) {
				row = (int)(slope*(double)i+intercept);
				anImage[row][i] = value;
			}
		}
		else if (lineOrientation == Primitive.VERTICAL) { //columns = m*rows + b
			for (int i = fromPoint.getRow() + 1; i < toPoint.getRow(); i++) {       
				column = (int)(slope*(double)i+intercept);
				anImage[i][column] = value;
			}  
			for (int i = fromPoint.getRow() - 1; i > toPoint.getRow(); i--) {
				column = (int)(slope*(double)i+intercept);
				anImage[i][column] = value;
			}  
		}  
		anImage[fromPoint.getRow()][fromPoint.getColumn()] = value;
		anImage[toPoint.getRow()][toPoint.getColumn()] = value;
	}

	/** 
	 * Determines the list of points that form a straight line between the
	 * two given points.
	 * 
	 * @param row1 The row number of the point from which a line will be determined  
	 * @param col1 The column number of the point from which a line will be determined  
	 * @param row2 The row number of the point to which a line will be determined
	 * @param col2 The column number of the point to which a line will be determined
	 * @param anImage The 2d array representation of an image on which the line between the two given points will be drawn
	 * @param value The color value of the line that will be drawn
	 */ 
	public void drawLine(int row1, int col1, int row2, int col2, int[][] anImage, int value) {
		PointPixel fromP = new PointPixel(row1, col1);
		PointPixel toP = new PointPixel(row2, col2);
		drawLine(fromP, toP, anImage, value);
	}

	/** 
	 * Returns a list of points in between the given two points.  
	 *
	 * @param fromPoint The point from which the straight line points will be determined
	 * @param toPoint The point to which the straight line points will be determined
	 * @return The linked list of points that are on the straight line between the two given points
	 */ 
	public LinkedList getInBetweenPixelPoints(Point fromPoint, Point toPoint) {
		int x1, y1, x2, y2;
		int cur_x, cur_y;
		int increment;
		boolean exChange_x_y=false;
		float slope=0, est_y, est_x; 
		LinkedList betweenPoints;

		x1 = (int) fromPoint.getX();
		y1 = (int) fromPoint.getY();
		x2 = (int) toPoint.getX();
		y2 = (int) toPoint.getY();
		betweenPoints = new LinkedList();

		if(Math.abs(y1-y2) > Math.abs(x1-x2)) {
			exChange_x_y = true;
			slope = (float)(x2 - x1)/(float)(y2- y1);
		}
		else {
			slope = (float)(y2 - y1)/(float)(x2- x1);
		}

		if (exChange_x_y) {
			increment = (int)((y2 - y1) / ((int)Math.abs(y2 - y1)));
			betweenPoints.add(new Point(x1, y1));

			for (cur_y = (y1 + increment); (increment) * (y2 - cur_y) >= 0; cur_y = (cur_y + increment)) {
				est_x = calcCoordLine(cur_y, slope, y1, x1);
				betweenPoints.add(new Point((int)(est_x + 0.5), cur_y));
			}
		}
		else {
			increment = (int)((x2 - x1) / ((int)Math.abs(x2 - x1)));
			betweenPoints.add(new Point(x1, y1));

			for (cur_x = (x1 + increment); (increment)* (x2 - cur_x) >= 0; cur_x = (cur_x + increment)) {
				est_y = calcCoordLine(cur_x,slope, x1, y1); 
				betweenPoints.add(new Point( cur_x, (int)(est_y + 0.5)));
			}
		}
		return (betweenPoints);
	}

}

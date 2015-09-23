import java.awt.Point;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Vector;
import java.lang.Math;

/**
 * A class to hold information for a chain of pixels.
 * 
 * @author Chart Reading project
 * @version 1.0
 */
public class Primitive {

	final static public int P_UNDEFINED = 0;
	final static public int POINT = 1;
  final static public int LINE_SEGMENT = 2;
  final static public int ARC_SEGMENT = 3;
  final static public int CURVE_SEGMENT = 4;

	final static public int LIST_FORWARD=0;
	final static public int LIST_BACKWARD=1;

	final static public int HORIZONTAL = 0;
	final static public int VERTICAL = 1;

	final static public int ENDBEGIN = 1;
  final static public int BEGINBEGIN = 2;
  final static public int BEGINEND = 3;
  final static public int ENDEND = 4;

	private int lineOrientation;
	private double length;
	private double slope;
	private double intercept;
	private double angle;
	private Point center;
	private double radius;
	private int size;
	private int distance; //distance to the origin

	private LinkedList listPoints;
	private LinkedList neighbors; 
	private int parentLabelNo;
	private int tagNo;
	private int partOfAxis;
	private int partOfTick;
	private int partOfRectangle;
	private int partOfWedge;
	private int partOfConnectedLine;
	private int partOfGridline;
	private boolean isLine;

  private int  p_type = P_UNDEFINED;
	private int equivPrimTagNo = -1;
	private boolean done = false;
	private int numNeighbors;
	private int numReferenced;

	private LinkedList beginNeigborList;
	private LinkedList endNeigborList;

 /**
	* Constructor.
	*
	* @param point1 A point on the primitive
	* @param point2 Another point on the primitive
	* @param tag The tag number of the primitive in its region
	* @param parentTag The label number of the region in which this primitive is
	*/
	public Primitive(Point point1, Point point2, int tag, int parentTag) {
		/**
		if(point1 == null || point2 == null) {
			throw Exception();
		}
		**/
		setPrimitive();
		listPoints.add(point1);
		listPoints.add(point2);
		tagNo = tag;
		parentLabelNo = parentTag;
		size = 2;
	}

 /**
	* Constructor.
	*
	* @param point A point on the primitive
	* @param tag The tag number of the primitive in its region
	* @param parentTag The label number of the region in which this primitive is
	*/
	public Primitive(Point point, int tag, int parentTag) {
		/***
		if(point == null) {
			throw Exception();
		}
		***/
		setPrimitive();
		listPoints.add(point);
		tagNo = tag;
		parentLabelNo = parentTag;
		size = 1;
	}

 /**
	* Constructor.
	*
	* @param list The linked list of points in this primitive
	* @param parentTag The label number of the region in which this primitive is
	*/
	public Primitive(LinkedList list, int parentTag) {
		/**
		if(list == null)
			throw Exception("null pointer passed");
		**/
		setPrimitive();
		listPoints = list;
		parentLabelNo = parentTag;
		size = list.size();
	}

 /**
	* Constructor.
	*
	* @param tag The tag number of the primitive in its region
	* @param parentTag The label number of the region in which this primitive is
	*/
	public Primitive(int tag, int parentTag) {
		setPrimitive();
		parentLabelNo = parentTag;
	}

 /**
	* Constructor.
	*
	* @param parentTag The label number of the region in which this primitive is
	*/
	public Primitive(int parentTag) {
		setPrimitive();
		parentLabelNo = parentTag;
	}

 /**
	* Sets the initial properties of a primitive. The region label number
	* is set to -1.
	*
	* @param none
	*/
	private void setPrimitive() {
		listPoints = new LinkedList();
		tagNo = 0;
		parentLabelNo = -1;
		neighbors = new LinkedList();
		size = 0;
		partOfAxis = 0;
		partOfTick = 0;
		partOfRectangle = 0;
		partOfWedge = 0;
		partOfConnectedLine = 0;
		partOfGridline = 0;
		isLine = false;
	 	numNeighbors = 0;
		numReferenced = 0;
		beginNeigborList = new LinkedList();
		endNeigborList = new LinkedList();
	}

 /**
	* Adds a point to the pixel list of the primitive at the 
	* position given by the index.
	*
	* @param index The position at which the given point is added to the pixel list
	* @param p The point to be added to the pixel list of the primitive
	*/
	public void addPointToList(int index, Point p) {
		//Can do some error handling
		listPoints.add(index, p);
		size++;
	}

 /**
	* Adds a point to the pixel list of the primitive at the 
	* end of the list.
	*
	* @param p The point to be added to the pixel list of the primitive
	*/
	public void addPointToList(Point p) {
		listPoints.add(p);
		size++;
	}

 /**
	* Removes the first point in the pixel list of the primitive.
	*
	* @param none
	*/
	public void removeFirstPoint() {
		listPoints.removeFirst();
		size--;
	}

 /**
	* Removes the last point in the pixel list of the primitive.
	*
	* @param none
	*/
	public void removeLastPoint() {
		listPoints.removeLast();
		size--;
	}

 /**
	* Adds a point to the pixel list of the primitive as an end point.
	*
	* @param p The point to be added to the pixel list as an end point 
	*/
	public boolean addEndPoint(Point p) {
		PointPixel aPixel = new PointPixel(p);
		return addEndPoint(aPixel);
	}

 /**
	* Adds a point to the pixel list of the primitive as an end point.
	*
	* @param aPixel The point to be added to the pixel list as an end point 
	*/
	public boolean addEndPoint(PointPixel aPixel) {
		if (aPixel.neigbors8(getBeginPoint())) {
			listPoints.addFirst(aPixel);
			size++;
			return true;
		}
		if (aPixel.neigbors8(getEndPoint())) {
			listPoints.addLast(aPixel);
			size++;
			return true;
		}
		return false;
	}
	
 /**
	* Adds a primitive to the current primitive. The pixel list is changed.
	*
	* @param aPrim The primitive to be added to the current primitive
	* @return The merging type; ENDBEGIN, BEGINBEGIN, BEGINEND, ENDEND or -1 if none of the first four types
	*/
	public int addPrimitive(Primitive aPrim) {
		int mergeType = -1;
		boolean added = false;
		PointPixel beginPixel = aPrim.getBeginPoint();
		PointPixel endPixel = aPrim.getEndPoint();
		LinkedList alist = aPrim.getAllPoints();
		if (beginPixel.equals(getBeginPoint()) || endPixel.equals(getBeginPoint())) {
			removeFirstPoint();
		}
		else if (beginPixel.equals(getEndPoint()) || endPixel.equals(getEndPoint())) {
			removeLastPoint();
		}
		ListIterator lItr;
		if (beginPixel.neigbors8(getEndPoint())) {
			mergeType = ENDBEGIN;
			lItr = alist.listIterator(0);
			while (lItr.hasNext()) {
				listPoints.add((Point)lItr.next());
			}
			added = true;
		}
		else if (beginPixel.neigbors8(getBeginPoint())) {
			mergeType = BEGINBEGIN;
			lItr = alist.listIterator(0);
			while (lItr.hasNext()) {
				listPoints.addFirst((Point)lItr.next());
			}
			added = true;
		}
		else if (endPixel.neigbors8(getBeginPoint())) {
			mergeType = BEGINEND;
			if (alist.size() == 1) {
				listPoints.addFirst((Point)alist.getFirst());
			}
			lItr = alist.listIterator(alist.size());
			while (lItr.hasPrevious()) {
				listPoints.addFirst((Point)lItr.previous());
			}
			added = true;
		}
		else if (endPixel.neigbors8(getEndPoint())) {
			mergeType = ENDEND;
			if (alist.size() == 1) {
				listPoints.addFirst((Point)alist.getFirst());
			}
			lItr = alist.listIterator(alist.size());
			while (lItr.hasPrevious()) {
				listPoints.add((Point)lItr.previous());
			}
			added = true;
		}
		return mergeType;
	}				

 /**
	* Adds a primitive to the current primitive according to the given
	* merging type. The pixel list is changed.
	*
	* @param aPrim The primitive to be added to the current primitive
	* @param mergeType The merging type; ENDBEGIN, BEGINBEGIN, BEGINEND or ENDEND
	* @return True if the primitive is added and false otherwise
	*/
	public boolean addPrimitive(Primitive aPrim, int mergetype) {
		boolean added = false;
		LinkedList alist = aPrim.getAllPoints();
		PointPixel beginPixel = aPrim.getBeginPoint();
		PointPixel endPixel = aPrim.getEndPoint();
		if (beginPixel.equals(getBeginPoint()) || endPixel.equals(getBeginPoint())) {
			removeFirstPoint();
		}
		else if (beginPixel.equals(getEndPoint()) || endPixel.equals(getEndPoint())) {
			removeLastPoint();
		}
		ListIterator lItr;
		if (mergetype == ENDBEGIN) {
			lItr = alist.listIterator(0);
			while (lItr.hasNext()) {
				listPoints.add((Point)lItr.next());
			}
			added = true;
		}
		else if (mergetype == BEGINBEGIN) {
			lItr = alist.listIterator(0);
			while (lItr.hasNext()) {
				listPoints.addFirst((Point)lItr.next());
			}
			added = true;
		}
		else if (mergetype == BEGINEND) {
			if (alist.size() == 1) {
				listPoints.addFirst((Point)alist.getFirst());
			}
			lItr = alist.listIterator(alist.size());
			while (lItr.hasPrevious()) {
				listPoints.addFirst((Point)lItr.previous());
			}
			added = true;
		}
		else if (mergetype == ENDEND) {
			if (alist.size() == 1) {
				listPoints.addFirst((Point)alist.getFirst());
			}
			lItr = alist.listIterator(alist.size());
			while (lItr.hasPrevious()) {
				listPoints.add((Point)lItr.previous());
			}
			added = true;
		}
		return added;
	}				

 /**
	* Fits a straight line to the pixel list of the primitive.
	*
	* @param none
	*/
	public void fitLine() {
		LineFitter aLineFitter = new LineFitter();
		Vector fitVector = aLineFitter.fitStraightLineLSE(listPoints);
		//Lineorientation, double slope, double intercept
		setLineOrientation(((Integer)fitVector.get(0)).intValue());
		setSlope(((Double)fitVector.get(1)).doubleValue());
		setIntercept(((Double)fitVector.get(2)).doubleValue());
		//Calculate the angle 
		//angle is from the row axis -the vertical axis, going counter clockwise
		angle = aLineFitter.getAngle(lineOrientation, slope);
		/*****
		if (lineOrientation == HORIZONTAL) {
			angle = (90 - Math.toDegrees(Math.atan(slope)));
			//System.out.print("Horizontal line. ");
		}
		else { //vertical
			angle = (Math.toDegrees(Math.atan(slope)));
			if (angle < 0)
				angle = 180 + angle;
			//System.out.println("Vertical line. ");
		}
		******/
		distance = (int)(Math.rint((Math.sqrt(aLineFitter.squareDistPointToLine(0, 0, slope, intercept, lineOrientation)))));
		//System.out.println("Slope: "+slope+", intercept: "+intercept+", angle: "+angle+", distance="+distance);
		isLine = aLineFitter.isLine(listPoints, lineOrientation, slope, intercept);
		//For the length,get the closest point on the fitted line to the begin point 
		//and the closest point on the fitted line to the end point of the primitive, 
		//and calculate the distance on the fitted line. 
		//Or just calculate the distance between the begin point and the end point. 
		//Those two will give different results. Which is better?
		length = Math.sqrt(aLineFitter.squareDistPointToPoint(getBeginPoint(), getEndPoint())); 
		if (isLine) {
			//System.out.println("Straight line, length between endpoints "+getBeginPoint()+" and "+getEndPoint()+" is "+length+", "+getSize()+" pixels");
		}
		else {
			//System.out.println("Not straight line, length between endpoints "+getBeginPoint()+" and "+getEndPoint()+" is "+length+", "+getSize()+" pixels");
		}
	}

 /**
	* Determines whether the pritimive is strictly vertical or not.
	*
	* @return True if the primitive is strictly vertical and false otherwise 
	*/
	public boolean isVertical() {
		if (getSize() == 1)
			return false;
		boolean flag = true;
		int r = getBeginPoint().getRow();
		int c = getBeginPoint().getColumn();
		Point aPoint;
		ListIterator lItr = listPoints.listIterator(1);
		while(lItr.hasNext()) {
			aPoint = (Point)lItr.next();
			if ((int)aPoint.getY() != c) {
				flag = false;
				break;
			}
		}
		return flag;
	}

 /**
	* Determines whether the pritimive is strictly horizontal or not.
	*
	* @return True if the primitive is strictly horizontal and false otherwise 
	*/
	public boolean isHorizontal() {
		if (getSize() == 1)
			return false;
		boolean flag = true;
		int r = getBeginPoint().getRow();
		int c = getBeginPoint().getColumn();
		Point aPoint;
		ListIterator lItr = listPoints.listIterator(1);
		while(lItr.hasNext()) {
			aPoint = (Point)lItr.next();
			if ((int)aPoint.getX() != r) {
				flag = false;
				break;
			}
		}
		return flag;
	}

 /**
	* Determines whether the current pritimive is parallel to 
	* the given primitive.
	*
	* @param p The primitive that the current primitive is compared with
	* @return True if the current primitive is parallel to the given primitive and false otherwise
	*/
	public boolean isParallelTo(Primitive p) {
		if (getLineOrientation() == p.getLineOrientation() && Math.abs(getSlope()-p.getSlope()) < 0.01) {
			return true;
		}
		if (getLineOrientation() != p.getLineOrientation() &&  Math.abs(getSlope()*p.getSlope() - 1) < 0.01) {
			return true;
		}
		return false;
	}

 /**
	* Determines whether the current pritimive is perpendicular to 
	* the given primitive.
	*
	* @param p The primitive that the current primitive is compared with
	* @return True if the current primitive is perpendicular to the given primitive and false otherwise
	*/
	public boolean isPerpendicularTo(Primitive p) {
		if (getLineOrientation() == p.getLineOrientation() && Math.abs(getSlope()*p.getSlope() + 1) < 0.05) {
			return true;
		}
		if (getLineOrientation() != p.getLineOrientation() &&  Math.abs(getSlope()+p.getSlope()) < 0.05) {
			return true;
		}
		if (getSize() == 1 && Math.abs(p.getAngle() - 0) < 1) { //Vertical line
			PointPixel aPoint = getBeginPoint();
			int check = (int)p.getIntercept();
			if (Math.abs(check - aPoint.getColumn()) <= 3) {
				return true;
			}
		}
		if (getSize() == 1 && Math.abs(p.getAngle() - 90) < 1) { //Horizontal line
			PointPixel aPoint = getBeginPoint();
			int check = (int)p.getIntercept();
			if (Math.abs(check - aPoint.getRow()) <= 3) {
				return true;
			}
		}
		return false;
	}

 /**
	* Determines whether the current pritimive and the given primitive
	* have the same region label and the same tag number in that region.
	*
	* @param p The primitive that the current primitive is compared with
	* @return True if the current primitive is the same one as the given primitive and false otherwise
	*/
	public boolean isEqualTo(Primitive p) {
		if(p.getParent() == this.parentLabelNo && p.getTagNo() == this.tagNo )
			return true;
		else
			return false;
	}

 /**
	* Determines whether the given pritimive contains all the pixels of 
	* the current primitive.
	*
	* @param p The primitive that the current primitive is compared with
	* @return True if the given primitive contains the current primitive and false otherwise
	*/
	public int isContainedBy(Primitive p) {
		if (p.getParent() == getParent() && p.getTagNo() != getTagNo()) { //Check all points!
			int index, index2;
			Point aPointinp;
			Point aPointhere;
			int noOfSamePoints = 0;
			LinkedList pList = p.getAllPoints();
			int index1 = pList.indexOf(getBeginPoint()); //Check the first point
			if (index1 > -1 && getSize() > 1) { //p contains the first point
				index2 = pList.indexOf((Point)(listPoints.get(1))); //Check the second point
				if (index2 > -1) {	//p contains the second point too 
					if (getSize() == 2) {
						if (Math.abs(index1 - index2) == 1) {
							return 2;
						}
					}
					else if (index2 - index1 == 1) { //index2 is greater, same direction
						if ((p.getSize() - index1) < getSize()) //p does not have as many points as needed
							return -1;
						ListIterator lItr = listPoints.listIterator(2);
						noOfSamePoints = 2;
						index = index2;
						while(lItr.hasNext()) {
							index++;
							aPointhere = (Point)lItr.next();
							aPointinp = (Point)pList.get(index);
							if (aPointinp.equals(aPointhere)) {
								noOfSamePoints++;
							}
							else {
								return noOfSamePoints;
							}
						}
						return noOfSamePoints;
					}	
					else if (index2 - index1 == -1) { //index1 is greater, reverse direction
						if ((index1 + 1) < getSize()) //p does not have as many points as needed
							return -1;
						ListIterator lItr = listPoints.listIterator(2);
						noOfSamePoints = 2;
						index = index2;
						while(lItr.hasNext()) {
							index--;
							aPointhere = (Point)lItr.next();
							aPointinp = (Point)pList.get(index);
							if (aPointinp.equals(aPointhere)) {
								noOfSamePoints++;
							}
							else {
								return noOfSamePoints;
							}
						} 
						return noOfSamePoints;
					}	
				} //end of if p contains the second point	
				else { //if p contains the first point, but not the second point
					return 1;
				}
			}	//end of if p contains the first point
		}
		return -1;
	}

 /**
	* Calculates the maximum consecutive intersecting points of the current primitive
	* with the given primitive.
	* How many and which consecutive points of the current primitive is in the 
	* given primitive?
	* Returns the maximum length pixel list that is contained in both primitives.
	* The two primitives need to be in the same region.
	*
	* @param p The primitive that the current primitive is compared with
	* @return The array of the indeces of the intersecting pixel list
	*/
	public int[] getIntersectionSegment(Primitive p) { 
		//System.out.println("In getIntersectionSegment of primitive.java");
		int[] segment = new int[2];
		segment[0] = 0;
		segment[1] = 0;
		if (p.getParent() == getParent() && p.getTagNo() != getTagNo()) { //Check all points!
			int[][] intersectionPoints = intersects(p);
			int noOfSamePoints = intersectionPoints[0][0];
			if (noOfSamePoints <= 0) {
				return segment;
			}
			int size = getSize();
			if (p.getSize() > size) {
				size = p.getSize();
			}
			boolean consecutive = false;
			int found = 0;
			int j = 0;
			int noOfSegments = 0;
			int[] indexBegin = new int[noOfSamePoints];
			int[] sizes = new int[noOfSamePoints];
			for (int i = 1; i<= size; i++) {
				if (intersectionPoints[0][i] == 1) {
					consecutive = true;
					if (found == 0) {
						indexBegin[j] = i - 1;
						sizes[j] = found;
					}
					found++;
				}
				else if (consecutive == true) {
					consecutive = false;
					sizes[j] = found;
					noOfSegments++;
					found = 0;
					j++;
				}
			}
			if (consecutive == true) {
				sizes[j] = found;
				noOfSegments++;
			}
			//System.out.println("Segment 1 is of size "+sizes[0]+", begins at index "+indexBegin[0]);
			int maxSegment = 0;
			for (int i = 1; i < noOfSegments; i++) {
				//System.out.println("Segment "+(i+1)+"is of size "+sizes[i]+", begins at index "+indexBegin[i]);
				if (sizes[i] > sizes[maxSegment]) {
					maxSegment = i;
				}
			}
			segment[0] = indexBegin[maxSegment];
			segment[1] = sizes[maxSegment];
		}
		//System.out.println("Intersection segment of "+segment[1]+" pixels");
		return segment;
	}


 /**
	* Calculates all the pixels that the current primitive has in 
	* common with the given primitive.
	* How many and which points of the current primitive is in the given
	* primitive?
	* 
	* @param p The primitive that the current primitive is compared with
	* @return The 2d array of the indeces of the intersecting pixel lists for each primitive
	*/
	public int[][] intersects(Primitive p) { 
		//System.out.println("In intersects of primitive.java");
		int size = getSize();
		if (p.getSize() > size) {
			size = p.getSize();
		}
		int[][] indexLists = new int[2][size+1];
		//Set all to zero
		int noOfSamePoints = 0;
		indexLists[0][0] = 0;
		indexLists[1][0] = 0;
		if (p.getParent() == getParent() && p.getTagNo() != getTagNo()) { //Check all points!
			LinkedList pList = p.getAllPoints();
			ListIterator pListlItr = pList.listIterator(0); 
			ListIterator lItr = listPoints.listIterator(0);
			int index = -1;
			int indexP;
			Point aPointhere, aPointinp;
			while(lItr.hasNext()) {
				index++;
				aPointhere = (Point)lItr.next();
				pListlItr = pList.listIterator(0); 
				indexP = -1;
				while(pListlItr.hasNext()) {
					indexP++;
					aPointinp = (Point)pListlItr.next();
					if (aPointinp.equals(aPointhere)) {
						noOfSamePoints++;
						indexLists[0][index+1] = 1;
						indexLists[1][indexP+1] = 1;
						break;
					}
				}
			}
		}
		indexLists[0][0] = noOfSamePoints;
		indexLists[1][0] = noOfSamePoints;
		//System.out.println("Intersects at "+noOfSamePoints+" points");
		return indexLists;
	}

 /**
	* Calculates all the pixels that the current primitive has in 
	* common or neighboring with the given primitive.
	* How many and which points of the current primitive touch a point in 
	* the given primitive or neighbor a point in the given primitive?
	* 
	* @param p The primitive that the current primitive is compared with
	* @return The 2d array of the indeces of the touching/neighboring pixel lists for each primitive
	*/
	public int[][] touches(Primitive p) { 
		//System.out.println("In touches of primitive.java");
		int size = getSize();
		if (p.getSize() > size) {
			size = p.getSize();
		}
		int[][] indexLists = new int[2][size+1];
		//Set all to zero
		int noOfSamePoints = 0;
		indexLists[0][0] = 0;
		indexLists[1][0] = 0;
		if (p.getParent() == getParent() && p.getTagNo() != getTagNo()) { //Check all points!
			int index = -1;
			int indexP;
			PointPixel aPointhere, aPointinp;
			LinkedList pList = p.getAllPoints();
			ListIterator pListlItr = pList.listIterator(0); 
			ListIterator lItr = listPoints.listIterator(0);
			while(lItr.hasNext()) {
				index++;
				aPointhere = new PointPixel(lItr.next());
				indexP = -1;
				pListlItr = pList.listIterator(0); 
				while(pListlItr.hasNext()) {
					indexP++;
					aPointinp = new PointPixel(pListlItr.next());
					if (aPointinp.neigbors8(aPointhere)) {
						noOfSamePoints++;
						indexLists[0][noOfSamePoints] = index;
						indexLists[1][noOfSamePoints] = indexP;
						break;
					}
				}
			}
		}
		indexLists[0][0] = noOfSamePoints;
		indexLists[1][0] = noOfSamePoints;
		//System.out.println("Touches at "+noOfSamePoints+" points");
		return indexLists;
	}


 /**
	* Returns the pixel list of the primitive.
	*
	* @param none
	* @return The linked list of pixels of the primitive
	*/
	public LinkedList getAllPoints() {
		return listPoints;
	}

 /**
	* Sets the pixel list of the primitive to the given one.
	*
	* @param alist The new pixel list for the primitive 
	*/
	public void setAllPoints(LinkedList alist) {
		listPoints = alist;
	}

 /**
	* Returns the beginning point of the pixels of the primitive.
	*
	* @param none
	* @return The beginning point of the primitive
	*/
	public PointPixel getBeginPoint() {
		return (new PointPixel(listPoints.getFirst()));
	}

 /**
	* Returns the end point of the pixels of the primitive.
	*
	* @param none
	* @return The end point of the primitive
	*/
	public PointPixel getEndPoint() {
		return (new PointPixel(listPoints.getLast()));
	}

 /**
	* Returns the number of pixels in the primitive.
	*
	* @param none
	* @return The number of pixels
	*/
	public int getSize() {
		return listPoints.size();
	}

 /**
	* Returns the tag number of the primitive in its region.
	*
	* @param none
	* @return The tag number of the primitive
	*/
	public int getTagNo() {
		return tagNo;
	}

 /**
	* Sets the tag number of the primitive in its region.
	*
	* @param t The tag number of the primitive 
	*/
	public void setTagNo(int t) {
		tagNo = t;
	}

 /**
	* Returns the label number of the region the primitive is in.
	*
	* @param none
	* @return The label number of the primitive's region
	*/
	public int getParent() {
		return parentLabelNo;
	}

 /**
	* Sets the label number of the region the primitive is in.
	*
	* @param l The label number of the primitive's region 
	*/
	public void setParent(int l) {
		parentLabelNo = l;
	}


 /**
	* Returns whether the primitive is part of an axis.
	*
	* @param none
	* @return Zero if the primitive is not part of an axis and greater than zero otherwise
	*/
	public int isPartOfAxis() {
		return partOfAxis;
	}

 /**
	* Sets the primitive to be a part of an axis. The <code>partOfAxis</code>
	* field is incremented by the given integer. If the given integer
	* is zero, <code>partOfAxis</code> is set to zero indicating that the
	* primitive is not part of any axis.
	*
	* @param b The amount by which the <code>partOfAxis</code> field is to be incremented 
	*/
	public void setPartOfAxis(int b) {
		if (b == 0)
			partOfAxis = b;
		else 
			partOfAxis += b;
	}

 /**
	* Returns whether the primitive is part of a tick mark of an axis.
	*
	* @param none
	* @return Zero if the primitive is not part of a tick mark of an axis and greater than zero otherwise
	*/
	public int isPartOfTick() {
		return partOfTick;
	}

 /**
	* Sets the primitive to be a part of a tick mark of an axis. 
	* The <code>partOfTick</code>
	* field is incremented by the given integer. If the given integer
	* is zero, <code>partOfTick</code> is set to zero indicating that the
	* primitive is not part of any tick mark.
	*
	* @param b The amount by which the <code>partOfTick</code> field is to be incremented 
	*/
	public void setPartOfTick(int b) {
		if (b == 0)
			partOfTick = b;
		else 
			partOfTick += b;
	}

 /**
	* Returns whether the primitive is part of a rectangle.
	*
	* @param none
	* @return Zero if the primitive is not part of a rectangle and greater than zero otherwise
	*/
	public int isPartOfRectangle() {
		return partOfRectangle;
	}

 /**
	* Sets the primitive to be a part of a rectangle. 
	* The <code>partOfRectangle</code>
	* field is incremented by the given integer. If the given integer
	* is zero, <code>partOfRectangle</code> is set to zero indicating that the
	* primitive is not part of any tick mark.
	*
	* @param b The amount by which the <code>partOfRectangle</code> field is to be incremented 
	*/
	public void setPartOfRectangle(int b) {
		if (b == 0)
			partOfRectangle = b;
		else 
			partOfRectangle += b;
	}

 /**
	* Returns whether the primitive is part of a wedge.
	*
	* @param none
	* @return Zero if the primitive is not part of a wedge and greater than zero otherwise
	*/
	public int isPartOfWedge() {
		return partOfWedge;
	}

 /**
	* Sets the primitive to be a part of a wedge. 
	* The <code>partOfWedge</code>
	* field is incremented by the given integer. If the given integer
	* is zero, <code>partOfWedge</code> is set to zero indicating that the
	* primitive is not part of any tick mark.
	*
	* @param b The amount by which the <code>partOfWedge</code> field is to be incremented 
	*/
	public void setPartOfWedge(int b) {
		if (b == 0)
			partOfWedge = b;
		else
			partOfWedge += b;
	}

 /**
	* Returns whether the primitive is part of a connected line.
	*
	* @param none
	* @return Zero if the primitive is not part of a connected line and greater than zero otherwise
	*/
	public int isPartOfConnectedLine() {
		return partOfConnectedLine;
	}

 /**
	* Sets the primitive to be a part of a connected line. 
	* The <code>partOfConnectedLine</code>
	* field is incremented by the given integer. If the given integer
	* is zero, <code>partOfConnectedLine</code> is set to zero indicating that the
	* primitive is not part of any tick mark.
	*
	* @param b The amount by which the <code>partOfConnectedLine</code> field is to be incremented 
	*/
	public void setPartOfConnectedLine(int b) {
		if (b == 0)
			partOfConnectedLine = b;
		else
			partOfConnectedLine += b;
	}

 /**
	* Returns whether the primitive is part of a gridline.
	*
	* @param none
	* @return Zero if the primitive is not part of a gridline and greater than zero otherwise
	*/
	public int isPartOfGridline() {
		return partOfGridline;
	}

 /**
	* Sets the primitive to be a part of a gridline. 
	* The <code>partOfGridline</code>
	* field is incremented by the given integer. If the given integer
	* is zero, <code>partOfGridline</code> is set to zero indicating that the
	* primitive is not part of any tick mark.
	*
	* @param b The amount by which the <code>partOfGridline</code> field is to be incremented 
	*/
	public void setPartOfGridline(int b) {
		if (b == 0)
			partOfGridline = b;
		else
			partOfGridline += b;
	}


 /**
	* Returns whether the primitive is a straight line.
	*
	* @param none
	* @return True if the primitive is a straight line and false otherwise
	*/
	public boolean isStraightLine() {
		return isLine;
	}

 /**
	* Sets the primitive to be a straight line according to the given parameter.
	* 
	* @param b True if the primitive is a straight line and false otherwise
	*/
	public void setIsStraightLine(boolean b) {
		isLine = b;
	}

 /**
	* Returns the primitive type.
	*
	* @param none
	* @return The type of the primitive; POINT, LINE_SEGMENT, ARC_SEGMENT or CURVE_SEGMENT
	*/
	public int getPrimitiveType() {
		return p_type;
	}

 /**
	* Sets the tag number of the other primitive that is actually the same one
	* as the current one. The current primitive needs to be merged with 
	* its equivalent primitive.
	* Method is currently not used.
	*
	* @param tag The tag number of the equivalent primitive
	*/
	public void setEquivPrimTag(int tag) {
		equivPrimTagNo = tag;
	}

 /**
	* Returns the tag number of the other primitive that is actually the same one
	* as the current one. The current primitive needs to be merged with 
	* its equivalent primitive.
	* Method is currently not used.
	*
	* @param none
	* @return The tag number of the equivalent primitive
	*/
	public int getEquivPrimTag() {
		return (equivPrimTagNo);
	}

 /**
	* Returns whether the primitive has ended; all the pixels are in its pixel list.
	* Method is currently not used.
	*
	* @param none
	* @return True if the primitive has ended and false otherwise
	*/
	public boolean hasPrimitiveEnded() {
		return done;
	}

 /**
	* Sets whether the primitive has ended; if all the pixels are in its pixel list.
	* Method is currently not used.
	*
	* @param x True if the primitive has ended and false otherwise
	*/
	public void setPrimitiveEndFlag(boolean x) {
		done = x;
	}

 /**
	* Sets the primitive type; POINT, LINE_SEGMENT, ARC_SEGMENT or CURVE_SEGMENT 
	*
	* @param type The primitive type
	*/
	public int setType(int type) {
		if(type == POINT || type == LINE_SEGMENT || type == ARC_SEGMENT || type == CURVE_SEGMENT)
			p_type = type;
		return p_type;
	}

 /**
	* Sets the primitive orientation; vertical or horizontal.
	*
	* @param o The orientation of the primitive
	*/
	public void setLineOrientation(int o) {
		lineOrientation = o;
	}

 /**
	* Returns the primitive orientation; vertical or horizontal.
	*
	* @param none
	* @return The line orientation of the primitive
	*/
	public int getLineOrientation() {
		return lineOrientation;
	}

 /**
	* Returns the length of the primitive. The length
	* is the physical length between the begin point and the end point
	* of the pixel list of the primitive.
	*
	* @param none
	* @return The length from the begin point to the end point
	*/
	public double getLength() {
		return length; 
	}

 /**
	* Sets the slope of the primitive.
	* 
	* @param s The slope of the primitive
	*/
	public void setSlope(double s) {
		slope = s;
	}

 /**
	* Returns the slope of the primitive.
	* 
	* @param none
	* @return The slope of the primitive
	*/
	public double getSlope() {
		return slope;
	}

 /**
	* Sets the row/column intercept of the primitive.
	*
	* @param i The row/column intercept of the primitive
	*/
	public void setIntercept(double i) {
		intercept = i;
	}

 /**
	* Returns the row/column intercept of the primitive.
	* 
	* @param none
	* @return The intercept of the primitive
	*/
	public double getIntercept() {
		return intercept;
	}

 /**
	* Sets the distance of the primitive from the origin.
	*
	* @param s The distance of the primitive from the origin
	*/
	public void setDistance(int s) {
		distance = s;
	}

 /**
	* Returns the distance of the primitive from the origin.
	* 
	* @param none
	* @return The distance of the primitive from the origin
	*/
	public int getDistance() {
		return distance;
	}

 /**
	* Sets the center point of the primitive for ARC_SEGMENT primitives.
	*
	* @param p The center point of the primitive 
	*/
	public void setCenter(Point p) {
		center = p;
	}

 /**
	* Returns the center point of the primitive if the primitive
	* is an ARC_SEGMENT.
	* 
	* @param none
	* @return The center point of the primitive
	*/
	public Point getCenter() {
		Point c = null;
		if (this.p_type == ARC_SEGMENT)
			c = center;
		return c;
	}

 /**
	* Sets the angle of the primitive from the positive row axis (negative
	* y axis).
	*
	* @param a The angle of the primitive from the positive row axis
	*/
	public void setAngle(double a) {
		angle = a;
	}

 /**
	* Returns the angle of the primitive from the positive row 
	* axis (negative y axis).
	* 
	* @param none
	* @return The angle of the primitive
	*/
	public double getAngle() {
		return angle;
	}

 /**
	* Sets the radius of the primitive for ARC_SEGMENT primitives.
	*
	* @param r The radius of the primitive
	*/
	public void setRadius(double r) {
		radius = r;
	}

 /**
	* Returns the radius of the primitive if the primitive
	* is an ARC_SEGMENT.
	* 
	* @param none
	* @return The radius of the primitive
	*/
	public double getRadius() {
		double r = 0;
		if (this.p_type == ARC_SEGMENT)
			r = radius;
		return r;
	}

  /**
	 * Returns a String holding information about the primitive
	 * for printing on the screen
	 *
	 * @param none
	 * @return The string of information for the primitive
	 */
	public String toString() {
		String message = new String("Primitive "+tagNo+" in region "+parentLabelNo+", ");
		if (isLine) {
			message = message + ("Line, ");
		}
		if (lineOrientation == Primitive.HORIZONTAL) {
			message = message + ("Horizontal, ");
		}
		else {
			message = message + ("Vertical, ");
		}
		message = message + ("gridline="+partOfGridline+", ");
		message = message + (getSize()+" pixels from "+getBeginPoint()+" to "+getEndPoint()+" length="+getLength()+", angle="+angle+", distance to origin="+distance+"\n");
		return message;
	}

 /**
	* Adds a point to the list of neighbors of the primitive at 
	* its begin point.
	* Method is not used currently.
	* 
	* @param p The point to be added as a neighbor
	*/
	public void addBeginNeigborPixelList(Point p) {
		beginNeigborList.add(p);
	}

 /**
	* Adds a point to the list of neighbors of the primitive at 
	* its end point.
	* Method is not used currently.
	* 
	* @param p The point to be added as a neighbor
	*/
	public void addEndNeigborPixelList(Point p) {
		endNeigborList.add(p);
	}

 /**
	* Prints on the screen the list of neighbors of the primitive
	* at its begin point.
	* Method is not used currently.
	*
	* @param none
	*/
	public void displayBeginNeigborPixelList() {
		//System.out.println("Chain "+tagNo+" region "+parentLabelNo+" begin neigbors: ");
		if (beginNeigborList.size() < 1) {
			//System.out.println("Begin neigbor list is empty.");
			return;
		}
		PointPixel aPixel;
		ListIterator lItr = beginNeigborList.listIterator(0);
		while (lItr.hasNext()) {
			aPixel = new PointPixel(lItr.next());
			//System.out.println(aPixel);
		}
	}

 /**
	* Prints on the screen the list of neighbors of the primitive
	* at its end point.
	* Method is not used currently.
	*
	* @param none
	*/
	public void displayEndNeigborPixelList() {
		//System.out.println("Chain "+tagNo+" region "+parentLabelNo+" end neigbors: ");
		if (endNeigborList.size() < 1) {
			//System.out.println("End neigbor list is empty.");
			return;
		}
		PointPixel aPixel;
		ListIterator lItr = endNeigborList.listIterator(0);
		while (lItr.hasNext()) {
			aPixel = new PointPixel(lItr.next());
			//System.out.println(aPixel);
		}
	}

 /**
	* Extends the primitive by one pixel at its begin point.
	* The pixel that is added is selected from the list of neighbors at the
	* primitive's begin point.
	* Method is not used currently.
	* 
	* @param none
	* @return True if a pixel was added and false otherwise
	*/
	public boolean addBeginPixel() {
		if (beginNeigborList.size() == 0)
			return false;
		PointPixel aPixel;
		double estValue;
		int minErrorIndex = -1;
		int index = -1;
		double minError = 0;
		double error = 0;
		ListIterator lItr = beginNeigborList.listIterator(0);
		while (lItr.hasNext()) {
			index++;
			aPixel = new PointPixel(lItr.next());
			if (p_type == LINE_SEGMENT) {
				if (lineOrientation == HORIZONTAL) { //slope = row/column
					estValue = slope * aPixel.getColumn() + intercept;			
					error = (estValue - aPixel.getRow()) * (estValue - aPixel.getRow());
				}
				else if (lineOrientation == VERTICAL) { //slope = column/row
					estValue = slope * aPixel.getRow() + intercept;			
					error = (estValue - aPixel.getColumn()) * (estValue - aPixel.getColumn());
				}

			}
			else if (p_type == ARC_SEGMENT) {  // row2/crow2 + column2/ccolumn2 = r2
				estValue = ((radius*radius) - ((aPixel.getColumn())*(aPixel.getColumn())/(((int)center.getY())*((int)center.getY())))) * ((int)center.getX()) * ((int)center.getX());
				estValue = Math.sqrt(estValue);
				error = (estValue - aPixel.getRow()) * (estValue - aPixel.getRow());
			}
			if (error <= minError) {
				minError = error;
				minErrorIndex = index;
			}
		}
		if (minErrorIndex >= 0) {
		  aPixel = (PointPixel)beginNeigborList.get(minErrorIndex);
			addPointToList(0, aPixel);
			return true;
		}
		return false;
	}

 /**
	* Extends the primitive by one pixel at its end point.
	* The pixel that is added is selected from the list of neighbors at the
	* primitive's end point.
	* Method is not used currently.
	* 
	* @param none
	* @return True if a pixel was added and false otherwise
	*/
	public boolean addEndPixel() {
		if (endNeigborList.size() == 0)
			return false;
		PointPixel aPixel;
		double estValue;
		int minErrorIndex = -1;
		int index = -1;
		double minError = 0;
		double error = 0;
		ListIterator lItr = endNeigborList.listIterator(0);
		while (lItr.hasNext()) {
			index++;
			aPixel = new PointPixel(lItr.next());
			if (p_type == LINE_SEGMENT) {
				if (lineOrientation == HORIZONTAL) { //slope = row/column
					estValue = slope * aPixel.getColumn() + intercept;			
					error = (estValue - aPixel.getRow()) * (estValue - aPixel.getRow());
				}
				else if (lineOrientation == VERTICAL) { //slope = column/row
					estValue = slope * aPixel.getRow() + intercept;			
					error = (estValue - aPixel.getColumn()) * (estValue - aPixel.getColumn());
				}

			}
			else if (p_type == ARC_SEGMENT) {  // row2/crow2 + column2/ccolumn2 = r2
				estValue = ((radius*radius) - ((aPixel.getColumn())*(aPixel.getColumn())/(((int)center.getY())*((int)center.getY())))) * ((int)center.getX()) * ((int)center.getX());
				estValue = Math.sqrt(estValue);
				error = (estValue - aPixel.getRow()) * (estValue - aPixel.getRow());
			}
			if (error <= minError) {
				minError = error;
				minErrorIndex = index;
			}
		}
		if (minErrorIndex >= 0) {
		  aPixel = (PointPixel)endNeigborList.get(minErrorIndex);
			addPointToList(listPoints.size(), aPixel);
			return true;
		}
		return false;
	}

 /**
	* Returns the inverted primitive. The pixel list is inverted.
	* The properties of the primitive are kept the same.
	*
	* @param none
	* @return The inverted primitive.
	*/
	public Primitive invPrimitive() {
		Primitive invP = new Primitive(tagNo, parentLabelNo);
		if (listPoints != null) {
			ListIterator lItr = listPoints.listIterator(listPoints.size());
			while(lItr.hasPrevious()) {
				invP.addPointToList((Point)lItr.previous());
			}
			invP.setParent(parentLabelNo);
			invP.setTagNo(tagNo);
			invP.setSlope(slope);
			invP.setIntercept(intercept);
			invP.setLineOrientation(lineOrientation);
			invP.setAngle(angle);
			invP.setType(p_type);
			invP.setCenter(center);
			invP.setRadius(radius);
			invP.setIsStraightLine(isLine);
			invP.setPartOfAxis(partOfAxis);
			invP.setPartOfTick(partOfTick);
			invP.setPartOfRectangle(partOfRectangle);
			invP.setPartOfWedge(partOfWedge);
			invP.setPartOfConnectedLine(partOfConnectedLine);
			invP.setPrimitiveEndFlag(done);
		}
		return invP;
	}


 /**
	* Increments the number of times the current primitive
	* is referenced from a graph component.
	* Method is not currently used.
	*
	* @param none
	*/
	public void incrNumReferenced() {
		numReferenced++;
	}

 /**
	* Returns the number of times the current primitive
	* is referenced from a graph component.
	* Method is not currently used.
	*
	* @param none
	* @return The number of times the current primitive is referenced
	*/
	public int getNumReferenced() {
		return numReferenced;
	}

 /**
	* Returns the pixel list of the primitive according to the 
	* given parameters.
	* Method is not currently used.
	*
	* @param index The index of the pixel in the pixel list from which the returned pixel list should start
	* @param numPoints The number of pixels the returned pixel list should contain
	* @param direction The direction towards which the pixel list is to be formed given the starting pixel point
	* @return The linked list of pixels
	*/
	public LinkedList getPoints(int index, int numPoints, int direction) {
		int count=0;
		//System.out.println("Request for Prim: " + this.tagNo + " is " + "Index: " + index + ", NumPoints: " + numPoints + " direction: " + direction );
		if (( index > (size - 1)) || (index < 0)) {
			//throw outofBoundException
			return (null);
		}
		else if (direction != LIST_FORWARD && direction != LIST_BACKWARD) {
			//throw outofBoundException
			return(null);
		}
		else if (direction == LIST_FORWARD) {
			//throw NotThatManyPointsInThePrimitiveException
			if ((index + numPoints) > size)
				return(null);
		}
		else if(direction == LIST_BACKWARD) {
			//throw NotThatManyPointsInThePrimitiveException
			if ((index + 1 - numPoints) < 0)
				return(null);
		}
		LinkedList tmpList = new LinkedList();

		ListIterator lItr = listPoints.listIterator(index);
		if(direction == LIST_FORWARD) {
			while(lItr.hasNext()) {
				tmpList.add(lItr.next());
				count++;
				if(count >= numPoints)
					break;
			}
		}
		else if (direction == LIST_BACKWARD) {
			while (lItr.hasPrevious()) {
				tmpList.add(lItr.previous());
				count++;
				if(count >= numPoints) 
					break;
			}
		}
		return( tmpList );
	}

 /**
	* Returns the pixel list of the primitive according to the 
	* given parameters.
	* Method is not currently used.
	*
	* @param index The index of the pixel in the pixel list from which the returned pixel list should start
	* @param direction The direction towards which the pixel list is to be formed given the starting pixel point
	* @return The linked list of pixels
	*/
	public LinkedList getPoints(int index, int direction) {
		int count=0;
		if ((index > (size - 1)) || (index < 0)) {
			//throw outofBoundException
			return (null);
		}
		else if (direction != LIST_FORWARD && direction != LIST_BACKWARD) {
			//throw outofBoundException
			return (null);
		}

		LinkedList tmpList = new LinkedList();
		ListIterator lItr = listPoints.listIterator(index);
		if (direction == LIST_FORWARD) {
			while(lItr.hasNext())
				tmpList.add(lItr.next());
		}
		else if (direction == LIST_BACKWARD) {
			while(lItr.hasPrevious())
				tmpList.add(lItr.previous());
		}
		return(tmpList);
	}

 /**
	* Returns the pixel list of the primitive according to the 
	* given parameters.
	* Method is not currently used.
	*
	* @param index The index of the pixel in the pixel list from which the returned pixel list should start
	* @return The linked list of pixels
	*/
	public LinkedList getPoints(int index) {
		int count=0;
		if (( index > (size - 1)) || (index < 0)) {
			//throw outofBoundException
			return (null);
		}

		LinkedList tmpList = new LinkedList();
		ListIterator lItr = listPoints.listIterator(index);

		while(lItr.hasNext())
			tmpList.add(lItr.next());

		return(tmpList);
	}

/***********************
METHODS BELOW ARE NOT USED
	public PrimitiveNeighbor addNeighbor(PrimitiveNeighbor n) {
		if(n!= null) {
			neighbors.add(n);
			numNeighbors++;
		}
		return n;
	}

	public PrimitiveNeighbor addNeighbor(Primitive n) {
		PrimitiveNeighbor pn = null;
		if(n!= null) {
			pn = new PrimitiveNeighbor(this, n);
			neighbors.add(pn);
			numNeighbors++;
		}
		return pn;
	}

	public LinkedList getPrimitiveNeighbors() {
		return neighbors;
	}

	public int getNumPrimitiveNeighbors() {
		return numNeighbors;
	}
***********/
}

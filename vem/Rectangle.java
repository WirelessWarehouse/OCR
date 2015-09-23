import java.util.LinkedList;
import java.util.ListIterator;

/**
 * A class to hold information for a rectangle.
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class Rectangle {
	/***
	private LinkedList leftEdge;
	private LinkedList rightEdge;
	private LinkedList topEdge;
	private LinkedList bottomEdge;
	***/

	private LinkedList allSides;
	private LinkedList corners;

	private int regionLabelNo;
	private int height;
	private int width;
	private int orientation;
	private int color;
	private boolean isFilledArea;
	private boolean isVerticalBar;
	private boolean isHorizontalBar;
	private boolean isLegend;
	private boolean isData;
	private PointPixel upperLeft;
	private PointPixel lowerRight;
	private PointPixel value; 
	//The value with respect to the axis (if it is a bar)
	//The middle point of the top edge (if it is a vertical bar)

	private LinkedList annotationWords;
	private LinkedList labelWords;
	private LinkedList annotationBlocks;
	private LinkedList labelBlocks;

	private final int VERTICAL = 0;
	private final int HORIZONTAL = 1;

  /**
	 * Constructor.
 	 *
	 * @param sides The linked list of the primitives that make up the sides of the rectangle
	 * @param c The linked list of corner points of the rectangle
	 * @param isFilled True if the rectangle is a filled region and false otherwise
	 */
	public Rectangle(LinkedList sides, LinkedList c, boolean isFilled) {
		isFilledArea = isFilled;
		isVerticalBar = false;
		isHorizontalBar = false;
		isLegend = false;
		isData = false;
		color = -1;
		if (c.size() == 4) {
			corners = c;
		}
		//if (sides.size() == 4) {
		Primitive primitive1 = (Primitive)sides.get(0);
		allSides = sides;
		regionLabelNo = primitive1.getParent();
		setBoundingBox();
			/*****
			topEdge = new LinkedList();
			topEdge.add(primitive1);
			Primitive primitive2 = (Primitive)sides.get(1);
			rightEdge = new LinkedList();
			rightEdge.add(primitive2);
			if (primitive1.getLineOrientation() == Primitive.VERTICAL && primitive2.getLineOrientation() == Primitive.HORIZONTAL) {
				height = primitive1.getSize();
				width = primitive2.getSize();
			}
			else if (primitive2.getLineOrientation() == Primitive.VERTICAL && primitive1.getLineOrientation() == Primitive.HORIZONTAL) {
				height = primitive2.getSize();
				width = primitive1.getSize();
			}
			Primitive primitive3 = (Primitive)sides.get(2);
			bottomEdge = new LinkedList();
			bottomEdge.add(primitive3);
			Primitive primitive4 = (Primitive)sides.get(3);
			leftEdge = new LinkedList();
			leftEdge.add(primitive4);
			allSides = new LinkedList();
			allSides.add(primitive1);
			allSides.add(primitive2);
			allSides.add(primitive3);
			allSides.add(primitive4);
			****/
		//}	
	}

  /**
	 * Sets the bounding box of the rectangle. The
	 * bounding box is stored as the upper left corner point
	 * and the lower right corner point.
	 * The height and the width of the rectangle are set 
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
		ListIterator lItr = allSides.listIterator(0);
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
	 * Returns the list of the primitives that make up the sides of the rectangle.
 	 *
	 * @param none
	 * @return The linked list of the side primitives of the rectangle
	 */
	public LinkedList getSides() {
		return allSides;
	}

  /**
	 * Returns the list of the four corners of the rectangle.
 	 *
	 * @param none
	 * @return The linked list of the corner points of the rectangle
	 */
	public LinkedList getCorners() {
		return corners;
	}

  /**
	 * Returns the height of the rectangle which is the height of the bounding box.
 	 *
	 * @param none
	 * @return The height of the rectangle
	 */
	public int getHeight() {
		return height;
	}

  /**
	 * Returns the width of the rectangle which is the width of the bounding box.
 	 *
	 * @param none
	 * @return The width of the rectangle
	 */
	public int getWidth() {
		return width;
	}

  /**
	 * Returns the upper left corner of the bounding box of the rectangle.
 	 *
	 * @param none
	 * @return The upper left corner point of the rectangle's bounding box
	 */
	public PointPixel getUpperLeft() {
		return upperLeft;
	}

  /**
	 * Returns the lower right corner of the bounding box of the rectangle.
 	 *
	 * @param none
	 * @return The lower right corner point of the rectangle's bounding box
	 */
	public PointPixel getLowerRight() {
		return lowerRight;
	}

  /**
	 * Returns the label number of the region the rectangle is in 
 	 *
	 * @param none
	 * @return The label number of the rectangle's region
	 */
	public int getRegionNo() {
		return regionLabelNo;
	}

  /**
	 * Sets the label number of the region the rectangle is in 
 	 *
	 * @param a The label number of the rectangle's region
	 */
	public void setRegionNo(int a) {
		regionLabelNo = a;
	}

  /**
	 * Sets whether the rectangle is a filled region or just lines.
 	 *
	 * @param c True if the rectangle is filled and false otherwise
	 */
	public void setIsFilledArea(boolean c) {
		isFilledArea = c;
	}

  /**
	 * Returns whether the rectangle is filled or not
 	 *
	 * @param none
	 * @return True if the rectangle is filled and false otherwise
	 */
	public boolean getIsFilledArea() {
		return isFilledArea;
	}

  /**
	 * Sets whether the rectangle is a vertical bar or not.
 	 *
	 * @param c True if the rectangle is a vertical bar and false otherwise
	 */
	public void setIsVerticalBar(boolean c) {
		isVerticalBar = c;
	}

  /**
	 * Sets whether the rectangle is a horizontal bar or not.
 	 *
	 * @param c True if the rectangle is a horizontal bar and false otherwise
	 */
	public void setIsHorizontalBar(boolean c) {
		isHorizontalBar = c;
	}

  /**
	 * Returns whether the rectangle is a vertical bar or not
 	 *
	 * @param none
	 * @return True if the rectangle is a vertical bar and false otherwise
	 */
	public boolean getIsVerticalBar() {
		return isVerticalBar;
	}

  /**
	 * Returns whether the rectangle is a horizontal bar or not
 	 *
	 * @param none
	 * @return True if the rectangle is a horizontal bar and false otherwise
	 */
	public boolean getIsHorizontalBar() {
		return isHorizontalBar;
	}

  /**
	 * Sets whether the rectangle is a bar holding a data point in the chart or not.
 	 *
	 * @param c True if the rectangle is a data point and false otherwise
	 */
	public void setIsData(boolean c) {
		isData = c;
	}

  /**
	 * Returns whether the rectangle holds a data point for the chart or not.
 	 *
	 * @param none
	 * @return True if the rectangle is a data point and false otherwise
	 */
	public boolean getIsData() {
		return isData;
	}

  /**
	 * Sets whether the rectangle is a legend in the chart or not.
 	 *
	 * @param c True if the rectangle is a legend and false otherwise
	 */
	public void setIsLegend(boolean c) {
		isLegend = c;
	}

  /**
	 * Returns whether the rectangle is a legend for the chart or not.
 	 *
	 * @param none
	 * @return True if the rectangle is a legend and false otherwise
	 */
	public boolean getIsLegend() {
		return isLegend;
	}

  /**
	 * Returns the color of the rectangle. It is the color of the inner region
	 * if the rectangle is filled and the color of the border line if
	 * the rectangle is not filled but defined by lines only.
 	 *
	 * @param none
	 * @return The color value of the rectangle in the image
	 */
	public int getColor() {
		return color;
	}

  /**
	 * Sets the color of the rectangle. It is the color of the inner region
	 * if the rectangle is filled and the color of the border line if
	 * the rectangle is not filled but defined by lines only.
 	 *
	 * @param c The color value of the rectangle in the image 
	 */
	public void setColor(int c) {
		color = c;
	}

  /**
	 * Returns the data value the rectangle depicts in the chart.
 	 *
	 * @param none
	 * @return The data value for the rectangle (the bar)
	 */
	public PointPixel getValue() {
		return value;
	}

  /**
	 * Sets the data value of the rectangle. The point of the 
	 * data value is the middle point at the top side of a vertical bar and 
	 * the middle point at the right side of a horizontal bar.
 	 *
	 * @param c The point at which the data of the rectangle (the bar) is depicted in the chart
	 */
	public void setValue(PointPixel c) {
		value = c;
	}

    public void initAnnotationWords() {
	annotationWords = new LinkedList();}

    public void initAnnotationBlocks() {
	annotationBlocks= new LinkedList();}

    public void addToAnnotationWords(Word w) {
	annotationWords.add(w);}

    public void addToAnnotationBlocks(TextBlock w) {
	annotationBlocks.add(w);}

    public LinkedList getAnnotationWords() {
	return annotationWords;}

    public LinkedList getAnnotationBlocks() {
	return annotationBlocks;}

    public void initLabelWords() {
	labelWords = new LinkedList();}

    public void initLabelBlocks() {
	labelBlocks = new LinkedList();}

    public void addToLabelWords(Word w) {
	labelWords.add(w);}

    public void addToLabelBlocks(TextBlock w) {
	labelBlocks.add(w);}

    public LinkedList getLabelWords() {
	return labelWords;}

    public LinkedList getLabelBlocks() {
	return labelBlocks;}

  /**
	 * Returns a string holding information about the rectangle
	 * to be printed on the screen.
 	 *
	 * @param none
	 * @return The string of information for the rectangle
	 */
	public String toString() {
		String message = new String ("Rectangle in region "+regionLabelNo+": ");
		if (isFilledArea) {
			message = message + ("Filled, ");
		}
		if (isLegend) {
			message = message + ("Legend, ");
		}
		if (isVerticalBar) {
			message = message + ("Vertical Bar, Value = "+value+", ");
		}
		else if (isHorizontalBar) {
			message = message + ("Horizontal Bar, Value = "+value+", ");
		}
		if (height == width) {
			message = (message + ("Square, "));
		}
		else if (orientation == VERTICAL) {
			message = (message + ("Vertical, "));
		}
		else {
			message = (message + ("Horizontal, "));
		}
		message = (message + ("Height = "+height+", Width = "+width+", Color = "+color+"\n"));

		return message;
	}

/******
METHODS BELOW ARE NOT USED
	public Rectangle(LinkedList l1, LinkedList l2, LinkedList l3, LinkedList l4) {
		setRectangle(l1,l2, l3, l4, VERTICAL);
	}

	public Rectangle(LinkedList l1, LinkedList l2, LinkedList l3, LinkedList l4, int o) {
		setRectangle(l1, l2, l3, l4, o);
	}

	private void setRectangle( LinkedList l1, LinkedList l2, LinkedList l3, LinkedList l4, int o ) {
		ListIterator i1 = l1.listIterator();
		ListIterator i2 = l2.listIterator();
		ListIterator i3 = l3.listIterator();
		ListIterator i4 = l4.listIterator();
		Primitive p1 = (Primitive)(i1.next());
		Primitive p2 = (Primitive)(i2.next());
		Primitive p3 = (Primitive)(i3.next());
		Primitive p4 = (Primitive)(i4.next());
		Primitive leftMost=null;
		Primitive topMost=null;
		//Find the vertical edges
		//Find the horizontal edges
		if( (Math.abs(p1.getAngle() - 90) < 2) && (Math.abs(p2.getAngle() - 90) < 2) ) {
			if( p1.getBeginPoint().getColumn() < p2.getBeginPoint().getColumn() ) {
				leftEdge = l1;
				rightEdge = l2;
			}
			else {
				leftEdge = l2;
				rightEdge = l1;
			}
			if( p3.getBeginPoint().getRow() < p4.getBeginPoint().getRow() ) {
				topEdge = l3;
				bottomEdge = l4;
			}
			else {
				topEdge = l4;
				bottomEdge = l3;
			}
		}
		else if( (Math.abs(p1.getAngle() - 90) < 2) && (Math.abs(p3.getAngle() - 90) < 2) ) {
			if( p1.getBeginPoint().getColumn() < p3.getBeginPoint().getColumn() ) {
				leftEdge = l1;
				rightEdge = l3;
			}
			else {
				leftEdge = l3;
				rightEdge = l1;
			}

			if( p2.getBeginPoint().getRow() < p4.getBeginPoint().getRow() ) {
				topEdge = l2;
				bottomEdge = l4;
			}
			else {
				topEdge = l4;
				bottomEdge = l2;
			}
		}
		else if( (Math.abs(p1.getAngle() - 90) < 2) && (Math.abs(p4.getAngle() - 90) < 2) ) {
			if( p1.getBeginPoint().getColumn() < p4.getBeginPoint().getColumn() ) {
				leftEdge = l1;
				rightEdge = l4;
			}
			else {
				leftEdge = l4;
				rightEdge = l1;
			}

			if( p2.getBeginPoint().getRow() < p3.getBeginPoint().getRow() ) {
				topEdge = l2;
				bottomEdge = l3;
			}
			else {
				topEdge = l3;
				bottomEdge = l2;
			}
		}
		else if( (Math.abs(p2.getAngle() - 90) < 2) && (Math.abs(p3.getAngle() - 90) < 2) ) {
			if( p2.getBeginPoint().getColumn() < p3.getBeginPoint().getColumn() ) {
				leftEdge = l2;
				rightEdge = l3;
			}
			else {
				leftEdge = l3;
				rightEdge = l2;
			}

			if( p1.getBeginPoint().getRow() < p4.getBeginPoint().getRow() ) {
				topEdge = l1;
				bottomEdge = l4;
			}
			else {
				topEdge = l4;
				bottomEdge = l1;
			}
		}
		else if( (Math.abs(p2.getAngle() - 90) < 2) && (Math.abs(p4.getAngle() - 90) < 2) ) {
			if( p2.getBeginPoint().getColumn() < p4.getBeginPoint().getColumn() ) {
				leftEdge = l2;
				rightEdge = l4;
			}
			else {
				leftEdge = l4;
				rightEdge = l2;
			}

			if( p1.getBeginPoint().getRow() < p3.getBeginPoint().getRow() ) {
				topEdge = l1;
				bottomEdge = l3;
			}
			else {
				topEdge = l3;
				bottomEdge = l1;
			}
		}
		else {
			if( p3.getBeginPoint().getColumn() < p4.getBeginPoint().getColumn() ) {
				leftEdge = l3;
				rightEdge = l4;
			}
			else {
				leftEdge = l4;
				rightEdge = l3;
			}
			if( p1.getBeginPoint().getRow() < p2.getBeginPoint().getRow() ) {
				topEdge = l1;
				bottomEdge = l2;
			}
			else {
				topEdge = l2;
				bottomEdge = l1;
			}
		}
		orientation = o;
		ListIterator lItrH = leftEdge.listIterator(); {
			height += ((Primitive)(lItrH.next())).getSize();
		}
		ListIterator lItrW = topEdge.listIterator();
		while(lItrW.hasNext() ) {
			width += ((Primitive)(lItrW.next())).getSize();
		}
	}

	public LinkedList getTopEdge() {
		return topEdge;
	}

	public LinkedList getLeftEdge() {
		return leftEdge;
	}

	public LinkedList getBottomEdge() {
		return bottomEdge;
	}

	public LinkedList getRightEdge() {
		return rightEdge;
	}

******/
}

import java.util.LinkedList;
import java.util.ListIterator;

/**
 * A class to hold information for a connected line.
 * Everything on the image that is not an axis, a tickmark or
 * a rectangle is made a connected line.
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class ConnectedLine {
	private LinkedList primitives;
	private int orientation;
	private int height;
	private int width;
	private int regionLabelNo;
	private int color;
	private int noOfPixels;
	
	private final int VERTICAL = 0;
	private final int HORIZONTAL = 1;
	private boolean isFilledArea;
	private boolean isDataLine;
	private PointPixel upperLeft;
	private PointPixel lowerRight;

	/**
	 * Constructor.
	 * 
	 * @param aPrim A primitive that is part of the connected line
	 * @param isFilled True if the connected line is a filled region and false otherwise
	 */
	public ConnectedLine(Primitive aPrim, boolean isFilled) {
		isFilledArea = isFilled;
		isDataLine = false;
		primitives = new LinkedList();
		primitives.add(aPrim);
		regionLabelNo = aPrim.getParent();
		color = -1;
		noOfPixels = aPrim.getSize();
	}

	/**
	 * Constructor.
	 * 
	 * @param aList A linked list of primitives that are part of the connected line
	 * @param isFilled True if the connected line is a filled region and false otherwise
	 */
	public ConnectedLine(LinkedList aList, boolean isFilled) {
		isFilledArea = isFilled;
		isDataLine = false;
		primitives = aList;
		Primitive aPrim = (Primitive)aList.getFirst();
		regionLabelNo = aPrim.getParent();
		color = -1;
		noOfPixels = 0;
		ListIterator lItr = primitives.listIterator(0);
		while (lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			noOfPixels += aPrim.getSize();
		}
	}

	/**
	 * Adds a primitive to the primitives list of the connected line.
	 * 
	 * @param aPrim A primitive that is part of the connected line to be added
	 */
	public void addPrimitive(Primitive aPrim) {
		primitives.add(aPrim);
		noOfPixels += aPrim.getSize();
	}

	/**
	 * Sets the bounding box of the connected line. The
	 * bounding box is stored as the upper left corner point
	 * and the lower right corner point.
	 * The height and the width of the rectangle are set
	 * as the height and the width of the bounding box.
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
		ListIterator lItr2;
		ListIterator lItr = primitives.listIterator(0);
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
	 * Returns the list of the primitives that make up the connected line.
	 *
	 * @param none
	 * @return The linked list of the primitives of the connected line 
	 */
	public LinkedList getPrimitives() {
		return primitives;
	}

  /**
	 * Returns the number of primitives that make up the connected line.
	 *
	 * @param none
	 * @return The number of primitives of the connected line 
	 */
	public int getNumPrimitives() {
		return primitives.size();
	}

  /**
	 * Returns the number of pixels that make up the connected line.
	 *
	 * @param none
	 * @return The number of pixels of the connected line 
	 */
	public int getNoOfPixels() {
		return noOfPixels;
	}

  /**
	 * Returns the label number of the region the connected line is in.
	 *
	 * @param none
	 * @return The label number of the connected line's region
	 */
	public int getRegionNo() {
		return regionLabelNo;
	}

  /**
	 * Sets the label number of the region the connected line is in.
	 *
	 * @param a The label number of the connected line's region
	 */
	public void setRegionNo(int a) {
		regionLabelNo = a;
	}

  /**
	 * Sets whether the connected line is a filled region or not
	 * 
	 * @param c True if the connected line is a filled region and false otherwise
	 */
	public void setIsFilledArea(boolean c) {
		isFilledArea = c;
	}

  /**
	 * Returns whether the connected line is a filled region or not
	 * 
	 * @param none
	 * @return True if the connected line is a filled region and false otherwise
	 */
	public boolean getIsFilledArea() {
		return isFilledArea;
	}

	/**
	 * Sets whether the connected line is a data line in a line chart or not
	 * 
	 * @param c True if the connected line is a data line and false otherwise
	 */
	public void setIsDataLine(boolean c) {
		isDataLine = c;
	}

	/**
	 * Returns whether the connected line is a data line in a line chart or not
	 * 
	 * @param none
	 * @return True if the connected line is a data line and false otherwise
	 */
	public boolean getIsDataLine() {
		return isDataLine;
	}

	/**
	 * Returns the upper left corner point of the bounding box of the connected line.
	 * 
	 * @param none
	 * @return The upper left corner point of the bounding box of the connected line
	 */
	public PointPixel getUpperLeft() {
		return upperLeft;
	}

	/**
	 * Returns the lower right corner point of the bounding box of the connected line.
	 * 
	 * @param none
	 * @return The lower right corner point of the bounding box of the connected line
	 */
	public PointPixel getLowerRight() {
		return lowerRight;
	}

	/**
	 * Returns the height of the connected line which is the height of the bounding box.
	 *
	 * @param none
	 * @return The height of the connected line's bounding box 
	 */
	public int getBoxHeight() {
		return height;
	}

	/**
	 * Returns the width of the connected line which is the width of the bounding box.
	 *
	 * @param none
	 * @return The width of the connected line's bounding box 
	 */
	public int getBoxWidth() {
		return width;
	}

	/**
	 * Returns the color value of the connected line in the image.
	 *
	 * @param none
	 * @return The color value of the connected line
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Sets the color value of the connected line in the image.
	 *
	 * @param c The color value of the connected line
	 */
	public void setColor(int c) {
		color = c;
	}

	/**
	 * Returns a string holding information about the connected line 
	 * to be printed on the screen.
	 *
	 * @param none
	 * @return The string of information for the connected line 
	 */
	public String toString() {
		String message = new String ("ConnectedLine in region "+regionLabelNo+": ");
		if (isFilledArea) {
			message = message + ("Filled, ");
		}	
		if (isDataLine) {
			message = message + ("Data line, ");
		}	
		if (orientation == VERTICAL) {
			message = message + ("Vertical, ");
		}
		else if (orientation == HORIZONTAL) {
			message = message + ("Horizontal, ");
		}
		message = message + getNumPrimitives() +(" primitives, Color = "+color+"\n");
		return message;
	}
}

import java.util.*;
import java.awt.Point;

/**
 * A class to hold the pixels.
 * 
 * @author Chart Reading project
 * @version 1.0
 */
public class PointPixel extends Point {
	final static int [] rPos = {-1, -1, -1, 0, 1, 1, 1, 0};  //already declared in ImagePrimitives.java
	final static int [] cPos = {1, 0, -1, -1, -1, 0, 1, 1};  //already declared in ImagePrimitives.java
	private int row;
	private int column;
	private int value;
	private int rowCount;
	private int columnCount;

	private int labelNo;
	/*
	private int tagNo;
	private boolean startPixel;
	private boolean endPixel;
	*/

	private LinkedList annotationWords;
	private LinkedList labelWords;
	private double yValue;
        private boolean realValue;
        private String unit = "";
        private String scale = "";
        private TextBlock annotationTextBlock = null;

	/**
	 * Constructor. Row and column numbers are set to 0. 
	 * The color value is set to 0.
	 * Row count and column count are set to 1.
	 * 
	 * @param none
	 */
	public PointPixel() {
		super();
		row = 0;
		column = 0;
		rowCount = 1;
		columnCount = 1;
		move(0, 0);
		value = 0;
                realValue = false;
	}

	/**
	 * Constructor.
	 * The color value is set to 0.
	 * Row count and column count are set to 1.
	 * 
	 * @param x The row number of the pixel
	 * @param y The column number of the pixel 
	 */
	public PointPixel(int x, int y) {
		super(x, y);
		row = x;
		column = y;
		rowCount = 1;
		columnCount = 1;
		value = 0;
	}
	
	/**
	 * Constructor.
	 * Row count and column count are set to 1.
	 * 
	 * @param x The row number of the pixel
	 * @param y The column number of the pixel 
	 * @param v The color value of the pixel 
	 */
	public PointPixel(int x, int y, int v) {
		super(x, y);
		row = x;
		column = y;
		rowCount = 1;
		columnCount = 1;
		value = v;
	}

	/**
	 * Constructor.
	 * The color value is set to 0.
	 * Row count and column count are set to 1.
	 * 
	 * @param o A <code>Point</code> object from which the row and column numbers are copied
	 */
	public PointPixel(Object o) {
		super();
		row = (int)((Point)o).getX();
		column = (int)((Point)o).getY();
		rowCount = 1;
		columnCount = 1;
		move(row, column);
		value = 0;
	}

	/**
	 * Constructor.
	 * The color value is set to 0.
	 * Row count and column count are set to 1.
	 * 
	 * @param p A <code>Point</code> object from which the row and column numbers are copied
	 */
	public PointPixel(Point p) {
		super();
		row = (int)p.getX();
		column = (int)p.getY();
		rowCount = 1;
		columnCount = 1;
		move(row, column);
		value = 0;
	}

	/**
	 * Returns the row number. 
	 * 
	 * @param none
	 * @return The row number
	 */
	public int getRow() {
		return row;
	}

	/**
	 * Returns the column number. 
	 * 
	 * @param none
	 * @return The column number
	 */
	public int getColumn() {
		return column;
	}

	/**
	 * Sets the row number. 
	 * 
	 * @param r The row number to be set to 
	 */
	public void setRow(int r) {
		row = r;
	}

	/**
	 * Sets the column number. 
	 * 
	 * @param c The column number to be set to 
	 */
	public void setColumn(int c) {
		column = c;
	}

	/**
	 * Changes the row and column numbers to the given values.
	 * 
	 * @param r The row number to be set 
	 * @param c The column number to be set 
	 */
	public void move(int r, int c) {
		row = r;
		column = c;
	}

	/**
	 * Changes the row and column numbers by an offset given by the parameters.
	 * 
	 * @param r The number of rows the row number is to be increased
	 * @param c The number of columns the row number is to be increased
	 */
	public void translate(int r, int c) {
		row = row + r;
		column = column + c;
	}

	/**
	 * Returns the row count; the number of pixels on the same row 
	 * that follow the current one. 
	 * 
	 * @param none
	 * @return The row count 
	 */
	public int getRowCount() {
		return rowCount;
	}

	/**
	 * Sets the row count; the number of pixels on the same row 
	 * that follow the current one. 
	 * 
	 * @param r The row count to be set 
	 */
	public void setRowCount(int r) {
		rowCount = r;
	}

	/**
	 * Returns the column count; the number of pixels on the same column  
	 * that follow the current one. 
	 * 
	 * @param none
	 * @return The column count 
	 */
	public int getColumnCount() {
		return columnCount;
	}

	/**
	 * Sets the column count; the number of pixels on the same column 
	 * that follow the current one. 
	 * 
	 * @param c The column count to be set 
	 */
	public void setColumnCount(int c) {
		columnCount = c;
	}

	/**
	 * Sets the color value of the current pixel.
	 * 
	 * @param v The color value
	 */
	public void setValue(int v) {
		value = v;
	}

	/**
	 * Returns the color value of the current pixel.
	 * 
	 * @param none
	 * @return The color value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * Sets the label number of the region the current pixel is in.
	 * 
	 * @param l The label number
	 */
	public void setLabelNo(int l) {
		labelNo = l;
	}

	/**
	 * Returns the label number of the region the current pixel is in. 
	 * 
	 * @param none
	 * @return The label number 
	 */
	public int getLabelNo() {
		return labelNo;
	}

	/*******************
	public void setTag(int t) {
		tagNo = t;
	}
	public int getTag() {
		return tagNo;
	}
	public void setStartPixel(boolean b) {
		startPixel = b;
	}
	public boolean isStartPixel() {
		return startPixel;
	}
	public void setEndPixel(boolean b) {
		endPixel = b;
	}
	public boolean isEndPixel() {
		return endPixel;
	}
	********************/

	/**
	 * Determines if the given <code>PointPixel</code> has the
	 * same row and the same column as the current one.
	 * 
	 * @param p The <code>PointPixel</code> the current pixel will be compared to 
	 * @return True if the row and the column numbers are the same, and false otherwise
	 */
	public boolean equals(PointPixel p) {
		if (p.getRow() == getRow() && p.getColumn() == getColumn())
			return true;
		else 
			return false;
	}

	/**
	 * Determines if the given <code>Point</code> has the
	 * same row and the same column as the current one.
	 * 
	 * @param p The <code>Point</code> the current pixel will be compared to 
	 * @return True if the row and the column numbers are the same, and false otherwise
	 */
	public boolean equals(Point p) {
		if ((int)p.getX() == getRow() && (int)p.getY() == getColumn())
			return true;
		else 
			return false;
	}

	/**
	 * Determines if the current pixel is a neighbor of the 
	 * given <code>PointPixel</code> 
	 * using the 4 neighbors.
	 * 
	 * @param p The <code>PointPixel</code> the current pixel will be compared to 
	 * @return True if the pixels are neighbors and false otherwise
	 */
	public boolean neigbors4(PointPixel p) {
		boolean isNeigbor = false;
		for (int i = 1; i <8; i+=2) {
			PointPixel neigPoint = new PointPixel(row+rPos[i], column+cPos[i]);
			if (p.equals(neigPoint)) {
				isNeigbor = true;
				break;
			}
		}
		return isNeigbor;
 	}

	/**
	 * Determines if the current pixel is a neighbor of the 
	 * given <code>PointPixel</code> 
	 * using the 8 neighbors.
	 * 
	 * @param p The <code>PointPixel</code> the current pixel will be compared to 
	 * @return True if the pixels are neighbors and false otherwise
	 */
	public boolean neigbors8(PointPixel p) {
		boolean isNeigbor = false;
		for (int i = 0; i <8; i++) {
			PointPixel neigPoint = new PointPixel(row+rPos[i], column+cPos[i]);
			if (p.equals(neigPoint)) {
				isNeigbor = true;
				break;
			}
		}
		return isNeigbor;
 	}

	/**
	 * Determines if the current pixel is a neighbor of the 
	 * given <code>PointPixel</code> 
	 * using the immediate 8 neighbors and the 8 neighbors of those.
	 * 
	 * @param p The <code>PointPixel</code> the current pixel will be compared to 
	 * @return True if the pixels are neighbors and false otherwise
	 */
	public boolean neigbors24(PointPixel p) {
		boolean isNeigbor = false;
		for (int i = 0; i <8; i++) {
			PointPixel neigPoint = new PointPixel(row+rPos[i], column+cPos[i]);
			if (p.equals(neigPoint)) {
				isNeigbor = true;
				break;
			}
			if (neigPoint.neigbors8(p)) {
				isNeigbor = true;
				break;
			}
		}
		return isNeigbor;
 	}

	/**
	 * Determines if the current pixel is a neighbor of the 
	 * given <code>Point</code> 
	 * using the 4 neighbors.
	 * 
	 * @param p The <code>Point</code> the current pixel will be compared to 
	 * @return True if the pixels are neighbors and false otherwise
	 */
	public boolean neigbors4(Point p) {
		boolean isNeigbor = false;
		for (int i = 1; i <8; i+=2) {
			Point neigPoint = new Point(row+rPos[i], column+cPos[i]);
			if (p.equals(neigPoint)) {
				isNeigbor = true;
				break;
			}
		}
		return isNeigbor;
 	}

	/**
	 * Determines if the current pixel is a neighbor of the 
	 * given <code>Point</code> 
	 * using the 8 neighbors.
	 * 
	 * @param p The <code>Point</code> the current pixel will be compared to 
	 * @return True if the pixels are neighbors and false otherwise
	 */
	public boolean neigbors8(Point p) {
		//System.out.println("Current point: ("+x+", "+y+").");
		//System.out.println("Isneigbor point: "+p);
		boolean isNeigbor = false;
		for (int i = 0; i <8; i++) {
			Point neigPoint = new Point(row+rPos[i], column+cPos[i]);
			if (p.equals(neigPoint)) {
				isNeigbor = true;
				break;
			}
		}
		return isNeigbor;
 	}

	/**
	 * Determines if the current pixel is a neighbor of the 
	 * given <code>Point</code> 
	 * using the immediate 8 neighbors and the 8 neighbors of those.
	 * 
	 * @param p The <code>Point</code> the current pixel will be compared to 
	 * @return True if the pixels are neighbors and false otherwise
	 */
	public boolean neigbors24(Point p) {
		boolean isNeigbor = false;
		for (int i = 0; i <8; i++) {
			Point neigPoint = new Point(row+rPos[i], column+cPos[i]);
			if (p.equals(neigPoint)) {
				isNeigbor = true;
				break;
			}
			if ((new PointPixel(neigPoint)).neigbors8(p)) {
				isNeigbor = true;
				break;
			}
		}
		return isNeigbor;
 	}

	/**
	 * Returns the linked list of the 4 neighbors of the current pixel.
	 * 
	 * @param none
	 * @return Linked list of neighboring <code>PointPixel</code>s
	 */
	public LinkedList getNeigbors4() {
		LinkedList neigbors = new LinkedList();
		for (int i = 1; i < 8; i+=2) {
			Point neigPoint = new Point(row+rPos[i], column+cPos[i]);
			neigbors.add(neigPoint);
		}
		return neigbors;
	}

	/**
	 * Returns the linked list of the 8 neighbors of the current pixel.
	 * 
	 * @param none
	 * @return Linked list of neighboring <code>PointPixel</code>s
	 */
	public LinkedList getNeigbors8() {
		LinkedList neigbors = new LinkedList();
		for (int i = 0; i < 8; i++) {
			Point neigPoint = new Point(row+rPos[i], column+cPos[i]);
			neigbors.add(neigPoint);
		}
		return neigbors;
	}

	/**
	 * Returns the neigbor numbers of the given pixel's
	 * neigbors that are in the same region as itself.
	 * The input list of neigbors are 8 neigbors of the current pixel.
	 * The neigbors need to have their values set.
	 * The neigbors are numbered from 0 to 7 beginning from the upper right
	 * neigbor, going counter clockwise and ending at the right neigbor.
	 *
	 * @param neigbors The linked list of all the neighbors
	 * @return The array of neighbors that are in the same region
	 */
	public int[] getRegionNeigborNumbers(LinkedList neigbors) {
		int i = 0;
		int k = 0;
		PointPixel aNeigbor;
		int[] neigborArray = new int[8];
		ListIterator lItr = neigbors.listIterator(0);
		while (lItr.hasNext()) {
			aNeigbor = (PointPixel)lItr.next();
			if (aNeigbor.getValue() == value) {
				neigborArray[i] = k;
				i++;
			}
			k++;
		}	
		return neigborArray;
	}

	/**
	 * Returns a <code>String</code> to print the <code>PointPixel</code> 
	 * 
	 * @return The string that hold the row and the column number of the curren pixel
	 */
	public String toString() {
	 	return ("("+getRow()+", "+getColumn()+")");
	}

    public void initAnnotationWords() {
	annotationWords = new LinkedList();}

    public void addToAnnotationWords(Word w) {
	annotationWords.add(w);}

    public LinkedList getAnnotationWords() {
	return annotationWords;}

    public void initLabelWords() {
	labelWords = new LinkedList();}

    public void addToLabelWords(Word w) {
	labelWords.add(w);}

    public LinkedList getLabelWords() {
	return labelWords;}

    public void setYValue(double v) {
	yValue = v;}

    public double getYValue() {
	return yValue;}

    public void setUnit(String str) {unit = str;}
    public String getUnit() {return unit;}
    public void setScale(String str) {scale = str;}
    public String getScale() {return scale;}
    public void setHasValue() { realValue = true;}
    public boolean hasValue() {return realValue;}
    public void setAnnotationTextBlock(TextBlock tb) {annotationTextBlock = tb;}
    public TextBlock getAnnotationTextBlock() {return annotationTextBlock;}

}

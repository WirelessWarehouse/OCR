import java.util.*;

/**
 * A class to hold the pixel information.
 * 
 * @author Chart Reading project
 * @version 1.0
 */
public class PixelData {
	final static public int STARTPOINT = 0;
	final static public int ENDPOINT = 1;
	final static public int INTERIORPOINT = -1;

	private int value;
	private int labelNo;
	private int tagNo;
	private int position;
	private int row;
	private int column;

  /**
	 * Constructor. Row and column numbers are set to -1.
	 * The color value is set to -1.
	 * The tag and label numbers are set to -1.
	 * The position is set to interior point.
	 *
	 * @param none
	 */
	public PixelData() {
		value = -1;
		labelNo = -1;
		tagNo = -1;
		position = INTERIORPOINT;
		row = -1;
		column = -1;
	}

  /**
	 * Constructor. Row and column numbers are set to -1.
	 * The color value is set to -1.
	 * The position is set to interior point.
	 *
	 * @param l The label number 
	 * @param t The tag number
	 */
	public PixelData(int l, int t) {
		value = -1;
		labelNo = l;
		tagNo = t;
		position = INTERIORPOINT;
		row = -1;
		column = -1;
	}

  /**
	 * Constructor. Row and column numbers are set to -1.
	 * The color value is set to -1.
	 *
	 * @param l The label number 
	 * @param t The tag number
	 * @param p The position; starting point, end point or interior point 
	 */
	public PixelData(int l, int t, int p) {
		value = -1;
		labelNo = l;
		tagNo = t;
		position = p; 
		row = -1;
		column = -1;
	}

  /**
	 * Constructor. 
	 * The color value is set to -1.
	 *
	 * @param l The label number 
	 * @param t The tag number
	 * @param p The position; starting point, end point or interior point 
	 * @param r The row number 
	 * @param c The column number
	 */
	public PixelData(int l, int t, int p, int r, int c) {
		value = -1;
		labelNo = l;
		tagNo = t;
		position = p; 
		row = r;
		column = c;
	}

  /**
	 * Constructor. 
	 *
	 * @param l The label number 
	 * @param t The tag number
	 * @param p The position; starting point, end point or interior point 
	 * @param r The row number 
	 * @param c The column number
	 * @param v The color value
	 */
	public PixelData(int l, int t, int p, int r, int c, int v) {
		value = v;
		labelNo = l;
		tagNo = t;
		position = p; 
		row = r;
		column = c;
	}

  /**
	 * Sets the color value of the pixel.
	 *
	 * @param v The color value
	 */
	public void setValue(int v) {
		value = v;
	}

  /**
	 * Returns the color value of the current pixel.
	 *
	 * @return The color value
	 */
	public int getValue() {
		return value;
	}

  /**
	 * Sets the label number of the pixel.
	 *
	 * @param l The label number 
	 */
	public void setLabel(int l) {
		labelNo = l;
	}

  /**
	 * Returns the label number of the current pixel.
	 *
	 * @return The label number 
	 */
	public int getLabel() {
		return labelNo;
	}

  /**
	 * Sets the tag number of the pixel.
	 *
	 * @param t The tag number 
	 */
	public void setTag(int t) {
		tagNo = t;
	}

  /**
	 * Returns the tag number of the current pixel.
	 *
	 * @return The tag number 
	 */
	public int getTag() {
		return tagNo;
	}

  /**
	 * Returns the row number of the current pixel.
	 *
	 * @return The row number 
	 */
	public int getRow() {
		return row;
	}

  /**
	 * Returns the column number of the current pixel.
	 *
	 * @return The column number 
	 */
	public int getColumn() {
		return column;
	}

  /**
	 * Sets the position of the pixel; start, end or interior point.
	 *
	 * @param p The position
	 */
	public void setPosition(int p) {
		position = p;
	}

  /**
	 * Returns the position of the current pixel; start, end or interior point.
	 *
	 * @return The position 
	 */
	public int getPosition() {
		return position;
	}

  /**
	 * Returns the <code>String</code> that holds the label, tag and 
	 * position information for the current pixel.
	 *
	 * @return The pixel information
	 */
	public String toString() {
	 	return ("("+getLabel()+", "+getTag()+", "+getPosition()+")");
	}
}

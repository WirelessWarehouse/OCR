import java.util.HashMap;
import java.util.Collection;
import java.util.*;
import java.lang.Integer;
import java.awt.Point;

/**
 * A class to hold information for a region of the image.
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class Region {

	private int RegionNo;
	private int numChains;
	private int numPixels;
	private int nextKey;
	private int orientation;
	private int color;
	private boolean isFrame;
	private boolean isCharacter;
	private boolean isThickLine;
	private boolean isDashedLine;
	private boolean isFilledArea;
	private boolean isGridline;
	private HashMap chains;
	private LinkedList allPixels;
	private PointPixel upperLeft;
	private PointPixel lowerRight;

  /**
	 * Constructor.
	 *
	 * @param labelNo The label number of the region
	 */
	public Region (int labelNo) {
		RegionNo = labelNo;
		color = -1;
		numChains = 0;
		numPixels = 0;
		chains = new HashMap(10);
		nextKey = 0;
		isFrame = false;
		isCharacter = false;
		isThickLine = false;
		isDashedLine = false;
		isFilledArea = false;
		isGridline = false;
		orientation = -1;
		allPixels = new LinkedList();
	}

  /**
	 * Constructor.
	 *
	 * @param labelNo The label number of the region
	 * @param p A primitive in the region to be added to the region's primitive hash table
	 */
	public Region(int labelNo, Primitive p) {
		RegionNo = labelNo;
		color = -1;
		numChains = 1;
		chains = new HashMap(10);
		chains.put(new Integer(0), p);
		p.setTagNo(0);
		nextKey = 1;
		isFrame = false;
		isCharacter = false;
		isThickLine = false;
		isDashedLine = false;
		isFilledArea = false;
		isGridline = false;
		orientation = -1;
		//System.out.println("Added Primitive: " + (nextKey - 1) + "to Label: " + labelNo );
	}

  /**
	 * Adds a pixel to the region's pixel list.
	 *
	 * @param p The pixel to be added to the region
	 */
	public void addPixelToRegion(PointPixel p) {
		allPixels.add(p);
		numPixels++;
	}

  /**
	 * Adds a primitive to the region's primitive hash table.
	 * The primitive's tag number is set.
	 *
	 * @param p The primitive to be added to the region
	 */
	public void addPrimitiveToRegion(Primitive p) {
		chains.put(new Integer(nextKey), p);
		p.setTagNo(nextKey);
		numChains++;
		nextKey++;
		//numPixels = numPixels + p.getSize();
		//System.out.println("Added Primitive: " + (nextKey-1) + "to Label: " + RegionNo );
	}

  /**
	 * Removes a primitive from the region's primitive hash table.
	 *
	 * @param key The key of the primitive to be removed from the hash table 
	 */
	public void removePrimitive(int key) {
		int size = ((Primitive)chains.get(new Integer(key))).getSize();
		chains.remove(new Integer(key));
		numChains--;
		//numPixels = numPixels - size;
		//System.out.println("Removed Primitive: " + key + "from Label: " + RegionNo+" There are "+numChains+" left, "+chains.size()+" entries.");
	}
	
  /**
	 * Fits a straight line to the pixels of this region.
	 *
	 * @param none
	 * @return The vector holding information of the fitted line; the orientation, the slope and the row/column intercept
	 */
	public Vector fitLine() {
		LineFitter aLineFitter = new LineFitter();
		Vector fitVector = aLineFitter.fitStraightLineLSE(allPixels);
		int lineOrientation = ((Integer)fitVector.get(0)).intValue();
		double slope = ((Double)fitVector.get(1)).doubleValue();
		double intercept = ((Double)fitVector.get(2)).doubleValue();
		double angle;
		//Calculate the angle
		//angle is from the row axis -the vertical axis, going counter clockwise
		if (lineOrientation == Primitive.HORIZONTAL) {
			angle = (90 - Math.toDegrees(Math.atan(slope)));
			//System.out.println("Region "+label+": Horizontal line. Slope: "+slope+", intercept: "+intercept+", angle: "+angle);
		}
		else { //vertical
			angle = (Math.toDegrees(Math.atan(slope)));
			if (angle < 0)
				angle = 180 + angle;
			//System.out.println("Region "+label+": Vertical line. Slope: "+slope+", intercept: "+intercept+", angle: "+angle);
		}
		fitVector.add(new Double(angle));
		return fitVector;
	}

  /**
	 * Returns the primitives of the region.
	 *
	 * @param none
	 * @return The collection of the primitives in the region
	 */
	public Collection getPrimitiveList() {
		return chains.values();
	}

  /**
	 * Returns the pixels of the region.
	 *
	 * @param none
	 * @return The linked list of the pixels in the region
	 */
	public LinkedList getPixelList() {
		return allPixels; 
	}

  /**
	 * Sets the pixel list to empty and the number of pixels to zero.
	 *
	 * @param none
	 */
	public void clearPixelList() {
		allPixels = new LinkedList();
		numPixels = 0;
	}

  /**
	 * Given the key of the primitive in the hash table, 
	 * returns the primitive.
	 *
	 * @param key The key in the hash table of primitives
	 * @return The primitive corresponding to the given key
	 */
	public Primitive getPrimitive(int key) {
		//This function should raise an exception if the key is not found
		return (Primitive)chains.get(new Integer(key));
	}

  /**
	 * Returns the number of primitives in the region.
	 *
	 * @param none
	 * @return The number of primitives in the region
	 */
	public int getNumChains() {
		return numChains;
	}

  /**
	 * Returns the number of pixels in the region.
	 *
	 * @param none
	 * @return The number of pixels in the region
	 */
	public int getNumPixels() {
		//return allPixels.size();
                return numPixels;
	}

  /**
	 * Returns the number of primitives in the region.
	 *
	 * @param none
	 * @return The number of primitives in the region
	 */
	public int getSize() {
		return chains.size();
	}

  /**
	 * Returns the next tag number the next primitive to be added
	 * to the region will have. This is the key of the
	 * primitives hash table.
	 *
	 * @param none
	 * @return The next key or tag for the next primitive
	 */
	public int getNextPrimitiveTag() {
		return nextKey;
	}

	/**
	 * Increments the number of primitives in the region by the given amount.
	 * 
	 * @param v The amount that the number of primitives will be incremented by
	 */
	public void incrNumChains(int v) {
		numChains += v;
	}

	/**
	 * Decrements the number of primitives in the region by the given amount.
	 * 
	 * @param v The amount that the number of primitives will be decremented by
	 */
	public void decrNumChains(int v) {
		numChains -= v;
	}

	/**
	 * Returns the label number of the region.
	 * 
	 * @param none
	 * @return The label number of the region
	 */
	public int getRegion() {
		return RegionNo;
	}

	/**
	 * Returns the upper left corner of the bounding box of the region.
	 * 
	 * @param none
	 * @return The upper left corner point of the bounding box of the region
	 */
	public PointPixel getUpperLeft() {
		return upperLeft;
	}

	/**
	 * Sets the upper left corner of the bounding box of the region.
	 * 
	 * @param p The upper left corner point of the bounding box of the region 
	 */
	public void setUpperLeft(PointPixel p) {
		upperLeft = p;
	}

	/**
	 * Returns the lower right corner of the bounding box of the region.
	 * 
	 * @param none
	 * @return The lower right corner point of the bounding box of the region
	 */
	public PointPixel getLowerRight() {
		return lowerRight;
	}

	/**
	 * Sets the lower right corner of the bounding box of the region.
	 * 
	 * @param p The lower right corner point of the bounding box of the region 
	 */
	public void setLowerRight(PointPixel p) {
		lowerRight = p;
	}

	/**
	 * Returns the height of the bounding box of the region.
	 * 
	 * @param none
	 * @return The height of the bounding box of the region
	 */
	public int getBoundingBoxHeight() {
		return (lowerRight.getRow() - upperLeft.getRow() + 1);
	}

	/**
	 * Returns the width of the bounding box of the region.
	 * 
	 * @param none
	 * @return The width of the bounding box of the region
	 */
	public int getBoundingBoxWidth() {
		return (lowerRight.getColumn() - upperLeft.getColumn() + 1);
	}

	/**
	 * Returns whether the region is a thick line or not.
	 * 
	 * @param c True if the region is a thick line and false otherwise
	 */
	public void setIsThickLine(boolean c) {
		isThickLine = c;
	}

	/**
	 * Returns whether the region is a thick line or not.
	 * 
	 * @param none
	 * @return True if the region is a thick line and false otherwise
	 */
	public boolean getIsThickLine() {
		return isThickLine;
	}

	/**
	 * Returns whether the region is part of a dashed line or not.
	 * 
	 * @param c True if the region is part of a dashed line and false otherwise
	 */
	public void setIsDashedLine(boolean c) {
		isDashedLine = c;
	}

	/**
	 * Returns whether the region is part of a dashed line or not.
	 * 
	 * @param none
	 * @return True if the region is part of a dashed line and false otherwise
	 */
	public boolean getIsDashedLine() {
		return isDashedLine;
	}

	/**
	 * Sets whether the region is a filled area or not.
	 * 
	 * @param c True if the region is a filled area and false otherwise
	 */
	public void setIsFilledArea(boolean c) {
		isFilledArea = c;
	}

	/**
	 * Returns whether the region is a filled area or not.
	 * 
	 * @param none
	 * @return True if the region is a filled area and false otherwise
	 */
	public boolean getIsFilledArea() {
		return isFilledArea;
	}

	/**
	 * Sets whether the region is a character or not.
	 * 
	 * @param c True if the region is a character and false otherwise
	 */
	public void setIsCharacter(boolean c) {
		isCharacter = c;
	}

	/**
	 * Returns whether the region is a character or not.
	 * 
	 * @param none
	 * @return True if the region is a character and false otherwise
	 */
	public boolean getIsCharacter() {
		return isCharacter;
	}

	/**
	 * Sets whether the region is part of the frame of the image or not.
	 * 
	 * @param c True if the region is part of the frame and false otherwise
	 */
	public void setIsFrame(boolean c) {
		isFrame = c;
	}

	/**
	 * Returns whether the region is part of the frame of the image or not.
	 * 
	 * @param none
	 * @return True if the region is part of the frame and false otherwise
	 */
	public boolean getIsFrame() {
		return isFrame;
	}


	/**
	 * Sets whether the region is part of a gridline or not.
	 * 
	 * @param c True if the region is part of a gridline and false otherwise
	 */
	public void setIsGridline(boolean c) {
		isGridline = c;
	}

	/**
	 * Returns whether the region is part of a gridline or not.
	 * 
	 * @param none
	 * @return True if the region is part of a gridline and false otherwise
	 */
	public boolean getIsGridline() {
		return isGridline;
	}

	/**
	 * Sets the orientation of the region; vertical or horizontal.
	 * 
	 * @param c The orientation of the region
	 */
	public void setOrientation(int c) {
		orientation = c;
	}

	/**
	 * Returns the orientation of the region; vertical or horizontal.
	 * 
	 * @param none
	 * @return The orientation of the region
	 */
	public int getOrientation() {
		orientation = Primitive.VERTICAL;
		if (getBoundingBoxWidth() > getBoundingBoxHeight()) {
			orientation = Primitive.HORIZONTAL;
		}
		return orientation;
	}

	/**
	 * Sets the color value of the region in the image.
	 * 
	 * @param c The color value of the region
	 */
	public void setColor(int c) {
		color = c;
	}

	/**
	 * Returns the color value of the region in the image.
	 * 
	 * @param none
	 * @return The color value of the region
	 */
	public int getColor() {
		return color;
	}

	/**
	 * Prints on the screen the information for the 
	 * primitives of the region.
	 * 
	 * @param none
	 */
	public void displayPrimitives() {
		//System.out.println("\nRegion "+RegionNo+" has "+numChains+ " chains, "+chains.size()+" key-value mappings.");
		Primitive aPrim;
		PointPixel firstPoint, lastPoint;
		Iterator itr = chains.values().iterator();
		while (itr.hasNext()) {
			aPrim = (Primitive)itr.next();
			firstPoint = new PointPixel(aPrim.getBeginPoint());
			lastPoint = new PointPixel(aPrim.getEndPoint());
			//System.out.println("Chain "+aPrim.getTagNo()+ " of type "+aPrim.getPrimitiveType()+" has "+aPrim.getSize()+" pixels. First pixel: "+ firstPoint +" Last pixel: "+lastPoint);
		}
	}

	/**
	 * Returns a string holding information about the region
	 * for printing on the screen.
	 * 
	 * @param none
	 * @return The string of information
	 */
	public String toString() {
		String message = ("Region "+RegionNo+": ");
		if (isCharacter) {
			message = message + ("Character, "); 
		}
		else if (isThickLine) {
			message = message + ("Thick line, "); 
		}
		else if (isFilledArea) {
			message = message + ("Filled area, "); 
		}
		else {
			message = message + ("Thin line, "); 
		}
		if (isDashedLine) {
			message = message + ("Dashed, "); 
		}
		if (isGridline) {
			message = message + ("Gridline, "); 
		}
		message = message + (numChains+ " chains, "+chains.size()+" key-value mappings, "+numPixels+" pixels, orientation = "+orientation+", color = "+color+"\n");
		return message;
	}

/*
* increments the number of Pixels in the region.
*/

public void incrementNumPixels() {
    numPixels += 1;}

/*
* counts the number of pixels in region and stores it in numPixels
*/

public void setNumPixels(int[][] pixelLabel) {
System.out.println("in setNumPixels");
  int i,j;
  for (i = upperLeft.getRow(); i<=lowerRight.getRow();i++) 
    for (j = upperLeft.getColumn(); j<=lowerRight.getColumn();j++)
       if (RegionNo == pixelLabel[i][j]) numPixels++;
System.out.println("numPixels = " + numPixels);
}


/********************
	void addPrimitiveToRegion( Map m) {
		chains.putAll(ll);
		numChains = chains.size();
		nextKey++;
	}

	Region(int labelNo, Map m) {
		RegionNo = labelNo;
		chains = new HashMap(10);
		chains.putAll(m);
		numChains = chains.size();
	}
*************/
}

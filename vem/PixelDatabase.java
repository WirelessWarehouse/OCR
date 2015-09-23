import java.util.HashMap;
import java.util.Collection;
import java.util.*;
import java.lang.Integer;
import java.awt.Point;

/**
 * A class to hold the pixel information for all the relevant pixels of the image.
 * Data is stored in two <code>HashMap</code>s.
 * For the first <code>HashMap</code>, each pixel, given its row and column,
 * is mapped to a linked list of <code>PixelData</code>.
 * A pixel has as many <code>PixelData</code> entries as the number of
 * chains (or <code>Primitive</code>s) that it is part of.
 * Each <code>PixelData</code> entry holds the label and the tag of the
 * primitive that the pixel is part of and the position of the pixel in that
 * chain (start point of a chain, end point of a chain or the interior point
 * of a chain).
 * <p>
 * The second <code>HashMap</code> holds the neigborhood information. 
 * Each pixel, given its
 * row and column, is mapped to a linked list of pixels that are its neigbors
 * in the same region.
 * 
 * @author Chart Reading project
 * @version 1.0
 */
public class PixelDatabase {
	private int imageHeight;
  private int	imageWidth;
	private int noOfPixels;
	private HashMap pixels;
	private HashMap pixelNeigbors;

	/**
	 * Constructor.
	 * 
	 * @param rows The number of rows in the image
	 * @param columns The number of columns in the image
	 */
	public PixelDatabase(int rows, int columns) {
		imageHeight = rows;
		imageWidth = columns;
		pixels = new HashMap(rows*columns);
		pixelNeigbors = new HashMap(rows*columns);
		noOfPixels = 0;
	}

	/**
 	 * Inserts a PixelData entry for the given point (row, column) 
 	 * and sets the given properties.
 	 *
   * @param row The row number of the pixel to be stored
   * @param column The column number of the pixel to be stored
   * @param labelNo The label of the region the point to be stored is in
   * @param tagNo The tag number of the primitive the point to be stored is in
   * @param pos The position of point to be stored (start, end or interior point)
 	 */
	public void put(int row, int column, int labelNo, int tagNo, int pos) {
		PixelData entry = new PixelData(labelNo, tagNo, pos, row, column);
		Integer key = new Integer(row * imageWidth + column);
		LinkedList alist;
		if (pixels.containsKey(key)) {
			alist = (LinkedList)pixels.get(key);
			//Check if the set already contains this information
			if (!alist.contains(entry)) {
				alist.add(entry);
			}
		}
		else {
			alist = new LinkedList();
			alist.add(entry);
			pixels.put(key, alist);
			noOfPixels++;
		}
	}

	/**
 	 * Inserts the list of neighbors  
 	 * for the given point (row, column) in the hash table of 
	 * pixelNeighbors.
 	 *
   * @param row The row number of the pixel whose neighbors are to be stored
   * @param column The column number of the pixel whose neighbors are to be stored
	 * @param neigborList The linked list of neighbor points for the given point
 	 */
	public void put(int row, int column, LinkedList neigborList) {
		Integer key = new Integer(row * imageWidth + column);
		if (!pixelNeigbors.containsKey(key)) {
			pixelNeigbors.put(key, neigborList);
		}			
	}

	/**
 	 * Inserts a PixelData entry for the given point (row, column), 
 	 * sets the given properties and also, inserts the list of neighbors  
 	 * for the given point (row, column) in the hash table of 
	 * pixelNeighbors.
 	 *
   * @param row The row number of the pixel to be stored
   * @param column The column number of the pixel to be stored
   * @param labelNo The label of the region the point to be stored is in
   * @param tagNo The tag number of the primitive the point to be stored is in
   * @param pos The position of point to be stored (start, end or interior point)
	 * @param neigborList The linked list of neighbor points for the given point
 	 */
	public void put(int row, int column, int labelNo, int tagNo, int pos, LinkedList neigborList) {
		put(row, column, labelNo, tagNo, pos);
		put(row, column, neigborList);
	}

/**
 * Inserts all points of the given list in the
 * PixelDatabase and sets the given properties.
 * All points are inserted as interior points.
 *
 * @param alist The linked list of points to be inserted in the database
 * @param labelNo The label of the region the points to be stored are in
 * @param tagNo The tag number of the primitive the points to be stored are in
 *
 */
	public void put(LinkedList alist, int labelNo, int tagNo) {
		PointPixel aPoint;
		ListIterator lItr = alist.listIterator(0);
		while(lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			put(aPoint.getRow(), aPoint.getColumn(), labelNo, tagNo, PixelData.INTERIORPOINT);
		}
	}


/**
 * Inserts all points of the given primitive in the
 * PixelDatabase and sets the given properties.
 * The first point of the primitive is inserted as the start point, 
 * the last point of the primitive is inserted as the end point and 
 * all the other points are inserted as interior points.
 *
 * @param aPrim The primitive whose points are to be inserted in the database
 */
	public void put(Primitive aPrim) {
		LinkedList aList = aPrim.getAllPoints();
		int label = aPrim.getParent(); 
		int tag = aPrim.getTagNo();
		//System.out.print("Putting primitive "+tag+" of region "+label+".");
		PointPixel aPoint;
		PixelData entry;
		int count = 0;
		ListIterator lItr = aList.listIterator(0);
		while(lItr.hasNext()) {
			count++;
			aPoint = new PointPixel(lItr.next());
			if (count == 1) {
				//System.out.print("Putting first point. ");
				put(aPoint.getRow(), aPoint.getColumn(), label, tag, PixelData.STARTPOINT);
			}
			else if (count == aList.size()) {
				//System.out.println("Putting last point. ");
				put(aPoint.getRow(), aPoint.getColumn(), label, tag, PixelData.ENDPOINT);
			}
			else {		
				put(aPoint.getRow(), aPoint.getColumn(), label, tag, PixelData.INTERIORPOINT);
			}
		}
	}


/**
 * Removes all PixelData entries for given point.
 *
 * @param row The row number of the point which will be removed from the database
 * @param column The column number of the point which will be removed from the database
 */
	public void removePixel(int row, int column) {
		Integer key = new Integer(row * imageWidth + column); 
		if (pixels.containsKey(key)) {
			pixels.remove(key); 
			noOfPixels--; 
			//System.out.println("Removed pixel: " + key);
		}
	}


/**
 * Removes all PixelData entries of the given point (row, column) 
 * that have the given tag number in the tag field.
 *
 * @param row The row number of the point which will be removed from the database
 * @param column The column number of the point which will be removed from the database
 * @param labelNo The label number of the region in which the given pixel is
 * @param tagNo The tag number which determined the entries to be removed
 */
	public void removePixelData(int row, int column, int labelNo, int tagNo) {
		Integer key = new Integer(row * imageWidth + column);
		PixelData entry;
		PointPixel aPoint;
		LinkedList alist;
		ListIterator lItr;
		if (pixels.containsKey(key)) {
			alist = (LinkedList)pixels.get(key);
			lItr = alist.listIterator(0);
			while(lItr.hasNext()) {
				entry = (PixelData)lItr.next();
				if (entry.getLabel() == labelNo && entry.getTag() == tagNo) {
					lItr.remove();
				}
			}
		}	
		//Have to check the neigbors, too, they will have this information as a Pixeldata
		//Get the neigbors
		//Get the entries, get the matching ones, delete the matching ones
	  LinkedList neigbors = getPixelNeigbors(row, column);
		lItr = neigbors.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			alist = getPixelData(aPoint.getRow(), aPoint.getColumn());
			ListIterator lItr2 = alist.listIterator(0);
			while (lItr2.hasNext()) {
				entry = (PixelData)lItr2.next();
				if (entry.getLabel() == labelNo && entry.getTag() == tagNo) {
					lItr2.remove();
				}
			}
		}
		//System.out.println("Removed pixeldata: " + labelNo + ", " + tagNo + " from key: " + key);
	}	


/**
 * Removes all PixelData entries for all points in the 
 * given primitive.
 *
 * @param aPrim The primitive whose points will be removed from the database 
 */
	public void removePixelData(Primitive aPrim) {
		int row, column;
		Point aPoint;
		int labelNo = aPrim.getParent();
		int tagNo = aPrim. getTagNo();
		LinkedList alist = aPrim.getAllPoints();
		ListIterator lItr = alist.listIterator(0);
		while(lItr.hasNext()) {
			aPoint = (Point)lItr.next();
			removePixelData((int)aPoint.getX(), (int)aPoint.getY(), labelNo, tagNo);
		}
	}


/**
 * Removes all PixelData entries for all points in the 
 * given primitive between the two index points given.
 * Data for the two index points are also removed.
 *
 * @param aPrim The primitive whose pixels will be removed from the database
 * @param beginIndex The index of the point that the removal will start from
 * @param endIndex The index of the point that the removal will end at
 */
	public void removePixelData(Primitive aPrim, int beginIndex, int endIndex) {
		int row, column;
		Point aPoint;
		int labelNo = aPrim.getParent();
		int tagNo = aPrim. getTagNo();
		LinkedList alist = aPrim.getAllPoints();
		int index = 0;
		ListIterator lItr = alist.listIterator(0);
		while(lItr.hasNext()) {
			aPoint = (Point)lItr.next();
			if (index == beginIndex - 1 && beginIndex > 1) {
				updatePixelPosition((int)aPoint.getX(), (int)aPoint.getY(), labelNo, tagNo, PixelData.INTERIORPOINT, PixelData.ENDPOINT);
			}
			else if (index >= beginIndex && index <= endIndex) {
				removePixelData((int)aPoint.getX(), (int)aPoint.getY(), labelNo, tagNo);
			}
			else if (index == endIndex + 1 && endIndex < aPrim.getSize() - 2) {
				updatePixelPosition((int)aPoint.getX(), (int)aPoint.getY(), labelNo, tagNo, PixelData.INTERIORPOINT, PixelData.STARTPOINT);
			}
			index++;
		}
		//Update start point, end point information

	}


/**
 * Changes the tag field of PixelData from an old tag number to a new tag number
 * at every entry that the old tag number is encountered for the given point (row, column). 
 *
 * @param row The row number of the pixel whose information is to be changed
 * @param column The column number of the pixel whose information is to be changed
 * @param labelNo The label of the region in which the given pixel is
 * @param oldTagNo The tag number that is to be changed
 * @param newTagNo The tag number that the old tag number is to be replaced with
 */
	public void updatePixelTag(int row, int column, int labelNo, int oldTagNo, int newTagNo) {
		Integer key = new Integer(row * imageWidth + column);
		PixelData entry;
		PointPixel aPoint;
		LinkedList alist;
		ListIterator lItr;
		if (pixels.containsKey(key)) {
			alist = (LinkedList)pixels.get(key);
			lItr = alist.listIterator(0);
			while(lItr.hasNext()) {
				entry = (PixelData)lItr.next();
				if (entry.getLabel() == labelNo && entry.getTag() == oldTagNo) {
					entry.setTag(newTagNo);
				}
			}
		}	
		//Have to check the neigbors, too, they will have this information as a Pixeldata
		//Get the neigbors
		//Get the entries, get the matching ones, delete the matching ones
	  LinkedList neigbors = getPixelNeigbors(row, column);
		lItr = neigbors.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			alist = getPixelData(aPoint.getRow(), aPoint.getColumn());
			ListIterator lItr2 = alist.listIterator(0);
			while (lItr2.hasNext()) {
				entry = (PixelData)lItr2.next();
				if (entry.getLabel() == labelNo && entry.getTag() == oldTagNo) {
					entry.setTag(newTagNo);
				}
			}
		}
		//System.out.println("Updated pixeldata: " + labelNo + ", " + tagNo + " from key: " + key);
	}	


/**
 * Changes the tag field of PixelData from an old tag number to a new tag number
 * at every entry that the old tag number is encountered for the given point (row, column). 
 *
 * @param aPoint The pixel whose information is to be changed
 * @param label The label of the region in which the given pixel is
 * @param oldTag The tag number that is to be changed
 * @param newTag The tag number that the old tag number is to be replaced with
 */
	public void updatePixelTag(PointPixel aPoint, int label, int oldTag, int newTag) {
		updatePixelTag(aPoint.getRow(), aPoint.getColumn(), label, oldTag, newTag);
	}


/**
 * Changes the tag field of PixelData from an old tag number to a new tag number
 * at every entry that the old tag number is encountered for every
 * point in the given list.
 *
 * @param alist The linked list of pixels whose information is to be changed
 * @param label The label of the region in which the given list of pixels is
 * @param oldTag The tag number that is to be changed
 * @param newTag The tag number that the old tag number is to be replaced with
 */
	public void updatePixelTag(LinkedList alist, int label, int oldTag, int newTag) {
		PointPixel aPoint;
		ListIterator lItr = alist.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			updatePixelTag(aPoint.getRow(), aPoint.getColumn(), label, oldTag, newTag);
		}
	}


/**
 * Changes the tag field of PixelData from an old tag number to a new tag number
 * at every entry that the old tag number is encountered for every
 * point in the given primitive.
 *
 * @param aPrim The primitive whose pixel information is to be changed
 * @param label The label of the region in which the given primitive is
 * @param newTag The tag number that the pixels will have
 */
	public void updatePixelTag(Primitive aPrim, int newTag) {
		PointPixel aPoint;
		LinkedList alist = aPrim.getAllPoints();
		ListIterator lItr = alist.listIterator(0);
		int oldTag = aPrim.getTagNo();
		int label = aPrim.getParent();
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			updatePixelTag(aPoint.getRow(), aPoint.getColumn(), label, oldTag, newTag);
		}
	}


/**
 * Changes the position field of PixelData from an old position number to a new position number
 * at every entry that the old position number is encountered for 
 * the given point.
 *
 * @param row The row number of the pixel whose information is to be changed
 * @param column The column number of the pixel whose information is to be changed
 * @param labelNo The label of the region in which the given pixel is
 * @param tag The tag number of the pixel whose information is to be changed
 * @param oldPos The position number of the pixel that is to be replaced
 * @param newPos The position number that the old position number is to be replaced with
 */
	public void updatePixelPosition(int row, int column, int labelNo, int tag, int oldPos, int newPos) {
		Integer key = new Integer(row * imageWidth + column);
		PixelData entry;
		PointPixel aPoint;
		LinkedList alist;
		ListIterator lItr;
		if (pixels.containsKey(key)) {
			alist = (LinkedList)pixels.get(key);
			lItr = alist.listIterator(0);
			while(lItr.hasNext()) {
				entry = (PixelData)lItr.next();
				if (entry.getLabel() == labelNo && entry.getTag() == tag && entry.getPosition() == oldPos) {
					entry.setPosition(newPos);
				}
			}
		}	
		//Have to check the neigbors, too, they will have this information as a Pixeldata
		//Get the neigbors
		//Get the entries, get the matching ones, delete the matching ones
	  LinkedList neigbors = getPixelNeigbors(row, column);
		lItr = neigbors.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			alist = getPixelData(aPoint.getRow(), aPoint.getColumn());
			ListIterator lItr2 = alist.listIterator(0);
			while (lItr2.hasNext()) {
				entry = (PixelData)lItr2.next();
				if (entry.getLabel() == labelNo && entry.getTag() == tag && entry.getPosition() == oldPos) {
					entry.setPosition(newPos);
				}
			}
		}
		//System.out.println("Updated pixeldata: " + labelNo + ", " + tagNo + " from key: " + key);
	}	


/**
 * Changes the position field of PixelData from an old position number to a new position number
 * at every entry that the old position number is encountered for 
 * the given point.
 *
 * @param aPixel The pixel whose information is to be changed
 * @param label The label of the region in which the given pixel is
 * @param tag The tag number of the pixel whose information is to be changed
 * @param oldpos The position number of the pixel that is to be replaced
 * @param newpos The position number that the old position number is to be replaced with
 */
	public void updatePixelPosition(PointPixel aPixel, int label, int tag, int oldpos, int newpos) {
		updatePixelPosition(aPixel.getRow(), aPixel.getColumn(), label, tag, oldpos, newpos); 
	}


/**
 * Changes the position field of PixelData from an old position number to a new position number
 * at every entry that the old position number is encountered for 
 * the given point.
 *
 * @param aPixel The pixel whose information is to be changed
 * @param label The label of the region in which the given pixel is
 * @param tag The tag number of the pixel whose information is to be changed
 * @param oldpos The position number of the pixel that is to be replaced
 * @param newpos The position number that the old position number is to be replaced with
 */
	public void updatePixelPosition(Point aPixel, int label, int tag, int oldpos, int newpos) {
		updatePixelPosition((int)aPixel.getX(), (int)aPixel.getY(), label, tag, oldpos, newpos); 
	}


/**
 * Finds out if the two primitives are next to or touch
 * each other at any point.
 * Checks PixelData of every point and its neigbors of the first primitive.
 * If there is an entry with the same tag as the second primitive's tag
 * then two primitives are neigbors at least at one point.
 *
 * @param prim1 The first primitive that might be neighboring the second primitive
 * @param prim2 The second primitive that might be neighboring the first primitive
 * @return The array of indexes of points in the first primitive that are neighboring the second primitive
 */
	public int[] neigbors(Primitive prim1, Primitive prim2) {
		int[] indexList = new int[prim1.getSize()+1];
		int noOfNeigPoints = 0;
		indexList[0] = 0;
		if (prim1.getParent() == prim2.getParent() && prim1.getTagNo() != prim2.getTagNo()) { 
			PixelData entry, entry2;
			PointPixel aPoint;
			LinkedList dataList, dataList2;
			int tag1 = prim1.getTagNo();
			int tag2 = prim2.getTagNo();
			int index = -1;
			boolean found = false;
			LinkedList pointsList1 = prim1.getAllPoints();
			ListIterator lItr1 = pointsList1.listIterator(0);
			while (lItr1.hasNext()) {
				index++;	
				aPoint = new PointPixel(lItr1.next());
				dataList = getPixelDataWithNeigbors(aPoint); 
				ListIterator lItrdata = dataList.listIterator(0);
				found = false;
				while (lItrdata.hasNext()) {
					entry = (PixelData)lItrdata.next();
					if (entry.getTag() == tag2) {
						indexList[noOfNeigPoints + 1] = index;
						noOfNeigPoints++;
						found = true;
						break;
					}
				}
				if ((index == 0 || index == pointsList1.size() - 1) && !found) {
				//The first or the last point, neigbor not found
				//check neigbor's neigbors
					lItrdata = dataList.listIterator(0);
					while (lItrdata.hasNext()) {
						entry = (PixelData)lItrdata.next();
						if (entry.getTag() != tag1) {
							aPoint = new PointPixel(entry.getRow(), entry.getColumn());
							dataList2 = getPixelDataWithNeigbors(aPoint); 
							ListIterator lItrdata2 = dataList2.listIterator(0);
							while (lItrdata2.hasNext()) {
								entry2 = (PixelData)lItrdata2.next();
								if (entry2.getTag() == tag2) {
									indexList[noOfNeigPoints + 1] = index;
									noOfNeigPoints++;
									found = true;
									break;
								}
							}
						}
						if (found) {
							break;
						}
					}
				}
			}
		}
		indexList[0] =  noOfNeigPoints;
		return indexList;
	}


/**
 * Finds out if the two primitives are next to or touch
 * each other at their endpoints. Only the endpoints are checked.
 *
 * @param prim1 The first primitive that might be neighboring the second primitive
 * @param prim2 The second primitive that might be neighboring the first primitive
 * @return True if the primitives neighbor each other at their endpoints and false otherwise
 */
	public boolean neigborsAtEndPoints(Primitive prim1, Primitive prim2) {
		if (prim1.getParent() == prim2.getParent() && prim1.getTagNo() != prim2.getTagNo()) { 
			PixelData entry, anotherEntry;
			int tag1 = prim1.getTagNo();
			int tag2 = prim2.getTagNo();
			PointPixel beginPoint1 = prim1.getBeginPoint();
			PointPixel endPoint1 = prim1.getEndPoint();
			PointPixel beginPoint2 = prim2.getBeginPoint();
			PointPixel endPoint2 = prim2.getEndPoint();
			LinkedList listbegin1 = getPixelDataWithNeigbors(beginPoint1); 
			LinkedList listend1 = getPixelDataWithNeigbors(endPoint1); 
			LinkedList listbegin2 = getPixelDataWithNeigbors(beginPoint2); 
			LinkedList listend2 = getPixelDataWithNeigbors(endPoint2); 
			ListIterator lItr, lItr2;
			lItr = listbegin1.listIterator(0);
			while (lItr.hasNext()) {
				entry = (PixelData)lItr.next();
				lItr2 = listbegin2.listIterator(0);
				while (lItr2.hasNext()) {
					anotherEntry = (PixelData)lItr2.next();
					if (entry.getLabel() == anotherEntry.getLabel() && entry.getTag() == anotherEntry.getTag()) {
						return true;
					}
				}
				lItr2 = listend2.listIterator(0);
				while (lItr2.hasNext()) {
					anotherEntry = (PixelData)lItr2.next();
					if (entry.getLabel() == anotherEntry.getLabel() && entry.getTag() == anotherEntry.getTag()) {
						return true;
					}
				}
			}
			lItr = listend1.listIterator(0);
			while (lItr.hasNext()) {
				entry = (PixelData)lItr.next();
				lItr2 = listbegin2.listIterator(0);
				while (lItr2.hasNext()) {
					anotherEntry = (PixelData)lItr2.next();
					if (entry.getLabel() == anotherEntry.getLabel() && entry.getTag() == anotherEntry.getTag()) {
						return true;
					}
				}
				lItr2 = listend2.listIterator(0);
				while (lItr2.hasNext()) {
					anotherEntry = (PixelData)lItr2.next();
					if (entry.getLabel() == anotherEntry.getLabel() && entry.getTag() == anotherEntry.getTag()) {
						return true;
					}
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * Returns the collection of pixelData values in the database.
	 * 
	 * @param none
	 * @return The collection of pixelData values in the database
	 */
	public Collection getPixelDataList() {
		return pixels.values();
	}

	/**
	 * Returns the collection of pixel neighbor values in the database.
	 * 
	 * @param none
	 * @return The collection of pixel neighbor values in the database
	 */
	public Collection getPixelNeigborList() {
		return pixelNeigbors.values();
	}

	/**
	 * Returns the pixelData for the given point. 
	 * 
	 * @param row The row number of the given point 
	 * @param column The column number of the given point 
	 * @return The linked list of pixelData values for the given point
	 */
	public LinkedList getPixelData(int row, int column) { 
		//This function should raise an exception if the key is not found 
		Integer key = new Integer (row * imageWidth + column); 
		return (LinkedList)pixels.get(key); 
	} 


	/**
	 * Returns the pixelData for the given point. 
	 * 
	 * @param aPoint The given point 
	 * @return The linked list of pixelData values for the given point
	 */
	public LinkedList getPixelData(PointPixel aPoint) { 
		//This function should raise an exception if the key is not found 
		int row = aPoint.getRow();
		int column = aPoint.getColumn();
		Integer key = new Integer(row * imageWidth + column); 
		return (LinkedList)pixels.get(key); 
	}

	/**
	 * Returns the pixelData for the given point and its neighbors. 
	 * 
	 * @param row The row number of the given point 
	 * @param column The column number of the given point 
	 * @return The linked list of pixelData values for the given point and its neighbors
	 */
	public LinkedList getPixelDataWithNeigbors(int row, int column) {
		LinkedList alist;
		PointPixel aPoint;
		PixelData entry;
		LinkedList pixelList = getPixelData(row, column); 
		LinkedList majorList = new LinkedList();
		ListIterator mlItr = pixelList.listIterator(0);
		while (mlItr.hasNext()) {
			entry = (PixelData)mlItr.next();
			majorList.add(entry);
		}
	  LinkedList neigbors = getPixelNeigbors(row, column);
		ListIterator lItr = neigbors.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			alist = getPixelData(aPoint.getRow(), aPoint.getColumn());
			ListIterator lItr2 = alist.listIterator(0);
			while (lItr2.hasNext()) {
				entry = (PixelData)lItr2.next();
				if (!majorList.contains(entry)) {
					majorList.add(entry);
				}
			}
		}
		return majorList;
	}


	/**
	 * Returns the pixelData for the given point and its neighbors. 
	 * 
	 * @param aPoint The given point 
	 * @return The linked list of pixelData values for the given point and its neighbors
	 */
	public LinkedList getPixelDataWithNeigbors(PointPixel aPoint) {
		return (getPixelDataWithNeigbors(aPoint.getRow(), aPoint.getColumn()));
	}


	/**
	 * Returns the pixelData for the given point and its neighbors 
	 * that are immediate and also surrounding the immediate neighbors. 
	 * 
	 * @param row The row number of the given point 
	 * @param column The column number of the given point 
	 * @return The linked list of pixelData values for the given point and its neighbors
	 */
	public LinkedList getPixelDataWithNeigbors24(int row, int column) {
		LinkedList alist;
		PointPixel aPoint;
		PixelData entry;
		LinkedList pixelList = getPixelData(row, column); 
		LinkedList majorList = new LinkedList();
		ListIterator mlItr = pixelList.listIterator(0);
		while (mlItr.hasNext()) {
			entry = (PixelData)mlItr.next();
			majorList.add(entry);
		}
	  LinkedList neigbors = getPixelNeigbors24(row, column);
		//System.out.println(neigbors.size()+" neigbors out of 24");
		ListIterator lItr = neigbors.listIterator(0);
		while (lItr.hasNext()) {
			//aPoint = new PointPixel(lItr.next());
			aPoint = (PointPixel)lItr.next();
			alist = getPixelData(aPoint.getRow(), aPoint.getColumn());
			ListIterator lItr2 = alist.listIterator(0);
			while (lItr2.hasNext()) {
				entry = (PixelData)lItr2.next();
				if (!majorList.contains(entry)) {
					majorList.add(entry);
				}
			}
		}
		return majorList;
	}


	/**
	 * Returns the pixelData for the given point and its neighbors 
	 * that are immediate and also surrounding the immediate neighbors. 
	 * 
	 * @param aPoint The given point 
	 * @return The linked list of pixelData values for the given point and its neighbors
	 */
	public LinkedList getPixelDataWithNeigbors24(PointPixel aPoint) {
		return (getPixelDataWithNeigbors24(aPoint.getRow(), aPoint.getColumn()));
	}

	/**
	 * Returns a shorter list for the given pixelData list
	 * eliminating the duplicate information.
	 * 
	 * @param pixelDataList The linked list of pixelDatas
	 * @return The shortened linked list of pixelData values
	 */
	public LinkedList reduceList(LinkedList pixelDataList) {
		PixelData entry;
		int tag, labelNo, position, index, row, column;
		LinkedList labels = new LinkedList();
		LinkedList tags = new LinkedList();
		LinkedList positions = new LinkedList();
		LinkedList newList = new LinkedList();
		ListIterator lItr = pixelDataList.listIterator(0);
		while (lItr.hasNext()) {
			entry = (PixelData)lItr.next();
			tag = entry.getTag();
			labelNo = entry.getLabel();
			position = entry.getPosition();
			row = entry.getRow();
			column = entry.getColumn();
			if (!tags.contains(new Integer(tag))) {
				tags.add(new Integer(tag));
				labels.add(new Integer(labelNo));
				positions.add(new Integer(position));
				newList.add(new PixelData(labelNo, tag, position, row, column));
			}
			else {
				index = tags.indexOf(new Integer(tag));
				if (((Integer)labels.get(index)).intValue() != labelNo) {
					tags.add(new Integer(tag));
					labels.add(new Integer(labelNo));
					positions.add(new Integer(position));
					newList.add(new PixelData(labelNo, tag, position, row, column));
				}
				else if (position != PixelData.INTERIORPOINT && ((Integer)positions.get(index)).intValue() == PixelData.INTERIORPOINT) {
				 	positions.set(index, new Integer(position));
					newList.set(index, new PixelData(labelNo, tag, position, row, column));
				}
			}
		}
		return newList;
	}


	/**
	 * Returns the list of neighbors for the given point.
	 * 
	 * @param row The row number of the given point 
	 * @param column The column number of the given point 
	 * @return The linked list of neighbors of the given point 
	 */
	public LinkedList getPixelNeigbors(int row, int column) { 
		//This function should raise an exception if the key is not found 
		Integer key = new Integer(row * imageWidth + column); 
		return (LinkedList)pixelNeigbors.get(key); 
	} 

	/**
	 * Returns the list of neighbors for the given point.
	 * 
	 * @param aPoint The given point 
	 * @return The linked list of neighbors of the given point 
	 */
	public LinkedList getPixelNeigbors(PointPixel aPoint) { 
		//This function should raise an exception if the key is not found 
		int row = aPoint.getRow();
		int column = aPoint.getColumn();
		Integer key = new Integer (row * imageWidth + column); 
		return (LinkedList)pixelNeigbors.get(key); 
	} 

	/**
	 * Returns the list of neighbors for the given point. The 
	 * neighbors list includes the immediate neighbors and the
	 * neighbors that surround the immediate neighbors.
	 * 
	 * @param row The row number of the given point 
	 * @param column The column number of the given point 
	 * @return The linked list of neighbors of the given point 
	 */
	public LinkedList getPixelNeigbors24(int row, int column) { 
		//This function should raise an exception if the key is not found 
		Integer key = new Integer(row * imageWidth + column); 
		PointPixel aPoint, anotherPoint;
		LinkedList alist;
		ListIterator lItr2;
		LinkedList neigbors8 = (LinkedList)pixelNeigbors.get(key); 
		//System.out.println(neigbors8.size()+" neigbors out of 8 for ("+row+", "+column+")");
		LinkedList neigbors = new LinkedList();
		ListIterator lItr = neigbors8.listIterator(0);
		while (lItr.hasNext()) {
			aPoint = new PointPixel(lItr.next());
			neigbors.add(aPoint);
			key = new Integer(aPoint.getRow() * imageWidth + aPoint.getColumn()); 
			alist = (LinkedList)pixelNeigbors.get(key);
			//System.out.println(alist.size()+" neigbors for a neigbor "+aPoint);
			lItr2 = alist.listIterator(0);
			while (lItr2.hasNext()) {
				anotherPoint = new PointPixel(lItr2.next()); 
				//System.out.println("Point "+anotherPoint);
				if (!(anotherPoint.getRow() == row && anotherPoint.getColumn() == column) && !neigbors.contains(anotherPoint)) {
					neigbors.add(anotherPoint);
					//System.out.println("Added point "+anotherPoint);
				}
			}
		}
		//System.out.println(neigbors.size()+" neigbors out of 24");
		return neigbors;
	} 

	/**
	 * Returns the list of neighbors for the given point. The 
	 * neighbors list includes the immediate neighbors and the
	 * neighbors that surround the immediate neighbors.
	 * 
	 * @param aPoint The given point 
	 * @return The linked list of neighbors of the given point 
	 */
	public LinkedList getPixelNeigbors24(PointPixel aPoint) { 
		//This function should raise an exception if the key is not found 
		return (getPixelNeigbors24(aPoint.getRow(), aPoint.getColumn()));
	} 

 /**
	* Returns the number of pixels in the database.
	* 
	* @param none
	* @return The number of pixels in the database
	*/
	public int getNoOfPixels() { 
		return noOfPixels; 
	} 

 /**
	* Prints the information for certain pixels on the screen.
	* 
	* @param none
	*/
	public void displayPixels() {
		//System.out.println("\nPixelDatabase has "+noOfPixels+ " pixels.");
		int akey, row, column;
		LinkedList alist, neigborList;
		PointPixel aPoint;
		PixelData entry;
		ListIterator lItr;
		Iterator itr = pixels.keySet().iterator();
		while (itr.hasNext()) {
			akey = ((Integer)itr.next()).intValue(); //Integer key = new Integer(row * imageWidth + column);
			column = akey - (imageWidth * (akey / imageWidth));
			row = (akey - column) / imageWidth;
			alist = (LinkedList)pixels.get(new Integer(akey));
			neigborList = (LinkedList)pixelNeigbors.get(new Integer(akey)); 
			if (row == 74 && column == 7 || 
					row == 479 && column == 692) {
				//System.out.println("Key "+akey+" Point ("+row+", "+column+") with "+neigborList.size()+" neigbors has "+alist.size()+" entries:");
				lItr = alist.listIterator(0);
				while (lItr.hasNext()) {
					entry = (PixelData)lItr.next();
					//System.out.println("Label-tag-position: "+entry);
				}
			}
		}
	}

}

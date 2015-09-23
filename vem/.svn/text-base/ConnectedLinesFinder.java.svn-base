import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find the connected lines in the image.
 * <p>
 * All primitives that are not part of an axis, a tick mark, a rectangle or
 * a wedge are considered to be part of a connected line.
 */
public class ConnectedLinesFinder {
  private int imageHeight;    // #rows in the input image
	private int imageWidth;     // #columns in the input image
	private int noOfLabels;
  private PixelDatabase allPixels;
  private	Region[] allRegions;
	private LinkedList allConnectedLines;

	/**
	 * Constructor.
	 *
	 * @param none
	 */
	public ConnectedLinesFinder() {
	}

	/**
	 * Constructor.
	 *
	 * @param regions The <code>Region</code> array of all the regions in the image
	 * @param labelCount The number of regions in the image
	 * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
	 * @param pixels The pixel database of the image
	 */
	public ConnectedLinesFinder(Region[] regions, int labelCount, int imageRows, int imageColumns, PixelDatabase pixels) {
		imageHeight = imageRows;
	 	imageWidth = imageColumns;
		noOfLabels = labelCount;
		allPixels = pixels;	
		allRegions = regions; 
		allConnectedLines = new LinkedList();
	}

	/**
	 * Finds the connected lines in each region. Each primitive that is not
	 * part of any other graph component is a connected line. The primitives that
	 * neighbor the first primitive are also added recursively to the connected
	 * line.
	 * 
	 * @param none
	 */
	public void findConnectedLines() {
		//System.out.println("In findConnectedLines of ConnectedLinesFinder.");
		Region aRegion;
		LinkedList neigborList;
		Collection chainList;
		Primitive aPrim, anotherPrim;
		PixelData entry;
		boolean flag;
		ConnectedLine aConnectedLine;
		PointPixel beginPixel, endPixel;
		int row, column, neigRow, neigColumn, noOfNeigbors, tag, tag2, labelNo, position;
		for (int i = 1; i < noOfLabels; i++) {
			aRegion = allRegions[i];
			if (aRegion.getNumChains() == 0 || aRegion.getIsGridline() == true) {
				continue;
			}
			chainList = aRegion.getPrimitiveList();
			Iterator itr = chainList.iterator();
			while (itr.hasNext()) {
				aPrim = (Primitive)itr.next();
				tag = aPrim.getTagNo();
				flag = false;
				/********
				if (i == 41 && (tag == 66 || tag == 67)) {
					if (aPrim.isPartOfConnectedLine() != 0) {
						//System.out.println("Region "+i+", primitive "+tag+" is part of a connected line");
					}
					if (aPrim.isPartOfAxis() != 0) {
						//System.out.println("Region "+i+", primitive "+tag+" is part of an axis");
					}
					if (aPrim.isPartOfRectangle() != 0) {
						//System.out.println("Region "+i+", primitive "+tag+" is part of a rectangle");
					}
					if (aPrim.isPartOfWedge() != 0) {
						//System.out.println("Region "+i+", primitive "+tag+" is part of a wedge");
					}
					if (aPrim.isPartOfGridline() != 0) {
						//System.out.println("Region "+i+", primitive "+tag+" is part of a gridline");
					}
					if (aPrim.isPartOfTick() != 0) {
						//System.out.println("Region "+i+", primitive "+tag+" is part of a tick");
					}
			 	}	
				********/
				if (aPrim.isPartOfConnectedLine() == 0 && aPrim.isPartOfAxis() == 0 && aPrim.isPartOfRectangle() == 0 && aPrim.isPartOfWedge() == 0 && aPrim.isPartOfGridline() == 0 && aPrim.isPartOfTick() == 0) { 
					aConnectedLine = new ConnectedLine(aPrim, aRegion.getIsFilledArea());
					aConnectedLine.setColor(aRegion.getColor());
					allConnectedLines.add(aConnectedLine);
					//System.out.println("Region "+i+": Primitive "+tag+" is part of a connected line."); 
					aPrim.setPartOfConnectedLine(1);
					//Go to first and last pixel, for primitives that can be added
					beginPixel = aPrim.getBeginPoint();
					endPixel = aPrim.getEndPoint();
					for (int k = 1; k <= 2; k++) { //Do it two times, for each endpoint
						if (k == 1) {
							row = beginPixel.getRow();
							column = beginPixel.getColumn();
						}
						else {
							row = endPixel.getRow();
							column = endPixel.getColumn();
						}
						LinkedList alist = (LinkedList)allPixels.getPixelDataWithNeigbors(row, column);
						//LinkedList alist = (LinkedList)allPixels.getPixelDataWithNeigbors24(row, column);
						ListIterator lItr = alist.listIterator(0);
						while (lItr.hasNext()) {
							//System.out.print("Getting an entry: ");
							entry = (PixelData)lItr.next();
							tag2 = entry.getTag();
							labelNo = entry.getLabel();
							position = entry.getPosition();
							//System.out.println("Entry: "+entry);
							if (labelNo == i && tag2 != tag && position != entry.INTERIORPOINT) { 
								//if (labelNo == i && tag2 != tag) { 
								//This point starts or ends a different primitive in the same region
								anotherPrim = aRegion.getPrimitive(tag2); 
								if (anotherPrim.isPartOfConnectedLine() == 0 && anotherPrim.isPartOfAxis() == 0 && anotherPrim.isPartOfRectangle() == 0 && anotherPrim.isPartOfWedge() == 0 && anotherPrim.isPartOfGridline() == 0 && anotherPrim.isPartOfTick() == 0) { 
									//System.out.println("Region "+i+": Adding primitive "+tag2+" to primitive "+tag);
									attachToLine(aConnectedLine, anotherPrim, position); 
								}
							} //end of if not interiorpoint
						} //end of traversing chains at one location
					} //end of checking begin point and end point

					aConnectedLine.setBoundingBox();
				} //end of if chain is already part of a connected line
			} //end of traversing of chains in a region
		} //end of traversing regions
		//System.out.println(allConnectedLines.size()+" separate connected lines are found.");
	}

	/**
	 * Given a primitive, finds the primitives that neighbor it at the given endpoint
	 * and adds to the connected line. It is called recursively to add as many
	 * primitives as possible.
	 *
	 * @param aConnectedLine The connected line which the first primitive is part of 
	 * @param aPrim The first primitive which is to be extended
	 * @param position The point (starting or end) at which the first primitive is to be extended
	 * @return The connected line that is updates as a primitive is added to it
	 */
	private ConnectedLine attachToLine(ConnectedLine aConnectedLine, Primitive aPrim, int position) {
		//Get all neigbors of anotherPrim
		int noOfNeigbors, neigRow, neigColumn, tag2, labelNo, index;
		int row = -1;
		int column = -1;
		int tag = aPrim.getTagNo();
		int label = aPrim.getParent();
		PixelData entry;
		LinkedList neigborList, alist;
		ListIterator lItr;
		aConnectedLine.addPrimitive(aPrim);						
		aPrim.setPartOfConnectedLine(1);
		//Go to endpoints of aPrim to see if it can be extended.
		if (position == PixelData.STARTPOINT) { //aPrim's begin point is next to the previous prim that is part of the axis 
			PointPixel endPixel = aPrim.getEndPoint();
			row = endPixel.getRow();
			column = endPixel.getColumn();
		}
		else if (position == PixelData.ENDPOINT) { //aPrim's end point is next to the previous prim that is part of the axis 
			PointPixel beginPixel = aPrim.getBeginPoint();
			row = beginPixel.getRow();
			column = beginPixel.getColumn();
		}
		//System.out.println("Getting entries for ("+row+", "+column+")");
		alist = (LinkedList)allPixels.getPixelDataWithNeigbors(row, column);
		//alist = (LinkedList)allPixels.getPixelDataWithNeigbors24(row, column);
		lItr = alist.listIterator(0);
		while (lItr.hasNext()) {
			//System.out.println("Getting an entry");
			entry = (PixelData)lItr.next();
			tag2 = entry.getTag();
			labelNo = entry.getLabel();
			index = entry.getPosition();
			//System.out.println("Entry: "+entry);
			if (labelNo == label && tag2 != tag && index != entry.INTERIORPOINT) { 
				//if (labelNo == label && tag2 != tag) { 
				//This point starts or ends a different primitive in the same region
				Primitive anotherPrim = allRegions[label].getPrimitive(tag2); 
				if (anotherPrim.isPartOfConnectedLine() == 0 && anotherPrim.isPartOfAxis() == 0 && anotherPrim.isPartOfRectangle() == 0 && anotherPrim.isPartOfWedge() == 0 && anotherPrim.isPartOfGridline() == 0 && anotherPrim.isPartOfTick() == 0) { 
					//System.out.println("Region "+labelNo+": Adding primitive "+tag2+" to line of primitive "+tag);
					attachToLine(aConnectedLine, anotherPrim, index); 
				}
			}
		} //end of traversing chains at one location
		return aConnectedLine;
	}


	/**
	 * Creates a 2d array representation of an image that has the connected lines pixels
	 * as near black and all others as white. (Gray-level is line number.)
	 *
	 * @param lines The linked list of lines that are to be drawn in the image 
	 * @return The 2d array representation of the connected lines image
	 */
	public int[][] makeConnectedLinesImage(LinkedList lines) {
		LinkedList alist, primlist, pointsList;
		Primitive aPrim;
		ConnectedLine aConnectedLine;
		Rectangle aRectangle;
		Point aPoint;
		ListIterator lItr, lItr2, lItr3;
		int[][] linesImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				linesImage[i][j] = 255;
			}
		}
		lItr = lines.listIterator(0);
                int lineNumber = 0;
		while(lItr.hasNext()) {
                        lineNumber = lineNumber + 1;
System.out.println("lineNumber = " + lineNumber);
			aConnectedLine = (ConnectedLine)lItr.next();
			primlist = aConnectedLine.getPrimitives();
			lItr2 = primlist.listIterator();
			while(lItr2.hasNext()) {
				aPrim = (Primitive)lItr2.next();
				pointsList = aPrim.getAllPoints();
				lItr3 = pointsList.listIterator(0);
				while (lItr3.hasNext()) {
					aPoint = (Point)lItr3.next();
					linesImage[(int)aPoint.getX()][(int)aPoint.getY()] = lineNumber;
				}
			}
		}
		return linesImage;
	}
				
	/**
	 * Returns the list of connected lines in the image.
	 * 
	 * @param none
	 * @return The linked list of connected lines	
	 */
	public LinkedList getConnectedLines() {
		return allConnectedLines;
	}

}

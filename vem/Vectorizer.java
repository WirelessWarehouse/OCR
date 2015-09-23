import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to segment the borders of the image.
 * Once the border pixels are found, border chains are
 * created by border following.
 * Chains are broken at abrupt and less abrupt changes and 
 * at junction points as the
 * border pixels are followed.
 * A curve and a horizontal line following it are merged.
 * Single pixels are added to their neigboring chains
 * if it is proper to add them by checking the chain's line fit.
 *
 * @author Chart Reading project
 * @version 1.0
 */

public class Vectorizer {

	final int[] rPos = {-1, -1, -1, 0, 1, 1, 1, 0};  //already declared in ImagePrimitives.jave
	final int[] cPos = {1, 0, -1, -1, -1, 0, 1, 1};  //already declared in ImagePrimitives.jave
	private final int UNTAGGED = -1;
	private final int BACKGROUND = 0;
	private final int ISOLATED = 1;
	private final int ENDPOINT = 2;
	private final int INTERIOR = 3;
	private final int CORNER = 4;
	private final int JUNCTION = 5;

	private final int NOCHANGE = 0;
	private final int DECREASING = -1;
	private final int INCREASING = 1;

	private final int LEFT = 0;
	private final int RIGHT = 1;

	private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
 	private int bPixLabel;			//the background label (0)

	private int[][] inPixImage;  //Bordered labelled image
	private int noOfLabels;
	private int[][] borderedImage;
  private Hashtable allChains; //each entry is a linked list of chains
	private PixelDatabase pixelData;
	private Region[] allRegions;

 /**
	* Constructor.
	*
	* @param inImage The 2d array representation of the borders of the image
	* @param labelCount The number of regions
	* @param imageRows The number of rows
	* @param imageColumns The number of columns
	* @param bLabel The label of the background pixels
	* @param regions The Region array for all the regions in the image
	* @param database The PixelDatabase for all the border pixels in the image
	*/
	public Vectorizer(int[][] inImage, int labelCount, int imageRows, int imageColumns, int bLabel, Region[] regions, PixelDatabase database) {
		borderedImage = new int[imageRows][imageColumns];
		for (int i=0; i<imageRows; i++) {
			for (int j=0; j<imageColumns; j++) {
				//System.out.println(inImage[i][j]);
				borderedImage[i][j] = inImage[i][j];
				//System.out.print(inPixImage[i][j]);
			}
		}	
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPixLabel = bLabel;
		noOfLabels = labelCount;
		pixelData = database;
		allRegions = regions;
		//pixelData = new PixelDatabase(imageRows, imageColumns);
	}

  /**
   * Calls followBorders to find the segments of the border image,
	 * calls storePrimitiveData to update pixel database,
	 * calls mergeChains and mergeChains2 to merge chains as needed and
	 * calls displayBorderChains to store information is chainList.txt file.
	 *
   * @param none
   */
	public void findSegments() {
		followBorders();
		//displayBorderChains("chainList.txt");
		//splitChains();
		storePrimitiveData();
		//System.out.println("Image "+imageHeight+"x"+imageWidth);
		//pixelData.displayPixels();
		mergeChains();
		mergeChains2();
		//displayBorderChains("chainList.txt");
		pixelData.displayPixels();
	}


  /**
   * Constructs the border chains of the border image. 
	 * p. 295 in Computer Vision by Shapiro and Stockman: Scan through the image and make a list of the first border pixel for each connected component. Then for each region, begin at its first border pixel and follow the border of the connected component around in a clockwise direction until the tracking returns to the first border pixel. 
	 *
	 * As a chain is made, borderImage is updated. The pixels are changed to background.
	 * (Idea: Change this, do not change the pixels to background but allow
	 * the chain to turn on itself until it reaches the start point again.
	 * The idea was applied, but at the end 
	 * the chain is not allowed to turn back on itself because
	 * of the difficulties of removing duplicate chains later.)
	 *
   * @param none
	 */	
	private void followBorders() { 
		//System.out.println("In followBorders of Border.java");
		int imageRows = imageHeight;
		int imageColumns = imageWidth;	
		int[][] borderImage = new int[imageRows+2][imageColumns+2];
		int label_c;
		//allRegions = new Region[noOfLabels];
		//allChains = new Hashtable(); //each entry is a linked list of chains

		//insert one row of background pixels at the bottom and one on top 
		for (int i = 0; i<imageColumns+2; i++) {
			borderImage[0][i] = bPixLabel;
			borderImage[imageRows+2-1][i] = bPixLabel;
		}
		for (int i = 0; i<imageRows+2; i++) {
			borderImage[i][0] = bPixLabel;
			borderImage[i][imageColumns+2-1] = bPixLabel;
		}
		//System.out.println("Copying...");
		for (int i = 1; i<imageRows+2-1; i++) {
			for (int j = 1; j<imageColumns+2-1; j++) {
				borderImage[i][j] = borderedImage[i-1][j-1];
			}
		}
		//System.out.println("Inserted background pixels on top and bottom.");

	 	for(int r = 1; r < imageRows+2-1; r++ ) {
			for( int c = 1; c < imageColumns+2-1; c++ ) {
				label_c = borderImage[r][c]; 
				if (label_c != bPixLabel && !(allRegions[label_c].getIsCharacter())) {
					makeAChain(borderImage, r, c);
				}
			}
		}
	}


  /**
   * Constructs a border chain given the border image and
	 * a start point. The label of the start point is obtained from the border image. 
	 * As a chain is made, borderImage is updated. The pixels are changed to background.
	 * p. 295 in Computer Vision by Shapiro and Stockman: Scan through the image and make a list of the first border pixel for each connected component. Then for each region, begin at its first border pixel and follow the border of the connected component around in a clockwise direction until the tracking returns to the first border pixel. 
	 * 1) Scan through the image to find the first point in the region, go back to the previous pixel, and take a clockwise route. Initially, .->. (first pixel found to be in the region.
	 * 2) Keep track of the current point r,c in the region and the previous point pr, pc. If r,c is the first pixel found in step 1, pr=r and pc=c-1.
	 * 3) Starting from pr, pc check each 8 neigbor of r,c in clockwise order until another point in the region is found. Call that point nr,nc. Set pr=r, pc=c and r=nr, c=nc.
	 * 4) Repeat from step 2 until the first point is reached again.
	 *
	 * As the pixels are followed, breakpoints are determined and
	 * chains are ended at those points, the following continues with a new chain.
	 * A chain is stored as a Primitive. The primitives are stores in Region objects.
	 *
   * @param borderImage The 2d array representation of the border pixels of the image
	 * @param row The row number of the beginning pixel
	 * @param column The column number of the beginning pixel
   */
	private void makeAChain(int[][] borderImage, int row, int column) {
		//System.out.println("In makeAChain of Border.java for point "+row+", "+column);
		int imageRows = imageHeight;
		int imageColumns = imageWidth;	
		//Size of borderImage is imageRows+2 x imageColumns+2
		int remainder, rowDifference, columnDifference;
		LinkedList chainSet;
		Region aRegion;
		LinkedList createdChains = new LinkedList();
		
		int	firstRow = row;
		int firstColumn = column;
		int labelNo = borderImage[row][column];
		int previousRow = row;
		int previousColumn = column - 1;
		int nextRow = row;
		int nextColumn = column;
		int newRow = row;
		int newColumn = column;
		int foundNext = 1;
		int isJunction = 0;
		int prevIsJunction = 0;
		int isBreakpoint = 0;
		int noOfPixels = 1;
		int onFirstPixel = 1;
		int onSecondPixel = 0;
		int checkRow = row;
		int checkColumn = column;
		int position = 3;
		int previousPosition = 3;
		int posSegment = 3;
		int previousPosSegment = 3;
		int posDiff = position - posSegment;
		int	previousPosDiff = previousPosition - previousPosSegment;
		int deviation = 0;
		int prevDeviation = 0;
		int deviation2 = 0;
		boolean newChain = true;
		Primitive aPrimitive = new Primitive(labelNo);
		aPrimitive.addPointToList(new Point(firstRow-1, firstColumn-1));

		//Change the beginning point the background in the border image.
		//The chain will not be allowed to turn back on itself.
		borderImage[firstRow][firstColumn] = bPixLabel;

		//System.out.println("\nStarting a new set of chains with point ("+(row-1)+", "+(column-1)+")");

		while (foundNext == 1 && onSecondPixel < 2) { //exits when the second pixel is crossed one more time; onSecondPixel becomes 2
			rowDifference = previousRow - row;
			columnDifference = previousColumn - column;
			previousPosition = position;
			position = findRelativeLocation(rowDifference, columnDifference); 
			//what does (r,c) see (pr, pc) as? Position is the answer to that question. In the first iteration, position will return 3. Previous position is set to 3, so deviation = 0, so the same chain is continuing.
			//if (position == 8)  //it is the same pixel; but this is not possible because (pr, cr) is going around (r, c), it will never be the same as (r, c) 
			if (noOfPixels > 2) {
				deviation = position - previousPosition;
				deviation2 = deviation + prevDeviation; //position - previousPreviousPosition
			}
			if (newChain) {
				posSegment = position;
				previousPosSegment = previousPosition;
			}
			posDiff = position - posSegment;
			previousPosDiff = previousPosition - previousPosSegment;
			if (position == 7 && posSegment == 0) {
				posDiff = -1;
			}
			else if (position == 0 && posSegment == 7) {
				posDiff = 1;
			}
			if (previousPosition == 7 && previousPosSegment == 0) {
				previousPosDiff = -1;
			}
			else if (previousPosition == 0 && previousPosSegment == 7) {
				previousPosDiff = 1;
			}
			isBreakpoint = 0;
			newChain = false;

			if (deviation == 0 || deviation == 1 || deviation == -1 || deviation == 7 || deviation == -7) { 
				/*CHAIN CONTINUES*/
				if (deviation2 == 0 || deviation2 == 1 || deviation2 == -1 || deviation2 == 7 || deviation2 == -7) { 
					/*CHAIN CONTINUES*/
				}

				else if (aPrimitive.getSize() > 3) { //Then this is a less sudden breakpoint
					/**********LESS SUDDEN BREAKPOINT***********/
					isBreakpoint = 1;
					newChain = true;
					aRegion = allRegions[labelNo];
					/********
					if (aRegion == null) { //there are no entries of this label yet
						//System.out.println("aRegion for label "+labelNo+" is null.");
						aRegion = new Region(labelNo);
						aRegion.addPrimitiveToRegion(aPrimitive);
						allRegions[labelNo] = aRegion;
					}
					else
					*******/
					//The chain ends, store the primitive in the corresponding Region.
					aRegion.addPrimitiveToRegion(aPrimitive);

					//Record all chains created, and at the end make the pixels in these chains background so that no other chain will start from these.
					createdChains.add(aPrimitive);

					//System.out.println("Found less sudden breakpoint, deviation: "+deviation+", deviation2: "+deviation2+". New chain, start point: ("+(row-1)+", "+(column-1)+")");

					//remove the last two points, begin the new chain with these points
					aPrimitive.removeLastPoint(); //will be the second point in the new chain
					aPrimitive.removeLastPoint(); //will be the first point in the new chain
					aPrimitive = new Primitive(labelNo);
					aPrimitive.addPointToList(new Point(previousRow-1, previousColumn-1));
					aPrimitive.addPointToList(new Point(row-1, column-1));
				} // end of else if -this is a less sudden breakpoint
			}

			else if (aPrimitive.getSize() > 2) { 
				/************SUDDEN BREAKPOINT***********/
				//Then this is a sudden breakpoint. Start a new chain, include (r, c). Then, (pr, pc) ends a chain.
				isBreakpoint = 1;
				newChain = true;
				aRegion = allRegions[labelNo];
				/**********
				if (aRegion == null) { //there are no entries of this label yet
					//System.out.println("aRegion for label "+labelNo+" is null.");
					aRegion = new Region(labelNo);
					aRegion.addPrimitiveToRegion(aPrimitive);
					allRegions[labelNo] = aRegion;
				}
				else 
				********/
				//The chain ends, store the primitive in the corresponding Region.
				aRegion.addPrimitiveToRegion(aPrimitive);

				//Record all chains created, and at the end make the pixels in these chains background so that no other chain will start from these.
				createdChains.add(aPrimitive);

				//System.out.println("Found sudden breakpoint, deviation: "+deviation+", deviation2: "+deviation2+". New chain, start point: ("+(row-1)+", "+(column-1)+"). Size: "+aPrimitive.getSize());

				//Remove the last point from the chain and start a new chain with that point
				aPrimitive.removeLastPoint();
				aPrimitive = new Primitive(labelNo);
				//Deviation 4 or -4 means that the chain turned back on itself, in that case include the pixel in both chains, otherwise only in the new chain.
				if (deviation == 4 || deviation == -4) { 
System.out.println("New chain starts with the previous point ("+(previousRow-1)+", "+(previousColumn-1)+")");
					aPrimitive.addPointToList(new Point(previousRow-1, previousColumn-1));
				}
				aPrimitive.addPointToList(new Point(row-1, column-1));
		  } //end of else (this is sudden a breakpoint)	

			//Check if there is another way - is this a junction, then this is a third kind of breakpoint.
			if (isBreakpoint == 0 && prevIsJunction == 0 && isJunction == 1 && aPrimitive.getSize() > 2) {
				/**************JUNCTION************/
				newChain = true;
				aRegion = allRegions[labelNo];
				/********
				if (aRegion == null) { //there are no entries of this label yet
					//System.out.println("aRegion for label "+labelNo+" is null.");
					aRegion = new Region(labelNo);
					aRegion.addPrimitiveToRegion(aPrimitive);
					allRegions[labelNo] = aRegion;
				}
				else 
				*******/
				//The chain ends, store the primitive in the corresponding Region.
				aRegion.addPrimitiveToRegion(aPrimitive);

				//Record all chains created, and at the end make the pixels in these chains background so that no other chain will start from these.
				createdChains.add(aPrimitive);

				//System.out.println("Found junction point. New chain, start point: ("+(row-1)+", "+(column-1)+"). Size: "+aPrimitive.getSize());

				//remove the last two points, begin the new chain with these points
				aPrimitive.removeLastPoint();
				aPrimitive = new Primitive(labelNo);
				aPrimitive.addPointToList(new Point(row-1, column-1));
			} // end of if this is a junction point 

/**********************
			if (!newChain && aPrimitive.getSize() > 2) { 
				if (Math.abs(posDiff) <= 1 && Math.abs(previousPosDiff) <= 1 && Math.abs(posDiff + previousPosDiff) < 2) {
				//	if (labelNo == 73) 
					//System.out.println("("+(row-1)+", "+(column-1)+") is a continuation of the segment.");
				}
				else {
					newChain = true;
					aRegion = allRegions[labelNo];
					aRegion.addPrimitiveToRegion(aPrimitive);
					createdChains.add(aPrimitive);
					//System.out.println("Found another break point. New chain, start point: ("+(row-1)+", "+(column-1)+"). Size: "+aPrimitive.getSize());
					aPrimitive.removeLastPoint();
					aPrimitive = new Primitive(labelNo);
					aPrimitive.addPointToList(new Point(row-1, column-1));
				}
			}
**********************/

			prevDeviation = deviation;
			prevIsJunction = isJunction;

			foundNext = 0;
			isJunction = 0;
			//Check the neighbors of the pixel
			for (int i = 7; i > 0; i--) { 
				//if i=0, the previous pixel is also checked, then the border can turn on itself.
				remainder = (position+i) - (((position+i)/8)*8); //position+i mod 8
				newRow = row + rPos[remainder];
				newColumn = column + cPos[remainder];
				if (borderImage[newRow][newColumn] == labelNo && foundNext == 0) {
					nextRow = newRow;
					nextColumn = newColumn;
					foundNext = 1;
					noOfPixels++;
					//break;
				}
				//Is there another one after this one? If yes, then this point is a junction 
				else if (borderImage[newRow][newColumn] == labelNo && foundNext == 1) {
					isJunction = 1;
				}
			}	//end of for (int i = 7; i >= 0; i--) { 

			//If nextRow, nextColumn is equal to previousRow, previousColumn, can we say that it started going back on itself? In that case, row, column was an endpoint?
			if (foundNext == 1) {
				if (nextRow == firstRow && nextColumn == firstColumn) {
				//System.out.println("Added the first pixel");
					onFirstPixel++;
				}
				if (noOfPixels == 2) {
					//System.out.println("Added the second pixel ("+nextRow+", "+nextColumn+")");
					checkRow = nextRow;
					checkColumn = nextColumn;
					onSecondPixel = 1;
				}
				if (noOfPixels > 2 && nextRow == checkRow && nextColumn == checkColumn && row == firstRow && column == firstColumn) {
					//System.out.println("Found the second pixel ("+nextRow+", "+nextColumn+") after the first one again. Chain should stop here, the last found pixel is not added.");
					onSecondPixel++;
				}
				else {
					//Add the next point to the chain
					aPrimitive.addPointToList(new Point(nextRow-1, nextColumn-1));

					//Change the border image so that this pixel is not backgrond
					borderImage[nextRow][nextColumn] = bPixLabel;
					previousRow = row;
					previousColumn = column;
					row = nextRow;
					column = nextColumn;
				}
			}//end of if (foundNext == 1)
		} //end of while

		//System.out.println("Adding a new chain beginning with "+firstRow+", "+firstColumn+" to the Region for label "+labelNo);
		//aChain.removeLast();

		if (aPrimitive.getSize() > 0) {
			aRegion = allRegions[labelNo];
			/*************
			if (aRegion == null) { //there are no entries of this label yet
				//System.out.println("Region for label "+labelNo+" is null.");
				aRegion = new Region(labelNo);
				aRegion.addPrimitiveToRegion(aPrimitive);
				allRegions[labelNo] = aRegion;
			}
			else
			**********/
			aRegion.addPrimitiveToRegion(aPrimitive);
			createdChains.add(aPrimitive);
			//System.out.println("Added the last chain with "+aPrimitive.getSize()+" pixels to the chains began with pixel ("+firstRow+", "+firstColumn+")");
		}

		//System.out.println("There are "+createdChains.size()+" chains created.");
		//Go through created chains, and make those pixels background.
		ListIterator lItr = createdChains.listIterator(0);
		ListIterator lItr2;
		Point aPoint;
		LinkedList aChain;
		while (lItr.hasNext()) {
			aPrimitive = (Primitive)lItr.next();
			//System.out.println("There are "+aPrimitive.getSize()+" pixels in aPrimitive.");
			aChain = aPrimitive.getAllPoints();
			lItr2 = aChain.listIterator(0);
			while (lItr2.hasNext()) {
				aPoint = (Point)lItr2.next();
				row = (int)aPoint.getX();
				column = (int)aPoint.getY();
				borderImage[row+1][column+1] = bPixLabel;
			}
		}
		//System.out.println("Set to background.");
	}


  /**
   * Stores the pixels that a pixel is neigboring in its own region 
	 * in the pixel database
	 *
   * @param none 
	 */	
	private	void storePixelNeigbors() {
		//System.out.println("Storing neigbor data in pixelData");
		int neigRow, neigColumn;
		Point aNeigbor;
		LinkedList neigborList;
		int[] neigbors;
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				if (borderedImage[i][j] != bPixLabel) {
					neigborList = new LinkedList();
					neigbors = getRegionNeigbors(i, j);
					for (int k = 1; k <= neigbors[0]; k++) {
						neigRow = i + rPos[neigbors[k]];
						neigColumn = j + cPos[neigbors[k]];
						aNeigbor = new Point(neigRow, neigColumn);
						neigborList.add(aNeigbor);
					}
					pixelData.put(i, j, neigborList);
				}
			}
		}
		//System.out.println("Stored pixel neigbors in pixelDatabase");
	}

  /**
   * Stores which primitives the pixel belongs to 
	 * in the pixel database.
	 *
   * @param none 
	 */	
	private	void storePrimitiveData() {
		//System.out.println("Storing data in pixelData");
		int row, column, tag, neigRow, neigColumn, index;
		int[] neigbors;
		Point aPoint, aNeigbor;
		Region aRegion;
		Primitive aPrim;
		Collection chainList;
		Iterator itr;
		ListIterator lItr;
		LinkedList alist, aPrimPoints, neigborList;
		for (int i = 1; i < noOfLabels; i++) {
			aRegion = allRegions[i];
			if (aRegion.getNumChains() > 0) {
				chainList = aRegion.getPrimitiveList();
				itr = chainList.iterator();
 		 		while (itr.hasNext()) {
					aPrim = (Primitive)itr.next();
					pixelData.put(aPrim);
				}
			}
		}
		//System.out.println("Stored pixelData in pixelDatabase");
	}


  /**
	 * Use pixelData to merge single pixel chains. 
	 * Check the size of each chain, if it is one and there are primitives
	 * in the region that neigbor that pixel or start or end with 
	 * that pixel, then try merging.
	 *
	 * Use pixelData to merge a curved pixel chain and a straight
	 * one following it. 
	 * Find purely horizontal chains
	 * Get the primitives that end or begin next to the beginning point
	 * of the horizontal chain at positions 4 or 5 of the begin point.
	 *
	 * Check to see that the neigbor is not vertical, if not, 
	 * then, it is a curve followed by the horizontal chain, merge them
	 * Merging is done recursively, one merged there may be other
	 * horizontal chains that can be merged to this one further.
	 *
	 * There are cases when two curves need to be merged -letter c and letter e
	 * Check not only horizontal lines, but all curves, they need to have a 
	 * neigbor near their beginning point at position 4 (bottom 
	 * left) if they are to be merged and no other neigbor in that area -
	 * nothing else is happening there.
	 *
	 * There is information in PointPixel regarding whether it is an endpoint or not.
	 *
   * @param none 
	 */	
	private void mergeChains() { 
		//System.out.println("\nMerging chains");
		int row, column, noOfPoints;
		int noOfNeigPrims = 0;
		int tag2 = -1;
		int neigTag = -1;
		int neigRow = -1;
		int neigColumn = -1;
		int tag, labelNo;
		int arow = -1;
		int acolumn = -1;
		boolean mergeable;
		Region aRegion;
		PointPixel aPixel, lastPixel;
		PixelData neigEntry, entry;
		Primitive aPrim, anotherPrim;
		Collection chainList;
		LinkedList alist, pixellist;
		ListIterator lItr, lItr2;
		for (int i = 1; i < noOfLabels; i++) {
			//System.out.println("Getting a region");
			aRegion = allRegions[i];
			if (aRegion.getNumChains() == 0) {
				continue;
			}
			chainList = aRegion.getPrimitiveList();
			LinkedList connectedChainList = new LinkedList();
			Iterator itr = chainList.iterator();
 		 	while (itr.hasNext()) {
				//System.out.println("Getting a primitive");
				aPrim = (Primitive)itr.next();
				mergeable = false;
				tag = -1;
				//Check the begin point of the horizontal chain
				row = (aPrim.getBeginPoint()).getRow();
				column = (aPrim.getBeginPoint()).getColumn();
				if (aPrim.getSize() == 1) { //there is a single pixel chain in this region
					tag = aPrim.getTagNo();
					//System.out.println("Primitive "+tag+" of region "+i+" has one pixel");
				}
				else if (row == (aPrim.getEndPoint()).getRow()) { 
					//This is a potential horizontal chain
					if (aPrim.isHorizontal()) {
						tag = aPrim.getTagNo();
						//System.out.println("Primitive "+tag+" of region "+i+" is horizontal");
					}
				}
				//Check cases of a primitive neigboring another at the begin or endpoints, one is much shorter than the other and the shorter one can extend the longer one -fit a straight line to the longer one, than fit a straight line to the shorter+longer one and see if the difference is small in the fitted straight line, if small, merge the shorter one to the longer one.
				if (tag != -1) {
					alist = (LinkedList)pixelData.getPixelDataWithNeigbors(row, column);
					noOfNeigPrims = 0;
					tag2 = -1;
					lItr = alist.listIterator(0);
					while (lItr.hasNext()) {
						neigEntry = (PixelData)lItr.next();
						neigTag = neigEntry.getTag();
						if (i == neigEntry.getLabel() && neigTag != tag && neigTag != tag2 && neigEntry.getPosition() != neigEntry.INTERIORPOINT) {
							tag2 = neigTag;
							neigRow = neigEntry.getRow();
							neigColumn = neigEntry.getColumn();
							entry = neigEntry;
							noOfNeigPrims++;
							/****
							anotherPrim = aRegion.getPrimitive(tag2);
							if (!anotherPrim.isVertical() && !anotherPrim.isHorizontal() && anotherPrim.getSize() > 4*aPrim.getSize()) {
								mergeable = connectChains(i, tag2, tag, connectedChainList, 1); 
							}
							****/
						}
					}
					//System.out.println("Region "+i+": Primitive "+tag+" has "+noOfNeigPrims+" neigboring primitives that end or begin at the neigborhood of its begin point");
					if (tag2 != -1)
						//System.out.println("Region "+i+": Primitive "+tag+" begins at "+row+", "+column+", primitive "+tag2+" begins or ends at "+neigRow+", "+neigColumn);						
						//Check the neigbors at position 4 of the horizontal chain only (the bottom left)
					if (noOfNeigPrims == 1 && neigRow - row == 1 && neigColumn - column == -1) {
						//Primitive tag has only one other primitive of the same label in its beginpoint neigborhood, and it is neigboring primitive of tag2 is at position 4 (bottom left) 
						anotherPrim = aRegion.getPrimitive(tag2);
						//System.out.println("Primitive "+tag+" of "+aPrim.getSize()+" pixels of region "+i+" has an endpoint neigbor primitive "+tag2+" of size "+anotherPrim.getSize());
						//if (!anotherPrim.isVertical() && !anotherPrim.isHorizontal() && anotherPrim.getSize() > 4*aPrim.getSize()) {
						if ((!anotherPrim.isVertical() && !anotherPrim.isHorizontal() && anotherPrim.getSize() > 2*aPrim.getSize()) || (anotherPrim.isHorizontal())) {
							//System.out.println("Entry: "+entry+" is an endpoint");
							//System.out.println("Last pixel of primitive "+tag2+" is "+lastPixel);
							//System.out.println("Primitive "+tag2+" is not vertical and not horizontal.");
							//System.out.println("Primitive "+tag+" of "+aPrim.getSize()+" pixels of region "+i+" has an endpoint neigbor primitive "+tag2+" of "+anotherPrim.getSize()+" points -> connect");
							mergeable = connectChains(i, tag2, tag, connectedChainList); 
							//No threshold means connect unconditionally. tag chain is added to tag2 chain. tag chain is updated, tag2 chain remains the same.
							//Once connected, need to check other horizontal chains that can be connected further -do it recursively
							//break;
						} //end of else if !anotherPrim.isVertical() && !anotherPrim.isHorizontal 
					} //end of if (noOfNeigPrims == 1 && neigRow - row == 1 && neigColumn - column == -1) 
				} //end of if tag != -1 

				//Go through the connected chain list
				//if (connectedChainList != null) {
				if (mergeable) {
					ListIterator clItr = connectedChainList.listIterator(0);
					while (clItr.hasNext()) {
						int removeTag = ((Integer)clItr.next()).intValue();
						//System.out.print("Primitive "+removeTag+" of region "+i+" has been merged. "); 
						aRegion.removePrimitive(removeTag);
						//System.out.println("Removed primitive from region.");
					}
					chainList = aRegion.getPrimitiveList();
					connectedChainList = new LinkedList();
					itr = chainList.iterator();
				}
			} //end of traversing chains in a region
		} //end of traversing regions
	}


  /**
	 * There is information in PointPixel regarding whether it is an endpoint or not.
	 * 
	 * Check cases of a primitive neigboring another at the begin or endpoints, 
	 * one is much shorter than the other and the shorter one can extend the longer one 
	 * -fit a straight line to the longer one, than fit a straight line to the 
	 * shorter+longer one and see if the difference is small in the 
	 * fitted straight line, if small, merge the shorter one to the longer one.
	 * Call connectChains with a treshold of 1.
	 * No threshold means connect unconditionally. tag chain is added to tag2 chain. 
	 * tag chain is updated, tag2 chain remains the same.
	 *
   * @param none 
	 */	
	private void mergeChains2() { 
		//System.out.println("\nMerging chains (2)");
		int row, column, noOfPoints;
		int noOfNeigPrims = 0;
		int tag2 = -1;
		int neigRow = -1;
		int neigColumn = -1;
		int tag, labelNo;
		int arow = -1;
		int acolumn = -1;
		boolean mergeable;
		Region aRegion;
		PointPixel aPixel, lastPixel;
		PixelData entry;
		Primitive aPrim, anotherPrim;
		Collection chainList;
		LinkedList alist, pixellist;
		ListIterator lItr, lItr2;
		for (int i = 1; i < noOfLabels; i++) {
			//System.out.println("Getting a region");
			aRegion = allRegions[i];
			if (aRegion.getNumChains() == 0) {
				continue;
			}
			chainList = aRegion.getPrimitiveList();
			LinkedList connectedChainList = new LinkedList();
			Iterator itr = chainList.iterator();
 		 	while (itr.hasNext()) {
				//System.out.println("Getting a primitive");
				aPrim = (Primitive)itr.next();
				mergeable = false;
				tag = aPrim.getTagNo();
				for (int k = 1; k <= 2; k++) { //do it two times, for both endpoints
					//Check the begin point and the end point of the chain
					if (k == 1) {
						row = (aPrim.getBeginPoint()).getRow();
						column = (aPrim.getBeginPoint()).getColumn();
					}
					else {
						row = (aPrim.getEndPoint()).getRow();
						column = (aPrim.getEndPoint()).getColumn();
					}
					alist = (LinkedList)pixelData.getPixelDataWithNeigbors(row, column);
					noOfNeigPrims = 0;
					tag2 = -1;
					lItr = alist.listIterator(0);
					while (lItr.hasNext()) {
						entry = (PixelData)lItr.next();
						tag2 = entry.getTag();
						if (i == entry.getLabel() && tag2 != tag && entry.getPosition() != entry.INTERIORPOINT) {
							neigRow = entry.getRow();
							neigColumn = entry.getColumn();
							noOfNeigPrims++;
							anotherPrim = aRegion.getPrimitive(tag2);
							//System.out.println("Region "+i+": Primitive "+tag+" of "+aPrim.getSize()+" pixels of region "+i+" has an endpoint neigbor primitive "+tag2+" of size "+anotherPrim.getSize());
							//System.out.println("Primitive "+tag+" begins at "+row+", "+column+", primitive "+tag2+" begins or ends at "+neigRow+", "+neigColumn);						
							//if ((!anotherPrim.isVertical() && !anotherPrim.isHorizontal()) || (anotherPrim.isVertical() && aPrim.isVertical()) || (anotherPrim.isHorizontal() && aPrim.isHorizontal()) ) {

							if (anotherPrim.getSize() > 4*aPrim.getSize() && !anotherPrim.isVertical() && !anotherPrim.isHorizontal()) {
								//System.out.println("Connect primitive "+tag+" to primitive "+tag2);
								mergeable = connectChains(i, tag2, tag, connectedChainList, 1); 
								if (mergeable) {
									break;
								}
							}
						}	
					}	
					//System.out.println("Region "+i+": Primitive "+tag+" has "+noOfNeigPrims+" neigboring primitives that end or begin at the neigborhood of its begin point");
					if (k == 1 && mergeable) 
						break;
				} //end of checking the begin and the end point of the chain
			} //end of traversing chains in a region

			//Go through the connected chain list (the chains that were connected to other
			//chains and therefore need to be removed
			if (connectedChainList != null) {
				ListIterator clItr = connectedChainList.listIterator(0);
				while (clItr.hasNext()) {
					int removeTag = ((Integer)clItr.next()).intValue();
					//System.out.print("Primitive "+removeTag+" of region "+i+" has been merged. "); 
					aRegion.removePrimitive(removeTag);
					//System.out.println("Removed primitive from region.");
				}
			}	
		} //end of traversing regions
	}


  /**
	 * No threshold is given. The chains are connected unconditionally.
	 * The second chain is connected to the first chain and the database 
	 * is updated. 
	 * After connecting, the endpoints of the new primitive are checked to see
	 * if there any more horizontal chains to be added.
	 * connectChains is called recursively.
	 *
   * @param labelNo The label of the region the chains are in
	 * @param tag The tag number of the primitive for the first chain
	 * @param tag2 The tag number of the primitive for the second chain
	 * @param connectedChains The linked list of second chains that are connected to the first chains
   * @param True if chains were merged, false if they were not
   */
	private boolean connectChains(int labelNo, int tag, int tag2, LinkedList connectedChains) {
		//System.out.println("In unconditional connect chains for "+tag+" and "+tag2+" of region "+labelNo);
		Region aRegion = allRegions[labelNo];
		Primitive firstPrim = aRegion.getPrimitive(tag);
		Primitive secondPrim = aRegion.getPrimitive(tag2);
		boolean mergeable = false;
		int mergeType = -1;
		/****
		int mergetype = -1;
		Point beginPixel = (Point)newChain.getFirst();
		Point endPixel = (Point)newChain.getLast();
		PointPixel beginPixel2 = new PointPixel(secondChain.getFirst());
		PointPixel endPixel2 = new PointPixel(secondChain.getLast());
		if (beginPixel2.neigbors8(beginPixel) || beginPixel2.equals(beginPixel)) { //Case end begin-begin end
			mergetype = Primitive.BEGINBEGIN;
		}
		else if (beginPixel2.neigbors8(endPixel) || beginPixel2.equals(endPixel)) { //Case begin end-begin end 
			mergetype = Primitive.ENDBEGIN;
		}
		else if (endPixel2.neigbors8(beginPixel) || endPixel2.equals(beginPixel)) { //Case end begin-end begin
			mergetype = Primitive.BEGINEND;
		}
		else if (endPixel2.neigbors8(endPixel) || endPixel2.equals(endPixel)) { //Case begin end-end begin 
			mergetype = Primitive.ENDEND;
		}
		//System.out.println("Merge type is "+mergetype);
		*******/
		//Change the begin point of the first primitive to an interior point in the database
		pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.STARTPOINT, PixelData.INTERIORPOINT);

		//Change the end point of the first primitive to an interior point in the database
		pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.ENDPOINT, PixelData.INTERIORPOINT);

		//Change the begin point of the second primitive to an interior point in the database
		pixelData.updatePixelPosition(secondPrim.getBeginPoint(), labelNo, tag, PixelData.STARTPOINT, PixelData.INTERIORPOINT);

		//Change the end point of the second primitive to an interior point in the database
		pixelData.updatePixelPosition(secondPrim.getEndPoint(), labelNo, tag, PixelData.ENDPOINT, PixelData.INTERIORPOINT);

		//Add the second primitive to the first primitive
		mergeType = firstPrim.addPrimitive(secondPrim);

		//Record the second primitive's tag in connected chains
		connectedChains.add(new Integer(tag2));

		mergeable = true;
		//The second primitive is added to the first primitive, its tag is now the same
		//as the first primitive
		pixelData.updatePixelTag(secondPrim, tag);
		secondPrim.setTagNo(tag);

		//The first primitive is the merged primitive. 
		//Set the begin point of the first primitive to a start point in the database 
		pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.STARTPOINT);

		//Set the end point of the first primitive to a start point in the database 
		pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.ENDPOINT);
		//System.out.println("pixelData is updated.");
		
		//Check if there are other primitives that can be connected to the first primitive
		int row = -1;
		int column = -1;
		if (mergeType == Primitive.BEGINEND || mergeType == Primitive.BEGINBEGIN) {
			row = (firstPrim.getBeginPoint()).getRow();
			column = (firstPrim.getBeginPoint()).getColumn();
		}
		else if (mergeType == Primitive.ENDBEGIN || mergeType == Primitive.ENDEND) {
			row = (firstPrim.getEndPoint()).getRow();
			column = (firstPrim.getEndPoint()).getColumn();
		}
		PixelData entry;
		int tag3;
		int label3;
		Primitive thirdPrim;
		LinkedList alist = (LinkedList)pixelData.getPixelDataWithNeigbors(row, column);
		ListIterator lItr = alist.listIterator(0);
		while (lItr.hasNext()) {
			//System.out.println("Getting an entry");
			entry = (PixelData)lItr.next();
			tag3 = entry.getTag();
			label3 = entry.getLabel();
			if (label3 == labelNo && tag3 != tag && entry.getPosition() != entry.INTERIORPOINT) { //This point is part of a different primitive in the same region
				thirdPrim = aRegion.getPrimitive(tag3);
				if (thirdPrim.isHorizontal()) {
					//Connect chain of tag3 to chain of tag
					connectChains(label3, tag, tag3, connectedChains);
				}
			}
		}	
		return mergeable;
	}


  /**
   * Merges two chains of the same label if they are found to be mergeable. 
	 * 
	 * tag2 chain is added to tag chain. 
	 * tag chain information in the region and in pixelDatabase is updated
	 * tag2 chain information stays the same, it is not chaged
	 *
	 * Fit a straight line to the first chain 
	 * -a straight line connecting the two endpoints. 
	 * Start adding the pixels of the second chain, 
	 * fit a new straight line each time from the begin point to this new point. 
	 * Calculate the distance of all the points from the new straight line and 
	 * find the maximum distance. 
	 * You can merge until the maximum distance is greater than the threshold.
	 * If the maximum distance is under a certain threshold for all the pixels
	 * of the second chain, you can merge the two chains completely. 
	 * 
	 * Another approach: 
	 * Fit a straight line to the first chain and the second chain and 
	 * to the chain that would result if they are merged. Compare.
	 *
   * @param labelNo The label of the region the chains are in
	 * @param tag The tag number of the primitive for the first chain
	 * @param tag2 The tag number of the primitive for the second chain
	 * @param connectedChains The linked list of second chains that are connected to the first chains
	 * @param threshold The max distance value when the two chains are connected. The chains are connected if the value is below this given threshold.
   * @param True if chains were merged, false if they were not
   */
	private boolean connectChains(int labelNo, int tag, int tag2, LinkedList connectedChains, int threshold) {
		//System.out.println("In conditional connectChains for "+tag+" and "+tag2+" of region "+labelNo);
		double maxDist = 0;
		double distance;
		int row, column;
		int mergetype = -1;
		boolean mergeable = false;
		PointPixel aPoint, oldPoint;
		LineFitter aLineFitter = new LineFitter();
		Primitive firstPrim = allRegions[labelNo].getPrimitive(tag);
		Primitive secondPrim = allRegions[labelNo].getPrimitive(tag2);
		LinkedList aChain = firstPrim.getAllPoints();
		LinkedList secondChain = secondPrim.getAllPoints();
		LinkedList newChain = new LinkedList(); //copy aChain into newChain
		ListIterator lItr = aChain.listIterator(0);
		while (lItr.hasNext()){
			newChain.add(new PointPixel(lItr.next()));
		}
		PointPixel beginPixel = new PointPixel(newChain.getFirst());
		PointPixel endPixel = new PointPixel(newChain.getLast());
		PointPixel beginPixel2 = new PointPixel(secondChain.getFirst());
		PointPixel endPixel2 = new PointPixel(secondChain.getLast());

		if (beginPixel2.neigbors8(beginPixel) || beginPixel2.equals(beginPixel)) { //Case end begin-begin end
			//Traverse second chain in forward direction
			//Add new points to the beginning of first chain
			endPixel = new PointPixel(newChain.getFirst());
			beginPixel = new PointPixel(newChain.getLast());
			mergetype = Primitive.BEGINBEGIN;
		}
		else if (beginPixel2.neigbors8(endPixel) || beginPixel2.equals(endPixel)) { //Case begin end-begin end 
			//Traverse second chain in forward direction
			//Add new points to the end of first chain
			mergetype = Primitive.ENDBEGIN;
		}
		else if (endPixel2.neigbors8(beginPixel) || endPixel2.equals(beginPixel)) { //Case end begin-end begin
			//Traverse second chain in reverse direction
			//Add new points to the beginning of first chain
			endPixel = new PointPixel(newChain.getFirst());
			beginPixel = new PointPixel(newChain.getLast());
			mergetype = Primitive.BEGINEND;
		}
		else if (endPixel2.neigbors8(endPixel) || endPixel2.equals(endPixel)) { //Case begin end-end begin 
			//Traverse second chain in reverse direction
			//Add new points to the end first chain
			mergetype = Primitive.ENDEND;
		}
		//System.out.println("Merge type is "+mergetype);
		if (mergetype == Primitive.BEGINBEGIN || mergetype == Primitive.ENDBEGIN) {
			ListIterator lItr2 = secondChain.listIterator(0);
			while (lItr2.hasNext() && maxDist < threshold) {
				aPoint = new PointPixel(lItr2.next()); //Try to extend the first chain by the first pixels of the second chain - is the last point of the first one next to the first point of the second one?
				maxDist = 0;
				lItr = newChain.listIterator(0); 
				while (lItr.hasNext() && maxDist < threshold) { 
					//Calculate the distance of each pixel to the line between beginPixel and aPoint
					oldPoint = new PointPixel(lItr.next());
					row = oldPoint.getRow();
					column = oldPoint.getColumn();
					distance = aLineFitter.squareDistPointToLine(row, column, beginPixel, aPoint);
					//System.out.println("Distance from point ("+row+", "+column+") to line from "+beginPixel+" to " +aPoint+" is "+distance);
					if (distance > maxDist) {
						maxDist = distance;
					}
				}			
				/****if (maxDist < threshold) { //extend the first chain (newChain) by adding aPoint
				//Discard duplicate points -if one endpoint is the same point!
					if (mergetype == Primitive.ENDBEGIN)
						newChain.add(aPoint);
					else if (mergetype == Primitive.BEGINBEGIN)
						newChain.addFirst(aPoint);
				}
				****/
			}
		}
		else if (mergetype == Primitive.BEGINEND || mergetype == Primitive.ENDEND) {
			if (secondChain.size() == 1) {
				aPoint = new PointPixel(secondChain.getFirst()); //Try to extend the first chain by the first pixels of the second chain - is the last point of the first one next to the first point of the second one???!!!
				maxDist = 0;
				lItr = newChain.listIterator(0); 
				while (lItr.hasNext() && maxDist < threshold) { //Calculate the distance of each pixel to the line between beginPixel and aPoint
					oldPoint = new PointPixel(lItr.next());
					row = oldPoint.getRow();
					column = oldPoint.getColumn();
					distance = aLineFitter.squareDistPointToLine(row, column, beginPixel, aPoint);
					//System.out.println("Distance from point ("+row+", "+column+") to line from "+beginPixel+" to " +aPoint+" is "+distance);
					if (distance > maxDist) {
						maxDist = distance;
					}
				}
			}
			ListIterator lItr2 = secondChain.listIterator(secondChain.size());
			while (lItr2.hasPrevious() && maxDist < threshold) {
				aPoint = new PointPixel(lItr2.previous()); //Try to extend the first chain by the first pixels of the second chain - is the last point of the first one next to the first point of the second one???!!!
				maxDist = 0;
				lItr = newChain.listIterator(0); 
				while (lItr.hasNext() && maxDist < threshold) { //Calculate the distance of each pixel to the line between beginPixel and aPoint
					oldPoint = new PointPixel(lItr.next());
					row = oldPoint.getRow();
					column = oldPoint.getColumn();
					distance = aLineFitter.squareDistPointToLine(row, column, beginPixel, aPoint);
					//System.out.println("Distance from point ("+row+", "+column+") to line from "+beginPixel+" to " +aPoint+" is "+distance);
					if (distance > maxDist) {
						maxDist = distance;
					}
				}			
				/*****if (maxDist < threshold) { //extend the first chain (newChain) by adding aPoint
				//Discard duplicate points -if one endpoint is the same point!
					if (mergetype == Primitive.ENDEND)
						newChain.add(aPoint);
					else if (mergetype == Primitive.BEGINEND)
						newChain.addFirst(aPoint);
				}
				*****/
			}
		}

		//System.out.println("Max distance is "+maxDist+". Threshold is "+threshold);
		if (mergetype != -1 && maxDist < threshold) { //threshold is an argument of this function
			//System.out.print("Mergeable, merging. ");
			//The first chain can be completely extended by the second chain

			/****
			mergeable = true;
			pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.STARTPOINT, PixelData.INTERIORPOINT);
			pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.ENDPOINT, PixelData.INTERIORPOINT);
			firstPrim.addPrimitive(secondPrim, mergetype);
			pixelData.put(secondPrim.getAllPoints(), labelNo, tag); 
			pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.STARTPOINT);
			pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.ENDPOINT);
			//System.out.println("pixelData is updated.");
			***/

			pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.STARTPOINT, PixelData.INTERIORPOINT);
			pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.ENDPOINT, PixelData.INTERIORPOINT);
			pixelData.updatePixelPosition(secondPrim.getBeginPoint(), labelNo, tag, PixelData.STARTPOINT, PixelData.INTERIORPOINT);
			pixelData.updatePixelPosition(secondPrim.getEndPoint(), labelNo, tag, PixelData.ENDPOINT, PixelData.INTERIORPOINT);
			firstPrim.addPrimitive(secondPrim, mergetype);
			//mergeType = firstPrim.addPrimitive(secondPrim);
			connectedChains.add(new Integer(tag2));
			mergeable = true;
			pixelData.updatePixelTag(secondPrim, tag);
			secondPrim.setTagNo(tag);
			pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.STARTPOINT);
			pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.ENDPOINT);
			//System.out.println("pixelData is updated.");

		}
		return mergeable;
	}




  /**
	 * Returns the relative location of a pixel with respect
	 * to another given the differences in row and column.
	 * What does (r,c) see (pr, pc) as?
 	 * This method returns the answer to that question.
	 *
	 * @param rowDifference The difference between the row numbers of the two pixels
	 * @param columnDifference The difference between the column numbers of the two pixels
	 * @return The relative position of the two pixels
   */
	private int findRelativeLocation(int rowDifference, int columnDifference) {
		int position = -1;
		if (rowDifference == -1) {
			if (columnDifference == 1) {
				position = 0;
			}
			else if (columnDifference == 0) {
				position = 1;
			}
			else if (columnDifference == -1) {
				position = 2;
			}
		}
		else if (rowDifference == 0) {
			if (columnDifference == -1) {
				position = 3;
			}
			else if (columnDifference == 0) {
				position = 8;
			}
			else if (columnDifference == 1) {
				position = 7;
			}
		}
		else if (rowDifference == 1) {
			if (columnDifference == 1) {
				position = 6;
			}
			else if (columnDifference == 0) {
				position = 5;
			}
			else if (columnDifference == -1) {
				position = 4;
			}
		}
		return position;
	}


	/**
	 * Returns the number and location of the neigbors
	 * of this pixel which are in the same region as itself.
	 *
	 * @param row The row number of the given pixel
	 * @param column The column number of the given pixel
	 * @return The integer array holding the positions of the neighbors in the same region
	 */
	private int[] getRegionNeigbors(int row, int column) {
		PointPixel neigPoint;
 		int labeln;
	 	int numNeigbors = 0;
	 	int k = 0;
	 	int[] neigborArray = new int[9];
	 	neigborArray[0] = numNeigbors;

		int labelc = inPixImage[row][column];
 		PointPixel aPoint = new PointPixel(row, column);
	 	LinkedList neigborList = aPoint.getNeigbors8();
	 	ListIterator lItr = neigborList.listIterator();
	 	while (lItr.hasNext()) {
		 	neigPoint = new PointPixel(lItr.next());
			labeln = borderedImage[neigPoint.getRow()][neigPoint.getColumn()];
			if (labeln == labelc) {
		 		numNeigbors++;
			 	neigborArray[numNeigbors] = k;
			}
		 	k++;
		}
	 	neigborArray[0] = numNeigbors;
	 	return neigborArray;
 	}


  /**
   * Prints the number of border chains in each region,
	 * and the primitive data; the tag number of the primitive in that region,
	 * the number of pixels in that primitive, the start point and the end point.
	 *
   * @param outfilename The name of the file in which the information is stored 
   */
  public void displayBorderChains(String outfilename) {
		int chainNo;
		Region aRegion;
		PointPixel firstPixel, lastPixel;
		Primitive aPrim;
		Collection chainList;
		try {
			BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(outfilename));
			for (int i = 1; i < noOfLabels; i++) {
				aRegion = allRegions[i];
				//aRegion.displayPrimitives();
				ostream.write(("\nRegion "+i+" has "+aRegion.getNumChains()+ " chains, "+aRegion.getSize()+" mappings.\n").getBytes());
				if (aRegion.getNumChains() == 0) {
					continue;
				}
				chainList = aRegion.getPrimitiveList();
				Iterator itr = chainList.iterator();
 			 	while (itr.hasNext()) {
					aPrim = (Primitive)itr.next();
					firstPixel = aPrim.getBeginPoint();
					lastPixel = aPrim.getEndPoint();
					ostream.write(("Chain " + aPrim.getTagNo() + " has " + aPrim.getSize() + " pixels. First pixel: "+ firstPixel + "  Last pixel: "+lastPixel+"\n").getBytes());
				}
			}
			ostream.close();
		}
		catch (Exception e) {System.out.println(e.getMessage());}
	}


  /**
   * Creates and returns a 2d array image of all the chains of the border image.
	 * The color of each chain is different.
	 *
   * @param none
	 * @return The 2d array representation of the chains image
   */
  public int[][] makeChainImage() {
		int noOfChains = 0;	
		int countChains = 0;
		int[][] chainImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				chainImage[i][j] = 255;
			}
		}
		PointPixel aPixel;

		Region aRegion;
		Collection chainList;
		LinkedList aChain;
		Primitive aPrim;
 		int remainder;
		int totalChainSize = 0;
		double averageChainSize;
		for (int i = 1; i < noOfLabels; i++) {
			aRegion = allRegions[i];
			noOfChains = aRegion.getNumChains();
			if (noOfChains == 0) {
				continue;
			}
			chainList = aRegion.getPrimitiveList();
			countChains = 0;
			totalChainSize = 0;
			Iterator itr = chainList.iterator();
			while (itr.hasNext()) {
				countChains++;
				aPrim = (Primitive)itr.next();
				totalChainSize = totalChainSize + aPrim.getSize();
			}
			averageChainSize = totalChainSize/countChains;
			countChains = 0;
			itr = chainList.iterator();
			while (itr.hasNext()) {
				aPrim = (Primitive)itr.next();
				countChains++;
				aChain = aPrim.getAllPoints();
				ListIterator lItr2 = aChain.listIterator(0);
				while (lItr2.hasNext()) {
					aPixel = new PointPixel(lItr2.next());
					if (noOfChains == 1) {
						chainImage[aPixel.getRow()][aPixel.getColumn()] = 0;
					}
					else {
						remainder = countChains - ((countChains/5)*5);
						chainImage[aPixel.getRow()][aPixel.getColumn()] = (int)(50+remainder*(180)/5);
					}
				}
			}
		}

		/******LinkedList chainSet, aChain;	
		for (int i = 1; i < noOfLabels; i++) {
			chainSet = (LinkedList)allChains.get(new Integer(i));
			noOfChains = chainSet.size();
			countChains = 0;
			ListIterator lItr = chainSet.listIterator(0);
 		 	while (lItr.hasNext()) {
				countChains++;
				aChain = (LinkedList)lItr.next();
				ListIterator lItr2 = aChain.listIterator(0);
 				while (lItr2.hasNext()) {
					aPixel = new PointPixel(lItr2.next());
					if (noOfChains == 1) {
						chainImage[aPixel.getRow()][aPixel.getColumn()] = 0;
					}
					else {
						chainImage[aPixel.getRow()][aPixel.getColumn()] = (int)(50+countChains*(120/noOfChains));
					}
				}
			}
		}
		********/
		return chainImage;
	}	


  /**
   * Returns the 2-d array of border pixels. 
	 *
   * @param none
   * @return The 2d array representation of the border pixels
   */
  int[][] getBorderEdgeImage2D() {
    return borderedImage;
  }

  /**
   * This method returns the array of regions. 
	 *
   * @param none
   * @return The Region array of all the regions in the image 
   */
  public Region[] getRegions() {
    return allRegions;
  }


  /**
   * Returns the PixelDatabase of all the pixels. 
	 *
   * @param none
	 * @return The pixel database
   */
  public PixelDatabase getPixelDatabase() {
    return pixelData;
  }
	

  /**
   * Returns the number of regions. 
	 *
   * @param none
   * @return The number of regions 
   */
  public int getNumRegions() {
    return noOfLabels;
  }


/**************
The method below here are not used!!!
**************/

  /**
   * @param none 
   * 
   * This method fits straight lines to chains and splits them. 
	 * aPrim is contained by anotherPrim at all points, but one point.
	 */	
	private boolean mergeAnEndpoint(Primitive aPrim, Primitive anotherPrim) {
		PixelData entry;
		boolean mergeable = false;
		PointPixel beginPoint = aPrim.getBeginPoint();
		PointPixel endPoint = aPrim.getEndPoint();
		Primitive secondPrim;
		int checkPoint = PixelData.STARTPOINT;
		int labelNo = aPrim.getParent();
		int tag = aPrim.getTagNo();
		LinkedList dataList2;
		ListIterator lItr2;
		LinkedList dataList = pixelData.getPixelData(beginPoint); 
		ListIterator lItr = dataList.listIterator(0);
		//System.out.println("In mergeAnEndpoint. Begin point of primitive "+tag+" has "+dataList.size()+" entries"); 
		while (lItr.hasNext()) {
			entry = (PixelData)lItr.next();
			if (entry.getTag() == anotherPrim.getTagNo()) {
			//Begin point is included in anotherPrim
				checkPoint = PixelData.ENDPOINT;
				break;
			}
		}
		//System.out.println("Checking the "+checkPoint+" of primitive "+tag);
		if (checkPoint == PixelData.STARTPOINT) { //this point is not contained by anotherPrim
			if (dataList.size() > 1) {
				//It is already a part of some other primitive other than aPrim
				mergeable = true;
			}
			else { //It is not part of another primitive.
				dataList2 = pixelData.getPixelDataWithNeigbors(beginPoint); 
				lItr2 = dataList2.listIterator(0);
				while (lItr2.hasNext()) {
					entry = (PixelData)lItr.next();
					if (entry.getTag() != aPrim.getTagNo() && entry.getPosition() != PixelData.INTERIORPOINT) {
					//It is part of another primitive's end point
					 	secondPrim = allRegions[labelNo].getPrimitive(entry.getTag());	
						if (secondPrim.isVertical() || secondPrim.isHorizontal()) {
							mergeable = connectEndpoint(labelNo, entry.getTag(), beginPoint, 1);
							//System.out.println("Region "+labelNo+": In mergeAnEndpoint. Connecting "+beginPoint+" to primitive "+entry.getTag()+" -> "+mergeable);
							if (mergeable)
								break;
						}
						else {
							mergeable = connectEndpoint(labelNo, entry.getTag(), beginPoint, imageHeight);
							//System.out.println("Region "+labelNo+": High threshold. Connecting "+beginPoint+" to primitive "+entry.getTag()+" -> "+mergeable);
							if (mergeable)
								break;
						}
					}
				}
			}
		}
		else if (checkPoint == PixelData.ENDPOINT) { //this point is not contained by anotherPrim
			dataList = pixelData.getPixelData(endPoint); 
		//System.out.println("End point of primitive "+tag+" has "+dataList.size()+" entries"); 
			if (dataList.size() > 1) {
				//It is already a part of some other primitive other than aPrim
				mergeable = true;
			}
			else { //It is not part of another primitive.
				dataList2 = pixelData.getPixelDataWithNeigbors(endPoint); 
		//System.out.println("End point of primitive "+tag+" has "+dataList2.size()+" entries with its neigbors"); 
				lItr2 = dataList2.listIterator(0);
				while (lItr2.hasNext()) {
					entry = (PixelData)lItr2.next();
					if (entry.getTag() != aPrim.getTagNo() && entry.getPosition() != PixelData.INTERIORPOINT) {
					//It is part of another primitive's end point
					 	secondPrim = allRegions[labelNo].getPrimitive(entry.getTag());	
						if (secondPrim.isVertical() || secondPrim.isHorizontal()) {
							mergeable = connectEndpoint(labelNo, entry.getTag(), endPoint, 1);
							//System.out.println("Region "+labelNo+": In mergeAnEndpoint. Connecting "+endPoint+" to primitive "+entry.getTag()+" -> "+mergeable);
							if (mergeable)
								break;
						}
						else {
							mergeable = connectEndpoint(labelNo, entry.getTag(), endPoint, imageHeight);
							//System.out.println("Region "+labelNo+": High threshold. Connecting "+endPoint+" to primitive "+entry.getTag()+" -> "+mergeable);
							if (mergeable)
								break;
						}
					}
				}
			}
		}
		return mergeable;
	}


  /**
   * @param none 
   * 
   * This method fits straight lines to chains and splits them. 
	 */	
	private void splitChains() { 
		//System.out.println("In splitChains of Border.java");
		int imageRows = imageHeight;
		int imageColumns = imageWidth;	
		int[][] borderImage = new int[imageRows+2][imageColumns+2];
		int label_c;
		Collection chainList;
		Primitive aPrim;
		LinkedList aChain, newChain;
		Region aRegion;
		Region anotherRegion;
		LineFitter aLineFitter = new LineFitter();
		Vector lineParam;

		int type, row, column, count;
		double slope, intercept;
		double est1 = 0;
		double est2 = 0;
		double sideLength2, dist2;
		double previousDist2 = 0;
	  int trend = NOCHANGE;
		int previousTrend = NOCHANGE;
		int side = RIGHT;
		int previousSide = RIGHT;
		Point aPoint;
		PointPixel firstPixel, lastPixel;

		for (int i = 1; i < noOfLabels; i++) {
		//System.out.println("Region "+i);
			aRegion = allRegions[i];
			if (aRegion.getNumChains() == 0) {
				continue;
			}
			anotherRegion = new Region(i);
			chainList = aRegion.getPrimitiveList();
			Iterator itr = chainList.iterator();
 		 	while (itr.hasNext()) {
//				//System.out.println("Getting a primitive");
				aPrim = (Primitive)itr.next();
//Fit straight line to the primitive. Compute the distances of all the points in the chain to that straight line. When you find a local maximum, break the chain at that point.
				aChain = aPrim.getAllPoints();
				//System.out.println("Got one prim of "+aPrim.getSize()+" pixels. Got one chain of "+aChain.size()+" pixels");
				lineParam = aLineFitter.fitStraightLineLSE(aChain);
//				//System.out.println("Got param");
//Go through the whole chain, calculate the distance of each point to the best fit line.
				type = ((Integer)lineParam.get(0)).intValue();
				slope = ((Double)lineParam.get(1)).doubleValue();
				intercept = ((Double)lineParam.get(2)).doubleValue();
				firstPixel = aPrim.getBeginPoint();
				lastPixel = aPrim.getEndPoint();
				//System.out.println("Chain is type: "+type+", "+firstPixel+", "+lastPixel+", slope: "+slope+", int: "+intercept);
				count = 0;
				trend = NOCHANGE;
				side = RIGHT;
				ListIterator lItr = aChain.listIterator(0);
				while (lItr.hasNext()) {
					count++;
					aPoint = (Point)lItr.next();
					row = (int)aPoint.getX();
					column = (int)aPoint.getY();
					if (type == aPrim.VERTICAL) {  //slope = columns/rows
						est1 = row;
						est2 = (slope * row) + intercept;
						if (column >= est2) {
							side = RIGHT;
						}
						else if (column < est2) {
							side = LEFT;
						}
					}
					else if (type == aPrim.HORIZONTAL) {  //slope = rows/columns 
						est2 = column;
						est1 = (slope * column) + intercept;
						if (row <= est1) {
							side = RIGHT;
						}
						else if (row > est1) {
							side = LEFT;
						}
					}
					sideLength2 = aLineFitter.squareDistPointToPoint(row, column, est1, est2);  //square of the length
					dist2 = sideLength2 / (1 + (slope*slope));  //distance from (row, column) to the estimated straight line
					if (count > 1) {
//						if (Math.abs(dist2 - previousDist2) < 0.0001) {
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
					if (side != previousSide && trend == DECREASING && (previousTrend == DECREASING || count == 2)) {
						trend = INCREASING;
					}
					if (i == 1 && firstPixel.getRow() == 483 && firstPixel.getColumn() == 7) {
				  	//System.out.println(count+"th pixel ("+row+", "+column+"): Side: "+side+", trend: "+trend+" dist2: "+dist2+" pdist2: "+previousDist2);	
					}
/*					if (i == 1 && firstPixel.getRow() == 478 && firstPixel.getColumn() == 602) {
				  	//System.out.println(count+"th pixel ("+row+", "+column+"): Side: "+side+", trend: "+trend+" dist2: "+dist2+" pdist2: "+previousDist2);	
					}
*/
					if (count > 2) {
						if ((side == previousSide && previousTrend != NOCHANGE && trend == NOCHANGE) || (side != previousSide && trend == NOCHANGE)) {
							anotherRegion.addPrimitiveToRegion(new Primitive(aChain, i));
							aPoint = (Point)aChain.get(count-2);
							aChain = breakChain(aChain, count-3); //index element stays in the old chain
//System.out.println("Case 1: Trend change: "+previousTrend+" to "+trend+" Side change: "+previousSide+" to "+side+". New chain begin point: ("+(int)aPoint.getX()+", "+(int)aPoint.getY()+")");
							lItr = aChain.listIterator(2);
							count = 2;
							//System.out.println("Continue with the new chain.");
						}
						else if ((side == previousSide && trend != previousTrend && trend != NOCHANGE) || ((side != previousSide) && (previousTrend == NOCHANGE || previousTrend == INCREASING))) {
							anotherRegion.addPrimitiveToRegion(new Primitive(aChain, i));
							aPoint = (Point)aChain.get(count-1);
							aChain = breakChain(aChain, count-2); //index element stays in the old chain
//System.out.println("Case 2: Trend change: "+previousTrend+" to "+trend+" Side change: "+previousSide+" to "+side+". New chain begin point: ("+(int)aPoint.getX()+", "+(int)aPoint.getY()+")");
							lItr = aChain.listIterator(1);
							count = 1;
							//System.out.println("Continue with the new chain.");
						}
					}	
					previousSide = side;
					previousDist2 = dist2;
					previousTrend = trend;
//					//System.out.println("Next point.");
				}
				//System.out.println("One chain is done.");
				anotherRegion.addPrimitiveToRegion(new Primitive(aChain, i));
			}	//end of while (itr.hasNext()) 
			//System.out.println("Looked at all chains. New region has "+anotherRegion.getNumChains()+" chains. Old one had "+aRegion.getNumChains()+" chains.");
			allRegions[i] = anotherRegion;
		} //end of for (int i = 1; i < noOfLabels; i++)
		//System.out.println("End of splitting.");
	}


  /**
   * @param  
   * 
	 * This method
	 * Are all the points in aChain also in another primitive, already?
	 */	
	private boolean isCovered(LinkedList aChain, int beginIndex, int endIndex, int tag, int label) {
		PointPixel aPoint;
		LinkedList entryList;
		PixelData entry;
		int tag2;
		int labelNo;
		boolean covered = false;
		int index = beginIndex;
		while (index <= endIndex) {
			aPoint = new PointPixel(aChain.get(index));
			covered = false;
			entryList = (LinkedList)pixelData.getPixelData(aPoint.getRow(), aPoint.getColumn());
			ListIterator entrylItr = entryList.listIterator(0);
			while (entrylItr.hasNext()) {
				entry = (PixelData)entrylItr.next();
				tag2 = entry.getTag();
				labelNo = entry.getLabel();
				if (tag2 != tag && labelNo == label) {
					covered = true;
					break;
				}
			}
			index++;
			if (covered == false) {
				return false;
			}
		}
		return true;
	}
	
  /**
	 * @param int row, int column 
   *
	 * This method processes and categorizes one pixel.
	 */
	private int pixelType(int row, int column) {
		int tag = UNTAGGED; 
		int[] neigborArray = getRegionNeigbors(row, column);
		int numNeigbors = neigborArray[0];
		switch (numNeigbors) {
			case 0:   //isolated pixel
				tag = ISOLATED;
			case 1:   //endpoint pixel
				tag = ENDPOINT;
			case 2:   //interior or corner pixel
				if (Math.abs(neigborArray[1] - neigborArray[2]) == 3 ||
						Math.abs(neigborArray[1] - neigborArray[2]) == 4 ||
						Math.abs(neigborArray[1] - neigborArray[2]) == 5) {
					tag = INTERIOR;
				}
				else {
					tag = CORNER;
				}
			case 3:   //junction pixel
			case 4:
			case 5:
			case 6:
				tag = JUNCTION;
 		}
		return tag;
	}


  /**
   * @param none
   * 
   * This method finds the bounding box of all the objects 
   * Returns an array of two points -the left upper corner and the right lower corner
   */
	private Point[] findBoundingBoxes() {
//Put this in a separate class: BoundingBoxFinder
//		//System.out.println("In findBoundingBoxes method.");
//		labelsArray = new int[noOfLabels]; this is a private variable
//		int noOfLabels = getBlobCount();
//		//System.out.println("Got image labels in findBoundingBoxes method. There are "+noOfLabels+" objects.");
		int rows = imageHeight;
		int columns = imageWidth;	
		int[] minRow = new int[noOfLabels];
		int[] minColumn = new int[noOfLabels];
		int[] maxRow = new int[noOfLabels];
		int[] maxColumn = new int[noOfLabels];
		int[] count = new int[noOfLabels];
		Point[] boundingBoxCorners = new Point[noOfLabels*2];
		int labelNo;

		for (int i=0; i < noOfLabels; i++) {
			count[i] = 0;
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				labelNo = inPixImage[i][j];
				if (count[labelNo] == 0) { //this is the first point encountered
					minRow[labelNo] = i;
					minColumn[labelNo] = j;
					maxRow[labelNo] = i;
					maxColumn[labelNo] = j;
				}
				if (i > maxRow[labelNo]) maxRow[labelNo] = i;
				if (j > maxColumn[labelNo]) maxColumn[labelNo] = j;
				if (j < minColumn[labelNo]) minColumn[labelNo] = j;
				count[labelNo]++;
			}
		}
//		System.out.println("Found min/max row/columns.");

		Point upperRight;
		Point lowerLeft;
		for (int i = 0; i < noOfLabels*2; i+=2) {
			upperRight = new Point(minRow[(int)(i/2)], minColumn[(int)(i/2)]);
			lowerLeft = new Point(maxRow[(int)(i/2)], maxColumn[(int)(i/2)]);
			boundingBoxCorners[i] = upperRight;
			boundingBoxCorners[i+1] = lowerLeft;
		}
		//System.out.println("Found and recorded bounding boxes.");
		return boundingBoxCorners;
	}



  /**
   * @param int labelNo, int tag, int tag2 
   * 
	 *
   */
	private boolean connectEndpoint(int labelNo, int tag, PointPixel aPoint, int threshold) {
		//System.out.println("In connectEndpoint for "+tag+" and "+aPoint+" of region "+labelNo);
		double maxDist = 0;
		double distance;
		int row, column;
		int mergetype = -1;
		boolean mergeable = false;
		PointPixel oldPoint;
		LineFitter aLineFitter = new LineFitter();
		Primitive firstPrim = allRegions[labelNo].getPrimitive(tag);
		LinkedList newChain = firstPrim.getAllPoints();
		PointPixel beginPixel = new PointPixel(newChain.getFirst());
		PointPixel endPixel = new PointPixel(newChain.getLast());
		if (aPoint.neigbors8(beginPixel) || aPoint.equals(beginPixel)) { //Case end begin-begin end
		//Traverse second chain in forward direction
		//Add new points to the beginning of first chain
			endPixel = new PointPixel(newChain.getFirst());
			beginPixel = new PointPixel(newChain.getLast());
			mergetype = Primitive.BEGINBEGIN;
		}
		else if (aPoint.neigbors8(endPixel) || aPoint.equals(endPixel)) { //Case begin end-begin end 
		//Traverse second chain in forward direction
		//Add new points to the end of first chain
			mergetype = Primitive.ENDBEGIN;
		}
		//System.out.println("Merge type is "+mergetype);
		if (mergetype == Primitive.BEGINBEGIN || mergetype == Primitive.ENDBEGIN) {
			maxDist = 0;
			ListIterator lItr = newChain.listIterator(0); //the first point is beginPixel
			while (lItr.hasNext() && maxDist < threshold) { //Calculate the distance of each pixel to the line between beginPixel and aPoint
				oldPoint = new PointPixel(lItr.next());
				row = oldPoint.getRow();
				column = oldPoint.getColumn();
				distance = aLineFitter.squareDistPointToLine(row, column, beginPixel, aPoint);
//				//System.out.println("Distance from point ("+row+", "+column+") to line from "+beginPixel+" to " +aPoint+" is "+distance+". Threshold is "+threshold);
				if (distance > maxDist) {
					maxDist = distance;
				}
			}			
		}
		//System.out.println("Max distance is "+maxDist);
		if (maxDist < threshold) { //threshold is an argument of this function
			//System.out.print("Mergeable, merging. ");
		//The first chain can be completely extended by the second chain
			mergeable = true;
			pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.STARTPOINT, PixelData.INTERIORPOINT);
			pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.ENDPOINT, PixelData.INTERIORPOINT);
			firstPrim.addEndPoint(aPoint);
			pixelData.put(aPoint.getRow(), aPoint.getColumn(), labelNo, tag, PixelData.INTERIORPOINT); 
			pixelData.updatePixelPosition(firstPrim.getBeginPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.STARTPOINT);
			pixelData.updatePixelPosition(firstPrim.getEndPoint(), labelNo, tag, PixelData.INTERIORPOINT, PixelData.ENDPOINT);
			//System.out.println("pixelData is updated.");
		}
		return mergeable;
	}




  /**
   * @param none (LinkedList aChain, LinkedList anotherChain)
   * 
   * This method links two border pixel chains into one; aChain is changed. 
   */
	private LinkedList mergeTwoChains(LinkedList aChain, LinkedList anotherChain) {
		if ((new PointPixel(aChain.getFirst())).neigbors8((Point)anotherChain.getLast())) { 
			if (anotherChain.size() == 1) {
				aChain.addFirst((Point)anotherChain.getFirst());
			}
			ListIterator lItr = anotherChain.listIterator(anotherChain.size());
	 		while (lItr.hasPrevious())
				aChain.addFirst((Point)lItr.previous());
		}
		else if ((new PointPixel(aChain.getFirst())).neigbors8((Point)anotherChain.getFirst())) {
			ListIterator lItr = anotherChain.listIterator(0);
	 		while (lItr.hasNext())
				aChain.addFirst((Point)lItr.next());
		}
		else if ((new PointPixel(aChain.getLast())).neigbors8((Point)anotherChain.getFirst())) {
			ListIterator lItr = anotherChain.listIterator(0);
   		while (lItr.hasNext())
				aChain.add((Point)lItr.next());
		}
		else if ((new PointPixel(aChain.getLast())).neigbors8((Point)anotherChain.getLast())) {
			if (anotherChain.size() == 1) {
				aChain.add((Point)anotherChain.getFirst());
			}
			ListIterator lItr = anotherChain.listIterator(anotherChain.size());
  		while (lItr.hasPrevious())
				aChain.add((Point)lItr.previous());
		}
		return aChain;
	}

  /**
   * @param LinkedList aChain, int index
   * 
   * This method breaks a chain into two chains.
	 * The first chain contains the elements upto and including the index
	 * the second chain contains all the elements after index
	 * Changes the input aChain into the first chain, and outputs the second chain.
   */
	private LinkedList breakChain(LinkedList aChain, int index) {
		LinkedList newChain = new LinkedList();
		int size = aChain.size();
		for (int i = size-1; i > index; i--) { 
			Point lastPoint = (Point)aChain.getLast();
			newChain.addFirst(lastPoint);
			aChain.removeLast();
		}
		return newChain;
	}

  /**
	 * @param none
	 *
	 * This method defines the structuring elements used for border thinning.
	 */
	private Vector defineModels() {
		Vector models = new Vector(12);
		int[] aModel1 = {0, 0, 0, 0, 1, 1, -1, 1, 1};
		models.add(aModel1);
		int[] aModel2 = {1, 1, -1, 1, 0, 0, 0, 0, 1};
		models.add(aModel2);
		int[] aModel3 = {0, 0, 0, 1, -1, 1, 1, 0, 1};
		models.add(aModel3);
		int[] aModel4 = {-1, 1, 1, 0, 0, 0, 0, 1, 1};
		models.add(aModel4);
		int[] aModel5 = {0, 0, 1, 1, -1, 1, 0, 0, 1};
		models.add(aModel5);
		int[] aModel6 = {-1, 1, 0, 0, 0, 0, 1, 1, 1};
		models.add(aModel6);
		int[] aModel7 = {1, 0, 0, 0, 0, 1, -1, 1, 1};
		models.add(aModel7);
		int[] aModel8 = {0, 1, -1, 1, 1, 0, 0, 0, 1};
		models.add(aModel8);
		return models;
	}
																																																		
  /**
	 * @param int[][] inImage, int imageRows, int imageColumns, Vector models
	 *
	 * This method thins the borders according to the structuring element given.
	 * Changes the private variable borderedImage and returns a linked list
	 * of pixels that were eroded.
	 */
//models should be a vector of length n (no. of the models to be used for erosion)
//each element should have one array having 9 members, the middle pixel and the 8 neigbors.
//Array has 0's, 1's, and -1's. 1's should be having the same label all and all 0's should have a label different than that one, the label of -1's is not important, they can have any label.
	private void thinBorders() {
		Vector models = defineModels();
		int imageRows = imageHeight;
		int imageColumns = imageWidth;
		int label_check = 0;
		int label, pixelLabel;
		int match;
		int[] modelArray;
		int[] rowPos = {-1, -1, -1, 0, 1, 1, 1, 0, 0};
		int[] colPos = {1, 0, -1, -1, -1, 0, 1, 1, 0};
		int[][] borderImage = new int[imageRows+2][imageColumns+2];
		//System.out.println("In thinBorders.");
		// insert one row of background pixels at the bottom and one on top
		for (int i = 0; i<imageColumns+2; i++) {
			borderImage[0][i] = bPixLabel;
			borderImage[imageRows+2-1][i] = bPixLabel;
		}
		for (int i = 0; i<imageRows+2; i++) {
			borderImage[i][0] = bPixLabel;
			borderImage[i][imageColumns+2-1] = bPixLabel;
		}
		//System.out.println("Copying...");
		for (int i = 1; i<imageRows+2-1; i++) {
			for (int j = 1; j<imageColumns+2-1; j++) {
				borderImage[i][j] = borderedImage[i-1][j-1];
			}
		}
//System.out.println("Inserted background pixels on top and bottom for border thinning");
		for(int r = 1; r < imageRows+2-1; r++ ) {
			for(int c = 1; c < imageColumns+2-1; c++ ) {
				pixelLabel = borderImage[r][c];
				if (pixelLabel != bPixLabel) {
					for (int k = 0; k < models.size(); k++) {
						modelArray = (int [])models.elementAt(k);
						match = 1;
				/* 	if (r==1 && c==1) {
							//System.out.println("Model Array is: ");
							for (int i = 0; i < 9; i++) {
								//System.out.print(modelArray[i] + " ");
							}
						}
				*/
						for (int i = 0; i < 9; i++) {
							if (modelArray[i] == 1) {
								label_check = borderImage[r+rowPos[i]][c+colPos[i]];
								break;
							}
						}
						for (int i = 0; i < 9; i++) {
							label = borderImage[r+rowPos[i]][c+colPos[i]];
							if (modelArray[i] == 1 && label != label_check)
								match = 0;
							if (modelArray[i] == 0 && label == label_check)
								match = 0;
						}
						if (match == 1) { //this pixel passed the test
							borderImage[r][c] = bPixLabel;
							borderedImage[r-1][c-1] = bPixLabel;
						//System.out.println("Removing: "+(r-1)+", "+(c-1));
							break;
						}
					}  //end of for loop for the arrays in the models Vector
				}
			}
		}
	}

}

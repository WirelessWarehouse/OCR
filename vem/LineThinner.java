import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to recognize thick lines and thin them.
 * <p> 
 * For each region, decides whether it is a thick line 
 * by checking the row and column counts of its border pixels.
 * If it is a thick line, then goes through its border pixels, picks one pixel 
 * in the x or y direction, erases the others without losing connectivity, thus making it a thin line.
 * Before thinning borders, needs to recognize borders of a thick line segment.
 *
 * @author Chart Reading project
 * @version 1.0
 *
 */

public class LineThinner {
	final int[] rPos = {-1, -1, -1, 0, 1, 1, 1, 0};  //already declared in ImagePrimitives.jave
	final int[] cPos = {1, 0, -1, -1, -1, 0, 1, 1};  //already declared in ImagePrimitives.jave

	final static public int DOWN = 0;
	final static public int UP = 1;
	final static public int RIGHT = 2;
	final static public int LEFT = 3;

	private int imageHeight;    //number of rows in the input image
	private int imageWidth;    	//number of columns in the input image
 	private int bPixLabel;			//background label 
	private int noOfLabels;

	private int[][] labelledImage;  //Labelled image
	private int[][] borderedImage;  //Bordered image
	private int[][] processedImage;
	private Region[] allRegions;

	/**
	 * Constructor
	 *
   * @param allR The Region array of all the regions in the image
	 * @param inImage The label image
	 * @param borImage The border image
	 * @param labelCount The number of regions in the image
	 * @param imageRows The number of rows
	 * @param imageColumns The number of columns
	 * @param bGroundLabel The label of the background pixels
	 */
	public LineThinner(Region[] allR, int[][] inImage, int[][] borImage, int labelCount, int imageRows, int imageColumns, int bGroundLabel) {
		labelledImage = inImage;
		borderedImage = borImage;
		allRegions = allR;
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPixLabel = bGroundLabel;
		noOfLabels = labelCount;
		processedImage = new int[imageRows][imageColumns];
	}

  /**
	 * Calculates the number of up/down/right/left pixels for
	 * each non-background pixel. 
	 * <p>
	 * calculatePixelCounts counts the up/down/right/left pixels for
	 * the chain beginning with the given non-background pixel.
	 * The counts are stored in the pointpixel itself.
	 *
   * @param none
   */
	public void getCounts() {
		//System.out.println("In detectLines of LineThinner.");
		//System.out.println("Image size: "+imageHeight+"x"+imageWidth);
		int label_c; //this pixels label
		int label_n; //one of the neighbours label
		boolean border_pixel = false;
	 	int[][] inImage = new int[imageHeight+2][imageWidth+2];  	
		int[][] borderImage = new int[imageHeight+2][imageWidth+2];

		//insert one row of background pixels at the bottom and one on top 
		for (int i = 0; i<imageWidth+2; i++) {
			inImage[0][i] = bPixLabel;
			inImage[imageHeight+2-1][i] = bPixLabel;
			borderImage[0][i] = bPixLabel;
			borderImage[imageHeight+2-1][i] = bPixLabel;
		}
		for (int i = 0; i<imageHeight+2; i++) {
			inImage[i][0] = bPixLabel;
			inImage[i][imageWidth+2-1] = bPixLabel;
			borderImage[i][0] = bPixLabel;
			borderImage[i][imageWidth+2-1] = bPixLabel;
		}
		//System.out.println("Copying...");
		for (int i = 1; i<imageHeight+2-1; i++) {
			for (int j = 1; j<imageWidth+2-1; j++) {
				inImage[i][j] = labelledImage[i-1][j-1];
				borderImage[i][j] = borderedImage[i-1][j-1];
				processedImage[i-1][j-1] = bPixLabel; //initialize to background 
			}
		}
		//System.out.println("Inserted background pixels on top and bottom.");

		Region aRegion;
		for (int i = 0; i < noOfLabels; i++) {
			aRegion = allRegions[i];
			aRegion.clearPixelList();
		}

		for(int r = 1; r < imageHeight+2-1; r++ ) {
			for( int c = 1; c < imageWidth+2-1; c++ ) {
				label_c = borderImage[r][c];
				//if (label_c != bPixLabel && !(allRegions[label_c].getIsCharacter())) {
				if (label_c != bPixLabel) {
					//Start from this pixel in this region. Follow the border.
					//Each time there is a new pixel, calculate the number of pixels in the row and column direction.
					//Each border pixel will have a row count and a column count
					calculatePixelCounts(inImage, borderImage, r, c);
				}
			}
		}

	}


  /**
	 * Identifies the regions that are thick lines and thins them.
	 * The regions that have border pixels that either have a count of
	 * one at a direction or have larger counts in all directions but 
	 * appear isolated are thin regions. The others are thick regions
	 * or filled areas. The thick lines need to be thinned, not
	 * the filled regions.
	 * <p>
	 * If the number of pixels that have rowCount>1 and columnCount>1 is 
	 * more than 0.3*the total number of pixels and 
	 * all these pixels follow one another, 
	 * then the region is not thin; it is either a thick line or a filled area.
	 * <p>
	 * Once the region is determined to be a filled region, the following apply
	 * to determine if it is a thick line or not.
	 * If the column range (difference between the maximum and the minimum number 
	 * of pixels in the column direction) and the row range are both zero, the
	 * region is a filled region (a rectangle); not a thick line.
	 * <p>
	 * Else, if column range or row range is smaller than 8 and the average row 
	 * or column count is smaller than 8, then it is a thick line.
	 * <p>
	 * Else, it is a filled region; not a thick line.
	 * <p>		
	 * If the region is found to be a thick line region, makeThinLine is called
	 * to change it into a thin line.
	 * 
   * @param none 
	 */	
	public void getThinLines() {
		int regionCount, row, column, rowCount, columnCount;		
		int count, countPixels, highCount, filledArea;
		Region aRegion;
		PointPixel aPixel, upperLeft, lowerRight;
		PointPixel prevPixel = new PointPixel();
		LinkedList aList;
		ListIterator lItr;
		int boxHeight, boxWidth, area, mass;
		int totalRowCount, totalColumnCount, maxRowCount, maxColumnCount, minRowCount, minColumnCount;
		for(int i = 1;  i < noOfLabels; i++ ) {
			aRegion = allRegions[i];
			upperLeft = aRegion.getUpperLeft();
			lowerRight = aRegion.getLowerRight();
			boxHeight = lowerRight.getRow() - upperLeft.getRow() + 1;
			boxWidth = lowerRight.getColumn() - upperLeft.getColumn() + 1;
			area = boxHeight*boxWidth;
			mass = findPixelCount(i, upperLeft, lowerRight);
			regionCount = aRegion.getNumPixels(); //number of border pixels in this region
			//System.out.println("Region "+i+" has "+regionCount+ " border pixels. box area="+area+ ", mass="+mass);
			count = 0;
			countPixels = 0;
			highCount = 0;
			filledArea = 0;
			totalRowCount = 0;
			totalColumnCount = 0;
			minRowCount = imageWidth;
			minColumnCount = imageHeight;
			maxRowCount = 0;
			maxColumnCount = 0;
			aList = aRegion.getPixelList();
			lItr = aList.listIterator(0);
			while(lItr.hasNext()) {
				countPixels++;
				aPixel = (PointPixel)lItr.next();
				row = aPixel.getRow(); 
				column = aPixel.getColumn();
				rowCount = aPixel.getRowCount();
				columnCount = aPixel.getColumnCount();
				totalRowCount += rowCount;
				totalColumnCount += columnCount;
				if (rowCount < minRowCount)
					minRowCount = rowCount;
				if (columnCount < minColumnCount)
					minColumnCount = columnCount;
				if (rowCount > maxRowCount)
					maxRowCount = rowCount;
				if (columnCount > maxColumnCount)
					maxColumnCount = columnCount;
				if (rowCount > 4 && columnCount > 4) {
					highCount++;
				}
				if (rowCount > 1 && columnCount > 1) {
					count++;
					if (countPixels > 1 && (double)count > (double)regionCount*0.3 && (aPixel.neigbors8(prevPixel))) { //not isolated
						filledArea = 1;
						//System.out.println("Region "+i+" is not thin");
					}
				}
				else {
					count = 0;
				}
				prevPixel = aPixel;
				//System.out.println("(" + row + ", "+column+"): rowCount="+rowCount+", colCount="+columnCount);
			}
			if (filledArea == 1) {
				double averageRowCount = (double)totalRowCount/(double)countPixels;
				double averageColumnCount = (double)totalColumnCount/(double)countPixels;
				int rangeRow = maxRowCount - minRowCount;
				int rangeColumn = maxColumnCount - minColumnCount;
				//System.out.println("Region "+i+": averageRowCount="+averageRowCount+", averageColumnCount="+averageColumnCount+", rangeRow="+rangeRow+", rangeColumn="+rangeColumn);
				if (rangeColumn == 0 && rangeRow == 0) {//occurs in the cases of filled rectangles and squares
					//System.out.println("Region "+i+" is a filled area.");
					aRegion.setIsFilledArea(true);
				}
				else if ((rangeColumn < 8 || rangeRow < 8) && (averageRowCount < 8 || averageColumnCount < 8)) { //and check if the smaller one is close (?) to the average computed above
					//System.out.println("Region "+i+" is a thick line.");
					aRegion.setIsThickLine(true);
					makeThinLine(i);
				}
				else {
					//System.out.println("Region "+i+" is a filled area.");
					aRegion.setIsFilledArea(true);
				}
			}
			/************
			//If the thickness in row or the column axis is never greater than a threshold, then it is a thick line, not a region.
			//if ((double)highCount < (double)regionCount*0.3 && filledArea == 1) {
			if ((double)mass < (double)(area*0.3) && filledArea == 1) {
				//System.out.println("Region "+i+" is a thick line (mass="+mass+" < area*0.3="+((double)(area*0.3))+" (highCount="+highCount+" < regionCount*0.3="+((double)regionCount*0.3)+")");
				//Region 73 in lineNJ is a thick line.
				aRegion.setIsThickLine(true);
				//makeThinLine(i);
			}
			//else if ((double)highCount >= (double)regionCount*0.3 && filledArea == 1) {
			else if ((double)mass >= (double)(area*0.3) && filledArea == 1) {
				//System.out.println("Region "+i+" is a filled area (mass="+mass+" >= area*0.3="+((double)(area*0.3))+" (highCount="+highCount+" < regionCount*0.3="+((double)regionCount*0.3)+")");
				aRegion.setIsFilledArea(true);
			}
			*************/
		}
	}


  /**
	 * Counts the number of up/down/right/left pixels for each pixel on 
	 * the chain beginning with the given non-background pixel.
	 * The counts are stored in the pointpixel itself.
	 *
   * @param labelImage The labelled image
	 * @param borderImage The boredered image
	 * @param row The row number of the beginning pixel 
	 * @param column The column number of the beginning pixel
	 */	
	private	void calculatePixelCounts(int[][] labelImage, int[][] borderImage, int row, int column) {
		//System.out.println("Calculate pixel counts beginning with ("+(row-1)+", "+(column-1)+")");
		int labelNo = borderImage[row][column];
		int firstRow = row;
		int firstColumn = column;
		int previousRow = row;
		int previousColumn = column - 1;
		int position = 3;
		int previousPosition = 3;
		int nextRow = row;
		int nextColumn = column;
		int newRow = row;
		int newColumn = column;
		int checkRow = row;
		int checkColumn = column;
		int foundNext = 1;
		int onFirstPixel = 1;
		int onSecondPixel = 0;
		int noOfPixels = 1;
		int rowC = 0;
		int columnC = 0;
		int remainder, rowDifference, columnDifference;
		PointPixel aPixel;
		Region aRegion = allRegions[labelNo];

		rowC = countRowPixelsRight(firstRow-1, firstColumn-1, labelNo) + countRowPixelsLeft(firstRow-1, firstColumn-1, labelNo) + 1;
		columnC = countColumnPixelsDown(firstRow-1, firstColumn-1, labelNo) + countColumnPixelsUp(firstRow-1, firstColumn-1, labelNo) + 1;
		aPixel = new PointPixel(firstRow-1, firstColumn-1);
		aPixel.setRowCount(rowC);
		aPixel.setColumnCount(columnC);
		aRegion.addPixelToRegion(aPixel);
		borderImage[firstRow][firstColumn] = bPixLabel; 

		while (foundNext == 1 && onSecondPixel < 2) {
			rowDifference = previousRow - row;
			columnDifference = previousColumn - column;
			previousPosition = position;
			position = findRelativeLocation(rowDifference, columnDifference);
			//what does (r,c) see (pr, pc) as? Position is the answer to that question. In the first iteration, position will return 3. Previous position is set to 3, so deviation = 0, so the same chain is continuing.
			foundNext = 0;
			for (int i = 7; i > 0; i--) {
				//if i=0, the previous pixel is also checked, then the border can turn on itself				
				remainder = (position+i) - (((position+i)/8)*8); //position+i mod 8
				newRow = row + rPos[remainder];
				newColumn = column + cPos[remainder];
				if (borderImage[newRow][newColumn] == labelNo && foundNext == 0) {
					nextRow = newRow;
					nextColumn = newColumn;
					foundNext = 1;
					noOfPixels++;
					break;
				}
			}//end of for (int i = 7; i >= 0; i--) 
			 if (foundNext == 1) {
				if (nextRow == firstRow && nextColumn == firstColumn) {
					//System.out.println("Added the first pixel");
					onFirstPixel++;
				}
				if (noOfPixels == 2) {
					//System.out.println("Added the second pixel ("+nextRow+", "+nextColumn+") ");
					checkRow = nextRow;
					checkColumn = nextColumn;
					onSecondPixel = 1;
				}
				if (noOfPixels > 2 && nextRow == checkRow && nextColumn == checkColumn && row == firstRow && column == firstColumn) {
					//System.out.println("Found the second pixel ("+nextRow+", "+nextColumn+") after the first one again. Chain should stop here, the last found pixel is not added.");
					onSecondPixel++;
				}
				else {
					rowC = countRowPixelsRight(nextRow-1, nextColumn-1, labelNo) + countRowPixelsLeft(nextRow-1, nextColumn-1, labelNo) + 1;
					columnC = countColumnPixelsDown(nextRow-1, nextColumn-1, labelNo) + countColumnPixelsUp(nextRow-1, nextColumn-1, labelNo) + 1;
					//System.out.println("("+(nextRow-1)+", "+(nextColumn-1)+") : rowCount="+rowC+", columnCount="+columnC);
					aPixel = new PointPixel(nextRow-1, nextColumn-1);
					aPixel.setRowCount(rowC);
					aPixel.setColumnCount(columnC);
					aRegion.addPixelToRegion(aPixel);
					borderImage[nextRow][nextColumn] = bPixLabel;
					previousRow = row;
					previousColumn = column;
					row = nextRow;
					column = nextColumn;
				}
			} //end of if (foundNext == 1) 
		} //end of while(foundNext == 1 && onSecondPixel < 2) 
	}


  /**
   * Thins the thick line region whose label is given as an argument.
	 * The 2d array processedImage is that region, but thinned.
	 * Begins from the first pixel of the border pixels list of the region.
	 * First tracks the line, in the direction right/down.
	 * When that is finished, a second tracking is done in the direction left/down.
	 * The beginning pixel is the first pixel, so it has the lowest row number, 
	 * tracking in the up direction will not be useful, there are no pixels that
	 * have lower row numbers than the beginning pixel.
	 *
   * @param label The label of the region that is to be thinned
	 */	
	private	void makeThinLine(int label) {
		//System.out.println("Making thin line for region "+label);
		Region aRegion = allRegions[label];
		int regionCount = aRegion.getNumPixels(); //number of border pixels in this region
		//System.out.println("Region "+label+" has "+regionCount+ " border pixels.");
		LinkedList aList = aRegion.getPixelList();
		PointPixel aPixel = (PointPixel)aList.getFirst();
		int row = aPixel.getRow(); 
		int column = aPixel.getColumn();
		trackThinLine(processedImage, row, column, label, RIGHT, DOWN);
		trackThinLine(processedImage, row, column, label, LEFT, DOWN);
	}


  /**
	 * Tracks the line beginning with the pixel given as a parameter and
	 * in the direction that is also given as a parameter.
	 * 
	 * If trend is RIGHT and DOWN:
	 * Go right, choose the middle point. Go down one row.
	 * Choose the middle point in that row.
	 * If you cannot go down, go right until you can go down one.
	 * Choose the middle point in that row.
	 * If you cannot go down and when you go right you cannot go down either,
	 * go up until you can go right.
   *
	 * If trend is RIGHT and UP:
	 * If you cannot go up, go right until you can go up one.
	 * If you cannot go up and when you go right you cannot go up still,
	 * go down until you can go right.
	 *
   * @param bImage The image that is being changed as the line is thinned
	 * @param r The row number of the beginning pixel
	 * @param c The column number of the beginning pixel
	 * @param label The label of the region that is to be thinned
	 * @param trendR The row trend (horizontal trend, right or left)
	 * @param trendC The column trend (vertical trend, down or up)
	 */	
	private	void trackThinLine(int[][] bImage, int r, int c, int label, int trendR, int trendC) {
		int row = r;
		int column = c;
		int previousRow = r;
		int previousColumn = c;
		PointPixel currentPixel = new PointPixel(row, column);
		PointPixel previousPixel = currentPixel;
		//System.out.println("\nTracking thin line beginning with ("+row+", "+column+").");
		int firstRow = row;
		int firstColumn = column;
		int	lastRow = row;
		int lastColumn = column;
		int row1, column1; 
		int rowCountRight = countRowPixelsRight(row, column, label); //does not count itself
		int	rowCountLeft = countRowPixelsLeft(row, column, label);
		int columnCountDown = countColumnPixelsDown(row, column, label);
		int	columnCountUp = countColumnPixelsUp(row, column, label);
		boolean nextStep = false;
		int trendColumn = trendC;
		int trendRow = trendR;
		int	lastTrendRow = trendRow;
		int lastTrendColumn = trendColumn;
		int	newTrendRow = trendRow;
		int newTrendColumn = trendColumn;
		int trendChange1 = 0;
		int trendChange2 = 0;
		int trend = Primitive.VERTICAL;
		int trendPrevious = trend;
		if (trendRow == RIGHT && rowCountLeft != 0) {
			nextStep = true;
		}
		if (trendColumn == DOWN && columnCountUp != 0) {
			nextStep = true;
		}
		int leftMostColumn = column;
		int middleColumn = column;
		int upperMostRow = row;
		int middleRow = row;
		int goingBack = 0;
		int count = 1;
		int countAddedPixels = 0;
		//bImage[row][column] = 0;
		//System.out.println("("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);

		//while (count != 0 && goingBack == 0 && ((columnCountDown + columnCountUp) != 0 || (rowCountRight + rowCountLeft) != 0)) {

		while (goingBack < 3) {
			count = 0;
			rowCountRight = countRowPixelsRight(row, column, label); //does not count itself
			rowCountLeft = countRowPixelsLeft(row, column, label);
			columnCountDown = countColumnPixelsDown(row, column, label);
			columnCountUp = countColumnPixelsUp(row, column, label);

			trendPrevious = trend;

			if ((columnCountDown+columnCountUp) >= (rowCountLeft+rowCountRight)) { 
				//Pick the middle point in this row and move down
				//System.out.println("Vertical ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
				leftMostColumn = column - rowCountLeft;
				middleColumn = leftMostColumn + (rowCountRight+rowCountLeft+1)/2;
				middleRow = row;
				trend = Primitive.VERTICAL;
			}
			else {
				//Pick the middle point in this column and move right
				//System.out.println("Horizontal ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
				upperMostRow = row - columnCountUp;
				middleRow = upperMostRow + (columnCountUp+columnCountDown+1)/2;
				middleColumn = column;
				trend = Primitive.HORIZONTAL;
			}

			/****
			for (int i = column + 1; i < middleColumn; i++) {
				bImage[row][i] = 0;
				count++;
			}
			for (int i = column - 1; i > middleColumn; i--) {
				bImage[row][i] = 0;
				count++;
			}
			****/

			if (trend == trendPrevious) {
				row = middleRow;
				column = middleColumn;
				//if (bImage[row][column] == 0 && !(row == firstRow && column == firstColumn)) {
				if (bImage[row][column] == label && !(row == firstRow && column == firstColumn)) {
					goingBack++;
					//System.out.println("Going back on itself");
					if (goingBack == 1) {
						lastRow = row;
						lastColumn = column;
						lastTrendRow = trendRow;
						lastTrendColumn = trendColumn;
					}
				}
				else {
					goingBack = 0;
				}
				//if (row, column) is not neigboring (previousRow, previousColumn)
				//connect them in a straight line.
				currentPixel = new PointPixel(row, column);
				if (countAddedPixels > 0 && !(currentPixel.neigbors8(previousPixel))) {
					connectPixels(previousPixel, currentPixel, bImage, label);
				}
				if (countAddedPixels > 1 && trendRow == RIGHT && column < previousPixel.getColumn()) {
					trendRow = LEFT;
					//System.out.println("Trend change from RIGHT to LEFT (1)");
				}
				else if (countAddedPixels > 1 && trendRow == LEFT && column > previousPixel.getColumn()) {
					trendRow = RIGHT;
					//System.out.println("Trend change from LEFT to RIGHT (1)");
				}
				if (countAddedPixels > 1 && trendColumn == DOWN && row < previousPixel.getRow()) {
					trendColumn = UP;
					//System.out.println("Trend change from DOWN to UP (1)");
				}
				else if (countAddedPixels > 1 && trendColumn == UP && row > previousPixel.getRow()) {
					trendColumn = DOWN;
					//System.out.println("Trend change from UP to DOWN (1)");
				}
				//bImage[row][column] = 0;
				bImage[row][column] = label; //Pick this point as part of the thin line
				countAddedPixels++;
				count++;
				previousPixel = currentPixel; 
				//System.out.println("Added ("+row+", "+column+")");
			}

			rowCountRight = countRowPixelsRight(row, column, label); //does not count itself
			rowCountLeft = countRowPixelsLeft(row, column, label);
			columnCountDown = countColumnPixelsDown(row, column, label);
			columnCountUp = countColumnPixelsUp(row, column, label);
			//System.out.println("("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);

			/****
			if (trendColumn == DOWN && columnCountDown == 0) {
				bImage[row][column] = 0;
				count++;
			}
			else if (trendColumn == UP && columnCountUp == 0){
				bImage[row][column] = 0;
				count++;
			}
			****/

			if (trend == Primitive.VERTICAL) {

			if (trendColumn == DOWN) {
				while(columnCountDown == 0) { //cannot go down
					if (trendRow == RIGHT) { //Keep moving right 
						if (rowCountRight == 0) { //cannot go down, cannot go right
							trendColumn = UP;
							//System.out.println("Trend change from DOWN to UP");
							break;
						}
						else { //cannot go down, can go right
							column++;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label);
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//bImage[row][column] = 0;
							//System.out.println("Moved right to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendRow = RIGHT
					else if (trendRow == LEFT) { //Keep moving left
						if (rowCountLeft == 0) { //cannot go down, cannot go left
							trendColumn = UP;
							//System.out.println("Trend change from DOWN to UP");
							break;
						}
						else { //cannot go fown, can go left
							column--;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label); 
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//bImage[row][column] = 0;
							//System.out.println("Moved left to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendRow = LEFT
				} //end of while columnCountDown == 0

				if (columnCountDown != 0) { //can go down, go down one row
					row++;
					rowCountRight = countRowPixelsRight(row, column, label); 
					rowCountLeft = countRowPixelsLeft(row, column, label); 
					columnCountDown = countColumnPixelsDown(row, column, label);
					columnCountUp = countColumnPixelsUp(row, column, label);
					//bImage[row][column] = 0;
					//System.out.println("Moved down to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
				}
			} //end of if trendColumn == DOWN

			rowCountRight = countRowPixelsRight(row, column, label); //does not count itself
			rowCountLeft = countRowPixelsLeft(row, column, label);
			columnCountDown = countColumnPixelsDown(row, column, label);
			columnCountUp = countColumnPixelsUp(row, column, label);
			if (trendColumn == UP) {
				while(columnCountUp == 0) { //cannot go up, keep moving right
					if (trendRow == RIGHT) {
						if (rowCountRight == 0) { //cannot go up, cannot go right
							trendColumn = DOWN;
							//System.out.println("Trend change from UP to DOWN");
							break;
						}
						else { //cannot go up, can go right
							column++;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label);
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//bImage[row][column] = 0;
							//System.out.println("Moved right to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendRow = RIGHT
					else if (trendRow == LEFT) { //cannot go up
						if (rowCountLeft == 0) { //cannot go up, cannot go left
							trendColumn = DOWN;
							//System.out.println("Trend change from UP to DOWN");
							break;
						}
						else { //cannot go up, can go left
							column--;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label); 
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//bImage[row][column] = 0;
							//System.out.println("Moved left to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendRow = LEFT
				} //end of while columnCountUp == 0	
				if (columnCountUp!= 0) { //can go up, go up one row
					row--;
					rowCountRight = countRowPixelsRight(row, column, label); 
					rowCountLeft = countRowPixelsLeft(row, column, label); 
					columnCountDown = countColumnPixelsDown(row, column, label);
					columnCountUp = countColumnPixelsUp(row, column, label);
					//bImage[row][column] = 0;
					//System.out.println("Moved up to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
				}
			} //end of of trendColumn == UP	
			} //end of if (trend = Primitive.VERTICAL)

			else { //trend = Primitive.HORIZONTAL
			if (trendRow == RIGHT) {
				while(rowCountRight == 0) { //cannot go right 
					if (trendColumn == DOWN) { //Keep moving down 
						if (columnCountDown == 0) { //cannot go down, cannot go right
							trendColumn = UP;
							//System.out.println("Trend change from DOWN to UP");
							break;
						}
						else { //cannot go right, can go down 
							row++;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label);
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//System.out.println("Moved down to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendColumn = DOWN 
					else if (trendColumn == UP) { //Keep moving up 
						if (columnCountUp == 0) { //cannot go right, cannot go up 
							trendColumn = DOWN;
							//System.out.println("Trend change from UP to DOWN");
							break;
						}
						else { //cannot go right, can go up 
							row--;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label); 
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//System.out.println("Moved up to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendColumn = UP 
				} //end of while rowCountRight == 0

				if (rowCountRight != 0) { //can go right, go right one column
					column++;
					rowCountRight = countRowPixelsRight(row, column, label); 
					rowCountLeft = countRowPixelsLeft(row, column, label); 
					columnCountDown = countColumnPixelsDown(row, column, label);
					columnCountUp = countColumnPixelsUp(row, column, label);
					//System.out.println("Moved right to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
				}
			} //end of if trendRow == RIGHT 

			rowCountRight = countRowPixelsRight(row, column, label); //does not count itself
			rowCountLeft = countRowPixelsLeft(row, column, label);
			columnCountDown = countColumnPixelsDown(row, column, label);
			columnCountUp = countColumnPixelsUp(row, column, label);
			if (trendRow == LEFT) {
				while(rowCountLeft == 0) { //cannot go left, keep moving down 
					if (trendColumn == DOWN) {
						if (columnCountDown == 0) { //cannot go down, cannot go left 
							trendColumn = UP;
							//System.out.println("Trend change from DOWN to UP");
							break;
						}
						else { //cannot go left, can go down 
							row++;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label);
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//System.out.println("Moved down to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendColumn = DOWN 
					else if (trendColumn == UP) { //cannot go left
						if (columnCountUp == 0) { //cannot go left, cannot go up 
							trendColumn = DOWN;
							//System.out.println("Trend change from UP to DOWN");
							break;
						}
						else { //cannot go left, can go up  
							row--;	
							rowCountRight = countRowPixelsRight(row, column, label); 
							rowCountLeft = countRowPixelsLeft(row, column, label); 
							columnCountDown = countColumnPixelsDown(row, column, label);
							columnCountUp = countColumnPixelsUp(row, column, label);
							//System.out.println("Moved up to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
						}
					} //end of trendColumn = UP 
				} //end of while rowCountLeft == 0	
				if (rowCountLeft!= 0) { //can go left, go left one column 
					column--;
					rowCountRight = countRowPixelsRight(row, column, label); 
					rowCountLeft = countRowPixelsLeft(row, column, label); 
					columnCountDown = countColumnPixelsDown(row, column, label);
					columnCountUp = countColumnPixelsUp(row, column, label);
					//System.out.println("Moved left to ("+row+", "+column+"): down="+columnCountDown+", up="+columnCountUp+", right="+rowCountRight+", left="+rowCountLeft);
				}
			} //end of of trendRow == LEFT	
			} //end of else (trend = Primitive.HORIZONTAL) 

		} //end of while loop

/****************
		rowCountRight = countRowPixelsRight(lastRow, lastColumn, label); 
		rowCountLeft = countRowPixelsLeft(lastRow, lastColumn, label); 
		columnCountDown = countColumnPixelsDown(lastRow, lastColumn, label);
		columnCountUp = countColumnPixelsUp(lastRow, lastColumn, label);
		int done = 0;
		if (lastTrendRow == RIGHT) {
			newTrendRow = LEFT;
			//System.out.println("New trendRow=LEFT");
			if (rowCountLeft == 0) {
				done = 1;
			}
		}
		else {
			newTrendRow = RIGHT;
			//System.out.println("New trendRow=RIGHT");
			if (rowCountRight == 0) {
				done = 1;
			}
		}
		if (lastTrendColumn == DOWN) {
			newTrendColumn = UP;
			//System.out.println("New trendColumn=UP");
			if (columnCountUp == 0) {
				done = 1;
			}
		}
		else {
			newTrendColumn = DOWN;
			//System.out.println("New trendColumn=DOWN");
			if (columnCountDown == 0) {
				done = 1;
			}
		}

		if (done == 0) {
			//System.out.println("\nContinue with ("+lastRow+", "+lastColumn+")");
			trackThinLine(bImage, lastRow, lastColumn, label, newTrendRow, newTrendColumn); 
		}

		//if (nextStep) {
		//	trackThinLine(bImage, firstRow, firstColumn, label, LEFT, DOWN);
		//}

****************/
	}


  /**
   * Connects the pixels in bImage from previousPixel to 
	 * currentPixel in a straight line.
	 *
   * @param previousPixel The pixel to be connected
	 * @param currentPixel The pixel the previousPixel is to be connected to 
	 * @param bImage The 2d array representation of the processed image that is changed when the two pixels get connected
	 * @param value The label of the pixels to be connected
	 */	
	private void connectPixels(PointPixel previousPixel, PointPixel currentPixel, int[][] bImage, int value) {
		//System.out.println("Connection "+previousPixel+" to "+currentPixel);
		int row, column;
		LinkedList points = new LinkedList();
		points.add(previousPixel);
		points.add(currentPixel);
		LineFitter aLineFitter = new LineFitter();
	  Vector lineInfo = aLineFitter.fitStraightLineLSE(points);	
		int lineOrientation = ((Integer)lineInfo.get(0)).intValue();
		double slope = ((Double)lineInfo.get(1)).doubleValue();
		double intercept = ((Double)lineInfo.get(2)).doubleValue();
		if (lineOrientation == Primitive.HORIZONTAL) { //rows = m*columns + b
			for (int i = previousPixel.getColumn() + 1; i < currentPixel.getColumn(); i++) {
				row = (int)(slope*(double)i+intercept);
				bImage[row][i] = value;
			}
			for (int i = previousPixel.getColumn() - 1; i > currentPixel.getColumn(); i--) {
				row = (int)(slope*(double)i+intercept);
				bImage[row][i] = value;
			}
		}
		else if (lineOrientation == Primitive.VERTICAL) { //columns = m*rows + b
			for (int i = previousPixel.getRow() + 1; i < currentPixel.getRow(); i++) {
				column = (int)(slope*(double)i+intercept);
				bImage[i][column] = value;
			}
			for (int i = previousPixel.getRow() - 1; i > currentPixel.getRow(); i--) {
				column = (int)(slope*(double)i+intercept);
				bImage[i][column] = value;
			}
		}
	}


  /**
   * Counts the number of pixels of the same label
	 * as the pixel at point (r, c) to the right of (r, c)
	 * Start from point (r, c). Go right as far as you can, count.
	 *
   * @param r The row number of the pixel
	 * @param c The column number of the pixel
	 * @param labelNo The label of the pixel (the region label)
	 * @return The number of pixels counted
	 */	
	private	int countRowPixelsRight(int r, int c, int labelNo) {
		int countRight = 0;
		if (r >= 0 && c >= 0 && r < imageHeight && c < imageWidth) {
			int nextLabel = labelNo;
			int nextColumn = c;
			countRight = 0;
			while(nextLabel == labelNo && nextColumn < imageWidth - 1) {
				countRight++;
				nextColumn++;
				nextLabel = labelledImage[r][nextColumn]; 
			}
		}
		return (countRight - 1);
	}

  /**
   * Counts the number of pixels of the same label
	 * as the pixel at point (r, c) to the left of (r, c)
	 * Start from point (r, c). Go left as far as you can, count.
	 *
   * @param r The row number of the pixel
	 * @param c The column number of the pixel
	 * @param labelNo The label of the pixel (the region label)
	 * @return The number of pixels counted
	 * 
	 */	
	private	int countRowPixelsLeft(int r, int c, int labelNo) {
		int countLeft = 0;
		if (r >= 0 && c >= 0 && r < imageHeight && c < imageWidth) {
			int nextLabel = labelNo;
			int nextColumn = c;
			countLeft = 0;
			while(nextLabel == labelNo && nextColumn > 0) {
				countLeft++;
				nextColumn--;
				nextLabel = labelledImage[r][nextColumn]; 
			}
		}
		return (countLeft - 1);
	}


  /**
   * Counts the number of pixels of the same label
	 * as the pixel at point (r, c) below (r, c)
	 * Start from point (r, c). Go down as far as you can, count.
	 *
   * @param r The row number of the pixel
	 * @param c The column number of the pixel
	 * @param labelNo The label of the pixel (the region label)
	 * @return The number of pixels counted
	 */	
	private	int countColumnPixelsDown(int r, int c, int labelNo) {
		int countDown = 0;
		if (r >= 0 && c >= 0 && r < imageHeight && c < imageWidth) {
			int nextLabel = labelNo;
			int row = r;
			int nextRow = r;
			countDown = 0;
			while(nextLabel == labelNo && nextRow < imageHeight - 1) {
				countDown++;
				nextRow++;
				nextLabel = labelledImage[nextRow][c]; 
			}
		}
		return (countDown - 1);
	}

  /**
   * Counts the number of pixels of the same label
	 * as the pixel at point (r, c) above (r, c)
	 * Start from point (r, c). Go up as far as you can, count.
	 *
   * @param r The row number of the pixel
	 * @param c The column number of the pixel
	 * @param labelNo The label of the pixel (the region label)
	 * @return The number of pixels counted
	 */	
	private	int countColumnPixelsUp(int r, int c, int labelNo) {
		int countUp = 0;
		if (r >= 0 && c >= 0 && r < imageHeight && c < imageWidth) {
			int nextLabel = labelNo;
			int row = r;
			int nextRow = r;
			countUp = 0;
			while(nextLabel == labelNo && nextRow > 0) {
				countUp++;
				nextRow--;
				nextLabel = labelledImage[nextRow][c]; 
			}
		}
		return (countUp - 1);
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
	 * Returns the number of pixels in that area.
	 *
	 * @param label The label of the region whose pixels are to be counted
	 * @param upperLeft The upper left corner of the bounding box of the region
	 * @param lowerRight The lower right corner of the bounding box of the region
	 * @return The number of pixels in the region
	 */
	private int findPixelCount(int label, PointPixel upperLeft, PointPixel lowerRight) {
		int count = 0;
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
				if (labelledImage[i][j] == label) {
					count++;
				}
			}
		}
		return count;
	}


  /**
   * Prints the row and column counts of all the pixels,
	 * writes to the outfilename file.
	 *
   * @param outfilename The file name in which the information is written
	 * @return The 2d array representation of the image depicting the count values for all regions
   */
  public int[][] displayCounts(String outfilename) {
		int regionCount, row, column, rowCount, columnCount;		
		Region aRegion;
		PointPixel aPixel;
		LinkedList aList;
		ListIterator lItr;
		int[][] bImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				bImage[i][j] = 255;
			}
		}
		try {
			BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(outfilename));
		for(int i = 1;  i < noOfLabels; i++ ) {
			aRegion = allRegions[i];
			regionCount = aRegion.getNumPixels(); //number of border pixels in this region
			ostream.write(("\nRegion "+i+" has "+regionCount+ " border pixels.\n").getBytes());
			//System.out.println("Region "+i+" has "+regionCount+ " border pixels.");
			aList = aRegion.getPixelList();
			lItr = aList.listIterator(0);
			while(lItr.hasNext()) {
				aPixel = (PointPixel)lItr.next();
				row = aPixel.getRow(); 
				column = aPixel.getColumn();
				rowCount = aPixel.getRowCount();
				columnCount = aPixel.getColumnCount();
				if (rowCount == 1 || columnCount == 1) {
					bImage[row][column] = 210;
				}
				else {
					bImage[row][column] = 0;
				}
				if (rowCount < columnCount) {
					ostream.write(("(" + row + ", "+column+"): rowCount="+rowCount+", colCount="+columnCount+", min is rowCount="+rowCount+"\n").getBytes());
				}
				else {
					ostream.write(("(" + row + ", "+column+"): rowCount="+rowCount+", colCount="+columnCount+", min is columnCount="+columnCount+"\n").getBytes());
				}
				//System.out.println("(" + row + ", "+column+"): rowCount="+rowCount+", colCount="+columnCount);
			}
		}
		ostream.close();
		//System.out.println("Wrote row and column counts to "+outfilename);
		}
		catch (Exception e) {System.out.println(e.getMessage());}
		return bImage;
	}


  /**
	 * Creates the new labelled image 
	 * that has the original thick lined thinned.
	 * The thick line pixels are set to background label and the thinned
	 * line pixels are set to their corresponding labels.
	 *
   * @param none
	 * @return The 2d array representation of the labelled thinned image
   */
  public int[][] makeLabelImage() {
		int[][] bImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				bImage[i][j] = labelledImage[i][j];
				//if part of a thick line, set to background
				if (allRegions[labelledImage[i][j]].getIsThickLine())
					bImage[i][j] = bPixLabel;
				if (processedImage[i][j] != bPixLabel) 
					bImage[i][j] = processedImage[i][j];
			}
		}
		return bImage;
	}


  /**
	 * Processes the thinned line image and creates an image 
	 * that has the original thick line and the thinned line on 
	 * top of it in a lighter color.
	 * The background pixels are set to white.
	 * The thick line pixels are set to black and the thinned
	 * line pixels are set to gray.
	 *
   * @param none
	 * @return The 2d array representation of the thinned line image
   */
  public int[][] getThinLineImage2() {
		int[][] bImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				if (labelledImage[i][j] == bPixLabel) {
					bImage[i][j] = 255;
				}
				else {
					bImage[i][j] = 150;
				}
				if (allRegions[labelledImage[i][j]].getIsThickLine())
					bImage[i][j] = 0;
				//if part of a thick line, set to background
				if (processedImage[i][j] != bPixLabel) 
					bImage[i][j] = 200; 
			}
		}
		return bImage;
	}


  /**
   * Returns the 2-d array of the thinned line labels image without any processing.
	 *
   * @param none
	 * @return The 2d array representation of the thinned line image
   */
  public int[][] getThinLineImage() {
    return processedImage;
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

}

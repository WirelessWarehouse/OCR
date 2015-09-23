import java.util.*;
import java.lang.Math;
//import simpleOCR.*;
import java.lang.reflect.*;

/**
 * A class to hold information for a word.
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class Word {

	final static public int UNSET = 361;
	final int[] rPos = {-1, -1, -1, 0, 1, 1, 1, 0};
	final int[] cPos = {1, 0, -1, -1, -1, 0, 1, 1};

	private int keyNo; 	//The key of this word 
	private int height;					//The height of the bounding box
	private int width;					//The width of the bounding box
	private int bPixValue;			//The label of the background pixels
	private int[][] wordArea;		//The rectangular region in which this word is

	private int orientation;
	private double angle;
	private double distance;
	private int color;

	private boolean isVerticalWord;
	private boolean isHorizontalWord;
	private boolean isTitle;
	private boolean isXTitle;
	private boolean isXLabel;
	private boolean isYLabel;
	private boolean isYTitle;
	private boolean isLegend;
	private boolean isAttached; // to a bar, line or wedge

	private PointPixel upperLeft;		//The upper left corner of the bounding box
	private PointPixel lowerRight;	//The lower right corner of the bounding box
	private PointPixel value; 
	private PointPixel middleLineB;	//The begin point of the initial axis of the word found
	private PointPixel middleLineE;	//The end point of the initial axis of the word found
	private PointPixel axisB;				//The begin point of the axis of the word found
	private PointPixel axisE;				//The end point of the axis of the word found

	private HashSet characters;				//All the characters in this word
	private HashSet largeCharacters;	//All the large characters in this word
	private HashSet relevantCharacters;		//All the characters that the word's axis passes through 
	private String text;
        private String font = "";
	private static int chrcnt = 0;
	private static boolean inputImageSaved = false;
        private TextPiece inTextPiece = null;
        private Rectangle nearestBar = null;

	/**
	 * Constructor. The given region label number becomes the key of this word. 
	 *
	 * @param uL The upper left corner point of the bounding box of the word
	 * @param lR The lower right corner point of the bounding box of the word
	 * @param lab The label number of the region the word is in
	 */
	public Word(PointPixel uL, PointPixel lR, int lab) {
		upperLeft = new PointPixel(uL.getRow(), uL.getColumn());
		lowerRight = new PointPixel(lR.getRow(), lR.getColumn());
		keyNo = lab;
		setWord();
	}

	/**
	 * Initializes the attributes of the Word object.
	 *
	 * @param none
	 */
	private void setWord() {
		isVerticalWord = false;
		isHorizontalWord = false;
		isTitle = false;
		isXTitle = false;
		isYTitle = false;
		isXLabel = false;
		isYLabel = false;
		isLegend = false;
		isAttached = false;
		angle = UNSET;
		middleLineB = new PointPixel(-1, -1);
		middleLineE = new PointPixel(-1, -1);
		axisB = new PointPixel(-1, -1);
		axisE = new PointPixel(-1, -1);
		relevantCharacters = new HashSet();
	}

	/**
	 * Sets the area (the pixels) that the bounding box
	 * of this word occupies.
	 *
	 * @param h The height of the bounding box of the word
	 * @param w The width of the bounding box of the word
	 * @param area The 2d array representing the pixels in the area of the word
	 * @param r The row number of the upper left corner of the bounding box of the word
	 * @param c The column number of the upper left corner of the bounding box of the word
	 * @param bP The label number of the background pixel in the image 
	 */
	public void setArea(int h, int w, int[][] area, int r, int c, int bP) {
		height = h;
		width = w;
		bPixValue = bP;
		wordArea = new int[h][w];
		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				wordArea[i][j] = area[i][j];
			}
		}
	}

	/**
	 * Finds all the characters in this word, and determines which are 
	 * the large characters.
	 *
	 * @param none
	 */
	public void findCharacterCount() {
		int label, area;
		characters = new HashSet();
		largeCharacters = new HashSet();
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				label = wordArea[i][j];
				if (label != bPixValue) {
					characters.add(new Integer(label));
					largeCharacters.add(new Integer(label));
				}
			}
		}
		//System.out.print("Word "+keyNo+" contains "+characters.size()+" characters: ");
		Iterator itr = characters.iterator();
		while (itr.hasNext()) {
			label = ((Integer)itr.next()).intValue();
			//System.out.print(label+" ");
		}
		//System.out.print("\n");

		double averageArea = getAverageCharacterArea(characters);
		//System.out.println("Average area is "+averageArea);
		itr = largeCharacters.iterator();
		while (itr.hasNext()) {
			label = ((Integer)itr.next()).intValue();
			area = getCharacterArea(label);
			//System.out.println("Area for character "+label+" is  "+area);
			//If the area of a character is less than 0.3 times the average
			//area, then that word is "small"
			if (area < 0.3*averageArea) {
				//System.out.println("Area of character "+label+" is smaller than 0.3*averageArea. Irrelevant character.");
				itr.remove();
			}
		}
		//System.out.print("Word "+keyNo+" contains "+largeCharacters.size()+" large characters: ");
		itr = largeCharacters.iterator();
		while (itr.hasNext()) {
			label = ((Integer)itr.next()).intValue();
			//System.out.print(label+" ");
		}
		//System.out.print("\n");
	}

	/**
	 * Calculates the number of pixels a particular character
	 * occupies in this word.
	 *
	 * @param label The given label number for a particular region in the word
	 * @return The number of pixel in the word that are of the region that is given
	 */
	private int getCharacterArea(int label) {
		int area = 0;
		int lab;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				lab = wordArea[i][j];
				if (label == lab) {
					area++;
				}
			}
		}
		return area;
	}

	/**
	 * Goes through all the character regions in this word
	 * and calculates the average area occupied by all the characters.
	 *
	 * @param aSet The set of the character regions in the word
	 * @return The average area occupied by all the characters of the word
	 */
	private double getAverageCharacterArea(HashSet aSet) {
		int area, count, totalArea;
		double avgArea;
		Iterator itr = aSet.iterator();
		count = 0;
		totalArea = 0;
		while (itr.hasNext()) {
			count++;
			area = getCharacterArea(((Integer)itr.next()).intValue());
			totalArea += area;
		}
		avgArea = (double)totalArea/(double)count;
		return avgArea;
	}

	/**
	 * Calculates the orientation angle of this word.
	 * First tries horizontal or vertical according to whether the 
	 * height of the word is greater than the width or not.
	 * Once the trial axis is defined, it is moved in the word region 
	 * parallel to itself and the characters that it crosses are 
	 * calculated. If the crossing characters are the same as the
	 * characters that are contained in the region, then the axis line
	 * is found. If not, another axis line is tried with a different angle.
	 *
	 * @param none
	 * @return The orientation angle of the word
	 */
	public double calculateAngle() {
		//Initialize the initial axis points
		middleLineB.move(-1, -1);
		middleLineE.move(-1, -1);
		//Initialize the final axis points
		axisB.move(-1, -1);
		axisE.move(-1, -1);
		if (characters.size() == 1) {
                   //This is a little too ad hoc
			//This is a vertical (upright) character.
			//Axis is horizontal, going through the bounding box in the middle of it.
			angle = 90;
// DLC what happens if we don't set orientation?
			//orientation = Primitive.HORIZONTAL;
// end DLC
			axisB.move(height/2, 0);
			axisE.move(height/2, width-1);
			distance = upperLeft.getRow() + height/2;
			//If width > height, this is a horizontal character.
			//Axis is vertical, going through the bounding box in the middle of it.
// DLC let's not do this?
/*
			if (width > height) {
				angle = 0;
				axisB.move(0, width/2);
				axisE.move(height-1, width/2);
				orientation = Primitive.VERTICAL;
				distance = upperLeft.getColumn() + width/2;
			}
*/ //end DLC
			return angle;
		}
		else if (characters.size() > 1) {
			//If there are more than one characters and height <= width,
			//this is a vertical (upright) word. The axis is horizontal.
			int angleNo = 1; //Horizontal
			//If there are more than one characters and height > width,
			//this is a horizontal word. The axis is vertical.
			if (height > width) {
				angleNo = 0; //Vertical
			}
			calculateCharacters(angleNo, -1, 0);
			//System.out.println("Angle is "+angle);
			//Using angle, middleLineB, middleLineE find the axis line
			findAxisLine();
		}
		//System.out.println("Word angle is "+angle+". Axis from "+axisB+" to "+axisE);
		return angle;
	}

	/**
	 * Calculates the number of characters that an axis
	 * of the specified angle crosses in this word.
	 *
	 * @param angNo The angle of the axis for which the crossing characters will be counted
	 * @param order The order that determines the next angle
	 * @param lineNo Not used, it is zero
	 */
	private void calculateCharacters(int angNo, int order, int lineNo) {
		if (angNo == 4) {
			angle = UNSET;
			return;
		}
		int label, charLabel, newAngNo, area;
		int keepSearching = 0;
		double averageArea;
		PointPixel aPoint;
		HashSet missingSet;
		Iterator itr;
		//Move the initial axis such that the it makes an angle of angNo.
		moveMiddleLine(angNo, lineNo);
		//Compute the set of characters that this axis crosses through.
		Vector angleChars = characterCrossing(middleLineB, middleLineE);
		relevantCharacters = (HashSet)angleChars.get(1);
		//relevantCharacters is the set of characters that this axis crosses through.
		/*
		//System.out.println("Size of relevantCharacters is "+relevantCharacters.size());
		itr = relevantCharacters.iterator();
		while (itr.hasNext()) {
				//System.out.println(((Integer)itr.next()).intValue());
		}	
		*/
		//Compare the set relevantCharacters with the set largeCharacters
		if (relevantCharacters.size() >= largeCharacters.size()) {
			//The relevantCharacters should be containing everything in the largeCharacters set.
			//Therefore, the axis that was tried is the right one.
			angle = ((Double)angleChars.get(0)).doubleValue();
			//System.out.println("Same size. Angle is "+angle);
			return;
		}
		else if (relevantCharacters.size() < largeCharacters.size()) {
			//The relevantCharacters set is missing some elements of the largeCharacters set.
			//Change the axis and try again.
			keepSearching = 0;
			missingSet = compareSets(relevantCharacters, largeCharacters);
			averageArea = getAverageCharacterArea(largeCharacters);
			//System.out.println("Average area is "+averageArea);
			itr = missingSet.iterator();
			while (itr.hasNext()) {
				charLabel = ((Integer)itr.next()).intValue();
				area = getCharacterArea(charLabel);
				//System.out.println("Area for missing character "+charLabel+" is  "+area);
				if (area > 0.3*averageArea) {
					//System.out.println("Area is greater than 0.3*averageArea. Keep searching.");
					keepSearching = 1;
				}
			}
			if (keepSearching == 0) {
		  	angle = ((Double)angleChars.get(0)).doubleValue();
				//System.out.println("Similar size. Angle is "+angle);
				return;
			}
			//lineNo++;
			//if (lineNo > 2) {
				if (order == -1) {
					newAngNo = 1 - angNo; 
					calculateCharacters(newAngNo, 0, 0);	
				}
				else if (order == 0) {
					newAngNo = 2;
					calculateCharacters(newAngNo, 1, 0);	
				}
				else if (order == 1) {
					newAngNo = angNo + 1;
					calculateCharacters(newAngNo, 1, 0);	
				}
			//}
			//else {
				//calculateCharacters(angNo, order, lineNo);	
			//}
		}
	}


	/**
	 * Changes the middle line according to the given parameters.
	 * 
	 * @param ang The new angle of the middle line
	 * @param lineNo Not used, set to zero.
	 */
	private void moveMiddleLine(int ang, int lineNo) {
		/*
		if (lineNo == 2) {
			lineNo = -1;
		}
		*/
		lineNo = 0;
		if (ang == 0) { //angle = 0 vertical 
			middleLineB.move(0, width/2+lineNo);
			middleLineE.move(height-1, width/2+lineNo);
			//middleLineB = new PointPixel(0, width/2+lineNo);
			//middleLineE = new PointPixel(height-1, width/2+lineNo);
		}
		else if (ang == 1) { //angle = 90 horizontal 
			middleLineB.move(height/2+lineNo, 0);
			middleLineE.move(height/2+lineNo, width-1);
			//middleLineB = new PointPixel(height/2+lineNo, 0);
			//middleLineE = new PointPixel(height/2+lineNo, width-1);
		}
		else if (ang == 2) { //angle = diagonal
			middleLineB.move(0, 0);
			middleLineE.move(height-1, width-1);
			/*
			if (lineNo == 0) {
				middleLineB.move(0, 0);
				middleLineE.move(height-1, width-1);
				//middleLineB = new PointPixel(0, 0);
				//middleLineE = new PointPixel(height-1, width-1);
			}
			else if (lineNo == 1) {
				middleLineB.move(0, 0+lineNo);
				middleLineE.move(height-1-lineNo, width-1);
				//middleLineB = new PointPixel(0, 0+lineNo);
				//middleLineE = new PointPixel(height-1-lineNo, width-1);
			}
			else if (lineNo == -1) {
				middleLineB.move(0-lineNo, 0);
				middleLineE.move(height-1, width-1+lineNo);
				//middleLineB = new PointPixel(0-lineNo, 0);
				//middleLineE = new PointPixel(height-1, width-1+lineNo);
			}
			*/
		}
		else if (ang == 3) { //angle = diagonal
			middleLineB.move(0, width-1);
			middleLineE.move(height-1, 0);
			/*
			if (lineNo == 0) {
				middleLineB.move(0, width-1);
				middleLineE.move(height-1, 0);
				//middleLineB = new PointPixel(0, width-1);
				//middleLineE = new PointPixel(height-1, 0);
			}
			else if (lineNo == 1) {
				middleLineB.move(0, width-1-lineNo);
				middleLineE.move(height-1-lineNo, 0);
				//middleLineB = new PointPixel(0, width-1-lineNo);
				//middleLineE = new PointPixel(height-1-lineNo, 0);
			}
			else if (lineNo == -1) {
				middleLineB.move(0-lineNo, width-1);
				middleLineE.move(height-1, 0-lineNo);
				//middleLineB = new PointPixel(0-lineNo, width-1);
				//middleLineE = new PointPixel(height-1, 0-lineNo);
			}
			*/
		}
	}

	/**
	 * Calculates the characters that a given line crosses in the current word.
	 *
	 * @param beginP The beginning point of the crossing line
	 * @param endP The end point of the crossing line
	 * @return The vector containing the angle of the crossing line and the set of characters it crosses in the word
	 */
	private Vector characterCrossing(PointPixel beginP, PointPixel endP) {
		int label, row, col;
		PointPixel aPoint;
		Vector angleSetVector = new Vector(2);
		HashSet aSet = new HashSet();
		LineFitter aFitter = new LineFitter();
		LinkedList points = aFitter.drawLinePoints(beginP, endP);
		Vector lineInfo = aFitter.getLine(beginP, endP);
		int lineOrientation = ((Integer)lineInfo.get(0)).intValue();
		double slope = ((Double)lineInfo.get(1)).doubleValue();
		double intercept = ((Double)lineInfo.get(2)).doubleValue();
		double ang = aFitter.getAngle(lineOrientation, slope); 
		ListIterator lItr = points.listIterator();
		while (lItr.hasNext()) {
			aPoint = (PointPixel)lItr.next();
			row = aPoint.getRow();
			col = aPoint.getColumn();
			if (row >= 0 && col >= 0 && row < height && col < width) {
				label = wordArea[row][col];
				if (label != bPixValue) {
					aSet.add(new Integer(label));
				}
			}
			if ((row-1) >= 0 && col >= 0 && (row-1) < height && col < width) {
				label = wordArea[row-1][col];
				if (label != bPixValue) {
					aSet.add(new Integer(label));
				}
			}
			if ((row+1) >= 0 && col >= 0 && (row+1) < height && col < width) {
				label = wordArea[row+1][col];
				if (label != bPixValue) {
					aSet.add(new Integer(label));
				}
			}
			if (row >= 0 && (col-1) >= 0 && row < height && (col-1) < width) {
				label = wordArea[row][col-1];
				if (label != bPixValue) {
					aSet.add(new Integer(label));
				}
			}
			if (row >= 0 && (col+1) >= 0 && row < height && (col+1) < width) {
				label = wordArea[row][col+1];
				if (label != bPixValue) {
					aSet.add(new Integer(label));
				}
			}
		}
		angleSetVector.add(new Double(ang));
		angleSetVector.add(aSet);
		return angleSetVector;
	}


	/**
	 * Using angle, middleLineB, middleLineE finds the axis line.
	 * Finds the two directions perpendicular to angle.
	 * Translates the middleLine points in the two
	 * perpendicular directions and finds out whether the 
	 * line crosses all the characters of the word.
	 * The point where the middleLine no longer crosses all 
	 * the large characters in both directions define
	 * the two lines of the word. The axis line 
	 * is the middle line between those two lines.
	 * 
	 * @param none
	 */
	private void findAxisLine() {
		//System.out.println("Finding axis line. Angle = "+angle);
		//System.out.print("Word contains "+relevantCharacters.size()+" relevant characters: ");
		int label;
		Iterator itr = relevantCharacters.iterator();
		while (itr.hasNext()) {
			label = ((Integer)itr.next()).intValue();
			//System.out.print(label+" ");
		}
		//System.out.print("\n");

		boolean crossing = true;
		PointPixel firstAxisB, firstAxisE, secondAxisB, secondAxisE;
		HashSet charSet;
		Vector lineInfo;
		double perpAngle = angle + 90;
		if (perpAngle >= 360) {
			perpAngle = perpAngle - 360;
		}
		int direction = mapToDirection(perpAngle);
		//System.out.println("Direction for angle "+perpAngle+" is "+direction);
		PointPixel beginP = new PointPixel(middleLineB.getRow(), middleLineB.getColumn());
		PointPixel endP = new PointPixel(middleLineE.getRow(), middleLineE.getColumn());
		//System.out.println("Starting line is from "+beginP+" and "+endP);
		while (crossing) {
			beginP.move(beginP.getRow()+rPos[direction], beginP.getColumn()+cPos[direction]);
			endP.move(endP.getRow()+rPos[direction], endP.getColumn()+cPos[direction]);
			//System.out.println("Translated to "+beginP+" and "+endP);
			lineInfo = characterCrossing(beginP, endP);	
			charSet = (HashSet)lineInfo.get(1);
			//if (!charSet.containsAll(relevantCharacters)) {
			/* } to balance commented code */
			if (!charSet.containsAll(largeCharacters)) {
				crossing = false;
			}
		}
		firstAxisB = new PointPixel(beginP.getRow(), beginP.getColumn());
		firstAxisE = new PointPixel(endP.getRow(), endP.getColumn());

		perpAngle = perpAngle + 180;
		if (perpAngle >= 360) {
			perpAngle = perpAngle - 360;
		}
		direction = mapToDirection(perpAngle);
		//System.out.println("Direction for angle "+perpAngle+" is "+direction);
		beginP.move(middleLineB.getRow(), middleLineB.getColumn());
		endP.move(middleLineE.getRow(), middleLineE.getColumn());
		crossing = true;
		//System.out.println("Starting line is from "+beginP+" and "+endP);
		while (crossing) {
			beginP.translate(rPos[direction], cPos[direction]);
			endP.translate(rPos[direction], cPos[direction]);
			//System.out.println("Translated to "+beginP+" and "+endP);
			lineInfo = characterCrossing(beginP, endP);	
			charSet = (HashSet)lineInfo.get(1);
			//if (!charSet.equals(relevantCharacters)) {
			/* } to balance commented code */
			//if (!charSet.containsAll(relevantCharacters)) {
			/* } to balance commented code */
			if (!charSet.containsAll(largeCharacters)) {
				crossing = false;
			}
		}
		secondAxisB = new PointPixel(beginP.getRow(), beginP.getColumn());
		secondAxisE = new PointPixel(endP.getRow(), endP.getColumn());
			
		//System.out.print("Axis 1 from "+firstAxisB+" to "+firstAxisE+"; axis 2 from "+secondAxisB+" to "+secondAxisE);

		axisB.move((firstAxisB.getRow()+secondAxisB.getRow())/2, (firstAxisB.getColumn()+secondAxisB.getColumn())/2);
		axisE.move((firstAxisE.getRow()+secondAxisE.getRow())/2, (firstAxisE.getColumn()+secondAxisE.getColumn())/2);

		LineFitter aFitter = new LineFitter();
		angle = aFitter.getAngle(axisB, axisE);
		orientation = aFitter.getOrientation(axisB, axisE);
		PointPixel aB = new PointPixel(axisB.getRow()+upperLeft.getRow(), axisB.getColumn()+upperLeft.getColumn());
		PointPixel aE = new PointPixel(axisE.getRow()+upperLeft.getRow(), axisE.getColumn()+upperLeft.getColumn());
		distance = (int)(Math.rint((Math.sqrt(aFitter.squareDistPointToLine(0, 0, aB, aE)))));
	}

	/**
	 * Compares two sets; set1 and set2. Returns set3 which contains
	 * the elements that set2 has but set1 does not have.
	 *
	 * @param set1 The first set
	 * @param set2 The second set
	 * @return The set of elements that the second set has but the first set does not have
	 */
	private HashSet compareSets(HashSet set1, HashSet set2) {
		HashSet set3 = new HashSet();
		Object elem;
		Iterator itr = set2.iterator();
		while (itr.hasNext()) {
			elem = itr.next();
			if (!set1.contains(elem)) {
				set3.add(elem);
			}
		}
		return set3;
	}

	/**
	 * Given an angle, converts the value to one of the 8 neighbor
	 * directions of a pixel. 
	 * <p>
	 * 2 1 0 <br>
	 * 3 - 7 <br>
	 * 4 5 6 <br>
	 * <p>
	 * The given angle is counter clockwise from the positive row axis
	 * (negative y axis).
	 * 
	 * @param ang The angle to be mapped to a direction
	 * @return The direction the given angle maps to
	 */
	private int mapToDirection(double ang) {
		ang = ang - 90;
		if (ang < 0) {
			ang = ang + 360;
		}
		if (ang > 360) {
			ang = ang - 360;
		}
		if (ang >= 22.5 && ang <= 360) {
			return ((int)((ang + 22.5) / 45) - 1); 
		}
		if (ang >= 0 && ang <= 22.5) {
			return 7;
		}
		/*
		if (ang >= 22.5 && ang <= 67.5) {
			return 0;
		}
		if (ang >= 67.5 && ang <= 112.5) {
			return 1;
		}
		if (ang >= 112.5 && ang <= 157.5) {
			return 2;
		}
		if (ang >= 157.5 && ang <= 202.5) {
			return 3;
		}
		if (ang >= 202.5 && ang <= 247.5) {
			return 4;
		}
		if (ang >= 247.5 && ang <= 292.5) {
			return 5;
		}
		if (ang >= 292.5 && ang <= 337.5) {
			return 6;
		}
		if ((ang >= 337.5 && ang <= 360) || (ang >= 0 && ang <= 22.5)) {
			return 7;
		}
		*/
		return -1;
	}

	/**
	 * Returns the set of the character regions in the current word.
	 *
	 * @param none
	 * @return The hash set of the character regions in the word
	 */
	public HashSet getCharacters() {
		return characters;
	}

	/**
	 * Returns the begin point of the axis line of the word.
	 *
	 * @param none
	 * @return The begin point of the axis line of the word
	 */
	public PointPixel getAxisBeginPoint() {
		PointPixel aB = new PointPixel(axisB.getRow()+upperLeft.getRow(), axisB.getColumn()+upperLeft.getColumn());
		return aB;
	}

	/**
	 * Returns the end point of the axis line of the word.
	 *
	 * @param none
	 * @return The end point of the axis line of the word
	 */
	public PointPixel getAxisEndPoint() {
		PointPixel aE = new PointPixel(axisE.getRow()+upperLeft.getRow(), axisE.getColumn()+upperLeft.getColumn());
		return aE;
	}


	/**
	 * Returns the height of the bounding box of the word.
	 *
	 * @param none
	 * @return The height of the bounding box of the word
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Returns the width of the bounding box of the word.
	 *
	 * @param none
	 * @return The width of the bounding box of the word
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Returns the upper left corner point of the bounding box of the word.
	 *
	 * @param none
	 * @return The upper left corner point of the bounding box of the word
	 */
	public PointPixel getUpperLeft() {
		return upperLeft;
	}

	/**
	 * Returns the lower right corner point of the bounding box of the word.
	 *
	 * @param none
	 * @return The lower right corner point of the bounding box of the word
	 */
	public PointPixel getLowerRight() {
		return lowerRight;
	}

	/**
	 * Sets the upper left corner point of the bounding box of the word.
	 *
	 * @param u The upper left corner point of the bounding box of the word
	 */
	public void setUpperLeft(PointPixel u) {
		upperLeft = u;
	}

	/**
	 * Sets the lower right corner point of the bounding box of the word.
	 *
	 * @param l The lower right corner point of the bounding box of the word
	 */
	public void setLowerRight(PointPixel l) {
		lowerRight = l;
	}

	/**
	 * Returns the key of the word in the hash table of words.
	 *
	 * @param none
	 * @return The key of the word
	 */
	public int getKey() {
		return keyNo;
	}

	/**
	 * Sets the key of the word in the hash table of words.
	 *
	 * @param a The key of the word 
	 */
	public void setKeyNo(int a) {
		keyNo = a;
	}

	/**
	 * Sets whether the word is vertical or not
	 * 
	 * @param c True if the word is vertical and false otherwise 
	 */
	public void setIsVerticalWord(boolean c) {
		isVerticalWord = c;
	}

	/**
	 * Sets whether the word is horizontal or not
	 * 
	 * @param c True if the word is horizontal and false otherwise 
	 */
	public void setIsHorizontalWord(boolean c) {
		isHorizontalWord = c;
	}

	/**
	 * Returns whether the word is vertical or not
	 * 
	 * @param none
	 * @return True if the word is vertical and false otherwise 
	 */
	public boolean getIsVerticalWord() {
		return isVerticalWord;
	}

	/**
	 * Returns whether the word is horizontal or not
	 * 
	 * @param none
	 * @return True if the word is horizontal and false otherwise 
	 */
	public boolean getIsHorizontalWord() {
		return isHorizontalWord;
	}

	/**
	 * Returns the orientation of the word's axis line; horizontal or vertical
	 * 
	 * @param none
	 * @return The orientation of the word
	 */
	public int getOrientation() {
		return orientation;
	}

	/**
	 * Returns the angle of the word's axis line.
	 * 
	 * @param none
	 * @return The angle of the word with respect to the positive row axis
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Returns the distance of the word's axis from the origin.
	 * 
	 * @param none
	 * @return The distance of the word's axis from the origin
	 */
	public double getDistance() {
		return distance;
	}

	/**
	 * Returns the area (the pixels) that the bounding box
	 * of this word occupies.
	 * 
	 * @param none
	 * @return The word area
	 */
	public int[][] getArea() {
		return wordArea;	
	}

	/**
	 * Sets whether the word appears in the legend or not
	 * 
	 * @param True if the word is in the legend and false otherwise
	 */
	public void setIsLegend(boolean c) {
		isLegend = c;
	}

	/**
	 * Returns whether the word appears in the legend or not
	 * 
	 * @param none
	 * @return True if the word is in the legend and false otherwise
	 */
	public boolean getIsLegend() {
		return isLegend;
	}

	/**
	 * Sets whether the word appears in the title of the chart or not
	 * 
	 * @param c True if the word is in the title and false otherwise
	 */
	public void setIsTitle(boolean c) {
		isTitle = c;
	}

	/**
	 * Returns whether the word appears in the title of the chart or not
	 * 
	 * @param none
	 * @return True if the word is in the title and false otherwise
	 */
	public boolean getIsTitle() {
		return isTitle;
	}

	/**
	 * Returns whether the word appears in the y axis title of the chart or not
	 * 
	 * @param none
	 * @return True if the word is in the y axis title and false otherwise
	 */
	public boolean getIsYTitle() {
		return isYTitle;
	}

	/**
	 * Sets whether the word appears in the y axis title of the chart or not
	 * 
	 * @param c True if the word is in the y axis title and false otherwise
	 */
	public void setIsYTitle(boolean c) {
		isYTitle = c;
	}

	/**
	 * Returns whether the word appears in the x axis title of the chart or not
	 * 
	 * @param none
	 * @return True if the word is in the x axis title and false otherwise
	 */
	public boolean getIsXTitle() {
		return isXTitle;
	}

	/**
	 * Sets whether the word appears in the x axis title of the chart or not
	 * 
	 * @param c True if the word is in the x axis title and false otherwise
	 */
	public void setIsXTitle(boolean c) {
		isXTitle = c;
	}

	/**
	 * Returns the number of character regions in the word.
	 * 
	 * @param none
	 * @return The number of character regions in the word
	 */
	public int getNoOfCharacters() {
		return characters.size();
	}

	/**
	 * Returns a string holding information about the word 
	 * to be printed on the screen.
	 *
	 * @param none
	 * @return The string of information for the word 
   */
	public String toString() {
		String message = new String ("Word of "+characters.size()+"characters: ");
		if (isVerticalWord) {
			message = message + ("Vertical, ");
		}
		else if (isHorizontalWord) {
			message = message + ("Horizontal, ");
		}
		if (isTitle) {
			message = message + ("Title, ");
		}
		if (isXTitle) {
			message = message + ("XTitle, ");
		}
		if (isYTitle) {
			message = message + ("YTitle, ");
		}
		if (isXLabel) {
			message = message + ("XLabel, ");
		}
		if (isYLabel) {
			message = message + ("YLabel, ");
		}
		if (isLegend) {
			message = message + ("Legend, ");
		}
		message = (message + ("Height = "+height+", Width = "+width+", Color = "+color+". "));
		message = (message + ("Upper left corner = "+upperLeft+", Lower right corner = "+lowerRight+". "));
		message = (message + ("Orientation = "+orientation+", angle = "+angle+", distance = "+distance+"\n"));
		return message;
	}

    public class HorizontalCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((Region)x).getUpperLeft().getColumn();
	    cy = ((Region)y).getUpperLeft().getColumn();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}

    public class VerticalCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((Region)x).getLowerRight().getRow();
	    cy = ((Region)y).getLowerRight().getRow();
	    if (cx>cy) return -1;
 		else if (cx==cy) return 0;
	    else return 1;}}
	    


/*
* In readWord, the characters in the word are sorted according to
* position.  Then the bounding box for each character
* is copied and passed to decideChar to decide what character it
* is.  This character is added to the text string.  Combinations
* of characters such as "i.", ".i", "j.", ".j" and "o/o" are
* examined to see if they are realy one character ("i', "j" or "%").
* The word is read horizontally and vertically. If both produce
* output, the shorter string is chosen.
*/

    public void readWord(Region[] allRegions, int[][] inputLabelImage) {
        String hs = readHorizontalWord(allRegions,inputLabelImage);
        String vs = readVerticalWord(allRegions,inputLabelImage);
//System.out.println("hs " + hs + " vs " + vs);
//Kluge for now. One of the TR17 periods in L21nb is read as a ` for some
// reason even though they all look the same.
          vs = "";
          if (hs.equals("") && vs.equals("")) {
            font = "TR01";  //an arbitrary choice
            text = "???";
            orientation = Primitive.HORIZONTAL;  //an arbitrary choice
            isHorizontalWord = true;}
          else if (hs.equals(vs)) { // character is symmetrical
            font = hs.substring(0,4);
            text = hs.substring(4);
            orientation = Primitive.HORIZONTAL;
            isHorizontalWord = true;
            isVerticalWord = true;}
          else if (vs.equals("")
                   || (!hs.equals("")
                       && hs.length() < vs.length())
                   || (!hs.equals("")
                       && hs.substring(0,4).equals("tr10"))) { //found horizontal word
            font = hs.substring(0,4);
            text = hs.substring(4);
            orientation = Primitive.HORIZONTAL;
            isHorizontalWord = true;}
          else { //found vertical word
            font = vs.substring(0,4);
            text = vs.substring(4);
            orientation = Primitive.VERTICAL;
            isVerticalWord = true;}
System.out.println("text = " + text);
    }
          
         
            


/*
*  Tries to read the word horizontally
*/
    public String readHorizontalWord(Region[] allRegions, int[][] inputLabelImage) {
	Object[] charArray = characters.toArray();
	String[] previousChar = new String[5];
	int[] previousCharPosition = new int[5];
	int previousCharIndex;
	int d = 3;
        String localFont = "";
	int label;
	int i;
	int j;
        int highDotXB = -1;
        int lowDotXB = -1;
        String highDot = "";
        String lowDot = "";
	Region reg;
        int rb = (upperLeft.getRow() + lowerRight.getRow())/2;
	previousCharIndex = 0;
/*
	if (!inputImageSaved) {
	    BWImageG.save("chrImage.pgm",
			  inputLabelImage,
			  Array.getLength(inputLabelImage),
			  Array.getLength(inputLabelImage[0]));
	    inputImageSaved = true;}
*/
//System.out.println("charArray.length " + charArray.length);

	    HorizontalCompare hC = new HorizontalCompare();
	    for (i = 0; i < charArray.length; i++) {
		label = ((Integer)charArray[i]).intValue();
//DLC
//System.out.println("horizontal chars i = " + i + " label = " + label);
//endDLC
		reg = allRegions[label];
		charArray[i] = reg;}
	    Arrays.sort(charArray,hC);
	    PointPixel upperleft;
	    PointPixel lowerright;
	    StringBuffer s = new StringBuffer();
	    for (i = 0; i < charArray.length; i++) {
		     reg = (Region)charArray[i];
		     upperleft = reg.getUpperLeft();
		     lowerright = reg.getLowerRight();
		     int yb = upperleft.getRow();
		     int ye = lowerright.getRow();
		     int xb = upperleft.getColumn();
		     int xe = lowerright.getColumn();
		     int[][] charImage = new int[ye - yb + 1][xe - xb + 1];
		     int y;
		     int x;
		     for (y=yb;y<=ye;y++)
			 for (x=xb;x<=xe;x++)
			     charImage[y - yb][x - xb] =
				 (inputLabelImage[y][x]==reg.getRegion())? 0 : 255;
if (i < 5) {
//System.out.println("i = " + i);
//System.out.println("yb ye xb xe: " + yb + " " + ye + " " + xb + " " + xe);
//System.out.println("chrcnt " + chrcnt);
		     //BWImageG.save("chr"+chrcnt,charImage,ye-yb+1,xe-xb+1);
}
		     chrcnt++;
		     String chr = simpleOCR.decideChar(charImage);
//System.out.println("chr " + chr);
                     String tempFont;
                     if (chr != "") {
System.out.println("horizontal " + chr);
                       tempFont = chr.substring(0,4);
                       if (localFont.equals("")) localFont = tempFont;
                       chr =  chr.substring(4);
		     //System.out.println("chr = " + (char)chr);
                     }
                     else {return "";}
//if (chr.equals("'")) System.out.println("' yb rb " + yb + " " + rb);
                     if (chr.equals("'") && yb > rb) chr = ",";
//System.out.println("chr now is " + chr);
                     if (chr.equals(".") || chr.equals(",")) {
//System.out.println("yb ye xb xe: " + yb + " " + ye + " " + xb + " " + xe);
// DLC omit dot if above axis
                        if (yb < rb) {
//System.out.println("throwing away a dot");
                            /* skip this char */ ;
                            highDotXB = xb; //but remember where it was
                            highDot = chr;
                            if (xb == lowDotXB) {
                                lowDotXB = highDotXB = -1;
                                chr = ":";
			        for (j=0;j<previousCharIndex;j++) {
			            s.append(previousChar[j]);}
			        s.append(chr);
			        previousCharIndex = 0;}
                        }
                        else {
                            lowDotXB = xb;
                            lowDot = chr;
                            if (xb == highDotXB) {
                                lowDotXB = highDotXB = -1;
                                chr = ":";
			        for (j=0;j<previousCharIndex;j++) {
			            s.append(previousChar[j]);}
			        s.append(chr);
			        previousCharIndex = 0;}}}
		     else {
                         if (lowDotXB > 0) {
			     for (j=0;j<previousCharIndex;j++) {
			         s.append(previousChar[j]);}
			     s.append(lowDot);
			     previousCharIndex = 0;}
                         highDotXB = lowDotXB = -1;
                         if (chr.equals("o")) {
			     if (previousCharIndex == 0) {
			         previousChar[0] = chr;
			         previousCharPosition[0] = xb;
			         previousCharIndex = 1;}
			     else if (previousCharIndex==1) {
			         if (previousChar[0].equals("/")
				     &&
			             Math.abs(previousCharPosition[0]-xb) <= d) {
				     previousChar[1] = chr;
				     previousCharPosition[1] = xb;
				     previousCharIndex = 2;}
			         else {
				     s.append(previousChar[0]);
				     previousChar[0] = chr;
				     previousCharPosition[0] = xb;}}
			     else { //previousCharIndex = 2 and array holds o,/
                                 s.append("%");
			         previousCharIndex = 0;}}
		         else if (chr.equals("/")) {
			     if (previousCharIndex == 0) {
			         previousChar[0] = chr;
			         previousCharPosition[0] = xb;
			         previousCharIndex = 1;}
			     else if (previousCharIndex==1) {
			         if (previousChar[0].equals("o")
				     &&
			             Math.abs(previousCharPosition[0]-xb) <= d) {
				     previousChar[1] = chr;
				     previousCharPosition[1] = xb;
				     previousCharIndex = 2;}
			         else {
				     s.append(previousChar[0]);
				     previousChar[0] = chr;
				     previousCharPosition[0] = xb;}}
			     else { //previousCharIndex==2, shouldn't happen
			         s.append(previousChar[0]);
			         s.append(previousChar[1]);
			         previousChar[0] = chr;
			         previousCharPosition[0] = xb;
			         previousCharIndex = 1;}}
		         else {
			     for (j=0;j<previousCharIndex;j++) {
			         s.append(previousChar[j]);}
			     s.append(chr);
			     previousCharIndex = 0;}}}
	    for (i=0;i<previousCharIndex;i++) {
	        s.append(previousChar[i]);}
            if (lowDotXB > 0) s.append(lowDot);

System.out.println("stostring= "+s.toString());
	    return localFont + s.toString();
	    }

/*
*  Tries to read a Vertical word
*/

    public String readVerticalWord(Region[] allRegions, int[][] inputLabelImage) {
	Object[] charArray = characters.toArray();
	String[] previousChar = new String[5];
	int[] previousCharPosition = new int[5];
	int previousCharIndex;
	int d = 3;
        String localFont = "";
	int label;
	int i;
	int j;
	Region reg;
        int cb = (upperLeft.getColumn() + lowerRight.getColumn())/2;
	previousCharIndex = 0;
	    VerticalCompare vC = new VerticalCompare();
	    for (i = 0; i < charArray.length; i++) {
//System.out.println("i = " + i);
		label = ((Integer)charArray[i]).intValue();
//DLC
//System.out.println("vertical chars i = " + i + " label = " + label);
//endDLC
		reg = allRegions[label];
		charArray[i] = reg;}
	    Arrays.sort(charArray,vC);
	    PointPixel upperleft;
	    PointPixel lowerright;
	    StringBuffer s = new StringBuffer();
	    for (i = 0; i < charArray.length; i++) {
		     reg = (Region)charArray[i];
		     upperleft = reg.getUpperLeft();
		     lowerright = reg.getLowerRight();
		     int xe = upperleft.getRow();
		     int xb = lowerright.getRow();
		     int yb = upperleft.getColumn();
		     int ye = lowerright.getColumn();
		     int[][] charImage = new int[ye - yb + 1][xb - xe + 1];
//System.out.println("yb ye xb xe: " + yb + " " + ye + " " + xb + " " + xe);
		     int y;
		     int x;
		     for (y=yb;y<=ye;y++)
			 for (x=xb;x>=xe;x--)
			     charImage[y - yb][xb - x] =
				 (inputLabelImage[x][y]==reg.getRegion()) ? 0 : 255;
		     //BWImageG.save("chr"+chrcnt,charImage,ye-yb+1,xb-xe+1);
		     chrcnt++;
		     String chr = simpleOCR.decideChar(charImage);
                     if (chr != "") {
//System.out.println("vertical " + chr);
                       String tempFont = chr.substring(0,4);
                       if (localFont.equals("")) localFont = tempFont;
                       chr =  chr.substring(4);
		     //System.out.println("chr = " + (char)chr);
                     }
                     else {return "";}

                     if (chr.equals(".")) {
// DLC omit dot if left of axis
                        if (yb < cb) /* skip this char */ ;
                        else {
			 for (j=0;j<previousCharIndex;j++) {
			     s.append(previousChar[j]);}
			 s.append(chr);
			 previousCharIndex = 0;}}
		     else if (chr.equals("o")) {
			 if (previousCharIndex == 0) {
			     previousChar[0] = chr;
			     previousCharPosition[0] = ye;
			     previousCharIndex = 1;}
			 else if (previousCharIndex==1) {
			     if (previousChar[0].equals("/")
				 &&
			         Math.abs(previousCharPosition[0]-ye) <= d) {
				 previousChar[1] = chr;
				 previousCharPosition[1] = ye;
				 previousCharIndex = 2;}
			     else {
				 s.append(previousChar[0]);
				 previousChar[0] = chr;
				 previousCharPosition[0] = ye;}}
			 else { //previousCharIndex = 2 and array holds o,/
                             s.append("%");
			     previousCharIndex = 0;}}
		     else if (chr.equals("/")) {
			 if (previousCharIndex == 0) {
			     previousChar[0] = chr;
			     previousCharPosition[0] = ye;
			     previousCharIndex = 1;}
			 else if (previousCharIndex==1) {
			     if (previousChar[0].equals("o")
				 &&
			         Math.abs(previousCharPosition[0]-ye) <= d) {
				 previousChar[1] = chr;
				 previousCharPosition[1] = ye;
				 previousCharIndex = 2;}
			     else {
				 s.append(previousChar[0]);
				 previousChar[0] = chr;
				 previousCharPosition[0] = ye;}}
			 else { //previousCharIndex==2, shouldn't happen
			     s.append(previousChar[0]);
			     s.append(previousChar[1]);
			     previousChar[0] = chr;
			     previousCharPosition[0] = ye;
			     previousCharIndex = 1;}}
		     else {
			 for (j=0;j<previousCharIndex;j++) {
			     s.append(previousChar[j]);}
			 s.append(chr);
			 previousCharIndex = 0;}}
	    for (i=0;i<previousCharIndex;i++) {
	        s.append(previousChar[i]);}
	    return localFont +  s.toString();
	    }

    public String getText() {
	return text;}

    public void setText(String str) {
        text = str;}

    public void setIsAttached(boolean b) {
	isAttached = b;}

    public boolean getIsAttached() {return isAttached;}

    // Remember, row numbers increase in the downward direction

    public boolean justAbove(Rectangle aBar) {
	if (lowerRight.getRow() <= aBar.getUpperLeft().getRow() &&
	    lowerRight.getRow() >= aBar.getUpperLeft().getRow() - 3*height &&
            upperLeft.getColumn() < aBar.getLowerRight().getColumn() &&
	    lowerRight.getColumn() > aBar.getUpperLeft().getColumn())
	    return true;
	else return false;}

    public boolean justBelow(Rectangle aBar) {
	if (aBar.getLowerRight().getRow() <= upperLeft.getRow() &&
//DLC
//was using a factor of 3 in place of 4 for Amex example 
	    aBar.getLowerRight().getRow() >= upperLeft.getRow() - 3*height &&
            aBar.getUpperLeft().getColumn() < lowerRight.getColumn() &&
	    aBar.getLowerRight().getColumn() > upperLeft.getColumn())
	    return true;
	else return false;}

    public boolean justRightOf(Rectangle aBar) {
	if (upperLeft.getColumn() >= aBar.getLowerRight().getColumn() &&
	    upperLeft.getColumn() <= aBar.getLowerRight().getColumn() + 3*height &&
	    lowerRight.getRow() > aBar.getUpperLeft().getRow() &&
	    upperLeft.getRow() < aBar.getLowerRight().getRow())
	    return true;
	else return false;}

    public boolean justLeftOf(Rectangle aBar) {
	if (aBar.getUpperLeft().getColumn() >= lowerRight.getColumn() &&
	    //aBar.getUpperLeft().getColumn() <= lowerRight.getColumn() + 3*height &&
	    aBar.getLowerRight().getRow() > upperLeft.getRow() &&
	    aBar.getUpperLeft().getRow() < lowerRight.getRow())
	    return true;
	else return false;}

    public boolean insideOf(Rectangle aBar) {
        if (aBar.getUpperLeft().getColumn() < upperLeft.getColumn() &&
            lowerRight.getColumn() <  aBar.getLowerRight().getColumn() &&
            aBar.getUpperLeft().getRow() < upperLeft.getRow() &&
            lowerRight.getRow() < aBar.getLowerRight().getRow())
            return true;
        else return false;}

    // Remember, row numbers increase in the downward direction

    public boolean justAbove(PointPixel aPoint) {
	if (lowerRight.getRow() <= aPoint.getRow() &&
	    lowerRight.getRow() >= aPoint.getRow() - 3*height &&
            upperLeft.getColumn() < aPoint.getColumn() &&
	    lowerRight.getColumn() > aPoint.getColumn())
	    return true;
	else return false;}

// being near needs to be more generous than this

    public boolean justBelow(PointPixel aPoint, Axis hAxis) {
//System.out.println(hAxis.getLowerRight().getRow() + " " + upperLeft.getRow());
//System.out.println(height + " " + aPoint.getColumn() + " " + upperLeft.getColumn());
	if (hAxis.getLowerRight().getRow() <= upperLeft.getRow() &&
                                  // 1 used to be 4
	    hAxis.getLowerRight().getRow() >= upperLeft.getRow() - 1*height &&
            aPoint.getColumn() < lowerRight.getColumn() + 3 &&
               // 5 used to be 3; this is a kluge because ' won't join 03 etc
               // in L-156bCO because it becomes a rotated -
	    aPoint.getColumn() > upperLeft.getColumn() - 5)
	    return true;
	else return false;}

    public boolean justLeftOf(PointPixel aPoint) {
	if (aPoint.getColumn() >= lowerRight.getColumn() &&
	    aPoint.getColumn() <= lowerRight.getColumn() + 5*height &&
	    aPoint.getRow() > upperLeft.getRow() &&
	    aPoint.getRow() < lowerRight.getRow())
	    return true;
	else return false;}

    public boolean justLeftOf(int col) {
        if (col >= lowerRight.getColumn() &&
            col <= lowerRight.getColumn() + 5*height)
           return true;
        else return false;}

/*
* get the stored font info
*/

    public String getFont() {
      return font;}

  public int getFontSize() {return Integer.parseInt(font.substring(2));}

  public char getFontType() {return font.charAt(0);}

  public boolean isBold() {
      if (font.charAt(1) == 'B') return true;
      else return false;}

  public static int distanceBetween(Word aWord1, Word aWord2) {
    int dc,dr,c1,c2,r1,r2;
    dc = dr = 1000; //arbitrary default distance
//System.out.println("word1 " + aWord1.getText() + " word2 " + aWord2.getText());
//System.out.println("corners" + aWord1.getUpperLeft() + " " + aWord1.getLowerRight() + " " + aWord2.getUpperLeft() + " " + aWord2.getLowerRight());
    c1 = aWord1.getLowerRight().getColumn();
    c2 = aWord2.getUpperLeft().getColumn();
    if (c1 < c2) dc = c2 - c1 - 1;
    c2 = aWord2.getLowerRight().getColumn();
    c1 = aWord1.getUpperLeft().getColumn();
    if (c2 < c1 && c1 - c2 - 1 < dc) dc = c1 - c2 - 1;
    r1 = aWord1.getLowerRight().getRow();
    r2 = aWord2.getUpperLeft().getRow();
    if (r1 < r2) dr = r2 - r1 - 1;
    r2 = aWord2.getLowerRight().getRow();
    r1 = aWord1.getUpperLeft().getRow();
    if (r2 < r1 && r1 - r2 -1 < dr) dr = r1 - r2 - 1;
//System.out.println("dc dr " + dc + " " + dr);
    if (dc < dr) return dc;
    else return dr;}

  public TextPiece getInTextPiece() {
    return inTextPiece;}

  public void setInTextPiece(TextPiece ptl) {
    inTextPiece = ptl;}

    public boolean isTriangle() {
        return text.equals("[^]") || text.equals("[>]");
    }

    public void setNearestBar(LinkedList lst,
                              int barOrientation,
                              int barLocation,
                              int VERTICAL) {
//System.out.println("bo bl vert " + barOrientation + " " + barLocation + " " + VERTICAL);
//System.out.println("row " + upperLeft.getRow());
        if (barOrientation == VERTICAL 
            && upperLeft.getRow() > barLocation
            && upperLeft.getRow() < barLocation + 20) {
            int halfWidth = -1;
            ListIterator lIter = lst.listIterator();
            halfWidth = ((Rectangle)lIter.next()).getValue().getColumn()
                         - ((Rectangle)lIter.next()).getValue().getColumn();
            halfWidth = -halfWidth / 2;
//System.out.println("halfWidth " + halfWidth);
            int thisCol = upperLeft.getColumn();
//System.out.println("thisCol " + thisCol);
            if (thisCol > ((Rectangle)lst.getFirst()).getValue().getColumn() - halfWidth) {
                lIter = lst.listIterator();
                while (lIter.hasNext()) {
                    Rectangle aBar = (Rectangle)lIter.next();
//System.out.println("bar col " + aBar.getValue().getColumn());
                    if (thisCol < aBar.getValue().getColumn() + halfWidth) {
                        nearestBar = aBar;
                        return;
                    }
                }
            }
        }
    }

    public Rectangle getNearestBar() {
        return nearestBar;
    }
            
    
}

import java.awt.Point;
//import java.util.LinkedList;
//import java.util.ListIterator;
//import java.util.Hashtable;
//import java.util.Vector;
import java.util.*;
import java.io.*;
import java.lang.Math;
import java.text.*;

/**
 * A class to store all the graph objects and determine what kind of graph 
 * the given image represents.
 * 
 * @author Chart Reading project
 * @version 1.0
 */
public class Graph {

	public final int UNKNOWNCHART = 0;
	public final int BARCHART = 1;
	public final int LINECHART = 2;
	public final int PIECHART = 3;
	public final int HORIZONTAL = 4;
	public final int VERTICAL = 5;

	private int imageHeight;
	private int imageWidth;

	private Axis hAxis;
	private Axis vAxis;
	private LinkedList rectangles;
	private LinkedList wedges;
	private LinkedList connectedLines;
	private LinkedList gridlines;
	private Hashtable words;

	private PointPixel origin;
	private int noOfBars;
        private int noOfLegends;
	private int noOfDataLines;
	private int chartType;
	private LinkedList dataValuesList;
	private LinkedList dataRectanglesList;
        private LinkedList legendsList;
        private Object[] HorizontalTickPoints;
        private Object[] VerticalTickPoints;
        private LinkedList HorizontalTextPieces;
        private LinkedList VerticalTextPieces;
        private LinkedList HorizontalTextBlocks;
        private LinkedList VerticalTextBlocks;
        private LinkedList Caption;
        private LinkedList Descriptions;
        private LinkedList TextInGraphic;
        private LinkedList TextUnderGraphic;
        private LinkedList XTitle;
        private LinkedList YTitle;
        private int barOrientation;
        private int barBaseLocation;
        private boolean ticksHaveValues = false;
        private boolean hTicksHaveValues = false;
        private boolean vTicksHaveValues = false;
        private LinkedList pointLabelList;

	/**
	 * Constructor.
 	 * 
	 * @param h The number of rows in the image
	 * @param w The number of columns in the image
 	 */
	public Graph(int h, int w) {
		imageHeight = h;
		imageWidth = w;
		hAxis = new Axis(Axis.HORIZONTAL_AXIS);
		vAxis = new Axis(Axis.VERTICAL_AXIS);
		rectangles = new LinkedList();
		wedges = new LinkedList();
		connectedLines = new LinkedList();
		gridlines = new LinkedList();
		noOfBars = 0;
                noOfLegends = 0;
		noOfDataLines = 0;
		chartType = UNKNOWNCHART;
		dataValuesList = new LinkedList();
		dataRectanglesList = new LinkedList();
                legendsList = new LinkedList();
                pointLabelList = new LinkedList();
	}

	/**
	 * Adds an axis to the <code>Graph</code> object.
 	 * 
	 * @param a An axis to be added
 	 */
	public void addAxes(Axis a, BWImageG image) {
System.out.println("addins axis "+a);
System.out.println("ulr= "+a.getUpperLeft().getRow());
		//if (a.getSize() > 0 && a.getOrientation() == Axis.HORIZONTAL_AXIS) {
			//hAxis = a;
		//}
		//else if (a.getSize() > 0 && a.getOrientation() == Axis.VERTICAL_AXIS) {
			//vAxis = a;
		//}
		if (a.getOrientation() == Axis.HORIZONTAL_AXIS) {
                  if (a.getSize() == 0) {  // look for virtual horizontal axis
//System.out.println("SORT THE RECTANGLES");
                    Object[] rectangleArray;
                    rectangleArray = rectangles.toArray();
                    BarRowCompare brc = new BarRowCompare();
                    Arrays.sort(rectangleArray,brc);
                    int barsBegin, barsEnd;
                    LinkedList baseBars = new LinkedList();
                    barsEnd = rectangleArray.length - 1;
                    for (barsBegin = barsEnd; barsBegin >= 0
                      && ((Rectangle)rectangleArray[barsEnd]).getLowerRight().getRow()
                          == ((Rectangle)rectangleArray[barsBegin]).getLowerRight().getRow();barsBegin--) {baseBars.add(rectangleArray[barsBegin]);}
                    if (barsEnd - 1 > barsBegin) {
//System.out.println("found virtual axis");
                      Object[] baseBarsArray = baseBars.toArray();
                      BarColumnCompare bcc = new BarColumnCompare();
                      Arrays.sort(baseBarsArray,bcc);
                         // create horizontal axis along bases of base bars
                      a.setBeginPoint(new PointPixel(
                         ((Rectangle)baseBarsArray[0]).getLowerRight().getRow(),
                         ((Rectangle)baseBarsArray[0]).getUpperLeft().getColumn()));
                      a.setEndPoint(new PointPixel(
                         ((Rectangle)baseBarsArray[baseBarsArray.length - 1]).getLowerRight().getRow(),
                         ((Rectangle)baseBarsArray[baseBarsArray.length - 1]).getLowerRight().getColumn()));
                      a.setUpperLeft(a.getBeginPoint());
                      a.setLowerRight(a.getEndPoint());
                      a.setLength();
                    }
                    else { // look for a measurement axis
                           // find width needed to span all rectangles,
                           // start axis a little below the bottom rectangle
//System.out.println("LOOKING FOR MEASUREMENT AXIS");
                      int i,leftCol,rightCol;
                      leftCol = ((Rectangle)rectangleArray[0]).getUpperLeft().getColumn();
                      rightCol = ((Rectangle)rectangleArray[0]).getLowerRight().getColumn();
                      for(i = 1;i<rectangleArray.length;i++) {
                        int x = ((Rectangle)rectangleArray[i]).getUpperLeft().getColumn();
                        if (x < leftCol) leftCol = x;
                        x = ((Rectangle)rectangleArray[i]).getLowerRight().getColumn();
                        if (x > rightCol)  rightCol = x;}
//System.out.println("left = " + leftCol + " right = " + rightCol);
                      a.setBeginPoint(new PointPixel(
                         ((Rectangle)rectangleArray[rectangleArray.length-1]).getLowerRight().getRow() + 3,
                         leftCol));
                      a.setEndPoint(new PointPixel(
                         ((Rectangle)rectangleArray[rectangleArray.length-1]).getLowerRight().getRow() + 3,
                         rightCol));
                      a.setUpperLeft(a.getBeginPoint());
                      a.setLowerRight(a.getEndPoint());
                      a.setLength();
                      a.setTickList(image.getVirtualTicks(a.getBeginPoint(),
                                                          a.getEndPoint(),
                                                          words));
                    }}
                  hAxis = a;
		}
		if (a.getOrientation() == Axis.VERTICAL_AXIS) {
//System.out.println("tick " + a.getTickList().size());
                  if (a.getSize() == 0) {  // look for virtual vertical axis
                    Object[] rectangleArray;
System.out.println("num data Rects " + dataRectanglesList.size());
                    rectangleArray = dataRectanglesList.toArray();
                    BarColumnCompare bcc = new BarColumnCompare();
                    Arrays.sort(rectangleArray,bcc);
                    int barsBegin, barsEnd;
                    LinkedList baseBars = new LinkedList();
                    barsBegin = 0;
                    for (barsEnd = barsBegin; barsEnd < rectangleArray.length
                      && ((Rectangle)rectangleArray[barsEnd]).getUpperLeft().getColumn()
                          == ((Rectangle)rectangleArray[barsBegin]).getUpperLeft().getColumn();barsEnd++) {baseBars.add(rectangleArray[barsEnd]);}
                    if (barsEnd - 1 > barsBegin) {
//System.out.println("found virtual axis");
                      Object[] baseBarsArray = baseBars.toArray();
                      BarRowCompare brc = new BarRowCompare();
                      Arrays.sort(baseBarsArray,brc);
                         // create vertical axis along bases of base bars
                      a.setBeginPoint(new PointPixel(
                         ((Rectangle)baseBarsArray[0]).getUpperLeft().getRow(),
                         ((Rectangle)baseBarsArray[0]).getUpperLeft().getColumn()));
                      a.setEndPoint(new PointPixel(
                         ((Rectangle)baseBarsArray[baseBarsArray.length - 1]).getLowerRight().getRow(),
                         ((Rectangle)baseBarsArray[baseBarsArray.length - 1]).getUpperLeft().getColumn()));
                      a.setUpperLeft(a.getBeginPoint());
                      a.setLowerRight(a.getEndPoint());
                      a.setLength();
                    }
                    else { // look for a measurement axis
                           // find width needed to span all rectangles,
                           // start axis a little before the first rectangle
//System.out.println("LOOKING FOR Vertical MEASUREMENT AXIS");
                      int i,firstRow,lastRow;
                      BarBaseColumnCompare  bbcc = new BarBaseColumnCompare();
                      Arrays.sort(rectangleArray,bbcc);

                    baseBars = new LinkedList();
                    barsEnd = rectangleArray.length - 1;
                    for (barsBegin = barsEnd; barsBegin >= 0
                      && ((Rectangle)rectangleArray[barsEnd]).getLowerRight().getRow()
                          == ((Rectangle)rectangleArray[barsBegin]).getLowerRight().getRow();barsBegin--) {baseBars.add(rectangleArray[barsBegin]);}
                    if (barsEnd - 1 > barsBegin) {
                      Object[] baseBarsArray = baseBars.toArray();


                      firstRow = ((Rectangle)baseBarsArray[0]).getUpperLeft().getRow();
                      lastRow = ((Rectangle)baseBarsArray[0]).getLowerRight().getRow();
                      for(i = 0;i<baseBarsArray.length;i++) {
System.out.println("rect. " + i + " " + ((Rectangle)baseBarsArray[i]).getUpperLeft()
+ "  " + ((Rectangle)baseBarsArray[i]).getLowerRight());
                        int y = ((Rectangle)baseBarsArray[i]).getUpperLeft().getRow();
                        if (y < firstRow) firstRow = y;
                        y = ((Rectangle)baseBarsArray[i]).getLowerRight().getRow();
                        if (y > lastRow)  lastRow = y;}
System.out.println("first = " + firstRow + " last = " + lastRow);
                      int colOffset = image.getClearColumn(firstRow,lastRow -5,
                         ((Rectangle)rectangleArray[0]).getUpperLeft().getColumn() - 3);

                      a.setBeginPoint(new PointPixel(firstRow, colOffset));
                      a.setEndPoint(new PointPixel(lastRow, colOffset));
                      a.setUpperLeft(a.getBeginPoint());
                      a.setLowerRight(a.getEndPoint());
System.out.println("begin " + a.getBeginPoint());
                      a.setLength();
                      a.setTickList(image.getVirtualTicks(a.getBeginPoint(),
                                                          a.getEndPoint(),
                                                          words));
                    }}
                  }
                  else if (barOrientation == VERTICAL
                           && a.getTickList().size() <= 2) {
                      a.setTickList(image.getVirtualTicks(a.getBeginPoint(),
                                                          a.getEndPoint(),
                                                          words));
//System.out.println("now tick " + a.getTickList().size());
                  }
	          vAxis = a;
System.out.println("b= "+vAxis.getUpperLeft().getRow());
System.out.println("e= "+vAxis.getLowerRight().getRow());
                  if (vAxis.getTickList() == null) {
                      vAxis.setTickList(image.getVirtualTicks(vAxis.getBeginPoint(),
                                                              vAxis.getEndPoint(),
                                                              words));
                  }
		}
		findOrigin();
	}

	/**
	 * Adds gridlines to the <code>Graph</code> object.
 	 * 
	 * @param g The linked list of gridlines (as <code>VirtualLine</code>s) to be added
 	 */
	public void addGridlines(LinkedList g) {
		gridlines = g;
	}	


        /**
         *Auxiliary function to addRectangle.
         *Determines whether a rectangle is tightly nested in one of the
         *other rectangles.
         *
         * @param aRec the rectangle being tested
         * @param r linked list of all rectangles
         */

         public Boolean nestedIn(Rectangle aRec, LinkedList r) {
            PointPixel upperLeft = aRec.getUpperLeft();
            int ulrow = upperLeft.getRow()-1;
            int ulcol = upperLeft.getColumn()-1;  
            PointPixel lowerRight = aRec.getLowerRight();
            int lrrow = lowerRight.getRow()+1;
            int lrcol = lowerRight.getColumn()+1;
            ListIterator lItr = r.listIterator(0);
            while (lItr.hasNext()) {
                Rectangle aRectangle2 = (Rectangle)lItr.next();
                PointPixel upperLeft2 = aRectangle2.getUpperLeft();
                PointPixel lowerRight2 = aRectangle2.getLowerRight();
                if (upperLeft2.getRow() == ulrow &
                    upperLeft2.getColumn() == ulcol &
                    lowerRight2.getRow() == lrrow &
                    lowerRight2.getColumn() == lrcol)
                   return true;}
            return false;}

	/**
	 * Adds rectangles to the <code>Graph</code> object.
	 * Finds which, if any, of the rectangles are bars of a bar chart.
         * Filters out tightly nested rectangles, since only the unnested
         * rectangles be considered.
 	 * 
	 * @param r The linked list of rectangles (as <code>Rectangle</code>s) to be added
 	 */
	public void addRectangles(LinkedList r) {
                rectangles = new LinkedList();
                ListIterator lItr = r.listIterator(0);
                while(lItr.hasNext()) {
                    Rectangle aRectangle = (Rectangle)lItr.next();
                    if (!nestedIn(aRectangle,r)) rectangles.add(aRectangle);}
		//rectangles = r;
		//findBars();
	}

	/**
	 * Adds wedges to the <code>Graph</code> object.
 	 * 
	 * @param w The linked list of wedges (as <code>Wedge</code>s) to be added
 	 */
	public void addWedges(LinkedList w) {
		wedges = w;
	}

	/**
	 * Adds connected lines to the <code>Graph</code> object.
	 * Finds which, if any, of the connected lines form the data of a line chart.
 	 * 
	 * @param w The linked list of connected lines (as <code>ConnectedLine</code>s) to be added
 	 */
	public void addConnectedLines(LinkedList w) {
		connectedLines = w;
		findDataLines();
	}

	/**
	 * Adds words to the <code>Graph</code> object.
	 * Checks the title words, determined earlier, 
	 * to see if they lie outside the axes area or above the wedges in
	 * the case of a pie chart. If not, those words are no
	 * longer title words.
 	 * 
	 * @param w The hash table of words (as <code>Word</code>s) to be added
 	 */
	public void addWords(Hashtable w) {
		words = w;
		findTitleWords();
	}


	/**
	 * Finds the type of chart; bar, pie or line chart.
 	 * 
	 * @param none
 	 */
	public void findChartType() {
System.out.println("noOfDataLines = " + noOfDataLines + "\n");
		//if (hAxis.getSize() <= 0 && vAxis.getSize() <= 0) { //no axes
		if (getNoOfWedges() > 0) {
//System.out.println("found some wedges\n");
			if (getNoOfWedges() > 0) { //there are wedges
				//Check if the total area of the wedges cover most of or at least half of the area of the whole chart
				double totalWedgeArea = 0;
				ListIterator lItr = wedges.listIterator(0);
				while (lItr.hasNext()) {
					totalWedgeArea += ((Wedge)lItr.next()).getArea();
				}
				int imageArea = imageHeight*imageWidth;
				//System.out.println("Image area is "+imageArea+". Total wedge area is "+totalWedgeArea);
				if (totalWedgeArea > 0.2*imageArea) {
					chartType = PIECHART;
					//System.out.println("This is a pie chart");
				}
			}
		}
		else { //There are axes
			if (noOfBars > 0) { //there are bars
				chartType = BARCHART;
                                System.out.println("noOfBars = "+noOfBars);
				System.out.println("This is a BAR chart");
			}
			else if (noOfDataLines > 0) { //there are no bars, but there are data lines
				//Check if the total bounding box of the connected lines covers most of or at least half of the area of the axes
				int height, width;
				double totalLineArea = 0;
				ConnectedLine aLine;
				ListIterator lItr = connectedLines.listIterator(0);
				while (lItr.hasNext()) {
					aLine = (ConnectedLine)lItr.next();
					if (aLine.getIsDataLine()) {
						height = aLine.getBoxHeight();
						width = aLine.getBoxWidth();
						totalLineArea += height*width;
					}
				}
				int imageArea = imageHeight*imageWidth;
				int axesArea = hAxis.getLength()*vAxis.getLength();
				System.out.println("Image area is "+imageArea+". Axes area is "+axesArea+". Total line area is "+totalLineArea);
				//if (totalLineArea > 0.3*axesArea) {
				if (totalLineArea > 0.03*axesArea) {
					chartType = LINECHART;
					System.out.println("This is a line chart");
				}
			}
		}
	}

	/**
	 * Finds if any of the rectangles of the <code>Graph</code> object
	 * is a bar.
	 * Calls methods on a <code>BarFinder</code> object.
 	 * 
	 * @param none
 	 */
	public void findBars() {
		if (hAxis.getSize() > 0 || vAxis.getSize() > 0) {
			boolean verticalBar, horizontalBar;
			Rectangle aRectangle;
			BarFinder aFinder = new BarFinder(rectangles);
			ListIterator lItr = rectangles.listIterator(0);
			while (lItr.hasNext()) {
				aRectangle = (Rectangle)lItr.next();
				if (hAxis.getSize() > 0) {
					verticalBar = aFinder.isBar(aRectangle, hAxis);
					if (verticalBar) {
						aRectangle.setIsVerticalBar(true);
						PointPixel value = aFinder.getBarValue(aRectangle, origin);
						aRectangle.setValue(value);
						noOfBars++;
System.out.println("findBars found vertical bar");
					}
				}
				if (vAxis.getSize() > 0) {
					horizontalBar = aFinder.isBar(aRectangle, vAxis);
					if (horizontalBar) {
						aRectangle.setIsHorizontalBar(true);
						PointPixel value = aFinder.getBarValue(aRectangle, origin);
						aRectangle.setValue(value);
						noOfBars++;
System.out.println("findBars found horizontal bar");
					}
				}
			}
		}
	}


/*
*  This procedure defines bars in a way that is independent of the
*  existence of axes.  Bars are rectangles that have a common value
*  for their beginning or end row or column.  Whether this common
*  value is a row or column determines the direction of the bars
*  as a group; the shape of a bar does not determine direction.
*
*  As a side effect, info about the orientation of the bars and
*  the location of the common base value are stored in the graph
*  object for future reference.
*/

public void findBars2() {
  int[] rowHistogram = new int[imageHeight];
  int[] colHistogram = new int[imageWidth];
  Arrays.fill(rowHistogram,0);
  Arrays.fill(colHistogram,0);
  Rectangle aRectangle;
  ListIterator lItr = rectangles.listIterator(0);
  BarFinder aFinder = new BarFinder(rectangles);
  while (lItr.hasNext()) {
    aRectangle = (Rectangle)lItr.next();
    if (aRectangle.getHeight() * aRectangle.getWidth() > 20) {
        rowHistogram[aRectangle.getUpperLeft().getRow()]++;
        rowHistogram[aRectangle.getLowerRight().getRow()]++;
        colHistogram[aRectangle.getUpperLeft().getColumn()]++;
        colHistogram[aRectangle.getLowerRight().getColumn()]++;}}
  int r,c,rCount,cCount,rIndex,cIndex;
  rCount = cCount = 0;
  rIndex = cIndex = -1;
  PointPixel value;
  for(r = 0; r < imageHeight; r++)
    if (rowHistogram[r] > rCount) {
    rIndex = r;
    rCount = rowHistogram[r];}
  for(c = 0; c < imageWidth; c++)
    if (colHistogram[c] > cCount) {
      cIndex = c;
      cCount = colHistogram[c];}
   PointPixel origin = new PointPixel(barBaseLocation,barBaseLocation);
System.out.println("in findBars2 " + rIndex + " " + rCount + " " + cIndex + " " + cCount);
  if (rCount > cCount) {
    barOrientation = VERTICAL;
    barBaseLocation = rIndex;
    noOfBars = rCount;
System.out.println("findBars2 found bars");
    lItr = rectangles.listIterator(0);
    while (lItr.hasNext()) {
      aRectangle = (Rectangle)lItr.next();
      if (aRectangle.getLowerRight().getRow() == barBaseLocation ||
          aRectangle.getUpperLeft().getRow() == barBaseLocation) {
        aRectangle.setIsVerticalBar(true);
        value = aFinder.getBarValue(aRectangle, origin);
        aRectangle.setValue(value);}
  }}
  else  if (cCount > rCount){
    barOrientation = HORIZONTAL;
    barBaseLocation = cIndex;
    noOfBars = cCount;
System.out.println("findBars2 found bars here");
    lItr = rectangles.listIterator(0);
    while (lItr.hasNext()) {
      aRectangle = (Rectangle)lItr.next();
      if (aRectangle.getLowerRight().getColumn() == barBaseLocation ||
          aRectangle.getUpperLeft().getColumn() == barBaseLocation) {
        aRectangle.setIsHorizontalBar(true);
        value = aFinder.getBarValue(aRectangle, origin);
        aRectangle.setValue(value);}
      else {
System.out.println("found a legend rectangle");
           aRectangle.setIsLegend(true);
           aRectangle.setValue(aFinder.getBarValue(aRectangle, origin));
      }
  }}
  else noOfBars = 0;
}


    void annotateBars() {
	Rectangle aBar;
	Word aWord;
//DLC
//System.out.println("in annotateBars");
	ListIterator itrB = dataRectanglesList.listIterator();
	while (itrB.hasNext()) {
	    aBar = (Rectangle)itrB.next();
	    aBar.initAnnotationWords();
	    aBar.initLabelWords();
//DLC
//System.out.println("\nstarting a bar");
	    Iterator itrW = words.values().iterator();
	    while (itrW.hasNext()) {
		aWord = (Word) itrW.next();
//DLC
//System.out.println("considering " + aWord.getText());
                if (aWord.getIsTitle()) continue;
		if (aBar.getIsVerticalBar()) {
		    if ( isNumber(aWord.getText())
                         && (aWord.justAbove(aBar) || aWord.insideOf(aBar))) {
			aBar.addToAnnotationWords(aWord);
			aWord.setIsAttached(true);}
		    if (aWord.justBelow(aBar)) {
//DLC
//System.out.println(aWord.getText());
			aBar.addToLabelWords(aWord);
			aWord.setIsAttached(true);}}
		if (aBar.getIsHorizontalBar() || aBar.getIsLegend()) {
		    if (isNumber(aWord.getText())
                        && (aWord.justRightOf(aBar) || aWord.insideOf(aBar))) {
			aBar.addToAnnotationWords(aWord);
			aWord.setIsAttached(true);}
		    if (aWord.justLeftOf(aBar) || aWord.justAbove(aBar)) {
			aBar.addToLabelWords(aWord);
			aWord.setIsAttached(true);}}}}


//DLC
//System.out.println("finished annotateBars");
    }

    void annotateBars2() {
	Rectangle aBar;
	//Word aWord;
        TextBlock aBlock;
//DLC
//System.out.println("in annotateBars2");
	ListIterator itrB = dataRectanglesList.listIterator();
//System.out.println("noOfLegends = "+noOfLegends);
//System.out.println("num bars = " + dataRectanglesList.size());
	while (itrB.hasNext()) {
	    aBar = (Rectangle)itrB.next();
	    aBar.initAnnotationBlocks();
	    aBar.initLabelBlocks();
//DLC
//System.out.println("\nstarting a bar");
	    ListIterator itrT = HorizontalTextBlocks.listIterator();
	    while (itrT.hasNext()) {
		aBlock = (TextBlock) itrT.next();
//DLC
//System.out.println("considering " + aBlock.getText());
                if (aBlock.getIsAttached()) continue;
		if (aBar.getIsVerticalBar()) {
		    if (isNumber(aBlock.getText())
                        && (aBlock.justAbove(aBar) || aBlock.insideOf(aBar))) {
			aBar.addToAnnotationBlocks(aBlock);
			aBlock.setIsAttached(true);}
		    if (aBlock.justBelow(aBar)) {
//DLC
//System.out.println(aBlock.getText());
			aBar.addToLabelBlocks(aBlock);
			aBlock.setIsAttached(true);}}
		if (aBar.getIsHorizontalBar()) {
                    //for Burns; drop isNumber test; no change
                    if (true
		    //if (isNumber(aBlock.getText())
                        && (aBlock.justRightOf(aBar) || aBlock.insideOf(aBar))) {
			aBar.addToAnnotationBlocks(aBlock);
			aBlock.setIsAttached(true);}
		    if (aBlock.justLeftOf(aBar) ||
                        aBlock.justAbove(aBar) ||
                        (noOfLegends > 0 && aBlock.justLeftOfGroup(aBar))) {
			aBar.addToLabelBlocks(aBlock);
			aBlock.setIsAttached(true);}}
		if (aBar.getIsLegend()) {
                    //for Burns; drop isNumber test; Iraq attached but no print
                    if (true
		    //if (isNumber(aBlock.getText())
                        && (aBlock.justRightOf(aBar) || aBlock.insideOf(aBar))) {
			aBar.addToAnnotationBlocks(aBlock);
			aBlock.setIsAttached(true);}
		    if (aBlock.justLeftOf(aBar) || aBlock.justAbove(aBar)) {
			aBar.addToLabelBlocks(aBlock);
			aBlock.setIsAttached(true);}}
              }}

	itrB = dataRectanglesList.listIterator();
//System.out.println("again, num bars = " + dataRectanglesList.size());
	while (itrB.hasNext()) {
	    aBar = (Rectangle)itrB.next();
	    ListIterator itrT = VerticalTextBlocks.listIterator();
	    while (itrT.hasNext()) {
		aBlock = (TextBlock) itrT.next();
//DLC
//System.out.println("considering " + aBlock.getText());
                if (aBlock.getIsAttached()) continue;
		if (aBar.getIsVerticalBar()) {
		    if (aBlock.justAbove(aBar)) {
			aBar.addToAnnotationBlocks(aBlock);
			aBlock.setIsAttached(true);}
		    if (aBlock.justBelow(aBar) || aBlock.insideOf(aBar)) {
//DLC
//System.out.println(aBlock.getText());
			aBar.addToLabelBlocks(aBlock);
			aBlock.setIsAttached(true);}}
		if (aBar.getIsHorizontalBar()) {
		    if (aBlock.justRightOf(aBar) || aBlock.insideOf(aBar)) {
			aBar.addToAnnotationBlocks(aBlock);
			aBlock.setIsAttached(true);}
		    if (aBlock.justLeftOf(aBar) || aBlock.justAbove(aBar)) {
			aBar.addToLabelBlocks(aBlock);
			aBlock.setIsAttached(true);}}}}

//DLC
//System.out.println("finished annotateBars2");
    }

	/**
	 * Finds if any of the connected lines of the <code>Graph</code> object
	 * is a data line of a line chart. Calls methods on a <code>DataLineFinder</code>
	 * object.
 	 * 
	 * @param none
 	 */
	public void findDataLines() {
//System.out.println("in findDataLines\n");
		if (hAxis.getSize() > 0 || vAxis.getSize() > 0) {
			boolean dataLine;
			ConnectedLine aLine;
//System.out.println("getting a new DataLineFinder");
			DataLineFinder aFinder = new DataLineFinder(connectedLines);
			ListIterator lItr = connectedLines.listIterator(0);
			while (lItr.hasNext()) {
				aLine = (ConnectedLine)lItr.next();
				dataLine = aFinder.isDataLine(aLine, hAxis, vAxis);
//System.out.println("dataLine is " + dataLine + "\n");
				if (dataLine) {
//System.out.println("Ahah! dataLines IS true\n");
					aLine.setIsDataLine(true);
					noOfDataLines++;
				}
			}
		}
	}
	
	/**
	 * Finds the origin of the axes; the point where the two axes meet.
 	 * 
	 * @param none
 	 */
	private void findOrigin() {
		if (hAxis.getSize() > 0 && vAxis.getSize() > 0) { //both axes
			PointPixel hBeginPoint = hAxis.getBeginPoint();
			PointPixel hEndPoint = hAxis.getEndPoint();
			PointPixel vBeginPoint = vAxis.getBeginPoint();
			PointPixel vEndPoint = vAxis.getEndPoint();
			LineFitter aFitter = new LineFitter();
			origin = aFitter.getIntersectionPoint(hBeginPoint, hEndPoint, vBeginPoint, vEndPoint);
			//System.out.println("Origin is "+origin);
		}
		else if (hAxis.getSize() > 0 && vAxis.getSize() <= 0) {
			origin = hAxis.getBeginPoint();
		}
		else if (vAxis.getSize() > 0 && hAxis.getSize() <= 0) {
			origin = vAxis.getBeginPoint();
		}
		else {
			origin = new PointPixel(-1, -1);
		}
	}

	/**
	 * Checks the title words with respect to the axes or
	 * the wedges area.
 	 * 
	 * @param none
 	 */
	private void findTitleWords() {
		TitleFinder aFinder = new TitleFinder(words, imageHeight, imageWidth, 255);
		if (chartType == BARCHART || chartType == LINECHART) {
			if (hAxis.getSize() > 0 && vAxis.getSize() > 0) { //both axes are present
				aFinder.checkAxes(vAxis);
			}
		}
		else if (chartType == PIECHART) {
			aFinder.checkWedges(wedges);
		}
	}

	/**
	 * Finds the data values. For bar and line charts, the data 
	 * value is in the form of (row number, column number). 
	 * For pie charts, it is (color value, angle).
 	 * <p>
	 * To find data values of a line chart: <br>
	 * For each <code>connectedLine</code>, checks if it is a data line. 
	 * If yes, checks if it is a straight line, if not, tries to split it
	 * into straight lines.
	 * Records the begin and the end points of all the segments.
	 * Goes through the begin and end points, discards the duplicate ones.
	 * Keeps the others as the data points.
	 *
	 * @param none
 	 */
	public void getDataValues() {
		if (chartType == BARCHART) {
			int skip;
			int count = -1;
			PointPixel aValue, anotherValue;
			Rectangle aRectangle, anotherRectangle;
//System.out.println("in getDataValues num rectangles = " + rectangles.size());
			ListIterator lItr = rectangles.listIterator(0);
			while (lItr.hasNext()) {
				count++;
				skip = 0;
				aRectangle = (Rectangle)lItr.next();
//System.out.println("got a rectangle");
				if (aRectangle.getIsVerticalBar() ||
                                    aRectangle.getIsHorizontalBar() ||
                                    aRectangle.getIsLegend()) {
//System.out.println("is a bar");
//if (aRectangle.getIsLegend()) System.out.println("actually a legend");
					aValue = aRectangle.getValue();
//System.out.println("got value " + aValue);
					if (count < rectangles.size() - 1) {
						ListIterator lItr2 = rectangles.listIterator(count+1);
						while (lItr2.hasNext()) {
							anotherRectangle = (Rectangle)lItr2.next();
							if (anotherRectangle.getIsVerticalBar() || anotherRectangle.getIsHorizontalBar()) {
								anotherValue = anotherRectangle.getValue();
//System.out.println("another value "+anotherValue);
								if (((Math.abs(aValue.getColumn() - anotherValue.getColumn())) <= 1) && ((Math.abs(aValue.getRow() - anotherValue.getRow())) <= 1)) {
									//Same data value, skip one 
									skip = 1;
									break;
								}
							}
						}
					}
					if (skip == 0) {
//System.out.println("adding rectangle to dataRectanglesList");
					    dataValuesList.add(aValue);
					    dataRectanglesList.add(aRectangle);
					    aRectangle.setIsData(true);
					}
				}
			}
                        if (barOrientation == VERTICAL) {
                            BarColumnCompare bcc = new BarColumnCompare();
                            Collections.sort(dataRectanglesList,bcc);
                        }
		}
		else if (chartType == PIECHART) {
			int skip;
			int count = -1;
			double aValue, anotherValue;
			int aColor, anotherColor;
			Wedge aWedge, anotherWedge;
			PointPixel upperLeft, lowerRight, upperLeft2, lowerRight2; ListIterator lItr = wedges.listIterator(0);
			while (lItr.hasNext()) {
				count++;
				skip = 0;
				aWedge = (Wedge)lItr.next();
				upperLeft = aWedge.getUpperLeft();
				lowerRight = aWedge.getLowerRight();
				aValue = aWedge.getAngle();
				aColor = aWedge.getColor();
				if (count < wedges.size() - 1) {
					ListIterator lItr2 = wedges.listIterator(count+1);
					while (lItr2.hasNext()) {
						anotherWedge= (Wedge)lItr2.next();
						anotherValue = anotherWedge.getAngle();
						anotherColor = anotherWedge.getColor();
						upperLeft2 = anotherWedge.getUpperLeft();
						lowerRight2 = anotherWedge.getLowerRight();
						if ((((Math.abs(upperLeft.getColumn() - upperLeft2.getColumn())) <= 4) && 
						((Math.abs(upperLeft.getRow() - upperLeft2.getRow())) <= 4) && 
						((Math.abs(lowerRight.getColumn() - lowerRight2.getColumn())) <= 4) && 
						((Math.abs(lowerRight.getRow() - lowerRight2.getRow())) <= 4) || 
						(upperLeft2.getRow() >= upperLeft.getRow() &&
						upperLeft2.getColumn() >= upperLeft.getColumn() &&
						lowerRight2.getRow() <= lowerRight.getRow() &&
						lowerRight2.getColumn() <= lowerRight.getColumn())) &&
						((Math.abs(aValue - anotherValue)) <= 2)){
							//If the bounding box corners are very close to each other or 
							//if the first box covers the second one and
							//the angles are close to each other, then 
							//Same data value, skip one
							skip = 1;
							break;
						}
					}
				}
				if (skip == 0) {
					dataValuesList.add(new PointPixel(aColor, (int)aValue));
					aWedge.setIsData(true);
				}
			}
		}
		else if (chartType == LINECHART ) {
		/* For each connectedLine, check if it is a data line. 
		 * If yes, check if it is a straight line, if not, try to split it
		 * into straight lines.
		 * Record the begin and the end points of all the segments.
		 * Go through the begin and end points, discard the duplicate ones.
		 */
			int skipBeginPoint, skipEndPoint, count, labelNo, aPrimLabel;
			LinkedList primitiveList, pointsList, indexList;
			ConnectedLine aLine;
			Primitive aPrim;
			PointPixel aValue, aPrimBeginPoint, aPrimEndPoint;
			LineFitter aLineFitter = new LineFitter();
			ListIterator lItr = connectedLines.listIterator(0);
			while (lItr.hasNext()) {
				aLine = (ConnectedLine)lItr.next();
				if (aLine.getIsDataLine() == false) {
					continue;
				}
				primitiveList = aLine.getPrimitives();
				ListIterator lItr2 = primitiveList.listIterator(0);
				while (lItr2.hasNext()) {
					aPrim = (Primitive)lItr2.next();
					skipBeginPoint = 0;
					skipEndPoint = 0;
					//Check dataValuesList to see of the point or a very close point in the same region has been added previously. Add only if a close point does not already exist
					//In lineNJ, the arrow and the dashed line are also connected lines. How to distingish between the arrow, the dashed line and the real data value line?
					aPrimBeginPoint = aPrim.getBeginPoint();
					aPrimEndPoint = aPrim.getEndPoint();
					aPrimLabel = aPrim.getParent();
					ListIterator lItr3 = dataValuesList.listIterator(0);
					while (lItr3.hasNext()) {
						aValue = (PointPixel)lItr3.next();
						labelNo = aValue.getLabelNo();
						if (labelNo == aPrimLabel) {
							if ((Math.abs(aValue.getRow() - aPrimBeginPoint.getRow()) < 3) && (Math.abs(aValue.getColumn() - aPrimBeginPoint.getColumn()) < 3)) {
								skipBeginPoint = 1;
							}
							if ((Math.abs(aValue.getRow() - aPrimEndPoint.getRow()) < 3) && (Math.abs(aValue.getColumn() - aPrimEndPoint.getColumn()) < 3)) {
								skipEndPoint = 1;
							}
						}
					}
					if (skipBeginPoint == 0) {
						aPrimBeginPoint.setLabelNo(aPrimLabel);
						dataValuesList.add(aPrimBeginPoint);
					}
					if (skipEndPoint == 0) {
						aPrimEndPoint.setLabelNo(aPrimLabel);
						dataValuesList.add(aPrimEndPoint);
					}
					pointsList = aPrim.getAllPoints();
					indexList = new LinkedList();
					aLineFitter.splitSegment(pointsList, indexList);
					lItr3 = indexList.listIterator(0);
					while (lItr3.hasNext()) {
						aValue = (PointPixel)lItr3.next();
						aValue.setLabelNo(aPrimLabel);
						dataValuesList.add(aValue);
					}
				}
			}
		}
		//System.out.println("\nThere are "+dataValuesList.size()+" data values.");
		if (dataValuesList.size() > 0) {
			if (chartType == BARCHART || chartType == LINECHART) {
				//System.out.println("Values are listed as (row, column) with respect to the origin "+origin);
				//System.out.println("Values are listed as (row, column), origin is "+origin);
				ListIterator lItr = dataValuesList.listIterator(0);
				while (lItr.hasNext()) {
					PointPixel aValue = (PointPixel)lItr.next();
					//System.out.println(aValue);
				}
			}
			else if (chartType == PIECHART) {
				//System.out.println("Values are listed as (color, angle in degrees)");
				ListIterator lItr = dataValuesList.listIterator(0);
				while (lItr.hasNext()) {
					PointPixel aValue = (PointPixel)lItr.next();
					//System.out.print(aValue);
					double percentage = (double)aValue.getColumn()*100/360;
					//System.out.println(" "+Math.round(percentage)+"%");
				}
			}
		}
	}


	/**
	 * Creates the 2d array representation of an image showing the axes.
 	 * 
	 * @param none
 	 */
	public void showAxes() {
		AxesFinder anAxesFinder = new AxesFinder();
		int[][] axesImage = anAxesFinder.makeAxesImage(hAxis, vAxis);
	}

	/**
	 * Creates the 2d array representation of an image showing the rectangles.
 	 * 
	 * @param none
 	 */
	public void showRectangles() {
		RectangleFinder aRectangleFinder = new RectangleFinder();
		int[][] rectangleImage = aRectangleFinder.makeRectangleImage(rectangles);
	}

	/**
	 * Creates the 2d array representation of an image showing the wedges.
 	 * 
	 * @param none
 	 */
	public void showWedges() {
		WedgeFinder aWedgeFinder = new WedgeFinder();
		int[][] wedgeImage = aWedgeFinder.makeWedgeImage(wedges);
	}

	/**
	 * Creates the 2d array representation of an image showing the connected lines.
 	 * 
	 * @param none
 	 */
	public void showConnectedLines() {
		ConnectedLinesFinder aFinder = new ConnectedLinesFinder();
		int[][] connectedLinesImage = aFinder.makeConnectedLinesImage(connectedLines);
	}

	/**
	 * Creates the 2d array representation of an image showing the 
	 * chart title words.
 	 * 
	 * @param none
	 * @return The 2d array representation of an image showing the chart title 
 	 */
	public int[][] showTitleWords() {
		TitleFinder aFinder = new TitleFinder(words, imageHeight, imageWidth, 255);
		int[][] wordsImage = aFinder.getTitleImage();
		return wordsImage;
	}

	/**
	 * Creates the 2d array representation of an image showing the data values.
 	 * 
	 * @param none
	 * @return The 2d array representation of the data image.
 	 */
	public int[][] showDataValues() {
		int[][] dataImage = new int[imageHeight][imageWidth];
		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {
				dataImage[i][j] = 255;
			}
		}
		if (chartType == BARCHART || chartType == LINECHART) {
			ListIterator lItr = dataValuesList.listIterator(0);	
			PointPixel aPoint;
			while (lItr.hasNext()) {
				aPoint = (PointPixel)lItr.next();
				dataImage[aPoint.getRow()][aPoint.getColumn()] = 0;
			}
		}
		return dataImage;
	}


	/**
	 * Returns the rectangles.
 	 * 
	 * @param none
	 * @return The linked list of rectangles.
 	 */
	public LinkedList getRectangles() {
		return rectangles;
	}

	/**
	 * Returns the number of rectangles.
 	 * 
	 * @param none
	 * @return The number of rectangles.
 	 */
	public int getNoOfRectangles() {
		return rectangles.size();
	}

	/**
	 * Returns the number of wedges.
 	 * 
	 * @param none
	 * @return The number of wedges.
 	 */
	public int getNoOfWedges() {
		return wedges.size();
	}

	/**
	 * Returns the number of connected lines.
 	 * 
	 * @param none
	 * @return The number of connected lines.
 	 */
	public int getNoOfConnectedLines() {
		return connectedLines.size();
	}

	/**
	 * Returns the origin point where the axes meet.
 	 * 
	 * @param none
	 * @return The location of the origin of the axes
 	 */
	public PointPixel getOrigin() {
		return origin;
	}

	/**
	 * Writes the graph information to file.
 	 * 
	 * @param filename The name of the file the graph information is to be written to
 	 */
	public void writeGraph(String filename) {
		ListIterator lItr;
		try {
			BufferedOutputStream ostream = new BufferedOutputStream(new FileOutputStream(filename));
			ostream.write((dataValuesList.size()+"\n").getBytes()); 
			if (dataValuesList.size() > 0) {
				if (chartType == BARCHART || chartType == LINECHART) {
					//System.out.println("Values are listed as (row, column) with respect to the origin "+origin);
					if (chartType == BARCHART) {
						ostream.write(("B"+"\n").getBytes()); 
					}
					else {
						ostream.write(("L"+"\n").getBytes()); 
					}
					//message = (message + "Values are listed as (row, column), origin is "+origin+"\n");
					lItr = dataValuesList.listIterator(0);
					while (lItr.hasNext()) {
						PointPixel aValue = (PointPixel)lItr.next();
						ostream.write((aValue+"\n").getBytes()); 
					}
				}
				else if (chartType == PIECHART) {
					ostream.write(("P"+"\n").getBytes()); 
					//message = (message + "Values are listed as (color, angle in degrees)\n");
					lItr = dataValuesList.listIterator(0);
					while (lItr.hasNext()) {
						PointPixel aValue = (PointPixel)lItr.next();
						double percentage = (double)aValue.getColumn()*100/360;
						ostream.write((aValue+" "+Math.round(percentage)+"\n").getBytes()); 
					}
				}
			}	
			ostream.write(("\n").getBytes());

			ostream.write(("Horizontal axis "+hAxis.getUpperLeft()+" "+hAxis.getLowerRight()+" "+hAxis.getBeginPoint()+" "+hAxis.getEndPoint()+"\n").getBytes());
			ostream.write(("Vertical axis "+vAxis.getUpperLeft()+" "+vAxis.getLowerRight()+" "+vAxis.getBeginPoint()+" "+vAxis.getEndPoint()+"\n\n").getBytes());
			Rectangle aRectangle;
			lItr = rectangles.listIterator(0);
			while (lItr.hasNext()) {
				aRectangle = (Rectangle)lItr.next();
				ostream.write(("Rectangle "+aRectangle.getUpperLeft()+" "+aRectangle.getLowerRight()+" "+aRectangle.getColor()+" ").getBytes());
				if (aRectangle.getIsFilledArea()) {
					ostream.write(("F"+" ").getBytes());
				}
				else {
					ostream.write(("U"+" ").getBytes());
				}
				if (aRectangle.getIsData()) {
					ostream.write(("D"+" ").getBytes());
				}
				else {
					ostream.write(("R"+" ").getBytes());
				}
				if (aRectangle.getIsVerticalBar()) {
					ostream.write(("V"+" ").getBytes());
				}
				else if (aRectangle.getIsHorizontalBar()) {
					ostream.write(("H"+" ").getBytes());
				}
				else {
					ostream.write(("R"+" ").getBytes());
				}
				if (aRectangle.getIsLegend()) {
					ostream.write(("L"+" ").getBytes());
				}
				else {
					ostream.write(("-"+" ").getBytes());
				}
				ostream.write(("\n").getBytes());
			}
			ostream.write(("\n").getBytes());
			Wedge aWedge;
			lItr = wedges.listIterator(0);
			while (lItr.hasNext()) {
				aWedge= (Wedge)lItr.next();
				ostream.write(("Wedge "+aWedge.getUpperLeft()+" "+aWedge.getLowerRight()+" "+aWedge.getColor()+" ").getBytes());
				if (aWedge.getIsFilledArea()) {
					ostream.write(("F"+" ").getBytes());
				}
				else {
					ostream.write(("U"+" ").getBytes());
				}
				if (aWedge.getIsData()) {
					ostream.write(("D"+" ").getBytes());
				}
				else {
					ostream.write(("W"+" ").getBytes());
				}
				ostream.write((aWedge.getAngle()+"\n").getBytes());
			}
			ostream.write(("\n").getBytes());
			ConnectedLine aLine;
			lItr = connectedLines.listIterator(0);
			while (lItr.hasNext()) {
				aLine = (ConnectedLine)lItr.next();
				ostream.write(("ConnectedLine "+aLine.getUpperLeft()+" "+aLine.getLowerRight()+" "+aLine.getColor()+" ").getBytes());
				if (aLine.getIsFilledArea()) {
					ostream.write(("F"+" ").getBytes());
				}
				else {
					ostream.write(("U"+" ").getBytes());
				}
				if (aLine.getIsDataLine()) {
					ostream.write(("D"+" ").getBytes());
				}
				else {
					ostream.write(("L"+" ").getBytes());
				}
				ostream.write(("\n").getBytes());
			}
			ostream.write(("\n").getBytes());
			ostream.close();
		} 
		catch (Exception e) {System.out.println(e.getMessage());}
	}

	/**
	 * Prints the <code>Graph</code> object.
 	 * 
	 * @param none
 	 */
	public String toString() {
		String message = new String("\nGraph components:\nAxes: \n"+hAxis+vAxis+"Origin is "+origin+"\n\nThere are "+rectangles.size()+" rectangles, "+noOfBars+" bars.\n");
		Rectangle aRectangle;
		ListIterator lItr = rectangles.listIterator(0);
		while (lItr.hasNext()) {
			aRectangle = (Rectangle)lItr.next();
			message = (message + aRectangle); 
		}
		message = (message + "\nThere are "+wedges.size()+" wedges.\n");
		Wedge aWedge;
		lItr = wedges.listIterator(0);
		while (lItr.hasNext()) {
			aWedge= (Wedge)lItr.next();
			message = (message + aWedge); 
		}
		message = (message + "\nThere are "+connectedLines.size()+" separate connected lines, "+noOfDataLines+" data lines.\n");
		ConnectedLine aLine;
		lItr = connectedLines.listIterator(0);
		while (lItr.hasNext()) {
			aLine = (ConnectedLine)lItr.next();
			message = (message + aLine); 
		}
		message = (message + "\nThere are "+dataValuesList.size()+" data values.\n");
		if (dataValuesList.size() > 0) {
			if (chartType == BARCHART || chartType == LINECHART) {
				//System.out.println("Values are listed as (row, column) with respect to the origin "+origin);
				if (chartType == BARCHART) {
					message = (message + "This is a bar chart.\n");
				}
				else {
					message = (message + "This is a line chart.\n");
				}
				message = (message + "Values are listed as (row, column), origin is "+origin+"\n");
				lItr = dataValuesList.listIterator(0);
				while (lItr.hasNext()) {
					PointPixel aValue = (PointPixel)lItr.next();
					message = (message + aValue +"\n"); 
				}
			}
			else if (chartType == PIECHART) {
				message = (message + "This is a pie chart.\n");
				message = (message + "Values are listed as (color, angle in degrees)\n");
				lItr = dataValuesList.listIterator(0);
				while (lItr.hasNext()) {
					PointPixel aValue = (PointPixel)lItr.next();
					double percentage = (double)aValue.getColumn()*100/360;
					message = (message + aValue +" "+Math.round(percentage)+"%\n");
				}
			}
		}	
		return message;
	}

	public void writeXML(String filename,
                             Region[] allRegions,
                             int[][] firstPixelLabel) {



//System.out.println("about to call makeLinerScale\n");
            //makeLinearScale(barOrientation);
            makeLinearScale(VERTICAL);
            makeLinearScale(HORIZONTAL);
            classifyTextBlocks();

	    try {
	        BufferedWriter ostream =
		    new BufferedWriter(new FileWriter(filename));
                ostream.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
		ostream.write("<InformationGraphic>\n");

System.out.println("chartType");
System.out.println(chartType);
	switch(chartType) {
	    case 0: break;
	    case 1: writeBarChart(ostream, allRegions, firstPixelLabel); break;
	    case 2: writeLineChart(ostream, allRegions, firstPixelLabel); break;
	    case 3: writePieChart(ostream); break;
	}
	ostream.write("</InformationGraphic>\n");
	ostream.close();
    }
    catch (Exception e) {System.out.println(e.getMessage());}
}

void writeBarChart(BufferedWriter ostream,
                   Region[] allRegions,
                   int [][] firstPixelLabel) {
    ListIterator lItr;
    TextBlock tb;
    WordCompare wc = new WordCompare();
    Word aWord;
    int i,n;
    //Separate legends from bars
    LinkedList justBars = new LinkedList();;
    Rectangle aBar;
    lItr = dataRectanglesList.listIterator();
    while (lItr.hasNext()) {
        aBar = (Rectangle)lItr.next();
        if (aBar.getIsLegend()) {
            legendsList.add(aBar);
            noOfLegends++;}
        else justBars.add(aBar);}
    annotateBars2();
    reclassifyBlocks();
    dataRectanglesList = justBars;
//System.out.println("legendsize = "+legendsList.size());
//System.out.println ("dataRectanglessize = " + dataRectanglesList.size());
//System.out.println("noOfLegends= "+noOfLegends);
    
    try {
        //System.out.println("writing bar chart\n");
        if (noOfLegends > 0) ostream.write("<GroupedBarChart BarDirection=\"");
	else ostream.write("<BarChart BarDirection=\"");
	int direction = getBarDirection();
	if (direction == HORIZONTAL) ostream.write("horizontal");
	if (direction == VERTICAL) ostream.write("vertical");
	ostream.write("\">\n");

                // Write title as caption
        //newer way
        if (!Caption.isEmpty()) {
          ostream.write("        <Caption>\n");
          ostream.write("                <Content>");
          lItr = Caption.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();

if (tb.getText().equals(".")) {System.out.println(tb.getUpperLeft());}

            ostream.write(tb.getText());
            if (lItr.hasNext()) ostream.write ("\n");}
          ostream.write("</Content>\n");
          ostream.write("        </Caption>\n");}

        if (!Descriptions.isEmpty()) {
          Collections.reverse(Descriptions);
          ostream.write("        <Descriptions>\n");
          lItr = Descriptions.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();
            ostream.write("                <Description>\n");
            ostream.write("                        <Content>");
            ostream.write(tb.getText());
            ostream.write("</Content>\n");
            ostream.write("                </Description>\n");
            }
          ostream.write("        </Descriptions>\n");}

        if (!TextInGraphic.isEmpty()) {
          ostream.write("        <TextInGraphic>");
          lItr = TextInGraphic.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();
            ostream.write(tb.getText());
            if (lItr.hasNext()) ostream.write(";");
            }
          ostream.write("</TextInGraphic>\n");}

        if (!TextUnderGraphic.isEmpty()) {
          ostream.write("        <TextUnderGraphic>");
          lItr = TextUnderGraphic.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();
            ostream.write(tb.getText());
            if (lItr.hasNext()) ostream.write(";");
            }
          ostream.write("</TextUnderGraphic>\n");}

        DecimalFormat formater = new DecimalFormat("##0.00");
        int MALength;
        if (direction == HORIZONTAL)
          MALength = hAxis.getSize();
        else MALength = vAxis.getSize();
	ostream.write("        <MeasurementAxis ");
        ostream.write("Length=\"");
        ostream.write(formater.format(MALength*2.54/72));
        ostream.write("\">\n");
        if (direction==VERTICAL) {
//System.out.println("test for YTitlewords");
          if (hasYTitleWords()) {
	    ostream.write("                <Label>");
            ostream.write(((TextBlock)YTitle.getFirst()).getText());
	    ostream.write("</Label>\n");}}
//System.out.println("maybe horizontal bars");
        if (direction==HORIZONTAL) {
          if (XTitle.size() > 0) {
            ostream.write("                <Label>");

            ostream.write(((TextBlock)XTitle.getFirst()).getText());

            ostream.write("</Label>\n");}}
        if (ticksHaveValues)
          writeTickmarks(ostream,direction);
	ostream.write("        </MeasurementAxis>\n");
	ostream.write("        <BarAxis ");
    
        ostream.write("Length=\"");

        if (direction == VERTICAL)
          ostream.write(formater.format(axisLength(hAxis.getBeginPoint(),
                                                   hAxis.getEndPoint())));
        else ostream.write(formater.format(axisLength(vAxis.getBeginPoint(),
                                                      vAxis.getEndPoint())));
        ostream.write("\">\n");

        if (direction == VERTICAL) {
          //LinkedList XTitleWords = findChartXTitle();
          if (XTitle.size() > 0) {
            ostream.write("                <Label>");

            ostream.write(((TextBlock)XTitle.getFirst()).getText());
            ostream.write("</Label>\n");}}
        if (direction==HORIZONTAL) {
          if (hasYTitleWords()) {
	    //ostream.write("                <Label>");
	    //writeYTitleWords(ostream);
	    //ostream.write("</Label>\n");}}
	    ostream.write("                <Label>");
            ostream.write(((TextBlock)YTitle.getFirst()).getText());
	    ostream.write("</Label>\n");}}


	ostream.write("        </BarAxis>\n");
	writeBarData(ostream,direction, allRegions, firstPixelLabel);
        if (noOfLegends>0) ostream.write("</GroupedBarChart>\n");
	else ostream.write("</BarChart>\n");}
    catch (Exception e) {System.out.println(e.getMessage());}}

void writePieChart(BufferedWriter ostream) {
    try {
	ostream.write("<PieChart NumberOfSlices=\"");
	ostream.write("\">\n");
	ostream.write("<Slice>\n");
	ostream.write("<Label>");
	ostream.write("</Label>\n");
	ostream.write("</Slice>\n");
	ostream.write("</PieChart>\n");}
    catch (Exception e) {System.out.println(e.getMessage());}}

void writeLineChart(BufferedWriter ostream,
                   Region[] allRegions,
                   int [][] firstPixelLabel) {
    ListIterator lItr;
    TextBlock tb;
    findPointLabels(allRegions, firstPixelLabel);
    reclassifyLines();
    reclassifyBlocks();
    try {
	ostream.write("<LineGraph>\n");
        if (Caption.isEmpty()) {
          ostream.write("        <Caption/>\n");}
        else {
          ostream.write("        <Caption>\n");
          ostream.write("                <Content>");
          lItr = Caption.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();

if (tb.getText().equals(".")) {System.out.println(tb.getUpperLeft());}

            ostream.write(tb.getText());
            if (lItr.hasNext()) ostream.write ("\n");}
          ostream.write("</Content>\n");
          ostream.write("                <Noun/>\n");
          ostream.write("                <Verb/>\n");
          ostream.write("                <Adjective/>\n");
          ostream.write("                <Value/>\n");
          ostream.write("                <Time/>\n");
          ostream.write("        </Caption>\n");}


        if (Descriptions.isEmpty()) {
            ostream.write("        <Descriptions>\n");
            ostream.write("                <Description>\n");
            ostream.write("                        <Content/>\n");
            ostream.write("                        <Noun/>\n");
            ostream.write("                        <Verb/>\n");
            ostream.write("                        <Adjective/>\n");
            ostream.write("                        <Value/>\n");
            ostream.write("                        <Time/>\n");
            ostream.write("                </Description>\n");
            ostream.write("        </Descriptions>\n");
            }
        else {
          Collections.reverse(Descriptions);
          ostream.write("        <Descriptions>\n");
          lItr = Descriptions.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();
            ostream.write("                <Description>\n");
            ostream.write("                        <Content>");
            ostream.write(tb.getText());
            ostream.write("</Content>\n");
            ostream.write("                        <Noun/>\n");
            ostream.write("                        <Verb/>\n");
            ostream.write("                        <Adjective/>\n");
            ostream.write("                        <Value/>\n");
            ostream.write("                        <Time/>\n");
            ostream.write("                </Description>\n");
            }
          ostream.write("        </Descriptions>\n");}

        if (TextInGraphic.isEmpty()) {
          ostream.write("        <TextInGraphic/>\n");}
        else {
          ostream.write("        <TextInGraphic>");
          lItr = TextInGraphic.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();
            ostream.write(tb.getText());
            if (lItr.hasNext()) ostream.write(";");
            }
          ostream.write("</TextInGraphic>\n");}

        if (TextUnderGraphic.isEmpty()) {
          ostream.write("        <TextUnderGraphic/>\n");}
        else {
          ostream.write("        <TextUnderGraphic>");
          lItr = TextUnderGraphic.listIterator();
          while (lItr.hasNext()) {
            tb = (TextBlock)lItr.next();
            ostream.write(tb.getText());
            if (lItr.hasNext()) ostream.write(";");
            }
          ostream.write("</TextUnderGraphic>\n");}


	NumberFormat nf = NumberFormat.getInstance();
	ostream.write("<XAxis Length= \"");
        ostream.write(nf.format(hAxis.getSize()));
        ostream.write("\">\n");
        if (hasXTitleWords()) {
	  ostream.write("<Label>");

          ostream.write(((TextBlock)XTitle.getFirst()).getText());


	//writeXTitleWords(ostream);
	  ostream.write("</Label>\n");}
        writeTickmarks(ostream, HORIZONTAL);
	ostream.write("</XAxis>\n");
	ostream.write("<YAxis Length=\"");
        ostream.write(nf.format(vAxis.getSize()));
        ostream.write("\">\n");
        if (hasYTitleWords()) {
	  ostream.write("<Label>");
          ostream.write(((TextBlock)YTitle.getFirst()).getText());

	//writeYTitleWords(ostream);
	  ostream.write("</Label>\n");}
        writeTickmarks(ostream, VERTICAL);
	ostream.write("</YAxis>\n");
	writeLineData(ostream);
	ostream.write("</LineGraph>\n");}
    catch (Exception e) {System.out.println(e.getMessage());}}

int getBarDirection() {
  return barOrientation;}
    //ListIterator litr = rectangles.listIterator();
    //Rectangle aRectangle;
    //while (litr.hasNext()) {
	//aRectangle = (Rectangle)litr.next();
	//if (aRectangle.getIsData()) {
	    //if (aRectangle.getIsVerticalBar()) return VERTICAL;
	    //if (aRectangle.getIsHorizontalBar()) return HORIZONTAL;}}
    //return UNKNOWNCHART;}

void writeXTitleWords(BufferedWriter ostream) {
    Word aWord;
    Iterator itr = words.values().iterator();
    LinkedList XTitleWords = new LinkedList();
    while (itr.hasNext()) {
	aWord = (Word) itr.next();
	if (aWord.getIsXTitle()) {
	    XTitleWords.addFirst(aWord.getText());}}
    ListIterator litr = XTitleWords.listIterator();
    try {
	while (litr.hasNext()) {
		ostream.write((String)litr.next());
		if (litr.hasNext()) ostream.write(" ");}}
    catch (Exception e) {System.out.println(e.getMessage());}}

void writeYTitleWords(BufferedWriter ostream) {
    Word aWord;
    Iterator itr = words.values().iterator();
    LinkedList YTitleWords = new LinkedList();
    while (itr.hasNext()) {
	aWord = (Word) itr.next();
	if (aWord.getIsYTitle()) {
	    YTitleWords.addFirst(aWord.getText());}}
    Collections.reverse(YTitleWords);
    ListIterator litr = YTitleWords.listIterator();
    try {
	while (litr.hasNext()) {
		ostream.write((String)litr.next());
		if (litr.hasNext()) ostream.write(" ");}}
    catch (Exception e) {System.out.println(e.getMessage());}}

/***
 * writeLabel writes the label of a bar.
 * 
 * @param aBar the bar whose label will be printed
 * @ostream the stream the label is to be printed to
 * @param allRegions the regions that have to be searched
 * $param firstPixelLabel
 */

void writeLabel(Rectangle aBar,
                  BufferedWriter ostream,
                  Region[] allRegions,
                  int[][] firstPixelLabel) {
          Region testRegion;
          try {
                if (!aBar.getLabelBlocks().isEmpty()) {
		  ostream.write("                <Label>\n");
                  ostream.write("                        <Content>");
		  //Collections.reverse(aBar.getLabelBlocks());
		  writeBlocks(aBar.getLabelBlocks(),ostream);
                  ostream.write("</Content>\n");
                  ostream.write("                        <Color>");
                  //testWord = (Word)(aBar.getLabelBlocks().getFirst());
                  //testArray = testWord.getCharacters().toArray();
                  //testRegion = allRegions[((Integer)testArray[0]).intValue()];
                  testRegion = allRegions[((TextBlock)(aBar.getLabelBlocks().getFirst())).getFirstCharacter().intValue()];
//System.out.println( testRegion.getColor() );
                  ostream.write( "" + testRegion.getColor() );
                  ostream.write("</Color>\n");
                  ostream.write("                        <Bold>");
                  if (isBold(((TextBlock)aBar.getLabelBlocks().getFirst()).getFirstWord(), allRegions, firstPixelLabel))
                    ostream.write("true");
                  else ostream.write("false");
                  ostream.write("</Bold>\n");
		  ostream.write("                </Label>\n");
                   }}
	    catch (Exception e) {System.out.println(e.getMessage());}
    }

/***
 *  wruteAnnotation writes the annotation attached to a bar or legend
 *
 * @param aBar the bar whose annotation will be printed
 * @ostream the stream the annotation is to be printed to
 * @param allRegions the regions that have to be searched
 * $param firstPixelLabel
 */

void writeAnnotation(Rectangle aBar,
                  BufferedWriter ostream,
                  Region[] allRegions,
                  int[][] firstPixelLabel) {
    Region testRegion;
    Word testWord;
    LinkedList testList;
    Object[] testArray;
		LinkedList blockList = aBar.getAnnotationBlocks();
try {
                if (!blockList.isEmpty()) {
		    boolean notInt = true;
		    boolean notFloat = true;
		    ostream.write("                <Annotation>\n");
                    ostream.write("                        <Content>");
                    Collections.reverse(blockList);
                    writeBlocks(blockList,ostream);
                    ostream.write("</Content>\n");
                    ostream.write("                        <Color>");

                TextBlock testBlock = (TextBlock)(blockList.getFirst());

                testList = testBlock.getPieces();
                testList = ((TextPiece)testList.getFirst()).getWords();
                testArray = ((Word)testList.getFirst()).getCharacters().toArray();
                testRegion = allRegions[((Integer)testArray[0]).intValue(
)];
                ostream.write("" + testRegion.getColor());



                    ostream.write("</Color>\n");
                    ostream.write("                        <Bold>");
                    if (isBold((Word)(testList.getFirst()), allRegions,
firstPixelLabel))
                       ostream.write("true");
                    else ostream.write("false");
                    ostream.write("</Bold>\n");
                    ostream.write("                </Annotation>\n");}
          }
	           catch (Exception e) {System.out.println(e.getMessage());}
   }


void writeBarData(BufferedWriter ostream,
                  int direction,
                  Region[] allRegions,
                  int[][] firstPixelLabel) {
    Rectangle aBar;
    Object[] rectangleArray;
    Word testWord;
    LinkedList testList;
    Object[] testArray;
    Region testRegion;
    int groupCount = 0;
    LinkedList groupList = new LinkedList();
//System.out.println("dir = " + direction);
            rectangleArray = dataRectanglesList.toArray();
            if (direction == VERTICAL) {
               BarColumnCompare bcc = new BarColumnCompare();
               Arrays.sort(rectangleArray,bcc);}
            else {
               BarRowCompare brc = new BarRowCompare();
               Arrays.sort(rectangleArray,brc);}
                int j;
//System.out.println("size of rectangleArray " + rectangleArray.length);
                if (noOfLegends > 0) {
                    ListIterator lItr = legendsList.listIterator();
try {
                    ostream.write("    <Legend>\n");
                    while (lItr.hasNext()) {
                      aBar = (Rectangle)lItr.next();
                      if (aBar.getIsLegend()) {
                        ostream.write("            <Series>\n");
		        ostream.write("                <Color>");
		        ostream.write(Integer.toString(aBar.getColor()));
		        ostream.write("</Color>\n");
                        writeAnnotation(aBar,ostream,allRegions,firstPixelLabel);
                        writeLabel(aBar,ostream,allRegions,firstPixelLabel);
                        ostream.write("            </Series>\n");}}
                     ostream.write("  </Legend>\n");}
	           catch (Exception e) {System.out.println(e.getMessage());}
                 }
                    
//System.out.println("j = " + j);
              for (j =  0;j < rectangleArray.length; j++) {
                aBar = (Rectangle)rectangleArray[j];
//System.out.println("have a bar");
try {
                if (noOfLegends>0 & groupCount==0) ostream.write("<Group>\n");
	        ostream.write("        <Bar>\n");
                if (noOfLegends>0) groupList.add(aBar);
                else writeLabel(aBar,ostream,allRegions,firstPixelLabel);
                 }
	    catch (Exception e) {System.out.println(e.getMessage());}
try {
		ostream.write("                <Color>");
		ostream.write(Integer.toString(aBar.getColor()));
		ostream.write("</Color>\n");
		ostream.write("                <Height>");
                double h;
                if (direction == VERTICAL)
		  h = aBar.getHeight();
                else h = aBar.getWidth();
                h = h*2.54/72;
                DecimalFormat formater = new DecimalFormat("##0.00");
                //ostream.write(Double.toString(h));
                ostream.write(formater.format(h));
		ostream.write("</Height>\n");

                ostream.write("                <AxisDistance>");
                 if (direction == VERTICAL)
                   h = aBar.getValue().getColumn() - origin.getColumn();
                 //else h = aBar.getValue().getRow() - origin.getRow();
                 else h = aBar.getValue().getRow() - vAxis.getBeginPoint().getRow();
                 h = h*2.54/72;
                 ostream.write(formater.format(h));
                ostream.write("</AxisDistance>\n");
                ostream.write("                <SightLine>");
                ostream.write("false");
                ostream.write("</SightLine>\n");


                //if (coefficients[0] != 0) {
                if(ticksHaveValues) {
                   ostream.write("                <Value>");

// adding +/- 1 to the getValue.getRow or .getColumn adds the border pixel to
// the bar
                   
                  
                 //xyz
                   int val;
                   double LSVal;
                   if (direction==VERTICAL)
                     val = aBar.getValue().getRow() - 1;
                   else
                     val = aBar.getValue().getColumn() + 1;
                   LSVal = readLinearScale(val,direction);
/*
                   int i;
	           Object[] anArray;
                   if (direction == VERTICAL) anArray = VerticalTickPoints;
                   else anArray = HorizontalTickPoints;
                   int[] r = new int[3];
                   double[] v = new double[3];
                   if (direction==VERTICAL)
                     r[0] = aBar.getValue().getRow() - 1;
                   else
                     r[0] = aBar.getValue().getColumn() + 1;
                   if (r[0] > measurementPosition((PointPixel)anArray[0],direction)) {
                     r[1] = measurementPosition((PointPixel)anArray[0],direction);
                     v[1] = ((PointPixel)anArray[0]).getYValue();
                     r[2] = measurementPosition((PointPixel)anArray[1],direction);
                     v[2] = ((PointPixel)anArray[1]).getYValue();
                     v[0] = v[1] + (r[0] - r[1]) * (v[2] - v[1])/(r[2] - r[1]);}
                   else {
                     for(i = anArray.length - 1; i >= 0; i--) {
//System.out.println("i = " + i + " v = " +
//((PointPixel)anArray[i]).getYValue()
//+ " r = " + measurementPosition((PointPixel)anArray[i],direction));
                       if (r[0] <= measurementPosition((PointPixel)anArray[i],direction)) break;}
                     if (i < anArray.length - 1) {
                       r[1] = measurementPosition((PointPixel)anArray[i],direction);
                       v[1] = ((PointPixel)anArray[i]).getYValue();
                       r[2] = measurementPosition((PointPixel)anArray[i+1],direction);
                       v[2] = ((PointPixel)anArray[i+1]).getYValue();}
                     else {
                       r[1] = measurementPosition((PointPixel)anArray[i],direction);
                       v[1] = ((PointPixel)anArray[i]).getYValue();
                       r[2] = measurementPosition((PointPixel)anArray[i-1],direction);
                       v[2] = ((PointPixel)anArray[i-1]).getYValue();}

                     v[0] = v[1] + (r[0] - r[1]) * (v[2] - v[1])/(r[2] - r[1]);}
                   
//System.out.println("v[0] = " + v[0]);

*/





                   ostream.write(formater.format(LSVal));
                   ostream.write("</Value>\n");}

                writeAnnotation(aBar,ostream,allRegions,firstPixelLabel);
	        ostream.write("        </Bar>\n");
                groupCount++;
                if (groupCount==noOfLegends) {
                    groupCount = 0;
                    ListIterator gItr = groupList.listIterator();
                    while (gItr.hasNext()) {
                      writeLabel((Rectangle)gItr.next(),ostream,allRegions,firstPixelLabel);}
                    groupList = new LinkedList();
                    ostream.write("</Group>\n");}
                }
//}
	    catch (Exception e) {System.out.println(e.getMessage());}}
}

    void writeWords (LinkedList lst,BufferedWriter ostream) {
	ListIterator itr = lst.listIterator();
	Word aWord;
	while (itr.hasNext()) {
	    aWord = (Word)itr.next();
	    try {
	        ostream.write(aWord.getText());
	        if (itr.hasNext()) ostream.write(" ");}
	    catch (Exception e) {}}}

    void writeBlocks (LinkedList lst,BufferedWriter ostream) {
	ListIterator itr = lst.listIterator();
	TextBlock aBlock;
	while (itr.hasNext()) {
	    aBlock = (TextBlock)itr.next();
	    try {
	        ostream.write(aBlock.getText());
	        if (itr.hasNext()) ostream.write(" ");}
	    catch (Exception e) {}}}

    void writeLineData(BufferedWriter ostream) {
//System.out.println("in writeLineData");
	LinkedList lineList = annotateLines2();
	ListIterator anItr = lineList.listIterator(0);
	NumberFormat nf = NumberFormat.getInstance();
	int i, last_column = 0;
	Object[] pointArray;
	try {
//System.out.println("in first try");
	    while (anItr.hasNext()) {
	        //ostream.write("<Line>\n");
//System.out.println("another line");
	        pointArray = (Object[])anItr.next();
//System.out.println("getting first column");
                int first_column = ((PointPixel)pointArray[0]).getColumn();
//System.out.println("first col "+first_column + "  last col "+ last_column);
                if (first_column < last_column) continue;
                int old_last_column = last_column;
                last_column = ((PointPixel)pointArray[pointArray.length-1]).getColumn();
	        for (i = 0;i< pointArray.length;i++) {
//System.out.println("i "+i+" 1st "+first_column+" old last "+ old_last_column);
//System.out.println("col "+((PointPixel)pointArray[i]).getColumn());
//System.out.println("row "+((PointPixel)pointArray[i]).getRow());
                 if (i == 0 && first_column == old_last_column) continue;
                 if (i == pointArray.length -1 ||
                    //((PointPixel)pointArray[i]).getColumn() + 1 <
                    ((PointPixel)pointArray[i]).getColumn() <
                    ((PointPixel)pointArray[i+1]).getColumn())
                    {
//System.out.println("...");
		    ostream.write("<SamplePoint>\n");
		    ostream.write("	<Coord><X>");
                    //if (hTicksHaveValues)
                      //ostream.write(nf.format(readLinearScale(((PointPixel)pointArray[i]).getColumn(),HORIZONTAL)));
                     // 
                    //else
		    //writeWords(((PointPixel)pointArray[i]).getLabelWords(),ostream);
                    ostream.write(nf.format(((PointPixel)pointArray[i]).getColumn() - origin.getColumn()));
                    ostream.write("</X>");
		    ostream.write("<Y>");
		    //ostream.write(nf.format(((PointPixel)pointArray[i]).getYValue()));
                    ostream.write(nf.format(origin.getRow() - ((PointPixel)pointArray[i]).getRow()));
		    ostream.write("</Y></Coord>\n");
                    //ostream.write("<C>");
                    //ostream.write(nf.format(((PointPixel)pointArray[i]).getColumn()));
                    //ostream.write("</C>");
                    //ostream.write("<R>");
                    //ostream.write(nf.format(((PointPixel)pointArray[i]).getRow()));
                    //ostream.write("</R>\n");
                    ostream.write("	<OnXGrid>false</OnXGrid>");
                    ostream.write("<OnYGrid>false</OnYGrid>\n");
                    writePointLabel(ostream,((PointPixel)pointArray[i]).getColumn());
		    ostream.write("</SamplePoint>\n");
                    if (i < pointArray.length - 1)
                        writeInterpolatedPointLabels(ostream,(PointPixel)pointArray[i],(PointPixel)pointArray[i+1]);}
                    }
                //ostream.write("<Color>");
                //ostream.write(nf.format(((PointPixel)pointArray[0]).getValue()));
                //ostream.write("</Color>\n");
                //ostream.write("<Width>1</Width>\n");
	        //ostream.write("</Line>\n");
                }}
	catch (Exception e) {System.out.println(e.getMessage()+"in writeLineData");}
       // writeRawLineData(ostream);
//System.out.println("done with writeLineData");
        }

    int max(int x, int y) {
        if (x >= y) return x;
        else return y;}

    int min(int x, int y) {
        if (x <= y) return x;
        else return y;}

    void writeRawLineData(BufferedWriter ostream) {
	LinkedList lineList = connectedLines;
	ListIterator anItr = lineList.listIterator(0);
	NumberFormat nf = NumberFormat.getInstance();
	int r0,c0,rPrev,rNow,rNext,rFirst,rLast,rMax,rMin,cPrev,cNow,cNext;
        Boolean debugging = false;
        //debugging = true;
        Primitive aPrim;
        ConnectedLine aLine;
        LinkedList primitivesList, pointsList;
        Point aPoint;
        r0 = origin.getRow();
        c0 = origin.getColumn();
	try {
	    while (anItr.hasNext()) {
                aLine = (ConnectedLine)anItr.next();
                if (aLine.getIsDataLine()) {
//if (aLine.getNoOfPixels() == 1) ostream.write("line is a point");
//ostream.write(nf.format(aLine.getNoOfPixels()));
	            ostream.write("<Raw_Line>\n");
                    primitivesList = aLine.getPrimitives();
                    ListIterator lItr2 = primitivesList.listIterator(0);
                    while (lItr2.hasNext()) {
                        aPrim = (Primitive)lItr2.next();
                        pointsList = aPrim.getAllPoints();
                        ListIterator lItr3 = pointsList.listIterator(0);
                        aPoint = (Point)lItr3.next();
                        rNow = (int)aPoint.getX();
                        cNow = (int)aPoint.getY();
                        cPrev = cNow -1;
                        rPrev = rNow;
                        rFirst = rNow;
                        rLast = rNow;
                        while (lItr3.hasNext()) {
                            aPoint = (Point)lItr3.next();
                            rNext = (int)aPoint.getX();  //I must have X and Y
                            cNext = (int)aPoint.getY();  // mixed up
                            if (cNext == cNow) rLast = rNext;
                            else {
                                ostream.write("<SamplePoint>\n	<Coord><X>");
                                ostream.write(nf.format(cNow - c0));
                                ostream.write("</X><Y>");
                                if (rFirst == rLast) rNow = rFirst;
                                else {
                                    rMax = max(rFirst, rLast);
                                    rMin = min(rFirst, rLast);
                                    if (rPrev <= rMax && rNext <= rMax)
                                        rNow = rMax;
                                    else if (rPrev >= rMin && rNext >= rMin)
                                        rNow = rMin;
                                    else rNow = (rMax + rMin)/2;}
                                ostream.write(nf.format(r0 - rNow));
                                ostream.write("</Y></Coord>\n");
                                if (debugging) {
                                    ostream.write("C:");
                                    ostream.write(nf.format(cNow));
                                    ostream.write(", R:");
                                    ostream.write(nf.format(rNow));}
                                else {
//System.out.println("at "+cNow+" "+rNow);
                                    ostream.write("	<OnXGrid>false</OnXGrid>");
                                    ostream.write("<OnYGrid>false</OnYGrid>\n");
                                    writePointLabel(ostream,cNow);
                                }
                                ostream.write("</SamplePoint>\n");
                                rPrev = rNow;
                                cPrev = cNow;
                                rFirst = rNext;
                                rLast = rNext;
                                cNow = cNext;}}
                        ostream.write("<SamplePoint>\n	<Coord><X>");
                        ostream.write(nf.format(cNow - c0));
                        ostream.write("</X><Y>");
                        if (rFirst == rLast) rNow = rFirst;
                        else {
                            rMax = max(rFirst, rLast);
                            rMin = min(rFirst, rLast);
                            if (rPrev <= rMax)
                                rNow = rMax;
                            else if (rPrev >= rMin)
                                rNow = rMin;
                            else rNow = (rMax + rMin)/2;}  //can't happen
                        ostream.write(nf.format(r0 - rNow));
                        ostream.write("</Y></Coord>\n");
//System.out.println("at "+cNow+" "+rNow);
                        ostream.write("	<OnXGrid>false</OnXGrid>");
                        ostream.write("<OnYGrid>false</OnYGrid>\n");
                        writePointLabel(ostream,cNow);
                        ostream.write("</SamplePoint>\n");}
                     ostream.write("<Color>");
                     ostream.write(nf.format(aLine.getColor()));
                     ostream.write("</Color>\n");
                     ostream.write("<Width>1</Width>\n");
                     ostream.write("</Raw_Line>\n");}}}
	catch (Exception e) {System.out.println(e.getMessage());}}

    LinkedList annotateLines() {
	//double[] coefficients = new double[2];
	//makeLinearScale(VERTICAL,coefficients);
	Object[] pointArray;
	Object[] primitiveArray;
	Primitive aPrim;
	LinkedList lineList = new LinkedList();
	LinkedList primitiveList, pointsList, indexList;
        ConnectedLine aLine;
	int i;
	ListIterator lItr = connectedLines.listIterator(0);
	while (lItr.hasNext()) {
//System.out.println("another line in annotateLines");
	    aLine = (ConnectedLine)lItr.next();
	    if (aLine.getIsDataLine()) {
                LinkedList linePointsList = new LinkedList();
		LineFitter aLineFitter = new LineFitter();
		primitiveList = aLine.getPrimitives();
		ListIterator lItr2 = primitiveList.listIterator(0);
		while (lItr2.hasNext()) {
//System.out.println("another primitive");
		    aPrim = (Primitive)lItr2.next();
		    pointsList = aPrim.getAllPoints();
		    indexList = new LinkedList();
		    indexList.add(aPrim.getBeginPoint());
		    indexList.add(aPrim.getEndPoint());
		    aLineFitter.splitSegment(pointsList, indexList);
		    pointArray = indexList.toArray();
		    //System.out.println("in annotate lines, palen="+pointArray.length);
	            ColumnCompare cC = new ColumnCompare();
	            Arrays.sort(pointArray,cC);
	            Word aWord;
	            PointPixel aPixel;
	            for(i=0;i<pointArray.length;i++) {
	                aPixel = (PointPixel)pointArray[i];
	                aPixel.initAnnotationWords();
	                aPixel.initLabelWords();
	                Iterator itrW = words.values().iterator();
	                while (itrW.hasNext()) {
		            aWord = (Word) itrW.next();
		            if (aWord.justAbove(aPixel)) {
		                aPixel.addToAnnotationWords(aWord);
		                aWord.setIsAttached(true);}
		            if (aWord.justBelow(aPixel,hAxis)) {
		                aPixel.addToLabelWords(aWord);
		                aWord.setIsAttached(true);}}}
	            for (i=0;i<pointArray.length;i++) {
                        aPixel = (PointPixel) pointArray[i];
	                //aPixel.setYValue(coefficients[0] * aPixel.getRow()
			                      //+ coefficients[1]);
// experiment
                        aPixel.setYValue(readLinearScale(aPixel.getRow(), VERTICAL));
}
                    ListIterator lItr3 = indexList.listIterator();
                    while (lItr3.hasNext()) {
                        linePointsList.add(lItr3.next());}}
                Object[] linePointsArray;
                linePointsArray = linePointsList.toArray();
	        ColumnCompare cC2 = new ColumnCompare();
	        Arrays.sort(linePointsArray,cC2);
                lineList.add(linePointsArray); }}
	return lineList;}

    LinkedList annotateLines2() {
	//double[] coefficients = new double[2];
	//makeLinearScale(VERTICAL,coefficients);
	Object[] pointArray;
	Object[] primitiveArray;
	Primitive aPrim;
	LinkedList lineList = new LinkedList();
        LinkedList grandPointsList = new LinkedList();
	LinkedList primitiveList, pointsList, indexList;
        ConnectedLine aLine;
	int i;
	ListIterator lItr = connectedLines.listIterator(0);
	while (lItr.hasNext()) {
//System.out.println("another line in annotateLines2");
	    aLine = (ConnectedLine)lItr.next();
	    if (aLine.getIsDataLine()) {
                LinkedList linePointsList = new LinkedList();
		LineFitter aLineFitter = new LineFitter();
		primitiveList = aLine.getPrimitives();
		ListIterator lItr2 = primitiveList.listIterator(0);
		while (lItr2.hasNext()) {
//System.out.println("another primitive");
		    aPrim = (Primitive)lItr2.next();
		    pointsList = aPrim.getAllPoints();
ListIterator LI = pointsList.listIterator();
while (LI.hasNext()) {
    Point pp = (Point)LI.next();
    System.out.println("column "+pp.getY() +" row "+pp.getX());}
		    indexList = new LinkedList();
		    indexList.add(aPrim.getBeginPoint());
		    indexList.add(aPrim.getEndPoint());
		    aLineFitter.splitSegment(pointsList, indexList);
		    pointArray = indexList.toArray();
//System.out.println("result of splitSegment");
int z;
//for(z=0;z<pointArray.length;z++){
  //System.out.println("ROW "+((PointPixel)pointArray[z]).getRow()+" COL "+((PointPixel)pointArray[z]).getColumn());}
		    //System.out.println("in annotate lines, palen="+pointArray.length);
	            ColumnCompare cC = new ColumnCompare();
	            Arrays.sort(pointArray,cC);
	            Word aWord;
	            PointPixel aPixel;
	            for(i=0;i<pointArray.length;i++) {
	                aPixel = (PointPixel)pointArray[i];
//System.out.println("row: "+aPixel.getRow()+" col: "+aPixel.getColumn());
	                aPixel.initAnnotationWords();
	                aPixel.initLabelWords();
	                Iterator itrW = words.values().iterator();
	                while (itrW.hasNext()) {
		            aWord = (Word) itrW.next();
		            if (aWord.justAbove(aPixel)) {
		                aPixel.addToAnnotationWords(aWord);
//System.out.println("attaching " + aWord.getText());
		                aWord.setIsAttached(true);}
		            //if (aWord.justBelow(aPixel,hAxis)) {
		                //aPixel.addToLabelWords(aWord);
		                //aWord.setIsAttached(true);}
                             }}
	            for (i=0;i<pointArray.length;i++) {
                        aPixel = (PointPixel) pointArray[i];
	                //aPixel.setYValue(coefficients[0] * aPixel.getRow()
			                      //+ coefficients[1]);
// experiment
                        aPixel.setYValue(readLinearScale(aPixel.getRow(), VERTICAL));
}
                    ListIterator lItr3 = indexList.listIterator();
                    while (lItr3.hasNext()) {
                        grandPointsList.add(lItr3.next());}}
                        //linePointsList.add(lItr3.next());}}
                //Object[] linePointsArray;
                //linePointsArray = linePointsList.toArray();
	        //ColumnCompare cC2 = new ColumnCompare();
	        //Arrays.sort(linePointsArray,cC2);
//System.out.println("yes?");
                //linePointsArray = (Object[])OnePerColumn(linePointsArray);
//System.out.println("yes!");
                //lineList.add(linePointsArray); }}
                }}
        Object[] linePointsArray;
        linePointsArray = grandPointsList.toArray();
        ColumnCompare cC2 = new ColumnCompare();
        Arrays.sort(linePointsArray,cC2);
        linePointsArray = (Object[])OnePerColumn(linePointsArray);
        lineList.add(linePointsArray);
	return lineList;}

    
    public class WordCompare implements Comparator {
      public int compare(Object x, Object y) {
        int xx, yy, offset;
//System.out.println("comparing " + ((Word)x).getText() + " " + ((Word)y).getText());
        xx = ((Word)x).getLowerRight().getRow();
        yy = ((Word)y).getUpperLeft().getRow();
        if (((Word)x).getText().equals(".")
            || ((Word)y).getText().equals(".")
            || ((Word)x).getText().equals(",")
            || ((Word)y).getText().equals(",")
            || ((Word)x).getText().equals("'")
            || ((Word)y).getText().equals("'"))
            offset = ((Word)x).getFontSize()/2;
        else offset = 0;
//System.out.println("xx = "+ xx + " yy = " + yy + " offset = " + offset);
        if (xx + offset < yy) return -1;
        //if (xx + offset < yy){System.out.println("<"); return -1;}
        yy = ((Word)y).getLowerRight().getRow();
        xx = ((Word)x).getUpperLeft().getRow();
//System.out.println("xx = "+ xx + " yy = " + yy + " offset = " + offset);
        if (yy + offset < xx) return 1;
        //if (yy + offset < xx){System.out.println(">="); return 1;}
        xx = ((Word)x).getLowerRight().getColumn();
        yy = ((Word)y).getUpperLeft().getColumn();
//System.out.println("xx = "+ xx + " yy = " + yy + " offset = " + offset);
        if (xx < yy) return -1;
        //if (xx <= yy){System.out.println("<"); return -1;}
        else return 1;}}
        //else{System.out.println(">="); return 1;}}}
    
    public class VWordCompare implements Comparator {
      public int compare(Object x, Object y) {
        int xx, yy;
        xx = ((Word)x).getLowerRight().getColumn();
        yy = ((Word)y).getUpperLeft().getColumn();
        if (xx < yy) return -1;
		else if (xx==yy) return 0;
        yy = ((Word)y).getLowerRight().getColumn();
        xx = ((Word)x).getUpperLeft().getColumn();
        if (yy < xx) return 1;
		else if (xx==yy) return 0;
        xx = ((Word)x).getUpperLeft().getRow();
        yy = ((Word)y).getLowerRight().getRow();
        if (xx > yy) return -1;
		else if (xx==yy) return 0;
        else return 1;}}

    public class LineColumnCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((ConnectedLine)x).getUpperLeft().getColumn();
	    cy = ((ConnectedLine)y).getUpperLeft().getColumn();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}

    public class TickColumnCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((Primitive)x).getBeginPoint().getColumn();
	    cy = ((Primitive)y).getBeginPoint().getColumn();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}

    public class TickRowCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((Primitive)x).getBeginPoint().getRow();
	    cy = ((Primitive)y).getBeginPoint().getRow();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}

    public class ColumnCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((PointPixel)x).getColumn();
	    cy = ((PointPixel)y).getColumn();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}
    	
    public class BarColumnCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((Rectangle)x).getUpperLeft().getColumn();
	    cy = ((Rectangle)y).getUpperLeft().getColumn();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}
    
    public class BarBaseColumnCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((Rectangle)x).getLowerRight().getColumn();
	    cy = ((Rectangle)y).getLowerRight().getColumn();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}
    
    public class BarRowCompare implements Comparator {
	public int compare(Object x, Object y) {
	    int cx, cy;
	    cx = ((Rectangle)x).getLowerRight().getRow();
	    cy = ((Rectangle)y).getLowerRight().getRow();
	    if (cx<cy) return -1;
		else if (cx==cy) return 0;
	    else return 1;}}

    int measurementPosition(PointPixel aPixel, int direction) {
      if (direction==VERTICAL) return aPixel.getRow();
      else return aPixel.getColumn();}

    int measurementPosition(Primitive aPrimitive, int direction) {
      if (direction==VERTICAL) return aPrimitive.getBeginPoint().getRow();
      else return aPrimitive.getBeginPoint().getColumn();}

	
    void makeLinearScale(int direction) {
	LinkedList ticks;
//System.out.println("in makeLinearScale dir = " + direction);
//kluge
//direction = VERTICAL;
        if (direction == VERTICAL) ticks = vAxis.getTickList();
        else ticks = hAxis.getTickList();
	Object[] anArray = ticks.toArray();
        if (direction==HORIZONTAL) {
          TickColumnCompare tcC = new TickColumnCompare();
          Arrays.sort(anArray,tcC);}
        else {
          TickRowCompare trC = new TickRowCompare();
          Arrays.sort(anArray,trC);}

// remove duplicate ticks

        LinkedList thinnedTicks = new LinkedList();
	int i;
        for (i = anArray.length - 1;i >= 0;i--) {
          if (i == 0 || measurementPosition((Primitive)anArray[i-1],direction) !=
                        measurementPosition((Primitive)anArray[i],direction))
            thinnedTicks.add(anArray[i]);}
        anArray = thinnedTicks.toArray();
        if (direction == VERTICAL) vAxis.setTickList(thinnedTicks);
        else hAxis.setTickList(thinnedTicks);

//System.out.println("getting begin points");
	for (i=0;i<anArray.length;i++) {
	    anArray[i] = ((Primitive)anArray[i]).getBeginPoint();}
	
	Word aWord;
	PointPixel aPixel;
	String str;
//System.out.println("getting labels for ticks");
//System.out.println("length of anArray");
//System.out.println(anArray.length);
	for(i=0;i<anArray.length;i++) {
//System.out.println("i = " + i);
	    aPixel = (PointPixel)anArray[i];
//System.out.println("got a pixel");
	    aPixel.initAnnotationWords();
            aPixel.initLabelWords();
//System.out.println("init its annotation words");
	    Iterator itrW = words.values().iterator();
//System.out.println("got iterator");
	    while (itrW.hasNext()) {
//System.out.println("in while loop");
		aWord = (Word) itrW.next();
		str = aWord.getText();
//System.out.println("str = " + str);
		if ((direction==VERTICAL && aWord.justLeftOf(aPixel)) ||
                    (direction==HORIZONTAL && aWord.justBelow(aPixel,hAxis))) {
//System.out.println("word = " + aWord);
		    str = aWord.getText();
//System.out.println("str = " + str);
		    if (str.substring(str.length()-1).equals("%")) {
                        aPixel.setUnit("%");
		        str = str.substring(0,str.length()-1);}
                    if (str.substring(0,1).equals("$")) {
                        aPixel.setUnit("$");
                        str = str.substring(1);}
                    try {
		    aPixel.addToAnnotationWords(aWord);
/*  Don't know what this is all about
                    LinkedList labelWordList = aWord.getInTextPiece().getWords();
                    ListIterator lItr = labelWordList.listIterator();
                    Word aWord2  = (Word)lItr.next();
                    if (lItr.hasNext()) {
                      aWord2 = (Word)lItr.next();
                      aPixel.setScale(aWord2.getText());}
*/
//System.out.println("Attaching " + aWord.getText());
		    aWord.setIsAttached(true);
                    aWord.getInTextPiece().getInTextBlock().setIsAttached(true);
                    if (aPixel.getAnnotationTextBlock() == null) {
                        aPixel.setAnnotationTextBlock(
                          aWord.getInTextPiece().getInTextBlock());}
//System.out.println("about to parseDouble");
                    aPixel.setYValue(Double.parseDouble(str));
//System.out.println("succeeded");
                    }
                    catch(Exception e) {}}}}

//System.out.println("out of for loop");
        if (direction==HORIZONTAL) {
          ColumnCompare cC = new ColumnCompare();
          Arrays.sort(anArray,cC);}

//System.out.println("past horizontal direction case");

/*  simplistic way to get coefficients */

        if (direction == VERTICAL) VerticalTickPoints = anArray;
        else HorizontalTickPoints = anArray;
//System.out.println("got " + anArray.length + " tickPoints");
	int j;
	j = 0;
	int[] r = new int[3];
	double[] v = new double[3];
	for (i=0;i<anArray.length;i++) {
//System.out.println("i = " + i);
	    aPixel = (PointPixel)anArray[i];
	    if (aPixel.getAnnotationWords().size() != 0) {
		//if (direction==VERTICAL) r[j] = aPixel.getRow();
                //else r[j] = aPixel.getColumn();
		//str = ((Word)aPixel.getAnnotationWords().getFirst()).getText();
                str = aPixel.getAnnotationTextBlock().getText();
//System.out.println("str = " + str);
		if (str.substring(str.length()-1).equals("%")) {
		    str = str.substring(0,str.length()-1);}
                if (str.substring(0,1).equals("$")) {
                    str = str.substring(1);}
                try {
		v[0] = Double.parseDouble(str);
                aPixel.setYValue(v[0]);
                aPixel.setHasValue();
		j++;
                }
                catch(Exception e) {aPixel.setYValue(0.0);}
                }}
//System.out.println("j = " + j);
        //if (j < 2) {System.out.println("Not enough labeled ticks on axis.");}
        if (j > 1)
            {
                ticksHaveValues = true;
                if (direction == HORIZONTAL) hTicksHaveValues = true;
                if (direction == VERTICAL) vTicksHaveValues = true;
            }

// now assign values to unannotated ticks

        if (j > 1) {
	  for (i=0;i<anArray.length;i++) {
//System.out.println("i " + i);
	      aPixel = (PointPixel)anArray[i];
              PointPixel aPixel1;
              PointPixel aPixel2;
	      if (!aPixel.hasValue()) {
                getRefPoints(r,i,anArray,direction);
                aPixel1 = (PointPixel)anArray[r[1]];
                aPixel2 = (PointPixel)anArray[r[2]];
                r[0] = measurementPosition(aPixel,direction);
                r[1] = measurementPosition(aPixel1,direction);
                r[2] = measurementPosition(aPixel2,direction);
                v[1] = aPixel1.getYValue();
                v[2] = aPixel2.getYValue();
//System.out.println("rs  = " + r[0] + " " + r[1] + " " + r[2]);
//System.out.println("vs  = " + v[1] + " " + v[2]);
                if (r[0] == r[1]) v[0] = v[1];
                else if (r[0] == r[2]) v[0] = v[2];
                else {
                  v[0] = v[1] + (r[0] - r[1]) * (v[2] - v[1])/(r[2] - r[1]);}
                aPixel.setYValue(v[0]);
             }}}

/* end simplistic way */

}

void getRefPoints(int[] v,int i,Object[] anArray,int direction){
  int[] r = new int[3];
  PointPixel aPixel,aPixel1,aPixel2;
  aPixel = (PointPixel)anArray[i];
/*
  if (direction==VERTICAL) r[0] = aPixel.getRow();
    else r[0] = aPixel.getColumn();
  if (i > 0) {
    v[1] = i - 1;  // use previous tick, which has value by now
    aPixel1 = (PointPixel)anArray[v[1]];
    if (direction==VERTICAL) r[1] = aPixel1.getRow();
      else r[1] = aPixel1.getColumn();
    if (r[0] == r[1]) { //double tick
      v[2] = v[1];
      return;}}
*/
  int j;
  v[1] = -1;
  for (j = i - 1;j >= 0; j--) {
    aPixel1 = (PointPixel)anArray[j];
    if (aPixel1.hasValue()) {
      v[1] = j;
      break;}}
  v[2] = -1;
  for (j = i + 1;j < anArray.length;j++) {
    aPixel2 = (PointPixel)anArray[j];
    if (aPixel2.hasValue()) {
      v[2] = j;
      break;}}
  if (v[1] == -1) { // no valued tick on one side
      v[1] = v[2];
      v[2] = -1;
      for (j = v[1] + 1;j < anArray.length;j++) {
        aPixel2 = (PointPixel)anArray[j];
        if (aPixel2.hasValue()) {
          v[2] = j;
          break;}}}
  else if (v[2] == -1) { // no valued tick on the other side
      v[2] = v[1];
      v[1] = -1;
      for (j = v[2] - 1;j >= 0; j--) {
        aPixel1 = (PointPixel)anArray[j];
        if (aPixel1.hasValue()) {
          v[1] = j;
          break;}}}

/*
  if (v[2] == -1) { // no labelled tick found below i; i must be > 1
    v[2] = i - 2;
    return;}
  else if (i == 0) { // v[1] hasn't been set yet
    v[1] = v[2];
    v[2] = -1;
    for (j = j + 1;j < anArray.length;j++) {
      aPixel2 = (PointPixel)anArray[j];
      if (aPixel2.getAnnotationWords().size()!= 0) {
        v[2] = j;
        break;}}
*/
  if (v[1] == -1 || v[2] == -1) System.out.println("PROBLEM! not enough labelled ticks");
  return;}

boolean isBold(Word aWord, Region[] allRegions, int[][] firstPixelLabel) {
//System.out.println("in isBold");
  double r;
  int n;
  boolean bold;
  String s;
  s = aWord.getFont();
  if (s!="") return s.charAt(1) == 'B';
System.out.println("NO FONT INFO FOUND");
  bold = false;
  n = 0;
  r = 0;
//System.out.println(aWord.getText());
  Object[] charArray = aWord.getCharacters().toArray();
  int i;
  int label;
  Region reg;
            for (i = 0; i < charArray.length; i++) {
                label = ((Integer)charArray[i]).intValue();
                reg = allRegions[label];
                charArray[i] = reg;}

  for(i=0;i < charArray.length;i++) {
    PointPixel ul = ((Region)charArray[i]).getUpperLeft();
    PointPixel lr = ((Region)charArray[i]).getLowerRight();
    int nP;
    int h;
    int w;
   
    //nP = ((Region)charArray[i]).getNumPixels();
    nP = countPixels(((Region)charArray[i]).getRegion(),ul,lr,firstPixelLabel);
//System.out.println("nP= " + nP + "  ulrow= " + ul.getRow() + "  ulcol= " + ul.getColumn());
//System.out.println("lrrow= " + lr.getRow() + "  lrcol= " + lr.getColumn());
    if (((Region)charArray[i]).getIsFilledArea()) {
      if (lr.getRow() - ul.getRow() > 1 && lr.getColumn() - ul.getColumn() > 1)
        bold = true;}
    else if (lr.getRow() > ul.getRow() && lr.getColumn() > ul.getColumn()){
      r += ((double)nP)/ ((lr.getRow() - ul.getRow() + 1)*(lr.getColumn() - ul.getColumn() + 1));
      n++;}}
//System.out.println(Double.toString(r)+ "  " + n + "  " + Double.toString(r/n));
  if (bold) return true;
  if (n > 0 && r/n > .5) return true;
  return false;}

double axisLength(PointPixel beginPoint, PointPixel endPoint) {
  int x1,x2,y1,y2;
  x1 = beginPoint.getColumn();
  y1 = beginPoint.getRow();
  x2 = endPoint.getColumn();
  y2 = endPoint.getRow();
  if (x1 == x2)  // vertical axis
    return (y2 - y1 + 1)*2.54/72; // length in centimeters
  else if (y1 == y2) // horizontal axis
    return (x2 - x1 + 1) * 2.54 / 72;
  else return -1000; // shouldn't happen
  }

boolean hasYTitleWords() {
    //Word aWord;
    //Iterator itr = words.values().iterator();
    //while (itr.hasNext()) {
	//aWord = (Word) itr.next();
	//if (aWord.getIsYTitle()) 
	    //return true;}
    //return false;}
    return !YTitle.isEmpty();}

boolean hasXTitleWords() { 
    //Word aWord;
    //Iterator itr = words.values().iterator();
    //while (itr.hasNext()) {
	//aWord = (Word) itr.next();
//System.out.println("in hasXTitleWords: " + aWord.getText());
	//if (aWord.getIsXTitle()) 
	    //return true;}
   //return false;}
    return !XTitle.isEmpty();}

void writeTickmarks(BufferedWriter ostream, int direction) {
        Object[] anArray;
        if (direction == VERTICAL) anArray = VerticalTickPoints;
        else anArray = HorizontalTickPoints;
        int i;
                DecimalFormat formater = new DecimalFormat("##0.00");

      try {
        for (i = 0;i < anArray.length;i++) {
//System.out.println("i = " + i);
          ostream.write("                <Tickmark>\n");
          PointPixel aPixel = (PointPixel)anArray[i];
          if (aPixel.getAnnotationWords().size() > 0) {
              ostream.write("                        <TickLabelled>");
              ostream.write("true");
              ostream.write("</TickLabelled>\n");
              ostream.write("                        <TickLabel>");
/*
              LinkedList wordList = aPixel.getAnnotationWords();
              ListIterator wItr = wordList.listIterator(0);
              while (wItr.hasNext()) {
                  ostream.write(((Word)wItr.next()).getText());}
*/
              ostream.write(aPixel.getAnnotationTextBlock().getText());
              ostream.write("</TickLabel>\n"); }
          else ostream.write("                        <TickLabelled>false</TickLabelled>\n");
          //else if (direction == HORIZONTAL && !hTicksHaveValues) {
               //writeWords(aPixel.getLabelWords(),ostream);}
          if ((direction == VERTICAL && vTicksHaveValues) ||
              (direction == HORIZONTAL && hTicksHaveValues)) {
              ostream.write("                        <TickValue>");
              ostream.write(formater.format(aPixel.getYValue()));
              ostream.write("</TickValue>\n");}
          if(!aPixel.getUnit().equals("")) {
            ostream.write("                        <TickUnit>");
            ostream.write(aPixel.getUnit());
            ostream.write("</TickUnit>\n");}
          if(!aPixel.getScale().equals("")) {
            ostream.write("                        <TickScale>");
            ostream.write(aPixel.getScale());
            ostream.write("</TickScale>\n");}
	  NumberFormat nf = NumberFormat.getInstance();

          ostream.write("                        <GridLine>");
           //fake line
          ostream.write("false");
          ostream.write("</GridLine>\n");
          if (direction ==VERTICAL) {
            ostream.write("                        <Coord><X>0</X><Y>");
            ostream.write(nf.format(origin.getRow() - aPixel.getRow()));
            ostream.write("</Y></Coord>\n");}
          else {
            ostream.write("                        <Coord><X>");
            ostream.write(nf.format(aPixel.getColumn() - origin.getColumn()));
            ostream.write("</X><Y>0</Y></Coord>\n");}
          ostream.write("                </Tickmark>\n");}}
     catch (Exception e) {System.out.println(e.getMessage());}}

int countPixels(int label,PointPixel ul, PointPixel lr, int[][] firstPixelLabel) {
  int numPixels = 0;
  int i,j;
  for (i = ul.getRow(); i<=lr.getRow();i++) 
    for (j = ul.getColumn(); j<=lr.getColumn();j++)
       if (label == firstPixelLabel[i][j]) numPixels++;
  return numPixels;}

LinkedList findChartXTitle() {
        LinkedList XTitleWords = new LinkedList();
        Word aWord;
        Iterator itr = words.values().iterator();
        while (itr.hasNext()) {
            aWord = (Word)itr.next();
            if (aWord.getOrientation() == Primitive.HORIZONTAL) {
              if (aWord.getUpperLeft().getRow() > 
                  (hAxis.getBeginPoint().getRow() + imageHeight)/2)
                //aWord.setIsYTitle(true);
// setting properties via iterators doesn't work
                XTitleWords.add(aWord); } }
// This seems to be a problem
       //if (XTitleWords.size() > 1) return XTitleWords;
       //else return new LinkedList();}
       return XTitleWords;}

/*
* looks at image to get the true color of the interior of a
* rectangle.  (White bars with black borders ended up as black
* bars.)
*/

  public void trueColorRectangles(BWImageG image) {
    ListIterator lItr = rectangles.listIterator(0);
    while (lItr.hasNext()) {
      Rectangle aRectangle = ((Rectangle)lItr.next());
      aRectangle.setColor(image.getImageValue(aRectangle.getUpperLeft().getRow() + 1,
                                              aRectangle.getUpperLeft().getColumn() + 1));}
  }

  private LinkedList findHorizontalTextPieces (Object[] wordArray) {
System.out.println("IN FINDHORIZONTALTEXTPIECES");
System.out.println("wordArray length = " + wordArray.length);
    LinkedList pieceList = new LinkedList();
    LinkedList Text;
    String font = "";
    int size = 0;
    int i;
    Text = new LinkedList();
    for (i=0;i< wordArray.length;i++) {
System.out.println("i = " + i);
      Word aWord = (Word)wordArray[i];
        aWord.setNearestBar(dataRectanglesList,
                            barOrientation,
                            barBaseLocation,
                            VERTICAL);
System.out.println("word = " + aWord.getText());
System.out.println("nearest bar " + aWord.getNearestBar());
System.out.println("font = " + aWord.getFont());
System.out.println(aWord.getUpperLeft());
System.out.println(aWord.getLowerRight());
      if (Text.isEmpty()) {
        Text.addFirst(aWord);
        size = aWord.getFontSize();
System.out.println("size " + size);
        font = aWord.getFont();}
      else {
        Word prevWord = (Word)Text.getLast();
        String aText = prevWord.getText();
System.out.println("aText.length = " + aText.length());
        String aChar = "?";
        if (aText.length() > 0) aChar =  aText.substring(aText.length() - 1);
System.out.println("aText = " + aText + "  aChar = " + aChar);
        boolean prevIsThinChar = aChar.equals(".") ||
                                 aChar.equals(",") ||
                                 aChar.equals("'") ||
                                 //for Burns but no change
                                 aChar.equals("1") ||
                                 prevWord.isTriangle();
        boolean thisIsThinChar = aWord.getText().equals(".")
                                 || aWord.getText().equals(":")
                                 || aWord.getText().equals("'")
                                 || aWord.getText().equals(",");

System.out.println("prevthin = "+prevIsThinChar);
System.out.println("thisthin = "+thisIsThinChar);
  // periods are sometimes the same in different fonts

        if ((aWord.getFont().equals(font) || thisIsThinChar ||
                // for Burns
                prevIsThinChar ||
                //(prevIsThinChar && aText.equals(aChar)) ||
                prevWord.isTriangle())
            && aWord.getNearestBar() == prevWord.getNearestBar()
            &&
            Math.abs(aWord.getUpperLeft().getRow() -
                     prevWord.getUpperLeft().getRow()) < size
            &&
            aWord.getUpperLeft().getColumn() >
              prevWord.getLowerRight().getColumn() 
            &&
             (((prevIsThinChar || thisIsThinChar) &&
                (aWord.getUpperLeft().getColumn() -
                 prevWord.getLowerRight().getColumn()) < 
                 1.5 * size) || //for Burns no change
                 //1.2 * size) ||   // 1.1 is too small, 2 is too large
              (!prevIsThinChar &&
                (aWord.getUpperLeft().getColumn() -
                 prevWord.getLowerRight().getColumn()) <=
                 //size))) {
                 //for Burns but no change
                 0.9*size))) {
                 //0.6*size))) {

//The above coefficient is very sensitive.  In IUI-1a, if it is
//0.8, The Description gets broken up into several descriptions;
//if it is 1.0, the labels '04 and '05 get merged as one label.
//Some other solution needs to be found.

// disambiguate symmetric characters
          prevWord.setIsVerticalWord(false);
          aWord.setIsVerticalWord(false);
          if (prevIsThinChar && (aText.equals(aChar) || prevWord.isTriangle())) {
              size = aWord.getFontSize();
//System.out.println("size " + size);
              font = aWord.getFont();}
              
          Text.addLast(aWord);}
        else if (prevWord.getText().equals(".") &&
                 aWord.getText().equals("."))
          prevWord.setText(":");
        else if (prevWord.getText().equals(",") &&
                 aWord.getText().equals(","))
          prevWord.setText(":");
        else {
          //found one text piece
System.out.println("found a text piece");
          //Collections.reverse(Text);
          pieceList.addLast(new TextPiece(Text));
          Text = new LinkedList();
          Text.addFirst(aWord); 
          size = aWord.getFontSize();
System.out.println("size " + size);
          font = aWord.getFont();} } }
    if (!Text.isEmpty()) {
          //Collections.reverse(Text);
System.out.println("adding new piece with text "+Text);
          pieceList.addLast(new TextPiece(Text));}
    return pieceList;}

/*
* Groups the text pieces together into blocks of text
*/

private LinkedList findHorizontalTextBlocks(LinkedList pieceList) {
  // the pieces are already in order: top-down and left-right
  LinkedList blockList = new LinkedList();
  Object[] pieceArray = pieceList.toArray();
  int i,j;
  for (i = 0; i < pieceArray.length;i++) {
    TextPiece aPiece = (TextPiece)pieceArray[i];
    if (!aPiece.getInABlock()) {
      aPiece.setInABlock();
      LinkedList oneBlock = new LinkedList();
      oneBlock.add(aPiece);
      for (j = i + 1;j < pieceArray.length;j++) {
        aPiece = (TextPiece)pieceArray[j];
        if (((TextPiece)oneBlock.getLast()).justAbove(aPiece)) {
          aPiece.setInABlock();
          oneBlock.add(aPiece);}}
      blockList.add(new TextBlock(oneBlock));}}
  return blockList;}

  private LinkedList findVerticalTextPieces (Object[] wordArray) {
//System.out.println("IN FINDVERTICALTEXTPIECES");
//System.out.println("wordArray length = " + wordArray.length);
    LinkedList pieceList = new LinkedList();
    LinkedList Text;
    String font;
    int i;
    Text = new LinkedList();
    for (i=0;i< wordArray.length;i++) {
//System.out.println("i = " + i);
      Word aWord = (Word)wordArray[i];
//System.out.println("word = " + aWord.getText());
//System.out.println("font = " + aWord.getFont());
//System.out.println(aWord.getUpperLeft());

// filter out disambiguated symmetric characters that are considered horizontal
      if (!aWord.getIsVerticalWord()) continue;
      if (Text.isEmpty()) {
        Text.addFirst(aWord);
        font = aWord.getFont();}
      else {
        Word prevWord = (Word)Text.getLast();
        if (aWord.getFont().equals(prevWord.getFont())
            &&
           Math.abs(aWord.getAxisBeginPoint().getColumn() -
                     prevWord.getAxisBeginPoint().getColumn()) < 6
            &&
            (aWord.getAxisBeginPoint().getRow() -
             prevWord.getAxisEndPoint().getRow()) < 
             Integer.parseInt(aWord.getFont().substring(2))) {
          Text.addLast(aWord);}
        else {
          //found one text piece
//System.out.println("found a text piece");
          //Collections.reverse(Text);
          pieceList.addLast(new TextPiece(Text));
          Text = new LinkedList();
          Text.addFirst(aWord);} }}
    if (!Text.isEmpty()) {
          //Collections.reverse(Text);
          pieceList.addLast(new TextPiece(Text));}
    return pieceList;}

/*
* Groups the text pieces together into blocks of text
*/

private LinkedList findVerticalTextBlocks(LinkedList pieceList) {
  // the pieces are already in order: top-down and left-right
  LinkedList blockList = new LinkedList();
  Object[] pieceArray = pieceList.toArray();
  int i,j;
  for (i = 0; i < pieceArray.length;i++) {
    TextPiece aPiece = (TextPiece)pieceArray[i];
    if (!aPiece.getInABlock()) {
      aPiece.setInABlock();
      LinkedList oneBlock = new LinkedList();
      oneBlock.add(aPiece);
      for (j = i + 1;j < pieceArray.length;j++) {
        aPiece = (TextPiece)pieceArray[j];
        if (((TextPiece)oneBlock.getLast()).justLeftOf(aPiece)) {
          aPiece.setInABlock();
          oneBlock.add(aPiece);}}
      blockList.add(new TextBlock(oneBlock));}}
  return blockList;}

/*
*  examine the Horizontal Text Blocks and decide whether they are
*  caption, desription, text in graphic or text below graphic.
*/
    
  void classifyTextBlocks() {
    int i,n,fontSize;
    int yb, ye;
    TextBlock tb;
    yb = vAxis.getUpperLeft().getRow();
    ye = vAxis.getLowerRight().getRow();
System.out.println("yb in classifyTextBlocks " + yb);
System.out.println("ye in classifyTextBlocks " + ye);
    fontSize = 0;
    Caption = new LinkedList();
    Descriptions = new LinkedList();
    TextInGraphic = new LinkedList();
    TextUnderGraphic = new LinkedList();
    XTitle = new LinkedList();
    YTitle = new LinkedList();
    ListIterator lItr = HorizontalTextBlocks.listIterator();
    while (lItr.hasNext()) {
          tb = (TextBlock)lItr.next();
          if (tb.getIsBold()){
            i = tb.getFontSize();
            if (i > fontSize) {
              Caption = new LinkedList();
              Caption.addFirst(tb);
              fontSize = i;}
            else if (i == fontSize) {
              Caption.addLast(tb);}}}
    lItr = Caption.listIterator();
    while (lItr.hasNext()) {
      tb = (TextBlock)lItr.next();
//System.out.println("caption attaching " + tb.getText());
      tb.setIsAttached(true);}
    lItr = HorizontalTextBlocks.listIterator();
    while (lItr.hasNext()) {
      tb = (TextBlock)lItr.next();
      if (tb.getLowerRight().getRow() == 1) continue; //get rid of Peng's dots
      if (tb.getText().equals(",")) continue; //slip over spurious block made
                                              // by top dot in colon.(L21nb)
//System.out.println("H block >" + tb.getText() + "<");
//System.out.println(tb.getFontSize());
//System.out.println(fontSize);
      if (!tb.getIsBold() || tb.getFontSize() < fontSize) {
          if (tb.getFirstWord().isTriangle()) {
              if (tb.getFirstWord().getText().equals("[^]")) {
//System.out.println("found [^]");
                  tb.setText(tb.getText().substring(3).trim());
                  YTitle.addLast(tb);
              }
              else if (tb.getFirstWord().getText().equals("[>]")) {
//System.out.println("found [>]");
                  tb.setText(tb.getText().substring(3).trim());
                  XTitle.addLast(tb);
              }
          }
        else if (tb.getLowerRight().getRow() < yb &&
          !isNumber(tb.getText())) {
//System.out.println("added to Descriptions");
          Descriptions.addFirst(tb);}
        else if (tb.getLowerRight().getRow() < ye &&
          !isNumber(tb.getText())) {
//System.out.println("added to textingraphic");
//System.out.println("text is " + tb.getText());
          TextInGraphic.addLast(tb);}
        //else if (tb.getLowerRight().getRow() > ye + 10) {
        //else if (tb.getLowerRight().getRow() > ye + 20) {
        else if (tb.getLowerRight().getRow() > ye + 30) {
          if (XTitle.isEmpty() &&
                 !tb.getText().substring(0,1).equals("*")) 
{
System.out.println("added to XTitle");
             XTitle.addLast(tb);
}
          else
    {
//System.out.println("added to textunder graphic");
            //tb.setIsAttached(false);
             TextUnderGraphic.addLast(tb);}}}}
  }

    public static Boolean isNumber(String txt) {
        txt = removeCommas(txt);
     
//System.out.println("txt =" + txt + "=" + " " + txt.length());
        if (txt.length() > 0 && txt.substring(txt.length()-1).equals("%")) {
	    txt = txt.substring(0,txt.length()-1);
        }
        if (txt.length() > 0 && txt.substring(0,1).equals("$")) {
            txt = txt.substring(1);
        }
        try {
//System.out.println("number? " + txt);
            Double r = Double.parseDouble(txt);
//System.out.println("yes");
            return true;
        } catch (Exception e) {return false;}
    }

    public static String removeCommas(String txt) {
        StringBuffer newTxt = new StringBuffer(txt);
        int i;
        for (i = 0; i < newTxt.length() ; i++) {
            if (newTxt.charAt(i) == ',') {
                newTxt.deleteCharAt(i);
            }
        }
        return newTxt.toString();
    }

  public void reclassifyBlocks() {
    LinkedList newList = new LinkedList();
    ListIterator lItr = Descriptions.listIterator();
    TextBlock aBlock;
    while (lItr.hasNext()) {
      aBlock = (TextBlock)lItr.next();
      if (!aBlock.getIsAttached()) newList.addLast(aBlock);}
    Descriptions = newList;
    newList = new LinkedList();
    lItr = TextInGraphic.listIterator();
    while (lItr.hasNext()) {
      aBlock = (TextBlock)lItr.next();
      if (!aBlock.getIsAttached()) newList.addLast(aBlock);}
    TextInGraphic = newList;
    newList = new LinkedList();
    lItr = TextUnderGraphic.listIterator();
    while (lItr.hasNext()) {
      aBlock = (TextBlock)lItr.next();
//System.out.println(aBlock.getText());
      if (!aBlock.getIsAttached()) newList.addLast(aBlock);}
    TextUnderGraphic = newList;
//System.out.println(TextUnderGraphic.size());
    newList = new LinkedList();
    lItr = VerticalTextBlocks.listIterator();
    while (lItr.hasNext()) {
      aBlock = (TextBlock)lItr.next();
      if (!aBlock.getIsAttached()) newList.addLast(aBlock);}
    VerticalTextBlocks = newList;
    //find axis titles
    //XTitle = new LinkedList();
    //YTitle = new LinkedList();
    if (XTitle.size() > 0) {
        newList = new LinkedList();
        lItr = XTitle.listIterator();
        while (lItr.hasNext()) {
            aBlock = (TextBlock)lItr.next();
            if (!aBlock.getIsAttached()) newList.addLast(aBlock);}
        XTitle = newList;}
    int col = vAxis.getBeginPoint().getColumn();
    lItr = VerticalTextBlocks.listIterator();
    while (lItr.hasNext()) {
      aBlock = (TextBlock)lItr.next();
      if (aBlock.getLowerRight().getColumn() < col) YTitle.addLast(aBlock);}
    if (!Descriptions.isEmpty()) {
      aBlock = (TextBlock)Descriptions.getFirst();
//System.out.println("considering " + aBlock.getText() + " for axis label");
//System.out.println("vAxis begin point " + vAxis.getBeginPoint());
//System.out.println("block upperLeff " + aBlock.getUpperLeft());
      if (aBlock.justAbove(vAxis.getBeginPoint())) {
        if (chartType == BARCHART && barOrientation == HORIZONTAL && XTitle.isEmpty()) {
          XTitle.addFirst(aBlock);
          Descriptions.removeFirst();}
        else if (chartType == BARCHART && barOrientation == VERTICAL && YTitle.isEmpty()) {
          YTitle.addFirst(aBlock);
          Descriptions.removeFirst();}}}
  }


    public void makeTextBlocks() {
	    Word aWord;
	    Iterator itr = words.values().iterator();
	    LinkedList TitleWords = new LinkedList();
            LinkedList HorizontalWords = new LinkedList();
            LinkedList VerticalWords = new LinkedList();
//System.out.println("in makeTextBlocks");
            while (itr.hasNext()) {
	        aWord = (Word) itr.next();
//System.out.println("word makeTextBlocks " + aWord.getText() + " " + aWord.getIsHorizontalWord() + aWord.getIsVerticalWord());
                if (aWord.getText().equals(",")) continue;  // get rid of , in
                                                           //L21nb
                if (aWord.getIsHorizontalWord())
                  HorizontalWords.addFirst(aWord);
                if (aWord.getIsVerticalWord())
                  VerticalWords.addFirst(aWord);
	        if (aWord.getIsTitle()) {
		    TitleWords.addFirst(aWord);}}

// Group words into text blocks
            
            Object[] HWordsArray = HorizontalWords.toArray();
            WordCompare hwc = new WordCompare();
            Arrays.sort(HWordsArray,hwc);

//Peek at the contents of HWordsArray
//System.out.println("horizontal words");
            //for (int i=0;i<HWordsArray.length;i++) {
              //aWord = (Word)HWordsArray[i];
//System.out.println(aWord.getText() + " " + aWord.getUpperLeft() + " " +
//aWord.getLowerRight()); }

            Object[] VWordsArray = VerticalWords.toArray();
            VWordCompare vwc = new VWordCompare();
            Arrays.sort(VWordsArray,vwc);
            HorizontalTextPieces = findHorizontalTextPieces(HWordsArray);
            HorizontalTextBlocks = findHorizontalTextBlocks(HorizontalTextPieces);
            VerticalTextPieces = findVerticalTextPieces(VWordsArray);
            VerticalTextBlocks = findVerticalTextBlocks(VerticalTextPieces);
    }



    public double readLinearScale(int val, int direction) {
        int i;
	Object[] anArray;
        if (direction == VERTICAL) anArray = VerticalTickPoints;
        else anArray = HorizontalTickPoints;
        int[] r = new int[3];
        double[] v = new double[3];
        r[0] = val;
        if (r[0] > measurementPosition((PointPixel)anArray[0],direction)) {
            r[1] = measurementPosition((PointPixel)anArray[0],direction);
            v[1] = ((PointPixel)anArray[0]).getYValue();
            r[2] = measurementPosition((PointPixel)anArray[1],direction);
            v[2] = ((PointPixel)anArray[1]).getYValue();
            v[0] = v[1] + (r[0] - r[1]) * (v[2] - v[1])/(r[2] - r[1]);

//System.out.println("r[0] " + r[0] + "r[1] " + r[1] + "v[1] " + v[1] + "r[2] " + r[2] + "v[2] " + v[2] + "v[0] " + v[0] + "\n");


}
        else {
            for(i = anArray.length - 1; i >= 0; i--) {
//System.out.println("i = " + i + " v = " +
//((PointPixel)anArray[i]).getYValue()
//+ " r = " + measurementPosition((PointPixel)anArray[i],direction));
                if (r[0] <= measurementPosition((PointPixel)anArray[i],direction)) break;}
            if (i < anArray.length - 1) {
                r[1] = measurementPosition((PointPixel)anArray[i],direction);
                v[1] = ((PointPixel)anArray[i]).getYValue();
                r[2] = measurementPosition((PointPixel)anArray[i+1],direction);
                v[2] = ((PointPixel)anArray[i+1]).getYValue();}
            else {
                r[1] = measurementPosition((PointPixel)anArray[i],direction);
                v[1] = ((PointPixel)anArray[i]).getYValue();
                r[2] = measurementPosition((PointPixel)anArray[i-1],direction);
                v[2] = ((PointPixel)anArray[i-1]).getYValue();}

            v[0] = v[1] + (r[0] - r[1]) * (v[2] - v[1])/(r[2] - r[1]);}
                   
//System.out.println("v[0] = " + v[0]);
            return v[0];
    }

    /**
     * Create and store the point labels.
     *
     * @param allRegions the array of plateaux
     * @param firstPixelLabel image of plateaux labels
     */

    public void findPointLabels(Region[] allRegions,
                                int[][] firstPixelLabel) {
        int i;
        TextBlock aBlock;
        for (i = 1; i< allRegions.length;i++){
            Region rgn = allRegions[i];
            PointPixel boxUL,boxLR, rgnUL, rgnLR;
            rgnUL = rgn.getUpperLeft();
            rgnLR = rgn.getLowerRight();
            int yb = rgnUL.getRow();
            int ye = rgnLR.getRow();
            int xb = rgnUL.getColumn();
            int xe = rgnLR.getColumn();
            ListIterator lItr = rectangles.listIterator(0);
            while (lItr.hasNext()) {
                Rectangle box = (Rectangle)lItr.next();
                boxUL = box.getUpperLeft();
                boxLR = box.getLowerRight();
                int byb = boxUL.getRow();
                int bye = boxLR.getRow();
                int bxb = boxUL.getColumn();
                int bxe = boxLR.getColumn();
                int n = 0;
                if (yb == byb) n++;
                if (ye == bye) n++;
                if (xb == bxb) n++;
                if (xe == bxe) n++;
                if (n == 3) {  //found a point label
                    PointLabel pl = new PointLabel(box, rgn, firstPixelLabel);
                    pointLabelList.add(pl);
/*
	            Iterator itrW = words.values().iterator();
	            while (itrW.hasNext()) {
		        Word aWord = (Word) itrW.next();
                        PointPixel wUL = aWord.getUpperLeft();
                        int wy = wUL.getRow();
                        int wx = wUL.getColumn();
                        if (byb < wy && wy < bye && bxb < wx && wx < bxe) {
//System.out.println("Attaching " + aWord.getText());
                            pl.addToAnnotationWords(aWord);
                            aWord.setIsAttached(true);}}
*/
                    Iterator itrT = HorizontalTextBlocks.listIterator();
                    while (itrT.hasNext()) {
                        aBlock = (TextBlock) itrT.next();
                        PointPixel tUL = aBlock.getUpperLeft();
                        int ty = tUL.getRow();
                        int tx = tUL.getColumn();
                        if (byb < ty && ty < bye && bxb < tx && tx < bxe) {
                        pl.addToAnnotationBlocks(aBlock);
                        aBlock.setIsAttached(true);}}
                    System.out.println("Found point label");}}}
    }

    public void writePointLabel(BufferedWriter ostream, int column) {
//System.out.println("IN WRITEPOINTLABEL");
        boolean annotated = false;
        ListIterator lItr = pointLabelList.listIterator();
        while (lItr.hasNext()) {
//System.out.println("ok");
            PointLabel pl = (PointLabel)lItr.next();
               // Right now, assumes only UP or DOWN orientation
//System.out.println(pl.getArrowColumn() + "  " + pl.getArrowRow() + "  " + column);
            if (column == pl.getArrowColumn()) {
                try {
                    annotated = true;
                    ostream.write("	<AnnotationOnSample>\n");
                    ostream.write("		<Content>");
/*
                    ListIterator wItr = pl.getAnnotationWords().listIterator();
                    while (wItr.hasNext()) {
                        Word aWord = (Word) wItr.next();
                        ostream.write(aWord.getText());}
*/
                    ListIterator bItr = pl.getAnnotationBlocks().listIterator();
                    while (bItr.hasNext()) {
                        TextBlock aBlock = (TextBlock)bItr.next();
                        ostream.write(aBlock.getText());}
                    ostream.write("</Content>\n");
                    //ostream.write("		<Noun/>\n");
                    //ostream.write("		<Verb/>\n");
                    //ostream.write("		<Adjective/>\n");
                    //ostream.write("		<Value/>\n");
                    //ostream.write("		<Time/>\n");
                    ostream.write("		<Color>");
                    ostream.write("0"); // kluge for now.  need to save color in Word
                    ostream.write("</Color>\n");
                    ostream.write("		<Bold>");
                    if (((TextBlock)pl.getAnnotationBlocks().getFirst()).getIsBold()) ostream.write("true");
                    //if (((Word)pl.getAnnotationWords().getFirst()).isBold()) ostream.write("true");
                    else ostream.write("false");
                    ostream.write("</Bold>\n");
                    ostream.write("		<Type/>\n");
                    ostream.write("	</AnnotationOnSample>\n");
                    ostream.write("	<SalienceOnSample/>\n");}
		catch (Exception e) {System.out.println(e.getMessage()+"annotationonsample failed");}
                break;}}
        if (!annotated) {
            try{
                ostream.write("	<AnnotationOnSample>\n");
                ostream.write("		<Content/>\n");
                //ostream.write("		<Noun/>\n");
                //ostream.write("		<Verb/>\n");
                //ostream.write("		<Adjective/>\n");
                //ostream.write("		<Value/>\n");
                //ostream.write("		<Time/>\n");
                ostream.write("		<Color/>\n");
                ostream.write("		<Bold/>\n");
                ostream.write("		<Type/>\n");
                ostream.write("	</AnnotationOnSample>\n");
                ostream.write("	<SalienceOnSample/>\n");}
            catch (Exception e) {System.out.println(e.getMessage());}}}

    /**
     * Reexamines connected lines and unmarks lines that are inside of
     * point labels.  Also reverses the points in the primitives if
     * they are in the wrong order and sorts the primitives in each
     * connected line.
     *
     * @param none
     */

    public void reclassifyLines() {
        LineColumnCompare  lcc = new LineColumnCompare();
        Collections.sort(connectedLines,lcc);
        ListIterator lItr = connectedLines.listIterator();
        while (lItr.hasNext()) {
            ConnectedLine aLine = (ConnectedLine)lItr.next();
            if (aLine.getNoOfPixels() == 1) aLine.setIsDataLine(false);
            int yb = aLine.getUpperLeft().getRow();
            int ye = aLine.getLowerRight().getRow();
            int xb = aLine.getUpperLeft().getColumn();
            int xe = aLine.getLowerRight().getColumn();
            ListIterator pItr = pointLabelList.listIterator();
            while (pItr.hasNext()){
                PointLabel pl = (PointLabel)pItr.next();
                int pyb = pl.getUpperLeft().getRow();
                int pye = pl.getLowerRight().getRow();
                int pxb = pl.getUpperLeft().getColumn();
                int pxe = pl.getLowerRight().getColumn();
                if (yb >= pyb && ye <= pye && xb >= pxb && xe <= pxe) {
                   //line is entirely inside of point label, so not data
                   aLine.setIsDataLine(false);
                   break;}}
            pItr = aLine.getPrimitives().listIterator();
            while (pItr.hasNext()) {
                Primitive p = (Primitive)pItr.next();
                if (p.getBeginPoint().getColumn() > p.getEndPoint().getColumn()) 
                    Collections.reverse(p.getAllPoints());}
            TickColumnCompare pcc = new TickColumnCompare();
            Collections.sort(aLine.getPrimitives(),pcc); }}

    /**
     * writes out point labeled points between the end points
     * of the line segments approxiating a line.
     *
     * @param ostream - the output stream to write to
     * @param P1 - the point at one end of the line segment
     * @param P2 - the point at the other end of the line segment
     */

    public void writeInterpolatedPointLabels(BufferedWriter ostream,
                                             PointPixel P1,
                                             PointPixel P2) {
//System.out.println("interpolating");
//System.out.println(P1.getColumn() + "  " + P2.getColumn());
//System.out.println("row "+P1.getRow() + " to " + P2.getRow());
	NumberFormat nf = NumberFormat.getInstance();
        ListIterator Itr = pointLabelList.listIterator();
        while (Itr.hasNext()) {
            PointLabel pl = (PointLabel)Itr.next();
//System.out.println(pl.getArrowColumn());
            if (pl.getArrowColumn() > P1.getColumn() &&
                pl.getArrowColumn() < P2.getColumn()) {
                    int r = P1.getRow() + (P2.getRow() - P1.getRow())*(pl.getArrowColumn() - P1.getColumn())/(P2.getColumn() - P1.getColumn());
//System.out.println("r = " + r);
                    try {
		        ostream.write("<SamplePoint>\n");
		        ostream.write("	<Coord><X>");
                        ostream.write(nf.format(pl.getArrowColumn() - origin.getColumn()));
                        ostream.write("</X>");
		        ostream.write("<Y>");
                        ostream.write(nf.format(origin.getRow() - r));
		        ostream.write("</Y></Coord>\n");
                        ostream.write("	<OnXGrid>false</OnXGrid>");
                        ostream.write("<OnYGrid>false</OnYGrid>\n");
                        ostream.write("	<AnnotationOnSample>\n");
                        ostream.write("		<Content>");
                        ListIterator wItr = pl.getAnnotationWords().listIterator();
                        while (wItr.hasNext()) {
                            Word aWord = (Word) wItr.next();
                            ostream.write(aWord.getText());}
                        ostream.write("</Content>\n");
                        //ostream.write("		<Noun/>\n");
                        //ostream.write("		<Verb/>\n");
                        //ostream.write("		<Adjective/>\n");
                        //ostream.write("		<Value/>\n");
                        //ostream.write("		<Time/>\n");
                        ostream.write("		<Color>");
                        ostream.write("0"); // kluge for now.  need to save color in Word
                        ostream.write("</Color>\n");
                        ostream.write("		<Bold>");
                        if (((Word)pl.getAnnotationWords().getFirst()).isBold()) ostream.write("true");
                        else ostream.write("false");
                        ostream.write("</Bold>\n");
                        ostream.write("		<Type/>\n");
                        ostream.write("	</AnnotationOnSample>\n");
                        ostream.write("	<SalienceOnSample/>\n");
		        ostream.write("</SamplePoint>\n");}
		 catch (Exception e) {System.out.println(e.getMessage()+"in writeInterpolatedPointLabels");} }}}

    /**
     * input is a list that may have more than one pixel per column
     * output has only one pixel per column
     *
     * I seem to be putting row in the x part of a point and col in the y part.
     *
     * @param A - array of pixels sorted by column, often several pixels for the
     * same column
     */

    PointPixel[] OnePerColumn(Object[] A) {
        int len = A.length;
        int[] col = new int[len];
        int[] lowRow = new int[len];
        int[] highRow = new int[len];
        int i = 0;
//System.out.println("len "+len);
           // find range of values for rows for each column
        col[0] = ((PointPixel)A[0]).getColumn();
        lowRow[0] = ((PointPixel)A[0]).getRow();
        highRow[0] = lowRow[0];
        int j = 0;
        while (j < len) {
//System.out.println("j= "+j);
            while (j < len && ((PointPixel)A[j]).getColumn() == col[i]) {
                int row = ((PointPixel)A[j]).getRow();
                if (row < lowRow[i]) lowRow[i] = row;
                if (row > highRow[i]) highRow[i] = row;
                j++;}
            i++;
//System.out.println("i== " + i);
            if (j < len) {
                col[i] = ((PointPixel)A[j]).getColumn();
                lowRow[i] = ((PointPixel)A[j]).getRow();
                highRow[i] = lowRow[i];}
                j++;
                if (j == len) i++;}
//System.out.println("i = "+i);
           // choose one row value for each column
        int[] row = new int[i];
        PointPixel[] result = new PointPixel[i];
        if (highRow[0] > highRow[1])
            result[0] = new PointPixel(highRow[0],col[0]);
        else if (lowRow[0] < lowRow[1])
            result[0] = new PointPixel(lowRow[0],col[0]);
        else result[0] = new PointPixel((lowRow[0]+highRow[0])/2,col[0]);
        for (j = 1;j < i - 1; j++) {
//System.out.println("j "+j+" col "+col[j]+" high "+highRow[j]+" low "+lowRow[j]);
          if (highRow[j] > highRow[j-1] && highRow[j] > highRow[j+1])
            result[j] = new PointPixel(highRow[j],col[j]);
          else if (lowRow[j] < lowRow[j-1] && lowRow[j] < lowRow[j+1])
            result[j] = new PointPixel(lowRow[j],col[j]);
          else result[j] = new PointPixel((lowRow[j] + highRow[j])/2,col[j]);
//System.out.println("result "+result[j].getColumn()+" "+result[j].getRow());
}
        if (i > 1) {
            j = i - 1;
            if (highRow[j] > highRow[j-1])
                result[j] = new PointPixel(highRow[j],col[j]);
            else if (lowRow[j] < lowRow[j-1])
                result[j] = new PointPixel(lowRow[j],col[j]);
            else result[j] = new PointPixel((lowRow[j] + highRow[j])/2,col[j]);}
//for (j = 0; j < i; j++) {
//System.out.println("returning  row " + result[j].getRow()+" col "+result[j].getColumn());}
        return result;
    }
                

}
     




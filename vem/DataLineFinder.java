import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find if the given connected lines form a data line for 
 * a line chart.
 */
public class DataLineFinder {

	private LinkedList allConnectedLines;
	private PointPixel upperLeft; //The points for the axes area
	private PointPixel lowerRight;

	/**
	 * Constructor.
	 * 
	 * @param connectedLines The linked list of connected lines in the image 
	 */
	public DataLineFinder(LinkedList connectedLines) {
		allConnectedLines = connectedLines;
	}

	/**
	 * Finds whether the given connected line is a data line in the area
	 * of the axes.
	 * 
	 * @param aLine The connected line which is begin checked is it is a data line 
	 * @param hAxis The horizontal axis in the image
	 * @param vAxis The vertical axis in the image
	 * @return True if the given connected line is a data line and false if it is not
	 */
	public boolean isDataLine(ConnectedLine aLine, Axis hAxis, Axis vAxis) {
		findAxesArea(hAxis, vAxis);
		PointPixel lineUpperLeft = aLine.getUpperLeft();
		PointPixel lineLowerRight = aLine.getLowerRight();
		System.out.println("Axes area points are upperLeft="+upperLeft+", lowerRight="+lowerRight);
		System.out.println("Line area points are upperLeft="+lineUpperLeft+", lowerRight="+lineLowerRight);
		if (aLine.getNoOfPixels() == 1) {
			return false;
		}
		if (lineUpperLeft.getRow()+2 >= upperLeft.getRow() && lineUpperLeft.getColumn()+2 >= upperLeft.getColumn() && lineLowerRight.getRow()-2 <= lowerRight.getRow() && lineLowerRight.getColumn()-2 <= lowerRight.getColumn()) { //line is inside the axes area
//System.out.println("returning true\n");
				//The ConnectedLine is a DataLine
				return true;
		}
		return false;
	}

	/**
	 * Calculates the area covered by the axes of the image.
	 * The upper left corner and the lower right corner of the axes area are
	 * recorded.
	 * 
	 * @param horizontalAxis The horizontal axis in the image
	 * @param verticalAxis The vertical axis in the image
	 */
  private void findAxesArea(Axis horizontalAxis, Axis verticalAxis) {
		PointPixel[] points = new PointPixel[4];
		points[0] = horizontalAxis.getBeginPoint();
		points[1] = horizontalAxis.getEndPoint();
		points[2] = verticalAxis.getBeginPoint();
		points[3] = verticalAxis.getEndPoint();
		int minRow = points[0].getRow();
		int maxRow = points[0].getRow();
		int minColumn = points[0].getColumn();
		int maxColumn = points[0].getColumn();
		for (int i = 1; i < 4; i++) {
			if (points[i].getRow() < minRow) {
				minRow = points[i].getRow();
			}
			if (points[i].getRow() > maxRow) {
				maxRow = points[i].getRow();
			}
			if (points[i].getColumn() < minColumn) {
				minColumn = points[i].getColumn();
			}
			if (points[i].getColumn() > maxColumn) {
				maxColumn = points[i].getColumn();
			}
		}
		upperLeft = new PointPixel(minRow, minColumn);
		lowerRight = new PointPixel(maxRow, maxColumn);
		//System.out.println("Axes area points are upperLeft="+upperLeft+", lowerRight="+lowerRight);
	}

	/***********************
	METHODS BELOW ARE NOT USED
  public int[][] makeDataLinesImage(LinkedList lines) {
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
		while(lItr.hasNext()) {
			aConnectedLine = (ConnectedLine)lItr.next();
			if (aConnectedLine.getIsDataLine()) {
			primlist = aConnectedLine.getPrimitives();
			lItr2 = primlist.listIterator();
			while(lItr2.hasNext()) {
				aPrim = (Primitive)lItr2.next();
				pointsList = aPrim.getAllPoints();
				lItr3 = pointsList.listIterator(0);
				while (lItr3.hasNext()) {
					aPoint = (Point)lItr3.next();
					linesImage[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
				}
			}
			}
		}
		return linesImage;
	}
	***********************/

}

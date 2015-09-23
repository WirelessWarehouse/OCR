import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find out if a rectangle is a bar.
 * 
 * The rectangle and the horizontal axis of the chart or 
 * the rectangle and the vertical axis of the chart are given.
 */
public class BarFinder {

	LinkedList allRectangles;

	/**
	 * Constructor.
	 * 
	 * @param none
	 */
	public BarFinder(LinkedList rectangles) {
		allRectangles = rectangles;
	}

	/**
	 * Finds out if the given rectangle is a bar emerging from the given axis.
	 * <p>
	 * For a vertical bar: Gets the lowest corner of the rectangle.
	 * Checks if this corner touches the given axis
	 * or if it is very close -1 or 2 pixels away- to the given axis.
	 * Needs to know the row coordinate of the given axis.
	 * A second pass is needed to find more bars that do not
	 * touch the given axis but are on top of other bars.
	 *
	 * @param aRectangle The rectangle that is being checked if it is a bar 
	 * @param anAxis The axis the rectangle might be emerging from
	 */
	public boolean isBar(Rectangle aRectangle, Axis anAxis) {
		if (anAxis.getOrientation() == Axis.HORIZONTAL_AXIS && aRectangle.getWidth() < 0.30*anAxis.getLength()) {
			//Finding vertical bars, anAxis is the x-axis
			int row, column;
			PointPixel aCorner;
			LinkedList corners = aRectangle.getCorners();
			int maxColumn = ((PointPixel)corners.getFirst()).getColumn();
			int maxRow = ((PointPixel)corners.getFirst()).getRow();
			PointPixel bottomCorner = new PointPixel(maxRow, maxColumn);
			ListIterator lItr = corners.listIterator(1);
			while (lItr.hasNext()) {
				aCorner = (PointPixel)lItr.next();
				row = aCorner.getRow();
				column = aCorner.getColumn();
				if (row > maxRow) {
					maxRow = row;
					bottomCorner.setRow(row);
					bottomCorner.setColumn(column);
				}
			}
			PointPixel axisPointBegin = anAxis.getBeginPoint();
			PointPixel axisPointEnd = anAxis.getEndPoint();

			//Calculate the distance between the line of the axis and
			//the lowest corner of the rectangle
			LineFitter aLineFitter = new LineFitter();
			double squareDist = aLineFitter.squareDistPointToLine(bottomCorner.getRow(), bottomCorner.getColumn(), axisPointBegin, axisPointEnd);
			if (squareDist <= 4) {
				//The rectangle is a bar
				return true;
			}
			else {
				//Check if this rectangle is on top of another one that is a bar
				//Find another rectangle whose upperLeft row is close this one's lowerRight row
				//and its upperLeft column is the same as this one's upperLeft column
				//and its lowerRight column is the same as this one's lowerRight column
				PointPixel upperLeft2, lowerRight2;
				Rectangle anotherRectangle;
				PointPixel upperLeft = aRectangle.getUpperLeft();
				PointPixel lowerRight = aRectangle.getLowerRight();
				lItr = allRectangles.listIterator(0);
				while (lItr.hasNext()) {
					anotherRectangle = (Rectangle)lItr.next();
					upperLeft2 = anotherRectangle.getUpperLeft();
					lowerRight2 = anotherRectangle.getLowerRight();
					if (upperLeft2.getRow() == upperLeft.getRow() && upperLeft2.getColumn() == upperLeft.getColumn() && lowerRight2.getRow() == lowerRight.getRow() && lowerRight2.getColumn() == lowerRight.getColumn()) {
						//anotherRectangle is the same as aRectangle
					}
					else {
						if (Math.abs(upperLeft2.getRow() - lowerRight.getRow()) < 5 && upperLeft2.getColumn() == upperLeft.getColumn() && lowerRight2.getColumn() == lowerRight.getColumn()) {
							if (isBar(anotherRectangle, anAxis)) {
								return true;
							}
						}
					}
				}
			}
		}

		else if (anAxis.getOrientation() == Axis.VERTICAL_AXIS && aRectangle.getHeight() < 0.3*anAxis.getLength()) {
			//Finding horizontal bars, anAxis is the y-axis
			int row, column;
			PointPixel aCorner;
			LinkedList corners = aRectangle.getCorners();
			int minColumn = ((PointPixel)corners.getFirst()).getColumn();
			int minRow = ((PointPixel)corners.getFirst()).getRow();
			PointPixel leftCorner = new PointPixel(minRow, minColumn);
			ListIterator lItr = corners.listIterator(1);
			while (lItr.hasNext()) {
				aCorner = (PointPixel)lItr.next();
				column = aCorner.getColumn();
				row = aCorner.getRow();
				if (column < minColumn) {
					minColumn = column;
					leftCorner.setRow(row);
					leftCorner.setColumn(column);
				}
			}
			PointPixel axisPointBegin = anAxis.getBeginPoint();
			PointPixel axisPointEnd = anAxis.getEndPoint();
			//Calculate the distance between the line of the axis and
			//the leftmost corner of the rectangle

			LineFitter aLineFitter = new LineFitter();
			double squareDist = aLineFitter.squareDistPointToLine(leftCorner.getRow(), leftCorner.getColumn(), axisPointBegin, axisPointEnd);
			if (squareDist <= 4) {
				//The rectangle is a bar
				return true;
			}
			else {
				//Check if this rectangle is to the right another one that is a bar
				//Find another rectangle whose lowerRight column is close this one's upperLeft column 
				//and its upperLeft row is the same as this one's upperLeft row 
				//and its lowerRight row is the same as this one's lowerRight row 
				PointPixel upperLeft2, lowerRight2;
				Rectangle anotherRectangle;
				PointPixel upperLeft = aRectangle.getUpperLeft();
				PointPixel lowerRight = aRectangle.getLowerRight();
				lItr = allRectangles.listIterator(0);
				while (lItr.hasNext()) {
					anotherRectangle = (Rectangle)lItr.next();
					upperLeft2 = anotherRectangle.getUpperLeft();
					lowerRight2 = anotherRectangle.getLowerRight();
					if (upperLeft2.getRow() == upperLeft.getRow() && upperLeft2.getColumn() == upperLeft.getColumn() && lowerRight2.getRow() == lowerRight.getRow() && lowerRight2.getColumn() == lowerRight.getColumn()) {
						//anotherRectangle is the same as aRectangle
					}
					else {
						if (Math.abs(lowerRight2.getColumn() - upperLeft.getColumn()) < 5 && Math.abs(upperLeft2.getRow() - upperLeft.getRow()) < 2 && Math.abs(lowerRight2.getRow() - lowerRight.getRow()) < 2) {
							if (aRectangle.getHeight() == 5 || aRectangle.getHeight() == 6) {
								//System.out.println("Checking "+anotherRectangle+" for "+aRectangle);
							}
							if (isBar(anotherRectangle, anAxis)) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Finds the value that the rectangle (the bar) depicts in the image.
	 * The value can be calculated with respect to the origin (the 
	 * point where the axes intersect).
	 * 
	 * @param aRectangle The rectangle that is the bar
	 * @param origin The point where the axes meet
	 * @return The value point
	 */
	public PointPixel getBarValue(Rectangle aRectangle, PointPixel origin) {
		if (aRectangle.getIsVerticalBar()) {
		//If it is a vertical bar
			PointPixel upperLeft = aRectangle.getUpperLeft();
			PointPixel lowerRight = aRectangle.getLowerRight();
			int middleColumn = (lowerRight.getColumn() + upperLeft.getColumn())/2;
			//middleColumn = middleColumn - origin.getColumn();
			//int row = origin.getRow() - upperLeft.getRow();
			int row = upperLeft.getRow();
			return (new PointPixel(row, middleColumn));
		}
		else if (aRectangle.getIsHorizontalBar()) {
			PointPixel upperLeft = aRectangle.getUpperLeft();
			PointPixel lowerRight = aRectangle.getLowerRight();
			int middleRow = (lowerRight.getRow() + upperLeft.getRow())/2;
			//middleRow = origin.getRow() - middleRow;
			//int column = lowerRight.getColumn() - origin.getColumn();
			int column = lowerRight.getColumn();
			return (new PointPixel(middleRow, column));
		}
		return (new PointPixel(-1, -1));
	}

}

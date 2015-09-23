import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find the wedges in the image.
 * <p>
 * For each region <br>
 *	For each chain <br>
 * <ol>
 * <li>
 *		1. Get the two lists of chains that are next to this one
 *		at each endpoint.
 *		The neigbors do not need to have an endpoint next to
 * 		the endpoint of the first chain.
 * <li>
 *		2. Check if there is a common chain that is in both
 *		lists. If there is, the first chain and this one might
 *		be forming a wedge.
 * <li>
 *		3. For each chain in the first list
 *				Check if it is neigbor of another chain in 
 *				the second list.
 * <li>
 *		4. If a chain in one of the lists is a neigbor of another 
 *		chain in the other list, the first chain and these two
 *		might be forming a wedge. The two chains do not need to
 *		be connected at their endpoints, they might be connected
 *		at an interior point.
 * <li>
 *		5. If the no chains in the two lists are the same or neigbor 
 *		each other, then they need to extended and checked again; extend
 *		and go back to step 1 to get the list of the extended chain and
 *		compare it with the other list.
 * <li>
 *		6. If no wedges are found in step 2 and step 4 and neither of the
 *		chains is extendible, then this chain is not part of a wedge. 
 *		Go to the next chain in the region.
 * </ol>
 * <p>
 * The wedge has to be stored as a list of points for each side, not
 * as a list of primitives since not the complete primitive might be
 * part of the wedge (like the outer circle if it is one chain by itself)!
 * <p>
 * A field of the primitives that make up the wedge can be set that says
 * that that primitive is part of wedge.
 */
public class WedgeFinder {
  private int imageHeight;    // #rows in the input image
	private int imageWidth;     // #columns in the input image
	private int noOfLabels;
  private PixelDatabase allPixels;
  private	Region[] allRegions;
	private LinkedList allWedges;
	private int[][] borderImage;

	/**
	 * Constructor.
	 * 
	 * @param none
	 */
	public WedgeFinder() {
	}

	/**
	 * Constructor.
	 * 
	 * @param regions The <code>Region</code> array of all the regions in the image 
	 * @param labelCount The number of regions in the image
	 * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
	 * @param pixels The pixel database of the image
	 * @param bImage The 2d array representation of the border pixels of the image
	 */
	public WedgeFinder(Region[] regions, int labelCount, int imageRows, int imageColumns, PixelDatabase pixels, int[][] bImage) {
		imageHeight = imageRows;
	 	imageWidth = imageColumns;
		noOfLabels = labelCount;
		allPixels = pixels;		
		allRegions = regions; 
		borderImage = bImage;
		allWedges = new LinkedList();
	}

	/**
	 * Finds the wedges in the image.
	 * Check all primitives of all regions.
	 * If a primitive is not part of a wedge and is longer than 2 pixels,
	 * checks the primitives that it is neigboring at its begin point and end point.
	 * Calls <code>makeWedge</code> once it finds the list
	 * of neigboring primitives at the two endpoints of the candidate primitive.
	 *
	 * @param none
	 */
	public void findWedges() {
		//System.out.println("In findWedges of WedgeFinder.");
		Region aRegion;
		Collection chainList;
		Primitive aPrim;
		PointPixel beginPixel, endPixel;
		int row, column, tag, labelNo;
		for (int i = 1; i < noOfLabels; i++) {
			aRegion = allRegions[i];
			chainList = aRegion.getPrimitiveList();
			Iterator itr = chainList.iterator();
			while (itr.hasNext()) {
				aPrim = (Primitive)itr.next();
				//if (aPrim.isPartOfWedge() < 2 && aPrim.getSize() > 1) {
				if (aPrim.isPartOfWedge() == 0 && aPrim.isPartOfGridline() == 0 && aPrim.isPartOfTick() == 0 && aPrim.getSize() > 2) {
					tag = aPrim.getTagNo();
					beginPixel = aPrim.getBeginPoint();
					endPixel = aPrim.getEndPoint();
					row = beginPixel.getRow();
					column = beginPixel.getColumn();
					//LinkedList beginlist = (LinkedList)allPixels.getPixelDataWithNeigbors(row, column);
					LinkedList beginlist = (LinkedList)allPixels.getPixelDataWithNeigbors24(row, column);
					row = endPixel.getRow();
					column = endPixel.getColumn();
					//LinkedList endlist = (LinkedList)allPixels.getPixelDataWithNeigbors(row, column);
					LinkedList endlist = (LinkedList)allPixels.getPixelDataWithNeigbors24(row, column);
					//System.out.println("\nRegion "+i+", primitive "+aPrim.getTagNo());
					makeWedge(i, tag, beginlist, endlist);
				}
			}
		}
		//System.out.println(allWedges.size()+" wedges are found.");
	}


	/**
	 * Given a region, a primitive in that region and two list of 
	 * primitives that this primitive neigbors at its endpoints,
	 * finds if a set of the primitives can form a wedge.
	 * <p>
	 * <ol>
	 * <li>
	 * 1) Tries to find a primitive that is in both lists
	 * <li>
	 * 2) Tries to find two different chains, one from the begin point
	 * list, one from the end point list and checks if these two
	 * primitives are connected at their other end points.
	 * <li>
	 * 3) If the two primitives from step 2 are not connected at their
	 * other end points, checks if they touch and the touching points are
	 * sufficiently away from their end points that touch the starting 
	 * primitive that was given.
	 * </ol>
	 *
	 * @param labelNo The label of the region the primitives are in
	 * @param tag The tag number of the starting primitive in that region
	 * @param bList The primitives that neighbor the starting primitive at its begin point
	 * @param eList The primitives that neighbor the starting primitive at its end point
	 */
	public void makeWedge(int labelNo, int tag, LinkedList bList, LinkedList eList) {
		Region aRegion = allRegions[labelNo];
		PixelData entryBegin, entryEnd;
		PointPixel aPrimBegin, aPrimEnd, pointBegin, pointEnd, joinPoint;
		int tagBegin, tagEnd, labelNoBegin, labelNoEnd, positionBegin, positionEnd;
		int rowBegin, columnBegin, rowEnd, columnEnd;
		boolean pointBeginFound = false;
		boolean pointEndFound = false;
		Primitive aPrim, primBegin, primEnd;
		LinkedList beginList = allPixels.reduceList(bList);
		LinkedList endList = allPixels.reduceList(eList);
		ListIterator beginlItr = beginList.listIterator(0);
		ListIterator endlItr;
		boolean isWedge = false;

		while (beginlItr.hasNext()) {
//		while (beginlItr.hasNext() && !isWedge) {
			entryBegin = (PixelData)beginlItr.next();
			tagBegin = entryBegin.getTag();
			labelNoBegin = entryBegin.getLabel();
			positionBegin = entryBegin.getPosition();
			rowBegin = entryBegin.getRow();
			columnBegin = entryBegin.getColumn();

			endlItr = endList.listIterator(0);
			while (endlItr.hasNext()) {
//			while (endlItr.hasNext() && !isWedge) {
				entryEnd = (PixelData)endlItr.next();
				tagEnd = entryEnd.getTag();
				labelNoEnd = entryEnd.getLabel();
				positionEnd = entryEnd.getPosition();
				rowEnd = entryEnd.getRow();
				columnEnd = entryEnd.getColumn();
				aPrim = aRegion.getPrimitive(tag);
				primBegin = aRegion.getPrimitive(tagBegin);
				primEnd = aRegion.getPrimitive(tagEnd);

				if (labelNo == labelNoBegin && labelNo == labelNoEnd && !(rowBegin == rowEnd && columnBegin == columnEnd) && primBegin.getSize() > 2 && primEnd.getSize() > 2 && primBegin.isPartOfGridline() == 0 && primEnd.isPartOfGridline() == 0 && primBegin.isPartOfTick() == 0 && primEnd.isPartOfTick() == 0) {
					//System.out.println("Checking region "+labelNo+" primitive "+tag+" connected to primitives "+tagBegin+" and "+tagEnd+" positionBegin="+positionBegin+", positionEnd="+positionEnd);
					if (tagBegin == tagEnd && tagBegin != tag && positionBegin != PixelData.INTERIORPOINT && positionEnd != PixelData.INTERIORPOINT && positionBegin != positionEnd) {
						//One chain connects the endpoints of the first chain.
						//Might be a wedge
						LinkedList sides = new LinkedList();
						sides.add(aPrim);
						sides.add(primBegin);
						LinkedList corners = new LinkedList();
						corners.add(aPrim.getBeginPoint()); 
						corners.add(aPrim.getEndPoint()); 
						//System.out.println("\nRegion "+labelNo+": Found a wedge. Corners are "+aPrim.getBeginPoint()+" and "+ aPrim.getEndPoint());
						Wedge aWedge = new Wedge(sides, corners, aRegion.getIsFilledArea());  
						isWedge = aWedge.setWedge();
						if (isWedge) {
							/*FOUND A WEDGE*/
							aPrim.setPartOfWedge(1);
							primBegin.setPartOfWedge(1);
							calculateArea(aWedge);
							aWedge.setColor(aRegion.getColor());
							allWedges.add(aWedge);
							//System.out.println("Primitive "+tag+": "+tagBegin+" is a neigbor at both end points");
							//isWedge = true;
						}
					}
					else if (tagBegin != tagEnd && tagBegin != tag && tagEnd != tag) {
						aPrimBegin = aPrim.getBeginPoint();
						aPrimEnd = aPrim.getEndPoint();
						pointBeginFound = false;
						pointEndFound = false;
						pointBegin = primBegin.getEndPoint();
						pointEnd = primEnd.getEndPoint();
						if (aPrimBegin.neigbors24(primBegin.getBeginPoint())) {
							pointBeginFound = true;
						}
						else if (aPrimBegin.neigbors24(primBegin.getEndPoint())) {
							pointBegin = primBegin.getBeginPoint();
							pointBeginFound = true;
						}
						if (aPrimEnd.neigbors24(primEnd.getBeginPoint())) {
							pointEnd = primEnd.getEndPoint();
							pointEndFound = true;
						}
						else if (aPrimEnd.neigbors24(primEnd.getEndPoint())) {
							pointEnd = primEnd.getBeginPoint();
							pointEndFound = true;
						}

						//Check if pointBegin and pointEnd are neigbors
						isWedge = false;
						if (pointBeginFound && pointEndFound && pointBegin.neigbors24(pointEnd)) {
							//System.out.println("\nRegion "+labelNo+": Found a wedge. Corners are "+aPrim.getBeginPoint()+", "+ aPrim.getEndPoint()+ " and "+pointBegin);
							//System.out.println("Primitive "+tag+": Points "+pointBegin+" of primitive "+tagBegin+" and "+pointEnd+" of primitive "+tagEnd+" are neigbors");
							isWedge = true;
						}
						else {
							//Check if aPrimBegin and aPrimEnd touch at a point
							int[][] touchingPoints = primBegin.touches(primEnd);
							int noOfTouchingPoints = touchingPoints[0][0];
							if (noOfTouchingPoints > 0) {
								int primBeginTouchIndex = touchingPoints[0][noOfTouchingPoints];
								int primEndTouchIndex = touchingPoints[1][noOfTouchingPoints];
								int primBeginIndex = 0;
								int primEndIndex = 0;
								aPrimBegin = aPrim.getBeginPoint();
								aPrimEnd = aPrim.getEndPoint();
								if (aPrimBegin.neigbors24(primBegin.getEndPoint())) {
									primBeginIndex = primBegin.getSize() - 1;
									primBeginTouchIndex = touchingPoints[0][1];
									primEndTouchIndex = touchingPoints[1][1];
								}
								if (aPrimEnd.neigbors24(primEnd.getEndPoint())) {
									primEndIndex = primEnd.getSize() - 1;
								}
								if (Math.abs(primBeginIndex-primBeginTouchIndex) > 0.5*primBegin.getSize() && Math.abs(primEndIndex-primEndTouchIndex) > 0.5*primEnd.getSize()) {
									LinkedList primBeginPoints = primBegin.getAllPoints();
									pointBegin = new PointPixel(primBeginPoints.get(primBeginTouchIndex));
									LinkedList primEndPoints = primEnd.getAllPoints();
									pointEnd = new PointPixel(primEndPoints.get(primEndTouchIndex));
									//System.out.println("\nRegion "+labelNo+": Found a wedge. Corners are "+aPrim.getBeginPoint()+", "+ aPrim.getEndPoint()+ " and "+pointBegin);
									//System.out.println("Started with primitive "+tag+". Primitive "+tagBegin+" and primitive "+tagEnd+" touch at "+noOfTouchingPoints+" points; "+pointBegin+" and "+pointEnd+" touch each other");
									isWedge = true;
								}
							}
						}	
						/*****
						else {
							//System.out.println(pointBegin+" of primitive "+tagBegin+" and "+pointEnd+" of primitive "+tagEnd+" are not neigbors; not a wedge.");
						}
						****/

						if (isWedge) {
							/*FOUND A WEDGE*/
							LinkedList sides = new LinkedList();
							sides.add(aPrim);
							sides.add(primEnd);
							sides.add(primBegin);
							LinkedList corners = new LinkedList();
							corners.add(aPrim.getBeginPoint());
							corners.add(aPrim.getEndPoint());
							corners.add(pointBegin);
							Wedge aWedge = new Wedge(sides, corners, aRegion.getIsFilledArea());  
							isWedge = aWedge.setWedge();
							if (isWedge) {
								aPrim.setPartOfWedge(1);
								primBegin.setPartOfWedge(1);
								primEnd.setPartOfWedge(1);
								calculateArea(aWedge);
								aWedge.setColor(aRegion.getColor());
								allWedges.add(aWedge);
								//isWedge = true;
							}
						}
/******************************
						//Check if tagBegin and tagEnd are connected
						int[] indexList = allPixels.neigbors(primBegin, primEnd);
						if (indexList[0] > 0) { //primBegin has 1 or more points that neigbor primEnd
							int compareIndex = -2;
//System.out.println("\nRegion "+labelNo+": Primitive "+tag+": Neigbors "+tagEnd+" and "+tagBegin + " are connected at " +indexList[0]+" points:");
							for (int i=1; i<=indexList[0];i++) {
								//System.out.println(indexList[i]+"th point of primitive "+tagBegin);
							}
							for (int i = 1; i <= indexList[0]; i++) {
								//They are connected, might be a wedge
								if (Math.abs(indexList[i] - compareIndex) != 1) { //so that two points that neigbor primEnd are not following each other in primBegin
									compareIndex = indexList[i];
									joinPoint = new PointPixel((primBegin.getAllPoints()).get(indexList[i])); 
									aPrim = aRegion.getPrimitive(tag);
									//System.out.println("Joining point: "+joinPoint);
//System.out.println("\nRegion "+labelNo+": Primitive "+tag+": Neigbors "+tagEnd+" and "+tagBegin + " are connected at " +indexList[0]+" points; point "+joinPoint);
					//			if (checkWedge(aPrim, primBegin, primEnd, joinPoint)) {
//System.out.println("Region "+labelNo+": Corners of the wedge are "+aPrim.getBeginPoint()+", "+ aPrim.getEndPoint()+ " and "+joinPoint);
										LinkedList sides = new LinkedList();
										sides.add(aPrim);
										sides.add(primEnd);
										sides.add(primBegin);
										LinkedList corners = new LinkedList();
										corners.add(aPrim.getBeginPoint());
										corners.add(aPrim.getEndPoint());
										corners.add(joinPoint);
										aPrim.setPartOfWedge(1);
										primBegin.setPartOfWedge(1);
										primEnd.setPartOfWedge(1);
										//First check if this wedge or a similar wedge with corners neigbors of this one is already listed in allWedges!
										allWedges.add(new Wedge(sides, corners));  
										isWedge = true;
					//			}
								}
							}
						}	
						else {
							//System.out.println("Region "+labelNo+": Primitives "+tagBegin+" and "+tagEnd+" are not neigbors");
						}
*****************************/
					}
				}
			} //end of traversing endList
		} //end of traversing beginList
	}


	/**
	 * Calculates the number of pixels inside the region of a wedge. 
	 * The area of the wedge is set in the <code>Wedge</code> object.
	 *
	 * @param aWedge The wedge whose area is to be calculated
	 */
	private void calculateArea(Wedge aWedge) {
		int labelNo;
		int	inside;
		int	previousLabelNo = -1; 
		int	regionNo = aWedge.getRegionNo();
		double area = 0;
		PointPixel upperLeft = aWedge.getUpperLeft();
		PointPixel lowerRight = aWedge.getLowerRight();
		for (int i = upperLeft.getRow(); i <= lowerRight.getRow(); i++) {
			inside = -1;
			previousLabelNo = -1;
			for (int j = upperLeft.getColumn(); j <= lowerRight.getColumn(); j++) {
				labelNo = borderImage[i][j];
				if (inside == 1 && labelNo != regionNo) {
					area++;
				}
				if (labelNo != regionNo && previousLabelNo == regionNo) {
					inside = inside * -1;
					/*
					if (regionNo == 26) {
						//System.out.println("("+i+", "+j+") , inside="+inside);
					}
					*/
				}
				previousLabelNo = labelNo;
			}
		}
		aWedge.setArea(area);
	}


	/**
	 * Creates a 2d array representation of an mage that has the Wedge pixels
	 * as black and all others as white. 
	 *
	 * @param wedges The linked list of wedges to be drawn on the image
	 * @return The 2d array representation of the wedge image
	 */
	public int[][] makeWedgeImage(LinkedList wedges) {
		LinkedList alist, primlist, pointsList;
		Primitive aPrim;
		Wedge aWedge;
		Point aPoint;
		ListIterator lItr, lItr2, lItr3;
		int[][] WedgeImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				WedgeImage[i][j] = 255;
			}
		}
		lItr = wedges.listIterator(0);
		while(lItr.hasNext()) {
			aWedge = (Wedge)lItr.next();
			primlist = aWedge.getSides();
			lItr2 = primlist.listIterator(0);
			while(lItr2.hasNext()) {
				aPrim = (Primitive)lItr2.next();
				pointsList = aPrim.getAllPoints();
				lItr3 = pointsList.listIterator(0);
				while (lItr3.hasNext()) {
					aPoint = (Point)lItr3.next();
					WedgeImage[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
				}
			}
		}
		return WedgeImage;
	}
				
	/**
	 * Returns all the wedges found in the image.
	 *
	 * @param none
	 * @return The linked list of all wedges.
	 */
	public LinkedList getWedges() {
		return allWedges;
	}


	/*******************************
	Methods below are not used!!!!
	*******************************/


	/*******************************
	 * @param none
	 * 	
	 * Wedge candidate: aPrim, connected to primBegin at its begin point,
	 * and connected to primEnd at its end point and primBegin and primEnd
	 * are connected to each other at joinPoint
	 *
	 * Move from joinPoint to aPrim's beginPoint on primBegin
	 *	there should not be any other primitive neigboring this path 
	 *	that is also neigboring aPrim or primEnd
	 *	(that connects primBegin to aPrim or to primEnd) 
	 * Move from joinPoint to aPrim's endPoint on primEnd
	 *	there should not be any other primitive neigboring this path 
	 *	that is also neigboring aPrim or primBegin
	 *	(that connects primEnd to aPrim or to primBegin) 
	 * Move on aPrim from beginPoint to endPoint
	 *	there should not be any other primitive neigboring this path 
	 *	that is also beginPrim or endPrim 
	 *	(that connects aPrim to primBegin or to primEnd) 
	 *
	public boolean checkWedge(Primitive aPrim, Primitive primBegin, Primitive primEnd, PointPixel joinPoint) {
		//System.out.println("In checkWedge");
		boolean check1 = false;
		boolean check2 = false;
		boolean check3 = false;
		PixelData entry;
		int rowPrimBegin, columnPrimBegin, rowPrimEnd, columnPrimEnd;
		int indexB1 = -1;
		int indexB2 = -1;
		int indexE1 = -1;
		int indexE2 = -1;
		LinkedList primBeginList = primBegin. getAllPoints();
		LinkedList primEndList = primEnd. getAllPoints();
		PointPixel beginPoint = aPrim.getBeginPoint();
		LinkedList entryList = allPixels.getPixelDataWithNeigbors(beginPoint);
		ListIterator lItr = entryList.listIterator(0);
		while (lItr.hasNext()) {
			entry = (PixelData)lItr.next();
			if (entry.getLabel() == aPrim.getParent() && entry.getTag() == primBegin.getTagNo()) {
				rowPrimBegin = entry.getRow();
				columnPrimBegin = entry.getColumn();
				indexB1 = primBeginList.indexOf(new Point(rowPrimBegin, columnPrimBegin));
				break;
			}
		}
		PointPixel endPoint = aPrim.getEndPoint();
		entryList = allPixels.getPixelDataWithNeigbors(endPoint);
		lItr = entryList.listIterator(0);
		while (lItr.hasNext()) {
			entry = (PixelData)lItr.next();
			if (entry.getLabel() == aPrim.getParent() && entry.getTag() == primEnd.getTagNo()) {
				rowPrimEnd = entry.getRow();
				columnPrimEnd = entry.getColumn();
				indexE1 = primEndList.indexOf(new Point(rowPrimEnd, columnPrimEnd));
				break;
			}
		}
		entryList = allPixels.getPixelDataWithNeigbors(joinPoint);
		lItr = entryList.listIterator(0);
		while (lItr.hasNext()) {
			entry = (PixelData)lItr.next();
			if (entry.getLabel() == aPrim.getParent()) {
				if (entry.getTag() == primBegin.getTagNo()) {
					rowPrimBegin = entry.getRow();
					columnPrimBegin = entry.getColumn();
					indexB2 = primBeginList.indexOf(new Point(rowPrimBegin, columnPrimBegin));
				}
				else if (entry.getTag() == primEnd.getTagNo()) {
					rowPrimEnd = entry.getRow();
					columnPrimEnd = entry.getColumn();
					indexE2 = primEndList.indexOf(new Point(rowPrimEnd, columnPrimEnd));
				}
			}
		}
		//System.out.println("Indexes are: Primitive "+primBegin.getTagNo()+" is from "+indexB1+" to "+indexB2+" and primitive "+primEnd.getTagNo()+" is from "+indexE1+" to "+indexE2);
		if (indexB1 != -1 && indexB2 != -1 && indexE1 != -1 && indexE2 != -1) {
			check1 = moveAndCheck(primBegin, indexB1, indexB2, primEnd, aPrim);
			//System.out.println("Primitive "+primBegin.getTagNo()+" checks "+check1);
			if (check1) {
				check2 = moveAndCheck(primEnd, indexE1, indexE2, primBegin, aPrim);
				//System.out.println("Primitive "+primEnd.getTagNo()+" checks "+check2);
				if (check2) {
					check3 = moveAndCheck(aPrim, 0, aPrim.getSize()-1, primBegin, primEnd);
					//System.out.println("Primitive "+aPrim.getTagNo()+" checks "+check3);
					return check3;
				}
				else {
					return false;
				}
			}
			else {
				return false;
			}
		}
		return false;
	}

	 * @param none
	 * 	
	 * Move on primitive aPrim from index index1 to index2
	 * check if on that path, there are any points that neigbor a 
	 * different primitive than aPrim, firstPrim and seconPrim that also
	 * connects to firstPrim and/or secondPrim
	 *
	 * Is there a way to connect from aPrim to firstPrim or to secondPrim 
	 * using a different primitive?
	 * If aPrim is a closed curve, have to go in both directions. No such 
	 * primitive should be found in at least one direction to return true.
	 *
	private boolean moveAndCheck(Primitive aPrim, int index1, int index2, Primitive firstPrim, Primitive secondPrim) {
		boolean found = false;
		boolean found1 = false;
		boolean found2 = false;
		int tagNeig;
		PixelData entry;
		PointPixel aPoint;
		Primitive anotherPrim;
		int[] indexList1, indexList2;
		LinkedList points, entryList;
		ListIterator lItr, elItr;
		int indexBegin = index1;
		int indexEnd = index2;
		if (index1 > index2) {
			indexBegin = index2;
			indexEnd = index1;
		}
		int noOfPixels = indexEnd - indexBegin;
		int indexFrom = indexBegin;
		if (aPrim.getSize() - 1 > indexBegin + 1) {
			indexFrom = indexBegin + 1;
		}
		if (aPrim.getSize() - 1 > indexFrom + noOfPixels/10) {
			indexFrom = indexFrom + noOfPixels/10;
		}
		int indexTo = indexEnd;
		if (indexEnd - 1 > 0) {
			indexTo = indexEnd - 1;
		}
		if (indexTo > noOfPixels/10) {
			indexTo = indexTo - noOfPixels/10;
		}
		found = traversePrimitive(aPrim, indexBegin, indexFrom, indexTo, firstPrim, secondPrim);
		//Check if aPrim is a closed curve; if it is, you have to go both directions 
		PointPixel aPrimBeginPoint = aPrim.getBeginPoint();
		PointPixel aPrimEndPoint = aPrim.getEndPoint();
		if (aPrimEndPoint.neigbors8(aPrimBeginPoint)) { //Closed curve
			//Go from 0 to beginIndex, then from endIndex to the end point		
			if (indexBegin - 1 > 0) {
				indexTo = indexBegin - 1;
			}
			if (indexTo > noOfPixels/10) {
				indexTo = indexTo - noOfPixels/10;
			}
			found1 = traversePrimitiveReverse(aPrim, indexBegin, 0, indexTo, firstPrim, secondPrim);
			if (aPrim.getSize() - 1 > indexEnd + 1) {
				indexFrom = indexEnd + 1;
			}
			if (aPrim.getSize() - 1 > indexFrom + noOfPixels/10) {
				indexFrom = indexFrom + noOfPixels/10;
			}
			found2 = traversePrimitive(aPrim, indexEnd, indexFrom, aPrim.getSize() - 1, firstPrim, secondPrim);
		}
		if (found && (found1 || found2)) {
			return false;
		}
		return true;
	}

	**
	 * @param none
	 * 	
	 *
	private boolean traversePrimitive(Primitive aPrim, int indexBegin, int indexFrom, int indexEnd, Primitive firstPrim, Primitive secondPrim) {
		//System.out.println("Primitive "+aPrim.getTagNo()+" going from "+indexFrom +" to "+indexEnd);
		PointPixel aPoint;
		LinkedList entryList;
		ListIterator elItr;
		PixelData entry;
		int tagNeig;
		Primitive anotherPrim;
		int[] indexList1, indexList2;
		int index = indexFrom;
		LinkedList points = aPrim.getAllPoints();
		ListIterator lItr = points.listIterator(indexFrom);
		while (lItr.hasNext() && index < indexEnd) {
			aPoint = new PointPixel(lItr.next());
			entryList = allPixels.getPixelDataWithNeigbors(aPoint);
			elItr = entryList.listIterator(0);
			while (elItr.hasNext()) {
				entry = (PixelData)elItr.next();	
				tagNeig = entry.getTag(); 
				if (entry.getLabel() == aPrim.getParent() && aPrim.getTagNo() != tagNeig && firstPrim.getTagNo() != tagNeig && secondPrim.getTagNo() != tagNeig) {
				//Totally different primitive in this region. Does it connect to firstPrim or secondPrim?
					anotherPrim = allRegions[aPrim.getParent()].getPrimitive(tagNeig); 
					indexList1 = allPixels.neigbors(anotherPrim, firstPrim);
					if (indexList1[0] != 0) {
						//System.out.println("Primitive "+tagNeig+" connects primitive "+aPrim.getTagNo()+" to primitive "+firstPrim.getTagNo()+" at point "+aPoint);
						//Potential wedge with corners indexBegin (aPrim), aPoint (aPrim-anotherPrim) and point in indexList (anotherPrim-firstPrim)
						PointPixel point2 = new PointPixel(points.get(indexBegin));
						LinkedList anotherPts = anotherPrim.getAllPoints();
						PointPixel point3 = new PointPixel(anotherPts.get(indexList1[1]));
						//System.out.println("Potential wedge with corners "+aPoint+", "+point2+" and "+point3);
						return true;
					}
					indexList2 = allPixels.neigbors(anotherPrim, secondPrim);
					if (indexList2[0] != 0) {
						//System.out.println("Primitive "+tagNeig+" connects primitive "+aPrim.getTagNo()+" to primitive "+secondPrim.getTagNo()+" at point "+aPoint);
						//Potential wedge with corners indexBegin (aPrim), aPoint (aPrim-anotherPrim) and point in indexList (anotherPrim-firstPrim)
						PointPixel point2 = new PointPixel(points.get(indexBegin));
						LinkedList anotherPts = anotherPrim.getAllPoints();
						PointPixel point3 = new PointPixel(anotherPts.get(indexList2[1]));
						//System.out.println("Potential wedge with corners "+aPoint+", "+point2+" and "+point3);
						return true;
					}
				}
			}
			index++;
***			
			if (index == aPrim.getSize() && index1 > index2) {
				//Continue at the beginning of the primitive
//if (index1 > index2),  go from index1 to the end of the primitive, then from the beginning of the primitive to index2
				lItr = points.listIterator(0);
				index = 0;
				indexEnd = index2;
			}
***
		}
		return false;
	}


	**
	 * @param none
	 * 	
	 *
	private boolean traversePrimitiveReverse(Primitive aPrim, int indexBegin, int indexFrom, int indexEnd, Primitive firstPrim, Primitive secondPrim) {
		//System.out.println("Primitive "+aPrim.getTagNo()+" going from "+indexEnd +" to "+indexFrom);
		PointPixel aPoint;
		LinkedList entryList;
		ListIterator elItr;
		PixelData entry;
		int tagNeig;
		Primitive anotherPrim;
		int[] indexList1, indexList2;
		int index = indexEnd;
		LinkedList points = aPrim.getAllPoints();
		ListIterator lItr = points.listIterator(indexEnd);
		while (lItr.hasPrevious() && index > indexFrom) {
			aPoint = new PointPixel(lItr.previous());
			entryList = allPixels.getPixelDataWithNeigbors(aPoint);
			elItr = entryList.listIterator(0);
			while (elItr.hasNext()) {
				entry = (PixelData)elItr.next();	
				tagNeig = entry.getTag(); 
				if (entry.getLabel() == aPrim.getParent() && aPrim.getTagNo() != tagNeig && firstPrim.getTagNo() != tagNeig && secondPrim.getTagNo() != tagNeig) {
				//Totally different primitive in this region. Does it connect to firstPrim or secondPrim?
					anotherPrim = allRegions[aPrim.getParent()].getPrimitive(tagNeig); 
					indexList1 = allPixels.neigbors(anotherPrim, firstPrim);
					if (indexList1[0] != 0) {
						//System.out.println("Primitive "+tagNeig+" connects primitive "+aPrim.getTagNo()+" to primitive "+firstPrim.getTagNo()+" at point "+aPoint);
						//Potential wedge with corners indexBegin (aPrim), aPoint (aPrim-anotherPrim) and point in indexList (anotherPrim-firstPrim)
						PointPixel point2 = new PointPixel(points.get(indexBegin));
						LinkedList anotherPts = anotherPrim.getAllPoints();
						PointPixel point3 = new PointPixel(anotherPts.get(indexList1[1]));
						//System.out.println("Potential wedge with corners "+aPoint+", "+point2+" and "+point3);
						return true;
					}
					indexList2 = allPixels.neigbors(anotherPrim, secondPrim);
					if (indexList2[0] != 0) {
						//System.out.println("Primitive "+tagNeig+" connects primitive "+aPrim.getTagNo()+" to primitive "+secondPrim.getTagNo()+" at point "+aPoint);
						//Potential wedge with corners indexBegin (aPrim), aPoint (aPrim-anotherPrim) and point in indexList (anotherPrim-firstPrim)
						PointPixel point2 = new PointPixel(points.get(indexBegin));
						LinkedList anotherPts = anotherPrim.getAllPoints();
						PointPixel point3 = new PointPixel(anotherPts.get(indexList2[1]));
						//System.out.println("Potential wedge with corners "+aPoint+", "+point2+" and "+point3);
						return true;
					}
				}
			}
			index--;
***			
			if (index == aPrim.getSize() && index1 > index2) {
				//Continue at the beginning of the primitive
//if (index1 > index2),  go from index1 to the end of the primitive, then from the beginning of the primitive to index2
				lItr = points.listIterator(0);
				index = 0;
				indexEnd = index2;
			}
***
		}
		return false;
	}
****************************/

}

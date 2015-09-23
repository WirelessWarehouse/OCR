import java.util.*;
import java.lang.Math;
import java.awt.Point;

/**
 * A class to find the rectangles in the image.
 * Each rectangle is composed of 4 or more primitives that are in the same region.
 * <p> 
 * For each region: <br>
 * 	For each chain: <br>
 * <ol>
 * <li>
 *		1. Find pairs of chains that are perpendicular to that chain
 *				at each endpoint and on the same side of it.
 *				These two perpendicular chains "side chains" need to have an 
 *				endpoint next to the first chain's end points.
 * <li>
 *		2. Check all points of the first of the side chains to find a 
 *				4th different chain that is perpendicular to it at an endpoint
 *				and on the same side of it as the very first chain is.
 * <li>
 *		3. If this 4th chain neigbors the other side chain too, then
 *				a rectangle is found.
 * <li>
 *		4. If the 4th chain does not neigbor the other side chain,
 *				find the point where the 4th chain and the other side 
 *				chain would intersect and try to extend both to
 *				that point. If they both can be extended, then a 
 *				rectangle is found.
 * <li>
 *		5. If still no rectangle is found, extend the primitive 
 *				checked in step 2 by one primitive and repeat.
 * </ol>
 * <p> 
 * The rectangle needs to be stored as a list of points for each side, not 
 * as a list of primitives. Only a part of the primitive can be in the wedge,
 * not completely.
 * <p> 
 * A field in the primitives that take part in making up the rectangle
 * can be set to say that that primitive is part of a rectangle.
 * 
 * @author Chart Reading project
 * @version 1.0
 */

public class RectangleFinder {
  private int imageHeight;    // #rows in the input image
	private int imageWidth;     // #columns in the input image
	private int noOfLabels;
  private PixelDatabase allPixels;
  private	Region[] allRegions;
	private LinkedList allRectangles;

	/**
	 * Constructor. 
	 *
	 * @param none
	 */
	public RectangleFinder() {
	}

	/**
	 * Constructor. 
	 *
	 * @param regions The Region array of all the regions in the image
	 * @param labelCount The number of regions in the image
	 * @param imageRows The number of rows of the image
	 * @param imageColumns The number of columns of the image
	 */
	public RectangleFinder(Region[] regions, int labelCount, int imageRows, int imageColumns, PixelDatabase pixels) {
		imageHeight = imageRows;
	 	imageWidth = imageColumns;
		noOfLabels = labelCount;
		allPixels = pixels;
		allRegions = regions;
		allRectangles = new LinkedList();
	}

	/**
	 * Finds the rectangles in the image. All primitives of all regions are searched.
	 * <code>startRectangle</code> is called to check each primitive that is not
	 * yet part of a rectangle.
	 *
	 * @param none
	 */
	public void findRectangles() {
		//System.out.println("In findRectangles of RectangleFinder.");
		boolean flag;
		boolean found = false;
		boolean sameSide = false;
		int row, column, neigRow, neigColumn, noOfNeigbors;
		int tag, labelNo, firstTag, secondTag;
		int noOfPerpendiculars, noOfRectangles, extensionTag;
		int firstjoinPoint = PixelData.INTERIORPOINT;
		int secondjoinPoint = PixelData.INTERIORPOINT;
		int firstTagjoinPoint = PixelData.INTERIORPOINT;
		int secondTagjoinPoint = PixelData.INTERIORPOINT;
		int[] perpArray1, perpArray2, extension;
		LinkedList linesList, neigborList, perpList;
		ListIterator lItr; 
		Collection chainList;
		PointPixel beginPixel, endPixel;
		PixelData entry;
		Primitive aPrim, anotherPrim;
		Region aRegion;
//DLC
//System.out.println("noOfLabels = " + noOfLabels);
		for (int i = 1; i < noOfLabels; i++) {
//DLC
//System.out.println("i = " + i);
			aRegion = allRegions[i];
//System.out.println("1");
			chainList = aRegion.getPrimitiveList();
//System.out.println("2");
			linesList = new LinkedList();
//System.out.println("3");
			Iterator itr = chainList.iterator();
//System.out.println("4");
			while (itr.hasNext()) {
//DLC
//System.out.println("in chainlist loop");
				aPrim = (Primitive)itr.next();
				if (aPrim.isPartOfRectangle() == 0 && aPrim.isPartOfGridline() == 0 && aPrim.isPartOfTick() == 0) {
					noOfRectangles = 0;
					noOfPerpendiculars = 0;
					tag = aPrim.getTagNo();
					//System.out.println("\nChecking region "+i+", primitive "+tag);
					firstTag = 0;
					secondTag = 0;
					flag = false;
					beginPixel = aPrim.getBeginPoint();
					endPixel = aPrim.getEndPoint();
					//Find two perpendicular primitives, one at the begin point, 
					//one at the endpoint, both on the same side of aPrim.
					LinkedList startPrimList = new LinkedList();
					startPrimList.add(aPrim);
//DLC
//System.out.println("beginning startRectangle");
					noOfRectangles = startRectangle(i, startPrimList, beginPixel, PixelData.STARTPOINT, endPixel, PixelData.ENDPOINT);
//DLC
//System.out.println("finished startRectangle");
				} //end of if not part of a rectangle
			} //end of traversing of chains in a region

//System.out.println("5");
		} //end of traversing regions
		//System.out.println(allRectangles.size()+" rectangles are found.");
	}


	/**
	 * Given a primitive to start with, two perpendicular primitives at each 
	 * endpoint of the given primitive are found. The primitive now becomes
	 * a candidate to be part of a rectangle.
	 * <p>
	 * There may be more than one perpendicular primitive at the endpoints; 
	 * each combination is checked.
	 * 
	 * @param label The label of the region in which the starting primitive is
	 * @param startList The list of primitive(s) that are candidates to start a rectangle
	 * @param beginPixel The point at which the perpendicular primitive is to be found
	 * @param beginPos The position of the begin pixel; start point or end point of the primitive
	 * @param endPixel The point at which the perpendicular primitive is to be found
	 * @param endPos The position of the end pixel; start point or end point of the p
	 rimitive
	 * @return The number of rectangles found starting from the given primitives
	 */
	public int startRectangle(int label, LinkedList startList, PointPixel beginPixel, int beginPos, PointPixel endPixel, int endPos) {
		//System.out.println("In startRectangle of RectangleFinder.");
		boolean flag;
		boolean foundRectangle = false;
		boolean primExtendible = true;
		int row, column, neigRow, neigColumn, noOfNeigbors;
		int tag, labelNo, firstTag, secondTag;
		int noOfPerpendiculars, extensionTag, extensionPoint;
		int firstjoinPoint = PixelData.INTERIORPOINT;
		int secondjoinPoint = PixelData.INTERIORPOINT;
		int firstTagjoinPoint = PixelData.INTERIORPOINT;
		int secondTagjoinPoint = PixelData.INTERIORPOINT;
		int noOfRectangles = 0;
		int noOfPerpStart = 0;
		int noOfPerpEnd = 0;
		int[] perpArray1, perpArray2, extension;
		LinkedList linesList, neigborList, perpList;
		LinkedList firstPrimList = new LinkedList(); 
		LinkedList secondPrimList = new LinkedList();
		LinkedList[] sidesList = new LinkedList[2];
		ListIterator lItr;
		Primitive aPrim, extensionPrim;
			
		while (noOfRectangles == 0 && primExtendible == true) {

			tag = ((Primitive)startList.getFirst()).getTagNo();
			int lastTag = ((Primitive)startList.getLast()).getTagNo();
//System.out.println("starting findPerpendicularPrims");
			perpList = findPerpendicularPrims(label, tag, beginPixel, beginPos, endPixel, endPos);
//System.out.println("finished findPerpendicularPrims");
			//System.out.println("Primitive list of "+startList.size()+" primitives from "+tag+" to "+lastTag+" have "+(perpList.size() - 2)+" perpendicular pairs");
			perpArray1 = (int[])perpList.get(0);
			perpArray2 = (int[])perpList.get(1);
			noOfPerpStart = perpArray1[2];
			noOfPerpEnd = perpArray2[2];
			//System.out.println("There are "+perpArray1[2]+" perpendiculars at first point");
			//System.out.println("There are "+perpArray2[2]+" perpendiculars at last point");
			lItr = perpList.listIterator(2);
			while (lItr.hasNext()) {
				perpArray1 = (int[])lItr.next();
				perpArray2 = (int[])lItr.next();
				firstTag = perpArray1[0];
				firstjoinPoint = perpArray1[1]; //endpoint of the first prim in startList
				firstTagjoinPoint = perpArray1[2];
				secondTag = perpArray2[0];
				secondjoinPoint = perpArray2[1]; //endpoint of the last prim in startList
				secondTagjoinPoint = perpArray2[2];
				firstPrimList.clear();
				secondPrimList.clear();
				firstPrimList.add(allRegions[label].getPrimitive(firstTag));
				secondPrimList.add(allRegions[label].getPrimitive(secondTag));
				//sidesList[0].clear();
				//sidesList[1].clear();
				sidesList[0] = firstPrimList;
				sidesList[1] = secondPrimList;
//System.out.println("starting makeRectangle");
				foundRectangle = makeRectangle(label, startList, beginPixel, endPixel, firstTag, firstjoinPoint, firstTagjoinPoint, secondTag, secondjoinPoint, secondTagjoinPoint, sidesList);
//System.out.println("finished makeRectangle");
				if (foundRectangle) {
					noOfRectangles++;
				}
			} //end of checking the perpendiculars list	
			//System.out.println("Region "+label+": "+noOfRectangles+" rectangles are found for primitive list from primitive "+tag+" to "+lastTag);
			primExtendible = false;
			if (noOfRectangles == 0 && noOfPerpStart > 0) { //No rectangles are found for tag 
				//Extend aPrim by one primitive and repeat
				aPrim = (Primitive)startList.getLast();
//System.out.println("starting extenOnePrimitive");
				extension = extendOnePrimitive(aPrim, endPos);
				//System.out.println("Primitive "+aPrim.getTagNo()+" is extended by primitive "+extension[0]);
				if (extension[0] >= 0) {
					primExtendible = true;
					extensionTag = extension[0];
					extensionPrim = allRegions[label].getPrimitive(extensionTag);
					extensionPoint = extension[1];
					//beginPixel = aPrim.getBeginPoint();
					endPixel = extensionPrim.getEndPoint();
					if (extensionPoint == PixelData.ENDPOINT) {
						endPixel = extensionPrim.getBeginPoint();
					}
//DLC to prevent endless loop
if (startList.contains(extensionPrim)) primExtendible = false;
					startList.add(extensionPrim);
					if (extensionPoint == PixelData.ENDPOINT) {
						endPos = PixelData.STARTPOINT;
						//noOfRectangles = startRectangle(label, startList, beginPixel, beginPos, endPixel, PixelData.STARTPOINT);
					}
					else if (extensionPoint == PixelData.STARTPOINT) {
						endPos = PixelData.ENDPOINT;
						//noOfRectangles = startRectangle(label, startList, beginPixel, beginPos, endPixel, PixelData.ENDPOINT);
					}
				}
				//extendOnePrimitive(aPrim, PixelData.STARTPOINT);
			}
		}
		return noOfRectangles;
	}
					


	/**
	 * Finds the two perpendicular primitives to the given primitive.
	 * Find all the perpendicular primitives at each endpoint, 
	 * match the ones on the same side. 
	 *
	 * @param label The label of the region the primitive is in
	 * @param tag The tag of the primitive in its region
	 * @param beginPixel The location of the pixel at the first endpoint of the primitive
	 * @param beginPos The position of the first end point; start or end point
	 * @param endPixel The location of the pixel at the second endpoint of the primitive 
	 * @param endPos The position of the second end point; start or end point
	 * @return The linked list of pairs of primitives that are perpendicular to the given primitive and on the same side of it
	 */
	private LinkedList findPerpendicularPrims(int label, int tag, PointPixel beginPixel, int beginPos, PointPixel endPixel, int endPos) {
		//System.out.println("Getting perpendicular primitive pairs for primitive "+tag);
		boolean flag;
		boolean found = false;
		boolean sameSide = false;
		int row, column;
		int tag2, labelNo, position, firstTag, secondTag;
		int noOfPerpendiculars;
		int firstjoinPoint = PixelData.INTERIORPOINT;
		int secondjoinPoint = PixelData.INTERIORPOINT;
		int firstTagjoinPoint = PixelData.INTERIORPOINT;
		int secondTagjoinPoint = PixelData.INTERIORPOINT;
		int[] primInfo;
		int[] primInfo1 = new int[3];
		int[] primInfo2 = new int[3];
		LinkedList pixelDataList, alist, primInfoList;
		LinkedList[] perpPrimsList = new LinkedList[2];
		LinkedList perpPrimPairsList = new LinkedList();
		ListIterator lItr, lItr2;
		Primitive anotherPrim;
		Primitive aPrim = allRegions[label].getPrimitive(tag);
		PixelData entry;
		for (int k = 0; k < 2; k++) { //Do it two times, for each endpoint
			found = false;
			if (k == 0) {
				row = beginPixel.getRow();
				column = beginPixel.getColumn();
			}
			else {
				row = endPixel.getRow();
				column = endPixel.getColumn();
			}
			noOfPerpendiculars = 0;
			primInfoList = new LinkedList();
			pixelDataList = (LinkedList)allPixels.getPixelDataWithNeigbors(row, column);
			alist = allPixels.reduceList(pixelDataList);
			lItr = alist.listIterator(0);
			while (lItr.hasNext()) {
				//System.out.print("Getting an entry: ");
				entry = (PixelData)lItr.next();
				tag2 = entry.getTag();
				labelNo = entry.getLabel();
				position = entry.getPosition();
				//System.out.println("Entry: "+entry);
				if (labelNo == label && tag2 != tag && position != PixelData.INTERIORPOINT) { 
					//System.out.println("Primitive "+tag2+" neigbors primitive "+tag+" at ("+row+", "+column+"). Position = "+position);
					//This point is part of a different primitive in the same region
					anotherPrim = allRegions[label].getPrimitive(tag2); 
					if (anotherPrim.isPerpendicularTo(aPrim) && anotherPrim.isPartOfGridline() == 0 && anotherPrim.isPartOfTick() == 0) { 
						//System.out.println("Primitive "+tag2+" is perpendicular to primitive "+tag);
						primInfo= new int[3];
						primInfo[0] = tag2;
						primInfo[1] = beginPos;
						if (k == 1)
							primInfo[1] = endPos; 
						primInfo[2] = position;
						primInfoList.add(primInfo);
						noOfPerpendiculars++;
					}
				}
			} //end of traversing entries at one location
			perpPrimsList[k] = primInfoList;
			primInfo= new int[3];
			primInfo[0] = -1;
			primInfo[1] = -1;
			primInfo[2] = primInfoList.size();
			if (k == 0) {
				primInfo[0] = PixelData.STARTPOINT;
				primInfo[1] = beginPos;
			}
			else if (k == 1) {
				primInfo[0] = PixelData.ENDPOINT; 
				primInfo[1] = endPos;
			}
			perpPrimPairsList.add(primInfo);
		} //end of checking begin point and end point

		if (perpPrimsList[0].size() > 0 && perpPrimsList[1].size() > 0) {
			lItr = perpPrimsList[0].listIterator(0);
			while (lItr.hasNext()) {
				primInfo1 = (int[])lItr.next();
				lItr2 = perpPrimsList[1].listIterator(0);
				while (lItr2.hasNext()) {
					primInfo2 = (int[])lItr2.next();
					//System.out.println("Region "+label+": Primitives "+primInfo1[0]+" and "+primInfo2[0]+" are perpendicular to primitive "+tag);
					if (primInfo1[0] != primInfo2[0]) { //Different tags
						if (isSameSide(label, tag, primInfo1[0], primInfo1[2], primInfo2[0], primInfo2[2])) {
							//System.out.println("Region "+label+": Primitives "+primInfo1[0]+" and "+primInfo2[0]+" are on the same side of primitive "+tag);
							perpPrimPairsList.add(primInfo1);	
							perpPrimPairsList.add(primInfo2);	
						}
					}
				}
			}
		}
		return perpPrimPairsList;
	}

	/**
	 * Primitives firstTag and secondTag are both perpendicular to 
	 * primitive tag. They are the side chains.
	 * Primitive firstTag is perpendicular at firstjoinPoint end of
	 * primitive tag, secondTag at secondjoinPoint end.
	 * Primitive firstTag is perpendicular to primitive tag
	 * at its firstTagjoinpoint end and 
	 * primitive secondTag is perpendicular to primitive tag
	 * at its secondTagjoinpoint end. 
	 * Potentially, these 3 primitives are part of a rectangle.
	 * <p>
	 * <ol>
	 * <li>
   *		2. Find out if either of the side chains is perpendicular 
	 *		to a fourth (different than the first chain). <br>
	 *		(Once a fourth chain that is perpendicular and next to one
	 *		of the side chains is found, check if it is perpendicular to
	 *		the other side chain and parallel to the first chain.
	 *		Do not need to check this condition, it has to hold anyway.)
	 *		This fourth chain does not need to be next to the other side 
	 *		chain.
	 * <li>
	 *		3. If the fourth chain is also next to the other side chain, then a
	 *		rectangle is found.
	 * <li>
	 *		4. If it is not, try to find one or more than one collinear chains
	 *		that would extend the other side chain to connect it to the
	 *		fourth chain or extend the fourth chain to connect it to the
	 *		other side chain. 
	 * <li>
	 *		5. None of the two side chains might be next to a fourth chain.
	 *		Then each has to be extended, and then search for the fourth chain
	 *		again, go back to step 2 (for the extended part of a side chain).
	 * </ol>
	 * <p>
	 * The rectangle needs to be stored as a list of points for each side, not 
	 * as a list of primitives. Only a part of the primitive can be in the rectangle,
	 * but not completely.
	 * <p>
	 * A field in the primitives that take part in making up the rectangle
	 * can be set to say that that primitive is part of a rectangle.
	 *
	 * @param label The label of the region the rectangle is in
	 * @param startList The list of primitives that the rectangle started with
	 * @param beginPixel The first end point of the starting primitive(s) 
	 * @param endPixel The second end point of the starting primitive(s)
	 * @param firstTag First primitive that is perpendicular to the starting primitive
	 * @param firstJoinPoint The end point of the starting primitive(s) at which the firstTag primitive is perpendicular to it
	 * @param firstTagJoinPoint The end point of the firstTag primitive at which it is perpendicular to the starting primitive(s)
	 * @param secondTag Second primitive that is perpendicular to the starting primitive 
	 * @param secondJoinPoint The end point of the starting primitive(s) at which the secondTag primitive is perpendicular to it
	 * @param secondTagJoinPoint The end point of the secondTag primitive at which it is perpendicular to the starting primitive(s)
	 * @param sidesList The list of the primitives that are perpendicular to the starting primitive(s) and may make up a rectangle's sides
	 * @return True if a rectangle is found and false if not found
	 */
	public boolean makeRectangle(int label, LinkedList startList, PointPixel beginPixel, PointPixel endPixel, int firstTag, int firstjoinPoint, int firstTagjoinPoint, int secondTag, int secondjoinPoint, int secondTagjoinPoint, LinkedList[] sidesList) {
		//System.out.println("In makeRectangle of RectangleFinder starting with primitive list and primitives "+firstTag+" and "+secondTag+" that are perpendicular to it");
		boolean formsRectangle = false;
		boolean primExtendible = true;
		int neigTag, labelNo, position, sideRow, sideColumn;
		int[] tags = new int[2];
		int[] joinPoints = new int[2];
		int[] tagjoinPoints = new int[2];
		int[] newTagArray = new int[2];
		int[] startTags = new int[2];
		PixelData entry;
		PointPixel aPoint, joinPoint, joinPoint2;
		LinkedList alist, pointslist, pixelDataList, sideList, perpList;
		LinkedList sides, corners;
		Region aRegion = allRegions[label];
		//Primitive aPrim = aRegion.getPrimitive(tag);
		Primitive anotherPrim, extendedPrim, firstPrim, secondPrim;
		Primitive[] sidePrims = new Primitive[2];
		//PointPixel beginPixel = aPrim.getBeginPoint();
		//PointPixel endPixel = aPrim.getEndPoint();
		tags[0] = firstTag;
		tags[1] = secondTag;
		joinPoints[0] = firstjoinPoint;
		joinPoints[1] = secondjoinPoint;
		tagjoinPoints[0] = firstTagjoinPoint;
		tagjoinPoints[1] = secondTagjoinPoint;

		while (formsRectangle == false && primExtendible == true) { 

			//System.out.println("In makeRectangle of RectangleFinder starting with primitive list of "+startList.size()+" primitives and primitives "+tags[0]+" and "+tags[1]+" that are perpendicular to it. Size of sidesList[0] = "+sidesList[0].size()+", size of sidesList[1] = "+sidesList[1].size());

			firstPrim = aRegion.getPrimitive(tags[0]);
			secondPrim = aRegion.getPrimitive(tags[1]);
			sidePrims[0] = firstPrim;
			sidePrims[1] = secondPrim;

			startTags[0] = ((Primitive)startList.getFirst()).getTagNo();
			startTags[1] = ((Primitive)startList.getLast()).getTagNo();

			for (int i = 0; i < 1; i++) { //Checking only one primitive, not both of them
				pointslist = sidePrims[i].getAllPoints(); 
				ListIterator pointslItr = pointslist.listIterator(0);
				formsRectangle = false;
				while (pointslItr.hasNext()) {
					aPoint = new PointPixel(pointslItr.next());
					sideRow = aPoint.getRow(); 
					sideColumn = aPoint.getColumn(); 
					pixelDataList = (LinkedList)allPixels.getPixelDataWithNeigbors(sideRow, sideColumn);
					//alist = allPixels.reduceList(pixelDataList);
					formsRectangle = false;
					ListIterator lItr = pixelDataList.listIterator(0);
					while (lItr.hasNext()) {
						//System.out.print("Getting an entry: ");
						entry = (PixelData)lItr.next();
						neigTag = entry.getTag();
						labelNo = entry.getLabel();
						position = entry.getPosition();
						if (labelNo == label && neigTag != startTags[0] && neigTag != startTags[1] && neigTag != tags[0] && neigTag != tags[1] && position != PixelData.INTERIORPOINT) { 

							//This point is part of a different primitive in the same region
							//System.out.println("Checking primitive "+neigTag);
							//anotherPrim needs to be perpendicular to sidePrims[i] and
							//be on the same side of sidePrims[i] as aPrim!
							//System.out.println("Primitive "+neigTag+" neigbors primitive "+tags[i]);
							anotherPrim = aRegion.getPrimitive(neigTag); 

							if (anotherPrim.isPerpendicularTo(sidePrims[i]) && anotherPrim.isPartOfGridline() == 0 && anotherPrim.isPartOfTick() == 0 && isSameSide(label, tags[i], neigTag, position, startTags[i], joinPoints[i])) {
								//System.out.println("Primitive "+neigTag+" is perpendicular to primitive "+tags[i]+" and on the same side of "+tags[i]+" as "+startTags[i]);
								int[] indexList2 = allPixels.neigbors(anotherPrim, sidePrims[i]);
								if (indexList2[0] > 0) {
									joinPoint2 = new PointPixel((anotherPrim.getAllPoints()).get(indexList2[1]));
									//Is it next to the other side chain?
									int[] indexList = allPixels.neigbors(anotherPrim, sidePrims[(i-1)*(-1)]);
									if (indexList[0] > 0) {
										joinPoint = new PointPixel((anotherPrim.getAllPoints()).get(indexList[1]));
										formsRectangle = true;
										sides = new LinkedList();
										ListIterator sideslItr = startList.listIterator();
										while (sideslItr.hasNext()) {
											extendedPrim = (Primitive)sideslItr.next();
											extendedPrim.setPartOfRectangle(1);
											sides.add(extendedPrim);
										}
										//sides.add(aPrim);
										//aPrim.setPartOfRectangle(1);
										//sides.add(sidePrims[0]);
										//sidePrims[0].setPartOfRectangle(1);
										for (int j = 0; j < 2; j++) {
											sideslItr = sidesList[j].listIterator();
											while (sideslItr.hasNext()) {
												extendedPrim = (Primitive)sideslItr.next();
												extendedPrim.setPartOfRectangle(1);
												sides.add(extendedPrim);
											}
										}
										/*FOUND A RECTANGLE*/
										sides.add(anotherPrim);
										anotherPrim.setPartOfRectangle(1);
										//sides.add(sidePrims[1]);
										//sidePrims[1].setPartOfRectangle(1);
										corners = new LinkedList();
										corners.add(beginPixel);
										corners.add(endPixel);
										corners.add(joinPoint);
										corners.add(joinPoint2);
										Rectangle aRectangle = new Rectangle(sides, corners, aRegion.getIsFilledArea());  
										aRectangle.setColor(aRegion.getColor());
										allRectangles.add(aRectangle);
										//System.out.println(joinPoint+" is the intersection of primitives "+anotherPrim.getTagNo()+" and "+(sidePrims[(i+1) - 2*((i+1) / 2)]).getTagNo());
										//System.out.println(joinPoint2+" is the intersection of primitives "+anotherPrim.getTagNo()+" and "+(sidePrims[i]).getTagNo());
										//System.out.println("Region "+label+": Corners are: "+beginPixel+", "+endPixel+", "+joinPoint+" and "+joinPoint2);
										//System.out.println("Found a rectangle: Primitives "+neigTag+", "+firstTag + ", "+secondTag+" and "+startTags[i]+"\n");
										//return true;
										break;
									} //end of if anotherPrim neigbors the other side prim

									else { //anotherPrim neigbors sidePrims[i] but not the other side prim
										//Check if the other sidePrim can be extended to meet anotherPrim
										//sideList = new LinkedList();
										//sideList.add(sidePrims[(i-1)*(-1)]);
										perpList = new LinkedList();
										perpList.add(anotherPrim);

										if (extendPrimitive(sidePrims[(i-1)*(-1)], anotherPrim, sidesList[(i-1)*(-1)], perpList)) { //Both primitives can be extended to meet
											LineFitter aFitter = new LineFitter();
											joinPoint = aFitter.getIntersectionPoint(sidePrims[(i-1)*(-1)].getLineOrientation(), sidePrims[(i-1)*(-1)].getSlope(), sidePrims[(i-1)*(-1)].getIntercept(), anotherPrim.getLineOrientation(), anotherPrim.getSlope(), anotherPrim.getIntercept());
											formsRectangle = true;
											sides = new LinkedList();
											ListIterator sideslItr = startList.listIterator();
											while (sideslItr.hasNext()) {
												extendedPrim = (Primitive)sideslItr.next();
												extendedPrim.setPartOfRectangle(1);
												sides.add(extendedPrim);
											}
											//sides.add(aPrim);
											//aPrim.setPartOfRectangle(1);
											//Go through sidesList and perpList, add prims and set to be part of rectangle
											//sides.add(sidePrims[i]);
											//sidePrims[i].setPartOfRectangle(1);
											sideslItr = perpList.listIterator();
											while (sideslItr.hasNext()) {
												extendedPrim = (Primitive)sideslItr.next();
												extendedPrim.setPartOfRectangle(1);
												sides.add(extendedPrim);
											}
											for (int j = 0; j < 2; j++) {
												sideslItr = sidesList[j].listIterator();
												while (sideslItr.hasNext()) {
													extendedPrim = (Primitive)sideslItr.next();
													extendedPrim.setPartOfRectangle(1);
													sides.add(extendedPrim);
												}
											}
											/*FOUND A RECTANGLE*/
											corners = new LinkedList();
											corners.add(beginPixel);
											corners.add(endPixel);
											corners.add(joinPoint);
											corners.add(joinPoint2);
											Rectangle aRectangle = new Rectangle(sides, corners, aRegion.getIsFilledArea());  
											aRectangle.setColor(aRegion.getColor());
											allRectangles.add(aRectangle);
											//System.out.println("Found a rectangle: Primitive "+neigTag+" is a neigbor of primitive "+firstTag + " and of primitive "+secondTag+" and is parallel to primitive "+startTags[i]+"\n");
											//return true;
											break;
										}
									}
								} //end of if anotherPrim neigbors sidePrims[i]
							}
						}
					} //end of traversing the list of PixelDatas
					if (formsRectangle)
						//return true;
						break;
				} //end of checking all points
				if (formsRectangle)
					//return true;
					break;
			} //end of checking both chains

			//if (!formsRectangle && tag == 18 && label == 33) {
			primExtendible = false;
			if (!formsRectangle) {
				//Try to extend the chain for one primitive, do all this again.
				int i = 0;
				newTagArray[0] = -1;
				newTagArray[1] = -1;
				if (tagjoinPoints[i] == PixelData.STARTPOINT) {
					newTagArray = extendOnePrimitive(sidePrims[i], PixelData.ENDPOINT); 
					//System.out.println("Region "+label+": No rectangle found. Try to extend primitive "+tags[i]+" from its endpoint");
				}
				else if (tagjoinPoints[i] == PixelData.ENDPOINT) {
					newTagArray = extendOnePrimitive(sidePrims[i], PixelData.STARTPOINT); 
					//System.out.println("Region "+label+": No rectangle found. Try to extend primitive "+tags[i]+" from its startpoint");
				}
				if (newTagArray[0] >= 0) {
					//System.out.println("Region "+label+": primitive "+newTagArray[0]+" extends primitive "+tags[i] +" at its "+newTagArray[1]+" point");
					primExtendible = true;
					tags[i] = newTagArray[0];
					tagjoinPoints[i] = newTagArray[1];
					//endpoint or startpoint; the point at which tags[i] primitive is connected to tag primitive
//DLC
if (sidesList[i].contains(allRegions[label].getPrimitive(newTagArray[0])))
  primExtendible = false;
//end DLC
					sidesList[i].add(allRegions[label].getPrimitive(newTagArray[0]));
					//formsRectangle = makeRectangle(label, startList, beginPixel, endPixel, tags[0], joinPoints[0], tagjoinPoints[0], tags[1], joinPoints[1], tagjoinPoints[1], sidesList);
				}
			}
		}
		return formsRectangle;
	}


	/**
	 * Extends the given primitive by one primitive
	 * 
	 * @param aPrim The primitive to be extended
	 * @param  position The end point at which the primitive is to be extended	
	 * @return An array of information about the extending primitive; its tag and the endpoint at which it extends the given primitive
	 */
	private int[] extendOnePrimitive(Primitive aPrim, int position) {
		boolean pointFound = false;
		int row = -1;
		int column = -1;
		int tag2, labelNo, index;
		int tag = aPrim.getTagNo();
		int label = aPrim.getParent();
		int[] extendPrimArray = new int[2];
		extendPrimArray[0] = -1; //tag number of the primitive that extends aPrim
		extendPrimArray[1] = -1; //end point or startpoint; point at which the new primitive is connected to aPrim
		double dist, length1, length2;
		PointPixel searchPoint;
		PointPixel distStart = new PointPixel();
		PointPixel distEnd = new PointPixel();
		PixelData entry;
		if (position == PixelData.ENDPOINT) {
			row = aPrim.getEndPoint().getRow();		
			column = aPrim.getEndPoint().getColumn();		
			pointFound = true;
		}
		else if (position == PixelData.STARTPOINT) {
			row = aPrim.getBeginPoint().getRow();		
			column = aPrim.getBeginPoint().getColumn();		
			pointFound = true;
		}
		if (pointFound) {
			LinkedList pixelList = (LinkedList)allPixels.getPixelDataWithNeigbors24(row, column);
			ListIterator lItr = pixelList.listIterator(0);
			while (lItr.hasNext()) {
				//System.out.println("Getting an entry");
				entry = (PixelData)lItr.next();
				tag2 = entry.getTag();
				labelNo = entry.getLabel();
				index = entry.getPosition();
				//System.out.println("Entry: "+entry);
				//if (tag2 != tag && index != entry.INTERIORPOINT)

				if (labelNo == label && tag2 != tag && index != entry.INTERIORPOINT) {
					//This point starts or ends a different primitive in the same region
					Primitive anotherPrim = allRegions[label].getPrimitive(tag2);
					if (anotherPrim.isParallelTo(aPrim)) { 
						//System.out.println("Primitive "+tag2+" extends primitive "+tag);
						//Check the distances: new beginpoint to endpoint should be equal to
						//around the sum of the two primitives, the extended one
						//and the one extending it.
						//Distance from position's opposite of aPrim to 
						//index's opposite of anotherPrim is the total distance.
						if (position == PixelData.ENDPOINT) {
							distStart = aPrim.getBeginPoint();
						}
						else if (position == PixelData.STARTPOINT) {
							distStart = aPrim.getEndPoint();
						}
						if (index == PixelData.ENDPOINT) {
							distEnd = anotherPrim.getBeginPoint();
						}
						else if (index == PixelData.STARTPOINT) {
							distEnd = anotherPrim.getEndPoint();
						}
						LineFitter afitter = new LineFitter();
						dist = Math.sqrt(afitter.squareDistPointToPoint(distStart, distEnd));
						length1 = aPrim.getLength();
						length2 = anotherPrim.getLength();
						if (Math.abs(dist - length1 -length2) <= 2) {
							extendPrimArray[0] = tag2;
							extendPrimArray[1] = index;
						}
					}
				}
			}
		}
		return extendPrimArray;
	}


	/**
	 * Extends the given two primitives to see if they can be extended such that
	 * they meet at a point.
	 * 	
	 * Gets the intersection point (in theory) of the two primitives.
	 * Are there primitives going from the intersection point till you reach aPrim? 
	 * Are there primitives going from the intersection point till you reach anotherPrim?
	 * 
	 * @param aPrim First primitive to be extended
	 * @param anotherPrim Second primitive to be extended; it is perpendicular to the first primitive
	 * @param aList Linked list of primitives that extend the first primitive
	 * @param anotherList Linked list of primitives that extend the second primitive
	 * @return True if the two primitives can meet and false if they cannot
	 */
	private boolean extendPrimitive(Primitive aPrim, Primitive anotherPrim, LinkedList aList, LinkedList anotherList) {
		boolean aPrimMeets = false;
		boolean anotherPrimMeets = false;
		LineFitter aFitter = new LineFitter();
		PointPixel intPoint = aFitter.getIntersectionPoint(aPrim.getLineOrientation(), aPrim.getSlope(), aPrim.getIntercept(), anotherPrim.getLineOrientation(), anotherPrim.getSlope(), anotherPrim.getIntercept());
		//System.out.println("Region "+aPrim.getParent()+"; Are primitives "+aPrim.getTagNo()+" and "+anotherPrim.getTagNo()+" extendible to point "+intPoint+"?");

		//Is aPrim extendible to reach intPoint?
		//Is anotherPrim extendible to reach intPoint?
		//Find which endpoint in aPrim is closer to anotherPrim 
		//start extending there.

		double dist1 = aFitter.squareDistPointToPoint(aPrim.getBeginPoint(), intPoint);
		double dist2 = aFitter.squareDistPointToPoint(aPrim.getEndPoint(), intPoint);
		double dist = dist1;
		int row = aPrim.getBeginPoint().getRow();		
		int column = aPrim.getBeginPoint().getColumn();		
		if (dist2 < dist1) {
			row = aPrim.getEndPoint().getRow();		
			column = aPrim.getEndPoint().getColumn();		
			dist = dist2; 
		}
		if (dist > 2) {
			aPrimMeets = extend(aPrim, row, column, intPoint, dist, aList);
		}
		else {
			aPrimMeets = true;
		}
		if (aPrimMeets == true) {
			//System.out.println("Primitive "+aPrim.getTagNo()+" is extended to "+intPoint);
		}
		dist1 = aFitter.squareDistPointToPoint(anotherPrim.getBeginPoint(), intPoint);
		dist2 = aFitter.squareDistPointToPoint(anotherPrim.getEndPoint(), intPoint);
		dist = dist1;
		row = anotherPrim.getBeginPoint().getRow();		
		column = anotherPrim.getBeginPoint().getColumn();		
		if (dist2 < dist1) {
			row = anotherPrim.getEndPoint().getRow();		
			column = anotherPrim.getEndPoint().getColumn();		
			dist = dist2; 
		}
		if (dist > 2) {
			anotherPrimMeets = extend(anotherPrim, row, column, intPoint, dist, anotherList);
		}
		else {
			anotherPrimMeets = true;
		}
		if (anotherPrimMeets == true) {
			//System.out.println("Primitive "+anotherPrim.getTagNo()+" is extended to "+intPoint);
		}
		if (aPrimMeets == true && anotherPrimMeets == true) {
			//System.out.println("Primitive "+aPrim.getTagNo()+" and "+anotherPrim.getTagNo()+" are extended to "+intPoint);
			return true;
		}
		return false;
	}

	/**
	 * Extends the given primitive by finding parallel primitives to it.
	 * It is called recursively until the primitive(s) are close enough to 
	 * the destination point. 
	 * 
	 * @param aPrim Primitive to be extended 
	 * @param row The row number of the point at which the primitive is to be extended
	 * @param column The column number of the point at which the primitive is to be extended
	 * @param toPoint The destination point to which the primitive is extended
	 * @param distance The minimum distance that the extended primitive can be to the point to which the primitive is extended to stop extending
	 * @param primList The linked list of primitives that extend the given primitive
	 */
	private boolean extend(Primitive aPrim, int row, int column, PointPixel toPoint, double distance, LinkedList primList) {
		int tag2, labelNo, index;
		int tag = aPrim.getTagNo();
		int label = aPrim.getParent();
		double dist;
		PointPixel searchPoint;
		PixelData entry;
		LinkedList alist;
		ListIterator lItr;
		alist = (LinkedList)allPixels.getPixelDataWithNeigbors24(row, column);
		lItr = alist.listIterator(0);
		while (lItr.hasNext()) {
			//System.out.println("Getting an entry");
			entry = (PixelData)lItr.next();
			tag2 = entry.getTag();
			labelNo = entry.getLabel();
			index = entry.getPosition();
			//System.out.println("Entry: "+entry);
			//if (tag2 != tag && index != entry.INTERIORPOINT) {
			if (labelNo == label && tag2 != tag && index != entry.INTERIORPOINT) {
				//This point starts or ends a different primitive in the same region
				Primitive anotherPrim = allRegions[label].getPrimitive(tag2);
				if (anotherPrim.isParallelTo(aPrim)) { 
					//System.out.println("Primitive "+tag2+" extends primitive "+tag);
					searchPoint = anotherPrim.getBeginPoint();
					if (index == entry.STARTPOINT) {
						searchPoint = anotherPrim.getEndPoint();
					}
					//Distance from anotherPrim to intPoint?
					LineFitter aFitter = new LineFitter();
					dist = aFitter.squareDistPointToPoint(toPoint.getRow(), toPoint.getColumn(), searchPoint.getRow(), searchPoint.getColumn()); 
					if (dist <= 4) { //anotherPrim goes to the intersection point
						//System.out.println("Distance="+dist+". Added primitive "+tag2+" region "+label+" to extended list");
						primList.add(anotherPrim);	
						return true;
					}
					else if(dist < distance) { //If dist is smaller than initial dist, then keep extending
						//System.out.println("Distance="+dist+". Added primitive "+tag2+" region "+label+" to extended list");
						primList.add(anotherPrim);	
						if (extend(anotherPrim, searchPoint.getRow(), searchPoint.getColumn(), toPoint, dist, primList)) {
							return true;
						}	
					}
				}
			}
		} //end of traversing chains at one location
		return false;
	}


	/**
	 * This method creates a 2d array representation of an image 
	 * that has the rectangle pixels
	 * as black and all others as white. 
	 *
	 * @param rectangles The linked list of rectangles 
	 * @return The 2d array representation of the rectangles image	
	 */
	public int[][] makeRectangleImage(LinkedList rectangles) {
		LinkedList alist, primlist, pointsList;
		Primitive aPrim;
		Rectangle aRectangle;
		Point aPoint;
		ListIterator lItr, lItr2, lItr3;
		int[][] rectangleImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				rectangleImage[i][j] = 255;
			}
		}
		lItr = rectangles.listIterator(0);
		while(lItr.hasNext()) {
			aRectangle = (Rectangle)lItr.next();
			primlist = aRectangle.getSides();
			lItr2 = primlist.listIterator(0);
			while(lItr2.hasNext()) {
				aPrim = (Primitive)lItr2.next();
				pointsList = aPrim.getAllPoints();
				lItr3 = pointsList.listIterator(0);
				while (lItr3.hasNext()) {
					aPoint = (Point)lItr3.next();
					rectangleImage[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
				}
			}
		}
		return rectangleImage;
	}
				
	/**
	 * Returns all the rectangles found in the image.
	 *
	 * @param none 
	 * @return The linked list of all the rectangles
	 */
	public LinkedList getRectangles() {
		return allRectangles;
	}


	/**
	 * Finds out whether the two primitives 
	 * lie on the same side of a third primitive tag.
	 *
	 * @param label The label of the region where the primitives are
	 * @param tag The tag of the initial primitive for which the other two primitives will be checked
	 * @param firstTag The tag of the first primitive that is perpendicular to the initial primitive
	 * @param firstjoinPoint The point (starting point or end point) of the first primitive at which it touches the initial primitive
	 * @param secondTag The tag of the second primitive that is perpendicular to the initial primitive
	 * @param secondjoinPoint The point (starting point or end point) of the second primitive at which it touches the initial primitive
	 * @return True if the two primitives are on the same side of the initial primitive, and false if they are not
	 */
	private boolean isSameSide(int label, int tag, int firstTag, int firstjoinPoint, int secondTag, int secondjoinPoint) {
		boolean sameSide;
		boolean endpoint1 = false;
		boolean endpoint2 = false;
		PointPixel beginPixel = (allRegions[label].getPrimitive(tag)).getBeginPoint();
		PointPixel endPixel = (allRegions[label].getPrimitive(tag)).getEndPoint();
		PointPixel firstP = new PointPixel();
		PointPixel secondP = new PointPixel();
		if (firstjoinPoint == PixelData.STARTPOINT) {
			firstP = (allRegions[label].getPrimitive(firstTag)).getEndPoint();
			endpoint1 = true;
		}
		else if (firstjoinPoint == PixelData.ENDPOINT) {
			firstP = (allRegions[label].getPrimitive(firstTag)).getBeginPoint();
			endpoint1 = true;
		}
		if (secondjoinPoint == PixelData.STARTPOINT) {
			secondP = (allRegions[label].getPrimitive(secondTag)).getEndPoint();
			endpoint2 = true;
		}
		else if (secondjoinPoint == PixelData.ENDPOINT) {
			secondP = (allRegions[label].getPrimitive(secondTag)).getBeginPoint();
			endpoint2 = true;
		}
		if (endpoint1 && endpoint2) {
			//System.out.println("Are "+firstP+" and "+secondP+" on the same side of line between "+beginPixel+" and "+endPixel+"?");
			LineFitter aLineFitter = new LineFitter();
			sameSide = aLineFitter.isOnSameSide(beginPixel, endPixel, firstP, secondP);
			return sameSide;
		}
		return false;
	}

	/**********************
	METHODS BELOW ARE NOT USED
	private LinkedList findPerpendicularPrimsOld(int tag, PointPixel beginPixel, PointPixel endPixel) {
		boolean flag;
		boolean found = false;
		boolean sameSide = false;
		int row, column, neigRow, neigColumn, noOfNeigbors;
		int tag, tag2, labelNo, position, firstTag, secondTag;
		int noOfPerpendiculars;
		int firstjoinPoint = PixelData.INTERIORPOINT;
		int secondjoinPoint = PixelData.INTERIORPOINT;
		int firstTagjoinPoint = PixelData.INTERIORPOINT;
		int secondTagjoinPoint = PixelData.INTERIORPOINT;
		for (int k = 1; k <= 2; k++) { //Do it two times, for each endpoint
			found = false;
			if (k == 1) {
				row = beginPixel.getRow();
				column = beginPixel.getColumn();
			}
			else {
				row = endPixel.getRow();
				column = endPixel.getColumn();
			}
			LinkedList pixelDataList = (LinkedList)allPixels.getPixelDataWithNeigbors(row, column);
			//LinkedList alist = allPixels.reduceList(pixelDataList);
			ListIterator lItr = pixelDataList.listIterator(0);
			while (lItr.hasNext()) {
				//System.out.print("Getting an entry: ");
				entry = (PixelData)lItr.next();
				tag2 = entry.getTag();
				labelNo = entry.getLabel();
				position = entry.getPosition();
				//System.out.println("Entry: "+entry);
				if (labelNo == i && tag2 != tag && position != PixelData.INTERIORPOINT) { 
					//System.out.println("Primitive "+tag2+" neigbors primitive "+tag+" at ("+row+", "+column+"). Position = "+position);
					//This point is part of a different primitive in the same region
					anotherPrim = aRegion.getPrimitive(tag2); 
					if (anotherPrim.isPerpendicularTo(aPrim) && anotherPrim.isPartOfGridline() == 0 && anotherPrim.isPartOfTick() == 0) { 
						//System.out.println("Primitive "+tag2+" is perpendicular to primitive "+tag);
						if (noOfPerpendiculars == 0 && k == 1) {
							firstTag = tag2;
							firstTagjoinPoint = position;
							firstjoinPoint = PixelData.STARTPOINT;
							noOfPerpendiculars++;
							found = true;
							break;
						}
						else if (noOfPerpendiculars == 1 && k == 2 && tag2 != firstTag) { //Perpendicular on the other end
							secondTagjoinPoint = position;
							secondjoinPoint = PixelData.ENDPOINT;
							noOfPerpendiculars++;
							found = true;
							break;
						}
					}
				}
			} //end of traversing entries at one location
		} //end of checking begin point and end point
		if (found && noOfPerpendiculars == 2) {
			//System.out.println("Region "+i+": Primitives "+firstTag+" and "+secondTag+" are perpendicular to primitive "+tag);
			if (isSameSide(i, tag, firstTag, firstTagjoinPoint, secondTag, secondTagjoinPoint)) {
				//System.out.println("Region "+i+": Primitives "+firstTag+" and "+secondTag+" are on the same side of primitive "+tag);
			}
		}
	}
	***************/
}

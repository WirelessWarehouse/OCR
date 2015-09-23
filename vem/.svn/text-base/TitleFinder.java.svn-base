import java.util.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to determine which words make up the title of the image, 
 * the x axis and the y axis titles and the x axis and y axis 
 * labels.
 * <p>
 * The title of the chart has a horizontal axis. The distance from
 * the origin is small. The height of the bounding boxes should be similar
 * and larger than the rest of the words in the image.
 * The title text is centered horizontally in the image.
 * 
 * @author Chart Reading project
 * @version 1.0
 */
public class TitleFinder {

  private int imageHeight;    //number of rows in the input image
	private int imageWidth;     //number of columns in the input image
	private int noOfTitleWords; 
	private int bPixValue; 
	private LinkedList titleWords; 

	private Hashtable words;			//all the words


	/**
	 * Constructor.
	 *
	 * @param inImage The image of the regions which are found to be characters
	 * @param imageRows The number of rows
	 * @param imageColumns The number of columns
	 */
	public TitleFinder(Hashtable w, int imageRows, int imageColumns, int bP) {
		words = w;
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPixValue = bP;
	}

	/**
	 * Finds the words that make up the title of the chart.
	 * First, the words in the upper part of the chart are extracted.
	 * The words are required to be larger than a threshold.
	 * Then, the axis lines of the words are inspected.
	 * The axis line of a line is determined.
	 * The axis line of a line of the chart title is assumed to be
	 * centered, so this factor is also tested.
	 * The words/lines that do not comply are determined not to be
	 * be title words. The rest stay as title words.
	 * After the axes of the chart is found and maybe
	 * after the chart type is determined, the title words
	 * need to be inspected again for other conditions.
	 * The title words cannot be in the axes area in the case of 
	 * a bar or a line chart. For a pie chart, the title words
	 * need to be above the wedges.
	 *
	 * @param none
	 */
	public void findChartTitle() {
		int aDist, noOfTitleWords, totalWordHeight;
		double averageHeight;
		Integer aKey;
		Word aWord;
		LinkedList aList;
		ListIterator lItr;
		titleWords = new LinkedList();
		Hashtable horWords = new Hashtable();
		Hashtable verWords = new Hashtable();
		Collection wordsCollection = words.values();
		Iterator itr = wordsCollection.iterator();
		while (itr.hasNext()) {
			aWord = (Word)itr.next();
			aDist = (int)(aWord.getDistance());
			//System.out.println(aWord);
			if (aWord.getOrientation() == Primitive.HORIZONTAL) {
				if (horWords.containsKey(new Integer(aDist))) {
					aList = (LinkedList)horWords.get(new Integer(aDist));
					aList.add(new Integer(aWord.getKey()));
				}
				else {
					aList = new LinkedList();
					aList.add(new Integer(aWord.getKey()));
					horWords.put(new Integer(aDist), aList);
				}
			}
			else {
				if (verWords.containsKey(new Integer(aDist))) {
					aList = (LinkedList)verWords.get(new Integer(aDist));
					aList.add(new Integer(aWord.getKey()));
				}
				else {
					aList = new LinkedList();
					aList.add(new Integer(aWord.getKey()));
					verWords.put(new Integer(aDist), aList);
				}
			}
		}
		//System.out.println("There are "+horWords.size()+" horizontal word lists.");
		//System.out.println("There are "+verWords.size()+" vertical word lists.");
		//Go through the horizontal words list for the distances
		//from 0 to 1/4 of the image height.
		averageHeight = imageHeight*0.03;
		totalWordHeight = 0;
		noOfTitleWords = 0;
		//System.out.println("Image height is "+imageHeight);
		//System.out.println("Average title word height is "+averageHeight);
		for (int i = 0; i < imageHeight/4; i++) {
			if (horWords.containsKey(new Integer(i))) {
				aList = (LinkedList)horWords.get(new Integer(i));
				lItr = aList.listIterator(0);
				while (lItr.hasNext()) {
					aKey = (Integer)lItr.next();
					aWord = (Word)words.get(aKey);
//DLC uncommented line below
					//System.out.println("Horizontal word "+aWord);
					//System.out.println("Average title word height is "+averageHeight);
// DLC dropped the first condition
					//if ((aWord.getHeight() >= imageHeight*0.03) & (aWord.getHeight() >= 0.75*averageHeight)) {
//DLC this condition is too strong until subtext can be distinguished
					//if ((aWord.getHeight() >= 0.75*averageHeight)) {
					if ((aWord.getHeight() >= 0.25*averageHeight)) {
// end DLC
// DLC uncommented the line below
						//System.out.println("This is a chart title word.");
						aWord.setIsTitle(true);
						titleWords.add(aKey);
						totalWordHeight += aWord.getHeight();
						noOfTitleWords++;
						averageHeight = totalWordHeight/noOfTitleWords;
						//System.out.println("Average title word height for "+noOfTitleWords+" is "+averageHeight+", total is "+totalWordHeight);
					}
				}
			}
		}
		//System.out.println("There are "+noOfTitleWords+" words in the title.");
		//Second pass. Check the words found previously. 
		//Find the axis lines and find the end points of the axis lines
		//that are aligned. Determine which axis lines are the title words.
		//The title words need to be sort of centered in the image.
		LinkedList titleDistances = new LinkedList();
		HashSet theSet = new HashSet();
		Integer aDistance;
		HashSet aDistSet;
		int minDistance, found, minDiff;
		double wordDistance;
		ListIterator lItr2;
		lItr = titleWords.listIterator();
		while (lItr.hasNext()) {
			aKey = (Integer)lItr.next();
			aWord = (Word)words.get(aKey);
			wordDistance = aWord.getDistance();
			//Compare the new distance with the ones already in the set.
			//If there is a close enough one, add it to that one.
			//If not, add as a new entry
			minDistance = imageHeight;
			minDiff = imageHeight;
			found = 0;
			lItr2 = titleDistances.listIterator();
			while (lItr2.hasNext()) {
				aDistSet = (HashSet)lItr2.next();
				itr = aDistSet.iterator();
				while (itr.hasNext()) {
					aDistance = (Integer)itr.next();
					if (aDistance.intValue() == (int)wordDistance) {
						found = 1;
						break;
					}
					if ((Math.abs(aDistance.intValue() - (int)wordDistance)) < minDiff) {
						minDistance = aDistance.intValue();
						theSet = aDistSet;
						found = 2;
					}
				}
				if (found == 1) {
					break;
				}
			}
			if (found == 1) {
				//Do nothing, the distance is already in the list
			}
			else if ((found == 2) && ((Math.abs(minDistance - (int)wordDistance)) < 5)) {
				//There is a very close distance, add this one to that one's hash set
				theSet.add(new Integer((int)wordDistance));
				//System.out.println("Added distance "+ (int)wordDistance + " to an existing set. Min distance is "+minDistance);
			}
			else {
				//There is no close distance, add this one as a new hash set
				aDistSet = new HashSet();
				aDistSet.add(new Integer((int)wordDistance));
				titleDistances.add(aDistSet);
				//System.out.println("Added distance "+ (int)wordDistance + " as a new set");
			}
		}

		PointPixel beginP = new PointPixel();
		PointPixel endP = new PointPixel();
		PointPixel beginP2, endP2;
		int dist1, dist2, foundTitle, count;

		lItr2 = titleDistances.listIterator();
		while (lItr2.hasNext()) {
			aDistSet = (HashSet)lItr2.next();
			beginP.move(0,0);
			endP.move(0,0);
			foundTitle = 0;
			count = 0;
			itr = aDistSet.iterator();
			while (itr.hasNext()) {
				aDistance = (Integer)itr.next();
				count++;
				if (horWords.containsKey(aDistance)) {
					aList = (LinkedList)horWords.get(aDistance);
					if (count == 1) {
						//Initialize the begin and end points of word axes
						aKey = (Integer)aList.getFirst();
						aWord = (Word)words.get(aKey);
						if (aWord.getIsTitle() == true) {
							foundTitle = 1;
						}
						beginP.move(aWord.getAxisBeginPoint().getRow(), aWord.getAxisBeginPoint().getColumn());
						endP.move(aWord.getAxisEndPoint().getRow(), aWord.getAxisEndPoint().getColumn());
					}
					lItr = aList.listIterator(0);
					while (lItr.hasNext()) {
						aKey = (Integer)lItr.next();
						aWord = (Word)words.get(aKey);
						if (aWord.getIsTitle() == true) {
							foundTitle = 1;
							beginP2 = aWord.getAxisBeginPoint();
							endP2 = aWord.getAxisEndPoint();
							if (beginP2.getColumn() < beginP.getColumn()) {
								beginP.move(beginP2.getRow(), beginP2.getColumn());
							}
							if (endP2.getColumn() > endP.getColumn()) {
								endP.move(endP2.getRow(), endP2.getColumn());
							}
						}
					}
					//System.out.println("Word axes for distance "+aDistance+" is from "+beginP+" to "+endP);
				}
			}
			//System.out.println("Word axes for set of distances is from "+beginP+" to "+endP);
			if (foundTitle == 1) {
				dist1 = beginP.getColumn();
				dist2 = imageWidth - endP.getColumn();
// Too strict for carberry1.gif
				//if (Math.abs(dist1 - dist2) > imageWidth/10) {
				if (Math.abs(dist1 - dist2) > imageWidth/5) {
					//The words are not title words.
					itr = aDistSet.iterator();
					while (itr.hasNext()) {
						aDistance = (Integer)itr.next();
						if (horWords.containsKey(aDistance)) {
							//System.out.println("Words at distance "+aDistance+" are not title words.");
							aList = (LinkedList)horWords.get(aDistance);
							lItr = aList.listIterator(0);
							while (lItr.hasNext()) {
								aKey = (Integer)lItr.next();
								aWord = (Word)words.get(aKey);
									aWord.setIsTitle(false);
							}
						}
					}
				}
			}
		}
	}


 	/**
	 * Checks the words that are found to be title words
	 * with respect to the axes of the image.
	 * The title words need to be above the axes area.
	 * 
	 * @param none
	 * @return The vertical axis in the image
	 */
	public void checkAxes(Axis vAxis) {
		//int checkRow = vAxis.getUpperLeft().getRow();
		int checkRow ;
                if (vAxis.getSize() > 0)
                   checkRow = vAxis.getBeginPoint().getRow();
                else checkRow = 30; // should always have a vertical axis
		Word aWord;
		Collection wordsCollection = words.values();
		Iterator itr = wordsCollection.iterator();
		while (itr.hasNext()) {
			aWord = (Word)itr.next();
			if (aWord.getIsTitle() == true) {
				if (aWord.getLowerRight().getRow() > checkRow) {
					aWord.setIsTitle(false);
				}
			}
		}
	}

 	/**
	 * Checks the words that are found to be title words
	 * with respect to the wedges of the image.
	 * The title words need to be above all the wedges.
	 * 
	 * @param none
	 * @return The vertical axis in the image
	 */
	public void checkWedges(LinkedList wedges) {
		int checkRow = imageHeight; 
		Wedge aWedge;
		Word aWord;
		ListIterator lItr = wedges.listIterator(0);
		while (lItr.hasNext()) {
			aWedge = (Wedge)lItr.next();
			if (aWedge.getUpperLeft().getRow() < checkRow) {
				checkRow = aWedge.getUpperLeft().getRow();
			}
		}
		Collection wordsCollection = words.values();
		Iterator itr = wordsCollection.iterator();
		while (itr.hasNext()) {
			aWord = (Word)itr.next();
			if (aWord.getIsTitle() == true) {
				if (aWord.getLowerRight().getRow() > checkRow) {
					aWord.setIsTitle(false);
				}
			}
		}
	}

 	/**
	 * Creates an image that shows the chart title only.
	 * 
	 * @param none
	 * @return The 2d array representation of the chart title image
	 */
	public int[][] getTitleImage() {
		int h, w;
		PointPixel upperLeft;
		int[][] titleImage = new int[imageHeight][imageWidth];
		int[][] wordArea;
		Word aWord;
		Collection wordsCollection = words.values();
		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {
				titleImage[i][j] = 255;
			}
		}
		Iterator itr = wordsCollection.iterator();
		while (itr.hasNext()) {
			aWord = (Word)itr.next();
			if (aWord.getIsTitle() == true) {
				wordArea = aWord.getArea();
				h = aWord.getHeight();
				w = aWord.getWidth();
				upperLeft = aWord.getUpperLeft();
				for (int i = 0; i < h; i++) {
					for (int j = 0; j < w; j++) {
						//titleImage[i+upperLeft.getRow()][j+upperLeft.getColumn()] = wordArea[i][j];
						if (wordArea[i][j] != bPixValue) {
							titleImage[i+upperLeft.getRow()][j+upperLeft.getColumn()] = 0;
						}
					}
				}
			}
		}
		return titleImage;
	}
 

 	/**
	 * Returns the word image in a 2d array
	 * 
	 * @param none
	 * @return The 2d array representation of the word image
	 *
	public int[][] getWordImage() {
		return wordImage;
	}
 
 	**
	 * Returns the set of words found
	 * 
	 * @param none
	 * @return The collection of the words
	 *
	public Collection getWords() {
		return words.values;
	}
	*******/

    /*
    * For now, findChartYTitle just finds all vertical words
    * and assumes that they are in the Y Title.
    */

/*
* Added condition that word be near left edge of chart
*/

    public void findChartYTitle() {
//System.out.println("XXXXXXX in findChartYTitle");
	Word aWord;
        Iterator itr = words.values().iterator();
	while (itr.hasNext()) {
	    aWord = (Word)itr.next();
	    if (aWord.getOrientation() == Primitive.VERTICAL) {
// DLC restrict search area for vertical label of Y axis
// was 20 for Amex example
              if (aWord.getUpperLeft().getColumn() < 40)
// end DLC
                {//System.out.println("found YTitle word " + aWord);
		aWord.setIsYTitle(true);}}}
                }



}


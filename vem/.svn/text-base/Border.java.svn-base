import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;

/**
 * A class to find the borders of the image once the image is labelled.
 *
 * @author Chart Reading project
 * @version 1.0
 */

public class Border {
	final int[] rPos = {-1, -1, -1, 0, 1, 1, 1, 0}; 
	final int[] cPos = {1, 0, -1, -1, -1, 0, 1, 1};
	private final int UNTAGGED = -1;
	private final int BACKGROUND = 0;
	private final int ISOLATED = 1;
	private final int ENDPOINT = 2;
	private final int INTERIOR = 3;
	private final int CORNER = 4;
	private final int JUNCTION = 5;

	private int imageHeight;    // #rows in the input image
	private int imageWidth;    	// #columns in the input image
	private int bPix;		    		//Background
 	private int bPixLabel;

	private int[][] labelledImage;  //Labelled image
	private int noOfLabels;
	private int[][] borderedImage;
	private PixelDatabase pixelData;

 /**
	* Constructor. Initializes a PixelDatabase object to hold 
	* information for each pixel according to the row and column of the pixel.
	*
	* @param inImage The 2d array representation of the labelled image
	* @param labelCount The number of labels (number of regions)
	* @param imageRows The number of rows
	* @param imageColumns The number of columns
	* @param bGroundPix The color of the background pixel; the label of the background colored regions is zero
	*/
	public Border(int[][] inImage, int labelCount, int imageRows, int imageColumns, int bGroundPix) {
		labelledImage = inImage;
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPix = bGroundPix;
		bPixLabel = 0;
		noOfLabels = labelCount;
		borderedImage = new int[imageRows][imageColumns];
		pixelData = new PixelDatabase(imageRows, imageColumns);
	}

  /**
   * Finds the border pixels of the already labelled image. 
	 * A 2d array, borderedImage, array is obtained.
	 * borderedImage is all background except the border pixels
	 * which are colored with their corresponding label.
	 *
   * @param none
   */
	public void applyBorderDetection() {
		int imageRows = imageHeight;
		int imageColumns = imageWidth;	
		//System.out.println(imageRows+"x"+imageColumns);
		int label_c; //this pixels label
		int label_n; //one of the neighbours label
		boolean border_pixel = false;
	 	int[][] inImage = new int[imageRows+2][imageColumns+2];  	
   	//labelsArray = new int[noOfLabels]; this is a private variable

		//thinBorders(labelledImage, 1);
		//insert one row of background pixels at the bottom and one on top 
		for (int i = 0; i<imageColumns+2; i++) {
			inImage[0][i] = bPixLabel;
			inImage[imageRows+2-1][i] = bPixLabel;
		}
		for (int i = 0; i<imageRows+2; i++) {
			inImage[i][0] = bPixLabel;
			inImage[i][imageColumns+2-1] = bPixLabel;
		}
		//System.out.println("Copying...");
		for (int i = 1; i<imageRows+2-1; i++) {
			for (int j = 1; j<imageColumns+2-1; j++) {
				inImage[i][j] = labelledImage[i-1][j-1];
				borderedImage[i-1][j-1] = bPixLabel; //initialize to background 
				//System.out.print(borderedImage[i-1][j-1]);
			}
		}
		//System.out.println("Inserted background pixels on top and bottom.");

		for(int r = 1; r < imageRows+2-1; r++ ) {
			for( int c = 1; c < imageColumns+2-1; c++ ) {
				label_c = inImage[r][c]; 
				border_pixel = false;

    		if (label_c != bPixLabel) { //not a background pixel
				//if (allChains.get(label_c) == null) //key label_c is not mapped to any value 
					//allChains.put(label_c, new LinkedList()); 
					border_pixel = false;
					for (int i = 1; i < 8; i += 2) {  //check 4 neighbors, not 8
 						label_n = inImage[r+rPos[i]][c+cPos[i]];
 						if (label_c != label_n) { //then (r,c) is a border pixel
							borderedImage[r-1][c-1] = label_c;
							border_pixel = true;
							//System.out.println("("+r+", "+c+") is a border pixel of label "+label_c);
							break;
						}
					}
				}
			}
		}
	}


  /**
   * Calls applyBorderDetection that 
	 * finds the borders of the already labelled image. 
	 * Then calls thinBorders method that erodes the image 
	 * and then storePixelNeigbors method that stores information in the pixelData database. 
	 * This method is called from BWImage.java after the thick lines are thinned.
	 *
   * @param none
   */
	public void findBordersAndRecord() {
		applyBorderDetection();
		thinBorders(borderedImage, 1);
		storePixelNeigbors();
	}

  /**
   * Stores the pixels that a pixel is neigboring in its own region 
	 * in the pixelData database.
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
	 * Returns the number and location of the neigbors
	 * of the given pixel which are in the same region as itself.
	 *
	 * @param row Row number of the pixel whose neighbors will be returned
	 * @param column Column number of the pixel whose neighbors will be returned
	 * @return The integer array specifying the position of the neighboring pixels of the given pixel
	 */
	private int[] getRegionNeigbors(int row, int column) {
		PointPixel neigPoint;
 		int labeln, neigRow, neigColumn;
	 	int numNeigbors = 0;
	 	int k = 0;
	 	int[] neigborArray = new int[9];
	 	neigborArray[0] = numNeigbors;

		int labelc = borderedImage[row][column];
 		PointPixel aPoint = new PointPixel(row, column);
	 	LinkedList neigborList = aPoint.getNeigbors8();
	 	ListIterator lItr = neigborList.listIterator();
	 	while (lItr.hasNext()) {
		 	neigPoint = new PointPixel(lItr.next());
			neigRow = neigPoint.getRow();
			neigColumn = neigPoint.getColumn();
			if (neigRow >= 0 && neigRow < imageHeight && neigColumn >= 0 && neigColumn < imageWidth) {
				labeln = borderedImage[neigRow][neigColumn];
				if (labeln == labelc) {
			 		numNeigbors++;
				 	neigborArray[numNeigbors] = k;
				}
			 	k++;
			}
		}
	 	neigborArray[0] = numNeigbors;
	 	return neigborArray;
 	}


  /**
	 * Defines the structuring elements used for border eroding.
	 *
	 * @param none
	 */
	private Vector defineModels() {
		Vector models = new Vector(12);
		int[] aModel1 = {0, 0, 0, 0, 1, 1, 0, 1, 1};
		models.add(aModel1);
		int[] aModel2 = {1, 1, 0, 1, 0, 0, 0, 0, 1};
		models.add(aModel2);
		int[] aModel3 = {0, 0, 0, 1, 0, 1, 1, 0, 1};
		models.add(aModel3);
		int[] aModel4 = {0, 1, 1, 0, 0, 0, 0, 1, 1};
		models.add(aModel4);
		int[] aModel5 = {0, 0, 1, 1, 0, 1, 0, 0, 1};
		models.add(aModel5);
		int[] aModel6 = {0, 1, 0, 0, 0, 0, 1, 1, 1};
		models.add(aModel6);
		int[] aModel7 = {1, 0, 0, 0, 0, 1, 0, 1, 1};
		models.add(aModel7);
		int[] aModel8 = {0, 1, 0, 1, 1, 0, 0, 0, 1};
		models.add(aModel8);
		/*
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
		*/
		/*
		int[] aModel9 = {0, 0, 0, -1, 1, 1, 1, -1, 1};
		models.add(aModel9);
		int[] aModel10 = {1, 1, 1, -1, 0, 0, 0, -1, 1};
		models.add(aModel10);
		int[] aModel11 = {0, -1, 1, 1, 1, -1, 0, 0, 1};
		models.add(aModel11);
		int[] aModel12 = {1, -1, 0, 0, 0, -1, 1, 1, 1};
		models.add(aModel12);
		*/
		return models;
	}
																																																		
  /**
	 * Defines more structuring elements used for border eroding,
	 * adds to the Vector models.
	 *
	 * @param Vector models 
	 */
	private void defineModelsAdd(Vector models) {
		int[] aModel9 = {-1, 1, -1, 1, 0, 0, 0, 1, 1};
		models.add(aModel9);
		int[] aModel10 = {0, 0, 0, 1, -1, 1, -1, 1, 1};
		models.add(aModel10);
		int[] aModel11 = {0, 1, -1, 1, -1, 1, 0, 0, 1};
		models.add(aModel11);
		int[] aModel12 = {-1, 1, 0, 0, 0, 1, -1, 1, 1};
		models.add(aModel12);
	}


  /**
	 * Erodes the given image (anImage) according to the structuring elements
	 * defined by defineModels method.
	 * Models should be a vector of length n (no. of the models to be used for erosion)
	 * Each element should have one array having 9 members, the middle pixel and the 8 neigbors. The middle pixel is that last element.
	 * Array has 0's, 1's, and -1's. 
	 * 1's should be having the same label all and 
	 * all 0's should have a label different than that one, 
	 * the label of -1's is not important, they can have any label.
	 * The 2d array anImage, which is an input parameter, is changed.
	 *
	 * @param anImage The input image which is eroded. It is changed by setting the eroded pixel values to the bacground value.
	 * @param option If option is 2, the structuring models are increased by calling defineModelsAdd after calling defineModels. Otherwise, only defineModels is called.
	 */
	private void thinBorders(int[][] anImage, int option) {
		//System.out.println("In Borders.");
		Vector models = defineModels();
		if (option == 2)
			defineModelsAdd(models);
		int imageRows = imageHeight;
		int imageColumns = imageWidth;
		int label_check = 0;
		int label, pixelLabel;
		int match;
		int[] modelArray;
		int[] rowPos = {-1, -1, -1, 0, 1, 1, 1, 0, 0};
		int[] colPos = {1, 0, -1, -1, -1, 0, 1, 1, 0};
		int[][] borderImage = new int[imageRows+2][imageColumns+2];
		//insert one row of background pixels at the bottom and one on top
		for (int i = 0; i<imageColumns+2; i++) {
			borderImage[0][i] = bPixLabel;
			borderImage[imageRows+2-1][i] = bPixLabel;
		}
		for (int i = 0; i<imageRows+2; i++) {
			borderImage[i][0] = bPixLabel;
			borderImage[i][imageColumns+2-1] = bPixLabel;
		}
		for (int i = 1; i<imageRows+2-1; i++) {
			for (int j = 1; j<imageColumns+2-1; j++) {
				borderImage[i][j] = anImage[i-1][j-1];
			}
		}
		int count = 1;
		while (count > 0) {
			count = 0;
			for(int r = 1; r < imageRows+2-1; r++ ) {
				for(int c = 1; c < imageColumns+2-1; c++ ) {
					pixelLabel = borderImage[r][c];
					if (pixelLabel != bPixLabel) {
						//Go through each structuring model
						for (int k = 0; k < models.size(); k++) {
							modelArray = (int [])models.elementAt(k);
							match = 1;
							//Find which label corresponds to the value 1 in the structuring element
							for (int i = 0; i < 9; i++) {
								if (modelArray[i] == 1) {
									label_check = borderImage[r+rowPos[i]][c+colPos[i]];
									break;
								}
							}
							//Check all the neighbors of the pixel
							for (int i = 0; i < 9; i++) {
								label = borderImage[r+rowPos[i]][c+colPos[i]];
								if (modelArray[i] == 1 && label != label_check)
									match = 0;
								if (modelArray[i] == 0 && label == label_check)
									match = 0;
							}
							if (match == 1) { //this pixel passed the test
								borderImage[r][c] = bPixLabel;
								anImage[r-1][c-1] = bPixLabel;
								count++;
								//System.out.println("Removing: "+(r-1)+", "+(c-1));
								break;
							}
						}//end of for loop for the arrays in the models Vector
					}
				}
			}
			//System.out.println("Matched "+count+" times.");
		}
	}


  /**
   * Processes and returns a 2d array of the border image.
	 * The borders are black, the background is set to white.
	 *
   * @param none
	 * @return 2d array representation of the border image
   */
  public int[][] makeBorderImage() {
		int noOfChains = 0;	
		int countChains = 0;
		int[][] bImage = new int[imageHeight][imageWidth];
		for (int i=0; i<imageHeight; i++) {
			for (int j=0; j<imageWidth; j++) {
				//borderImage[i][j] = ((int)255/noOfLabels)*borderImage[i][j];
				if (borderedImage[i][j] == 0)
					bImage[i][j] = 255;
				else if (borderedImage[i][j] == 10)
					bImage[i][j] = 100;
				else
					bImage[i][j] = 0;
			}
		}
		return bImage;
	}


  /**
   * Returns the 2d array of border pixels. 
	 *
   * @param none
	 * @return 2d array representation of the border image
   */
  public int[][] getBorderImage() {
    return borderedImage;
  }


  /**
   * Sets the 2d array of labelled pixels. 
	 *
   * @param anImage The 2d array representation of the labelled image
   */
  public void setLabelImage(int[][] anImage) {
		labelledImage = anImage;
  }

  /**
   * Returns the 2d array of labelled pixels. 
	 *
   * @param none
	 * @return The 2d array representation of the labelled image
   */
  public int[][] getLabelImage() {
    return labelledImage;
  }



  /**
   * Returns the PixelDatabase of all the pixels. 
	 *
   * @param none
	 * @return The PixelDatabase giving information about all the pixels
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
}

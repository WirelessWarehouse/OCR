import java.util.*;
import java.lang.Math;
import java.lang.*;

/**
 * A class to determine the background color(s) of the image. 
 * Creates an histogram, check the largest value.
 * Checks the values at the left upper corner.
 * 
 * @author Chart Reading project 
 * @version 1.0
 */

public class Background{
	final static int [] rPos = {-1, -1, -1, 0, 1, 1, 1, 0};  //already declared in ImagePrimitives.jave
	final static int [] cPos = {1, 0, -1, -1, -1, 0, 1, 1};  //already declared in ImagePrimitives.jave
	//Storing the image as 2D array
	private int[][] inputImage;
	private int noOfColors;
	private int noOfBackgroundColors;
	private int imageHeight;
	private int imageWidth;
 	private LinkedList bPixels; //value of the background pixel(s)
 	private int bPixLabel; //label of the background pixels (set to 0)
	private int[] histogramArray;


  /**
   * Constructor. 
	 *	
   * @param inImage2D The 2d array representation of the image
	 * @param imageRows The number of rows in the image
	 * @param imageColumns The number of columns in the image
	 * @param numColors The number of colors in the image (256)
   */
	public Background(int[][] inImage2D, int imageRows, int imageColumns, int numColors) {
		inputImage = inImage2D;  
		imageHeight = imageRows;
		imageWidth = imageColumns;
		bPixels = new LinkedList();
		noOfColors = numColors;
		noOfBackgroundColors = 0;
	}

  /**
	 * Returns a linked list of the color values that are 
	 * determined to be background colors. The histogram is created first,
	 * the max color that appears in the image if found. The upper left corner
	 * of the image is also checked.
	 *
   * @param none
	 * @return The linked list of the background color values.
   */
	public LinkedList findBackground() {
		//histogramArray = histogram(noOfColors);
		getHistogram();
		int[] indeces = findMinMax(histogramArray, noOfColors);
		int maxIndex = indeces[1];
		int maxCorner = checkCorner(10, 10);
		//System.out.println("Max color is "+maxIndex+"; occurs at "+histogramArray[maxIndex]+" pixels. Max corner color is "+maxCorner);
		//Add the color that appears the most.
		bPixels.add(new Integer(maxIndex));
		noOfBackgroundColors = 1;
		//If the upper left corner color is different than the color that appears the most,
		//add the upper left corner color to the list of background colors.
		if (maxIndex != maxCorner) {
			bPixels.add(new Integer(maxCorner));
			noOfBackgroundColors++;
		}
		for (int i = 0; i < noOfColors; i++) {
			//Also, add the colors that appear more than half the maximum color that
			//appears in the image.
			if (i != maxIndex && i != maxCorner && histogramArray[i] > 0.5*histogramArray[maxIndex]) {
				bPixels.add(new Integer(i));
			}
		}
		return bPixels;
	}

  /**
	 * Checks the upper left corner of the image, in an area that
	 * is given by the input parameters, and determines which color
	 * appears the most in that area.
	 * It returns an integer indicating the color value.
	 *
   * @param height The height of the area to be checked.
	 * @param width The width of the area to be checked.
   */
	private int checkCorner(int height, int width) {
		int[] colors = new int[noOfColors];
		int max = 0;
		int maxColor = -1;
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				colors[inputImage[i][j]]++;
				if (colors[inputImage[i][j]] > max) {
					max = colors[inputImage[i][j]];
					maxColor = inputImage[i][j];
				}
			}
		}
		return maxColor;
	}

  /**
	 * Creats the histogram array of the colors of the input image.
	 * It returns an integer array.
	 *
   * @param size The maximum size of the histogram (the number of colors -256- in this case)
	 * @return The 1d array of histogram.
   */
	private int[] histogram(int size) {
		int[] his = new int[size];
		for (int k = 0; k < size; k++) {
			his[k] = 0;
		}
		for (int i = 0; i < imageHeight; i++) {
			for (int j = 0; j < imageWidth; j++) {
				his[inputImage[i][j]]++;
			}
		}
		return his;
	}

  /**
   * Finds the minimum and the maximum value of a 1-d array,
	 * in this case the histogram array. The result is returned as an integer
	 * array of size 2. The first entry is the index to the input 1-d array
	 * that is the minimum value in that array, and the second entry
	 * is the index of the maximum value in the input array.
	 *
   * @param his The 1d array
	 * @param size The length of the 1d array 
   */
	private int[] findMinMax(int[] his, int size) {
		int min = his[0];
		int minIndex = 0;
		int max = his[0];
		int maxIndex = 0;
		for (int i = 1; i < size; i++) {
			if (his[i] < min) {
				min = his[i];
				minIndex = i;
			}
			if (his[i] > max) {
				max = his[i];
				maxIndex = i;
			}
		}
		int[] indeces = new int[2];
		indeces[0] = minIndex;
		indeces[1] = maxIndex;
		return indeces;
	}

  /**
	 * Creates a 2-d array that depicts the histogram
	 * of the image. It is like a bar chart. The bars are scaled.
	 *
   * @param his
	 * @param size
	 * @param margin
   */
	public int[][] makeHistogramImage(int[] his, int size, int margin) {
		int[] indeces = findMinMax(his, size);
		int minIndex = indeces[0];
		int maxIndex = indeces[1];
		double scale = (his[maxIndex]+1.0)/size;
		//System.out.println("Histogram scale is "+scale);
		//System.out.println("Histogram minIndex is "+minIndex);
		//System.out.println("Histogram maxIndex is "+maxIndex);
		//System.out.println("his[minIndex] = " + his[minIndex]);
		//System.out.println("his[maxIndex] = " + his[maxIndex]);
		int height = size + margin;
		int width = size + margin;
		int[][] anImage = new int[height][width];
		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				anImage[i][j] = 255;
			}
		}
		for (int k = 0; k < size; k++) {
			int h = (int) (his[k] / scale);
            if (his[k] > 0) h++;
			//System.out.println("From "+(size-1)+" to "+(size-1-h));
			for (int i = size-1; i > (size-1-h); i--) {
				anImage[i+margin/2][k+margin/2] = 0;
				//System.out.println("anImage["+i+"]["+k+"] is 0");
			}
		}
		return anImage;
	}

  /**
   * Returns the 1-d integer array (the histogram) 
	 * that is found and saved as a variable of this class. 
	 *
   * @param none
	 * @return The array of the number of color values for each color value.
   */
	public int[] getHistogram() {

		if(histogramArray == null)
			histogramArray = histogram(noOfColors);
		return histogramArray;
	}
}


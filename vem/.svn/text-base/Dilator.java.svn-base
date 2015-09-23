import java.util.*;
//import java.math.*;
import java.lang.Math;
import java.awt.*;
import java.io.*;
//DLC
import java.lang.reflect.*;
//DLC

/**
 * A class to dilate a given image.
 *
 * @author Chart Reading project
 * @version 1.0
 */
public class Dilator {

	/**
	 * Constructor
	 *
	 * @param none
	 */
	public Dilator() {
	}

	/**
	 * Returns the dilated image.
	 * The structuring element is 3x3 of all 1s.
	 *
	 * @param inImage The image to be dilated
	 * @param imageRows The number of rows 
	 * @param imageColumns The number of columns
	 * @param bPix The label of the background pixels
	 * @return The dilated image in 2d array representation
	 */
	public int[][] dilate(int[][] inImage, int imageRows, int imageColumns, int bPix) {
//System.out.println("bPix = " + bPix);
		//Dilation of a binary image
		int i, j, m, value, r, c;
		int[] rowPos = {-1, -1, -1, 0, 1, 1, 1, 0, 0};
		int[] colPos = {1, 0, -1, -1, -1, 0, 1, 1, 0};
		int[][] dilatedImage = new int[imageRows][imageColumns];
		for (i = 0; i < imageRows; i++) {
			for (j = 0; j < imageColumns; j++) {
				dilatedImage[i][j] = bPix;
			}
		}
		int[] arr = defineModels();
		for (i = 0; i < imageRows; i++) {
			for (j = 0; j < imageColumns; j++) {
				value = inImage[i][j];
				if (value != bPix && arr[8] == 1) {
					for (m = 0; m < 9; m++) {
						r = i + rowPos[m];
						c = j + colPos[m];
						if (arr[m] == 1 && r >= 0 && c >= 0 && r < imageRows && c < imageColumns) {
							dilatedImage[r][c] = value;
						}
					}
				}
			}
		}
//DLC
/*
            BWImageG.save("dilated.pgm",
                          dilatedImage,
                          Array.getLength(dilatedImage),
                          Array.getLength(dilatedImage[0]));
            BWImageG.save("undilated.pgm",
                          inImage,
                          Array.getLength(inImage),
                          Array.getLength(inImage[0]));
*/

//DLC
		return dilatedImage;
  }	

 	/**
	 * Defines the structuring elements used for character dilation.
	 *
	 * @param none
	 * @return The array of the structuring element
	 */
	private int[] defineModels() {
		//Vector models = new Vector(12);
		int[] aModel1 = {1, 1, 1, 1, 1, 1, 1, 1, 1};
		//models.add(aModel1);
		//return models;
		return aModel1;
	}

/*
*  diletes on two sides only so that spacing of three pixels can
*  be sufficient to merge characters
*/

	public int[][] dilate2(int[][] inImage, int imageRows, int imageColumns, int bPix) {
		//Dilation of a binary image
		int i, j, m, value, r, c;
		int[] rowPos = {-1, -1, -1, 0, 1, 1, 1, 0, 0};
		int[] colPos = {1, 0, -1, -1, -1, 0, 1, 1, 0};
		int[][] dilatedImage = new int[imageRows][imageColumns];
		for (i = 0; i < imageRows; i++) {
			for (j = 0; j < imageColumns; j++) {
				dilatedImage[i][j] = bPix;
			}
		}
		int[] arr = {0,1,1,1,0,0,0,0,1};
		for (i = 0; i < imageRows; i++) {
			for (j = 0; j < imageColumns; j++) {
				value = inImage[i][j];
				if (value != bPix && arr[8] == 1) {
					for (m = 0; m < 9; m++) {
						r = i + rowPos[m];
						c = j + colPos[m];
						if (arr[m] == 1 && r >= 0 && c >= 0 && r < imageRows && c < imageColumns) {
							dilatedImage[r][c] = value;
						}
					}
				}
			}
		}
//DLC
            BWImageG.save("dilated2.pgm",
                          dilatedImage,
                          Array.getLength(dilatedImage),
                          Array.getLength(dilatedImage[0]));

//DLC
		return dilatedImage;
  }	
}


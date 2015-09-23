import java.io.*;
import java.util.*;
import java.awt.Point;
import javax.swing.JTextArea;
import java.awt.image.*;

 /**
 * A class to read in greyscale images in pgm format.
 * Has methods to label the image and find the borders,
 * segment borders, find characters, find words, find axes, 
 * find gridlines, find rectangles, find wedges, 
 * find connected lines,
 * determine chart type and output data values.
 * Saves processed images in pgm format.
 * <p>
 * Once the image is labelled and the border pixels of the image are found and 
 * vectorized (segmented), 
 * data is stored in an array of <code>Region</code>s. The array has as many
 * elements as the number of labels (number of regions in the image). 
 * Each <code>Region</code> has a linked list of <code>Primitive</code>s (chains)
 * in it, each <code>Primitive</code> has a distinct tag.
 * <p>
 * Data is also stored in two <code>HashMap</code>s as a <code>PixelDatabase</code>.
 * For the first <code>HashMap</code>, each pixel, given its row and column,
 * is mapped to a linked list of <code>PixelData</code>.
 * A pixel has as many <code>PixelData</code> entries as the number of 
 * chains (or <code>Primitive</code>s) that it is part of. 
 * Each <code>PixelData</code> entry holds the label and the tag of the
 * primitive that the pixel is part of and the position of the pixel in that
 * chain (start point of a chain, end point of a chain or the interior point
 * of a chain).
 * <p>
 * The second <code>HashMap</code> holds the neigborhood information. 
 * Each pixel, given its
 * row and column, is mapped to a linked list of pixels that are its neigbors
 * in the same region.
 * <p>
 * Using the database and the regions array, the axes, the rectangles, 
 * the wedges and the connected lines of the image are found. These 
 * components are stored in a <code>Graph</code> object.
 * At each stage, the results (the components that are found) are
 * stored as pgm images too. 
 * The <code>Graph</code> class has methods to determine the type
 * of the chart and obtain the data values.
 * 
 * @author Chart Reading project
 * @version 1.0
 */

public class BWImageG {
  private int rows;
  private int columns;
  private int[][] image;

	//Processed image properties:
	private int blobCount;	//number of regions 
	private int bPixValue;
	private int pixelLabel[][];	//image labels
	private int firstPixelLabel[][];	//image labels 
	private int pixelLabelThinned[][]; 	//image labels after thinning
	private int labelsArray[];	
	private int imageBorders[][];	//image borders
	private int firstImageBorders[][];	//image borders
	private int countsImage[][];	
	private Hashtable borderChains;

	private Hashtable allLines;
	private Hashtable words;
	private Region[] allRegions;
	private PixelDatabase allPixels;
	private Axis hAxis;
	private Axis vAxis;
	private boolean isFrame; //Is there a frame in the image
	private	PointPixel frameUpperLeft; //upper left corner of the bounding box of the frame
	private	PointPixel frameLowerRight;	//lower right corner of the bounding box of the frame

	private Graph aGraph;

	/**
	 * Constructor. Sets rows, columns and blobCount (number of objects or labels) 
	 * to zero, bPixValue (the background color value) to 255 and isFrame (is there
	 * a frame in the image of the chart) to false.
	 *
	 * @param none
	 */
  public BWImageG() {
		rows = 0;
		columns = 0;
		blobCount = 0;
		bPixValue = 255;
		isFrame = false;
	}


	/**
	 * Opens the given file and loads the pgm file into a 2d array.
	 *
	 * @param infilename Filename to be loaded
	 */
  public void loadFile(String infilename) {
    try {
      BufferedInputStream x =
        new BufferedInputStream(new FileInputStream(infilename));
      if (x.read() != 'P' || x.read() != '5') {
        System.out.println("not a raw pgm file");
      }
      else {
        columns = oneNum(x);
        rows = oneNum(x);
        int throw_away = oneNum(x);
        image = new int[rows][columns];
        int r, c;
        for (r = 0; r < rows; r++) {
          for (c = 0; c < columns; c++) {
            image[r][c] = x.read();
					}
				}
        x.close();
      }
    }
    catch(Exception e) {System.out.println(e.getMessage());}
  }

	/**
	 * Skips comments and reads one integer from buffered input stream 
	 *
	 * @param x 
	 */
  static int oneNum(BufferedInputStream x) {
    int c;
    int n;
    n = 0;
    try {
      c = x.read();
      while (c < '0' || c > '9') {
        if (c == '#') {
          while (c != '\n') c = x.read();
        }
        else c = x.read();
      }
      while (c >= '0' && c <= '9') {
        n = 10*n + c - '0';
        c = x.read();
      }
    }
    catch(Exception e) {System.out.println(e.getMessage());}
    return n;
  }


	/**
	 * Writes data of 2d array (oImage) to the outfilename file. 
	 *
	 * @param outfilename Filename that the image is to be saved to
	 * @param oImage The array that is to be saved
	 */
  public void save(String outfilename,int[][] oImage) {
    try {
      BufferedOutputStream x =
        new BufferedOutputStream(new FileOutputStream(outfilename));
      int r,c;
      int Imax = oImage[0][0];
      int Imin = Imax;
      for (r = 0; r < rows; r++) {
        for (c = 0; c < columns; c++) {
				  if (oImage[r][c] < Imin) Imin = oImage[r][c];
	  			if (oImage[r][c] > Imax) Imax = oImage[r][c];
      	}
			}	
//			System.out.println("Max min are "+Imax +", "+Imin);
			int k;
			if (Imax == Imin)
				k = 0;
			else
      	k = 255/(Imax - Imin);
      x.write(("P5 " + Integer.toString(columns) + " "
        + Integer.toString(rows) + " 255 ").getBytes());
      for (r = 0; r < rows; r++)
        for (c = 0; c < columns; c++)
          x.write((int)(k*(oImage[r][c] - Imin)));
      x.close();
    }
    catch(Exception e) {System.out.println(e.getMessage());}
  }

	/**
	 * Writes data of 2d array (oImage) to the outfilename file. 
	 *
	 * @param outfilename
	 * @param oImage
	 * @param height
	 * @param width 
	 */
    public static void save(String outfilename,int[][] oImage, int height, int width) {
        try {
            BufferedOutputStream x =
              new BufferedOutputStream(new FileOutputStream(outfilename));
            int r,c;
	    double k;
            int Imax = oImage[0][0];
            int Imin = Imax;
            for (r = 0; r < height; r++) {
                for (c = 0; c < width; c++) {
	            if (oImage[r][c] < Imin) Imin = oImage[r][c];
	  	    if (oImage[r][c] > Imax) Imax = oImage[r][c];
      	        }
	    }	
//System.out.println("Max min are "+Imax +", "+Imin);
	    if (Imax == Imin) {
	        k = 0;
            }
	    else {
      	        k = 255.0/(Imax - Imin);
            }
            x.write(("P5 " + Integer.toString(width) + " "
              + Integer.toString(height) + " 255 ").getBytes());
            for (r = 0; r < height; r++)
                for (c = 0; c < width; c++)
                    x.write((int)(k*(oImage[r][c] - Imin)));
                 //{if (oImage[r][c] < 255) x.write(255);
                  //else x.write(0);}
            x.close();
        } catch(Exception e) {System.out.println(e.getMessage());}
    }


/*
* Loads a java image into a BWImageG image.
*/

public void loadJavaImage (BufferedImage jImage) {
    columns = jImage.getWidth();
    rows = jImage.getHeight();
ColorModel cm = jImage.getColorModel();
//System.out.println(cm.toString());
    image = new int[rows][columns];
    int rgb;
    int r,c;
    int gray;

    for (r = 0; r < rows; r++) {
        for (c = 0; c < columns; c++) {
            //rgb = jImage.getRGB(c,r) & 255;
            rgb = jImage.getRGB(c,r);
            gray = (int)((0.3*((rgb>>16)&0xff)+0.59*((rgb>>8)&0xff)
                          + 0.11*(rgb&0xff)));
            //System.out.println(rgb);
            //System.out.println(gray);
            //image[r][c] = rgb;}}
            image[r][c] = gray;}}
//save("a.pgm",image);
}


  /**
	 * Creates an object of the Background class 
	 * and calls findBackground method of Background class that 
	 * determines which value(s) should be considered as background.
	 * Also, changes the image array if there is more than one value
	 * for the background.
	 *
   * @param none
   */
	public void findBackground() {
		Background aFinder = new Background(image, rows, columns, 256);
		LinkedList bPixels = aFinder.findBackground();
		int margin = 10; //For the histogram image
		int[][] histogramImage = aFinder.makeHistogramImage(aFinder.getHistogram(), 256, margin);
		//System.out.println("Histogram image margins are "+(margin/2)+" pixels wide.");
		//save("Histogram.pgm", histogramImage, (256+margin), (256+margin)); 
		//System.out.println("Saved histogram image to Histogram.pgm");
		bPixValue = ((Integer)bPixels.getFirst()).intValue();
		//System.out.println("There are "+bPixels.size()+" background colors");
		//System.out.println("Background color is "+bPixValue);
		try {
			//System.out.println("Histogram image margins are "+(margin/2)+" pixels wide. Saved histogram image to Histogram.pgm\n");
			//System.out.println("There are "+bPixels.size()+" background colors. Background color is "+bPixValue+"\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
		//If there are more than one background color values,
		//Change all of them to the first one
		if (bPixels.size() > 1) {
			int aValue;
			for (int i = 0; i < rows; i++) {
				for (int j = 0; j < columns; j++) {
					ListIterator lItr = bPixels.listIterator(1);
					while (lItr.hasNext()) {
						aValue = ((Integer)lItr.next()).intValue();
						if (image[i][j] == aValue) {
							image[i][j] = bPixValue;
						}
					}
				}
			}
		}
		//save("NewImage.pgm", image);
	}


  /**
	 * Labels the image.
	 * Calls findBackground to determine the background colors.
	 * Calls addImage to add a row/column on each side of the image.
	 * Creates an object of the LabelImage class 
	 * and calls applyLabelling method of LabelImage class that 
	 * labels the image using the code of p.65 in Computer Vision
	 * by Shapiro and Stockman.
	 * pixelLabel variable which is the 2d array representation of the 
	 * image depicting the labels is obtained.
	 * The labelled image is saved to Labelled.pgm.
	 *
   * @param none
   */
	public void labelImage() {
		findBackground();
		addImage();
		//save("NewImage.pgm", image);
		LabelImage iLabeler = new LabelImage(image, rows, columns, bPixValue);
		Vector labelledImageInfo = iLabeler.applyLabelling();
		blobCount = ((Integer)labelledImageInfo.get(0)).intValue();
		labelsArray = (int [])labelledImageInfo.get(1);
		//the index of labelsArray is the label number, the value is the pixel value (the color) of the original image
		pixelLabel = (int [][])labelledImageInfo.get(2);

		//Save the pixelLabel array in firstPixelLabel. pixelLabel will be changed later.
		firstPixelLabel = pixelLabel;

		int[][] pixelLabelSave = getLabelImageToSave();
		save("Labelled.pgm", pixelLabelSave);
		try {
			System.out.println("Image size: "+rows+"x"+columns+ ". There are "+blobCount+" objects.\n");
			System.out.println("Saved label image to Labelled.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}

		//Initialize aGraph
		aGraph = new Graph(rows, columns);
	}


  /**
   * Creates an object of the Border class and 
	 * calls applyBorderDetection method of Border class that 
	 * finds the borders of the already labelled image.
	 * imageBorders variable is obtained.
	 * allRegions array that holds information for each region is initialized.
	 * The bounding boxes of all regions are found.
	 * The bordered image is saved to Bordered.pgm.
	 *
   * @param none
   */
	public void findBorders() { 
//System.out.println("in findBorders");
		Border borderFinder = new Border(pixelLabel, blobCount, rows, columns, bPixValue);
		borderFinder.applyBorderDetection();
		imageBorders = borderFinder.getBorderImage();

		//Initialize an array of Regions
		allRegions = new Region[blobCount];
		for (int i = 0; i < blobCount; i++) {
			Region aRegion = new Region(i);
			aRegion.setColor(labelsArray[i]);
//DLC
//System.out.println("color for region " + i + " set");
// end DLC

			allRegions[i] = aRegion;
		}
		findBoundingBoxes();
/*
* setting numPixels here doesn't seem to be visible elsewhere

                for (int i = 1; i < blobCount; i++) {
                     allRegions[i].setNumPixels(firstPixelLabel);}
*/
		//int[][] borderImageSave = getBorderImageToSave();
		//save("Bordered.pgm", borderImageSave);
		try {
			//System.out.println("Saved bordered image to Bordered.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}


  /**
   * Creates an object of the TextRecognizer class and
	 * calls findTextRegions method of TextRecognizer class that 
	 * determines the regions that are characters and dashed lines. 
	 *
	 * Checks if a frame exists in the image.
	 * The character and the frame regions are removed from the image;
	 * from pixelLabel and imageBorders arrays.
	 * The image with only text regions is saved to Text.pgm.
	 * The original image without the text regions is saved to BorderedGraph.pgm.
	 *
   * @param none
   */
	public void findCharacters() { 
		//The background label is zero.
		TextRecognizer atext = new TextRecognizer(imageBorders, blobCount, rows, columns, allRegions, 0);
		atext.findTextRegions();
		//int[][] dashedImage = getDashedLineImageToSave();
		//save("DashedLines.pgm", dashedImage); 
		//System.out.println("Saved dashed lines image to DashedLines.pgm");
		int[][] textImage = getTextImageToSave(pixelLabel);
		save("Text.pgm", textImage); 
		System.out.println("Saved text image to Text.pgm");
		findFrame();
		removeRegions();
		try {
			//System.out.println("There are "+atext.getNoOfDashedLines()+ " dashed lines. \n");
			//System.out.println("Saved dashed lines image to DashedLines.pgm\n");
			//System.out.println(atext.getNoOfCharacters()+ " regions out of "+blobCount+ " regions are character regions. \n");
			//System.out.println("Saved text image to Text.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
		int[][] borderImageSave = getBorderImageToSave();
		save("BorderedGraph.pgm", borderImageSave);
		try {
			System.out.println("Saved bordered image without characters to BorderedGraph.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}

  /**
	 * Finds words from the character regions that should already be identified.
	 * The image of words is saved to Words.pgm.
	 *
   * @param none
   */
	public boolean findWords() { 
		//The background label is zero.
		//The background color in the text image is 255
		int[][] textImage = getTextLabelImage(firstPixelLabel);
		WordFinder aFinder = new WordFinder(textImage, rows, columns, 0);
		aFinder.findWords(allRegions);
System.out.println("wordfinder found words");
		int[][] wordImage = aFinder.getWordImage();
		save("Words.pgm", wordImage); 
System.out.println("Saved word image to Words.pgm");
		try {
			//System.out.println("Saved word image to Words.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
		words = aFinder.getWordsHashtable();
                aGraph.addWords(words);
//System.out.println("added words");
                aGraph.makeTextBlocks();
		if (words.size() > 0) {
			TitleFinder tFinder = new TitleFinder(words, rows, columns, 255);
			tFinder.findChartTitle();
			//int[][] tImage = tFinder.getTitleImage();
			//save("InitialTitle.pgm", tImage); 
			//System.out.println("Saved chart title image to InitialTitle.pgm");
			tFinder.findChartYTitle();
			try {
				//System.out.println("Saved chart title image to Title.pgm\n");
			} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
			return true;
		}
		return false;
	}

  /**
	 * Dilates the image. Dilated image is saved to Dilated.pgm.
	 *
   * @param none
   */
	public void dilate() { 
		//The background value is 255 (white).
		int[][] textImage = getTextImageToSave(firstPixelLabel);
		Dilator adilator = new Dilator();
		int[][] dilatedImage = adilator.dilate(textImage, rows, columns, 255);
		save("Dilated.pgm", dilatedImage); 
		//System.out.println("Saved dilated image to Dilated.pgm");
		try {
			//System.out.println("Saved dilated image to Dilated.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}


  /**
	 * Creates an object of the FrameFinder class and
	 * calls findFrameRegions method of FrameFinder class that 
	 * checks if there is a frame, finds the region that is the frame.
	 * isFrame is set to true if there is a frame in the image.
	 *
   * @param none
   */
	public void findFrame() { 
		//The background label is zero.
		FrameFinder aFinder = new FrameFinder(imageBorders, blobCount, rows, columns, allRegions, 0);
		isFrame = false;
		if (aFinder.findFrameRegions()) {
			frameUpperLeft = aFinder.getUpperLeft();
			frameLowerRight = aFinder.getLowerRight();
			isFrame = true;
			try {
				//System.out.println("Found frame.\n");
			} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
		}
	}

  /**
   * Removes regions that are characters and
	 * regions that are part of the frame of the region from the image;
	 * from pixelLabel and imageBorders arrays. 
	 * Also, if there is a frame, sets everything outside the frame to
	 * background pixel value or background label value.
	 *
   * @param none
   */
	public void removeRegions() { 
		//The background label is zero.
		firstImageBorders = new int[rows][columns];
		firstPixelLabel = new int[rows][columns];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				firstImageBorders[i][j] = imageBorders[i][j];
				firstPixelLabel[i][j] = pixelLabel[i][j];
				int label = pixelLabel[i][j];
				//if (allRegions[label].getIsCharacter() || allRegions[label].getIsFrame() || allRegions[label].getIsDashedLine()) {
				if (allRegions[label].getIsCharacter() || allRegions[label].getIsFrame()) {
					imageBorders[i][j] = 0;
					pixelLabel[i][j] = 0;
				}
				if (isFrame) {
					if (i < frameUpperLeft.getRow() || i > frameLowerRight.getRow() || j < frameUpperLeft.getColumn() || j > frameLowerRight.getColumn()) {
						imageBorders[i][j] = 0;
						pixelLabel[i][j] = 0;
					}
				}
			}
		}
	}

  /**
   * Creates an object of the LineThinner class and 
	 * calls getThinLines method of LineThinner class that 
	 * finds out regions that are thick lines and thins the thick lines.
	 * pixelLabelThinned is the labelled image of the thinned image. 
	 * The borders are found again using pixelLabelThinned instead of pixelLabel this time.
	 * imageBorders array is updated. Information is recorded by the findBordersAndRecord method of the Border class into allPixel database.
	 *
   * @param none
   */
	public void thinThickLines() { 
		LineThinner aThinner = new LineThinner(allRegions, pixelLabel, imageBorders, blobCount, rows, columns, 0);  
		//0 is the label of the background
		
		aThinner.getCounts();
		//countsImage = aThinner.displayCounts("countList.txt");
		//save("Counts.pgm", countsImage); 

		aThinner.getThinLines();
		//int[][] thinImage = aThinner.getThinLineImage2();
		//save("ThinLines.pgm", thinImage);
		pixelLabelThinned = aThinner.makeLabelImage();	
		//save("ThinLabel.pgm", pixelLabelThinned);
		//pixelLabel = aThinner.getLabelImage();

		Border borderFinder = new Border(pixelLabelThinned, blobCount, rows, columns, bPixValue);
		//borderFinder.setLabelImage(pixelLabelThinned);
		borderFinder.findBordersAndRecord();
		imageBorders = borderFinder.getBorderImage();

		//pixelLabelThinned = borderFinder.getLabelImage();
		allPixels = borderFinder.getPixelDatabase();

		//int[][] afterThinning = borderFinder.makeThinnedBorderImage();
		//System.out.println("Got thinned. Saving.");
		//save("thinnedBorder.pgm", afterThinning);
		//System.out.println("Saved thinned border image in thinnedBorder.pgm");

		int[][] newBorderImage = getBorderImageToSave();
		int[][] newLabelImage = getThinLabelImageToSave();
		try {
			save("BorderedThin.pgm", newBorderImage);
			System.out.println("Saved thin border image to BorderedThin.pgm\n");
			save("LabelledThin.pgm", newLabelImage);
			System.out.println("Saved thin label image to LabelledThin.pgm\n");
		} catch(Exception ex) {System.out.println(ex.getMessage()+"\n");}
	}
	

  /**
	 * Creates an object of the Vectorizer class and 
	 * calls findSegments method of the Vectorizer class that 
	 * finds the line segments (primitives) defined by the border pixels.
	 * Necessary breaking and merging operations are done by findSegments method. 
	 * The array holding information about the <code>Region</code>s and 
	 * the database holding information about the pixels (the two 
	 * <code>HashMap</code>s are updated.
	 * <p>
	 * A pgm image of the chains is created: Chains.pgm.
	 * The chain information for all regions is written to a file: chainList.txt.
	 *
   * @param none
   */
	public void segmentBorders() { 
		Vectorizer aVectorizer = new Vectorizer(imageBorders, blobCount, rows, columns, 0, allRegions, allPixels);
		aVectorizer.findSegments();
		//borderChains = borderFinder.getBorderChains();
		int[][] chainImage = aVectorizer.makeChainImage();
		//System.out.println("Got chains. Saving.");
		save("Chains.pgm", chainImage);
		System.out.println("Saved chains in Chains.pgm");
		//allRegions = new Region[blobCount];
		allRegions = aVectorizer.getRegions();
		allPixels = aVectorizer.getPixelDatabase();
		try {
			//System.out.println("Saved chains in Chains.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}


  /**
   * Fits lines to all the primitives in all regions found by Vectorizer.
	 * Uses the least squares method. 
	 * The line information (orientation, slope, intercept and angle) is
	 * stored in the corresponding primitive.
	 * VirtualLines (discontinuous lines) are found and stored in a hashtable.
	 *
   * @param none 
   */
	public void fitLines() { 
		//System.out.println("In fitLines of BWImageG.java");
		Region aRegion;
		Collection chainList;
		Primitive aPrim;
		for (int i = 1; i < blobCount; i++) {
			aRegion = allRegions[i];
			chainList = aRegion.getPrimitiveList();
			Iterator itr = chainList.iterator();
			while (itr.hasNext()) {
				aPrim = (Primitive)itr.next();
				//System.out.println("\nRegion "+i+", primitive "+aPrim.getTagNo());
				aPrim.fitLine();
			}
		}
		//System.out.println("Fit lines to all primitives.");
		LineFinder aFinder = new LineFinder(rows, columns, blobCount, 0, imageBorders, allRegions);
		aFinder.findLines();
		allLines = aFinder.getAllLines();
		//horizontalLines = aFinder.getHorizontalLines();
		//verticalLines = aFinder.getVerticalLines();
		try {
			//System.out.println("Fit lines to all chains.\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}

	/**
   * Creates an object of the <code>AxesFinder</code> class and
	 * calls the <code>findAxes</code> method of the <code>AxesFinder<code> class that
	 * finds the coordinate axes (horizontal and vertical axes), if they exist. 
	 * Each axis is stored in an <code>Axis</code> object.
	 * A pgm image of the axes is created: Axes.pgm. 
	 * The image is all black if no axes are found.
	 *
   * @param none 
   */
	public void findCoordinateAxes(BWImageG passedImage) { 
		//System.out.println("In findCoordinateAxes of BWImageG.java");
		//AxesFinder anAxesFinder = new AxesFinder(allRegions, blobCount, rows, columns, allPixels);
		AxesFinder anAxesFinder = new AxesFinder(rows, columns, allRegions, allPixels, allLines);
		boolean isAxis = anAxesFinder.findAxes();
		if (isAxis) {
			try {
				System.out.println("AxesFinder found axes\n");
			} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
			//System.out.println("AxesFinder found axes");
		}
		else {
			try {
				System.out.println("AxesFinder did not find axes\n");
			} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
			//System.out.println("AxesFinder did not find axes");
		}
		hAxis = anAxesFinder.getHorizontalAxis();
		vAxis = anAxesFinder.getVerticalAxis();
		//System.out.println("Horizontal axis: "+hAxis);
		//System.out.println("Vertical axis: "+vAxis);
		int[][] axesImage = anAxesFinder.makeAxesImage(hAxis, vAxis);
		save("Axes.pgm", axesImage);
		//System.out.println("Saved axes in Axes.pgm\n");
		try {
			System.out.println("Horizontal axis: "+hAxis);
System.out.println("in BWImageG");
			System.out.println("Vertical axis: "+vAxis);
			System.out.println("Saved axes in Axes.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
		aGraph.addAxes(hAxis,passedImage);
		aGraph.addAxes(vAxis,passedImage);
	}

	/**
	 * If there is a horizontal axis and a vertical axis,
	 * findGridlines method of the GridlineFinder class is called
	 * to find the regions inside the axes area that are gridlines.
	 *
   * @param none 
	 * @return True if there are gridlines and false otherwise
   */
	public boolean findGridlines() {
		if (hAxis.getSize() > 0 && vAxis.getSize() > 0) {
			PointPixel origin = aGraph.getOrigin();
			GridlineFinder aFinder = new GridlineFinder(rows, columns, allLines, hAxis, vAxis, origin);
			aFinder.findGridlines();
			LinkedList allGridlines = aFinder.getGridlines();
			if (allGridlines.size() > 0) {
				aGraph.addGridlines(allGridlines);
				int[][] gridImage = aFinder.makeGridlineImage(allGridlines);
				//int[][] gridImage = getGridlineImageToSave(allGridlines);
				save("Gridlines.pgm", gridImage); 
				System.out.println("Saved gridline image to Gridlines.pgm");
				
				removeGridlines(allGridlines);
				//int[][] dataImage = getBorderImageToSave();
				//save("DataImage.pgm", dataImage);
				//System.out.println("Saved image without gridlines to DataImage.pgm");
				
				try {
					//System.out.println("There are "+(aFinder.getGridlines()).size()+" gridlines.\n");
					//System.out.println("Saved gridline image to Gridlines.pgm\n");
					//System.out.println("Saved image without gridlines to DataImage.pgm\n");
				} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
				return true;
			}
		}
		return false;
	}

  /**
   * Removes primitives that are part of a gridline from the image;
	 * from <code>pixelLabel</code>, <code>pixelLabelThinned</code> and 
	 * <code>imageBorders</code> arrays. 
	 *
   * @param lines The linked list of the gridlines (each gridline is a VirtualLine) 
   */
	public void removeGridlines(LinkedList lines) { 
		//The background label is zero.
		Point aPoint;
		Primitive aPrim;
		VirtualLine aline;
		LinkedList primList, points;
		ListIterator lItrp, lItr2;
		ListIterator lItr = lines.listIterator(0);
		while (lItr.hasNext()) {
			aline = (VirtualLine)lItr.next();
			primList = aline.getPrimitives();
			lItrp = primList.listIterator(0);
			while (lItrp.hasNext()) {
				aPrim = (Primitive)lItrp.next();
				//aPrim.setPartOfGridline(1);
				points = aPrim.getAllPoints();
				lItr2 = points.listIterator(0);
				while (lItr2.hasNext()) {
					aPoint = (Point)lItr2.next();
					imageBorders[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
					pixelLabel[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
					pixelLabelThinned[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
				}
			}
		}
	}	

	/**
   * Creates an object of the <code>RectangleFinder</code> class and
	 * calls the <code>findRectangles</code> method of the 
	 * <code>RectangleFinder</code> class that
   * finds the rectangles in the image.
	 * Each rectangle is stored in a <code>Rectangle</code> object.
	 * A pgm image of the rectangles is created: Rectangles.pgm. 
	 *
   * @param none 
   */
	public void findRectangles(BWImageG passedImage) { 
		System.out.println("In findRectangles of BWImageG.java");
		RectangleFinder aRectangleFinder = new RectangleFinder(allRegions, blobCount, rows, columns, allPixels);
//DLC
                System.out.println("got rectanglefinder");
		aRectangleFinder.findRectangles();
//DLC
                System.out.println("found rectangles");
		LinkedList allRectangles = aRectangleFinder.getRectangles();	
		int[][] rectangleImage = aRectangleFinder.makeRectangleImage(allRectangles);
		save("Rectangles.pgm", rectangleImage);
		System.out.println("Saved rectangles in Rectangles.pgm\n");
		
                // Don't use LegendFinder Class; legends handled in Graph.java
		//LegendFinder aLegendFinder = new LegendFinder(rows, columns, firstImageBorders, 0, blobCount, allRegions, allRectangles);
		//aLegendFinder.findLegend();
		//int[][] legendImage = aLegendFinder.makeLegendImage(allRectangles);
		//save("Legend.pgm", legendImage);
		//System.out.println("Saved legend in Legend.pgm\n");
		try {
			//System.out.println("There are "+allRectangles.size()+" rectangles.\n");
			//System.out.println("Saved rectangles in Rectangles.pgm\n");
			//System.out.println("Saved legend in Legend.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
		aGraph.addRectangles(allRectangles);
                aGraph.trueColorRectangles(passedImage);
	}


	/**
   * Creates an object of the <code>WedgeFinder</code> class and
	 * calls the <code>findWedges</code> method of the <code>WedgeFinder</code> class 
	 * that finds the wedges in the image.
	 * Each wedge is stored in a <code>Wedge</code> object.
	 * A pgm image of the wedges is created: Wedges.pgm.
	 *
   * @param none 
   */
	public void findWedges() { 
		//System.out.println("In findWedges of BWImageG.java");
		WedgeFinder aWedgeFinder = new WedgeFinder(allRegions, blobCount, rows, columns, allPixels, imageBorders);
		aWedgeFinder.findWedges();
		LinkedList allWedges = aWedgeFinder.getWedges();	
		//int[][] wedgeImage = aWedgeFinder.makeWedgeImage(allWedges);
		//save("Wedges.pgm", wedgeImage);
		//System.out.println("Saved wedges in Wedges.pgm\n");
		aGraph.addWedges(allWedges);
		try {
			//System.out.println("There are "+allWedges.size()+" wedges.\n");
			//System.out.println("Saved wedges in Wedges.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}

	/**
   * Creates an object of the <code>ConnectedLinesFinder</code> class and
	 * calls the <code>findConnectedLines</code> method of the 
	 * <code>ConnectedLinesFinder</code> class that
   * finds the connected lines in the image after finding the axes, 
	 * rectangles and the wedges.
	 *
   * @param none 
   */
	public void findConnectedLines() { 
		//System.out.println("In findConnectedLines of BWImageG.java");
		ConnectedLinesFinder aFinder = new ConnectedLinesFinder(allRegions, blobCount, rows, columns, allPixels);
		aFinder.findConnectedLines();
		LinkedList allConnectedLines = aFinder.getConnectedLines();	
		int[][] linesImage = aFinder.makeConnectedLinesImage(allConnectedLines);
		save("ConnectedLines.pgm", linesImage);
		System.out.println("Saved connected lines in ConnectedLines.pgm\n");
		aGraph.addConnectedLines(allConnectedLines);
		try {
			System.out.println("There are "+allConnectedLines.size()+" connected lines.\n");
			System.out.println("Saved connected lines in ConnectedLines.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
  aGraph.findChartType();  //putting this here is a kludge
	}

	/**
   * Displays the Graph object; the axes, rectangles, wedges and
	 * connected lines found so far.
	 * findChartType method of the Graph class determines the type of the chart; 
	 * bar, line or pie chart or not any of these three.
	 * getDataValues method of the Graph class determines the location of 
	 * the important data values depicted by the chart -if the image is
	 * classified as one of the above three chart types.
	 *
   * @param none 
   */
	public void displaySummary(String filename) { 
		aGraph.writeXML(filename, allRegions, firstPixelLabel);
		try {
			//System.out.println(aGraph+"\n");
			//System.out.println("Saved data image in Data.pgm\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}


	/**
   * Prints information regarding all the regions in the image.
	 *
   * @param none 
   */
	public void displayAllRegions() { 
		for (int i = 1; i < blobCount; i++) {
			//System.out.print(allRegions[i]);
		}
	}

 /**
	* Finds the bounding box of all the regions. 
	* Records two points -the left upper corner and the right lower corner of
	* each region.
	*
	* @param none
	*/
	private void findBoundingBoxes() {
	//Put this in a separate class: BoundingBoxFinder
	    //System.out.println("In findBoundingBoxes method 1.");
		int[] minRow = new int[blobCount];
		int[] minColumn = new int[blobCount];
		int[] maxRow = new int[blobCount];
		int[] maxColumn = new int[blobCount];
		int[] count = new int[blobCount];
		int labelNo;
		PointPixel aPoint;
		for (int i=0; i < blobCount; i++) {
			count[i] = 0;
		}
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				labelNo = pixelLabel[i][j];
				if (labelNo > 0) {
					aPoint = new PointPixel(i, j);
					allRegions[labelNo].addPixelToRegion(aPoint);
					if (count[labelNo] == 0) { //this is the first point encountered
						minRow[labelNo] = i;
						minColumn[labelNo] = j;
						maxRow[labelNo] = i;
						maxColumn[labelNo] = j;
					}
					if (i > maxRow[labelNo]) maxRow[labelNo] = i;
					if (i < minRow[labelNo]) minRow[labelNo] = i;
					if (j > maxColumn[labelNo]) maxColumn[labelNo] = j;
					if (j < minColumn[labelNo]) minColumn[labelNo] = j;
					count[labelNo]++;
				}
			}
		}
	//    System.out.println("Found min/max row/columns.");
		PointPixel upperLeft;
		PointPixel lowerRight;
		for (int i = 2; i < blobCount*2; i+=2) {
			upperLeft = new PointPixel(minRow[(int)(i/2)], minColumn[(int)(i/2)]);
			lowerRight = new PointPixel(maxRow[(int)(i/2)], maxColumn[(int)(i/2)]);
			//boundingBoxCorners[i] = upperLeft;
			//boundingBoxCorners[i+1] = lowerRight;
			allRegions[(int)(i/2)].setUpperLeft(upperLeft);
			allRegions[(int)(i/2)].setLowerRight(lowerRight);
		}
		//System.out.println("\nFound bounding boxes.");
		try {
			//System.out.println("Found bounding boxes.\n");
		} catch (Exception e) {System.out.println(e.getMessage()+"\n");}
	}

  /**
	 * Adds one row/column on each side of the input image.
	 * The number of rows increase by two, the number of columns increase by two. 
	 *
   * @param none
   */
	public void addImage() {
		rows = rows + 2;
		columns = columns + 2;
		int[][] newImage = new int[rows][columns];
		for (int j = 0; j < columns; j++) {
			newImage[0][j] = bPixValue;
			newImage[rows-1][j] = bPixValue;
		}
		for (int i = 0; i < rows; i++) {
			newImage[i][0] = bPixValue;
			newImage[i][columns-1] = bPixValue;
		}
		for (int i = 1; i < rows-1; i++) {
			for (int j = 1; j < columns-1; j++) {
				newImage[i][j] = image[i-1][j-1];
			}
		}
		image = newImage;
	}

  /**
   * Returns the 2d array of image labels.
	 *
   * @param none 
	 * @return The 2d array of image labels.
   */
	public int[][] getRegionLabels() {
		return pixelLabel;
	}

  /**
   * Returns the 2d array of the image counts
	 * found in the thinThickLines method.
	 *
   * @param none 
	 * @return The 2d array of image counts.
   */
  public int[][] getCountsImageToSave() {
		return countsImage;
	}

  /**
   * Processes and returns the 2d array of image labels
	 *
   * @param none 
   */
  public void appendOther(String filename) {
		try {
			FileOutputStream appendedFile = new FileOutputStream(filename, true);
			BufferedOutputStream ostream = new BufferedOutputStream(appendedFile);
			for (int i = 1; i < blobCount; i++) {
				Region aRegion = allRegions[i];
				if (aRegion.getIsCharacter()) {
					ostream.write(("Character "+aRegion.getUpperLeft()+" "+aRegion.getLowerRight()+"\n").getBytes());
				}
				if (aRegion.getIsDashedLine()) {
					ostream.write(("DashedLine"+aRegion.getUpperLeft()+" "+aRegion.getLowerRight()+"\n").getBytes());
				}
				if (aRegion.getIsGridline()) {
					ostream.write(("Gridline "+aRegion.getUpperLeft()+" "+aRegion.getLowerRight()+"\n").getBytes());
				}
			}
			ostream.close();
		}
		catch (Exception e) {System.out.println(e.getMessage());}
	}

  /**
   * Processes and returns the 2d array of image labels
	 *
	 * @param none
   */
  public int [][] getLabelImageToSave() {
		int[][] labelImage = new int[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				labelImage[i][j] = ((int)255/blobCount)*pixelLabel[i][j];
				if (pixelLabel[i][j] == 0)
					labelImage[i][j] = 255;
				else if (pixelLabel[i][j] == blobCount - 1)
					labelImage[i][j] = 0;
			}
		}
		return labelImage;
	}

  /**
   * Processes and returns the 2d array of thinned image labels
	 *
   * @param none 
   */
	public int [][] getThinLabelImageToSave() {
		int[][] labelImage = new int[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				labelImage[i][j] = ((int)255/blobCount)*pixelLabelThinned[i][j];
				if (pixelLabelThinned[i][j] == 0)
					labelImage[i][j] = 255;
				else if (pixelLabelThinned[i][j] == blobCount - 1)
					labelImage[i][j] = 0;
			}
		}
		return labelImage;
	}

	
  /**
   * Processes and returns the 2d array of image borders 
	 *
   * @param none
   */
	public int[][] getBorderImageToSave() {
		//The background label is zero.
		int[][] bImage = new int[rows][columns];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				if (imageBorders[i][j] == 0) {
					bImage[i][j] = 255;
				}
				else {
					bImage[i][j] = 0;
				}
			}
		}
		return bImage;
	}


  /**
   * Returns the 2d array of the 'label' region of the image. 
	 *
   * @param label The label of the region to be output
	 */
	public int[][] getOneRegionImageToSave(int label) {
		//The background label is zero.
		int[][] bImage = new int[rows][columns];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				bImage[i][j] = 255;
				if (firstPixelLabel[i][j] == label) {
					bImage[i][j] = 0;
				}
			}
		}
		return bImage;
	}

  /**
   * Returns the 2d array of one line image. 
	 *
   * @param regionNo The number of the region that the primitive is in
	 * @param primNo The number of the primitive in that region that is
	 * to be output
	 */
	public int[][] getOnePrimImageToSave(int regionNo, int primNo) {
		Primitive aPrim = allRegions[regionNo].getPrimitive(primNo);
		int[][] bImage = new int[rows][columns];
		for (int i=0; i < rows; i++) {
			for (int j=0; j < columns; j++) {
				bImage[i][j] = 255;
			}
		}
		Point aPoint;
		LinkedList primPoints = aPrim.getAllPoints();
		ListIterator lItr = primPoints.listIterator();
		while (lItr.hasNext()) {
			aPoint = (Point)lItr.next();
			bImage[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
		}
		return bImage;
	}

  /**
   * Returns the 2d array of one line image. 
	 *
   * @param rectNo The number of the rectangle to be output
	 */
	public int[][] getOneRectangleImageToSave(int rectNo) {
		LinkedList allRectangles = aGraph.getRectangles();
		Rectangle aRect = (Rectangle)allRectangles.get(rectNo);
		int[][] bImage = new int[rows][columns];
		for (int i=0; i < rows; i++) {
			for (int j=0; j < columns; j++) {
				bImage[i][j] = 255;
			}
		}
		Point aPoint;
		LinkedList primPoints;
		ListIterator lItr2;
		Primitive aPrim;
		LinkedList sides = aRect.getSides();
		ListIterator lItr = sides.listIterator();
		while (lItr.hasNext()) {
			aPrim = (Primitive)lItr.next();
			primPoints = aPrim.getAllPoints();
			lItr2 = primPoints.listIterator();
			while (lItr2.hasNext()) {
				aPoint = (Point)lItr2.next();
				bImage[(int)aPoint.getX()][(int)aPoint.getY()] = 0;
			}
		}
		//System.out.println("Rectangle "+aRect);
		//System.out.println("Rectangle corners:\n");
		//System.out.print("Rectangle "+aRect);
		//System.out.println("Rectangle corners:");
		PointPixel aCorner;
		LinkedList corners = aRect.getCorners();
		lItr = corners.listIterator(0);
		while (lItr.hasNext()) {
			aCorner = (PointPixel)lItr.next();
			//System.out.println(aCorner+"\n");
			//System.out.println(aCorner);
		}
		return bImage;
	}


  /**
   * Returns the 2d array of one line image. 
	 *
   * @param angle The angle of the line with respect to the positive row axis 
	 * (vertical axis in the -y direction) 
	 * @param dist The distance of the line from the origin
	 */
	public int[][] getOneLineImageToSave(double angle, int dist) {
		LineFinder aFinder = new LineFinder(rows, columns, allLines);
		int[][] bImage = aFinder.makeLineImage(angle, dist);
		return bImage;
	}


  /**
   * Processes and returns the 2d array of the character regions of the image. 
	 *
   * @param inImage The 2d array of the image whose characters are to be output
	 */
	public int[][] getTextLabelImage(int[][] inImage) {
		int label;
		int[][] bImage = new int[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				label = inImage[i][j];
				//bImage[i][j] = 255;
				bImage[i][j] = 0;
				if ((allRegions[label]).getIsCharacter()) {
					bImage[i][j] = label;
				}
			}
		}
		return bImage;
	}


  /**
   * Processes and returns the 2d array of the character regions of the image. 
	 *
   * @param inImage The 2d array of the image whose characters are to be output
	 */
	public int[][] getTextImageToSave(int[][] inImage) {
		int label;
		int[][] bImage = new int[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				label = inImage[i][j];
				bImage[i][j] = 255;
				if ((allRegions[label]).getIsCharacter()) {
					bImage[i][j] = 0;
				}
			}
		}
		return bImage;
	}


  /**
   * Processes and returns the 2d array of the dashed line regions of the image. 
	 *
   * @param none 
   */
	public int[][] getDashedLineImageToSave() {
		int[][] bImage = new int[rows][columns];
		for (int i = 0; i < rows; i++) {
			for (int j = 0; j < columns; j++) {
				int label = pixelLabel[i][j];
				bImage[i][j] = 255;
				if ((allRegions[label]).getIsDashedLine()) {
					bImage[i][j] = 0;
					//bImage[i][j] = ((int)255/blobCount)*label;
				}
			}
		}
		return bImage;
	}


  /**
   * Processes and returns the 2d array of the gridline regions of the image. 
	 *
   * @param lines The linked list of gridlines to be output
	 */
	public int[][] getGridlineImageToSave(LinkedList lines) {
		GridlineFinder aFinder = new GridlineFinder(rows, columns);
		int[][] bImage = aFinder.makeGridlineImage(lines);
		return bImage;
	}


  /**
   * Processes and returns the 2d array of the given image
	 *
   * @param data The 2d array that shows the data in the image
   */
	public int[][] getDataImageToSave(int[][] data) {
		int[][] bImage = new int[rows][columns];
		for (int i=0; i<rows; i++) {
			for (int j=0; j<columns; j++) {
				bImage[i][j] = 255;
				if (image[i][j] != bPixValue) {
					bImage[i][j] = 200;
				}
				if (data[i][j] == 0) {
					bImage[i][j] = 0;
				}
			}
		}
		return bImage;
	}


  /**
   * Returns the number of rows of the image
	 *
   * @param none
	 * @return Number of rows
   */
	public int getImageHeight( ) {
		return rows;
	}

  /**
   * Returns the number of columns of the image
	 *   
   * @param none
	 * @return Number of columns 
   */
	public int getImageWidth( ) {
		return columns;
	}

  /**
   * Returns the number of labels (blobs) in the image
	 *
   * @param none
	 * @return Number of labels (blobs, or objects)
	 */
	public int getBlobCount() {
		return blobCount;
	}


  /**
   * Returns the number of rectangles in the image
	 *
   * @param none
	 */
	public int getNoOfRectangles() {
		return aGraph.getNoOfRectangles();
	}

  /**
   * Returns the Graph object of the image 
	 *
   * @param none
   */
	public Graph getGraph() {
		return aGraph;
	}

/*
*  creates virtual ticks to go on a virtual measurement axis
*/

    public LinkedList getVirtualTicks(PointPixel beginPt, PointPixel endPt,
                                      Hashtable stuff) {
      int i,br,bc,er,ec;
      br = beginPt.getRow();
      bc = beginPt.getColumn();
      er = endPt.getRow();
      ec = endPt.getColumn();
//System.out.println("br bc er ec = " + br + " " + bc + " " + er + " " + ec);
      LinkedList virtualTicks = new LinkedList();
      Iterator itrW = words.values().iterator();
      Word aWord;
      if (br == er) { // horizontal axis
        for (i=bc;i < ec;i++) 
          if (image[br][i] != bPixValue) 
  {
//System.out.println("image[br][i] = " + image[br][i]);
            virtualTicks.add(new Primitive(new PointPixel(br,i),0,0));}
  }
      else { // vertical axis
        if (bc > 0) {
          for (i=br;i<er;i++)
            if (image[i][bc] != bPixValue)
              virtualTicks.add(new Primitive(new PointPixel(i,bc),0,0));
//System.out.println("vticksize = " + virtualTicks.size());
          if (virtualTicks.size() < 2
              || virtualTicks.size() > (er - br) / 5) { //look for numbers 
            virtualTicks = new LinkedList();
            while (itrW.hasNext()) {
              aWord = (Word)itrW.next();
//System.out.println(aWord);
//System.out.println(aWord.getInTextPiece());
//System.out.println(aWord.getInTextPiece().getInTextBlock());
              if (aWord.justLeftOf(bc) &&
                    aWord.getInTextPiece().getInTextBlock().isPossibleTickLabel()) {
                int row = (aWord.getUpperLeft().getRow() +
                           aWord.getLowerRight().getRow())/2;
                if (row < br) br = row;
                virtualTicks.add(new Primitive(new PointPixel(row,bc),0,0));}}
            }
          beginPt.setRow(br);}}
//System.out.println("now vticksize = " + virtualTicks.size());
      return virtualTicks;
    }

/*
* gets the value of a pixel in the image
*/
    public int getImageValue(int row, int col) {
      return image[row][col];}

/*
*  changes blobs that are 2x2 pixels or less and that are
*  not black  to background color
*/

    public void blankNoise() {
        int sthresh = 4;
        for (int i = 1; i < blobCount; i++) {
            int label;
            Region rgn;
            rgn = allRegions[i];
            int clr;
            clr = rgn.getColor();
            int h,w,lbl;
            h = rgn.getBoundingBoxHeight();
            w = rgn.getBoundingBoxWidth();
            lbl = rgn.getRegion();
            if (h < sthresh && w < sthresh && clr > 20) { //found a noise spot
               PointPixel ul = rgn.getUpperLeft();
               PointPixel lr = rgn.getLowerRight();
               for (int r = ul.getRow();r<=lr.getRow();r++)
                 for (int c = ul.getColumn();c<=lr.getColumn();c++) {
                     if (pixelLabel[r][c] == lbl) image[r][c] = bPixValue;}}
        }}

  public void findBars() {
//System.out.println("in findBars\n");
    aGraph.findBars2();
//System.out.println("finished call to findBars2\n");
    aGraph.findChartType();
//System.out.println("finished call to findChartType\n");
    aGraph.getDataValues();}

  public int getClearColumn(int firstRow, int lastRow, int column) {
    int r,c;
    for (c = column - 1; c >= 0;c--) {
      for (r = firstRow; r<= lastRow; r++) {
         if (image[r][c] != bPixValue) break;}
      if (r > lastRow) return c;}
    return 0;}



}

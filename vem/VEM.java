import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.filechooser.*;
import java.lang.*;
import java.awt.image.*;
import javax.imageio.*;
import java.net.*;

/**
 * The main program for the chart reading project.
 * It calls methods of the <code>BWImageG</code> class.
 * 
 */

//SOmething here 

public class VEM {


	protected String getClassName(Object o) {
		String classString = o.getClass().getName();
		int dotIndex = classString.lastIndexOf(".");
		return classString.substring(dotIndex+1);
	}

	public static void main(String args[]) throws Exception { 
		VEM app = new VEM();
        BWImageG image;
        URL u = new URL(args[0]);
        BufferedImage jImage = ImageIO.read(u);
		image = new BWImageG();
        image.loadJavaImage(jImage);
		image.filterImage();
		int numImageRows = image.getImageHeight();
		int numImageColumns = image.getImageWidth();
                image.labelImage();
                //System.out.println("Labelled.\n\n");
                image.findBorders();
                //System.out.println("Bordered.\n\n");
                image.findCharacters();
                //System.out.println("Found characters.\n\n");
                image.thinThickLines();
                //System.out.println("Thinned.\n\n");
                image.segmentBorders();
                //System.out.println("Found chains.\n\n");
                image.fitLines();
                //System.out.println("Fit lines.\n\n");
                image.findRectangles(image);
                //System.out.println("Found rectangles.\n\n");
                image.findBars();
                //System.out.println("Found bars");
                simpleOCR.loadFonts();
                image.findWords();
                image.findCoordinateAxes(image);
                //System.out.println("Found axes.\n\n");
                image.findWedges();
                //System.out.println("Found wedges.\n\n");
                image.findConnectedLines();
                //System.out.println("Found connected line.\n\n");
                int i = args[0].lastIndexOf('/');
                String filename = args[0].substring(i);
                i = filename.lastIndexOf('.');
                filename = filename.substring(1,i) + "-vision.xml";
                //System.out.println(filename);
                image.displaySummary(filename);
                System.out.println("Displayed summary.\n\n");
		System.exit(0);
	}	


}


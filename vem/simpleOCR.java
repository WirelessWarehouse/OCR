/* simple form of ocr.  Uses the file "chars/fontkey" to get images of font
* characters and their corresponding letter names, does direct match
* with arrays containing the characters in chart.
*
* Assumes that each line in file "chars/fontkey" has the form
* <character image file name> <character><cr>
*/


import java.io.*;
import java.util.*;
import java.lang.reflect.*;

class simpleOCR {
    static int[][][] TB21fontTable ;
    static String[] TB21charTable ; 
    static int[][][] TR21fontTable ;
    static String[] TR21charTable ; 
    static int[][][] TB17fontTable ;
    static String[] TB17charTable ; 
    static int[][][] TR17fontTable ;
    static String[] TR17charTable ; 
    static int[][][] TR14fontTable ;
    static String[] TR14charTable ; 
    static int[][][] TR12fontTable ;
    static String[] TR12charTable ; 
    static int[][][] TB12fontTable ;
    static String[] TB12charTable ; 
    static int[][][] TR10fontTable ;
    static String[] TR10charTable ; 
    static int[][][] TB10fontTable ;
    static String[] TB10charTable ; 
    static int[][][] CB10fontTable ;
    static String[] CB10charTable ; 
    static int[][][] CO10fontTable ;
    static String[] CO10charTable ; 
    static int[][][] CB12fontTable ;
    static String[] CB12charTable ; 
    static int[][][] CO12fontTable ;
    static String[] CO12charTable ; 
    static int[][][] CB17fontTable ;
    static String[] CB17charTable ; 
    static int[][][] CO17fontTable ;
    static String[] CO17charTable ; 
    static int[][][] CB21fontTable ;
    static String[] CB21charTable ; 
    static int[][][] CO21fontTable ;
    static String[] CO21charTable ; 

    public static void loadFonts() {
      BufferedReader x;
      String s;
      int i,j,k,n,rows,cols;
      try {
            x = new BufferedReader(new FileReader("CB21fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CB21fontTable = new int[n][][];
            CB21charTable = new String[n];
            loadOneFont(CB21fontTable, CB21charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("CO21fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CO21fontTable = new int[n][][];
            CO21charTable = new String[n];
            loadOneFont(CO21fontTable, CO21charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("CB17fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CB17fontTable = new int[n][][];
            CB17charTable = new String[n];
            loadOneFont(CB17fontTable, CB17charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("CO17fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CO17fontTable = new int[n][][];
            CO17charTable = new String[n];
            loadOneFont(CO17fontTable, CO17charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("CB12fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CB12fontTable = new int[n][][];
            CB12charTable = new String[n];
            loadOneFont(CB12fontTable, CB12charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("CO12fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CO12fontTable = new int[n][][];
            CO12charTable = new String[n];
            loadOneFont(CO12fontTable, CO12charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("CB10fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CB10fontTable = new int[n][][];
            CB10charTable = new String[n];
            loadOneFont(CB10fontTable, CB10charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("CO10fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            CO10fontTable = new int[n][][];
            CO10charTable = new String[n];
            loadOneFont(CO10fontTable, CO10charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 

/*  old Times Roman files */
      try {
            x = new BufferedReader(new FileReader("TB10fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TB10fontTable = new int[n][][];
            TB10charTable = new String[n];
            loadOneFont(TB10fontTable, TB10charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("TR10fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TR10fontTable = new int[n][][];
            TR10charTable = new String[n];
            loadOneFont(TR10fontTable, TR10charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("TB12fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TB12fontTable = new int[n][][];
            TB12charTable = new String[n];
            loadOneFont(TB12fontTable, TB12charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("TR12fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TR12fontTable = new int[n][][];
            TR12charTable = new String[n];
            loadOneFont(TR12fontTable, TR12charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("TB21fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TB21fontTable = new int[n][][];
            TB21charTable = new String[n];
            loadOneFont(TB21fontTable, TB21charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("TR21fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TR21fontTable = new int[n][][];
            TR21charTable = new String[n];
            loadOneFont(TR21fontTable, TR21charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("TB17fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TB17fontTable = new int[n][][];
            TB17charTable = new String[n];
            loadOneFont(TB17fontTable, TB17charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
      try {
            x = new BufferedReader(new FileReader("TR17fontTable"));
            s = x.readLine().trim();
            n = Integer.parseInt(s);
            TR17fontTable = new int[n][][];
            TR17charTable = new String[n];
            loadOneFont(TR17fontTable, TR17charTable, x);}
      catch(Exception e) {System.out.println(e.getMessage());} 
   /* */
}

    public static void loadOneFont(
        int[][][] fontTable,
        String[] charTable,
        BufferedReader x) {
      String s;
      int i,j,k,n,rows,cols;
      try {
            n = Array.getLength(charTable); 
            for (i = 0; i < n; i++) {
              charTable[i] = x.readLine().trim();
              rows = Integer.parseInt(x.readLine().trim());
              cols = Integer.parseInt(x.readLine().trim());
              fontTable[i] = new int[rows][cols];
              for (j = 0; j < rows; j++) {
                s = x.readLine().trim();
                for (k = 0;k < cols; k++) {
                  if (s.charAt(k) == '0')
                    fontTable[i][j][k] = 0;
                  else
                    fontTable[i][j][k] = 255;}}}}
        catch(Exception e) {System.out.println(e.getMessage());} 
}

    // Arrays.equals doesn't work for 2D arrays
    // a1 is the image being analyzed
    // a2 is the character template being matched against

    static boolean matches(int[][] a1,
                           int[][] a2,
                           int rowOffset,
                           int colOffset) {
	int i,j;
	int h1,h2;
        int w1,w2;
	h1 = Array.getLength(a1);
	h2 = Array.getLength(a2);
	if (h2 + rowOffset > h1) return false;
        w1 = Array.getLength(a1[0]);
        w2 = Array.getLength(a2[0]);
        if (w2 + colOffset > w1) return false;
        for (i=0;i < rowOffset;i++)
          for (j=0;j < w2;j++)
            if (a1[i][j + colOffset] == 0) return false;
        for (i=h2 + rowOffset;i < h1;i++)
          for (j=0;j < w2;j++)
            if (a1[i][j + colOffset] == 0) return false;
	for (i=0;i < h2;i++)
          for (j=0;j < w2;j++)
            if (a1[i + rowOffset][j + colOffset] != a2[i][j]) return false;
        if (h2 <= 3 && w2 <= 3 && colOffset + w2 < w1) {
//System.out.println("h2 w2 rowOffset " + h2 + " " + w2 + " " + rowOffset);
//System.out.println("colOffset " + colOffset);
          for (i = rowOffset;i <= rowOffset + h2;i++)
            if (i >= 0 && i < h1 && a1[i][colOffset + w2] == 0) return false;}
	return true;}

    public static String decideChar(int[][] im) {
        String result;
        // test for triangles
        if ((result = findTriangle(im)) != "") return "tr10" + result;
/* old Times Roman fonts */
        if ((result=decideCharN(im,TB21fontTable,TB21charTable,0)) != "")
          return "TB21" + result;
        if ((result=decideCharN(im,TR21fontTable,TR21charTable,0)) != "")
          return "TR21" + result;
        if ((result=decideCharN(im,TB17fontTable,TB17charTable,0)) != "")
          return "TB17" + result;
        if ((result=decideCharN(im,TR17fontTable,TR17charTable,0)) != "")
          return "TR17" + result;
        if ((result=decideCharN(im,TB12fontTable,TB12charTable,0)) != "")
          return "TB12" + result;
        if ((result=decideCharN(im,TR12fontTable,TR12charTable,0)) != "")
          return "TR12" + result;
        if ((result=decideCharN(im,TB10fontTable,TB10charTable,0)) != "")
          return "TB10" + result;
        if ((result=decideCharN(im,TR10fontTable,TR10charTable,0)) != "")
          return "TR10" + result;
  /* */
        if ((result=decideCharN(im,CB21fontTable,CB21charTable,0)) != "")
          return "CB21" + result;
        if ((result=decideCharN(im,CO21fontTable,CO21charTable,0)) != "")
          return "CO21" + result;
        if ((result=decideCharN(im,CB17fontTable,CB17charTable,0)) != "")
          return "CB17" + result;
        if ((result=decideCharN(im,CO17fontTable,CO17charTable,0)) != "")
          return "CO17" + result;
        if ((result=decideCharN(im,CB12fontTable,CB12charTable,0)) != "")
          return "CB12" + result;
        if ((result=decideCharN(im,CO12fontTable,CO12charTable,0)) != "")
          return "CO12" + result;
        if ((result=decideCharN(im,CB10fontTable,CB10charTable,0)) != "")
          return "CB10" + result;
        if ((result=decideCharN(im,CO10fontTable,CO10charTable,0)) != "")
          return "CO10" + result;
        else return "";}

    public static String decideCharN(int[][] im,
                                    int[][][] fontTable,
                                    String[] charTable,
                                    int colOffset) {
	int tableIndex;
	int i;
        String s;
        int rowOffset;
        tableIndex = Array.getLength(charTable);
	for (i=0;i<tableIndex;i++) {
	//for (i=tableIndex - 1;i >= 0;i--) {
//System.out.println("i = " + i);
//System.out.println(i + "  " + charTable[i]);
//System.out.println(Array.getLength(im));
//System.out.println(Array.getLength(fontTable[i]));
          rowOffset = Array.getLength(im) - Array.getLength(fontTable[i]);
          for (int j = 0;j <= rowOffset;j++) {
            s = decideCharNi(im,fontTable,charTable,i,j,colOffset);
            if (s != "") return s;}} 
	return "";}

    public static String decideCharNi(int[][] im,
                                      int[][][] fontTable,
                                      String[] charTable,
                                      int i,
                                      int rowOffset,
                                      int colOffset) {
        String s1,s2;
	if (matches(im,fontTable[i],rowOffset,colOffset)) {
          s1 = charTable[i];
//System.out.println("matched " + s1);
          int w = colOffset + Array.getLength(fontTable[i][0]);
          if (w == Array.getLength(im[0])) return s1;
          s2 = decideCharN(im, fontTable, charTable, w);
          if (s2 != "") return s1 + s2;}
        return "";}

/*
    static void loadFont() {
        try {
	    BufferedReader x = new BufferedReader(new FileReader("chars/fontkey"));
	    String s;
	    String fn;
	    while (x.ready() && tableIndex < 512) {
		s = x.readLine().trim();
                fn = "chars/" + s.substring(0,s.indexOf(' '));
		charTable[tableIndex] = s.substring(s.indexOf(' ')).trim();
		fontTable[tableIndex] = load(fn);
		tableIndex++;} }
        catch(Exception e) {System.out.println(e.getMessage());} }
*/

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

  // reads in image from .pgm file
  static int[][] load(String infilename) {
    try {
      BufferedInputStream x =
        new BufferedInputStream(new FileInputStream(infilename));
      if (x.read() != 'P' || x.read() != '5') {
        System.out.println("not a raw pgm file");
        return null;
      }
      int nc = oneNum(x);  // number of columns
      int nr = oneNum(x);  // numer of rows
      int throw_away = oneNum(x);
      int [][] I = new int[nr][nc];
      int r,c;
      for (r=0;r<nr;r++)
        for(c=0;c<nc;c++)
          I[r][c] = x.read();
      x.close();
      return I;
    }
    catch(Exception e) {System.out.println(e.getMessage() + " not found");}
    return null; }

    private static String findTriangle(int[][] img) {
        int r,c,height,width,count;
        height = Array.getLength(img);
        width = Array.getLength(img[0]);
        if (height < 6 || width < 6) {
            return "";
        }
        count = 0;
        for (c = 0; c < width; c++) {
            for (r = 0; r < height && img[r][c] != 0; r++); // skip whitespace
            count = 0;
            for (; r < height && img[r][c] == 0; r++) {
                count++;
            }
            for (; r < height && img[r][c] != 0; r++); // skip on other side
            if (r < height
                || count < height * (double) (width - c - 1) / width
                || count > height * (double) (width - c + 1) / width) {
                break;
            }
        }
        if (c == width && count < 5 && 2 * count < height) {
            return "[>]"; // found triangle pointing right
        }
        for (r = height - 1; r>= 0; r--) {
            for (c = 0; c < width && img[r][c] != 0; c++); // skip whitespace
            count = 0;
            for (; c < width && img[r][c] == 0; c++) {
                count++;
            }
            for (; c < width && img[r][c] != 0; c++); // skip on other side
            if (c < width
                || count < width * (double)r / height
                || count > width * (double)(r + 2)/height) {
                return ""; // not triangle shape
            }
        }
        if (count < 5 && 2 * count < width) {
            return "[^]"; // found triangle pointing up
        }
        return "";  // triangle not pointy enough
    }

}

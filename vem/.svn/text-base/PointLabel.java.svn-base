import java.util.*;

/**
 * A class for the point annotations appearing in line graphs.
 *
 * @author Daniel Chester
 * @version 1.0
 */

public class PointLabel {
    final static int UP = 0;
    final static int DOWN = 1;
    final static int LEFT = 2;
    final static int RIGHT = 3;
    private PointPixel upperLeft;
    private PointPixel lowerRight;
    private Rectangle rectangle;
    private int orientation;
    private int arrowColumn;
    private int arrowRow;
    private LinkedList annotationWords;
    private LinkedList annotationBlocks;
    private Region region;

    /**
     * Constructor.
     *
     * @param box The box containing the annotation
     * @param rgn The region containing both the box and attached arrow
     * @param inputLabelImage The image array with all the regions labelled
     */
    public PointLabel(Rectangle box, Region rgn, int[][] inputLabelImage) {
        PointPixel boxUL,boxLR, rgnUL, rgnLR;
        rectangle = box;
        region = rgn;
        rgnUL = rgn.getUpperLeft();
        rgnLR = rgn.getLowerRight();
        upperLeft = rgnUL;
        lowerRight = rgnLR;
        boxUL = box.getUpperLeft();
        boxLR = box.getLowerRight();
        int yb = rgnUL.getRow();
        int ye = rgnLR.getRow();
        int xb = rgnUL.getColumn();
        int xe = rgnLR.getColumn ();
        int x, y;
        if (boxUL.getRow() > yb)
            orientation = UP;
        else if (boxLR.getColumn() < xe)
            orientation = RIGHT;
        else if (boxLR.getRow() < ye)
            orientation = DOWN;
        else orientation = LEFT;
        annotationWords = new LinkedList();
        annotationBlocks = new LinkedList();

        /* now find the column or row the arrow shaft is aligned in */
        int label = rgn.getRegion();
//System.out.println("label= " + label);
//System.out.println("orientation= "+ orientation + "  " + DOWN);
        arrowColumn = -1;
        arrowRow = -1;
        if (orientation == UP) {
            for (x=xb;x<=xe;x++)
                if (inputLabelImage[yb][x]==label) {
                   arrowColumn = x;
                   break;}}
        else if (orientation == RIGHT){
            for (y=yb;y<=ye;y++)
                if (inputLabelImage[y][xe]==label) {
                   arrowRow = y;
                   break;}}
        else if (orientation == DOWN)
{System.out.println("orientation == DOWN");
            for (x=xb;x<=xe;x++)
                 {System.out.println("see " + inputLabelImage[ye][x]);
                if (inputLabelImage[ye][x]==label) {
                   arrowColumn = x;
                   break;}
                 }
}
        else for (y=yb;y<=ye;y++)
                if (inputLabelImage[y][xb]==label) {
                   arrowRow = y;
                   break;}}

    /**
     * Returns the rectangle associated with this PointLabel.
     *
     * @param none
     */

    public Rectangle getRectangle() {return rectangle;}

    /**
     * Sets the annotation words in this PointLabel.
     *
     * @param w - a word to be added to the annotationWords list
     */

    public void addToAnnotationWords(Word w) {annotationWords.add(w);}

    /**
     * Returns the annotationWords list.
     *
     * @param none
     */

    public LinkedList getAnnotationWords() {return annotationWords;}

    public void addToAnnotationBlocks(TextBlock tb) {annotationBlocks.add(tb);}
    public LinkedList getAnnotationBlocks() {return annotationBlocks;}
    public int getArrowColumn() { return arrowColumn;}
    public int getArrowRow() { return arrowRow;}
    public PointPixel getUpperLeft() { return upperLeft;}
    public PointPixel getLowerRight() {return lowerRight;}
}


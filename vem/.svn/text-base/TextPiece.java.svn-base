import java.util.*;


/*
* This class holds a sequence of words that appear to be one line of text.
* The words in a TextPiece have the same orientation and font and are close to
* each other in the direction of orientation.
*/

public class TextPiece {

  private LinkedList Words;
  private int orientation;
  private String font;
  private String text;
  private PointPixel upperLeft;
  private PointPixel lowerRight;
  private boolean inABlock = false;
  private TextBlock inTextBlock = null;

  public TextPiece(LinkedList wordList) {
    Words = wordList;
    font = ((Word)wordList.getFirst()).getFont();
    int rb, re, cb, ce;
    upperLeft = ((Word)wordList.getFirst()).getUpperLeft();
    lowerRight = ((Word)wordList.getFirst()).getLowerRight();
    rb = upperLeft.getRow();
    re = lowerRight.getRow();
    cb = upperLeft.getColumn();
    ce = lowerRight.getColumn();
    ListIterator pItr = wordList.listIterator();
    while (pItr.hasNext()) {
      Word aWord = (Word)pItr.next();
      upperLeft = aWord.getUpperLeft();
      lowerRight = aWord.getLowerRight();
      if (upperLeft.getRow() < rb) rb = upperLeft.getRow();
      if (lowerRight.getRow() > re) re = lowerRight.getRow();
      if (upperLeft.getColumn() < cb) cb = upperLeft.getColumn();
      if (lowerRight.getColumn() > ce) ce = lowerRight.getColumn();}
    upperLeft = new PointPixel(rb,cb);
    lowerRight = new PointPixel(re,ce);

    //orientation = ((Word)wordList.getFirst()).getOrientation();
    //if (orientation == Primitive.HORIZONTAL) {
      //upperLeft = ((Word)wordList.getFirst()).getUpperLeft();
      //lowerRight = ((Word)wordList.getLast()).getLowerRight();}
    //else {
      //upperLeft = ((Word)wordList.getLast()).getUpperLeft();
      //lowerRight = ((Word)wordList.getFirst()).getLowerRight();}
    text = new String();
    Word previousWord = (Word)wordList.getFirst(); // dummy value
    ListIterator lItr = wordList.listIterator();
    while (lItr.hasNext()) {
      Word aWord = (Word)lItr.next();
      aWord.setInTextPiece(this);
System.out.println("just set word " + aWord.getText() + " to this");
      if (text.equals("")) text = aWord.getText();
      else {

      // MERGE WORDS TOGETHER AS ONE WORD WHEN THEY ARE CLOSE ENOUGH
      // Thin chars (e.g., ".", "'") must be allowed more space to
      // next or previous char than is allowed for other chars.
System.out.println(text + "->" + aWord.getText());
System.out.println("distance " + Word.distanceBetween(previousWord,aWord));
System.out.println("wr pwr " + aWord.getUpperLeft().getRow() + " " +
previousWord.getUpperLeft().getRow());
        double fontFudgeFactor;
        if (previousWord.getFontType() == 'T' || aWord.getFontType() == 'T')
            fontFudgeFactor = 0.4;
        else fontFudgeFactor = 1.1;
        //else fontFudgeFactor = 1;

        if (Word.distanceBetween(previousWord,aWord) <
             fontFudgeFactor * max(previousWord.getFontSize(), aWord.getFontSize())/2.5 + 2) {
             //fontFudgeFactor * max(previousWord.getFontSize(), aWord.getFontSize())/2.5 + 1) {
           if (aWord.getText().equals("'") &&
               aWord.getUpperLeft().getRow() > previousWord.getUpperLeft().getRow() + 4)
             text = text +",";
           else text = text + aWord.getText();
}
        else text = text + " " + aWord.getText();
System.out.println("resulting text: " + text);
        previousWord = aWord;}}
    }

  public String getText() { return text;}
  public String getFont() {return font;}
  public int getOrientation() {return orientation;}
  public PointPixel getUpperLeft() {return upperLeft;}
  public PointPixel getLowerRight() {return lowerRight;}
  public Boolean getIsBold() {return font.charAt(1) == 'B';}
  public int getFontSize() {return Integer.parseInt(font.substring(2));}
  public void setInABlock() {inABlock = true;}
  public Boolean getInABlock() {return inABlock;}
  public LinkedList getWords() {return Words;}
  public Boolean justAbove(TextPiece aPiece) {
    PointPixel upperLeft2 = aPiece.getUpperLeft();
    PointPixel lowerRight2 = aPiece.getLowerRight();
    int r = lowerRight.getRow();
    int r2 = upperLeft2.getRow();
    int cb = upperLeft.getColumn();
    int ce = lowerRight.getColumn();
    int cb2 = upperLeft2.getColumn();
    int ce2 = lowerRight2.getColumn();
//System.out.println(font + " " + aPiece.getFont());
//System.out.println("r,r2 = " + r + "," + r2);
    if (font.equals(aPiece.getFont()) &&
        r2 > r &&
        r + getFontSize() > r2 &&     
        ((cb <= cb2 + 2 && ce >= ce2 - 2) || (cb2 <= cb + 2 && ce2 >= ce - 2)))
        return true;
        //((cb <= cb2 + 1) && 
         //(cb2 <= cb + 1))) return true;
    return false;
  }

    public static int max(int x, int y) {
//System.out.println("x y " + x + " " + y);
        if (x > y) return x;
        else return y;
    }

  public Boolean justLeftOf(TextPiece aPiece) {
    PointPixel upperLeft2 = aPiece.getUpperLeft();
    PointPixel lowerRight2 = aPiece.getLowerRight();
    int c = lowerRight.getColumn();
    int c2 = upperLeft2.getColumn();
    int rb = upperLeft.getRow();
    int re = lowerRight.getRow();
    int rb2 = upperLeft2.getRow();
    int re2 = lowerRight2.getRow();
    if (font.equals(aPiece.getFont()) &&
        c2 > c &&
        c + getFontSize() > c2 &&     
        ((rb <= rb2 + 1) && 
         (rb2 <= rb + 1))) return true;
    return false;
  }

    public TextBlock getInTextBlock() { return inTextBlock;}

    public void setInTextBlock(TextBlock tb) { inTextBlock = tb;}

    public Word getFirstWord() { return (Word)Words.getFirst();}
}

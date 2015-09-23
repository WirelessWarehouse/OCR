import java.util.*;


/*
* A TextBlock is a sequence of TextPieces that appear to be one block
* of text, possibly written over more than one line.
*/

public class TextBlock {

  private LinkedList pieces;
  private int orientation;
  private String font;
  private String text;
  private PointPixel upperLeft;
  private PointPixel lowerRight;
  private boolean isAttached = false; // to a bar, line or wedge or is a caption

  public TextBlock(LinkedList pieceList) {
    int lastColumn = -1;
    int thisColumn;
    pieces = pieceList;
    font = ((TextPiece)pieceList.getFirst()).getFont();
    orientation = ((TextPiece)pieceList.getFirst()).getOrientation();
    if (orientation == Primitive.HORIZONTAL) {
      upperLeft = ((TextPiece)pieceList.getFirst()).getUpperLeft();
      lowerRight = ((TextPiece)pieceList.getLast()).getLowerRight();}
    else {
      upperLeft = ((TextPiece)pieceList.getLast()).getUpperLeft();
      lowerRight = ((TextPiece)pieceList.getFirst()).getLowerRight();}
    text = new String();
    ListIterator lItr = pieceList.listIterator();
    while (lItr.hasNext()) {
      TextPiece aPiece = (TextPiece)lItr.next();
      aPiece.setInTextBlock(this);
      if (text.equals("")) text = aPiece.getText();
      else text = text + " " + aPiece.getText();
      thisColumn = aPiece.getLowerRight().getColumn();
      if (thisColumn > lastColumn) lastColumn = thisColumn;
      }
    if (orientation == Primitive.HORIZONTAL) {
      lowerRight = new PointPixel(((TextPiece)pieceList.getLast()).getLowerRight().getRow(),
                                  lastColumn);}
    else {
      lowerRight = new PointPixel(((TextPiece)pieceList.getFirst()).getLowerRight().getRow(),
                                  lastColumn);}

    }

  public String getText() { return text;}
  public void setText(String str) {text = str;}
  public String getFont() {return font;}
  public int getOrientation() {return orientation;}
  public PointPixel getUpperLeft() {return upperLeft;}
  public PointPixel getLowerRight() {return lowerRight;}
  public Boolean getIsBold() {return font.charAt(1) == 'B';}
  public int getFontSize() {return Integer.parseInt(font.substring(2));}
  public void setIsAttached(boolean b) {isAttached = b;}
  public boolean getIsAttached() {return isAttached;}
  public LinkedList getPieces() {return pieces;}

    // Remember, row numbers increase in the downward direction

    public boolean justAbove(Rectangle aBar) {
	if (lowerRight.getRow() <= aBar.getUpperLeft().getRow() &&
	    lowerRight.getRow() >= aBar.getUpperLeft().getRow() - 2*getFontSize() &&
            upperLeft.getColumn() < aBar.getLowerRight().getColumn() &&
	    lowerRight.getColumn() > aBar.getUpperLeft().getColumn())
	    return true;
	else return false;}

    public boolean justBelow(Rectangle aBar) {
	if (aBar.getLowerRight().getRow() <= upperLeft.getRow() &&
//DLC
//was using a factor of 3 in place of 4 for Amex example 
	    aBar.getLowerRight().getRow() >= upperLeft.getRow() - 2*getFontSize() &&
            aBar.getUpperLeft().getColumn() < lowerRight.getColumn() &&
	    aBar.getLowerRight().getColumn() > upperLeft.getColumn())
	    return true;
	else return false;}

    public boolean justAbove(PointPixel aPoint) {
	if (lowerRight.getRow() <= aPoint.getRow() &&
	    lowerRight.getRow() >= aPoint.getRow() - 6*getFontSize() &&
            upperLeft.getColumn() < aPoint.getColumn() + 6*getFontSize() &&
	    lowerRight.getColumn() > aPoint.getColumn() - 6*getFontSize())
	    return true;
	else return false;}

    public boolean justRightOf(Rectangle aBar) {
	if (upperLeft.getColumn() >= aBar.getLowerRight().getColumn() &&
            // for Burns; grabs .66 as another annotation word
	    //upperLeft.getColumn() <= aBar.getLowerRight().getColumn() + 3*getFontSize() &&
	    upperLeft.getColumn() <= aBar.getLowerRight().getColumn() + 2*getFontSize() &&
	    lowerRight.getRow() > aBar.getUpperLeft().getRow() &&
	    upperLeft.getRow() < aBar.getLowerRight().getRow())
	    return true;
	else return false;}

    public boolean justLeftOf(Rectangle aBar) {
	if (aBar.getUpperLeft().getColumn() >= lowerRight.getColumn() &&
	    //aBar.getUpperLeft().getColumn() <= lowerRight.getColumn() + 2*getFontSize() &&
	    aBar.getLowerRight().getRow() > upperLeft.getRow() &&
	    aBar.getUpperLeft().getRow() < lowerRight.getRow())
	    return true;
	else return false;}

    public boolean justLeftOfGroup(Rectangle aBar) {
        int height = lowerRight.getRow() - upperLeft.getRow();
	if (aBar.getUpperLeft().getColumn() >= lowerRight.getColumn() &&
	    //aBar.getUpperLeft().getColumn() <= lowerRight.getColumn() + 2*getFontSize() &&
	    aBar.getLowerRight().getRow() > upperLeft.getRow() &&
	    aBar.getUpperLeft().getRow() < lowerRight.getRow() + height)
	    return true;
	else return false;}

    public boolean insideOf(Rectangle aBar) {
        if (aBar.getUpperLeft().getColumn() < upperLeft.getColumn() &&
            lowerRight.getColumn() <  aBar.getLowerRight().getColumn() &&
            aBar.getUpperLeft().getRow() < upperLeft.getRow() &&
            lowerRight.getRow() < aBar.getLowerRight().getRow())
            return true;
        else return false;}


public Integer getFirstCharacter() {
  Object[] testArray = ((Word)((TextPiece)pieces.getFirst()).getWords().getFirst()).getCharacters().toArray();
  return (Integer)testArray[0];}

public Word getFirstWord() {
  return ((Word)((TextPiece)pieces.getFirst()).getWords().getFirst());}

    public boolean isPossibleTickLabel()
        {
        String[] s = text.split(" ");
        if(s.length < 1) return false;
        if (Graph.isNumber(s[0]) && s.length <3) return true;
        return false;
        }
    

}

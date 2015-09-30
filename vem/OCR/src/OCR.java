

public class OCR{
  RightProcessor rightscan;
  SegmentScan segmentScan;
  
  
  /*
  For now this class will just hold the different processed stuff (pass it in through the main method maybe?)
  Eventually it will load up a premade dictionary of segment pieces. 
  
  */
  OCR(){
    rightscan = new RightProcessor();
    segmentScan = new SegmentScan();  
  }
  
  public char getCharacter(int[][] glyph){
    int he = glyph.length(); //height of image
    int wi = glyph[0].length(); // width of image
    
    
    return('a');
  }
  
  
}
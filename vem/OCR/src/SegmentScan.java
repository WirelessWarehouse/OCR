public class Seg_Form {
  // this structure formats the data specific to each segment
  unsigned int ID;
  unsigned int start_X;
  unsigned int start_Y;
  unsigned int leftMost;
  unsigned int rightMost;
  unsigned int longestLine; //longest straight path in a given segment  
  unsigned int currLine // length of current computed pixels in a segment
  unsigned int end_X;
  unsigned int end_Y;
  
  // don't forget to initialize 
  
}
public class SegmentScan{
  // This is where the work is done!
  
  SegmentScan(){
    SegmentScan piece[unsigned int of_Puzzle=8] = new SegmentScan; //index may change; an object will hold all segments to a glyph
    }
    
    void public update() {
      if (present.x_co < leftMost) leftMost= present.x_co; 
      
      if (present.y_co > rightMost)rightMost= present.y_co;
      
      if (present.currLine > longestLine)
            longestLine = present.currLine; 
            
    }
}

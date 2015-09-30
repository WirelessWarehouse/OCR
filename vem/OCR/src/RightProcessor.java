


public class RightProcessor{

  int[][] RightProcessor(int [][] glyph){
      int [][] returnGlyph;
      int hi = Array.getLength(glyph);
      int wi = Array.getLength(glyph[0]);

      // initialize return matrix
      for(int i = 0; i < hi; i++) {
        for(int j = 0; j < wi; j++) {
            returnGlyph[i][j] = 1;
        }
      }

      // change right edges to black pixels
      for(int i = 0; i < hi; i++) {
        for(int j = 0; j < wi; j++) {
            // found a black pixel
            if(glyph[i][j] == 0) {
                // j+1 within bounds
                if(j+1 <= wi) {
                    // pixel immediately right of black pixel is white,
                    // so make returnGlyph[i][j] a right edge
                    if(glyph[i][j+1] == 1) returnGlyph[i][j] = 0;
                }
                // if j+1 is off the screen, make returnGlyph[i][j] a right edge
                else returnGlyph[i][j] = 0;
            }
        }
      }

      return returnGlyph;
  }
}

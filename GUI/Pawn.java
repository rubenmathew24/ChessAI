import java.util.HashMap;
import java.util.ArrayList;
class Pawn extends GamePiece{
  public Pawn(int pos_, boolean hasMoved_, boolean pieceColor_){
      super(pos_, hasMoved_, pieceColor_);
  }  
    
  public int[] possibleMoves(HashMap<Integer,GamePiece> board){
    ArrayList<Integer> moves = new ArrayList<Integer>();
    int X = this.getX();
    int Y = this.getY();
    
    
    
    return null;
  }  
}

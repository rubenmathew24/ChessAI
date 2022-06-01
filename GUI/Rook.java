import java.util.HashMap;
import java.util.ArrayList;
class Rook extends GamePiece{
  public Rook(int pos_, boolean hasMoved_, boolean pieceColor_){
      super(pos_, hasMoved_, pieceColor_);
    }  
  
  public int[] possibleMoves(HashMap<Integer,GamePiece> board){
    ArrayList<Integer> moves = new ArrayList<Integer>();
    int X = this.getX();
    int Y = this.getY();
    
    //Right
    for(int i = X+1; i < 8; i++){
      int pos = 8*i+Y;
      if(board.get(pos) != null){
        if(board.get(pos).isWhite() != this.isWhite()) moves.add(pos);
        break;
      }
      moves.add(pos);
    }
    
    //Left
    for(int i = X-1; i >= 0; i--){
      int pos = 8*i+Y;
      if(board.get(pos) != null){
        if(board.get(pos).isWhite() != this.isWhite()) moves.add(pos);
        break;
      }
      moves.add(pos);
    }
    
    //Down
    for(int i = Y+1; i < 8; i++){
      int pos = 8*X+i;
      if(board.get(pos) != null){
        if(board.get(pos).isWhite() != this.isWhite()) moves.add(pos);
        break;
      }
      moves.add(pos);
    }
    
    //Up
    for(int i = Y-1; i >= 0; i--){
      int pos = 8*X+i;
      if(board.get(pos) != null){
        if(board.get(pos).isWhite() != this.isWhite()) moves.add(pos);
        break;
      }
      moves.add(pos);
    }
    
    //------------------------------------------------------------
    
    int[] temp = new int[moves.size()];
    for(int i = 0; i < moves.size(); i++) temp[i] = moves.get(i);
    return temp;
  }
}

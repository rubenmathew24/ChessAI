class King extends GamePiece{
  public King(int pos_, boolean hasMoved_, boolean pieceColor_){
      super(pos_, hasMoved_, pieceColor_);
  }  
  
  
  public int[] possibleMoves(HashMap<Integer,GamePiece> board){
    ArrayList<Integer> moves = new ArrayList<Integer>();
    int X = this.getX();
    int Y = this.getY();
    
    //Upper Left
    if(X-1 >= 0 && Y-1 >=0 && board.get(this.getPos()).isWhite() != this.isWhite()) moves.add(8*(X-1)-(Y-1));
    
    return null;
  }
}

class Game{
  Board GameBoard;
  
  //Reset Variables
  int turn; 
  String moves;
  
  public Game(){
    GameBoard = new Board();
    turn = 1; 
    moves = "";
  }
  
  public int[] pieceSelected(int x, int y, int selectedX, int selectedY){
    int[] selected = {selectedX, selectedY};
    
    GamePiece selectedPiece = GameBoard.getPiece(selectedX, selectedY);
    GamePiece newPiece = GameBoard.getPiece(x, y);
    
    if(newPiece != null){
      
      //Nothing Selected
      if(selectedPiece == null){
        
        //Select your piece
        if(GameBoard.turn == newPiece.isWhite()){
          selected = new int[]{x,y};
        }
        //Hax mode
        //else if (hax){}
      }
      
      //Already Have selected piece
      else{
        
        //Deselect current piece
        if(x == selectedX && y == selectedY){
          selected = new int[]{-1,-1};
        }
        
        //Select different piece
        if(selectedPiece.isWhite() == GameBoard.turn){
          selected = new int[]{x,y};
        } 
      }
    }

    
    return selected;
  }
}

class Game{
	Board gameBoard;
	
	//Reset Variables
	int turn; 
	String moves;
	
	public Game(){
		gameBoard = new Board();
		turn = 1; 
		moves = "";
	}

	public int[] pieceSelected(int[] newXY, int[] oldXY){
		
		GamePiece selected = gameBoard.getPiece(toPos(oldXY));
		GamePiece newPiece = gameBoard.getPiece(toPos(newXY));
		
		//Clicked Already selected piece (Also handles clicking empty square w/ nothing selected)
		if(newPiece == selected) return new int[]{-1,-1};
		//Clicked Own Piece
        if(newPiece != null && newPiece.isWhite() == gameBoard.turn()) return newXY;
		//Clicked Enemy Piece with nothing selected
		if(newPiece != null && selected == null) return new int[]{-1,-1};
		//Clicked Occupied or Empty Square with selected Piece
		if(selected != null && gameBoard.possibleMoves.get(selected).contains(toPos(newXY))) {
    		//Moves
    		gameBoard.move(toPos(oldXY), toPos(newXY));
    		System.out.println("Legal Move");
    		return new int[]{-1,-1};
    	}
		
		return oldXY;
		
		

		





		/*int[] selected = {selectedX, selectedY};
		
		

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

		
		return selected;*/
		//return null;
	}

	//Helper Methods
	public static int toPos(int[] xy){
    	return 8*xy[0]+xy[1];
	}

	public static int[] toXY(int pos){
		return new int[]{pos/8, pos%8};
	}
}

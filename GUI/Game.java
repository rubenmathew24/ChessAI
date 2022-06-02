import java.util.Scanner;
import java.io.File;

class Game{
	Board gameBoard;
	public boolean hax = true; 

	//Reset Variables
	int turn; 
	String moves;
	
	public Game(){
		reset();
	}
	
	public void reset(){
		gameBoard = new Board();
		turn = 1;
		moves = "";
		if(hax) gameBoard.toggleHackMode();
	}

	public boolean hack(){
		hax = !hax;
		if(hax) gameBoard.toggleHackMode();
		return hax;
	}

	public int[] pieceSelected(int[] newXY, int[] oldXY){
		
		GamePiece selected = gameBoard.getPiece(toPos(oldXY));
		GamePiece newPiece = gameBoard.getPiece(toPos(newXY));
		
		//Clicked Already selected piece (Also handles clicking empty square w/ nothing selected)
		if(newPiece == selected) return new int[]{-1,-1};
		//Handle Hax mode capturing (Since technically all pieces are your pieces)
		if(hax) {
			if (newPiece != null && selected != null && gameBoard.possibleMoves.get(selected).contains(toPos(newXY))) {
				//Moves
			    gameBoard.move(toPos(oldXY), toPos(newXY));
			    System.out.println("Hax Capture");
			    return new int[]{-1,-1};
			}
		}
		//Clicked Own Piece
        if(newPiece != null && (newPiece.isWhite() == gameBoard.turn() || hax)) return newXY;
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

	public void importBoardState(File file){
		byte[] temp = new byte[39];
		String line;
		try(Scanner reader = new Scanner(file)){
    		line = reader.nextLine();
    		char[] c = line.toCharArray();
    		System.out.println(c.length + " " + line); // 78
    
    		for(int i = 0; i < c.length; i+=2){
        		//temp[i/2] = Byte.parseByte(""+c[i]+c[i+1], 16);
        		temp[i/2] = (byte) ((Character.digit(c[i],16) << 4) + Character.digit(c[i+1], 16));
        		System.out.println(i/2 +": "+c[i]+c[i+1] + " = " + String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(temp[i/2])))); //Debug
    		}
    		//for(int i = 0; i < temp.length; i++) System.out.println(Integer.toString(Byte.toUnsignedInt(temp[i]),2));
    	} catch(Exception e){
    		e.printStackTrace();
    	}
    	
    	gameBoard = new Board(temp);
    	if(hax) gameBoard.toggleHackMode();
	}
		

	//Helper Methods
	public static int toPos(int[] xy){
    	return 8*xy[0]+xy[1];
	}

	public static int[] toXY(int pos){
		return new int[]{pos/8, pos%8};
	}
}

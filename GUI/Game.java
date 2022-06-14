import java.util.Scanner;
import java.io.File;

class Game{
	Board gameBoard;
	public boolean hax = false; 
	public boolean promoting = false;
	public boolean inCheck = false;

	//Reset Variables
	//int turn; 
	//String moves;
	
	public Game(){
		reset();
	}
	
	public void reset(){
		gameBoard = new Board();
		promoting = false;
		inCheck = false;
		if(hax) gameBoard.toggleHackMode();
	}

	public boolean hack(){
		hax = !hax;
		gameBoard.toggleHackMode();
		return hax;
	}

	public void inCheck() {
		this.inCheck = gameBoard.checkCzech();
	}
 //<>//
	public void promote(int[] fromTo, int toPromote){
		gameBoard.move(Game.toPos(fromTo[0],fromTo[1]),Game.toPos(fromTo[2],fromTo[3])+(64*toPromote));
		promoting = false;
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
				return new int[]{-1,-1};
			}
		}
		//Clicked Own Piece
		if(newPiece != null && (newPiece.isWhite() == gameBoard.turn() || hax)){
			System.out.println(newPiece);
			return newXY;
		}
		//Clicked Enemy Piece with nothing selected
		if(newPiece != null && selected == null) return new int[]{-1,-1};
		//Clicked Occupied or Empty Square with selected Piece
		if(selected != null && gameBoard.possibleMoves.get(selected).contains(toPos(newXY))) {
			//Moves
			if(selected instanceof Pawn && newXY[1] == (selected.isWhite()? 0 : 7)){
				promoting = true;
			} else {
				gameBoard.move(toPos(oldXY), toPos(newXY));
				System.out.println("Legal Move");
				ChessEncoder ce = new ChessEncoder();
				byte[] com = ce.compressBoardState(gameBoard);
				System.out.println(ce.constructBoard(com).visualString());
				inCheck();
				return new int[]{-1,-1};
			}
		}

		return oldXY;
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
				System.out.println(String.format("%2s",i/2) +": "+c[i]+c[i+1] + " = " + String.format("%8s", Integer.toBinaryString(Byte.toUnsignedInt(temp[i/2]))).replace(" ", "0")); //Debug
			}
			//for(int i = 0; i < temp.length; i++) System.out.println(Integer.toString(Byte.toUnsignedInt(temp[i]),2));
		} catch(Exception e){
			e.printStackTrace();
		}
		
		gameBoard = new ChessEncoder().constructBoard(temp);
		if(hax) gameBoard.toggleHackMode();
		inCheck();
	}
		


	//Helper Methods
	public static int toPos(int[] xy){
		return 8*xy[0]+xy[1];
	}

	public static int toPos(int x, int y){
		return 8*x+y;
	}

	public static int[] toXY(int pos){
		return new int[]{pos/8, pos%8};
	}
}

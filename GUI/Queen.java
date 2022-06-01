import java.util.HashMap;
import java.util.ArrayList;
class Queen extends GamePiece{
	public Queen(int pos_, boolean hasMoved_, boolean pieceColor_, int ind_){
		super(pos_, hasMoved_, pieceColor_, ind_);
		this.img = (this.pieceColor) ? "WhiteQueen.png" : "BlackQueen.png";
	}	
		
	public ArrayList<Integer> possibleMoves(HashMap<Integer,GamePiece> board){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int X = this.getX();
		int Y = this.getY();
		int legality, pos;
		
		//Right
		for(int i = X+1; i < 8; i++){
			pos = 8*i+Y;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
		}
		
		//Left
		for(int i = X-1; i >= 0; i--){
			pos = 8*i+Y;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
			moves.add(pos);
		}
		
		//Down
		for(int i = Y+1; i < 8; i++){
			pos = 8*X+i;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
			moves.add(pos);
		}
		
		//Up
		for(int i = Y-1; i >= 0; i--){
			pos = 8*X+i;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
			moves.add(pos);
		}
	 
		//Upper Left
		for(int i = X-1,j = Y-1; i>=0 && j>=0; i--,j--){
			pos = 8*i+j;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
		}
		
		//Upper Right
		for(int i = X+1,j = Y-1; i<8 && j>=0; i++,j--){
			pos = 8*i+j;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
		}
		
		//Lower Left
		for(int i = X-1,j = Y+1; i>=0 && j<8; i--,j++){
			pos = 8*i+j;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
		}
		
		//Lower Right
		for(int i = X+1,j = Y+1; i<8 && j<8; i++,j++){
			pos = 8*i+j;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
		}
		
		//------------------------------------------------------------
		
		return moves;
	}
}

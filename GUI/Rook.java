import java.util.HashMap;
import java.util.ArrayList;
class Rook extends GamePiece{
	public Rook(int pos_, boolean hasMoved_, boolean pieceColor_){
		super(pos_, hasMoved_, pieceColor_);
		this.img = (this.pieceColor) ? "WhiteRook.png" : "BlackRook.png";
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
			pos = 8*i+Y;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
			moves.add(pos);
		}
		
		//Up
		for(int i = Y-1; i >= 0; i--){
			pos = 8*i+Y;
			legality = this.isLegalMove(board, pos);
			if(legality < 2)
				moves.add(pos);
			if(legality > 0)
				break;
			moves.add(pos);
		}
		
		//------------------------------------------------------------
		
		return moves;
	}
}

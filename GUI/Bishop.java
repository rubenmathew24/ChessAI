import java.util.HashMap;
import java.util.ArrayList;
class Bishop extends GamePiece{
    
    
	public Bishop(int pos_, boolean hasMoved_, boolean pieceColor_){
		super(pos_, hasMoved_, pieceColor_);
		this.img = (this.pieceColor) ? "WhiteBishop.png" : "BlackBishop.png";
	}	
	
	public ArrayList<Integer> possibleMoves(HashMap<Integer,GamePiece> board){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int X = this.getX();
		int Y = this.getY();
		int legality, pos;

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

	public Bishop clone() throws CloneNotSupportedException {
        return new Bishop(this.pos, this.hasMoved, this.pieceColor);
    }
}

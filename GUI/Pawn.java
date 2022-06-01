import java.util.HashMap;
import java.util.ArrayList;
class Pawn extends GamePiece{
	public Pawn(int pos_, boolean hasMoved_, boolean pieceColor_){
		super(pos_, hasMoved_, pieceColor_);
		this.img = (this.pieceColor) ? "WhitePawn.png" : "BlackPawn.png";
	}	
	
	public int[] possibleMoves(HashMap<Integer,GamePiece> board){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int X = this.getX();
		int Y = this.getY();
		int dir = (this.isWhite()? -1: 1);
		int pos = 8*X+Y+dir;
		int legality = this.isLegalMove(board, pos);
		// Moving forward 1
		if(legality == 0)
		{
			moves.add(pos);
			// Moving forward 2
			if(!this.hasMoved() && this.isLegalMove(board, pos+dir) == 0)
				moves.add(pos+dir);
		}
		
		// Capturing Diagonally
		pos = 8*(X+1)+Y+dir;
		legality = this.isLegalMove(board, pos);
		if(this.isLegalMove(board, pos) == 1)
			moves.add(pos);
		pos = 8*(X-1)+Y+dir;
		legality = this.isLegalMove(board, pos);
		if(this.isLegalMove(board, pos) == 1)
			moves.add(pos);
		
		// EN FUCKING PASSANT
		GamePiece p = board.get(-1);
		// There is a pawn to en passanted
		if(p != null && p.isWhite() != this.isWhite())
			// p is to the left
			if(this.getPos()-p.getPos() == 8)
				moves.add(8*(X-1)+Y+dir);
			// p is to the right
			else if(this.getPos()-p.getPos() == -8)
				moves.add(8*(X-1)+Y+dir);
		
		
		return this.toArray(moves);
	}
}

import java.util.HashMap;
import java.util.ArrayList;
class King extends GamePiece{
	public King(int pos_, boolean hasMoved_, boolean pieceColor_){
			super(pos_, hasMoved_, pieceColor_);
	}	
	
	
	public int[] possibleMoves(HashMap<Integer,GamePiece> board){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int X = this.getX();
		int Y = this.getY();
		int pos;
		int[][] possibleMoves = {{X-1, Y-1}, {X, Y-1}, {X+1, Y-1},
                                {X-1, Y}, {X+1, Y},
                                {X-1, Y+1}, {X, Y+1}, {X+1, Y+1}};		
		
		for(int[] move: possibleMoves)
        {
            pos = 8*move[0]+move[1];
            if(0 <= pos && pos < 64 && this.isLegalMove(board, pos) < 2)
                moves.add(pos);
        }
        
        // Castling
        if(!this.hasMoved)
        {
            GamePiece temp;
            
        	//King Side
        	pos = 8*(X+3)+Y;
        	temp = board.get(pos);
        	if(temp != null && !temp.hasMoved() && temp instanceof Rook) moves.add(8*(X+2)+Y);
        
        	//Queen Side
        	pos = 8*(X-4)+Y;
            temp = board.get(pos);
            if(temp != null && !temp.hasMoved() && temp instanceof Rook) moves.add(8*(X-2)+Y);
        }
        
        //------------------------------------------------------------
        
        return this.toArray(moves);
	}
}

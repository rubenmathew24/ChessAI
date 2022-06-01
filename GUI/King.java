import java.util.HashMap;
import java.util.ArrayList;
class King extends GamePiece{
	public King(int pos_, boolean hasMoved_, boolean pieceColor_, int ind_){
		super(pos_, hasMoved_, pieceColor_, ind_);
		this.img = (this.pieceColor) ? "WhiteKing.png" : "BlackKing.png";
	}	
	
	
	public ArrayList<Integer> possibleMoves(HashMap<Integer,GamePiece> board){
		ArrayList<Integer> moves = new ArrayList<Integer>();
		int X = this.getX();
		int Y = this.getY();
		int pos;
		int[][] possibleMoves = {{X-1, Y-1}, {X, Y-1}, {X+1, Y-1},
                                {X-1, Y}, {X+1, Y},
                                {X-1, Y+1}, {X, Y+1}, {X+1, Y+1}};		
		
		for(int[] move: possibleMoves)
        {
			if(0 > move[0] || 7 < move[0] || 0 > move[1] || 7 < move[1])
				continue;
            pos = 8*move[0]+move[1];
            if(this.isLegalMove(board, pos) < 2)
                moves.add(pos);
        }
        
        // Castling
        if(!this.hasMoved)
        {
            GamePiece temp;
            boolean blocked = false;
            
        	//King Side
        	for(int i = 1; i < 3; i++) if(board.get(8*(X+i)+Y) != null) blocked = true;
        	if(!blocked){
            	pos = 8*(X+3)+Y;
            	temp = board.get(pos);
            	if(temp != null && !temp.hasMoved() && temp instanceof Rook) moves.add(8*(X+2)+Y);
        	}
        	blocked = false;
        
        	//Queen Side
        	for(int i = 1; i < 4; i++) if(board.get(8*(X-i)+Y) != null) blocked = true;
        	if(!blocked){	
            	pos = 8*(X-4)+Y;
                temp = board.get(pos);
                if(temp != null && !temp.hasMoved() && temp instanceof Rook) moves.add(8*(X-2)+Y);
        	}
        }
        
        //------------------------------------------------------------
        
        return moves;
	}
}

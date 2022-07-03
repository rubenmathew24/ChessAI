import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;

class Larry
{
	Game g;
	boolean team;
    
    
	public Larry(Game g_, boolean team_){
		this.g = g_;
		this.team = team_;
	}

	public void move(){
		HashMap<GamePiece, ArrayList<Integer>> moves = g.gameBoard.possibleMoves;
		int to = -1;

		//Generate Moves
		GamePiece[] keys = new GamePiece[1];
		keys = moves.keySet().toArray(keys);
		GamePiece random;
		do{
			random = keys[(int)(Math.random()*keys.length)];
			ArrayList<Integer> vals = moves.get(random);
			to = (vals == null || vals.size()<1) ? -1 : vals.get((int)(Math.random()*vals.size()));
		}while(random.pieceColor != team || to == -1);

		//Make Move
		for(Map.Entry<Integer, GamePiece> me : g.gameBoard.board.entrySet()){
    		if(me.getValue() == random) System.out.println("Key in board: " + me.getKey());
		}
		System.out.println("\nAI MOVE:\n" + random + "From: " + random.getPos() + " To: " + to);
		g.gameBoard.move(random.getPos(), to);
	}
}

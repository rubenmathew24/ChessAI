import java.util.*;

class Larry
{
	HashMap<Class, Integer> pieceVals;
	HashMap<Class, ArrayList<Double>> offsets;
	Game g;
	boolean team;

	public Larry(Game g_, boolean team_){
		this.g = g_;
		this.team = team_;
		initializeEvals();
		initializeOffsets();
	}
	
	private void initializeEvals()
	{
		pieceVals = new HashMap<Class, Integer>();
		pieceVals.put(King.class, 0);
		pieceVals.put(Pawn.class, 1);
		pieceVals.put(Knight.class, 3);
		pieceVals.put(Bishop.class, 3);
		pieceVals.put(Rook.class, 5);
		pieceVals.put(Queen.class, 9);
	}
	
	private void initializeOffsets()
	{
		Double[] pieceOffsets;
		offsets = new HashMap<Class, ArrayList<Double>>();
		//King
		pieceOffsets = new Double[] {
				-3.0, -3.0, -3.0, -3.0, -2.0, -1.0, 2.0, 2.0,
				-4.0, -4.0, -4.0, -4.0, -3.0, -2.0, 2.0, 3.0,
				-4.0, -4.0, -4.0, -4.0, -3.0, -2.0, 0.0, 1.0,
				-5.0, -5.0, -5.0, -5.0, -4.0, -2.0, 0.0, 0.0,
				-5.0, -5.0, -5.0, -5.0, -4.0, -2.0, 0.0, 0.0,
				-4.0, -4.0, -4.0, -4.0, -3.0, -2.0, 0.0, 1.0,
				-4.0, -4.0, -4.0, -4.0, -3.0, -2.0, 2.0, 3.0,
				-3.0, -3.0, -3.0, -3.0, -2.0, -1.0, 2.0, 2.0
				};
		offsets.put(King.class, new ArrayList<Double>());
		offsets.get(King.class).addAll(Arrays.asList(pieceOffsets));
		
		//Queen
		pieceOffsets = new Double[] {
				-2.0, -1.0, -1.0, -0.5, 0.0, -1.0, -1.0, -2.0,
				-1.0, 0.0, 0.0, 0.0, 0.0, 0.5, 0.0, -1.0,
				-1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.5, -1.0,
				-0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5,
				-0.5, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -0.5,
				-1.0, 0.0, 0.5, 0.5, 0.5, 0.5, 0.0, -1.0,
				-1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -1.0,
				-2.0, -1.0, -1.0, -0.5, -0.5, -1.0, -1.0, -2.0,
		};
		offsets.put(Queen.class, new ArrayList<Double>());
		offsets.get(Queen.class).addAll(Arrays.asList(pieceOffsets));
		
		//Rook
		pieceOffsets = new Double[] {
				0.0, 0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0,
				0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5,
				0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.5,
				0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 1.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0,
				0.0, 0.5, -0.5, -0.5, -0.5, -0.5, -0.5, 0.0,
		};
		offsets.put(Rook.class, new ArrayList<Double>());
		offsets.get(Rook.class).addAll(Arrays.asList(pieceOffsets));
		
		//Bishop
		pieceOffsets = new Double[] {
				-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0,
				-1.0, 0.0, 0.0, 0.5, 0.0, 1.0, 0.5, -1.0,
				-1.0, 0.0, 0.5, 0.5, 1.0, 1.0, 0.0, -1.0,
				-1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, -1.0,
				-1.0, 0.0, 1.0, 1.0, 1.0, 1.0, 0.0, -1.0,
				-1.0, 0.0, 0.5, 0.5, 1.0, 1.0, 0.0, -1.0,
				-1.0, 0.0, 0.0, 0.5, 0.0, 1.0, 0.5, -1.0,
				-2.0, -1.0, -1.0, -1.0, -1.0, -1.0, -1.0, -2.0
		};
		offsets.put(Bishop.class, new ArrayList<Double>());
		offsets.get(Bishop.class).addAll(Arrays.asList(pieceOffsets));
		
		//Knight
		pieceOffsets = new Double[] {
				-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0,
				-4.0, -2.0, 0.0, 0.5, 0.0, 0.5, -2.0, -4.0,
				-3.0, 0.0, 1.0, 1.5, 1.5, 1.0, 0.0, -3.0,
				-3.0, 0.0, 1.5, 2.0, 2.0, 1.5, 0.5, -3.0,
				-3.0, 0.0, 1.5, 2.0, 2.0, 1.5, 0.5, -3.0,
				-3.0, 0.0, 1.0, 1.5, 1.5, 1.0, 0.0, -3.0,
				-4.0, -2.0, 0.0, 0.5, 0.0, 0.5, -2.0, -4.0,
				-5.0, -4.0, -3.0, -3.0, -3.0, -3.0, -4.0, -5.0
		};
		offsets.put(Knight.class, new ArrayList<Double>());
		offsets.get(Knight.class).addAll(Arrays.asList(pieceOffsets));
		
		//Pawn
		pieceOffsets = new Double[] {
				0.0, 5.0, 1.0, 0.5, 0.0, 0.5, 0.5, 0.0,
				0.0, 5.0, 1.0, 0.5, 0.0, -0.5, 1.0, 0.0,
				0.0, 5.0, 2.0, 1.0, 0.0, -1.0, 1.0, 0.0,
				0.0, 5.0, 3.0, 2.5, 2.0, 0.0, -2.0, 0.0,
				0.0, 5.0, 3.0, 2.5, 2.0, 0.0, -2.0, 0.0,
				0.0, 5.0, 2.0, 1.0, 0.0, -1.0, 1.0, 0.0,
				0.0, 5.0, 1.0, 0.5, 0.0, -0.5, 1.0, 0.0,
				0.0, 5.0, 1.0, 0.5, 0.0, 0.5, 0.5, 0.0
		};
		offsets.put(Pawn.class, new ArrayList<Double>());
		offsets.get(Pawn.class).addAll(Arrays.asList(pieceOffsets));
	}
	
	public void move(){
		HashMap<GamePiece, ArrayList<Integer>> moves = g.gameBoard.possibleMoves;
		int[] move = findBestMove(moves, this.team);
		g.gameBoard.move(move[0], move[1]);
		System.out.println("Evaluation: "+evaluateBoardState(g.gameBoard.board));
		
	}
	
	private int[] findBestMove(HashMap<GamePiece, ArrayList<Integer>> moves, boolean player)
	{
		int[] move = new int[2];
		double bestEval = -314, eval;
		HashMap<Integer, GamePiece> tempBoard;
		for(GamePiece p: moves.keySet())
			if(p.pieceColor == player)
				for(Integer i: moves.get(p))
				{
					tempBoard = g.gameBoard.cloneBoard();
					g.gameBoard.updateBoardState(p.getPos(), i, tempBoard);
					eval = (player ? 1 : -1) * evaluateBoardState(tempBoard);
					if(bestEval < eval)
					{
						bestEval = eval;
						move[0] = p.getPos();
						move[1] = i;
					}
				}
		return move;
	}
	
	private int[] generateRandomMove(HashMap<GamePiece, ArrayList<Integer>> moves, boolean player)
	{
        //Generate Moves
        GamePiece[] keys = new GamePiece[1];
        int to;
        keys = moves.keySet().toArray(keys);
        GamePiece random;
        do{
            random = keys[(int)(Math.random()*keys.length)];
            ArrayList<Integer> vals = moves.get(random);
            to = (vals == null || vals.size()<1) ? -1 : vals.get((int)(Math.random()*vals.size()));
        }while(random.pieceColor != player || to == -1);
        
        //Make Move
        for(Map.Entry<Integer, GamePiece> me : g.gameBoard.board.entrySet()){
            if(me.getValue() == random) System.out.println("Key in board: " + me.getKey());
        }
        return new int[]{random.getPos(), to};
        
	}

	private double evaluateBoardState(HashMap<Integer,GamePiece> b)
	{
		double value = 0;
		int evalIndex = -1;
		for(GamePiece p: b.values())
		{
			value += (p.isWhite()? 1: -1) * pieceVals.get(p.getClass());
			evalIndex = p.isWhite()? p.getPos() : 8*p.getX() + (7 - p.getY());
			value += (p.isWhite()? 1: -1) * offsets.get(p.getClass()).get(evalIndex);
		}
		return value;
	}
}

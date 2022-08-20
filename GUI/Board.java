import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Board
{
	private boolean turn;
	private boolean gameOver;
	public boolean mateType; // true if checkmate false if stalemate
	private boolean hackMode;
	public HashMap<Integer,GamePiece> board;
	public HashMap<Integer, ArrayList<Integer>> possibleMoves;
	private static ChessEncoder ce = new ChessEncoder();
	private CompressedState cs;
	private boolean compressedChanged;
	
	//Default Constructor (New Game)
	public Board()
	{
		turn = true;
		gameOver = false;
		compressedChanged = true;
		board = new HashMap<Integer,GamePiece>();	
		possibleMoves = new HashMap<Integer, ArrayList<Integer>>();
	
		// Black
		board.put(0, new Rook(0, false, false));
		board.put(8, new Knight(8, false, false));
		board.put(16, new Bishop(16, false, false));
		board.put(24, new Queen(24, false, false));
		board.put(32, new King(32, false, false)); board.put(-3, board.get(32));
		board.put(40, new Bishop(40, false, false));
		board.put(48, new Knight(48, false, false));
		board.put(56, new Rook(56, false, false));
	
		for(int i = 1; i<64; i +=8) board.put(i, new Pawn(i, false, false));
	
		// White
		board.put(7, new Rook(7, false, true));
		board.put(15, new Knight(15, false, true));
		board.put(23, new Bishop(23, false, true));
		board.put(31, new Queen(31, false, true));
		board.put(39, new King(39, false, true)); board.put(-2, board.get(39));
		board.put(47, new Bishop(47, false, true));
		board.put(55, new Knight(55, false, true));
		board.put(63, new Rook(63, false, true));
	
		for(int i = 6; i<64; i +=8) board.put(i, new Pawn(i, false, true));
		cs = new CompressedState();
		updateCompressedState();
		updatePossibleMoves(board);
	}

	// Used by ChessEncoder to create Boards
	public Board(boolean _turn, HashMap<Integer, GamePiece> _board, byte[] _compressed)
	{
		turn = _turn;
		gameOver = false;
		board = _board;
		possibleMoves = new HashMap<Integer, ArrayList<Integer>>();
		cs = new CompressedState(_compressed);
		compressedChanged = false;
		updatePossibleMoves(board);
	}
	public void updatePossibleMoves(HashMap<Integer, GamePiece> board)
	{
		for(Integer i: board.keySet())
			if(board.get(i) != null) //&& (p.isWhite() == turn || hackMode))
				possibleMoves.put(i, board.get(i).possibleMoves(board));
			else
				possibleMoves.remove(i);
		if(board == this.board)
			filterPossibleMoves(board);
	}
	
	private void filterPossibleMoves(HashMap<Integer, GamePiece> board)
	{
		HashMap<Integer, GamePiece> tempBoard;
		HashSet<Integer> all;
		HashSet<Integer> illegal = new HashSet<Integer>();
		for(Integer i: board.keySet())
		{
			if(board.get(i) == null)
				continue;
			GamePiece p = board.get(i);
			if(possibleMoves.get(i) != null)
			{
				for(Integer to : possibleMoves.get(i))
				{
					
					tempBoard = cloneBoard();
					updateBoardState(p.getPos(), to, tempBoard);
					turn = !turn;
					updatePossibleMoves(tempBoard);
					all = allPossibleMoves(tempBoard, turn);
					turn = !turn;
					if(all.contains(tempBoard.get(turn? -2 : -3).getPos()))
						illegal.add(to);
				}
				// Castling through check
				if(p instanceof King)
				{
					if(possibleMoves.get(i).contains(p.getPos()-16) && illegal.contains(p.getPos()-8))
						illegal.add(p.getPos()-16);
					if(possibleMoves.get(i).contains(p.getPos()+16) && illegal.contains(p.getPos()+8))
						illegal.add(p.getPos()+16);
				}
				possibleMoves.get(i).removeAll(illegal);
				illegal.clear();
			}
		}
	}
	private HashMap<Integer, GamePiece> cloneBoard()
	{
		HashMap<Integer, GamePiece> ret = new HashMap<Integer, GamePiece>();
		for(int i: board.keySet())
		{
			try {
				ret.put(i, board.get(i).clone());
				if(board.get(i) == board.get(-1))
					ret.put(-1, ret.get(i));
				else if(board.get(i) == board.get(-2))
					ret.put(-2, ret.get(i));
				else if(board.get(i) == board.get(-3))
					ret.put(-3, ret.get(i));
			} catch(Exception e) {}	
		}
		return ret;
	}
	
	public HashSet<Integer> allPossibleMoves(HashMap<Integer, GamePiece> board, boolean team)
	{
		HashSet<Integer> r = new HashSet<Integer>();
		for(Integer i: board.keySet())
			if(board.get(i) != null && board.get(i).isWhite() == team)
				r.addAll(possibleMoves.get(i));
		return r;
	}
	private void updateCompressedState()
	{
		cs.compressed = ce.compressBoardState(this);
		compressedChanged = false;
	}
	private void updateBoardState(int from, int to, HashMap<Integer,GamePiece> board)
	{
		GamePiece f = board.get(from);
		GamePiece t = board.get(to % 64);
		GamePiece p;
		byte control;
		//Promoting
		if(f instanceof Pawn && to%8 == (f.isWhite()? 0 : 7))
		{
			board.remove(from);
			board.put(to%64, ((Pawn)f).promotedPiece(to));
			f.setPos(to%64);
			f.moved();
		}
		//Moving to empty space
		else if(t == null)
		{
			to %= 64;
			board.remove(from);
			board.put(to, f);
			f.setPos(to);
			f.moved();
			// Pawn checks for En Passant
			if(f instanceof Pawn)
			{
				p = board.get(-1);
				if(p != null && to-p.getPos() == (f.isWhite()? -1: 1))
					board.remove(p.getPos());
				// Some idiot pawn is leaving En Passant up
				if(to - from == (f.isWhite()? -2: 2))
					board.put(-1, f);
				else
					board.remove(-1);
			}
			else
				board.remove(-1);
			
			// Castling
			if(f instanceof King)
			{
				to %= 64;
				if(to-from == 16)
				{
					p = board.get(f.isWhite()? 63: 56);
					board.remove(p.getPos());
					board.put(p.getPos()-16, p);
					p.setPos(p.getPos()-16);
					p.moved();
				}
				else if(to-from == -16)
				{
					p = board.get(f.isWhite()? 7: 0);
					board.remove(p.getPos());
					board.put(p.getPos()+24, p);
					p.setPos(p.getPos()+24);
					p.moved();
				}
			}
		}
		//Capturing
		else
		{
			to %= 64;
			board.remove(from);
			board.put(to, f);
			board.remove(-1);
			f.setPos(to);
			f.moved();
		}
	}
	// Assumes to -> from is a legal move
	public void move(int from, int to)
	{
		updateBoardState(from, to, board);
		if(!hackMode)
			turn = !turn;
		updatePossibleMoves(board);
		compressedChanged = true;
		if(allPossibleMoves(board, turn).size() == 0)
		{
			gameOver = true;
			mateType = checkCzech();
		}
	}
	// Accessor Methods
	public GamePiece getPiece(int pos)
	{
		return board.get(pos);
	}
	public byte[] getCompressed()
	{
		if(compressedChanged)
			updateCompressedState();
		return cs.compressed;
	}
	public boolean turn()
	{
		return turn;
	}
	public boolean gameOver()
	{
		return gameOver;
	}
	public boolean checkCzech(){
		return allPossibleMoves(board, !turn).contains(board.get((turn) ? -2 : -3).getPos());
    }

	public void toggleHackMode()
	{
		this.hackMode = !this.hackMode;
		updatePossibleMoves(board);
	}
	// Returns 1939597999b9d9f9 9d1dfd3ddd5dbd7d 0525456585a5c5e581 01e121c141a161 00000000000001 for a starting board
	public String compressedString()
	{
	if(compressedChanged) updateCompressedState();
		String ret = "";
		for(byte b: cs.compressed)
			//ret += Integer.toString(Byte.toUnsignedInt(b), 16);
			ret += String.format("%02x", Byte.toUnsignedInt(b));
		return ret;
	}
	public String visualString(){
		final String border = "-------------------------";
		String board = border + "\n";
		String temp = "|";
		int counter = 0;
		
		for(int i = 0; i < 8; i++){
			for(int j = 0; j < 8; j++){
				GamePiece p = this.board.get(i+(8*j));
				String piece = " ";
		
				//Check Piece letter
				if(p instanceof Pawn)  piece = "P";
				else if(p instanceof Bishop)piece = "B";
				else if(p instanceof Knight)piece = "N";
				else if(p instanceof Rook)  piece = "R";
				else if(p instanceof King)  piece = "K";
				else if(p instanceof Queen) piece = "Q";
				
				temp += ((p != null) ? ((p.isWhite()) ? "W" : "B") : " ") + piece + "|";
				if (++counter % 8 == 0){
					board += temp + "\n" + border + "\n";
					temp = "|";
				}
			}	
		}
		
		return board;
	}
	public String toString()
	{
		return compressedString();
	}
}

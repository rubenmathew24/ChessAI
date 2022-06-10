import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

class Board
{
	private boolean turn;
	private boolean gameOver;
	private boolean hackMode;
	private byte[] compressed;
	public HashMap<Integer,GamePiece> board;
	public HashMap<GamePiece, ArrayList<Integer>> possibleMoves;
	public Board()
	{
		//                      White Pawn 0      White Pawn 1      White Pawn 2      White Pawn 3      White Pawn 4      White Pawn 5      White Pawn 6      White Pawn 7
		compressed = new byte[]{(byte)0b00011001, (byte)0b00111001, (byte)0b01011001, (byte)0b01111001, (byte)0b10011001, (byte)0b10111001, (byte)0b11011001, (byte)0b11111001,
		//                      White King        White Rook        White Rook        White Knight      White Knight      White Bishop      White Bishop      White Queen
								(byte)0b10011101, (byte)0b00011101, (byte)0b11111101, (byte)0b00111101, (byte)0b11011101, (byte)0b01011101, (byte)0b10111101, (byte)0b01111101,
		//                      Black Pawn 0      Black Pawn 1      Black Pawn 2      Black Pawn 3      Black Pawn 4      Black Pawn 5      Black Pawn 6      Black Pawn 7
								(byte)0b00000101, (byte)0b00100101, (byte)0b01000101, (byte)0b01100101, (byte)0b10000101, (byte)0b10100101, (byte)0b11000101, (byte)0b11100101,
		//                      Black King        Black Rook        Black Rook        Black Knight      Black Knight      Black Bishop      Black Bishop      Black Queen
								(byte)0b10000001, (byte)0b00000001, (byte)0b11100001, (byte)0b00100001, (byte)0b11000001, (byte)0b01000001, (byte)0b10100001, (byte)0b01100001,
		//                      White Promoted    Black Promoted    White Promo Type  White Promo Type  Black Promo Type  Black Promo Type  Control
								(byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000001};
		turn = true;
		gameOver = false;
		hackMode = false;
		possibleMoves = new HashMap<GamePiece, ArrayList<Integer>>();
		uncompressBoard();
		updatePossibleMoves();
	}
	public Board(byte[] _compressed)
	{
		compressed = _compressed;
		turn = (compressed[38] & 0b00000001) == 1;
		gameOver = (compressed[38] & 0b00000010)>>1 == 1;
		hackMode = false;
		possibleMoves = new HashMap<GamePiece, ArrayList<Integer>>();
		uncompressBoard();
		updatePossibleMoves();
	}
	private void uncompressBoard()
	{
		board = new HashMap<Integer, GamePiece>();
		boolean enPassant = (compressed[38] & 0b00000100)>>2 == 1;
		int pawnIndex = (compressed[38] & 0b00111000)>>3 + (turn? 1: 0)*16;
		int pos, promotedIndex;
		boolean hasMoved, color;
		Class[] promotedTypes = {Queen.class, Knight.class, Rook.class, Bishop.class};
		Class[] types = {Pawn.class, Pawn.class, Pawn.class, Pawn.class, Pawn.class, Pawn.class, Pawn.class, Pawn.class,
                King.class, Rook.class, Rook.class, Knight.class, Knight.class, Bishop.class, Bishop.class, Queen.class};
		Class[] params = {int.class, boolean.class, boolean.class, int.class};
		for(int i=0; i<32; i++)
		{
			if(compressed[i]%2 == 0)
				continue;
			pos = (compressed[i] & 0b11111100)>>2;
			hasMoved = (compressed[i] & 0b00000010)>>1 == 1;
			color = i < 16;
			if(i%16 < 8) // Piece is a pawn
			{
				if((compressed[(color?32:33)] & (1 << (i%8)) >> (i%8)) == 1) // Pawn was promoted
				{
					promotedIndex = (i%16)*2+32*(i<16? 0: 1);
					promotedIndex = (compressed[(34+promotedIndex/8)] & 0b11 << (promotedIndex%16)) >> (promotedIndex%16);
					try
					{
						board.put(pos, (GamePiece)promotedTypes[promotedIndex].getConstructor(params).newInstance(pos, hasMoved, color, i));
					} catch(Exception e) {System.err.print(e);}
				}
				else
					board.put(pos, new Pawn(pos, hasMoved, color, i));
				if(i == pawnIndex && enPassant)
					board.put(-1, (Pawn)board.get(pos));
			}
			else
				try
				{
					board.put(pos, (GamePiece)types[i%16].getConstructor(params).newInstance(pos, hasMoved, color, i));
				} catch(Exception e) {System.err.print(e);}
		}
		if(!enPassant)
			board.remove(-1);
	}
	
	public void updatePossibleMoves()
	{
		for(GamePiece p: board.values())
			if(p != null && (p.isWhite() == turn || hackMode))
				possibleMoves.put(p, p.possibleMoves(board));
	}
	public HashSet<Integer> allPossibleMoves(boolean team)
	{
		HashSet<Integer> r = new HashSet<Integer>();
		for(GamePiece p: possibleMoves.keySet())
			if(p.isWhite() == team)
				r.addAll(possibleMoves.get(p));
		return r;
	}
	// Assumes to -> from is a legal move
	public void move(int from, int to)
	{
		GamePiece f = board.get(from);
		GamePiece t = board.get(to);
		GamePiece p;
		byte control;
		//Moving to empty space
		if(t == null)
		{
			board.remove(from);
			board.put(to, f);
			compressed[f.index()] = (byte)((to<<2) + 0b11);
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
				{
					board.put(-1, f);
					compressed[38] = (byte) (((f.index()%8) << 3) + (1 << 2) + (turn? 1 : 0));
				}
				else if(to < 0)
				{
					board.put(from + (f.isWhite()? -1: 1), ((Pawn)f).promotedPiece(1-to));
					compressed[(f.isWhite()?32:33)] |= 1 << (f.index()%8);
					int promotedIndex = (f.index()%16)*2+32*(f.index()<16? 0: 1);
					compressed[(34+promotedIndex/8)] |= 1-to << (promotedIndex%16); 
				}
				else
				{
					board.remove(-1);
					compressed[38] = (byte) (turn? 1 : 0);
				}
			}
			else
				board.remove(-1);
			
			// Castling
			if(f instanceof King)
			{
				if(to-from == 16)
				{
					p = board.get(f.isWhite()? 63: 56);
					board.remove(p.getPos());
					board.put(p.getPos()-16, p);
					p.setPos(p.getPos()-16);
					p.moved();
					compressed[p.index()] = (byte)((p.getPos()<<2) + 0b11);
				}
				else if(to-from == -16)
				{
					p = board.get(f.isWhite()? 7: 0);
					board.remove(p.getPos());
					board.put(p.getPos()+24, p);
					p.setPos(p.getPos()+24);
					p.moved();
					compressed[p.index()] = (byte)((p.getPos()<<2) + 0b11);
				}
			}
		}
		//Capturing
		else
		{
			board.remove(from);
			board.put(to, f);
			compressed[f.index()] = (byte)((to<<2) + 0b11);
			compressed[t.index()] -= 1;
			f.setPos(to);
			f.moved();
		}

		if(!hackMode){
			turn = !turn;
			compressed[38] = (byte)(compressed[38] + (turn? 1: -1));
		}
		updatePossibleMoves();
		//Deal with promotion 6/3/22
	}
	//Accessor Methods
	public GamePiece getPiece(int pos)
	{
		return board.get(pos);
	}
	
	public byte[] getCompressed()
	{
		return compressed;
	}

	public boolean turn()
	{
		return turn;
	}

	public boolean gameOver()
	{
		return gameOver;
	}

	public void toggleHackMode(){
		this.hackMode = !this.hackMode;
		updatePossibleMoves();
	}

	// Returns 1939597999b9d9f9 9d1dfd3ddd5dbd7d 0525456585a5c5e581 01e121c141a161 00000000000001 for a starting board
	private String compressedString()
	{
		String ret = "";
		for(byte b: compressed)
			//ret += Integer.toString(Byte.toUnsignedInt(b), 16);
			ret += String.format("%02x", Byte.toUnsignedInt(b));
		return ret;
	}
	
	private String visualString(){
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

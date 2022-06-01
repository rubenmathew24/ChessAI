import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
class Board
{
	private boolean turn;
	private boolean gameOver;
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
								(byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000000, (byte)0b00000000};
		turn = true;
		gameOver = false;
		possibleMoves = new HashMap<GamePiece, ArrayList<Integer>>();
		uncompressBoard();
		updatePossibleMoves();
	}
	public Board(byte[] _compressed)
	{
		compressed = _compressed;
		turn = (compressed[38] & 0b00000001) == 1;
		gameOver = (compressed[38] & 0b00000010)>>1 == 1;
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
		Class[] params = {int.class, boolean.class, boolean.class};
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
						board.put(pos, (GamePiece)promotedTypes[promotedIndex].getConstructor(params).newInstance(pos, hasMoved, color));
					} catch(Exception e) {System.err.print(e);}
				}
				else
					board.put(pos, new Pawn(pos, hasMoved, color));
				if(i == pawnIndex && enPassant)
					board.put(-1, (Pawn)board.get(pos));
			}
			else
				try
				{
					board.put(pos, (GamePiece)types[i%16].getConstructor(params).newInstance(pos, hasMoved, color));
				} catch(Exception e) {System.err.print(e);}
		}
		if(!enPassant)
			board.remove(-1);
	}
	
	public void updatePossibleMoves()
	{
		for(GamePiece p: board.values())
			if(p != null && p.isWhite() == turn)
				possibleMoves.put(p, p.possibleMoves(board));
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

	public String toString(){
		return "Hello! Nothing has caught fire";
	}
}

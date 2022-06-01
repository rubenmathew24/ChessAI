import java.util.ArrayList;
import java.util.HashMap;
abstract class GamePiece
{
	protected int pos;
	protected boolean pieceColor;
	protected boolean hasMoved;
	public GamePiece(int pos_, boolean hasMoved_, boolean pieceColor_)
	{
		pos = pos_;
		hasMoved = hasMoved_;
		pieceColor = pieceColor_;
	}
	public boolean hasMoved()
	{
		return hasMoved;
	}
	public int getPos()
	{
		return pos;
	}
	public int getX()
	{
		return pos/8; 
	}
	public int getY()
	{
		return pos%8;
	}
	public boolean isWhite()
	{
		return pieceColor;
	}
	abstract public int[] possibleMoves(HashMap<Integer, GamePiece> board);
	public int isLegalMove(HashMap<Integer, GamePiece> board, int pos)
	{
		if(0<pos || pos >= 64)
			return 3;
		GamePiece p = board.get(pos);
		if(p == null)
			return 0; // Empty
		if(p.isWhite() != this.isWhite())
			return 1; // Take
		return 2; // Blocked	
	}
	public int[] toArray(ArrayList<Integer> moves)
	{
		int[] temp = new int[moves.size()];
		for(int i = 0; i < moves.size(); i++) temp[i] = moves.get(i);
		return temp;
	}
}

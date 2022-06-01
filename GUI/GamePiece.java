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
	//abstract public int[] possibleMoves(HashMap<Integer, GamePiece> board);
}

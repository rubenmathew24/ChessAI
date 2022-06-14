import java.util.ArrayList;
import java.util.HashMap;
abstract class GamePiece implements Cloneable
{
	protected int pos;
	protected boolean pieceColor;
	protected boolean hasMoved;
	protected String img;
	public GamePiece(int pos_, boolean hasMoved_, boolean pieceColor_)
	{
		pos = pos_;
		hasMoved = hasMoved_;
		pieceColor = pieceColor_;
		img = "";
	}
	public void setPos(int pos_)
	{
		pos = pos_;
	}
	public void moved()
	{
		hasMoved = true;
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
	public String getImg()
	{
		return img;
	}
	public boolean isWhite()
	{
		return pieceColor;
	}
	abstract public ArrayList<Integer> possibleMoves(HashMap<Integer, GamePiece> board);
	public int isLegalMove(HashMap<Integer, GamePiece> board, int pos)
	{
		if(0 > pos || pos > 63)
			return 3; // Out of Bounds
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

	public String toString(){
		return "Type: " + img.substring(0, img.length()-4) + "\nPos = " + pos + "\npieceColor = " + pieceColor + "\nhasMoved = " + hasMoved + "\n";
	}

	public abstract GamePiece clone() throws CloneNotSupportedException;
}

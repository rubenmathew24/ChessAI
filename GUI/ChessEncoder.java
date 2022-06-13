import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class ChessEncoder
{
	// Using HUffman Coding scheme from https://codegolf.stackexchange.com/questions/19397/smallest-chess-board-compression
	private class Node
	{
		public Node left, right, parent;
		public GamePiece type;
		public boolean leaf;
		public Node()
		{
			parent = null;
			leaf = false;
		}
		public Node(Node p)
		{
			parent = p;
			leaf = false;
		}
		public Node(GamePiece _type)
		{
			type = _type;
			leaf = true;
		}
	}
	// private Node[] trees; (to be implemented for further compression of endgame board states
	private Node tree;
	public ChessEncoder()
	{
		constructTree();
	}
	private void constructTree()
	{
		tree = new Node();
		Node empty = new Node((GamePiece)null);
		Node K = new Node(new King(-1, false, true, -1)), k = new Node(new King(-1, false, false, -1));
		Node Q = new Node(new Queen(-1, false, true, -1)), q = new Node(new Queen(-1, false, false, -1));
		Node R = new Node(new Rook(-1, false, true, -1)), r = new Node(new Rook(-1, false, false, -1));
		Node N = new Node(new Knight(-1, false, true, -1)), n = new Node(new Knight(-1, false, false, -1));
		Node B = new Node(new Bishop(-1, false, true, -1)), b = new Node(new Bishop(-1, false, true, -1));
		Node P = new Node(new Pawn(-1, false, true, -1)), p = new Node(new Pawn(-1, false, true, -1));
		
		
		Node t1;
		
		t1 = tree;
		
		t1.left = empty;
		empty.parent = t1;
		
		t1.right = new Node(t1);
		t1 = t1.right;
		
		// White Branch
		t1.right = new Node(t1);
		t1 = t1.right;
		
		// White Pawn
		t1.left = P;
		P.parent = t1;
		
		// Rest of the pieces
		t1.right = new Node(t1);
		t1 = t1.right;
		
		// Rooks and Bishops
		t1.left = new Node(t1);
		t1 = t1.left;
		
		t1.left = R;
		R.parent = t1;
		
		t1.right = B;
		B.parent = t1;
		
		// Knights, Queens Kings
		t1 = t1.parent;
		t1.right = new Node(t1);
		t1 = t1.right;
		
		t1.left = N;
		N.parent = t1;
		
		t1.right = new Node(t1);
		t1 = t1.right;
		
		t1.left = Q;
		Q.parent = t1;
		
		t1.right = K;
		K.parent = t1;
		//------------------------------------------------
		// Black Pieces
		//------------------------------------------------
		t1 = tree.right;
		
		t1.left = new Node(t1);
		t1 = t1.left;
		
		// Black Pawn
		t1.left = p;
		p.parent = t1;
		
		// Rest of the pieces
		t1.right = new Node(t1);
		t1 = t1.right;
		
		// Rooks and Bishops
		t1.left = new Node(t1);
		t1 = t1.left;
		
		t1.left = r;
		r.parent = t1;
		
		t1.right = b;
		b.parent = t1;
		
		// Knights, Queens Kings
		t1 = t1.parent;
		t1.right = new Node(t1);
		t1 = t1.right;
		
		t1.left = n;
		n.parent = t1;
		
		t1.right = new Node(t1);
		t1 = t1.right;
		
		t1.left = q;
		q.parent = t1;
		
		t1.right = k;
		k.parent = t1;
	}
	public Board constructBoard(byte[] c)
	{
		int i = 0;
		// First three bits are a header
		boolean turn = getBit(i++,c) == 1;
		boolean castlingPossible = getBit(i++, c) == 1;
		boolean enPassantPossible = getBit(i++, c) == 1;
		
		// if castling possible next four bits are where it can be done, in order
		// White Left Castle, White Right Castle, Black Left Castle, Black Right Castle
		boolean L = false, R = false, l = false, r = false;
		if(castlingPossible)
		{
			L = getBit(i++, c) == 1;
			R = getBit(i++, c) == 1;
			l = getBit(i++, c) == 1;
			r = getBit(i++, c) == 1;
		}
		
		// if En Passant Possible, next 3 bits are the column it is possible on.
		// Note: Color of piece is opposite current turn and row of piece is 4 if black, 5 if white
		int enPassantColumn = -1;
		if(enPassantPossible)
			enPassantColumn = 4*getBit(i++, c) + 2*getBit(i++, c) + getBit(i++, c);
		
		// Traverse Huffman Tree
		Node n;
		HashMap<Integer, GamePiece> board = new HashMap<Integer, GamePiece>();
		Class[] params = new Class[]{int.class, boolean.class, boolean.class, int.class};
		boolean hasMoved = false;
		GamePiece t, p;
		for(int pos=0; pos < 64; pos++)
		{
			n = tree;
			while(!n.leaf)
				if(getBit(i++, c) == 0)
					n = n.left;
				else
					n = n.right;
			t = n.type;
			if(t != null)
			{
				if(t instanceof King)
				{
					if(t.isWhite() && (L || R))
						hasMoved = true;
					else if(!t.isWhite() && (l || r))
						hasMoved = true;
					else
						hasMoved = false;
				}
				else if(t instanceof Rook)
				{
					if(L && pos == 7)
						hasMoved = false;
					else if(R && pos == 63)
						hasMoved = false;
					else if(l && pos == 0)
						hasMoved = false;
					else if(r && pos == 56)
						hasMoved = false;
					else
						hasMoved = true;
				}
				else if(t instanceof Pawn)
				{
					if(pos%8 == (t.isWhite()? 1 : 6))
						hasMoved = false;
					else
						hasMoved = true;
				}
				else
					hasMoved = false;
				
				try {
					// TODO: Replace all gamePiece Constructors with one that no longer has a compressed Index
					p = t.getClass().getConstructor(params).newInstance(pos, hasMoved, t.isWhite(), -1);
					board.put(pos, p);
					if(enPassantPossible && pos == 8*enPassantColumn + (turn? 3 : 4))
						board.put(-1, p);
				} catch (Exception e) {}
			}
		}
		return new Board(turn, board);
	}
	
	private int getBit(int i, byte[] compressed)
	{
		if(i/8 < compressed.length)
			return (compressed[i/8] & 1 << (8 - i%8 - 1)) >> (8 - i%8 - 1);
		return 0;
	}
}

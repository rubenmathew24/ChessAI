import java.util.ArrayList;
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
	private HashMap<Class, Integer> treeIndex;	
	public ChessEncoder()
	{
		constructTree();
	}
	private void constructTree()
	{
		tree = new Node();
		Node empty = new Node((GamePiece)null);
		Node K = new Node(new King(-1, false, true)), k = new Node(new King(-1, false, false));
		Node Q = new Node(new Queen(-1, false, true)), q = new Node(new Queen(-1, false, false));
		Node R = new Node(new Rook(-1, false, true)), r = new Node(new Rook(-1, false, false));
		Node N = new Node(new Knight(-1, false, true)), n = new Node(new Knight(-1, false, false));
		Node B = new Node(new Bishop(-1, false, true)), b = new Node(new Bishop(-1, false, false));
		Node P = new Node(new Pawn(-1, false, true)), p = new Node(new Pawn(-1, false, false));
		
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
		
		treeIndex = new HashMap<Class, Integer>();
		
		treeIndex.put(null, 0);
		treeIndex.put(Pawn.class, 0b0);
		treeIndex.put(Rook.class, 0b100);
		treeIndex.put(Bishop.class, 0b101);
		treeIndex.put(Knight.class, 0b110);
		treeIndex.put(Queen.class, 0b1110);
		treeIndex.put(King.class, 0b1111);
	}
	public Board constructBoard(byte[] c)
	{
		int i = 0;
		// First three bits are a header
		boolean gameOver = getBit(i++, c) == 1;
		if(gameOver)
			return null;
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
		Class[] params = new Class[]{int.class, boolean.class, boolean.class};
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
						hasMoved = false;
					else if(!t.isWhite() && (l || r))
						hasMoved = false;
					else
						hasMoved = true;
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
					if(pos%8 == (t.isWhite()? 6 : 1))
						hasMoved = false;
					else
						hasMoved = true;
				}
				else
					hasMoved = false;
				
				try {
					p = t.getClass().getConstructor(params).newInstance(pos, hasMoved, t.isWhite());
					board.put(pos, p);
					if(enPassantPossible && pos == 8*enPassantColumn + (turn? 3 : 4))
						board.put(-1, p);
					if(p instanceof King)
						board.put(p.isWhite()? -2 : -3, p);
				} catch (Exception e) {}
			}
		}
		return new Board(turn, board, c);
	}
	
	public byte[] compressBoardState(Board b)
	{
		HashMap<Integer, GamePiece> board = b.board;
		ArrayList<Integer> compressed = new ArrayList<Integer>();
		// Header bits
		// if game over don't compress board state
		compressed.add(b.gameOver()? 1: 0);
		if(b.gameOver())
		{
			compressed.add(b.mateType? 1: 0);
			return toArray(compressed);
		}
		
		compressed.add(b.turn()? 1 : 0);
		
		boolean L = (!board.get(-2).hasMoved() && board.get(7) instanceof Rook && !board.get(7).hasMoved());
		boolean R = (!board.get(-2).hasMoved() && board.get(63) instanceof Rook && !board.get(63).hasMoved());
		boolean l = (!board.get(-3).hasMoved() && board.get(0) instanceof Rook && !board.get(0).hasMoved());
		boolean r = (!board.get(-3).hasMoved() && board.get(56) instanceof Rook && !board.get(56).hasMoved());
		int enPassantColumn = (board.get(-1) != null)? board.get(-1).getX() : -1;
		
		compressed.add((L || R || l || r)? 1 : 0);
		compressed.add((enPassantColumn > 0)? 1 : 0);
		
		// Castling Block
		if(L || R || l || r)
		{
			compressed.add(L? 1:0);
			compressed.add(R? 1:0);
			compressed.add(l? 1:0);
			compressed.add(r? 1:0);
		}
		
		// En Passant Block
		if(enPassantColumn > 0)
		{
			compressed.add(enPassantColumn/4);
			compressed.add((enPassantColumn%4)/2);
			compressed.add(enPassantColumn%2);
		}
		GamePiece p;
		for(int pos = 0; pos < 64; pos++)
		{
			p = board.get(pos);
			if(p == null)
				compressed.add(0);
			else
			{
				compressed.add(1);
				compressed.add(p.isWhite()? 1: 0);
				switch(treeIndex.get(p.getClass()))
				{
				case 0b0:
					compressed.add(0);
					break;
				case 0b100:
					compressed.add(1);
					compressed.add(0);
					compressed.add(0);
					break;
				case 0b101:
					compressed.add(1);
					compressed.add(0);
					compressed.add(1);
					break;
				case 0b110:
					compressed.add(1);
					compressed.add(1);
					compressed.add(0);
					break;
				case 0b1110:
					compressed.add(1);
					compressed.add(1);
					compressed.add(1);
					compressed.add(0);
					break;
				case 0b1111:
					compressed.add(1);
					compressed.add(1);
					compressed.add(1);
					compressed.add(1);
				}
			}
		}
		return toArray(compressed);
	}
	
	private int getBit(int i, byte[] compressed)
	{
		if(i/8 < compressed.length)
			return (compressed[i/8] & 1 << (8 - i%8 - 1)) >> (8 - i%8 - 1);
		return 0;
	}
	
	private byte[] toArray(ArrayList<Integer> compressed)
	{
		byte[] ret = new byte[(int)Math.ceil(compressed.size()/8.0)];
		for(int i = 0, b; i < compressed.size();i++)
		{
			b = compressed.get(i);
			if(b == 0)
				ret[i/8] &= ~(1 << (8 - i%8 -1));
			else
				ret[i/8] |= (1 << (8 - i%8 -1));
		}
		return ret;
	}
}

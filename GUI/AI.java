import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map;
import java.io.File;
import java.io.PrintWriter;

class Larry
{
	Game g;
	boolean team;
	int tempFileNum=0;
    
    
	public Larry(Game g_, boolean team_){
		this.g = g_;
		this.team = team_;
	}

	public void move(){
		HashMap<Integer, ArrayList<Integer>> moves = g.gameBoard.possibleMoves;
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

    public static String getArrString(ArrayList<Integer> arr, String delim)
    {
        String temp = "";
        for(Integer t: arr)
            temp += t+""+delim;
        return temp+"\n";
    }
    
    public void logPossibleMoves(HashMap<GamePiece, ArrayList<Integer>> moves){ 
        try{ 
            File file = new File("C:\\Users\\ruben\\OneDrive\\Desktop\\moves"+tempFileNum+".txt");
            file.createNewFile();
            PrintWriter writer = new PrintWriter(file);
            System.out.println("All Moves:");
            for(Map.Entry<GamePiece, ArrayList<Integer>> entry : moves.entrySet()){
            writer.print("From: " + entry.getKey().getPos() + "To: ");
            writer.print(getArrString(entry.getValue(), ", "));
        }
            tempFileNum++;
            writer.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}

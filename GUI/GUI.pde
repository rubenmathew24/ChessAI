//Version 1.2.8

//Adujustable Sizes

/*
	4K size: {2500, 2000, 200};
	1080p size: {1250, 1000,100};
*/
final int[] sizeConstants = {1250, 1000,100}; //width, height, size of board squares
final int boxSize = sizeConstants[2]; //Sets for easy variable usage in equations

//AI Settings
boolean AIActive = false; // T/F to turn AI on/off
boolean AIColor = true; // T/F AI color white/black

//Variables
int boardOrigin = ((sizeConstants[0]/2)-(4*boxSize))/2; //Centers it then adjusts it a little to give room to menu on right
int[] selected = {-1,-1}; //Stores X,Y of currently selected piece //{-1,-1} if nothing selected
boolean gameFinished = false;
boolean promoting = false;

//Game and AI Objects
Game g = new Game(); //Game Object
//AI albert = (AIActive) ? new AI(AIColor, g) : null;

//Find a better way to do this later:
//Storing lastMove temporarily for decompression
//GamePiece L = null;

void setup() {
	surface.setResizable(true);
	size(2000, 2000); //Intial window size because processing doesn't allow variable parameters for size()
	surface.setSize(sizeConstants[0], sizeConstants[1]);
	background(0);
	frameRate(30);
}

void draw() {
	//Draw Board (Squares and labels)
	grid();
	
	//Draw Import Button
	importButton();
	
	//Draw Export Button
	exportButton();
	
	//Draw Restart Button
	restartButton();
	
	//Promote Menu (for debugging)
	//promoteMenu(false);
	
	//Error Menu (for debugging)
	//error();
	
	//Highlight selected piece and possible moves
	selectedPieceGUI();
	
	//Draw Pieces
	drawPieces();
}

void mouseClicked(){
	//Get X & Y
	//if(AIActive && AIColor && g.turn == 1) albert.generateMove(); //If AI is playing white, make first move
	
	int x = (mouseX < boardOrigin) ? -1 : (mouseX-boardOrigin)/boxSize; //Checks if left of board
	int y = (mouseY < boardOrigin) ? -1 : (mouseY-boardOrigin)/boxSize; //Checks if above board

	//If Clicking on the board
	if(!promoting && !gameFinished && (x >= 0 && x < 8 && y >= 0 && y < 8)){
		selected = g.pieceSelected(new int[]{x,y}, selected);
		//System.out.println(selected[0] + "," + selected[1]);
	} 
	
	//Check if its on the reset button
	else if(mouseX >= boardOrigin + (8.5 * boxSize) && mouseX < boardOrigin + (10.8 * boxSize) && mouseY >= boardOrigin + (7.5 * boxSize) && mouseY < boardOrigin + (8 * boxSize)){
		//g.reset();
		
		//Resets booleans
		promoting = false;
		gameFinished = false;
		
		//Handles Game Over and Promotion Menu
		stroke(0);
		fill(0);
		rect(boardOrigin + (8.1*boxSize), boardOrigin, 8*boxSize, 6*boxSize);
		
		//Unselects
		selected = new int[]{-1,-1};
	}
	
	//Allow Export of game
	if(mouseX >= boardOrigin + (9.8 * boxSize) && mouseX < boardOrigin + (10.8 * boxSize) && mouseY >= boardOrigin + (6.8 * boxSize) && mouseY < boardOrigin + (7.3 * boxSize)){
		PrintWriter export = createWriter("ExportedGame.txt");
		//export.println(g.getBoardState());
		export.flush();
		export.close();
		
		System.out.println("\nExport Successful");
	}
	
	//Allow Import of game
	if(mouseX >= boardOrigin + (8.5 * boxSize) && mouseX < boardOrigin + (9.5 * boxSize) && mouseY >= boardOrigin + (6.8 * boxSize) && mouseY < boardOrigin + (7.3 * boxSize)){
		selectInput("Select a file to process:", "importGame");
		
		//Reset booleans
		promoting = false;
		gameFinished = false;
	}
	
	//If Promoting
	if(!gameFinished && promoting && mouseX >= boardOrigin + (8.1 * boxSize) && mouseX < boardOrigin + (9.1 * boxSize) && mouseY >= boardOrigin && mouseY < boardOrigin + (4*boxSize)){
		handlePromote(y);
	}
	
	//Debug
	//if(g.turn > 2) printCompressed();
}

void handlePromote(int toPromote){
	/*boolean success = g.promote(toPromote);
	if(success) {
		
		//Handles Promotion menu
		promoting = false;
		stroke(0);
		fill(0);
		rect(boardOrigin + (8.1*boxSize), boardOrigin, boxSize, 4*boxSize);
		
		if(g.checkForMate()){
			King kC = g.GameBoard.getKing(g.GameBoard.getTurn());
			gameOver(kC.inCheck);
		}
	}
	else error();
	*/
}

void importButton(){
	//Variables
	int[] buttonColor = {125,148,93}; // (R, G, B)
	int[] textColor = {238,238,213}; // (R, G, B)
	float buttonOrigin = boardOrigin + (8.5 * boxSize);
	
	//Setup
	stroke(200);
	textSize(boxSize/8);
	
	//Button
	fill(buttonColor[0],buttonColor[1],buttonColor[2]);
	rect(buttonOrigin, boardOrigin + (6.8 * boxSize), boxSize, boxSize/2);
	
	//Icon
	PImage imp = loadImage("Imp.png");
	imp.resize((int)(0.35*boxSize), (int)(0.35*boxSize));
	image(imp, buttonOrigin+(0.31*boxSize), boardOrigin+(6.8*boxSize));
	
	//Text
	fill(textColor[0],textColor[1],textColor[2]);
	text("Import", buttonOrigin + (0.29 * boxSize), boardOrigin + (7.25 * boxSize));
}

void exportButton(){
	//Variables
	int[] buttonColor = {125,148,93}; // (R, G, B)
	int[] textColor = {238,238,213}; // (R, G, B)
	float buttonOrigin = boardOrigin + (9.8 * boxSize);
	
	//Setup
	stroke(200);
	textSize(boxSize/8);
	
	//Button
	fill(buttonColor[0],buttonColor[1],buttonColor[2]);
	rect(buttonOrigin, boardOrigin + (6.8 * boxSize), boxSize, boxSize/2);
	
	//Icon
	PImage exp = loadImage("Exp.png");
	exp.resize((int)(0.35*boxSize), (int)(0.35*boxSize));
	image(exp, buttonOrigin+(0.31*boxSize), boardOrigin+(6.8*boxSize));
	
	//Text
	fill(textColor[0],textColor[1],textColor[2]);
	text("Export", buttonOrigin + (0.3 * boxSize), boardOrigin + (7.25 * boxSize));
}

void importGame(File game) {
	if (game == null) {
		println("Window was closed or the user hit cancel.");
	} else {
		//Handles Game Over and Promotion Menu
		stroke(0);
		fill(0);
		rect(boardOrigin + (8.1*boxSize), boardOrigin, 8*boxSize, 6*boxSize);
		
		//g.importBoardState(game.getAbsolutePath());
		System.out.println("\nImport Successful");
	}
}

void error(){
	//Prevent redrawing of game
	noLoop();
	background(0);
	surface.setSize(2500,1800);
	
	//Notify User of Error
	fill(255,0,0);
	textSize(boxSize);
	text("Error", (width/2)-(1.5*boxSize), boardOrigin);
	text("Something went wrong", (width/2)-(5.5*boxSize), boardOrigin+(2*boxSize));
	text("Terminating Program", (width/2)-(5.2*boxSize), boardOrigin+(4*boxSize));
	
	//Maybe use later for funny meme, it doesn't seem to work now... Maybe issue with the file itself?
	PImage img = loadImage("pieces.png");
	img.resize(300, 300);
	image(img, (width/2)-(1.2*boxSize), boardOrigin+(5*boxSize));
	
	
	//Delay to keep message shown before exiting
	try {
		Thread.sleep(2000);
	} catch(InterruptedException ex) {
		Thread.currentThread().interrupt();
	}
	
	exit();
}

void restartButton(){
	//Variables
	int[] buttonColor = {125,148,93}; // (R, G, B)
	int[] textColor = {238,238,213}; // (R, G, B)
	float buttonOrigin = boardOrigin + (8.5 * boxSize);
	
	//Setup
	stroke(200);
	textSize(boxSize/3);
	
	//Button
	fill(buttonColor[0],buttonColor[1],buttonColor[2]);
	rect(buttonOrigin, boardOrigin + (7.5 * boxSize), 2.3*boxSize, boxSize/2);
	
	//Text
	fill(textColor[0],textColor[1],textColor[2]);
	text("New Game", buttonOrigin + (0.3 * boxSize), boardOrigin + (7.85 * boxSize));
}

void grid() {
	//Variables
	int end = boardOrigin + (8*boxSize);
	int txtSize = boxSize/6;
	char[] fileValues = {'a','b','c','d','e','f','g','h'}; //rows
	char[] rankValues = {'8','7','6','5','4','3','2','1'}; //columns
	int[] lightColor = {1,238,238,213}; // {boolean, R, G, B}
	int[] darkColor = {0,125,148,93}; // {boolean, R, G, B}
	int[] checkColor = {102, 5, 5}; // {R, G, B}
	int[] squareColor = darkColor;
	
	//Setup
	stroke(100);
	strokeWeight(boxSize/40);
	textSize(txtSize);
	
	//Creates Grid
	for(int r = boardOrigin; r < end; r += boxSize) {
		for(int c = boardOrigin; c < end; c += boxSize) {
			squareColor = (squareColor[0] == 0) ? lightColor : darkColor; //toggles dark and light color squares
			fill(squareColor[1],squareColor[2],squareColor[3]);
			rect(c, r, boxSize, boxSize); //Creates square
		}
		squareColor = (squareColor[0] == 0) ? lightColor : darkColor;
	}
	
	fill(100,100,100); //to fill text grey
	
	//Rank labels
	for(int rank = boardOrigin; rank < end; rank += boxSize){
		int index = ((rank-boardOrigin)/boxSize);
		text(rankValues[index], boardOrigin+(txtSize/4), rank+txtSize+(txtSize/10));
	}
	
	//File labels
	for(int file = boardOrigin+boxSize; file < end+boxSize; file += boxSize){
		int index = ((file-boardOrigin-boxSize)/boxSize);
		
		switch(index){
			case 6:
				text(fileValues[index], file-(txtSize)+(txtSize/3), end-(txtSize/3)); //Only here because 'g' goes lower than other chars
				break; 
			case 7: case 1: case 3:
				text(fileValues[index], file-(txtSize)+(txtSize/4), end-(txtSize/5)); //Only here because 'b','d','h' goes further right than other chars
				break;
			default:
				text(fileValues[index], file-(txtSize)+(txtSize/3), end-(txtSize/5));
		}
		
	}
}

void gameOver(boolean checkmate){
	/*//Variables
	gameFinished = true;
	int[] stalemateColor = {191,170,61}; // (R, G, B)
	int[] checkmateColor = {102, 5, 5}; // (R, G, B)
	int[] textColor = {255,255,255}; // (R, G, B)
	float buttonOrigin = boardOrigin + (8.5 * boxSize);
	
	//Setup
	stroke(200);
	textSize(boxSize/3);
	
	//Button
	int[] button = (checkmate) ? checkmateColor : stalemateColor;
	fill(button[0],button[1],button[2]);
	rect(buttonOrigin, boardOrigin + (0 * boxSize), 2.3*boxSize, boxSize/2);
	
	//Text
	fill(textColor[0],textColor[1],textColor[2]);
	if(checkmate) text("Checkmate", buttonOrigin + (0.29 * boxSize), boardOrigin + (0.36 * boxSize));
	else text("Stalemate", buttonOrigin + (0.3 * boxSize), boardOrigin + (0.36 * boxSize));
	
	if(checkmate){
		String winner = (!g.GameBoard.getTurn()) ? "White" : "Black";
		text(winner+ " Wins", buttonOrigin + (0.3 * boxSize), boardOrigin + (1.0 * boxSize));
	} else text("Draw", buttonOrigin + (0.4 * boxSize), boardOrigin + (1.0 * boxSize));
	*/
}

void selectedPieceGUI(){
	//Get selected position, if nothing is selected then exit
	if(selected[0] == -1 || selected[1] == -1) return;
	int selectedPos = Game.toPos(selected); 
	
	GamePiece selectedPiece = g.gameBoard.getPiece(selectedPos);
	
	//Yellow Higlight of Chosen Piece
    fill(200,200,50);
    rect(boardOrigin + (selected[0] * boxSize), boardOrigin + (selected[1] * boxSize), boxSize, boxSize);
    
    //Puts Indicator for legal moves of piece
    stroke(100);
    
    for(int pos : g.gameBoard.possibleMoves.get(selectedPiece)){
        //if potential move would not put king in check
        
        int[] moveTo = Game.toXY(pos);
        
        //Legal Capture
        if(g.gameBoard.board.get(pos) != null){
        	fill(184, 117, 112);
			rect(boardOrigin + (moveTo[0] * boxSize), boardOrigin + (moveTo[1] * boxSize), boxSize, boxSize);
        } 
        
        //Legal Move
        else {       
            fill(180,180,180);
            circle(boardOrigin + moveTo[0]*boxSize + boxSize/2, boardOrigin + moveTo[1]*boxSize + boxSize/2, boxSize/10);
        }
        
    }

	/*GamePiece selected = g.GameBoard.getSelected();
	
	if (selected == null) return;
	
	//Get Board
	GamePiece[][] board = g.GameBoard.getPieceMatrix();
	
	//Yellow Higlight of Chosen Piece
	fill(200,200,50);
	rect(boardOrigin + (selected.getX() * boxSize), boardOrigin + (selected.getY() * boxSize), boxSize, boxSize);
			
	//Puts Indicator for legal moves of piece
	stroke(100);
	
	ArrayList<String> moves = selected.findLegalMoves(board);
			
	//Only needs an indicator if its legal
	for (String move : moves){
		int[] moveFrom = squareToCoords(move.substring(0,2));
		int[] moveTo = squareToCoords(move.substring(2));
		if(!g.GameBoard.potentialMove(moveFrom[0], moveFrom[1], moveTo[0], moveTo[1]).checkCzech(g.GameBoard.getTurn())){

			if(selected instanceof Pawn){ //Signify Capture for En Passant
					Pawn en = (Pawn) selected;
					if(en.isEnPassantPossible() && moveTo[0] != en.getX()){
						fill(184, 117, 112);
						rect(boardOrigin + (moveTo[0] * boxSize), boardOrigin + (moveTo[1] * boxSize), boxSize, boxSize);
					}
			}
			if(board[moveTo[1]][moveTo[0]] == null){ //Normal Legal Move
				fill(180,180,180);
				circle(boardOrigin + moveTo[0]*boxSize + boxSize/2, boardOrigin + moveTo[1]*boxSize + boxSize/2, boxSize/10);
			}
			else{ //Legal Capture
				fill(184, 117, 112);
				rect(boardOrigin + (moveTo[0] * boxSize), boardOrigin + (moveTo[1] * boxSize), boxSize, boxSize);
			}
		}
	}*/
}

void promoteMenu(boolean pieceColor){
	//Variables
	/*int[] lightColor = {238,238,213}; // {R, G, B}
	int[] darkColor = {125,148,93}; // {R, G, B}
	PImage piece = null;
	
	//Setup
	stroke(100);
	strokeWeight(boxSize/40);
	fill(lightColor[0],lightColor[1],lightColor[2]);
 
	//Queen
	rect(boardOrigin + (8.1*boxSize), boardOrigin, boxSize, boxSize); //Box 1
	piece = loadImage(pieceColor ? "WhiteQueen.png" : "BlackQueen.png");
	piece.resize(boxSize, boxSize);
	image(piece, boardOrigin + (8.1*boxSize),	boardOrigin);
	
	//Knight
	rect(boardOrigin + (8.1*boxSize), boardOrigin + (2*boxSize), boxSize, boxSize); //Box 3
	piece = loadImage(pieceColor ? "WhiteKnight.png" : "BlackKnight.png");
	piece.resize(boxSize, boxSize);
	image(piece, boardOrigin + (8.1*boxSize),	boardOrigin + (2*boxSize));
	
	//Change Colors
	fill(darkColor[0],darkColor[1],darkColor[2]);
	
	//Bishop
	rect(boardOrigin + (8.1*boxSize), boardOrigin + (1*boxSize), boxSize, boxSize); //Box 2
	piece = loadImage(pieceColor ? "WhiteBishop.png" : "BlackBishop.png");
	piece.resize(boxSize, boxSize);
	image(piece, boardOrigin + (8.1*boxSize),	boardOrigin + (1*boxSize));
	
	//Rook
	rect(boardOrigin + (8.1*boxSize), boardOrigin + (3*boxSize), boxSize, boxSize); //Box 4
	piece = loadImage(pieceColor ? "WhiteRook.png" : "BlackRook.png");
	piece.resize(boxSize, boxSize);
	image(piece, boardOrigin + (8.1*boxSize),	boardOrigin + (3*boxSize));
	
	//Notify In promote Menu
	promoting = true;*/
}

void drawPieces(){	
	for(GamePiece p : g.gameBoard.board.values()){
    	if(p != null){
        	PImage piece = loadImage(p.getImg());
            piece.resize(boxSize, boxSize);
            image(piece, boardOrigin + (p.getX() * boxSize), boardOrigin + (p.getY() * boxSize));
        }
	}	
    
	/*GamePiece[][] board = g.GameBoard.getPieceMatrix();

	for(GamePiece[] a : board){
		for(GamePiece p : a){
			if(p != null){
				//Load, resize, and draw pieces
				PImage piece = loadImage(p.getImageString());
				piece.resize(boxSize, boxSize);
				image(piece, boardOrigin + (p.getX() * boxSize), boardOrigin + (p.getY() * boxSize));
			}
		}
	}*/
}


//Helper Methods
public String coordsToSquare(int x, int y){
	/*char[] fileValues = {'a','b','c','d','e','f','g','h'}; //rows
	char[] rankValues = {'8','7','6','5','4','3','2','1'}; //columns
	
	return "" + fileValues[x] + rankValues[y];*/
	return null;
}

public int[] squareToCoords(String square){
	/*int[] coords = null;
	if(square.length() != 2) return null;
	String fileValues = "abcdefgh"; //rows
	String rankValues = "87654321"; //columns
	
	coords = new int[]{fileValues.indexOf(square.charAt(0)), rankValues.indexOf(square.charAt(1))};
	
	if(coords[0] == -1 || coords[1] == -1) return null;
	else return coords;*/
	return null;
}

//Debug Method
public void printCompressed(){
	/*byte[] B = g.GameBoard.compressBoardState();
	for(byte b : B){
		System.out.println(b);
	}*/
}

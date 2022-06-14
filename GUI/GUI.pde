//Version 2.1

//Adujustable Sizes

/*
	4K size: {2500, 2000, 200};
	1080p size: {1250, 1000,100};
*/
final int[] sizeConstants = {1250, 1000,100}; //width, height, size of board squares
final int boxSize = sizeConstants[2]; //Sets for easy variable usage in equations
final int boardOrigin = ((sizeConstants[0]/2)-(4*boxSize))/2; //Centers it then adjusts it a little to give room to menu on right

//AI Settings
boolean AIActive = false; // T/F to turn AI on/off
boolean AIColor = true; // T/F AI color white/black

//Game Variables
int[] selected = {-1,-1}; //Stores X,Y of currently selected piece //{-1,-1} if nothing selected
int[] promoting = {-1,-1,-1,-1}; //Stores X,Y of promoting piece, and X,Y of destination //{-1,-1,-1,-1} if nothing selected
//boolean gameFinished = false;

//Aesthetic Variables (R,G,B)
color lightColor = color(238,238,213);
color darkColor = color(125,148,93); 
color selectedColor = color(200,200,50); 
color captureColor = color(184, 117, 112); 
color checkColor = color(102, 5, 5);
color circleColor = color(180);
color moveOutlineColor = color(100);
color gridOutlineColor = moveOutlineColor;
color labelColor = moveOutlineColor;
color haxIndicatorOn = color(0,255,0);
color haxIndicatorOff = color(255,0,0);

//Game Object
Game g = new Game(); //Game Object

//Run at Start
void setup() {
	surface.setResizable(true);
	size(2000, 2000); //Intial window size because processing doesn't allow variable parameters for size()
	surface.setSize(sizeConstants[0], sizeConstants[1]);
	background(0);
	frameRate(30); //How many times a second to call draw()
}

//What is drawn each frame
void draw() {
	//Draw Board (Squares and labels)
	grid();
	
	//Draw hax Button
	haxButton();

	//Draw Import Button
	importButton();
	
	//Draw Export Button
	exportButton();
	
	//Draw Restart Button
	restartButton();
	
	//Promote Menu (only if able to promote)
	if(g.promoting) promoteMenu(g.gameBoard.turn());
	
	//Highlight selected piece and possible moves
	selectedPieceGUI();
	
	//Draw Pieces
	drawPieces();
}

//Called when there is a Mouse Click event
void mouseClicked(){
	//Get X & Y
	
	int x = (mouseX < boardOrigin) ? -1 : (mouseX-boardOrigin)/boxSize; //Checks if left of board
	int y = (mouseY < boardOrigin) ? -1 : (mouseY-boardOrigin)/boxSize; //Checks if above board

	//If Clicking on the board
	if(!g.promoting && (x >= 0 && x < 8 && y >= 0 && y < 8)){
		selected = g.pieceSelected(new int[]{x,y}, selected);
		if(g.promoting) promoting = new int[]{selected[0],selected[1],x,y};
		else promoting = new int[]{-1,-1,-1,-1};
	} 
	
	//Check if its on the reset button
	else if(mouseX >= boardOrigin + (8.5 * boxSize) && mouseX < boardOrigin + (10.8 * boxSize) && mouseY >= boardOrigin + (7.5 * boxSize) && mouseY < boardOrigin + (8 * boxSize)){
		g.reset();
		resetIndicators();
		
		//Unselects
		selected = new int[]{-1,-1};
	}
	
	//Check if its on the hax button
    else if(mouseX >= boardOrigin + (8.5 * boxSize) && mouseX < boardOrigin + (10.8 * boxSize) && mouseY >= boardOrigin + (6.1 * boxSize) && mouseY < boardOrigin + (6.6 * boxSize)){
		g.hack();
    }

	//If Promoting
  	else if(g.promoting && mouseX >= boardOrigin + (8.1 * boxSize) && mouseX < boardOrigin + (9.1 * boxSize) && mouseY >= boardOrigin && mouseY < boardOrigin + (4*boxSize)){
    	handlePromote(y);
  	}

	//Allow Export of game
	else if(mouseX >= boardOrigin + (9.8 * boxSize) && mouseX < boardOrigin + (10.8 * boxSize) && mouseY >= boardOrigin + (6.8 * boxSize) && mouseY < boardOrigin + (7.3 * boxSize)){
    	selectOutput("Choose a name for Exported Game", "exportGame");	
		System.out.println("\nExport Successful");
	}
	
	//Allow Import of game
	else if(mouseX >= boardOrigin + (8.5 * boxSize) && mouseX < boardOrigin + (9.5 * boxSize) && mouseY >= boardOrigin + (6.8 * boxSize) && mouseY < boardOrigin + (7.3 * boxSize)){
		selectInput("Select a file to process:", "importGame");
		
		//Unselects
		selected = new int[]{-1,-1};
	}
}

void handlePromote(int toPromote){
  //Codes
  // Queen - 0
  // Knight - 1
  // Rook - 2
  // Bishop - 3
  g.promote(promoting, toPromote);
  resetIndicators();
  //boolean success = g.promote(toPromote);
  /*if(success) {
    
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
  else error();*/
}

void resetIndicators(){
	//Handles Promotion menu
    stroke(0);
    strokeWeight(boxSize/20);
    fill(0);
    rect(boardOrigin + (8.1*boxSize), boardOrigin, boxSize, 4*boxSize);
}

//Method to create Hack Mode button
void haxButton(){
    //Variables
    float buttonOrigin = boardOrigin + (8.5 * boxSize);
    
    //Setup
    stroke(lightColor);
    textSize(boxSize/4);
    
    //Button
    fill(darkColor);
    rect(buttonOrigin, boardOrigin + (6.1 * boxSize), 2.3*boxSize, boxSize/2);
    
    //Text
    fill(lightColor);
    text("Hacks Mode", buttonOrigin + (0.2 * boxSize), boardOrigin + (6.45 * boxSize));
    
    //Indicator
    fill((g.hax) ? haxIndicatorOn : haxIndicatorOff);
    rect(buttonOrigin + (1.8 * boxSize), boardOrigin + (6.18 * boxSize), boxSize/3, boxSize/3);
}

//Method to create import button
void importButton(){
	//Variables
	float buttonOrigin = boardOrigin + (8.5 * boxSize);
	
	//Setup
	stroke(lightColor);
	textSize(boxSize/8);
	
	//Button
	fill(darkColor);
	rect(buttonOrigin, boardOrigin + (6.8 * boxSize), boxSize, boxSize/2);
	
	//Icon
	PImage imp = loadImage("Imp.png");
	imp.resize((int)(0.35*boxSize), (int)(0.35*boxSize));
	image(imp, buttonOrigin+(0.31*boxSize), boardOrigin+(6.8*boxSize));
	
	//Text
	fill(lightColor);
	text("Import", buttonOrigin + (0.29 * boxSize), boardOrigin + (7.25 * boxSize));
}

//Method to create export button
void exportButton(){
	//Variables
	float buttonOrigin = boardOrigin + (9.8 * boxSize);
	
	//Setup
	stroke(lightColor);
	textSize(boxSize/8);
	
	//Button
	fill(darkColor);
	rect(buttonOrigin, boardOrigin + (6.8 * boxSize), boxSize, boxSize/2);
	
	//Icon
	PImage exp = loadImage("Exp.png");
	exp.resize((int)(0.35*boxSize), (int)(0.35*boxSize));
	image(exp, buttonOrigin+(0.31*boxSize), boardOrigin+(6.8*boxSize));
	
	//Text
	fill(lightColor);
	text("Export", buttonOrigin + (0.3 * boxSize), boardOrigin + (7.25 * boxSize));
}

//Method called when import file chosen
void importGame(File game) {
	if (game == null) {
		println("Window was closed or the user hit cancel.");
	} else {
		//Handles Game Over and Promotion Menu
		stroke(0);
		fill(0);
		rect(boardOrigin + (8.1*boxSize), boardOrigin, 8*boxSize, 6*boxSize);
		
		//Has Game Object import game in chosen file
		g.importBoardState(game);
		System.out.println("\nImport Successful");
	}
}

//Method called when export file named/chosen
void exportGame(File game) {
	if (game == null){
		println("Window was closed or the user hit cancel.");
	} else {
    	//Exports game to chosen file
    	String path = game.getAbsolutePath();
    	if(!path.substring(path.length()-4).equals(".txt")) path += ".txt";
    	PrintWriter export = createWriter(path);
        export.println(g.gameBoard);
        export.flush();
        export.close();
	}
}

//Method to create New Game button
void restartButton(){
	//Variables
	float buttonOrigin = boardOrigin + (8.5 * boxSize);
	
	//Setup
	stroke(lightColor);
	textSize(boxSize/3);
	
	//Button
	fill(darkColor);
	rect(buttonOrigin, boardOrigin + (7.5 * boxSize), 2.3*boxSize, boxSize/2);
	
	//Text
	fill(lightColor);
	text("New Game", buttonOrigin + (0.3 * boxSize), boardOrigin + (7.85 * boxSize));
}

//Method that draws the 8x8 board
void grid() {
	//Variables
	int end = boardOrigin + (8*boxSize);
	int txtSize = boxSize/6;
	color squareCol;
	char[] fileValues = {'a','b','c','d','e','f','g','h'}; //rows
	char[] rankValues = {'8','7','6','5','4','3','2','1'}; //columns
	
	//Setup
	stroke(gridOutlineColor);
	strokeWeight(boxSize/40);
	textSize(txtSize);
	
	//Creates Grid
	for(int r = 0; r < 8; r++) {
		for(int c = 0; c < 8; c++) {
    		squareCol = (r%2 == c%2) ? lightColor : darkColor; //Switches between lightColor and darkColor
			fill(squareCol);
			rect(boardOrigin + (c*boxSize), boardOrigin + (r*boxSize), boxSize, boxSize); //Creates square
		}
	}
	
	//Selects color for the text parts below
	fill(labelColor); 
	
	//Rank labels
	for(int rank = 7; rank >= 0; rank--){
		text(rankValues[rank], boardOrigin+(txtSize/4), boardOrigin+(rank*boxSize)+txtSize+(txtSize/10));
	}

	//File labels
	for(int file = 0; file < 8; file++){
		switch(file){
    		case 6:
                text(fileValues[file], boardOrigin+((file+1)*boxSize)-(txtSize)+(txtSize/3), end-(txtSize/3)); //Only here because 'g' goes lower than other chars
                break; 
            case 7: case 1: case 3:
                text(fileValues[file], boardOrigin+((file+1)*boxSize)-(txtSize)+(txtSize/4), end-(txtSize/5)); //Only here because 'b','d','h' goes further right than other chars
                break;
            default:
                text(fileValues[file], boardOrigin+((file+1)*boxSize)-(txtSize)+(txtSize/3), end-(txtSize/5));
		}
	}
}

//Creates Game Over notification: Checkmate (with winner) or Stalemate
//void gameOver(boolean checkmate){}

//Method that Highlights the selected piece as well as possible moves and captures
void selectedPieceGUI(){
	//Get selected position, if nothing is selected then exit
	int selectedPos = Game.toPos(selected); 
	GamePiece selectedPiece = g.gameBoard.getPiece(selectedPos);
	if(selectedPiece == null) return;
	
	//Higlights Chosen Piece
	stroke(gridOutlineColor);
    fill(selectedColor);
    rect(boardOrigin + (selected[0] * boxSize), boardOrigin + (selected[1] * boxSize), boxSize, boxSize);
    
    //Outline color for Legal Moves
    stroke(moveOutlineColor);
    
    //Mark all legal moves of the selected piece
    for(int pos : g.gameBoard.possibleMoves.get(selectedPiece)){
        //X,Y of the move's ending square
        int[] moveTo = Game.toXY(pos%64);
        
        //Normal Capture
        if(g.gameBoard.board.get(pos) != null){
        	fill(captureColor);
			rect(boardOrigin + (moveTo[0] * boxSize), boardOrigin + (moveTo[1] * boxSize), boxSize, boxSize);
        } 
        
        //En Passant
        else if(g.gameBoard.board.get(-1) != null && selectedPiece != g.gameBoard.board.get(-1) && pos-g.gameBoard.board.get(-1).getPos() == (selectedPiece.isWhite()? -1 : 1)){
        	fill(captureColor);
            rect(boardOrigin + (moveTo[0] * boxSize), boardOrigin + (moveTo[1] * boxSize), boxSize, boxSize);
            fill(circleColor);
            circle(boardOrigin + moveTo[0]*boxSize + boxSize/2, boardOrigin + moveTo[1]*boxSize + boxSize/2, boxSize/10);
        }

        //Normal Move
        else {       
            fill(circleColor);
            circle(boardOrigin + moveTo[0]*boxSize + boxSize/2, boardOrigin + moveTo[1]*boxSize + boxSize/2, boxSize/10);
            //System.out.println("Normal: " + moveTo[0] + "," + moveTo[1]);
        }
    }
}

//Method to create menu to choose piece for pawn promotion
void promoteMenu(boolean pieceColor){
  //Variables
  PImage piece = null;
  float gap = boardOrigin + (8.1*boxSize);
  //QKRB
  //Setup
  stroke(100);
  strokeWeight(boxSize/40);
  fill(lightColor);
 
  //Queen
  rect(gap, boardOrigin, boxSize, boxSize); //Box 1
  piece = loadImage(pieceColor ? "WhiteQueen.png" : "BlackQueen.png");
  piece.resize(boxSize, boxSize);
  image(piece, gap,  boardOrigin);
  
  //Rook
  rect(gap, boardOrigin + (2*boxSize), boxSize, boxSize); //Box 3 
  piece = loadImage(pieceColor ? "WhiteRook.png" : "BlackRook.png");
  piece.resize(boxSize, boxSize);
  image(piece, gap,  boardOrigin + (2*boxSize));
  
  //Change Colors
  fill(darkColor);
  
  //Knight
  rect(gap, boardOrigin + (1*boxSize), boxSize, boxSize); //Box 2
  piece = loadImage(pieceColor ? "WhiteKnight.png" : "BlackKnight.png");
  piece.resize(boxSize, boxSize);
  image(piece, gap,  boardOrigin + (1*boxSize));
  
  //Bishop
  rect(gap, boardOrigin + (3*boxSize), boxSize, boxSize); //Box 4
  piece = loadImage(pieceColor ? "WhiteBishop.png" : "BlackBishop.png");
  piece.resize(boxSize, boxSize);
  image(piece, gap,  boardOrigin + (3*boxSize));
}

//Method to put all pieces on the board
void drawPieces(){	
	for(GamePiece p : g.gameBoard.board.values()){
    		if(p == g.gameBoard.board.get((g.gameBoard.turn() ? -2 : -3)) && g.inCheck){
        		stroke(gridOutlineColor);
    			strokeWeight(boxSize/40);
        		fill(checkColor);
            	rect(boardOrigin + (p.getX() * boxSize), boardOrigin + (p.getY() * boxSize), boxSize, boxSize);
    		}
    
        	PImage piece = loadImage(p.getImg());
            piece.resize(boxSize, boxSize);
            image(piece, boardOrigin + (p.getX() * boxSize), boardOrigin + (p.getY() * boxSize));
	}	
}

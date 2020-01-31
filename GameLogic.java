/**
 * Contains the main logic part of the game, as it processes.
 *
 * @author : The unnamed tutor.
 */
public class GameLogic {
	
	private Map map;
	private HumanPlayer human;
	private BotPlayer bot;
	private int[] playerPosition;
	private int[] collectedGold;
	private boolean active;
	
	// who's turn is it?
	private int whatPlayer;
	private static final int HUMAN_PLAYER = 0;
	private static final int BOT_PLAYER = 1;
	
	public GameLogic(){
		map = new Map();
		map.readMap("maps/example_map.txt");
		// playerPosition[0] and playerPosition[1] are human players x and y coordinates
		// playerPosition[2] and playerPosition[3] are bot players x and y coordinates
		// create a new human player and place them randomly on the map
    	human = new HumanPlayer();
    	// create a new bot player and place it randomly on the map
    	bot = new BotPlayer();
    	// initial player positions on the map
    	playerPosition = new int[4];
		playerPosition[0] = 3;
		playerPosition[1] = 5;
		playerPosition[2] = 3;
		playerPosition[3] = 6;
		// collectedGold[0] is human players total collected gold
		// collectedGold[1] is bot players total collected gold
		collectedGold = new int[2];
		collectedGold[0] = 0;
		collectedGold[1] = 0;
	}
	
	// Creates a simple game between a human player and a bot
	public void playGame(){
    	// play game until someone wins
    	active = true;
    	while(gameRunning()){
    		whatPlayer = HUMAN_PLAYER;
    		String humanAction = human.getNextAction();
    		System.out.println(processCommand(humanAction));
    		whatPlayer = BOT_PLAYER;
    		String botAction = bot.getNextAction();
    		System.out.println(processCommand(botAction));
    	}
	}
		
	/*
	 *   Helper methods for when switching between human player and bot player
	 */
	
    // get current players total of collected gold
    private int getPlayersCollectedGold(){
    	return (whatPlayer == HUMAN_PLAYER) ? collectedGold[0] : collectedGold[1];
    }
    
    // increment current players total of collected gold
    private void incrementPlayersCollectedGold(){
    	if(whatPlayer == HUMAN_PLAYER){
    		collectedGold[0] = collectedGold[0] + 1;
    	}
    	else{
    		collectedGold[1] = collectedGold[1] + 1;
    	}
    }
    
    // get current players x coordinate
    private int getPlayersXCoordinate(){
    	return (whatPlayer == HUMAN_PLAYER) ? playerPosition[0] : playerPosition[2];
    }
    
    // set current players x coordinate
    private void setPlayersXCoordinate(int newX){
    	if(whatPlayer == HUMAN_PLAYER){ 
    		playerPosition[0] = newX;
    	}
    	else{
    		playerPosition[2] = newX;
    	}
    }
    
    // get current opponent x coordinate
    private int getOpponentXCoordinate(){
    	return (whatPlayer == HUMAN_PLAYER) ? playerPosition[2] : playerPosition[0];
    }
    
    // get current players y coordinate
    private int getPlayersYCoordinate(){
    	return (whatPlayer == HUMAN_PLAYER) ? playerPosition[1] : playerPosition[3];
    }
 
    // set current players y coordinate
    private void setPlayersYCoordinate(int newY){
    	if(whatPlayer == HUMAN_PLAYER){ 
    		playerPosition[1] = newY;
    	}
    	else{
    		playerPosition[3] = newY;
    	}
    }
    
    // get current opponent y coordinate
    private int getOpponentYCoordinate(){
    	return (whatPlayer == HUMAN_PLAYER) ? playerPosition[3] : playerPosition[1];
    }
    
    // current players icon
    private char getPlayersIcon(){
    	return (whatPlayer == HUMAN_PLAYER) ? 'P' : 'B';
    }
    
    // opponent icon
    private char getOpponentIcon(){
    	return (whatPlayer == HUMAN_PLAYER) ? 'B' : 'P';
    }
	
	/**
     * Processes the command. It should return a reply in form of a String, as the protocol dictates.
     * Otherwise it should return the string "Invalid".
     *
     * @param command : Input entered by the user.
     * @return : Processed output or Invalid if the @param command is wrong.
     */
    private String processCommand(String action) {
    	String [] command = action.trim().split(" ");
		String answer = "FAIL";
		
		switch (command[0].toUpperCase()){
		case "HELLO":
			answer = hello();
			break;
		case "MOVE":
			if (command.length == 2 ){
				answer = move(command[1].toUpperCase().charAt(0));
			}
			break;
		case "PICKUP":
			answer = pickup();
			break;
		case "LOOK":
			answer = look();
			break;
		case "QUIT":
			quitGame();
		default:
			answer = "FAIL";
		}
		
		return answer;
    }

    /**
     * @return if the game is running.
     */
    private boolean gameRunning() {
        return active;
    }

    /**
     * @return : Returns back gold player requires to exit the Dungeon.
     */
    private String hello() {
        return "GOLD: " + (map.getGoldToWin() - getPlayersCollectedGold());
    }

    /**
     * Checks if movement is legal and updates player's location on the map.
     *
     * @param direction : The direction of the movement.
     * @return : Protocol if success or not.
     */
    protected String move(char direction) {
    	int newX = getPlayersXCoordinate();
    	int newY = getPlayersYCoordinate();
		switch (direction){
		case 'N':
			newY -=1;
			break;
		case 'E':
			newX +=1;
			break;
		case 'S':
			newY +=1;
			break;
		case 'W':
			newX -=1;
			break;
		default:
			break;
		}
		
		// check if the player can move to that tile on the map
		int xDistance = newX - getOpponentXCoordinate();
    	int yDistance = newY - getOpponentYCoordinate();
		if(xDistance == 0 && yDistance == 0){
			return "FAIL";
		}
		else if(map.getTile(newX, newY) != '#'){
			//System.out.println("moved from " + getPlayersXCoordinate() + ", " + getPlayersYCoordinate());
			setPlayersXCoordinate(newX);
			setPlayersYCoordinate(newY);
			//System.out.println("moved to " + getPlayersXCoordinate() + ", " + getPlayersYCoordinate());
			if (checkWin()){
				quitGame();
			}
			return "SUCCESS";
		} 
		else {
			return "FAIL";
		}
    }

    /**
     * Converts the map from a 2D char array to a single string.
     *
     * @return : A String representation of the game map.
     */
    private String look() {
    	// get look window for current player
    	char[][] look = map.look(getPlayersXCoordinate(), getPlayersYCoordinate());
    	// add current player's icon to look window
    	look[2][2] = getPlayersIcon();
    	// is opponent visible? if they are then add them to the look window
    	int xDistance =  getPlayersXCoordinate() - getOpponentXCoordinate();
    	int yDistance = getPlayersYCoordinate() - getOpponentYCoordinate();
    	if(xDistance <= 2 && xDistance >= -2 && yDistance <= 2 && yDistance >= -2){
    		look[2-xDistance][2-yDistance] = getOpponentIcon();
    	}
    	// return look window as a String for printing
    	String lookWindow = "";
    	for(int i=0; i<look.length; i++){
    		for(int j=0; j<look[i].length; j++){
    			lookWindow += look[j][i];
    		}
    		lookWindow += "\n";
    	}
        return lookWindow;
    }

    /**
     * Processes the player's pickup command, updating the map and the player's gold amount.
     *
     * @return If the player successfully picked-up gold or not.
     */
    protected String pickup() {
    	if (map.getTile(getPlayersXCoordinate(), getPlayersYCoordinate()) == 'G') {
    		incrementPlayersCollectedGold();
			map.replaceTile(getPlayersXCoordinate(), getPlayersYCoordinate(), '.');
			return "SUCCESS, GOLD COINS: " + getPlayersCollectedGold();
		}

		return "FAIL" + "\n" + "There is nothing to pick up...";
    }

    /**
	 * checks if the player collected all GOLD and is on the exit tile
	 * @return True if all conditions are met, false otherwise
	 */
	protected boolean checkWin() {
		if (getPlayersCollectedGold() >= map.getGoldToWin() && 
			map.getTile(getPlayersXCoordinate(), getPlayersYCoordinate()) == 'E') {
			System.out.println("Congratulations!!! \n You have escaped the Dungeon of Dooom!!!!!! \n"
					+ "Thank you for playing!");
			return true;
		}
		return false;
	}

	/**
	 * Quits the game when called
	 */
	public void quitGame() {
		System.out.println("The game will now exit");
		active = false;
	}
    
    public static void main(String[] args) {
        GameLogic game = new GameLogic();
        game.playGame();
    }
}
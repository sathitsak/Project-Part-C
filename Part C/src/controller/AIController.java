package controller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.HealthTrap;
import tiles.LavaTrap;
import tiles.MapTile;
import tiles.MapTile.Type;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.World;
import world.WorldSpatial;

public class AIController extends CarController {
	
	// How many minimum units the wall is away from the player.
	private int wallSensitivity =2;
	///////////////////////////////////////////////////////////////////////////////
	ArrayList<Coordinate> keyTile = new ArrayList<Coordinate>(); //NOT Use will be remove soon
	ArrayList<Coordinate> healTile = new ArrayList<Coordinate>();
	ArrayList<Coordinate> visitedTile = new ArrayList<Coordinate>();
	ArrayList<TestTileCollector> tileCollectorArrayList = new ArrayList<TestTileCollector>();
	ArrayList<TestTileCollector> keyCollectorArrayList = new ArrayList<TestTileCollector>();
	int totalKey = getKey();
	int mapHeight = World.MAP_HEIGHT;
	int mapWidth = World.MAP_WIDTH;
	int totalTile = mapHeight*mapWidth;
	float currentHealth = getHealth();
	
	
	
	
	///////////////////////////////////////////////////////////////////////////////
	
	private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
	private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
	private boolean isTurningLeft = false;
	private boolean isTurningRight = false; 
	private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
	
	// Car Speed to move at
	private final float CAR_SPEED = 3;
	
	// Offset used to differentiate between 0 and 360 degrees
	private int EAST_THRESHOLD = 3;
	
	public AIController(Car car) {
		super(car);
	}
	
	Coordinate initialGuess;
	boolean notSouth = true;
	@Override
	public void update(float delta) {
		
		// Gets what the car can see
		HashMap<Coordinate, MapTile> currentView = getView();
		
		
		checkStateChange();

////////WHAT I ADDED/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
		Coordinate currentPosition = new Coordinate(getPosition());
		System.out.print("Total Tile in MAP"+totalTile);
		System.out.print("MAP H "+mapHeight);
		System.out.print("MAP W"+mapWidth);
		
		if(searchForDuplicateCoordinate(visitedTile, currentPosition) == false) {
			visitedTile.add(currentPosition);
			
			System.out.print("visitedTile"+visitedTile);
			
		}
		if(sameTile(visitedTile,currentPosition) ==true) {
			System.out.println("THIS IS SAME TILE!!!!!!!!!!!!!!!!!!!!!!!!!");

			
		}
		recordTileTypeAroundTheCar(currentView,currentPosition);
		System.out.print("GET KEY TEST"+totalKey);
		
		if(haveAllKeyLocation() == true) {
			System.out.println("YOU GOT ALL KEY LOCATION!!!!!!!");			
			
		}
		if(haveOneHealTile() == true) {
			//System.out.println("YOU GOT ONE HEAL LOCATION!!!!!!!");			
			
		}
		
////////WHAT I ADDED ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// If you are not following a wall initially, find a wall to stick to!
		if(!isFollowingWall){
			if(getSpeed() < CAR_SPEED){
				applyForwardAcceleration();
				System.out.println("CURRENT1"+getHealth());
				
			}
			// Turn towards the north
			if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				applyLeftTurn(getOrientation(),delta);
			}
			if(checkNorth(currentView)){
				// Turn right until we go back to east!
				if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					applyRightTurn(getOrientation(),delta);
				}
				else{
					isFollowingWall = true;
				}
			}
		}
		// Once the car is already stuck to a wall, apply the following logic
		else{
			
			// Readjust the car if it is misaligned.
			
			readjust(lastTurnDirection,delta);
			
			if(isTurningRight){
				applyRightTurn(getOrientation(),delta);
			}
			else if(isTurningLeft){
				// Apply the left turn if you are not currently near a wall.
				if(!checkFollowingWall(getOrientation(),currentView)){
					applyLeftTurn(getOrientation(),delta);
				}
				else{
					isTurningLeft = false;
				}
			}
			// Try to determine whether or not the car is next to a wall.
			else if(checkFollowingWall(getOrientation(),currentView)){
				// Maintain some velocity
				if(getSpeed() < CAR_SPEED){
					applyForwardAcceleration();
				}
				// If there is wall ahead, turn right!
				if(checkWallAhead(getOrientation(),currentView)){
					lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
					isTurningRight = true;				
					
				}

			}
			// This indicates that I can do a left turn if I am not turning right
			else{
				lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
				isTurningLeft = true;
			}
		}
		System.out.println("CURREN3"+getHealth());
		

	}
	
	/**
	 * Readjust the car to the orientation we are in.
	 * @param lastTurnDirection
	 * @param delta
	 */
	////////////////////////////////////////////////////////////////////////////////////////////
	public boolean haveAllKeyLocation() {
		if (keyCollectorArrayList.size() == totalKey-1 )
		{	
			return true;
		}
			return false;		
	}
	
	public boolean haveOneHealTile() {
		if (healTile.size()>0) {
			return true;
		}
		return false;
	}
	
	public boolean sameTile(ArrayList<Coordinate> collection,Coordinate current) {
		for(int i=0;i<collection.size();i++) {
		if(collection.get(i) == current) {
			System.out.println("SAME TILE");
			return true;
		}
			
		}
		
		return false;
		
	}
	
	public void addtilePosition(Coordinate currentPosition, ArrayList<Coordinate> collection) {
		collection.add(currentPosition);
	}
	
	public boolean landOnLavaTileWithKey(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.TRAP == currentType){
			
			if(((TrapTile) currentTile).getTrap()=="lava"){
				
				TrapTile a = (TrapTile) currentTile;
				LavaTrap b = (LavaTrap) a;
				
				if(b.getKey() > 0) {
					//System.out.println("GET KEY "+b.getKey());
				return true;}
			}
		}return false;
	}
	
	public int getKeyNum(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		MapTile currentTile = currentView.get(currentPosition);	
		TrapTile a = (TrapTile) currentTile;
		LavaTrap b = (LavaTrap) a;
		return b.getKey();
		
	}
	public boolean landOnHealTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.TRAP == currentType){
			
			if(((TrapTile) currentTile).getTrap()=="health"){
				
				return true;
			}
		}return false;
	}
	
	public void printOutArrayListMember(ArrayList<Coordinate> arrayList) {
		for(int i = 0; i<arrayList.size(); i++) {
			System.out.println("The"+i+"of arrayList is"+arrayList.get(i));
		}
		
	}
	public boolean searchForDuplicateCoordinate(ArrayList<Coordinate> arrayList, Coordinate coordinate) {
		for(int i=0; i<arrayList.size();i++) {
			Coordinate coo = arrayList.get(i);
			;
			if(coordinate.equals(coo)) {
			
				return true;
			}
		}
		
		//System.out.println("not dup");
		return false;
	}
	
	public void recordTileTypeAroundTheCar(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		//Quadrant 1
		//Scan every possible tile in Q1 and collect their Type and location to TileCollector
				for(int y=4; y>-1;y--) {
					for(int x=4; x>-1;x--) {
						MapTile scanTile = currentView.get(new Coordinate(currentPosition.x-x, currentPosition.y+y));				
						Coordinate scanCoo = new Coordinate(currentPosition.x-x, currentPosition.y+y);
		//If found tile with key store in special TileCollector				
						if(landOnLavaTileWithKey(currentView,scanCoo)) {	
							
							if(searchForDuplicateCoordinate(keyTile, scanCoo) == false) {
									keyTile.add(scanCoo);
								//	System.out.println("KEY TILE"+keyTile);
									int keyNum = getKeyNum(currentView, scanCoo);
									TestTileCollector keyTC = new TestTileCollector(scanCoo,keyNum);
									keyCollectorArrayList.add(keyTC);
									System.out.println("KEY TILE"+keyTC.getCoordinate()+"KEY NUM"+keyTC.getKeyNum());
									
								}
								
							
						}
						
		// For healTile we just going to remember it location so we use Coordinator				
						if(landOnHealTile(currentView,scanCoo)) {
							if(searchForDuplicateCoordinate(healTile, scanCoo) == false) {
								healTile.add(scanCoo);
								//System.out.println("Heal TILE"+healTile);
							}
							
						}
		// Record every Tile				
						MapTile.Type scanType = scanTile.getType();
						TestTileCollector tctile = new TestTileCollector(scanCoo,scanType);
						
						tileCollectorArrayList.add(tctile);
						
					}
				}
		//Quadrant 2
		for(int y=4; y>-1;y--) {
			for(int x=0; x<5;x++) {
				MapTile scanTile = currentView.get(new Coordinate(currentPosition.x+x, currentPosition.y+y));				
				Coordinate scanCoo = new Coordinate(currentPosition.x+x, currentPosition.y+y);
				
				if(landOnLavaTileWithKey(currentView,scanCoo)) {	
					
					if(searchForDuplicateCoordinate(keyTile, scanCoo) == false) {
							//keyTile.add(scanCoo);
							//System.out.println("KEY TILE"+keyTile);
						keyTile.add(scanCoo);
						//	System.out.println("KEY TILE"+keyTile);
							int keyNum = getKeyNum(currentView, scanCoo);
							TestTileCollector keyTC = new TestTileCollector(scanCoo,keyNum);
							keyCollectorArrayList.add(keyTC);
							System.out.println("KEY TILE"+keyTC.getCoordinate()+"KEY NUM"+keyTC.getKeyNum());
						}
						
					
				}
				
				if(landOnHealTile(currentView,scanCoo)) {
					if(searchForDuplicateCoordinate(healTile, scanCoo) == false) {
						healTile.add(scanCoo);
						//System.out.println("Heal TILE"+healTile);
					}
					
				}
				
				MapTile.Type scanType = scanTile.getType();
				TestTileCollector tctile = new TestTileCollector(scanCoo,scanType);
				
				tileCollectorArrayList.add(tctile);
				
				
				
			}
		}
		//Quadrant 3
				for(int y=0; y<5;y++) {
					for(int x=4; x>-1;x--) {
						MapTile scanTile = currentView.get(new Coordinate(currentPosition.x-x, currentPosition.y-y));				
						Coordinate scanCoo = new Coordinate(currentPosition.x-x, currentPosition.y-y);
						
						if(landOnLavaTileWithKey(currentView,scanCoo)) {	
							
							if(searchForDuplicateCoordinate(keyTile, scanCoo) == false) {
									//keyTile.add(scanCoo);
									//System.out.println("KEY TILE"+keyTile);
								keyTile.add(scanCoo);
								//	System.out.println("KEY TILE"+keyTile);
									int keyNum = getKeyNum(currentView, scanCoo);
									TestTileCollector keyTC = new TestTileCollector(scanCoo,keyNum);
									keyCollectorArrayList.add(keyTC);
									System.out.println("KEY TILE"+keyTC.getCoordinate()+"KEY NUM"+keyTC.getKeyNum());
								}
								
							
						}
						
						
						if(landOnHealTile(currentView,scanCoo)) {
							if(searchForDuplicateCoordinate(healTile, scanCoo) == false) {
								healTile.add(scanCoo);
								//System.out.println("Heal TILE"+healTile);
							}
							
						}
						
						MapTile.Type scanType = scanTile.getType();
						TestTileCollector tctile = new TestTileCollector(scanCoo,scanType);
						//System.out.println("tile coordinate"+tctile.getCoordinate()+"tile type"+tctile.getType());
						tileCollectorArrayList.add(tctile);
						
						
						
					}
				}
		//Quadrant 4
		for(int y=4; y>-1;y--) {
			for(int x=0; x<5;x++) {
				MapTile scanTile = currentView.get(new Coordinate(currentPosition.x+x, currentPosition.y-y));				
				Coordinate scanCoo = new Coordinate(currentPosition.x+x, currentPosition.y-y);
				
					if(landOnLavaTileWithKey(currentView,scanCoo)) {	
					
					if(searchForDuplicateCoordinate(keyTile, scanCoo) == false) {
							//keyTile.add(scanCoo);
							//System.out.println("KEY TILE"+keyTile);
						keyTile.add(scanCoo);
						//	System.out.println("KEY TILE"+keyTile);
							int keyNum = getKeyNum(currentView, scanCoo);
							TestTileCollector keyTC = new TestTileCollector(scanCoo,keyNum);
							keyCollectorArrayList.add(keyTC);
							System.out.println("KEY TILE"+keyTC.getCoordinate()+"KEY NUM"+keyTC.getKeyNum());
						}
						
					
				}
				
				
				if(landOnHealTile(currentView,scanCoo)) {
					if(searchForDuplicateCoordinate(healTile, scanCoo) == false) {
						healTile.add(scanCoo);
						//System.out.println("Heal TILE"+healTile);
					}
					
				}
				MapTile.Type scanType = scanTile.getType();
				TestTileCollector tctile = new TestTileCollector(scanCoo,scanType);			
				tileCollectorArrayList.add(tctile);
						
						
						
									}}
							}	
	////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	private void readjust(WorldSpatial.RelativeDirection lastTurnDirection, float delta) {
		if(lastTurnDirection != null){
			if(!isTurningRight && lastTurnDirection.equals(WorldSpatial.RelativeDirection.RIGHT)){
				adjustRight(getOrientation(),delta);
			}
			else if(!isTurningLeft && lastTurnDirection.equals(WorldSpatial.RelativeDirection.LEFT)){
				adjustLeft(getOrientation(),delta);
			}
		}
		
	}
	
	/**
	 * Try to orient myself to a degree that I was supposed to be at if I am
	 * misaligned.
	 */
	private void adjustLeft(WorldSpatial.Direction orientation, float delta) {
		
		switch(orientation){
		case EAST:
			if(getAngle() > WorldSpatial.EAST_DEGREE_MIN+EAST_THRESHOLD){
				turnRight(delta);
			}
			break;
		case NORTH:
			if(getAngle() > WorldSpatial.NORTH_DEGREE){
				turnRight(delta);
			}
			break;
		case SOUTH:
			if(getAngle() > WorldSpatial.SOUTH_DEGREE){
				turnRight(delta);
			}
			break;
		case WEST:
			if(getAngle() > WorldSpatial.WEST_DEGREE){
				turnRight(delta);
			}
			break;
			
		default:
			break;
		}
		
	}

	private void adjustRight(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(getAngle() > WorldSpatial.SOUTH_DEGREE && getAngle() < WorldSpatial.EAST_DEGREE_MAX){
				turnLeft(delta);
			}
			break;
		case NORTH:
			if(getAngle() < WorldSpatial.NORTH_DEGREE){
				turnLeft(delta);
			}
			break;
		case SOUTH:
			if(getAngle() < WorldSpatial.SOUTH_DEGREE){
				turnLeft(delta);
			}
			break;
		case WEST:
			if(getAngle() < WorldSpatial.WEST_DEGREE){
				turnLeft(delta);
			}
			break;
			
		default:
			break;
		}
		
	}
	
	/**
	 * Checks whether the car's state has changed or not, stops turning if it
	 *  already has.
	 */
	private void checkStateChange() {
		if(previousState == null){
			previousState = getOrientation();
		}
		else{
			if(previousState != getOrientation()){
				if(isTurningLeft){
					isTurningLeft = false;
				}
				if(isTurningRight){
					isTurningRight = false;
				}
				previousState = getOrientation();
			}
		}
	}
	
	/**
	 * Turn the car counter clock wise (think of a compass going counter clock-wise)
	 */
	private void applyLeftTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
				turnLeft(delta);
			}
			break;
		case NORTH:
			if(!getOrientation().equals(WorldSpatial.Direction.WEST)){
				turnLeft(delta);
			}
			break;
		case SOUTH:
			if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
				turnLeft(delta);
			}
			break;
		case WEST:
			if(!getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				turnLeft(delta);
			}
			break;
		default:
			break;
		
		}
		
	}
	
	/**
	 * Turn the car clock wise (think of a compass going clock-wise)
	 */
	private void applyRightTurn(WorldSpatial.Direction orientation, float delta) {
		switch(orientation){
		case EAST:
			if(!getOrientation().equals(WorldSpatial.Direction.SOUTH)){
				turnRight(delta);
			}
			break;
		case NORTH:
			if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
				turnRight(delta);
			}
			break;
		case SOUTH:
			if(!getOrientation().equals(WorldSpatial.Direction.WEST)){
				turnRight(delta);
			}
			break;
		case WEST:
			if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
				turnRight(delta);
			}
			break;
		default:
			break;
		
		}
		
	}

	/**
	 * Check if you have a wall in front of you!
	 * @param orientation the orientation we are in based on WorldSpatial
	 * @param currentView what the car can currently see
	 * @return
	 */
	private boolean checkWallAhead(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView){
		switch(orientation){
		case EAST:
			return checkEast(currentView);
		case NORTH:
			return checkNorth(currentView);
		case SOUTH:
			return checkSouth(currentView);
		case WEST:
			return checkWest(currentView);
		default:
			return false;
		
		}
	}
	
	/**
	 * Check if the wall is on your left hand side given your orientation
	 * @param orientation
	 * @param currentView
	 * @return
	 */
	private boolean checkFollowingWall(WorldSpatial.Direction orientation, HashMap<Coordinate, MapTile> currentView) {
		
		switch(orientation){
		case EAST:
			return checkNorth(currentView);
		case NORTH:
			return checkWest(currentView);
		case SOUTH:
			return checkEast(currentView);
		case WEST:
			return checkSouth(currentView);
		default:
			return false;
		}
		
	}
	

	/**
	 * Method below just iterates through the list and check in the correct coordinates.
	 * i.e. Given your current position is 10,10
	 * checkEast will check up to wallSensitivity amount of tiles to the right.
	 * checkWest will check up to wallSensitivity amount of tiles to the left.
	 * checkNorth will check up to wallSensitivity amount of tiles to the top.
	 * checkSouth will check up to wallSensitivity amount of tiles below.
	 */
	
	
	
	public boolean checkHealthTrap(HashMap<Coordinate, MapTile> currentView) {
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.TRAP)){
				
				return true;
			}
		}
		return false;
	}
	
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
		// Check tiles to my right
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to my left
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles to towards the top
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
		// Check tiles towards the bottom
		Coordinate currentPosition = new Coordinate(getPosition());
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
}

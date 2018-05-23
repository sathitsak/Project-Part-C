package mycontroller;

import controller.CarController;
import controller.TestTileCollector;
import world.Car;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
public class MyAIController extends CarController{
	// How many minimum units the wall is away from the player.
		private int wallSensitivity = 2;
		
		
		HashMap<Coordinate, MapTile> maze = new HashMap<Coordinate, MapTile>();
		HashMap<Coordinate, Integer> KeyMap = new HashMap<Coordinate, Integer>();
		ArrayList<Coordinate> HealMap = new ArrayList<Coordinate>();
		
		ArrayList<Coordinate> keyTile = new ArrayList<Coordinate>(); //NOT Use will be remove soon
		ArrayList<Coordinate> healTile = new ArrayList<Coordinate>();
		ArrayList<Coordinate> visitedTile = new ArrayList<Coordinate>();
		ArrayList<TileCollector> tileCollectorArrayList = new ArrayList<TileCollector>();
		ArrayList<TileCollector> keyCollectorArrayList = new ArrayList<TileCollector>();
		int totalKey = getKey();
//		int totalTile = 
		float currentHealth = getHealth();
		
		private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
		private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
		private boolean isTurningLeft = false;
		private boolean isTurningRight = false; 
		private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
		
		Path testPath;
		private PathFinder finder;
		
		// Car Speed to move at
		private final float CAR_SPEED = 1;
		Coordinate currentPosition;
		
		// Offset used to differentiate between 0 and 360 degrees
		private int EAST_THRESHOLD = 3;
		private static final int SensorLimit = 4;
		private static final int NoChange = 0;
		boolean FinishPath = true;
		
		public MyAIController(Car car) {
			super(car);
		}
		
		Coordinate initialGuess;
		boolean notSouth = true;
		
		@Override
		public void update(float delta) {
			currentPosition = new Coordinate(getPosition());
			
			// Gets what the car can see
			HashMap<Coordinate, MapTile> currentView = getView();
			
			//Read current view into internal map
			Iterator it = currentView.entrySet().iterator();
			while(it.hasNext()) {
				Map.Entry tile = (Map.Entry)it.next();
				if(!maze.containsKey(tile.getKey())) {
					MapTile mt = (MapTile)tile.getValue();
					Coordinate Coord = (Coordinate)tile.getKey();
					maze.put(Coord, mt);
					
					//Add key to key hashmap if it exists
					if(ContainsKey(mt) != 0) {
						KeyMap.put(Coord, ContainsKey(mt));
					}
					if(ContainsHeal(mt)) {
						HealMap.add(Coord);
					}
					
				}
			}
			
			
			
			printMaze();
			
			if(FinishPath) {
				finder = new AStarPathFinder(maze, 500, false);
				testPath = finder.findPath(maze, 2, 3, 3, 5);
				FinishPath = false;
			}



			
			
			
			int i = 0, j = 1;
			
			while(i < testPath.getLength()) {
				System.out.println(testPath.getX(i) + "," + testPath.getY(i));
				i++;
			}
			

				while(	j < testPath.getLength()
						&& testPath.getX(j) <= testPath.getX(0) + SensorLimit
						&& testPath.getX(j) >= testPath.getX(0) - SensorLimit
						&& testPath.getY(j) <= testPath.getY(0) + SensorLimit
						&& testPath.getY(j) >= testPath.getY(0) - SensorLimit) 
				{
					
					
					
					Coordinate nextStep = FollowStep(testPath, j, currentPosition);
					System.out.println(FollowStep(testPath, j, currentPosition));
					
					//If Y changes
					if(nextStep.y != NoChange) {
						//If its northwards
						if(nextStep.y > NoChange) {
							//If not facing north, face north and drive
							if(!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
								lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
								applyLeftTurn(getOrientation(),delta);
							}
						}		
					}
					//If X changes
					else if(nextStep.x != NoChange) {
						//If its eastwards
						if(nextStep.x > NoChange) {
							//If not facing East, face east and drive
							if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
								lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
								applyRightTurn(getOrientation(),delta);
							}
						}
					}
	
					applyForwardAcceleration();
					
					
					j++;
				}
			
			System.out.println("KICKED");
			
			
			
//			checkStateChange();
//
//			// If you are not following a wall initially, find a wall to stick to!
//			if(!isFollowingWall){
//				if(getSpeed() < CAR_SPEED){
//					applyForwardAcceleration();
//				}
//				// Turn towards the north
//				if(!getOrientation().equals(WorldSpatial.Direction.NORTH)){
//					lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
//					applyLeftTurn(getOrientation(),delta);
//				}
//				if(checkNorth(currentView)){
//					// Turn right until we go back to east!
//					if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
//						lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
//						applyRightTurn(getOrientation(),delta);
//					}
//					else{
//						isFollowingWall = true;
//					}
//				}
//			}
//			// Once the car is already stuck to a wall, apply the following logic
//			else{
//				
//				// Readjust the car if it is misaligned.
//				readjust(lastTurnDirection,delta);
//				
//				if(isTurningRight){
//					applyRightTurn(getOrientation(),delta);
//				}
//				else if(isTurningLeft){
//					// Apply the left turn if you are not currently near a wall.
//					if(!checkFollowingWall(getOrientation(),currentView)){
//						applyLeftTurn(getOrientation(),delta);
//					}
//					else{
//						isTurningLeft = false;
//					}
//				}
//				// Try to determine whether or not the car is next to a wall.
//				else if(checkFollowingWall(getOrientation(),currentView)){
//					// Maintain some velocity
//					if(getSpeed() < CAR_SPEED){
//						applyForwardAcceleration();
//					}
//					// If there is wall ahead, turn right!
//					if(checkWallAhead(getOrientation(),currentView)){
//						lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
//						isTurningRight = true;				
//						
//					}
//
//				}
//				// This indicates that I can do a left turn if I am not turning right
//				else{
//					lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
//					isTurningLeft = true;
//				}
//			}
//			
//			

		}
		
		
		
		/**
		 * Readjust the car to the orientation we are in.
		 * @param lastTurnDirection
		 * @param delta
		 */
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
		
		
		public void printMaze() {
			Iterator it = this.maze.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry tile = (Map.Entry)it.next();
				MapTile mt = (MapTile) tile.getValue();
				System.out.print("Coords: " + (Coordinate)tile.getKey() +" , TileType: " + mt.getType() + " ");
				if(mt.getType().toString().equals("TRAP")) {
					TrapTile tt = (TrapTile) mt;
					tt.getTrap();
					System.out.print("Trap type: " + tt.getTrap() + " ");
					if(tt.getTrap().equals("lava")) {
						LavaTrap lt = (LavaTrap) tt;
						if(lt.getKey() != 0) {
							System.out.print("Contains Key: " + lt.getKey());
						}
					}
				}
				System.out.print("\n");
				
//				it.remove();
			}
			
		}
		
		
		//Check if MapTile contains key
		public int ContainsKey(MapTile mt) {
			if(mt.getType().toString().equals("TRAP")) {
				TrapTile tt = (TrapTile) mt;
				if(tt.getTrap().equals("lava")) {
					LavaTrap lt = (LavaTrap) tt;
					if(lt.getKey() != 0) {
						return getKey();
					}
				}
			}
			return 0;
		}
		
		//Check if heal tile
		public boolean ContainsHeal(MapTile mt) {
			if(mt.getType().toString().equals("TRAP")) {
				TrapTile tt = (TrapTile) mt;
				if(tt.getTrap().equals("health")) {
					return true;
				}
			}
			
			return false;
		}
		
		public HashMap<Coordinate, MapTile> getMap() {
			return maze;
		}
		
		//Check if solid tile
		public boolean IsBlocked(int x, int y) {
			Coordinate coord = new Coordinate(x, y);
			MapTile mt = null;
			if(maze.containsKey(coord)) {
				mt = maze.get(coord);
			}
			else {
				return false;
			}
			if(mt.getType().toString().equals("WALL") || mt.getType().toString().equals("EMPTY")) {
				return true;
			}
			
			return false;
		}
		
//		returns coordinate to know movement distance
		public Coordinate FollowStep(Path path, int index, Coordinate currentPosition) {
			Coordinate nextMove = new Coordinate(path.getX(index) - currentPosition.x, 
					path.getY(index) - currentPosition.y);
			return nextMove;
		}

}

package mycontroller;

import controller.CarController;
import controller.TestTileCollector;
import world.Car;
import world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.WorldSpatial;
public class MyAIController extends CarController{
	// How many minimum units the wall is away from the player.
//		private int wallSensitivity = 2;
		
		
		private HashMap<Coordinate, MapTile> maze = new HashMap<Coordinate, MapTile>();
		private HashMap<Coordinate, Integer> KeyMap = new HashMap<Coordinate, Integer>();
		private ArrayList<Coordinate> HealMap = new ArrayList<Coordinate>();
		
		
		private int destinationX= 4;
		private int destinationY= 4;
		private int totalKey = getKey();
		
		
		private float currentHealth = getHealth();
//		private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
		private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
		private boolean isTurningLeft = false;
		private boolean isTurningRight = false; 
		private boolean NextKey = false;
		private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
		
		private Path testPath;
		private PathFinder finder;
		
		// Car Speed to move at
		private final double CAR_SPEED = 1.25;
		private Coordinate currentPosition;
		
		// Offset used to differentiate between 0 and 360 degrees
//		private int EAST_THRESHOLD = 3;
//		private static final int SensorLimit = 4;
		private static final int NoChange = 0;
		private static int j = 1;
		private boolean FinishPath = true;
		
		public MyAIController(Car car) {
			super(car);
		}
		
//		Coordinate initialGuess;
//		boolean notSouth = true;
		
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
					
					
					//Place location of traps etc in maze
					maze.put(Coord, mt);

					//Add key to key hashmap if it exists
					if(ContainsKey(mt) != 0) {
						KeyMap.put(Coord, ContainsKey(mt));
					}
					//Add heal to array list if found
					if(ContainsHeal(mt)) {
						HealMap.add(Coord);
					}
					
				}
			}
			
			
			
			
			//If path is finished find a new one
			System.out.println(FinishPath);
//			if(FinishPath) {
				//Path finding algorithm
					finder = new AStarPathFinder(maze, 500, false);	
					
					//If you have found the next key, path to it over everything else
					Iterator KeyIt = KeyMap.entrySet().iterator();
					while(KeyIt.hasNext()) {
						Map.Entry Key = (Map.Entry)KeyIt.next();											
						if((int)Key.getValue() == totalKey - 1) {
							Coordinate KeyCo = (Coordinate)Key.getKey();
							testPath = finder.findPath(maze, currentPosition.x, currentPosition.y, KeyCo.x, KeyCo.y);
							//NextKey = true;
						}
					}
					
					//Otherwise search as normal
					if(NextKey == false) {
						
						Coordinate destination = new Coordinate(destinationX,destinationY);
						
						if(inRangeOfDes(currentPosition,destination)) {
							destinationX = ThreadLocalRandom.current().nextInt(currentPosition.x, currentPosition.x+4);
							 destinationY = ThreadLocalRandom.current().nextInt(currentPosition.y, currentPosition.y+4);
							 destination = new Coordinate(destinationX,destinationY);
							System.out.println("inrange");
							while(isWall(destination) ) {
								System.out.println("Destination is wall");
								if(currentPosition.x-5>0 && currentPosition.y-5>0) {
									System.out.println("Random Coordinate X or Y is > 4 " );
									destinationX = ThreadLocalRandom.current().nextInt(currentPosition.x-4, currentPosition.x+4);
									destinationY = ThreadLocalRandom.current().nextInt(currentPosition.y-4, currentPosition.y+4);
									destination = new Coordinate(destinationX,destinationY);
								}else {
									System.out.println("Random Coordinate X or Y is < 4 " );
									destinationX = ThreadLocalRandom.current().nextInt(currentPosition.x, currentPosition.x+4);
									destinationY = ThreadLocalRandom.current().nextInt(currentPosition.y, currentPosition.y+4);
									destination = new Coordinate(destinationX,destinationY);
								}
								
								// destinationX = ThreadLocalRandom.current().nextInt(currentPosition.x-4, currentPosition.x+4);
								// destinationY = ThreadLocalRandom.current().nextInt(currentPosition.y-4, currentPosition.y+4);
								 
								 
							}
								
						}
						
						System.out.println("current position is"+ currentPosition);
						System.out.println("current destination is"+ destination);
						testPath = finder.findPath(maze, currentPosition.x, currentPosition.y, destination.x , destination.y );
					}

//					FinishPath = false;
//				}


			

				//As long as the path exists
			if(testPath != null) {
												
//				//Print suggested path
//				int i = 0;
//				while(i < testPath.getLength()) {
//					System.out.println(testPath.getX(i) + "," + testPath.getY(i));
//					i++;
//				}
				
				//If within initially sensed range and not fully incremented through
				if(	j < testPath.getLength())
	//					&& testPath.getX(j) <= testPath.getX(0) + SensorLimit
	//					&& testPath.getX(j) >= testPath.getX(0) - SensorLimit
	//					&& testPath.getY(j) <= testPath.getY(0) + SensorLimit
	//					&& testPath.getY(j) >= testPath.getY(0) - SensorLimit) 
				{
					
					
					//Create next step for car movement
					Coordinate nextStep = FollowStep(testPath, j, currentPosition);
					System.out.println(FollowStep(testPath, j, currentPosition));
					
					/////////////////////////////////Start driving to path//////////////////////////////////////////////////////////////////
					
					checkStateChange();
					
					//If Y changes
					if(nextStep.y != NoChange) {
						//If its northwards
						if(nextStep.y > NoChange) {
							//If not facing north, face north and drive
							if(!getOrientation().equals(WorldSpatial.Direction.NORTH)) {
								if(getOrientation().equals(WorldSpatial.Direction.EAST)) {
									lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
									applyLeftTurn(getOrientation(),delta);
								}
								else {
									lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
									applyRightTurn(getOrientation(),delta);
									}
							}
						}
						else {
							//If not facing south, face south and drive
							if(!getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
								if(getOrientation().equals(WorldSpatial.Direction.EAST)) {
									lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
									applyRightTurn(getOrientation(),delta);
								}
								else {
									lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
									applyLeftTurn(getOrientation(),delta);
									}
							}
						}
					}
					//If X changes
					else if(nextStep.x != NoChange) {
						//If its eastwards
						if(nextStep.x > NoChange) {
							//If not facing East, face east and drive
							if(!getOrientation().equals(WorldSpatial.Direction.EAST)){
								if(getOrientation().equals(WorldSpatial.Direction.NORTH)) {
									lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
									applyRightTurn(getOrientation(),delta);
								}
								else {
									lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
									applyLeftTurn(getOrientation(),delta);
									}
							}
						}
						else {
							//If not facing west, face west and drive
							if(!getOrientation().equals(WorldSpatial.Direction.WEST)){
								if(getOrientation().equals(WorldSpatial.Direction.NORTH)) {
									lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
									applyLeftTurn(getOrientation(),delta);
								}
								else {
									lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
									applyRightTurn(getOrientation(),delta);
									}
							}
						}
					}
					
					if(getSpeed() < CAR_SPEED) {
						applyForwardAcceleration();
					}
	
					
				}
				
				//Increment index and brake if at current tile
				if(j < testPath.getLength() && currentPosition.x == testPath.getX(j) && currentPosition.y == testPath.getY(j)) {
					applyBrake();
					j++;
				}
				
				//Reset counter and pathchecking boolean
				if(j == testPath.getLength()) {
					j = 0;
					NextKey = false;
					FinishPath = true;
				}
							
				System.out.println("KICKED");
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
		

//		/**
//		 * Method below just iterates through the list and check in the correct coordinates.
//		 * i.e. Given your current position is 10,10
//		 * checkEast will check up to wallSensitivity amount of tiles to the right.
//		 * checkWest will check up to wallSensitivity amount of tiles to the left.
//		 * checkNorth will check up to wallSensitivity amount of tiles to the top.
//		 * checkSouth will check up to wallSensitivity amount of tiles below.
//		 */
//		public boolean checkEast(HashMap<Coordinate, MapTile> currentView){
//			// Check tiles to my right
//			Coordinate currentPosition = new Coordinate(getPosition());
//			for(int i = 0; i <= wallSensitivity; i++){
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
//				if(tile.isType(MapTile.Type.WALL)){
//					return true;
//				}
//			}
//			return false;
//		}
//		
//		public boolean checkWest(HashMap<Coordinate,MapTile> currentView){
//			// Check tiles to my left
//			Coordinate currentPosition = new Coordinate(getPosition());
//			for(int i = 0; i <= wallSensitivity; i++){
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
//				if(tile.isType(MapTile.Type.WALL)){
//					return true;
//				}
//			}
//			return false;
//		}
//		
//		public boolean checkNorth(HashMap<Coordinate,MapTile> currentView){
//			// Check tiles to towards the top
//			Coordinate currentPosition = new Coordinate(getPosition());
//			for(int i = 0; i <= wallSensitivity; i++){
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
//				if(tile.isType(MapTile.Type.WALL)){
//					return true;
//				}
//			}
//			return false;
//		}
//		
//		public boolean checkSouth(HashMap<Coordinate,MapTile> currentView){
//			// Check tiles towards the bottom
//			Coordinate currentPosition = new Coordinate(getPosition());
//			for(int i = 0; i <= wallSensitivity; i++){
//				MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
//				if(tile.isType(MapTile.Type.WALL)){
//					return true;
//				}
//			}
//			return false;
//		}
		
//		public boolean searchForDuplicateCoordinate(ArrayList<Coordinate> arrayList, Coordinate coordinate) {
//			for(int i=0; i<arrayList.size();i++) {
//				Coordinate coo = arrayList.get(i);
//				;
//				if(coordinate.equals(coo)) {
//				
//					return true;
//				}
//			}
//			
//			//System.out.println("not dup");
//			return false;
//		}
		
		public boolean haveOneHealTile() {
			if (HealMap.size()>0) {
				return true;
			}
			return false;
		}
//		
//		public void addtilePosition(Coordinate currentPosition, ArrayList<Coordinate> collection) {
//			collection.add(currentPosition);
//		}
		
//		public boolean landOnLavaTileWithKey(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
//			
//			MapTile currentTile = currentView.get(currentPosition);
//			MapTile.Type currentType = currentTile.getType();
//			if(MapTile.Type.TRAP == currentType){
//				
//				if(((TrapTile) currentTile).getTrap()=="lava"){
//					
//					TrapTile a = (TrapTile) currentTile;
//					LavaTrap b = (LavaTrap) a;
//					
//					if(b.getKey() > 0) {
//						//System.out.println("GET KEY "+b.getKey());
//					return true;}
//				}
//			}return false;
//		}
//		
//		public int getKeyNum(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
//			MapTile currentTile = currentView.get(currentPosition);	
//			TrapTile a = (TrapTile) currentTile;
//			LavaTrap b = (LavaTrap) a;
//				
//			return b.getKey();
//			
//		}
		
//		public boolean landOnHealTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
//			
//			MapTile currentTile = currentView.get(currentPosition);
//			MapTile.Type currentType = currentTile.getType();
//			if(MapTile.Type.TRAP == currentType){
//				
//				if(((TrapTile) currentTile).getTrap()=="health"){
//					
//					return true;
//				}
//			}return false;
//		}
		
		//Print list of tiles
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
				System.out.print(" LENGTH:" + maze.size() +"\n");
				
//				it.remove();
			}
			
		}
		
		
		//Check if MapTile contains key
		private int ContainsKey(MapTile mt) {
			if(mt.getType().toString().equals("TRAP")) {
				TrapTile tt = (TrapTile) mt;
				if(tt.getTrap().equals("lava")) {
					LavaTrap lt = (LavaTrap) tt;
					if(lt.getKey() != 0) {
						return lt.getKey();
					}
				}
			}
			return 0;
		}
		
		//Check if heal tile
		private boolean ContainsHeal(MapTile mt) {
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
		
//		//Check if solid tile
//		public boolean IsBlocked(int x, int y) {
//			Coordinate coord = new Coordinate(x, y);
//			MapTile mt = null;
//			if(maze.containsKey(coord)) {
//				mt = maze.get(coord);
//			}
//			else {
//				return false;
//			}
//			if(mt.getType().toString().equals("WALL") || mt.getType().toString().equals("EMPTY")) {
//				return true;
//			}
//			
//			return false;
//		}
		
//		returns coordinate to know movement distance
		private Coordinate FollowStep(Path path, int index, Coordinate currentPosition) {
			Coordinate nextMove = new Coordinate(path.getX(index) - currentPosition.x, 
					path.getY(index) - currentPosition.y);
			return nextMove;
		}

		
		
		private Coordinate RandomCo(Coordinate currentPosition) {
			
			
			int randomNumX = 0;
			int randomNumY = 0;
			Coordinate coord = currentPosition;
			
				if(randomNumX==0)				{
					while(!isWall(coord)) {
						if(currentPosition.x-4<0 || currentPosition.y-4<0) {
							 randomNumX = ThreadLocalRandom.current().nextInt(currentPosition.x-4, currentPosition.x+4);
							 randomNumY = ThreadLocalRandom.current().nextInt(currentPosition.y-4, currentPosition.y+4);
						}else {
							System.out.println("Random Coordinate X or Y is < 4 " );
							randomNumX = ThreadLocalRandom.current().nextInt(currentPosition.x, currentPosition.x+4);
							 randomNumY = ThreadLocalRandom.current().nextInt(currentPosition.y, currentPosition.y+4);
							
						}
					
					  coord = new Coordinate(randomNumX, randomNumY);
					}
				}
			
			
				
				System.out.println("Random Coordinate X is"+randomNumX+"Y = "+randomNumY );
				return coord;
			
		}
		
		private boolean isWall(Coordinate coordinate) {
			HashMap<Coordinate, MapTile> currentView = getView();
			MapTile currentTile = currentView.get(coordinate);
			MapTile.Type currentType = currentTile.getType();
			if(MapTile.Type.WALL == currentType){
				
				return true;
			}
			
			
			return false;
			
		}
		
		private boolean inRangeOfDes(Coordinate current,Coordinate destination) {
			if (current.x == destination.x-1) {
				if (current.y == destination.y-1) {
					return true;
				}
				if (current.y == destination.y) {
					return true;
				}
				if (current.y == destination.y+1) {
					return true;
				}
			}
			if (current.x == destination.x) {
				if (current.y == destination.y-1) {
					return true;
				}
				if (current.y == destination.y) {
					return true;
				}
				if (current.y == destination.y+1) {
					return true;
				}
			}
			if (current.x == destination.x+1) {
				if (current.y == destination.y-1) {
					return true;
				}
				if (current.y == destination.y) {
					return true;
				}
				if (current.y == destination.y+1) {
					return true;
				}
			}
			
			return false;
		}
}

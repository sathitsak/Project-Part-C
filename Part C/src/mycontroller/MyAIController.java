//Heal Tile at test map is 35,19

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
		private int wallSensitivity = 2;
		private int EAST_THRESHOLD = 3;
		Coordinate startPosition =  new Coordinate(0, 0);
		HashMap<Coordinate, MapTile> maze = new HashMap<Coordinate, MapTile>();
		HashMap<Coordinate, Integer> KeyMap = new HashMap<Coordinate, Integer>();
		ArrayList<Coordinate> HealMap = new ArrayList<Coordinate>();
		private int updateCount=0;
		private int reverseCount=0;
		int once = 0;
		Coordinate destination = new Coordinate(0, 0);
		int destinationX;
		int destinationY;
		int totalKey = getKey();
		int mapHeight = World.MAP_HEIGHT;
		int mapWidth = World.MAP_WIDTH;
		int totalTile = mapHeight*mapWidth;
		ArrayList<Coordinate> shouldVisitedTile = new ArrayList<Coordinate>();
		ArrayList<Coordinate> mustVisitKeyTile = new ArrayList<Coordinate>();
		ArrayList<Coordinate> keyTile = new ArrayList<Coordinate>(); //NOT Use will be remove soon
		ArrayList<Coordinate> healTile = new ArrayList<Coordinate>();
		ArrayList<Coordinate> finishTile = new ArrayList<Coordinate>();
		ArrayList<TileCollector> tileCollectorArrayList = new ArrayList<TileCollector>();
		ArrayList<TileCollector> keyCollectorArrayList = new ArrayList<TileCollector>();
		float currentHealth = getHealth();
		int i = 0;
		int k = 0;
		
		
		
		int tempx;
		int tempy;
		int healSwitch=0;
		int healTileSwitch=0;
//		private boolean isFollowingWall = false; // This is initialized when the car sticks to a wall.
		private WorldSpatial.RelativeDirection lastTurnDirection = null; // Shows the last turn direction the car takes.
		private boolean isTurningLeft = false;
		private boolean isTurningRight = false; 
		private boolean NextKey = false;
		private WorldSpatial.Direction previousState = null; // Keeps track of the previous state
		
		Path testPath;
		PathFinder finder;
		
		// Car Speed to move at
		private  double CAR_SPEED = 5; //3.1 4.5 or >5 is good
		Coordinate currentPosition;
		
		// Offset used to differentiate between 0 and 360 degrees
//		private int EAST_THRESHOLD = 3;
		private static final int SensorLimit = 4;
		private static final int NoChange = 0;
		private static int j = 1;
		boolean FinishPath = true;
		
		public MyAIController(Car car) {
			super(car);
		}
		
		Coordinate initialGuess;
		boolean notSouth = true;
		
		@Override
		public void update(float delta) {
			currentPosition = new Coordinate(getPosition());
			System.out.println(getSpeed());
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
			
			
			// This part will run only once during entire simulation to get start position and calculate shouldVisitTile
			if(once == 0) {
				
				locationShouldVisit(shouldVisitedTile,startPosition);
				
				
				once = 1;
			
				
			}
			//System.out.println("car speed = " +CAR_SPEED);
			//If path is finished find a new one
			//System.out.println(FinishPath);
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
					
					//System.out.println(updateCount);
					
					//System.out.println("SVT s"+shouldVisitedTile.get(s));
					
					//set destination as coordinate e=x y=t
					destination = new Coordinate(destinationX,destinationY);
					
					//record every tile around the car, this will record tile and it type
					recordTileTypeAroundTheCar(currentView,currentPosition);
					System.out.println("HealSWitch"+healSwitch);
					
					//If Health get below 60 the AI will go back to nearest heal tile
					if(getHealth()<70 && healSwitch==0) {
						
						 tempx = destinationX;
						 tempy = destinationY;
						 destinationX= nearestTileInList(currentPosition,healTile).x;
						 destinationY= nearestTileInList(currentPosition,healTile).y;
						System.out.println("tempx = "+tempx+ "tempy = "+tempy);
						healSwitch =1;
						healTileSwitch =1;
					}
					//If it  takes too long to get to heal tile will find the nearest again
					if(updateCount %300 ==0&& healSwitch==1) {
						destinationX= nearestTileInList(currentPosition,healTile).x;
						destinationY= nearestTileInList(currentPosition,healTile).y;
					}
					//If land on heal tile set destination to current location so car stop
					if(landOnHealTile(currentView,currentPosition)&& healSwitch==1) {
						
						destinationX= currentPosition.x;
						destinationY= currentPosition.y;
					}
					// If health is full healSwitch will be off(0) and the car will go back to it normal routine
					if(getHealth()>99 && healSwitch==1) {
						
						System.out.println("Fully heal");
						System.out.println("tempx = "+tempx+ "tempy = "+tempy);
						destinationX= tempx;
						destinationY= tempy;
						healSwitch =0;
					}
					
					
					
					// If healSwitch is off(Car health is >60 the car will go back to try to find and collect key and escape)
					else if(healSwitch==0){
						
						
						// If We found all the key location, one heal tile, and finish tile, the car is ready to escape					
						if(haveAllKeyLocation() == true && k==0 && !finishTile.isEmpty() && !healTile.isEmpty()) {
							//Sort key coordinate in arraylist by key number from large to small
							sortTileList(keyCollectorArrayList);
						
							// The key and finish tile will be combine into one Arraylist
							prepareEscapePlan(mustVisitKeyTile,keyCollectorArrayList);
							
							// We also want to run this just once by set k=1 
							k++;
					}
					
					
					// This wil start the escape plan
						if(inPosition(currentPosition, destination)&& !mustVisitKeyTile.isEmpty() ) {
							destinationX=mustVisitKeyTile.get(0).x;
							destinationY=mustVisitKeyTile.get(0).y;
							mustVisitKeyTile.remove(0);
					}
						
						
					// If we don't have escape plan yet
						else if(mustVisitKeyTile.isEmpty()) {
						
						// If shouldVisitLocation is wall, we will shift it
							if(inRangeOfA(currentPosition, destination,4) ) {
						
								if(isWall(destination)) {
									destinationX=shouldVisitedTile.get(i).x;
									destinationY=shouldVisitedTile.get(i).y-2;
								}
							
							// This is to check if Car reach should visit location or not
							// i is index for getting the location from list
								if(inPosition(currentPosition, destination) || i==0 ) {							
									destinationX=shouldVisitedTile.get(i).x;
									destinationY=shouldVisitedTile.get(i).y;
									i++;
								}				
							}			
						}
					}
					
					//Set the car route here
					testPath = finder.findPath(maze, currentPosition.x, currentPosition.y, destinationX, destinationY);
					
					
					//System.out.println("X"+e);
					//System.out.println("Y"+t);
					
						updateCount++;
					//If the car crash
					if(getSpeed()<=0 && updateCount %20 ==0) {
						System.out.println("orientation" + getOrientation());
						//apply reverse	  
						//applyReverseAcceleration();
						//set reverse count to apply constant ReverseAcceleration	
						  reverseCount=20;//10,15
						}
					
					if(reverseCount>0 &&!inPosition(currentPosition, destination)) {
						reverseCount--;	
						applyReverseAcceleration();
						//Check south wall
						/*
						if(getOrientation().equals(WorldSpatial.Direction.SOUTH)) {
							if(checkWest(currentView)){
								lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
								applyLeftTurn(getOrientation(),delta);	
							}else if(checkEast(currentView)){
								lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
								applyRightTurn(getOrientation(),delta);	
							}
							
							
						}
						if(getOrientation().equals(WorldSpatial.Direction.NORTH)) {
							
							if(checkWest(currentView)){
								lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
								applyRightTurn(getOrientation(),delta);	
							}else if(checkEast(currentView)){
								lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
								applyLeftTurn(getOrientation(),delta);	
							}
						}
						if(getOrientation().equals(WorldSpatial.Direction.EAST)) {
							
							if(checkSouth(currentView)){
								lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
								applyLeftTurn(getOrientation(),delta);	
							}else if(checkNorth(currentView)){
								lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;
								applyRightTurn(getOrientation(),delta);
							}
							
						}
						if(getOrientation().equals(WorldSpatial.Direction.WEST)) {
							if(checkSouth(currentView)){
								lastTurnDirection = WorldSpatial.RelativeDirection.RIGHT;

								applyRightTurn(getOrientation(),delta);	
							}else {
								lastTurnDirection = WorldSpatial.RelativeDirection.LEFT;
								applyLeftTurn(getOrientation(),delta);	
							}
						}
						*/
						// if there is south wall turn left
						
							readjust(lastTurnDirection,delta);
							return;
						}
						
					//	System.out.println("Current destination X="+e +" Y = " +t);

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
//						&& testPath.getX(j) <= testPath.getX(0) + SensorLimit
//						&& testPath.getX(j) >= testPath.getX(0) - SensorLimit
//						&& testPath.getY(j) <= testPath.getY(0) + SensorLimit
//						&& testPath.getY(j) >= testPath.getY(0) - SensorLimit) 
				{
					
					
					//Create next step for car movement
					Coordinate nextStep = FollowStep(testPath, j, currentPosition);
			//		System.out.println(FollowStep(testPath, j, currentPosition));
					
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
					
				//	applyForwardAcceleration();
					j++;
				}
				
				//Reset counter and pathchecking boolean
				if(j == testPath.getLength()) {
					j = 0;
					NextKey = false;
					FinishPath = true;
				}
							
			//	System.out.println("KICKED");
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
		
		// If have at least one heal tile in memory return true
		public boolean haveOneHealTile() {
			if (HealMap.size()>0) {
				return true;
			}
			return false;
		}
		
		
		//Check if Coordinate is on lava tile with a key inside
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
		
		//Get key number from lava tile with a key inside
		public int getKeyNum(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
			MapTile currentTile = currentView.get(currentPosition);	
			TrapTile a = (TrapTile) currentTile;
			LavaTrap b = (LavaTrap) a;				
			return b.getKey();			
		}
		
		//Check if Coordinate is on heal tile 
		public boolean landOnHealTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {			
			MapTile currentTile = currentView.get(currentPosition);
			MapTile.Type currentType = currentTile.getType();
			if(MapTile.Type.TRAP == currentType){				
				if(((TrapTile) currentTile).getTrap()=="health"){				
					return true;
				}
			}return false;
		}
		
		//Check if Coordinate is on heal tile 
		public boolean landOnFinishTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
			
			MapTile currentTile = currentView.get(currentPosition);
			MapTile.Type currentType = currentTile.getType();
			if(MapTile.Type.FINISH == currentType){
				return true;				
			}
			return false;
		}
		//Print list of tiles
		public void printMaze() {
			Iterator it = this.maze.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry tile = (Map.Entry)it.next();
				MapTile mt = (MapTile) tile.getValue();
			//	System.out.print("Coords: " + (Coordinate)tile.getKey() +" , TileType: " + mt.getType() + " ");
				if(mt.getType().toString().equals("TRAP")) {
					TrapTile tt = (TrapTile) mt;
					tt.getTrap();
				//	System.out.print("Trap type: " + tt.getTrap() + " ");
					if(tt.getTrap().equals("lava")) {
						LavaTrap lt = (LavaTrap) tt;
						if(lt.getKey() != 0) {
					//		System.out.print("Contains Key: " + lt.getKey());
						}
					}
				}
			//	System.out.print(" LENGTH:" + maze.size() +"\n");
				
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
						return lt.getKey();
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

		
		//Pick new coordinates for pathfinding
		public Coordinate NewTarget() {

			int newX = currentPosition.x;
			int newY = currentPosition.y;
			
			Iterator it = this.maze.entrySet().iterator();
			
			while(it.hasNext()) {
				Map.Entry tile = (Map.Entry)it.next();
				Coordinate Coord = (Coordinate)tile.getKey();
				if(newX < Coord.x && Coord.x < World.MAP_WIDTH) {
					newX = Coord.x;
				}
			}
			
			Coordinate coord = new Coordinate(newX, newY);
			return coord;
		}
		// Check if the coordinate give is WALL or not
		public boolean isWall(Coordinate coordinate) {
			HashMap<Coordinate, MapTile> currentView = getView();
			MapTile currentTile = currentView.get(coordinate);
			MapTile.Type currentType = currentTile.getType();
			if(MapTile.Type.WALL == currentType){			
				return true;
			}
				return false;			
			}
		
		
		// Check if the current coordinate is in range of 4*4(91 tiles) of the destination coordinate or not.
		public boolean inRangeOfA(Coordinate current,Coordinate destination, int a) {
			if (current.x < destination.x+a && current.x > destination.x-a ) {
				if (current.y < destination.y+a && current.y > destination.y-a ) {
					return true;
				}				
			}return false;
		}
		
		// Check if the car had reached the destination or not
		public boolean inPosition(Coordinate current,Coordinate destination) {
			if (current.x == destination.x  ) {
				if (current.y == destination.y ) {
					return true;
				}				
			}return false;
		}
		
		//This how we check which point the car should visit to get every tile information
		public ArrayList<Coordinate> locationShouldVisit(ArrayList<Coordinate> listSV, Coordinate startpoint) {
			
			
			// Car sensor range is 9 and -5 is to not let it go to map corner
			int totalX = (int) Math.ceil((mapWidth-5)/9)+1;
			int totalY = (int) Math.ceil((mapHeight-5)/9)+1;
			
			for(int i=0; i<totalY ; i++) {
				for(int j=0; j<totalX ; j++) {
					int x = 5+9*(j);
					int y = mapHeight-5-9*(i);
					if (x>=mapWidth) {
						x=mapWidth;
					}
					if (y>=mapHeight) {
						y=mapHeight;
					}
					if(x==mapWidth&&y==mapHeight) {
						totalX =0;
						totalY =0;
					}
					Coordinate coordinate = new Coordinate(x,y);
					listSV.add(coordinate);
					//listSV.add(startpoint);
				}
				
			}
					return listSV;			
		}
		
		//After the car got all the key location, we create new arraylist of coordinate that contain key and finish location
		public ArrayList<Coordinate> prepareEscapePlan(ArrayList<Coordinate> listSV,ArrayList<TileCollector> keylo) {
			
			int size =keylo.size();
			// Add key to arraylist
			for (int i = 0; i <size; i++) {								
					//listSV.add(startpoint);
					Coordinate coordinate = new Coordinate(keylo.get(i).getCoordinate().x,keylo.get(i).getCoordinate().y);
					listSV.add(coordinate);						
			}
			// Add finishTile to Arraylist
			for(int i = 0; i <finishTile.size(); i++) {
				Coordinate goal = finishTile.get(0);	
				listSV.add(goal);
			}
			
			return listSV;
			
		}
		
		//Sort key in Arraylist to be in order
		public ArrayList<TileCollector> sortTileList( ArrayList<TileCollector> listSV) {
	
			int size =listSV.size();
			for (int i = 0; i < size-1; i++) {

                if (listSV.get(i).getKeyNum() < listSV.get(i+1).getKeyNum()) {
                   TileCollector temp1= listSV.get(i + 1);
                    TileCollector temp2= listSV.get(i);
                    listSV.set(i,temp1);
                    listSV.set(i+1,temp2);
                }
            }			
				return listSV;
		}
		
		// Record 100 Tiles around the car
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
										TileCollector keyTC = new TileCollector(scanCoo,keyNum);
										keyCollectorArrayList.add(keyTC);
								//		System.out.println("KEY TILE"+keyTC.getCoordinate()+"KEY NUM"+keyTC.getKeyNum());
										
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
							if(landOnFinishTile(currentView,scanCoo)) {
								finishTile.add(scanCoo);
							}
							MapTile.Type scanType = scanTile.getType();
							TileCollector tctile = new TileCollector(scanCoo,scanType);
							
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
								TileCollector keyTC = new TileCollector(scanCoo,keyNum);
								keyCollectorArrayList.add(keyTC);
							//	System.out.println("KEY TILE"+keyTC.getCoordinate()+"KEY NUM"+keyTC.getKeyNum());
							}
							
						
					}
					
					if(landOnHealTile(currentView,scanCoo)) {
						if(searchForDuplicateCoordinate(healTile, scanCoo) == false) {
							healTile.add(scanCoo);
							//System.out.println("Heal TILE"+healTile);
						}
						
					}
					if(landOnFinishTile(currentView,scanCoo)) {
						finishTile.add(scanCoo);
					}
					MapTile.Type scanType = scanTile.getType();
					TileCollector tctile = new TileCollector(scanCoo,scanType);
					
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
										TileCollector keyTC = new TileCollector(scanCoo,keyNum);
										keyCollectorArrayList.add(keyTC);
									//	System.out.println("KEY TILE"+keyTC.getCoordinate()+"KEY NUM"+keyTC.getKeyNum());
									}
									
								
							}
							
							
							if(landOnHealTile(currentView,scanCoo)) {
								if(searchForDuplicateCoordinate(healTile, scanCoo) == false) {
									healTile.add(scanCoo);
									//System.out.println("Heal TILE"+healTile);
								}
								
							}
							if(landOnFinishTile(currentView,scanCoo)) {
								finishTile.add(scanCoo);
							}
							MapTile.Type scanType = scanTile.getType();
							TileCollector tctile = new TileCollector(scanCoo,scanType);
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
								TileCollector keyTC = new TileCollector(scanCoo,keyNum);
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
					if(landOnFinishTile(currentView,scanCoo)) {
						finishTile.add(scanCoo);
					}
					MapTile.Type scanType = scanTile.getType();
					TileCollector tctile = new TileCollector(scanCoo,scanType);			
					tileCollectorArrayList.add(tctile);
							
							
							
										}
								}	
			
			
			}
		public Coordinate  nearestTileInList(Coordinate current,ArrayList<Coordinate> list) {
			int Xc = current.x;
			int Yc = current.y;
			double leastDistance = 10000;
			double distance;
			Coordinate nearest = new Coordinate(0, 0);
			for(int i = 0; i<list.size();i++) {
				int Xd = list.get(i).x;
				int Yd = list.get(i).y;
				
				 distance= (Math.sqrt((((Xc-Xd)^2))+(((Yc-Yd)^2))));
				if(distance<leastDistance) {
					leastDistance = distance;
					nearest = list.get(i);
				}				
			}
			
			return nearest;
			
		}
		
		//Check if car have every tile with key location
		public boolean haveAllKeyLocation() {
			if (keyCollectorArrayList.size() == totalKey-1 )
			{	
				return true;
			}
				return false;		
		}
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
		
		
}

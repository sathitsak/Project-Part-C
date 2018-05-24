package controller;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;
import world.Car;
import world.World;

// Manual Controls for the car
public class ManualController extends CarController{
	
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
	public ManualController(Car car){
		super(car);
	}
	
	
	
	public void update(float delta){
		
		
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate currentPosition = new Coordinate(getPosition());
		Coordinate destination = new Coordinate(currentPosition.x,currentPosition.y+1);
		//System.out.print("Total Tile in MAP"+totalTile);
		//System.out.print("MAP H "+mapHeight);
		//System.out.print("MAP W"+mapWidth);
	//	MapTile currentTile = currentView.get(currentPosition);
		System.out.println(getSpeed());
		if(isWall(destination)) {
			System.out.print("YWALLLLLLLLLLLLLL");
		}
		
		if(inRangeOfFour(currentPosition,destination)) {
			//System.out.print("You are in range of four");
		}
		
		if(searchForDuplicateCoordinate(visitedTile, currentPosition) == false) {
			visitedTile.add(currentPosition);
			
			//System.out.print("visitedTile"+visitedTile);
			
		}
		if(sameTile(visitedTile,currentPosition) ==true) {
			//System.out.println("THIS IS SAME TILE!!!!!!!!!!!!!!!!!!!!!!!!!");

			
		}
		recordTileTypeAroundTheCar(currentView,currentPosition);
		//System.out.print("GET KEY TEST"+totalKey);
		
		if(haveAllKeyLocation() == true) {
			//System.out.println("YOU GOT ALL KEY LOCATION!!!!!!!");			
			
		}
		if(haveOneHealTile() == true) {
			//System.out.println("YOU GOT ONE HEAL LOCATION!!!!!!!");			
			
		}
		
		
		
        if (Gdx.input.isKeyPressed(Input.Keys.B)) {
            applyBrake();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
        	applyForwardAcceleration();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
        	applyReverseAcceleration();
        }
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)){
        	turnLeft(delta);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)){
        	turnRight(delta);
        }
	}
	//Check if we
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
		
//		int i = 0;
//		
//		while(i < tileCollectorArrayList.size()) {
//			System.out.println(tileCollectorArrayList.get(i).location + "\n");
//			System.out.println(tileCollectorArrayList.get(i).tileType);
//			
//			i++;
//		}
//		
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
						
						
						
									}
							}	
		
		
		}
	public boolean inRangeOfFour(Coordinate current,Coordinate destination) {
		if (current.x > destination.x-4 && current.x < destination.x+4  ) {
			if (current.y > destination.y-4 && current.y < destination.y+4 ) {
				return true;
			}				
		}return false;
	}
	public boolean isWall(Coordinate coordinate) {
		HashMap<Coordinate, MapTile> currentView = getView();
		MapTile currentTile = currentView.get(coordinate);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.WALL == currentType){
			System.out.println("WALLLLLLLLLLLLLLLLL AHHHHHHHHHHHHHHHHH" );
			return true;
		}
		
		
		return false;
		
	}
}

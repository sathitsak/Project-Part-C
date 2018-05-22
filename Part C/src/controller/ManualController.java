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
	
	ArrayList<Coordinate> keyTile = new ArrayList<Coordinate>();
	ArrayList<Coordinate> healTile = new ArrayList<Coordinate>();
	ArrayList<TileCollector> tileCollectorArrayList = new ArrayList<TileCollector>();
	
	public ManualController(Car car){
		super(car);
	}
	
	
	
	public void update(float delta){
		
		
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate currentPosition = new Coordinate(getPosition());
		MapTile currentTile = currentView.get(currentPosition);
		
		recordTileTypeAroundTheCar(currentView,currentPosition);
		
		if(landOnLavaTileWithKey(currentView,currentPosition)) {	
	
			if(searchForDuplicateCoordinate(keyTile, currentPosition) == false) {
					keyTile.add(currentPosition);
					System.out.println("KEY TILE"+keyTile);
				}
				
			
		}
		/*
		if(landOnHealTile(currentView,currentPosition)) {
			if(searchForDuplicateCoordinate(healTile, currentPosition) == false) {
				healTile.add(currentPosition);
				System.out.println("Heal TILE"+healTile);
			}
		
				
			//	healTile.add(currentPosition);
				//System.out.println("Heal TILE"+healTile);
			
		}
		 */
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
	
	public void addtilePosition(Coordinate currentPosition, ArrayList<Coordinate> collection) {
		collection.add(currentPosition);
	}
	
	public boolean landOnLavaTileWithKey(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		//HashMap<Coordinate, MapTile> currentView = getView();
		//Coordinate currentPosition = new Coordinate(getPosition());
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.TRAP == currentType){
			//System.out.println("Trap type ="+((TrapTile) currentTile).getTrap());
			if(((TrapTile) currentTile).getTrap()=="lava"){
				
				TrapTile a = (TrapTile) currentTile;
				LavaTrap b = (LavaTrap) a;
				
				if(b.getKey() > 0) {
					//System.out.println("GET KEY "+b.getKey());
				return true;}
			}
		}return false;
	}
	public boolean landOnHealTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.TRAP == currentType){
			
			if(((TrapTile) currentTile).getTrap()=="health"){
				//System.out.println("Trap type ="+((TrapTile) currentTile).getTrap());
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
			//System.out.println("COO ="+coo);
			//System.out.println("COORDINATE ="+coordinate);
			if(coordinate.equals(coo)) {
				//System.out.println("Duplicate");
				return true;
			}
		}
		
		//System.out.println("not dup");
		return false;
	}
	
	public void recordTileTypeAroundTheCar(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		//Quadrant 1
				for(int y=4; y>-1;y--) {
					for(int x=4; x>-1;x--) {
						MapTile scanTile = currentView.get(new Coordinate(currentPosition.x-x, currentPosition.y+y));				
						Coordinate scanCoo = new Coordinate(currentPosition.x-x, currentPosition.y+y);
						
						
						
						if(landOnHealTile(currentView,scanCoo)) {
							if(searchForDuplicateCoordinate(healTile, currentPosition) == false) {
								healTile.add(currentPosition);
								System.out.println("Heal TILE"+healTile);
							}
							
						}
						MapTile.Type scanType = scanTile.getType();
						TileCollector tctile = new TileCollector(scanCoo,scanType);
						//System.out.println("tile coordinate"+tctile.getCoordinate()+"tile type"+tctile.getType());
						tileCollectorArrayList.add(tctile);
						//int mapHeight = World.MAP_HEIGHT;
						//int mapWeight = World.MAP_WIDTH;
						//System.out.println("mapH = " +mapHeight + "mapW = " + mapWeight);
						
						//System.out.println(tileCollectorArrayList);
					}
				}
		//Quadrant 2
		for(int y=4; y>-1;y--) {
			for(int x=0; x<5;x++) {
				MapTile scanTile = currentView.get(new Coordinate(currentPosition.x+x, currentPosition.y+y));				
				Coordinate scanCoo = new Coordinate(currentPosition.x+x, currentPosition.y+y);
				
				
				if(landOnHealTile(currentView,scanCoo)) {
					if(searchForDuplicateCoordinate(healTile, currentPosition) == false) {
						healTile.add(currentPosition);
						System.out.println("Heal TILE"+healTile);
					}
					
				}
				
				MapTile.Type scanType = scanTile.getType();
				TileCollector tctile = new TileCollector(scanCoo,scanType);
				//System.out.println("tile coordinate"+tctile.getCoordinate()+"tile type"+tctile.getType());
				tileCollectorArrayList.add(tctile);
				
				
				
			}
		}
		//Quadrant 3
				for(int y=0; y<5;y++) {
					for(int x=4; x>-1;x--) {
						MapTile scanTile = currentView.get(new Coordinate(currentPosition.x-x, currentPosition.y-y));				
						Coordinate scanCoo = new Coordinate(currentPosition.x-x, currentPosition.y-y);
						
						
						
						if(landOnHealTile(currentView,scanCoo)) {
							if(searchForDuplicateCoordinate(healTile, currentPosition) == false) {
								healTile.add(currentPosition);
								System.out.println("Heal TILE"+healTile);
							}
							
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
				
				
				
				if(landOnHealTile(currentView,scanCoo)) {
					if(searchForDuplicateCoordinate(healTile, currentPosition) == false) {
						healTile.add(currentPosition);
						System.out.println("Heal TILE"+healTile);
					}
					
				}

				MapTile.Type scanType = scanTile.getType();
				TileCollector tctile = new TileCollector(scanCoo,scanType);
				//System.out.println("tile coordinate"+tctile.getCoordinate()+"tile type"+tctile.getType());
				tileCollectorArrayList.add(tctile);
						
						
						
									}
							}				
	}
}

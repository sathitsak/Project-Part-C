package mycontroller;

import java.util.ArrayList;
import java.util.HashMap;

import tiles.LavaTrap;
import tiles.MapTile;
import tiles.TrapTile;
import utilities.Coordinate;

public class TileUtilities {

	
	public  boolean landOnHealTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {			
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.TRAP == currentType){				
			if(((TrapTile) currentTile).getTrap()=="health"){				
				return true;
			}
		}return false;
	}
	
	public int getKeyNum(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		MapTile currentTile = currentView.get(currentPosition);	
		TrapTile a = (TrapTile) currentTile;
		LavaTrap b = (LavaTrap) a;				
		return b.getKey();			
	}
	public boolean checkEast(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition, int wallSensitivity){
		// Check tiles to my right
		
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x+i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkWest(HashMap<Coordinate,MapTile> currentView, Coordinate currentPosition, int wallSensitivity){
		// Check tiles to my left
		
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x-i, currentPosition.y));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkNorth(HashMap<Coordinate,MapTile> currentView, Coordinate currentPosition, int wallSensitivity){
		// Check tiles to towards the top
		
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y+i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public boolean checkSouth(HashMap<Coordinate,MapTile> currentView, Coordinate currentPosition, int wallSensitivity){
		// Check tiles towards the bottom
		
		for(int i = 0; i <= wallSensitivity; i++){
			MapTile tile = currentView.get(new Coordinate(currentPosition.x, currentPosition.y-i));
			if(tile.isType(MapTile.Type.WALL)){
				return true;
			}
		}
		return false;
	}
	
	public  boolean searchForDuplicateCoordinate(ArrayList<Coordinate> arrayList, Coordinate coordinate) {
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
	public  boolean haveOneHealTile(ArrayList<Coordinate> HealMap) {
		if (HealMap.size()>0) {
			return true;
		}
		return false;
	}
	
	public  boolean landOnLavaTileWithKey(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		
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
	
	
	//Check if Coordinate is on heal tile 
	public boolean landOnFinishTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.FINISH == currentType){
			return true;				
		}
		return false;
	}
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
}

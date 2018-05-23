package mycontroller;

public class TileCollector {

	public utilities.Coordinate location;
	public tiles.MapTile.Type tileType;
	public int keyNumber;
	
	public TileCollector(utilities.Coordinate Coordinate,tiles.MapTile.Type type) {
		tileType = type;
		location = Coordinate;
		
	}
	
	public TileCollector(utilities.Coordinate Coordinate,int keyNum) {
		keyNumber = keyNum;
		location = Coordinate;
		
	}
	public  utilities.Coordinate getCoordinate(){
		return location;
		
	}
	public tiles.MapTile.Type getType(){
		return tileType;
	}
	public int getKeyNum(){
		return keyNumber;
	}
}

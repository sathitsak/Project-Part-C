package controller;

public class TestTileCollector {

	public utilities.Coordinate location;
	public tiles.MapTile.Type tileType;
	public int keyNumber;
	
	public TestTileCollector(utilities.Coordinate Coordinate,tiles.MapTile.Type type) {
		tileType = type;
		location = Coordinate;
		
	}
	
	public TestTileCollector(utilities.Coordinate Coordinate,int keyNum) {
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

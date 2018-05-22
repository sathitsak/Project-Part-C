package controller;

public class TileCollector {

	public utilities.Coordinate location;
	public tiles.MapTile.Type tileType;
	
	public TileCollector(utilities.Coordinate Coordinate,tiles.MapTile.Type type) {
		tileType = type;
		location = Coordinate;
		
	}
	public  utilities.Coordinate getCoordinate(){
		return location;
		
	}
	public tiles.MapTile.Type getType(){
		return tileType;
	}
}

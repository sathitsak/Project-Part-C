package controller;

import utilities.Coordinate;
import tiles.MapTile.Type;

public class TileCollector {

	public Coordinate location;
	public Type tileType;
	public int keyNumber;
	
	public TileCollector(Coordinate Coordinate, Type type) {
		tileType = type;
		location = Coordinate;
		
	}
	
	public TileCollector(Coordinate Coordinate,int keyNum) {
		keyNumber = keyNum;
		location = Coordinate;
		
	}
	public Coordinate getCoordinate(){
		return location;
		
	}
	public Type getType(){
		return tileType;
	}
	public int getKeyNum(){
		return keyNumber;
	}
}
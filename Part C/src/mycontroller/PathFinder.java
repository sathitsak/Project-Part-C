package mycontroller;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

public interface PathFinder {
	
	public Path findPath(HashMap<Coordinate, MapTile> map, int sx, int sy, int tx, int ty);
	
}

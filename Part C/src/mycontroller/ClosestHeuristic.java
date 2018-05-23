package mycontroller;

import world.World;

import java.util.HashMap;

import tiles.MapTile;
import utilities.Coordinate;
import world.Car;

/**
 * A heuristic that uses the tile that is closest to the target
 * as the next best tile.
 * 
 * @author Kevin Glass
 * Source: http://www.cokeandcode.com/info/showsrc/showsrc.php?src=../../pathfinder/PathFindingTutorial/target/src/org/newdawn/slick/util/pathfinding/heuristics/ClosestHeuristic.java
 */
public class ClosestHeuristic implements AStarHeuristic {
	
	/**
	 * @see AStarHeuristic#getCost(TileBasedMap, Mover, int, int, int, int)
	 */
	public float getCost(HashMap<Coordinate, MapTile> map, int x, int y, int tx, int ty) {		
		float dx = tx - x;
		float dy = ty - y;
		
		float result = (float) (Math.sqrt((dx*dx)+(dy*dy)));
		
		return result;
	}

}

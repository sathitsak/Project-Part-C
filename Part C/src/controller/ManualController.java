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

// Manual Controls for the car
public class ManualController extends CarController{
	
	ArrayList<Coordinate> keyTile = new ArrayList<Coordinate>();
	ArrayList<Coordinate> healTile = new ArrayList<Coordinate>();
	
	public ManualController(Car car){
		super(car);
	}
	
	
	
	public void update(float delta){
		
		
		HashMap<Coordinate, MapTile> currentView = getView();
		Coordinate currentPosition = new Coordinate(getPosition());
		MapTile currentTile = currentView.get(currentPosition);
		
		
		
		if(landOnLavaTileWithKey(currentView,currentPosition)) {
			
			//addtilePosition(currentPosition,keyTile);
			System.out.println("KEY TILE"+keyTile);
			keyTile.add(currentPosition);
			//printOutArrayListMember(keyTile);
			
		}
		if(landOnHealTile(currentView,currentPosition)) {
			
			//addtilePosition(currentPosition,healTile);
			System.out.println("Heal TILE"+healTile);
			healTile.add(currentPosition);
			printOutArrayListMember(healTile);
			
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
	
	public void addtilePosition(Coordinate currentPosition, ArrayList<Coordinate> collection) {
		collection.add(currentPosition);
	}
	
	public boolean landOnLavaTileWithKey(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		//HashMap<Coordinate, MapTile> currentView = getView();
		//Coordinate currentPosition = new Coordinate(getPosition());
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.TRAP == currentType){
			System.out.println("Trap type ="+((TrapTile) currentTile).getTrap());
			if(((TrapTile) currentTile).getTrap()=="lava"){
				
				TrapTile a = (TrapTile) currentTile;
				LavaTrap b = (LavaTrap) a;
				
				if(b.getKey() != 0) {
					System.out.println("GET KEY "+b.getKey());
				return true;}
			}
		}return false;
	}
	public boolean landOnHealTile(HashMap<Coordinate, MapTile> currentView, Coordinate currentPosition) {
		
		MapTile currentTile = currentView.get(currentPosition);
		MapTile.Type currentType = currentTile.getType();
		if(MapTile.Type.TRAP == currentType){
			System.out.println("Trap type ="+((TrapTile) currentTile).getTrap());
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
}

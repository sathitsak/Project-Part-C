package mycontroller;

import java.util.ArrayList;

public class Path {

	private ArrayList<Step> path;
	
	//Path class constructor
	public Path() {
		this.path = new ArrayList<Step>();
	}
	
	//return length of path in steps
	public int getLength() {
		return this.path.size();
	}
	
	//return step at index
	public Step getStep(int index) {
		return this.path.get(index);
	}
	
	
	//Returns X coord of specific index
	public int getX(int index) {
		return path.get(index).getX();
	}

	//return Y coord of specific index
	public int getY(int index) {
		return path.get(index).getY();
	}
	
	//Add step to end of oath
	public void appendStep(int x, int y) {
		Step newStep = new Step(x, y);
		this.path.add(newStep);
	}
	
	//Add step to beginning of path
	public void prependStep(int x, int y) {
		Step newStep = new Step(x, y);
		this.path.add(0, newStep);
	}
	
	//Check of path contains step object at these coords, return true or false
	public boolean contains(int x, int y) {
		Step newStep = new Step(x, y);
		if(!this.path.contains(newStep)){
			return false;
		}
		return true;
	}
	
}

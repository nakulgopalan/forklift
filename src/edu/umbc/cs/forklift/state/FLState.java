package edu.umbc.cs.forklift.state;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.MutableOOState;
import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.oo.state.exceptions.UnknownClassException;
import burlap.mdp.core.state.MutableState;

<<<<<<< HEAD
=======
//import static edu.umbc.cs.forklift.forklift.ATT_X;
//import static edu.umbc.cs.forklift.forklift.ATT_Y;
//import static edu.umbc.cs.forklift.forklift.ATT_D;
//import static edu.umbc.cs.forklift.forklift.ATT_W;
//import static edu.umbc.cs.forklift.forklift.ATT_L;
//import static edu.umbc.cs.forklift.forklift.ATT_N;
import static edu.umbc.cs.forklift.forklift.*;
>>>>>>> 9fd304b218e3d2e67ba3d29efb4fc742e3e3e99b
//TODO make all the attributes of a forklift agent play with an agent object, not FLState
public class FLState implements MutableOOState{
	
	FLAgent agent;
	List <FLWall> walls;

	private static final List<Object> keys = Arrays.<Object>asList(CLASS_AGENT,CLASS_WALL,CLASS_BOX);
	
	public FLState(){
	}
	
	public FLState(FLAgent agent, List<FLWall> walls)
	{
		this.agent = agent;
		this.walls = walls;
	}
	
	public FLState copy() {
		return new FLState(agent, walls);
	}
	
	public List<Object> variableKeys() {
		return OOStateUtilities.flatStateKeys(this);
	}
	
	public String toString() {
		return OOStateUtilities.ooStateToString(this);
	}

	//TODO when FLState is more object oriented, fill these out
	public int numObjects() {
		return 1 + walls.size();
	}

	public ObjectInstance object(String s) {
		if(agent.name().equals(s))
			return agent;
		else{
			for(FLWall w: walls)
			{
				if(w.name() == s)
					return w;
			}
		}
		return null;
	}

	public List<ObjectInstance> objects() {
		List<ObjectInstance> obj = new ArrayList<ObjectInstance>();
		obj.add(agent);
		for(FLWall w: walls)
		{
			obj.add(w);
		}
		return obj;
	}

	public List<ObjectInstance> objectsOfClass(String s) {
		List<ObjectInstance> ooc = new ArrayList<ObjectInstance>();;
		if(agent.className() == s){
			ooc.add(agent);
		}
		else if(walls.get(0).className().equals(s))
		{
			for(FLWall w: walls)
			{
				ooc.add(w);
			}
		}
		return ooc;
	}

	public MutableOOState addObject(ObjectInstance o) {
		if(o instanceof FLAgent){
			agent = (FLAgent)o;
		}
		else if(o instanceof FLWall){
			walls.add((FLWall)o);
		}
		else{
			throw new UnknownClassException(o.className());
		}

		return this;
	}

	public MutableOOState removeObject(String s) {
		if(agent.name().equals(s)){
			agent = new FLAgent(); //cannot remove, so copy
		}
		return this;
	}

	public MutableOOState renameObject(String s1, String s2) {
		ObjectInstance o = this.object(s1);
		o = o.copyWithName(s2);
		this.removeObject(s1);
		this.addObject(o);
		return this;
	}

	public Object get(Object key) {
		if(key.equals(CLASS_AGENT))
			return agent;
		else if(key.equals(CLASS_WALL))
			return walls;
		return null;
	}

	public MutableState set(Object key, Object value) {
		if(key.equals(CLASS_AGENT))
			agent = (FLAgent) value;
		else if(key.equals(CLASS_WALL))
			walls = (List<FLWall>) value;
		return this;
	}
	

}

package edu.umbc.cs.forklift.state;

import static edu.umbc.cs.forklift.forklift.CLASS_AREA;
import static edu.umbc.cs.forklift.forklift.ATT_L;
import static edu.umbc.cs.forklift.forklift.ATT_W;
import static edu.umbc.cs.forklift.forklift.ATT_X;
import static edu.umbc.cs.forklift.forklift.ATT_Y;

import java.util.Arrays;
import java.util.List;

import burlap.mdp.core.oo.state.OOStateUtilities;
import burlap.mdp.core.oo.state.ObjectInstance;
import burlap.mdp.core.state.State;

public class FLArea implements ObjectInstance {


	public double x;
	public double xWidth;
	public double y;
	public double yLength;
	public String name;
	public static String className;


	
	private static final List<Object> keys = Arrays.<Object>asList(ATT_X, ATT_W, ATT_Y, ATT_L);

	public FLArea(){

	}

	public FLArea(double l, double r, double t, double b, String name) {
		this.x = l;
		this.xWidth = r;
		this.y = t;
		this.yLength = b;
		this.className = CLASS_AREA;
		this.name = name;
		
	}
	public List<Object> variableKeys() {
		return keys;
	}
	public Object get(Object variableKey) {
		if(variableKey instanceof String)
			if(variableKey.equals(ATT_X))
				return x;
			else if(variableKey.equals(ATT_W))
				return xWidth;
			else if(variableKey.equals(ATT_Y))
				return y;
			else if(variableKey.equals(ATT_L))
				return yLength;
			else
				throw new RuntimeException("Unknown key " + variableKey);
		
		else if(variableKey instanceof Integer)
				switch((Integer)variableKey){
					case 0:	return x;
					case 1:	return xWidth;
					case 2: return y;
					case 3: return yLength;
					default: throw new RuntimeException("Unknown key " + variableKey);
				}
		//if key is not string or integer
		throw new RuntimeException("Unknown key " + variableKey);
	}
	public State copy() {
		return new FLArea(x, xWidth, y, yLength, name);
	}
	public String className() {
		return className;
	}
	public String name() {
		return this.name;
	}
	public ObjectInstance copyWithName(String objectName) {
		return new FLArea(x, xWidth, y, yLength, objectName);
	}

	public String toString() {
		return OOStateUtilities.objectInstanceToString(this);
	}
}

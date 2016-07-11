package edu.umbc.cs.forklift;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burlap.domain.singleagent.lunarlander.state.LLAgent;
import burlap.domain.singleagent.lunarlander.state.LLBlock;
import burlap.mdp.auxiliary.DomainGenerator;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.action.ActionType;
import burlap.mdp.core.action.UniversalActionType;
import burlap.mdp.core.Domain;
import burlap.mdp.core.StateTransitionProb;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.MutableState;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.GoalBasedRF;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.model.RewardFunction;
import burlap.mdp.singleagent.model.SampleModel;
import burlap.mdp.singleagent.model.statemodel.FullStateModel;
import burlap.mdp.singleagent.oo.OOSADomain;
import edu.umbc.cs.forklift.state.FLAgent;
import edu.umbc.cs.forklift.state.FLState;
import edu.umbc.cs.forklift.state.FLWall;

public class forklift implements DomainGenerator{

	public static final String ATT_X = "x";
	public static final String ATT_Y = "y";
	public static final String ATT_D = "d";
	public static final String ATT_L = "l";
	public static final String ATT_W = "w";
	public static final String ATT_N = "n";
	public static final String PREFIX_MOVE = "M_";
	public static final String PREFIX_ROTATE = "R_";
	public static final String MOVE_FORWARD = PREFIX_MOVE+"forward";
	public static final String MOVE_BACKWARD = PREFIX_MOVE+"backward";
	public static final String ROTATE_CLOCKWISE = PREFIX_ROTATE+"clockwise";
	public static final String ROTATE_COUNTERCLOCKWISE = PREFIX_ROTATE+"counterclockwise";
	public static final String CLASS_AGENT = "agent";
	public static final String WALL = "wall";
	
	public static final double xBound = 20;
	public static final double yBound = 20;
	
	protected RewardFunction rf;
	protected TerminalFunction tf;
		
	double velocity = 0.5;
	double rotVel = 5;
	
	public List<Double> goalArea; //xmin,xmax,ymin,ymax
	public static ArrayList<FLWall> Walls = new ArrayList<FLWall>();
	public static ArrayList<FLState> Boxes = new ArrayList<FLState>();
	
	public static int captured = 0; 
	
	public forklift()
	{
		for(int i = 0; i < 10; i++)
		{
			Walls.add(new FLWall(i, 10 , 1, 1, "wall"+i));
		}
	}
	
	public TerminalFunction getTf() {
		return tf;
	}

	public void setTf(TerminalFunction tf) {
		this.tf = tf;
	}

	public RewardFunction getRf() {
		return rf;
	}

	public void setRf(RewardFunction rf) {
		this.rf = rf;
	}
	
	public OOSADomain generateDomain() {
		
		OOSADomain domain = new OOSADomain();
		
		FLModel fmodel = new FLModel(velocity, rotVel);
		
		tf = new FLTF(Boxes, goalArea);
		rf = new GoalBasedRF(new FLRF(Boxes, goalArea), 1, 0);
		
		domain.setModel(fmodel);
		
		domain.addActionTypes(new UniversalActionType(MOVE_FORWARD), 
				new UniversalActionType(MOVE_BACKWARD),
				new UniversalActionType(ROTATE_CLOCKWISE),
				new UniversalActionType(ROTATE_COUNTERCLOCKWISE));
		
		domain.addStateClass(CLASS_AGENT, FLAgent.class)
		.addStateClass(WALL, FLWall.class);
		
		return domain;
	}
	
	public class FLAction implements Action
	{
		private String name;

		public FLAction(String n)
		{
			name = n;
		}
		
		public String actionName() {
			return name;
		}

		public Action copy() {
			return this;
		}
		
	}
	
	public class FLActionType implements ActionType
	{
		String name;
		List<Action> actions;

		public FLActionType(String name, List<Action> actions)
		{
			this.name = name;
			this.actions = actions;
		}
		
		public List<Action> allApplicableActions(State s) {
			return actions;
		}

		public Action associatedAction(String s) {
			for(Action a: actions)
			{
				if(s.equals(a.actionName())){
					return a;
				}
			}
			
			return null;
		}

		public String typeName() {
			return name;
		}
		
	}
	
	public static class FLModel implements SampleModel
	{
		double speed;
		double rotationalSpeed;
		
		public FLModel(double speed, double rotationalSpeed)
		{
			this.speed = speed;
			this.rotationalSpeed = rotationalSpeed;
		}

		public State move(State s, Action a) {

			double direction = (Double)s.get(ATT_D);
			
			String actionName = a.actionName();
			//check if action is a rotate or a move
			if(actionName.startsWith(PREFIX_ROTATE)){
				
				if(actionName.equals(ROTATE_CLOCKWISE))
					direction += rotationalSpeed;
				else if(actionName.equals(ROTATE_COUNTERCLOCKWISE))
					direction -= rotationalSpeed;
				if(direction < 0)
					direction += 360;
				direction %= 360;
				((MutableState)s).set(ATT_D, direction);
				
			}else if(actionName.startsWith(PREFIX_MOVE)){
				double px = (Double)s.get(ATT_X);
				double py = (Double)s.get(ATT_Y);
				double deltax = Math.cos((direction/360)*2*Math.PI) * speed;
				double deltay = Math.sin((direction/360)*2*Math.PI) * speed;
				if(actionName.equals(MOVE_FORWARD)){
					px += deltax;
					py += deltay;
				}
				else if(actionName.equals(MOVE_BACKWARD)){
					px -= deltax;
					py -= deltay;
				}
				((MutableState)s).set(ATT_X, px);
				((MutableState)s).set(ATT_Y, py);
			}
			return s;
		}

		public EnvironmentOutcome sample(State s, Action a) {
			s = s.copy();
			return new EnvironmentOutcome(s, a, move(s, a), -1, false);
		}

		public boolean terminal(State s) {
			return false;
		}
		
	}
	
	public static class FLTF implements TerminalFunction
	{
		
		private ArrayList<FLState> Boxes;
		private List<Double> goal;
		
		public FLTF(ArrayList<FLState> Boxes, List<Double> goal)
		{
			this.Boxes = Boxes;
			this.goal = goal;
		}
		public boolean isTerminal(State s) {
			for(FLState b: Boxes)
			{
				if((Double)b.get(forklift.ATT_X) < goal.get(0))
				{
					return false;
				}
				else if((Double)b.get(forklift.ATT_X) > goal.get(1))
				{
					return false;
				}
				else if((Double)b.get(forklift.ATT_Y) < goal.get(2))
				{
					return false;
				}
				else if((Double)b.get(forklift.ATT_Y) > goal.get(3))
				{
					return false;
				}
			}
			return true;
		}
		
	}
	
	public static class FLRF implements TerminalFunction
	{
		private ArrayList<FLState> Boxes;
		private List<Double> goal;
		
		public FLRF(ArrayList<FLState> Boxes, List<Double> goal)
		{
			this.Boxes = Boxes;
			this.goal = goal;
		}
		
		public boolean isTerminal(State arg0) {
			int captured = 0;
			for(FLState b: Boxes)
			{
				if((Double)b.get(forklift.ATT_X) > goal.get(0) && 
						(Double)b.get(forklift.ATT_X) < goal.get(1) &&
						(Double)b.get(forklift.ATT_Y) > goal.get(2) &&
						(Double)b.get(forklift.ATT_Y) < goal.get(3)){
					captured++;
				}
			}
			if(captured > forklift.captured)
			{
				forklift.captured = captured;
				return true;
			}
			return false;
		}
		
	}
	
	
}

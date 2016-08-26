package edu.umbc.cs.forklift;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import burlap.behavior.functionapproximation.DifferentiableStateActionValue;
import burlap.behavior.functionapproximation.dense.ConcatenatedObjectFeatures;
import burlap.behavior.functionapproximation.dense.NumericVariableFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TileCodingFeatures;
import burlap.behavior.functionapproximation.sparse.tilecoding.TilingArrangement;
import burlap.behavior.policy.GreedyQPolicy;
import burlap.behavior.policy.Policy;
import burlap.behavior.policy.PolicyUtils;
import burlap.behavior.singleagent.Episode;
import burlap.behavior.singleagent.auxiliary.EpisodeSequenceVisualizer;
import burlap.behavior.singleagent.learning.tdmethods.vfa.GradientDescentSarsaLam;
import burlap.behavior.singleagent.planning.deterministic.uninformed.bfs.BFS;
import burlap.behavior.singleagent.planning.stochastic.rtdp.BoundedRTDP;
import burlap.behavior.singleagent.planning.stochastic.sparsesampling.SparseSampling;
import burlap.behavior.singleagent.planning.vfa.fittedvi.FittedVI;
import burlap.behavior.valuefunction.ConstantValueFunction;
import burlap.mdp.auxiliary.StateGenerator;
import burlap.mdp.auxiliary.stateconditiontest.StateConditionTest;
import burlap.mdp.core.TerminalFunction;
import burlap.mdp.core.state.State;
import burlap.mdp.singleagent.SADomain;
import burlap.mdp.singleagent.common.UniformCostRF;
import burlap.mdp.singleagent.environment.SimulatedEnvironment;
import burlap.mdp.singleagent.oo.OOSADomain;
import burlap.shell.visual.VisualExplorer;
import burlap.statehashing.simple.SimpleHashableStateFactory;
import burlap.visualizer.Visualizer;
import edu.umbc.cs.forklift.state.FLAgent;
import edu.umbc.cs.forklift.state.FLArea;
import edu.umbc.cs.forklift.state.FLState;
import edu.umbc.cs.forklift.state.FLBlock;
import edu.umbc.cs.forklift.state.FLBlock.FLWall;

public class ForkliftClass {

	public static void main(String [] args){

		forklift gen = new forklift(new UniformCostRF(), new AreaTerminationFunction());
		SADomain domain = gen.generateDomain();

		FLAgent agent = new FLAgent(1.6, 1.6, 0, 0, 0, 0, 1, 1,"agent", null, 0);
		FLArea goalArea = new FLArea(2.5, 2.0, 0., 3.0, "goal"); // 3, 2, 2, 2
		
		ArrayList<FLBlock> walls = new ArrayList<FLBlock>();
		ArrayList<FLBlock> boxes = new ArrayList<FLBlock>(); 
		FLBlock box = new FLBlock.FLBox(10.0, 10.0, 1, 1, "Boxer", true);
		boxes.add(box);

		ArrayList<Point2D.Double> gaps = new ArrayList<Point2D.Double>();
		gaps.add(new Point2D.Double(10.0,20.0));
		gaps.add(new Point2D.Double(9.0, 20.0)); 
		gaps.add(new Point2D.Double(11.0, 20.0)); 
		gaps.add(new Point2D.Double(20.0, 10.0)); 
		gaps.add(new Point2D.Double(20.0, 9.0)); 
		gaps.add(new Point2D.Double(20.0, 11.0)); 
		gaps.add(new Point2D.Double(30.0, 20.0)); 
		gaps.add(new Point2D.Double(29.0, 20.0)); 
		gaps.add(new Point2D.Double(31.0, 20.0)); 
		gaps.add(new Point2D.Double(20.0, 30.0)); 
		gaps.add(new Point2D.Double(20.0, 29.0)); 
		gaps.add(new Point2D.Double(20.0, 31.0)); 

		walls.addAll(GenerateRoom(0,20,0,20,gaps));
		walls.addAll(GenerateRoom(20,39,0,20,gaps));
		walls.addAll(GenerateRoom(20,39,20,39,gaps));
		walls.addAll(GenerateRoom(0,20,20,39,gaps));
		//use wally for testing 
		//walls.add(new FLBlock.FLWall(0, 0, 0, 0, "Wally"));





		FLState state = new FLState(agent, walls, boxes, goalArea);
		System.out.println(domain.getModel().terminal(state));
		SimulatedEnvironment env = new SimulatedEnvironment(domain, state);


		SimpleHashableStateFactory shf = new SimpleHashableStateFactory(false);

		StateConditionTest sc = new StateConditionTest() {
			@Override
			public boolean satisfies(State s) {
				return new AreaTerminationFunction().isTerminal(s);
			}
		};


		Visualizer flv = new FLVisualizer().getVisualizer();

		if(true) {
//		BFS planner = new BFS(domain, sc, shf);

//			FittedVI

			BoundedRTDP planner = new BoundedRTDP(domain, 0.99, shf,
					new ConstantValueFunction(-100.), new ConstantValueFunction(0.), 0.2, 400);
			planner.setMaxRolloutDepth(25);

			Policy policy = planner.planFromState(state);
			Episode episode = PolicyUtils.rollout(policy, env, 100);
//		System.out.println("Terminated correctly: " + new AreaTerminationFunction().isTerminal(episode.state(episode.stateSequence.size()-1)));
//		System.out.println("episode size: " +episode.stateSequence.size());
//		System.out.println("State:" + episode.state(episode.stateSequence.size()-1).toString());
//
//
			new EpisodeSequenceVisualizer(flv, domain, Arrays.asList(episode));
		}
		
//		ConcatenatedObjectFeatures inputFeatures = new ConcatenatedObjectFeatures()
//				.addObjectVectorizion(forklift.CLASS_AGENT, new NumericVariableFeatures());
//				/*.addObjectVectorizion(forklift.CLASS_AREA, new NumericVariableFeatures())
//				.addObjectVectorizion(forklift.CLASS_BLOCK, new NumericVariableFeatures())
//				.addObjectVectorizion(forklift.CLASS_BOX, new NumericVariableFeatures())
//				.addObjectVectorizion(forklift.CLASS_WALL, new NumericVariableFeatures());
//*/
//		int nTilings = 9;
//		double resolution = 10.;
//
//		double xWidth = (forklift.xBound) / resolution;
//		double yWidth = (forklift.yBound) / resolution;
//		double velocityWidth = 40 / resolution;
//		double angleVelocityWidth = 60 / resolution;
//		double angleWidth = 360 / resolution;
//		double xWidthRes = 2 / resolution;
//		double yWidthRes = 1 / resolution;
//		double magWidth = velocityWidth * velocityWidth;
//
		
		
//		TileCodingFeatures tilecoding = new TileCodingFeatures(inputFeatures);
//		tilecoding.addTilingsForAllDimensionsWithWidths(
//				new double []{xWidth, yWidth, velocityWidth, velocityWidth, angleVelocityWidth, angleWidth, yWidthRes, xWidthRes, magWidth},
//				nTilings,
//				TilingArrangement.RANDOM_JITTER);
//
//		double defaultQ = 0.5;
//		DifferentiableStateActionValue vfa = tilecoding.generateVFA(defaultQ/nTilings);
//		GradientDescentSarsaLam ag = new GradientDescentSarsaLam(domain, 0.99, vfa, 0.02, 0.5);
//
//		List episodes = new ArrayList();
//		for(int i = 0; i < 5000; i++){
//			System.out.println("Starting episode");
//			Episode ea = ag.runLearningEpisode(env);
//			episodes.add(ea);
//			System.out.println(i + ": " + ea.maxTimeStep());
//			env.resetEnvironment();
//		}
//		new EpisodeSequenceVisualizer(v, domain, episodes);
		
//		SparseSampling ss = new SparseSampling(domain, 1, new SimpleHashableStateFactory(), 2, 1);
//		ss.setForgetPreviousPlanResults(true);
//		ss.toggleDebugPrinting(true);
//		Policy p = new GreedyQPolicy(ss);
//
//		Episode e = PolicyUtils.rollout(p, state, domain.getModel(), 1000);
//		System.out.println("Num steps: " + e.maxTimeStep());
//
//		new EpisodeSequenceVisualizer(v, domain, Arrays.asList(e));
	
//		StateGenerator rStateGen

//		System.out.println(state.toString());
		else{
		VisualExplorer exp = new VisualExplorer(domain, env, flv);

		exp.addKeyAction("w", forklift.MOVE_FORWARD, "");
		exp.addKeyAction("s", forklift.MOVE_BACKWARD, "");
		exp.addKeyAction("d", forklift.ROTATE_CLOCKWISE, "");
		exp.addKeyAction("a", forklift.ROTATE_COUNTERCLOCKWISE, "");
		exp.addKeyAction(" ", forklift.BRAKE, "");
		exp.addKeyAction("x", forklift.IDLE, "");
//		exp.addKeyAction("q", forklift.PICKUP, "");
//		exp.addKeyAction("e", forklift.DROP, "");

		exp.initGUI();
		}
		
	}
	
	//preconditions: x1 < x2 and y1 < y2
	private static List<FLWall> GenerateRoom(int x1, int x2, int y1, int y2, ArrayList<Point2D.Double> doors){
		List<FLWall> room = new ArrayList<FLWall>();
		for(int i = x1; i <= x2; i++){
			
			boolean addY1 = true;
			for(Point2D.Double door: doors){
				if(i == door.x && y1 == door.y)
					addY1 = false;
			}
			if(addY1){
				room.add(new FLWall(i, y1, 1, 1, "Wall " + i + ", "+ y1));
			}
			boolean addY2 = true;
			for(Point2D.Double door: doors){
				if(i == door.x && y2 == door.y)
					addY2 = false;
			}
			if(addY2){
				room.add(new FLWall(i, y2, 1, 1, "Wall " + i + ", "+ y2));
			}
		}
		for(int j  = y1+1; j <= y2-1; j++){
			boolean addX1 = true;
			for(Point2D.Double door: doors){
				if(j == door.y && x1 == door.x)
					addX1 = false;
			}
			if(addX1){
				room.add(new FLWall(x1, j, 1, 1, "Wall " + x1 + ", "+ j));
			}
			boolean addX2 = true;
			for(Point2D.Double door: doors){
				if(j == door.y && x2 == door.x)
					addX2 = false;
			}
			if(addX2){
				room.add(new FLWall(x2, j, 1, 1, "Wall " + x2 + ", "+ j));
			}
		}
		return room;
	}
	
	public static void FLSARSA()
	{
		

	}

	public static class AreaTerminationFunction implements TerminalFunction{

		@Override
		public boolean isTerminal(State s) {
			FLAgent agent = (FLAgent) s.get(forklift.CLASS_AGENT);
			FLArea area = (FLArea) s.get(forklift.CLASS_AREA);

			double ax = (Double)agent.get(forklift.ATT_X);
			double ay = (Double)agent.get(forklift.ATT_Y);

			double bottomLeftX = (Double)area.get(forklift.ATT_X);
			double botthmLefty = (Double)area.get(forklift.ATT_Y);
			double xWidth = (Double)area.get(forklift.ATT_W);
			double yHeight = (Double)area.get(forklift.ATT_L);

			if((ax<bottomLeftX+xWidth) && (ax>bottomLeftX) && (ay>botthmLefty) && (ay<botthmLefty+yHeight)){
				return true;
			}

			return false;
		}
	}
}

package tetris;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;

import cemethod.CESolver;
import cemethod.DataWriter;
import cemethod.MethodType;

public class CompareMethods {
	
	public static int height = 20;
	public static int width = 10;
	
	private static final int MAX_GEN = 150;
	
	private static int nThreads = 8;
	private static int nElitists = 10;
	private static double initialNoise = 50.0;
	private static double noiseStep = -0.1;
	
	private static int populationSize = 100;
	private static int nTrials = 1;
	
	private static MethodType[] methods = {MethodType.ELITIST, MethodType.SEMIPROP};
	
	private static BitSet featureBits = BitSet.valueOf(new long[] {0b011_111_111_111_111_1});
	
	public static void main(String[] args) {
		
		int trialIdx = 0;
		
		while(true) {
		
			MethodType mt = methods[trialIdx % methods.length];
			
			Tetris tetris = new Tetris(width, height, new Random(), nTrials, featureBits.cardinality());
			tetris.setFeatureSubset(featureBits);
			
			CESolver solver = new CESolver(nThreads, new MersenneTwister(), mt);
			
			solver.setProblem(tetris);
			solver.setMaxGenerations(MAX_GEN);
			solver.setInitialNoise(initialNoise);
			solver.setNoiseStep(noiseStep);
			solver.setSamples(populationSize);
			solver.setNElitists(nElitists);
			
			try {
				
				solver.solve();
				
			} catch (Exception e) {
				
				e.printStackTrace();
				return;
				
			}
			
			trialIdx++;
			
			if(trialIdx % methods.length == 0) {
				
				updatePlot();
				
			}
			
		}
		
	}
	
	public static void updatePlot() {
		
		ArrayList<ArrayList<Double>> data = new ArrayList<ArrayList<Double>>();
		
		try {
			
			for (MethodType mt : methods) {
			
				File folder = new File(
						DataWriter.DATA_FOLDER + 
						mt.toString() + "/" +
						populationSize + "-" +
						nElitists + "-" +
						populationSize + "/");
				
				for (File file : folder.listFiles()) {
					
					ArrayList<Double> methodData = new ArrayList<Double>();
					
					FileReader freader = new FileReader(file);
					
					BufferedReader br = new BufferedReader(freader);
					
					String line = br.readLine();
					
					while(line != null) {
						
						if(line.substring(0, 3).equals("AVG")) {
							
							String value = line.split(":")[1].substring(1);
							
							methodData.add(Double.parseDouble(value));
							
						}
						
						line = br.readLine();
						
					}
					
					data.add(methodData);
					
					System.out.println(methodData.toString());
					
				}
			
			}
			
		} catch (Exception e) {
			
			e.printStackTrace();
			//handle later
			
		}
		
		
		
	}

}

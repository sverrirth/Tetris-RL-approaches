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

public class Experiments {
	
	public static int height = 20;
	public static int width = 10;
	
	private static final int MAX_GEN = 20;
	
	private static int nThreads = 32;
	private static int nElitists = 10;
	private static double initialNoise = 50.0;
	private static double noiseStep = -0.1;
	
	private static int populationSize = 100;
	private static int nTrials = 1;
	
	private static MethodType[] methods = {MethodType.ELITIST, MethodType.SEMIPROP};
	
	private static BitSet featureBits = BitSet.valueOf(new long[] {0b011_111_111_111_111_1});
	
	private static cemethod.CEProblem cem;
	
	public static void main(String[] args) {
		
		cem = new Tetris(width, height, new Random(), nTrials, featureBits.cardinality());
		cem.setFeatureSubset(featureBits);
		
		int trialIdx = 0;
		
		while(true) {
		
			MethodType mt = methods[trialIdx % methods.length];
			
			CESolver solver = new CESolver(nThreads, new MersenneTwister(), mt);
			
			solver.setProblem(cem);
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
			
				String name = DataWriter.DATA_FOLDER + 
						cem.toString() + "/" +
						mt.toString() + "/" +
						populationSize + "-" +
						nElitists + "-" +
						nTrials + "/" +
						initialNoise + "_" + noiseStep;
				
				File folder = new File(name);
				
				ArrayList<Double> methodData = new ArrayList<Double>();
				
				int runs = 0;
				
				for (File file : folder.listFiles()) {
					
					FileReader freader = new FileReader(file);
					BufferedReader br = new BufferedReader(freader);
					
					String line = br.readLine();
					
					int idx = 0;
					
					while(line != null) {
						
						if(line.substring(0, 3).equals("AVG")) {
							
							String value = line.split(":")[1].substring(1);
							double dValue = Double.parseDouble(value);
							
							if (methodData.size() > idx) {
								
								methodData.set(idx, (methodData.get(idx) * runs + dValue)/(runs + 1)); //calculate average online
								
							} else {
							
								methodData.add(dValue);
								
							}
							
							idx++;
							
						}
						line = br.readLine();	
					}
					br.close();
					runs++;		
				}
				data.add(methodData);
			}
			
			PythonPlot plot = new PythonPlot(false);
			
			int idx = 0;
			
			for(MethodType method : methods) {
			
				ArrayList<Integer> xaxis = new ArrayList<Integer>();
				
				for(int i = 0; i < data.get(idx).size(); i++) {
					
					xaxis.add(i * 10);
					
				}
				
				plot.plot().add(xaxis, data.get(idx)).label(method.name());
				
				idx++;
				
			}
			
			plot.legend();
			
			String plotName =
					
					DataWriter.DATA_FOLDER + 
					cem.toString() + "_" +
					populationSize + "_" +
					nElitists + "_" +
					nTrials + "_" +
					initialNoise + "_" + 
					noiseStep +
					".png";
			
			plot.savefig(plotName);
			plot.executeSilently();
			plot.close();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return;
			//handle later
			
		}
		
	}

}

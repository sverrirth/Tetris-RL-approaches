package tetris;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;

import cemethod.CESolver;

public class TestClass {
	
	/*
	
	public static void writeToFile(String filename, String content) {
		
		FileWriter fw = null;
		BufferedWriter bw = null;
		
		try {

			fw = new FileWriter(filename, true);
			bw = new BufferedWriter(fw);
			bw.write(content);

			System.out.println("Done");

		} catch (IOException e) {

			e.printStackTrace();

		} finally {

			try {

				if (bw != null)
					bw.close();

				if (fw != null)
					fw.close();

			} catch (IOException ex) {

				ex.printStackTrace();

			}
			
		}
		
	}
	
	public static void compareCEMethods() { //one with normalization, one without
		
		final boolean proportional = false;
		
		int width = 10;
		int height = 14;
		int threads = 4; //TODO: change to a higher value
		int maxGenerations = 10000;
		double initialNoise = 7.0;
		double noiseStep = -0.1;
		int generationSize = 100;
		int elitistsSize = 10;
		
		Tetris tetris = new Tetris(width, height, new Random(), 100, 15); //make sure 16 is correct
		BitSet featureBits = BitSet.valueOf(new long[] {0b111_111_111_111_111_0});
		
		tetris.setFeatureSubset(featureBits);
		
		CESolver solver = new CESolver(threads, new MersenneTwister());
		solver.setProblem(tetris);
		
		solver.setProportional(proportional);
		
		solver.setMaxGenerations(maxGenerations);
		solver.setSamples(generationSize);
		
		if(proportional) {
		
			solver.setNElitists(generationSize);
		
		} else {
			
			solver.setNElitists(elitistsSize);
			
		}
		solver.setInitialNoise(initialNoise);
		solver.setNoiseStep(noiseStep);
		
		try {
		
			solver.solve();
			
		} catch (Exception e) {
			
			//do something later
			
		}
		
	}
	
	public static void evalParams() {
		
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		for(int i=0; i<20; i++) {
			
			double[] params = {173.93319535448177, -139.09664325398327, -5.717233775013957, -40.24937976328622, -28.639652258335047, -210.38936956738053, -122.47306772103906, -143.73082731504743, -129.8481678234196, -161.71770951401302, -698.7323197479805, -116.85960106391916, -146.90358075379913, 10.751737320025455, -117.5606726799959};
			int width = 10;
			int height = 20;
			
			BitSet featureBits = BitSet.valueOf(new long[] {0b011_111_111_111_111_1});
			Tetris tetris = new Tetris(width, height, new Random(), 100, featureBits.cardinality());
			
			tetris.setFeatureSubset(featureBits);
			
			int result = tetris.runTrial(params, false);
			
			results.add(result);
			
			System.out.println(i + ": " + tetris.runTrial(params, false));
			
		}
		
		System.out.println("average = " + results.stream().mapToInt(val -> val).average().orElse(0.0));

		
	}
	
	public static void holeComparison() throws InterruptedException, IOException {
		
		int width = 10;
		int height = 20;
		int threads = 2; //TODO: change to a higher value
		int maxGenerations = 15;
		double initialNoise = 7.0;
		double noiseStep = -0.1;
		int generationSize = 100;
		//int elitistSize = 10;
		
		//Tetris training = new Tetris(width, trainingHeight, new Random(), 50, nFeatures);
		//Tetris evaluation = new Tetris(width, evaluationHeight, new Random(), 1000, nFeatures);
		
		BitSet holesFeature = BitSet.valueOf(new long[] {0b111_111_111_111_111_1});
		BitSet noHolesFeature = BitSet.valueOf(new long[] {0b111_111_111_111_111_0});
		
		Tetris holeTetris = new Tetris(width, height, new Random(), 100, holesFeature.cardinality());
		Tetris noHoleTetris = new Tetris(width, height, new Random(), 100, noHolesFeature.cardinality());
		
		try {
			
			holeTetris.setFeatureSubset(holesFeature);
			noHoleTetris.setFeatureSubset(noHolesFeature);
		
		} catch(Exception e) {
			
			System.err.println("The feature subset was of the wrong size.");
			return;
		}
		
		CESolver solver = new CESolver(threads, new MersenneTwister());
		solver.setProblem(holeTetris);
		
		solver.setProportional(true);
		
		solver.setMaxGenerations(maxGenerations);
		solver.setSamples(generationSize);
		solver.setNElitists(generationSize);
		solver.setInitialNoise(initialNoise);
		solver.setNoiseStep(noiseStep);
		
		solver.solve();
		
	}
	
	public static void main(String[] args) throws InterruptedException {
		
		//holeComparison();
		
		//compareCEMethods();
	
		evalParams();
		
		/*
		
		try {
		
			holeTetris.setFeatureSubset(holesFeature);
			noHoleTetris.setFeatureSubset(noHolesFeature);
		
		} catch(Exception e) {
			
			System.err.println("The feature subset was of the wrong size.");
			return;
		}
		
		for(int i = 0; i < 100; i++) {
		
			CESolver solver = new CESolver(threads, new MersenneTwister());
			solver.setProblem(holeTetris);
			
			solver.setProportional(true);
			
			solver.setMaxGenerations(maxGenerations);
			solver.setSamples(generationSize);
			solver.setElitists(generationSize);
			solver.setInitialNoise(initialNoise);
			solver.setNoiseStep(noiseStep);
			
			solver.solve();
			
			writeToFile("results_proportional.txt", Arrays.toString(solver.bestSamples) + System.lineSeparator());
			
			solver = new CESolver(threads, new MersenneTwister());
			solver.setProblem(holeTetris);
			
			solver.setProportional(false);
			
			solver.setMaxGenerations(maxGenerations);
			solver.setSamples(generationSize);
			solver.setElitists(elitistSize);
			solver.setInitialNoise(initialNoise);
			solver.setNoiseStep(noiseStep);
			
			solver.solve();
			
			writeToFile("results_elitism.txt", Arrays.toString(solver.bestSamples) + System.lineSeparator());
			
		}
		
	}
	
	*/

}

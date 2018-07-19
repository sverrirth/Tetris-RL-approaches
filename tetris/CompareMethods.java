package tetris;

import java.util.BitSet;
import java.util.Random;

import org.apache.commons.math3.random.MersenneTwister;

import cemethod.CESolver;

public class CompareMethods {
	
	public enum ComparisonType {
		
		PROPORTIONAL, ELITIST, SEMIPROP, FULL_ADAPTIVE, ADAPTIVE_PROPORTIONAL
		
	}
	
	public static int height = 20;
	public static int width = 10;
	
	public static void main(String[] args) {
		
		ComparisonType ct;
		int nThreads = 16;
		
		int nElitists = 10;
		double initialNoise = 50.0;
		double noiseStep = -0.1;
		
		int populationSize = 100;
		int nTrials = 1;
		
		ct = ComparisonType.PROPORTIONAL;
		
		BitSet featureBits = BitSet.valueOf(new long[] {0b011_111_111_111_111_1});
		Tetris tetris = new Tetris(width, height, new Random(), nTrials, featureBits.cardinality());
		tetris.setFeatureSubset(featureBits);
		
		CESolver solver = new CESolver(nThreads, new MersenneTwister());
		solver.setProblem(tetris);
		solver.setMaxGenerations(Integer.MAX_VALUE);
		
		if(ct == ComparisonType.ELITIST) {
			
			solver.setProportional(false);

			solver.setElitists(nElitists);
			solver.setSamples(populationSize);
			
			solver.setInitialNoise(initialNoise);
			solver.setNoiseStep(noiseStep);
				
		} else if(ct == ComparisonType.PROPORTIONAL) {
			
			solver.setProportional(true);
			
			solver.setInitialNoise(initialNoise);
			solver.setNoiseStep(noiseStep);

			solver.setElitists(populationSize);
			solver.setSamples(populationSize);
			
		} else if(ct == ComparisonType.SEMIPROP) {
			
			solver.setProportional(false);
			solver.setSemiProp(true);
			
			solver.setInitialNoise(initialNoise);
			solver.setNoiseStep(noiseStep);

			solver.setElitists(nElitists);
			solver.setSamples(populationSize);
			
		}
		
		try {
			
			solver.solve();
			
		} catch (Exception e) {
			
			e.printStackTrace();
			return;
			
		}
		
	}

}

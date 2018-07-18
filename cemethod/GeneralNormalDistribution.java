package cemethod;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.commons.math3.distribution.MultivariateNormalDistribution;
import org.apache.commons.math3.random.RandomGenerator;
import org.apache.commons.math3.stat.correlation.Covariance;

public class GeneralNormalDistribution {
	/**
	 * The dimensionality of the distribution.
	 */
	public final int dim;
	private MultivariateNormalDistribution d;
	private RandomGenerator r;

	/**
	 * @param dimension the dimension required 
	 */
	public GeneralNormalDistribution(int dimension, RandomGenerator r) {
		dim = dimension;
		double[] means = new double[dim];
		double[][] covarianceMatrix = new double[dim][dim];
		for(int i = 0; i < dim; i++) {
			means[i] = 0;
			covarianceMatrix[i][i] = 100;
		}
		this.r = r;
		d = new MultivariateNormalDistribution(this.r, means, covarianceMatrix);
	}
	
	public void fitTo(List<Point> samples, double noise, boolean proportional) {
		
		System.out.println("samples size: " + samples.size());
		
		if (proportional) {
			
			fitProportional(samples, noise);
			
		} else {
			
			fitElitism(samples, noise);
			
		}
		
	}
	
	/**
	 * Fits a new normal distribution to the samples given,
	 * adding the specified amount of noise, weighting the samples by performance.
	 * @param samples This list is sorted by performance
	 * @param noise
	 */
	public void fitProportional(List<Point> samples, double noise) {
		
		int nSamples = samples.size();
		
		System.out.print("Best performances (PROPORTIONAL): ");
		
		for(Point p : samples) {
			
			System.out.print(" " + p.performance + ", ");
			
		}
		
		double highest = samples.get(0).performance;
		double lowest = samples.get(nSamples-1).performance;
		double diff = highest - lowest;
		
		double[] means = new double[dim];
		
		double[] weights = new double[nSamples];
		
		double weightSum = 0;
		
		double[] averages = new double[dim];
		
		//System.out.println("Samples:");
		
		for(int k = 0; k < nSamples; k++) {
			
			Point sample = samples.get(k);
			
			for(double d : sample.par) {
				
				//System.out.print(Math.round(d * 10)/10.0 + ", ");
				
			}
			
			double weight = (sample.performance - lowest) / diff;
			weights[k] = weight;
			weightSum += weight;
			
			for(int i = 0; i < dim; i++) {
				double si = sample.par[i];
				
				averages[i] += si;
		
				means[i] += si * weight;
			
			}	

		}
		
		//System.out.println("Averages:");
		
		for (int i = 0; i < averages.length; i++) {
			
			averages[i] = averages[i] / samples.size();
			//System.out.println(Math.round(averages[i] * 10)/10.0);
			
		}
		
		for (int i = 0; i < means.length; i++) {
			
			means[i] = means[i] / weightSum; 
			
		}
		
		double weightsSquared = 0;
		
	    for(int i = 0; i < samples.size(); i++) {
	    	
	    	weights[i] = weights[i] / weightSum;
	    	weightsSquared += weights[i] * weights[i];

	    }
	    
	    double coeff = 1/(1 - weightsSquared);
	    
	    //System.out.println("coeff " + coeff);
	    //System.out.println("Weights: ");
	    
		for(double d : weights) {
			
			//System.out.print(Math.round(d * 100)/100.0 + ", ");
			
		}
		
		//System.out.println("");
	    
		
		double[][] covarianceMatrix = new double[dim][dim];
	    
	    for(int i = 0; i < dim; i++) {
	    	
	    	for(int j = i; j < dim; j++) {
	    		
	    		double term = 0;
	    		
	    		for(int k = 0; k < samples.size(); k++) {
	    			
	    			term += weights[k] * (samples.get(k).par[i] - averages[i]) * (samples.get(k).par[j] - averages[j]);
	    			
	    		}
	    		
	    		covarianceMatrix[i][j] = coeff * term;
	    		covarianceMatrix[j][i] = covarianceMatrix[i][j];
	    			
	    	}
	    	
	    }
		
		if (true) {
			
			//System.out.println("Covariance: \n");
			//printMatrix(covarianceMatrix);
		
		}
		
		for(int i = 0; i < dim; i++) {
			covarianceMatrix[i][i] += noise + 0.05;
		}
	    
	    /*
		
		double[][] arr = new double[samples.size()][dim];
		for(int i = 0; i < samples.size(); i++) {
			arr[i] = samples.get(i).par;
		}
		double[][] covarianceMatrix = new Covariance(arr, false).getCovarianceMatrix().getData();
		for(int i = 0; i < dim; i++) {
			covarianceMatrix[i][i] += noise + 0.05;
		}
		for(double[] a : covarianceMatrix) {
			for(double f : a) {
				System.out.printf("%07.5f ", f);
			}
			System.out.println();
		}
		
		*/
		
		/*
		
		int nElitists = 10;
		
		double[][] arr = new double[nElitists][nElitists];
		for(int i = 0; i < nElitists; i++) {
			arr[i] = samples.get(i).par;
		}
		double[][] anotherCovarianceMatrix = new Covariance(arr, false).getCovarianceMatrix().getData();
		
		System.out.println("Alternative covariance");
		printMatrix(anotherCovarianceMatrix);
		
		*/
	    
	    d = new MultivariateNormalDistribution(r, means, covarianceMatrix);
		
	}
	
	public static void printMatrix(double[][] matrix) {
		
		for (int i = 0; i < matrix.length; i++) {
		    for (int j = 0; j < matrix[i].length; j++) {
		    	String number = String.format("%.1f", matrix[i][j]);
		    	String space = number.length() == 4 ? "   " : "    ";
		    	if(number.length() == 5) {
		    		
		    		space = "  ";
		    		
		    	} else if (number.length() == 6) {
		    		
		    		space = " ";
		    		
		    	}
		        System.out.print(number + space);
		    }
		    System.out.println();
		}
		
	}

	/**
	 * Fits a new normal distribution to the samples given,
	 * adding the specified amount of noise.
	 * @param samples This list is sorted by performance
	 * @param noise
	 */
	public void fitElitism(List<Point> samples, double noise) {
		
		System.out.print("Best performances (ELITISM):");
		
		for(Point p : samples) {
			
			System.out.print(" " + p.performance + ", ");
			
		}
		
		System.out.println("");
		
		int nsamples = samples.size();
		double[] means = new double[dim];
		
		for(Point sample : samples) {
			for(int i = 0; i < dim; i++) {
				double si = sample.par[i];
				means[i] += si / nsamples;
			}
		}
		double[][] arr = new double[samples.size()][dim];
		
		for(int i = 0; i < samples.size(); i++) {
			arr[i] = samples.get(i).par;
		}
		
		
		double[][] covarianceMatrix = new Covariance(arr, false).getCovarianceMatrix().getData();
		for(int i = 0; i < dim; i++) {
			covarianceMatrix[i][i] += noise + 0.05;
		}
		
		if(false) {
				
			System.out.println("Covariance: \n");
			printMatrix(covarianceMatrix);
		
		}
		
		d = new MultivariateNormalDistribution(r, means, covarianceMatrix);
	}

	public double avgVar() {
		double ans = 0;
		double[] a = d.getStandardDeviations();
		for(double x : a) {
			ans += x * x;
		}
		return ans / dim;
	}

	public double[] getMeans() {
		return d.getMeans();
	}

	public double[] sample() {
		return d.sample();
	}
}

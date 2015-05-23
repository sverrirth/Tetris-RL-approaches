package cemethod;

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

	/**
	 * Fits a new normal distribution to the samples given,
	 * adding the specified amount of noise.
	 * @param samples
	 * @param noise
	 */
	public void fitTo(List<Point> samples, double noise) {
		int nsamples = samples.size();
		double[] means = new double[dim];
		double[][] covarianceMatrix = new double[dim][dim];
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
		covarianceMatrix = new Covariance(arr, false).getCovarianceMatrix().getData();
		for(int i = 0; i < dim; i++) {
			covarianceMatrix[i][i] += noise + 0.05;
		}
		for(double[] a : covarianceMatrix) {
			for(double f : a) {
				System.out.printf("%07.5f ", f);
			}
			System.out.println();
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

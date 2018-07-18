package cemethod;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Implements the normal distribution under the assumption that
 * each component has the same variance and that they all are
 * independent.
 */
public class NormalDistribution {
	/**
	 * The dimensionality of the distribution.
	 */
	public final int dim;
	/**
	 * The vector of mean values.
	 */
	private final double[] means;
	/**
	 * The variance, common to each component of the vector.
	 */
	private final double var;

	/**
	 * @param dimension The dimension required.
	 */
	public NormalDistribution(int dimension) {
		dim = dimension;
		means = new double[dim];
		for(int i = 0; i < dim; i++) {
			means[i] = 0;
		}
		var = 100;
	}

	/**
	 * Fits a new normal distribution to the samples given,
	 * adding the specified amount of noise.
	 * @param samples
	 * @param noise
	 */
	public NormalDistribution(List<Point> samples, double noise) {
		dim = samples.get(0).par.length;
		means = new double[dim];
		int nsamples = samples.size();
		
		System.out.println("Best performance:" );
		for(Point sample : samples) {
			for(int i = 0; i < dim; i++) {
				System.out.print(" " + sample.performance + ", ");
				
				double si = sample.par[i];
				means[i] += si / nsamples;
			}
		}
		System.out.println("");
		double nvar = 0;
		for(Point sample : samples) {
			for(int i = 0; i < dim; i++) {
				double si = sample.par[i];
				nvar += (si - means[i]) * (si - means[i]);
			}
		}
		var = nvar / nsamples / dim + noise;
	}

	public double avgVar() {
		return var;
	}

	public double[] getMean() {
		return Arrays.copyOf(means, dim);
	}

	public double[] sample(Random rng) {
		double[] ret = new double[dim];
		for(int i = 0; i < dim; i++) {
			ret[i] = means[i] + rng.nextGaussian() * Math.sqrt(var);
		}
		return ret;
	}
}

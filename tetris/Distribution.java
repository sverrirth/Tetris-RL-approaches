package tetris;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

class Distribution {
	public final int dim;
	private final double[] means;
	private final double var;

	Distribution(int dimension) {
		dim = dimension;
		means = new double[dim];
		for(int i = 0; i < dim; i++) {
			means[i] = -1;
		}
		var = 100;
	}

	Distribution(List<AIParameters> samples, double noise) {
		dim = samples.get(0).par.length;
		means = new double[dim];
		int nsamples = samples.size();
		for(AIParameters sample : samples) {
			for(int i = 0; i < dim; i++) {
				double si = sample.par[i];
				means[i] += si / nsamples;
			}
		}
		double nvar = 0;
		for(AIParameters sample : samples) {
			for(int i = 0; i < dim; i++) {
				double si = sample.par[i];
				nvar += (si - means[i]) * (si - means[i]);
			}
		}
		var = nvar / nsamples / dim + noise;
	}

	AIParameters sample(Random rng) {
		double[] ret = new double[dim];
		for(int i = 0; i < dim; i++) {
			ret[i] = means[i] + rng.nextGaussian() * Math.sqrt(var);
		}
		return new AIParameters(ret);
	}

	AIParameters getMean() {
		return new AIParameters(Arrays.copyOf(means, dim));
	}

	public double avgVar() {
		return var;
	}
}

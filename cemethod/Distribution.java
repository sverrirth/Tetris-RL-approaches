package cemethod;

import java.util.Random;

/**
 * A distribution for use by the cross-entropy method.
 */
public interface Distribution {
	/**
	 * @return The variance of this distibution.
	 */
	double avgVar();

	/**
	 * @return The mean value of this distribution
	 */
	double[] getMean();

	/**
	 * @param rng The source of randomness to use.
	 * @return A sample from this distribution.
	 */
	double[] sample(Random rng);

}

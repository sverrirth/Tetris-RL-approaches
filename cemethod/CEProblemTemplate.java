package cemethod;

import java.util.BitSet;

/**
 * An interface capturing a function to optimize
 * with the cross-entropy method. Note:
 * <b>All functions must be thread-safe.</b>
 */
public interface CEProblemTemplate {
	/**
	 * @return The dimension of the problem space.
	 */
	int dimension();
	
	BitSet getFeatureSubset();

	/**
	 * This function may not mutate v.
	 * @param v
	 * @return The value of the function at v.
	 */
	double fitness(double[] v);
	
	int getNTrials();
	
	int runTrial(double[] param, boolean display);  //TODO: remove variable display
	
}

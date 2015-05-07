package cemethod;

/**
 * An interface capturing a function to optimize
 * with the cross-entropy method.
 */
public interface CEProblem {
	/**
	 * @return The dimension of the problem space.
	 */
	int dimension();

	/**
	 * @param v
	 * @return The value of the function at v.
	 */
	double fitness(double[] v);
}

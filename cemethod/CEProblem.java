package cemethod;

/**
 * An interface capturing a function to optimize
 * with the cross-entropy method. Note:
 * <b>All functions must be thread-safe.</b>
 */
public interface CEProblem {
	/**
	 * @return The dimension of the problem space.
	 */
	int dimension();

	/**
	 * This function may not mutate v.
	 * @param v
	 * @return The value of the function at v.
	 */
	double fitness(double[] v);
}

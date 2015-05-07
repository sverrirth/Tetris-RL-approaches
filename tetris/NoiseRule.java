package tetris;

/**
 * Specifies how much noise to add when using the cross-entropy method.
 */
public interface NoiseRule {
	/**
	 * @param generation
	 * @param totGenerations
	 * @return The noise level to use when at generation n,
	 * if the total number of generations are totGenerations.
	 */
	double getNoiseLevel(int n, int totGenerations);
}

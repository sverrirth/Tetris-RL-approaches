package tetris;

/**
 * A NoiseRule for which the noise changes linearly.
 */
public class LinearlyChangingNoise implements NoiseRule {
	/**
	 * The initial noise.
	 */
	private final double a;
	/**
	 * The final noise.
	 */
	private final double b;

	/**
	 * @param start
	 * @param end
	 */
	public LinearlyChangingNoise(double start, double end) {
		a = start;
		b = end;
	}

	@Override
	public double getNoiseLevel(int generation, int totGenerations) {
		return (totGenerations - generation) * (b - a) / totGenerations + a;
	}

}

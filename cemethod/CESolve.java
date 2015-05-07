package cemethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This utility class implements the cross-entropy method for optimization.
 */
public final class CESolve {
	private CESolve() {
	}

	/**
	 * @param sampleSizes The size of each generation.
	 * @param kept The number of samples kept from each generation.
	 * @param generations The total number of iterations.
	 * @param prob The problem to solve.
	 * @param r The source of randomness to use.
	 * @return The final value.
	 */
	public static double[] solve(int sampleSizes, int kept, int generations, CEProblem prob, Random r) {
		Point sample;
		Distribution d = new NormalDistribution(prob.dimension());
		for(int gen = 0; gen < generations; gen++) {
			List<Point> params = new ArrayList<Point>();
			for(int smpls = 0; smpls < sampleSizes; smpls++) {
				sample = new Point(d.sample(r));
				sample.performance = prob.fitness(sample.par);
				params.add(sample);
			}
			Collections.sort(params);
			d = new NormalDistribution(params.subList(0, kept), 2);

			System.out.println("Done with generation " + gen + ": ");
			for(Point p : params) {
				System.out.print((int)p.performance + " ");
			}
			System.out.println();
			System.out.println("New mean: " + Arrays.toString(d.getMean()));
			System.out.println("New variance: " + d.avgVar());
			System.out.println("New performance: " + (int)prob.fitness(d.getMean()) + "\n");
		}
		return d.getMean();
	}
}

package tetris;

import java.util.Arrays;

public class AIParameters implements Comparable<AIParameters> {
	public double[] par;
	public double estimatedPerformance;

	public AIParameters(double[] params) {
		par = params;
	}

	@Override
	public String toString() {
		return Arrays.toString(par);
	}

	@Override
	public int compareTo(AIParameters o) {
		return -new Double(estimatedPerformance).compareTo(new Double(o.estimatedPerformance));
	}
}

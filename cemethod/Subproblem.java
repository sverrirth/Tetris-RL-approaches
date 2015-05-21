package cemethod;

public class Subproblem {
	public CEProblem problem;
	public double[] parameters;
	public int index;

	public Subproblem(CEProblem p, double[] params, int i) {
		problem = p;
		parameters = params;
		index = i;
	}
}

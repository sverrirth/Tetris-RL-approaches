package cemethod;

public class Subproblem {
	public CEProblemTemplate problem;
	public double[] parameters;
	public int index;

	public Subproblem(CEProblemTemplate p, double[] params, int i) {
		problem = p;
		parameters = params;
		index = i;
	}
}

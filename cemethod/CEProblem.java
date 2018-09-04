package cemethod;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public abstract class CEProblem implements CEProblemTemplate {
	
	protected BitSet featureSubset;
	protected int nTrials;
	
	protected final static int N_VAR_TRIALS = 100;
	
	public void setFeatureSubset(BitSet featureSubset) throws RuntimeException {
		
		if(featureSubset.cardinality() != dimension()) {
			throw new RuntimeException("The feature subset needs to have the same "
					+ "cardinality as the number of dimensions");
		}
		
		this.featureSubset = featureSubset;
		
	}
	
	public BitSet getFeatureSubset() {
		
		return featureSubset;
		
	}
	
	public int getNTrials() {
		
		return nTrials;
		
	}
	
	/* (non-Javadoc)
	 * @see cemethod.CEProblem#fitness(double[])
	 */
	public double fitness(double[] v) {
		
		double perf = 0;
		for(int i = 0; i < nTrials; i++) {
			perf += runTrial(v, false);
		}
		perf /= nTrials;
		return perf;
	}
	
	public double fitness_variance(double[] v) {
		
		try {
		
		LinkedBlockingQueue<Subproblem> q;
		LinkedBlockingQueue<Perf> qq;
		
		q = new LinkedBlockingQueue<Subproblem>();
		qq = new LinkedBlockingQueue<Perf>();
		
		ArrayList<CEWorker> workers = new ArrayList<CEWorker>();
		
		for(int i = 0; i < 8; i++) {
			workers.add(new CEWorker(q, qq));
			workers.get(i).start();
		}
		
		double mean = 0;
		
		double[] perfs = new double[N_VAR_TRIALS];
		
		List<Point> params = new ArrayList<Point>();
		
		for(int i = 0; i < N_VAR_TRIALS; i++) {
			params.add(i, new Point(v));
		}
		for(int i = 0; i < N_VAR_TRIALS; i++) {
			q.add(new Subproblem(this, v, i));
		}
		
		for(int i = 0; i < N_VAR_TRIALS; i++) {
			Perf perf = qq.take();
			mean += perf.performance;
			perfs[i] = mean;
		}
		
		mean /= N_VAR_TRIALS;
		
		double var = 0;
		for(double pf : perfs) {
			var += (pf - mean) * (pf - mean);
		}
		var /= N_VAR_TRIALS;
		
		return Math.sqrt(var);
		
		} catch(InterruptedException e) {
			
			return 0.0;
			
		}
		
		
	}

}

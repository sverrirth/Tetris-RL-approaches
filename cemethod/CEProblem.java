package cemethod;

import java.util.BitSet;

public abstract class CEProblem implements CEProblemTemplate {
	
	protected BitSet featureSubset;
	protected int nTrials;
	
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

}

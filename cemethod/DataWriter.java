package cemethod;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DataWriter {
	
	private static final int EVAL_FREQ = 10;
	private static final int N_TRIALS = 50;
	
	private BufferedWriter writer;
	
	private CEProblemTemplate ceproblem;
	private MethodType ct;
	private CESolver ces;
	
	public DataWriter(CEProblemTemplate cept, CESolver ces) {

		this.ceproblem = cept;
		this.ct = ces.getCT();
		this.ces = ces;
		
		String fileName = String.format("methodsComparison/%1$s/%2$s-%3$s-%4$s/%5$s", 
				ct.toString(),
				ces.getNSamples(),
				ces.getNElitists(),
				cept.getNTrials(),
				new SimpleDateFormat("yyyy-MM-dd---HH-mm-ss").format(new Date()));
		
		File file = new File(fileName);
		file.getParentFile().mkdirs();
		
		try {
		
			this.writer = new BufferedWriter(new FileWriter(file));
			
		} catch (IOException e) {
			
			System.err.println("Error writing and/or finding file.");
			e.printStackTrace();
			System.exit(0);
			return;
			
		}
		
	}
	
	public void init() {
		
		try {
		
			String init = String.format("%1$s. Noise: (%2$s, %3$s)", 
					ceproblem, ces.getInitialNoise(), ces.getNoiseStep());
			
			writer.append(init);
			writer.newLine();
			writer.flush();
		
		} catch (IOException e) {
			
			System.err.println("Could not write to file.");
			e.printStackTrace();
			System.exit(0);
			return;
			
		}
		
	}
	
	public void writeData(Point bestPoint, int genIdx) {
		
		try {
		
			if (genIdx % EVAL_FREQ == 0) {
				
				//TODO: let an extra thread do this and write it in a separate file
				
				ArrayList<Integer> results = new ArrayList<Integer>();
				
				for(int i = 0; i < N_TRIALS; i++) {
					
					results.add(ceproblem.runTrial(bestPoint.par, false));
					
				}
				
				String perfInfo = String.format("AVG PERF OVER %1$s TRIALS: %2$s", 
						N_TRIALS, 
						results.stream().mapToInt(val -> val).average().orElse(0.0));
				
				writer.append(perfInfo);
				writer.newLine();
					
			}
			
			String info = String.format("Generation %1$s: %2$s --- %3$s", 
					genIdx,
					bestPoint.performance,
					bestPoint.toString().replace("[", "{").replace("]", "}"));
			
			writer.append(info);
			writer.newLine();
			writer.flush();
				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	public void end() {
		
		try {
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}

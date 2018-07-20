package tetris;

import com.github.sh0nk.matplotlib4j.Plot;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;

import java.io.IOException;
import java.util.Arrays;

public class TestClass {
	
	public static void main(String[] args) throws IOException, PythonExecutionException {
		testPlot();
		
	}

    public static void testPlot() throws IOException, PythonExecutionException {
        Plot plt = new PythonPlot(false);
        plt.plot()
           .add(Arrays.asList(1.3, 2))
           .label("label")
           .linestyle("--");
        plt.xlabel("xlabel");
        plt.ylabel("ylabel");
        plt.text(0.5, 0.2, "text");
        plt.title("Title!");
        plt.legend();
        plt.savefig("test.png");
        plt.show();
    }

}
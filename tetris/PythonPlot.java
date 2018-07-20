//Code from https://github.com/sh0nk. Lightly changed.

package tetris;

import java.io.IOException;
import java.util.LinkedList;

import com.github.sh0nk.matplotlib4j.*;
import com.github.sh0nk.matplotlib4j.PythonConfig;
import com.github.sh0nk.matplotlib4j.PythonExecutionException;
import com.github.sh0nk.matplotlib4j.builder.ArgsBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.Builder;
import com.github.sh0nk.matplotlib4j.builder.CLabelBuilder;
import com.github.sh0nk.matplotlib4j.builder.CLabelBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.ContourBuilder;
import com.github.sh0nk.matplotlib4j.builder.ContourBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.HistBuilder;
import com.github.sh0nk.matplotlib4j.builder.HistBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.LabelBuilder;
import com.github.sh0nk.matplotlib4j.builder.LabelBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.LegendBuilder;
import com.github.sh0nk.matplotlib4j.builder.LegendBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.PColorBuilder;
import com.github.sh0nk.matplotlib4j.builder.PColorBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.PlotBuilder;
import com.github.sh0nk.matplotlib4j.builder.PlotBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.SaveFigBuilder;
import com.github.sh0nk.matplotlib4j.builder.SaveFigBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.ScaleBuilder;
import com.github.sh0nk.matplotlib4j.builder.ScaleBuilderImpl;
import com.github.sh0nk.matplotlib4j.builder.TextBuilder;
import com.github.sh0nk.matplotlib4j.builder.TextBuilderImpl;
import com.google.common.base.Joiner;

public class PythonPlot implements Plot {
    LinkedList<Builder> registeredBuilders = new LinkedList<>();
    private LinkedList<Builder> showBuilders = new LinkedList<>();

    private final boolean dryRun;
    private final PythonConfig pythonConfig;

    PythonPlot(PythonConfig pythonConfig, boolean dryRun) {
        this.pythonConfig = pythonConfig;
        this.dryRun = dryRun;
    }

    PythonPlot(boolean dryRun) {
        this(PythonConfig.systemDefaultPythonConfig(), dryRun);
    }

    @Override
    public LegendBuilder legend() {
        LegendBuilder builder = new LegendBuilderImpl();
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public void title(String title) {
        registeredBuilders.add(new ArgsBuilderImpl("title", title));
    }

    @Override
    public LabelBuilder xlabel(String label) {
        LabelBuilder builder = LabelBuilderImpl.xLabelBuilder(label);
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public LabelBuilder ylabel(String label) {
        LabelBuilder builder = LabelBuilderImpl.yLabelBuilder(label);
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public ScaleBuilder xscale(ScaleBuilder.Scale scale) {
        ScaleBuilder builder = ScaleBuilderImpl.xScaleBuilder(scale);
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public ScaleBuilder yscale(ScaleBuilder.Scale scale) {
        ScaleBuilder builder = ScaleBuilderImpl.yScaleBuilder(scale);
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public void xlim(Number xmin, Number xmax) {
        registeredBuilders.add(new ArgsBuilderImpl("xlim", xmin, xmax));
    }

    @Override
    public void ylim(Number ymin, Number ymax) {
        registeredBuilders.add(new ArgsBuilderImpl("ylim", ymin, ymax));
    }

    @Override
    public TextBuilder text(double x, double y, String s) {
        TextBuilder builder = new TextBuilderImpl(x, y, s);
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public PlotBuilder plot() {
        PlotBuilder builder = new PlotBuilderImpl();
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public ContourBuilder contour() {
        ContourBuilder builder = new ContourBuilderImpl();
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public PColorBuilder pcolor() {
        PColorBuilder builder = new PColorBuilderImpl();
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public HistBuilder hist() {
        HistBuilder builder = new HistBuilderImpl();
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public CLabelBuilder clabel(ContourBuilder contour) {
        CLabelBuilder builder = new CLabelBuilderImpl(contour);
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public SaveFigBuilder savefig(String fname) {
        SaveFigBuilder builder = new SaveFigBuilderImpl(fname);
        registeredBuilders.add(builder);
        return builder;
    }

    @Override
    public void close() {
        registeredBuilders.add(new ArgsBuilderImpl("close"));
    }

    @Override
    public void close(String name) {
        registeredBuilders.add(new ArgsBuilderImpl("close", name));
    }

    @Override
    public void executeSilently() throws IOException, PythonExecutionException {
        LinkedList<String> scriptLines = new LinkedList<>();
        scriptLines.add("import numpy as np");
        scriptLines.add("import matplotlib as mpl");
        scriptLines.add("mpl.use('Agg')");
        scriptLines.add("import matplotlib.pyplot as plt");
        registeredBuilders.forEach(b -> scriptLines.add(b.build()));
        showBuilders.forEach(b -> scriptLines.add(b.build()));
        PyCommand command = new PyCommand(pythonConfig);
        command.execute(Joiner.on('\n').join(scriptLines));
    }

    /**
     * matplotlib.pyplot.show(*args, **kw)
     */
    @Override
    public void show() throws IOException, PythonExecutionException {
        LinkedList<String> scriptLines = new LinkedList<>();
        scriptLines.add("import numpy as np");
        if (dryRun) {
            // No need DISPLAY for test run
            scriptLines.add("import matplotlib as mpl");
            scriptLines.add("mpl.use('Agg')");
        }
        scriptLines.add("import matplotlib.pyplot as plt");
        registeredBuilders.forEach(b -> scriptLines.add(b.build()));

        // show
        if (!dryRun) {
            scriptLines.add("plt.show()");
        }

        PyCommand command = new PyCommand(pythonConfig);
        command.execute(Joiner.on('\n').join(scriptLines));

        // After showing, registered plot is cleared
        registeredBuilders.clear();
    }

}
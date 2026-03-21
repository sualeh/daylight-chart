package daylightchart.options.chart;

import daylightchart.daylightchart.chart.DaylightChart;
import java.util.Objects;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;

/** Applies chart options to charts and captures chart state as serializable options. */
public final class ChartOptionsService {

  public void applyChartOptions(final ChartOptions chartOptions, final JFreeChart chart) {
    Objects.requireNonNull(chart, "Chart must not be null");

    final ChartOptions effectiveChartOptions = chartOptions == null ? new ChartOptions() : chartOptions;
    if (chart instanceof ChartOptionsListener listener) {
      listener.beforeSettingChartOptions(effectiveChartOptions);
    }

    chart.setAntiAlias(effectiveChartOptions.antiAlias());
    chart.setBackgroundPaint(effectiveChartOptions.backgroundPaint());
    applyPlotOptions(effectiveChartOptions.plotOptions(), chart.getPlot());
    applyTitleOptions(effectiveChartOptions.titleOptions(), chart);

    if (chart instanceof ChartOptionsListener listener) {
      listener.afterSettingChartOptions(effectiveChartOptions);
    }
  }

  public ChartOptions captureChartOptions(final JFreeChart chart) {
    Objects.requireNonNull(chart, "Chart must not be null");

    return new ChartOptions(
        chart.getAntiAlias(),
        chart.getBackgroundPaint(),
        capturePlotOptions(chart.getPlot()),
        captureTitleOptions(chart));
  }

  public ChartOptions createDefaultChartOptions() {
    return captureChartOptions(new DaylightChart());
  }

  private void applyAxisOptions(final AxisOptions axisOptions, final Axis axis) {
    if (axis == null || axisOptions == null) {
      return;
    }

    axis.setLabel(axisOptions.label());
    if (axisOptions.labelFont() != null) {
      axis.setLabelFont(axisOptions.labelFont());
    }
    if (axisOptions.labelPaint() != null) {
      axis.setLabelPaint(axisOptions.labelPaint());
    }
    if (axisOptions.labelInsets() != null) {
      axis.setLabelInsets(axisOptions.labelInsets());
    }

    axis.setTickMarksVisible(axisOptions.tickMarksVisible());
    axis.setTickLabelsVisible(axisOptions.tickLabelsVisible());
    if (axisOptions.tickLabelFont() != null) {
      axis.setTickLabelFont(axisOptions.tickLabelFont());
    }
    if (axisOptions.tickLabelPaint() != null) {
      axis.setTickLabelPaint(axisOptions.tickLabelPaint());
    }
    if (axisOptions.tickLabelInsets() != null) {
      axis.setTickLabelInsets(axisOptions.tickLabelInsets());
    }
  }

  private void applyPlotOptions(final PlotOptions plotOptions, final Plot plot) {
    if (plot == null || plotOptions == null) {
      return;
    }

    plot.setBackgroundPaint(plotOptions.backgroundPaint());
    plot.setOutlinePaint(plotOptions.outlinePaint());
    plot.setOutlineStroke(plotOptions.outlineStroke());
    if (plotOptions.insets() != null) {
      plot.setInsets(plotOptions.insets());
    }

    applyAxisOptions(plotOptions.domainAxisOptions(), getDomainAxis(plot));
    applyAxisOptions(plotOptions.rangeAxisOptions(), getRangeAxis(plot));
  }

  private void applyTitleOptions(final TitleOptions titleOptions, final JFreeChart chart) {
    if (titleOptions == null) {
      return;
    }

    final TextTitle title = chart.getTitle();
    if (title != null) {
      if (titleOptions.titleFont() != null) {
        title.setFont(titleOptions.titleFont());
      }
      if (titleOptions.titlePaint() != null) {
        title.setPaint(titleOptions.titlePaint());
      }
      if (titleOptions.titleText() != null) {
        title.setText(titleOptions.titleText());
      }
    }
    if (!titleOptions.showTitle()) {
      chart.setTitle((TextTitle) null);
    }
  }

  private AxisOptions captureAxisOptions(final Axis axis) {
    if (axis == null) {
      return new AxisOptions();
    }

    return new AxisOptions(
        axis.getLabel(),
        axis.getLabelFont(),
        axis.getLabelInsets(),
        axis.getLabelPaint(),
        axis.getTickLabelFont(),
        axis.getTickLabelInsets(),
        axis.getTickLabelPaint(),
        axis.isTickLabelsVisible(),
        axis.isTickMarksVisible());
  }

  private PlotOptions capturePlotOptions(final Plot plot) {
    return new PlotOptions(
        plot.getBackgroundPaint(),
        plot.getOutlinePaint(),
        plot.getOutlineStroke(),
        plot.getInsets(),
        captureAxisOptions(getDomainAxis(plot)),
        captureAxisOptions(getRangeAxis(plot)));
  }

  private TitleOptions captureTitleOptions(final JFreeChart chart) {
    final TextTitle title = chart.getTitle();
    if (title == null) {
      return new TitleOptions(false, null, null, null);
    }

    return new TitleOptions(true, title.getFont(), title.getPaint(), title.getText());
  }

  private Axis getDomainAxis(final Plot plot) {
    if (plot instanceof CategoryPlot categoryPlot) {
      return categoryPlot.getDomainAxis();
    }
    if (plot instanceof XYPlot xyPlot) {
      return xyPlot.getDomainAxis();
    }
    return null;
  }

  private Axis getRangeAxis(final Plot plot) {
    if (plot instanceof CategoryPlot categoryPlot) {
      return categoryPlot.getRangeAxis();
    }
    if (plot instanceof XYPlot xyPlot) {
      return xyPlot.getRangeAxis();
    }
    return null;
  }
}

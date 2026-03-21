/*
 *
 * Daylight Chart
 * http://sualeh.github.io/DaylightChart
 * Copyright (c) 2007-2016, Sualeh Fatehi.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 *
 */
package daylightchart.options.chart;


import java.awt.Paint;
import java.awt.Stroke;
import java.io.Serial;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.Axis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.ui.RectangleInsets;

import daylightchart.options.chart.serialization.ChartOptionSerializers.PaintDeserializer;
import daylightchart.options.chart.serialization.ChartOptionSerializers.PaintSerializer;
import daylightchart.options.chart.serialization.ChartOptionSerializers.RectangleInsetsDeserializer;
import daylightchart.options.chart.serialization.ChartOptionSerializers.RectangleInsetsSerializer;
import daylightchart.options.chart.serialization.ChartOptionSerializers.StrokeDeserializer;
import daylightchart.options.chart.serialization.ChartOptionSerializers.StrokeSerializer;
import tools.jackson.databind.annotation.JsonDeserialize;
import tools.jackson.databind.annotation.JsonSerialize;

/**
 * Options for customizing charts.
 *
 * @author sfatehi
 */
public class PlotOptions
  extends BaseChartOptions
{

  @Serial
  private static final long serialVersionUID = 6801804849033048049L;

  @JsonSerialize(using = PaintSerializer.class)
  @JsonDeserialize(using = PaintDeserializer.class)
  private Paint backgroundPaint;
  @JsonSerialize(using = PaintSerializer.class)
  @JsonDeserialize(using = PaintDeserializer.class)
  private Paint outlinePaint;
  @JsonSerialize(using = StrokeSerializer.class)
  @JsonDeserialize(using = StrokeDeserializer.class)
  private transient Stroke outlineStroke;
  @JsonSerialize(using = RectangleInsetsSerializer.class)
  @JsonDeserialize(using = RectangleInsetsDeserializer.class)
  private RectangleInsets insets;
  //
  private final AxisOptions domainAxisOptions;
  private final AxisOptions rangeAxisOptions;

  /**
   * Constructor.
   */
  public PlotOptions()
  {
    domainAxisOptions = new AxisOptions();
    rangeAxisOptions = new AxisOptions();
  }

  /**
   * {@inheritDoc}
   *
   * @see BaseChartOptions#copyFromChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void copyFromChart(final JFreeChart chart)
  {
    final Plot plot = chart.getPlot();

    backgroundPaint = plot.getBackgroundPaint();
    outlinePaint = plot.getOutlinePaint();
    outlineStroke = plot.getOutlineStroke();
    insets = plot.getInsets();
    //
    // Update axes
    Axis domainAxis = null;
    if (plot instanceof CategoryPlot p)
    {
      domainAxis = p.getDomainAxis();
    }
    else if (plot instanceof XYPlot p)
    {
      domainAxis = p.getDomainAxis();
    }
    if (domainAxis != null)
    {
      domainAxisOptions.getAxisProperties(domainAxis);
    }

    Axis rangeAxis = null;
    if (plot instanceof CategoryPlot p)
    {
      rangeAxis = p.getRangeAxis();
    }
    else if (plot instanceof XYPlot p)
    {
      rangeAxis = p.getRangeAxis();
    }
    if (rangeAxis != null)
    {
      rangeAxisOptions.getAxisProperties(rangeAxis);
    }
  }

  /**
   * @return the backgroundPaint
   */
  public final Paint getBackgroundPaint()
  {
    return backgroundPaint;
  }

  /**
   * @return the domainAxisOptions
   */
  public final AxisOptions getDomainAxisOptions()
  {
    return domainAxisOptions;
  }

  /**
   * @return the insets
   */
  public final RectangleInsets getInsets()
  {
    return insets;
  }

  /**
   * @return the outlinePaint
   */
  public final Paint getOutlinePaint()
  {
    return outlinePaint;
  }

  /**
   * @return the outlineStroke
   */
  public final Stroke getOutlineStroke()
  {
    return outlineStroke;
  }

  /**
   * @return the rangeAxisOptions
   */
  public final AxisOptions getRangeAxisOptions()
  {
    return rangeAxisOptions;
  }

  /**
   * @param backgroundPaint
   *        the backgroundPaint to set
   */
  public final void setBackgroundPaint(final Paint backgroundPaint)
  {
    this.backgroundPaint = backgroundPaint;
  }

  /**
   * @param insets
   *        the insets to set
   */
  public final void setInsets(final RectangleInsets insets)
  {
    this.insets = insets;
  }

  /**
   * @param outlinePaint
   *        the outlinePaint to set
   */
  public final void setOutlinePaint(final Paint outlinePaint)
  {
    this.outlinePaint = outlinePaint;
  }

  /**
   * @param outlineStroke
   *        the outlineStroke to set
   */
  public final void setOutlineStroke(final Stroke outlineStroke)
  {
    this.outlineStroke = outlineStroke;
  }

  /**
   * {@inheritDoc}
   *
   * @see BaseChartOptions#updateChart(org.jfree.chart.JFreeChart)
   */
  @Override
  public void updateChart(final JFreeChart chart)
  {
    final Plot plot = chart.getPlot();

    plot.setBackgroundPaint(backgroundPaint);
    plot.setOutlinePaint(outlinePaint);
    plot.setOutlineStroke(outlineStroke);
    if (insets != null)
    {
      plot.setInsets(insets);
    }

    // Update axes
    Axis domainAxis = null;
    if (plot instanceof CategoryPlot p)
    {
      domainAxis = p.getDomainAxis();
    }
    else if (plot instanceof XYPlot p)
    {
      domainAxis = p.getDomainAxis();
    }
    if (domainAxis != null)
    {
      domainAxisOptions.setAxisProperties(domainAxis);
    }

    Axis rangeAxis = null;
    if (plot instanceof CategoryPlot p)
    {
      rangeAxis = p.getRangeAxis();
    }
    else if (plot instanceof XYPlot p)
    {
      rangeAxis = p.getRangeAxis();
    }
    if (rangeAxis != null)
    {
      rangeAxisOptions.setAxisProperties(rangeAxis);
    }

  }

}

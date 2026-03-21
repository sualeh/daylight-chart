package daylightchart.test.chart.options;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import daylightchart.chart.DaylightChart;
import daylightchart.chart.options.ChartOptions;
import daylightchart.chart.options.ChartOptionsService;
import java.awt.Color;
import org.junit.jupiter.api.Test;

class ChartOptionsServiceTest {

  @Test
  void shouldApplyChartOptionsToChart() {
    final ChartOptionsService service = new ChartOptionsService();
    final ChartOptions chartOptions = service.createDefaultChartOptions();
    final DaylightChart chart = new DaylightChart();

    service.applyChartOptions(chartOptions, chart);

    assertThat(chart.getBackgroundPaint(), is(chartOptions.getBackgroundPaint()));
  }

  @Test
  void shouldCreateDefaultChartOptionsFromRenderingDefaults() {
    final ChartOptionsService service = new ChartOptionsService();

    final ChartOptions chartOptions = service.createDefaultChartOptions();

    assertThat(chartOptions, is(notNullValue()));
    assertThat(chartOptions.getPlotOptions(), is(notNullValue()));
    assertThat(chartOptions.getPlotOptions().getBackgroundPaint(), is(instanceOf(Color.class)));
    assertThat(chartOptions.getTitleOptions().getShowTitle(), is(true));
  }
}

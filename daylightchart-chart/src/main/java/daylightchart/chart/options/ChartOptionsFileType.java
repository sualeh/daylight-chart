package daylightchart.chart.options;

import daylightchart.options.persistence.FileType;

/** Chart options file. */
class ChartOptionsFileType implements FileType {

  @Override
  public String getDescription() {
    return "Chart options file";
  }

  @Override
  public String getFileExtension() {
    return ".yaml";
  }
}

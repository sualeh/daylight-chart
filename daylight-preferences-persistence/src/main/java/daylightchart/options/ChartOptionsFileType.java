package daylightchart.options;

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

package daylightchart.options.chart.serialization;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Paint;
import java.awt.Stroke;
import org.jfree.chart.ui.RectangleInsets;
import tools.jackson.core.JsonGenerator;
import tools.jackson.core.JsonParser;
import tools.jackson.databind.DeserializationContext;
import tools.jackson.databind.SerializationContext;
import tools.jackson.databind.ValueDeserializer;
import tools.jackson.databind.ValueSerializer;

/** Jackson serializers for chart option value types that are not directly deserializable. */
public final class ChartOptionSerializers {

  public static final class FontDeserializer extends ValueDeserializer<Font> {
    @Override
    public Font deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      final String[] parts = parser.getValueAsString().split("\\|", -1);
      return new Font(parts[0], Integer.parseInt(parts[1]), Integer.parseInt(parts[2]));
    }
  }

  public static final class FontSerializer extends ValueSerializer<Font> {
    @Override
    public void serialize(
        final Font value, final JsonGenerator gen, final SerializationContext ctxt) {
      gen.writeString(value.getName() + "|" + value.getStyle() + "|" + value.getSize());
    }
  }

  public static final class PaintDeserializer extends ValueDeserializer<Paint> {
    @Override
    public Paint deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      final String value = parser.getValueAsString();
      final int rgba = (int) Long.parseLong(value.substring(1), 16);
      return new Color(rgba, true);
    }
  }

  public static final class PaintSerializer extends ValueSerializer<Paint> {
    @Override
    public void serialize(
        final Paint value, final JsonGenerator gen, final SerializationContext ctxt) {
      if (!(value instanceof Color)) {
        throw new IllegalArgumentException("Only Color paints are supported");
      }
      final Color color = (Color) value;
      gen.writeString("#%08X".formatted(color.getRGB()));
    }
  }

  public static final class RectangleInsetsDeserializer extends ValueDeserializer<RectangleInsets> {
    @Override
    public RectangleInsets deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      final String[] parts = parser.getValueAsString().split("\\|", -1);
      return new RectangleInsets(
          Double.parseDouble(parts[0]),
          Double.parseDouble(parts[1]),
          Double.parseDouble(parts[2]),
          Double.parseDouble(parts[3]));
    }
  }

  public static final class RectangleInsetsSerializer extends ValueSerializer<RectangleInsets> {
    @Override
    public void serialize(
        final RectangleInsets value, final JsonGenerator gen, final SerializationContext ctxt) {
      gen.writeString(
          value.getTop()
              + "|"
              + value.getLeft()
              + "|"
              + value.getBottom()
              + "|"
              + value.getRight());
    }
  }

  public static final class StrokeDeserializer extends ValueDeserializer<Stroke> {
    @Override
    public Stroke deserialize(final JsonParser parser, final DeserializationContext ctxt) {
      final String[] parts = parser.getValueAsString().split("\\|", -1);
      final float lineWidth = Float.parseFloat(parts[0]);
      final int endCap = Integer.parseInt(parts[1]);
      final int lineJoin = Integer.parseInt(parts[2]);
      final float miterLimit = Float.parseFloat(parts[3]);
      final float[] dashArray;
      if (parts[4].isEmpty()) {
        dashArray = null;
      } else {
        final String[] dashParts = parts[4].split(",", -1);
        dashArray = new float[dashParts.length];
        for (int i = 0; i < dashParts.length; i++) {
          dashArray[i] = Float.parseFloat(dashParts[i]);
        }
      }
      final float dashPhase = Float.parseFloat(parts[5]);
      return new BasicStroke(lineWidth, endCap, lineJoin, miterLimit, dashArray, dashPhase);
    }
  }

  public static final class StrokeSerializer extends ValueSerializer<Stroke> {
    @Override
    public void serialize(
        final Stroke value, final JsonGenerator gen, final SerializationContext ctxt) {
      if (!(value instanceof BasicStroke)) {
        throw new IllegalArgumentException("Only BasicStroke values are supported");
      }
      final BasicStroke stroke = (BasicStroke) value;
      final float[] dashArray = stroke.getDashArray();
      final StringBuilder dashBuilder = new StringBuilder();
      if (dashArray != null) {
        for (int i = 0; i < dashArray.length; i++) {
          if (i > 0) {
            dashBuilder.append(',');
          }
          dashBuilder.append(dashArray[i]);
        }
      }
      gen.writeString(
          stroke.getLineWidth()
              + "|"
              + stroke.getEndCap()
              + "|"
              + stroke.getLineJoin()
              + "|"
              + stroke.getMiterLimit()
              + "|"
              + dashBuilder
              + "|"
              + stroke.getDashPhase());
    }
  }

  private ChartOptionSerializers() {
    // Prevent instantiation
  }
}

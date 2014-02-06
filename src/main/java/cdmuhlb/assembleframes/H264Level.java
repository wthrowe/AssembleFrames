package cdmuhlb.assembleframes;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class H264Level {
  private final String level;
  private final int maxPixels;
  private final int maxPixelRate;
  private final int maxBitrate;
  private final int maxBitrateHigh;

  public String getLevel() { return level; }
  public int getMaxPixels() { return maxPixels; }
  public int getMaxPixelRate() { return maxPixelRate; }
  public int getMaxBitrate() { return maxBitrate; }
  public int getMaxBitrateHigh() { return maxBitrateHigh; }

  private H264Level(final String level, final int maxPixels,
      final int maxPixelRate, final int maxBitrate, final int maxBitrateHigh) {
    this.level = level;
    this.maxPixels = maxPixels;
    this.maxPixelRate = maxPixelRate;
    this.maxBitrate = maxBitrate;
    this.maxBitrateHigh = maxBitrateHigh;
  }
  
  public static H264Level LEVEL_4_1 = new H264Level(
      "4.1", 2097152,  62914560,  50000,  62500);

  private static final List<H264Level> levels = new ArrayList<H264Level>();
  static {
    levels.add(new H264Level("1",     25344,    380160,     64,     80));
    //levels.add(new H264Level("1b",    25344,    380160,    128,    160));
    levels.add(new H264Level("1.1",  101376,    768000,    192,    240));
    levels.add(new H264Level("1.2",  101376,   1536000,    384,    480));
    //levels.add(new H264Level("1.3",  101376,   3041280,    768,    960));
    levels.add(new H264Level("2",    101376,   3041280,   2000,   2500));
    levels.add(new H264Level("2.1",  202752,   5068800,   4000,   5000));
    levels.add(new H264Level("2.2",  414720,   5184000,   4000,   5000));
    levels.add(new H264Level("3",    414720,  10368000,  10000,  12500));
    levels.add(new H264Level("3.1",  921600,  27648000,  14000,  17500));
    levels.add(new H264Level("3.2", 1310720,  55296000,  20000,  25000));
    //levels.add(new H264Level("4",   2097152,  62914560,  20000,  25000));
    levels.add(LEVEL_4_1);
    levels.add(new H264Level("4.2", 2228224, 133693440,  50000,  62500));
    levels.add(new H264Level("5",   5652480, 150994944, 135000, 168750));
    levels.add(new H264Level("5.1", 9437184, 251658240, 240000, 300000));
    levels.add(new H264Level("5.1", 9437184, 530841600, 240000, 300000));
  }

  public static H264Level findLevel(final int width, final int height,
      final int fps) {
    final int nPixels = width*height;
    final int pixelRate = nPixels*fps;
    // TODO: binary search
    final Iterator<H264Level> it = levels.iterator();
    H264Level ans = null;
    while (it.hasNext()) {
      ans = it.next();
      if ((ans.getMaxPixels() >= nPixels) &&
          (ans.getMaxPixelRate() >= pixelRate)) {
        break;
      }
    }
    return ans;
  }
}

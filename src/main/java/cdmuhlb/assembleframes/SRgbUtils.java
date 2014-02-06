package cdmuhlb.assembleframes;

public final class SRgbUtils {
  public static final float reverseTransfer(final float v) {
    //if (v <= 0.04045f) return v/12.92f;
    //else return (float)Math.pow((v + 0.055f)/1.055f, 2.4f);
    if (v <= 0.04045f) return 0x1.3d0722p-4f*v;
    else return (float)Math.pow(0x1.e54edcp-1f*v + 0x1.ab1232p-5f, 2.4f);
  }
}

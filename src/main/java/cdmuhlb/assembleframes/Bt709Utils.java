package cdmuhlb.assembleframes;

public final class Bt709Utils {
  public static final float forwardTransfer(final float lc) {
    if (lc < 0.018f) return 4.5f*lc;
    else return 1.099f*(float)Math.pow(lc, 0.45f) - 0.099f;
  }

  public static final float eY(final float r, final float g, final float b) {
    return 0.2126f*r + 0.7152f*g + 0.0722f*b;
  }

  public static final float ePb(final float b, final float eY) {
    //return 0.5f*(b - eY) / 0.9278f;
    return 0x1.13ebeap-1f*(b - eY);
  }

  public static final float ePr(final float r, final float eY) {
    //return 0.5f*(r - eY) / 0.7874f;
    return 0x1.451ee2p-1f*(r - eY);
  }

  public static final byte encodeY(final float eY) {
    return (byte)Math.round(219.0f*eY + 16.0f);
  }

  public static final byte encodeC(final float eP) {
    return (byte)Math.round(224.0f*eP + 128.0f);
  }
}

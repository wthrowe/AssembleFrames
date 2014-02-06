package cdmuhlb.assembleframes;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;

public class FrameEncoder {
  private final int width;
  private final int height;
  private final int nPixels;
  private final int subWidth;
  private final int subHeight;
  private final int subNPixels;
  private final int[] rgbBuf;
  private final float[] rBuf;
  private final float[] gBuf;
  private final float[] bBuf;
  private final float[] subRBuf;
  private final float[] subGBuf;
  private final float[] subBBuf;
  
  private final byte[] yBytes;
  private final byte[] cbBytes;
  private final byte[] crBytes;

  public int getWidth() { return width; }
  public int getHeight() { return height; }

  public FrameEncoder(final int width, final int height) {
    this.width = width;
    this.height = height;

    nPixels = width*height;
    subWidth = (width - 1)/2 + 1;
    subHeight = (height - 1)/2 + 1;
    subNPixels = subWidth*subHeight;

    rgbBuf = new int[nPixels];

    rBuf = new float[nPixels];
    gBuf = new float[nPixels];
    bBuf = new float[nPixels];
    subRBuf = new float[subNPixels];
    subGBuf = new float[subNPixels];
    subBBuf = new float[subNPixels];

    yBytes = new byte[nPixels];
    cbBytes = new byte[subNPixels];
    crBytes = new byte[subNPixels];
  }

  public static FrameEncoder createEncoder(final File exampleImage)
      throws IOException {
    final BufferedImage img = ImageIO.read(exampleImage);
    return new FrameEncoder(img.getWidth(), img.getHeight());
  }

  public void encodeFrame(final File image, final OutputStream out)
      throws IOException {
    final BufferedImage img = ImageIO.read(image);
    assert(img.getWidth() == width);
    assert(img.getHeight() == height);
    img.getRGB(0, 0, width, height, rgbBuf, 0, width);

    // Extract linear RGB
    for (int i=0; i<nPixels; ++i) {
      int c = rgbBuf[i];
      bBuf[i] = SRgbUtils.reverseTransfer((c & 0xff) / 255.0f);
      gBuf[i] = SRgbUtils.reverseTransfer(((c >>> 8) & 0xff) / 255.0f);
      rBuf[i] = SRgbUtils.reverseTransfer(((c >>> 16) & 0xff) / 255.0f);
    }

    // Compute luma
    for (int i=0; i<nPixels; ++i) {
      final float r = Bt709Utils.forwardTransfer(rBuf[i]);
      final float g = Bt709Utils.forwardTransfer(gBuf[i]);
      final float b = Bt709Utils.forwardTransfer(bBuf[i]);
      yBytes[i] = Bt709Utils.encodeY(Bt709Utils.eY(r, g, b));
    }

    // TODO: Can invoke a non-blocking write of yBytes here

    // Subsample linear RGB
    subsampleLoc0(rBuf, subRBuf);
    subsampleLoc0(gBuf, subGBuf);
    subsampleLoc0(bBuf, subBBuf);

    // Compute chroma
    for (int i=0; i<subNPixels; ++i) {
      final float r = Bt709Utils.forwardTransfer(subRBuf[i]);
      final float g = Bt709Utils.forwardTransfer(subGBuf[i]);
      final float b = Bt709Utils.forwardTransfer(subBBuf[i]);
      final float eY = Bt709Utils.eY(r, g, b);
      cbBytes[i] = Bt709Utils.encodeC(Bt709Utils.ePb(b, eY));
      crBytes[i] = Bt709Utils.encodeC(Bt709Utils.ePr(r, eY));
    }

    // Write output
    out.write(yBytes);
    out.write(cbBytes);
    out.write(crBytes);
  }
  
  private void subsampleLoc0(final float[] src, final float[] dst) {
    assert(src.length == nPixels);
    assert(dst.length == subNPixels);
    // Assumes that width and height are even
    
    for (int i=0; i<subHeight; ++i) {
      final int idxAbove = 2*i*width;
      final int idxBelow = (2*i + 1)*width;
      final int dstIdx = i*subWidth;
      assert((2*i + 1) < height);

      // First column
      dst[dstIdx] = 0.25f*(
            src[idxAbove] + src[idxAbove+1] +
            src[idxBelow] + src[idxBelow+1]);

      // Remaining columns
      for (int j=1; j<subWidth; ++j) {
        final int jj = 2*j;
        dst[dstIdx+j] = 0.125f*(
            src[idxAbove+jj-1] + 2.0f*src[idxAbove+jj] + src[idxAbove+jj+1] +
            src[idxBelow+jj-1] + 2.0f*src[idxBelow+jj] + src[idxBelow+jj+1]);
      }
    }
  }
}

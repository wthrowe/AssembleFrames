package cdmuhlb.assembleframes;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class AssembleFrames {
  public static void main(final String[] args) throws IOException {
    if (args.length < 1) {
      System.err.println("Usage: java -jar assembleframes-<version>.jar <output_prefix> <fps> <input_filename>...");
      System.exit(1);
    }

    final String outputPrefix = args[0];
    int fps = 0;
    try {
      fps = Integer.parseInt(args[1]);
    } catch (NumberFormatException nfe) {
      System.err.println("Error: Could not parse <fps>=" + args[1] +
          " as an integer");
      System.exit(1);
    }
    FrameEncoder encoder = FrameEncoder.createEncoder(new File(args[2]));
    final int width = encoder.getWidth();
    final int height = encoder.getHeight();
    if ((width%2 != 0) || (height%2 != 0)) {
      System.err.println("Error: Width (" + width + ") and height (" + height +
          ") of <input_filename>=" + args[2] + " must be even");
      System.exit(1);
    }
    //final H264Level level = H264Level.findLevel(width, height, fps);
    final H264Level level = H264Level.LEVEL_4_1;

    ProcessBuilder pb = new ProcessBuilder("x264", "--crf", "13",
        "--preset", "veryslow", "--tune", "animation",
        "--fps", Integer.toString(fps), "--keyint", Integer.toString(fps),
        "--profile", "high", "--level", level.getLevel(),
        "--vbv-maxrate", Integer.toString(level.getMaxBitrateHigh()),
        "--vbv-bufsize", Integer.toString(level.getMaxBitrateHigh()),
        "--non-deterministic",
        //"--quiet", "--no-progress",
        "--sar", "1:1", "--overscan", "show",
        "--range", "tv", "--colorprim", "bt709", "--transfer", "bt709",
        "--colormatrix", "bt709", "--chromaloc", "0",
        "--output", outputPrefix + ".mp4",
        "--demuxer", "raw", "--input-csp", "i420", "--input-depth", "8",
        "--input-range", "tv",
        "--input-res", encoder.getWidth() + "x" + encoder.getHeight(),
        "-");
    pb.redirectErrorStream(true);
    pb.redirectOutput(ProcessBuilder.Redirect.INHERIT);
    final Process proc = pb.start();
    
    final OutputStream out = proc.getOutputStream();
    for (int i=2; i<args.length; ++i) {
      encoder.encodeFrame(new File(args[i]), out);
    }
    out.close();

    boolean finished = false;
    while (!finished) {
      try {
        proc.waitFor();
        finished = true;
      } catch (InterruptedException ie) {
        ie.printStackTrace();
        // swallow the exception
      }
    }
    final int x264Status = proc.exitValue();
    if (x264Status != 0) System.exit(x264Status);
  }
}

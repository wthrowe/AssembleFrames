AssembleFrames
==============

Assemble a sequence of image files into a video file via an intermediate YCbCr
stream.  The interpretation of the input and output data is precisely defined
and is specified in the Video Usability Information (VUI) in the output file.

In particular, the input images (which can be in any format understood by your
JVM's Image I/O subsystem) are first converted to the sRGB color space (the
conversion is handled by Java's `BufferedImage` class).  The data is then
transformed to a linear RGB space in which subsampling is performed.  Chroma
subsamples are located at the nominal sites for H.264 4:2:0 (code `0`).  Data at
luma and chroma sites is finally decomposed into YCbCr and encoded as 8-bit
samples according to BT.709.  This raw YCrCb 4:2:0 stream is passed to `x264`
for encoding as H.264 in an MP4 container.

Dependencies
------------

_AssembleFrames_ is written in Java and therefore requires a Java SE 7 (or
later) JVM to run.  Video encoding additionally requires that
[x264](http://www.videolan.org/developers/x264.html) be installed and visible
in your `$PATH`.

Building _AssembleFrames_ requires a Java SE 7 (or later) JDK.  Building with
[SBT](http://www.scala-sbt.org/) requires an `sbt` launcher compatible with
version 0.13.1.

Usage
-----

To compile _AssembleFrames_ using SBT, simply execute `sbt package`.  This will
produce a JAR file at `target/assembleframes-<version>.jar`.  To run
_AssembleFrames_, execute a command like the following:

    java -jar assembleframes-<version>.jar <output_prefix> <fps> <input_filename>...

Here, `<input_filenames>...` is a list of image files that should be assembled
(in order) into a video.  The video file will be written to
`<output_prefix>.mp4`, and the framerate will be `<fps>` frames per second.

Currently, encoder options are hard-coded in `AssembleFrames.java` and may
require modification to best meet your needs.

Technical details
-----------------

Chroma subsampling is performed using the following filtering and interpolation
kernel coefficients:

    | 0.125 | 0.25  | 0.125 |
    | 0.125 | 0.25  | 0.125 |

Reflective boundary conditions are assumed for the first column (even about the
first column).

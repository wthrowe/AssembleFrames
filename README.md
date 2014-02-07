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

Building _AssembleFrames_ requires a Java SE 7 (or later) JDK and an
[SBT](http://www.scala-sbt.org/) launcher compatible with version 0.13.1.

Usage
-----

To create an `assembleframes` executable, simply execute
`sbt universal:package-bin`.  This will produce a ZIP file at
`target/universal/assembleframes-<version>.zip`.  To run
_AssembleFrames_, unzip this archive, add its `bin` subdirectory to your
`$PATH`, and execute a command of the following form:

    assembleframes <output_prefix> <fps> <input_filename>...

Here, `<input_filenames>...` is a list of image files that should be assembled
(in the order listed) into a video.  The video file will be written to
`<output_prefix>.mp4`, and the framerate will be `<fps>` frames per second.

Build-time encoder options are specified in `application.conf`.  These may be
overridden at runtime by defining the corresponding properties for the JVM with
command line flags of the form `-Dcdmuhlb.assembleframes.<property>=<value>`.

Technical details
-----------------

Chroma subsampling is performed using the following filtering and interpolation
kernel coefficients:

    0.125 0.25 0.125
    0.125 0.25 0.125

Reflective boundary conditions are assumed for the first column (that is, when
subsampling in column 0, column -1 is assumed to be equal to column 1).

Performance notes
-----------------

Performance is currently limited by Java's Image I/O library, which is very slow
at both reading a PNG file and filling an array with its pixel data.

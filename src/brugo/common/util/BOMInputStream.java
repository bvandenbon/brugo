package brugo.common.util;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

class BOMInputStream extends FilterInputStream {
  private final boolean include;
  private final List<ByteOrderMark> boms;
  private ByteOrderMark byteOrderMark;
  private int[] firstBytes;
  private int fbLength;
  private int fbIndex;
  private int markFbIndex;
  private boolean markedAtStart;

  private static final Comparator<ByteOrderMark> BOM_LENGTH_COMPARATOR = (bom1, bom2) -> {
    int len1 = bom1.length();
    int len2 = bom2.length();
    return Integer.compare(len2, len1);
  };

  public BOMInputStream(InputStream delegate, ByteOrderMark... boms) {
    this(delegate, false, boms);
  }

  public BOMInputStream(InputStream delegate, boolean include, ByteOrderMark... boms) {
    super(delegate);

    // bom parameter is mandatory
    if (boms == null || boms.length == 0)
      throw new IllegalArgumentException("No BOMs specified");

    this.include = include;
    Arrays.sort(boms, BOM_LENGTH_COMPARATOR);
    this.boms = Arrays.asList(boms);

  }

  public boolean hasBOM() throws IOException {
    return getBOM() != null;
  }

  public boolean hasBOM(ByteOrderMark bom) throws IOException {
    if (!boms.contains(bom))
      throw new IllegalArgumentException("Stream not configure to detect " + bom);

    return (byteOrderMark != null) && getBOM().equals(bom);
  }

  public ByteOrderMark getBOM() throws IOException {
    if (firstBytes == null) {
      fbLength = 0;
      int maxBomSize = boms.get(0).length();
      firstBytes = new int[maxBomSize];

      for (int i = 0; i < firstBytes.length; ++i) {
        firstBytes[i] = in.read();
        ++fbLength;
        if (firstBytes[i] < 0) break;
      }

      byteOrderMark = find();
      if (byteOrderMark != null && !include) {
        if (byteOrderMark.length() < firstBytes.length) {
          fbIndex = byteOrderMark.length();
        } else {
          fbLength = 0;
        }
      }
    }

    return byteOrderMark;
  }

  public String getBOMCharsetName() throws IOException {
    getBOM();
    return (byteOrderMark == null) ? null : byteOrderMark.getCharsetName();
  }

  private int readFirstBytes() throws IOException {
    getBOM();
    return fbIndex < fbLength ? firstBytes[fbIndex++] : -1;
  }

  private ByteOrderMark find() {
    for (ByteOrderMark bom : boms)
      if (matches(bom)) return bom;
    return null;
  }

  private boolean matches(ByteOrderMark bom) {
    for (int i = 0; i < bom.length(); ++i)
      if (bom.get(i) != firstBytes[i]) return false;

    return true;
  }

  @Override
  public int read() throws IOException {
    int b = readFirstBytes();
    return b >= 0 ? b : in.read();
  }

  @Override
  public int read(byte[] buf, int off, int len) throws IOException {
    int firstCount = 0;
    int b = 0;

    while (len > 0 && b >= 0) {
      b = readFirstBytes();
      if (b >= 0) {
        buf[off++] = (byte) (b & 255);
        --len;
        ++firstCount;
      }
    }

    int secondCount = in.read(buf, off, len);
    return secondCount < 0 ? (firstCount > 0 ? firstCount : -1) : firstCount + secondCount;
  }

  @Override
  public int read(byte[] buf) throws IOException {
    return read(buf, 0, buf.length);
  }

  @Override
  public synchronized void mark(int readlimit) {
    markFbIndex = fbIndex;
    markedAtStart = firstBytes == null;
    in.mark(readlimit);
  }

  @Override
  public synchronized void reset() throws IOException {
    fbIndex = markFbIndex;
    if (markedAtStart) firstBytes = null;

    in.reset();
  }

  @Override
  public long skip(long n) throws IOException {
    int skipped;
    for (skipped = 0; (n > (long) skipped) && (readFirstBytes() >= 0); ++skipped) {
      ;
    }

    return in.skip(n - (long) skipped) + (long) skipped;
  }
}

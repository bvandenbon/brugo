package brugo.common.util;

import java.io.*;

public final class FileUtil {
  private FileUtil() {
  }

  public static String readFully(File file, String encoding) {
    try {
      FileInputStream inputStream = new FileInputStream(file);
      return readFully(inputStream, encoding);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  public static String readFully(InputStream inputStream, String encoding) {
    StringBuilder contents = new StringBuilder();

    try {
      InputStream bomInputStream = new BOMInputStream(inputStream, ByteOrderMark.UTF_8, ByteOrderMark.UTF_16BE,
          ByteOrderMark.UTF_16LE, ByteOrderMark.UTF_32BE, ByteOrderMark.UTF_32LE);

      Reader reader = (encoding == null) ? new InputStreamReader(bomInputStream) : new InputStreamReader(bomInputStream, encoding);
      BufferedReader input = new BufferedReader(reader);
      try {
        /*
         * readLine is a bit quirky :
         * it returns the content of a line MINUS the newline.
         * it returns null only for the END of the stream.
         * it returns an empty String if two newlines appear in a row.
         */
        int character;
        while ((character = input.read()) != -1) {
          contents.append((char) character);
        }
      } finally {
        input.close();
      }
    } catch (IOException ex) {
      return null;
    }

    return contents.toString();
  }
}

import java.util.zip.Adler32;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class Mitigation
{
  /** Interleaves an array of square length, essentially performing a matrix transpose. This method is it's own inverse.
   * @param data The data to interleave, length must natural square
   * @param rows number of rows in the data
   * @param columns number of columns in the data
   */
  public static void interleave(byte[] data, final int rows, final int columns)
  {
    for (int r = 0; r < rows; ++r)
    {
      for (int c = r; c < columns; ++c)
      {
        final int x = r*rows+c;
        final int y = c*rows+r;
        byte t = data[x];
        data[x] = data[y];
        data[y] = t;
      }
    }
  }

  /**
   * computes the checksum for data based on the currently used checksum algorithm
   * @param data data to compute checksum for
   * @return the computed checksum
   */
  public static int checksum(byte[] data)
  {
    chk.reset();
    chk.update(data, 0, data.length);
    return (int)chk.getValue();
  }

  public static void useCRC32()
  {
    useChecksums = true;
    chk = new CRC32();
  }
  public static void useAdler32()
  {
    useChecksums = true;
    chk = new Adler32();
  }

  public static boolean useInterleaving = false;
  public static final int INTERLEAVE_ROWS = 32;
  public static final int INTERLEAVE_COLUMNS = 32;

  public static boolean useChecksums = false;
  private static Checksum chk = new CRC32();
}

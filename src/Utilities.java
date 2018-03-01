import java.nio.ByteBuffer;

public class Utilities
{
  public static byte[] longToBytes(long l)
  {
    buffl.clear();
    buffl.putLong(0, l);
    return buffl.array();
  }

  public static long bytesToLong(byte[] b)
  {
    buffl.clear();
    buffl.put(b, 0, b.length);
    buffl.flip();
    return buffl.getLong();
  }

  public static byte[] intToBytes(int i)
  {
    buffi.clear();
    buffi.putInt(0, i);
    return buffi.array();
  }

  public static int bytesToInt(byte[] b)
  {
    buffi.clear();
    buffi.put(b, 0, b.length);
    buffi.flip();
    return buffi.getInt();
  }

  private static ByteBuffer buffl = ByteBuffer.allocate(Long.BYTES);
  private static ByteBuffer buffi = ByteBuffer.allocate(Integer.BYTES);
}

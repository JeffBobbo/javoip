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

  public static byte[] intToBytes(int n)
  {
    byte[] bytes = new byte[Integer.BYTES];
    for (int i = 0; i < Integer.BYTES; ++i)
      bytes[i] = (byte)(n >> (8 * i));
    return bytes;
  }

  public static int bytesToInt(byte[] b)
  {
    int n = 0;
    for (int i = 0; i < Integer.BYTES; ++i)
      n |= b[i] << (8 * i);
    return n;
  }

  private static ByteBuffer buffl = ByteBuffer.allocate(Long.BYTES);
  private static ByteBuffer buffi = ByteBuffer.allocate(Integer.BYTES);
}

import java.nio.ByteBuffer;

public class Utilities
{
  public static byte[] longToBytes(long l)
  {
    buffer.clear();
    buffer.putLong(0, l);
    return buffer.array();
  }

  public static long bytesToLong(byte[] b)
  {
    buffer.clear();
    buffer.put(b, 0, b.length);
    buffer.flip();
    return buffer.getLong();
  }

  private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
}

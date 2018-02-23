
public class Mitigation
{
  /**
   * Interleaves an array of square length, essentially performing a matrix transpose. This method is it's own inverse.
   * @param data The data to interleave, length must natural square
   * @throws IllegalArgumentException when length of data is not a natural square
   */
  public static void interleave(byte[] data) throws IllegalArgumentException
  {
    int n = (int)Math.sqrt(data.length);
    if (n*n != data.length)
      throw new IllegalArgumentException("Data is not of a squared length");

    for (int i = 0; i < n; ++i)
    {
      for (int j = i; j < n; ++j)
      {
        final int x = i*n+j;
        final int y = j*n+i;
        byte t = data[x];
        data[x] = data[y];
        data[y] = t;
      }
    }
  }

  public static boolean useInterleaving = false;
}

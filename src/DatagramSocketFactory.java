import com.sun.javaws.exceptions.InvalidArgumentException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import java.net.DatagramSocket;
import java.net.SocketException;

public class DatagramSocketFactory
{
  public enum SocketType
  {
    STOCK,
    DATAGRAMSOCKET_2, // causes packet loss (~25%)
    DATAGRAMSOCKET_3, // causes packet loss (~16%) and rearrangement (~50% of non-dropped packets)
    DATAGRAMSOCKET_4  // sets a quarter of the packet data to random data 10% of the time.
  }

  public static DatagramSocket make(final SocketType type) throws SocketException
  {
    switch (type)
    {
      case STOCK:
        return new DatagramSocket();
      case DATAGRAMSOCKET_2:
        return new DatagramSocket2();
      case DATAGRAMSOCKET_3:
        return new DatagramSocket3();
      case DATAGRAMSOCKET_4:
        return new DatagramSocket4();
    }
    throw new SocketException("Unknown SocketType");
  }

  public static SocketType getType(final int type) throws SocketException
  {
    switch (type)
    {
      case 1:
        return SocketType.STOCK;
      case 2:
        return SocketType.DATAGRAMSOCKET_2;
      case 3:
        return SocketType.DATAGRAMSOCKET_3;
      case 4:
        return SocketType.DATAGRAMSOCKET_4;
      default:
        throw new SocketException("Type must be in range [1, 4]");
    }
  }
}

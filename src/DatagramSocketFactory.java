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
    BEN_1,
    BEN_2,
    BEN_3
  }

  public static DatagramSocket produce(final SocketType type) throws SocketException
  {
    switch (type)
    {
      case STOCK:
        return new DatagramSocket();
      case BEN_1:
        return new DatagramSocket2();
      case BEN_2:
        return new DatagramSocket3();
      case BEN_3:
        return new DatagramSocket4();
    }
    throw new SocketException("Unknown SocketType");
  }

  public static DatagramSocket produce(final SocketType type, final int port) throws SocketException
  {
    switch (type)
    {
      case STOCK:
        return new DatagramSocket(port);
      case BEN_1:
        return new DatagramSocket2(port);
      case BEN_2:
        return new DatagramSocket3(port);
      case BEN_3:
        return new DatagramSocket4(port);
    }
    throw new SocketException("Unknown SocketType");
  }
}

import java.net.*;
import java.io.*;

public class CommunicatorUp
{
  public CommunicatorUp(final String host, final int port)
  {
    this.host = host;
    this.port = port;

    client = null;
    try
    {
      if (host == null)
        client = InetAddress.getByName(host);
      else
        client = InetAddress.getByName("localhost");  //CHANGE localhost to IP or NAME of client machine
    }
    catch (UnknownHostException e)
    {
      System.out.println("ERROR: CommunicatorUp: Could not find client: " + e.getMessage());
      System.exit(1);
    }

    try
    {
      socket = DatagramSocketFactory.make(DatagramSocketFactory.SocketType.STOCK);
      final int IPTOS_LOWCOST     = 0x02;
      final int IPTOS_RELIABILITY = 0x04;
      final int IPTOS_THROUGHPUT  = 0x08;
      final int IPTOS_LOWDELAY    = 0x10;
      socket.setTrafficClass(IPTOS_LOWDELAY | IPTOS_THROUGHPUT | IPTOS_LOWCOST);
    }
    catch (SocketException e)
    {
      System.out.println("ERROR: CommunicatorUp: Failed to open socket: " + e.getMessage());
      System.exit(1);
    }
  }

  public void setSocketType(DatagramSocketFactory.SocketType type)
  {
    try
    {
      DatagramSocket sock = DatagramSocketFactory.make(type);
      socket.close();
      socket = sock;
    }
    catch (SocketException e)
    {
      System.err.println("ERROR: Failed to switch to new socket type, continuing on old");
    }
  }

  public void send(final byte[] data) throws IOException
  {
    DatagramPacket dp = new DatagramPacket(data, data.length, client, port);
    socket.send(dp);
  }

  public void close()
  {
    socket.close();
  }

  /**
   * @return returns the number of packets sent by this CommunicatorUp.
   */
  public int packetsSent()
  {
    return pcount;
  }

  private int pcount;
  private DatagramSocket socket;
  private String host;
  private InetAddress client;
  private int port;
}
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

/**
 * CommunicatorDown provides a down-link for receiving data from a remote device.
 */
public class CommunicatorDown implements Runnable
{
  public CommunicatorDown(final int port)
  {
    try
    {
      socket = new DatagramSocket(port);
    }
    catch (SocketException e)
    {
      System.out.println("ERROR: Failed to open socket: "+ e.getMessage());
    }
    this.port = port;
  }

  @Override
  public void run()
  {

    Thread thread = new Thread(this);
    thread.start();
  }

  private DatagramSocket socket;
  private int port;
}

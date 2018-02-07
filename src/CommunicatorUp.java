import java.io.IOException;
import java.net.*;

/**
 * CommunicatorUp provides an up-link for sending data to a remote host
 */
public class CommunicatorUp implements Runnable
{
  public CommunicatorUp(final String host, final int port)
  {
    run = false;
    this.port = port;
    try
    {
      this.host = InetAddress.getByName(host);
    }
    catch (UnknownHostException e)
    {
      System.out.println("ERROR: Unknown host");
    }
  }

  public void start()
  {
    try
    {
      this.socket = new DatagramSocket();
    }
    catch (SocketException e)
    {
      System.out.println("ERROR: Failed to open socket: " + e.getMessage());
      return; // bail early, instead of starting a bad thread
    }

    run = true;
    Thread thread = new Thread(this);
    thread.start();
  }

  public void close()
  {
    run = false;
  }

  @Override
  public void run()
  {
    while (run)
    {
      byte[] buff = new byte[80];
      DatagramPacket dp = new DatagramPacket(buff, 0, 80);

      try
      {
        socket.receive(dp);
      }
      catch (IOException e)
      {
        continue;
      }

      String str = new String(buff);
      System.out.println(str);
    }
  }

  public void send(final byte[] buff) throws IOException
  {
    DatagramPacket dp = new DatagramPacket(buff, buff.length, host, port);
    socket.send(dp);
  }


  private DatagramSocket socket;
  private InetAddress host;
  private int port;
  private boolean run;
}

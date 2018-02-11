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
      socket = new DatagramSocket();
    }
    catch (SocketException e)
    {
      System.out.println("ERROR: CommunicatorUp: Failed to open socket: " + e.getMessage());
      System.exit(1);
    }

  }

  public void sendText(final String message) throws IOException
  {
    //Convert it to an array of bytes
    byte[] buff = new byte[message.getBytes().length+1];
    buff[0] = 0;
    System.arraycopy(message.getBytes(), 0, buff, 1, message.getBytes().length);

    DatagramPacket dp = new DatagramPacket(buff, buff.length, client, port);
    socket.send(dp);
  }

  public void sendAudio(final byte[] block) throws IOException
  {
    byte[] buff = new byte[block.length+1];
    buff[0] = 1;
    System.arraycopy(block, 0, buff, 1, block.length);

    DatagramPacket dp = new DatagramPacket(buff, buff.length, client, port);
    socket.send(dp);
  }

  public void close()
  {
    socket.close();
  }

  private DatagramSocket socket;
  private String host;
  private InetAddress client;
  private int port;
}
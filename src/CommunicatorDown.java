import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class CommunicatorDown implements Runnable
{
  public CommunicatorDown(final int port)
  {
    this.port = port;
    running = true;
    queue = new LinkedList<>();
    pcount = 0;
  }
  public void start()
  {
    Thread thread = new Thread(this);
    thread.start();
  }

  public void run()
  {
    try
    {
      socket = new DatagramSocket(port);
      socket.setSoTimeout(SOCKET_WAIT);
    }
    catch (SocketException e)
    {
      System.out.println("ERROR: CommunicatorDown: Failed to open socket: " + e.getMessage());
      System.exit(1);
    }

    while (running)
    {
      byte[] buffer = new byte[JaVoIP.PACKET_SIZE];
      DatagramPacket packet = new DatagramPacket(buffer, 0, JaVoIP.PACKET_SIZE);

      try
      {
        socket.receive(packet);
        ++pcount;
        byte[] header = Arrays.copyOfRange(buffer, 0, JaVoIP.HEADER_SIZE);
        byte[] payload = Arrays.copyOfRange(buffer, JaVoIP.HEADER_SIZE, buffer.length);
        if ((header[0] & 0xF0) != 0)
          Mitigation.interleave(payload);
        if ((header[0] & 0xF) != 0) // header corrupt, skip it
          continue;
        queue.offer(payload);
      }
      catch (SocketTimeoutException | IllegalArgumentException ignore)
      {
      }
      catch (IOException e)
      {
        System.out.println("ERROR: CommunicatorDown: IOException: " + e.getMessage());
      }
    }
    socket.close();
  }



  public void close()
  {
    running = false;
  }

  public int size()
  {
    return queue.size();
  }

  public byte[] peek()
  {
    return queue.peek();
  }

  public byte[] poll()
  {
    return queue.poll();
  }

  /**
   * @return returns the number of packets received by this CommunicatorDown
   */
  public int packetsReceived()
  {
    return pcount;
  }

  private int pcount;

  private final int SOCKET_WAIT = 10;
  private volatile boolean running;
  private DatagramSocket socket;
  private Queue<byte[]> queue;
  private int port;
}
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;
import java.util.zip.CRC32;

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
        if (Mitigation.useInterleaving)
          Mitigation.interleave(payload);

        long sum_r = Utilities.bytesToLong(header);
        CRC32 chk = new CRC32();
        chk.update(payload);
        long sum_c = chk.getValue();


        if (sum_c != sum_r)
        {
          ++bcount;
          // we have a corrupted packet, or at least the checksums don't add up
          // we should attempt to fix this, using MAGIC
          
        }

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
  public int corruptReceived() { return bcount; }

  private int pcount;
  private int bcount;

  private final int SOCKET_WAIT = 10;
  private volatile boolean running;
  private DatagramSocket socket;
  private Queue<byte[]> queue;
  private int port;
}
import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public class CommunicatorDown implements Runnable
{
  public CommunicatorDown(final int port) throws LineUnavailableException
  {
    this.port = port;
    running = true;
    player = new AudioPlayer();
    queue = new LinkedList<>();
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
      //Receive a DatagramPacket (note that the string cant be more than 80 chars) -- Why?
      byte[] buffer = new byte[1025];
      DatagramPacket packet = new DatagramPacket(buffer, 0, 1025);

      try
      {
        socket.receive(packet);
      }
      catch (SocketTimeoutException ignore)
      {
        continue;
      }
      catch (IOException e)
      {
        System.out.println("ERROR: CommunicatorDown: IOException: " + e.getMessage());
      }

      queue.offer(buffer);
    }
    socket.close();
  }

  public void close()
  {
    running = false;
  }

  public byte[] peek()
  {
    return queue.peek();
  }

  public byte[] poll()
  {
    return queue.poll();
  }

  public void receive()
  {
    byte[] bytes = poll();
    if (bytes == null)
      return;

    try
    {
      switch (bytes[0])
      {
        case 0:
          System.out.println(new String(Arrays.copyOfRange(bytes, 1, bytes.length)));
          break;
        case 1:
          player.playBlock(Arrays.copyOfRange(bytes, 1, bytes.length));
          break;
      }
    }
    catch (IOException e)
    {
      System.out.println("ERROR: CommunicatorDown: IOException" + e.getMessage());
    }
  }

  public int size()
  {
    return queue.size();
  }

  private final int SOCKET_WAIT = 10;
  private volatile boolean running;
  private AudioPlayer player;
  private DatagramSocket socket;
  private Queue<byte[]> queue;
  private int port;
}
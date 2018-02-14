import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.util.Arrays;

public class CommunicatorDown implements Runnable
{
  public CommunicatorDown(final int port) throws LineUnavailableException
  {
    this.port = port;
    running = true;
    player = new AudioPlayer();
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
      try
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

        switch (buffer[0])
        {
          case 0:
            System.out.println(new String(Arrays.copyOfRange(buffer, 1, buffer.length)));
            break;
          case 1:
            player.playBlock(Arrays.copyOfRange(buffer, 1, buffer.length));
            break;
        }
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

  private final int SOCKET_WAIT = 10;
  private volatile boolean running;
  private AudioPlayer player;
  private DatagramSocket socket;
  private int port;
}
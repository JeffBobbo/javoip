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
    player = new AudioPlayer();
  }
  public void start()
  {
    Thread thread = new Thread(this);
    thread.start();
  }

  public void run()
  {
    //***************************************************
    //Open a socket to receive from on port PORT

    //DatagramSocket receiving_socket;
    try
    {
      socket = new DatagramSocket(port);
    }
    catch (SocketException e)
    {
      System.out.println("ERROR: CommunicatorDown: Failed to open socket: " + e.getMessage());
      System.exit(1);
    }
    //***************************************************

    //***************************************************
    //Main loop.

    boolean running = true;

    while (running)
    {

      try
      {
        //Receive a DatagramPacket (note that the string cant be more than 80 chars)
        byte[] buffer = new byte[80];
        DatagramPacket packet = new DatagramPacket(buffer, 0, 80);

        socket.receive(packet);

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
    //Close the socket
    socket.close();
    //***************************************************
  }

  private AudioPlayer player;
  private DatagramSocket socket;
  private int port;
}
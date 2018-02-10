import java.net.*;
import java.io.*;

public class CommunicatorUp implements Runnable
{
  public CommunicatorUp(final String host, final int port)
  {
    this.host = host;
    this.port = port;
  }
  public void start()
  {
    Thread thread = new Thread(this);
    thread.start();
  }

  public void run()
  {
    //IP ADDRESS to send to
    InetAddress client = null;
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
    //***************************************************

    //***************************************************
    //Open a socket to send from
    //We dont need to know its port number as we never send anything to it.
    //We need the try and catch block to make sure no errors occur.

    //DatagramSocket sending_socket;
    try
    {
      socket = new DatagramSocket();
    }
    catch (SocketException e)
    {
      System.out.println("ERROR: CommunicatorUp: Failed to open socket: " + e.getMessage());
      System.exit(1);
    }
    //***************************************************

    //***************************************************
    //Get a handle to the Standard Input (console) so we can read user input

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
    //***************************************************

    //***************************************************
    //Main loop.

    boolean running = true;

    while (running)
    {
      try
      {
        //Read in a string from the standard input
        String str = in.readLine();

        //Convert it to an array of bytes
        byte[] buffer = new byte[str.getBytes().length+1];
        buffer[0] = 0;
        for (int i = 0; i < str.getBytes().length; ++i)
          buffer[1+i] = str.getBytes()[i];

        //Make a DatagramPacket from it, with client address and port number
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, client, port);

        //Send it
        socket.send(packet);

        //The user can type EXIT to quit
        if (str.equals("EXIT"))
          running = false;

      }
      catch (IOException e)
      {
        System.out.println("ERROR: CommunicatorUp: IOException: " + e.getMessage());
      }
    }

    //Close the socket
    socket.close();
    //***************************************************
  }

  private DatagramSocket socket;
  private String host;
  private int port;
}
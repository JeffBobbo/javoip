import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * @author james
 */
public class JaVoIP
{
  /**
   * If you need documentation for this, you're a scrub.
   * @param args Command line arguments
   */
  public static void main(String[] args)
  {
    final int PORT = 55555;
    Communicator comms;
    try
    {
      comms = new Communicator(InetAddress.getLocalHost(), PORT);
    }
    catch (UnknownHostException uhe)
    {
      System.err.println("FATAL: Failed to get localhost");
      return;
    }
    catch (SocketException e)
    {
      System.err.println("FATAL: Failed to open communcication ports");
      return;
    }

    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    // setup is done, so start the thread
    comms.start();

    boolean run = true;


    while (run)
    {
      String line = null;
      try
      {
        line = in.readLine();
      }
      catch (IOException e)
      {
        System.err.println("WARNING: Failed to read input, sending nothing:");
        System.err.println("    " + e.getMessage());
        continue;
      }
      if (line.equals("EXIT"))
      {
        run = false;
        continue;
      }

      byte[] buff = line.getBytes();
      try
      {
        comms.send(buff);
      }
      catch (IOException e)
      {
        System.err.println("WARNING: Failed to send input, continuing");
        System.err.println("    " + e.getMessage());
      }
    }

    comms.close();
  }
}

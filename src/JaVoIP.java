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
    final int PORT = 55556;
    Communicator comms = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

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
      if (line.substring(0, 1).equals("/")) // is this some command
      {
        String[] command = line.substring(1).split(" ");
        switch (command[0])
        {
          case "quit":
            run = false;
            continue;
          case "connect":
          {
            String host = command[1];
            String port = command[2];

            if (comms != null)
              comms.close();
            try
            {
              comms = new Communicator(InetAddress.getByName(host), Integer.parseInt(port));
              comms.start();
            }
            catch (UnknownHostException uhe)
            {
              System.out.println("ERROR: Unknown host");
            }
            catch (SocketException se)
            {
              System.out.println("ERROR: " + se.getMessage());
            }
          }
          break;
          case "disconnect":
            if (comms != null)
              comms.close();
            break;
        }
      }

      if (comms == null)
        continue;
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

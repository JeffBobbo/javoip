import com.sun.javaws.exceptions.InvalidArgumentException;

import java.io.*;
import java.net.SocketException;
import java.util.LinkedList;
import java.util.Queue;

public class InputText implements Runnable
{
  public InputText()
  {
    running = true;
    queue = new LinkedList<>();
  }
  public void start()
  {
    Thread thread = new Thread(this);
    thread.start();
  }

  @Override
  public void run()
  {
    //Get a handle to the Standard Input (console) so we can read user input
    BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

    while (running)
    {
      try
      {
        //Read in a string from the standard input
        String str = in.readLine();

        if (str.charAt(0) != '/')
          continue;

        String[] command = str.substring(1).split(" ");
        switch (command[0])
        {
          case "quit":
            running = false;
            continue;
          case "help":
            StringBuilder sb = new StringBuilder("List of available commands:\n");
            sb.append("  help - shows this message.\n");
            sb.append("  volume V - sets the playback volume, range 0.0 to 1.0.\n");
            sb.append("  deafen - deafens yourself (no audio played).\n");
            sb.append("  socket [1-4] - sets the type of socket for sending data.\n");
            sb.append("  interleave - toggles interleaving the audio payload.\n");
            sb.append("  quit - shuts everything down cleanly and exits.\n");
            System.out.println(sb.toString());
            break;
          case "deafen":
            JaVoIP.player.deafen(!JaVoIP.player.isDeaf());
            System.out.println("Toggled deafness.");
            break;
          case "socket":
            try
            {
              int i = Integer.parseInt(command[1]);
              DatagramSocketFactory.SocketType type = DatagramSocketFactory.getType(i);
              JaVoIP.uplink.setSocketType(type);
              System.out.println("Switched to socket " + type.toString());
            }
            catch (SocketException e)
            {
              System.err.println("ERROR: Invalid socket type: " + e.getMessage());
            }
            break;
          case "interleave":
            Mitigation.useInterleaving = !Mitigation.useInterleaving;
            System.out.println("Interleaving turned " + (Mitigation.useInterleaving ? "on" : "off"));
            break;
          default:
            System.out.println("Unknown command");
            break;
        }
      }
      catch (IOException e)
      {
        System.out.println("ERROR: CommunicatorUp: IOException: " + e.getMessage());
      }
    }
  }

  public boolean shouldClose()
  {
    return !running;
  }

  public void close()
  {
    running = false;
    queue.clear();
  }

  private volatile boolean running;
  private volatile Queue<String> queue;
}
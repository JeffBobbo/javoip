import java.io.*;
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

        if (str.charAt(0) == '/')
        {
          String[] command = str.substring(1).split(" ");
          switch (command[0])
          {
            case "quit":
              running = false;
              continue;
            default:
              System.out.println("Unknown command");
              break;
          }
        }
        else
        {
          queue.offer(str);
        }
      }
      catch (IOException e)
      {
        System.out.println("ERROR: CommunicatorUp: IOException: " + e.getMessage());
      }
    }
  }

  public String peek()
  {
    return queue.peek();
  }

  public String poll()
  {
    return queue.poll();
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
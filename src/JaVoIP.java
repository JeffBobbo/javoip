import javax.sound.sampled.LineUnavailableException;
import java.util.Scanner;

public class JaVoIP
{
  public static void main(String[] args)
  {
    Scanner scan = new Scanner(System.in);

    System.out.print("Port to host on: ");
    final int port = scan.nextInt();
    scan.nextLine();
    System.out.print("Host to connect to (host port): ");
    final String host = scan.nextLine();
    final int rport = Integer.parseInt(host.substring(host.indexOf(" ")+1));
    CommunicatorDown downlink = null;
    try
    {
      downlink = new CommunicatorDown(port);
    }
    catch (LineUnavailableException e)
    {
      System.err.println("ERROR: Failed to open audio playback device: " + e.getMessage());
      return; // bail out
    }
    CommunicatorUp uplink = new CommunicatorUp(host, rport);

    downlink.start();
    uplink.start();
  }
}
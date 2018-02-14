import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.Scanner;

public class JaVoIP
{
  public static void main(String[] args) throws IOException
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
    InputText inputText = new InputText();
    InputAudio inputAudio = new InputAudio();

    downlink.start();
    inputText.start();
    try
    {
      inputAudio.start();
    }
    catch (LineUnavailableException e)
    {
      System.err.println("Failed to obtain audio recording device: " + e.getMessage());
      return;
    }

    System.out.println("Connected!");
    final int FRAME_COUNT = 2;
    final int FRAME_SIZE = 512;
    final int PAYLOAD_SIZE = FRAME_COUNT * FRAME_SIZE;
    final int HEADER_SIZE = 0;
    final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;
    int pos = 0;

    byte[] packet = new byte[PACKET_SIZE];
    while (!inputText.shouldClose())
    {
      String text = inputText.poll();
      if (text != null)
        uplink.sendText(text);

      byte[] block = inputAudio.poll();
      if (block != null)
      {
        System.arraycopy(block, 0, packet, pos, FRAME_SIZE);
        pos += FRAME_SIZE;
      }

      if (pos >= PACKET_SIZE)
      {
        uplink.sendAudio(packet);
        pos = 0;
      }
    }

    System.out.println("Shutting down");
    downlink.close();
    uplink.close();
    inputText.close();
    inputAudio.close();
  }
}
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Scanner;
import java.util.zip.CRC32;

public class JaVoIP
{
  public static void main(String[] args) throws IOException
  {
    try
    {
      player = new AudioPlayer();
    }
    catch (LineUnavailableException e)
    {
      System.err.println("ERROR: Failed to open audi playback device: " + e.getMessage());
      return;
    }
    Scanner scan = new Scanner(System.in);

    System.out.print("Port to host on: ");
    final int port = scan.nextInt();
    scan.nextLine();
    System.out.print("Host to connect to (host port): ");
    final String host = scan.nextLine();
    final int rport = Integer.parseInt(host.substring(host.indexOf(" ")+1));
    downlink = new CommunicatorDown(port);
    uplink = new CommunicatorUp(host, rport);
    inputText = new InputText();
    inputAudio = new InputAudio();

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
    int pos = 0;

    byte[] payload = new byte[PAYLOAD_SIZE];
    while (!inputText.shouldClose())
    {
      byte[] block = inputAudio.poll();
      if (block != null)
      {
        System.arraycopy(block, 0, payload, pos, FRAME_SIZE);
        pos += FRAME_SIZE;
      }

      if (pos >= PAYLOAD_SIZE)
      {
        byte[] header = new byte[HEADER_SIZE];
        Arrays.fill(header, (byte)0);
        try
        {
          if (Mitigation.useInterleaving)
          {
            Mitigation.interleave(payload);
            //header[HEADER_SIZE-1] |= 0xF0; // toggle interleaving bit
          }
        }
        catch (IllegalArgumentException ignore)
        {
        }

        CRC32 chk = new CRC32();
        chk.update(payload);
        long sum = chk.getValue();
        System.arraycopy(Utilities.longToBytes(sum), 0, header, 0, Long.BYTES);

        byte[] packet = new byte[PACKET_SIZE];
        System.arraycopy(header, 0, packet, 0, HEADER_SIZE);
        System.arraycopy(payload, 0, packet, HEADER_SIZE, PAYLOAD_SIZE);
        uplink.send(packet);
        pos = 0;
      }

      byte[] snd = downlink.poll();
      if (snd != null)
        player.playBlock(snd);
    }

    System.out.println("Shutting down");
    downlink.close();
    uplink.close();
    inputText.close();
    inputAudio.close();
  }

  public static AudioPlayer player;
  public static CommunicatorDown downlink;
  public static CommunicatorUp uplink;
  public static InputText inputText;
  public static InputAudio inputAudio;

  public static final int FRAME_COUNT = 2;
  public static final int FRAME_SIZE = 512;
  public static final int PAYLOAD_SIZE = FRAME_COUNT * FRAME_SIZE;
  public static final int HEADER_SIZE = Long.BYTES;
  public static final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;

}
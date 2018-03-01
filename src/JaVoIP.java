import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

public class JaVoIP
{
  public static void main(String[] args) throws IOException
  {
    {
      Scanner scan = new Scanner(System.in);

      System.out.print("Port to host on: ");
      final int port = scan.nextInt();
      scan.nextLine();
      System.out.print("Host to connect to (host port): ");
      final String host = scan.nextLine();
      final int rport = Integer.parseInt(host.substring(host.indexOf(" ")+1));
      downlink = new CommunicatorDown(port);
      uplink = new CommunicatorUp(host, rport);
    }

    try
    {
      player = new AudioPlayer();
    }
    catch (LineUnavailableException e)
    {
      System.err.println("ERROR: Failed to open audio playback device: " + e.getMessage());
      return;
    }
    try
    {
      recorder = new AudioRecorder();
    }
    catch (LineUnavailableException e)
    {
      System.err.println("FATAL: Failed to open audio recording device: " + e.getMessage());
      return;
    }

    inputText = new InputText();

    downlink.start();
    inputText.start();

    System.out.println("Connected!");

    int pos = 0;

    // allocation can be expensive, do it all up front, we're overwriting everything anyway
    byte[] payload = new byte[PAYLOAD_SIZE];
    byte[] header = new byte[HEADER_SIZE];
    byte[] packet = new byte[PACKET_SIZE];
    while (!inputText.shouldClose())
    {
      // transmission tasks
      byte[] block = recorder.getBlock();
      if (block != null)
      {
        System.arraycopy(block, 0, payload, pos, FRAME_SIZE);
        pos += FRAME_SIZE;
      }

      if (pos >= PAYLOAD_SIZE)
      {

        // include the sequence number
        System.arraycopy(Utilities.intToBytes(uplink.packetsSent()), 0, header, 0, Integer.BYTES);

        // interleave if we need to
        if (Mitigation.useInterleaving)
          Mitigation.interleave(payload, Mitigation.INTERLEAVE_ROWS, Mitigation.INTERLEAVE_COLUMNS);

        // compute checksum if required
        if (Mitigation.useChecksums)
          System.arraycopy(Utilities.intToBytes(Mitigation.checksum(payload)), 0, header, Integer.BYTES, Integer.BYTES);

        // produce the packet and punch it
        System.arraycopy(header, 0, packet, 0, HEADER_SIZE);
        System.arraycopy(payload, 0, packet, HEADER_SIZE, PAYLOAD_SIZE);
        uplink.send(packet);
        pos = 0;
      }

      // receiving tasks
      byte[] snd = downlink.poll();
      if (snd != null)
        player.playBlock(snd);
    }

    System.out.println("Shutting down");
    System.out.println("Skipped " + downlink.skippedPackets() + " of " + downlink.packetsReceived() + " received packets");
    recorder.close();
    player.close();
    downlink.close();
    uplink.close();
    inputText.close();
  }

  public static AudioPlayer player;
  public static AudioRecorder recorder;
  public static CommunicatorDown downlink;
  public static CommunicatorUp uplink;
  public static InputText inputText;

  public static final int FRAME_COUNT = 2;
  public static final int FRAME_SIZE = 512;
  public static final int PAYLOAD_SIZE = FRAME_COUNT * FRAME_SIZE;
  public static final int HEADER_SIZE = Integer.BYTES + Integer.BYTES;
  public static final int PACKET_SIZE = HEADER_SIZE + PAYLOAD_SIZE;

}
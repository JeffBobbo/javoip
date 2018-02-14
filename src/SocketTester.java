import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

public class SocketTester
{
  public static void main(String[] args) throws LineUnavailableException, IOException, InterruptedException
  {
    final int port = 55555;
    final String host = "localhost";

    final int PACKETS = 1000;
    final int BURSTS = 10;

    CommunicatorDown downlink = new CommunicatorDown(port);
    CommunicatorUp uplink = new CommunicatorUp(host, port);

    downlink.start();

    // generate an array of 256 bytes to send
    final byte[] data = new byte[256];
    for (int i = 0; i < data.length; ++i)
      data[i] = (byte)i;

    int recOkay = 0;
    int recBad = 0;
    int recCount = 0;
    byte[] bytes;

    for (int j = 0; j < BURSTS; ++j)
    {
      for (int i = 0; i < PACKETS; ++i)
        uplink.send(data);
      Thread.sleep(1000);
    }

    int pcount;
    do
    {
      pcount = downlink.size();
      Thread.sleep(25);
      System.out.println(downlink.size() - pcount);
    } while (pcount < downlink.size());

    while ((bytes = downlink.poll()) != null)
    {
      ++recCount;
      boolean isBad = false;
      for (int i = 0; i < data.length; ++i)
      {
        if (bytes[i] != data[i])
        {
          isBad = true;
          break;
        }
      }
      if (!isBad)
      {
        ++recOkay;
      }
      else
        ++recBad;
    }

    uplink.close();
    downlink.close();

    StringBuilder sb = new StringBuilder();
    sb.append("Sent: ").append(PACKETS * BURSTS).append(" packets, of which:").append(System.lineSeparator());
    sb.append(recOkay).append(" were received correctly").append(System.lineSeparator());
    sb.append(recBad).append(" were received with malformed data").append(System.lineSeparator());
    sb.append(recCount).append(" were received in total").append(System.lineSeparator());
    System.out.println(sb.toString());
  }
}

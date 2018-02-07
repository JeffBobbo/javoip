/**
 * Communicator provides an interface for threaded duplex connections
 * @author james
 */
public class Communicator
{
  public Communicator(final String host, final int port)
  {
    uplink = null;
    downlink = null;
  }



  private CommunicatorUp uplink;
  private CommunicatorDown downlink;
}

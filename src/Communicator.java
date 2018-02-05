import java.io.IOException;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;

import static java.lang.Thread.interrupted;

/**
 * Communicator provides an interface for threaded duplex connections
 * @author james
 */
public class Communicator implements Runnable
{
  Communicator(final InetAddress addr, final int port) throws SocketException
  {
    System.out.println("Opening sockets");

    // incoming - receiving
    incoming = new DatagramSocket(port);
    incoming.setSoTimeout(TIMEOUT); // set timeout so we don't block

    // outgoing - sending
    outgoing = new DatagramSocket();
    address = addr;

    thread = null;
  }

  public void start()
  {
    thread = new Thread(this);
    thread.start();
  }

  @Override
  public void run()
  {
    System.out.println("Starting networking thread");

    while (!interrupted())
    {
      try
      {
        byte[] buff = new byte[80];
        DatagramPacket dp = new DatagramPacket(buff, 0, 80);

        incoming.receive(dp);
        System.out.println(new String(buff));
      }
      catch (SocketTimeoutException ignored) // do nothing with this, it's expected
      {
      }
      catch (IOException e)
      {
        System.err.println("ERROR: Communicator: Some IO error: " + e.getMessage());
      }
    }

    incoming.close();
    outgoing.close();
  }

  public void send(final byte[] buff) throws IOException
  {
    DatagramPacket dp = new DatagramPacket(buff, buff.length, address, incoming.getLocalPort());
    outgoing.send(dp);
  }

  public void close()
  {
    thread.interrupt();
  }

  // socket for incoming data, on a specific port
  private DatagramSocket incoming;
  // socket for outgoing data, on any old port
  private DatagramSocket outgoing;

  private InetAddress address;

  // receiving socket read timeout, so the call doesn't block waiting for data
  private static int TIMEOUT = 1;

  // thread for running communication in
  private Thread thread;
}

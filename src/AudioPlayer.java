import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

/**
 * A modified version of the AudioPlayer supplied that runs on it's own thread
 * the external interface is the same, but now it doesn't block, but rather queues up
 * blocks to be played internally and plays them as it can.
 * It also doesn't write files to disk
 */
public class AudioPlayer implements Runnable
{
  /**
   * Constructs and starts the AudioPlayer.
   * @throws LineUnavailableException thrown is the playback device could not be acquired
   */
  public AudioPlayer() throws LineUnavailableException
  {
    queue = new LinkedBlockingQueue<>();
    running = true;

    AudioFormat linearFormat = new AudioFormat(8000.0F, 16, 1, true, false);
    Info info = new Info(SourceDataLine.class, linearFormat);
    sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);
    sourceDataLine.open(linearFormat);
    sourceDataLine.start();

    Thread thread = new Thread(this);
    thread.setName("AudioPlayer");
    thread.start();
  }

  /**
   * Queues sound data to be played by the internal thread of the AudioPlayer
   * @param data Data to play, 8kHz 16bit signal in blocks of 512 bytes
   */
  public void playBlock(byte[] data)
  {
    queue.offer(data);
  }

  /**
   * Controls the deafness of the AudioPlayer
   * @param d if true, then deafen
   */
  public void deafen(final boolean d)
  {
    BooleanControl control = (BooleanControl)sourceDataLine.getControl(BooleanControl.Type.MUTE);
    if (control != null)
      control.setValue(d);
  }

  /**
   * @return true if this AudioPlayer is deaf
   */
  public boolean isDeaf()
  {
    BooleanControl control = (BooleanControl)sourceDataLine.getControl(BooleanControl.Type.MUTE);
    return control.getValue();
  }

  /**
   * Marks this AudioPlayer to close on the next cycle of it's internal loop
   */
  public void close()
  {
    running = false;
  }

  /**
   * the internal thread loop, do not call manually
   */
  @Override
  public void run()
  {
    byte[] prev = null;
    while (running)
    {
      byte[] next = queue.poll();
      if (next == null)
      {
        if (prev != null)
          sourceDataLine.write(prev, 0, prev.length);
        prev = null;
      }
      else
      {
        if (queue.size() > 2)
          next = queue.poll();
        sourceDataLine.write(next, 0, next.length);
        prev = next;
      }
    }
    sourceDataLine.drain();
    sourceDataLine.stop();
    sourceDataLine.close();
  }

  private SourceDataLine sourceDataLine;
  private BlockingQueue<byte[]> queue;
  private volatile boolean running;
}

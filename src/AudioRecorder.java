import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * A modified version of the AudioRecorder supplied that runs on it's own thread
 * the external interface is the same, but now it doesn't block, but rather queues up
 * blocks of recorded audioed internally and spits them out on request.
 * It also doesn't write files to disk
 */
public class AudioRecorder implements Runnable
{
  /**
   * Constructs and starts the AudioRecorder
   * @throws LineUnavailableException thrown if the audio line in device could not be acquired
   */
  public AudioRecorder() throws LineUnavailableException
  {
    running = true;
    queue = new LinkedBlockingQueue<>();

    AudioFormat linearFormat = new AudioFormat(8000.0F, 16, 1, true, false);
    Info info = new Info(TargetDataLine.class, linearFormat);
    targetDataLine = (TargetDataLine)AudioSystem.getLine(info);
    targetDataLine.open(linearFormat);
    targetDataLine.start();
    linearStream = new AudioInputStream(this.targetDataLine);

    Thread thread = new Thread(this);
    thread.setName("AudioRecorder");
    thread.start();
  }

  /**
   * @return returns a block of queued sound data, or null if there's non available
   */
  public byte[] getBlock()
  {
    return queue.poll();
  }

  /**
   * Controls the mute of this AudioRecorder, preventing recording of data
   * @param d true if to mute
   */
  public void mute(final boolean d)
  {
    BooleanControl control = (BooleanControl)targetDataLine.getControl(BooleanControl.Type.MUTE);
    if (control != null)
      control.setValue(d);
  }

  /**
   * @return true if this AudioRecorder is muted
   */
  public boolean isMute()
  {
    BooleanControl control = (BooleanControl)targetDataLine.getControl(BooleanControl.Type.MUTE);
    return control.getValue();
  }

  /**
   * Marks this AudioRecorder to close on the next cycle of it's internal loop
   */
  public void close()
  {
    queue.clear();
    running = false;
  }

  /**
   * The internal loop of the AudioRecorder, do not call manually
   */
  @Override
  public void run()
  {
    byte[] block = new byte[512];
    while (running)
    {
      try
      {
        linearStream.read(block, 0, block.length);
      }
      catch (IOException e)
      {
        System.err.println("ERROR: Failed to read line in: " + e.getMessage());
      }
      queue.offer(block);
    }
    targetDataLine.stop();
    targetDataLine.close();
  }

  private TargetDataLine targetDataLine;
  private AudioInputStream linearStream;
  private volatile boolean running;
  private BlockingQueue<byte[]> queue;
}

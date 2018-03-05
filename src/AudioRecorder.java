import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

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
    queue = new LinkedList<>();

    linearFormat = new AudioFormat(8000.0F, 16, 1, true, false);
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
    while (running)
    {
      byte[] block = new byte[512];
      try
      {
        linearStream.read(block, 0, block.length);
      }
      catch (IOException e)
      {
        System.err.println("ERROR: Failed to read line in: " + e.getMessage());
      }
      queue.offer(block);
      if (counter < 937)
     {
        System.arraycopy(block, 0, cached, counter*block.length, block.length);
        ++counter;
      }
      else if (counter == 938)
        writeFile();
    }
    targetDataLine.stop();
    targetDataLine.close();
  }

  private TargetDataLine targetDataLine;
  private AudioInputStream linearStream;
  private volatile boolean running;
  private volatile Queue<byte[]> queue;

  private AudioFormat linearFormat;
  private byte[] cached = new byte[938 * 512];
  private int counter = 0;

  private void writeFile()
  {
    Thread thread = new Thread(new Writer());
    thread.start();
  }

  private class Writer implements Runnable
  {
    private Writer()
    {
    }

    public void run()
    {
      try
      {
        String filename = "input.wav";
        File audioFile = new File(filename);
        System.err.println("Writing File: " + audioFile.getCanonicalPath());
        ByteArrayInputStream baiStream = new ByteArrayInputStream(cached);
        AudioInputStream aiStream = new AudioInputStream(baiStream, linearFormat, cached.length);
        AudioSystem.write(aiStream, AudioFileFormat.Type.WAVE, audioFile);
        aiStream.close();
        baiStream.close();
        cached = null;
      }
      catch (IOException var9)
      {
        System.err.println(var9.getMessage());
      }
    }
  }
}

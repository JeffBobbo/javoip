import javax.sound.sampled.*;
import javax.sound.sampled.DataLine.Info;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;

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
    queue = new LinkedList<>();
    running = true;

    linearFormat = new AudioFormat(8000.0F, 16, 1, true, false);
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
//    if (counter < 468)
//    {
//      System.arraycopy(data, 0, cached, counter*data.length, data.length);
//      ++counter;
//    }
//    else if (counter == 938)
//      this.writeFile();
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
    while (running)
    {
      byte[] block = queue.poll();
      if (block != null)
        sourceDataLine.write(block, 0, block.length);
    }
    sourceDataLine.drain();
    sourceDataLine.stop();
    sourceDataLine.close();
  }

  private SourceDataLine sourceDataLine;
  private volatile Queue<byte[]> queue;
  private volatile boolean running;

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
        String filename = "output.wav";
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

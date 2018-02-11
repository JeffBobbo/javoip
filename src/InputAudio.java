import CMPC3M06.AudioRecorder;

import javax.sound.sampled.LineUnavailableException;
import java.io.*;
import java.util.LinkedList;
import java.util.Queue;

public class InputAudio implements Runnable
{
  public InputAudio()
  {
    running = true;
    queue = new LinkedList<>();
  }
  public void start() throws LineUnavailableException
  {
    recorder = new AudioRecorder();
    Thread thread = new Thread(this);
    thread.start();
  }

  @Override
  public void run()
  {
    while (running)
    {
      // grab a block of audio
      try
      {
        byte[] block = recorder.getBlock();
        queue.offer(block);
      }
      catch (IOException e)
      {
        System.err.println("ERROR: Failed to get audio block: " + e.getMessage());
      }
    }
  }

  public byte[] peek()
  {
    return queue.peek();
  }

  public byte[] poll()
  {
    return queue.poll();
  }

  public void close()
  {
    running = false;
    queue.clear();
  }

  private volatile boolean running;
  private volatile Queue<byte[]> queue;
  private AudioRecorder recorder;
}
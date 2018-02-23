//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.*;
import javax.sound.sampled.AudioFileFormat.Type;
import javax.sound.sampled.DataLine.Info;

public class AudioPlayer
{
  public AudioPlayer() throws LineUnavailableException
  {
    linearFormat = new AudioFormat(8000.0F, 16, 1, true, false);
    Info info = new Info(SourceDataLine.class, this.linearFormat);
    this.sourceDataLine = (SourceDataLine)AudioSystem.getLine(info);
    this.sourceDataLine.open(this.linearFormat);
    this.sourceDataLine.start();
    cached = new ArrayList<>();
    counter = 0;
  }

  public void playBlock(byte[] voiceData) throws IOException
  {
    //++this.counter;
    this.sourceDataLine.write(voiceData, 0, voiceData.length);
    /*
    if (this.counter < 938)
      this.cached.add(voiceData);
    else if(this.counter == 938)
      this.writeFile();
    */
  }

  public void deafen(final boolean d)
  {
    BooleanControl control = (BooleanControl)sourceDataLine.getControl(BooleanControl.Type.MUTE);
    if (control != null)
      control.setValue(d);
  }

  public boolean isDeaf()
  {
    BooleanControl control = (BooleanControl)sourceDataLine.getControl(BooleanControl.Type.MUTE);
    return control.getValue();
  }

  public void close()
  {
    this.sourceDataLine.drain();
    this.sourceDataLine.stop();
    this.sourceDataLine.close();
  }

  private void writeFile() throws IOException
  {
    Thread thr = new Thread(new AudioPlayer.Writer());
    thr.start();
  }

  private AudioFormat linearFormat;
  private SourceDataLine sourceDataLine;
  private ArrayList<byte[]> cached;
  private int counter;


  private class Writer implements Runnable
  {
    private Writer()
    {
    }

    public void run()
    {
      try
      {
        byte[] fullArray = new byte[AudioPlayer.this.cached.size() * 512];

        for (int i = 0; i < AudioPlayer.this.cached.size(); ++i)
          System.arraycopy(AudioPlayer.this.cached.get(i), 0, fullArray, i * 512, 512);

        String filename = "output.wav";
        File audioFile = new File(filename);
        System.err.println("Writing File: " + audioFile.getCanonicalPath());
        ByteArrayInputStream baiStream = new ByteArrayInputStream(fullArray);
        AudioInputStream aiStream = new AudioInputStream(baiStream, AudioPlayer.this.linearFormat, (long)fullArray.length);
        AudioSystem.write(aiStream, Type.WAVE, audioFile);
        aiStream.close();
        baiStream.close();
        AudioPlayer.this.cached = null;
      }
      catch (IOException var9)
      {
        System.err.println(var9.getMessage());
      }
    }
  }
}

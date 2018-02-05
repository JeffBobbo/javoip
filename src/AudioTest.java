import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;


/**
 * CMPC3M06 Audio Test
 *
 *  This class is designed to test the audio player and recorder.
 *
 * @author Philip Harding
 */
public class AudioTest
{
  public static void main(String args[]) throws Exception
  {
    //Initialise AudioPlayer and AudioRecorder objects
    AudioRecorder recorder = new AudioRecorder();
    AudioPlayer player = new AudioPlayer();

    while (true)
    {
      // this can block
      byte[] block = recorder.getBlock();
      player.playBlock(block);
    }

    //recorder.close();
    //player.close();
  }
}
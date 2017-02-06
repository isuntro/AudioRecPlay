package AudioRecPlay;
import java.util.Vector;
import java.util.Iterator;
import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import java.io.IOException;

/**
 * This class provides methods for recording and playing audio
 * @author xdn15mcu
 */
public class Audio {
    Vector<byte[]> voiceVector = null;
    AudioRecorder recorder = null;
    AudioPlayer player = null;
    int recordTime = 10;
    public Audio() throws Exception {
        recorder = new AudioRecorder();
        player = new AudioPlayer();
        voiceVector = new Vector<byte[]>();
    }
    
    public byte[] getRecordBytes() throws IOException{
        byte[] block = null;
        System.out.println("Recording...");
        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
            block = recorder.getBlock();
            //voiceVector.add(block);
        }
        recorder.close();
        // Return bytes
        return block;
    }
    
    public void play(byte[] blocks) throws IOException{
        // Form the vector
        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
            voiceVector.add(blocks);
        }
        
        // Play it
        System.out.println("Playing Audio...");

        Iterator<byte[]> voiceItr = voiceVector.iterator();
        while (voiceItr.hasNext()) {
            player.playBlock(voiceItr.next());
        }
        
        player.close();
    }
}

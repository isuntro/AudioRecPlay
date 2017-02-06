package AudioRecPlay;
import java.io.*;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;
import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;

/**
 * This class provides methods for recording and playing audio
 * @author xdn15mcu
 */
public class Audio {
    private AudioRecorder recorder = null;
    private AudioPlayer player = null;
    int recordTime = 10;
    public Audio() throws Exception {
        recorder = new AudioRecorder();
        player = new AudioPlayer();
    }
    
    public byte[] getRecordBytes() throws IOException, ClassNotFoundException {

        Vector<byte[]> voiceVector = new Vector<byte[]>();
        System.out.println("Recording...");
        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
            byte[] block = recorder.getBlock();
            voiceVector.add(block);
        }
        recorder.close();

        // Serialise
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(voiceVector);

        // Return bytes
        return b.toByteArray();
    }
    
    public void play(byte[] blocks) throws IOException, ClassNotFoundException {
        Vector<byte[]> voiceVector = new Vector<>();
        byte[] block = new byte[512];
        //byte[] blockss = null;

        // Deserialise
        ByteArrayInputStream b = new ByteArrayInputStream(blocks);
        ObjectInputStream o = new ObjectInputStream(b);

//        while ((voiceVector = (Vector<byte[]>)o.readObject()) != null){
//            System.out.println("Working");
//        }

        //Vector<byte[]> voice = (Vector<byte[]>) o.readObject();
        //System.out.println(o.available());
        // Create blocks

        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++){
            block[i] = blocks[i];
            if (i % Math.ceil(recordTime / 0.032) == 0) {
                voiceVector.add(block);
                // Reset the block
                block = new byte[512];
            }
        }

        //Object ob = o.readObject();

        //voiceVector.add(blocks);
//        for (int i = 0; i < Math.ceil(recordTime / 0.032); i++) {
//            voiceVector.add(ob);
//        }

        // Play it
        System.out.println("Playing Audio...");

        Iterator<byte[]> voiceItr = voiceVector.iterator();
        while (voiceItr.hasNext()) {
            player.playBlock(voiceItr.next());
        }
        
        player.close();
    }
}

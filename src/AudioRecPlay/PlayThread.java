package AudioRecPlay;

import Tools.AudioPacket;
import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.LinkedList;

/**
 * Creates a buffer before playing the audio
 * @author  Diego Viteri
 */
public class PlayThread extends Thread{

    private final LinkedList<byte[]> audioBuffer;
    private final AudioPlayer player;

    public PlayThread() throws LineUnavailableException {
        player = new AudioPlayer();
        audioBuffer = new LinkedList<>();
    }

    public void addPacket(byte[] packet){
        System.out.println("packet : " + packet[0] + " block :" + packet[1]);
        audioBuffer.add(packet);
    }

    @Override
    public void run(){
        while(true){
            synchronized (this) {
                // If we have data
                if (!audioBuffer.isEmpty()) {
                    // Gets first element from the buffer



                    try {
                        byte[] frame = audioBuffer.poll();
                        if(frame == null || frame.length == 0){
                            frame = new byte[AudioPacket.DATA_SIZE];
                        }

                        player.playBlock(frame);
                        Thread.currentThread().wait(1);

                    } catch (IOException |InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}

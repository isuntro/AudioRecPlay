package AudioRecPlay;

import Tools.AudioPacket;
import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.util.LinkedList;

/**
 * Creates a buffer before playing the audio
 * @author  Diego Viteri
 */
public class PlayThread extends Thread{

    private final LinkedList<AudioPacket> audioBuffer;
    private final AudioPlayer player;

    public PlayThread() throws LineUnavailableException {
        player = new AudioPlayer();
        audioBuffer = new LinkedList<>();
    }

    public void addPacket(AudioPacket packet){
        audioBuffer.add(packet);
    }

    @Override
    public void run(){
        while(true){
            synchronized (this) {
                // If we have data
                if (!audioBuffer.isEmpty()) {
                    // Gets first element from the buffer
                    AudioPacket packet = audioBuffer.poll();
                    byte[] block = packet.getBlock();

                    try {
                        System.out.println("Playing...");
                        // Check if we losing packets
                        if (block.length == 0 || block == null) {
                            System.out.println("Lost packet!");
                        } else {
                            // Play otherwise
                            player.playBlock(block);
                            Thread.currentThread().wait(32);
                        }
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

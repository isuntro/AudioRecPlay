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
    
    /**
     * Adds packet without processing it
     * Only used for simple DatagramSocket
     * @param packet 
     */
    public synchronized void addPacket(AudioPacket packet){
        audioBuffer.add(packet);
    }
    
    /**
     * Adds a frame of packets to the audioBuffer
     * @param packets 
     */
    public synchronized void addPackets(AudioPacket[] packets){
        for (AudioPacket ap : packets){
            // TODO:
            // If the audio packet is empty
            // fill it
            
            audioBuffer.add(ap);
        }
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
                        System.out.println("Playing..." + packet.packetID);
                        // Check if we losing packets
                        if (block.length == 0 || block == null) {
                            System.out.println("Lost packet!");
                        } else {
                            // Play otherwise
                            player.playBlock(block);
                            Thread.currentThread().wait(5);
                        }
                    } catch (IOException |InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

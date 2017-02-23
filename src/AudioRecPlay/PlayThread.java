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
     * Adds packet to the player buffer 
     * @param packet 
     */
    public synchronized void addAudioPacket(AudioPacket packet){
        
        audioBuffer.add(packet);
    }
    
    /**
     * Adds a frame of packets to the audioBuffer
     * @param packets 
     */
    public synchronized void addAudioPackets(AudioPacket[] packets, int pos){
       // Check for null 
        for (int i = 0; i < packets.length; i++){
            if (packets[i] == null){
                // Fill with silence
                packets[i] = new AudioPacket(new byte[512]);
                // TODO : FILL IT
                packets[i].packetID = pos + i;
                
            }
        audioBuffer.add(packets[i]);
        }
    }
    
    public synchronized void addDelayedPacket(AudioPacket packet){
        int pos = 0;
        // find the correct position for this packet
        for (AudioPacket ap : audioBuffer) {
                if (ap.packetID > packet.packetID) {
                        if (pos != 0) {
                            pos = audioBuffer.indexOf(ap);
                            break;
                        }
                        else {
                            return;
                        }
                }
                pos++;
        }
        
        audioBuffer.set(pos - 1, packet);
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
                    
                    //System.out.println("Playing: " + packet.packetID);
                    System.out.println("Playing: " + packet.packetID + (block.length == 0 ? " MISSING PACKET" : ""));
//                    if (block.length == 0) 
//                        System.out.println("Missing packet");
//                    else
//                        System.out.println("Good packet");
//                  

                    
                    
                    try {
                        // Play 
                        player.playBlock(block);
                        Thread.currentThread().wait(16);
                    
                    } catch (IOException |InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

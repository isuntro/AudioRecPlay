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
    
    private int correctionMethod;
    
    public PlayThread(int correctionMethod) throws LineUnavailableException {
        player = new AudioPlayer();
        audioBuffer = new LinkedList<>();
        
        this.correctionMethod = correctionMethod;
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
        // 0 fill with silence
        // 1 splice buffer
        if (correctionMethod == 0){
            // Check for null 
            for (int i = 0; i < packets.length; i++){
                if (packets[i] == null){
                    // Fill with silence
                    //packets[i] = new AudioPacket(new byte[0]);
                    packets[i] = new AudioPacket(new byte[512]);
                    packets[i].packetID = pos + i;

                }
            audioBuffer.add(packets[i]);
            }
        } else if (correctionMethod == 1) {
            // Drop the nulls and add the others sequentially
            // effectively, splicing
            for (AudioPacket packet : packets) {
                if (packet != null) audioBuffer.add(packet);
            }
        } else if (correctionMethod == 2){
            
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
        if (pos == 0)
            return;
        
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
                        
                        if (correctionMethod == 1){
                            Thread.currentThread().wait(32);
                        } else {
                            Thread.currentThread().wait(16);
                        }
                        // for datagram2, make it wait 32
                        //
                        
                        // For datagram3, make it wait 16
                        
                        
                        
                    
                    } catch (IOException |InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}

package AudioRecPlay;
/*
 * TextReceiver.java
 *
 * Created on 15 January 2003, 15:43
 */

/**
 *
 * @author  Diego Viteri
 */
import java.net.*;
import java.io.*;
import Tools.AudioPacket;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;

public class ReceiverThread implements Runnable{
    
    private final int headerSize;
    private final int bufferSize;
    private boolean firstBuffer;
    private int packetsCollected;
    private int nullPackets;
    private int currentBuffer;
    
    int oldPos;
    
    static DatagramSocket receivingSocket;
    private PlayThread player;
    private AudioPacket[] receivingBuffer;
    
    //private PacketProcessor packetProcessor;
    
    public ReceiverThread(DatagramSocket socket) throws LineUnavailableException {
        nullPackets = 0;
        bufferSize = 8;
        headerSize = 4;
        firstBuffer = true;
        receivingBuffer = new AudioPacket[bufferSize];
        packetsCollected = 0;
        currentBuffer = 0;
        receivingSocket = socket;
        
        player = new PlayThread();
        player.start();
    }

    public void start(){
        Thread thread = new Thread(this);
	thread.start();
    }
    
    @Override
    public void run (){
        byte[] buffer;
        while (!Thread.interrupted()){
            // Depends on the headerSize
            // 512 + headerSize
            buffer = new byte[512 + headerSize];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try{
                //receiving_socket.setSoTimeout(1000);
                receivingSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            // If using datagram socket 2
            if(receivingSocket instanceof DatagramSocket2 ||
                receivingSocket instanceof DatagramSocket3){
                processPacket(packet);
            } else {
                AudioPacket ap = new AudioPacket(buffer);
                player.addAudioPacket(ap);
            }
            
            
            // else
            
        }
    }
    
    public void processPacket(DatagramPacket packet){
        byte[] block = packet.getData();
        
        // Get and strip the first byte from the block
        int packetID = (block[0] & 0xFF) | ((block[1] & 0xFF) << 8) | 
                ((block[2] & 0xFF) << 16) | ((block[3] & 0xFF) << 24);
        int bufferID = packetID / bufferSize;
        
        AudioPacket audioPacket = new AudioPacket(getAudio(packet));
        audioPacket.packetID = packetID;
        audioPacket.bufferID = bufferID;
        
        
        int pos = packetID % bufferSize;
        
        if (firstBuffer){
            currentBuffer = bufferID;
            firstBuffer = false;
        }
        // first buffer
        if (currentBuffer == bufferID){
            receivingBuffer[pos] = audioPacket;
            packetsCollected++;
            if (packetsCollected == bufferSize){
                // fill nulls before sending
                player.addAudioPackets(receivingBuffer, currentBuffer * bufferSize);
                receivingBuffer = new AudioPacket[bufferSize];
                currentBuffer++;
                packetsCollected = 0;
            }
        } else if (bufferID < currentBuffer){
            if (bufferID < currentBuffer - 1){
                System.out.println("too late");
            }
            player.addDelayedPacket(audioPacket);
            
        } else {
            while (bufferID > currentBuffer){
                // Send previous buffer
                player.addAudioPackets(receivingBuffer, currentBuffer * bufferSize);
                currentBuffer = bufferID;
                // Create new one
                receivingBuffer = new AudioPacket[bufferSize];

                // Add it to the new one
                receivingBuffer[pos] = audioPacket;
                packetsCollected = 1;
                System.out.println("Belongs to next buffer");
            }
        }
        
//        if (packetID > packetCount + bufferSize){
//            prevPacket = audioPacket;
//            oldPos = prevPacket.packetID % bufferSize;
//            goNextBuffer = true;
//        } else {
//            receivingBuffer[pos] = audioPacket;
//            packetsCollected++;
//        }
        
//        if (packetsCollected == bufferSize || goNextBuffer == true){
//            if (receivingBuffer[oldPos] == null && goNextBuffer == true){
//                receivingBuffer[oldPos] = prevPacket;
//                receivingBuffer[oldPos].packetID = prevPacket.packetID;
//                packetsCollected++;
//            }
//            for (int i = 0; i < receivingBuffer.length; i++){
//                packetCount++;
//                // Will leave the audioblock null and add the packetID
//                if (receivingBuffer[i] == null){
//                    receivingBuffer[i] = new AudioPacket(new byte[0]);
//                    receivingBuffer[i].packetID = i;
//                    packetsCollected++;
//                    nullPackets++;
//                }
//                packetCount = receivingBuffer[i].packetID;
//            }
//            //packetCount++;
//            if (enableCorrection){
//                player.addAudioPackets(addSilence(receivingBuffer));
//                //player.addAudioPackets(spliceBuffer(receivingBuffer));
//            } else {
//                player.addAudioPackets(receivingBuffer);
//            }
//            packetsCollected = 0;
//            // Reset buffer
//            receivingBuffer = new AudioPacket[bufferSize];
//        } else if (receivingBuffer[3] == null && goNextBuffer == false){
//            // Means packet [3] is lost
//            
//        }
//        
//        if (packetCount == bufferSize){
//            packetCount = 0;
//        }
        
        
        // Check buffer completed
//        if (packetCount >= bufferSize){
//            player.addAudioPackets(receivingBuffer);
//            // Reset buffer
//            receivingBuffer = new AudioPacket[bufferSize];
//            packetCount = 0;
//        }
    }
    
    public byte[] getAudio(DatagramPacket packet){
        byte[] block = new byte[packet.getLength() - headerSize];
        System.arraycopy(packet.getData(), headerSize, block, 0, block.length);
        return block;
    }
    
    public AudioPacket[] spliceBuffer(AudioPacket[] receivingBuffer){
        AudioPacket[] newBuff = new AudioPacket[receivingBuffer.length - nullPackets];
        int lostPackets = 0;
        // Count how many lost packets there are
        for (int i = 0; i < receivingBuffer.length; i++){
            if (receivingBuffer[i].getBlock().length == 0){
                lostPackets++;
            } else {
                newBuff[i - lostPackets] = receivingBuffer[i];
            }
        }
        nullPackets = 0;
        return newBuff;
    }
    
    public AudioPacket[] addSilence(AudioPacket[] rBuffer){
        for (int i = 0; i < rBuffer.length; i++){
            if (rBuffer[i].getBlock().length == 0){
                rBuffer[i] = new AudioPacket(new byte[512]);
            }
        }
        return rBuffer;
    }
    
    public AudioPacket[] repetitionFix(AudioPacket[] receivingBuffer){
        return null;
    }
}

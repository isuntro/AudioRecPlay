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
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class ReceiverThread implements Runnable{
    
    private final int headerSize;
    private final int bufferSize;
    private boolean firstBuffer;
    private int packetsCollected;
    private int currentBuffer;
    private boolean enableCorrection;
    int correctionMethod;
    int oldPos;
    
    static DatagramSocket receivingSocket;
    private PlayThread player;
    private AudioPacket[] receivingBuffer;
    
    //private PacketProcessor packetProcessor;
    
    public ReceiverThread(DatagramSocket socket, boolean enableCorrection,
            int bufferSize, int headerSize) throws LineUnavailableException {
        this.bufferSize = bufferSize;
        this.headerSize = headerSize;
        this.enableCorrection = enableCorrection;
        firstBuffer = true;
        receivingBuffer = new AudioPacket[bufferSize];
        packetsCollected = 0;
        currentBuffer = 0;
        receivingSocket = socket;
        correctionMethod = 0;
        
        if (receivingSocket instanceof DatagramSocket2){
            correctionMethod = 0;
        } else if (receivingSocket instanceof DatagramSocket3){
            correctionMethod = 0;
        }
        
        player = new PlayThread(correctionMethod);
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
            
            // If it's datagram4, extra 2 bytes for checksum
            if (receivingSocket instanceof DatagramSocket4){
                buffer = new byte[512 + headerSize + 2];
            } else {
                buffer = new byte[512 + headerSize];
            }
            
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try{
                //receiving_socket.setSoTimeout(1000);
                receivingSocket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            // If using datagram socket 2
            if(enableCorrection){
                processPacket(packet);
            } else {
                AudioPacket ap = new AudioPacket(buffer);
                player.addAudioPacket(ap);
            }
            
            
        }
    }
    
    public void processPacket(DatagramPacket packet){
        byte[] block = packet.getData();
        
        if (receivingSocket instanceof DatagramSocket4){
            block = getCheckSum(block);
        }
        
        // if null means bad checksum
        if (block == null)
            block = new byte[4];
        
        // Get and strip the packetID from the block
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
                // send buffer to play
                player.addAudioPackets(receivingBuffer, 
                        currentBuffer * bufferSize);
                
                receivingBuffer = new AudioPacket[bufferSize];
                currentBuffer++;
                packetsCollected = 0;
            }
        } else if (bufferID < currentBuffer){
            if (bufferID < currentBuffer - 1){
                //System.out.println("too late");
                // don't add it
                return;
            }
            player.addDelayedPacket(audioPacket);
            
        } else {
            while (bufferID > currentBuffer){
                // Send previous buffer
                player.addAudioPackets(receivingBuffer, 
                        currentBuffer * bufferSize);
                currentBuffer = bufferID;
                // Create new one
                receivingBuffer = new AudioPacket[bufferSize];

                // Add it to the new one
                receivingBuffer[pos] = audioPacket;
                packetsCollected = 1;
                //System.out.println("Belongs to next buffer");
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
    
    private byte[] getCheckSum(byte[] block) {
        byte[] checkSum = new byte[2];

        byte[] blockWithoutChecksum = new byte[block.length - 2];
        System.arraycopy(block, 2, blockWithoutChecksum, 0, blockWithoutChecksum.length);

        checkSum[0] = 0;
        checkSum[1] = 0;
        for (int i = 0; i < blockWithoutChecksum.length; i++) {
                checkSum[0] = (byte) ((checkSum[0] + blockWithoutChecksum[i]) % 255);
                checkSum[1] = (byte) ((checkSum[1] + checkSum[0]) % 255);
        }

        if (checkSum[0] != block[0] || checkSum[1] != block[1])
            return null;
        
        return blockWithoutChecksum;
    }
    
    public byte[] getAudio(DatagramPacket packet){
        byte[] block = new byte[packet.getLength() - headerSize];
        System.arraycopy(packet.getData(), headerSize, block, 0, block.length);
        return block;
    }
    
    public AudioPacket[] spliceBuffer(AudioPacket[] receivingBuffer){
//        AudioPacket[] newBuff = new AudioPacket[receivingBuffer.length - nullPackets];
//        int lostPackets = 0;
//        // Count how many lost packets there are
//        for (int i = 0; i < receivingBuffer.length; i++){
//            if (receivingBuffer[i].getBlock().length == 0){
//                lostPackets++;
//            } else {
//                newBuff[i - lostPackets] = receivingBuffer[i];
//            }
//        }
//        nullPackets = 0;
//        return newBuff;
            return null;
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

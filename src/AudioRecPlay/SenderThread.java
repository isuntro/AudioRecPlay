package AudioRecPlay;

/*
 * TextSender.java
 *
 * Created on 15 January 2003, 15:29
 */

/**
 *
 * @author  Diego Viteri
 */
import CMPC3M06.AudioRecorder;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

public class SenderThread implements Runnable {

    private final DatagramSocket sendingSocket;
    private final InetSocketAddress connection;
    private final AudioRecorder recorder;
    //private PacketProcessor packetProcessor;
    
    private boolean enableCorrection;
    private int headerSize;
    private int pID;
    private int bufferIndex;
    private final int bufferSize;
    DatagramPacket[] sendingBuffer;

    public SenderThread(DatagramSocket socket, InetSocketAddress connection,
            boolean enableCorrection, int bufferSize, int headerSize) 
                                            throws LineUnavailableException {
        pID = 0;
        bufferIndex = 0;
        this.bufferSize = bufferSize;
        this.headerSize = headerSize;
        this.enableCorrection = enableCorrection;
        
        sendingSocket = socket;
        this.connection = connection;
        sendingBuffer = new DatagramPacket[bufferSize];
        
        recorder = new AudioRecorder();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        byte[] audioBlock;
        DatagramPacket packet;
        while (!Thread.interrupted()) {
            try {
                //System.out.println("Recording...");
                audioBlock = recorder.getBlock();
                
                if (enableCorrection){
                    // Get packet with header
                    packet = addHeader(audioBlock);

                    if (sendingSocket instanceof DatagramSocket4){
                        // add checkSum
                        packet = addCheckSum(packet);
                    }
                    
                    // Create buffer
                    if (sendingSocket instanceof DatagramSocket2 || 
                            sendingSocket instanceof DatagramSocket4){
                        sendingBuffer[interleave(bufferIndex)] = packet;
                    } else {
                        sendingBuffer[bufferIndex] = packet;
                    }
                    

                    // Increment packet id and the buffer index
                    pID++;
                    bufferIndex++;

                    // Send packets if buffer is full
                    if (bufferIndex >= bufferSize){
                        //addParityPackets(pID - bufferSize, sendingBuffer);
                        for (DatagramPacket ap : sendingBuffer){
                            sendingSocket.send(ap);
                        }
                        // Reset the buffer
                        sendingBuffer = new DatagramPacket[bufferSize];
                        bufferIndex = 0;
                    }
                } else {
                    DatagramPacket dp = new DatagramPacket(audioBlock, 
                            audioBlock.length, connection);
                    
                    sendingSocket.send(dp);
                }
                
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     *
     * @param audioBlock
     * @return
     * @throws IOException
     */
    public DatagramPacket addHeader(byte[] audioBlock) throws IOException{
        byte[] sendingBlock = new byte[audioBlock.length + headerSize];

        sendingBlock[0] = (byte) this.pID;
        sendingBlock[1] = (byte) (((this.pID) >> 8) & 0xFF);
        sendingBlock[2] = (byte) (((this.pID) >> 16) & 0xFF);
        sendingBlock[3] = (byte) (((this.pID) >> 24) & 0xFF);
        
        // Add packetID to block 
        //sendingBlock[0] = (byte)pID;
        System.arraycopy(audioBlock, 0, sendingBlock, headerSize, audioBlock.length);

        // Create datagram packet
        return new DatagramPacket(sendingBlock, sendingBlock.length, connection);

    }
    
    public DatagramPacket addCheckSum(DatagramPacket packet){
        byte[] block = packet.getData();
        
        byte[] blockWithChecksum = new byte[2 + block.length];
        System.arraycopy(block, 0, blockWithChecksum, 2, block.length);
        blockWithChecksum[0] = 0;
        blockWithChecksum[1] = 0;
        for (int i = 0; i < block.length; i++) {
                blockWithChecksum[0] = (byte) ((blockWithChecksum[0] + block[i]) % 255);
                blockWithChecksum[1] = (byte) ((blockWithChecksum[1] + blockWithChecksum[0]) % 255);
        }
        
        packet.setData(blockWithChecksum);
        packet.setLength(blockWithChecksum.length);
        
        return packet;
    }
    
    public void createParityPacket(DatagramPacket[] sBuffer){
        
    }
    
    public void sendBufferAndParity(DatagramPacket[] buff, DatagramPacket[] pack){
        
    }
    
    public int interleave(int pNo){
        // size 2 interleaver
        int size = 2;
        return ((pNo % size) * size) + (pNo / size);
    }
}
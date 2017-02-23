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

public class SenderThread implements Runnable {

    private final DatagramSocket sendingSocket;
    private final InetSocketAddress connection;
    private final AudioRecorder recorder;
    //private PacketProcessor packetProcessor;
    
    private int headerSize;
    private int pID;
    private int bufferIndex;
    private final int bufferSize;
    DatagramPacket[] sendingBuffer;

    public SenderThread(DatagramSocket socket, InetSocketAddress connection) 
                                            throws LineUnavailableException {
        pID = 0;
        bufferIndex = 0;
        bufferSize = 8;
        headerSize = 4;
        
        sendingSocket = socket;
        this.connection = connection;
        sendingBuffer = new DatagramPacket[bufferSize];
        
        recorder = new AudioRecorder();
        //packetProcessor = new PacketProcessor(null, socket);
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
                
                // Get packet with header
                packet = addHeader(audioBlock);
                
                // Create buffer
                sendingBuffer[interleave(bufferIndex)] = packet;
                
                // Increment packet id and the buffer index
                pID++;
                bufferIndex++;
                
                // Reset the packet ID after 100
//                if (pID == bufferSize)
//                    pID = 0;
                
                // Send packets if buffer is full
                if (bufferIndex >= bufferSize){
                    for (DatagramPacket ap : sendingBuffer){
                        sendingSocket.send(ap);
                    }
                    // Reset the buffer
                    sendingBuffer = new DatagramPacket[bufferSize];
                    bufferIndex = 0;
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
    
    public int interleave(int pNo){
        return ((pNo % 3) * 3) + (pNo / 3);
    }
}
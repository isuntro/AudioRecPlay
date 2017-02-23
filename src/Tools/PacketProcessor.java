///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//package Tools;
//
//import AudioRecPlay.ReceiverThread;
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//
///**
// * 
// * @author Diego Viteri
// */
//public class PacketProcessor {
//    
//    private final DatagramSocket socket;
//    private final int bufferSize;
//    private final int headerSize;
//    // Sender
//    private int pID;
//    private DatagramPacket[] sendingBuffer;
//    private int packetsBuffered;
//    
//    // Receiver
//    private final ReceiverThread receiver;
//    private AudioPacket[] receivingBuffer;
//    
//    public PacketProcessor(ReceiverThread receiver, DatagramSocket socket){
//        this.receiver = receiver;
//        this.socket = socket;
//        headerSize = 1;
//        pID = 0;
//        bufferSize = 4;
//        packetsBuffered = 0;
//        
//        // Used for buffers for future interleaver
//        sendingBuffer = new DatagramPacket[bufferSize];
//        receivingBuffer = new AudioPacket[bufferSize];
//    }
//    
//    /**
//     * Add a header to the send buffer and send
//     * @param packet
//     * @throws IOException 
//     */
//    public void addHeader(DatagramPacket packet) throws IOException{
//        // Get basic packet, set new block size
//        byte[] originalBlock = packet.getData();
//        
//        // Create new block of data
//        byte[] block = new byte[packet.getLength() + headerSize];
//        
//        // Add type byte
//        
//        // Add sequence number byte(s)
//        block[0] = (byte)pID;
//        
//        System.arraycopy(originalBlock, 0, block, headerSize, originalBlock.length);
//        packet.setData(block);
//        
//        sendingBuffer[packetsBuffered] = packet;
//        
//        // Increment pSequence and the packets buffered
//        pID++;
//        if (pID == 120)
//            pID = 0;
//        // Used for buffer, for future interleaver
//        packetsBuffered++;
//        
//        //socket.send(packet);
//        
//        if (packetsBuffered >= bufferSize){
//            // Send them
//            for (DatagramPacket ap : sendingBuffer) {
//                socket.send(ap);
//            }
//            sendingBuffer = new DatagramPacket[bufferSize];
//            packetsBuffered = 0;
//        }
//    }
//    
//    /**
//     * Send packets in the send buffer
//     * @throws java.io.IOException
//     */
////    public void sendPackets() throws IOException {
////        
////    }
//    
//    /**
//     * Processes a modified packet
//     * @param packet The received packet
//     */
//    public void processPacket(DatagramPacket packet){
//        byte[] block = packet.getData();
//        //int bufferID = packetID / 4;
//        
//        // Get and strip the first byte from the block
//        int packetID = block[0];
//        AudioPacket audioPacket = new AudioPacket(getAudio(packet));
//        
//        audioPacket.packetID = packetID;
//        
//        int bufferIndex = packetID % 4;
//        receiver.receivePacket(audioPacket);
//        
//        receivingBuffer[bufferIndex] = audioPacket;
//        
//        // Check buffer completed
//        if (receivingBuffer.length == bufferSize){
//            receiver.receiveBuffer(receivingBuffer);
//            receivingBuffer = new AudioPacket[bufferSize];
//        }
//        
//    }
//    
//    public byte[] getAudio(DatagramPacket packet){
//        byte[] block = new byte[packet.getLength() - headerSize];
//        System.arraycopy(packet.getData(), headerSize, block, 0, block.length);
//        return block;
//    }
//    
//}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Tools;

import AudioRecPlay.ReceiverThread;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

/**
 * 
 * @author Diego Viteri
 */
public class PacketProcessor {
    
    private final DatagramSocket socket;
    
    // Sender
    int pSequence;
    
    // Receiver
    private final ReceiverThread receiver;
    
    public PacketProcessor(ReceiverThread receiver, DatagramSocket socket,
                                InetSocketAddress conn){
        this.receiver = receiver;
        this.socket = socket;
        pSequence = 0;
        
    }
    
    /**
     * Add a packet to the send buffer and send
     * @param packet
     * @throws IOException 
     */
    public void addPacket(DatagramPacket packet) throws IOException{
        // Get basic packet
        
        // Add type byte
        
        // Add sequence number byte(s)
        
        // Increment pSequence
    }
    
    /**
     * Send packets in the send buffer
     * @throws java.io.IOException
     */
    public void sendPackets() throws IOException {
        
    }
    
    /**
     * Receives a packet from the network
     * @param packet 
     */
    public void receive(DatagramPacket packet) {
        byte[] block = packet.getData();
        
        // Check type of block[0]
        // If 1, it's a packet with parity
        
        // At the moment, just process the packet
        processPacket(packet);
        
    }
    
    /**
     * Processes a packet
     * @param packet The received packet
     */
    public void processPacket(DatagramPacket packet){
        byte[] block = packet.getData();
        AudioPacket audioPacket = new AudioPacket(block);
        // Decode the packet ID
        audioPacket.packetID = block[0];
        
        
        
    }
    
}

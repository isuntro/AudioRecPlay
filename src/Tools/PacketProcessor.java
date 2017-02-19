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
    private final ReceiverThread receiver;
    
    // Sender
    
    // Receiver
    
    public PacketProcessor(ReceiverThread receiver, DatagramSocket socket,
                                InetSocketAddress conn){
        this.receiver = receiver;
        this.socket = socket;
        
    }
    
    /**
     * Add a packet to the send buffer and send
     * @param packet
     * @throws IOException 
     */
    public void addPacket(DatagramPacket packet) throws IOException{
        
    }
    
    /**
     * Send packets in the send buffer
     */
    public void sendPackets(int packetID) throws IOException {
        
    }
    
    /**
     * Receives a packet from the network
     * @param packet 
     */
    public void receive(DatagramPacket packet) {
        
    }
    
    /**
     * Processes a packet
     * @param packet 
     */
    public void processPacket(DatagramPacket packet){
        
    }
    
    
}

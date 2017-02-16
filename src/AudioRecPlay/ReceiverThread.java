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

public class ReceiverThread implements Runnable{
    
    static DatagramSocket receiving_socket;
    private PlayThread player;

    public ReceiverThread(DatagramSocket socket) throws LineUnavailableException {
        //Open a socket to receive from on port PORT
        receiving_socket = socket;

        // Get audio player ready
        player = new PlayThread();
        player.start();
    }

    public void start(){
        Thread thread = new Thread(this);
	    thread.start();
    }
    
    public void run (){
        byte[] buffer;

        while (!Thread.interrupted()){
            buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try{
                //receiving_socket.setSoTimeout(1000);
                receiving_socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            byte[] newBuff = new byte[packet.getLength()];
            System.arraycopy(buffer, 0, newBuff, 0, packet.getLength());
            AudioPacket ap = new AudioPacket(newBuff);
            // Add the packet to the buffer
            player.addPacket(ap);

        }
    }
}

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
import uk.ac.uea.cmp.voip.DatagramSocket2;

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
            // data size 512 + 1 ID byte
            buffer = new byte[514];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try{
                //receiving_socket.setSoTimeout(1000);
                receiving_socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }

            byte[] newBuff = new byte[512];
            System.arraycopy(buffer, 2, newBuff, 0, 512);
            AudioPacket ap = new AudioPacket(newBuff);
            // Add the packet to the buffer
            System.out.println("Played packet " + buffer[0] + " sequence :" + buffer[1]);
            player.addPacket(ap);

        }
    }
}

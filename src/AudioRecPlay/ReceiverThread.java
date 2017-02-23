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
import java.util.ArrayList;

import Tools.AudioPacket;

import javax.sound.sampled.LineUnavailableException;

public class ReceiverThread implements Runnable{
    
    static DatagramSocket receiving_socket;
    private PlayThread player;
    private ArrayList<byte[]> audBuffer;
    private byte[][] buffer;
    private int pckCount;
    private int currentBlock = 1;

    public ReceiverThread(DatagramSocket socket) throws LineUnavailableException {
        //Open a socket to receive from on port PORT
        receiving_socket = socket;
        audBuffer = new ArrayList<>();
        buffer = new byte [4][AudioPacket.DATA_SIZE];
        // Get audio player ready
        player = new PlayThread();
        player.start();
    }

    public void start(){
        Thread thread = new Thread(this);
	    thread.start();
    }
    
    public void run () {
        byte[] buffer;
        while (!Thread.interrupted()) {
            // data size 512 + 2 ID byte
            buffer = new byte[AudioPacket.SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            try {
                //receiving_socket.setSoTimeout(1000);
                receiving_socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }


            // Add the packet to the buffer
            if(SenderThread.isInterleaving()){
                processPackets(buffer,buffer[1]);
            }
            else {
                System.out.println("Played packet " + buffer[0] + " sequence :" + buffer[1]);
                player.addPacket(buffer);
            }

        }
    }
    private void processPackets(byte[] buffer, byte blockID){
        if(currentBlock < blockID){
            concealLoss(this.buffer);
            currentBlock++;
        }
        else {
            int position = deinterleaver(buffer[0]);
            System.out.println("Adding packet " + buffer[0] + "         Sequence : " + buffer[1]);
            this.buffer[position] = buffer;
            pckCount++;

            if (pckCount == 4) {
                for (int i = 0; i < 4; i++) {
                    player.addPacket(this.buffer[i]);
                }
                System.out.println(" Playing sequence : " + buffer[1]);
                currentBlock++;
                pckCount = 0;
            }
        }
    }

    private int deinterleaver(int no){
        return ((no % SenderThread.I_SIZE) * SenderThread.I_SIZE) + no/SenderThread.I_SIZE;
    }
    private void concealLoss(byte[][] buffer){
        for(byte[] frame : buffer){
            if(frame[1] == 0 || frame == null || frame.length == 0){
                frame = new byte[AudioPacket.SIZE];
                System.out.println("Empty packet");
            }
            player.addPacket(frame);
        }
        pckCount = 0;
        System.out.println(" Playing sequence :" + currentBlock);
    }
}


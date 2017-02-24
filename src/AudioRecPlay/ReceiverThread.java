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
import uk.ac.uea.cmp.voip.DatagramSocket4;

import javax.sound.sampled.LineUnavailableException;

public class ReceiverThread implements Runnable {

    static DatagramSocket receiving_socket;
    private PlayThread player;
    private ArrayList<byte[]> audBuffer;
    private byte[][] block;
    private int pckCount = 0;
    private int currentBlock = 1;

    public ReceiverThread(DatagramSocket socket) throws LineUnavailableException {
        //Open a socket to receive from on port PORT
        receiving_socket = socket;
        audBuffer = new ArrayList<>();
        block = new byte[SenderThread.BLOCK_SIZE][AudioPacket.DATA_SIZE];
        // Get audio player ready
        player = new PlayThread();
        player.start();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        byte[] buffer;
        while (!Thread.interrupted()) {
            if(!(this.receiving_socket instanceof DatagramSocket4)) {
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
                if (SenderThread.isInterleaving()) {
                    processPackets(buffer, buffer[1]);
                } else {
                    System.out.println("Played packet " + buffer[0] + " sequence :" + buffer[1]);
                    player.addPacket(buffer);
                }
            }
            buffer = new byte[AudioPacket.SIZE];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            byte[] copy;
            try {
                //receiving_socket.setSoTimeout(1000);
                receiving_socket.receive(packet);
                copy = buffer;
                receiving_socket.receive(packet);
                pckCount++;
            } catch (IOException e) {
                e.printStackTrace();
                continue;
            }
            if(buffer[0] == pckCount){
                player.addPacket(buffer);
            }
            else{
                player.addPacket(copy);
            }
            if(pckCount == 4){
                pckCount = 0;
            }
        }
    }

    private void processPackets(byte[] buffer, byte blockID) {
        if (currentBlock < blockID) {
            concealLoss(this.block);
            currentBlock++;
        } else {
            int position = buffer[0] % SenderThread.BLOCK_SIZE;
            this.block[position] = buffer;
            pckCount++;

            if (pckCount == SenderThread.BLOCK_SIZE) {
                for (int i = 0; i < SenderThread.BLOCK_SIZE; i++) {
                    System.out.println("Adding packet " + block[i][0] + "      Block : " + block[i][1]);
                    player.addPacket(this.block[i]);
                }
                System.out.println(" Playing sequence : " + buffer[1]);
                currentBlock++;
                pckCount = 0;
            }
        }
    }

    private int deinterleaver(int no) {
        return ((no % SenderThread.I_SIZE) * SenderThread.I_SIZE) + no / SenderThread.I_SIZE;
    }

    private void concealLoss(byte[][] buffer) {
        for (byte[] frame : buffer) {
            if (frame[1] != currentBlock || frame == null || frame.length == 0) {
                frame = silence();
                System.out.println("Empty packet");
            } else {
                System.out.println("Adding packet " + frame[0] + "      Block : " + frame[1]);
            }
            player.addPacket(frame);
        }
        pckCount = 0;
        System.out.println(" Playing sequence :" + currentBlock);
    }

    private byte[] silence() {
        return new byte[AudioPacket.SIZE];
    }
}

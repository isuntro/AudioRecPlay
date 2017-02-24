package AudioRecPlay;

/*
 * SenderThread.java
 *
 * Created on 15 January 2003, 15:29
 */

/**
 *
 * @author  Diego Viteri
 */
import CMPC3M06.AudioRecorder;
import Tools.AudioPacket;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class SenderThread implements Runnable {

    private DatagramSocket sending_socket;
    private InetSocketAddress connection;
    private final AudioRecorder recorder;
    private static boolean interleave;
    private AudioPacket[] block;
    public final static int I_SIZE = 3;
    public final static int BLOCK_SIZE = I_SIZE*I_SIZE;
    private int packCount = 0;
    public SenderThread(DatagramSocket socket, InetSocketAddress connection, boolean interleave) throws LineUnavailableException {
        this.interleave = interleave;
        sending_socket = socket;
        this.connection = connection;
        block = new AudioPacket[BLOCK_SIZE];
        recorder = new AudioRecorder();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        DatagramPacket packet;
        DatagramPacket apack;
        while (!Thread.interrupted()) {
            try {
                // create a new audio packet
                // with newly recorded audio data
                if(!(this.sending_socket instanceof DatagramSocket4)) {
                    AudioPacket audioPack = new AudioPacket(recorder.getBlock());
                    packCount++;
                    if (interleave) {
                        processPackets(audioPack);
                    } else {
                        packet = new DatagramPacket(audioPack.getBytes(), AudioPacket.SIZE, connection);
                        sending_socket.send(packet);
                    }
                }
                else {
                    AudioPacket audioPacket = new AudioPacket(recorder.getBlock());
                    packCount++;
                    packet = new DatagramPacket(audioPacket.getBytes(), AudioPacket.SIZE, connection);
                    apack = new DatagramPacket(audioPacket.getBytes(), AudioPacket.SIZE, connection);
                    sending_socket.send(packet);
                    sending_socket.send(apack);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void processPackets(AudioPacket packet) throws IOException {
        block[interleaver(packet.getPacketID())] = packet;
        if(packCount == BLOCK_SIZE){
            for(int i=0; i < BLOCK_SIZE ; i++){
                sending_socket.send(new DatagramPacket(block[i].getBytes(),AudioPacket.SIZE, connection));
            }
            packCount = 0;
        }
    }
    private int interleaver(int no){
        return ((no % I_SIZE) * I_SIZE) + no / I_SIZE;
    }
    public static boolean isInterleaving(){
        return interleave;
    }
}
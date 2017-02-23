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

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class SenderThread implements Runnable {

    private DatagramSocket sending_socket;
    private InetSocketAddress connection;
    private final AudioRecorder recorder;
    private static boolean interleave = true;
    private ArrayList<AudioPacket> buffer;
    public final static int I_SIZE = 2;

    public SenderThread(DatagramSocket socket, InetSocketAddress connection, boolean interleave) throws LineUnavailableException {
        this.interleave = interleave;
        sending_socket = socket;
        this.connection = connection;
        buffer = new ArrayList<>();
        recorder = new AudioRecorder();
    }

    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    public void run() {
        DatagramPacket packet;

        while (!Thread.interrupted()) {
            try {
                // create a new audio packet
                // with newly recorded audio data
                AudioPacket audioPack = new AudioPacket(recorder.getBlock());
                if(interleave){
                    processPackets(audioPack);
                }
                else {
                    packet = new DatagramPacket(audioPack.getBytes(), AudioPacket.SIZE, connection);
                    sending_socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private void processPackets(AudioPacket packet) throws IOException {
        buffer.add(packet);
        if(buffer.size() == 4){
            for(int i=1; i < 5 ; i++){
                int interPos = interleaver(i);
                sending_socket.send(new DatagramPacket(buffer.get(interPos).getBytes(),AudioPacket.SIZE, connection));
            }
            buffer.clear();
        }
    }
    private int interleaver(int no){
        return ((no % I_SIZE) * I_SIZE) + no/ I_SIZE;
    }
    public static boolean isInterleaving(){
        return interleave;
    }
}
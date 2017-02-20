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
import Tools.AudioPacket;
import uk.ac.uea.cmp.voip.DatagramSocket2;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SenderThread implements Runnable {

    private DatagramSocket sending_socket;
    private InetSocketAddress connection;
    private final AudioRecorder recorder;
    private static boolean interleave = true;
    private ArrayList<AudioPacket> buffer;

    public SenderThread(DatagramSocket socket, InetSocketAddress connection) throws LineUnavailableException {
        sending_socket = socket;
        this.connection = connection;
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
                    packet = new DatagramPacket(audioPack.getBytes(), 514, connection);
                    sending_socket.send(packet);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void processPackets(AudioPacket apacket) throws IOException {
        buffer.add(apacket);
        if(buffer.size() == 4){
            sending_socket.send(new DatagramPacket(buffer.get(1).getBytes(),514,connection));
            sending_socket.send(new DatagramPacket(buffer.get(3).getBytes(),514,connection));
            sending_socket.send(new DatagramPacket(buffer.get(0).getBytes(),514,connection));
            sending_socket.send(new DatagramPacket(buffer.get(2).getBytes(),514,connection));
            buffer.clear();
        }
    }
    public void interleaver(){

    }
}
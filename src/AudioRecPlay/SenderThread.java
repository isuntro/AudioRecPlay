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

    public SenderThread(DatagramSocket socket, InetSocketAddress connection, boolean interleave) throws LineUnavailableException {
        this.interleave = interleave;
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
    private void processPackets(AudioPacket apacket) throws IOException {
        buffer.add(interleaver(apacket.getPacketID()),apacket);
        if(buffer.size() == 4){
            for(AudioPacket aPack : buffer){
                sending_socket.send(new DatagramPacket(aPack.getBytes(),514, connection));
            }
            buffer.clear();
        }
    }
    private int interleaver(int no){
        return ((no % 2) * 2) + no/2;
    }
    public static boolean isInterleaving(){
        return interleave;
    }
}
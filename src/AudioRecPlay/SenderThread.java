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
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SenderThread implements Runnable {

    private DatagramSocket sending_socket;
    private InetSocketAddress connection;
    private final AudioRecorder recorder;

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
        byte[] buffer;
        DatagramPacket packet;
        int i=1;
        while (!Thread.interrupted()) {
            try {
                //System.out.println("Recording...");
                // create a new audio packet
                // with newly recorded audio data
                AudioPacket aPacket = new AudioPacket(recorder.getBlock());
                if(i == 50) i=1;
                System.out.println("Sent packet : " + i);
                aPacket.setPacketID(i++);
                packet = new DatagramPacket(aPacket.getBytes(), 513, connection);
                sending_socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
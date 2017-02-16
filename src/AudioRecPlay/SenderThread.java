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
        while (!Thread.interrupted()) {
            try {
                System.out.println("Recording...");
                buffer = recorder.getBlock();
                packet = new DatagramPacket(buffer, buffer.length, connection);
                sending_socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
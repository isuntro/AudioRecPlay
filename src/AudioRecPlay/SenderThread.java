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
import Tools.PacketProcessor;

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import java.io.*;

public class SenderThread implements Runnable {

    private final DatagramSocket sending_socket;
    private final InetSocketAddress connection;
    private final AudioRecorder recorder;
    private PacketProcessor packetProcessor;

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
                packetProcessor.addPacket(packet);
                
                //sending_socket.send(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
package AudioRecPlay;

/*
 * VoiceDuplex.java
 *
 * Created on 15 January 2003, 17:11
 */

import javax.sound.sampled.LineUnavailableException;
import java.net.*;

/**
 *
 * @author  Diego Viteri
 */
public class VoiceDuplex {
    
    public static void main (String[] args){



        // Globals
        int PORT = 55555;
        InetSocketAddress connection = new InetSocketAddress("localhost", PORT);
        DatagramSocket socket;

        try{
           socket = new DatagramSocket(PORT);

            // Start receiver and sender
           new ReceiverThread(socket).start();
           new SenderThread(socket, connection).start();
        } catch (SocketException e){
            System.out.println("Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }
    
}

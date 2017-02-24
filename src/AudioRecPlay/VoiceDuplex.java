package AudioRecPlay;

/*
 * VoiceDuplex.java
 *
 * Created on 15 January 2003, 17:11
 */

import javax.sound.sampled.LineUnavailableException;
import java.net.*;
import uk.ac.uea.cmp.voip.*;

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
        
        boolean enableCorrection = false;
        int bufferSize = 4;
        int headerSize = 4;
        
        try{
           socket = new DatagramSocket3(PORT);
           
           if (socket instanceof DatagramSocket2 ||
                socket instanceof DatagramSocket3 ||
                socket instanceof DatagramSocket4){
                enableCorrection = true;
                new ReceiverThread(socket, enableCorrection, bufferSize, headerSize).start();
                new SenderThread(socket, connection, enableCorrection, bufferSize, headerSize).start();
           } else {
                new ReceiverThread(socket, enableCorrection, bufferSize, headerSize).start();
                new SenderThread(socket, connection, enableCorrection, bufferSize, headerSize).start();
           }
            // Start receiver and sender
           //new ReceiverThread(socket).start();
           //new SenderThread(socket, connection).start();
        } catch (SocketException e){
            System.out.println("Could not open UDP socket to receive from.");
            e.printStackTrace();
            System.exit(0);
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }

    }
    
}

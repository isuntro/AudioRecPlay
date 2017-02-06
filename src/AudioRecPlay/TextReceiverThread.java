package AudioRecPlay;

/*
 * TextReceiver.java
 *
 * Created on 15 January 2003, 15:43
 */

/**
 *
 * @author  abj
 */
import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TextReceiverThread implements Runnable{
    
    static DatagramSocket receiving_socket;
    
    public void start(){
        Thread thread = new Thread(this);
	thread.start();
    }
    
    public void run (){
     
        //***************************************************
        //Port to open socket on
        int PORT = 55555;
        //***************************************************
        
        //***************************************************
        //Open a socket to receive from on port PORT
        
        //DatagramSocket receiving_socket;
        try{
		receiving_socket = new DatagramSocket(PORT);
	} catch (SocketException e){
                System.out.println("ERROR: TextReceiver: Could not open UDP socket to receive from.");
		e.printStackTrace();
                System.exit(0);
	}
        //***************************************************
        
        //***************************************************
        //Main loop.
        
        boolean running = true;
        
        while (running){
         
            try{
                //Receive a DatagramPacket (note that the string cant be more than 80 chars)
                byte[] buffer = new byte[400];
                DatagramPacket packet = new DatagramPacket(buffer, 0, 400);

                receiving_socket.receive(packet);
                
                Audio audio = new Audio();
                audio.play(buffer);
                
                //Get a string from the byte buffer
                //String str = new String(buffer);
                //Trim the string and display it
                //System.out.print(str);
                
                //The user can type EXIT to quit
//                if (str.substring(0,4).equals("EXIT")){
//                     running=false;
//                }
                running = false;
            } catch (IOException e){
                System.out.println("ERROR: TextReceiver: Some random IO error occured!");
                e.printStackTrace();
            } catch (Exception ex) {
                Logger.getLogger(TextReceiverThread.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        //Close the socket
        receiving_socket.close();
        //***************************************************
    }
}

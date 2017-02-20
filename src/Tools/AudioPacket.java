package Tools;

import java.util.Iterator;

/**
 * An audio packet (array of bytes)
 * The only reason this is declared as an object is to add some extra properties
 * along with the block rather than on its own.
 * @author Diego Viteri
 */
public class AudioPacket {
    private static int metaSize = 2;
    private int packetID;
    private int sequenceID;
    private byte[] data;
    private static int i=1;
    private static int j=1;
    public AudioPacket(byte[] block){
        this.packetID = i;
        this.data = block;
        this.sequenceID = j;
        if(i == 20) {
            j++;
            i=0;
        }
        i++;
    }

    public byte[] getData(){
        return data;
    }

    public int getPacketID(){
        return packetID;
    }
    public void setPacketID(int id){
        this.packetID = id;
    }
    public byte[] getBytes(){
        int size = this.data.length + metaSize;
        byte[] bytes = new byte[size];
        bytes[0] = (byte)packetID;
        bytes[1] = (byte)sequenceID;
        System.arraycopy(this.data, 0, bytes, metaSize, this.data.length);
        return bytes;
    }
}

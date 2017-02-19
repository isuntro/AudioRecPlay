package Tools;

/**
 * An audio packet (array of bytes)
 * The only reason this is declared as an object is to add some extra properties
 * along with the block rather than on its own.
 * @author Diego Viteri
 */
public class AudioPacket {
    private int packetID;
    private byte[] data;

    public AudioPacket(byte[] block){
        this.data = block;
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
        int metaSize = 1;
        int size = this.data.length + metaSize;
        byte[] bytes = new byte[size];
        bytes[0] = (byte)packetID;
        System.arraycopy(this.data, 0, bytes, metaSize, this.data.length);
        return bytes;
    }
}

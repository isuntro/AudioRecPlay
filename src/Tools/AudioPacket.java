package Tools;

/**
 * An audio packet (array of bytes)
 * The only reason this is declared as an object is to add some extra properties
 * along with the block rather than on its own.
 * @author Diego Viteri
 */
public class AudioPacket {
    public int packetID;
    private byte[] block;

    public AudioPacket(byte[] block){
        this.block = block;
    }

    public byte[] getBlock(){
        return block;
    }

}

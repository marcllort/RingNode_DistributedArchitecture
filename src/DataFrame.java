import java.io.Serializable;


public class DataFrame implements Serializable {

    //true if it is a token frame
    boolean isACK = false;
    //true if a frame reached its intended recipient
    boolean frameStatus = false;
    //Destination port of the frame
    int destination_addr;
    //Source port of the frame
    int source_addr;
    //Message the frame is transmitting (if any)
    int actualValue;

    public DataFrame( boolean isACK, boolean frameStatus, int destination_addr, int source_addr, int actualValue) {
        this.isACK = isACK;
        this.frameStatus = frameStatus;
        this.destination_addr = destination_addr;
        this.source_addr = source_addr;
        this.actualValue = actualValue;
    }

    //create either a Token or amp frame
    public DataFrame() {
    }

    public boolean getFrameStatus() {
        return frameStatus;
    }

    public void acknowledge() {
        frameStatus = true;
    }

    public int getDes() {
        return this.destination_addr;
    }

    public int getSource() {
        return this.source_addr;
    }

    public int getActualValue() {
        return this.actualValue;
    }
}
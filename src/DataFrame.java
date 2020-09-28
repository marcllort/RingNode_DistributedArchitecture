import java.io.Serializable;

/**
 * A class which represents a data frame on the
 * token ring network.It is serializable which means I can
 * flatten it and reuse later. This essentially means that the object
 * exists beyond the lifetime of the virtual machine.
 */

public class DataFrame implements Serializable {
    private static final long serialVersionUID = -7309788301614787772L;
    //true if it is a token frame
    boolean token = false;
    boolean isACK = false;
    //true if a frame reached its intended recipient
    boolean frameStatus = false;
    //Destination port of the frame
    int destination_addr;
    //Source port of the frame
    int source_addr;
    //Message the frame is transmitting (if any)
    int actualValue;

    public DataFrame(boolean token, boolean isACK, boolean frameStatus, int destination_addr, int source_addr, int actualValue) {
        this.token = token;
        this.isACK = isACK;
        this.frameStatus = frameStatus;
        this.destination_addr = destination_addr;
        this.source_addr = source_addr;
        this.actualValue = actualValue;
    }

    //create either a Token or amp frame
    public DataFrame() {

    }


    //create a data frame
    public DataFrame(int source, int actualValue) {
        this.source_addr = source;
        this.actualValue = actualValue;
    }

    public void setAsToken() {
        token = true;
    }

    public void setAsNoToken() {
        token = false;
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
import java.io.Serializable;


public class DataFrame implements Serializable {

    int destination_addr;

    int actualValue;

    public DataFrame() {
    }

    public int getDes() {
        return this.destination_addr;
    }

    public int getActualValue() {
        return this.actualValue;
    }
}
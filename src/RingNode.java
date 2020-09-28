import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

/**
 * An individual node on the token ring network.
 * Implements runnable as it is meant to be executed by a thread.
 * Once it is ran it will first wait to receive a packet (unless it is
 * the monitor, in that case it will inject the token).
 * When it receives a packet it unpacks it into the DataFrame class
 * and examines it.
 * If it is the token it moves it on unless it has a message
 * to send.
 * Otherwise it checks if it is meant for it, if it isn't it just sends
 * it on.
 * The node keeps on receiving frames while the boolean receiving is true.
 */

public class RingNode implements Runnable {

    public static final int MAX_BUFFER = 1024;

    int id;
    int port;
    InetAddress address;
    DatagramSocket socket = null;
    int nextNodePort;
    boolean receiving = true;
    boolean hasMessageToSend = false;
    //the value we are going to send
    int sendingValue;
    //the value that the node thinks it's the actual one
    int savedValue;
    //the value in which the node wants to modify the actualvalue
    int addingValue;
    int destin;
    ArrayList<RingNode> nodes;
    int receiverPort;

    RingNode(int id, int port) {
        try {
            savedValue = 0;
            this.id = id;
            this.port = port;
            socket = new DatagramSocket(port);
            address = socket.getLocalAddress();

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public int getSocketPort() {
        return port;
    }

    public void changeTotalValue(int addingValue) {
        hasMessageToSend = true;
        this.addingValue = addingValue;
    }

    public void sendMessage(DataFrame frame) {
        hasMessageToSend = false;
        frame.setAsNoToken();
        sendFrame(frame);
    }

    //send a frame with the DatagramPacket through the socket
    public void sendFrame(DataFrame frame) {

        DatagramPacket packet = null;
        byte[] buf = new byte[MAX_BUFFER];

        try {
            ByteArrayOutputStream fis = new ByteArrayOutputStream();
            ObjectOutputStream is = new ObjectOutputStream(fis);
            is.writeObject(frame);
            is.flush();
            buf = fis.toByteArray();
            address = InetAddress.getByName("127.0.0.1");
            packet = new DatagramPacket(buf, buf.length, address, nextNodePort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

    }

    //Make a new token frame send it on
    public void makeToken() {
        DataFrame tokenFrame = new DataFrame();
        tokenFrame.setAsToken();
        sendFrame(tokenFrame);
    }

    public void makeAmp() {
        DataFrame ampFrame = new DataFrame();
        ampFrame.setAsNoToken();
        sendFrame(ampFrame);
    }


    //stop the node from receiving
    public void switchReceiving() {
        try {
            receiving = !receiving;
            //socket.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    int checkFrame(DataFrame frame) {
        //The message was sent by itself and it isn't a token
        if (frame.source_addr == id - 1 && !frame.token){
            return 2;
        }
        if (frame.token) {
            return 0;
        } else  {
            return 1;
        }
    }

    public void run() {
        DatagramPacket packet = null;
        byte[] buffer;

        try {
            System.out.println("Node " + id + " has started");
            //When we start we create the token from node 1 and pass it
            if (id == 1){
                DataFrame frame = new DataFrame();
                frame.setAsToken();
                sendMessage(frame);
            }
            while (receiving) {
                buffer = new byte[MAX_BUFFER];
                packet = new DatagramPacket(buffer, buffer.length);
                //socket.setSoTimeout(5000);
                socket.receive(packet);
                buffer = packet.getData();

                ByteArrayInputStream fis = new ByteArrayInputStream(buffer);
                ObjectInputStream in = new ObjectInputStream(fis);
                DataFrame frame = (DataFrame) in.readObject();


                switch (checkFrame(frame)) {
                    case 0:
                        //I have the token and something to send
                        if (hasMessageToSend) {
                            //I update the actual value adding my addingvalue and the value received
                            sendingValue = frame.actualValue + addingValue;
                            savedValue = sendingValue;
                            frame.actualValue = sendingValue;
                            sendMessage(frame);
                        } else {
                            //the message has the value updated but it's not for me
                            savedValue = frame.actualValue;
                            sendFrame(frame);
                        }
                        break;

                    case 1:
                        //I pass the message with the token
                            sendFrame(frame);
                        break;

                    case 2:
                            /*System.out.println("Node " + id + " has received the following message.");
                            System.out.println("Received: " + frame.getActualValue());
                            //if(monitor){System.out.println("Data Link Frame has passed the monitor.");}
                            if (frame.getDes() == port) {
                                //The node has received a confirmation that previous message it sent has been received.
                                if (frame.getFrameStatus()) {
                                    System.out.println("Node " + id + " acknowledges their frame reached the destination");
                                    System.out.println("Node " + id + " has released the token");
                                    makeToken();    // Token to next Node
                                } else {
                                    //The message is meant for this node, the new value has passed through all nodes
                                    frame.setAsToken();
                                    System.out.println("Node " + id + " has sent ACK.");
                                }
                            } else {
                                sendFrame(frame);
                            }*/
                        //The message was originaly sent by me so all the nodes have now the correct value
                        frame.setAsToken();
                        break;
                }
            }
            //If after 5 seconds nothing has been received, timeout
            //If the node is the first one, creates the token again
        } catch (SocketTimeoutException E) {
            System.out.println("ST: Hit timeout !");
            if (id == 1) {
                makeToken();
            }
            run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

}
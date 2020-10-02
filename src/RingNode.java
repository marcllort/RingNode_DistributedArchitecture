import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import static java.lang.Thread.sleep;


public class RingNode implements Runnable {

    public static final int MAX_BUFFER = 1024;

    int port;
    InetAddress address;
    DatagramSocket socket = null;

    boolean receiving = true;
    boolean readMode = true;
    boolean hasMessageToSend = false;
    boolean firstReceived = false;
    //the value we are going to send
    int sendingValue;
    //the value that the node thinks it's the actual one
    int savedValue;
    //the value in which the node wants to modify the actualvalue
    int addingValue;
    ArrayList<RingNode> nodes;


    RingNode(int port, boolean readMode) {
        try {
            savedValue = 0;
            this.port = port;
            this.readMode = readMode;
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

    private int getCurrentValue() {
        return savedValue;
    }

    public void updateCurrentValue(int addingValue) {
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
        try {
            ByteArrayOutputStream fis = new ByteArrayOutputStream();
            ObjectOutputStream is = new ObjectOutputStream(fis);
            is.writeObject(frame);
            is.flush();
            byte[] buf = fis.toByteArray();
            address = InetAddress.getByName("127.0.0.1");
            DatagramPacket packet = new DatagramPacket(buf, buf.length, address, nextPortAvailable());
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

    //stop the node from receiving
    public void switchReceiving() {
        try {
            receiving = !receiving;
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    int checkFrame(DataFrame frame) {
        //The message was sent by itself and it isn't a token
        if (frame.source_addr == port - 1 && !frame.token) {
            return 2;
        }
        if (frame.token) {
            return 0;
        } else {
            return 1;
        }
    }


    private void tokenManagement() throws Exception {
        while (receiving) {
            byte[] buffer = new byte[MAX_BUFFER];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

            socket.receive(packet);
            buffer = packet.getData();

            ByteArrayInputStream fis = new ByteArrayInputStream(buffer);
            ObjectInputStream in = new ObjectInputStream(fis);
            DataFrame frame = (DataFrame) in.readObject();

            firstReceived = true;

            switch (checkFrame(frame)) {
                case 0:
                    //If I have the token and something to send

                    if (hasMessageToSend) {
                        //I update the actual value adding my addingvalue and the value received
                        sendingValue = frame.actualValue + addingValue;
                        savedValue = sendingValue;
                        frame.actualValue = sendingValue;
                        sendMessage(frame);
                        System.out.println("Sending value:"+frame.actualValue);
                    } else {
                        System.out.println("Saving value:"+frame.actualValue);
                        savedValue = frame.actualValue;
                        sendFrame(frame);
                    }
                    break;

                case 1:
                    //I pass the message with the token
                    sendFrame(frame);
                    break;

                case 2:
                    System.out.println("Node " + port + " has received the following message.");
                    System.out.println("Received: " + frame.getActualValue());

                    //The message was originally sent by me so all the nodes have now the correct value
                    frame.setAsToken();
                    break;
            }
        }
    }

    public void startNode() {
        try {
            while (true) {
                while (firstReceived) {
                    if (!readMode) {
                        for (int i = 0; i < 10; i++) {
                            int value = getCurrentValue();
                            updateCurrentValue(1);

                            sleep(1000);

                        }
                    } else {
                        for (int i = 0; i < 10; i++) {
                            savedValue = getCurrentValue();
                            sleep(1000);
                        }
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void run() {

        try {
            System.out.println("Node " + port + " has started");
            //When we start we create the token from node 1 and pass it
            /*if (port == Utils.MIN_PORT_NUMBER) {
                DataFrame frame = new DataFrame();
                frame.setAsToken();
                sendMessage(frame);
            }*/
            tokenManagement();


            //If after 5 seconds nothing has been received, timeout else, if the node is the first one, creates the token again
        } catch (SocketTimeoutException E) {
            System.out.println("ST: Hit timeout !");
            if (port == Utils.MIN_PORT_NUMBER) {
                makeToken();
            }
            run();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public int nextPortAvailable() {
        ArrayList<Integer> ports = Utils.checkPorts();
        if (ports.size() > port - Utils.MIN_PORT_NUMBER + 1) {
            return ports.get(port - Utils.MIN_PORT_NUMBER + 1);
        }
        return ports.get(0);
    }


}
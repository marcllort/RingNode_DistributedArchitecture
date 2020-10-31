import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static int MIN_PORT_NUMBER = 1000;
    public static int MAX_PORT_NUMBER = 1020;

    private static BufferedReader inputStream =
            new BufferedReader(new InputStreamReader(System.in));

    public static int readNonNegativeInt() {
        int readInt = readInt();
        while (readInt < 0) {
            System.out.println("Not a non-negative integer: " + readInt);
            System.out.println("Enter a non-negative integer:");
            readInt = readInt();
        }

        return readInt;
    }

    public static int readInt() {
        String line = null;
        int readInt = 0;

        while (true) {
            try {
                line = inputStream.readLine();
                readInt = Integer.parseInt(line);
                break;
            } catch (IOException e) {
                System.err.println("Unexpected IO ERROR: " + e);
                System.exit(1);
            } catch (NumberFormatException e) {
                System.err.println("Not a valid integer: " + line);
            }
        }
        return readInt;
    }


    /**
     * Checks to see if a specific port is available.
     *
     * @param port the port to check for availability
     */
    public static boolean available(int port) {
        if (port < MIN_PORT_NUMBER || port > MAX_PORT_NUMBER) {
            throw new IllegalArgumentException("Invalid start port: " + port);
        }

        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return true;
        } catch (IOException e) {
        } finally {
            if (ds != null) {
                ds.close();
            }

            if (ss != null) {
                try {
                    ss.close();
                } catch (IOException e) {
                    /* should not be thrown */
                }
            }
        }

        return false;
    }

    /**
     * Checks the ports avaliable between the range of ports defined
     * @return array of avaliable ports
     */
    public static ArrayList<Integer> checkPorts(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = MIN_PORT_NUMBER; i < MAX_PORT_NUMBER; i++){
            if (!Utils.available(i)){
                arrayList.add(i);
            }
        }
        return arrayList;
    }

    /**
     * Returns the number of the first avaliable port
     * @return port number
     */
    public static int firstPortAvaliable(){
        for (int i = MIN_PORT_NUMBER; i < MAX_PORT_NUMBER; i++){
            if (Utils.available(i)){
                return i;
            }
        }
        return 0;
    }

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static int MIN_PORT_NUMBER = 1000;
    public static int MAX_PORT_NUMBER = 1100;

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

    public static String readString() {
        String line = "";
        while (true) {
            try {
                line = inputStream.readLine();
                break;
            } catch (IOException e) {
                System.err.println("Unexpected IO ERROR: " + e);
                System.exit(1);
            }
        }
        return line;
    }

    public static int printMenu(){
        System.out.println("Menu:");
        System.out.println("(0) Send a message.");
        System.out.println("(1) Add a node.");
        System.out.println("(2) Delete a node.");
        System.out.println("(3) Quit.\n");
        System.out.println("Please enter menu choice: ");

        int option = Utils.readNonNegativeInt();

        return option;
    }

    public static int printWriteServers(){
        System.out.println("Number of servers in WRITING MODE: ");
        int number = Utils.readNonNegativeInt();

        return number;
    }

    public static int printReadServers(){
        System.out.println("Number of servers in READING MODE: ");
        int number = Utils.readNonNegativeInt();

        return number;
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

    public static ArrayList<Integer> checkPorts(){
        ArrayList<Integer> arrayList = new ArrayList<>();
        for (int i = MIN_PORT_NUMBER; i < MAX_PORT_NUMBER; i++){
            if (Utils.available(i)){
                arrayList.add(i);
                break;
            }
        }
        return arrayList;
    }

    public static int firstPortAvaliable(){
        for (int i = MIN_PORT_NUMBER; i < MAX_PORT_NUMBER; i++){
            if (Utils.available(i)){
                return i;
            }
        }
        return 0;
    }

}

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Utils {

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
        System.out.println("(3) Quit.");
        System.out.println();
        System.out.println("Please enter menu choice: ");

        int option = Utils.readNonNegativeInt();

        return option;
    }

}

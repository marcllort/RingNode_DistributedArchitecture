public class Main {

    // marc.llort
    // alex.almansa

    public static void main(String args[]) {

        boolean readMode = false;
        System.out.println("Enter the node mode:");
        System.out.println("    1. Read mode");
        System.out.println("    2. Write mode");
        System.out.println("    3. Start servers");

        int mode = Utils.readNonNegativeInt();
        while (mode != 1 && mode != 2 && mode != 3) {
            System.out.println("Enter the node mode:");
            System.out.println("    1. Read mode");
            System.out.println("    2. Write mode");
            System.out.println("    3. Start servers");
            mode = Utils.readNonNegativeInt();
        }

        if (mode == 1) readMode = true;

        if (mode == 3) {
            // Enviem Token (al primer port, 1000)
            RingNode node = new RingNode(999, readMode);
            node.sendFirst();
            node.closeNode();

        } else {
            //Fer arp, amb aixo sabem quins port hi ha oberts i quants, aixi obrim el seguent port
            int port = Utils.firstPortAvaliable();
            RingNode node = new RingNode(port, readMode);
            System.out.println("PORT: " + port);

            Thread thread = new Thread(node);
            thread.start();

            node.startNode();
        }

    }

}

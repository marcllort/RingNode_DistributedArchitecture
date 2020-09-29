import java.io.*;
import java.util.*;


public class Network {

    private static BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
    private ArrayList<RingNode> nodes = new ArrayList<RingNode>();
    private int option = -1;
    private int numNodes = -1;
    private int ports = 8501;

    public void startNetwork() {

        initNetwork2();

        do {
            option = Utils.printMenu();

            switch (option) {
                case 0:
                    System.out.println("Please enter the sender's id:");
                    int senderId = Utils.readNonNegativeInt();
                    System.out.println("Please enter value you want to add:");
                    int value = Utils.readInt();

                    nodes.get(senderId - 1).updateCurrentValue(value);
                    break;
                case 1:
                    System.out.println("ReadMode or WriteMode? [W/R]");
                    String mode = Utils.readString();

                    while (mode != "W" && mode != "R") {
                        System.out.println("Mode unrecognized. ReadMode or WriteMode? [W/R]");
                        mode = Utils.readString();
                    }

                    boolean readMode = true;
                    if (mode == "W") {
                        readMode = false;
                    }

                    nodes = addNode(numNodes, ports, readMode);
                    numNodes++;
                    ports++;
                    System.out.println(numNodes);
                    break;
                case 2:
                    System.out.println("Please enter the node's id:");
                    int deleteId = Utils.readNonNegativeInt();
                    deleteNode(deleteId);
                    numNodes--;
                    break;
                case 3:
                    for (int l = 0; l < numNodes; l++) {
                        nodes.get(l).switchReceiving();
                    }
                    System.out.println("Program quitting.");
                    break;
                default:
                    System.out.println("Not a valid menu choice.");
                    System.out.println();
                    break;
            }
        } while (option != 1);

    }

    private void initNetwork() {
        System.out.println("Please enter the number of nodes:");
        numNodes = Utils.readNonNegativeInt();

        ArrayList<Thread> nodesThreads = new ArrayList<Thread>();
        nodes = new ArrayList<RingNode>();
        RingNode temp;
        for (int j = 0; j < numNodes; j++) {
            temp = new RingNode(j + 1, ports, false);
            nodes.add(temp);
            ports++;
        }

        initRingConn(numNodes);
        Thread r;

        for (int i = 0; i < numNodes; i++) {
            r = new Thread(nodes.get(i));
            nodesThreads.add(r);
        }

        for (int k = 0; k < numNodes; k++) {
            nodesThreads.get(k).start();
        }
    }

    private void initNetwork2() {
        int write = Utils.printWriteServers();
        int read = Utils.printReadServers();


        // Start first de read servers and then the write servers
        ArrayList<Thread> nodesThreads = new ArrayList<Thread>();
        nodes = new ArrayList<RingNode>();
        RingNode temp;
        int j = 0;
        for (j = 0; j < read; j++) {
            temp = new RingNode(j + 1, ports, true);
            nodes.add(temp);
            ports++;
        }
        for (j = j; j < read + write; j++) {
            temp = new RingNode(j + 1, ports, false);
            nodes.add(temp);
            ports++;
        }

        numNodes = read + write;

        initRingConn(numNodes);
        Thread r;

        for (int i = 0; i < numNodes; i++) {
            r = new Thread(nodes.get(i));
            nodesThreads.add(r);
        }

        System.out.println("Start? [Y/N]");
        String start = Utils.readString();

        while (!start.equals("Y")) {
            System.out.println("Error. Start? [Y/N]");
            start = Utils.readString();
        }

        // First the read Nodes will start
        for (int k = 0; k < numNodes; k++) {
            nodesThreads.get(k).start();
        }

    }

    public ArrayList<RingNode> addNode(int numNodes, int port, boolean readMode) {
        RingNode lastNode, next, node;
        Thread newNode;

        lastNode = nodes.get(numNodes - 1);
        next = new RingNode(lastNode.id + 1, port, readMode);
        next.nextNodePort = lastNode.nextNodePort;
        lastNode.nextNodePort = next.getSocketPort();

        nodes.add(next);
        newNode = new Thread(nodes.get(numNodes));

        for (int i = 0; i < numNodes + 1; i++) {
            node = nodes.get(i);
            System.out.println(node.nextNodePort);
            System.out.println(node.port);
            System.out.println(node.id);
        }

        newNode.start();

        return nodes;
    }

    public void deleteNode(int nodeid) {
        nodes.get(nodeid - 2).nextNodePort = nodes.get(nodeid - 1).nextNodePort;
        nodes.get(nodeid - 1).switchReceiving();
        nodes.remove(nodeid);
    }

    public void initRingConn(int numNodes) {
        RingNode node = null;
        RingNode nextNode = null;
        node = nodes.get(numNodes - 1);
        nextNode = nodes.get(0);
        node.nextNodePort = nextNode.getSocketPort();
        node = nextNode;
        for (int i = 1; i < numNodes; i++) {
            nextNode = nodes.get(i);
            node.nextNodePort = nextNode.getSocketPort();
            node = nextNode;
            node.nodes = nodes;
        }
    }


}
import java.io.*;
import java.util.*;


public class Network {

    private static BufferedReader inputStream = new BufferedReader(new InputStreamReader(System.in));
    private ArrayList<RingNode> nodes = new ArrayList<RingNode>();
    private int option = -1;
    private int numNodes = -1;
    private int ports = 8501;

    public void main(String args[]) {

        initNetwork();

        do {
            option = Utils.printMenu();

            switch (option) {
                case 0:
                    System.out.println("Please enter the sender's id:");
                    int senderid = Utils.readNonNegativeInt();
                    System.out.println("Please enter the receiver's id:");
                    int receiverid = Utils.readNonNegativeInt();
                    System.out.println("Please enter the message:");
                    String msg = Utils.readString();

                    nodes.get(senderid - 1).giveMessageToSend(msg, (nodes.get(receiverid - 1).getSocketPort()));
                    break;
                case 1:
                    nodes = addNode(nodes, numNodes, ports);
                    numNodes++;
                    ports++;
                    System.out.println(numNodes);
                    break;
                case 2:
                    System.out.println("Please enter the node's id:");
                    int deleteid = Utils.readNonNegativeInt();
                    deleteNode(deleteid, nodes, numNodes);
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
        ArrayList<RingNode> nodes = new ArrayList<RingNode>();
        RingNode temp;
        for (int j = 0; j < numNodes; j++) {
            temp = new RingNode(j + 1, ports);
            nodes.add(temp);
            ports++;
        }

        initRingConn(nodes, numNodes);
        Thread r;

        for (int i = 0; i < numNodes; i++) {
            r = new Thread(nodes.get(i));
            nodesThreads.add(r);
        }

        for (int k = 0; k < numNodes; k++) {
            nodesThreads.get(k).start();
        }
    }

    public ArrayList<RingNode> addNode(ArrayList<RingNode> nodes, int numNodes, int port) {
        RingNode lastNode, next, node;
        Thread newNode;

        lastNode = nodes.get(numNodes - 1);
        next = new RingNode(lastNode.id + 1, port);
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

    public void deleteNode(int nodeid, ArrayList<RingNode> nodes, int numNodes) {
        nodes.get(nodeid - 2).nextNodePort = nodes.get(nodeid - 1).nextNodePort;
        nodes.get(nodeid - 1).switchReceiving();
        nodes.remove(nodeid);
    }

    public void initRingConn(ArrayList<RingNode> nodes, int numNodes) {
        RingNode node = null;
        RingNode nextNode = null;

        nodes.get(0).setMonitor();
        node = nodes.get(numNodes - 1);
        nextNode = nodes.get(0);
        node.nextNodePort = nextNode.getSocketPort();
        node = nextNode;
        for (int i = 1; i < numNodes; i++) {
            nextNode = nodes.get(i);
            node.nextNodePort = nextNode.getSocketPort();
            node = nextNode;
        }
    }


}
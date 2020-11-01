# RingNode_DistributedArchitecture
Ring Node for distributed architecture implementation

The objective of this project, is to mantain a value updated between the different instances of the program, using a Ring Node algorithm.

To detect the other nodes, a ARP call is being used to detect the open and closed ports, and autoassing the correct port.
Later on, there is the possibility to send a Token for the algorithm to start once you started all the nodes needed.

There are two types of nodes, write and read. The write ones, will read and update the value, and the read ones only show the actual value that is being received.

In the PDF named Theory.pdf more information about the project can be found.

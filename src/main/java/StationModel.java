import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StationModel {
    private static MyFileContents fileContents;
    private static final int DATAGRAM_BYTE_SIZE = 128;
    private DatagramChannel datagramChannel;
    private Selector selector;

    private String myName;
    private String myFile;
    private int myPort;
    private HashMap<String, StationNeighbour> myNeighbours;

    public StationModel(DatagramChannel datagramChannel, Selector selector) {
        this.datagramChannel = datagramChannel;
        this.selector = selector;
        fileContents.updateFileContents();
    }

    public StationModel() {
        this(null, null);
    }


    public void setMyName(String myName) { this.myName = myName; }

    public void setMyFile(String myFile) { this.myFile = myFile; }

    public void setMyPort(int myPort) { this.myPort = myPort; }

    public static void setFileContents(MyFileContents fileContents) { StationModel.fileContents = fileContents; }


    public int getMyUdpPort() {
        return fileContents.getMyUdpPort();
    }

    public String getMyFileName() {
        return fileContents.getMyFileName();
    }

    public String getMyName() { return fileContents.getMyName(); }

    private void getMissingNeighbourPorts() {
        List<Integer> portsWithoutNames = fileContents.getPortsWithoutNames();

        try {
            for (Integer portWithoutName : portsWithoutNames) {
                String requestString = UdpPacketConstructor.getPortName();
                ByteBuffer requestBytes = ByteBuffer.wrap(requestString.getBytes(StandardCharsets.UTF_8));
                InetSocketAddress destination = new InetSocketAddress("127.0.0.1", portWithoutName);
                datagramChannel.send(requestBytes, destination);


            }
        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }//DONT RESPOND IF NOT finished

    }

    public HashMap<String, StationNeighbour> getNeighbours() {
        getMissingNeighbourPorts();
        /*
        Need to call other ports to get their neighbours
        Need to Send UDP request to other Servers
         */


        return fileContents.getNeighbours();
    }


    public ArrayList<Connection> getConnections(String destination) {
        getMissingNeighbourPorts();
        HashMap<String, StationNeighbour> neighbours = fileContents.getNeighbours();

        ArrayList<Connection> connections = new ArrayList<>();
        if (neighbours.containsKey(destination)) { //'Direct Route'

            StationNeighbour neighbour = neighbours.get(destination);
            Connection connection = neighbour.getConnections().get(0); ////!!!!!!!!!!!!!!!!!!WIll have to change

            connections.add(connection);

        } else {
            //This is the big boys work
//            String myName = model.getMyName();
//            int myPort = model.getMyTcpPort();
//            model.getConnectionsTo(value);
//            //
        }
        return connections;

    }

    public void setNamePort(String name, int port) {
        fileContents.updateNeighbourNamePort(name, port);
    }

}

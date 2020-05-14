import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StationModel {
    private static PersistentServerData fileContents;
    private static final int DATAGRAM_BYTE_SIZE = 128;

    private String myName;
    private String myFile;
    private int myPort;

    private HashMap<String, StationNeighbour> myNeighbours;

    private SelectionKey datagramChannelKey;
    private Selector selector;
    private Station station;
    private boolean awaitingResponses;
    private ArrayList<Integer> awaitingPacketNumbers;
    private ArrayList<Connection> connections;


    public StationModel(SelectionKey datagramChannelKey, Selector selector, Station station) {
        this.datagramChannelKey = datagramChannelKey;
        this.selector = selector;
        fileContents.updateFileContents();
        this.station = station;
        this.awaitingPacketNumbers = new ArrayList<>();
        this.awaitingResponses = false;
        this.connections = new ArrayList<>();
    }
    public StationModel() {
        this(null, null, null);
    }

    public static void setFileContents(PersistentServerData fileContents) {
        StationModel.fileContents = fileContents;
    }

    public boolean isAwaitingResponses() {
        return awaitingResponses;
    }




    public void setMyName(String myName) { this.myName = myName; }

    public void setMyFile(String myFile) { this.myFile = myFile; }

    public void setMyPort(int myPort) { this.myPort = myPort; }

    public ArrayList<Integer> getAwaitingPacketNumbers() {
        return awaitingPacketNumbers;
    }


    public int getMyUdpPort() {
        return fileContents.getMyUdpPort();
    }

    public String getMyFileName() {
        return fileContents.getMyFileName();
    }

    public String getMyName() { return fileContents.getMyName(); }

    private void receiveMissingNeighbourPort() {

    }

    private void receiveConnections() {

    }

    //Different headers?


    public void receiveResponse(String[] header, String body, String methodAwaitingResponse) {
        String[] bodyLines = body.split("\n");

        if (methodAwaitingResponse.equals("getHttp()")) { //getting neighbour ports
            String[] values = bodyLines[1].split(",");
            String name = values[0];
            int port = Integer.parseInt(values[1]);
            setNamePort(name, port);
        } else if (methodAwaitingResponse.equals("getHttp(key,value)")) {
            //You wont receive yourself
            //RECURSIVE PATTERN HARD
            //LOGIC TO ADD - DID YOU HTTP REQUEST THIS OR DO YOU NEED TO SEND IT ON?
            //ASSUME YOU HTTP REQUEST THIS, i.e can only ask neighbour - ONE HOP
            //SAME THING FROM THE MODELS POV, MODEL FINDING FASTEST ROUTE FROM IT TO DESTINATION
            ArrayList<Connection> possibleConnections = new ArrayList<>();
            for (int i = 0; i < bodyLines.length; i++) {
                Connection connection = new Connection();
                connection.populateFromString(bodyLines[i]);
                possibleConnections.add(connection);
            }
            if (!possibleConnections.get(0).equals(Connection.NO_CONNECTION)) {

            }

            //Variables in each line: depPort,depName,depStopName,vehicleName,depTime,arrPort,arrName,arrTime

        }
        awaitingPacketNumbers.removeIf(packetNo -> packetNo.equals(Integer.parseInt(header[3])));
        this.awaitingResponses = !awaitingPacketNumbers.isEmpty();
    }

    //It needs to wait for a response and read the response

    public HashMap<String, StationNeighbour> getNeighbours() {
        List<Integer> portsWithoutNames = fileContents.getPortsWithoutNames();

        for (Integer portWithoutName : portsWithoutNames) {
            try {
                int packetNo = station.getPacketCount();
                awaitingPacketNumbers.add(packetNo);
                station.incrementPacketCount();
                String requestString = UdpPacketConstructor.getPortName(packetNo);
                ByteBuffer requestBytes = ByteBuffer.wrap(requestString.getBytes(StandardCharsets.UTF_8));
                InetSocketAddress destination = new InetSocketAddress("127.0.0.1", portWithoutName);
                DatagramChannel channel = (DatagramChannel) datagramChannelKey.channel();
                channel.send(requestBytes, destination); //Not asking the Selector key

                this.awaitingResponses = true;
            } catch (Exception e) {
                System.out.println("Err: " + e);
                System.exit(1);
            }
        }
        return fileContents.getNeighbours();
    }


    public ArrayList<Connection> getConnections(String destinationName) {
        if (destinationName.equals(fileContents.getMyName())) return new ArrayList<>();

        HashMap<String, StationNeighbour> neighbours = getNeighbours(); //Should only start this if this is alredy done

        ArrayList<Connection> connections = new ArrayList<>();
        if (neighbours.containsKey(destinationName)) { //'Direct Route'

            StationNeighbour neighbour = neighbours.get(destinationName);
            Connection connection = neighbour.getConnections().get(0); ////!!!!!!!!!!!!!!!!!!WIll have to change to soonest time arrival

            this.connections.add(connection);
        }

        try {
            for (StationNeighbour neighbour : neighbours.values()) {
                int packetNo = station.getPacketCount();
                awaitingPacketNumbers.add(packetNo);
                station.incrementPacketCount();

                int neighbourPort = neighbour.getPort();
                String requestString = UdpPacketConstructor.getConnections(packetNo, destinationName);
                ByteBuffer requestBytes = ByteBuffer.wrap(requestString.getBytes(StandardCharsets.UTF_8));
                InetSocketAddress destination = new InetSocketAddress("127.0.0.1", neighbourPort);
                DatagramChannel channel = (DatagramChannel) datagramChannelKey.channel();
                channel.send(requestBytes, destination); //Not asking the Selector key

                this.awaitingResponses = true;

            }
        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }


        return connections;

    }

    public void setNamePort(String name, int port) {
        fileContents.updateNeighbourNamePort(name, port);
    }

}

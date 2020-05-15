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
    private static PersistentServerData persistentServerData; //should be able to update
    private SelectionKey datagramChannelKey; //should be able to send data
    private Selector selector;
    private static final int DATAGRAM_BYTE_SIZE = 128;

    private HashMap<String, StationNeighbour> myNeighbours;


    private Station station;
    private boolean awaitingResponses;
    private ArrayList<Integer> awaitingPacketNumbers;


    private ArrayList<Connection> connections; //it really should just be this


    public StationModel(SelectionKey datagramChannelKey, Selector selector, Station station) {
        this.datagramChannelKey = datagramChannelKey;
        this.selector = selector;

        persistentServerData.updateFileContents();
        this.station = station;
        this.awaitingPacketNumbers = new ArrayList<>();
        this.awaitingResponses = false;
        this.connections = new ArrayList<>();
    }
//    public StationModel() {
//        this(null, null, null);
//    }

    public static void setFileContents(PersistentServerData persistentServerData) {
        StationModel.persistentServerData = persistentServerData;
    }

    public boolean isAwaitingResponses() {
        return awaitingResponses;
    }


//    public void setMyName(String myName) { this.myName = myName; }
//
//    public void setMyFile(String myFile) { this.myFile = myFile; }
//
//    public void setMyPort(int myPort) { this.myPort = myPort; }

    public ArrayList<Integer> getAwaitingPacketNumbers() {
        return awaitingPacketNumbers;
    }


    public int getMyUdpPort() {
        return persistentServerData.getMyUdpPort();
    }

    public String getMyFileName() {
        return persistentServerData.getMyFileName();
    }

    public String getMyName() {
        return persistentServerData.getMyName();
    }

    private void receiveMissingNeighbourPort() {

    }

    private void receiveConnections() {

    }

    public boolean isNeighbour(String destination) {
        return persistentServerData.getNeighbour(destination) != null;
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
        List<Integer> portsWithoutNames = persistentServerData.getPortsWithoutNames();

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
        return persistentServerData.getNeighbours();
    }

    private Connection getSoonestDepartureConnection(ArrayList<Connection> connections)

}

    public ArrayList<Connection> getConnections(String destinationName) {
        if (destinationName.equals(persistentServerData.getMyName())) return new ArrayList<>();

        HashMap<String, StationNeighbour> neighbours = getNeighbours(); //Should only start this if this is alredy done

        ArrayList<Connection> connections = new ArrayList<>();
        if (neighbours.containsKey(destinationName)) { //'Direct Route'

            StationNeighbour neighbour = neighbours.get(destinationName);
            Connection connection = neighbour.getSoonestConnection(); ////!!!!!!!!!!!!!!!!!!WIll have to change to soonest time arrival

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
        persistentServerData.updateNeighbourNamePort(name, port);
    }

}

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StationModel {
    private static PersistentServerData persistentServerData; //should be able to update
    private SelectionKey datagramChannelKey; //should be able to send data

    private HashMap<String, StationNeighbour> myNeighbours;


    private Station station;
    private boolean awaitingResponses;
    private ArrayList<Integer> awaitingPacketNumbers;
    private boolean isTcpModel;
    private InetSocketAddress udpSenderAddress;
    private String[] connectionsSoFar;


    private ArrayList<Connection> connections; //it really should just be this
    private String destinationName;


    public StationModel(SelectionKey datagramChannelKey, Station station, boolean isTcpModel, InetSocketAddress udpSenderAddress, String[] connectionsSoFar) {
        this.datagramChannelKey = datagramChannelKey;

        persistentServerData.updateFileContents();
        this.station = station;
        this.awaitingPacketNumbers = new ArrayList<>();
        this.awaitingResponses = false;
        this.connections = new ArrayList<>();
        this.destinationName = "";
        this.isTcpModel = isTcpModel;
        this.udpSenderAddress = udpSenderAddress;
        this.connectionsSoFar = connectionsSoFar;

    }

    public StationModel(SelectionKey datagramChannelKey, Station station, boolean isTcpModel) {
        this(datagramChannelKey, station, isTcpModel, null, new String[0]);
    }

    public static void setFileContents(PersistentServerData persistentServerData) {
        StationModel.persistentServerData = persistentServerData;
    }

    public boolean isAwaitingResponses() {
        return awaitingResponses;
    }

    public ArrayList<Integer> getAwaitingPacketNumbers() {
        return awaitingPacketNumbers;
    }


    public int getMyUdpPort() {
        return persistentServerData.getMyUdpPort();
    }

    public String getMyFileName() {
        return persistentServerData.getMyFileName();
    }

    public String getDestinationName() {
        return destinationName;
    }

    public boolean isNeighbour(String destination) {
        return persistentServerData.getNeighbour(destination) != null;
    }

    //Different headers?

    public String getMyName() {
        return persistentServerData.getMyName();
    }

    public HashMap<String, StationNeighbour> getNeighbours() {
        return persistentServerData.getNeighbours();
    }

    //It needs to wait for a response and read the response
    //Receiving a UDP Response
    //"RESPONSE name ALEX/1.0 FOUND" + packetNo
    //"RESPONSE connections ALEX/1.0 " + FOUND packetNo ;
    public void receiveResponse(String[] header, String[] datagramData) { // lol you have the body right here

        awaitingPacketNumbers.removeIf(packetNo -> packetNo.equals(Integer.parseInt(header[4])));
        this.awaitingResponses = !awaitingPacketNumbers.isEmpty();

        if (header[1].equals("name")) {
            String[] values = datagramData[0].split(",");
            String name = values[0];
            int port = Integer.parseInt(values[1]);
            persistentServerData.updateNeighbourNamePort(name, port);

            if (!awaitingResponses) { //have all neighbour ports
                HashMap<String, StationNeighbour> neighboursToRequestConnection = this.getNeighbours();
                if (!isTcpModel) {//udp controller
                    for (String connection : connectionsSoFar) { //header and body are of current request not previous request that initially triggered
                        String[] connectionValues = connection.split(",");
                        String departureName = connectionValues[0];
                        neighboursToRequestConnection.remove(departureName);
                    }
                }
                sendConnectionsUdpRequests(this.destinationName, new ArrayList<>(neighboursToRequestConnection.values()));
            }
        } else { //get a response to connections request
            if (header[3].equals("FOUND")) {
                if (connections.isEmpty()) {
                    for (int i = 0; i < datagramData.length; i++) {
                        connections.add(new Connection(datagramData[i]));
                    }
                } else {
                    int lastConnectionIndex = connections.size() - 1;
                    Connection lastConnectionSoFar = connections.get(lastConnectionIndex);
                    Connection possibleLastConnection = new Connection(datagramData[datagramData.length - 1]);
                    if (lastConnectionSoFar.getArrivalTime().isAfterOrEqualTo(possibleLastConnection.getArrivalTime())) {
                        connections.clear();
                        for (int i = 0; i < datagramData.length; i++) {
                            connections.add(new Connection(datagramData[i]));
                        }
                    }

                }
            }
            this.awaitingResponses = !awaitingPacketNumbers.isEmpty();

        }
    }

    public ArrayList<Connection> getConnections() {
        return this.connections;
    }

    //This is a problem, getConnectionRequests, vs just getConnection that have already been received
    public void setConnections(String destinationName) {
        this.destinationName = destinationName;

        HashMap<String, StationNeighbour> neighbours = getNeighbours(); //Should only start this if this is alredy done

        if (neighbours.containsKey(destinationName)) { //'Direct Route'
            StationNeighbour neighbour = neighbours.get(destinationName);
            Connection connection = neighbour.getSoonestConnection(); ////!!!!!!!!!!!!!!!!!!WIll have to change to soonest time arrival
            if (connection != null)
                this.connections.add(connection);
        } else {
            //Ask for port, then send
            List<Integer> portsWithoutNames = persistentServerData.getPortsWithoutNames();
            if (!(portsWithoutNames.size() == 0)) {
                sendPortNameUdpRequests(portsWithoutNames);
            } else {
                sendConnectionsUdpRequests(destinationName, new ArrayList<>(neighbours.values()));
            }
        }
    }

    private void sendConnectionsUdpRequests(String destinationName, ArrayList<StationNeighbour> neighbours) {
        try {
            for (StationNeighbour neighbour : neighbours) {
                int packetNo = station.getPacketCount();
                awaitingPacketNumbers.add(packetNo);
                station.incrementPacketCount();

                int neighbourPort = neighbour.getUdpPort();
                Connection soonestConnectionToNeighbour = neighbour.getSoonestConnection();
                String requestString = UdpPacketConstructor.getConnections(packetNo, destinationName, soonestConnectionToNeighbour, connectionsSoFar);
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
    }

    private void sendPortNameUdpRequests(List<Integer> portsWithoutNames) {
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
    }
}

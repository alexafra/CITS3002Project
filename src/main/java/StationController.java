import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

//What to do if url hits base path "/"
//Contains java methods that are called if
//server received different http method requests
public class StationController {
    private StationModel model;
    private SelectionKey key; //could be either http or udp key
    private Station station; //Does a controller own this key?
    private boolean isTcpController;
    private InetSocketAddress udpSenderAddress;
    private String[] connectionsSoFar;
    private int packetNumberResponse;


    public StationController(StationModel model, SelectionKey key, Station station, boolean isTcpController, InetSocketAddress udpSenderAddress, String[] connectionsSoFar) {
        this.model = model;
        this.key = key;
        this.station = station;
        this.isTcpController = isTcpController;
        this.udpSenderAddress = udpSenderAddress;
        this.connectionsSoFar = connectionsSoFar;
        this.packetNumberResponse = -1;
    }

    public StationController(StationModel model, SelectionKey key, Station station, boolean isTcpController) {
        this(model, key, station, isTcpController, null, new String[0]);
    }

    private void executeWriteHttp(String httpResponse) {
        ByteBuffer inputBuf = (ByteBuffer) key.attachment();
        byte[] outBytes = httpResponse.getBytes(StandardCharsets.UTF_8);
        inputBuf.clear();
        inputBuf.put(outBytes);
        key.interestOps(SelectionKey.OP_WRITE);

    }

    /**
     * get method is called on base with a single key-value pairs
     * should return the connections required to get to destination station
     */
    public void getHttp(String destination) {
        String myName = model.getMyName();
        if (destination.equals(myName)) {
            String response = StationView.displayArrivalIsDeparture(myName);
            executeWriteHttp(response);
        }
        model.setConnections(destination);
        if (!model.isAwaitingResponses()) {
            ArrayList<Connection> connections = model.getConnections(); //earliest destination
            String response;
            if (connections.size() == 0) { //no connections after your time
                response = StationView.displayNoConnectionAvailable(myName, destination);
            } else {
                response = StationView.displayConnections(myName, destination, connections);
            }
            executeWriteHttp(response);
        } else {
            ArrayList<Integer> awaitingPacketNumbers = model.getAwaitingPacketNumbers();
            for (Integer packetNo : awaitingPacketNumbers) {
                station.addControllerAwaitingResponse(packetNo, this);
            }
        }
    }

    public void getUdp(String destination, int packetNo) {
        this.packetNumberResponse = packetNo;
        model.setConnections(destination);
        if (!model.isAwaitingResponses()) {
            ArrayList<Connection> connectionsToDestination = model.getConnections(); //earliest destination
            String response = "";
            //int packetNo, String destination, ArrayList<Connection> connectionsToDestination, String[] connectionsToHere
            response = UdpPacketConstructor.sendConnections(packetNumberResponse, destination, connectionsToDestination, connectionsSoFar);

            executeWriteUdp(response, udpSenderAddress);
        } else {
            ArrayList<Integer> awaitingPacketNumbers = model.getAwaitingPacketNumbers();
            for (Integer packetNumber : awaitingPacketNumbers) {
                station.addControllerAwaitingResponse(packetNumber, this);
            }
        }
    }

    public void receiveResponse(String[] header, String[] datagramData) {
        int packetNo = Integer.parseInt(header[4]);
        station.removeControllerAwaitingResponse(packetNo);
        model.receiveResponse(header, datagramData);
        ArrayList<Integer> awaitingPacketNumbers = model.getAwaitingPacketNumbers();
        for (Integer packetNumber : awaitingPacketNumbers) {
            station.addControllerAwaitingResponse(packetNumber, this);
        }

//        "RESPONSE name ALEX/1.0 " + packetNo, only gets called in sequence with bellow
//          Only happen
//        "RESPONSE connectionsTo=destination ALEX/1.0 "


        if (!model.isAwaitingResponses()) {
            //THIS WILL BE "RESPONSE connectionsTo=destination ALEX/1.0 "
            ArrayList<Connection> connections = model.getConnections();
            String response;
            String myName = model.getMyName();
            String myDestination = model.getDestinationName();
            if (isTcpController) {
                if (connections.isEmpty()) {
                    response = StationView.displayNoConnectionAvailable(myName, myDestination);
                } else {
                    response = StationView.displayConnections(myName, myDestination, connections);
                }
                executeWriteHttp(response);
            } else {
                //Reply to who is your initial requester WRONG PACKET NUMBER
                response = UdpPacketConstructor.sendConnections(packetNumberResponse, myDestination, connections, connectionsSoFar);
                executeWriteUdp(response, udpSenderAddress);
            }
        }
    }

    private void executeWriteUdp(String udpResponse, InetSocketAddress address) {
        byte[] outBytes = udpResponse.getBytes(StandardCharsets.UTF_8);
        DatagramPacket outputDatagramPacket = (DatagramPacket) key.attachment();
        //Set datagramPacket data response
        outputDatagramPacket.setData(outBytes);
        outputDatagramPacket.setSocketAddress(address);
        //Sender address already set

        key.interestOps(SelectionKey.OP_WRITE);

    }
    //    /**
//     * getName method is called on base for the UDP protocol
//     * should return the Station name, model should never not be ready because its in PersistentServerData from start
//     */
    public void getNameUdp(int packetNo) {
        String myName = model.getMyName();
        int myUdpPort = model.getMyUdpPort();
        InetSocketAddress address = udpSenderAddress;
        String udpResponse = UdpPacketConstructor.sendPortName(packetNo, myName, myUdpPort);
        executeWriteUdp(udpResponse, address);
    }
}


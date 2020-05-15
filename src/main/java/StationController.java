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
    private StationView view;
    private SelectionKey key; //could be either http or udp key
    private Station station; //Does a controller own this key?
    private boolean isTcpController;
    private InetSocketAddress udpSenderAddress;
    private String[] connectionsSoFar;


    public StationController(StationModel model, StationView view, SelectionKey key, Station station, boolean isTcpController, InetSocketAddress udpSenderAddress, String[] connectionsSoFar) {
        this.model = model;
        this.view = view;
        this.key = key;
        this.station = station;
        this.isTcpController = isTcpController;
        this.udpSenderAddress = udpSenderAddress;
        this.connectionsSoFar = connectionsSoFar;
    }

    public StationController(StationModel model, StationView view, SelectionKey key, Station station, boolean isTcpController) {
        this(model, view, key, station, isTcpController, null, new String[0]);
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
            String response = view.displayArrivalIsDeparture(myName);
            executeWriteHttp(response);
        }
        model.setConnections(destination);
        if (!model.isAwaitingResponses()) {
            ArrayList<Connection> connections = model.getConnections(); //earliest destination
            String response;
            if (connections.size() == 0) { //no connections after your time
                response = view.displayNoConnectionAvailable(myName, destination);
            } else {
                response = view.displayConnections(myName, destination, connections);
            }
            executeWriteHttp(response);
        } else {
            ArrayList<Integer> awaitingPacketNumbers = model.getAwaitingPacketNumbers();
            for (Integer packetNo : awaitingPacketNumbers) {
                station.addControllerAwaitingResponse(packetNo, this);
            }
        }
    }

    /**
     * get method is called on base with no key-value pairs
     * should return all timetable information associated with station
     * If you get UDP, parse existing info
     * Send packets along
     * <p>
     * GET /?to=dest ALEX/1.0 packetNo - 2^12 = 4096
     */
    public void getUdp(String destination, int packetNo) {
        model.setConnections(destination);
        if (!model.isAwaitingResponses()) {
            ArrayList<Connection> connectionsToDestination = model.getConnections(); //earliest destination
            String response = "";
            //int packetNo, String destination, ArrayList<Connection> connectionsToDestination, String[] connectionsToHere
            response = UdpPacketConstructor.sendConnections(packetNo, destination, connectionsToDestination, connectionsSoFar);

            executeWriteUdp(response, udpSenderAddress);
        } else {
            ArrayList<Integer> awaitingPacketNumbers = model.getAwaitingPacketNumbers();
            for (Integer packetNumber : awaitingPacketNumbers) {
                station.addControllerAwaitingResponse(packetNumber, this);
            }
        }
    }


    /**
     * Always receiving a udp response
     * If its a name udp, update models name - same for udp and http controller
     * You know its a name by is
     * You will always update model to quickest connections to destination
     * If you have more incoming
     * <p>
     * <p>
     * If you receive a response  send it to model.
     * If your model isnt waiting on any further responses then
     * If you are a tcp controller, send http response to whoever sent initial request to you
     * If you are a udp controller, send ud[ response to whoever sent initial request to you
     *
     * @param header
     * @param datagramData
     */
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
                response = view.displayConnections(myName, myDestination, connections);
                executeWriteHttp(response);
            } else {
                //Reply to who is your initial requester
                response = UdpPacketConstructor.sendConnections(packetNo, myDestination, connections, connectionsSoFar);
                executeWriteUdp(response, udpSenderAddress);
            }
            //Not quite right
            //Do something
        } // else Do nothing
        //}
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


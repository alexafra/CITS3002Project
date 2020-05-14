import java.net.DatagramPacket;
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
    private String methodAwaitingResponse; //only a single method can be awaiting a response
    private Station station;

    public StationController(StationModel model, StationView view, SelectionKey key, Station station) {
        this.model = model;
        this.view = view;
        this.key = key;
        this.station = station;
        this.methodAwaitingResponse = "";
    }

    private void executeWriteHttp(String httpResponse) {
        ByteBuffer inputBuf = (ByteBuffer) key.attachment();
        byte[] outBytes = httpResponse.getBytes(StandardCharsets.UTF_8);
        inputBuf.clear();
        inputBuf.put(outBytes);
        key.interestOps(SelectionKey.OP_WRITE);

    }

    private void executeGetHttp() {
        String myName = model.getMyName(); //model should update
        int myPort = model.getMyUdpPort();
        String myFileName = model.getMyFileName();
        ArrayList<StationNeighbour> neighbours = new ArrayList<StationNeighbour>(model.getNeighbours().values());

        String httpResponse = view.displayWholeStation(myName, myPort, myFileName, neighbours);
        executeWriteHttp(httpResponse);
    }

    /**
     * get method is called on base with no key-value pairs
     * should return all timetable information associated with station
     */
    public void getHttp() {

//        HashMap<String, StationNeighbour> neighboursMap = model.getNeighbours();
        model.getNeighbours();
        if (!model.isAwaitingResponses()) {
            executeGetHttp();
        } else {
            ArrayList<Integer> awaitingPacketNumbers = model.getAwaitingPacketNumbers();
            for (Integer packetNo : awaitingPacketNumbers) {
                station.addControllerAwaitingResponse(packetNo, this);
            }
            methodAwaitingResponse = "getHttp()";
        }
    }


    /**
     * get method is called on base with a single key-value pairs
     * should return the connections required to get to destination station
     *
     */
    public void getHttp(String destination) {
        if (!destination.equals(model.getMyName())) {
            model.getConnections(destination);
        }
        if (!model.isAwaitingResponses()) {
            executeGetHttp(destination);
        } else {
            ArrayList<Integer> awaitingPacketNumbers = model.getAwaitingPacketNumbers();
            for (Integer packetNo : awaitingPacketNumbers) {
                station.addControllerAwaitingResponse(packetNo, this);
            }
            methodAwaitingResponse = "getHttp(key,value)";
        }
    }

    //Execute if the model has the correct connections
    private void executeGetHttp(String destination) {
        String myName = model.getMyName();
        int myPort = model.getMyUdpPort();

        ArrayList<Connection> connections = model.getConnections(destination);

        String response = "";
        if (destination.equals(model.getMyName())) {
            response = view.displayArrivalIsDeparture(model.getMyName(), myPort);
        } else if (connections.isEmpty()) {
            response = view.displayNoConnectionAvailable(myName, myPort, destination);
        } else {
            int destinationPort = connections.get(connections.size() - 1).getArrivalPort();
            response = view.displayConnections(myName, myPort, destination, destinationPort, connections);
        }
        executeWriteUdp(response);
    }




    /**
     * getName method is called on base for the UDP protocol
     * should return the Station name, model should never not be ready because its in PersistentServerData from start
     *
     */
    public void getNameUdp(int packetNo) {
        if (!model.isAwaitingResponses()) {
            executeGetNameUdp(packetNo);
        }
    }

    private void executeGetNameUdp(int packetNo) {
        String myName = model.getMyName();
        int myUdpPort = model.getMyUdpPort();
        String udpResponse = UdpPacketConstructor.sendPortName(packetNo, myName, myUdpPort);
        executeWriteUdp(udpResponse);
    }

    private void executeWriteUdp(String udpResponse) {
        byte[] outBytes = udpResponse.getBytes(StandardCharsets.UTF_8);
        DatagramPacket outputDatagramPacket = (DatagramPacket) key.attachment();
        //Set datagramPacket data response
        outputDatagramPacket.setData(outBytes);
        //Sender address already set

        key.interestOps(SelectionKey.OP_WRITE);

    }

//    String datagramResponse = router.route(datagramHeader, datagramBody);

    public void receiveResponse(String[] header, String body) {
        int packetNo = Integer.parseInt(header[3]);
        station.removeControllerAwaitingResponse(packetNo);
        model.receiveResponse(header, body, methodAwaitingResponse);

        if (!model.isAwaitingResponses()) {
            if (methodAwaitingResponse.equals("getHttp()")) {
                executeGetHttp();
            } else if (methodAwaitingResponse.equals("getHttp(key,value)")) {
                //Do something
            }
        } // else Do nothing
        //}
    }

    /**
     * get method is called on base with no key-value pairs
     * should return all timetable information associated with station
     */
    public void getUdp(String key, String destination, int packetNo) {
        return;
    }


}

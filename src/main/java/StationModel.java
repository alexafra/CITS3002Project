import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class StationModel {
    private static MyFileContents fileContents;

    private String myName;
    private String myFile;
    private int myPort;
    private HashMap<String, StationNeighbour> myNeighbours;

    public StationModel() {
        fileContents.updateFileContents();
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
        List portsWithoutNames = fileContents.getPortsWithoutNames();

        //Send request for names to other ports

//        Set<String> neighbourNames = neighbours.keySet();
//        for (String neighbourName : neighbourNames) {
//            if (neighbourNamePorts.get(neighbourName) == null) {
//                //In the mode
////                try {
////                    String requestString = "GET /#name ALEX/1.0";
////                    byte[] requestBytes = requestString.getBytes(StandardCharsets.UTF_8);
////                    InetSocketAddress destination = new InetSocketAddress("127.0.0.1", myUdpPort);
////                    DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, destination);
////
////                    DatagramSocket datagramSocket = new DatagramSocket(myUdpPort); //HOW WILL THIS INTERFERE WITH MAIN SERVER SECTION!!!!!!!!!!
////                    datagramSocket.send(requestPacket);
////
////                } catch (Exception e) {
////                    System.out.println("Err: " + e);
////                    System.exit(1);
////                }
//            }
//        }
    }

    public HashMap<String, StationNeighbour> getNeighbours() {
        getMissingNeighbourPorts();
        /*
        Need to call other ports to get their neighbours
        Need to Send UDP request to other Servers
         */



        return fileContents.getNeighbours();
    }


    public void setNamePort(String name, int port) {
        fileContents.updateNeighbourNamePort(name, port);
    }
    public ArrayList<Connection> getConnections(String destination) {
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

}

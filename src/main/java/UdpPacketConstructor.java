import java.util.ArrayList;

public class UdpPacketConstructor {

    public static String sendPortName(int packetNo, String myName, int myPort) {
        String header = "RESPONSE name ALEX/1.0 " + "FOUND " + packetNo + "\n";
        String body = "myName,myPort\n" + myName + "," + myPort;
        String response = header + "\n" + body;
        return response;
    }

    public static String getPortName(int packetNo) {
        return "GET / ALEX/1.0 " + packetNo + "\n";
    }

    //depPort,depName,depStopName,vehicleName,depTime,arrTime,arrPort,arrName
    //packetNo, destinationName, soonestConnectionToNeighbour, connectionsSoFar
    public static String getConnections(int packetNo, String destination, Connection connectionToNeighbour, String[] connectionsToHere) {
        String header = "GET /?to=" + destination + " ALEX/1.0 " + packetNo + "\n";
        String body = "";
        body = body + "depName,depStopName,vehicleName,arrName,depTime,arrTime\n";
        for (int i = 0; i < connectionsToHere.length; i++) {
            body = body + connectionsToHere[i] + "\n";
        }
        body = body + connectionToNeighbour.toString();
        String response = header + "\n" + body;
        return response;
    }

    //depPort,depName,depStopName,vehicleName,depTime,arrTime,arrPort,arrName
    public static String sendConnections(int packetNo, String destination, ArrayList<Connection> connectionsToDestination, String[] connectionsToHere) {

        if (connectionsToDestination.size() == 0) {
            String header = "RESPONSE connectionsTo=" + destination + " ALEX/1.0 " + "NOTFOUND " + packetNo + "\n";
            return header;
        }
        String header = "RESPONSE connectionsTo=" + destination + " ALEX/1.0 " + "FOUND " + packetNo + "\n";
        String body = "";
        body = body + "depName,depStopName,vehicleName,arrName,depTime,arrTime\n";
        for (int i = 0; i < connectionsToHere.length; i++) {
            body = body + connectionsToHere[i] + "\n";
        }
        for (int i = 0; i < connectionsToDestination.size(); i++) {
            body = body + connectionsToDestination.get(i).toString() + "\n";
        }
        String response = header + "\n" + body;
        return response;
    }
}
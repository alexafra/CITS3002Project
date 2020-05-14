import java.util.ArrayList;

public class UdpPacketConstructor {

    public static String sendPortName(int packetNo, String myName, int myPort) {
        String header = "RESPONSE /#name ALEX/1.0 " + packetNo + "\n";
        String body = "myName,myPort\n" + myName + "," + myPort;
        String response = header + "\n" + body;
        return response;
    }

    public static String getPortName(int packetNo) {
        return "GET /#name ALEX/1.0 " + packetNo;
    }

    public static String getConnections(int packetNo, String destination) {
        return "GET /?to=" + destination + " ALEX/1.0 " + packetNo;
    }

    //depPort,depName,depStopName,vehicleName,depTime,arrTime,arrPort,arrName
    public static String sendConnections(int packetNo, ArrayList<Connection> connections) {
        String header = "RESPONSE /#name ALEX/1.0 " + packetNo + "\n";
        String body = "";
        body = body + "depPort,depName,depStopName,vehicleName,depTime,arrTime,arrPort,arrName\n";
        for (int i = 0; i < connections.size(); i++) {
            body = connections.get(i).toString() + "\n";
        }
        String response = header + "\n" + body;
        return response;
    }

//    public String[] parseUdpResponse(String response) {
//
//        String[] headerAndBody = response.split("\n", 2);
//        String header = headerAndBody[0];
//        String body = headerAndBody[1].trim();
//
//        String[] parseHeader = header.split(" ");
//
//
//        String method = parseHeader[0];
//        String urlString = parseHeader[1];
//        String packetNo = parseHeader[2];
//
//        String fragment = "";
//        String urlLocation = "";
//        String[] urlParser = urlString.split("#");
//        if (urlParser.length == 2) {
//            urlLocation = urlParser[0];
//            fragment = urlParser[1];
//        }
//
//        String[] requestLineInfo = {method, urlLocation, fragment, body, packetNo};
//
//
//        return requestLineInfo;
//    }
}

//Accepts Posts
//Accepts gets and sends correct port
//Doesnt automatically send gets if missing postname
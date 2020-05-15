import java.util.ArrayList;

public class StationView {

    private String stationView;

    public StationView(String stationView) { this.stationView = stationView; }

    public StationView() { this(""); }

    public String getStationView() { return stationView; }


    public String displayConnections(String myName, String destinatipn, ArrayList<Connection> connections) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayConnections(myName, destinatipn, connections);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
        return response;
    }

    public String bodyDisplayConnections(String myName, String destination, ArrayList<Connection> connections) {
        String header = "The suggested route from your location: " + myName + " to your destination: " + destination + " is:";
        String connectionsString = "";
        for (int i = 0; i < connections.size(); i++) {
            Connection connection = connections.get(i);
            String vehicleType;
            if (connection.getVehicleName().matches("\\d+")) {
                vehicleType = "bus ";
            } else {
                vehicleType = "train ";
            }
            connectionsString = "Take " + vehicleType + connection.getVehicleName() + " From " + connection.getDepartureStopName();
            connectionsString = connectionsString + " At " + connection.getDepartureName() + " to " + connection.getArrivalName() + " Departure Time: ";
            connectionsString = connectionsString + connection.getDepartureTime() + " Arrival Time: " + connection.getArrivalTime() + "\n";
        }

        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>" + header + "</h1>\n";
        body = body + "<p>" + connectionsString + "</p>\n";
        body = body + "</body>\n";
        body = body + "</html>\n";
        return body;
    }


    public String displayArrivalIsDeparture(String myName) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayArrivalIsDeparture(myName);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
        return response;
    }

    public String bodyDisplayArrivalIsDeparture(String myName) {
        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>Your arrival location: " + myName + "is your departure location" + "</h1>\n";
        body = body + "</body>\n";
        body = body + "</html>\n";
        return body;
    }


    public String displayNoConnectionAvailable(String myName, String neighbour) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayNoConnectionAvailable(myName, neighbour);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
        return response;
    }

    public String bodyDisplayNoConnectionAvailable(String myName, String neighbour) {
        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>There is no known route from: " + myName + " to your destination: " + neighbour + " today</h1>\n";
        body = body + "</body>\n";
        body = body + "</html>\n";
        return body;
    }

    public void badRequestResponse() { stationView = badHeader(); }

    public String badHeader() {
        String header = "";
        header = header + "HTTP/1.1 400 BAD\n";
        header = header + "Content-Type: text/html\n";
        header = header + "Connection: Closed\n";
        return header;
    }

    public String goodHeader() {
        String header = "";
        header = header + "HTTP/1.1 200 OK\n";
        header = header + "Content-Type: text/html\n";
        header = header + "Connection: Closed\n";
        return header;
    }


    //DEAD METHODS ..... hmmm not quite

    public String displayWholeStation(String myName, int myPort, String myFileName, ArrayList<StationNeighbour> neighbours) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayWholeStation(myName, myPort, myFileName, neighbours);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
        return response;
    }

    public String bodyDisplayWholeStation(String myName, int myPort, String myFileName, ArrayList<StationNeighbour> neighbours) {
        String neighbourNames = "";
        String neighbourPorts = "";

        for (StationNeighbour neighbour : neighbours) {
            neighbourNames = neighbourNames + neighbour.getName() + "    ";
            neighbourPorts = neighbourPorts + neighbour.getUdpPort() + "    ";
        }
        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>My Name is: " + myName + "</h1>\n";
        body = body + "<h1>My Port is: " + myFileName + "</h1>\n";
        body = body + "<p>The file I'm reading from is: " + myFileName + "</p>\n";
        body = body + "<p>My neighbours are: " + neighbourNames + "</p>\n";
        body = body + "<p>My neighbour Ports are: " + neighbourPorts + "</p>\n";
        body = body + "</body>\n";
        body = body + "</html>\n";
        return body;
    }
}

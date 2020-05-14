import java.util.ArrayList;

public class StationView {

    private String stationView;

    public StationView(String stationView) { this.stationView = stationView; }

    public StationView() { this(""); }

    public String getStationView() { return stationView; }

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
            neighbourPorts = neighbourPorts + neighbour.getPort() + "    ";
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

    public String displayConnections(String myName, int myPort, String neighbour, int portDestination, ArrayList<Connection> connections) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayConnections(myName, myPort, neighbour, portDestination, connections);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
        return response;
    }

    public String displayArrivalIsDeparture(String myName, int myPort) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayArrivalIsDeparture(myName, myPort);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
        return response;
    }

    public String bodyDisplayArrivalIsDeparture(String myName, int myPort) {
        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>Your arrival location: " + myName + "Port: " + myPort + "is your departure location" + "</h1>\n";
        body = body + "</body>\n";
        body = body + "</html>\n";
        return body;
    }


    public String bodyDisplayConnections(String myName, int myPort, String neighbour, int portDestination, ArrayList<Connection> connections) {
        String header = "The suggested route from your location: " + myName + " port: " + myPort + " to your destination: " + neighbour + " port: " + portDestination + " is:";
        String connectionsString = "";
        for (int i = 0; i < connections.size(); i ++) {
            Connection connection = connections.get(i);
            String vehicleType;
            if (connection.getVehicleName().matches("\\d+")) {
                vehicleType = "bus";
            } else {
                vehicleType = "train";
            }
            connectionsString = "Take the " + vehicleType + connection.getVehicleName() + " From " + connection.getDepartureStopName();
            connectionsString = connectionsString + " At " + myName + " to " + neighbour + " Departure Time: ";
            connectionsString = connectionsString + connection.getDepartureTime() + " Arrival Time: ";
            connectionsString = connectionsString + connection.getArrivalTime() + "\n";
        }

        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>" + header + "</h1>\n";
        body = body + "<p>" + connectionsString +"</p>\n";
        body = body + "</body>\n";
        body = body + "</html>\n";
        return body;
    }

    public String displayNoConnectionAvailable(String myName, int myPort, String neighbour) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayNoConnectionAvailable(myName, myPort, neighbour);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
        return response;
    }

    public String bodyDisplayNoConnectionAvailable(String myName, int myPort, String neighbour) {
        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>There is no known route from: " + myName + " port: " + myPort + " to your destination: " + neighbour + "</h1>\n";
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


}

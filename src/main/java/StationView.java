import java.util.HashMap;
import java.util.Set;

public class StationView {

    private String stationView;

    public StationView(String stationView) { this.stationView = stationView; }

    public StationView() { this(""); }

    public String getStationView() { return stationView; }

    public void displayWholeStation(StationModel model) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayWholeStation(model);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
    }

    public String bodyDisplayWholeStation(StationModel model) {
        String myNeighbours = "";
        HashMap<String, StationNeighbour> neighbours = model.getNeighbours();
        for(String neighbour : neighbours.keySet()) {
            myNeighbours = myNeighbours + neighbour + "\t";
        }
        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>My Name is: " + StationModel.getMyName() + "</h1>\n";
        body = body + "<h1>My Port is: " + StationModel.getMyPort() + "</h1>\n";
        body = body + "<h1>The file I'm reading from is: " + StationModel.getMyFile() + "</h1>\n";
        body = body + "<h1>My neighbours are: " + myNeighbours + "</h1>\n";
        body = body + "</body>\n";
        body = body + "</html>\n";
        return body;
    }

    public void displayStationNeighbour(String myName, String neighbour, Connection connection) {
        String response = "";
        String responseHeader = goodHeader();
        String responseBody = bodyDisplayStationNeighbour(myName, neighbour, connection);
        response = response + responseHeader + "\n" + responseBody;
        stationView = response;
    }

    public String bodyDisplayStationNeighbour(String myName, String neighbour, Connection connection) {
        String connectionString = "Take bus " + connection.getVehicleName() + " From " + connection.getStopName();
        connectionString = connectionString + " At " + myName + " Departure Time: " ;
        connectionString = connectionString + connection.getDepartureTime() + " Arrival Time: " + connection.getArrivalTime();


        String body = "";
        body = body + "<html>\n";
        body = body + "<body>\n";
        body = body + "<h1>The suggested route from your location: " + myName + " to your destination: " + neighbour + " is:</h1>\n";
        body = body + "<h1>" + connectionString +"</h1>\n";
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

import java.time.LocalTime;
import java.util.ArrayList;

public class StationNeighbour {
    private String name;
    private int udpPort;
    private ArrayList<Connection> connections; //probably want to order connections by arrival or departure time or both


    public StationNeighbour(String name, int udpPort, ArrayList<Connection> connections) {
        this.name = name;
        this.udpPort = udpPort;
        this.connections = connections;
    }


    public StationNeighbour(String name) {
        this(name, -1, new ArrayList<>());
    }

    public int getUdpPort() {
        return udpPort;
    }

    public void setUdpPort(int port) {
        this.udpPort = port;
    }

    public void addConnection(Connection connection) { this.connections.add(connection); }

//    public void removeConnection(Connection connection) { this.connections.remove(connection); }

    public Connection getSoonestConnection() {
        return getSoonestConnection(new Time(LocalTime.now()));
    }

    public Connection getSoonestConnection(Time earliestDepartureTime) {
        ArrayList<Connection> connectionsAfterDepTime = new ArrayList<>(); //all connections leave after dep time
        for (Connection connection : connections) {
            if (connection.getDepartureTime().isAfterOrEqualTo(earliestDepartureTime)) {
                connectionsAfterDepTime.add(connection);
            }
        }
        if (connectionsAfterDepTime.size() == 0) return null;
        Connection earliestArrivalConnection = connectionsAfterDepTime.get(0); // assumes one exist
        for (Connection connection : connectionsAfterDepTime) {
            if (earliestArrivalConnection.getArrivalTime().isAfterOrEqualTo(connection.getArrivalTime())) {
                earliestArrivalConnection = connection;
            }
        }
        return earliestArrivalConnection;


    }

    //DEAD METHPDS

//    public void fromString(String serverString) { //String is 1 route and a server name
//        String[] splitServerString = serverString.split(",");
//        this.name = splitServerString[4];
//        Connection newConnection = new Connection();
//        newConnection.populateFromString(serverString);
//        connections.add(newConnection);
//    }
//    public ArrayList<Connection> getConnections() { return connections; }

//    public void setName(String name) { this.name = name; }


//    public void setConnections(ArrayList<Connection> connections) { this.connections = connections; }

    //Dead
//    public StationNeighbour() {
//        this("", -1, new ArrayList<>());
//    }

//    public StationNeighbour(String name, int port) {
//        this(name, port, new ArrayList<>());
//    }

//    public String getName() { return name; }
}

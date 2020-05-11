import java.util.ArrayList;

public class StationNeighbour {
    private String name;
    private int port;
    private ArrayList<Connection> connections; //probably want to order connections by arrival or departure time or both



    public StationNeighbour(String name, int port, ArrayList<Connection> connections) {
        this.name = name;
        this.port = port;
        this.connections = connections;
    }

    public StationNeighbour(String name, int port) {
        this(name, port, new ArrayList<>());
    }

    public StationNeighbour() {
        this("", 0, new ArrayList<>());
    }

    public String getName() { return name; }
    public int getPort() { return port; }
    public ArrayList<Connection> getConnections() { return connections; }

    public void setName(String name) { this.name = name; }
    public void setPort(int port) { this.port = port; }
    public void setConnections(ArrayList<Connection> connections) { this.connections = connections; }

    public void addConnection(Connection connection) { this.connections.add(connection); }
    public void removeConnection(Connection connection) { this.connections.remove(connection); }

    public void fromString(String serverString) { //String is 1 route and a server name
        String[] splitServerString = serverString.split(",");
        this.name = splitServerString[4];
        Connection newConnection = new Connection();
        newConnection.populateFromString(serverString);
        connections.add(newConnection);
    }


}

import java.util.ArrayList;

public class ServerNeighbour {
    private String name;
    private int port;
    private ArrayList<Route> routes;



    public ServerNeighbour(String name, int port, ArrayList<Route> routes) {
        this.name = name;
        this.port = port;
        this.routes = routes;
    }

    public ServerNeighbour() {
        this("", -1, new ArrayList<>());
    }

    public String getName() { return name; }
    public int getPort() { return port; }
    public ArrayList<Route> getRoutes() { return routes; }

    public void setName(String name) { this.name = name; }
    public void setPort(int port) { this.port = port; }
    public void setRoutes(ArrayList<Route> routes) { this.routes = routes; }

    public void addRoute(Route route) { this.routes.add(route); }
    public void removeRoute(Route route) { this.routes.remove(route); }


}

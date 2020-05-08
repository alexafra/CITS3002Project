import java.net.*;
import java.io.*;
import java.util.ArrayList;

public class Server {
    private String serverName;
    private int myPort;
    private ServerSocket socket;
    private Socket newSocket;
    private ArrayList<ServerNeighbour> neighbours;


    public Server(String serverName, int myPort, ArrayList<ServerNeighbour> neighbours) {
        this.serverName = serverName;
        this.myPort = myPort;
        this.neighbours = neighbours;

        try {
            this.socket = new ServerSocket(myPort);
            System.out.println("Now listening on port " + myPort);
            newSocket = socket.accept();

        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }
    }

    public String getServerName() { return serverName; }
    public int getServerPort() { return myPort; }
    public ArrayList<ServerNeighbour> getNeighbours() { return neighbours; }

    public void setServerName(String serverName) { this.serverName = serverName; }
    public void setServerPort(int myPort) { this.myPort = myPort; }
    public void setNeighbours(ArrayList<ServerNeighbour> neighbours) { this.neighbours = neighbours; }


}

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

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


    public int getMyPort() {
        return fileContents.getMyPort();
    }
    public String getMyFileName() {
        return fileContents.getMyFileName();
    }
    public String getMyName() {
        return fileContents.getMyName();
    }
    public HashMap<String, StationNeighbour> getNeighbours() {
        return fileContents.getNeighbours();
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
//            int myPort = model.getMyPort();
//            model.getConnectionsTo(value);
//            //
        }
        return connections;

    }

}

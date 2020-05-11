import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.io.File;
import java.util.Set;
import java.util.ArrayList;
import java.util.List;

public class MyFileContents {
    //Key to file
    private String myFileName;
    private File myFile;
    private long lastRead;

    //Persistent info
    private String stationName;
    private int myTcpPort;
    private int myUdpPort;
    private ArrayList<Integer> neighbourPorts;
    private HashMap<String, Integer> neighbourNamePorts;

    //Info that may change
    private HashMap<String, StationNeighbour> neighbours;


    public MyFileContents(String myFileName, String stationName, int myTcpPort, int myUdpPort, ArrayList<Integer> neighbourPorts) {
        this.stationName = stationName;
        this.myFileName = myFileName;
        this.myTcpPort = myTcpPort;
        this.myUdpPort = myUdpPort;
        this.lastRead = 0;
        this.myFile = new File(myFileName);
        this.neighbours = new HashMap<>();
        this.neighbourNamePorts = new HashMap<>();
        this.neighbourPorts = neighbourPorts;
    }
    public void updateNeighbourNamePort (String name, int port) {
        if (neighbours.get(name) == null) {
            System.out.println("Not my neighbour");
            return;
        } //Do nothing if its not your neighbour
        neighbourNamePorts.put(name, port);
        StationNeighbour neighbour = neighbours.get(name);
        for (Connection connection : neighbour.getConnections()) {
            connection.setArrivalPort(port);
        }
        neighbour.setPort(port);
    }

    public List<String> getNamesWithoutPorts() {
        List<String>neighboursWithoutPorts = new ArrayList<>();
        for (String neighbour : neighbours.keySet()) {
            if (neighbourNamePorts.get(neighbour) == null) {
                neighboursWithoutPorts.add(neighbour);
            }
        }
        return neighboursWithoutPorts;
    }

    public List<Integer> getPortsWithoutNames() {


        List<Integer> portsWithoutNames = new ArrayList<>();
        for (Integer port : neighbourPorts) {
            if (!neighbourNamePorts.values().contains(port)) {
                portsWithoutNames.add(port);
            }
        }
        return portsWithoutNames;
    }



    public void updateFileContents() {
        long lastModified = myFile.lastModified();
        if (lastModified > lastRead) {
            Date date = new Date();
            this.neighbours = new HashMap<>();
            readMyFile();
            lastRead = date.getTime();
        }
    }

    public void addNeighbourNamePort(String name, int port) {
        neighbourNamePorts.put(name, port);
        neighbours.get(name).setPort(port);
    }

    public void readMyFile(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(myFile));
            String nextLine = reader.readLine();
            String[] words;

            //First line
            if (nextLine != null) {
                words = nextLine.split(",");
                if (!words[0].equals(stationName)) //Invalid file format, has no connections, assume no file exists
                    return;
            }
            while ((nextLine = reader.readLine()) != null) {
                words = nextLine.split(",");
                String neighbourName = words[4];

                Connection newConnection = new Connection();
                newConnection.setDepartureLocation(stationName);
                newConnection.setDeparturePort(myUdpPort);
                newConnection.populateFromString(nextLine);

                if (!neighbours.containsKey(neighbourName)) { //A)neighbour does not exist
                    StationNeighbour neighbour = new StationNeighbour(neighbourName, 0);
                    newConnection.setArrivalPort(0);
                    if (neighbourNamePorts.get(neighbourName) != null) {
                        neighbour.setPort(neighbourNamePorts.get(neighbourName));
                        newConnection.setArrivalPort(neighbourNamePorts.get(neighbourName));
                    }
                    neighbour.addConnection(newConnection);

                    neighbours.put(neighbourName, neighbour);
                } else { //Neighbour already exist
                    StationNeighbour neighbour = neighbours.get(neighbourName);
                    newConnection.setArrivalPort(neighbour.getPort());
                    neighbour.addConnection(newConnection);
                }
            }
        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }
    }

    public void setMyName(String myName) { myName = myName; }
    public void setMyFile(String myFile) { myFile = myFile; }
    public void setMyTcpPort(int myTcpPort) { myTcpPort = myTcpPort; }

    public int getMyTcpPort() { return myTcpPort; }

    public int getMyUdpPort() { return myUdpPort; }

    public File getMyFile() { return myFile; }
    public String getMyFileName() { return myFileName; }
    public String getMyName() { return stationName; }
    public HashMap<String, StationNeighbour> getNeighbours() { return neighbours; }
}

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PersistentServerData {
    //Key to file
    private String myFileName;
    private File myFile;
    private long lastRead;

    //Persistent info
    private String stationName;
    private int myTcpPort;
    private int myUdpPort;
    private ArrayList<Integer> neighbourPorts;
    private HashMap<String, Integer> neighbourNamePorts; //Not  changed if there is a change with the file

    //Info that may change
    private HashMap<String, StationNeighbour> neighbours; //fully recreaated if there is a change with file


    public PersistentServerData(String myFileName, String stationName, int myTcpPort, int myUdpPort, ArrayList<Integer> neighbourPorts) {
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

    public StationNeighbour getNeighbour(String neighbourName) {
        return neighbours.get(neighbourName);
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


    public void readMyFile() {
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
                if (nextLine.equals("")) continue;
                words = nextLine.split(",");


                Connection newConnection = new Connection();
                //,depName,depStopName,vehicleName,arrName,depTime,arrTime
                newConnection.setDepartureName(stationName);

                //05:15,Line1,PlatformB,06:10,West_Station
                newConnection.setDepartureTime(new Time(words[0]));
                newConnection.setVehicleName(words[1]);
                newConnection.setDepartureStopName(words[2]);
                newConnection.setArrivalTime(new Time(words[3]));
                newConnection.setArrivalName(words[4]);


                String neighbourName = words[4];

                if (!neighbours.containsKey(neighbourName)) { //A)neighbour does not exist
                    StationNeighbour neighbour = new StationNeighbour(neighbourName);
                    if (neighbourNamePorts.get(neighbourName) != null) {
                        int neighbourUdpPort = neighbourNamePorts.get(neighbourName);
                        neighbour.setUdpPort(neighbourUdpPort);
//                        newConnection.setArrivalPort(neighbourUdpPort);
                    }
                    neighbour.addConnection(newConnection);

                    neighbours.put(neighbourName, neighbour);
                } else { //Neighbour already exist
                    StationNeighbour neighbour = neighbours.get(neighbourName);
//                    newConnection.setArrivalPort(neighbour.getUdpPort());
                    neighbour.addConnection(newConnection);
                }
            }
        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }
    }


    public void updateNeighbourNamePort(String name, int neighbourUdpPort) {
        if (neighbours.get(name) == null) {
            System.out.println("Not my neighbour");
            return;
        } //Do nothing if its not your neighbour
        neighbourNamePorts.put(name, neighbourUdpPort);
        StationNeighbour neighbour = neighbours.get(name);
        neighbour.setUdpPort(neighbourUdpPort);
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

    public int getMyUdpPort() {
        return myUdpPort;
    }


    public String getMyFileName() {
        return myFileName;
    }

    public String getMyName() {
        return stationName;
    }


    public HashMap<String, StationNeighbour> getNeighbours() {
        return neighbours;
    }
}

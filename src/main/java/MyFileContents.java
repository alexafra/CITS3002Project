import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.io.File;

public class MyFileContents {
    //Key to file
    private String myFileName;
    private File myFile;
    private long lastRead;

    //Persistent info
    private String stationName;
    private int myPort;
    private HashMap<String, Integer> neighbourNamePort;

    //Info that may change
    private HashMap<String, StationNeighbour> neighbours;


    public MyFileContents(String myFileName, String stationName, int myPort) {
        this.stationName = stationName;
        this.myFileName = myFileName;
        this.myPort = myPort;
        this.lastRead = 0;
        this.myFile = new File(myFileName);
        this.neighbours = new HashMap<>();
        this.neighbourNamePort = new HashMap<>();
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
        neighbourNamePort.put(name, port);
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
                newConnection.setDeparturePort(myPort);
                newConnection.populateFromString(nextLine);

                if (!neighbours.containsKey(neighbourName)) { //A)neighbour does not exist
                    StationNeighbour neighbour = new StationNeighbour(neighbourName, 0);
                    newConnection.setArrivalPort(0);
                    if (neighbourNamePort.get(neighbourName) != null) {
                        neighbour.setPort(neighbourNamePort.get(neighbourName));
                        newConnection.setArrivalPort(neighbourNamePort.get(neighbourName));
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
    public void setMyPort(int myPort) { myPort = myPort; }

    public int getMyPort() { return myPort; }
    public File getMyFile() { return myFile; }
    public String getMyFileName() { return myFileName; }
    public String getMyName() { return stationName; }
    public HashMap<String, StationNeighbour> getNeighbours() { return neighbours; }
}

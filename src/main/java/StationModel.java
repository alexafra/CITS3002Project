import java.io.BufferedReader;
import java.io.FileReader;
import java.util.HashMap;

public class StationModel {
    private static String myName;
    private static String myFile;
    private static int myPort;
    private HashMap<String, StationNeighbour> neighbours;

    public StationModel() {
        neighbours = new HashMap<>();
    }

    public void readMyFile(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(myFile));
            String nextLine = reader.readLine();
            String[] words;

            //First line
            if (nextLine != null) {
                words = nextLine.split(",");
                if (!words[0].equals(myName)) //Invalid file format, has no connections, assume no file exists
                    return;
            }
            while ((nextLine = reader.readLine()) != null) {
                words = nextLine.split(",");
                String neighbourName = words[4];
                if (!neighbours.containsKey(neighbourName)) { //A) does neighbour not exist
                    StationNeighbour neighbour = new StationNeighbour();
                    neighbour.fromString(nextLine); //populates neighbour and first route !!!!!!!!!!!NOT LINKING TO NEIGHBOUR PORT!!!!!!!
                    neighbours.put(neighbourName, neighbour);
                } else { //Does neighbour already exist
                    StationNeighbour neighbour = neighbours.get(neighbourName);
                    Connection newConnection = new Connection();
                    newConnection.fromString(nextLine); //create route from the line of text
                    neighbour.addRoute(newConnection);
                }
            }
        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }
    }

    public static void setMyName(String myName) { StationModel.myName = myName; }

    public static void setMyFile(String myFile) { StationModel.myFile = myFile; }

    public static void setMyPort(int myPort) { StationModel.myPort = myPort; }

    public static int getMyPort() { return myPort; }
    public static String getMyFile() { return myFile; }
    public static String getMyName() { return myName; }
    public HashMap<String, StationNeighbour> getNeighbours() { return neighbours; }
}

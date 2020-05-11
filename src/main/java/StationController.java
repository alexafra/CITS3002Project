import java.util.HashMap;
import java.util.ArrayList;

//What to do if url hits base path "/"
//Contains java methods that are called if
//server received different http method requests
public class StationController {
    private StationModel model;
    private StationView view;

    public StationController(StationModel model, StationView view) {
        this.model = model;
        this.view = view;
    }
    /**
     * get method is called on base with no key-value pairs
     * should return all timetable information associated with station
     */
    public void get () {
        String myName = model.getMyName(); //model should update
        int myPort = model.getMyPort();
        String myFileName = model.getMyFileName();
        HashMap<String, StationNeighbour> neighboursMap = model.getNeighbours();
        ArrayList<StationNeighbour> neighbours = new ArrayList<> (neighboursMap.values());

        view.displayWholeStation(myName, myPort, myFileName, neighbours);
    }

    /**
     * get method is called on base with a single key-value pairs
     * should return the connections required to get to destination station
     *
     * For the complicated case we need to "ring" each station to get their name
     */
    public void get(String key, String destination) {
        if (!key.equals("to")) {
            view.badRequestResponse();
        }


        String myName = model.getMyName();
        int myPort = model.getMyPort();
        ArrayList<Connection> connections = model.getConnections(destination);
        if (connections.isEmpty()) {
            view.displayNoConnectionAvailable(myName, myPort, destination);
        } else {
            int destinationPort = connections.get(connections.size() - 1).getArrivalPort();
            view.displayConnections(myName, myPort, destination, destinationPort, connections);
        }


    }

}

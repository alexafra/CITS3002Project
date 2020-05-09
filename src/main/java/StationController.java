import java.util.HashMap;

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
        model.readMyFile(); //model should update
        view.displayWholeStation(model);
    }

    /**
     * get method is called on base with a single key-value pairs
     * should return the connections required to get to destination station
     *
     * For the complicated case we need to "ring" each station to get their name
     */
    public void get(String key, String value) {
        if (!key.equals("to")) {
            view.badRequestResponse();
        }
        model.readMyFile(); //model should update
        HashMap<String, StationNeighbour> neighbours = model.getNeighbours();
        if (neighbours.containsKey(value)) {
            String myName = model.getMyName();
            String neighbourName = value;
            StationNeighbour neighbour = neighbours.get(value);
            Connection soonestConnection = neighbour.getConnections().get(0);////!!!!!!!!!!!!!!!!!!WIll have to change

            view.displayStationNeighbour(myName, neighbourName, soonestConnection);
        }
    }

}

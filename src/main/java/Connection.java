public class Connection {
    private String departureLocation;
    private String arrivalLocation;
    private int departurePort;
    private int arrivalPort;
    private Time departureTime;
    private Time arrivalTime;
    private String vehicleName; //Line one bus 12
    private String stopName; //platform b or stopA

    public Connection(String departureLocation, String arrivalLocation, int departurePort, int arrivalPort, Time departureTime, Time arrivalTime, String vehicleName, String stopName) {
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
        this.departurePort = departurePort;
        this.arrivalPort = arrivalPort;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.vehicleName = vehicleName;
        this.stopName = stopName;
    }

    public Connection() {
        this ("", "", 0, 0, new Time(), new Time(), "", "");
    }

    public String getDepartureLocation() { return departureLocation; }
    public String getArrivalLocation() { return arrivalLocation; }
    public int getDeparturePort() { return departurePort; }
    public int getArrivalPort() { return arrivalPort; }

    public Time getDepartureTime() { return departureTime; }
    public Time getArrivalTime() { return arrivalTime; }
    public String getVehicleName() { return vehicleName; }
    public String getStopName() { return stopName; }

    public void setDepartureLocation(String departureLocation) { this.departureLocation = departureLocation; }
    public void setArrivalLocation(String arrivalLocation) { this.arrivalLocation = arrivalLocation; }
    public void setDeparturePort(int departurePort) { this.departurePort = departurePort; }
    public void setArrivalPort(int arrivalPort) { this.arrivalPort = arrivalPort; }
    public void setDepartureTime(Time departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(Time arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    public void setStopName(String stopName) { this.stopName = stopName; }

    /**
     *
     * @return a string representation of the Connection object
     */
    public String toString() {
        String routeString = "";
        routeString = routeString + departureTime.toString() + "," + vehicleName + "," + stopName + "," + arrivalTime.toString();
        return routeString;
    }


    /**
     * sets a route objects internal variables based on a String
     * @param routeString is the string representation of the route
     */
    public void populateFromString(String routeString) {
        String[] splitRouteString = routeString.split(",");
        this.departureTime.fromString(splitRouteString[0]);
        this.vehicleName = splitRouteString[1];
        this.stopName = splitRouteString[2];
        this.arrivalTime.fromString(splitRouteString[3]);
        this.arrivalLocation = splitRouteString[4];
    }


    /**
     * Tests if two route objects are equal
     * @param connectionOther the other route object
     * @return true if two objects are equal, false otherwise
     */
    public boolean equals(Connection connectionOther) {
        if (connectionOther == null || connectionOther.getClass() != getClass())
            return false;

        boolean sameDepartureLocation = this.getDepartureLocation() == connectionOther.getDepartureLocation();
        boolean sameArrivalLocation = this.getArrivalLocation() == connectionOther.getArrivalLocation();
        boolean sameDeparturePort = this.getDeparturePort() == connectionOther.getDeparturePort();
        boolean sameArrivalPort = this.getArrivalPort() == connectionOther.getArrivalPort();

        boolean sameDepartureTime = this.getDepartureTime() == connectionOther.getDepartureTime();
        boolean sameVehicleName = this.getVehicleName() == connectionOther.getVehicleName();
        boolean sameStopName = this.getStopName() == connectionOther.getStopName();
        boolean sameArrivalTime = this.getArrivalTime() == connectionOther.getArrivalTime();

        return sameDepartureLocation && sameArrivalLocation && sameDeparturePort && sameArrivalPort &&sameDepartureTime
                && sameVehicleName && sameStopName && sameArrivalTime;
    }
}

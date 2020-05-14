public class Connection {
    public static Connection NO_CONNECTION = new Connection(0, "", "", "", 0, "", new Time(), new Time());
    private String departureName;
    private int departurePort;
    private int arrivalPort;
    private Time departureTime;
    private Time arrivalTime;
    private String vehicleName; //Line one bus 12
    private String arrivalName;
    private String departureStopName; //platform b or stopA

    //depPort,depName,depStopName,vehicleName,depTime,arrTime,arrPort,arrName
    public Connection(int departurePort, String departureName, String departureStopName, String vehicleName, int arrivalPort, String arrivalName, Time departureTime, Time arrivalTime) {
        this.departurePort = departurePort;
        this.departureName = departureName;
        this.departureStopName = departureStopName;
        this.vehicleName = vehicleName;
        this.arrivalPort = arrivalPort;
        this.arrivalName = arrivalName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;


    }

    public Connection() {
        this(0, "", "", "", 0, "", new Time(), new Time());
    }

    public String getDepartureName() {
        return departureName;
    }

    public void setDepartureName(String departureName) {
        this.departureName = departureName;
    }
    public int getDeparturePort() { return departurePort; }
    public int getArrivalPort() { return arrivalPort; }

    public Time getDepartureTime() { return departureTime; }
    public Time getArrivalTime() { return arrivalTime; }
    public String getVehicleName() { return vehicleName; }

    public String getArrivalName() {
        return arrivalName;
    }

    public void setArrivalName(String arrivalName) {
        this.arrivalName = arrivalName;
    }

    public String getDepartureStopName() {
        return departureStopName;
    }
    public void setDeparturePort(int departurePort) { this.departurePort = departurePort; }
    public void setArrivalPort(int arrivalPort) { this.arrivalPort = arrivalPort; }
    public void setDepartureTime(Time departureTime) { this.departureTime = departureTime; }
    public void setArrivalTime(Time arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }

    public void setDepartureStopName(String departureStopName) {
        this.departureStopName = departureStopName;
    }

    /**
     *
     * @return a string representation of the Connection object
     */
    //depPort,depName,depStopName,vehicleName,arrPort,arrName,depTime,arrTime
    public String toString() {
        String routeString = "";
        routeString = routeString + departurePort + "," + departureName + "," + departureStopName + "," + vehicleName +
                "," + arrivalPort + "," + arrivalName + "," + departureTime + "," + arrivalTime;
        return routeString;
    }


    /**
     * sets a route objects internal variables based on a String
     * @param routeString is the string representation of the route
     */
    public void populateFromString(String connectionString) {
        String[] connectionStringSplit = connectionString.split(",");

        this.departurePort = Integer.parseInt(connectionStringSplit[0]);
        this.departureName = connectionStringSplit[1];
        this.departureStopName = connectionStringSplit[2];
        this.vehicleName = connectionStringSplit[3];
        this.arrivalPort = Integer.parseInt(connectionStringSplit[4]);
        this.arrivalName = connectionStringSplit[5];
        this.departureTime.populateFromString(connectionStringSplit[6]);
        this.arrivalTime.populateFromString(connectionStringSplit[7]);

    }


    /**
     * Tests if two route objects are equal
     * @param connectionOther the other route object
     * @return true if two objects are equal, false otherwise
     */
    public boolean equals(Connection connectionOther) {
        if (connectionOther == null || connectionOther.getClass() != getClass())
            return false;

        boolean sameDepartureLocation = this.getDepartureName() == connectionOther.getDepartureName();
        boolean sameArrivalLocation = this.getArrivalName() == connectionOther.getArrivalName();
        boolean sameDeparturePort = this.getDeparturePort() == connectionOther.getDeparturePort();
        boolean sameArrivalPort = this.getArrivalPort() == connectionOther.getArrivalPort();

        boolean sameDepartureTime = this.getDepartureTime() == connectionOther.getDepartureTime();
        boolean sameVehicleName = this.getVehicleName() == connectionOther.getVehicleName();
        boolean sameStopName = this.getDepartureStopName() == connectionOther.getDepartureStopName();
        boolean sameArrivalTime = this.getArrivalTime() == connectionOther.getArrivalTime();

        return sameDepartureLocation && sameArrivalLocation && sameDeparturePort && sameArrivalPort &&sameDepartureTime
                && sameVehicleName && sameStopName && sameArrivalTime;
    }
}

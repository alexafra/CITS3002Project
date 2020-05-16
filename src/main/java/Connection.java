public class Connection {
    private String departureName;
    private String vehicleName; //Line one bus 12
    private String arrivalName;
    private String departureStopName; //platform b or stopA
    private Time departureTime;
    private Time arrivalTime;

    //depName,depStopName,vehicleName,arrName,depTime,arrTime
    public Connection(String departureName, String departureStopName, String vehicleName, String arrivalName, Time departureTime, Time arrivalTime) {
        this.departureName = departureName;
        this.departureStopName = departureStopName;
        this.vehicleName = vehicleName;
        this.arrivalName = arrivalName;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;


    }

    //depName,depStopName,vehicleName,arrName,depTime,arrTime
    public Connection(String connectionString) {
        String[] connectionStringSplit = connectionString.split(",");

        //"depName,depStopName,vehicleName,arrName,depTime,arrTime
        this.departureName = connectionStringSplit[0];
        this.departureStopName = connectionStringSplit[1];
        this.vehicleName = connectionStringSplit[2];
        this.arrivalName = connectionStringSplit[3];
        this.departureTime = new Time(connectionStringSplit[4]);
        this.arrivalTime = new Time(connectionStringSplit[5]);

    }

    public Connection() {
        this("", "", "", "", new Time(), new Time());
    }

    public String getDepartureName() {
        return departureName;
    }

    public void setDepartureName(String departureName) {
        this.departureName = departureName;
    }
//    public int getDeparturePort() { return departurePort; }
//    public int getArrivalPort() { return arrivalPort; }

    public Time getDepartureTime() {
        return departureTime;
    }

    //    public void setDeparturePort(int departurePort) { this.departurePort = departurePort; }
//    public void setArrivalPort(int arrivalPort) { this.arrivalPort = arrivalPort; }
    public void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    public Time getArrivalTime() {
        return arrivalTime;
    }

    public String getArrivalName() {
        return arrivalName;
    }

    public void setArrivalName(String arrivalName) {
        this.arrivalName = arrivalName;
    }

    public String getDepartureStopName() {
        return departureStopName;
    }

    public void setArrivalTime(Time arrivalTime) {
        this.arrivalTime = arrivalTime;
    }

    public String getVehicleName() {
        return vehicleName;
    }

    public void setVehicleName(String vehicleName) {
        this.vehicleName = vehicleName;
    }

    public void setDepartureStopName(String departureStopName) {
        this.departureStopName = departureStopName;
    }

    /**
     * @return a string representation of the Connection object
     */
    //"depName,depStopName,vehicleName,arrName,depTime,arrTime
    public String toString() {
        String connectionString = "";
        connectionString = connectionString + departureName + "," + departureStopName + "," + vehicleName + ","
                + arrivalName + "," + departureTime + "," + arrivalTime;
        return connectionString;
    }
}


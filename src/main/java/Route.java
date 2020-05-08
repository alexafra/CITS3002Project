public class Route {
    private Time departureTime;
    private Time arrivalTime;
    private String vehicleName; //Line one bus 12
    private String stopName; //platform b or stopA
    private boolean isBus;

    public Route (Time departureTime, Time arrivalTime, String vehicleName, String stopName, boolean isBus) {
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.vehicleName = vehicleName;
        this.stopName = stopName;
        this.isBus = false;
    }

    public Route() {
        this (new Time(), new Time(), "", "", false);
    }

    public Time getDepartureTime() { return departureTime; }
    public Time getArrivalTime() { return arrivalTime; }
    public String getVehicleName() { return vehicleName; }
    public String getStopName() { return stopName; }
    public boolean getIsBus() { return isBus; }

    public void setDeparture(Time departureTime) { this.departureTime = departureTime; }
    public void setArrival(Time arrivalTime) { this.arrivalTime = arrivalTime; }
    public void setVehicleName(String vehicleName) { this.vehicleName = vehicleName; }
    public void setStopName(String stopName) { this.stopName = stopName; }
    public void setIsBus(boolean bus) { isBus = bus; }

    /**
     *
     * @return a string representation of the Route object
     */
    public String toString() {
        String routeString = "";
        routeString = routeString + departureTime.toString() + "," + vehicleName + "," + stopName;
        routeString = routeString + arrivalTime.toString();
        return routeString;
    }


    /**
     * sets a route objects internal variables based on a String
     * @param routeString is the string representation of the route
     */
    public void fromString(String routeString) {
        String[] splitRouteString = routeString.split("[,]");
        this.departureTime.fromString(splitRouteString[0]);
        this.vehicleName = splitRouteString[1];
        this.stopName = splitRouteString[2];
        this.arrivalTime.fromString(splitRouteString[3]);
    }

    /**
     * Tests if two route objects are equal
     * @param routeOther the other route object
     * @return true if two objects are equal, false otherwise
     */
    public boolean equals(Route routeOther) {
        boolean sameDepartureTime = this.getDepartureTime() == routeOther.getDepartureTime();
        boolean sameVehicleName = this.getVehicleName() == routeOther.getVehicleName();
        boolean sameStopName = this.getStopName() == routeOther.getStopName();
        boolean sameArrivalTime = this.getArrivalTime() == routeOther.getArrivalTime();
        boolean sameIsBus = this.getIsBus() == routeOther.getIsBus(); //This shouldnt ever impact

        return sameDepartureTime && sameVehicleName && sameStopName && sameArrivalTime && sameIsBus;
    }
}

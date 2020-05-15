public class Time {
    private int hour;
    private int minute;

    /**
     *
     * @param hour
     * @param minute
     */
    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    public Time(String stringTime) { //hh:mm
        String[] hourMinuteString = stringTime.split("[:]");
        this.hour = Integer.parseInt(hourMinuteString[0]);
        this.minute = Integer.parseInt(hourMinuteString[1]);

    }

    /**
     * Default constructor
     */
    public Time() {
        this(0, 0);
    }


    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }
    public void setMinute(int minute) { this.minute = minute; }

    /**
     *
     * @return String of the time object in the format "hh:mm"
     */
    public String toString() {
        String time = hour + ":";
        if (hour < 10)
            time = "0" + time;
        if (minute < 10)
            time = time + "0" + minute;
        else
            time = time + minute;
        return time;
    }

    /**
     * Sets hour and time based on a String
     * @param time in the format "hh:mm"
     */
    public void populateFromString2(String time) { //not really good method
        String[] hourMinuteString = time.split("[:]");
        this.hour = Integer.parseInt( hourMinuteString[0]);
        this.minute = Integer.parseInt( hourMinuteString[1]);
    }

    public boolean isAfterOrEqualTo(Time other) {

        return (this.hour == other.getHour() && this.minute >= other.getMinute()) || this.hour > other.getHour();

    }

    /**
     * tests if two Time objects are equal
     * @param timeOther is another Time object
     * @return true if timeOther equals this object, false otherwise
     */
    public boolean equals(Time timeOther) {
        if (timeOther == null || timeOther.getClass() != getClass())
            return false;

        boolean sameHour = this.getHour() == timeOther.getHour();
        boolean sameMinute = this.getMinute() == timeOther.getMinute();

        return sameHour && sameMinute;
    }

}

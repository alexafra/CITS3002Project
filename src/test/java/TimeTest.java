import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class TimeTest {

    private Time time1;
    private Time time2;
    private Time time3;

    private String[] timesString = {"07:08", "08:15", "14:20"};

    @BeforeEach
    public void init () {
        time1 = new Time();
        time2 = new Time();
        time3 = new Time();
    }

    @AfterEach
    public void cleanup() {
        time1 = null;
        time2 = null;
        time3 = null;
    }

    @Test
    @DisplayName("it should construct correct object")
    public void constructorTest() {
        time1 = new Time(7, 8);
        time2 = new Time(8, 15);
        time3 = new Time(14, 20);

        assertThat(time1.toString() == "07:08");
        assertThat(time2.toString() == "08:15");
        assertThat(time3.toString() == "14:20");

    }

    @Test
    @DisplayName("it should get time")
    public void fromStringTest() {
//        time1.populateFromString(timesString[0]);
//        time2.populateFromString(timesString[1]);
//        time3.populateFromString(timesString[2]);
//        assertThat(time1.getHour() == 7);
//        assertThat(time2.getHour() == 8);
//        assertThat(time3.getHour() == 14);
//
//        assertThat(time1.getMinute() == 8);
//        assertThat(time2.getMinute() == 15);
//        assertThat(time3.getMinute() == 20);
    }

    @Test
    @DisplayName("it should return correct string")
    public void toStringTest() {
        time1.setHour(7);
        time2.setHour(8);
        time3.setHour(14);

        time1.setMinute(8);
        time2.setMinute(15);
        time2.setMinute(20);

        assertThat(time1.toString() == "07:08");
        assertThat(time2.toString() == "08:15");
        assertThat(time3.toString() == "14:20");
    }

    @Test
    @DisplayName("it should see if two times are the same")
    public void equalsTest() {
        time1 = new Time (7, 10);
        time2 = new Time (7, 30);
        time3 = new Time (8, 20);
        Time time4 = new Time(8, 30);
        Time time5 = new Time(8, 20);

        assertThat(time1.equals(time2) == false );
        assertThat(time2.equals(time4) == false);
        assertThat(time3.equals(time5) == false);
    }
}


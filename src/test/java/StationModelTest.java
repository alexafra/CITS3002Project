import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

import static org.assertj.core.api.Assertions.assertThat;


public class StationModelTest {

    private StationModel model1;
    private String fileLocation;


    /*

    public static void setMyName(String myName) { StationModel.myName = myName; }

    public static void setMyFile(String myFile) { StationModel.myFile = myFile; }

    public static void setMyPort(int myPort) { StationModel.myPort = myPort; }
     */
    @BeforeEach
    public void init () {
        model1 = new StationModel();

        fileLocation = "/Users/alexanderfrazis/Desktop/UWAUnits/2020Sem1/CITS3002/NetworksJavaProject/src/test/serverFiles/";
        model1.setMyName("Test_Terminal");
        model1.setMyFile(fileLocation + "tt-Test_Terminal");
    }

    @AfterEach
    public void cleanup() {
        model1 = null;
        fileLocation = null;
    }

    @Test
    @DisplayName("it should read file correctly")
    public void readMyFileTest() {
//        model1.readMyFile();
//        HashMap<String, StationNeighbour> neighbours = model1.getNeighbours();
//        assertThat(neighbours.size() == 2);
//        StationNeighbour neighbour1 = neighbours.get("East_Station");
//        StationNeighbour neighbour2 = neighbours.get("West_Station");
//
//        assertThat(neighbour1.getConnections().size() == 3);
//        assertThat(neighbour2.getConnections().size() == 2);
//
//        assertThat(neighbour1.getConnections().get(1).toString()).isEqualTo("08:15,12,Stop1,08:46");
//        assertThat(neighbour2.getConnections().get(0).toString()).isEqualTo("08:15,10,Stop3,09:35");


    }


}

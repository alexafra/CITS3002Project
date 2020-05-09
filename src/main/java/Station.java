import java.io.BufferedReader;
import java.io.FileReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.io.PrintWriter;
import java.io.InputStreamReader;

public class Station {
    private static final int maxWordsPerLine = 5;
    private String myName;
    private int myPort;
    private ServerSocket serverSocket;

    public Station(String myName, int myPort) {
        this.myName = myName;
        this.myPort = myPort;
    }

    public Station(String myName) {
        this(myName, 0);
    }

    public void runServer() {
        try {
            serverSocket = new ServerSocket(myPort);
            System.out.println("Now bound to port " + myPort);
            serverSocket.setSoTimeout(1000000);


            System.out.println("About to listen traffic on my port " + myPort);
            try {
                Socket socket = serverSocket.accept();
                System.out.println("Received request at " + myName + " from: " + socket.getPort());

                BufferedReader input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

                Router router = new Router();
                String httpRequestLine;

                String viewString = null;
                if (input.ready() && (httpRequestLine = input.readLine()) != null) {
                    viewString = router.route(httpRequestLine);
                }
                if (viewString != null) {
                    output.write(viewString);
                }
                output.close();
                input.close();
                socket.close();
            } catch (Exception e) {
                System.out.println("Err: " + e);
                System.exit(1);
            }

        } catch (Exception e) {
            System.out.println("Err: " + e);
            System.exit(1);
        }
    }


    public static void main(String[] args) {
        String fileLocation = "/Users/alexanderfrazis/Desktop/UWAUnits/2020Sem1/CITS3002/NetworksJavaProject/src/main/serverFiles/";
        String serverName = args[0];
        int tcpPortNo = Integer.parseInt(args[1]);
        int udpPortNo = Integer.parseInt(args[2]);

        //Until we get a better idea of where these go
        StationModel.setMyName(serverName);
        StationModel.setMyFile(fileLocation + "tt-" + serverName);
        StationModel.setMyPort(tcpPortNo);

        Station myStation = new Station(serverName, tcpPortNo);



        myStation.runServer();
    }

}
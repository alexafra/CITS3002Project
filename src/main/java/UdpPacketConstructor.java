public class UdpPacketConstructor {

    public static String sendPortName(String myName, int myPort) {
        String header = "POST /#name ALEX/1.0\n";
        String body = "myName,myPort\n" + myName + "," + myPort;
        String response = header + "\n" + body;
        return response;
    }

    public static String getPortName(String myName, int myPort) {
        return "GET /#name ALEX/1.0";
    }
}

//Accepts Posts
//Accepts gets and sends correct port
//Doesnt automatically send gets if missing postname
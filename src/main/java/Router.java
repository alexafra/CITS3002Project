/**
 * Take a URL path maps it to correct controller, model, view
 * Then makes the controller call the right method
 * Then returns the view response which is a string
 */
public class Router {
    StationController controller;

    public Router(StationController controller) {
        this.controller = controller;
    }


    /**
     * Should return request line method, location, first key-value pair, protocol, packetNo
     * @param httpRequestLine
     * @return
     * Possible Requests:
     * GET / ALEX/1.0 packetNo - 2^12 = 4096
     * GET /?to=dest ALEX/1.0 packetNo - 2^12 = 4096
     * GET / http/1.1
     */
    public static String[] parseRequestLine(String httpRequestLine) {
        String[] requestLineInfo = {"", "", "", "", "", ""};

        String[] requestParser = httpRequestLine.split(" ");

        if (requestParser.length > 2) {
            String method = requestParser[0];
            String url = requestParser[1];
            String protocol = requestParser[2];
            String packetNo = "";
            if (protocol.equals("ALEX/1.0")) {
                packetNo = requestParser[3];
            }
            String[] urlParser = url.split("\\?");
            String location = urlParser[0];

            String queryString;
            String[] keyValuePair;
            String key = "";
            String value = "";

            if (urlParser.length > 1) {
                queryString = urlParser[1];
                keyValuePair = queryString.split("=");
                key = keyValuePair[0];
                value = keyValuePair[1];
            }

            requestLineInfo[0] = method;
            requestLineInfo[1] = location;
            requestLineInfo[2] = key;
            requestLineInfo[3] = value;
            requestLineInfo[4] = protocol;
            requestLineInfo[5] = packetNo;
        }

        return requestLineInfo;
    }

    public String route(String httpRequestLine, String body) {
        String[] urlSummary = Router.parseRequestLine(httpRequestLine);


        //IS it a request or response

        String response = "";
        String method = urlSummary[0];
        String location = urlSummary[1];
        String key = urlSummary[2];
        String value = urlSummary[3];
        String protocol = urlSummary[4];

        boolean requestHasKeyValuePair = key != null && key.equals("to") && value != null && !value.equals("");

        if (protocol.equals("ALEX/1.0")) {
            int packetNo = Integer.parseInt(urlSummary[5]);
            if (location.equals("/")) { //Station controller
                if (method.equals("GET")) { //Get method - TCP
                    if (requestHasKeyValuePair) { //single key-value
                        controller.getUdp(value, packetNo); //Not yet implemented
                    } else {
                        controller.getNameUdp(packetNo);
                    }

                }
            }
            //Alex routing
        } else {
            if (location.equals("/")) { //Station controller
                if (method.equals("GET")) { //Get method - TCP
                    if (requestHasKeyValuePair) { //single key-value
                        controller.getHttp(value); //providing destination arguments
                    }
                }
            }
        }

        return ""; //Could not map url to a controller method - should do nothing
    }

}

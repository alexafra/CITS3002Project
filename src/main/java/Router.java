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
     * Should return request line method, location, first key-value pair, fragment, protocol, packetNo
     * @param httpRequestLine
     * @return
     */
    public static String[] parseRequestLine(String httpRequestLine) {
        String[] requestLineInfo = {"", "", "", "", "", "", ""};

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
            String key;
            String value;
            String fragment;

            if (urlParser.length > 1) {
                queryString = urlParser[1];
                keyValuePair = queryString.split("=");
                key = keyValuePair[0];
                value = keyValuePair[1];
            } else {
                key = "";
                value = "";
            }

            String[] locationParser = location.split("#");
            if (locationParser.length == 2) {
                location = locationParser[0];
                fragment = locationParser[1];
            } else {
                fragment = "";
            }

            requestLineInfo[0] = method;
            requestLineInfo[1] = location;
            requestLineInfo[2] = key;
            requestLineInfo[3] = value;
            requestLineInfo[4] = fragment;
            requestLineInfo[5] = protocol;
            requestLineInfo[6] = packetNo;
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
        String protocol = urlSummary[5];

        boolean requestHasKeyValuePair = key != null && key.equals("to") && value != null && !value.equals("");

        if (protocol.equals("ALEX/1.0")) {
            int packetNo = Integer.parseInt(urlSummary[6]);
            if (location.equals("/")) { //Station controller
                if (method.equals("GET")) { //Get method - TCP
                    if (requestHasKeyValuePair) { //single key-value
                        controller.getUdp(key, value, packetNo); //Not yet implemented
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
                return response;
            }
        }

        return ""; //Could not map url to a controller method - should do nothing
    }

}

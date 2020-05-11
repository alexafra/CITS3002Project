/**
 * Take a URL path maps it to correct controller, model, view
 * Then makes the controller call the right method
 * Then returns the view response which is a string
 */
public class Router {

    public String route(String httpRequestLine, String body) {
        String[] urlSummary = this.parseRequestLine(httpRequestLine);
        String method = urlSummary[0];
        String location = urlSummary[1];

        //IS it a request or response


        if (location.equals("/")) { //Station controller

            StationModel model = new StationModel();
            StationView view = new StationView();
            StationController controller = new StationController(model, view);
            String key = urlSummary[2];
            String value = urlSummary[3];
            String fragment = urlSummary[4];

            if (method.equals("GET")) { //Get method - TCP
                if (key != "" && key != null && value != "" && value != null) { //single key-value
                    controller.get(key, value);

                } else if (fragment != null && fragment != "") {
                    controller.getName();

                } else { //no key-value
                    controller.get();
                }
            } else if (method.equals("POST")) {
                if (fragment.equals("name")) {
                    controller.postName(body);
                }

            }

            return view.getStationView();
        }
        return ""; //Could not map url to a controller method - should do nothing
    }




    /**
     * Should return request line method, location, first key-value pair, fragment
     * @param httpRequestLine
     * @return
     */
    public String[] parseRequestLine(String httpRequestLine) {
        String[] requestLineInfo = {"", "", "", "", ""};

        String[] requestParser = httpRequestLine.split(" ");

        if (requestParser.length > 2) {
            String method = requestParser[0];
            String url = requestParser[1];
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
        }



        return requestLineInfo;
    }

}

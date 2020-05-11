/**
 * Take a URL path maps it to correct controller, model, view
 * Then makes the controller call the right method
 * Then returns the view response which is a string
 */
public class Router {

    public String route(String httpRequestLine) {
        String[] urlSummary = this.parseRequestLine(httpRequestLine);
        String method = urlSummary[0];
        String location = urlSummary[1];
        String key = urlSummary[2];
        String value = urlSummary[3];


        if (location.equals("/")) { //Station controller

            StationModel model = new StationModel();
            StationView view = new StationView();
            StationController controller = new StationController(model, view);

            if (method.equals("GET")) { //Get method
                if (key != "" && key != null && value != "" && value != null) { //single key-value
                    controller.get(key, value);

                } else { //no key-value
                    controller.get();
                }
            }

            return view.getStationView();
        }
        return ""; //Could not map url to a controller method - should do nothing
    }




    /**
     * Should return request line method, location, first key-value pair
     * @param httpRequestLine
     * @return
     */
    public String[] parseRequestLine(String httpRequestLine) {
        String[] requestLineInfo = new String[4];

        String[] requestParser = httpRequestLine.split(" ");

        String method = requestParser[0];
        String url = requestParser[1];
        String[] urlParser = url.split("\\?");
        String location = urlParser[0];

        String queryString;
        String[] keyValuePair;
        String key;
        String value;

        if (urlParser.length > 1) {
            queryString = urlParser[1];
            keyValuePair = queryString.split("=");
            key = keyValuePair[0];
            value = keyValuePair[1];
        } else {
            key = "";
            value = "";
        }

        requestLineInfo[0] = method;
        requestLineInfo[1] = location;
        requestLineInfo[2] = key;
        requestLineInfo[3] = value;

        return requestLineInfo;
    }

}

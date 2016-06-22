package connectionObjects;

import java.io.Serializable;

/**
 * Created by Kepplinger on 22.06.2016.
 */
public class Request implements Serializable {

    private RequestType requestType;

    public Request(RequestType requestType) {
        this.requestType = requestType;
    }

    public RequestType getRequestType() {
        return requestType;
    }
}

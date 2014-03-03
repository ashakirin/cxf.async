package demo.hw.common;

import javax.xml.transform.stream.StreamSource;

public class ResponseHandler {
    
    private StreamSource response;
    private String callbackOperation;
    
    public StreamSource getResponse() {
        return response;
    }
    public void setResponse(StreamSource response) {
        this.response = response;
    }
    public String getCallbackOperation() {
        return callbackOperation;
    }
    public void setCallbackOperation(String callbackOperation) {
        this.callbackOperation = callbackOperation;
    }
    
    
}

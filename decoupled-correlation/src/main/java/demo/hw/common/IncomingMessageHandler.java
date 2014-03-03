package demo.hw.common;

import javax.xml.transform.stream.StreamSource;

public interface IncomingMessageHandler {

    void handleMessage(StreamSource request, CallContext callContext, ResponseHandler responseHandler);
}

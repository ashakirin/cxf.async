package demo.hw.server.business;

import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.helpers.IOUtils;

import demo.hw.common.CallContext;
import demo.hw.common.IncomingMessageHandler;
import demo.hw.common.ResponseHandler;

public class SeekBookInBasementHandler implements IncomingMessageHandler {

    @Override
    public void handleMessage(StreamSource request, CallContext context, ResponseHandler responseHandler) {
        try {
            System.out.println("Invoked SeekBookInBasement handler");
            System.out.println(String.format("Message: %s\n related with: %s\n correlation: %s\n",
                                             context.getMessageID(), context.getRelatedMessages(),
                                             context.getCorrelationID()));
            System.out.println(IOUtils.readStringFromStream(request.getInputStream()));
            StreamSource response = new StreamSource(this.getClass().getResourceAsStream("/response-library.xml"));
            responseHandler.setResponse(response);
            responseHandler.setCallbackOperation("seekBookInBasementResponse");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

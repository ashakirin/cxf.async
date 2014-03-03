package demo.hw.server.business;

import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.helpers.IOUtils;

import demo.hw.common.CallContext;
import demo.hw.common.IncomingMessageHandler;
import demo.hw.common.ResponseProxy;

public class SeekBookInBasementHandler implements IncomingMessageHandler {

    @Override
    public void handleMessage(StreamSource request, CallContext context, ResponseProxy responseProxy) {
        try {
            System.out.println("Invoked SeekBookInBasement handler");
            System.out.println(IOUtils.readStringFromStream(request.getInputStream()));
            System.out.println(String.format("Message: %s\n related with: %s\n correlation: %s\n",
                                             context.getMessageID(), context.getRelatedMessages(),
                                             context.getCorrelationID()));
            StreamSource response = new StreamSource(this.getClass().getResourceAsStream("/response-library.xml"));
            responseProxy.sendResponse(response, context, "seekBookInBasementResponse");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

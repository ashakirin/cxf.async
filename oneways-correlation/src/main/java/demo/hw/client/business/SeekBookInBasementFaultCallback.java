package demo.hw.client.business;

import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.helpers.IOUtils;

import demo.hw.common.CallContext;
import demo.hw.common.IncomingMessageHandler;
import demo.hw.common.ResponseProxy;

public class SeekBookInBasementFaultCallback implements IncomingMessageHandler {

    @Override
    public void handleMessage(StreamSource request, CallContext context, ResponseProxy responseHandler) {
        try {
            System.out.println("Invoked SeekBookInBasementFault callback");
            System.out.println(IOUtils.readStringFromStream(request.getInputStream()));
            System.out.println(String.format("Message: %s\n related with: %s\n correlation: %s\n",
                                             context.getMessageID(), context.getRelatedMessages(),
                                             context.getCorrelationID()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}

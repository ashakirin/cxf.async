package demo.hw.client;

import java.util.Map;

import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Response;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.ws.addressing.AddressingProperties;

import demo.hw.common.CallContext;
import demo.hw.common.IncomingMessageHandler;
import demo.hw.common.MessageTools;

public class DispatchCallbackHandler implements AsyncHandler<StreamSource> {
    private final Client client;
    private final Map<String, IncomingMessageHandler> callbackMap;

    public DispatchCallbackHandler(Client client, Map<String, IncomingMessageHandler> callbackMap) {
        this.client = client;
        this.callbackMap = callbackMap;
    }
    
    @Override
    public void handleResponse(Response<StreamSource> response) {
        try {
            CallContext callContext = buildCallContext(client.getResponseContext());
            if (!callbackMap.containsKey(callContext.getOperationName())) {
                throw new RuntimeException("Unknown callback operation: " + callContext.getOperationName());
            }
            IncomingMessageHandler businessHandler = callbackMap.get(callContext.getOperationName());
            businessHandler.handleMessage(response.get(), callContext, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private CallContext buildCallContext(Map<String, Object> context) {
        AddressingProperties props = (AddressingProperties)context.get("javax.xml.ws.addressing.context.inbound");
        String messageID = props.getMessageID().getValue();
        String action = props.getAction().getValue();
        String relatesTo = props.getRelatesTo().getValue();
        String correlationID = MessageTools.getSoapHeader(MessageTools.CORRELATION_ID, context);
        CallContext callContext = new CallContext(messageID, correlationID, action);
        callContext.addRelatedMessage(relatesTo);        
        return callContext;
    }
}

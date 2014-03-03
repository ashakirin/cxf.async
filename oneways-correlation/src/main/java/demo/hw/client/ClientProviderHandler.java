package demo.hw.client;

import java.util.Map;

import javax.annotation.Resource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.ws.addressing.AddressingProperties;

import demo.hw.common.CallContext;
import demo.hw.common.IncomingMessageHandler;
import demo.hw.common.MessageTools;

@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
public class ClientProviderHandler implements Provider<StreamSource> {
    @Resource
    private WebServiceContext wsContext;
    private final Map<String, IncomingMessageHandler> callbackMap;

    public ClientProviderHandler(Map<String, IncomingMessageHandler> callbackMap) {
        this.callbackMap = callbackMap;
    }
    
    @Override
    public StreamSource invoke(StreamSource request) {
        CallContext context = buildCallContext(wsContext.getMessageContext());
        if (!callbackMap.containsKey(context.getOperationName())) {
            throw new RuntimeException("Unknown callback operation: " + context.getOperationName());
        }
        IncomingMessageHandler businessHandler = callbackMap.get(context.getOperationName());
        businessHandler.handleMessage(request, context, null);
        return null;
    }
    
    private CallContext buildCallContext(MessageContext messageContext) {
        AddressingProperties props = (AddressingProperties)messageContext.get("javax.xml.ws.addressing.context.inbound");
        String messageID = props.getMessageID().getValue();
        String action = props.getAction().getValue();
        String relatesTo = props.getRelatesTo().getValue();
        String correlationID = MessageTools.getSoapHeader(MessageTools.CORRELATION_ID, messageContext);
        CallContext context = new CallContext(messageID, correlationID, action);
        context.addRelatedMessage(relatesTo);        
        return context;
    }
    
 }

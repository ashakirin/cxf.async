package demo.hw.server;

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
import demo.hw.common.ResponseProxy;

@WebServiceProvider
@ServiceMode(value = Service.Mode.PAYLOAD)
public class ServiceProviderHandler implements Provider<StreamSource> {
    @Resource
    private WebServiceContext wsContext;
    private IncomingMessageHandler businessHandler;

    public ServiceProviderHandler(IncomingMessageHandler businessHandler) {
        this.businessHandler = businessHandler;
    }
    
    @Override
    public StreamSource invoke(StreamSource request) {
        System.out.println("Service is invoked!!!");

        CallContext callContext = buildCallContext(wsContext.getMessageContext());
        
        // TODO: cache proxies on the base of callback endpoints
        String replyTo = getReplyTo(wsContext.getMessageContext());
        ResponseProxy responseProxy = new ResponseProxy(replyTo);
        
        businessHandler.handleMessage(request, callContext, responseProxy);
        return null;
    }
        
    private String getReplyTo(MessageContext messageContext) {
        AddressingProperties props = (AddressingProperties)messageContext.get("javax.xml.ws.addressing.context.inbound");
        return props.getReplyTo().getAddress().getValue();
    }
    
    private CallContext buildCallContext(MessageContext messageContext) {
        AddressingProperties props = (AddressingProperties)messageContext.get("javax.xml.ws.addressing.context.inbound");
        String messageID = props.getMessageID().getValue();
        String action = props.getAction().getValue();
        String correlationID = MessageTools.getSoapHeader(MessageTools.CORRELATION_ID, messageContext);
        return new CallContext(messageID, correlationID, action);
    }
}

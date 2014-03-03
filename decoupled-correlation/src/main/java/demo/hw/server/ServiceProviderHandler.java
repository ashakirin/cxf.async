package demo.hw.server;

import java.util.concurrent.Future;

import javax.annotation.Resource;
import javax.xml.bind.JAXBException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Provider;
import javax.xml.ws.Service;
import javax.xml.ws.ServiceMode;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.WebServiceProvider;
import javax.xml.ws.handler.MessageContext;

import org.apache.cxf.annotations.UseAsyncMethod;
import org.apache.cxf.jaxws.ServerAsyncResponse;
import org.apache.cxf.ws.addressing.AddressingProperties;

import demo.hw.common.CallContext;
import demo.hw.common.IncomingMessageHandler;
import demo.hw.common.MessageTools;
import demo.hw.common.ResponseHandler;

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
    @UseAsyncMethod
    public StreamSource invoke(StreamSource request) {
        System.out.println("Service is invoked!!!");
        ResponseHandler responseHandler = new ResponseHandler();
        CallContext callContext = buildCallContext(wsContext.getMessageContext());
        businessHandler.handleMessage(request, callContext, responseHandler);
        setResponseOperation(wsContext.getMessageContext(), responseHandler.getCallbackOperation(), callContext);
        // Would necessary to sleep here until response in responseHandler is ready or use async model
        return responseHandler.getResponse();
    }
    
    public Future<?> invokeAsync(final StreamSource s, final AsyncHandler<Source> asyncHandler) {
        System.out.println("*** Called async ***");
        final ServerAsyncResponse<Source> r = new ServerAsyncResponse<Source>();
        final MessageContext msgContext = wsContext.getMessageContext();
        new Thread() {
            public void run() {
                ResponseHandler responseHandler = new ResponseHandler();
                CallContext callContext = buildCallContext(msgContext);
                businessHandler.handleMessage(s, callContext, responseHandler);
                r.set(responseHandler.getResponse());
                setResponseOperation(msgContext, responseHandler.getCallbackOperation(), callContext);
                asyncHandler.handleResponse(r);
            }
        }.start();
        return r;
    }

    private void setResponseOperation(MessageContext msgContext, String callbackOperation, CallContext context) {
        try {
            MessageTools.addSoapHeader(MessageTools.CORRELATION_ID, context.getCorrelationID(), msgContext);
        } catch (JAXBException e) {
            throw new RuntimeException("Cannot unmarshal correlation ID: " + e.getMessage(), e);
        }
    }

    private CallContext buildCallContext(MessageContext messageContext) {
        AddressingProperties props = (AddressingProperties)messageContext.get("javax.xml.ws.addressing.context.inbound");
        String messageID = props.getMessageID().getValue();
        String action = props.getAction().getValue();
        String correlationID = MessageTools.getSoapHeader(MessageTools.CORRELATION_ID, messageContext);
        return new CallContext(messageID, correlationID, action);
    }
}

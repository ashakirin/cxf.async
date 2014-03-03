package demo.hw.common;

import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

public class ResponseProxy {
    
    private Dispatch<StreamSource> proxy;
    
    public ResponseProxy(String replyTo) {
        WSDLConfiguration config = new WSDLConfiguration();
        Service service = Service.create(config.getServiceName());
        service.addPort(config.getEndpointName(), config.getBinding(), replyTo);
        proxy = service.createDispatch(config.getEndpointName(),
                                                                   StreamSource.class,
                                                                   Service.Mode.PAYLOAD);
        Client dClient = ((DispatchImpl<StreamSource>)proxy).getClient();
        WSAddressingFeature wsAddressingFeature = new WSAddressingFeature();
        wsAddressingFeature.initialize(dClient, dClient.getBus());
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.initialize(dClient, dClient.getBus());
        proxy.getRequestContext().put("thread.local.request.context", "true");
    }
    
    public void sendResponse(StreamSource response, CallContext callContext, String callbackOperation) {
        MessageTools.setWSAProps(proxy.getRequestContext(), callbackOperation,
                                 callContext.getMessageID(), callContext.getCorrelationID(), null);
        proxy.invokeOneWay(response);
   }
    
}

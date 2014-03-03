package demo.hw.common;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;
import org.apache.cxf.wsdl11.WSDLServiceFactory;

import demo.hw.client.business.SeekBookInBasementFaultCallback;
import demo.hw.client.business.SeekBookInBasementResponseCallback;

public class WSDLConfiguration {
    private final String wsdlLocation = "/Library.wsdl";
    private final QName serviceName = new QName("http://services.sopware.org/demos/Library/1.0", "LibraryProvider"); 

    private final QName portName = new QName("http://services.sopware.org/demos/Library/1.0", "Library_WS-I");
//    private final QName portName = new QName("http://services.sopware.org/demos/Library/1.0", "LibraryNotificationProvider_jmsPort");
    
    private final ServiceInfo si;
    private final EndpointInfo ei;
    
    private final String clientCallbackEndpoint = "http://127.0.0.1:2888/soap/LibraryConsumer";
//    private final String clientCallbackEndpoint = "jms:jndi:dynamicQueues/test.cxf.jmstransport.callback.queue?jndiInitialContextFactory=org.apache.activemq.jndi.ActiveMQInitialContextFactory&jndiConnectionFactoryName=ConnectionFactory&jndiURL=tcp://localhost:61616";

    private final Map<String, IncomingMessageHandler> callbackMap = new HashMap<String, IncomingMessageHandler>();

    public WSDLConfiguration() {
        WSDLServiceFactory wsdlSvcFactory = new WSDLServiceFactory(CXFBusFactory.getThreadDefaultBus(),
                                                                   getWsdlLocation());
        Service svc = wsdlSvcFactory.create();
        si = ServiceUtils.findServiceByName(svc, getServiceName());
        if (si == null) {
            throw new RuntimeException("WSDL does not contain "
                                       + getServiceName() + " service.");
        }

        ei = ServiceUtils.findEndpoint(si, portName);

        callbackMap.put("seekBookInBasementResponse", new SeekBookInBasementResponseCallback());
        callbackMap.put("seekBookInBasementFault", new SeekBookInBasementFaultCallback());
    }

    public String getWsdlLocation() {
        return wsdlLocation;
    }

    public QName getServiceName() {
        return serviceName;
    }

    public QName getEndpointName() {
        return ei.getName();
    }

    public String getServiceEndpoint() {
        return ei.getAddress();
    }

    public String getClientCallbackEndpoint() {
        return clientCallbackEndpoint;
    }

    public Map<String, IncomingMessageHandler> getCallbackMap() {
        return callbackMap;
    }

    public String getBinding() {
        return ei.getBinding().getBindingId();
    }
    
}

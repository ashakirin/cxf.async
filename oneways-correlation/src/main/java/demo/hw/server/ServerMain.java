package demo.hw.server;

import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import demo.hw.common.WSDLConfiguration;
import demo.hw.server.business.SeekBookInBasementHandler;

public class ServerMain {

    public static void main(String args[]) throws Exception {

        WSDLConfiguration config = new WSDLConfiguration();

        SeekBookInBasementHandler businessHandler = new SeekBookInBasementHandler();
        Object implementor = new ServiceProviderHandler(businessHandler);

        JaxWsServerFactoryBean factory = new JaxWsServerFactoryBean();

        factory.setServiceName(config.getServiceName());
        factory.setEndpointName(config.getEndpointName());
        factory.setWsdlLocation(config.getWsdlLocation());
        factory.setServiceBean(implementor);

        factory.getFeatures().add(new WSAddressingFeature());
        factory.getFeatures().add(new LoggingFeature());
        factory.getProperties(true).put("jaxws.provider.interpretNullAsOneway", Boolean.TRUE);
        factory.create();
        
    }
}

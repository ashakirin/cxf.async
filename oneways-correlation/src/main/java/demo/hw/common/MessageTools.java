package demo.hw.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.ws.addressing.AddressingProperties;
import org.apache.cxf.ws.addressing.AttributedURIType;
import org.apache.cxf.ws.addressing.EndpointReferenceType;
import org.apache.cxf.ws.addressing.RelatesToType;
import org.apache.cxf.ws.addressing.impl.AddressingPropertiesImpl;
import org.w3c.dom.Element;

public class MessageTools {
    public static final QName CORRELATION_ID = new QName("http://sopera.de", "CorrelationID");

    public static String getSoapHeader(QName name, Map<String, Object> context) {
        @SuppressWarnings("unchecked")
        List<Header> headers = (List<Header>)context.get(Header.HEADER_LIST);
        for (Header header : headers) {
            if (header.getName().equals(name)) {
                Element headerElement = (Element) header.getObject();
                return headerElement.getTextContent();
            }
        }
        return null;
    }
    
    public static void addSoapHeader(QName name, String value, Map<String, Object> context) throws JAXBException {
        List<Header> headers = new ArrayList<Header>();
        Header header = new Header(name, value, new JAXBDataBinding(String.class));
        headers.add(header);
         
        context.put(Header.HEADER_LIST, headers);
    }
    
    public static void setWSAProps(Map<String, Object> context, String action, String relatesTo,
                                   String correlationID, String replyTo) {
        try {
            AddressingProperties maps = new AddressingPropertiesImpl();
            if (action != null) {
                AttributedURIType actionAttr = new AttributedURIType();
                actionAttr.setValue(action);
                maps.setAction(actionAttr);
            }
            if (replyTo != null) {
                EndpointReferenceType replyToRef = new EndpointReferenceType();
                AttributedURIType address = new AttributedURIType();
                address.setValue(replyTo);
                replyToRef.setAddress(address);
                maps.setReplyTo(replyToRef);
            }
            RelatesToType relatesToAttr = new RelatesToType();
            relatesToAttr.setRelationshipType("message");
            relatesToAttr.setValue(relatesTo);
            maps.setRelatesTo(relatesToAttr);

            context.put("javax.xml.ws.addressing.context", maps);    
            MessageTools.addSoapHeader(MessageTools.CORRELATION_ID, correlationID, context);
        } catch (JAXBException e) {
            throw new RuntimeException("Cannot marshal SOAP header: " + e.getMessage(), e);
        }
    }
}
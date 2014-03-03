package demo.hw.common;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBException;
import javax.xml.namespace.QName;

import org.apache.cxf.headers.Header;
import org.apache.cxf.jaxb.JAXBDataBinding;
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
}
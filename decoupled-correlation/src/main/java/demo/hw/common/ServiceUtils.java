package demo.hw.common;

import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.apache.cxf.helpers.IOUtils;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.model.EndpointInfo;
import org.apache.cxf.service.model.ServiceInfo;

public class ServiceUtils {
	
	public static final String CONSUMER_POLICY_NAME = "LibraryServiceConsumerPolicy";

	public static final String PROVIDER_POLICY_NAME = "LibraryServicePolicy";

	public static final QName SERVICE_NAME = new QName(
			"http://services.sopware.org/demos/Library/1.0", "LibraryProvider");

	public static final QName OPERATION_NAME = new QName(
			"http://services.sopware.org/demos/Library/1.0", "seekBook");

	public static StreamSource getRequestMessage() {
		return new StreamSource(
				ServiceUtils.class.getResourceAsStream("/seekBookRequest.xml"));
	}

	public static StreamSource getResponseMessage() {
		return new StreamSource(
				ServiceUtils.class.getResourceAsStream("/seekBookResponse.xml"));
	}

	public static ServiceInfo findServiceByName(Service service,
			QName serviceName) {
		for (ServiceInfo si : service.getServiceInfos()) {
			if (si.getName().equals(serviceName)) {
				return si;
			}
		}
		return null;
	}

	public static EndpointInfo findEndpoint(ServiceInfo si, QName name) {
		for (EndpointInfo ei : si.getEndpoints()) {
			if (name.equals(ei.getName())) {
				return ei;
			}
		}
		return null;
	}

        public static EndpointInfo findJmsEndpoint(ServiceInfo si) {
            for (EndpointInfo ei : si.getEndpoints()) {
                    if ("http://cxf.apache.org/transports/jms".equals(ei
                                    .getTransportId())) {
                            return ei;
                    }
            }
            return null;
    }

        public static String toString(StreamSource src) {
		try {
			if (src.getInputStream() != null) {
				return IOUtils.toString(src.getInputStream());
			} else if (src.getReader() != null) {
				return IOUtils.toString(src.getReader());
			}
			return null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private ServiceUtils() {
	}
}

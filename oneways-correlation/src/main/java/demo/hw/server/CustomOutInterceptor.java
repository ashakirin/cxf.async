package demo.hw.server;

import org.apache.cxf.binding.soap.SoapBindingConstants;
import org.apache.cxf.binding.soap.SoapMessage;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.apache.cxf.ws.addressing.ContextUtils;

public class CustomOutInterceptor extends AbstractPhaseInterceptor<SoapMessage> {
	
	public CustomOutInterceptor() {
		super(Phase.POST_LOGICAL);
	}

	@Override
	public void handleMessage(SoapMessage message) throws Fault {
	    System.out.println(message.getExchange().getInMessage().get(ContextUtils.ACTION));
	    System.out.println(message.getExchange().getInMessage().getContextualProperty(ContextUtils.ACTION));
            System.out.println(message.getExchange().getInMessage().get("Test"));
            System.out.println(message.getExchange().getInMessage().getContextualProperty("Test"));
            System.out.println(message.get(SoapBindingConstants.SOAP_ACTION));
            System.out.println(message.getContextualProperty(SoapBindingConstants.SOAP_ACTION));
	}
	
	@Override
	public void handleFault(SoapMessage message) throws Fault {
		System.out.println("Fault is called");
	}
}

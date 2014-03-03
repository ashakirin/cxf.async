/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package demo.hw.client;

import java.util.UUID;

import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.AsyncHandler;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import demo.hw.common.WSDLConfiguration;
import demo.hw.common.MessageTools;

public final class ClientMainDecoupled {

    private static final String WS_ADDRESSING_REPLYTO = "org.apache.cxf.ws.addressing.replyto";

    private ClientMainDecoupled() {
    }

    public static void main(String args[]) throws Exception {

        WSDLConfiguration config = new WSDLConfiguration();

        Service service = Service.create(config.getServiceName());
        service.addPort(config.getEndpointName(), config.getBinding(), config.getServiceEndpoint());

        Dispatch<StreamSource> dispatcher = service.createDispatch(config.getEndpointName(),
                                                                   StreamSource.class,
                                                                   Service.Mode.PAYLOAD);

        Client dClient = ((DispatchImpl<StreamSource>)dispatcher).getClient();
        WSAddressingFeature wsAddressingFeature = new WSAddressingFeature();
        wsAddressingFeature.initialize(dClient, dClient.getBus());
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.initialize(dClient, dClient.getBus());

        dispatcher.getRequestContext().put(WS_ADDRESSING_REPLYTO,
                                           config.getClientCallbackEndpoint());
        
        MessageTools.addSoapHeader(MessageTools.CORRELATION_ID, UUID.randomUUID().toString(), dispatcher.getRequestContext());

        StreamSource request = new StreamSource(
                                                ClientMainDecoupled.class
                                                    .getResourceAsStream("/request-library.xml"));

        AsyncHandler<StreamSource> handler = new DispatchCallbackHandler(dClient,
                                                                         config.getCallbackMap());
        dispatcher.invokeAsync(request, handler);

    }

}

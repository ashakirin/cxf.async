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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Endpoint;
import javax.xml.ws.Service;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.Feature;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.jaxws.DispatchImpl;
import org.apache.cxf.jaxws.EndpointImpl;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import demo.hw.common.WSDLConfiguration;
import demo.hw.common.MessageTools;

public final class ClientMain {

    private ClientMain() {
    }

    public static void main(String args[]) throws Exception {

        WSDLConfiguration config = new WSDLConfiguration();

        Service service = Service.create(ClientMain.class
            .getResource(config.getWsdlLocation()), config.getServiceName());
        service.addPort(config.getEndpointName(), config.getBinding(), config.getServiceEndpoint());

        // 1. Creating client
        Dispatch<StreamSource> dispatcher = service.createDispatch(config.getEndpointName(),
                                                                   StreamSource.class,
                                                                   Service.Mode.PAYLOAD);

        Client dClient = ((DispatchImpl<StreamSource>)dispatcher).getClient();
        WSAddressingFeature wsAddressingFeature = new WSAddressingFeature();
        wsAddressingFeature.initialize(dClient, dClient.getBus());
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.initialize(dClient, dClient.getBus());

        // 2. Register own endpoint on the client side
        ClientProviderHandler callbackHandler = new ClientProviderHandler(config.getCallbackMap());
        EndpointImpl ep = (EndpointImpl)Endpoint.create(callbackHandler);
        List<Feature> features = new ArrayList<Feature>();
        features.add(new WSAddressingFeature());
        features.add(new LoggingFeature());
        ep.setFeatures(features);
        ep.getProperties().put("jaxws.provider.interpretNullAsOneway", Boolean.TRUE);
        ep.publish(config.getClientCallbackEndpoint());
        
        StreamSource request = new StreamSource(
                                                ClientMain.class
                                                    .getResourceAsStream("/request-library.xml"));

        MessageTools.setWSAProps(dispatcher.getRequestContext(), null,
                                 null, UUID.randomUUID().toString(), config.getClientCallbackEndpoint());

        // 3. Invoke service operation
        dispatcher.invokeOneWay(request);

    }

}

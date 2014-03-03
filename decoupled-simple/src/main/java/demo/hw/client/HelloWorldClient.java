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

import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Service;
import javax.xml.ws.soap.SOAPBinding;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.feature.LoggingFeature;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.ws.addressing.WSAddressingFeature;

import demo.hw.server.HelloWorld;
import demo.hw.server.User;
import demo.hw.server.UserImpl;

public final class HelloWorldClient {

    private static final String WS_ADDRESSING_REPLYTO = "org.apache.cxf.ws.addressing.replyto";

    private static final QName SERVICE_NAME 
        = new QName("http://server.hw.demo/", "HelloWorld");
    private static final QName PORT_NAME 
        = new QName("http://server.hw.demo/", "HelloWorldPort");


    private HelloWorldClient() {
    } 

    public static void main(String args[]) throws Exception {
        Service service = Service.create(SERVICE_NAME);
        // Endpoint Address
        String endpointAddress = "http://localhost:9000/helloWorld";
        String decoupledAddress = "http://localhost:9001/response";

        // Add a port to the Service
        service.addPort(PORT_NAME, SOAPBinding.SOAP11HTTP_BINDING, endpointAddress);
        
        HelloWorld hw = service.getPort(HelloWorld.class);
        System.out.println(hw.sayHi("World"));

        Client client = ClientProxy.getClient(hw);
        WSAddressingFeature wsAddressingFeature = new WSAddressingFeature();
        wsAddressingFeature.initialize(client, client.getBus());
        LoggingFeature loggingFeature = new LoggingFeature();
        loggingFeature.initialize(client, client.getBus());

        ((BindingProvider) hw).getRequestContext().put(WS_ADDRESSING_REPLYTO,
        		decoupledAddress);

        User user = new UserImpl("World");
        System.out.println(hw.sayHiToUser(user));

        //say hi to some more users to fill up the map a bit
        user = new UserImpl("Galaxy");
        System.out.println(hw.sayHiToUser(user));

        user = new UserImpl("Universe");
        System.out.println(hw.sayHiToUser(user));

        System.out.println();
        System.out.println("Users: ");
        Map<Integer, User> users = hw.getUsers();
        for (Map.Entry<Integer, User> e : users.entrySet()) {
            System.out.println("  " + e.getKey() + ": " + e.getValue().getName());
        }

    }

}

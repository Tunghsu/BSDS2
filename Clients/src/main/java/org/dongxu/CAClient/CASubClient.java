/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.CAClient;

import java.util.LinkedList;
import java.util.Queue;
import org.dongxu.utils.log;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.glassfish.jersey.client.ClientConfig;
import org.json.JSONTokener;
import org.json.JSONObject;

/**
 *
 * @author Ian Gortan
 * Simple client to test subscribing from CAServer over RMI
 */
public class CASubClient {

    private static log out = new log("Sub");

    private CASubClient() {}

    private static String getLatestContent(Integer subscriberId){
        Client client = ClientBuilder.newClient();
        String dst_sub = config.REST_BASE_URL+"/getlatestcontent/"+subscriberId.toString();
        Response sub_res = client.target(dst_sub)
                .request()
                .accept(MediaType.APPLICATION_JSON)
                .get(Response.class);

        String jsonMessage = sub_res.readEntity(String.class);
        out.println("Data received:" + jsonMessage);
        JSONTokener jsonMessageTokener = new JSONTokener(jsonMessage);
        JSONObject jsonMessageObject;
        jsonMessageObject = (JSONObject) jsonMessageTokener.nextValue();
        String message = jsonMessageObject.getString("message");

        return message;
    }

    public static void main(String[] args) {
        Integer count=0, sleepInterval=0, i=0;
        String host = (args.length < 1) ? null : args[0];
        try {
            out.println ("Subscriber Client Starter");
            Client client = ClientBuilder.newClient();
            //Registry registry = LocateRegistry.getRegistry(host);
            out.println ("Connected to registry");
            //BSDSSubscribeInterface CAServerStub = (BSDSSubscribeInterface) registry.lookup("CAServerSubscriber");
            out.println ("Stub initialized");
            String dst = config.REST_BASE_URL+"/regsubscriber/"+args[1];
            Response res = client.target(dst).request().accept(MediaType.APPLICATION_JSON).get(Response.class);
            //int id = CAServerStub.registerSubscriber(args[1]);
            String jsonText = res.readEntity(String.class);
            out.println("Data received:" + jsonText);
            JSONTokener jsonTokener = new JSONTokener(jsonText);
            JSONObject studentJSONObject;
            studentJSONObject = (JSONObject) jsonTokener.nextValue();
            Integer id = studentJSONObject.getInt("id");
            out.name = "Sub"+Integer.toString(id);
            out.println("Sub id = " + Integer.toString(id));

            /* time for all subscribers to reg on server */
            Thread.sleep(2000);

            Long timeInterval = (long)0;
            while (sleepInterval < 6401) {
                i++;
                long currentTimeStamp = System.currentTimeMillis();

                String message = getLatestContent(id);

                //String message = CAServerStub.getLatestContent(id);
                if (!message.equals("")) {
                    timeInterval += System.currentTimeMillis() - currentTimeStamp;
                    //out.println("Query #"+i.toString()+": message is " + message);
                    count++;
                    sleepInterval = 0;
                }
                else {
                    out.println("Query #"+i.toString()+": Got empty message");
                    if (sleepInterval == 0){
                        sleepInterval = 100;
                    } else {
                        sleepInterval *= 2;
                    }
                    out.println("Sleep for "+ sleepInterval.toString()+ "ms");
                    Thread.sleep(sleepInterval);
                }
                //System.out.println("message is " + message);
                //Thread.sleep(1);
            }
            out.println(" ... Looks like Subscribe Client is working too");
            out.println("Totally retrieved messages for " + args[1] + ": " + count.toString());
            out.println("Totally cost " + timeInterval.toString() + " ms");
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
} 

package org.dongxu.CAClient;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Ian Gortan
 */
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
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
import org.glassfish.jersey.jackson.JacksonFeature;

// Simple client to test publishing to CAServer over RMI

class PubThread extends Thread {
    log out = new log("Pub_t");
    private String[] args;
    PubThread(String[] arguments){
        args = arguments;
    }
    public void run(){
        try {
            Client client = ClientBuilder.newClient(/*new ClientConfig()
                    .register(JacksonFeature.class)*/);
            out.println("Publisher Client Starter");
            //Registry registry = LocateRegistry.getRegistry(args[0], 1099);
            out.println("Connected to registry");
            //BSDSPublishInterface CAServerStub = (BSDSPublishInterface) registry.lookup("CAServerPublisher");
            out.println("Stub initialized");
            String dst = config.REST_BASE_URL+"/regpublisher/"+args[2]+"/"+args[3];
/*            WebTarget webTarget = client.target(dst);
            Invocation.Builder invocationBuilder =
                    webTarget.request(MediaType.APPLICATION_JSON);
            invocationBuilder.header("some-header", "true");
            Response res = invocationBuilder.get();*/
            Response res = client.target(dst).request().accept(MediaType.APPLICATION_JSON).get(Response.class);
            //int id = CAServerStub.registerPublisher(args[2], args[3]);
            out.println("Pub id = " + res.readEntity(String.class).toString());
            //out.name = "Pub"+Integer.toString(id);

/*            Integer TimesToSend = Integer.parseInt(args[4]);
            Long currentTimeStamp = System.currentTimeMillis();
            for (Integer i = 0; i < TimesToSend; i++) {
                CAServerStub.publishContent(id, "#" + i.toString(), "Message#" + i.toString(), 30000);
                //out.println("#" + i.toString() + " message sent");
            }
            Long timeInterval = System.currentTimeMillis() - currentTimeStamp;
            out.println("Totally sent messages for " + args[3] + ": " + TimesToSend.toString());
            out.println("Totally cost " + timeInterval.toString() + " ms");*/
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}

public class CAPubClient {

    private CAPubClient() {}

    public static void main(String[] args) {
        log out = new log("Pub");
        if (args.length < 4){
            out.println("Not enough arguments, exit");
            return;
        }
        final Integer numOfThreads = Integer.parseInt(args[1]);
        Queue<Thread> threadQueue = new LinkedList<Thread>();

        for (int i=0; i<numOfThreads; i++){
            Thread t = new PubThread(args);
            threadQueue.add(t);
            t.start();
        }
        for (int i=0; i<numOfThreads; i++) {
            Thread t = threadQueue.poll();
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        out.println(numOfThreads.toString()+" Publish Thread(s) have done their work");
    }
}    


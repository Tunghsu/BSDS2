package org.dongxu.CAClient;

import org.dongxu.utils.log;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by dongxu on 12/4/16.
 */


// Simple client to test publishing to CAServer over JAX-RS

class TopThread extends Thread {

    private static void getTopN(Integer n){
        Client client = ClientBuilder.newClient();
        String dst = config.REST_BASE_URL+"/gettopn/"+n.toString();
        Response res = client.target(dst).
                request().accept(MediaType.TEXT_HTML).get(Response.class);
        String html = res.readEntity(String.class);

        out.println(html);
    }
    private static log out = new log("Top_t");
    private Integer n;
    private String[] args;
    TopThread(String[] args, Integer n){
        this.n = n;
        this.args = args;
    }
    public void run(){
        try {
            out.println("TopN Client Starting");

            while(true) {
                getTopN(this.n);
                sleep(1000);
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }

    }
}

public class CATopNClient {

    private CATopNClient() {}

    public static void main(String[] args) {
        log out = new log("Top");
        if (args.length <= 1){
            out.println("Not enough arguments, exit");
            return;
        }
        final Integer numOfThreads = Integer.parseInt(args[1]);
        Queue<Thread> threadQueue = new LinkedList<Thread>();

        for (int i=0; i<numOfThreads; i++){
            Random rand = new Random();
            int  n = rand.nextInt(100) + 1;
            Thread t = new TopThread(args, n);
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

        out.println(numOfThreads.toString()+" TopN Thread(s) have done their work");
    }
}



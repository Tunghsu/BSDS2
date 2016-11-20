/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.CAClient;

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import org.dongxu.utils.log;

/**
 *
 * @author Ian Gortan
 * Simple client to test subscribing from CAServer over RMI
 */
public class CASubClient {

    private CASubClient() {}

    public static void main(String[] args) {
        Integer count=0, sleepInterval=0, i=0;
        log out = new log("Sub");
        String host = (args.length < 1) ? null : args[0];
        try {
            out.println ("Publisher Client Starter");
            Registry registry = LocateRegistry.getRegistry(host);
            out.println ("Connected to registry");
            BSDSSubscribeInterface CAServerStub = (BSDSSubscribeInterface) registry.lookup("CAServerSubscriber");
            out.println ("Stub initialized");
            int id = CAServerStub.registerSubscriber(args[1]);
            out.name = "Sub"+Integer.toString(id);
            out.println("Sub id = " + Integer.toString(id));

            /* time for all subscribers to reg on server */
            Thread.sleep(2000);

            Long timeInterval = (long)0;
            while (sleepInterval < 6401) {
                i++;
                long currentTimeStamp = System.currentTimeMillis();
                String message = CAServerStub.getLatestContent(id);
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

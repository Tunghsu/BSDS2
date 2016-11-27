package org.dongxu.CAClient;

/**
 * Created by dongxu on 10/13/16.
 */

import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import org.dongxu.utils.log;

public class CADiagnoseClient {

    private CADiagnoseClient() {}

    public static void main(String[] args) {
        String host = (args.length < 1) ? null : args[0];
        log out = new log("Diag");

        try {
            out.println ("Diagnose Client Starter");
            Registry registry = LocateRegistry.getRegistry(host);
            out.println ("Connected to registry");
            BSDSDiagnoseInterface CAServerStub = (BSDSDiagnoseInterface) registry.lookup("CADiagnoseServer");
            out.println ("Stub initialized");
            ArrayList<String> topicsCollection = new ArrayList<>();
            Integer timeInvertval = Integer.parseInt(args[1]);
            for (int i=2; i<args.length; i++){
                topicsCollection.add(args[i]);
            }
            String[] topics = topicsCollection.toArray(new String[topicsCollection.size()]);
            ConcurrentHashMap<String, Integer> counts;
            Integer timeCount=0;
            while (true) {
                Thread.sleep(timeInvertval);
                counts = CAServerStub.getNumOfMessages(topics);
                for (int i=0; i<topics.length;i++){
                    out.println(topics[i]+" current count: "+counts.get(topics[i]).toString());
                }
                timeCount++;
                out.println("Time: "+ timeCount.toString() + " ... Current Round Done");
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}


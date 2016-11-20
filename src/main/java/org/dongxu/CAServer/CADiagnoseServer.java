package org.dongxu.CAServer;

import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by dongxu on 10/13/16.
 */
public class CADiagnoseServer implements BSDSDiagnoseInterface{
    private CAServer pub;
    CADiagnoseServer(CAServer pubServer){
        pub = pubServer;
    }
    public ConcurrentHashMap<String, Integer> getNumOfMessages(String[] Topics){
        ConcurrentHashMap<String, Integer> result = new ConcurrentHashMap<>();
        for (String topic:Topics){
            if (pub.TopicToGlobalConverter.containsKey(topic))
                result.put(topic, pub.TopicToGlobalConverter.get(topic).size());
            else
                result.put(topic, 0);
        }
        return result;
    }
}

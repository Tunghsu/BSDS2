package org.dongxu.CAServer;

/**
 * Created by dongxu on 10/13/16.
 */

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.concurrent.ConcurrentHashMap;


public interface BSDSDiagnoseInterface extends Remote {
    ConcurrentHashMap<String, Integer> getNumOfMessages(String[] Topics);

}

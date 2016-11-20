
/**
 * Created by Ian Gortan on 9/20/2016.
 */

package org.dongxu.CAServer;
import java.rmi.Remote;
import java.rmi.RemoteException;



public interface BSDSPublishInterface{
    // returns unique publisherID. Each publisher publishes messages on a single topic
    int registerPublisher(String name, String topic);

    // publishes a message to the server
    void publishContent (int publisherID,
                            String title,
                            String message,
                            int TimeToLive);
}

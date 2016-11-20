
/**
 *
 * @author Ian Gortan
 */

package org.dongxu.CAServer;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Created by Ian Gortan on 9/19/2016.
 */
public interface BSDSSubscribeInterface{
    // registers a new subscriber and resturns a dubscriber id
    int registerSubscriber (String topic);

    // gets next outstanding message for a subscription
    String getLatestContent(int subscriberID);
}
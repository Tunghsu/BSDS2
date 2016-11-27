/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.ejb;

import java.util.concurrent.ConcurrentLinkedQueue;
import javax.ejb.Remote;

/**
 *
 * @author dongxu
 */
@Remote
public interface CAServerSessionBeanRemote {
    
    int registerPublisher(String name, String topic);

    int registerSubscriber(String topic);

    String getLatestContent(int subscriberID);

    String getTopN(Integer n);

    void publishContent(Integer publisherID, String title, String message, Integer TimeToLive);

    void addToContentBuffer(org.dongxu.ejb.BSDSMessage message);
    
}

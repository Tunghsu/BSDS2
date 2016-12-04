/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.server;

import org.dongxu.ejb.BSDSMessage;

/**
 *
 * @author dongxu
 */
class AsyncPublisherConsumer extends Thread {
    CAServerSessionBean caserver;
    public AsyncPublisherConsumer(CAServerSessionBean server){
        caserver = server;
    }
    public void run(){
        BSDSMessage message;
        while (true){
            while (caserver.contentBuffer.isEmpty());
            do {
                message = caserver.contentBuffer.poll();
            } while (message == null);
            caserver.publishContent(message.getPublisherId(), message.getTitle(),
                    message.getMessage(), 300000);
        }


    }
}

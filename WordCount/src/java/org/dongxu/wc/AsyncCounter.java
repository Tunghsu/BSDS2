/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.wc;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 *
 * @author dongxu
 */
public class AsyncCounter extends Thread {
    SingMessageClass messageCounter;
    public AsyncCounter(SingMessageClass singMessageClass){
            messageCounter = singMessageClass;
            System.out.print("Initialized messageBuffer");
    }
    public void run(){
        while (true){
            while (messageCounter.incomingMessageBuffer.isEmpty());
            String message = messageCounter.incomingMessageBuffer.poll();
            try {
                messageCounter.countWords(message);
            } catch (Exception e){
                System.out.print("Error at run of AsyncCounter " + 
                        e.toString());
            }
        }


    }
    
}

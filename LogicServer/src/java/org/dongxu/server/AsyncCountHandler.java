/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.server;

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
public class AsyncCountHandler extends Thread {
    public static ConcurrentLinkedQueue<String> messageBuffer;
    ConnectionFactory cf;
    Destination destination;
    MessageProducer mp;
    Session s;
    Connection conn;
    
    public AsyncCountHandler(ConcurrentLinkedQueue<String> q){
        messageBuffer = q;
        
        try {
            IntializeMessageQueue();
        } catch (Exception e){
                System.out.print("Error at Init of AsyncCountHandler" + 
                        e.getStackTrace().toString());
        }
        
        System.out.print("Initialized messageBuffer");
    }
    
    public void run(){
        String message;
        while (true){
            while (messageBuffer.isEmpty());
            do {
                message = messageBuffer.poll();
            } while (message == null);
            try {
                sendJMSMessageToMessageQueue(message);
            } catch (Exception e){
                System.out.print("Error at run of AsyncCountHandler "+
                        e.toString());
            }
        }


    }

    private Message createJMSMessageForjmsMessageQueue(Session session, Object messageData) throws JMSException {
        // TODO create and populate message to send
        TextMessage tm = session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }

    private synchronized void sendJMSMessageToMessageQueue(Object messageData) throws JMSException, NamingException {        
        mp.send(createJMSMessageForjmsMessageQueue(s, messageData));
    }
    
    private void IntializeMessageQueue() throws JMSException, NamingException { 
        Context c = new InitialContext();    
        cf = (ConnectionFactory) c.lookup("jms/MessageConnectionFactory");
        destination = (Destination) c.lookup("jms/MessageQueue");
        conn = cf.createConnection();
        s = conn.createSession(false, s.AUTO_ACKNOWLEDGE);
        mp = s.createProducer(destination);     
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.wc;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

/**
 *
 * @author dongxu
 */
@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/MessageQueue")
    ,
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue")
})
public class MessageBean implements MessageListener {

    private static SingMessageClass singMessageClass = SingMessageClass.getInstance();
    
    public MessageBean() {
        //System.out.print("MessageBean Initialized");
    }
    
    @Override
    public void onMessage(Message message) {
        //System.out.print("Got a message");
        try{
            String messageText = ((TextMessage)message).getText();
            //System.out.print(messageText);
            singMessageClass.incomingMessageBuffer.add(messageText);
            //singMessageClass.countWords(messageText);
        } catch (Exception e){
            System.out.print("Error at onMessage"+e.getMessage());
        }
    }
    
}
/**
 * Created by Ian Gortan on 9/19/2016.
 */
package org.dongxu.CAServer;

import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;
import org.dongxu.utils.log;

//Skeleton of CAServer supporting both BSDS interfaces

public class CAServer implements BSDSPublishInterface, BSDSSubscribeInterface{

    private log out = new log("Serv");

    public class TimeElement{
        public long TimeStamp;
        public Integer GlobalId;
        TimeElement(long ts, int id){
            TimeStamp = ts;
            GlobalId = id;
        }
    }

    public class MessageIdentifier{
        public String Topic;
        public Integer TopicId;
        MessageIdentifier(String name, Integer id){
            Topic = name;
            TopicId = id;
        }
    }

    private int CurrentMessageIdOrder=0;
    private static int CurrentUserIdOrder=0;
    private static ConcurrentHashMap<String, ConcurrentHashMap<Integer, Integer>> MessageDeliveredCount = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, ConcurrentHashMap<Integer, Integer>> TopicToGlobalConverter = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, MessageIdentifier> GlobalToTopicConverter = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, Integer> LastTopicIdBySubscriberId = new ConcurrentHashMap<>();
    // Used by publishers
    private ConcurrentHashMap<Integer, String[]> TopicByPublisherId = new ConcurrentHashMap<>();
    private ConcurrentHashMap<String, Integer> TopicIdGenerator = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<Integer, BSDSMessage> Messages = new ConcurrentHashMap<Integer, BSDSMessage>();
    public static ConcurrentHashMap<String, Integer> CurrentProgressByTopic = new ConcurrentHashMap<>();
    public static ConcurrentHashMap<String, Integer> LowestIdByTopic = new ConcurrentHashMap<>();

    // Used by subscribers
    private static ConcurrentHashMap<String, Integer> TopicSubscriberCount = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Integer, String> SubscribedTopicBySubscriberId = new ConcurrentHashMap<>();

    public static class PQsort implements Comparator<TimeElement> {

        public int compare(TimeElement one, TimeElement two) {
            return two.TimeStamp - one.TimeStamp > 0? -1: 1;
        }
    }

    public PriorityBlockingQueue<TimeElement> TimeOutQueue = new PriorityBlockingQueue<>(1, new PQsort());

    public Boolean MessageRemover(Integer GlobalId){
        if (!Messages.containsKey(GlobalId))
            // which means this message has already been removed because it has been
            // delivered to all available subscribers
            return false;
        Messages.remove(GlobalId);
        CAServer.MessageIdentifier message = GlobalToTopicConverter.get(GlobalId);
        GlobalToTopicConverter.remove(GlobalId);
        ConcurrentHashMap<Integer, Integer> h = TopicToGlobalConverter.get(message.Topic);
        h.remove(message.TopicId);
        TopicToGlobalConverter.replace(message.Topic, h);
        if (message.TopicId == LowestIdByTopic.get(message.Topic)){

            // If the current removed element has the lowest ID of this topic, then
            // we move forward to find the new lowest ID
            if (h.isEmpty()){
                LowestIdByTopic.replace(message.Topic, CurrentProgressByTopic.get(message.Topic));
            } else {
                for (Integer j = message.TopicId + 1; j <= CurrentProgressByTopic.get(message.Topic); j++) {
                    if (h.containsKey(j)) {
                        LowestIdByTopic.replace(message.Topic, j);
                        break;
                    }
                }
            }
        }
        return true;
    }

    public int registerPublisher(String name, String topic) {
        String[] value = new String[]{name, topic};
        int id;
        synchronized (this) {
             id = ++CurrentUserIdOrder;
        }
        TopicByPublisherId.put(id, value);

        if (!TopicIdGenerator.containsKey(topic)){
            TopicIdGenerator.put(topic, 0);
        }
        out.println(
                        "Publisher: " +
                        name + "\t" +
                        topic
        );
        return id;
    }

    // publishes a message to the server
     public void  publishContent (int publisherID, String title,String message, int TimeToLive){
         String[] value = TopicByPublisherId.get(publisherID);
         String topic = value[1], name = value[0];
         //ContentByTopic.put(topic, value);

         Integer GlobalMessageId, TopicMessageId;
         synchronized (this){
             GlobalMessageId = ++CurrentMessageIdOrder;
         }
         synchronized (this){
             TopicMessageId = TopicIdGenerator.get(topic)+1;
             TopicIdGenerator.replace(topic, TopicMessageId);
         }
         BSDSMessage bsdsMessage= new BSDSMessage(title, message, name);
         Messages.put(GlobalMessageId, bsdsMessage);

         ConcurrentHashMap<Integer, Integer> converter, messageCount;
         if (!TopicToGlobalConverter.containsKey(topic)){
             converter = new ConcurrentHashMap<>();
             TopicToGlobalConverter.put(topic, converter);
             LowestIdByTopic.put(topic, 0);
         } else {
             converter = TopicToGlobalConverter.get(topic);
         }
         converter.put(TopicMessageId, GlobalMessageId);
         GlobalToTopicConverter.put(GlobalMessageId, new MessageIdentifier(topic, TopicMessageId));
         TopicToGlobalConverter.replace(topic, converter);

         if (!MessageDeliveredCount.containsKey(topic)) {
             messageCount = new ConcurrentHashMap<>();
             MessageDeliveredCount.put(topic, messageCount);
         } else {
             messageCount = MessageDeliveredCount.get(topic);
         }
         messageCount.put(TopicMessageId, 0);
         MessageDeliveredCount.replace(topic, messageCount);

         long currentTimeStamp = System.currentTimeMillis();

         TimeElement te = new TimeElement(currentTimeStamp+TimeToLive+100, GlobalMessageId);
         TimeOutQueue.add(te);

         if (CurrentProgressByTopic.containsKey(topic)){
             CurrentProgressByTopic.replace(topic, TopicMessageId);
         } else {
             CurrentProgressByTopic.put(topic, TopicMessageId);
         }


         //out.println ("Message: #" + GlobalMessageId.toString() + " received");
    }

     public int registerSubscriber (String topic) {
         int id;
         synchronized (this) {
             id = ++CurrentUserIdOrder;
         }

         // TODO: Handle errors when the specified topic does not exist
         SubscribedTopicBySubscriberId.put(id, topic);

         Object StartId = LowestIdByTopic.get(topic);
         if (StartId == null)
             StartId = 0;
         LastTopicIdBySubscriberId.put(id, (Integer)StartId);
         out.println("Topic is  " + topic);

         if (TopicSubscriberCount.containsKey(topic)){
             TopicSubscriberCount.replace(topic, TopicSubscriberCount.get(topic)+1);
         } else {
             TopicSubscriberCount.put(topic, 1);
         }

         return id;
     }

    // gets next outstanding message for a subscription
    public String getLatestContent(int subscriberID) {
        String message = "";
        BSDSMessage retrievedMessage;
        //String message = "Title: Soccer scores, Message: City win 8 straight games";
        // TODO: Handle error if subscriberId does not exist
        String topic = SubscribedTopicBySubscriberId.get(subscriberID);
        if (CurrentProgressByTopic.containsKey(topic)){
            Integer LatestId = CurrentProgressByTopic.get(topic);
            // TODO: Handle error if subscriberId does not exist
            Integer LastId = LastTopicIdBySubscriberId.get(subscriberID);
/*
            if (LastId == 0)
                return message;
*/

            if (LatestId >= LastId){
                Integer globalId;
                do {
                    LastId++;
                    globalId = TopicToGlobalConverter.get(topic).get(LastId);
                    //TopicToGlobalConverter.get(topic).remove(LastId+1);
                } while (LastId < LatestId && (globalId == null || !Messages.containsKey(globalId)));
                if (LastId > LatestId || globalId == null){
                    //out.println("Buffer is empty");
                    return message;
                }
                retrievedMessage = Messages.get(globalId);
                Integer readCount = MessageDeliveredCount.get(topic).get(LastId);
                if (readCount+1 == TopicSubscriberCount.get(topic)){
                    MessageRemover(globalId);
                }
                message += "Title: ";
                message += retrievedMessage.getTitle();
                message += ", Message: ";
                message += retrievedMessage.getMessage();
                LastTopicIdBySubscriberId.replace(subscriberID, LastId);
                //out.println("Delivered latest content: " + message);
            } else {
                //out.println("returning empty content");
            }
        }
        return message;
    }
}

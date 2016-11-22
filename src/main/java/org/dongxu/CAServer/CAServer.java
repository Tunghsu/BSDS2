/**
 * Created by Ian Gortan on 9/19/2016.
 */
package org.dongxu.CAServer;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.Comparator;
import org.dongxu.utils.log;
import java.util.StringTokenizer;
import java.util.HashSet;
import org.sqlite.JDBC;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

//Skeleton of CAServer supporting both BSDS interfaces

public class CAServer implements BSDSPublishInterface, BSDSSubscribeInterface{

    private static Connection connection;
    private static Statement statement;
    private static HashSet<String> set = new HashSet<>();
    private log out = new log("Serv");

    public CAServer(){
        try {
            Class.forName("org.sqlite.JDBC");
            //connection = DriverManager.getConnection("jdbc:sqlite:db/db.sqlite");
            connection = DriverManager.getConnection("jdbc:sqlite:/home/dongxu/Workspace/IntelliJ13.0-JerseyWS-master/db/db.sqlite");
            statement = connection.createStatement();
        } catch (Exception e){
            out.println(e.toString());
        }
        set.add("a");
        set.add("the");
        set.add("is");
        set.add("are");
        set.add("of");
    }

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
         BSDSMessage bsdsMessage= new BSDSMessage(title, message, name, 0);
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

    public void countWords(String message){
        StringTokenizer st = new StringTokenizer(message, " ");
        ConcurrentHashMap<String, Integer> wordsMap = new ConcurrentHashMap<>();
        while(st.hasMoreTokens()) {
            String word = st.nextToken();
            System.out.println(word);
            if (set.contains(word)){
                continue;
            }
            if (wordsMap.containsKey(word)){
                wordsMap.replace(word, wordsMap.get(word)+1);
            } else {
                wordsMap.put(word, 1);
            }
        }

        /* update sqlite db */
        try {
            for (ConcurrentHashMap.Entry<String, Integer> cursor : wordsMap.entrySet()){
                String select = "SELECT * FROM words_count WHERE word=\'"
                        + cursor.getKey() + "\'";
                synchronized(this){
                    ResultSet rs = statement.executeQuery(select);
                    if (rs.next()) {
                        Integer oldWordCount = rs.getInt("word_count");
                        Integer oldDerivedCount = rs.getInt("derived_message_count");
                        String sql = "UPDATE words_count SET word_count=\'" +
                                ((Integer) (oldWordCount + cursor.getValue())).toString() +
                                "\', derived_message_count=\'" + ((Integer) (oldDerivedCount + 1)).toString()
                                + "\' WHERE word=\'" + cursor.getKey() + "\'";
                        statement.executeUpdate(sql);
                    } else {
                        String sql = "INSERT INTO words_count (word, word_count, derived_message_count)" +
                                " VALUES(\'" + cursor.getKey() + "\', " + cursor.getValue().toString() +
                                ", 1)";
                        statement.executeUpdate(sql);
                    }
                }
            }
        } catch (Exception e){
            System.out.println(e.toString());
        }


    }

    public String getTopN(Integer n){
        String query = "SELECT * FROM words_count ORDER BY word_count DESC LIMIT " + n.toString();
        String topNResult = "";
        try {
            ResultSet rs = statement.executeQuery(query);
            while (rs.next()){
                topNResult += rs.getString("word") + ": " +
                        ((Integer)rs.getInt("word_count")).toString() + "< br />";
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return topNResult;
    }
}

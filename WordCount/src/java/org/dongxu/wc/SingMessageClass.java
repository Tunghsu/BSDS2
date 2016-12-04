/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.wc;

import static java.lang.System.out;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import javax.ejb.Singleton;

/**
 *
 * @author dongxu
 */
public class SingMessageClass{
    private static HashSet<String> set = new HashSet<>();
    public static ConcurrentLinkedQueue<String> incomingMessageBuffer = new ConcurrentLinkedQueue<String>();
    private static Connection connection;
    private static Statement statement;
    
    private volatile static SingMessageClass sing = null;

    public static SingMessageClass getInstance() {
        if (sing == null) {
            synchronized (SingMessageClass.class) {
                if (sing == null) {
                    sing = new SingMessageClass();
                }
            }
        }
        return sing;
    }
    
    SingMessageClass(){
   
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:/opt/db.sqlite");
            //connection = DriverManager.getConnection("jdbc:sqlite:/home/dongxu/Workspace/IntelliJ13.0-JerseyWS-master/db/db.sqlite");
            statement = connection.createStatement();
        } catch (Exception e){
            out.println("Error at Init of SingMessageClass"+
                    e.getMessage());
        }
        set.add("a");
        set.add("the");
        set.add("is");
        set.add("are");
        set.add("of");
        System.out.print("Initialized SingMessageClass");
        Thread counter = new AsyncCounter(this);
        counter.start();
    }
    
    public void countWords(String message){
        //System.out.print(message);
        StringTokenizer st = new StringTokenizer(message, " ");
        ConcurrentHashMap<String, Integer> wordsMap = new ConcurrentHashMap<>();
        while(st.hasMoreTokens()) {
            String word = st.nextToken();
            //System.out.println(word);
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
            System.out.println("Error at countWords" + e.getMessage());
        }
    }
    public synchronized String getTopN(Integer n) {
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
        //System.out.print("Got Top N");
        return topNResult;
    }
}
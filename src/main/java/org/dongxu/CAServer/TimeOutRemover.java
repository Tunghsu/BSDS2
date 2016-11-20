/**
 * Created by dongxu on 10/13/16.
 */
package org.dongxu.CAServer;

import org.dongxu.utils.log;

public class TimeOutRemover extends Thread implements Runnable{
    private CAServer pub;
    private log out = new log("TTL");
    private Integer totalCount=0;

    TimeOutRemover(CAServer PubServer){
        pub = PubServer;
    }
    public void run(){
        Integer count;
        while (true) {
            count = 0;
            try {
                sleep(100);
            } catch (InterruptedException e) { }
            long currentTimeStamp = System.currentTimeMillis();
            Object obj = pub.TimeOutQueue.peek();
            while (obj != null) {
                CAServer.TimeElement te = (CAServer.TimeElement)obj;
                long timeOutStamp = te.TimeStamp;
                if (timeOutStamp <= currentTimeStamp){
                    if (pub.GlobalToTopicConverter.containsKey(te.GlobalId)) {
                        totalCount++;
                        count++;
                    }
                    Boolean result = pub.MessageRemover(te.GlobalId);
                    if (result) {
                        out.println("#"+te.GlobalId.toString()+" been removed");
                    }
                    try {
                        pub.TimeOutQueue.take();
                    } catch (InterruptedException e) { }
                } else break;
                obj = pub.TimeOutQueue.peek();
            }
            if (count > 0) {
                out.println(count.toString() + " msgs removed this round");
                out.println("Totally " + totalCount.toString() + " msgs removed");
            }
        }
    }
}

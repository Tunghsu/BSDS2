package org.dongxu.utils;

/**
 * Created by dongxu on 10/16/16.
 */
public class log {
    public String name;

    public log(String str){
        name = str;
    }

    public void println (String text){
        text = "["+name+"]\t" + text;
        synchronized(this) {
            System.out.println(text);
        }
    }
}

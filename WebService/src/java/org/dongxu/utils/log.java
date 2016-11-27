/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.utils;

/**
 *
 * @author dongxu
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

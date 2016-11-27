/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.ejb;

/**
 *
 * @author dongxu
 */
public class BSDSMessage {
    private String title;
    private String message;
    private String source;
    private Integer publisherId;

    public BSDSMessage(String title, String message, String source, Integer id){
        this.title = title;
        this.message = message;
        this.source = source;
        this.setPublisherId(id);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Integer getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(Integer publisherId) {
        this.publisherId = publisherId;
    }
}


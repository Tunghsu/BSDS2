/**
 * Created by Ian Gortan on 9/20/2016.
 */
package org.dongxu.CAServer;

//contains data for message delivery to subscribers

public class BSDSMessage {
    private String title;
    private String message;
    private String source;

    BSDSMessage(String title, String message, String source){
        this.title = title;
        this.message = message;
        this.source = source;
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
}


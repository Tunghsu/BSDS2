package org.dongxu.rs.server;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;

import org.dongxu.CAClient.BSDSMessage;
import org.dongxu.CAServer.CAServer;
import javax.ws.rs.core.Response;

import org.dongxu.CAClient.BSDSMessage;
import org.json.JSONObject;
import org.json.JSONStringer;
import org.dongxu.utils.log;


// Plain old Java Object it does not extend as class or implements
// an interface

// The class registers its methods for the HTTP GET request using the @GET annotation.
// Using the @Produces annotation, it defines that it can deliver several MIME types,
// text, XML and HTML.

// The browser requests per default the HTML MIME type.

//Sets the path to base URL + /hello
@Path("/rest")
public class Server {
    private static CAServer caServer = new CAServer();
    private static log out = new log("Rest");

/*    Server(){
        *//*caServer = new CAServer();*//*
    }*/

    private static Integer it = 666;

    // This method is called if TEXT_PLAIN is request
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String sayPlainTextHello() {
        return "Hello Jersey " + it.toString();
    }

    // This method is called if XML is request
    @GET
    @Produces(MediaType.TEXT_XML)
    public String sayXMLHello() {
        return "<?xml version=\"1.0\"?>" + "<hello> Hello Jersey" + "</hello>";
    }

    // This method is called if HTML is request
    @GET
    @Produces(MediaType.TEXT_HTML)
    public String sayHtmlHello() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey " + it.toString() + "</body></h1>" + "</html> ";
    }

    @GET
    @Path("/regpublisher/{name}/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPublisher_rest(@PathParam("name") String name, @PathParam("topic") String topic){
        Integer res;

        res = caServer.registerPublisher(name, topic);

        JSONStringer json = new JSONStringer();
        json.object().key("id").value(res).endObject();
        System.out.println(json.toString());

        return Response.ok(json.toString()).build();
    }

    @GET
    @Path("/regsubscriber/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerSubscriber_rest(@PathParam("topic") String topic){
        Integer res;

        res = caServer.registerSubscriber(topic);

        JSONStringer json = new JSONStringer();
        json.object().key("id").value(res).endObject();
        System.out.println(json.toString());

        return Response.ok(json.toString()).build();
    }

    @GET
    @Path("/getlatestcontent/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLatestContent_rest(@PathParam("id") Integer subscriberID){
        String res;

        res = caServer.getLatestContent(subscriberID);

        JSONStringer json = new JSONStringer();
        json.object().key("message").value(res).endObject();
        System.out.println(json.toString());

        return Response.ok(json.toString()).build();
    }

    @GET
    @Path("/gettopn/{n}")
    @Produces(MediaType.TEXT_HTML)
    public Response getTopN_rest(@PathParam("n") Integer n){
        String res;

        res = caServer.getTopN(n);

/*        JSONStringer json = new JSONStringer();
        json.object().key("message").value(res).endObject();
        System.out.println(json.toString());*/

        return Response.ok(res).build();
    }

    @POST
    @Path("/publishcontent/")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    @Produces(MediaType.APPLICATION_JSON)
    public Response publishContent_rest(@FormParam("id") String publisherId,
                                        @FormParam("title") String title,
                                        @FormParam("message") String message){
        caServer.publishContent(Integer.parseInt(publisherId), title, message, 300000);
        caServer.countWords(message);
        out.println("content published");
        return Response.ok().build();

    }

}
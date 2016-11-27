/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.dongxu.ws;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.Consumes;
import javax.ws.rs.PathParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.JSONStringer;


import org.dongxu.ejb.CAServerSessionBeanRemote;
import org.dongxu.utils.log;



/**
 *
 * @author dongxu
 */
@Stateless
@LocalBean
@Path("/rest")
public class WebService {

    @EJB
    private CAServerSessionBeanRemote cAServerSessionBean = lookupCAServerSessionBeanRemote();
    private static log out = new log("Rest");
    
    public WebService(){
        System.out.print("Started");
    }
    
    @GET
    @Path("/regpublisher/{name}/{topic}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerPublisher_rest(@PathParam("name") String name, @PathParam("topic") String topic){
        Integer res;

        res = cAServerSessionBean.registerPublisher(name, topic);

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

        res = cAServerSessionBean.registerSubscriber(topic);

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

        res = cAServerSessionBean.getLatestContent(subscriberID);

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

        res = cAServerSessionBean.getTopN(n);

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
        cAServerSessionBean.addToContentBuffer(new org.dongxu.ejb.BSDSMessage(title, message, "", Integer.parseInt(publisherId)));
        out.println("content buffered for publishing");
        return Response.ok().build();

    }
    
    @GET
    public String sayHtmlHello() {
        return "<html> " + "<title>" + "Hello Jersey" + "</title>"
                + "<body><h1>" + "Hello Jersey " + "</body></h1>" + "</html> ";
    }


    private CAServerSessionBeanRemote lookupCAServerSessionBeanRemote() {
        try {
            Context c = new InitialContext();
            return (CAServerSessionBeanRemote) c.lookup("java:global/Server_2/CAServerSessionBean!org.dongxu.ejb.CAServerSessionBeanRemote");
        } catch (NamingException ne) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, "exception caught", ne);
            throw new RuntimeException(ne);
        }
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WSpkg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javax.faces.bean.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import phonedirdao.PhoneDirectoryDAO;
import phonedirdao.PhoneDirectoryEntry;
import phonedirdao.Link;

/**
 * REST Web Service
 *
 * @author DJ
 */
@RequestScoped
public class ContactResource {

    @Context
    private UriInfo context;

    private String phoneNo;
    PhoneDirectoryDAO phoneDirectoryDAO = new PhoneDirectoryDAO();

    private ContactResource(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public static ContactResource getInstance(String phoneNo) {
        return new ContactResource(phoneNo);
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    public Response getXmlOrJson(@HeaderParam("If-Modified-Since") String ifModifiedSince) throws Exception {
        boolean recordExist = phoneDirectoryDAO.checkPhoneNoExist(phoneNo);

        if (recordExist) { // If the Record Exists
            PhoneDirectoryEntry entry = phoneDirectoryDAO.getPhoneNumberDetails(phoneNo);

            // get lastModified Timestamp from Database & convert to Date object with proper formatting
            Timestamp lastModifiedTS = phoneDirectoryDAO.getLastModifiedDate(phoneNo);
            long milliseconds = lastModifiedTS.getTime() + (lastModifiedTS.getNanos() / 1000000);
            Date lastModifiedDate = new Date(milliseconds);

            // build hypermedia links
            entry.setLink(new ArrayList<Link>());
            Link linkSelf = new Link();
            linkSelf.setRel("self");
            linkSelf.setHref("/" + phoneNo);
            Link deleteSelf = new Link();
            deleteSelf.setRel("/linkrel/contact-details/delete");
            deleteSelf.setHref("/" + phoneNo);
            Link updateSelf = new Link();
            updateSelf.setRel("/linkrel/contact-details/update");
            updateSelf.setHref("/" + phoneNo);

            entry.getLink().add(linkSelf);
            entry.getLink().add(deleteSelf);
            entry.getLink().add(updateSelf);

            // Conditional GET
            if (ifModifiedSince != null) { // If-Modified-Since Header is present in the Request 
                // convert "If-Modified-Since" String to a Date Object
                DateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz");
                Date ifModifiedSinceDate = df.parse(ifModifiedSince); // now we have two date objects
                if (ifModifiedSinceDate.after(lastModifiedDate)) { 
                    return Response
                            .status(Response.Status.OK) // Return OK Status
                            .header(phoneNo, entry)
                            .lastModified(lastModifiedDate)
                            .entity(entry)
                            .build();
                } else {
                    return Response
                            .noContent() // No Message Body will be returned
                            .status(Response.Status.NOT_MODIFIED) // Return Not Modified Status
                            .header(phoneNo, entry)
                            .lastModified(lastModifiedDate)
                            .build();
                }
            } else { // If-Modified-Since Header was not present in the Request 
                return Response
                        .status(Response.Status.OK) // return message body with Last Modified Date Header
                        .header(phoneNo, entry)
                        .lastModified(lastModifiedDate)
                        .entity(entry)
                        .build();
            }
        } else { // Phone Number does not exist
            return Response
                    .status(Response.Status.NOT_FOUND) // 404 Not Found
                    .entity("<PhoneNumber_Not_Found - PhoneNumber = '" + phoneNo + "'>")
                    .build();
        }
    }

    @PUT //Update
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    public Response putXmlOrJson(PhoneDirectoryEntry myEntry) {
        String phoneNo = myEntry.getPhoneNo();
        boolean recordExist = phoneDirectoryDAO.checkPhoneNoExist(phoneNo);

        myEntry.setLink(new ArrayList<Link>());
        Link linkSelf = new Link();
        linkSelf.setRel("self");
        linkSelf.setHref("/" + phoneNo);
        Link deleteSelf = new Link();
        deleteSelf.setRel("/linkrel/contact-details/delete");
        deleteSelf.setHref("/" + phoneNo);
        Link updateSelf = new Link();
        updateSelf.setRel("/linkrel/contact-details/update");
        updateSelf.setHref("/" + phoneNo);

        myEntry.getLink().add(linkSelf);
        myEntry.getLink().add(deleteSelf);
        myEntry.getLink().add(updateSelf);

        if (recordExist == true) {
            phoneDirectoryDAO.updatePhoneDetails(myEntry);

            return Response
                    .status(Response.Status.OK)
                    .entity(myEntry)
                    .header(phoneNo, myEntry)
                    .build();
        } else if (recordExist != true) {
            phoneDirectoryDAO.addPhoneNumber(myEntry);

            return Response
                    .status(Response.Status.CREATED)
                    .entity(myEntry)
                    .header(phoneNo, myEntry)
                    .build();
        } else { // unforseen error occured        
            return Response
                    .noContent()
                    .status(Response.Status.NOT_IMPLEMENTED)
                    .header(phoneNo, myEntry)
                    .build();
        }
    }

    @DELETE
    public Response deletePhoneNumber(@PathParam("phoneNo") String phoneNo) {
        boolean recordExist = phoneDirectoryDAO.checkPhoneNoExist(phoneNo);
        if (recordExist == true) {
            phoneDirectoryDAO.deletePhoneNumber(phoneNo);
            return Response
                    .noContent()
                    .status(Response.Status.NO_CONTENT)
                    .build();
        } else {
            return Response
                    .status(Response.Status.NOT_FOUND) 
                    .entity("<PhoneNumber_Not_Found - PhoneNumber = '" + phoneNo + "'>")
                    .build();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package WSpkg;

import phonedirdao.PhoneDirectoryEntry;
import phonedirdao.PhoneDirectoryDAO;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.POST;
import javax.ws.rs.PathParam;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * REST Web Service
 *
 * @author DJ
 */
@Path("/phoneRWS")
public class ContactCollectionResource {

    @Context
    private UriInfo context;
    PhoneDirectoryDAO phoneDirectoryDAO = new PhoneDirectoryDAO();

    public ContactCollectionResource() {
    }

    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    public ArrayList<PhoneDirectoryEntry> getXml() {
        ArrayList<PhoneDirectoryEntry> myEntries = new ArrayList<>();
        myEntries = phoneDirectoryDAO.getAllNumbers();
        return myEntries;
    }

    /**
     * POST method for creating an instance of ContactResource
     *
     * @param content representation for the new resource
     * @return an HTTP response with content of the created resource
     */
    @POST // Create
    @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON, MediaType.TEXT_PLAIN, MediaType.TEXT_HTML})
    public Response postXmlOrJson(PhoneDirectoryEntry myEntry) {
        String phoneNo = myEntry.getPhoneNo();
        boolean recordExist = phoneDirectoryDAO.checkPhoneNoExist(phoneNo);

        if (recordExist == true) {
            return Response
                    .status(Response.Status.CONFLICT) // .CONFLICT
                    .header("Location ",
                            String.format("%s%s",
                                    context.getAbsolutePath().toString(),
                                    myEntry.getPhoneNo()))
                    .entity("<PhoneNumber_AlreadyExists PhoneNumber = '" + phoneNo + "'>")
                    .build();
        } else {
            phoneDirectoryDAO.addPhoneNumber(myEntry);
            
            return Response
                    .status(Response.Status.CREATED)
                    .header("Location ",
                            String.format("%s%s",
                                    context.getAbsolutePath().toString(),
                                    myEntry.getPhoneNo()))
                    .entity(myEntry)
                    .build();
        }
    }

    @DELETE
    public Response deleteAllAccounts() {
        phoneDirectoryDAO.deleteAllNumbers();

        return Response
                .noContent()
                .status(Response.Status.NO_CONTENT)
                .build();
    }

    @HEAD
    public Response doTheHeadVerb() {
        return Response
                .noContent()
                .status(Response.Status.OK)
                .build();
    }

    @OPTIONS
    public Response doOptions() {
        Set<String> allowedVerbs = new TreeSet<>();
        allowedVerbs.add("GET");
        allowedVerbs.add("POST");
        allowedVerbs.add("DELETE");
        allowedVerbs.add("HEAD");

        return Response
                .noContent()
                .status(Response.Status.OK)
                .allow(allowedVerbs)
                .build();
    }

    /**
     * Sub-resource locator method for {phoneNo}
     */
    @Path("{phoneNo}")
    public ContactResource getContactResource(@PathParam("phoneNo") String phoneNo) {
        return ContactResource.getInstance(phoneNo);
    }
}

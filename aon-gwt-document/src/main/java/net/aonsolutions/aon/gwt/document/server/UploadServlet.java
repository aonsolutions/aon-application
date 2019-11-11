package net.aonsolutions.aon.gwt.document.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.attachment.RegistryAttachmentType;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.api.services.drive.Drive;

import net.aonsolutions.aon.google.apis.drive.AonDrive;

@SuppressWarnings("serial")
@WebServlet(name = "Upload Documental Filesx", urlPatterns = {"/aon_gwt_aio/ms/uploadDocumentalx/*"})
public class UploadServlet extends HttpServlet{
	
	 private static final String UPLOAD_DIRECTORY = "upload";
	 // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 16; // 16MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
    
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Object object = new JSONObject();

		String domainName = req.getParameter("domain_name");
		String domain_id = req.getParameter("domain_id");
		Integer domainId = Integer.parseInt(domain_id);
		String login = req.getParameter("login");
		String categoryStr = req.getParameter("category");
		Integer category = !"".equals(categoryStr) ?  Integer.parseInt(categoryStr) : null;
		String tagStr = req.getParameter("tag");
		String[] tags = tagStr.substring(0, tagStr.length()).split(",");
		String scopeStr = req.getParameter("scope");
		Integer scope = !"".equals(scopeStr) ? Integer.parseInt(scopeStr) : null;
		Boolean confidential = "true".equalsIgnoreCase(req.getParameter("confidential"));

		
		  // checks if the request actually contains upload file
        if (!ServletFileUpload.isMultipartContent(req)) {
            // if not, we stop here
            PrintWriter writer = resp.getWriter();
            writer.println("Error: Form must has enctype=multipart/form-data.");
            writer.flush();
            return;
        }
 
        // configures upload settings
        DiskFileItemFactory factory = new DiskFileItemFactory();
        // sets memory threshold - beyond which files are stored in disk
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        // sets temporary location to store files
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
         
        // sets maximum size of upload file
        upload.setFileSizeMax(MAX_FILE_SIZE);
         
        // sets maximum size of request (include file + form data)
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        // constructs the directory path to store upload file
        // this path is relative to application's directory
        String uploadPath = getServletContext().getRealPath("")
                + File.separator + UPLOAD_DIRECTORY;
         
        // creates the directory if it does not exist
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }
 
        try {
            // parses the request's content to extract file data
            List<FileItem> formItems = upload.parseRequest(req);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                    	Domain domain = AON.getDomain(domainName, domainId, login);
                    	Attach attach = new Attach()
                    			.setAttachModule(1) // TODO
                    			.setAttachType(AttachType.REGISTRY)
                    			.setData(item.get())
                    			.setDescription(item.getName())
                    			.setMimeType(MimeType.get(item.getContentType()))
                    			.setDomain(domain)
                    			.setType(RegistryAttachmentType.CORPORATE_IDENTITY.value())
                    			.setCategory(category)
                    			.setDate(new Date())
                    			.setScope(scope)
                    			.setConfidential(confidential)
                    			.setDparentId(Long.toString(item.getSize()));
                    	Integer attachId = AON.insertAttach(domain.getName(), domain.getId(), login, attach);
                   	
                    	for(Integer i = 0 ; i < tags.length ; i++) {
                    		if(!"".equals(tags[i])){
                    			Integer tagId = Integer.parseInt(tags[i]);
                    			AON.insertRegistryAttachTag(domain.getName(), domain.getId(), login, attachId, tagId);
                    		}
                    	}
                   	
                    	attach.setId(attachId);
            			                   	
                    	DomainGserviceaccount d = AON.getDomainGserviceaccount(domainName, domainId, login);
                    	Drive drive = AonDrive.getInstace().serviceInitialize(d);
                    	User user = AON.getUser(domainName, domainId, login);
                    	AonDrive.getInstace().sync(drive, user, attach, false);
                    	SendNotification.sendGmail(domain, user, attach, true);
                    }
                }
            }
        } catch (Exception ex) {
            req.setAttribute("message",
                    "There was an error: " + ex.getMessage());
        }
        
		resp.setContentType("application/json;charset=UTF-8");
		resp.addHeader("Access-Control-Allow-Origin", "*");
	    resp.addHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, PUT, DELETE, HEAD");
	    resp.addHeader("Access-Control-Allow-Headers", "X-PINGOTHER, Origin, X-Requested-With, Content-Type, Accept");
	    resp.addHeader("Access-Control-Max-Age", "1728000");
	
	    
		PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
		os.println(object.toString());
		os.flush();
		os.close();
       
	}
	
}

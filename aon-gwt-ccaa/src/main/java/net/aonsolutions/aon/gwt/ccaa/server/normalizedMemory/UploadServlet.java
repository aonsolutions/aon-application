package net.aonsolutions.aon.gwt.ccaa.server.normalizedMemory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.json.JSONObject;

import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.DBConsults;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Esquema;
import com.esferalia.aon.occam.impl.jooq.dao.d2_deposit.Utils;

@SuppressWarnings("serial")
@WebServlet(name = "Upload D2 IMPORT", urlPatterns = {"/aon_gwt_aio/uploadD2/*"})
public class UploadServlet extends HttpServlet{
	
	 private static final String UPLOAD_DIRECTORY = "upload";
	 // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
    
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Object object = new JSONObject();

		String domainName = req.getParameter("domain_name");
		String domain_id = req.getParameter("domain_id");
		Integer domainId = Integer.parseInt(domain_id);
		String login = req.getParameter("login");
		String y = req.getParameter("year");
		Integer year = Integer.parseInt(y);
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
                    	Esquema schema = Utils.readXml(item.get());	
                    	try {
                			byte[] b = Utils.writeXml(schema);
                			DBConsults.insertDeposit(domainName, b, domainId, year, login);
                			DBConsults.insertDeposit(domainName, b, domainId, year, login);
                		} catch (JAXBException | IOException e) {
                			e.printStackTrace();
                		}
                    /*
                    	Attach attach = new Attach()
                    			.setAttachModule(1) // TODO
                    			.setAttachType(AttachType.REGISTRY)
                    			.setData(item.get())
                    			.setDescription(item.getName())
                    			.setMimeType(MimeType.get(item.getContentType()))
                    			.setDomain(new Domain().setId(domainId).setName(domainName))
                    			.setType(RegistryAttachmentType.CORPORATE_IDENTITY.value())
                    			.setCategory(category)
                    			.setDate(new Date())
                    			.setScope(scope)
                    			.setConfidential(confidential)
                    			.setDparentId(Long.toString(item.getSize()));
                    	Integer attachId = AON.insertAttach(domainName, domainId, login, attach);
                    	
                    	AON.insertRegistryAttachTag(domainName, domainId, login, attachId, tag);
                   	*/
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

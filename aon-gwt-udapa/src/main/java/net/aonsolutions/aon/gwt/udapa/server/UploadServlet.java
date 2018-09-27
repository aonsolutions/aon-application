package net.aonsolutions.aon.gwt.udapa.server;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.List;

import javax.imageio.ImageIO;
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
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
@WebServlet(name = "UploadImages", urlPatterns = {"/aon_gwt_aio/uploadImages/*"})
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
		String idStr = req.getParameter("id");
		Integer id = Integer.parseInt(idStr); 
		String attach_type = req.getParameter("attach_type");
		AttachType attachType = AttachType.getAttachType(attach_type);
		
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
            @SuppressWarnings("unchecked")
            List<FileItem> formItems = upload.parseRequest(req);
 
            if (formItems != null && formItems.size() > 0) {
                // iterates over form's fields
                for (FileItem item : formItems) {
                	byte[] bb = reduceImage(item);
                	
                    // processes only fields that are not form fields
                    if (!item.isFormField()) {
                    	Attach attach = new Attach()
                    			.setAttachModule(id)
                    			.setAttachType(attachType)
                    			.setData(bb)
                    			.setDescription(item.getName())
                    			.setMimeType(MimeType.get(item.getContentType()))
                    			.setDomain(new Domain().setId(domainId).setName(domainName))
                    			.setType((byte)0)
                    			.setSourceType((byte)0)
                    			.setSourceBatch(id);
                    	AON.insertAttach(domainName, domainId, login, attach);
                    	
                    	// TODO RESPONSE JsAttachment!!!! (json)
                    	
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
	
    public static int MAX_WIDTH=500;

    public static int MAX_HEIGHT=500;
	
	private byte[] reduceImage(FileItem item) {
		MimeType m = MimeType.get(item.getContentType());
		byte[] b = item.get();
		System.out.println("TAMAÑO ANTES: " + b.length);
		ByteArrayInputStream bis = new ByteArrayInputStream(b);
		try {
			BufferedImage bimage = ImageIO.read(bis);
			if(bimage.getHeight()>bimage.getWidth()){
	            int heigt = (bimage.getHeight() * MAX_WIDTH) / bimage.getWidth();
	            bimage = resize(bimage, MAX_WIDTH, heigt);
	            int width = (bimage.getWidth() * MAX_HEIGHT) / bimage.getHeight();
	            bimage = resize(bimage, width, MAX_HEIGHT);
	        }else{
	            int width = (bimage.getWidth() * MAX_HEIGHT) / bimage.getHeight();
	            bimage = resize(bimage, width, MAX_HEIGHT);
	            int heigt = (bimage.getHeight() * MAX_WIDTH) / bimage.getWidth();
	            bimage = resize(bimage, MAX_WIDTH, heigt);
	        }
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ImageIO.write(bimage,  m.getExtension(), bos );
			byte[] b2 = bos.toByteArray();
			System.out.println("TAMAÑO DESPUES: " + b2.length);
			return b2;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return b;
	}
	
    public static BufferedImage resize(BufferedImage bufferedImage, int newW, int newH) {
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        BufferedImage bufim = new BufferedImage(newW, newH, bufferedImage.getType());
        Graphics2D g = bufim.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(bufferedImage, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return bufim;
    }
}

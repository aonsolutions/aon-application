package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.esferalia.aon.occam.api.model.fiscal.mod200_2013.Mod2002013;
import com.esferalia.aon.occam.server.fiscal.format.mod200.Mod200Reader;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2013 BOE Upload ", urlPatterns = { "/aon_gwt_fiscal/Mod2002013BOEUpload" })
public class Mod2002013BOEUpload extends HttpServlet {

    // upload settings
    private static final int MEMORY_THRESHOLD   = 1024 * 1024 * 3;  // 3MB
    private static final int MAX_FILE_SIZE      = 1024 * 1024 * 40; // 40MB
    private static final int MAX_REQUEST_SIZE   = 1024 * 1024 * 50; // 50MB
    
    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
        if (!ServletFileUpload.isMultipartContent(request)) {
            response.getWriter().println("Error: Form must has enctype=multipart/form-data.");
            response.getWriter().flush();
            return;
        }
        DiskFileItemFactory factory = new DiskFileItemFactory();
        factory.setSizeThreshold(MEMORY_THRESHOLD);
        factory.setRepository(new File(System.getProperty("java.io.tmpdir")));
 
        ServletFileUpload upload = new ServletFileUpload(factory);
        upload.setFileSizeMax(MAX_FILE_SIZE);
        upload.setSizeMax(MAX_REQUEST_SIZE);
 
        try {
            List<FileItem> formItems = upload.parseRequest(request);
            
            if (formItems != null && formItems.size() > 0) {
                for (FileItem item : formItems) {
                    if (!item.isFormField()) {
                    	ByteArrayInputStream input = new ByteArrayInputStream(item.get());
                    	Mod2002013 mod100 = Mod200Reader.getMod2002013(input);
                    	System.out.println( mod100.toString() ); 
                    }
                }
            }
        } catch (FileUploadException ex) {
        	throw new ServletException(ex.getMessage(),ex); 
        }

    }

}

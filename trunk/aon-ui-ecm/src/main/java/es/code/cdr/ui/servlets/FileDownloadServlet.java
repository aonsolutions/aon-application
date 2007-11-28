/**
 * 
 */
package es.code.cdr.ui.servlets;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import javax.jcr.RepositoryException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import es.code.cdr.beans.Document;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 24/07/2007
 *
 */
public class FileDownloadServlet extends HttpServlet {

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doPost(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
		try {
			perform( req, response );
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse response) 
				throws ServletException, IOException {
		try {
			perform( req, response );
		} catch (RepositoryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void perform(HttpServletRequest req, HttpServletResponse response) 
				throws RepositoryException, IOException, ServletException {
//	Gets selected document.
		Document selected = (Document) req.getSession().getAttribute( req.getParameter("selected") );
//	Now we create some variables we will use for writting the file to the response
		int read = 0;
		byte[] bytes = new byte[1024];
//	Now set the content type for our response, be sure to use the best suitable content type depending on your file
//	the content type presented here is ok for, lets say, text files and others (like  CSVs, PDFs)
		response.setContentType( selected.getMimeType() );
		String fileName = selected.getName();
		String agent = req.getHeader("USER-AGENT");
        response.setHeader("Expires", "Sat, 6 May 1971 12:00:00 GMT");
        response.setHeader("Cache-Control", "max-age=0, must-revalidate");
        response.addHeader("Cache-Control", "post-check=0, pre-check=0");
        response.setHeader("Pragma", "no-cache");
        if(agent != null && -1 != agent.indexOf("MSIE")) {
            fileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", " ");
        } else if(agent != null && -1 != agent.indexOf("Mozilla")) {
//            fileName = MimeUtility.encodeText(fileName, "UTF-8", "B");
        }
		
//	This is another important attribute for the header of the response
//	Here fileName, is a String with the name that you will suggest as a name to save as
//	I use the same name as it is stored in the file system of the server.
		response.setHeader("Content-Disposition", "attachment;filename=\"" + fileName + "\"");		
//	Streams we will use to read, write the file bytes to our response
		InputStream is = null;
		OutputStream os = null;
//	First we load the file in our InputStream
		is = selected.getContent();
		os = response.getOutputStream();
//	While there are still bytes in the file, read them and write them to our OutputStream
		while((read = is.read(bytes)) != -1){
			os.write(bytes,0,read);
		}
//	Clean resources
		os.flush();
		os.close();
//	Removes selected document from current session.
//		req.getSession().removeAttribute( req.getParameter("selected") );
	}
}

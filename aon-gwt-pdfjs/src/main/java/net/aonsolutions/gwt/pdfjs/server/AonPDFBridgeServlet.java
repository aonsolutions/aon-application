package net.aonsolutions.gwt.pdfjs.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "AonPDFBridgeServlet", urlPatterns = { "/aon_gwt_aio/ms/AonPDFBridgeServlet/*",
		"/aon_gwt_fiscal/ms/AonPDFBridgeServlet/*" })
public class AonPDFBridgeServlet extends HttpServlet {

	private static final long serialVersionUID = -5441304759185014838L;
	private static final Logger LOGGER = Logger.getLogger(AonPDFBridgeServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String qs = req.getQueryString();
		String url = AonStringUtils.removeStart(qs,"URL=");
		if (url != null) {
			InputStream in = null;
			try {
				URL u = new URL(url);
				LOGGER.info("Attemp connect to [" + u + "]");
				URLConnection conn = u.openConnection();
				conn.connect();
				in = conn.getInputStream();
		        resp.addHeader("Content-Disposition","attachment; filename=\"" + u.getFile() +"\"");
		        resp.setContentType("application/pdf");
				AonIOUtils.copy(in, resp.getOutputStream()) ;
			} catch (Throwable t) {
				t.printStackTrace();
				throw t;
			} finally {
				AonIOUtils.closeQuietly(in);
			}
		}
		
	}

}

package net.aonsolutions.aon.api.servlet;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.json.JSONObject;

@SuppressWarnings("serial")
@WebServlet(name = "ManifestServlet", urlPatterns = {"/ms/api/manifest/*"})
public class ManifestServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ManifestServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("AON API INVOICE SERVLET - GET METHOD");
		try {
			InputStream in = this.getServletContext().getResourceAsStream("META-INF/MANIFEST.MF");
			
			Manifest m = new Manifest(in);
			Attributes attrs = m.getMainAttributes();

			JSONObject json = new JSONObject();
//			for (Object k : attrs.keySet()) {
//				if(k != null) {
//					String key = k.toString();
//					String value = StringUtils.trimToNull(attrs.getValue(key));
//					json.put(key, value);
//				}
//			}
			String buildDate = StringUtils.trimToNull(attrs.getValue("buildDate"));
			json.put("build_date", buildDate);
			String origin = req.getHeader("access-control-allow-origin") != null ? req.getHeader("access-control-allow-origin") : "*";
	    	resp.addHeader("Access-Control-Allow-Origin", origin);
			Utils.giveBack(req, resp, json, new JSONObject());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - POST METHOD");
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("AON API INVOICE SERVLET - DELETE METHOD");
	}
	

}

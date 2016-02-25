package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod202;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.mod202.Mod202Writer;

@SuppressWarnings("serial")
@WebServlet(name = "Mod202 File download", urlPatterns = { "/aon_gwt_fiscal/Model202File" })
public class Mod202File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("modId"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod202 mod202 = FISCAL.getMod202(domainName, domainId,AonServletUtils.getLoggedUser(), id);
			
			String s = mod202.getName();
		    StringBuilder sb = new StringBuilder();
		    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
		        sb.append("_");
		    }
		    for (char c : s.toCharArray()) {
		        if(Character.isJavaIdentifierPart(c)) {
		            sb.append(c);
		        }
		    }		

		    String fileName = "Mod202" 
					+ "_" + mod202.getYear() 
					+ "_" + sb.toString();
			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");

			OutputStreamWriter writer = new OutputStreamWriter( resp.getOutputStream() , "ISO-8859-15" );
			Mod202Writer.fill(writer, mod202);
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

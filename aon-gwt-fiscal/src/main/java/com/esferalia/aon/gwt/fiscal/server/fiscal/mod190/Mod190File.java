package com.esferalia.aon.gwt.fiscal.server.fiscal.mod190;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.fiscal.MODEL190;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.mod190.Mod190Writer;
import com.esferalia.aon.watson.server.AonEnumUtils;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod190 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model190File" })
public class Mod190File extends HttpServlet {

	private static final long serialVersionUID = 3686887459432670409L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("modelID"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			String boeFormatParam = req.getParameter("boeFormat");
			boolean boeFormat = AonEnumUtils.getAonBoolean(boeFormatParam);
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod190 mod190 = MODEL190.get(occam, id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod190Writer.fillWriter(mod190, writer, boeFormat);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String s = mod190.getName();
		    StringBuilder sb = new StringBuilder();
		    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
		        sb.append("_");
		    }
		    for (char c : s.toCharArray()) {
		        if(Character.isJavaIdentifierPart(c)) {
		            sb.append(c);
		        }
		    }		
			
		    String fileName = "Mod190" 
					+ "_" + mod190.getYear() 
					+ "_" + sb.toString();
			
		    resp.setCharacterEncoding("ISO-8859-1");
			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

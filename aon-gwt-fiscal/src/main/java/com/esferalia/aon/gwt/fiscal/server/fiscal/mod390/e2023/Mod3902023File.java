package com.esferalia.aon.gwt.fiscal.server.fiscal.mod390.e2023;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.esferalia.aon.occam.api.fiscal.MODEL3902023;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.mod390.Mod3902023;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.mod390.Mod3902023Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "Mod390 2023 File download", urlPatterns = { "/aon_gwt_fiscal/Model3902023File" })
public class Mod3902023File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("modelID"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
					.setDomainName(domainName)
					.setDomain(domainId)
					.setUser(user);			
			Mod3902023 mod390 = MODEL3902023.get(occam, id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod3902023Writer.fillWriter(mod390, writer);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());

			
			String s = mod390.getName();
			if (mod390.isLegalEntity()) {
				s = mod390.getName();
			} else {
				s = AonStringUtils.defaultIfBlank(mod390.getName(),"") 
						+ " " + AonStringUtils.defaultIfBlank(mod390.getFirstSurname(),"") 
						+ " " + AonStringUtils.defaultIfBlank(mod390.getSecondSurname(),"");
			}
			
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

		    String fileName = "Mod390" 
					+ "_" + mod390.getYear() 
					+ "_" + sb.toString();
			
			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

}

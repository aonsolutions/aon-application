package com.esferalia.aon.gwt.fiscal.server.fiscal.mod425.e2025;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import com.esferalia.aon.occam.api.fiscal.MODEL4252025;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.mod425.Mod4252025;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "Mod425 2025 File download", urlPatterns = { "/aon_gwt_fiscal/Model4252025File" })
public class Mod4252025File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		// FALTA - HACERLO IGUAL QUE SE HACE PARA EL 420/417, PERO AHORA SE PUEDE LEER DIRECTAMENTE EL XML SI ES NECESARIO

		try {
			int id = Integer.parseInt(req.getParameter("modelID"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
					.setDomainName(domainName)
					.setDomain(domainId)
					.setUser(user);			
			Mod4252025 mod425 = MODEL4252025.get(occam, id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			// NO ES NECESARIO WRITER SE PASARA DIRECTAMENTE EL FICHERO GUARDADO AL MODULO DE LA ATC
//			Mod3902024Writer.fillWriter(mod425, writer);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String s = mod425.getName();
			if (mod425.isLegalEntity()) {
				s = mod425.getName();
			} else {
				s = AonStringUtils.defaultIfBlank(mod425.getName(),"") 
						+ " " + AonStringUtils.defaultIfBlank(mod425.getFirstSurname(),"") 
						+ " " + AonStringUtils.defaultIfBlank(mod425.getSecondSurname(),"");
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

		    String fileName = "Mod425" 
					+ "_" + mod425.getYear() 
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

package com.esferalia.aon.gwt.fiscal.server.fiscal.mod349;

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

import com.esferalia.aon.occam.api.fiscal.MODEL349;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod349;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod349Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod349 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model349File" })
public class Mod349File extends HttpServlet {

	private static final long serialVersionUID = 5156577156936767723L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("modelID"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Occam occam = new Occam()
					.setDomainName(domainName)
					.setDomain(domainId)
					.setUser(user);
			Mod349 mod349 = MODEL349.get(occam,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod349Writer.fillWriter(mod349, writer);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
			String s = mod349.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}
			 
			String fileName = AonFiscalFileUtils.getFileName(mod349);
			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

}

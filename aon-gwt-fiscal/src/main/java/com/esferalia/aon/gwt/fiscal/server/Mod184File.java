package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod184;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.Mod184Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod184 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model184File" })
public class Mod184File extends HttpServlet {

	private static final long serialVersionUID = -7210842204447778487L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod184"));
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod184 mod184 = FISCAL.getMod184(domainName, domainId,user, id);
			
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod184Writer.fillWriter(mod184, writer);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());

			String s = mod184.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}

			String fileName = "Mod184" + "_" + mod184.getYear() + "_"
					+ sb.toString();

			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".txt\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}
	}

}

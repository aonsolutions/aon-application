package com.esferalia.aon.gwt.mod200.server.e2020;

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

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.mod200.api.MODEL2002020;
import com.esferalia.aon.occam.mod200.api.model.mod200_2020.Mod2002020;
import com.esferalia.aon.occam.mod200.server.format.mod200_2020.Mod2002020Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2020 File download", urlPatterns = { "/aon_gwt_mod200/ms/Model2002020File" })
public class Mod2002020File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("modId"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam().setDomainName(domainName)
									.setDomain(domainId)
									.setUser(user);
			Mod2002020 mod200 = MODEL2002020.getMod2002020ById(occam,id);
			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod2002020Writer.fillWriter(mod200, writer);

			String s = mod200.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}
			String fileName = "Mod200" + "_" + mod200.getYear() + "_" + sb.toString();
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

}

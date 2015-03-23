package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod390;

@SuppressWarnings("serial")
@WebServlet(name = "Mod390 File download", urlPatterns = { "/aon_gwt_fiscal/Model390File" })
public class Mod390File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("mod390"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod390 mod390 = AON.getMod390(domainName, domainId, id);
			String content = AON.getMod390XML(domainName, domainId, id);

			String s = mod390.getName();
			StringBuilder sb = new StringBuilder();
			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
				sb.append("_");
			}
			for (char c : s.toCharArray()) {
				if (Character.isJavaIdentifierPart(c)) {
					sb.append(c);
				}
			}
			String fileName = "Mod390" + "_" + mod390.getYear() + "_" + sb.toString();
			if (content != null) {
				resp.setContentType("text/xml");
				resp.setCharacterEncoding("ISO-8859-1");
				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xml\";");
				resp.getWriter().print(content);
			}
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}

}

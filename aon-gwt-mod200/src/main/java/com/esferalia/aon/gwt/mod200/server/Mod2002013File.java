package com.esferalia.aon.gwt.mod200.server;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

//@SuppressWarnings("serial")
//@WebServlet(name = "Mod200 - 2013 File download", urlPatterns = { "/aon_gwt_mod200/Model2002013File" })
public class Mod2002013File extends HttpServlet {

	private static final long serialVersionUID = 8015131222923744056L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

//		try {
//			int id = Integer.parseInt(req.getParameter("modId"));
//			String domainName = req.getParameter("domainName");
//			int domainId = Integer.parseInt(req.getParameter("domainId"));
//			Mod2002013 mod200 = AON.getMod2002013ById(domainName,domainId,id);
//			MOD2002013Writer writer = new MOD2002013Writer();
//			FileOutput fileoutput = writer.createMOD200(mod200);
//			String s = mod200.getEnterpriseName();
//			StringBuilder sb = new StringBuilder();
//			if (!Character.isJavaIdentifierStart(s.charAt(0))) {
//				sb.append("_");
//			}
//			for (char c : s.toCharArray()) {
//				if (Character.isJavaIdentifierPart(c)) {
//					sb.append(c);
//				}
//			}
//			String fileName = "Mod200" + "_" + mod200.getYear() + "_" + sb.toString();
//			ByteArrayInputStream in = new ByteArrayInputStream(fileoutput.getContent());
//			resp.setContentType(MimeType.TXT.getName());
//			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
//			AonIOUtils.copy(in, resp.getOutputStream());
//			resp.flushBuffer();
//		} catch (Throwable e) {
//			throw new ServletException(e);
//		}

	}

}

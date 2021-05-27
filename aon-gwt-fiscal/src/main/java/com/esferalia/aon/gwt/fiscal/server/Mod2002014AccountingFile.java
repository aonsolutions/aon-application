package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.mod200_2014.Mod2002014;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.MOD2002014;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2014.jaxb.Mod2002014toMOD2002014;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2014 Accounting File", urlPatterns = { "/aon_gwt_fiscal/Model2002014AccountingFile" })
public class Mod2002014AccountingFile extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("modId"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod2002014 mod200 = FISCAL.getMod2002014ById(domainName,domainId,AonServletUtils.getLoggedUser(),id);

			MOD2002014 mod = Mod2002014toMOD2002014.getMOD2002014(mod200);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(MOD2002014.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			um.marshal(mod,writer);
			String content = writer.toString();
			
			String s = mod200.getEnterpriseName();
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
			if (content != null) {
				resp.setContentType(MimeType.XML.getName());
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

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
import com.esferalia.aon.occam.api.model.fiscal.mod200_2017.Mod2002017;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2017.jaxb.MOD2002017;
import com.esferalia.aon.occam.impl.jooq.dao.mod200_2017.jaxb.Mod2002017toMOD2002017;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2017 Accounting File", urlPatterns = { "/aon_gwt_fiscal/Model2002017AccountingFile" })
public class Mod2002017AccountingFile extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			int id = Integer.parseInt(req.getParameter("modId"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod2002017 mod200 = FISCAL.getMod2002017ById(domainName,domainId,AonServletUtils.getLoggedUser(),id);

			MOD2002017 mod = Mod2002017toMOD2002017.getMOD2002017(mod200);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(MOD2002017.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			um.marshal(mod,writer);
			String content = writer.toString();
			
		    String fileName = AonFiscalFileUtils.getFileName(mod200);
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

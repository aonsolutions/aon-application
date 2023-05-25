package com.esferalia.aon.gwt.mod200.server.e2022;

import java.io.IOException;
import java.io.StringWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.mod200.api.MODEL2002022;
import com.esferalia.aon.occam.mod200.api.model.mod200_2022.Mod2002022;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022.jaxb.MOD2002022;
import com.esferalia.aon.occam.mod200.impl.jooq.dao.mod200_2022.jaxb.Mod2002022toMOD2002022;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 - 2022 Accounting File", urlPatterns = { "/aon_gwt_mod200/ms/Model2002022AccountingFile" })
public class Mod2002022AccountingFile extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			//int id = Integer.parseInt(req.getParameter("modId"));
			int id = Integer.parseInt(req.getParameter("modelID"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam().setDomainName(domainName)
									.setDomain(domainId)
									.setUser(user);
			Mod2002022 mod200 = MODEL2002022.getMod2002022ById(occam,id);

			MOD2002022 mod = Mod2002022toMOD2002022.getMOD2002022(mod200);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(MOD2002022.class);
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

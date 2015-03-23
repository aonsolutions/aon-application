package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.server.mod200.xml.MOD2002013;
import com.esferalia.aon.gwt.fiscal.server.mod200.xml.Mod200toMOD2002013;
import com.esferalia.aon.gwt.fiscal.shared.mod200.Mod200;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod200;

@SuppressWarnings("serial")
@WebServlet(name = "Mod200 Accounting File", urlPatterns = { "/aon_gwt_fiscal/Model200AccountingFile" })
public class Mod200AccountingFile extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			int id = Integer.parseInt(req.getParameter("mod200"));
			Mod200 mod200 = SQLMod200.getById(id, conn);

			MOD2002013 mod = Mod200toMOD2002013.getMOD2002013(mod200);
			StringWriter writer = new StringWriter();
			JAXBContext context = JAXBContext.newInstance(MOD2002013.class);
			Marshaller um = context.createMarshaller();
			um.setProperty("jaxb.encoding", "ISO-8859-1");
			um.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE );
			um.marshal(mod,writer);
			String content = writer.toString();
			
			commit(conn);

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
				resp.setContentType("text/xml");
				resp.setCharacterEncoding("ISO-8859-1");
				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xml\";");
				resp.getWriter().print(content);
			}
			resp.flushBuffer();
		} catch (Throwable e) {
			rollback(conn);
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}

	}

}

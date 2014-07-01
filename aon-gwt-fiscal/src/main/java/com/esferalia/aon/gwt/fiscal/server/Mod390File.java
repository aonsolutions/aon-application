package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.shared.Mod390;
import com.esferalia.aon.gwt.fiscal.sql.SQLMod390;

@SuppressWarnings("serial")
@WebServlet(name = "Mod390 File download", urlPatterns = { "/aon_gwt_fiscal/Model390File" })
public class Mod390File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			int id = Integer.parseInt(req.getParameter("mod390"));
			Mod390 mod390 = SQLMod390.getById(id, conn);
			String content = SQLMod390.getXMLContentById(id, conn);
			commit(conn);

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
				resp.setContentType(MimeType.MIME_XML.getName());
				resp.setCharacterEncoding("ISO-8859-1");
				resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xml\";");
				resp.getWriter().print(content);
			}
			resp.flushBuffer();
		} catch (AonSQLException e) {
			rollback(conn);
			throw new ServletException(e);
		} catch (Throwable e) {
			rollback(conn);
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			SQLUtils.closeQuietly(conn);
		}

	}

}

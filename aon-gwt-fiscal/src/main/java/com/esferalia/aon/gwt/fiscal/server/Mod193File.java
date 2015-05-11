package com.esferalia.aon.gwt.fiscal.server;

import static com.esferalia.aon.gwt.common.server.AonServletUtils.commit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.disableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.enableAutoCommit;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.getConnection;
import static com.esferalia.aon.gwt.common.server.AonServletUtils.rollback;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.fiscal.server.file.MOD193Writer;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod193;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDatabaseUtil;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Mod193 File download", urlPatterns = { "/aon_gwt_fiscal/Model193File" })
public class Mod193File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			MOD193Writer writer = new MOD193Writer();
			int id = Integer.parseInt(req.getParameter("mod193"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod193 mod193 = AON.getMod193(domainName, domainId, id);

			FileOutput fileoutput = writer.createMOD193(domainName,domainId, id,
					mod193.getYear(), mod193.getAdministration());
			commit(conn);
			
			String s = mod193.getName();
		    StringBuilder sb = new StringBuilder();
		    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
		        sb.append("_");
		    }
		    for (char c : s.toCharArray()) {
		        if(Character.isJavaIdentifierPart(c)) {
		            sb.append(c);
		        }
		    }		
			
		    String fileName = "Mod193" 
					+ "_" + mod193.getYear() 
					+ "_" + sb.toString();
			
			
			ByteArrayInputStream in = new ByteArrayInputStream(fileoutput.getContent());
			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (AonSQLException e) {
			rollback(conn);
			throw new ServletException(e);
		} catch (Throwable e) {
			rollback(conn);
			throw new ServletException(e);
		} finally {
			enableAutoCommit(conn);
			AonDatabaseUtil.closeQuietly(conn);
		}

	}
}

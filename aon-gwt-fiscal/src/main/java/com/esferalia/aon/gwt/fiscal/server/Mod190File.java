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

import org.apache.commons.io.IOUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.file.format.output.FileOutput;
import com.esferalia.aon.gwt.common.shared.AonSQLException;
import com.esferalia.aon.gwt.common.sql.SQLUtils;
import com.esferalia.aon.gwt.fiscal.server.file.MOD190Writer;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.fiscal.Mod190;

@SuppressWarnings("serial")
@WebServlet(name = "Mod190 File download", urlPatterns = { "/aon_gwt_fiscal/Model190File" })
public class Mod190File extends HttpServlet {

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		Connection conn = null;
		try {
			conn = getConnection();
			disableAutoCommit(conn);
			MOD190Writer writer = new MOD190Writer();
			int id = Integer.parseInt(req.getParameter("mod190"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			Mod190 mod190 = AON.getMod190(domainName, domainId, id);

			FileOutput fileoutput = writer.createMOD190(domainName,domainId, id,
					mod190.getYear(), mod190.getAdministration());
			commit(conn);
			
			String s = mod190.getName();
		    StringBuilder sb = new StringBuilder();
		    if(!Character.isJavaIdentifierStart(s.charAt(0))) {
		        sb.append("_");
		    }
		    for (char c : s.toCharArray()) {
		        if(Character.isJavaIdentifierPart(c)) {
		            sb.append(c);
		        }
		    }		
			
		    String fileName = "Mod190" 
					+ "_" + mod190.getYear() 
					+ "_" + sb.toString();
			
			
			ByteArrayInputStream in = new ByteArrayInputStream(fileoutput.getContent());
			resp.setContentType(MimeType.MIME_TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".txt\";");
			IOUtils.copy(in, resp.getOutputStream());
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

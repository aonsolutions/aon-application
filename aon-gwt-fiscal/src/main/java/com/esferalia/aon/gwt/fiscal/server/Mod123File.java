package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.Mod123;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod123Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod123 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model123File" })
public class Mod123File extends HttpServlet {

	private static final long serialVersionUID = -1902511895853708043L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("mod123"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
 			Mod123 mod123 = FISCAL.getMod123(domainName, domainId, user ,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod123Writer.fillWriter(mod123, writer);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
		    String fileName = AonFiscalFileUtils.getFileName(mod123);
		    MimeType mime = mod123.isAraba()?MimeType.XML:MimeType.TXT;
		    resp.setCharacterEncoding("ISO-8859-1");
			resp.setContentType(mime.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "." + mime.getExtension()+ "\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();

		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}
 
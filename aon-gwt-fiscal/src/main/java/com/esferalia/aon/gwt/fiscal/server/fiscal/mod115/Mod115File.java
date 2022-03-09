package com.esferalia.aon.gwt.fiscal.server.fiscal.mod115;

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

import com.esferalia.aon.occam.api.fiscal.MODEL115;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod115;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod115Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod115 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model115File" })
public class Mod115File extends HttpServlet {

	private static final long serialVersionUID = -7252782405693489654L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			int id = Integer.parseInt(req.getParameter("modelID"));
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String user = req.getParameter("user");
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			Mod115 mod115 = MODEL115.get(occam,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod115Writer.fillWriter(mod115, writer);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
		    String fileName = AonFiscalFileUtils.getFileName(mod115);
		    MimeType mime = mod115.isAraba()?MimeType.XML:MimeType.TXT;
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
 
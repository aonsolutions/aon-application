package com.esferalia.aon.gwt.fiscal.server.fiscal.mod130;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.fiscal.MODEL130;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.Mod130;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.server.fiscal.format.AonFiscalFileUtils;
import com.esferalia.aon.occam.server.fiscal.format.Mod130Writer;
import com.esferalia.aon.watson.server.io.AonIOUtils;

@WebServlet(name = "Mod130 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model130File" })
public class Mod130File extends HttpServlet {

	private static final long serialVersionUID = 2643119785824552950L;

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
			Mod130 mod130 = MODEL130.get(occam,id);

			ByteArrayOutputStream output = new ByteArrayOutputStream();
			OutputStreamWriter wr = null;
			try {
				wr = new OutputStreamWriter(output,"ISO-8859-1");
			} catch (UnsupportedEncodingException e) {
				wr = new OutputStreamWriter(output);
			}
			PrintWriter writer = new PrintWriter(wr);
			Mod130Writer.fillWriter(mod130, writer);
			ByteArrayInputStream in = new ByteArrayInputStream(output.toByteArray());
			
		    String fileName = AonFiscalFileUtils.getFileName(mod130);
		    MimeType mime = mod130.isAraba()?MimeType.XML:MimeType.TXT;
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
 
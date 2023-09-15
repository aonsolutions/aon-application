package com.esferalia.aon.gwt.fiscal.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Context;
import com.esferalia.aon.occam.api.model.fiscal.Mod140Params;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.impl.jooq.dao.CompanyDAO;
import com.esferalia.aon.occam.impl.jooq.dao.Mod140DAO;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Mod140 File download", urlPatterns = { "/aon_gwt_fiscal/ms/Model140File" })
public class Mod140File extends HttpServlet {
	
	private static final long serialVersionUID = 5944684076606387207L;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		CloseableAONContext ctx = null;
		try {
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			String epigraph = req.getParameter("epigraph");
			String fromDateParam = req.getParameter("fromDate");
			String onlyEmitidasParam = req.getParameter("onlyEmitidas");		
			boolean onlyEmitidas = Boolean.parseBoolean(onlyEmitidasParam);			
			Date fromDate = null;
			if (AonStringUtils.isNotBlank(fromDateParam)){
				fromDate = DATE_FORMAT.parse(fromDateParam);
			}
			Date toDate = null;
			String toDateParam = req.getParameter("toDate");
			if (AonStringUtils.isNotBlank(toDateParam)){
				toDate = DATE_FORMAT.parse(toDateParam);
			}
					
			ctx = AONContext.getAONContext(domainName, domainId, user);
			Company company = CompanyDAO.getCompany(ctx, domainId);

			Mod140Context m140ctx = new Mod140Context();
			m140ctx.setDocument(company.getDocument());
			m140ctx.setEpigraph(epigraph);
			
			Mod140Params params = new Mod140Params();
			params.setDomain(domainId);
			params.setFromDate(fromDate);
			params.setToDate(toDate);
			params.setOnlyEmitidas(onlyEmitidas);
			
			ByteArrayOutputStream fos = new ByteArrayOutputStream();
			OutputStreamWriter writer = new OutputStreamWriter(fos,"ISO-8859-1");
			Mod140DAO.getInvoices(ctx, m140ctx, params, writer);
			writer.flush();

			String fileName = "Mod140" + "_" + 2014 + "_" + m140ctx.getDocument();

			ByteArrayInputStream in = new ByteArrayInputStream(
					fos.toByteArray());
			resp.setContentType(MimeType.TXT.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".txt\";");
			AonIOUtils.copy(in, resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		} finally {
			if (ctx != null) ctx.close();
		}
	}

}

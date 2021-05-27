package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AccountEntryParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.accounting.report.AccountJournalReportPDF;

@WebServlet(name = "AccountJournalReport PDF Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountJournalReportPDFPrint" })
public class AccountJournalReportPDFPrint extends HttpServlet {

	private static final long serialVersionUID = -4329476479371433519L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountEntryParams = req.getParameter( IRequestParamsNames.ACCOUNT_ENTRY_PARAMS );
			AccountEntryParams params = JsonParser.parse(accountEntryParams);
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			
			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"Diario de movimientos."+ MimeType.PDF.getExtension()+ "\";");
			AccountJournalReportPDF reportPDF = new AccountJournalReportPDF();
			reportPDF.printBalanceReport(resp.getOutputStream(), params);
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}

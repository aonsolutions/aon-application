package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.accounting.report.AccountBalanceReportPDF;

@WebServlet(name = "AccountBalanceReport PDF Print ", urlPatterns = { "/aon_gwt_fiscal/roms/AccountBalanceReportPDFPrint" })
public class AccountBalanceReportPDFPrint extends HttpServlet {

	private static final long serialVersionUID = -4737903276711035815L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountReportParams = req.getParameter( IRequestParamsNames.ACCOUNT_REPORT_PARAMS );
			AccountingReportParams params = JsonParser.parseAccountingParams(accountReportParams);
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			

			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"Balance_Oficial."+ MimeType.PDF.getExtension()+ "\";");
			AccountBalanceReportPDF reportPDF = new AccountBalanceReportPDF();
			reportPDF.printBalanceReport(resp.getOutputStream(), params);
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

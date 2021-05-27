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

import net.aonsolutions.aon.accounting.report.VatReportPDF;

@WebServlet(name = "VatReport PDF Print ", urlPatterns = { "/aon_gwt_fiscal/roms/VatReportPDFPrint" })
public class VatReportPDFPrint extends HttpServlet {

	private static final long serialVersionUID = -4737903276711035815L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountReportParams = req.getParameter( IRequestParamsNames.VAT_PARAMS);
			AccountingReportParams params = JsonParser.parseAccountingParams(accountReportParams);
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			
//			String vatParams = req.getParameter("vatParams");
//			AccountingReportParams params = VatReportUtils.getParams(req);
			
			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"IVA."+ MimeType.PDF.getExtension()+ "\";");
			VatReportPDF reportPDF = new VatReportPDF();
			reportPDF.printReport(resp.getOutputStream(), params);
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

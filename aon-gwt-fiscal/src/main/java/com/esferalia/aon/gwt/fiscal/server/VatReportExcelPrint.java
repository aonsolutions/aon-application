package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "VatReport Excel Print ", urlPatterns = { "/aon_gwt_fiscal/roms/VatReportExcelPrint" })
public class VatReportExcelPrint extends HttpServlet {

	private static final long serialVersionUID = 2782900860290220524L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String accountReportParams = req.getParameter( IRequestParamsNames.VAT_PARAMS);
			AccountingReportParams params = JsonParser.parseAccountingParams(accountReportParams);
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			
			VatContextExcelAction action = new VatContextExcelAction();
			action.initialize("IVA");
			Occam occam = new  Occam()
				.setDomain(params.getDomain())
				.setDomainName(params.getDomainName())
				.setUser(params.getUser());
			FISCAL.getVatContext(occam, params).forEach(action);
			resp.setContentType(MimeType.MS_EXCEL.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"IVA."+ MimeType.MS_EXCEL.getExtension()+ "\";");
			action.finalize(resp.getOutputStream());
			
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
	
}

package com.esferalia.aon.gwt.fiscal.server.irpf;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.fiscal.server.JsonParser;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.accounting.report.IrpfReportPDF;

@WebServlet(name = "IRPFReport PDF Print ", urlPatterns = { "/aon_gwt_fiscal/roms/IrpfReportPDFPrint" })
public class IrpfReportPDFPrint extends HttpServlet {

	private static final long serialVersionUID = -4737903276711035815L;
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String irpfParams = req.getParameter( IRequestParamsNames.IRPF_PARAMS);
			IRPFParams params = JsonParser.parseIRPFParams(irpfParams);
			params.setDomain(Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID)));
			params.setUser(req.getParameter(IRequestParamsNames.USER));
			params.setDomainName(req.getParameter(IRequestParamsNames.DOMAIN_NAME));
			
			resp.setContentType(MimeType.PDF.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\"IRPF."+ MimeType.PDF.getExtension()+ "\";");
			IrpfReportPDF reportPDF = new IrpfReportPDF();
			reportPDF.printReport(resp.getOutputStream(), params);
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

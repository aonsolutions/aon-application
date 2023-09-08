package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.gwt.payroll.util.SettleBuilder;
import com.esferalia.aon.gwt.payroll.util.PdfPrintResponses;
import com.esferalia.aon.in.payroll.pdf.maker.exception.CanNotCreatePdfException;

@SuppressWarnings("serial")
@WebServlet(name = "Settle-vallhala", urlPatterns = { "/aon_gwt_aio/vallhala/*", "/aon_gwt_payroll/vallhala/*" })
public class SettlePrintServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		OutputStream out = resp.getOutputStream();

		String enterpriseString	= req.getParameter(PayrollPrintService.Parameter.ENTERPRISE.getName());
		String domain			= req.getParameter(PayrollPrintService.Parameter.DOMAIN.getName());

		if (req.getParameter(PayrollPrintService.Parameter.ID.getName()) == null || enterpriseString == null || domain == null)
		{
			error(resp, out);
			return;
		}

		int enterprise = Integer.parseInt(enterpriseString);

		Integer[] ids = new Integer[req.getParameterValues(PayrollPrintService.Parameter.ID.getName()).length];
		for (int i = 0; i < ids.length; i++)
			ids[i] = Integer.parseInt(req.getParameterValues(PayrollPrintService.Parameter.ID.getName())[i]);

		try
		{
			resp.setContentType(MimeType.MIME_PDF.getName());
			SettleBuilder.printSettles(domain, enterprise, ids, out, req.getLocale());
		} catch (CanNotCreatePdfException e)
		{
			error(resp, out);
		}
	}

	private static void error(HttpServletResponse resp, OutputStream out) throws IOException {
		resp.setContentType(MimeType.MIME_HTML.getName());
		out.write(PdfPrintResponses.pdfPrintError("No se ha podido imprimir el finiquito.").getBytes());
	}
}

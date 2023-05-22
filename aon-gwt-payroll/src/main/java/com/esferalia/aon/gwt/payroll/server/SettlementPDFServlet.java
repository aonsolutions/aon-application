package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPDFSettlementBuilder;
import com.esferalia.aon.occam.api.model.type.MimeType;

@SuppressWarnings("serial")
@WebServlet(
		name = "Settlement-PDF", 
		urlPatterns = { 
				"/aon_gwt_aio/settlement_pdf/*" ,
				"/aon_gwt_payroll/settlement_pdf/*" 
		}
)
public class SettlementPDFServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String domain = req.getParameter(PayrollPrintService.Parameter.DOMAIN.getName());
		String login = req.getParameter(PayrollPrintService.Parameter.USER.getName());
		Integer selectedSettle = getSettleId(req);
		resp.setContentType(MimeType.PDF.getName());
		try (OutputStream os = resp.getOutputStream()) {			
			JooqPDFSettlementBuilder settleBuilder = new JooqPDFSettlementBuilder(domain, login, selectedSettle, os);
			settleBuilder.write();
			os.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	
	private static Integer getSettleId (HttpServletRequest req) {
		String[] ids = req.getParameterValues(PayrollPrintService.Parameter.ID.getName());
		if (ids != null && ids.length == 1) {
			try {
				return Integer.parseInt(ids[0]);
			} catch (NullPointerException | NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

}
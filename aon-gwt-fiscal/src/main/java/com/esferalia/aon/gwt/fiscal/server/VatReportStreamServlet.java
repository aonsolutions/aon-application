package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.SimpleDateFormat;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.AccountingReportParams;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.impl.jooq.dao.VATFormatter;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Vat Report Stream", urlPatterns = { "/aon_gwt_fiscal/roms/VatReportStream" })
public class VatReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -6662360372576864159L;
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String vatParams = req.getParameter("vatParams");
			String domainName = req.getParameter("domainName");
			String user = req.getParameter("user");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			AccountingReportParams params = new AccountingReportParams();
			JSONParser parser = new JSONParser();
			JSONObject jsonParams =  (JSONObject) parser.parse(vatParams);
			Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
			params.setDomain(domain.intValue());
			Long registry = (Long) jsonParams.get(IRequestParamsNames.REGISTRY);
			if (registry != null) {
				params.setRegistry(registry.intValue());	
			}
			Long activity = (Long) jsonParams.get(IRequestParamsNames.ACTIVITY);
			if (activity != null) {
				params.setActivity(activity.intValue());	
			}
			String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
			if (AonStringUtils.isNotBlank(fromDate)) {
				params.setFromDate( FORMATTER.parse(fromDate));			
			}
			String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
			if (AonStringUtils.isNotBlank(toDate)) {
				params.setToDate( FORMATTER.parse(toDate));			
			}
			Number percent = (Number) jsonParams.get(IRequestParamsNames.PERCENT);
			if (percent != null) {
				params.setPercent(percent.doubleValue());	
			}
			Long type = (Long) jsonParams.get(IRequestParamsNames.VAT_SUMMARY_TYPE);
			if (type != null) {
				int t = type.intValue();
				params.setVatSummaryType( VatSummaryType.safeValueOf(t));
			}
			Long output = (Long) jsonParams.get(IRequestParamsNames.OUTPUT);
			if (output != null) {
				params.setOutput(output==1);
			}
			Long surcharge = (Long) jsonParams.get(IRequestParamsNames.SURCHARGE);
			if (surcharge != null) {
				params.setSurcharge(surcharge==1);
			}
			Long farmerRegime = (Long) jsonParams.get(IRequestParamsNames.FARMER_REGIME);
			if (farmerRegime != null) {
				params.setFarmerRegime(farmerRegime==1);
			}
			Long accrualRegime = (Long) jsonParams.get(IRequestParamsNames.ACCRUAL_REGIME);
			if (accrualRegime != null) {
				params.setAccrualRegime(accrualRegime==1);
			}
			Long investment = (Long) jsonParams.get(IRequestParamsNames.INVESTMENT);
			if (investment != null) {
				params.setInvestment(investment==1);
			}
			Long service = (Long) jsonParams.get(IRequestParamsNames.SERVICE);
			if (service != null) {
				params.setService(service==1);
			}
			Long rect = (Long) jsonParams.get(IRequestParamsNames.RECTIFICATION);
			if (rect!= null) {
				params.setRectificationType( RectificationType.safeValueOf(rect.intValue()));
			}
			
			resp.setContentType(MimeType.HTML.getName());
			VATFormatter.formatInvoices(resp.getWriter()
					,FISCAL.getVatContext(domainName, domainId, user, params)
					,"LISTADO IVA"
					, FiscalUtils.toString(params));
			
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

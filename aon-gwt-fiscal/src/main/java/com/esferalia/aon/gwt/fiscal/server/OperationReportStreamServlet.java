package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Comparator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.FISCAL;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "Operation Report Stream", urlPatterns = { "/aon_gwt_fiscal/OperationReportStream" })
public class OperationReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -2697508555670615321L;
	
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String irpfParams = req.getParameter("irpfParams");
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			OperationParams params = new OperationParams();
			JSONParser parser = new JSONParser();
			JSONObject jsonParams =  (JSONObject) parser.parse(irpfParams);
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
			Long expenses = (Long) jsonParams.get(IRequestParamsNames.EXPENSES);
			if (expenses != null) {
				params.setExpenses(expenses==1?true:false);
			}
			Long irpf = (Long) jsonParams.get(IRequestParamsNames.IRPF);
			if (irpf != null) {
				params.setIrpf(irpf==1?true:false);
			}

			String user = AonServletUtils.getLoggedUser();
			
			resp.setContentType(MimeType.HTML.getName());
			OperationFormatter.formatIrpf(resp.getWriter()
				,FISCAL.getOperationBreakdown(domainName, user, domainId, activity.intValue(), expenses==1?true:false, irpf==1?true:false, params)
				,"PANEL IRPF"
//				, FiscalUtils.toString(params));
				, "");
			resp.flushBuffer();
			
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

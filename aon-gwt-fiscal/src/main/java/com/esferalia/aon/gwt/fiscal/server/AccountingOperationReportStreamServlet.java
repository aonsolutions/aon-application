package com.esferalia.aon.gwt.fiscal.server;

import java.io.IOException;
import java.io.PrintWriter;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.tools.json.ParseException;
import org.json.JSONObject;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.fiscal.OperationBreakdown;
import com.esferalia.aon.occam.api.model.fiscal.OperationParams;
import com.esferalia.aon.occam.api.model.type.MimeType;

@WebServlet(name = "Accounting Operation Report Stream", urlPatterns = { "/aon_gwt_fiscal/roms/AccountingOperationReportStream" })
public class AccountingOperationReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = 939542456364543981L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String operationParams = req.getParameter(IRequestParamsNames.IRPF_PARAMS);
			String domainName = req.getParameter(IRequestParamsNames.DOMAIN_NAME);;
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			String user = req.getParameter(IRequestParamsNames.USER);
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(user);
			OperationParams params = JsonParser.parseOperationParams(operationParams);
			resp.setContentType(MimeType.HTML.getName());
			PrintWriter wr = resp.getWriter();
			wr.print('[');
			ACCOUNTING.getOperationBreakdown(occam, params)
				.forEach(br -> writeToJSONOperation(wr, br));
			wr.print(']');
			resp.flushBuffer();
		} catch (ParseException | java.text.ParseException e) {
			throw new ServletException(e);
		}
	}
	
	private void writeToJSONOperation(final PrintWriter out, OperationBreakdown op) {
		out.print(new JSONObject()
			.put(IJsonNames.ENTRY_ID, op.getEntryId()==null ? null : op.getEntryId())
			.put(IJsonNames.EPIGRAPH, op.getActivityIAE())
			.put(IJsonNames.ACCOUNT, op.getAccount())
			.put(IJsonNames.ACCOUNT_DESCRIPTION, op.getAccountDescription())
			.put(IJsonNames.CONCEPT, op.getConcept())
			.put(IJsonNames.DOCUMENT_NUMBER, op.getDocNumber())
			.put(IJsonNames.REGISTRY_DOCUMENT, op.getRegistryDocument())
			.put(IJsonNames.REGISTRY_NAME, op.getRegistryName())
			.put(IJsonNames.ENTRY_DATE, op.getEntryDate()==null ? null : String.format("%1$tY-%1$tm-%1$td",op.getEntryDate()))
			.put(IJsonNames.TAX_DATE, op.getTaxDate()==null ? null : String.format("%1$tY-%1$tm-%1$td",op.getTaxDate()))
			.put(IJsonNames.BASE, op.getBase() )
			.put(IJsonNames.PERCENT, op.getPercent() )
			.put(IJsonNames.QUOTA, op.getQuota() )
			.put(IJsonNames.SURCHARGE_PERCENT, op.getSurchargePercent() )
			.put(IJsonNames.SURCHARGEQUOTA, op.getSurchargeQuota() )
			.put(IJsonNames.TOTAL, op.getTotal() )
			.toString());
		out.flush();
	}

}

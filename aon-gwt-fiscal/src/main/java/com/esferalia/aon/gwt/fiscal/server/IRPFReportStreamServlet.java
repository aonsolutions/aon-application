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
import com.esferalia.aon.occam.api.model.fiscal.IRPFParams;
import com.esferalia.aon.occam.api.model.fiscal.IrpfBreakdown;
//import com.esferalia.aon.occam.api.model.fiscal.VatParams;
//import com.esferalia.aon.occam.api.model.fiscal.VatSummaryType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.occam.api.model.type.WithholdingType;
import com.esferalia.aon.occam.impl.jooq.dao.IRPFFormatter;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@WebServlet(name = "IRPF Report Stream", urlPatterns = { "/aon_gwt_fiscal/IRPFReportStream" })
public class IRPFReportStreamServlet extends HttpServlet {

	private static final long serialVersionUID = -2697508555670615321L;
	
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		try {
			String irpfParams = req.getParameter("irpfParams");
			String domainName = req.getParameter("domainName");
			int domainId = Integer.parseInt(req.getParameter("domainId"));
			IRPFParams params = new IRPFParams();
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
			Number percent = (Number) jsonParams.get(IRequestParamsNames.PERCENT);
			if (percent != null) {
				params.setPercent(percent.doubleValue());	
			}
			Long type = (Long) jsonParams.get(IRequestParamsNames.TYPE);
			if (type != null) {
				int t = type.intValue();
				params.setWithholdingType( WithholdingType.safeValueOf(t));
			}
			Long output = (Long) jsonParams.get(IRequestParamsNames.OUTPUT);
			if (output != null) {
				params.setOutput(output==1);
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
			Long orderBy = (Long) jsonParams.get(IRequestParamsNames.ORDER_BY);
			if (orderBy != null) {
				params.setOrderBy(orderBy.intValue());
			}
			Long groupByNif = (Long) jsonParams.get(IRequestParamsNames.GROUP_BY_NIF);
			if (groupByNif != null) {
				params.setGroupByNif(groupByNif.intValue());
			}

			String user = AonServletUtils.getLoggedUser();
			
			// Comparador - Ordenar resultado por selección del usuario
			Comparator<IrpfBreakdown> comp = null;
			switch (params.getOrderBy()) {
				case 0: // Fecha Iva
					comp = Comparator.comparing(IrpfBreakdown::getTaxDate); break;
				case 1: // Fecha de factura
					comp = Comparator.comparing(IrpfBreakdown::getIssueDate); break;
				case 2: // Número de Documento
					comp = Comparator.comparing(IrpfBreakdown::getNumber); break;
				case 3: // Número de Factura
					comp = Comparator.comparing(IrpfBreakdown::getReferenceCode); break;
				case 4: // Nombre de Cliente/Proveedor/Acreedor
					comp = Comparator.comparing(IrpfBreakdown::getName); break;
				case 5: // NIF/DNI de Cliente/Proveedor/Acreedor
					comp = Comparator.comparing(IrpfBreakdown::getRegistryDocument); break;
				default:
					comp = Comparator.comparing(IrpfBreakdown::getTaxDate); break;
			}

			resp.setContentType(MimeType.HTML.getName());
			if (params.getGroupByNif() == 1) {
				IRPFFormatter.formatGroupedInvoices(resp.getWriter()
					,FISCAL.getIrpfBreakdown(domainName, user, domainId, params)
					,"LISTADO IRPF AGRUPADO"
					, FiscalUtils.toString(params));
			} else {
				IRPFFormatter.formatInvoices(resp.getWriter()
					,FISCAL.getIrpfBreakdown(domainName, user, domainId, params).sorted(comp)
					,"LISTADO IRPF"
					, FiscalUtils.toString(params));
			}
			
			resp.flushBuffer();
			
		} catch (Throwable e) {
			throw new ServletException(e);
		}

	}
}

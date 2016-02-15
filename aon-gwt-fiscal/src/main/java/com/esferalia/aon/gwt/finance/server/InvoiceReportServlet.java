package com.esferalia.aon.gwt.finance.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Invoice Report (excel)", urlPatterns = { "/aon_gwt_fiscal/InvoiceReport" })
public class InvoiceReportServlet extends HttpServlet {
	
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String domainName = req.getParameter( IRequestParamsNames.DOMAIN_NAME);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			
			String fromDateParam = req.getParameter(IRequestParamsNames.FROM_DATE);
			final Date fromDate = (AonStringUtils.isNotBlank(fromDateParam))
					?DATE_FORMAT.parse(fromDateParam)
					:null;
			String toDateParam = req.getParameter(IRequestParamsNames.TO_DATE);
			final Date toDate = (AonStringUtils.isNotBlank(toDateParam))
					?DATE_FORMAT.parse(toDateParam)
					:null;
			String login = AonServletUtils.getRequestUser(req);
					
			InvoiceExcelAction action = new InvoiceExcelAction();
			List<String> tags = AON.getProductTags(domainName, domainId,login);
			Map<Integer,String[]> productTags = null;
			if (tags != null && tags.size() > 0) {
				productTags = AON.getProductTagMap(domainName, domainId,login);	
			}
			action.setTags(tags);
			action.setProductTags(productTags);
			action.initialize("FACTURAS");

			List<Byte> typesList = new LinkedList<Byte>();
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.INVOICE_TYPE_SALES))) {
				typesList.add(InvoiceType.SALES.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.INVOICE_TYPE_PURCHASES))) {
				typesList.add(InvoiceType.PURCHASE.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.INVOICE_TYPE_EXPENSES))) {
				typesList.add(InvoiceType.EXPENSES.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.INVOICE_TYPE_UNDEDUCTIBLE))) {
				typesList.add(InvoiceType.UNDEDUCTIBLE.value());
			}
			Byte[] typ = new Byte[typesList.size()]; 
			final Byte[] types = typesList.toArray(typ);
			
			
			User user = AON.getUser(domainName, domainId, login ); 
			Integer[] scopes = AON.getUserScopes(domainName, domainId,login, user.getId());
			
			
			AON.getInvoiceDetails(domainName, domainId, login,
					p -> {
						Filter f = p.getDomainProperty().eq(domainId)
							.and(p.getTypeProperty().in(types))
							.and(p.getStartIssueDateProperty().ge(fromDate))
							.and(p.getEndIssueDateProperty().le(toDate));
						f = scopes == null?f:f.and(p.getScopeProperty().in( scopes ));
						f = user.hasConfidentialityRole()?f:f.and(p.getConfidentialProperty().eq( SecurityLevel.OFFICIAL.value()));	
						return f;
					}
					)
			.forEach(action);
			
			String fileName = "Facturas";
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".xlsx\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			e.printStackTrace();
			throw new ServletException(e);
		} 
	}

}

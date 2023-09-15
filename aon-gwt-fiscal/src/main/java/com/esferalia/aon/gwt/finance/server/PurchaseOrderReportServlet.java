package com.esferalia.aon.gwt.finance.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.PurchaseStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Purchase Order Report (excel)", urlPatterns = { "/aon_gwt_fiscal/PurchaseOrderReport",
																 	"/aon_gwt_aio/PurchaseOrderReport" })
public class PurchaseOrderReportServlet extends HttpServlet {
	
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
			
			PurchaseOrderExcelAction action = new PurchaseOrderExcelAction();
			List<String> tags = AON.getProductTags(domainName, domainId, login);
			Map<Integer,String[]> productTags = null;
			if (tags != null && tags.size() > 0) {
				productTags = AON.getProductTagMap(domainName, domainId, login);	
			}
			action.setTags(tags);
			action.setDomainName(domainName);
			action.setProductTags(productTags);
			action.initialize("PEDIDOS DE COMPRA");

			List<Byte> typesList = new LinkedList<Byte>();
		
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.ORDER_STATUS_PENDING))) {
				typesList.add(PurchaseStatus.PENDING.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.ORDER_STATUS_BLOCKED))) {
				typesList.add(PurchaseStatus.BLOCKED.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.ORDER_STATUS_SERVED))) {
				typesList.add(PurchaseStatus.SERVED.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.ORDER_STATUS_CLOSED))) {
				typesList.add(PurchaseStatus.CLOSED.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.ORDER_STATUS_INVOICED))) {
				typesList.add(PurchaseStatus.INVOICED.value());
			}
			Byte[] typ = new Byte[typesList.size()]; 
			final Byte[] types = typesList.toArray(typ);
			
			User user = AON.getUser(domainName, domainId, login ); 
			Integer[] scopes = AON.getUserScopes(domainName, domainId,login, user.getId());
			
			final Byte[] sec = new Byte[user.hasConfidentialityRole()?2:1]; 
			sec[0] = 0;
			if (user.hasConfidentialityRole()) {
				sec[1] = 1;	
			}
			
			AON.getPurchaseDetails(domainName, domainId, login,
					p -> {
						Filter f = p.getDomainProperty().eq(domainId)
							.and(p.getStatusProperty().in(types))
							.and(p.getIssueDateProperty().ge(AonDateUtils.toSql(fromDate)))
							.and(p.getIssueDateProperty().le(AonDateUtils.toSql(toDate)));
						f = scopes == null?f:f.and(p.getScopeProperty().in( scopes ));
						f = user.hasConfidentialityRole()?f:f.and(p.getConfidentialProperty().eq( SecurityLevel.OFFICIAL.value()));	
						
						return f;
					}
				).forEach(action);
						
			String fileName = "Pedidos de Compra";
			resp.setContentType(MimeType.MS_EXCEL_2007.getName());
			resp.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".xlsx\";");
			action.finalize(resp.getOutputStream());
			resp.flushBuffer();
		} catch (Throwable e) {
			throw new ServletException(e);
		} 
	}

}

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
import com.esferalia.aon.occam.api.model.type.OfferStatus;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Offer Report (excel)", urlPatterns = { "/aon_gwt_fiscal/OfferReport",
														   "/aon_gwt_aio/OfferReport" })
public class OfferReportServlet extends HttpServlet {
	
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
			
			OfferExcelAction action = new OfferExcelAction();
			List<String> tags = AON.getProductTags(domainName, domainId, login);
			Map<Integer,String[]> productTags = null;
			if (tags != null && tags.size() > 0) {
				productTags = AON.getProductTagMap(domainName, domainId, login);	
			}
			action.setTags(tags);
			action.setProductTags(productTags);
			action.initialize("PRESUPUESTOS");

			List<Byte> typesList = new LinkedList<Byte>();
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.OFFER_STATUS_PENDING))) {
				typesList.add(OfferStatus.PENDING.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.OFFER_STATUS_APPROVED))) {
				typesList.add(OfferStatus.APPROVED.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.OFFER_STATUS_REFUSED))) {
				typesList.add(OfferStatus.REFUSED.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.OFFER_STATUS_BLOCKED))) {
				typesList.add(OfferStatus.BLOCKED.value());
			}
			if (AonStringUtils.equals("on",req.getParameter(IRequestParamsNames.OFFER_STATUS_INVOICED))) {
				typesList.add(OfferStatus.INVOICED.value());
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
			
			AON.getOfferDetails(domainName, domainId, login,
					p -> {
						Filter f = p.getDomainProperty().eq(domainId)
							.and(p.getStatusProperty().in(types))
							.and(p.getStartIssueDateProperty().ge(fromDate))
							.and(p.getEndIssueDateProperty().le(toDate));
						f = scopes == null?f:f.and(p.getScopeProperty().in( scopes ));
						f = user.hasConfidentialityRole()?f:f.and(p.getConfidentialProperty().eq( SecurityLevel.OFFICIAL.value()));	
						return f;
					}
				).forEach( action);
						
			String fileName = "Presupuestos";
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

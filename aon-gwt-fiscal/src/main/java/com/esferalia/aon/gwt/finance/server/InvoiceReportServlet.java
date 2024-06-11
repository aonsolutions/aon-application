package com.esferalia.aon.gwt.finance.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import com.esferalia.aon.gwt.fiscal.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.invoice.InvoiceDetailExtended;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.occam.api.model.type.SecurityLevel;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet(name = "Invoice Report (excel)", urlPatterns = { "/aon_gwt_fiscal/InvoiceReport",
															 "/aon_gwt_aio/InvoiceReport" })
public class InvoiceReportServlet extends HttpServlet {
	
	private static final long serialVersionUID = -6480050528094260796L;
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String domainName = req.getParameter( IRequestParamsNames.DOMAIN_NAME);
			int domainId = Integer.parseInt(req.getParameter(IRequestParamsNames.DOMAIN_ID));
			String login = req.getParameter( IRequestParamsNames.USER);
			
			String registryParam = req.getParameter(IRequestParamsNames.REGISTRY_ID);
			Integer registryId = AonStringUtils.isNotBlank(registryParam)? AonNumberUtils.toint(registryParam) :null;
			
			String productParam = req.getParameter(IRequestParamsNames.PRODUCT_ID);
			Integer productId = AonStringUtils.isNotBlank(productParam)? AonNumberUtils.toint(productParam) :null;

			String fromDateParam = req.getParameter(IRequestParamsNames.FROM_DATE);
			final Date fromDate = (AonStringUtils.isNotBlank(fromDateParam))
					?DATE_FORMAT.parse(fromDateParam)
					:null;
			String toDateParam = req.getParameter(IRequestParamsNames.TO_DATE);
			final Date toDate = (AonStringUtils.isNotBlank(toDateParam))
					?DATE_FORMAT.parse(toDateParam)
					:null;
					
			InvoiceExcelAction action = new InvoiceExcelAction();
			List<String> tags = AON.getProductTags(domainName, domainId,login);
			Map<Integer,String[]> productTags = null;
			if (tags != null && tags.size() > 0) {
				productTags = AON.getProductTagMap(domainName, domainId,login);	
			}
			action.setTags(tags);
			action.setProductTags(productTags);
			action.initialize("FACTURAS");
			
			String invoiceTypes = req.getParameter( IRequestParamsNames.INVOICE_TYPES);
			List<Byte> typesList = new LinkedList<Byte>();
			if (AonStringUtils.isBlank(invoiceTypes)) {
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
			} else {
				String[] ids = AonStringUtils.split(invoiceTypes, ',');
				for (String id: ids ) {
					typesList.add(AonNumberUtils.toByte(id));		
				}
			}
			Byte[] typ = new Byte[typesList.size()];
			final Byte[] types = typesList.toArray(typ);
			
			String categoryIds = req.getParameter( IRequestParamsNames.CATEGORY_IDS);
			List<Integer> categoryList = new LinkedList<Integer>();
			if (!AonStringUtils.isBlank(categoryIds)) {
				for (String id: AonStringUtils.split(categoryIds, ',') ) {
					categoryList.add(AonNumberUtils.toInteger(id));		
				}
			}
			Integer[] ids = new Integer[categoryList.size()];
			final Integer[] categories = categoryList.toArray(ids);
			
			String brandIds = req.getParameter( IRequestParamsNames.BRAND_IDS);
			List<Integer> brandList = new LinkedList<Integer>();
			if (!AonStringUtils.isBlank(brandIds)) {
				for (String id: AonStringUtils.split(brandIds, ',') ) {
					brandList.add(AonNumberUtils.toInteger(id));		
				}
			}
			ids = new Integer[brandList.size()];
			final Integer[] brands = brandList.toArray(ids);

			
			String workplaceIds = req.getParameter( IRequestParamsNames.WORKPLACE_IDS);
			List<Integer> workplaceList = new LinkedList<Integer>();
			if (!AonStringUtils.isBlank(workplaceIds)) {
				for (String id: AonStringUtils.split(workplaceIds, ',') ) {
					workplaceList.add(AonNumberUtils.toInteger(id));		
				}
			}
			ids = new Integer[workplaceList.size()];
			final Integer[] workplaces = workplaceList.toArray(ids);
			

			String sellerIds = req.getParameter( IRequestParamsNames.SELLER_IDS);
			List<Integer> sellerList = new LinkedList<Integer>();
			if (!AonStringUtils.isBlank(sellerIds)) {
				for (String id: AonStringUtils.split(sellerIds, ',') ) {
					sellerList.add(AonNumberUtils.toInteger(id));		
				}
			}
			ids = new Integer[sellerList.size()];
			final Integer[] seller = sellerList.toArray(ids);

			User user = AON.getUser(domainName, domainId, login ); 
			Integer[] scopes = AON.getUserScopes(domainName, domainId,login, user.getId());
			Occam occam = new Occam()
				.setDomainName(domainName)
				.setDomain(domainId)
				.setUser(login);
			
			Stream<InvoiceDetailExtended> stream = AON.getInvoiceDetailsExtended(occam,
					p -> {
						Filter f = p.getDomainProperty().eq(domainId)
							.and(types.length==0?p.getIdProperty().isNotNull():p.getTypeProperty().in(types))
							.and(registryId == null?p.getRegistryProperty().isNotNull():p.getRegistryProperty().eq(registryId))
							.and(p.getStartIssueDateProperty().ge(fromDate))
							.and(p.getEndIssueDateProperty().le(toDate))
							.and(productId == null?p.getIdProperty().isNotNull():p.getProductProperty().eq(productId))
							.and(categories.length==0?p.getIdProperty().isNotNull():p.getProductCategoryProperty().in(categories))
							.and(brands.length==0?p.getIdProperty().isNotNull():p.getProductBrandProperty().in(brands))
							.and(seller.length==0?p.getIdProperty().isNotNull():p.getSellerProperty().in(seller))
							.and(workplaces.length==0?p.getIdProperty().isNotNull():p.getWorkplaceProperty().in(workplaces))
							;
						f = scopes == null?f:f.and(p.getScopeProperty().in( scopes ));
						f = user.hasConfidentialityRole()?f:f.and(p.getConfidentialProperty().eq( SecurityLevel.OFFICIAL.value()));	
						return f;
					}
				)
			;
			stream.forEach(action);
			stream.close();
			
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

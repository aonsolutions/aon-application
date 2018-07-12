package com.code.aon.webservice.finance;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.common.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.ApplicationParameter;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.ItemProperties;
import com.esferalia.aon.occam.api.model.Properties.ProductProperties;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.type.AppParam;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.RectificationType;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "FinanceServlet", urlPatterns = { "/finance/*",
													 "/aon_gwt_aio/finance/*"})
public class FinanceServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(FinanceServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Finance Servlet - GET METHOD");
		String accessToken = req.getParameter(MSG.ACCESS_TOKEN);
		String[] pathInfo = req.getPathInfo().split("/");
		
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));
		Domain domain = AON.getDomain(domainName, domainId, userName);

		String md5 = Utils.getMd5(userName+domain.getName());
		
		if(accessToken.equals(md5)){
			if(pathInfo.length > 3){
				Object object = new Object();
				JSONObject meta = new JSONObject();
				switch (pathInfo[3]) {
				case MSG.INVOICE: // INVOICE
					if(pathInfo.length > 4){
						if (MSG.REGISTRY.equals(pathInfo[4])) {
							if(pathInfo.length > 5) // LISTA DE INVOICE CON REGISTRY X
								object = getInvoiceList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(pathInfo[4].equals("id")) {
							if(pathInfo.length > 5) // INVOICE CON ID X
								object = getInvoice(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(pathInfo[4].equals("movements")) {
							object = getInvoiceMovements(domain, userName, req.getParameterMap());
						}
					} else {// LISTA DE INVOICE CONDICION DOMAIN
						object = getInvoiceList(domain, userName, req);
					}
					break;
				case MSG.FEE: // FEE
					if(pathInfo.length > 4){
						if (MSG.CUSTOMER.equals(pathInfo[4])) {
							if(pathInfo.length > 5) // LISTA DE FEE CON CUSTOMER X
								object = getFeeList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(MSG.ID.equals(pathInfo[4])) {
							if(pathInfo.length > 5) // FEE CON ID X
								object = getFee(domain, userName, Integer.parseInt(pathInfo[5])); 
						}
					} else {// LISTA DE INVOICE CONDICION DOMAIN
						getFeeList(domain, userName);
					}
					break;
				case MSG.BOUGHT_PRODUCT: // INVOICE
					if(pathInfo.length > 4){
						if (MSG.REGISTRY.equals(pathInfo[4])) {
							if(pathInfo.length > 5) // LISTA DE INVOICE CON REGISTRY X
								object = getBoughtProductList(domain, userName, Integer.parseInt(pathInfo[5]));
						}
					}
					break;
				case MSG.BILLING_PERIOD: // FEE
					object = getBillingPeriodList();
					break;
				default:
					break;
				}
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Finance Servlet - POST METHOD");	
	}

    private JSONArray getInvoiceList(Domain domain, String login, HttpServletRequest req){
    	if(req.getParameterMap().containsKey(MSG.SII)){
    		Integer page =  req.getParameterMap().containsKey("page") ? Integer.parseInt(req.getParameter("page")) : 1;
    		Integer perPage = req.getParameterMap().containsKey("per_page") ? Integer.parseInt(req.getParameter("per_page")) : 40;
    		Date from =  req.getParameterMap().containsKey("from") ? new Date(Long.parseLong(req.getParameter("from"))) : AonDateUtils.getDate(2017, 07, 01);  
    		Boolean pending = req.getParameterMap().containsKey("pending") ? req.getParameter("pending").equalsIgnoreCase("true") : false;
    		Boolean sent = req.getParameterMap().containsKey("sent") ? req.getParameter("sent").equalsIgnoreCase("true") : false;
    		Boolean sent_error = req.getParameterMap().containsKey("sent_error") ? req.getParameter("sent_error").equalsIgnoreCase("true") : false;
    		Boolean error = req.getParameterMap().containsKey("error") ? req.getParameter("error").equalsIgnoreCase("true") : false;
    		Boolean anulada = req.getParameterMap().containsKey("anulada") ? req.getParameter("anulada").equalsIgnoreCase("true") : false;
    		Boolean partial = req.getParameterMap().containsKey("partial") ? req.getParameter("partial").equalsIgnoreCase("true") : false;
    		Boolean paid = req.getParameterMap().containsKey("paid") ? req.getParameter("paid").equalsIgnoreCase("true") : false;
    		
    		if("fe_emitidas".equals(req.getParameter(MSG.SII))
    			|| "fe_generales".equals(req.getParameter(MSG.SII))
    			|| "fe_simplificadas".equals(req.getParameter(MSG.SII))
    			|| "fe_rectificativas".equals(req.getParameter(MSG.SII))
    			|| "fe_intracomunitarias".equals(req.getParameter(MSG.SII))){
    	    	if(from.compareTo(AonDateUtils.getDate(2017, 0, 1)) < 0){
    	    		from = AonDateUtils.getDate(2017, 0, 1);    				
    	    	}
    			return getInvoiceEmitidasList(domain, login, page, perPage, from, pending, sent, sent_error, error, anulada, req.getParameter("sii") );
    		} else if("fr_recibidas".equals(req.getParameter(MSG.SII))
    			|| "fr_compras".equals(req.getParameter(MSG.SII))
    			|| "fr_gastos".equals(req.getParameter(MSG.SII))
    			|| "fr_rectificativas".equals(req.getParameter(MSG.SII))
    			|| "fr_intracomunitarias".equals(req.getParameter(MSG.SII))){
    			if(from.compareTo(AonDateUtils.getDate(2017, 0, 1)) < 0){
    	    		from = AonDateUtils.getDate(2017, 0, 1);    				
    	    	}
    			return getInvoiceRecibidasList(domain, login, page, perPage, from, pending, sent, sent_error, error, anulada, req.getParameter(MSG.SII));
    		} else if("bienes".equals(req.getParameter(MSG.SII))){
    			return getInvoiceBienesList(domain, login, page, perPage, from, pending, sent, sent_error, error, anulada, req.getParameter(MSG.SII));
    		} else if("intracomunitarias".equals(req.getParameter(MSG.SII))){
    			return getInvoiceIntracomunitariasList(domain, login, page, perPage, from, pending, sent, sent_error, error, anulada, req.getParameter(MSG.SII));
    		} else if("cp_cobros_pagos".equals(req.getParameter(MSG.SII))
    					|| "cp_cobros".equals(req.getParameter(MSG.SII))
    					|| "cp_pagos".equals(req.getParameter(MSG.SII))
    				){
    			return getInvoiceCobrosPagosList(domain, login, page, perPage, from, pending, error, partial, paid, req.getParameter(MSG.SII));
    		}
    	}
    	JSONArray array = new JSONArray();
    	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(rm -> array.put(ToJSON.invoiceToJSON(rm)));
    	return array;
    }
   
    private JSONArray getInvoiceEmitidasList(Domain domain, String login, Integer page, Integer perPage, Date from
    		,Boolean pending, Boolean sent, Boolean sent_error, Boolean error, Boolean anulada, String sii){
    	JSONArray array = new JSONArray();
 
    	AON.getSiiInvoiceStream(domain.getName(), domain.getId(), login, 	
    			f -> iFilterEmitidas(domain, login, f, from, page, perPage, sii)   			
    			,pending, sent, sent_error, error, anulada, sii)
    		.forEach(rm -> {
    			JSONObject json = ToJSON.invoiceToJSON(rm);
    			json.put(MSG.SII_SENT, true);
    			json.put(MSG.SII, "emitida");
    			array.put(json);
    		});
    	return array;
    }
    
    public static Filter iFilterEmitidas(Domain domain, String login, InvoiceProperties f, Date from, Integer page, Integer perPage, String sii) {
    	ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
    	Boolean isRegistro = "R".equals(ap.getValue());
    	Filter filter =  f.getDomainProperty().eq(domain.getId())
		.and(f.getTypeProperty().eq(InvoiceType.SALES.value()))
		.and(isRegistro 
				? f.getCreationDateProperty().ge(new Timestamp(from.getTime()))
				: f.getTaxDateProperty().ge(from));
		
    	if("fe_generales".equals(sii)){
			filter = filter.and(f.getRectificationInvoiceProperty().isNull())
					.and(f.getPosShiftroperty().isNull())
					.and(f.getTransactionProperty().ne(InvoiceTransactionType.INTRACOMMUNITY.value()));
		} else if("fe_simplificadas".equals(sii)){
			filter = filter.and(f.getPosShiftroperty().isNotNull());
		} else if("fe_rectificativas".equals(sii)){
			filter = filter.and(f.getRectificationInvoiceProperty().isNotNull());
		} else if("fe_intracomunitarias".equals(sii)){
			filter = filter.and(f.getTransactionProperty().eq(InvoiceTransactionType.INTRACOMMUNITY.value()));
		} 
    	
    	filter = filter.page(page).perPage(perPage);
		return filter;
    }
    
    public static Filter iFilterRecibidas(Domain domain, String login, InvoiceProperties f, Date from, Integer page, Integer perPage, String sii) {
    	ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
    	Boolean isRegistro = "R".equals(ap.getValue());
    	Filter filter =  f.getDomainProperty().eq(domain.getId())
    			.and(isRegistro 
    				? f.getCreationDateProperty().ge(new Timestamp(from.getTime()))
    				: f.getTaxDateProperty().ge(from));
    	
    	if("fr_recibidas".equals(sii)){
			filter = filter.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()) 
					.or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())));
		} else if("fr_compras".equals(sii)){
			filter = filter.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()));
		} else if("fr_gastos".equals(sii)){
			filter = filter.and(f.getTypeProperty().eq(InvoiceType.EXPENSES.value()));
		} else if("fr_intracomunitarias".equals(sii)){
			filter = filter.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()) 
					.or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())))
					.and(f.getTransactionProperty().eq(InvoiceTransactionType.INTRACOMMUNITY.value()));
		} else if("fr_rectificativas".equals(sii)){
			filter = filter.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()) 
					.or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())))
					.and(f.getRectificationTypeProperty().eq(RectificationType.NORMAL_RECTIFIER.value()));
		} 
    	
    	filter = filter.page(page).perPage(perPage);
		return filter;
    }
    
    private JSONArray getInvoiceRecibidasList(Domain domain, String login, Integer page, Integer perPage, Date from
    		,Boolean pending, Boolean sent, Boolean sent_error, Boolean error, Boolean anulada, String sii){
    	JSONArray array = new JSONArray();
    	AON.getSiiInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> iFilterRecibidas(domain, login, f, from, page, perPage, sii)
    			,pending, sent, sent_error, error, anulada, sii)
    		.forEach(rm ->{
    			JSONObject json = ToJSON.invoiceToJSON(rm);
    			json.put(MSG.SII_SENT, true);
    			json.put(MSG.SII, "recibida");
    			array.put(json);
    		});
    	return array;
    }
	
	private JSONArray getInvoiceBienesList(Domain domain, String login, Integer page, Integer perPage, Date from
			,Boolean pending, Boolean sent, Boolean sent_error, Boolean error, Boolean anulada, String sii){
		JSONArray array = new JSONArray();
    	AON.getSiiInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value())
   					.or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())))
    			.and(f.getInvestmentProperty().eq((byte) 1))
    			.page(page).perPage(perPage)
    			,pending, sent, sent_error, error, anulada, sii)
    		.forEach(rm -> {
    			JSONObject json = ToJSON.invoiceToJSON(rm);
    			json.put(MSG.SII_SENT, true);
    			json.put(MSG.SII, "bienes");
    			array.put(json);
    		});
    	return array;
	}
	
	private JSONArray getInvoiceIntracomunitariasList(Domain domain, String login, Integer page, Integer perPage, Date from
			,Boolean pending, Boolean sent, Boolean sent_error, Boolean error, Boolean anulada, String sii){
		ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
    	Boolean isRegistro = "R".equals(ap.getValue());
		JSONArray array = new JSONArray();
    	AON.getSiiInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getTransactionProperty().eq(InvoiceTransactionType.INTRACOMMUNITY.value()))
				.and(isRegistro 
	    				? f.getCreationDateProperty().ge(new Timestamp(from.getTime()))
	    	    		: f.getTaxDateProperty().ge(from))
				.page(page).perPage(perPage)
    			,pending, sent, sent_error, error, anulada, sii)
    		.forEach(rm ->{
    			JSONObject json = ToJSON.invoiceToJSON(rm);
    			json.put(MSG.SII_SENT, true);
    			json.put(MSG.SII, "intracomunitaria");
    			array.put(json);
    		});
    	return array;
	}
    
	public static Filter iFilterCobrosPagos(Domain domain, String login, InvoiceProperties f, Date from, Integer page, Integer perPage, String sii) {
		ApplicationParameter ap = AON.getApplicationParameter(domain.getName(), domain.getId(), login, AppParam.FS_MODEL_CFG_SII);
    	Boolean isRegistro = "R".equals(ap.getValue());	
    	Filter filter =  f.getDomainProperty().eq(domain.getId())
    			.and(f.getVatAccrualPayment().eq((byte) 1))
    			.and(isRegistro 
    					? f.getCreationDateProperty().ge(new Timestamp(from.getTime()))
    					: f.getTaxDateProperty().ge(from));
	    	
    	if("cp_pagos".equals(sii)){
			filter = filter.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()) 
					.or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())));
		} else if("cp_cobros".equals(sii)){
			filter = filter.and(f.getTypeProperty().eq(InvoiceType.SALES.value()));
		} 
    	filter = filter.page(page).perPage(perPage);
		return filter;
    }
	
	private JSONArray getInvoiceCobrosPagosList(Domain domain, String login, Integer page, Integer perPage, Date from, Boolean pending,Boolean error, Boolean partial, Boolean paid, String sii){
		JSONArray array = new JSONArray();
    	AON.getSiiInvoiceStream(domain.getName(), domain.getId(), login,
    			f ->  iFilterCobrosPagos(domain, login, f, from, page, perPage, sii)
    			,pending, partial, paid, error, false, sii)
    		.forEach(rm ->{
    			JSONObject json = ToJSON.invoiceToJSON(rm);
    			json.put(MSG.SII_SENT, true);
    			json.put(MSG.SII, sii);
    			array.put(json);
    		});
    	return array;
	}
    
	
	private JSONArray getInvoiceList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
       	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getRegistryProperty().eq(registryId)))
       		.forEach(rm -> array.put(ToJSON.invoiceToJSON(rm)));
    	return array;
    }
	
	private JSONArray getInvoiceMovements(Domain domain, String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	AON.getInvoiceDetailStream(domain.getName(), domain.getId(), login,
				f -> invoiceFilter(domain, map, f),
				f -> productFilter(domain, map, f),
				f -> itemFilter(domain, map, f))
			.forEach(detail -> array.put(ToJSON.invoiceDetailFullToJSON(detail)));
    	return array;
    }
    
    private JSONObject getInvoice(Domain domain, String login, Integer id){
    	return ToJSON.invoiceToJSON(AON.getInvoice(domain.getName(),
    			domain.getId(), login, f -> f.getIdProperty().eq(id)));
    }
    
    private JSONArray getFeeList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getFeeStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(fee -> array.put(ToJSON.feeToJSON(fee)));
    	return array;
    }
    
    private JSONArray getFeeList(Domain domain, String login, Integer customerId){
    	JSONArray array = new JSONArray();
       	AON.getFeeStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getCustomerProperty().eq(customerId)))
       		.forEach(fee -> array.put(ToJSON.feeToJSON(fee)));
    	return array;
    }
    
    private JSONObject getFee(Domain domain, String login, Integer id){
    	return ToJSON.feeToJSON(AON.getFee(domain.getName(),
    			domain.getId(), login, f -> f.getIdProperty().eq(id)));
    }
    
    private JSONArray getBoughtProductList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
    	AON.getBoughtProductStream(domain.getName(), domain.getId(), login,
    			f -> f.getRegistryProperty().eq(registryId))
    		.sorted((e1, e2) -> e2.getInvoice().getIssueDate().compareTo(e1.getInvoice().getIssueDate()))
    		.forEach(id -> {
    			JSONObject json = ToJSON.boughtProductToJSON(id);
    			JSONArray ar = new JSONArray();
    			if(id.getItem().getCode() != null){
    				AON.getInvoiceDetails(domain.getName(), domain.getId(), login,
    						f2-> f2.getProductCodeProperty().eq(id.getItem().getCode())
    						.and(f2.getRegistryProperty().eq(id.getInvoice().getRegistry())))
    				.sorted((e1, e2) -> e2.getInvoice().getIssueDate().compareTo(e1.getInvoice().getIssueDate()))
    				.forEach(id2 -> ar .put(ToJSON.boughtProductToJSON(id2)));
    			}
    			json.put("array", ar);
    			array.put(json);
    		});
    	return array;
    }
    
    private JSONArray getBillingPeriodList(){
    	JSONArray array = new JSONArray();
    	for (BillingPeriod period : BillingPeriod.values()) {
			array.put(ToJSON.objectToJSON(period.ordinal(), ToJSON.getPeriod(period)));
		}
    	return array;
    }
     
    public static Filter invoiceFilter(Domain domain, Map<String, String[]> filterMap, InvoiceProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.FROM)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.FROM)[0])));
			filter = filter.and(f.getStartIssueDateProperty().ge(AonDateUtils.toSql(date)));
		}

		if(filterMap.containsKey(MSG.TO)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.TO)[0])));
			filter = filter.and(f.getEndIssueDateProperty().le(AonDateUtils.toSql(date)));
		}
		
		if(filterMap.containsKey(MSG.TYPE)){
			filter = filter.and(f.getTypeProperty().eq(new Byte(filterMap.get(MSG.TYPE)[0])));
		}
		
		return filter;
	}
    
    public static Filter productFilter(Domain domain, Map<String, String[]> filterMap, ProductProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.PRODUCT)){
			Integer[] ids = Arrays.stream(filterMap.get(MSG.PRODUCT)).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
			filter = filter.and(f.getIdProperty().in(ids));
		}
		
		if(filterMap.containsKey("product_id")){
			Integer[] ids = Arrays.stream(filterMap.get("product_id")).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
			filter = filter.and(f.getIdProperty().in(ids));
		}
		
		if(filterMap.containsKey(MSG.CATEGORY)){
			Integer[] ids = Arrays.stream(filterMap.get(MSG.CATEGORY)).mapToInt(Integer::parseInt).boxed().toArray(Integer[]::new);
			filter = filter.and(f.getCategoryProperty().in(ids));
			
		}
		
		return filter;
	}
    
    public static Filter itemFilter(Domain domain, Map<String, String[]> filterMap, ItemProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.ITEM)){
			Integer id = Integer.parseInt(filterMap.get(MSG.ITEM)[0]);
			filter = filter.and(f.getIdProperty().eq(id));
		}
		
		if(filterMap.containsKey("serial_number")){
			filter = filter.and(f.getSerialNumberProperty().like("%"+filterMap.get("serial_number")[0]+"%"));
		}
		
		return filter;
	}
}

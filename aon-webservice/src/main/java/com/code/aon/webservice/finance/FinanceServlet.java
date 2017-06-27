package com.code.aon.webservice.finance;
import java.io.IOException;
import java.util.Date;
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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.finance.InvoiceProperties;
import com.esferalia.aon.occam.api.model.type.BillingPeriod;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
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
		String userName = pathInfo[2];
		String domainName = pathInfo[1]; 
		String md5 = Utils.getMd5(userName+domainName);
		
		if(accessToken.equals(md5)){
			Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
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
    	if(req.getParameterMap().containsKey("sii")){
    		Integer page =  req.getParameterMap().containsKey("page") ? Integer.parseInt(req.getParameter("page")) : 1;
    		Integer perPage = req.getParameterMap().containsKey("per_page") ? Integer.parseInt(req.getParameter("per_page")) : 40;
    		Date from =  req.getParameterMap().containsKey("from") ? new Date(Long.parseLong(req.getParameter("from"))) : AonDateUtils.getDate(2017, 07, 01);  
    		Boolean pending = req.getParameterMap().containsKey("pending") ? req.getParameter("pending").equalsIgnoreCase("true") : false;
    		Boolean sent = req.getParameterMap().containsKey("sent") ? req.getParameter("sent").equalsIgnoreCase("true") : false;

    		if("emitidas".equals(req.getParameter("sii"))){
    	    	if(from.compareTo(AonDateUtils.getDate(2017, 0, 1)) < 0){
    	    		from = AonDateUtils.getDate(2017, 0, 1);    				
    	    	}
    			return getInvoiceEmitidasList(domain, login, page, perPage, from, pending, sent);
    		} else if("recibidas".equals(req.getParameter("sii"))){
    			if(from.compareTo(AonDateUtils.getDate(2017, 0, 1)) < 0){
    	    		from = AonDateUtils.getDate(2017, 0, 1);    				
    	    	}
    			return getInvoiceRecibidasList(domain, login, page, perPage, from, pending, sent);
    		} else if("bienes".equals(req.getParameter("sii"))){
    			return getInvoiceBienesList(domain, login, page, perPage);
    		} else if("intracomunitarias".equals(req.getParameter("sii"))){
    			return getInvoiceIntracomunitariasList(domain, login, page, perPage);
    		}
    	}
    	JSONArray array = new JSONArray();
    	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(rm -> array.put(ToJSON.invoiceToJSON(rm)));
    	return array;
    }
   
    private JSONArray getInvoiceEmitidasList(Domain domain, String login, Integer page, Integer perPage, Date from
    		,Boolean pending, Boolean sent){
    	JSONArray array = new JSONArray();
    	Boolean onlyPending = pending && !sent;
    	Boolean onlySent = !pending && sent;
    	AON.getInvoiceStream(domain.getName(), domain.getId(), login, 	
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getTypeProperty().eq(InvoiceType.SALES.value()))
    			.and(f.getTaxDateProperty().ge(from))
    			.and(iFilter(domain, login,onlyPending, onlySent, f))
    			.page(page).perPage(perPage))
    		.forEach(rm -> {
    			JSONObject json = ToJSON.invoiceToJSON(rm);
    			Boolean sent1 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataVariableProperty().eq("invoice_sum_ok").and(f.getValueProperty().eq(rm.getId().toString()))).isPresent();
    			json.put("sii_sent", sent1);
    			array.put(json);
    		});
    	return array;
    }
    
    public static Filter iFilter(Domain domain, String login,Boolean pending, Boolean sent, InvoiceProperties f) {
    	Filter filter =  f.getDomainProperty().eq(domain.getId());
		if(sent){
			Integer[] a = AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, h -> 
			h.getDomainProperty().eq(domain.getId()).and(h.getDataVariableProperty().eq("invoice_sum_ok")))
			.map(r -> Integer.parseInt(r.getValue())).toArray(Integer[]::new);
			filter = filter.and(f.getIdProperty().in(a));
		} else if(pending){
			Integer[] a = AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, h -> 
			h.getDomainProperty().eq(domain.getId()).and(h.getDataVariableProperty().eq("invoice_sum_ok")))
			.map(r -> Integer.parseInt(r.getValue())).toArray(Integer[]::new);
			filter = filter.and(f.getIdProperty().notIn(a));
		}
		return filter;
    }
    
    private JSONArray getInvoiceRecibidasList(Domain domain, String login, Integer page, Integer perPage, Date from
    		,Boolean pending, Boolean sent){
    	JSONArray array = new JSONArray();
    	Boolean onlyPending = pending && !sent;
    	Boolean onlySent = !pending && sent;
    	
    	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()) 
    					.or(f.getTypeProperty().eq(InvoiceType.EXPENSES.value())))
    			.and(f.getTaxDateProperty().ge(from))
    			.and(iFilter(domain, login,onlyPending, onlySent, f))
    			.page(page).perPage(perPage))
    		.forEach(rm ->{
    			JSONObject json = ToJSON.invoiceToJSON(rm);
    			Boolean sent1 = AON.getDataResponseDetail(domain.getName(), domain.getId(), login, f -> f.getDataVariableProperty().eq("invoice_sum_ok").and(f.getValueProperty().eq(rm.getId().toString()))).isPresent();
    			json.put("sii_sent", sent1);
    			array.put(json);
    		});
    	return array;
    }
	
	private JSONArray getInvoiceBienesList(Domain domain, String login, Integer page, Integer perPage){
		JSONArray array = new JSONArray();
    /*	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getTypeProperty().eq(InvoiceType.PURCHASE.value()))
    			.and(f.getTransactionProperty().ne(InvoiceTransactionType.INTRACOMMUNITY.value()))
    			.and(f.getTaxDateProperty().ge(AonDateUtils.addDays(new Date(), -7)))
    			.and(f.getTaxDateProperty().le(AonDateUtils.addDays(new Date(), 1)))
    			.page(page).perPage(perPage))
    		.forEach(rm -> array.put(ToJSON.invoiceToJSON(rm)));
    */	return array;
	}

	private JSONArray getInvoiceIntracomunitariasList(Domain domain, String login, Integer page, Integer perPage){
    	JSONArray array = new JSONArray();
    /*	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    				.and(f.getTransactionProperty().eq(InvoiceTransactionType.INTRACOMMUNITY.value()))
    				.page(page).perPage(perPage))
    		.forEach(rm -> array.put(ToJSON.invoiceToJSON(rm)));
    */	return array;
	}
    
    private JSONArray getInvoiceList(Domain domain, String login, Integer registryId){
    	JSONArray array = new JSONArray();
       	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId())
    			.and(f.getRegistryProperty().eq(registryId)))
       		.forEach(rm -> array.put(ToJSON.invoiceToJSON(rm)));
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
}

package com.code.aon.webservice.finance;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.issues.Utils;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;

@SuppressWarnings("serial")
@WebServlet(name = "FinanceServlet", urlPatterns = { "/finance/*" })
public class FinanceServlet extends HttpServlet{
		
	String h = "http://";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
		if(req.getServerPort() == 80) h = "http://";
		else if(req.getServerPort() == 443) h = "https://";
		String accessToken = req.getParameter("access_token");
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
				case "invoice": // INVOICE
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5) // LISTA DE INVOICE CON REGISTRY X
								object = getInvoiceList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(pathInfo[4].equals("id")) {
							if(pathInfo.length > 5) // INVOICE CON ID X
								object = getInvoice(domain, userName, Integer.parseInt(pathInfo[5])); 
						}
					} else {// LISTA DE INVOICE CONDICION DOMAIN
						getInvoiceList(domain, userName);
					}
					break;
				case "fee": // FEE
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("customer")) {
							if(pathInfo.length > 5) // LISTA DE FEE CON CUSTOMER X
								object = getFeeList(domain, userName, Integer.parseInt(pathInfo[5]));
						} else if(pathInfo[4].equals("id")) {
							if(pathInfo.length > 5) // FEE CON ID X
								object = getFee(domain, userName, Integer.parseInt(pathInfo[5])); 
						}
					} else {// LISTA DE INVOICE CONDICION DOMAIN
						getFeeList(domain, userName);
					}
					break;
				case "bought_product": // INVOICE
					if(pathInfo.length > 4){
						if (pathInfo[4].equals("registry")) {
							if(pathInfo.length > 5) // LISTA DE INVOICE CON REGISTRY X
								object = getBoughtProductList(domain, userName, Integer.parseInt(pathInfo[5]));
						}
					}
					break;
				default:
					break;
				}
				String js = req.getParameter("callback");
				if(js != null){
					resp.setContentType("application/javascript; charset=utf-8");     
					PrintWriter out = resp.getWriter();
					out.print(js + "({" +"\"meta\":"+ meta +", \"data\":" + object +"});");
					out.flush();
				} else {
					resp.setContentType("application/json");     
					PrintWriter out = resp.getWriter();
					out.print(object);
					out.flush();
				}
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
	}

    private JSONArray getInvoiceList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getInvoiceStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(rm -> array.put(ToJSON.invoiceToJSON(rm)));
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
    		.forEach(id -> {
    			JSONObject json = ToJSON.boughtProductToJSON(id);
    			JSONArray ar = new JSONArray();
    			if(id.getItem().getCode() != null)
    				AON.getOldBoughtProductStream(domain.getName(), domain.getId(), login,
    						f2-> f2.getProductCodeProperty().eq(id.getItem().getCode())
    						.and(f2.getRegistryProperty().eq(registryId)))
    					.forEach(id2 -> {
    						if(id.getId() != id2.getId())	
    							ar .put(ToJSON.boughtProductToJSON(id2));	
    					});
    			json.put("array", ar);
    			array.put(json);
    		});
    	return array;
    }
}

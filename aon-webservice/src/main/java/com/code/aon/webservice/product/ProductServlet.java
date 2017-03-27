package com.code.aon.webservice.product;
import java.io.IOException;

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

@SuppressWarnings("serial")
@WebServlet(name = "ProductServlet", urlPatterns = { "/product/*",
													 "/aon_gwt_aio/product/*"})
public class ProductServlet extends HttpServlet{
		
	String h = "http://";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
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
				case "category": // PRODUCT CATEGPRY
					if(pathInfo.length > 4){
						
					} else {// LISTA DE PRODUCT CATEGPRY
						object = getCategoryList(domain, userName);
					}
					break;
				case MSG.ITEM:
					if(pathInfo.length > 4){
						object = getItem(domain, userName, Integer.parseInt(pathInfo[4]));
					} else {
						object = getItemList(domain, userName);
					}
					break;
				default:
					Integer id;
					try {
						id = Integer.parseInt(pathInfo[3]);
						object = getProduct(domain, userName, id);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
					}
					break;
				}
				
				Utils.giveBack(req, resp, object, meta);
			}
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("POST METHOD");
	}

    private JSONArray getCategoryList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getProductCategoryStream(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    		.forEach(pc -> array.put(ToJSON.productCategoryToJSON(pc)));
    	return array;
    }
    
    private Object getProduct(Domain domain, String userName, int id) {
		return ToJSON.productToJSON(AON.getProduct(domain.getName(), domain.getId(), userName, id));
    }
    
    private Object getItem(Domain domain, String userName, int id) {
    	return ToJSON.itemToJSON(AON.getItem(domain.getName(), domain.getId(), userName, id));
    }
    
    private JSONArray getItemList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getItemList(domain.getName(), domain.getId(), login,
    			f -> f.getDomainProperty().eq(domain.getId()))
    			.forEach(i -> array.put(ToJSON.itemToJSON(i)));
    	return array;
    }
  
}

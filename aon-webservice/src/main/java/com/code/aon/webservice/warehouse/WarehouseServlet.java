package com.code.aon.webservice.warehouse;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Date;

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
import com.esferalia.aon.occam.api.model.warehouse.CarrierPacking;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingStatus;
import com.esferalia.aon.occam.api.model.warehouse.CarrierPackingType;

@SuppressWarnings("serial")
@WebServlet(name = "WarehouseServlet", urlPatterns = { "/warehouse/*",
													 "/aon_gwt_aio/warehouse/*"})
public class WarehouseServlet extends HttpServlet{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		System.out.println("GET METHOD");
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
				case "carrier_packing": // PRODUCT CATEGPRY
					if(pathInfo.length > 4){
						if("series".equals(pathInfo[4])){
							
						} else if("type".equals(pathInfo[4])){
							
						} else if("status".equals(pathInfo[4])){
							
						} else if("carrier".equals(pathInfo[4])){
							object = getCarrierList(domain, userName);
						}
					} else {// LISTA DE PRODUCT CATEGPRY
						object = getCarrierPackingList(domain, userName);
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
		String line = "";
		String s = "";
		while((line = req.getReader().readLine()) != null)
			s = s + " " + line;
		System.out.println(s);
		s = Utils.checkString(s);
		System.out.println(s);
		if(s == null || s.equals("")) s = "{}";
		JSONObject json = new JSONObject(s);
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			switch (pathInfo[3]) {
			case "carrier_packing":
				if(pathInfo.length > 4){ 
					if("update".equals(pathInfo[4])){
						object = updateCarrierPacking(domain, userName, Integer.parseInt(pathInfo[5]), json);
					} else if("delete".equals(pathInfo[4])){
						AON.deleteCarrierPacking(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[5]));
					} 
				} else {
					object = insertCarrierPacking(domain, userName, json);
				}
				break;
			default:
				break;
			}
				
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
		}
	}

	private JSONObject insertCarrierPacking(Domain domain, String login, JSONObject json) {
		CarrierPacking carrierPacking = AON.insertCarrierPacking(domain.getName(), domain.getId(), login, new CarrierPacking());
		return ToJSON.carrierPackingToJSON(carrierPacking);
	}
	
	private JSONObject updateCarrierPacking(Domain domain, String login, Integer id, JSONObject json) {
		CarrierPacking carrierPacking = getCarrierPacking(domain, login, id, json);
		carrierPacking = AON.updateCarrierPacking(domain.getName(), domain.getId(), login, carrierPacking);
		return ToJSON.carrierPackingToJSON(carrierPacking);
	}
	
	private JSONArray getCarrierList(Domain domain, String login) {
    	JSONArray array = new JSONArray();
		AON.getCarrierStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId()))
		.forEach(c -> {
			JSONObject json = new JSONObject();
			json.put("id", c.getId());
			json.put("name", c.getName());
			array.put(json);
		});
		return array;
	}
	
	private CarrierPacking getCarrierPacking(Domain domain, String login, Integer id, JSONObject json) {
		CarrierPacking carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, id);

		if(json.opt("series") != null){
			carrierPacking.setSeries(json.getString("series"));
			carrierPacking.setNumber(AON.getCarrierPackingStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getSeriesProperty().eq(carrierPacking.getSeries()))).sorted((n1, n2) -> n2.getNumber().compareTo(n1.getNumber()))
				.map(r -> r.getNumber()).findFirst().orElse(1));
		}
		if(json.opt("number") != null){
			carrierPacking.setNumber(json.getInt("number"));
		}
		if(json.opt("type") != null){
			carrierPacking.setType(CarrierPackingType.values()[json.getInt("type")]);
		}
		if(json.opt("status") != null){
			carrierPacking.setStatus(CarrierPackingStatus.values()[json.getInt("status")]);
		}
		if(json.opt("issue_date") != null){
			carrierPacking.setIssueDate(new Date(json.getLong("issue_date")));
		}
		if(json.opt("carrier") != null){
			carrierPacking.setNumber(json.getInt("carrier"));
		}
		if(json.opt("delivery_date") != null){
			carrierPacking.setDeliveryDate(new Date(json.getLong("delivery_date")));
		}
		if(json.opt("carrier_reference") != null){
			carrierPacking.setCarrierReference(json.getString("carrier_reference"));
		}
		if(json.opt("number_plate") != null){
			carrierPacking.setNumberPlate(json.getString("number_plate"));
		}
		if(json.opt("driver_document") != null){
			carrierPacking.setDriverDocument(json.getString("driver_document"));
		}
		if(json.opt("driver_name") != null){
			carrierPacking.setDriverName(json.getString("driver_name"));
		}
	
		return carrierPacking;
	}
	
    private JSONArray getCarrierPackingList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getCarrierPackingStream(domain.getName(), domain.getId(), login,
    		f ->f.getDomainProperty().eq(domain.getId())).forEach(cp -> {
    			JSONObject json = new JSONObject();
    			json.put("name", cp.getSeries());
    			json.put("id", cp.getId());
    			json.put("type", cp.getType());
    			array.put(json);
    		});
    	return array;
    }
  
}

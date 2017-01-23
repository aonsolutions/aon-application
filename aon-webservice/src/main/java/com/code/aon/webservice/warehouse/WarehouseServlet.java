package com.code.aon.webservice.warehouse;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
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

	private static final Logger LOGGER  = Logger.getLogger(WarehouseServlet.class.getName());
	private static final String CARRIER_PACKING = "carrier_packing";
	private static final String SERIES = "series";
	private static final String TYPE = "type";
	private static final String STATUS = "status";
	private static final String CARRIER = "carrier";
	private static final String EMPTY = "";
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Warehouse Servlet - GET METHOD");
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
				if(CARRIER_PACKING.equals(pathInfo[3])){ // PRODUCT CATEGPRY
					if(pathInfo.length > 4){
						if(SERIES.equals(pathInfo[4])){
							object = getSeriesList();
						} else if(TYPE.equals(pathInfo[4])){
							object = getCarrierPackingTypeList();
						} else if(STATUS.equals(pathInfo[4])){
							object = getCarrierPackingStatusList();
						} else if(CARRIER.equals(pathInfo[4])){
							object = getCarrierList(domain, userName);
						} else if("purchase".equals(pathInfo[4])){
							object = getPurchaseList(domain, userName, req.getParameterMap());
						}
					} else {// LISTA DE PRODUCT CATEGPRY
						object = getCarrierPackingList(domain, userName);
					}
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
		LOGGER.info("Warehouse Servlet - POST METHOD");
		String line = "";
		String s = "";
		while((line = req.getReader().readLine()) != null)
			s = s + " " + line;
		s = Utils.checkString(s);
		if(s == null || s.equals("")) s = "{}";
		JSONObject json = new JSONObject(s);
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			if(CARRIER_PACKING.equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					if("update".equals(pathInfo[4])){
						object = updateCarrierPacking(domain, userName, Integer.parseInt(pathInfo[5]), json);
					} else if("delete".equals(pathInfo[4])){
						AON.deleteCarrierPacking(domain.getName(), domain.getId(), userName, Integer.parseInt(pathInfo[5]));
					} 
				} else {
					object = insertCarrierPacking(domain, userName, json);
				}	
			}
				
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}

	private JSONObject insertCarrierPacking(Domain domain, String login, JSONObject json) {
		CarrierPacking carrierPacking = getCarrierPacking(domain, login, json, new CarrierPacking());
		carrierPacking = AON.insertCarrierPacking(domain.getName(), domain.getId(), login, carrierPacking);
		return ToJSON.carrierPackingToJSON(carrierPacking);
	}
	
	private JSONObject updateCarrierPacking(Domain domain, String login, Integer id, JSONObject json) {
		CarrierPacking carrierPacking = AON.getCarrierPacking(domain.getName(), domain.getId(), login, id);
		carrierPacking = getCarrierPacking(domain, login, json, carrierPacking);
		AON.updateCarrierPacking(domain.getName(), domain.getId(), login, carrierPacking);
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
	
	private CarrierPacking getCarrierPacking(Domain domain, String login, JSONObject json, CarrierPacking carrierPacking) {
		if(json.opt("series") != null){
			carrierPacking.setSeries(json.getString("series"));
			carrierPacking.setNumber(AON.getCarrierPackingStream(domain.getName(), domain.getId(), login, f -> f.getDomainProperty().eq(domain.getId())
				.and(f.getSeriesProperty().eq(carrierPacking.getSeries()))).sorted((n1, n2) -> n2.getNumber().compareTo(n1.getNumber()))
				.map(r -> r.getNumber()).findFirst().orElse(1));
		}
		if(json.opt("number") != null && !EMPTY.equals(json.opt("number"))){
			carrierPacking.setNumber(json.getInt("number"));
		}
		if(json.opt("type") != null && !EMPTY.equals(json.opt("type"))){
			carrierPacking.setType(CarrierPackingType.values()[json.getInt("type")]);
		}
		if(json.opt("status") != null && !EMPTY.equals(json.opt("status"))){
			carrierPacking.setStatus(CarrierPackingStatus.values()[json.getInt("status")]);
		}
		if(json.opt("issue_date") != null && !EMPTY.equals(json.opt("issue_date"))){
			carrierPacking.setIssueDate(new Date(json.getLong("issue_date")));
		}
		if(json.opt("carrier") != null && !EMPTY.equals(json.opt("carrier"))){
			carrierPacking.setCarrier(json.getInt("carrier"));
		}
		if(json.opt("delivery_date") != null && !EMPTY.equals(json.opt("delivery_date"))){
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
    		f ->f.getDomainProperty().eq(domain.getId()))
    	.forEach(cp -> array.put(ToJSON.carrierPackingToJSON(cp)));
    	return array;
    }
    
    private JSONArray getCarrierPackingTypeList() {
    	JSONArray array = new JSONArray();
    	for(CarrierPackingType cpt :CarrierPackingType.values()){
    		JSONObject json = new JSONObject();
    		json.put("name", cpt.getName());
			json.put("id", cpt.value());
			array.put(json);
    	}
    	return array;
    }
    
    private JSONArray getPurchaseList(Domain domain,String login, Map<String, String[]> map){
    	JSONArray array = new JSONArray();
    	// TODO
    	return array;
    }
    
    private JSONArray getCarrierPackingStatusList() {
    	JSONArray array = new JSONArray();
    	for(CarrierPackingStatus cps :CarrierPackingStatus.values()){
    		JSONObject json = new JSONObject();
    		json.put("name", cps.getName());
			json.put("id", cps.value());
			array.put(json);
    	}
    	return array;
    }
    
    private JSONArray getSeriesList() {
    	JSONArray array = new JSONArray();
    	
    	JSONObject json = new JSONObject();
    	json.put("name", "2016");
    	json.put("id", 2016);
    	array.put(json);
    	
    	JSONObject json2 = new JSONObject();
    	json2.put("name", "2015");
    	json2.put("id", 2015);
    	array.put(json2);
    	
    	return array;
    }
  
}

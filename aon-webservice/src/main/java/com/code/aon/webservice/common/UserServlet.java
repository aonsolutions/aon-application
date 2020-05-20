package com.code.aon.webservice.common;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.DataResponse;
import com.esferalia.aon.occam.api.model.DataResponseDetail;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Properties.DataResponseProperties;
import com.esferalia.aon.occam.api.model.security.UserScope;
import com.esferalia.aon.occam.api.model.type.DataResponseSource;
import com.esferalia.aon.occam.api.model.warehouse.Income;
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "UserServlet", urlPatterns = {"/ms/user/*",
												   "/aon_gwt_aio/ms/user/*",
												   "/aon_gwt_commercial/ms/user/*",
												   "/aon_gwt_fiscal/ms/user/*"})
public class UserServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(UserServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("User Servlet - GET METHOD");
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Integer domainId = Integer.parseInt(req.getParameter(MSG.DOMAIN));
//		String token = req.getHeader("session_id");
//		Integer domainId = Integer.parseInt(req.getHeader("domain_id"));
//		String domainName = req.getHeader("domain_name");

		Domain domain = AON.getDomain(domainName, domainId, userName);
				
		JSONArray arr = new JSONArray();
		AON.getUsers(domain.getId(), domain.getName(), "").stream().forEach(u -> {
			arr.put(ToJSON.userToJSON(u));
		});
		
		Utils.addCorsHeader(resp);
		Utils.giveBack(req, resp, arr, new JSONObject());
		
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("Common Servlet - POST METHOD");
		JSONObject json = Utils.getRequestJSON(req);

		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			Object object = new Object();
			if("app_param".equals(pathInfo[3])){
				if(pathInfo.length > 4){ 
					if(MSG.UPDATE.equals(pathInfo[4])){
						object = new JSONObject();
					} else if(MSG.DELETE.equals(pathInfo[4])){
						Integer id = json.getInt("id");
						AON.deleteApplicationParameter(domain.getName(), domain.getId(), userName, f-> 
							f.getIdProperty().eq(id).and(f.getDomainProperty().eq(domain.getId())));
						object = new JSONObject();
					} 
				} else {
					object = insertAppParam(domain, userName, json);
				}	
			} else if(MSG.DATA_RESPONSE.equals(pathInfo[3])){
				if(pathInfo.length > 4){
					if(MSG.DETAIL.equals(pathInfo[4])){
						object = insertDataResponseDetail(domain, userName, json);
					}
				} else {
					object = insertDataResponse(domain, userName, json);
				}
			} else if((MSG.DATA_RESPONSE + "2").equals(pathInfo[3])){
				if(pathInfo.length > 4){
					if(MSG.DETAIL.equals(pathInfo[4])){
						object = insertDataResponseDetail(domain, userName, json);
					}
				} else {
					object = insertDataResponse2(domain, userName, json);
				}
			} else if("copyUserScope".equals(pathInfo[3])){
				if(pathInfo.length > 5){
					Integer userId = Integer.parseInt(pathInfo[4]);
					Integer copyUserId = Integer.parseInt(pathInfo[5]);
					copyUserScope(domain, userName, userId, copyUserId);
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
	

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("User Servlet - DELETE METHOD");
	
		String[] pathInfo = req.getPathInfo().split("/");
		String domainName = pathInfo[1]; 
		String userName = pathInfo[2];
		Domain domain = AON.getDomain(domainName, 1, userName, f->f.getNameProperty().eq(domainName));
		if(pathInfo.length > 3){				
			JSONObject object = new JSONObject();
			
			if("deleteScope".equals(pathInfo[3])){
				if(pathInfo.length > 5){
					Integer userId = Integer.parseInt(pathInfo[4]);
					Integer copyUserId = Integer.parseInt(pathInfo[5]);
					deleteUserScopes(domain, userName, userId, copyUserId);
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
	
	private JSONArray getDataResponseList2(Domain domain, String login, Map<String,String[]> map) {
		JSONArray array = new JSONArray();
		AON.getDataResponseStream(domain.getName(), domain.getId(), login, null,
				f -> map.containsKey("filter2") ? dataResponseFilter2(domain, map, f) : dataResponseFilter(domain, map, f))
		.sorted((dr1, dr2) -> dr2.getResponseDate().compareTo(dr1.getResponseDate()))
		.forEach(dr -> {
			JSONObject json = ToJSON.dataResponseToJSON(dr);
			JSONArray jsarray = new JSONArray(); 
			AON.getDataResponseDetailStream(domain.getName(), domain.getId(), login, f -> f.getDataResponseProperty().eq(dr.getId())).forEach(r -> {
				jsarray.put(ToJSON.dataResponseDetailToJSON(r));
			});
			json.put("detail", jsarray);
			array.put(json);			
		});
		return array;
	}

	private JSONArray getDataResponseList(Domain domain, String login, Map<String,String[]> map){
		if(map.containsKey("filter2")) {
			return getDataResponseList2(domain, login, map);
		}
		JSONArray array = new JSONArray();
		AON.getDataResponseStream(domain.getName(), domain.getId(), login, null,
				f -> dataResponseFilter(domain, map, f))
		.sorted((dr1, dr2) -> dr2.getResponseDate().compareTo(dr1.getResponseDate()))
		.forEach(dr -> {
			String source = AON.getDataResponseDetail(domain.getName(), domain.getId(), login,
					f -> f.getDataResponseProperty().eq(dr.getId())
					.and(f.getDataVariableProperty().eq("source"))).get().getDataValue();
			String[] s = source.split("@");
			if("income_detail".equals(s[0])){
				Integer id =Integer.parseInt(s[1]);
				Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f2 -> f2.getIdProperty().eq(id));	
				if(incomeDetail.isPresent()){
					Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeDetail.get().getIncome().getId()));
					if(income.isPresent() && (!map.containsKey("supplier") || hasSupplier(map.get("supplier"), income.get().getSupplier().toString()))) {
						JSONObject o = ToJSON.dataResponseToJSON(dr);
						o.put("product", incomeDetail.get().getDescription());
						o.put("supplier", ToJSON.objectToJSON(income.get().getSupplier(),income.get().getSupplierName()));
						array.put(o);	
					}
				}
			}
		});
	    return array;
	}
	
	private Boolean hasSupplier(String[] suppliers, String supplier) {
		LinkedList<String> list = new LinkedList<>(Arrays.asList(suppliers));
		return list.contains(supplier);
	}
	
	public static Filter dataResponseFilter(Domain domain, Map<String, String[]> filterMap, DataResponseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey("from")){
			String from = filterMap.get(MSG.FROM)[0];
			Date d = new Date(Long.parseLong(from));
			Filter fDate = f.getIssueDateProperty().ge(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("to")){
			String to = filterMap.get(MSG.TO)[0];
			Date d = new Date(Long.parseLong(to));
			Filter fDate = f.getIssueDateProperty().le(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey(MSG.NUMBER)){
			Filter fnumber = f.getNumberProperty().eq(filterMap.get(MSG.NUMBER)[0]);
			for(Integer i = 1; i < filterMap.get(MSG.NUMBER).length ; i++){
				fnumber = fnumber.or(f.getNumberProperty().eq(filterMap.get(MSG.NUMBER)[i]));
			}
			filter = filter.and(fnumber);
		} 
		
		if(filterMap.containsKey("code")){
			Filter fcode = f.getNumberProperty().like("%" + filterMap.get("code")[0] + "%");
			filter = filter.and(fcode);
		} 
		
		if(filterMap.containsKey("source")){
			Filter fsourceValue = f.getDetailVariableProperty().eq("source").and(f.getDetailValueProperty().like(filterMap.get("source")[0] + "@%"));				
			for(Integer i = 1; i < filterMap.get("source").length ; i++){
				fsourceValue = fsourceValue.or(f.getDetailVariableProperty().eq("source").and(f.getDetailValueProperty().like(filterMap.get("source")[i] + "@%")));
			}
			filter = filter.and(fsourceValue);
		}
		
		if(filterMap.containsKey("type")){
			Filter ftypeValue = f.getDetailVariableProperty().eq("type").and(f.getDetailValueProperty().eq(filterMap.get("type")[0]));				
			for(Integer i = 1; i < filterMap.get("type").length ; i++){
				ftypeValue = ftypeValue.or(f.getDetailVariableProperty().eq("type").and(f.getDetailValueProperty().like(filterMap.get("type")[i])));
			}
			filter = filter.and(ftypeValue);
		}
		if(filterMap.containsKey("per_page")){
			String per_page = filterMap.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			filter.perPage(perPage);
		}
		if(filterMap.containsKey("page")){
			String page_str = filterMap.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			filter.page(page);
		}
		return filter;
	}
	
	public static Filter dataResponseFilter2(Domain domain, Map<String, String[]> filterMap, DataResponseProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey("from")){
			String from = filterMap.get(MSG.FROM)[0];
			Date d = new Date(Long.parseLong(from));
			Filter fDate = f.getIssueDateProperty().ge(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey("to")){
			String to = filterMap.get(MSG.TO)[0];
			Date d = new Date(Long.parseLong(to));
			Filter fDate = f.getIssueDateProperty().le(AonDateUtils.toSql(d));
			filter = filter.and(fDate);
		}
		
		if(filterMap.containsKey(MSG.NUMBER)){
			Filter fnumber = f.getNumberProperty().eq(filterMap.get(MSG.NUMBER)[0]);
			for(Integer i = 1; i < filterMap.get(MSG.NUMBER).length ; i++){
				fnumber = fnumber.or(f.getNumberProperty().eq(filterMap.get(MSG.NUMBER)[i]));
			}
			filter = filter.and(fnumber);
		} 
		
		if(filterMap.containsKey("code")){
			Filter fcode = f.getNumberProperty().like("%" + filterMap.get("code")[0] + "%");
			filter = filter.and(fcode);
		} 
		
		if(filterMap.containsKey("source")){
			Filter fsourceValue = f.getSourceProperty().eq((byte) Integer.parseInt(filterMap.get("source")[0]));	
			filter = filter.and(fsourceValue);
		}
		
		if(filterMap.containsKey("source_id")){
			Filter fsourceValue = f.getSourceIdProperty().eq(Integer.parseInt(filterMap.get("source_id")[0]));	
			filter = filter.and(fsourceValue);
		}
		
		if(filterMap.containsKey("type")){
			Filter ftypeValue = f.getDetailVariableProperty().eq("type").and(f.getDetailValueProperty().eq(filterMap.get("type")[0]));				
			for(Integer i = 1; i < filterMap.get("type").length ; i++){
				ftypeValue = ftypeValue.or(f.getDetailVariableProperty().eq("type").and(f.getDetailValueProperty().like(filterMap.get("type")[i])));
			}
			filter = filter.and(ftypeValue);
		}
		if(filterMap.containsKey("per_page")){
			String per_page = filterMap.get("per_page")[0];
			Integer perPage = Integer.parseInt(per_page);
			filter.perPage(perPage);
		}
		if(filterMap.containsKey("page")){
			String page_str = filterMap.get("page")[0];
			Integer page = Integer.parseInt(page_str);
			filter.page(page);
		}
		return filter;
	}
	
    private JSONArray getWorkplaceList(Domain domain, String login){
    	JSONArray array = new JSONArray();
    	AON.getWorkplaceList(domain.getName(), domain.getId(), login, 
    			f -> f.getDomainProperty().eq(domain.getId()))
    	.stream().forEach(wp -> 
    		array.put(ToJSON.objectToJSON(wp.getId(), wp.getDescription())));
    	return array;
    }
    
    private JSONArray getMailAccountList(Domain domain, String login) {		
    	JSONArray array = new JSONArray();
    	Integer userId = AON.getUser(domain.getName(), domain.getId(), login).getId();
    	if(domain.isEnableHeredity())
    		AON.getMailAccountList(domain.getName(), domain.getId(), login, f -> 
    			(f.getUserIdProperty().isNull().or(f.getUserIdProperty().eq(userId)))
				.and(f.getDomainProperty().eq(domain.getId()).or(f.getDomainProperty().eq(domain.getParentId()))))
			.stream().forEach(ma -> array.put(ToJSON.objectToJSON(ma.getId(), ma.getName())));
		else AON.getMailAccountList(domain.getName(), domain.getId(), login, f -> 
				(f.getUserIdProperty().isNull().or(f.getUserIdProperty().eq(userId)))
				.and(f.getDomainProperty().eq(domain.getId())))
			.stream().forEach(ma -> array.put(ToJSON.objectToJSON(ma.getId(), ma.getName())));
    	return array;
	}
    
	public  JSONArray getSignatureList(Domain domain, String login){
		JSONArray array = new JSONArray();
		AON.getSignatureList(domain.getName(), domain.getId(), login,
				f -> f.getUserIdProperty().isNull().and(f.getDomainProperty().eq(domain.getId())))
		.stream().forEach(s -> array.put(ToJSON.objectToJSON(s.getId(), s.getName())));
		return array;
	}
  
	private JSONObject insertAppParam(Domain domain, String login, JSONObject json) {
		String parameter = json.getString("parameter");
		String value = json.getString("value");
		AON.insertApplicationParameter(domain.getName(), domain.getId(), login, parameter, value);
		return new JSONObject();
	}
	
	
	private JSONObject insertDataResponse2(Domain domain, String login, JSONObject json) {
		DataResponse dataResponse = new DataResponse()
				.setDomain(domain.getId())
				.setCode(json.optString("code").isEmpty() ? "" : json.getString("code"))
				.setResponseDate(json.optString("date").isEmpty() ? new Date() : AonDateUtils.dateTimeParse(json.getString("date")))
				.setSource(DataResponseSource.values()[json.getInt("source")])
				.setSourceId(json.getInt("source_id"));
		
		DataResponse dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, dataResponse);
		return ToJSON.dataResponseToJSON(dr);
	}
	
	private JSONObject insertDataResponse(Domain domain, String login, JSONObject json) {
		DataResponse dataResponse = null;
		if(json.opt("order") != null) {
			Integer incomeId = json.optInt("order");
			Optional<Income> income = AON.getIncome(domain.getName(), domain.getId(), login, f -> f.getIdProperty().eq(incomeId));

			dataResponse = new DataResponse()
					.setSource(DataResponseSource.QUALITY)// TODO 
					.setSourceId(json.optInt("order_detail"))
					.setCode(income.get().getReferenceCode())
					.setDomain(domain.getId())
					.setResponseDate(income.get().getIssueDate());
		} else {

			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
			Date date = new Date();
			try {
				date = dateFormat.parse(json.getString("issue_date"));
			} catch (JSONException | ParseException e) {
				e.printStackTrace();
			}
			String idStr = json.getString("source").split("@")[1];
			
			dataResponse = new DataResponse()
					.setSource(DataResponseSource.QUALITY)// TODO 
					.setSourceId(Integer.parseInt(idStr))
					.setCode(json.getString("number"))
					.setDomain(domain.getId())
					.setResponseDate(date);
		}
				
		DataResponse dr = AON.insertDataResponse(domain.getName(), domain.getId(), login, dataResponse);
		return ToJSON.dataResponseToJSON(dr);
	}
	
	private JSONObject insertDataResponseDetail(Domain domain, String login, JSONObject json) {
		Integer dataResponseId = json.getInt("data_response");
		Iterator<String> it = json.keys();
		while(it.hasNext()){
			String key = it.next();
			if(!key.equals("data_response")){
				DataResponseDetail drd = new DataResponseDetail()
						.setDomain(domain.getId())
						.setDataResponse(dataResponseId)
						.setDataVariable(key)
						.setDataValue(json.getString(key));
				AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
			}
		}
		return new JSONObject();
	}
	
	private void copyUserScope(Domain domain, String login, Integer userId, Integer copyUserId) {
		Integer[] scopes = AON.getUserScopes(domain.getName(), domain.getId(), login, copyUserId);
		for (Integer scope : scopes) {
			UserScope us = AON.getUserScope(domain.getName(), domain.getId(), login, userId, scope);
			if(us == null || us.getId() == null) {
				AON.insertUserScope(domain.getName(), domain.getId(), login, new UserScope()
					.setDomain(domain.getId())
					.setScope(scope)
					.setUserId(userId));
			}
		}
	}
	
	private void deleteUserScopes(Domain domain, String login, Integer userId, Integer copyUserId) {
		Integer[] scopes = AON.getUserScopes(domain.getName(), domain.getId(), login, copyUserId);
		for (Integer scope : scopes) {
			AON.deleteUserScope(domain.getName(), domain.getId(), login, userId, scope);
		}
	}

}

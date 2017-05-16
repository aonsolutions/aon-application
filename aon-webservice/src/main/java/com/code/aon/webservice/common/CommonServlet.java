package com.code.aon.webservice.common;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
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
import com.esferalia.aon.occam.api.model.warehouse.IncomeDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

@SuppressWarnings("serial")
@WebServlet(name = "CommonServlet", urlPatterns = { "/common/*",
													 "/aon_gwt_aio/common/*"})
public class CommonServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(CommonServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
		LOGGER.info("Common Servlet - GET METHOD");
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
				case MSG.WORKPLACE:
					object = getWorkplaceList(domain, userName);
					break;
				case MSG.MAIL_ACCOUNT: 
					object = getMailAccountList(domain, userName);
					break;
				case MSG.SIGNATURE: 
					object = getSignatureList(domain, userName);
					break;
				case MSG.APP_PARAM: 
					String param = req.getParameter("param");
					JSONArray array = new JSONArray();
					AON.getApplicationParameterStream(domain.getName(), domain.getId(), userName, f -> 
						f.getDomainProperty().eq(domain.getId()).and(f.getNameProperty().like(param+"%")))
					.forEach(app -> array.put(ToJSON.applicationParameterToJSON(app)));
					object = array;
					break;
				case MSG.DATA_RESPONSE: 
					object = getDataResponseList(domain, userName, req.getParameterMap());
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
			}
			
			resp.setContentType("application/json;charset=UTF-8");
			Utils.addCorsHeader(resp);
			PrintStream os = new PrintStream(resp.getOutputStream(), false, "UTF-8");
			os.println(object.toString());
			os.flush();
			os.close();
		}
	}

	private JSONArray getDataResponseList(Domain domain, String login, Map<String,String[]> map){
		JSONArray array = new JSONArray();
		AON.getDataResponseStream(domain.getName(), domain.getId(), login,  
				f -> dataResponseFilter(domain, map, f))
		.forEach(dr -> {
			String source = AON.getDataResponseDetail(domain.getName(), domain.getId(), login,
					f -> f.getDataResponseProperty().eq(dr.getId())
					.and(f.getDataVariableProperty().eq("source"))).get().getValue();
			String[] s = source.split("@");
			if("income_detail".equals(s[0])){
				Integer id =Integer.parseInt(s[1]);
				Optional<IncomeDetail> incomeDetail = AON.getIncomeDetail(domain.getName(), domain.getId(), login, f2 -> f2.getIdProperty().eq(id));	
				if(incomeDetail.isPresent()){
					JSONObject o = ToJSON.dataResponseToJSON(dr);
					o.put("product", incomeDetail.get().getDescription());
					array.put(o);
				}
			}
		});
	    return array;
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
		
		if(filterMap.containsKey("source")){
			Filter fsourceValue = f.getTypeProperty().eq("source").and(f.getSourceProperty().like(filterMap.get("source")[0] + "@%"));				
			for(Integer i = 1; i < filterMap.get("source").length ; i++){
				fsourceValue = fsourceValue.or(f.getTypeProperty().eq("source").and(f.getSourceProperty().like(filterMap.get("source")[i] + "@%")));
			}
			filter = filter.and(fsourceValue);
		}
		
		if(filterMap.containsKey("type")){
			Filter ftypeValue = f.getTypeProperty().eq("type").and(f.getSourceProperty().eq(filterMap.get("type")[0]));				
			for(Integer i = 1; i < filterMap.get("type").length ; i++){
				ftypeValue = ftypeValue.or(f.getTypeProperty().eq("type").and(f.getSourceProperty().like(filterMap.get("type")[i])));
			}
			filter = filter.and(ftypeValue);
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
	
	private JSONObject insertDataResponse(Domain domain, String login, JSONObject json) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
		Date date = new Date();
		try {
			date = dateFormat.parse(json.getString("issue_date"));
		} catch (JSONException | ParseException e) {
			e.printStackTrace();
		}
		
		DataResponse dataResponse = new DataResponse()
				.setNumber(json.getString("number"))
				.setDomain(domain.getId())
				.setIssueDate(date);
		
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
						.setValue(json.getString(key));
				AON.insertDataResponseDetail(domain.getName(), domain.getId(), login, drd);
			}
		}
		return new JSONObject();
	}
}

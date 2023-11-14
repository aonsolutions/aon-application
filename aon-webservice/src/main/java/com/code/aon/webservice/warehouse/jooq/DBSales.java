package com.code.aon.webservice.warehouse.jooq;

import java.util.Date;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.webservice.common.MSG;
import com.code.aon.webservice.util.ToJSON;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.Options;
import com.esferalia.aon.occam.api.model.Properties.SalesDetailProperties;
import com.esferalia.aon.occam.api.model.Properties.SalesProperties;
import com.esferalia.aon.occam.api.model.management.SalesDetail;
import com.esferalia.aon.watson.server.AonDateUtils;

public class DBSales {
		
	public static JSONObject getSales(Domain domain, String login, Integer id) {
		return ToJSON.salesToJSON(AON.getSales(domain.getName(), domain.getId(), login,
				f -> f.getIdProperty().eq(id), new Options().setFull(true)));
	}

	public static JSONArray getSalesDetails(Domain domain, String login,
			Map<String, String[]> map) {
		JSONArray array = new JSONArray();
		AON.getSalesDetailStream(domain.getName(), domain.getId(), login,
				f -> salesDetailFilter(domain, map, f)).forEach(
				detail -> array.put(ToJSON.salesDetailToJSON(detail)));
		return array;
	}
	
	public static JSONObject getSalesDetail(Domain domain, String login,
			Integer id) {
		SalesDetail detail = AON
				.getSalesDetailStream(domain.getName(), domain.getId(), login,
						f -> f.getIdProperty().eq(id)).findFirst()
				.orElse(null);
		return detail!=null ? ToJSON.salesDetailToJSON(detail):null;
	}
	
    public static Filter salesFilter(Domain domain, Map<String, String[]> filterMap, SalesProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());
		
		if(filterMap.containsKey(MSG.CUSTOMER)){
			
		}
		
		if(filterMap.containsKey(MSG.ISSUE_DATE)){
			Date date = AonDateUtils.getDateWithoutTime(new Date(Long.parseLong(filterMap.get(MSG.ISSUE_DATE)[0])));
			filter = filter.and(f.getIssueDateProperty().ge(AonDateUtils.toSql(date)));
		}
		
		return filter;
    }
    
    public static Filter salesDetailFilter(Domain domain, Map<String, String[]> filterMap, SalesDetailProperties f) {
		Filter filter = f.getDomainProperty().eq(domain.getId());

		if(filterMap.containsKey(MSG.SALES)){
			Integer sales = Integer.parseInt(filterMap.get(MSG.SALES)[0]);
			filter = filter.and(f.getSalesProperty().eq(sales));
		}
		
		return filter;
    }

    
}

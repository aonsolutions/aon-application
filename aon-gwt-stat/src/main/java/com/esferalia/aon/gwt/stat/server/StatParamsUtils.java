package com.esferalia.aon.gwt.stat.server;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

import org.jooq.tools.json.JSONArray;
import org.jooq.tools.json.JSONObject;
import org.jooq.tools.json.JSONParser;
import org.jooq.tools.json.ParseException;

import com.esferalia.aon.gwt.stat.shared.IRequestParamsNames;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.stat.StatType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class StatParamsUtils {
	
	private static SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");

	public static StatParams get(String json) throws ParseException, java.text.ParseException {
		
		StatParams params = new StatParams();
		JSONParser parser = new JSONParser();
		JSONObject jsonParams = (JSONObject) parser.parse(json);
		Long domain = (Long) jsonParams.get(IRequestParamsNames.DOMAIN);
		params.setDomain(domain.intValue());
		String fromDate = (String) jsonParams.get(IRequestParamsNames.FROM_DATE);
		if (AonStringUtils.isNotBlank(fromDate)) {
			params.setFrom( FORMATTER.parse(fromDate));			
		}
		String toDate = (String) jsonParams.get(IRequestParamsNames.TO_DATE);
		if (AonStringUtils.isNotBlank(toDate)) {
			params.setTo( FORMATTER.parse(toDate));			
		}
		Long viewAmounts = (Long) jsonParams.get(IRequestParamsNames.VIEW_AMOUNTS);
		if (viewAmounts != null) {
			params.setViewAmounts(viewAmounts==1);
		}
		Long statType= (Long) jsonParams.get(IRequestParamsNames.STAT_TYPE);
		if (statType != null) {
			params.setStatType( StatType.safeValueOf(statType.intValue()));
		}
		Long chartType = (Long) jsonParams.get(IRequestParamsNames.CHART_TYPE);
		if (chartType!= null) {
			params.setChartType( chartType.byteValue());
		}
		Long registry = (Long) jsonParams.get(IRequestParamsNames.REGISTRY);
		if (registry != null) {
			params.setRegistry(registry.intValue());	
		}
		Long product = (Long) jsonParams.get(IRequestParamsNames.PRODUCT);
		if (product != null) {
			params.setProduct(product.intValue());	
		}
		
		JSONArray items = (JSONArray) jsonParams.get(IRequestParamsNames.FILTER_ITEMS);
		if (items != null) {
			LinkedList<StatFilterItem> list = new LinkedList<StatFilterItem>();
			for (int i = 0; i < items.size(); i++) {
				StatFilterItem item = new StatFilterItem();
				JSONObject a = (JSONObject) items.get(i);
				Long statFilterType = (Long) a.get(IRequestParamsNames.STAT_FILTER_TYPE);
				if (statFilterType != null) {
					item.setType( StatFilterType.safeValueOf(statFilterType.intValue()));
				}
				String id = (String) a.get(IRequestParamsNames.ID);
				if (id != null) {
					item.setId(id);	
				}
				String label = (String) a.get(IRequestParamsNames.LABEL);
				if (label != null) {
					item.setLabel(label);	
				}
				item.setSelected(true);
				list.add(item);
			}
			params.setFilterItems(list);
		}
		return params;
	}
	
	public static StatParams getPeriodParams(StatParams params) {
		StatParams p = clone(params);
		Date fromDate = AonDateUtils.getMonthFirstDay(p.getFrom());
		Date toDate = AonDateUtils.getMonthLastDay(fromDate);
		p.setFrom(fromDate);
		p.setTo(toDate);
		return p;
	}

	public static StatParams getPreviousMonthParams(StatParams params) {
		StatParams p = clone(params);
		Date fromDate = AonDateUtils.add(p.getFrom(), Calendar.MONTH, -1);
		fromDate = AonDateUtils.getMonthFirstDay(fromDate);
		Date toDate = AonDateUtils.getMonthLastDay(fromDate);
		p.setFrom(fromDate);
		p.setTo(toDate);
		return p;
	}

	public static StatParams getPreviousYearParams(StatParams params) {
		StatParams p = clone(params);
		Date fromDate = AonDateUtils.add(p.getFrom(), Calendar.YEAR, -1);
		fromDate = AonDateUtils.getYearFirstDay(fromDate);
		Date toDate = AonDateUtils.getYearLastDay(fromDate);
		p.setFrom(fromDate);
		p.setTo(toDate);
		return p;
	}

	public static StatParams getPeriodPreviousYearParams(StatParams params) {
		StatParams p = clone(params);
		Date fromDate = AonDateUtils.add(p.getFrom(), Calendar.YEAR, -1);
		fromDate = AonDateUtils.getMonthFirstDay(fromDate);
		Date toDate = AonDateUtils.getMonthLastDay(fromDate);
		p.setFrom(fromDate);
		p.setTo(toDate);
		return p;
	}

	public static StatParams getCurrentYearParams(StatParams params) {
		StatParams p = clone(params);
		Date toDate = AonDateUtils.getMonthLastDay(p.getFrom());
		p.setTo(toDate);
		Date fromDate = AonDateUtils.getYearFirstDay(p.getFrom());
		p.setFrom(fromDate);
		return p;
	}
	
	public static StatParams clone(StatParams params) {
		StatParams cloned = new StatParams().setDomain(params.getDomain()).setFrom(params.getFrom())
				.setTo(params.getTo()).setViewAmounts(params.isViewAmounts())
				.setViewPreviousPeriod(params.isViewPreviousPeriod()).setStatType(params.getStatType())
				.setChartType(params.getChartType()).setRegistry(params.getRegistry()).setProduct(params.getProduct());
		if (params.getFilterItems() != null && !params.getFilterItems().isEmpty()) {
			cloned.setFilterItems(new LinkedList<StatFilterItem>());
			for (StatFilterItem item : params.getFilterItems()) {
				cloned.getFilterItems().add(item.clone());
			}
		}
		if (params.getFilterMap() != null && !params.getFilterMap().isEmpty()) {
			cloned.setFilterMap(new HashMap<String, String[]>());
			for (String key : params.getFilterMap().keySet()) {
				String[] values = params.getFilterMap().get(key);
				cloned.getFilterMap().put(key, values == null ? null : values.clone());
			}
		}
		return cloned;
	}
}

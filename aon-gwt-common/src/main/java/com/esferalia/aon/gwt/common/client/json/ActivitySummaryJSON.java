package com.esferalia.aon.gwt.common.client.json;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryObject;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONObject;

public class ActivitySummaryJSON {
	
	public static List<ActivitySummaryObject> parseActivitySummaryArr(JSONArray arr) {
		List<ActivitySummaryObject> list = new LinkedList<>();
		
		for(Integer i = 0; i < arr.size(); i++)
			list.add(parseActivitySummaryJSON(arr.get(i).isObject()));
 		
		return list;
	}
	
	public static ActivitySummaryObject parseActivitySummaryJSON(JSONObject json) {
		ActivitySummaryObject activitySummary = new ActivitySummaryObject();
		
		activitySummary.setId(JsonGWTUtils.getInteger(json, IJsonNames.ID));
		activitySummary.setName(JsonGWTUtils.getString(json, IJsonNames.NAME));
		activitySummary.setNameUrl(JsonGWTUtils.getString(json, IJsonNames.NAME_URL));
		activitySummary.setFirstSurname(JsonGWTUtils.getString(json, IJsonNames.FIRST_SURNAME));
		activitySummary.setSecondSurname(JsonGWTUtils.getString(json, IJsonNames.SECOND_SURNAME));
		
		activitySummary.setStartDate(JsonGWTUtils.getDate(json, IJsonNames.START_DATE));
		activitySummary.setEndDate(JsonGWTUtils.getDate(json, IJsonNames.END_DATE));
		
		activitySummary.setStartCount(JsonGWTUtils.getInteger(json, IJsonNames.START_COUNT));
		activitySummary.setEndCount(JsonGWTUtils.getInteger(json, IJsonNames.END_COUNT));
		
		activitySummary.setSalaryCount(JsonGWTUtils.getInteger(json, IJsonNames.SALARY_COUNT));
		activitySummary.setSalaryExtraCount(JsonGWTUtils.getInteger(json, IJsonNames.SALARY_EXTRA_COUNT));
		activitySummary.setSalarySettleCount(JsonGWTUtils.getInteger(json, IJsonNames.SALARY_SETTLE_COUNT));
		activitySummary.setSalaryOtherCount(JsonGWTUtils.getInteger(json, IJsonNames.SALARY_OTHER_COUNT));
		
		activitySummary.setItCommonDiseaseCount(JsonGWTUtils.getInteger(json, IJsonNames.IT_COMMON_DISEASE_COUNT));
		activitySummary.setItOccupationalDiseaseCount(JsonGWTUtils.getInteger(json, IJsonNames.IT_OCCUPATIONAL_DISEASE_COUNT));
		activitySummary.setItMaternityCount(JsonGWTUtils.getInteger(json, IJsonNames.IT_MATERNITY_COUNT));
		activitySummary.setItOtherCount(JsonGWTUtils.getInteger(json, IJsonNames.IT_OTHER_COUNT));
		
		
		List<ActivitySummaryObject> childs = new ArrayList<>();
		JSONArray childsArr = JsonGWTUtils.getJSONArray(json, IJsonNames.CHILDS);
		
		for(Integer i = 0; i < childsArr.size(); i++)
			childs.add(parseActivitySummaryJSON(childsArr.get(i).isObject()));
		
		activitySummary.setChilds(childs);
		
		return activitySummary;
	}

}

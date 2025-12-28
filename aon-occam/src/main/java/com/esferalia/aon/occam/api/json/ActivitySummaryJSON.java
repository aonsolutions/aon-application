package com.esferalia.aon.occam.api.json;

import java.util.List;
import java.util.stream.Stream;

import org.json.JSONArray;
import org.json.JSONObject;

import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.activity.ActivitySummaryObject;
import com.esferalia.aon.watson.server.AonDateUtils;

public class ActivitySummaryJSON {
	
	
	public static JSONArray toJSON(List<ActivitySummaryObject> list) {
		return toJSON(list.stream());
	}
	
	public static JSONArray toJSON(Stream<ActivitySummaryObject> stream) {
		JSONArray array = new JSONArray();
		stream.forEach(object -> array.put(toJSON(object)));
		return array;
	}
	
	public static JSONObject toJSON(ActivitySummaryObject activitySummary) {
		JSONObject json = new JSONObject();

		json.put(IJsonNames.ID, activitySummary.getId())
			.put(IJsonNames.NAME, activitySummary.getName())
			.put(IJsonNames.NAME_URL, activitySummary.getNameUrl())
			.put(IJsonNames.FIRST_SURNAME, activitySummary.getFirstSurname()) 
			.put(IJsonNames.SECOND_SURNAME, activitySummary.getSecondSurname())
			;
		
		json.put(IJsonNames.START_DATE2, AonDateUtils.format(activitySummary.getStartDate(), AonDateUtils.SIMPLE_DATE_FORMAT))
			.put(IJsonNames.END_DATE2,AonDateUtils.format(activitySummary.getEndDate(), AonDateUtils.SIMPLE_DATE_FORMAT))
			.put(IJsonNames.START_COUNT, activitySummary.getStartCount())
			.put(IJsonNames.END_COUNT, activitySummary.getEndCount()) 
			;
		
		json.put(IJsonNames.SALARY_COUNT, activitySummary.getSalaryCount())
			.put(IJsonNames.SALARY_EXTRA_COUNT, activitySummary.getSalaryExtraCount()) 
			.put(IJsonNames.SALARY_SETTLE_COUNT, activitySummary.getSalarySettleCount())
			.put(IJsonNames.SALARY_OTHER_COUNT, activitySummary.getSalaryOtherCount()) 
			;
			
		json.put(IJsonNames.IT_COMMON_DISEASE_COUNT, activitySummary.getItCommonDiseaseCount())
			.put(IJsonNames.IT_OCCUPATIONAL_DISEASE_COUNT, activitySummary.getItOccupationalDiseaseCount()) 
			.put(IJsonNames.IT_MATERNITY_COUNT, activitySummary.getItMaternityCount())
			.put(IJsonNames.IT_OTHER_COUNT, activitySummary.getItOtherCount()) 
			;
		
		JSONArray childs = new JSONArray();
		if(null != activitySummary.getChilds())
			activitySummary.getChilds().forEach(object -> childs.put(toJSON(object)));
		
		json.put(IJsonNames.CHILDS, childs);
			
		return json;
	}

}

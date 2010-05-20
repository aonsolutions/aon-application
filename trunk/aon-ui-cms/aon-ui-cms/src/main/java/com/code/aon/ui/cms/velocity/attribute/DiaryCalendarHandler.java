package com.code.aon.ui.cms.velocity.attribute;

import java.util.List;

import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.velocity.ArticleCalendarGenerator;
import com.code.aon.ui.cms.velocity.utils.MonthContent;

public class DiaryCalendarHandler {

	private String main_url;
	
	private List<MonthContent> monthsList;

	public DiaryCalendarHandler(List<MonthContent> monthsList){
		this.monthsList = monthsList;
		this.main_url = Templates.DIARY.getHtmlName();
		this.main_url = this.main_url.replaceAll("%NAME%", ArticleCalendarGenerator.DIARY_INDEX_PAGE);
	}

	public String getMain_url() {
		return main_url;
	}

	public List<MonthContent> getMonthsList() {
		return monthsList;
	}

}

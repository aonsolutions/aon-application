package com.code.aon.ui.cms.velocity.attribute;

import java.util.ArrayList;
import java.util.Date;

public class NextArticlesHandler {

	private Date currentDate;
	
	private ArrayList<ArticleHandler> day;
	
	private ArrayList<ArticleHandler> week;

	private ArrayList<ArticleHandler> month;

	public NextArticlesHandler (Date currentDate, 
			ArrayList<ArticleHandler> day,
			ArrayList<ArticleHandler> week,
			ArrayList<ArticleHandler> month) {
		this.currentDate = currentDate;
		this.day = day;
		this.week = week;
		this.month = month;
	}

	public Date getCurrentDate() {
		return currentDate;
	}

	public ArrayList<ArticleHandler> getDay() {
		return day;
	}

	public ArrayList<ArticleHandler> getWeek() {
		return week;
	}

	public ArrayList<ArticleHandler> getMonth() {
		return month;
	}

	
}

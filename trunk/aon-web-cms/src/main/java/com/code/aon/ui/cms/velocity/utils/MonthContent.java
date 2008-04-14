package com.code.aon.ui.cms.velocity.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import com.code.aon.cms.ArticleDetail;
import com.code.aon.cms.enumeration.Templates;
import com.code.aon.ui.cms.velocity.attribute.DiaryHandler;

public class MonthContent {

	private int month;
	
	private int year;
	
	private String code;
	
	private Object[] values;
	
	DiaryHandler[][] diary;

	public MonthContent(int month, int year){
		this.month = month;
		this.year = year;
		String code = month+MonthContent.SEPARATOR+year;
		this.code = code;
		this.values = new Object[diasDelMes(this.month,this.year)];
		init();
	}
	
	public int getMonth() {
		return month;
	}

	public int getYear() {
		return year;
	}

	public String getCode() {
		return code;
	}

	public Object[] getValues() {
		return values;
	}

	public void assign(int day, ArticleDetail articleDetail){
		ArrayList<ArticleDetail> list = (ArrayList<ArticleDetail>)this.values[day-1];
		if (list==null){
			list = new ArrayList<ArticleDetail>();
			this.values[day-1] = list;
		}
		list.add(articleDetail);
	}

	public void assign(Date date, ArticleDetail articleDetail){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		ArrayList<ArticleDetail> list = (ArrayList<ArticleDetail>)this.values[calendar.get(Calendar.DATE)-1];
		if (list==null){
			list = new ArrayList<ArticleDetail>();
			this.values[calendar.get(Calendar.DATE)-1] = list;
		}
		list.add(articleDetail);
	}

	private void init(){
		for (int i = 0; i < diasDelMes(this.month,this.year); i++){
			this.values[i] = null;
		}
	}
	
	private static boolean esBisiesto(int year){
		if ((( year % 4 == 0 ) && ( year % 100 != 0 )) || ( year % 400 == 0 ))                    
			return true;        
		else             
			return false;            
	}
	
	public static int diasDelMes(int month, int year){
		if (month == 0 ||
				month == 2 ||
				month == 4 ||
				month == 6 ||
				month == 7 ||
				month == 9 ||
				month == 11){
			return 31;
		}else if (month == 3 ||
					month == 5 ||
					month == 8 ||
					month == 10){
			return 30;
		}else{
			if (esBisiesto(year)){
				return 29;
			}else{
				return 28;
			}
		}
	}
	
	public DiaryHandler[][] getCalendar(){
		if (diary == null){
			diary = new DiaryHandler[6][7];
			for (int i = 0;i < 6; i++){
				for (int j = 0;j < 7; j++){
					diary[i][j] = new DiaryHandler(0,null);
				}
			}
			Calendar cal = Calendar.getInstance();
			cal.set(year,month,1);
			int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
			if (dayOfWeek==1)
				dayOfWeek = 8;
			--dayOfWeek;
			for (int i = 0;i < dayOfWeek; i++){
				diary[0][i] = new DiaryHandler(0,null);
			}
			Object[] array = this.values;
			int week = 1;
			for (int i = 0;i < array.length; i++){
				if (dayOfWeek==8){
					dayOfWeek = 1;
					++week;
				}
				if (array[i]!=null){
					String url = Templates.DIARY.getHtmlName();
					String name = year+"_"+month+"_"+(i+1);
					url = url.replaceAll("%NAME%", name);
					diary[week-1][dayOfWeek-1] = new DiaryHandler(i+1,url);
				}else{
					diary[week-1][dayOfWeek-1] = new DiaryHandler(i+1,null);
				}
				++dayOfWeek;
			}
		}
		return diary;
	}

	
	public static String parseDateCode(Date date){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return calendar.get(Calendar.MONTH)+MonthContent.SEPARATOR+calendar.get(Calendar.YEAR);
	}

	public static MonthContent instantiate(Date date){
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		return new MonthContent(calendar.get(Calendar.MONTH),calendar.get(Calendar.YEAR));
	}
	
	private static String SEPARATOR = "_";
	
	public static void main(String[] args){
		MonthContent mc = new MonthContent(3,2008);
		mc.getCalendar();
	}
	
}

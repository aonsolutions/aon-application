package com.esferalia.aon.ui.calendar.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.Holiday;
import com.esferalia.aon.calendar.HolidayDetail;
import com.esferalia.aon.calendar.dao.ICalendarAlias;


public class CalendarHolidayDataController {
	
	
	public List<HolidayData> getHolidayData(){
		List<HolidayData> list = new LinkedList<HolidayData>();
		Calendar calendar = (Calendar) FormUtil.getController("calendar").getTo();
		Holiday holiday = calendar.getHoliday();
		Criteria criteria;
		IManagerBean bean;
		HolidayData data;
		try {
			bean = BeanManager.getManagerBean(HolidayDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(ICalendarAlias.HOLIDAY_DETAIL_HOLIDAY_ID), holiday.getId());
			data = new HolidayData();
			data.setDescription(holiday.getDescription());
			data.setModel(new ListDataModel(bean.getList(criteria)));
			list.add(data);
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		holiday = holiday.getHoliday();
		while(holiday!=null){
			try {
				bean = BeanManager.getManagerBean(HolidayDetail.class);
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(ICalendarAlias.HOLIDAY_DETAIL_HOLIDAY_ID), holiday.getId());
				data = new HolidayData();
				data.setDescription(holiday.getDescription());
				data.setModel(new ListDataModel(bean.getList(criteria)));
				list.add(data);
			} catch (ManagerBeanException e) {
				e.printStackTrace();
			}
			holiday = holiday.getHoliday();
		}
		return list;
	}
	
	
	
	
	public class HolidayData {
		private String description;
		private DataModel model;
		
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {
			this.description = description;
		}
		public DataModel getModel() {
			return model;
		}
		public void setModel(DataModel model) {
			this.model = model;
		}
	}
}



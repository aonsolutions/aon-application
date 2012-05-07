package com.esferalia.aon.ui.calendar.controller;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.Holiday;
import com.esferalia.aon.calendar.HolidayDetail;
import com.esferalia.aon.entity.IEntityAlias;


public class CalendarHolidayDataController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarHolidayDataController.class.getName());

	private Map<String,List<ITransferObject>> holidays;

	public Map<String,List<ITransferObject>> getHolidays(){
		return holidays;
	}
	
	public List<HolidayData> getHolidayDataModels(){
		List<HolidayData> list = new LinkedList<HolidayData>();
		searchHolidays();
		for(String key: holidays.keySet()){
			HolidayData data = new HolidayData();
			data.setDescription(key);
			data.setModel(new ListDataModel(holidays.get(key)));
			list.add(data);
		}
		return list;
	}
	
	private void searchHolidays() {
		holidays = new HashMap<String,List<ITransferObject>>();
		CalendarController controller = (CalendarController) FormUtil.getController(ICalendarConstants.CALENDAR_CONTROLLER_NAME);
		Calendar calendar = (Calendar) controller.getTo();
		Holiday holiday = calendar.getHoliday();
		java.util.Calendar startCal = new GregorianCalendar();
		java.util.Calendar endCal = new GregorianCalendar();
		startCal.set(controller.getYear(), java.util.Calendar.JANUARY, 1);
		endCal.set(controller.getYear(), java.util.Calendar.DECEMBER, 31);
		Criteria criteria;
		IManagerBean bean;
		List<ITransferObject> list;
		try {
			bean = BeanManager.getManagerBean(HolidayDetail.class);
			criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_HOLIDAY_ID), holiday.getId());
			criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_DATE), startCal.getTime(), endCal.getTime());
			criteria.addOrder(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_DATE));
			list = bean.getList(criteria);
			holidays.put(holiday.getDescription(), list);
			holiday = holiday.getHoliday();
			while(holiday!=null && holiday.getId()!=null){
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_HOLIDAY_ID), holiday.getId());
				criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_DATE), startCal.getTime(), endCal.getTime());
				criteria.addOrder(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_DATE));
				list = bean.getList(criteria);
				holidays.put(holiday.getDescription(), list);
				holiday = holiday.getHoliday();
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	
	/*
	 * HOLIDAY DATA
	 */
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



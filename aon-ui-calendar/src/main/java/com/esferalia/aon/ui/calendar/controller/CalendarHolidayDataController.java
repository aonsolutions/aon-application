package com.esferalia.aon.ui.calendar.controller;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.AbortProcessingException;
import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.Holiday;
import com.esferalia.aon.calendar.HolidayDetail;
import com.esferalia.aon.entity.IEntityAlias;


public class CalendarHolidayDataController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
			data.setModel(new SerializableListDataModel(holidays.get(key)));
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
			Integer[] ids = {0, DomainManager.getCurrentDomain(), getParentDomainId()};
			criteria.setSkipDomainFilter( true );
			criteria.addInExpression("HolidayDetail.domain", ids);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_HOLIDAY_ID), holiday.getId());
			criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_DATE), startCal.getTime(), endCal.getTime());
			criteria.addOrder(bean.getFieldName(IEntityAlias.HOLIDAY_DETAIL_DATE));
			list = bean.getList(criteria);
			holidays.put(holiday.getDescription(), list);
			holiday = holiday.getHoliday();
			while(holiday!=null && holiday.getId()!=null){
				criteria = new Criteria();
				criteria.setSkipDomainFilter( true );
				criteria.addInExpression("HolidayDetail.domain", ids);
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
	
	private Integer getParentDomainId() {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT parent FROM domain WHERE id = " + DomainManager.getCurrentDomain();
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()){
				return rs.getInt(1);
			}
		} catch (SQLException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (AonConnectionException e) {
			String msg = "Se ha producido un error al obtener los convenios. ("+ e.getMessage()+")";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
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



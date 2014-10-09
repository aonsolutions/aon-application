package com.esferalia.aon.ui.calendar.controller;

import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.CalendarHoliday;
import com.esferalia.aon.calendar.CalendarPeriod;
import com.esferalia.aon.entity.IEntityAlias;

public class CalendarController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class.getName());
	
	private Integer year;
	private String agreementName;
	private String enterpriseName;
	private String workPlaceName;
	private String contractName;
	private Calendar masterCalendar;
	private Integer calendarId;
	private Calendar currentCalendar;
	private IControllerListener excludeCurrentCalendar;
	
	public Integer getCalendarId() {
		return calendarId;
	}
	public void setCalendarId(Integer calendarId) {
		this.calendarId = calendarId;
	}
	public Calendar getCurrentCalendar() {
		return currentCalendar;
	}
	public void setCurrentCalendar(Calendar currentCalendar) {
		this.currentCalendar = currentCalendar;
	}
	
	public Calendar getMasterCalendar() {
		return masterCalendar;
	}
	public void setMasterCalendar(Calendar masterCalendar) {
		this.masterCalendar = masterCalendar;
	}
	public String getAgreementName() {
		return agreementName;
	}
	public void setAgreementName(String agreementName) {
		this.agreementName = agreementName;
	}

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}

	public String getWorkPlaceName() {
		return workPlaceName;
	}

	public void setWorkPlaceName(String workPlaceName) {
		this.workPlaceName = workPlaceName;
	}

	public String getContractName() {
		return contractName;
	}

	public void setContractName(String contractName) {
		this.contractName = contractName;
	}
	
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public IControllerListener getExcludeCurrentCalendar() {
		if ( this.excludeCurrentCalendar == null ) {
			this.excludeCurrentCalendar = new ExcludeCurrentCalendarFilter();
		}
		return this.excludeCurrentCalendar;
	}
	
	public DataModel getInheritHolidaysModel(){
		return new SerializableListDataModel(getInheritHolidays());
	}
	
	public List<ITransferObject> getInheritHolidays(){
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Calendar currentCalendar = ((Calendar) getTo()).getCalendar();
		if(currentCalendar!=null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(CalendarHoliday.class);
				Criteria criteria = new Criteria();
				Expression e1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CALENDAR_HOLIDAY_CALENDAR_ID), currentCalendar.getId()); 
				Calendar calendar = currentCalendar.getCalendar();
				while(calendar!=null && calendar.getId()!=null && !calendar.getId().equals(currentCalendar.getId())){
					Expression e2 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CALENDAR_HOLIDAY_CALENDAR_ID), calendar.getId());
					e1 = ExpressionUtilities.getOrExpression(e1, e2);
					calendar = calendar.getCalendar();
				}
				criteria.addExpression(e1);
				criteria.addOrder(bean.getFieldName(IEntityAlias.CALENDAR_HOLIDAY_DATE));
				java.util.Calendar startCal = new GregorianCalendar();
				java.util.Calendar endCal = new GregorianCalendar();
				startCal.set(year, java.util.Calendar.JANUARY, 1);
				endCal.set(year, java.util.Calendar.DECEMBER, 31);
				criteria.addBetweenExpression(bean.getFieldName(IEntityAlias.CALENDAR_HOLIDAY_DATE), startCal.getTime(), endCal.getTime());
				for(ITransferObject to: bean.getList(criteria)){
					list.add(to);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage());
				AonUtil.addErrorMessage("No se pueden mostrar las fechas heredadas");
			}
		}
		return list;
	}
	
	public DataModel getInheritPeriodsModel(){
		return new SerializableListDataModel(getInheritPeriods());
	}
	
	public List<ITransferObject> getInheritPeriods(){
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Calendar currentCalendar = ((Calendar) getTo()).getCalendar();
		if(currentCalendar!=null){
			try {
				IManagerBean bean = BeanManager.getManagerBean(CalendarPeriod.class);
				Criteria criteria = new Criteria();
				Expression e1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CALENDAR_PERIOD_CALENDAR_ID), currentCalendar.getId()); 
				Calendar calendar = currentCalendar.getCalendar();
				while(calendar!=null && calendar.getId()!=null && !calendar.getId().equals(currentCalendar.getId())){
					Expression e2 = ExpressionUtilities.getEqualExpression(bean.getFieldName(IEntityAlias.CALENDAR_PERIOD_CALENDAR_ID), calendar.getId());
					e1 = ExpressionUtilities.getOrExpression(e1, e2);
					calendar = calendar.getCalendar();
				}
				criteria.addExpression(e1);
				for(ITransferObject to: bean.getList(criteria)){
					list.add(to);
				}
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage());
				AonUtil.addErrorMessage("No se pueden mostrar los periodos heredados");
			}
		}
		return list;
	}

	/*
	 * ACTION LISTENERS
	 */
	public void onInitialize(ActionEvent event){
		try {
			GregorianCalendar cal= new GregorianCalendar();
			this.setYear(cal.get(java.util.Calendar.YEAR));
			if(getCalendarId()!=null){
				this.setTo(this.getManagerBean().get(getCalendarId()));
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			LOGGER.error(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onChangeYear(ActionEvent event){
		refreshLinesYear();
	}
	
	private void refreshLinesYear(){
		IController holiday = (IController) AonUtil.getRegisteredBean(ICalendarConstants.CALENDAR_HOLIDAY_CONTROLLER_NAME);
		IController period = (IController) AonUtil.getRegisteredBean(ICalendarConstants.CALENDAR_PERIOD_CONTROLLER_NAME);
		holiday.initializeModel();
		period.initializeModel();
	}

	public void onCalendarLookupChanged(LookupChangeEvent event){
		if(event.getNewValue()!=null && this.isNevv()){
			Calendar newCalendar = (Calendar) event.getNewValue();
			Calendar calendar = (Calendar) getTo();
			calendar.setAnualHours(newCalendar.getAnualHours());
			calendar.setHoliday(newCalendar.getHoliday());
			calendar.setDescription(newCalendar.getDescription());
			calendar.setComments(newCalendar.getComments());
			
			calendar.setMonday(newCalendar.getMonday());
			calendar.setMondayHours(newCalendar.getMondayHours());
			calendar.setTuesday(newCalendar.getTuesday());
			calendar.setTuesday(newCalendar.getTuesday());
			calendar.setWednesday(newCalendar.getWednesday());
			calendar.setWednesdayHours(newCalendar.getWednesdayHours());
			calendar.setThursday(newCalendar.getThursday());
			calendar.setThursdayHours(newCalendar.getThursdayHours());
			calendar.setFriday(newCalendar.getFriday());
			calendar.setFridayHours(newCalendar.getFridayHours());
			calendar.setSaturday(newCalendar.getSaturday());
			calendar.setSaturdayHours(newCalendar.getSaturdayHours());
			calendar.setSunday(newCalendar.getSunday());
			calendar.setSundayHours(newCalendar.getSundayHours());
		}
	}

	private static class ExcludeCurrentCalendarFilter extends ControllerAdapter {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

		@Override
		public void beforeModelInitialized(ControllerEvent event)
				throws ControllerListenerException {
			IController controller = event.getController();
			IController cc = FormUtil.getController(ICalendarConstants.CALENDAR_CONTROLLER_NAME);
			Calendar calendar = (Calendar) cc.getTo();
			if ( calendar!=null && calendar.getId()!=null ) {
				try {
					String alias = controller.getFieldName(IEntityAlias.CALENDAR_ID);
					controller.getCriteria().addNotEqualExpression(alias, calendar.getId());
				} catch (ManagerBeanException e) {
					LOGGER.error("Error filtering current calendar", e);
				}
			}
		}
		
	}


}

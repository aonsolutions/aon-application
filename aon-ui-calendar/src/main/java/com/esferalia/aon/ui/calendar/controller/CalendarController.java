package com.esferalia.aon.ui.calendar.controller;

//import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Agreement;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.CalendarHoliday;
import com.esferalia.aon.calendar.CalendarPeriod;
import com.esferalia.aon.calendar.dao.ICalendarAlias;
import com.esferalia.aon.calendar.enumeration.CalendarSource;
import com.esferalia.aon.payroll.Contract;

public class CalendarController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class.getName());
	
	private String sourceKey;
	private CalendarSource source;
	private Integer sourceId;
	private Integer year;
	private Agreement agreement;
	private Enterprise enterprise;
	private WorkPlace workPlace;
	private Contract contract;
	private boolean generic;
	private boolean ownCalendar;
	private boolean usingExisting;
	private Calendar masterCalendar;
	
	public boolean isMissingSource(){
		return getSource()==CalendarSource.NONE;
	}
	public Calendar getMasterCalendar() {
		return masterCalendar;
	}
	public void setMasterCalendar(Calendar masterCalendar) {
		this.masterCalendar = masterCalendar;
	}
	public boolean isGeneric() {
		return generic;
	}
	public boolean isOwnCalendar() {
		return ownCalendar;
	}
	public void setOwnCalendar(boolean ownCalendar) {
		this.ownCalendar = ownCalendar;
	}
	public boolean isUsingExisting() {
		return usingExisting;
	}
	public void setUsingExisting(boolean usingExisting) {
		this.usingExisting = usingExisting;
	}
	public void setGeneric(boolean generic) {
		this.generic = generic;
	}
	public boolean isEditable() {
		return (ownCalendar&&!generic)||getSource()==CalendarSource.NONE;
	}
	public Agreement getAgreement() {
		return agreement;
	}
	public void setAgreement(Agreement agreement) {
		this.agreement = agreement;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	public Integer getYear() {
		return year;
	}
	
	public boolean isAgreementSource() {
		return getSource()==CalendarSource.AGREEMENT;
	}
	
	public boolean isEnterpriseSource() {
		return getSource()==CalendarSource.ENTERPRISE;
	}
	
	public boolean isWorkPlaceSource() {
		return getSource()==CalendarSource.WORKPLACE;
	}
	
	public boolean isContractSource() {
		return getSource()==CalendarSource.CONTRACT;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	

	public String getSourceKey() {
		return sourceKey;
	}

	public void setSourceKey(String sourceKey) {
		if(CalendarSource.NONE.ordinal()==Integer.parseInt(sourceKey)){
			setSource(CalendarSource.NONE);
		} else if(CalendarSource.ENTERPRISE.ordinal()==Integer.parseInt(sourceKey)){
			setSource(CalendarSource.ENTERPRISE);
		} else if(CalendarSource.WORKPLACE.ordinal()==Integer.parseInt(sourceKey)){
			setSource(CalendarSource.WORKPLACE);
		} else if(CalendarSource.CONTRACT.ordinal()==Integer.parseInt(sourceKey)){
			setSource(CalendarSource.CONTRACT);
		}
		this.sourceKey = sourceKey;
	}
	
	public CalendarSource getSource() {
		return source;
	}

	public void setSource(CalendarSource source) {
		this.source = source;
	}
	
	public Integer getSourceId() {
		return sourceId;
	}
	
	public void setSourceId(Integer sourceId) {
		this.sourceId = sourceId;
	}

	public void loadSource() throws ManagerBeanException {
		setAgreement(null);
		setEnterprise(null);
		setWorkPlace(null);
		setContract(null);
		IManagerBean bean;
		if(getSource()==CalendarSource.AGREEMENT){
			bean = BeanManager.getManagerBean(Agreement.class);
			setAgreement((Agreement) bean.get(getSourceId()));
		} else if(getSource()==CalendarSource.ENTERPRISE){
			bean = BeanManager.getManagerBean(Enterprise.class);
			setEnterprise((Enterprise) bean.get(getSourceId()));
		} else if(getSource()==CalendarSource.WORKPLACE){
			bean = BeanManager.getManagerBean(WorkPlace.class);
			setWorkPlace((WorkPlace) bean.get(getSourceId()));
			setEnterprise(getWorkPlace().getEnterprise());
		} else if(getSource()==CalendarSource.CONTRACT){
			bean = BeanManager.getManagerBean(Contract.class);
			setContract((Contract) bean.get(getSourceId()));
			setWorkPlace(getContract().getWorkPlace());
			setEnterprise(getWorkPlace().getEnterprise());
		} else if(getSource()==CalendarSource.NONE){
			setGeneric(true);
			setSourceId(null);
		}
	}
	
	private void initialize(ActionEvent event) throws ManagerBeanException{
		if(getSource()!=null && getSource()!=CalendarSource.NONE){
			Calendar c = null;
			this.resetTo();
			if(getSource()==CalendarSource.CONTRACT){ 
				c = obtainCalendar(getContract());
			} else if(getSource()==CalendarSource.WORKPLACE){ 
				c = obtainCalendar(getWorkPlace());
			} else if(getSource()==CalendarSource.ENTERPRISE){ 
				c = obtainCalendar(getEnterprise());
			} else if(getSource()==CalendarSource.AGREEMENT){ 
				c = obtainCalendar(getAgreement());
			}
			if(c!=null){
				this.select(event, c);
				if(c.getCalendar()!=null && c.getCalendar().getId()!=null){
					setGeneric(c.isGeneric());
					setMasterCalendar(c.getCalendar());
				}
			} else {
				loadDefaultCalendar(event);
			} 
		} 
	}
	
	private void loadDefaultCalendar(ActionEvent event){
		this.onReset(event);
	}
	
	public DataModel getInheritHolidaysModel(){
		return new ListDataModel(getInheritHolidays());
	}
	
	public List<ITransferObject> getInheritHolidays(){
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Calendar calendar = ((Calendar) getTo()).getCalendar();
		if(calendar==null){
			return null;
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(CalendarHoliday.class);
			Criteria criteria = new Criteria();
			Expression e1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(ICalendarAlias.CALENDAR_HOLIDAY_CALENDAR_ID), calendar.getId()); 
			calendar = calendar.getCalendar();
			while(calendar!=null){
				Expression e2 = ExpressionUtilities.getEqualExpression(bean.getFieldName(ICalendarAlias.CALENDAR_HOLIDAY_CALENDAR_ID), calendar.getId());
				e1 = ExpressionUtilities.getOrExpression(e1, e2);
				calendar = calendar.getCalendar();
			}
			criteria.addExpression(e1);
			criteria.addOrder(bean.getFieldName(ICalendarAlias.CALENDAR_HOLIDAY_DATE));
			java.util.Calendar startCal = new GregorianCalendar();
			java.util.Calendar endCal = new GregorianCalendar();
			startCal.set(year, java.util.Calendar.JANUARY, 1);
			endCal.set(year, java.util.Calendar.DECEMBER, 31);
			criteria.addBetweenExpression(bean.getFieldName(ICalendarAlias.CALENDAR_HOLIDAY_DATE), startCal.getTime(), endCal.getTime());
			for(ITransferObject to: bean.getList(criteria)){
				list.add(to);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage());
			AonUtil.addErrorMessage("No se pueden mostrar las fechas heredadas");
		}
		return list;
	}
	
	public DataModel getInheritPeriodsModel(){
		return new ListDataModel(getInheritPeriods());
	}
	
	public List<ITransferObject> getInheritPeriods(){
		List<ITransferObject> list = new LinkedList<ITransferObject>();
		Calendar calendar = ((Calendar) getTo()).getCalendar();
		if(calendar==null){
			return null;
		}
		try {
			IManagerBean bean = BeanManager.getManagerBean(CalendarPeriod.class);
			Criteria criteria = new Criteria();
			Expression e1 = ExpressionUtilities.getEqualExpression(bean.getFieldName(ICalendarAlias.CALENDAR_PERIOD_CALENDAR_ID), calendar.getId()); 
			calendar = calendar.getCalendar();
			while(calendar!=null){
				Expression e2 = ExpressionUtilities.getEqualExpression(bean.getFieldName(ICalendarAlias.CALENDAR_PERIOD_CALENDAR_ID), calendar.getId());
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
		return list;
	}

	/*
	 * ACTION LISTENERS
	 */
	public void onInitialize(ActionEvent event){
		try {
			loadSource();
			initialize(event);
			GregorianCalendar cal= new GregorianCalendar();
			this.setYear(cal.get(java.util.Calendar.YEAR));
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
		if(event.getNewValue()!=null && this.isNew()){
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

	public void onChangeUsingExisting(ActionEvent event){
		if(isUsingExisting()){
			setOwnCalendar(false);
		} else {
			setOwnCalendar(true);
		}
	}
	
	@Override
	public void accept(ActionEvent event) {
		if(!isUsingExisting()){
			super.accept(event);
			updateSourceEntity((Calendar) this.getTo());
		} else {
			updateSourceEntity(getMasterCalendar());
			setNew(false);
			
		}
	}
	
	@Override
	public void onRemove(ActionEvent event) {
		updateSourceEntity(null);
		if(!isUsingExisting()){
			super.onRemove(event);
		}
	}
	
	public Calendar obtainCalendar(Contract contract) {
		setOwnCalendar(false);
		if(contract.getCalendar()!=null){
			setOwnCalendar(true);
			return contract.getCalendar();
		} else if (contract.getWorkPlace().getCalendar()!=null){
			return contract.getWorkPlace().getCalendar();
		}  else if (contract.getWorkPlace().getEnterprise().getCalendar()!=null){
			return contract.getWorkPlace().getEnterprise().getCalendar();
		}  else if (contract.getWorkPlace().getEnterprise().getAgreement()!=null){
			if (contract.getWorkPlace().getEnterprise().getAgreement().getCalendar()!=null){
				return contract.getWorkPlace().getEnterprise().getAgreement().getCalendar();
			}
		}
		return null;
	}
	public Calendar obtainCalendar(WorkPlace wp) {
		setOwnCalendar(false);
		if (wp.getCalendar()!=null){
			setOwnCalendar(true);
			return wp.getCalendar();
		}  else if (wp.getEnterprise().getCalendar()!=null){
			return wp.getEnterprise().getCalendar();
		}  else if (wp.getEnterprise().getAgreement()!=null){
			if (wp.getEnterprise().getAgreement().getCalendar()!=null){
				return wp.getEnterprise().getAgreement().getCalendar();
			}
		}
		return null;
	}
	public Calendar obtainCalendar(Enterprise enterprise) {
		setOwnCalendar(false);
		if (enterprise.getCalendar()!=null){
			setOwnCalendar(true);
			return enterprise.getCalendar();
		}  else if (enterprise.getAgreement()!=null){
			if (enterprise.getAgreement().getCalendar()!=null){
				return enterprise.getAgreement().getCalendar();
			}
		}
		return null;
	}
	public Calendar obtainCalendar(Agreement agreement) {
		setOwnCalendar(false);
		if (agreement.getCalendar()!=null){
			setOwnCalendar(true);
			return agreement.getCalendar();
		}
		return null;
	}
	
	/**
	 * Actualiza la entidad a la que se asigna el calendario
	 */
	private void updateSourceEntity(Calendar calendar){
		IManagerBean bean;
		try {
			if(getSource()==CalendarSource.AGREEMENT){
				bean = BeanManager.getManagerBean(Agreement.class);
				getAgreement().setCalendar(calendar);
				bean.update(getAgreement());
			} else if(getSource()==CalendarSource.ENTERPRISE){
				bean = BeanManager.getManagerBean(Enterprise.class);
				getEnterprise().setCalendar(calendar);
				bean.update(getEnterprise());
			} else if(getSource()==CalendarSource.WORKPLACE){
				bean = BeanManager.getManagerBean(WorkPlace.class);
				getWorkPlace().setCalendar(calendar);
				bean.update(getWorkPlace());
			} else if(getSource()==CalendarSource.CONTRACT){
				bean = BeanManager.getManagerBean(Contract.class);
				getContract().setCalendar(calendar);
				bean.update(getContract());
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}
	
}

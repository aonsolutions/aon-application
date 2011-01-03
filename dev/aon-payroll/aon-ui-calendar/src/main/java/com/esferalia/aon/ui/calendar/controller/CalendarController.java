package com.esferalia.aon.ui.calendar.controller;

//import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.company.Agreement;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.employee.Contract;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.Calendar;
import com.esferalia.aon.calendar.enumeration.CalendarSource;

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
		if(CalendarSource.ENTERPRISE.ordinal()==Integer.parseInt(sourceKey)){
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
		}
	}
	
	private void loadDefaultCalendar(){
		//TODO cargar el calendario basico que estara definido por ley
		this.onReset(null);
		
	}
	
	public void initialize() throws ManagerBeanException{
		this.resetTo();
		if(getSource()==CalendarSource.CONTRACT){ 
			if(getContract()!=null && getContract().getCalendar()!=null){
//				setTo(getContract().getCalendar());
				this.select(null, getContract().getCalendar());
			}
		} else if(getSource()==CalendarSource.WORKPLACE){ 
			if(getWorkPlace()!=null && getWorkPlace().getCalendar()!=null){
//				setTo(getWorkPlace().getCalendar());
				select(null, getWorkPlace().getCalendar());
			}
		} else if(getSource()==CalendarSource.ENTERPRISE){ 
			if(getEnterprise()!=null && getEnterprise().getCalendar()!=null){
//				setTo(getEnterprise().getCalendar());
				select(null, getEnterprise().getCalendar());
			}
		} else if(getSource()==CalendarSource.AGREEMENT){ 
			if(getAgreement()!=null && getAgreement().getCalendar()!=null){
//				setTo(getAgreement().getCalendar());
				select(null, getAgreement().getCalendar());
			}
		}
		if(getTo()==null){
			loadDefaultCalendar();
		} 
	}

	/*
	 * ACTION LISTENERS
	 */
	public void onInitialize(ActionEvent event){
		try {
			loadSource();
//			loadWorkPlaces();
			initialize();
			GregorianCalendar cal= new GregorianCalendar();
			this.setYear(cal.get(java.util.Calendar.YEAR));
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
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
	
	@Override
	public void accept(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				super.accept(event);
				// se actualiza la entidad a la que se asigna el calendario
				IManagerBean bean;
				if(getSource()==CalendarSource.AGREEMENT){
					bean = BeanManager.getManagerBean(Agreement.class);
					getAgreement().setCalendar((Calendar) this.getTo());
					bean.update(getAgreement());
				} else if(getSource()==CalendarSource.ENTERPRISE){
					bean = BeanManager.getManagerBean(Enterprise.class);
					getEnterprise().setCalendar((Calendar) this.getTo());
					bean.update(getEnterprise());
				} else if(getSource()==CalendarSource.WORKPLACE){
					bean = BeanManager.getManagerBean(WorkPlace.class);
					getWorkPlace().setCalendar((Calendar) this.getTo());
					bean.update(getWorkPlace());
				} else if(getSource()==CalendarSource.CONTRACT){
					bean = BeanManager.getManagerBean(Contract.class);
					getContract().setCalendar((Calendar) this.getTo());
					bean.update(getContract());
				}
				// END operaciones de la transaccion
				HibernateUtil.getSession(sessionName).flush();
				HibernateUtil.commitTransaction(sessionName);
			} catch (Exception e) {
				String msg = e.getMessage();
				try {
					HibernateUtil.rollbackTransaction(sessionName);
				} catch (DAOException daoe) {
					msg = "Unable to rollback transaction! (" + msg + ")";
				}
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(e);
			} finally {
				HibernateUtil.closeSession(sessionName);
			}
		} finally {
			HibernateUtil.setCloseSession(mustCloseSession);
			HibernateUtil.setBeginTransaction(mustBeginTransaction);
		}
	}
	
	
}

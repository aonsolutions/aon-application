package com.code.aon.ui.employee.controller;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.employee.Contract;
import com.code.aon.employee.ContractCalendarEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class ContractCalendarEventController extends LinesController {

//	private static final Logger LOGGER = LoggerFactory.getLogger(ContractTrackingController.class.getName());

	private ContractCalendarEvent event;
	private boolean fullTime;
	private Date startDate;
	private Date endDate;
	private boolean showMultipleTrackingWindow;

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public boolean isFullTime() {
		return fullTime;
	}

	public void setFullTime(boolean fullTime) {
		this.fullTime = fullTime;
	}

	public ContractCalendarEvent getEvent() {
		if(event==null){
			event = new ContractCalendarEvent();
		}
		return event;
	}

	public void setEvent(ContractCalendarEvent event) {
		this.event = event;
	}
	
	public boolean isShowMultipleTrackingWindow() {
		return showMultipleTrackingWindow;
	}

	public void setShowMultipleTrackingWindow(boolean showMultipleTrackingWindow) {
		this.showMultipleTrackingWindow = showMultipleTrackingWindow;
	}

	public void onSelectFullTime(ActionEvent event){
		if(isFullTime()){
			
		} else{
			
		}
	}
	
	public void onMultipleTrackingShow(ActionEvent event)  {
		setEvent(null);
		setFullTime(false);
		setStartDate(null);
		setEndDate(null);
	}
	
	
	public void onAcceptMultipleTracking(ActionEvent event) {
		boolean mustBeginTransaction = HibernateUtil.mustBeginTransaction();
		boolean mustCloseSession = HibernateUtil.mustCloseSession();
		String sessionName = HibernateUtil.getSessionFactoryName();
		try {
			try {
				HibernateUtil.setBeginTransaction(false);
				HibernateUtil.setCloseSession(false);
				HibernateUtil.beginTransaction(sessionName);
				// BEGIN operaciones de la transaccion
				Calendar startDate = new GregorianCalendar();
				Calendar endDate = new GregorianCalendar();
				startDate.setTimeInMillis(getStartDate().getTime());
				endDate.setTimeInMillis(getEndDate().getTime());
				
				ContractCalendarEvent ct;
				while(startDate.before(endDate) || startDate.equals(endDate)){
					ct = new ContractCalendarEvent();
					ct.setContract(((Contract)getMasterController().getTo()));
					ct.setDate(startDate.getTime());
					ct.setType(getEvent().getType());
					ct.setDuration(getEvent().getDuration());
					this.getManagerBean().insert(ct);
					startDate.add(Calendar.DATE, 1);
				}
				// FIN operaciones de la transaccion
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
		onSearch(event);
		
	}

	
}

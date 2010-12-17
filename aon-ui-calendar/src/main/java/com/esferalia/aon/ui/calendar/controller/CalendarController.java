package com.esferalia.aon.ui.calendar.controller;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.employee.Contract;
import com.code.aon.employee.dao.IEmployeeAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.employee.event.ContractListListener;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.calendar.dao.ICalendarAlias;
import com.esferalia.aon.calendar.enumeration.CalendarSource;

public class CalendarController extends BasicController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CalendarController.class.getName());
	
	private Integer year;
	private String sourceKey;
	private CalendarSource source;
	private Integer sourceId;
	private Enterprise enterprise;
	private WorkPlace workPlace;
	private Contract contract;
	private boolean workPlaceEnabled;
	private boolean contractEnabled;
	private List<SelectItem> workPlaces;
	
	public List<SelectItem> getWorkPlaces() {
		if(workPlaces==null){
			workPlaces = new LinkedList<SelectItem>();
		}
		return workPlaces;
	}
	public void setWorkPlaces(List<SelectItem> workPlaces) {
		this.workPlaces = workPlaces;
	}
	
	public boolean isWorkPlaceEnabled() {
		return workPlaceEnabled;
	}

	public void setWorkPlaceEnabled(boolean workPlaceEnabled) {
		this.workPlaceEnabled = workPlaceEnabled;
	}

	public boolean isContractEnabled() {
		return contractEnabled;
	}

	public void setContractEnabled(boolean contractEnabled) {
		this.contractEnabled = contractEnabled;
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

	public String getSourceFullName() {
		String name=null;
		try {
			com.esferalia.aon.calendar.Calendar cal =(com.esferalia.aon.calendar.Calendar)getModel().getRowData();
			cal.getSource();
			cal.getSourceId();
			IManagerBean bean;
			Criteria criteria = new Criteria();
			if(cal.getSource()==CalendarSource.ENTERPRISE){
				bean = BeanManager.getManagerBean(Enterprise.class);
				criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.ENTERPRISE_ID), cal.getSourceId());
				Enterprise e = (Enterprise)bean.getList(criteria).get(0);
				name = e.getRegistry().getFullName();	
			} else if(cal.getSource()==CalendarSource.WORKPLACE){
				bean = BeanManager.getManagerBean(WorkPlace.class);
				criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ID), cal.getSourceId());
				WorkPlace w = (WorkPlace)bean.getList(criteria).get(0);
				name = w.getDescription();	
			} else if(cal.getSource()==CalendarSource.CONTRACT){
				bean = BeanManager.getManagerBean(Contract.class);
				criteria.addEqualExpression(bean.getFieldName(IEmployeeAlias.CONTRACT_ID), cal.getSourceId());
				Contract c = (Contract) bean.getList(criteria).get(0);
				name = c.getPerson().getRegistry().getFullName();	
			}
		} catch (ManagerBeanException e) {
			// NADA. no se puede obtener el nombre del source
			LOGGER.error("ERROR in getSourceFullName");
		}
		return name;
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

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	@Override
	public com.esferalia.aon.calendar.Calendar getTo() {
		return (com.esferalia.aon.calendar.Calendar)super.getTo();
	}

	private void loadWorkPlaces() throws ManagerBeanException{
		workPlaces = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(ICompanyAlias.WORK_PLACE_ENTERPRISE_ID), getEnterprise().getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			WorkPlace w = (WorkPlace)to; 
			String name = w.getDescription();
			SelectItem item = new SelectItem(w, name);
			workPlaces.add(item);
		}
	}
	
	public void loadSource() throws ManagerBeanException {
		Criteria criteria = new Criteria();
		if(getSource()==CalendarSource.ENTERPRISE){
			IController c = FormUtil.getController(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
			criteria.addEqualExpression(c.getFieldName(ICompanyAlias.ENTERPRISE_ID), getSourceId());
			setEnterprise((Enterprise)c.getManagerBean().getList(criteria).get(0));
		} else if(getSource()==CalendarSource.WORKPLACE){
			IController c = FormUtil.getController(ICompanyConstants.WORK_PLACE_CONTROLLER_NAME);
			criteria.addEqualExpression(c.getFieldName(ICompanyAlias.WORK_PLACE_ID), getSourceId());
			setWorkPlace((WorkPlace)c.getManagerBean().getList(criteria).get(0));
			setEnterprise(getWorkPlace().getEnterprise());
		} else if(getSource()==CalendarSource.CONTRACT){
			IController c = FormUtil.getController(ICompanyConstants.CONTRACT_CONTROLLER_NAME);
			criteria.addEqualExpression(c.getFieldName(IEmployeeAlias.CONTRACT_ID), getSourceId());
			setContract((Contract)c.getManagerBean().getList(criteria).get(0));
			setWorkPlace(getContract().getWorkPlace());
			setEnterprise(getContract().getWorkPlace().getEnterprise());
		} else {
			
		}
	}
	
	private void refreshLinesYear(){
		IController holiday = (IController) AonUtil.getRegisteredBean(ICalendarConstants.CALENDAR_HOLIDAY_CONTROLLER_NAME);
		IController period = (IController) AonUtil.getRegisteredBean(ICalendarConstants.CALENDAR_PERIOD_CONTROLLER_NAME);
		holiday.initializeModel();
		period.initializeModel();
	}
	
	/*
	 * ACTION LISTENERS
	 */
	
	public void initialize(){
		setEnterprise(new Enterprise());
		setWorkPlace(new WorkPlace());
		workPlaces = null;
		setContract(new Contract());
	}

	public void onInitialize(ActionEvent event){
		try {
			initialize();
			clearCriteria();
			getCriteria().addEqualExpression(getFieldName(ICalendarAlias.CALENDAR_SOURCE), getSource());
			getCriteria().addEqualExpression(getFieldName(ICalendarAlias.CALENDAR_SOURCE_ID), getSourceId());
			onSearch(event);
			GregorianCalendar cal= new GregorianCalendar();
			this.setYear(cal.get(Calendar.YEAR));				
			if(getModel().getRowCount() == 0){
				onReset(event);
				(getTo()).setSource(getSource());
				(getTo()).setSourceId(getSourceId());
			} else {
				onSelectFirst(event);
			}
			loadSource();
			loadWorkPlaces();
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e);
		}
	}

	public void onChangeYear(ActionEvent event){
		refreshLinesYear();
	}
	
	public void onEnterpriseChanged(LookupChangeEvent event){
		setWorkPlace(null);
		setContract(null);
		setContractEnabled(false);
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setEnterprise((Enterprise)event.getNewValue());
			getTo().setSource(CalendarSource.ENTERPRISE);
			getTo().setSourceId(getEnterprise().getId());
			try {
				loadWorkPlaces();
			} catch (ManagerBeanException e) {
				//NADA, la ista de workplaces estara vacia
				LOGGER.error("ERROR in onEnterpriseChanged");
			}
			setWorkPlaceEnabled(true);
		} else {
			getTo().setSource(null);
			getTo().setSourceId(null);
			setWorkPlaceEnabled(false);
		}
	}
	public void onWorkPlaceChanged(ActionEvent event){
		setContract(new Contract());
		if (getWorkPlace() != null && getWorkPlace().getId()!=null) {
			getTo().setSource(CalendarSource.WORKPLACE);
			getTo().setSourceId(getWorkPlace().getId());
			ContractListListener bean = (ContractListListener)AonUtil.getRegisteredBean("contractLookupList");
			bean.setWorkPlace(getWorkPlace());
			setContractEnabled(true);
		} else {
			getTo().setSource(CalendarSource.ENTERPRISE);
			getTo().setSourceId(getEnterprise().getId());
			setContractEnabled(false);
		}
	}
	public void onContractChanged(LookupChangeEvent event){
		if (event.getNewValue() != null && !event.getNewValue().equals("")) {
			setContract((Contract)event.getNewValue());
			getTo().setSource(CalendarSource.CONTRACT);
			getTo().setSourceId(getContract().getId());
		} else {
			getTo().setSource(CalendarSource.WORKPLACE);
			getTo().setSourceId(getWorkPlace().getId());
		}
	}
	
}

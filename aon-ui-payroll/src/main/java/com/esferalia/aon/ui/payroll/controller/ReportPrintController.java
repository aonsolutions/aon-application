package com.esferalia.aon.ui.payroll.controller;


import java.io.Serializable;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.person.Person;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;

public class ReportPrintController implements Serializable, ICollectionProvider {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ReportPrintController.class);
	
	private static final String SALARY_NAME_PATTERN = "{0} {1} ({2,date,dd.MM.yyyy}-{3,date,dd.MM.yyyy})";
	
	private List<SelectItem> availableWorkPlaces;

	private List<SelectItem> availableCCCs;
	
	private boolean showWorkPlaces;

	private boolean showCCCs;
	
	private Enterprise enterprise;
	
	private WorkPlace workPlace;
	
	private Person person;

	private EnterpriseCCC enterpriseCCC;
	
	private String[] salaryTypes;
		
	private Month month;

	private Integer year;

	private boolean betweenDatesEnabled;
	
	private Date fromDate;
	
	private Date toDate;
	
	private boolean groupByEmployee;
	
	
	
	
	public boolean isGroupByEmployee() {
		return groupByEmployee;
	}

	public void setGroupByEmployee(boolean groupByEmployee) {
		this.groupByEmployee = groupByEmployee;
	}

	public Month getMonth() {
		return month;
	}

	public void setMonth(Month month) {
		this.month = month;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}

	public boolean isBetweenDatesEnabled() {
		return betweenDatesEnabled;
	}

	public void setBetweenDatesEnabled(boolean betweenDatesEnabled) {
		this.betweenDatesEnabled = betweenDatesEnabled;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public EnterpriseCCC getEnterpriseCCC() {
		return enterpriseCCC;
	}

	public void setEnterpriseCCC(EnterpriseCCC enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
	}

	public WorkPlace getWorkPlace() {
		return workPlace;
	}

	public void setWorkPlace(WorkPlace workPlace) {
		this.workPlace = workPlace;
	}

	public Enterprise getEnterprise() {
		return enterprise;
	}

	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}
	
	public boolean isShowWorkPlaces() {
		return showWorkPlaces;
	}
	
	public boolean isShowCCCs() {
		return showCCCs;
	}

	public List<SelectItem> getAvailableWorkPlaces() {
		return availableWorkPlaces;
	}

	public List<SelectItem> getAvailableCCCs() {
		return availableCCCs;
	}
	
	public String[] getSalaryTypes() {
		return salaryTypes;
	}

	public void setSalaryTypes(String[] salaryTypes) {
		this.salaryTypes = salaryTypes;
	}
	
	public void onInit( ActionEvent event ) throws ManagerBeanException {
		loadWorkPlaces(getEnterprise());
		clearFilters();
	}
	
	public void onClearFilter( ActionEvent event ) {
		clearFilters();
		
	}
	
	private void clearFilters() {
		this.fromDate = new Date();
		this.toDate = new Date();
		this.enterprise = null;
		this.workPlace = null;
		this.salaryTypes = null;
		this.betweenDatesEnabled = false;
		this.groupByEmployee = false;
		this.month = Month.getMonthByValue(CommonUtil.getMonth(new Date()));
		this.year = CommonUtil.getYear(new Date());
		try {
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(new Date());
			startCal.set(Calendar.HOUR_OF_DAY, 0);
			startCal.set(Calendar.MINUTE, 0);
			startCal.set(Calendar.SECOND, 0);
			startCal.set(Calendar.DAY_OF_MONTH, 1);
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(new Date());
			endCal.set(Calendar.HOUR_OF_DAY, 0);
			endCal.set(Calendar.MINUTE, 0);
			endCal.set(Calendar.SECOND, 0);
			endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			this.fromDate = startCal.getTime();
			this.toDate = endCal.getTime();
			
			if(DomainManager.isDomainManagementAvailable()){
				setEnterprise((Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo());
			} else {
				setEnterprise(PayrollUtils.getInstance().getCurrentDomainEnterprise());
				setPerson((Person)BeanManager.getManagerBean(Person.class).createNewTo());
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("No se han podido limpiar los criterios de busqueda.");
			throw new AbortProcessingException(e.getMessage());
		}
	}

	
	public void onChangePeriod( ActionEvent event ) throws ManagerBeanException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.YEAR, getYear());
		cal.set(Calendar.MONTH, getMonth().getValue());
		cal.set(Calendar.DAY_OF_MONTH, 1);
		this.fromDate = cal.getTime();
		
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.YEAR, getYear());
		cal.set(Calendar.MONTH, getMonth().getValue());
		cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
		this.toDate = cal.getTime();
	}
	
	public void onChangeEnterprise( LookupChangeEvent event ) throws ManagerBeanException {
		loadWorkPlaces((Enterprise) event.getNewValue());
	}

	public void onChangeWorkplace( ActionEvent event ) throws ManagerBeanException {
		loadCCCs(getEnterprise());
	}
	
	public void onSearch(ActionEvent arg0) {
		
	}
	



	
	private void loadWorkPlaces(Enterprise enterprise) throws ManagerBeanException {
		this.availableWorkPlaces = null;
		this.showWorkPlaces = false;
		if(enterprise!=null && enterprise.getId()!=null){
			IManagerBean bean = BeanManager.getManagerBean(WorkPlace.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.WORK_PLACE_DOMAIN), enterprise.getDomain());
			criteria.addOrder(bean.getFieldName(IEntityAlias.WORK_PLACE_DESCRIPTION));
			if ( bean.getCount(criteria) > 1 ) {
				List<ITransferObject> list = bean.getList(criteria);
				this.availableWorkPlaces = new LinkedList<SelectItem>();
				for (ITransferObject to : list) {
					WorkPlace workPlace = (WorkPlace)to;
					availableWorkPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
				}
				this.showWorkPlaces = true;
			}
		}
		loadCCCs(enterprise);
	}	

	private void loadCCCs(Enterprise enterprise) throws ManagerBeanException {
		this.availableCCCs = null;
		this.showCCCs = false;
		if(enterprise!=null && enterprise.getId()!=null){
			IManagerBean bean = BeanManager.getManagerBean(EnterpriseCCC.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_DOMAIN), enterprise.getDomain());
			if(getWorkPlace()!=null && getWorkPlace().getId()!=null){
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_GEOZONE_ID), getWorkPlace().getAddress().getGeozone().getId());
			}
			criteria.addOrder(bean.getFieldName(IEntityAlias.ENTERPRISE_CCC_TYPE));
			if ( bean.getCount(criteria) > 1 ) {
				List<ITransferObject> list = bean.getList(criteria);
				this.availableCCCs = new LinkedList<SelectItem>();
				for (ITransferObject to : list) {
					EnterpriseCCC enterpriseCCC = (EnterpriseCCC)to;
					Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
					String label = enterpriseCCC.getType().getName(locale) +" (";
					label += enterpriseCCC.getActivity().getType().getCode();
					label += enterpriseCCC.getCcc() + ")";
					availableCCCs.add(new SelectItem(enterpriseCCC, label));
				}
				this.showCCCs = true;
			}
		}
	}	
	
	
	@Override
	public Collection<ITransferObject> getCollection() {
		try {
			return getCollection(false);
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		return null;
	}

	@Override
	public Collection<ITransferObject> getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		return l;
	}
	

	public String onPrint() throws ManagerBeanException {
		ReportManager reportManager = new ReportManager();
//		reportManager.setReportKey(obtainSalaryTemplate());
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( this );
		return reportManager.onExecute();	
	}
	
	
}
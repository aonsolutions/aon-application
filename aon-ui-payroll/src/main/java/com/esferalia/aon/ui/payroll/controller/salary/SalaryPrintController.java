package com.esferalia.aon.ui.payroll.controller.salary;


import static com.code.aon.ui.common.ICommonMessages.COMPANY_EMAIL_BODY_FOOTER;
import static com.code.aon.ui.common.ICommonMessages.NOT_MAIL_ACCOUNTS;
import static com.code.aon.ui.common.ICommonMessages.SALARY_EMAIL_BODY_HEADER;
import static com.code.aon.ui.common.ICommonMessages.SALARY_EMAIL_BODY_LINE;
import static com.code.aon.ui.common.ICommonMessages.SALARY_EMAIL_SUBJECT;
import static com.code.aon.ui.company.controller.ICompanyConstants.COMPANY_CONTROLLER_NAME;
import static com.code.aon.ui.webmail.controller.IWebMailConstants.BEAN_MAIL_CONFIG;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.AonFile;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.enumeration.SalaryTemplate;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.faces.component.util.DownloadUtil;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.EnterpriseController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.util.CompanyEmailUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.controller.IWebMailConstants;
import com.code.aon.ui.webmail.controller.MailConfigController;
import com.code.aon.ui.webmail.controller.MessageController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPayrollBuilder;
import com.esferalia.aon.payroll.Contract;
import com.esferalia.aon.payroll.ContractData;
import com.esferalia.aon.payroll.EnterpriseCCC;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.payroll.enumeration.ContextVariable;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.controller.EnterpriseParamsController;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.file.EnterpriseCostProvider;
import com.esferalia.aon.ui.payroll.utils.PayrollUtils;
import com.esferalia.aon.watson.util.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class SalaryPrintController extends BasicController implements ICollectionProvider, IPayrollConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(SalaryPrintController.class);
	
	private static final String SALARY_NAME_PATTERN = "{0} {1} ({2,date,dd.MM.yyyy}-{3,date,dd.MM.yyyy})";
	
	private static final String SALARIES_ZIP_NAME = "nominas";

	private static final String SALARY_COST_FILE_NAME = "costes_empresa";

	private static final String TP_CONTRACT_HOURS_FILE_NAME = "horas_contratos_tp";
	
	private List<SelectItem> availableWorkPlaces;

	private List<SelectItem> availableCCCs;
	
	private boolean showWorkPlaces;

	private boolean showCCCs;
	
	private Enterprise enterprise;
	
	private WorkPlace workPlace;

	private EnterpriseCCC enterpriseCCC;
	
	private String[] types;
	
	private boolean includeEnterpriseCost;

	private boolean includeTPhours;
	
	private Month month;

	private Integer year;

	private boolean betweenDatesEnabled;
	
	private Date fromDate;
	
	private Date toDate;
	
	private Contract contract;

	private Set<Integer> checks = new HashSet<Integer>();


	public boolean isIncludeTPhours() {
		return includeTPhours;
	}

	public void setIncludeTPhours(boolean includeTPhours) {
		this.includeTPhours = includeTPhours;
	}

	public boolean isIncludeEnterpriseCost() {
		return includeEnterpriseCost;
	}

	public void setIncludeEnterpriseCost(boolean includeEnterpriseCost) {
		this.includeEnterpriseCost = includeEnterpriseCost;
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

	public Set<Integer> getChecked() {
		return checks;
	}

	public void clearChecked() {
		checks = new HashSet<Integer>();
	}
	
	public boolean isSelectionEmpty() {
		return this.checks.isEmpty();
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
	
	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}
	
	public Contract getContract() {
		return contract;
	}

	public void setContract(Contract contract) {
		this.contract = contract;
	}
	
	public int getLastYear() {
		try {
			Date lastSalaryDate = obtainLastSalaryDate();
			return AonDateUtils.get(lastSalaryDate, Calendar.YEAR);
		} catch (Exception e) {
			return Calendar.getInstance().get(Calendar.YEAR);
		} 
	}
	
	public int getFirstYear() {
		try {
			Date firstSalaryDate = obtainFirstSalaryDate();
			return AonDateUtils.get(firstSalaryDate, Calendar.YEAR);
		} catch (Exception e) {
			return Calendar.getInstance().get(Calendar.YEAR) - 5;
		} 
	}
	
	public void onInit( ActionEvent event ) throws ManagerBeanException {
		setIncludeEnterpriseCost(false);
		setIncludeTPhours(false);
		clearFilters();
		loadWorkPlaces(getEnterprise());
		resetCriteria();
		clearChecked();
	}

	public void onClearFilter( ActionEvent event ) throws ManagerBeanException {
		clearFilters();
		resetCriteria();
		this.initializeModel();
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
	
	@Override
	public void onSearch(ActionEvent arg0) {
		try {
			onFilter(arg0);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage("No se ha podido procesar la búsqueda.");
			throw new AbortProcessingException(e);
		}
	}
	
	public void onFilter( ActionEvent event ) throws ManagerBeanException {
		resetCriteria();
		Criteria criteria = this.getCriteria();
		if(this.fromDate!=null){
			Calendar from = Calendar.getInstance();
			from.setTime(fromDate);
			from.set(Calendar.HOUR_OF_DAY, 0);
			from.set(Calendar.MINUTE, 0);
			from.set(Calendar.SECOND, 0);
			from.set(Calendar.MILLISECOND, 0);
			criteria.addGreaterThanOrEqualExpression(this.getFieldName(IEntityAlias.SALARY_END_DATE), from.getTime());
		}
		if(this.toDate!=null){
			Calendar to = Calendar.getInstance();
			to.setTime(toDate);
			to.set(Calendar.DAY_OF_MONTH, to.getActualMaximum(Calendar.DAY_OF_MONTH));
			to.set(Calendar.HOUR_OF_DAY, 23);
			to.set(Calendar.MINUTE, 59);
			to.set(Calendar.SECOND, 59);
			to.set(Calendar.MILLISECOND, 59);
			criteria.addLessThanOrEqualExpression(this.getFieldName(IEntityAlias.SALARY_END_DATE), to.getTime());
		}
		if(getTypes()!=null && getTypes().length>0){
			String alias = this.getFieldName(IEntityAlias.SALARY_TYPE);			
			List<Object> list = new LinkedList<Object>();
			for( String value : getTypes() ) {
				if ( value != null ) {
					list.add(SalaryType.valueOf(value));
				}
			}
			if ( list.size() == 1 ) {
				criteria.addEqualExpression(alias, list.get(0));
			} else {
				criteria.addInExpression(alias, list);	
			}
		}
		if ((getWorkPlace() != null) && (getWorkPlace().getId() !=null)) {
			String alias = this.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID);
			criteria.addEqualExpression(alias, getWorkPlace().getId());
		}
		if ((getEnterpriseCCC() != null) && (getEnterpriseCCC().getId() !=null)) {
//			String alias = this.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID);
			String alias = "Salary.contract.enterpriseCCC.id";
			criteria.addEqualExpression(alias, getEnterpriseCCC().getId());
		}
		if ((getContract() != null) && (getContract().getId() != null)) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.SALARY_CONTRACT_ID), getContract().getId());			
		}
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(this.getFieldName(IEntityAlias.SALARY_DOMAIN), getEnterprise().getDomain());
		this.initializeModel();
		checkAll(event);
	}
	
	public void checkAll(ActionEvent event) throws ManagerBeanException {
		String id = this.getFieldName(IEntityAlias.SALARY_ID);
		ProjectionList pl = new ProjectionList( Projection.property(id) );
		List<Integer> list = this.getManagerBean().getList(pl, this.getCriteria());
		clearChecked();
		checks.addAll( list );
	}

	public void checkNone(ActionEvent event) {
		clearChecked();
	}

	private void clearFilters()  throws ManagerBeanException {
		IManagerBean contractBean = BeanManager.getManagerBean(Contract.class);
		setContract((Contract) contractBean.createNewTo());
		this.fromDate = new Date();
		this.toDate = new Date();
		this.enterprise = null;
		this.workPlace = null;
		this.types = null;
		this.betweenDatesEnabled = false;
		this.month = Month.getMonthByValue(CommonUtil.getMonth(new Date()));
		this.year = CommonUtil.getYear(new Date());
		Date lastSalaryDate = null;
		try {
			lastSalaryDate = obtainLastSalaryDate();
			Calendar startCal = Calendar.getInstance();
			startCal.setTime(new Date());
			startCal.set(Calendar.HOUR_OF_DAY, 0);
			startCal.set(Calendar.MINUTE, 0);
			startCal.set(Calendar.SECOND, 0);
			if(lastSalaryDate!=null){
				startCal.set(Calendar.YEAR, CommonUtil.getYear(lastSalaryDate));
				startCal.set(Calendar.MONTH, CommonUtil.getMonth(lastSalaryDate));
			}
			startCal.set(Calendar.DAY_OF_MONTH, 1);
			Calendar endCal = Calendar.getInstance();
			endCal.setTime(new Date());
			endCal.set(Calendar.HOUR_OF_DAY, 0);
			endCal.set(Calendar.MINUTE, 0);
			endCal.set(Calendar.SECOND, 0);
			if(lastSalaryDate!=null){
				endCal.set(Calendar.YEAR, CommonUtil.getYear(lastSalaryDate));
				endCal.set(Calendar.MONTH, CommonUtil.getMonth(lastSalaryDate));
			}
			endCal.set(Calendar.DAY_OF_MONTH, endCal.getActualMaximum(Calendar.DAY_OF_MONTH));
			this.fromDate = startCal.getTime();
			this.toDate = endCal.getTime();
			
			if(DomainManager.isDomainManagementAvailable()){
				setEnterprise((Enterprise)BeanManager.getManagerBean(Enterprise.class).createNewTo());
			} else {
				setEnterprise(PayrollUtils.getInstance().getCurrentDomainEnterprise());
			}
		} catch (AonConnectionException e) {
			LOGGER.error("No se han podido limpiar los criterios de busqueda.");
			throw new AbortProcessingException(e.getMessage());
		} catch (SQLException e) {
			LOGGER.error("No se han podido limpiar los criterios de busqueda.");
			throw new AbortProcessingException(e.getMessage());
		} catch (ManagerBeanException e) {
			LOGGER.error("No se han podido limpiar los criterios de busqueda.");
			throw new AbortProcessingException(e.getMessage());
		}
	}
	
	private Date obtainLastSalaryDate() throws AonConnectionException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT max(end_date) FROM salary";
			select += " WHERE domain = " + DomainManager.getCurrentDomain() + " ;";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDate(1);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}

	private Date obtainFirstSalaryDate() throws AonConnectionException, SQLException {
		Connection conn = null;
		PreparedStatement ps = null;
		try {
			conn = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT min(end_date) FROM salary";
			select += " WHERE domain = " + DomainManager.getCurrentDomain() + " ;";
			ps = conn.prepareStatement(select);
			ResultSet rs = ps.executeQuery();
			if(rs.next()) return rs.getDate(1);
		} finally {
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		return null;
	}

	private void resetCriteria() throws ManagerBeanException {
		this.clearCriteria();
		Criteria criteria = this.getCriteria();
		String alias = this.getFieldName(IEntityAlias.SALARY_DOMAIN);
		criteria.addEqualExpression(alias, getEnterprise().getDomain());		
	}

	public boolean getRowChecked() throws ManagerBeanException {
		Salary to = (Salary) this.getModel().getRowData();
		return checks.contains(to.getId());
	}

	public void setRowChecked(boolean rowChecked) throws ManagerBeanException {
		Integer id = ((Salary) this.getModel().getRowData()).getId();		
		if (rowChecked) {
			if (!checks.contains(id)) {
				checks.add(id);
			}
		} else {
			if (checks.contains(id)) {
				checks.remove(id);
			}
		}
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
					label += PayrollUtils.getInstance().getRegimeCode(enterpriseCCC);
					label += enterpriseCCC.getCcc() + ")";
					availableCCCs.add(new SelectItem(enterpriseCCC, label));
				}
				this.showCCCs = true;
			}
		}
	}	
	
	public boolean isContainsPartialTime(){
		try {
			String id = this.getFieldName(IEntityAlias.SALARY_CONTRACT_ID);
			ProjectionList pl = new ProjectionList( Projection.property(id) );
			Criteria criteria = new Criteria();
			criteria.addInExpression(this.getManagerBean().getFieldName(IEntityAlias.SALARY_ID), checks);
			List<Integer> list = this.getManagerBean().getList(pl, criteria);
			IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
			criteria = new Criteria();
			criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), list);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), ContextVariable.TC2.getName());
			Expression exp1 = ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION), "\"2%\"");
			Expression exp2 = ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION), "\"3%\"");
			Expression exp3 = ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION), "\"5%\"");
			criteria.addExpression( ExpressionUtilities.getOrExpression(ExpressionUtilities.getOrExpression(exp1, exp2), exp3) );
			return bean.getCount(criteria)>0;
		} catch (ManagerBeanException e) {
			return false;
		}
	}
	
	public boolean isContainsSalary(){
		return containsSalary(SalaryType.SALARY);
	}
	public boolean isContainsExtra(){
		return containsSalary(SalaryType.EXTRA);
	}
	public boolean isContainsSettle(){
		return containsSalary(SalaryType.SETTLE);
	}
	public boolean isContainsDelay(){
		return containsSalary(SalaryType.DELAY);
	}
	private boolean containsSalary(SalaryType type){
		try {
			Criteria criteria = new Criteria();
			criteria.addInExpression(this.getManagerBean().getFieldName(IEntityAlias.SALARY_ID), checks);
			criteria.addEqualExpression(this.getManagerBean().getFieldName(IEntityAlias.SALARY_TYPE), type);
			criteria.addEqualExpression(this.getManagerBean().getFieldName(IEntityAlias.SALARY_DOMAIN), getEnterprise().getDomain());
			criteria.setSkipDomainFilter(true);
			return this.getManagerBean().getCount(criteria)>0;
		} catch (ManagerBeanException e) {
			return false;
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
		IManagerBean bean = this.getManagerBean();
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		Map<String, String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String type = parameters.get("reportType");
		if (type == null) {
			LOGGER.warn("Empty reportType!");
			type = "SALARY";
		}
		for( Integer id : checks ) {
			ITransferObject to = bean.get(id);
			if(((Salary)to).getType()==SalaryType.valueOf(type)){
				l.add( to );
			}
		}
		return l;
	}
	
	private Collection<ITransferObject> getSelectedAllSalaries() throws ManagerBeanException {
		IManagerBean bean = this.getManagerBean();
		List<ITransferObject> l = new LinkedList<ITransferObject>();
		for( Integer id : checks ) {
			l.add( bean.get(id) );
		}
		return l;		
	}

	public String onPrint() throws ManagerBeanException {
		String reportTemplate = obtainSalaryTemplate();
		Integer[] ids = checks != null ? checks.toArray(Integer[]::new) : new Integer[0];
		String domainName = AonUtil.getDomainName();
		HttpServletResponse response = DownloadUtil.getResponse();
		try (OutputStream os = response.getOutputStream()) {
			if (AonStringUtils.equalsIgnoreCase(reportTemplate, SalaryTemplate.AON_SOLUTIONS_DEFAULT.getValue())) {
				JooqPayrollBuilder.generateClassicPayroll(domainName, os, Optional.empty(), ids);
			} else {
				JooqPayrollBuilder.generatePayroll(domainName, os, Optional.empty(), ids);			
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	private String obtainSalaryTemplate() throws ManagerBeanException{
		Map<String, String> parameters = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
		String type = parameters.get("reportType");
		if (type == null) {
			LOGGER.warn("Empty reportType!");
			type = "SALARY";
		}
		if(SalaryType.valueOf(type) == SalaryType.SETTLE){
			return IPayrollConstants.SETTLE_REPORT;
		} else {
			CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
			Company company = companyController.obtainCompany();
			EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
			try {
				controller.select(null, company.getId());
			} catch (ManagerBeanException e) {
				LOGGER.error(e.getMessage(), e);
			}
			EnterpriseParamsController enterpriseParams = (EnterpriseParamsController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_PARAMS_CONTROLLER_NAME);
			return enterpriseParams.getParameter("PAY_REPORT_salary_PAY").getExpression();
		}
	}

	private String obtainSalarySendingEmail() throws ManagerBeanException{
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(COMPANY_CONTROLLER_NAME);
		Company company = companyController.obtainCompany();
		EnterpriseController controller = (EnterpriseController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_CONTROLLER_NAME);
		try {
			controller.select(null, company.getId());
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
		EnterpriseParamsController enterpriseParams = (EnterpriseParamsController) AonUtil.getRegisteredBean(ICompanyConstants.ENTERPRISE_PARAMS_CONTROLLER_NAME);
		return enterpriseParams.getParameter("PAY_salarySending_email_PAY").getExpression();
	}
	
	private void writeSalariesZip( File file, Collection<ITransferObject> collection ) throws IOException, ReportException, ManagerBeanException {
		OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(file) );
		ZipOutputStream zipOut = new ZipOutputStream(fileOut);
		for (ITransferObject to : collection) {
			Salary salary = (Salary) to;
			String fileName = this.getFileName(salary) + "." + MimeType.MIME_PDF.getExtension();
       		zipOut.putNextEntry(new ZipEntry(fileName));
       		this.writeReport(salary, zipOut);
        	zipOut.closeEntry();
        }
		IOUtils.closeQuietly(zipOut);
	}
	
	private AonFile getSalariesZipFile( Collection<ITransferObject> salaries ) throws IOException, ReportException, ManagerBeanException {
		File file = File.createTempFile( SALARIES_ZIP_NAME, "." + MimeType.MIME_ZIP.getExtension() );
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeSalariesZip( file, salaries );
		IOUtils.closeQuietly(out);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( SALARIES_ZIP_NAME + "." + MimeType.MIME_ZIP.getExtension() );
		return aonFile;
	}
	
	private AonFile getSalaryCostFile( ) throws IOException, ReportException {
		File file = File.createTempFile( SALARY_COST_FILE_NAME, "." + MimeType.MIME_PDF.getExtension() );
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeEnterpriseCostReport(new BufferedOutputStream( new FileOutputStream(file) ));
		IOUtils.closeQuietly(out);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( SALARY_COST_FILE_NAME + "." + MimeType.MIME_PDF.getExtension() );
		return aonFile;
	}

	private AonFile getTPContractHoursFile( ) throws IOException, ReportException {
		File file = File.createTempFile( TP_CONTRACT_HOURS_FILE_NAME, "." + MimeType.MIME_PDF.getExtension() );
		OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
		writeTPContractHoursReport(new BufferedOutputStream( new FileOutputStream(file) ));
		IOUtils.closeQuietly(out);
		AonFile aonFile = new AonFile();
		aonFile.setFile(file);	
		aonFile.setFileName( TP_CONTRACT_HOURS_FILE_NAME + "." + MimeType.MIME_PDF.getExtension() );
		return aonFile;
	}

	public void onSendByEmail( ActionEvent event ) {
		try {
			Collection<ITransferObject> salaries = getSelectedAllSalaries();
			MailConfigController mailConfig = (MailConfigController) AonUtil.getRegisteredBean(BEAN_MAIL_CONFIG);
			if (mailConfig.getMailAccountCount() > 0) {
				MessageController messageController = (MessageController) AonUtil.getRegisteredBean(IWebMailConstants.BEAN_MESSAGE);
				messageController.initNewMessage();
				messageController.setSubject( getEmailSubject() );
				messageController.updateMessageBody(getEmailContent(salaries));
				setRecipients(messageController);
				
				messageController.addAttachment( getSalariesZipFile(salaries) );
				
				if(isIncludeEnterpriseCost()){
					messageController.addAttachment( getSalaryCostFile() );
				}
				if(isIncludeTPhours()){
					messageController.addAttachment( getTPContractHoursFile() );
				}
				messageController.setShowNewMessageWindow(true);
			} else {
				AonUtil.addErrorMessageFromBundle(NOT_MAIL_ACCOUNTS);
			}
		} catch (Throwable e) {
			LOGGER.error(">>>> onSendByEmail ",e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}
	
	private String getEmailSubject() {
		String message = AonUtil.getMessage(SALARY_EMAIL_SUBJECT);
		Enterprise enterprise = PayrollUtils.getInstance().getCurrentDomainEnterprise();
		return MessageFormat.format(message, enterprise.getRegistry().getFullName() );
	}
	
	private String getEmailContent( Collection<ITransferObject> salaries ) {
		StringBuffer body = new StringBuffer();
		body.append( "<html><head>" );
		body.append( "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\" />" );
		body.append( "</head><body>" );
		
		body.append(AonUtil.getMessage(SALARY_EMAIL_BODY_HEADER) );
		for( ITransferObject to : salaries ) {
			Salary salary = (Salary) to;
			body.append( "<ul>" );
			String message = AonUtil.getMessage(SALARY_EMAIL_BODY_LINE);
			String line = MessageFormat.format(message, salary.getContract().getPerson().getFullName(), salary.getIssueDate() );
			body.append( line );
			body.append( "</ul>" );
		}
		body.append("<br />" );		
		body.append(AonUtil.getMessage(COMPANY_EMAIL_BODY_FOOTER) );		
		return body.toString();
	}
	
	private void setRecipients( MessageController messageController) throws ManagerBeanException {
		String[] emails = null;			
		String email = obtainSalarySendingEmail();
		if(StringUtils.isNotEmpty(email)){
			emails = new String[]{email};
		} else {
			Enterprise enterprise = PayrollUtils.getInstance().getCurrentDomainEnterprise();
			emails = CompanyEmailUtil.getAdministrativeEmails(enterprise.getRegistry());			
		}
		CompanyEmailUtil.initMessageController(messageController, emails);
	}
	
	private void writeReport( Salary salary, OutputStream out ) throws ReportException, ManagerBeanException {
		ReportManager reportManager = new ReportManager();
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( new SingleCollectionProvider(salary) );
		reportManager.execute( out, obtainSalaryTemplate() );
	}

	private void writeEnterpriseCostReport( OutputStream out ) throws ReportException {
		// TODO 
		SalaryExpenseController salaryExpense = new SalaryExpenseController();
//		salaryExpense.setYear(getYear());
//		salaryExpense.setMonth(getMonth());
		salaryExpense.setStartDate(getFromDate());
		salaryExpense.setEndDate(getToDate());
		
		ReportManager reportManager = new ReportManager();
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( salaryExpense );
		reportManager.execute( out, salaryExpense.getReportKey() );
	}

	private String writeTPContractHoursReport( OutputStream out ) throws ReportException {
		ReportManager reportManager = new ReportManager();
		reportManager.setOutputFormat(OutputFormat.PDF);
		reportManager.setCollectionProvider( new PartialContractProvider() );
		return reportManager.execute( out, CONTRACT_MONTHLY_HOURS );
	}
	
	public String onExecuteTPContractHoursReport(){
		OutputStream out = null;
		try {
			out = DownloadUtil.initDownload(DownloadUtil.getResponse(), CONTRACT_MONTHLY_HOURS, OutputFormat.PDF.getMimeType2());
			return writeTPContractHoursReport( out );
		} catch (IOException e) {
			return null;
		} catch (ReportException e) {
			return null;
		} finally {
			DownloadUtil.finishDownload(DownloadUtil.getResponse(), out);
		}
	}
	
	
	private String getFileName( Salary salary ) {
		String name = salary.getContract().getPerson().getFullName();
		return MessageFormat.format(SALARY_NAME_PATTERN, salary.getType().getName(AonUtil.getCurrentLocale()), name, salary.getStartDate(), salary.getEndDate());
	}
	
	public String onQuoteExcelReport() {
		SimpleDateFormat dateFormatter = new SimpleDateFormat();
		Connection connection = null; 
		try {
			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			Locale locale = AonUtil.getCurrentLocale();
			EnterpriseCostProvider provider = new EnterpriseCostProvider();
			
			FacesContext faces = FacesContext.getCurrentInstance();
			HttpServletResponse response = (HttpServletResponse) faces.getExternalContext().getResponse();
			String fileName = "";
			fileName += month.getName(locale) + "_";
			fileName += year;
			dateFormatter.applyPattern("yyyy/MM/dd_HH:mm:ss");
			fileName += " - " + dateFormatter.format(new Date());
			response.setContentType(MimeType.MIME_MS_EXCEL_2007.getName());
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".xls\";");
			ServletOutputStream output = response.getOutputStream();

			if(!provider.excelReport(getFromDate(), getToDate(), null, null, null, new LinkedList<Integer>(checks), output)){
				AonUtil.addErrorMessage("No existen datos para generar el informe.");
			}
			
			response.flushBuffer();
			faces.responseComplete();
			return null;
		} catch (ReportException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(connection);
		}
	}
	
	public class PartialContractProvider implements ICollectionProvider {
				
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
			
			List<Integer> salaryContractIdList = null;
			try {
				IManagerBean bean = BeanManager.getManagerBean(Salary.class);
				String id = bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID);
				ProjectionList pl = new ProjectionList( Projection.property(id) );
				Criteria criteria = new Criteria();
				criteria.addInExpression(bean.getFieldName(IEntityAlias.SALARY_ID), checks);
				salaryContractIdList = bean.getList(pl, criteria);
			} catch (ManagerBeanException e) {
				return Collections.emptyList();	
			}
			List<Integer> contractIdList = null;
			try {
				IManagerBean bean = BeanManager.getManagerBean(ContractData.class);
				String id = bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID);
				ProjectionList pl = new ProjectionList( Projection.property(id) );
				Criteria criteria = new Criteria();
				criteria.addInExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_CONTRACT_ID), salaryContractIdList);
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_NAME), ContextVariable.TC2.getName());
				Expression exp1 = ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION), "\"2%\"");
				Expression exp2 = ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION), "\"3%\"");
				Expression exp3 = ExpressionUtilities.getLikeExpression(bean.getFieldName(IEntityAlias.CONTRACT_DATA_EXPRESSION), "\"5%\"");
				criteria.addExpression( ExpressionUtilities.getOrExpression(ExpressionUtilities.getOrExpression(exp1, exp2), exp3) );
				contractIdList = bean.getList(pl, criteria);
			} catch (ManagerBeanException e) {
				return Collections.emptyList();	
			}
			
			List<ITransferObject> l = new LinkedList<ITransferObject>();
			IManagerBean bean = BeanManager.getManagerBean(Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.SALARY_TYPE), SalaryType.SALARY);
			criteria.addInExpression(bean.getFieldName(IEntityAlias.SALARY_ID), checks);
			criteria.addInExpression(bean.getFieldName(IEntityAlias.SALARY_CONTRACT_ID), contractIdList);
			bean.getList(criteria);
			
			for( ITransferObject to : bean.getList(criteria) ) {
				l.add( to );
			}
			return l;
		}
		
	}
	
}
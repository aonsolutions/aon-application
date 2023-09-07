package com.esferalia.aon.ui.payroll.controller;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.faces.model.SelectItem;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.DSLContext;
import org.jooq.DatePart;
import org.jooq.Field;
import org.jooq.impl.DSL;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.person.Person;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.in.payroll.excel.AggregatedAnnualSummary;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayrollExcelParams;
import com.esferalia.aon.in.payroll.excel.ExcelType;
import com.esferalia.aon.in.payroll.pdf.JooqEnterpriseSalaryBuilder;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.type.SalaryType;
import com.ibm.icu.util.Calendar;

public class EnterpriseSalaryReportController implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3910637732757521646L;
	private Date endDate;
	private Date startDate;
	private Person person;
	private String[] salaryTypes = {"SALARY", "EXTRA", "DELAY", "SETTLE"};
	private List<SelectItem> availableYears = getYears();

	private int currentYear;
	private int yearlyYear;
	private boolean groupByPerson;
	
	
	public int getYearlyYear() {
		return yearlyYear;
	}

	public void setYearlyYear(int yearlyYear) {
		this.yearlyYear = yearlyYear;
	}
	
	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Person getPerson() {
		return person;
	}

	public void setPerson(Person person) {
		this.person = person;
	}

	public String[] getSalaryTypes() {
		return salaryTypes;
	}

	public void setSalaryTypes(String[] salaryTypes) {
			this.salaryTypes = salaryTypes;
	}
	
	public List<SelectItem> getAvailableYears() {
		return availableYears;
	}
	
	public void setAvailableYears(List<SelectItem> availableYears) {
		this.availableYears = availableYears;
	}

	public boolean isGroupByPerson() {
		return groupByPerson;
	}

	public void setGroupByPerson(boolean groupByPerson) {
		this.groupByPerson = groupByPerson;
	}
	
	public int getCurrentYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}
	
	public void setCurrentYear(int currentYear) {
		this.currentYear = currentYear;
	}

	public String onPDF() throws IOException {
		
		Integer domain = DomainManager.getCurrentDomain();
		String connectionDomainName = AonUtil.getDomainName();
		String login = getLogin();
		HttpServletResponse response = DownloadUtil.getResponse();
		
		try (OutputStream out = DownloadUtil.initDownload(response, "Costes", MimeType.MIME_PDF);
				CloseableAONContext aonContext = AONContext.getAONContext(connectionDomainName, domain, login)) {
			
			SalaryType[] salaryEnumTypes = new SalaryType[salaryTypes.length];
			
			for (int i=0; i < salaryTypes.length; i++) {
				salaryEnumTypes[i] = SalaryType.valueOf(salaryTypes[i]);
			}
			
			Optional<EnterpriseRecord> optEnterprise = aonContext.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domain)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
			int enterpriseId = 0;
			if (optEnterprise.isPresent()) {
				enterpriseId = optEnterprise.get().getRegistry();
			}
			if (groupByPerson)
				JooqEnterpriseSalaryBuilder.generateEnterprisePayrollByEmployee(out, connectionDomainName, domain, login, startDate, endDate, enterpriseId, null, salaryEnumTypes, person);
			else
				JooqEnterpriseSalaryBuilder.generateEnterprisePayrollByPeriod(out, connectionDomainName, domain, login, startDate, endDate, enterpriseId, null, salaryEnumTypes, person);
				
			
			out.flush();
		}
		return null;
	}
	
	
	public String onExcel() throws IOException {
		
		Integer domain = DomainManager.getCurrentDomain();
		String connectionDomainName = AonUtil.getDomainName();
		String login = getLogin();
		HttpServletResponse response = DownloadUtil.getResponse();
		
		try (OutputStream out = DownloadUtil.initDownload(response, "Costes", MimeType.MIME_MS_EXCEL_2007);
				CloseableAONContext aonContext = AONContext.getAONContext(connectionDomainName, domain, login)) {
			
			
			SalaryType[] salaryEnumTypes;
			
			salaryEnumTypes = new SalaryType[salaryTypes != null ? salaryTypes.length : 0];
			
			int maximum = salaryTypes != null ? salaryTypes.length : 0;
				
			for (int i=0; i < maximum; i++) {
				salaryEnumTypes[i] = SalaryType.valueOf(salaryTypes[i]);
			}
			
			
			Optional<EnterpriseRecord> optEnterprise = aonContext.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domain)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
			int enterpriseId = 0;
			if (optEnterprise.isPresent()) {
				enterpriseId = optEnterprise.get().getRegistry();
			}
			
			EnterprisePayrollExcelParams params = new EnterprisePayrollExcelParams()
					.setDomainName(connectionDomainName)
					.setLogin(login)
					.setOs(out)
					.setEnterpriseId(enterpriseId);
			
			if (groupByPerson) {
				EnterprisePayrollExcel.enterprisePayrollGeneratorByEmployee(params.setExcelType(ExcelType.EMPLOYEE_COMPLETE), startDate, endDate, salaryEnumTypes, person);
			} else {
				EnterprisePayrollExcel.enterprisePayrollGeneratorByPeriod(params.setExcelType(ExcelType.PERIOD_COMPLETE), startDate, endDate, salaryEnumTypes, person);
			}
			
		}
		return null;
		
	}
	
	public String onDetailedExcel() throws IOException {
				
		Integer domain = DomainManager.getCurrentDomain();
		String connectionDomainName = AonUtil.getDomainName();
		String login = getLogin();
		HttpServletResponse response = DownloadUtil.getResponse();
		
		try (	OutputStream out = DownloadUtil.initDownload(response, "Costes", MimeType.MIME_MS_EXCEL_2007);
				CloseableAONContext aonContext = AONContext.getAONContext(connectionDomainName, domain, login)) {
			
			
			Calendar calendar = Calendar.getInstance(new Locale("es", "ES"));
			calendar.set(Calendar.MILLISECOND, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.HOUR, 0);
			
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.MONTH, Calendar.JANUARY);
			calendar.set(Calendar.YEAR, yearlyYear);			
			Date yearStart = calendar.getTime();
			
			calendar.set(Calendar.MONTH, Calendar.DECEMBER);
			calendar.set(Calendar.DAY_OF_MONTH, 31);
			Date yearEnd = calendar.getTime();
			
			
			SalaryType[] salaryEnumTypes;
			
			salaryEnumTypes = new SalaryType[salaryTypes != null ? salaryTypes.length : 0];
				
			int maximum = salaryTypes != null ? salaryTypes.length : 0;
			
			for (int i=0; i < maximum; i++) {
				salaryEnumTypes[i] = SalaryType.valueOf(salaryTypes[i]);
			}
			
			
			Optional<EnterpriseRecord> optEnterprise = aonContext.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domain)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
			int enterpriseId = 0;
			if (optEnterprise.isPresent()) {
				enterpriseId = optEnterprise.get().getRegistry();
			}
			EnterprisePayrollExcelParams params = new EnterprisePayrollExcelParams()
					.setDomainName(connectionDomainName)
					.setLogin(login)
					.setOs(out)
					.setEnterpriseId(enterpriseId);
			
			EnterprisePayrollExcel.completeEnterprisePayrollGenerator(params, yearStart, yearEnd, salaryEnumTypes/*, person*/);
			
		}
		return null;
		
	} 
	
	private String getAggregatedSummary(AggregatedAnnualSummary.SummaryType type) throws IOException {
		Integer domain = DomainManager.getCurrentDomain();
		String connectionDomainName = AonUtil.getDomainName();
		String login = getLogin();
		HttpServletResponse response = DownloadUtil.getResponse();
		
		try (	OutputStream out = DownloadUtil.initDownload(response, "Costes", MimeType.MIME_MS_EXCEL_2007);
				CloseableAONContext aonContext = AONContext.getAONContext(connectionDomainName, domain, login)) {
			
			SalaryType[] salaryEnumTypes;
			
			salaryEnumTypes = new SalaryType[salaryTypes != null ? salaryTypes.length : 0];
			
			int maximum = salaryTypes != null ? salaryTypes.length : 0;
			
			for (int i=0; i < maximum; i++) {
				salaryEnumTypes[i] = SalaryType.valueOf(salaryTypes[i]);
			}
			
			
			Optional<EnterpriseRecord> optEnterprise = aonContext.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domain)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
			int enterpriseId = 0;
			if (optEnterprise.isPresent()) {
				enterpriseId = optEnterprise.get().getRegistry();
			}
			
			AggregatedAnnualSummary.writeExcel(out, connectionDomainName, login, Optional.ofNullable(enterpriseId), Optional.empty(), yearlyYear, type, true);
			
		}
		return null;
	}
	
	public String onAggregatedMonthly() throws IOException {
		return getAggregatedSummary(AggregatedAnnualSummary.SummaryType.MONTHLY);
		
	} 
	
	public String onAggregatedQuarterly() throws IOException {
		return getAggregatedSummary(AggregatedAnnualSummary.SummaryType.QUARTERLY);
		
		
	} 
	
	public static List<SelectItem> getYears() {
		
		Integer domain = DomainManager.getCurrentDomain();
		String connectionDomainName = AonUtil.getDomainName();
		String login = getLogin();
		
		try (CloseableAONContext aonContext = AONContext.getAONContext(connectionDomainName, domain, login) ){
				DSLContext ctx = aonContext.getDslContext();
			//select distinct(extract( year from issue_date )) as year from salary where domain = 7138 order by start_date;
			Field<Integer> yearField = DSL.extract(SALARY.END_DATE, DatePart.YEAR).as("year");
			
			return ctx.selectDistinct(yearField)
			.from(SALARY)
			.where(SALARY.DOMAIN.eq(domain))
			.orderBy(yearField.desc())
			.fetchStream()
			.map(result -> result.get(yearField) != null ? new SelectItem(result.get(yearField)) : null)
			.filter(Objects::nonNull)
			.collect(Collectors.toList());
		}
		
	}
	
	private static String getLogin() {
		String login = "";
		UserUtils userUtils = new UserUtils();
		if (userUtils.getLoggedUser() != null && userUtils.getLoggedUser().getLogin() != null) {
			login = userUtils.getLoggedUser().getLogin();
		}
		return login;
	}
	
}

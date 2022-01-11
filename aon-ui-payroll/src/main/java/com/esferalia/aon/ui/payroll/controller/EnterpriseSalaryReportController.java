package com.esferalia.aon.ui.payroll.controller;

import static com.esferalia.aon.jooq.tables.Enterprise.ENTERPRISE;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.faces.component.util.DownloadUtil;
import com.code.aon.person.Person;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.ExcelType;
import com.esferalia.aon.in.payroll.pdf.JooqEnterpriseSalaryBuilder;
import com.esferalia.aon.jooq.tables.records.EnterpriseRecord;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.type.SalaryType;

public class EnterpriseSalaryReportController implements Serializable {

	private Date endDate;
	private Date startDate;
	private Person person;
	private String[] salaryTypes = {"SALARY", "EXTRA", "DELAY", "SETTLE"};
	private boolean groupByPerson;
	
	
	
	
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

	public boolean isGroupByPerson() {
		return groupByPerson;
	}

	public void setGroupByPerson(boolean groupByPerson) {
		this.groupByPerson = groupByPerson;
	}

	public String onPDF() throws IOException {
		HttpServletResponse response = DownloadUtil.getResponse();
		try ( OutputStream out = DownloadUtil.initDownload(response, "Costes", MimeType.MIME_PDF) ) {
			
			UserUtils userUtils = new UserUtils();
			String login = "";
			
			if (userUtils.getLoggedUser() != null && userUtils.getLoggedUser().getLogin() != null) {
				login = userUtils.getLoggedUser().getLogin();
			}
			
			Integer domain = DomainManager.getCurrentDomain();
			String connectionDomainName = AonUtil.getDomainName();
			
			SalaryType[] salaryEnumTypes = new SalaryType[salaryTypes.length];
			
			for (int i=0; i < salaryTypes.length; i++) {
				salaryEnumTypes[i] = SalaryType.valueOf(salaryTypes[i]);
			}
			
			AONContext ctx = AONContext.getAONContext(connectionDomainName, domain, login);
			
			Optional<EnterpriseRecord> optEnterprise = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domain)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
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
		
		HttpServletResponse response = DownloadUtil.getResponse();
		try ( OutputStream out = DownloadUtil.initDownload(response, "Costes", MimeType.MIME_MS_EXCEL_2007) ) {
			
			UserUtils userUtils = new UserUtils();
			String login = "";
			
			if (userUtils.getLoggedUser() != null && userUtils.getLoggedUser().getLogin() != null) {
				login = userUtils.getLoggedUser().getLogin();
			}
			
			Integer domain = DomainManager.getCurrentDomain();
			String connectionDomainName = AonUtil.getDomainName();
			
			SalaryType[] salaryEnumTypes;
			
//			if (salaryTypes.length == 0) {
//				salaryEnumTypes = new SalaryType[SalaryType.SALARIES.size()];
//				int ind = 0;
//				for (Byte enumValue : SalaryType.SALARIES) {
//					salaryEnumTypes[ind] = SalaryType.values()[enumValue];
//				}
//				
//			} else {
				salaryEnumTypes = new SalaryType[salaryTypes != null ? salaryTypes.length : 0];
//			}
			
				int maximum = salaryTypes != null ? salaryTypes.length : 0;
				
			for (int i=0; i < maximum; i++) {
				salaryEnumTypes[i] = SalaryType.valueOf(salaryTypes[i]);
			}
			
			AONContext ctx = AONContext.getAONContext(connectionDomainName, domain, login);
			
			Optional<EnterpriseRecord> optEnterprise = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domain)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
			int enterpriseId = 0;
			if (optEnterprise.isPresent()) {
				enterpriseId = optEnterprise.get().getRegistry();
			}
			
			if (groupByPerson)
				EnterprisePayrollExcel.enterprisePayrollGeneratorByEmployee(connectionDomainName, login, out, Optional.ofNullable(enterpriseId), Optional.empty(), startDate, endDate, ExcelType.EMPLOYEE_SUMMARY, salaryEnumTypes, person);
			else
				EnterprisePayrollExcel.enterprisePayrollGeneratorByPeriod(connectionDomainName, login, out, Optional.ofNullable(enterpriseId), Optional.empty(), startDate, endDate, ExcelType.PERIOD_SUMMARY, salaryEnumTypes, person);
				
			
		}
		return null;
		
	}
	
	public String onDetailedExcel() throws IOException {
		
		HttpServletResponse response = DownloadUtil.getResponse();
		try ( OutputStream out = DownloadUtil.initDownload(response, "Costes", MimeType.MIME_MS_EXCEL_2007) ) {
			
			UserUtils userUtils = new UserUtils();
			String login = "";
			
			if (userUtils.getLoggedUser() != null && userUtils.getLoggedUser().getLogin() != null) {
				login = userUtils.getLoggedUser().getLogin();
			}
			
			Integer domain = DomainManager.getCurrentDomain();
			String connectionDomainName = AonUtil.getDomainName();
			
			SalaryType[] salaryEnumTypes;
			
//			if (salaryTypes.length == 0) {
//				salaryEnumTypes = new SalaryType[SalaryType.SALARIES.size()];
//				int ind = 0;
//				for (Byte enumValue : SalaryType.SALARIES) {
//					salaryEnumTypes[ind] = SalaryType.values()[enumValue];
//				}
//				
//			} else {
				salaryEnumTypes = new SalaryType[salaryTypes != null ? salaryTypes.length : 0];
//			}
				
			int maximum = salaryTypes != null ? salaryTypes.length : 0;
			
			for (int i=0; i < maximum; i++) {
				salaryEnumTypes[i] = SalaryType.valueOf(salaryTypes[i]);
			}
			
			AONContext ctx = AONContext.getAONContext(connectionDomainName, domain, login);
			
			Optional<EnterpriseRecord> optEnterprise = ctx.getDslContext().select(ENTERPRISE.REGISTRY).from(ENTERPRISE).where(ENTERPRISE.DOMAIN.eq(domain)).fetchStreamInto(ENTERPRISE).filter(Objects::nonNull).findFirst();
			int enterpriseId = 0;
			if (optEnterprise.isPresent()) {
				enterpriseId = optEnterprise.get().getRegistry();
			}
			
			EnterprisePayrollExcel.completeEnterprisePayrollGenerator(connectionDomainName, login, out, Optional.ofNullable(enterpriseId), Optional.empty(), startDate, endDate, salaryEnumTypes, person);
//			if (groupByPerson) {				
//				EnterprisePayrollExcel.enterprisePayrollGeneratorByEmployee(connectionDomainName, login, out, Optional.ofNullable(enterpriseId), Optional.empty(), startDate, endDate, ExcelType.EMPLOYEE_COMPLETE, salaryEnumTypes, person);
//			} else {
////				EnterprisePayrollExcel.enterprisePayrollGeneratorByPeriod(connectionDomainName, login, out, Optional.ofNullable(enterpriseId), Optional.empty(), startDate, endDate, ExcelType.PERIOD_COMPLETE, salaryEnumTypes, person);
//			}
			
		}
		return null;
		
	}
	
	
}

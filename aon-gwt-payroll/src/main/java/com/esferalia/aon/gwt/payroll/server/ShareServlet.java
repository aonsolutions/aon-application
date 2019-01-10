package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.getSalaryReport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.RelationalExpression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.report.StatelessReportManager;
import com.esferalia.aon.gwt.payroll.shared.ShareService;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.attachment.AttachType;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.payroll.Salary;
import com.esferalia.aon.watson.util.AonStringUtils;

public class ShareServlet extends HttpServlet implements ShareService {

	private static Locale ES = new Locale("es");

	static class SalaryProvider implements ICollectionProvider {

		private int salaryId;
		private IManagerBean managerBean;

		public SalaryProvider(int salaryId, IManagerBean managerBean) {
			this.salaryId = salaryId;
			this.managerBean = managerBean;
		}

		@Override
		public Collection getCollection() {
			try {
				return getCollection(true);
			} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				return null;
			}
		}

		@Override
		public Collection getCollection(boolean forceRefresh)
				throws ManagerBeanException {
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					managerBean.getFieldName(IEntityAlias.SALARY_ID), salaryId);
			return getSalaries(managerBean, criteria);
		}

	};

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
	
		initFacesContext(req, resp);

		try {
			ReportManager reportManager = new StatelessReportManager();
			reportManager.setOutputFormat(OutputFormat.PDF);

			IManagerBean beanManager = BeanManager
				.getManagerBean(com.esferalia.aon.payroll.Salary.class);
			Criteria criteria = getCriteria(beanManager, req);
			List<Salary> salaries = getSalaries(beanManager, criteria);

			PrintStream os = new PrintStream(resp.getOutputStream(), false,"UTF-8");

			for (Salary salary : salaries) {
				Domain domain = AON.getDomain(req.getServerName(), salary.getDomain(), "");
				
				LinkedList<String> emails = AON.getRMediaStream(domain.getName(), domain.getId(), "", 
					f -> f.getRegistryProperty().eq(salary.getContract().getPerson().getId())
					.and(f.getMediaProperty().eq(com.esferalia.aon.occam.api.model.type.MediaType.EMAIL.value())))
					.map(r -> r.getValue()).collect(Collectors.toCollection(LinkedList::new));
				if ( emails == null  || emails.isEmpty() ) {
					doJson(salary, 0, null, "Trabajador sin email", os);
					continue;
				}
				
				DomainGserviceaccount domainGserviceaccount = AON.getDomainGserviceaccount(domain.getName(), domain.getId(), "");
				
				reportManager.setCollectionProvider(new SalaryProvider(salary
					.getId(), beanManager));
				byte data[] = generate(domain.getName(), reportManager, salary);

				Attach attach = new Attach()
					.setData(data)
					.setAttachType(AttachType.PAYSHEET)
					.setAttachModule(salary.getContract().getPerson().getId())
					.setDomain(domain)
					.setMimeType(MimeType.PDF)
					.setDate(new Date(System.currentTimeMillis()))
					.setDescription(getDescrition(salary))
					.setDate(salary.getIssueDate());
							
				Boolean ok = DriveUtils.paysheet(domainGserviceaccount, attach, emails);
				
				doJson(salary, data.length, attach.getDescription(), !ok ? "Error al compartir" : "",	os);
			}

			os.close();
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		} catch (ReportException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			releaseFacesContext();
		}
	}

	// -------------------------------------------------------- private methods

	private void releaseFacesContext() {
		AonServletUtils.releaseFacesContext();
	}

	private void initFacesContext(HttpServletRequest req,
			HttpServletResponse resp) {
		AonServletUtils.initFacesContext(getServletContext(), req, resp);
	}

	private void doJson(Salary salary, int size, String description,
			String error, PrintStream os) {
		os.print('{');
		os.printf("\"id\":\"%d\"", salary.getId());
		os.printf(",\"size\":\"%d\"", size);
		if (!AonStringUtils.isBlank(error))
			os.printf(",\"error\":\"%s\"", error);
		if (!AonStringUtils.isBlank(description))
			os.printf(",\"description\":\"%s\"", description);
		os.printf(",\"employeeId\":\"%d\"", salary.getContract().getId());
		os.printf(",\"employeeName\":\"%s\"", salary.getEmployeeName());
		os.print('}');
		os.flush();
	}

	private byte[] generate(String domain, ReportManager reportManager, Salary salary)
			throws ReportException, SQLException {
		String salaryReport = getSalaryReport(
				domain,
				salary.getContract().getWorkPlace().getEnterprise().getId(), 
				salary.getType()
				);

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		reportManager.execute(outputStream, salaryReport);

		return outputStream.toByteArray();
	}

	// ------------------------------------------------------------------------

	private static List<Salary> getSalaries(IManagerBean beanManager,
			Criteria criteria) throws ManagerBeanException {
		List<?> list = beanManager.getList(criteria);
		return (List<Salary>) list;
	}

	private static Criteria getCriteria(IManagerBean beanManager,
			HttpServletRequest req) throws ManagerBeanException {
		RelationalExpression employeesExpr = getEmployeesExpression(
				beanManager, req);
		RelationalExpression workPlacesExpr = getWorkPlacesExpression(
				beanManager, req);
		RelationalExpression enterprisesExpr = getEnperprisesExpression(
				beanManager, req);
		RelationalExpression salariesExpr = getSalariesExpression(beanManager,
				req);

		Criteria criteria = new Criteria();
		if (salariesExpr != null)
			criteria.addExpression(salariesExpr);
		if (employeesExpr != null)
			criteria.addExpression(employeesExpr);
		if (workPlacesExpr != null)
			criteria.addExpression(workPlacesExpr);
		if (enterprisesExpr != null)
			criteria.addExpression(enterprisesExpr);

		String month = req.getParameter(MONTH);
		String year = req.getParameter(YEAR);
		if (!AonStringUtils.isBlank(month) && !AonStringUtils.isBlank(year)) {

			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, Integer.valueOf(year));
			calendar.set(Calendar.MONTH, Integer.valueOf(month));
			calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the
													// month
													// has value 1.
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);

			Date startDate = new Date(calendar.getTimeInMillis());

			calendar.set(Calendar.DAY_OF_MONTH,
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = new Date(calendar.getTimeInMillis());

			criteria.addBetweenExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_CHARGE_DATE),
					startDate, endDate);
		}

		criteria.addOrder(beanManager
				.getFieldName(IEntityAlias.SALARY_CONTRACT_PERSON_ID));

		return criteria;
	}

	private static RelationalExpression getSalariesExpression(
			IManagerBean beanManager, HttpServletRequest request)
			throws ManagerBeanException {
		String salaries[] = request.getParameterValues(SALARY);
		if (salaries == null || salaries.length == 0)
			return null;

		List<Integer> salariesList = new ArrayList<Integer>(salaries.length);
		for (String salary : salaries)
			salariesList.add(Integer.valueOf(salary));

		return ExpressionUtilities.getInExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_ID), salariesList);
	}

	private static RelationalExpression getEnperprisesExpression(
			IManagerBean beanManager, HttpServletRequest request)
			throws ManagerBeanException {
		String enterprises[] = request.getParameterValues(ENTERPRISE);
		if (enterprises == null || enterprises.length == 0)
			return null;

		List<Integer> enterprisesList = new ArrayList<Integer>(
				enterprises.length);
		for (String enterprise : enterprises)
			enterprisesList.add(Integer.valueOf(enterprise));

		return ExpressionUtilities
				.getInExpression(
						beanManager
								.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID),
						enterprisesList);
	}

	private static RelationalExpression getWorkPlacesExpression(
			IManagerBean beanManager, HttpServletRequest request)
			throws ManagerBeanException {
		String workplaces[] = request.getParameterValues(WORKPLACE);
		if (workplaces == null || workplaces.length == 0)
			return null;

		List<Integer> workplacesList = new ArrayList<Integer>(workplaces.length);
		for (String workplace : workplaces)
			workplacesList.add(Integer.valueOf(workplace));

		return ExpressionUtilities.getInExpression(beanManager
				.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID),
				workplacesList);
	}

	private static RelationalExpression getEmployeesExpression(
			IManagerBean beanManager, HttpServletRequest request)
			throws ManagerBeanException {
		String employees[] = request.getParameterValues(EMPLOYEE);
		if (employees == null || employees.length == 0)
			return null;

		List<Integer> employeesList = new ArrayList<Integer>(employees.length);
		for (String employee : employees)
			employeesList.add(Integer.valueOf(employee));

		return ExpressionUtilities.getInExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_ID),
				employeesList);
	}

	private static String getDescrition(final Salary salary) {
		if (salary.getEndDate().getYear() == salary.getStartDate().getYear())
			if (salary.getEndDate().getMonth() == salary.getStartDate()
					.getMonth())
				return String.format(salary.getContract().getPerson().getFullName() + 
						" - %1$s del %2$te al %3$te de %3$tB de %3$tY", salary
								.getType().getName(ES), salary.getStartDate(),
						salary.getEndDate());
			else
				return String.format(salary.getContract().getPerson().getFullName() +
						" - %1$s del %2$te de %2$tB  al %3$te de %3$tB de %3$tY",
						salary.getType().getName(ES), salary.getStartDate(),
						salary.getEndDate());

		else
			return String
					.format(salary.getContract().getPerson().getFullName() +
						" - %1$s del %2$te de %2$tB de %3$tY al %3$te de %3$tB de %3$tY",
						salary.getType().getName(ES),
						salary.getStartDate(), salary.getEndDate());
	}
}

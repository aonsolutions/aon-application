package com.esferalia.aon.gwt.employee.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.PERSON;
import static com.esferalia.aon.payroll.sql.SQLConstants.RADDRESS;
import static com.esferalia.aon.payroll.sql.SQLConstants.REGISTRY;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;
import static com.esferalia.aon.payroll.sql.SQLConstants.WORKPLACE;
import static com.esferalia.aon.gwt.employee.server.AonServletUtils.*;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.faces.FactoryFinder;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.context.FacesContextFactory;
import javax.faces.lifecycle.Lifecycle;
import javax.faces.lifecycle.LifecycleFactory;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.time.DateUtils;


import net.sf.jasperreports.engine.export.JRHtmlExporter;
import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.dao.CriteriaUtilities;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Order;
import com.code.aon.ql.OrderByList;
import com.code.aon.registry.Registry;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.employee.client.EmployeesService;
import com.esferalia.aon.gwt.employee.shared.Cost;
import com.esferalia.aon.gwt.employee.shared.Employee;
import com.esferalia.aon.gwt.employee.shared.Enterprise;
import com.esferalia.aon.gwt.employee.shared.Salary;
import com.esferalia.aon.gwt.employee.shared.Workplace;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.EnterpriseColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PersonColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RaddressColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.RegistryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.WorkplaceColumns;
import com.esferalia.aon.ui.payroll.controller.IPayrollConstants;
import com.esferalia.aon.ui.payroll.controller.salary.SalaryExpenseController;
import com.esferalia.aon.web.employee.controller.ManagerController;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class EmployeesServiceImpl extends AonRemoteServiceServlet implements
		EmployeesService {
	
	private static final Map<Object, Object> JR_HTML_EXPORTER_PARAMS = 
			new HashMap<Object, Object>() {
		{
			put(JRHtmlExporterParameter.HTML_HEADER, "<div class='page' >");
			put(JRHtmlExporterParameter.BETWEEN_PAGES_HTML, "</div><div class='page' >");
			put(JRHtmlExporterParameter.HTML_FOOTER, "</div>");
		}
	};

	public Enterprise getEnterprise() throws IllegalArgumentException {
		try {
			initFacesContext();

			Integer registryID = getEnterpriseID();
			Connection connection = getConnection();
			return getEnterprise(registryID, connection);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();			
		}
	}

	public List<Salary> getSalaries(Employee employee)
			throws IllegalArgumentException {
		try {
			initFacesContext();

			Connection connection = getConnection();
			if ( employee != null ) {
				return getSalaries(connection, employee.getId());
			}
			else {
				Integer personId = getPersonID();
				Integer enterpriseId = getEnterpriseID();
				Date maxChargeDate = Calendar.getInstance().getTime();
				return getSalaries(connection, enterpriseId, personId, maxChargeDate);
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();			
		}
	}

	
	public String getSalaryReceiptHTML(Salary salary, int zoom)
			throws IllegalArgumentException {

			Map<Object, Object> parameters = 
					new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
			parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /* not roud to int*/);
			
			
			Map<Object, Object> images = new HashMap<Object, Object>();
			parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images );
			String imagesUri = String.format( "jasper_image/salary/%d/", salary.getId());  
			parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri );

			String html  = getSalaryReceiptHTML(salary, parameters);
			
			for (Entry<Object, Object> image : images.entrySet()) {
				String name = String.format("%s%s", imagesUri, image.getKey());
				JasperImageServlet.saveImage(name,  ( byte [] )image.getValue());
			}
			
			return html;
	}

	public String getSalaryReceiptHTML(Cost cost, int zoom)
			throws IllegalArgumentException {

			Map<Object, Object> parameters = 
					new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
			parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /* not roud to int*/);
			
			
			Map<Object, Object> images = new HashMap<Object, Object>();
			parameters.put(JRHtmlExporterParameter.IMAGES_MAP, images );
			
			String imagesUri = String.format( "jasper_image/salary/%d/%d/%d/%d/",
					cost.getMonth(),
					cost.getYear(),
					cost.getWorkplaceId(),
					cost.getEnterpriseId() );  
			
			parameters.put(JRHtmlExporterParameter.IMAGES_URI, imagesUri );

			String html  = getSalaryReceiptHTML(cost, parameters);
			
			for (Entry<Object, Object> image : images.entrySet()) {
				String name = String.format("%s%s", imagesUri, image.getKey());
				JasperImageServlet.saveImage(name,  ( byte [] )image.getValue());
			}
			
			return html;
	}

	public String getSalaryReceiptHTML(Salary salary, Map<Object, Object> parameters )
			throws IllegalArgumentException {

		try {

			initFacesContext();

			IManagerBean beanManager = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_ID),
					salary.getId());

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);
			reportManager.setCollectionProvider(new AonServletUtils.SalaryProvider(criteria));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			String salaryReport = getSalaryReport();
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();			
		}

	}
	
	public String getSalaryReceiptHTML(Cost cost, Map<Object, Object> parameters )
			throws IllegalArgumentException {
		try {

			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);
			reportManager.setCollectionProvider(getSalariesProvider(cost));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			String salaryReport = getSalaryReport();
			reportManager.execute(out, salaryReport, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();			
		}
	}
	
	public String getCostReceiptHTML(Cost cost, int zoom)
			throws IllegalArgumentException {
		try {
			initFacesContext();

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);
			
			reportManager.setCollectionProvider(getSalariesProvider(cost) );
			
			// Really I hate this spaghetti piece of code. 
			// For pass 'month' & 'year' to a report, we 
			// must put it in a controller ?????. 
			SalaryExpenseController controller = 
					(SalaryExpenseController) AonUtil.getRegisteredBean(IPayrollConstants.SALARY_EXPENSE_CONTROLLER_NAME);
			controller.setShowSalaryExpenseWindow(false);
			controller.setYear(cost.getYear());
			Month month = Month.getMonthByValue(cost.getMonth() );
			controller.setMonth(month);
			
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			Map<Object, Object> parameters = new HashMap<Object, Object>(JR_HTML_EXPORTER_PARAMS);
			parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoom / 100.00f /* not round to int*/);
			reportManager.execute(out, IPayrollConstants.COST_REPORT, parameters);

			return out.toString();

		} catch (ReportException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			throw new IllegalArgumentException(e);
		} finally {
			releaseFacesContext();			
		}

	}

	private String getSalaryReport() throws ReportException {
		Connection connection = getConnection();
		int enterpriseId = getEnterpriseID();
		try {
			return AonServletUtils.getSalaryReport(connection, enterpriseId);
		} catch (SQLException e) {
			throw new ReportException(e.getLocalizedMessage());
		}
	}
	
	
	private static ICollectionProvider getSalariesProvider(Cost cost) throws ManagerBeanException{
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.esferalia.aon.payroll.Salary.class);
		Criteria criteria = new Criteria();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.YEAR, cost.getYear());
		calendar.set( Calendar.MONTH, cost.getMonth());
		calendar.set( Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
		calendar.set( Calendar.HOUR, 0);
		calendar.set( Calendar.MINUTE, 0);
		calendar.set( Calendar.SECOND, 0);
				
		Date startDate = calendar.getTime();
		
		calendar.set(Calendar.DAY_OF_MONTH, 
				calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();
		
		if ( cost.getWorkplaceId() != 0  ){
			criteria.addEqualExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID ),
					cost.getWorkplaceId() );
		}
		else {
			criteria.addEqualExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID ),
					cost.getEnterpriseId() );
		}
		
		criteria.addBetweenExpression(
				beanManager.getFieldName(IEntityAlias.SALARY_END_DATE),
				startDate, 
				endDate );
		
		criteria.addOrder(beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
		criteria.addOrder(beanManager.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
		
		
		return new AonServletUtils.SalaryProvider(criteria);
	}


	private static List<Salary> getSalaries(Connection connection, Integer contractId)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			
			
			String sql = "SELECT * " 
					+ " FROM " + SALARY 
					+ " WHERE " + SALARY + "." + SalaryColumns.CONTRACT + " = ?"
					+ " ORDER BY " + SALARY + "." + SalaryColumns.CHARGE_DATE + " DESC" ;
			
			
			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, contractId);
			rs = stmt.executeQuery();

			List<Salary> salaries = new LinkedList<Salary>();
			while (rs.next()) {
				Salary salary = new Salary();
				salary.setId(rs.getInt(SalaryColumns.ID));
				
				salary.setStartDate(rs.getDate(SalaryColumns.START_DATE));
				salary.setEndDate(rs.getDate(SalaryColumns.END_DATE));
				salary.setIssueDate(rs.getDate(SalaryColumns.ISSUE_DATE));
				salary.setChargeDate(rs.getDate(SalaryColumns.CHARGE_DATE));
				
				salary.setType(getSalaryType((Integer) rs.getObject(SalaryColumns.TYPE)));
				
				salaries.add(salary);
			}

			return salaries;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}
	
	private static List<Cost> getEnterpriseCosts(Connection connection, Integer enterpriseId)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";
			
			String sql = "SELECT"
					+" MONTH("+ SALARY +"."+SalaryColumns.CHARGE_DATE+") " + monthCol
					+", YEAR("+ SALARY +"."+SalaryColumns.CHARGE_DATE+") " + yearCol
					+" FROM "+ENTERPRISE
					+", "+WORKPLACE
					+", "+CONTRACT
					+", "+SALARY
					+" WHERE"
					+" "+ENTERPRISE+"."+ EnterpriseColumns.REGISTRY +" = "+WORKPLACE+"."+WorkplaceColumns.ENTERPRISE
					+" AND "+WORKPLACE+"." + WorkplaceColumns.ID + " = "+CONTRACT+"." + ContractColumns.WORKPLACE
					+" AND "+CONTRACT+"."+ ContractColumns.ID +" = "+ SALARY +"." + SalaryColumns.CONTRACT
					+" AND "+ENTERPRISE+"."+EnterpriseColumns.REGISTRY+" = ?" 
					+" GROUP BY 1, 2"
					+" ORDER BY 2 DESC , 1 DESC";


			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, enterpriseId);
			rs = stmt.executeQuery();

			List<Cost> costs = new LinkedList<Cost>();
			while (rs.next()) {
				
				int month = rs.getInt(monthCol);
				// MySQL MONTH(date) function returns the month for date, 
				// in the range 1 to 12 for January to December, or 0 for 
				// dates such as '0000-00-00' or '2008-00-00' that have a zero month part.
				if ( month == 0 ) {
					continue;
				} 
				
				
				Cost cost = new Cost();
				int year = rs.getInt(yearCol);
				
				cost.setYear(year);
				cost.setMonth(month -1 ); 
				cost.setEnterpriseId(enterpriseId);
				
				costs.add(cost);
			}

			return costs;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}
	
	private static List<Cost> getWorkplaceCosts(Connection connection, Integer workplaceId)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String yearCol = "YEAR";
			String monthCol = "MONTH";
			
			String sql = "SELECT"
					+" MONTH("+ SALARY +"."+SalaryColumns.CHARGE_DATE+") " + monthCol
					+", YEAR("+ SALARY +"."+SalaryColumns.CHARGE_DATE+") " + yearCol
					+" FROM "+WORKPLACE
					+", "+CONTRACT
					+", "+SALARY
					+" WHERE"
					+" "+WORKPLACE+"." + WorkplaceColumns.ID + " = "+CONTRACT+"." + ContractColumns.WORKPLACE
					+" AND "+CONTRACT+"."+ ContractColumns.ID +" = "+ SALARY +"." + SalaryColumns.CONTRACT
					+" AND "+WORKPLACE+"."+WorkplaceColumns.ID+" = ?" 
					+" GROUP BY 1, 2"
					+" ORDER BY 2 DESC , 1 DESC";


			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, workplaceId);
			rs = stmt.executeQuery();

			List<Cost> costs = new LinkedList<Cost>();
			while (rs.next()) {
				
				int month = rs.getInt(monthCol);
				// MySQL MONTH(date) function returns the month for date, 
				// in the range 1 to 12 for January to December, or 0 for 
				// dates such as '0000-00-00' or '2008-00-00' that have a zero month part.
				if ( month == 0 ) {
					continue;
				} 
				
				
				Cost cost = new Cost();
				int year = rs.getInt(yearCol);
				
				cost.setYear(year);
				cost.setMonth(month -1 ); 
				cost.setWorkplaceId(workplaceId);
				
				costs.add(cost);
			}

			return costs;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}
	

	private static Enterprise getEnterprise(Integer registryID, Connection connection)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + " FROM " + REGISTRY + ", " + ENTERPRISE
					+ ", " + WORKPLACE + ", " + RADDRESS + ", " + CONTRACT
					+ ", " + PERSON + " WHERE " + REGISTRY + "."
					+ RegistryColumns.ID + " = ?" + " AND " + REGISTRY + "."
					+ RegistryColumns.ID + " = " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " AND " + ENTERPRISE + "."
					+ EnterpriseColumns.REGISTRY + " = " + WORKPLACE + "."
					+ WorkplaceColumns.ENTERPRISE + " AND " + WORKPLACE + "."
					+ WorkplaceColumns.ADDRESS + " = " + RADDRESS + "."
					+ RaddressColumns.ID + " AND " + WORKPLACE + "."
					+ WorkplaceColumns.ID + " = " + CONTRACT + "."
					+ ContractColumns.WORKPLACE + " AND " + CONTRACT + "."
					+ ContractColumns.PERSON + " = " + PERSON + "."+ PersonColumns.REGISTRY
					
					+ " AND ( " + CONTRACT + "." + ContractColumns.END_DATE + " IS NULL"
					+ " OR " + CONTRACT + "." + ContractColumns.END_DATE + " >= ? )"
					
					+ " ORDER BY " + WORKPLACE + "." + WorkplaceColumns.ID 
					+ " ," + PERSON + "." + PersonColumns.FIRST_SURNAME;

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, registryID);
			
			stmt.setDate(2, getMonthStartDate());
			rs = stmt.executeQuery();

			EnterpriseHandler enterpriseHandler = new EnterpriseHandler();

			WorkplaceHandler workplaceHandler = new WorkplaceHandler(
					enterpriseHandler);

			EmployeeHandler employeeHandler = new EmployeeHandler(
					workplaceHandler);

			groups(rs, enterpriseHandler, workplaceHandler, employeeHandler);
			
			Enterprise enterprise = enterpriseHandler.getEnterprise();
			
			List<Cost> enterpriseCosts = getEnterpriseCosts(connection, enterprise.getId());
			enterprise.setCosts(enterpriseCosts);
			
			List<Workplace> workplaces = enterprise.getWorkplaces();
			for (Workplace workplace : workplaces) {
				List<Cost> workplaceCosts = 
						getWorkplaceCosts(connection, workplace.getId());
				workplace.setCosts(workplaceCosts);
			}
			

			return enterprise;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				stmt.close();
			}
		}
	}
	
	
	private static List<Salary> getSalaries(Connection connection, Integer enterpriseID, Integer personId, Date toDate)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			
			
			String sql = "SELECT " + SALARY + ".* "  
					+" FROM " + SALARY 
					+", " + CONTRACT 
					+", " + WORKPLACE 
					+" WHERE " + SALARY + "." + SalaryColumns.CONTRACT + " = " + CONTRACT + "." +  ContractColumns.ID
					+" AND " + CONTRACT + "." +  ContractColumns.WORKPLACE + " = " + WORKPLACE + "." + WorkplaceColumns.ID  
					+" AND " + CONTRACT + "." + ContractColumns.PERSON + " = ? "
					+" AND " + WORKPLACE + "." + WorkplaceColumns.ENTERPRISE + " = ? "
					+" AND " + SALARY + "." + SalaryColumns.CHARGE_DATE + " <= ? "
					+ " ORDER BY " + SALARY + "." + SalaryColumns.CHARGE_DATE + " DESC" ;

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, personId);
			stmt.setInt(2, enterpriseID);
			stmt.setDate(3, new java.sql.Date(toDate.getTime()));
			rs = stmt.executeQuery();


			List<Salary> salaries = new LinkedList<Salary>();
			while (rs.next()) {
				Salary salary = new Salary();
				salary.setId(rs.getInt(SalaryColumns.ID));
				
				salary.setStartDate(rs.getDate(SalaryColumns.START_DATE));
				salary.setEndDate(rs.getDate(SalaryColumns.END_DATE));
				salary.setIssueDate(rs.getDate(SalaryColumns.ISSUE_DATE));
				salary.setChargeDate(rs.getDate(SalaryColumns.CHARGE_DATE));
				
				salary.setType(getSalaryType((Integer) rs.getObject(SalaryColumns.TYPE)));
				
				salaries.add(salary);
			}

			return salaries;
		} finally {
			if (rs != null) {
				rs.close();
			}
			if (stmt != null) {
				rs.close();
			}
		}
	}



	private static void groups(ResultSet rs, GroupHandler... handlers)
			throws SQLException {

		Map<String, Object> values = new HashMap<String, Object>();

		while (rs.next()) {
			for (int i = 0; i < handlers.length; i++) {
				GroupHandler handler = handlers[i];
				String col = handler.getCol();
				Object oldValue = values.get(col);
				Object newValue = rs.getObject(col);
				if (!equals(oldValue, newValue)) {
					handler.beginGroup(rs);
					values.put(col, newValue);
				}
			}
		}

	}

	private static boolean equals(Object one, Object another) {
		if (one == another) {
			return true;
		}
		if (one == null || another == null) {
			return false;
		}
		return one.equals(another);
	}
	
	
	private static Salary.Type getSalaryType(Integer ordinal) {
		if ( ordinal == null || ordinal < 0 ) { 
			return null;
		}
		
		Salary.Type types [] = Salary.Type.values();
		
		if ( ordinal >= types.length ) {
			return null;
		}
		
		return types[ordinal];
	}

	private static String tableCol(String table, String col) {
		return String.format("%1$s.%2$s", table, col);
	}


	private static interface GroupHandler {

		String getCol();

		void beginGroup(ResultSet rs) throws SQLException;
	}

	private static abstract class AbstractHandler implements GroupHandler {

		private String col;

		public AbstractHandler(String table, String col) {
			this(tableCol(table, col));
		}

		public AbstractHandler(String col) {
			this.col = col;
		}

		@Override
		public String getCol() {
			return col;
		}
	}

	private static class EnterpriseHandler extends AbstractHandler {

		private Enterprise enterprise;

		public EnterpriseHandler() {
			super(ENTERPRISE, EnterpriseColumns.REGISTRY);
		}

		public Enterprise getEnterprise() {
			return enterprise;
		}

		@Override
		public void beginGroup(ResultSet rs) throws SQLException {
			enterprise = new Enterprise();
			enterprise.setId(rs.getInt(tableCol(REGISTRY,
					RegistryColumns.ID)));
			enterprise.setName(rs.getString(tableCol(REGISTRY,
					RegistryColumns.NAME)));
			
		}

	}

	private static class WorkplaceHandler extends AbstractHandler {

		private Workplace workplace;
		private EnterpriseHandler enterpriseHandler;

		public WorkplaceHandler(EnterpriseHandler enterpriseHandler) {
			super(WORKPLACE, WorkplaceColumns.ID);
			this.enterpriseHandler = enterpriseHandler;
		}

		/**
		 * 
		 * @return Last, active workplace.
		 */
		public Workplace getWorkplace() {
			return workplace;
		}

		@Override
		public void beginGroup(ResultSet rs) throws SQLException {
			workplace = new Workplace();
			workplace.setId(rs.getInt(tableCol(WORKPLACE,
					WorkplaceColumns.ID)));
			workplace.setAddress(rs.getString(tableCol(RADDRESS,
					RaddressColumns.ADDRESS)));
			workplace.setDescription(rs.getString(tableCol(WORKPLACE,
					WorkplaceColumns.DESCRIPTION)));

			enterpriseHandler.getEnterprise().addWorkplace(workplace);
		}

	}

	private static class EmployeeHandler extends AbstractHandler {

		private WorkplaceHandler workplaceHandler;

		public EmployeeHandler(WorkplaceHandler workplaceHandler) {
			super(CONTRACT, ContractColumns.ID);
			this.workplaceHandler = workplaceHandler;
		}

		@Override
		public void beginGroup(ResultSet rs) throws SQLException {
			Employee employee = new Employee();
			employee.setId(rs.getInt(tableCol(CONTRACT, ContractColumns.ID)));
			employee.setName(rs.getString(tableCol(PERSON, PersonColumns.NAME)));
			employee.setFirstSurname(rs.getString(tableCol(PERSON,
					PersonColumns.FIRST_SURNAME)));
			employee.setSecondSurName(rs.getString(tableCol(PERSON,
					PersonColumns.SECOND_SURNAME)));
			workplaceHandler.getWorkplace().addEmployee(employee);
		}
	}
	
	private static java.sql.Date getMonthStartDate() {
		Calendar calendar = Calendar.getInstance();
		calendar.set( Calendar.YEAR, 2011);
		calendar.set( Calendar.MONTH, 0);
		calendar.set( Calendar.DAY_OF_MONTH, 1);
		
		return new java.sql.Date ( calendar.getTimeInMillis());
	}

}

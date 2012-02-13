package com.esferalia.aon.gwt.employee.server;

import static com.esferalia.aon.payroll.sql.SQLConstants.CONTRACT;
import static com.esferalia.aon.payroll.sql.SQLConstants.ENTERPRISE;
import static com.esferalia.aon.payroll.sql.SQLConstants.PERSON;
import static com.esferalia.aon.payroll.sql.SQLConstants.RADDRESS;
import static com.esferalia.aon.payroll.sql.SQLConstants.REGISTRY;
import static com.esferalia.aon.payroll.sql.SQLConstants.SALARY;
import static com.esferalia.aon.payroll.sql.SQLConstants.WORKPLACE;

import java.io.ByteArrayOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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

import net.sf.jasperreports.engine.export.JRHtmlExporterParameter;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.SingleCollectionProvider;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Month;
import com.code.aon.company.EnterpriseUser;
import com.code.aon.ql.Criteria;
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

	public Enterprise getEnterprise() throws IllegalArgumentException {
		try {
			Integer registryID = getEnterpriseID();
			Connection connection = getConnection();
			return getEnterprise(registryID, connection);
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	public List<Salary> getSalaries(Employee employee)
			throws IllegalArgumentException {
		try {
			Connection connection = getConnection();
			if ( employee != null ) {
				return getSalaries(connection, employee.getId());
			}
			else {
				Integer personId = getPersonID();
				Integer enterpriseId = getEnterpriseID();
				List<Integer> contractIds = 
						getContractIDs(connection, enterpriseId, personId);
				List<Salary> salaries = new LinkedList<Salary>();
				for (Integer contractId : contractIds) {
					salaries.addAll(getSalaries(connection, contractId));
				}
				return salaries;
			}
		} catch (SQLException e) {
			throw new IllegalArgumentException(e);
		}
	}

	
	public String getSalaryReceiptHTML(Salary salary, float zoomRatio)
			throws IllegalArgumentException {

		try {

			initFacesContext();

			IManagerBean beanManager = BeanManager
					.getManagerBean(com.esferalia.aon.payroll.Salary.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_ID),
					salary.getId());
			List<ITransferObject> list = beanManager.getList(criteria);
			com.esferalia.aon.payroll.Salary aonSalary = (com.esferalia.aon.payroll.Salary) list
					.get(0);

			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);
			reportManager.setCollectionProvider(new SingleCollectionProvider(
					aonSalary));

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			
			
			Map<Object, Object> parameters = 
					new HashMap<Object, Object>();
			parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoomRatio);
			reportManager.execute(out, IPayrollConstants.SALARY_REPORT, parameters);

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


	public String getCostReceiptHTML(Cost cost, float zoomRatio)
			throws IllegalArgumentException {
		try {
			initFacesContext();

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
			
			final List<ITransferObject> list = beanManager.getList(criteria);
			
			ReportManager reportManager = new ReportManager();
			reportManager.setOutputFormat(OutputFormat.HTML);
			
			reportManager.setCollectionProvider(new ICollectionProvider() {
				@Override
				public Collection<ITransferObject> getCollection() {
					return list;
				}

				@Override
				public Collection<ITransferObject> getCollection(boolean forceRefresh)
						throws ManagerBeanException {
					return list;
				}
			});
			
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
			
			Map<Object, Object> parameters = new HashMap<Object, Object>();
			parameters.put(JRHtmlExporterParameter.ZOOM_RATIO, zoomRatio);
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


	private static List<Salary> getSalaries(Connection connection, Integer contractId)
			throws SQLException {

		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {

			String sql = "SELECT * " + " FROM " + SALARY + " WHERE " + SALARY
					+ "." + SalaryColumns.CONTRACT + " = ?";

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
					+ ContractColumns.PERSON + " = " + PERSON + "."
					+ PersonColumns.REGISTRY + " ORDER BY " + WORKPLACE + "."
					+ WorkplaceColumns.ID;

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, registryID);
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
				rs.close();
			}
		}
	}
	
	
	private static List<Integer> getContractIDs(Connection connection, Integer enterpriseID, Integer personId)
		throws SQLException {
		ResultSet rs = null;
		PreparedStatement stmt = null;

		try {
			String sql = "SELECT * " + 
					" FROM " + CONTRACT +
					", " + WORKPLACE +
					" WHERE " + CONTRACT + "." +  ContractColumns.WORKPLACE + " = " + WORKPLACE + "." + WorkplaceColumns.ID + 
					" AND " + CONTRACT + "." + ContractColumns.PERSON + " = ? "+
					" AND " + WORKPLACE + "." + WorkplaceColumns.ENTERPRISE + " = ? ";

			stmt = connection.prepareStatement(sql);
			stmt.setInt(1, personId);
			stmt.setInt(2, enterpriseID);
			rs = stmt.executeQuery();
			
			List<Integer> ids = new LinkedList<Integer>();
			
			while ( rs.next() ) {
				ids.add(rs.getInt(tableCol(CONTRACT, ContractColumns.ID)));
			}
			return ids;

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

}

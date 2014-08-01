package com.code.aon.aio.controller;

import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.jooq.AggregateFunction;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.impl.DSL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.fiscal.config.Model;
import com.code.aon.fiscal.config.ModelConfig;
import com.code.aon.fiscal.config.ModelManager;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.modules.account.entry.EmptyAccountEntryCheck;
import com.code.aon.ui.accounting.check.modules.account.entry.UnbalancedAccountEntryCheck;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.fiscal.controller.IFiscalModelController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;

public class DashboardController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory
			.getLogger(DashboardController.class);

	private static final String WHERE = " WHERE ";

	private List<DashboardMessage> messages;
	private DashboardEntry[] periodEntriesCount;

	private List<Map<String, Object>> salaryEntriesCount;
	private LinkedList<DashboardStaff> staff;
	private DashboardEntry[] salaryMonthsEntriesCount;
	private DashboardEntry[] contractMonthsEntriesCount;

	private List<ModelConfig> fiscalConfig;
	
	private com.code.aon.accounting.Period accountingPeriod;
	private Integer fiscalYear;
	private Integer payrollYear;
	private Integer salaryYear;
	private Integer contractYear; 
	
	/*
	 * Inicializo metodos en el constructor ya que estan implementados en el onChange.
	 * Aparecera la primera vez que se carga la pagina.
	 */

	public DashboardController() {
		try {
			this.getSalaryYears();
			this.onSalaryYearChanged(null);
			this.getContractYears();
			this.onContractYearChanged(null);
		} catch (ManagerBeanException ex) {
			// No funciona el grafico
		}

	}

	public com.code.aon.accounting.Period getAccountingPeriod() {
		if (accountingPeriod == null) {
			try {
				accountingPeriod = AccountingPeriodUtil.getDefaultPeriod();
			} catch (ManagerBeanException e) {
				LOGGER.debug(e.getMessage(), e);
			}
			if (accountingPeriod == null) {
				try {
					AccountingUtil au = new AccountingUtil();
					accountingPeriod = au.obtainPeriod(new Date());
				} catch (ManagerBeanException e1) {
					LOGGER.debug(e1.getMessage(), e1);
				}
			}
			if (accountingPeriod == null) {
				try {
					IManagerBean periodBean = BeanManager
							.getManagerBean(com.code.aon.accounting.Period.class);
					List<ITransferObject> list = periodBean.getList(null);
					if (list != null && list.size() > 0) {
						accountingPeriod = (com.code.aon.accounting.Period) list
								.get(0);
					}
				} catch (ManagerBeanException e) {
					LOGGER.debug(e.getMessage(), e);
				}
			}
		}
		return accountingPeriod;
	}

	public void setAccountingPeriod(
			com.code.aon.accounting.Period accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}

	public void onAccountingPeriodChanged(ActionEvent event) {
		periodEntriesCount = null;
		messages = null;
	}

	public void onFiscalPeriodChanged(ActionEvent event) {
		this.fiscalConfig = null;
		this.fiscalConfig = getFiscalInfo();
	}

	public Integer getFiscalYear() {
		if (fiscalYear == null) {
			FiscalParametersController fiscalParams = (FiscalParametersController) AonUtil
					.getRegisteredBean(FiscalParametersController.FISCAL_PARAMS_BEAN_NAME);
			String defYear = fiscalParams.getDefaultYear();
			try {
				fiscalYear = Integer.parseInt(defYear);
			} catch (NumberFormatException e) {
				LOGGER.debug(e.getMessage(), e);
			}
			if (fiscalYear == null) {
				fiscalYear = CommonUtil.getYear(new Date());
			}
		}
		return fiscalYear;
	}

	public void setFiscalYear(Integer fiscalYear) {
		this.fiscalYear = fiscalYear;
	}

	public void onRefresh(ActionEvent event) {
		this.messages = null;
		this.periodEntriesCount = null;
		this.fiscalConfig = null;

	}

	public DashboardEntry[] getPeriodEntriesCount() throws ManagerBeanException {
		if (periodEntriesCount == null && getAccountingPeriod() != null) {
			Connection c = null;
			periodEntriesCount = new DashboardEntry[12];
			try {
				Locale locale = AonUtil.getCurrentLocale();
				for (int i = 0; i < 12; i++) {
					DashboardEntry de = new DashboardEntry();
					de.setName(Month.getMonthByValue(i).getName(locale));
					de.setValue(0);
					periodEntriesCount[i] = de;
				}
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				int domainId = DomainManager.getCurrentDomain();
				DSLContext ctx = DSL.using(c, AccountingUtil.getDefaultSettings());
				AggregateFunction<Integer> countFunc = DSL.count(); 
				Field<Integer> monthFunc = DSL.month(ACCOUNT_ENTRY.ENTRY_DATE);
				for (Record2<Integer,Integer> record:
						ctx.select(countFunc, monthFunc)
							.from(ACCOUNT_ENTRY)
							.where(ACCOUNT_ENTRY.DOMAIN.equal(domainId))
							.and(ACCOUNT_ENTRY.ACCOUNT_PERIOD.equal(getAccountingPeriod().getId()))
							.groupBy(monthFunc)
							.fetch() ) {
					int count = record.getValue(countFunc);
					int month = record.getValue(monthFunc) - 1;
					periodEntriesCount[month].setValue(count);
				}
			} catch (AonConnectionException e) {
				e.printStackTrace();
				// Nothing. Se mostrara array vacio.
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return periodEntriesCount;
	}
	public class DashboardFiscalInfo {
		
	}
	
	public List<ModelConfig> getFiscalInfo() {
		if (fiscalConfig == null) {
			fiscalConfig = new LinkedList<ModelConfig>();
			Connection c = null;
			try {
				ModelManager mm = new ModelManager();
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				int domainId = DomainManager.getCurrentDomain();
				fiscalConfig = mm.getModelsPanel(c, domainId, getFiscalYear());
			} catch (Throwable e) {
				// Nothing. Se mostrara array vacio. Pero se podrá usar la aplicación.
				LOGGER.error(e.getMessage());
			} finally {
				DatabaseUtil.closeQuietly(c);
			}
		}
		return fiscalConfig;
	}

	public String navigateModel() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		String ad = params.get("adm");
		Administration administration = null;
		if (StringUtils.isNotBlank(ad)) {
			administration = Administration.valueOf(ad);
		}
		Model mod = Model.valueOf(params.get("model"));
		Period period = Period.valueOf(params.get("period"));
		boolean missing = Boolean.valueOf(params.get("status"));
		String beanName = null;
		if ( mod == Model.M111) {
			beanName = "mod111";
		} else if ( mod == Model.M115) {
			beanName = "mod115";
		} else if ( mod == Model.M123) {
			beanName = "mod123";
		} else if ( mod == Model.M130) {
			beanName = "mod130";
		} else if ( mod == Model.M131) {
			beanName = "mod131";
		} else if ( mod == Model.M303_RG) {
			beanName = "vatTax";
		} else if ( mod == Model.M303_RS) {
			beanName = "mod303";
		} else if ( mod == Model.M347) {
			beanName = "mod347";
		} else if ( mod == Model.M349) {
			beanName = "mod349";
		} else if ( mod == Model.M390_HF) {
			beanName = "vatTax";
		} else if ( mod == Model.M311) {
			beanName = "mod311";
		} else if ( mod == Model.M310) {
			beanName = "mod310";
		} else if ( mod == Model.M390) {
			return "gwt_mod390";
		} else if ( mod == Model.M180) {
			return "gwt_mod180";
		} else if ( mod == Model.M190) {
			return "gwt_mod190";
		} else if ( mod == Model.M200) {
			return "gwt_mod200";
		}
		IFiscalModelController controller = (IFiscalModelController) FormUtil.getController(beanName);
		try {
			String navKey = "";
			if (missing) {
				navKey = controller.newModel(administration, getFiscalYear(), period);	
			} else {
				navKey = controller.editModel(administration, getFiscalYear(), period);
			}
			this.fiscalConfig = null;
			return navKey;
		} catch (ManagerBeanException e) {
			String message = "Imposible realizar la navegación al modelo solicitado.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message,e);
		}
	}

	public List<DashboardMessage> getMessages() {
		Connection c = null;
		if (messages == null && getAccountingPeriod() != null) {
			messages = new LinkedList<DashboardMessage>();
			try {
				if (getAccountingPeriod() != null) {
					String domainName = AonUtil.getDomainName();
					int domainId = DomainManager.getCurrentDomain();
					CheckParams params = new CheckParams(domainName,domainId);
					params.setPeriod(getAccountingPeriod());

					UnbalancedAccountEntryCheck uc = new UnbalancedAccountEntryCheck();
					uc.onExecute(params);
					List<ICheckEntry> list = uc.getCheckList();
					if (list != null && list.size() > 0) {
						DashboardMessage msg = new DashboardMessage();
						msg.setCategory("CONTABILIDAD");
						msg.setLevel("red");
						if (list.size() > 1) {
							msg.setMessage("Existen "
									+ list.size()
									+ " apuntes descuadrados. Verifique la integridad de la contabilidad.");
						} else {
							msg.setMessage("Existe 1 apunte descuadrado. Verifique la integridad de la contabilidad.");
						}
						messages.add(msg);
					}

					EmptyAccountEntryCheck ec = new EmptyAccountEntryCheck();
					ec.onExecute(params);
					list = ec.getCheckList();
					if (list != null && list.size() > 0) {
						DashboardMessage msg = new DashboardMessage();
						msg.setCategory("CONTABILIDAD");
						msg.setLevel("orange");
						if (list.size() > 1) {
							msg.setMessage("Existen "
									+ list.size()
									+ " apuntes sin líneas. Verifique la integridad de la contabilidad.");
						} else {
							msg.setMessage("Existe 1 apunte sin líneas. Verifique la integridad de la contabilidad.");
						}
						messages.add(msg);
					}
				}
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				messages.addAll(getInvestmentInvoices(c));
				messages.addAll(getIntracommunitaryInvoices(c));
				messages.addAll(getRetentionInvoices(c));
			} catch (AonCheckException e) {
				e.printStackTrace();
			} catch (AonConnectionException e) {
				e.printStackTrace();
			} finally {
				DatabaseUtil.closeQuietly(c);
			}

		}
		return messages;
	}

	private List<DashboardMessage> getRetentionInvoices(Connection c) {
		String select = "SELECT count(it.id),it.withholding_type "
				+ " FROM invoice_tax it"
				+ " INNER JOIN invoice_detail id ON it.invoice_detail = id.id"
				+ " INNER JOIN invoice i ON id.invoice = i.id" + WHERE
				+ DomainManager.getSQLWhereClause("it.domain")
				+ " AND it.tax_type = 2" + " AND i.issue_date BETWEEN ? AND ?"
				+ " GROUP BY it.withholding_type";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DashboardMessage> msgs = new LinkedList<DashboardMessage>();
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(getAccountingPeriod()
					.getInitiationDate().getTime()));
			ps.setDate(2, new java.sql.Date(getAccountingPeriod().getDeadline()
					.getTime()));
			rs = ps.executeQuery();
			while (rs.next()) {
				int i = rs.getInt(1);
				WithholdingType wt = WithholdingType.values()[rs.getInt(2)];
				DashboardMessage msg = new DashboardMessage();
				msg.setCategory("GESTIÓN");
				msg.setLevel("green");
				String type = "";
				if (wt == WithholdingType.PROFESSIONAL) {
					type = "profesionales";
				} else if (wt == WithholdingType.RENTING) {
					type = "alquileres";
				} else if (wt == WithholdingType.MOVABLE_CAPITAL) {
					type = "capital mobiliario";
				} else if (wt == WithholdingType.FARMER) {
					type = "agricultura";
				} else if (wt == WithholdingType.TRANSPORT_OPERATOR) {
					type = "transportes";
				}
				msg.setMessage("Existen facturas con retención de " + type
						+ " ( " + i + " ).");
				msgs.add(msg);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return msgs;
	}

	private Collection<DashboardMessage> getIntracommunitaryInvoices(
			Connection c) {
		String select = "SELECT count(i.id)" + " FROM invoice i" + WHERE
				+ DomainManager.getSQLWhereClause("i.domain")
				+ " AND i.transaction = 1"
				+ " AND i.issue_date BETWEEN ? AND ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DashboardMessage> msgs = new LinkedList<DashboardMessage>();
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(getAccountingPeriod()
					.getInitiationDate().getTime()));
			ps.setDate(2, new java.sql.Date(getAccountingPeriod().getDeadline()
					.getTime()));
			rs = ps.executeQuery();
			if (rs.next()) {
				int i = rs.getInt(1);
				if (i > 0) {
					DashboardMessage msg = new DashboardMessage();
					msg.setCategory("GESTIÓN");
					msg.setLevel("green");
					msg.setMessage("Existen facturas intracomunitarias ( " + i
							+ " ).");
					msgs.add(msg);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return msgs;
	}

	private Collection<DashboardMessage> getInvestmentInvoices(Connection c) {
		String select = "SELECT count(i.id)" + " FROM invoice i" + WHERE
				+ DomainManager.getSQLWhereClause("i.domain")
				+ " AND i.issue_date BETWEEN ? AND ?" + " AND i.investment = 1"
				+ " AND i.id NOT IN (SELECT invoice FROM amortization_invoice)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DashboardMessage> msgs = new LinkedList<DashboardMessage>();
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(getAccountingPeriod()
					.getInitiationDate().getTime()));
			ps.setDate(2, new java.sql.Date(getAccountingPeriod().getDeadline()
					.getTime()));
			rs = ps.executeQuery();
			if (rs.next()) {
				int i = rs.getInt(1);
				if (i > 0) {
					DashboardMessage msg = new DashboardMessage();
					msg.setCategory("GESTIÓN");
					msg.setLevel("red");
					msg.setMessage("Existen facturas de inversión sin ficha de amortización ( "
							+ i + " ).");
					msgs.add(msg);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
		}
		return msgs;
	}

	
	public List<Map<String, Object>> onSalaryYearChanged(ActionEvent event)
			throws ManagerBeanException {

		salaryEntriesCount = null;

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;

		salaryEntriesCount = new ArrayList<Map<String, Object>>(12);
		try {
			Locale locale = AonUtil.getCurrentLocale();

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			salaryMonthsEntriesCount = new DashboardEntry[12];
			for (int i = 0; i < 12; i++) {
				DashboardEntry dashboardEntry = new DashboardEntry();
				dashboardEntry
						.setName(Month.getMonthByValue(i).getName(locale));
				salaryMonthsEntriesCount[i] = dashboardEntry;
			}

			for (int i = 0; i < 12; i++) {
				Map<String, Object> dashboardEntry = new Hashtable<String, Object>();// new
																						// DashboardEntry();
				dashboardEntry.put("Mes", salaryMonthsEntriesCount[i].getName().toString());																		// dashboardEntry.put("name",
				dashboardEntry.put("Nominas", 0);
				dashboardEntry.put("Extras", 0);
				dashboardEntry.put("Finiquitos", 0);
				dashboardEntry.put("Atrasos", 0);
				dashboardEntry.put("Vacaciones", 0);

				// Month.getMonthByValue(i).getName(locale));
				salaryEntriesCount.add(i, dashboardEntry);
			}

			String select = "SELECT MONTH(end_date) as Mes, COUNT(0) as Todas, "
					+ "COUNT(IF(type=0,type,NULL)) AS Nominas,"
					+ " COUNT(IF(type=1,type,NULL)) AS Extras, "
					+ "COUNT(IF(type=2,type,NULL)) AS Finiquito,"
					+ " COUNT(IF(type=3,type,NULL)) AS Atrasos, "
					+ "COUNT(IF(type=4,type,NULL)) AS Vacaciones FROM salary "
					+ "WHERE "
					+ DomainManager.getSQLWhereClause("domain")
					+ " AND YEAR(end_date)=?" + " GROUP BY Mes";
			// @formatter=on

			stmt = connection.prepareStatement(select);
			stmt.setInt(1, salaryYear);

			rs = stmt.executeQuery();

			while (rs.next()) {
				int month = rs.getInt(1) - 1;
			    //int all = rs.getInt(2);
				int payroll = rs.getInt(3);
				int extras = rs.getInt(4);
				int finiquito = rs.getInt(5);
				int atrasos = rs.getInt(6);
				int vacaciones = rs.getInt(7);
				/*
				 * int type = rs.getInt(1); int count = rs.getInt(1); int month
				 * = rs.getInt(2) - 1;
				 */

				// payrollEntriesCount.get(month).put("Todas", all);
				salaryEntriesCount.get(month).put("Mes",salaryMonthsEntriesCount[month].getName());
				salaryEntriesCount.get(month).put("Nominas", payroll);
				salaryEntriesCount.get(month).put("Extras", extras);
				salaryEntriesCount.get(month).put("Finiquitos", finiquito);
				salaryEntriesCount.get(month).put("Atrasos", atrasos);
				salaryEntriesCount.get(month).put("Vacaciones", vacaciones);
			}

			/*
			 * stmt.setInt(1, getAccountingPeriod().getId()); rs =
			 * stmt.executeQuery(); while (rs.next()) { int count =
			 * rs.getInt(1); int month = rs.getInt(2) - 1;
			 * periodEntriesCount[month].setValue(count); }
			 */

		} catch (SQLException e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} catch (Exception e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
			DatabaseUtil.closeQuietly(connection);
		}
		return salaryEntriesCount;

	}

	public List<Map<String, Object>> getPeriodPayrollCount()
			throws ManagerBeanException {
		return salaryEntriesCount;
	}

	public Integer getSalaryYear() {
		return salaryYear;
	}

	public void setSalaryYear(Integer salaryYear) {
		this.salaryYear = salaryYear;
	}
	public void setContractYear(Integer contractYear) {
		this.contractYear = contractYear;
	}
	public Integer getContractYear() {
		return this.contractYear;
	}

	public Integer getPayrollYear() {
		return payrollYear;
	}

	public void setPayrollYear(Integer payrollYear) {
		this.payrollYear = payrollYear;
	}
	
	public LinkedList<DashboardStaff> getPeriodContractCount()
			throws ManagerBeanException {		
		return staff;
	}

	public LinkedList<DashboardStaff> onContractYearChanged(ActionEvent event)
	throws ManagerBeanException {
		
		/*if (staff != null)
			getPeriodContractCount();*/
		
		staff = null;
		staff = new LinkedList<DashboardStaff>();
		int countAltas = 0;
		int countBajas = 0;		

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		contractMonthsEntriesCount = new DashboardEntry[12];
		try {
			Locale locale = AonUtil.getCurrentLocale();

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());

			for (int i = 0; i < 12; i++) {
				DashboardEntry dashboardEntry = new DashboardEntry();
				dashboardEntry
						.setName(Month.getMonthByValue(i).getName(locale));
				contractMonthsEntriesCount[i] = dashboardEntry;
				
				staff.add(new DashboardStaff(contractMonthsEntriesCount[i].getName().toString(),countAltas,countBajas));

			}

			String select = "(SELECT \"ALTAS\" as tipo, month("+ContractColumns.START_DATE+"),"
					+ " count(*) as numero" 
					+ " FROM " + SQLConstants.CONTRACT
					+ WHERE
					+ " (year("+ContractColumns.START_DATE+")=?) and "
					+ DomainManager.getSQLWhereClause("domain")
					+ " Group By 1,2)"
					+ " UNION "
					+ "(SELECT \"BAJAS\" as tipo, month("+ContractColumns.END_DATE+"),"
							+ " count(*) as numero" 
					+ " FROM " + SQLConstants.CONTRACT
					+ WHERE
					+ " (year("+ContractColumns.END_DATE+")=?) and "
					+ DomainManager.getSQLWhereClause("domain")
					+ " GROUP BY 1,2)";

			stmt = connection.prepareStatement(select);
			stmt.setInt(1, contractYear);
			stmt.setInt(2, contractYear);

			rs = stmt.executeQuery();

			while (rs.next()) {
				String type = rs.getString(1);
				int month = rs.getInt(2) - 1;
				
				if(type.compareTo("ALTAS")==0) {
					countAltas = rs.getInt(3);
					staff.get(month).setAltas(countAltas);
				}
				else {
					countBajas = rs.getInt(3);
					staff.get(month).setBajas(countBajas);
				}				
			}			

			/*
			 * stmt.setInt(1, getAccountingPeriod().getId()); rs =
			 * stmt.executeQuery(); while (rs.next()) { int count =
			 * rs.getInt(1); int month = rs.getInt(2) - 1;
			 * periodEntriesCount[month].setValue(count); }
			 */

		} catch (SQLException e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} catch (AonConnectionException e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
			DatabaseUtil.closeQuietly(connection);
		}
		return staff;
	}

	/*
	 * Devuelvo los anyos donde existan nominas
	 */
	public List<SelectItem> getSalaryYears() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		int lastYear=0;
		try {

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT extract(year from "
					+ SalaryColumns.END_DATE + ") as Salary from "
					+ SQLConstants.SALARY + " Where"
					+ DomainManager.getSQLWhereClause("domain")
					+ " group by Salary";
			stmt = connection.prepareStatement(select);

			rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(new SelectItem(rs.getInt(1), Integer.toString(rs
						.getInt(1))));
				lastYear = rs.getInt(1);
			}
			/*
			 *Si la empresa no tiene el año actual en registro, selecciono el ultimo.
			 */
			if(getSalaryYear() == null) {
				this.setSalaryYear(lastYear);
			}
						

		} catch (SQLException e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} catch (AonConnectionException e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
			DatabaseUtil.closeQuietly(connection);

		}
		return list;
	}
	
	/*
	 * Devuelvo losa anyos donde existan contratos
	 */
	public List<SelectItem> getContractYears() {
		List<SelectItem> list = new LinkedList<SelectItem>();
		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		int lastYear = 0;
		try {

			connection = DatabaseUtil.getConnection(AonUtil.getDomainName());
			String select = "SELECT extract(year from "
					+ ContractColumns.START_DATE + ") from "
					+ SQLConstants.CONTRACT + " Where"
					+ DomainManager.getSQLWhereClause("domain")
					+ " group by 1";
			stmt = connection.prepareStatement(select);

			rs = stmt.executeQuery();

			while (rs.next()) {
				list.add(new SelectItem(rs.getInt(1), Integer.toString(rs
						.getInt(1))));
				lastYear = rs.getInt(1);
			}
			if(getContractYear() == null) {
				this.setContractYear(lastYear);
			}
			

		} catch (SQLException e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} catch (AonConnectionException e) {
			e.printStackTrace();
			// Nothing. Se mostrara array vacio.
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(stmt);
			DatabaseUtil.closeQuietly(connection);

		}
		return list;
	}
	
	
}




package com.code.aon.aio.controller;

import static com.code.aon.ui.config.controller.ConfigConstants.DOMAIN_SWITCHER;
import static com.esferalia.aon.jooq.tables.AccountEntry.ACCOUNT_ENTRY;
import static com.esferalia.aon.jooq.tables.Category.CATEGORY;
import static com.esferalia.aon.jooq.tables.Domain.DOMAIN;
import static com.esferalia.aon.jooq.tables.Rattach.RATTACH;
import static com.esferalia.aon.jooq.tables.Salary.SALARY;
import static com.esferalia.aon.jooq.tables.UserScope.USER_SCOPE;

import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Vector;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jooq.AggregateFunction;
import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Record6;
import org.jooq.Record9;
import org.jooq.Result;
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
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.fiscal.config.Model;
import com.code.aon.fiscal.config.ModelConfig;
import com.code.aon.fiscal.config.ModelManager;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.google.apis.jooq.DBConsults;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.modules.account.entry.EmptyAccountEntryCheck;
import com.code.aon.ui.accounting.check.modules.account.entry.UnbalancedAccountEntryCheck;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.fiscal.controller.IFiscalModelController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.DomainGserviceaccount;
import com.esferalia.aon.occam.api.model.registry.Category;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.SalaryColumns;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.google.api.services.drive.Drive;

import net.aonsolutions.core.dbutils.DatabaseUtil;
import net.aonsolutions.core.pool.AonConnectionException;

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
	private DashboardFiscalPortal fiscalPortal;
	public Vector<DashboardRecentFiles> recentFiles;
	public Vector<DashboardDocs> types;

	private com.code.aon.accounting.Period accountingPeriod;
	private Integer fiscalYear;
	private Integer payrollYear;
	private Integer salaryYear;
	private Integer contractYear; 
	
	private boolean pieChart=true;
	private boolean columnChart=false;
	private boolean bubbleChart=false;
	private boolean totalsize=false;
	
	private static Double occupied;
	private static long free;
	
	
	private Integer graficSelection;
	
	
	
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
		fiscalPortal = null;
	}
	public DashboardFiscalPortal getFiscalPortal() {
		if (fiscalPortal == null) {
			fiscalPortal = new DashboardFiscalPortal();
			fiscalPortal.setDomainId(DomainManager.getCurrentDomain());
			fiscalPortal.setDomainName(AonUtil.getDomainName());
			fiscalPortal.setPygEntriesPeriod(getAccountingPeriod());
			fiscalPortal.setExpensesPeriod(getAccountingPeriod());
			DesktopController controller = (DesktopController) AonUtil.getRegisteredBean(DesktopController.CONTROLLER_NAME);
			if ( controller.getState().isFiscalInfoVisibleForPortal() && ! controller.getState().isAccountingInfoVisibleForPortal() ) {
				fiscalPortal.setSelectedTab(DashboardFiscalPortal.FISCAL_PORTLET_TAB);
			}
		}
		return fiscalPortal;
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
		this.fiscalPortal = null;
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
				int userId = AonUtil.getAuthPrincipal().getUserId();
				fiscalConfig = mm.getModelsPanel(c, domainId, getFiscalYear(), userId);
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
			return "gwt_mod111";
		} else if ( mod == Model.M115) {
			return "gwt_mod115";
		} else if ( mod == Model.M123) {
			return "gwt_mod123";
		} else if ( mod == Model.M130) {
			return "gwt_mod130";
		} else if ( mod == Model.M131) {
			return "gwt_mod131";
		} else if ( mod == Model.MIVA) {
			return "gwt_mod303";
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
		} else if ( mod == Model.M390) {
			return "gwt_mod390";
		} else if ( mod == Model.M180) {
			return "gwt_mod180";
		} else if ( mod == Model.M184) {
			return "gwt_mod184";
		} else if ( mod == Model.M190) {
			return "gwt_mod190";
		} else if ( mod == Model.M193) {
			return "gwt_mod193";
		} else if ( mod == Model.M200) {
			return "gwt_mod200";
		} else if ( mod == Model.M202) {
			return "gwt_mod202";
		} else {
			String message = "Imposible realizar la navegación al modelo solicitado.";
			AonUtil.addErrorMessage(message);
			throw new AbortProcessingException(message);
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
		String select = "SELECT count(DISTINCT i.id),it.withholding_type "
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
		Integer currentYear = AonDateUtils.getYear(new Date());
		Boolean isCurrentYear = false;
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
				if(currentYear.equals(rs.getInt(1)))
					isCurrentYear = true;
			}
			if(!isCurrentYear) 
				list.add(new SelectItem(currentYear, currentYear.toString()));

			if(getSalaryYear() == null) {
				this.setSalaryYear(currentYear);
				
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
		Integer currentYear = AonDateUtils.getYear(new Date());
		Boolean isCurrentYear = false;
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
				if(currentYear.equals(rs.getInt(1)))
					isCurrentYear = true;
			}
			
			if(!isCurrentYear) 
				list.add(new SelectItem(currentYear, currentYear.toString()));

			if(getContractYear() == null) {
				this.setContractYear(currentYear);		
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
	
	/***************************** DOCUMENTAL  
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 * @throws KeyStoreException **********************************/ 
	
	// esta en DBConsults
	public  Vector<DashboardDocs> getTypes() throws AonConnectionException,
	SQLException {
			Integer aux = RegistryAttachmentType.values().length;
			sizes();
			Vector<DashboardDocs> types= new Vector<DashboardDocs>(); 
			
			for(Byte i = 0 ; i<aux ; i++ ){
				DashboardDocs a = getTypesBD(i);
				if (!a.gettype().equals("Logo") && !a.gettype().equals("Firma")){
					if (a.getsize()>0){
						types.add(a);	
						free= free - a.getsize();
					}
				}
			}	
			Byte b = 100;
			DashboardDocs dd = getTypesBD(b);
			dd.settype("Otros");
			if ((!dd.gettype().equals("Logo") && !dd.gettype().equals("Firma")) ){
				if (dd.getsize()>0){
					types.add(dd);
				}
			}
			
			DashboardDocs d = new DashboardDocs();
			d.settype("Disponible");
			dd.settype("Otros");
			d.setnum(0);
			d.setsize(free);
			
			if ((!d.gettype().equals("Logo") && !d.gettype().equals("Firma"))){
				if (d.getsize()>0){

					types.add(d);
				}
			}
					
			return types;
	}
	
	public static Result<Record3<Integer, String, String>> getCategoryAux(
			String domainName, Integer domainId) {
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			Result<Record3<Integer, String, String>> category = ctx.getDslContext()
					.select(CATEGORY.ID, CATEGORY.NAME, CATEGORY.DESCRIPTION)
					.from(CATEGORY)
					.join(DOMAIN)
					.on(CATEGORY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.ID
							.eq(domainId)
							.or(DOMAIN.ID.in(ctx.getDslContext().select(DOMAIN.PARENT)
									.from(DOMAIN).where(DOMAIN.ID.eq(domainId)))))
					.fetch();

			return category;
		} finally {
			if (ctx != null)
				ctx.close();
		}

	}
	
	public   Vector<DashboardDocs> getTypesCat() {
		if (types == null){
			types= new Vector<DashboardDocs>();
			DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);
			Integer domainId = ds.getDomainId();
			String domain = AonUtil.getDomainName();
			Result<Record3<Integer, String,String>> category =  getCategoryAux(domain, domainId);
			sizes();
			getTypesCatBD2();
			//Vector<DashboardDocs> vector= new Vector<DashboardDocs>(); 
			for (Record3<Integer, String, String> record3 : category) {
				if (categories.containsKey(record3.value1())){
					DashboardDocs a = categories.get(record3.value1());
					
					if (a.getsize()>=0){
						types.add(a);	
						free= free - a.getsize();
					}
				}
				
			}
			
			DashboardDocs a = categories.get(-1);
			if (a != null && a.getsize()>=0){
				types.add(a);	
				free= free - a.getsize();
			}

		}	
		return types;
	}
	
	public   DashboardDocs getTypesCatBD(Integer category){
		Domain domain = getDomain();
		com.esferalia.aon.occam.api.model.security.User user = getUser();
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()); 

			DashboardDocs a=null;
			Result<Record3<Integer, String, String>> data ;
			if (category == 100){
				data =  ctx.getDslContext()
						.select(RATTACH.CATEGORY,RATTACH.DPARENT_ID,RATTACH.DRIVE_ID)
						.from(RATTACH)
						.where(RATTACH.CATEGORY.isNull().and(RATTACH.DOMAIN.eq(domain.getId()))).fetch();
				a = new DashboardDocs("Otros",0,0,0);

			}
			else{
				data =  ctx.getDslContext()
						.select(RATTACH.CATEGORY,RATTACH.DPARENT_ID,RATTACH.DRIVE_ID)
						.from(RATTACH)
						.where(RATTACH.CATEGORY.eq(category).and(RATTACH.DOMAIN.eq(domain.getId()))).fetch();
				Category c = DBConsults.getCategory(getDomain(), getUser(), category);
				a = new DashboardDocs(c.getName() != null ? c.getName() : "Otros",0,0,0);
			}
			
			long aux = 0;
			for (Record3<Integer, String, String> record : data) {
				LOGGER.debug("{}",a.getnum());
				LOGGER.debug(record.value2());
				LOGGER.debug(record.value3());
				a.setnum(a.getnum()+1);
				String s = record.value2();
				if (s != null) a.setsize(a.getsize()+(long) Integer.parseInt(record.value2()));
				if (record.value3()!=null) aux = aux+1;
			}
			LOGGER.debug(a.getsize()+" "+a.getnum());
			a.setmediaDrive(aux, a.getnum());
			a.setColor(category);
		
			return a;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	HashMap<Integer, DashboardDocs> categories;
	public void getTypesCatBD2() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();

		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, login);
			Result<Record3<Integer, Integer, String>> data ;
			
			HashMap<Integer, DashboardDocs> map = new HashMap<Integer, DashboardDocs>();
	
				data =  ctx.getDslContext()
						.select(RATTACH.CATEGORY,RATTACH.DATA.length(),RATTACH.DRIVE_ID)
						.from(RATTACH)
						.where(getAttachmentCondition()).fetch();
				
				for (Record3<Integer, Integer, String> record : data) {
					Integer categoryId = (record.value1() != null) ? record.value1() : -1;
					if(map.containsKey(categoryId)){
						map.get(categoryId).setnum(map.get(categoryId).getnum()+1);
						Integer s = record.value2();
						if (s != null) map.get(categoryId).setsize(map.get(categoryId).getsize()+(long) record.value2().longValue());
						if (record.value3()!=null) map.get(categoryId).setmediaDrive(map.get(categoryId).getmediaDrive()+1,map.get(categoryId).getnum());
					}	
					else{
						Category c = DBConsults.getCategory(getDomain(), getUser(), categoryId);
						DashboardDocs a;
						String category = c != null && c.getName() != null ? c.getName() : "Otros";
						long size = (record.value2() != null) ? record.value2().longValue() : 0;
						if (category != null)
							a= new DashboardDocs(category, 1, size, 0);
						else a= new DashboardDocs("Otros", 1, size, 0);
						if (record.value3()!=null) a.setmediaDrive(1,a.getnum());
						map.put(categoryId,a);
					}
				}
				
		
			categories =  map;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	
	public  DashboardDocs getTypesBD(byte type){
		Locale locale = AonUtil.getCurrentLocale();
		Domain domain = getDomain();
		com.esferalia.aon.occam.api.model.security.User user = getUser();
		
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()); 
			
			DashboardDocs a=null;
			Result<Record3<Byte, String,String>> data ;
			if (type == 100){
				data =  ctx.getDslContext()
						.selectDistinct(RATTACH.TYPE,RATTACH.DPARENT_ID,RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.ID.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.isNull().and(DOMAIN.NAME.eq(domain.getName()).or(RATTACH.DOMAIN.eq(domain.getId())))).fetch();
				a = new DashboardDocs("Otros",0,0);

			}
			else{
				data =  ctx.getDslContext()
						.selectDistinct(RATTACH.TYPE,RATTACH.DPARENT_ID,RATTACH.DRIVE_ID)
						.from(RATTACH).join(DOMAIN).on(DOMAIN.ID.eq(RATTACH.DOMAIN))
						.where(RATTACH.TYPE.eq(type).and(DOMAIN.NAME.eq(domain.getName()).or(RATTACH.DOMAIN.eq(domain.getId())))).fetch();
				String typeName = RegistryAttachmentType.values()[type].getName(locale);
				a = new DashboardDocs(typeName,0,0);

			}
			
			long aux = 0;
			
			for (Record3<Byte, String,String> record : data) {
				a.setnum(a.getnum()+1);
				String s = record.value2();
				if (s != null) a.setsize(a.getsize()+(long) Integer.parseInt(record.value2()));
				if (record.value3()!=null) aux = aux+1;
			}
			a.setmediaDrive(aux, a.getnum());
			a.setColor(type);
		
			return a;
		} finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	private List<Integer> getCurrentUserScopeIds() {
		User user = UserUtils.getInstance().getLoggedUser();
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		List<Integer> scopes = null;
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domainName, domainId, user.getLogin());
			scopes = ctx.getDslContext().select(USER_SCOPE.SCOPE).from(USER_SCOPE)
					.where(USER_SCOPE.USER_ID.eq(user.getId()))
					.fetch(USER_SCOPE.SCOPE);

		} finally {
			if (ctx != null)
				ctx.close();
		}
		return scopes;
	}
	
	private Condition getAttachmentCondition() {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String login = UserUtils.getInstance().getLoggedUser().getLogin();
		DomainSwitcher ds = (DomainSwitcher) AonUtil.getRegisteredBean(DOMAIN_SWITCHER);

		com.esferalia.aon.occam.api.model.Domain domain = AON.getDomain(domainName, domainId, login);
		LinkedList<Integer> domains = new LinkedList<>();
		domains.add(domain.getId());
		if(domain.isEnableHeredity()){
			domains.add(domain.getParentId());
		}
		Condition condition = RATTACH.DOMAIN.in(domains);
		if ( !AonUtil.getRoleManager().isConfidentiality() ) {
			condition = condition.and(RATTACH.SECURITY_LEVEL.eq((byte)SecurityLevel.OFFICIAL.ordinal()));
		}
		if (!ds.isParentDomainUserInChildDomain()) {
			Condition scopeCondition = RATTACH.SCOPE.isNull();
			List<Integer> scopes = getCurrentUserScopeIds();
			if (scopes!= null && !scopes.isEmpty() ) {
				if ( scopes.size() == 1 ) {
					scopeCondition = scopeCondition.or(RATTACH.SCOPE.eq(scopes.get(0)));
				} else {
					scopeCondition = scopeCondition.or(RATTACH.SCOPE.in(scopes));
				}
			}
			condition = condition.and(scopeCondition);
		}
		condition = condition.and(RATTACH.TYPE.eq((byte)RegistryAttachmentType.CORPORATE_IDENTITY.ordinal()));
		
		return condition;
	}
	
	public String getInitialize(){
		recentFiles= null;
		types= null;
		list= null;
		listAno=null;
		return "";
	}
	
	public  Vector<DashboardRecentFiles> getRecentsFiles() throws AonConnectionException,
	SQLException, KeyStoreException, IOException, GeneralSecurityException {
		if (recentFiles == null) {
			Locale locale = AonUtil.getCurrentLocale();
			Domain domain = getDomain();
			com.esferalia.aon.occam.api.model.security.User user = getUser();
			
			DomainGserviceaccount g = DBConsults.getServiceAccount(domain, user);
			Drive drive = null;
			if (g.getClientId()!= null) drive = DriveUtils.serviceInitialize(g);
			AONContext ctx = null;
			try {
				ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()); 

				Result<Record9<Byte, Integer, String, java.sql.Date, String, Integer, String, Byte, Integer>> data ;
				data =  ctx.getDslContext()
						.selectDistinct(RATTACH.TYPE,RATTACH.CATEGORY,RATTACH.DESCRIPTION,RATTACH.ATTACH_DATE,RATTACH.DRIVE_ID, RATTACH.DATA.length(),RATTACH.DPARENT_ID,RATTACH.MIMETYPE,RATTACH.ID)
						.from(RATTACH)
						.where(getAttachmentCondition())					
						.orderBy(RATTACH.ATTACH_DATE.desc()).limit(10).fetch();
				
				recentFiles = new Vector<DashboardRecentFiles>();
				int j=0;
				for (Record9<Byte, Integer, String, java.sql.Date, String, Integer, String, Byte,Integer> record : data) {
					if (record.value6() != null
							|| (record.value6() == null && record.value5() != null)) {
						DashboardRecentFiles drc = new DashboardRecentFiles();
						drc.setId(record.value9());
						String typeName = "-";
						if (record.value1() != null)
							typeName = RegistryAttachmentType.values()[record
									.value1()].getName(locale);
						if (!typeName.equals("Logo")
								&& !typeName.equals("Firma")) {
							drc.setname(record.value3());
							String category = "Otros";
							if (record.value2() != null) {
								Category c = DBConsults.getCategory(getDomain(), getUser(), record.value2());
								category = c.getName() != null ? c.getName() : "Otros";
							} 
							drc.setcategory(category);

							String driveId = record.value5();
							drc.setDriveId(driveId);
							if (driveId != null) {
								Integer size = 0;
								if (NumberUtils.isNumber(record.value7())) {
									size = Integer.parseInt(record.value7());
								}
								drc.setsize(size.longValue());
							} else {
								drc.setsize(record.value6().longValue());
							}
							// Integer s = record.value2();
							// if (s != null) drc.setsize(s.longValue());
							if (record.value1() != null)
								drc.settype(typeName);
							else
								drc.settype("Otros");

							if (record.value4() != null) {
								drc.setDate(record.value4().toString());

							}
							if (record.value8() != null)
								drc.setIcon(record.value8().intValue());
							else
								drc.setIcon(-1);
							drc.setMimetype(record.value8());
							recentFiles.add(drc);
							j++;
						}
					}
					if (j >= 10)
						return recentFiles;
				}
			} finally {
				if (ctx != null)
					ctx.close();
			}
		}
		return recentFiles;
	}
	
	public Integer getFileSize(String domainName, Integer id){
		Domain domain = getDomain(domainName);
		com.esferalia.aon.occam.api.model.security.User user = getUser();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin()); 
			Result<Record1<Integer>> data ;
			data =  ctx.getDslContext()
				.selectDistinct(RATTACH.DATA.length())
				.from(RATTACH)
				.where(RATTACH.ID.eq(id)).fetch();
		
			Integer size = null;
			for (Record1<Integer> record : data) {
				size = record.value1(); 
			}
			return size;
		}finally {
			if (ctx != null)
				ctx.close();
		}
	}
	
	public void onGraficSelectionChanged(ActionEvent event){
		if (graficSelection.equals(1)){
			pieChart=true;
			columnChart=false;
			bubbleChart=false;
			totalsize=false;
		}
		if (graficSelection.equals(2)){
			pieChart=false;
			columnChart=true;
			bubbleChart=false;
			totalsize=false;
		}
		if (graficSelection.equals(3)){
			pieChart=false;
			columnChart=false;
			bubbleChart=true;
			totalsize=false;
		}

	}
	
	public List<SelectItem> getGraficSelections(){
		SelectItem a = new SelectItem(1,"Pie Chart");
		SelectItem b = new SelectItem(2,"Column Chart");
		SelectItem c = new SelectItem(3,"Bubble Chart");
		
		List<SelectItem> list = new Vector<SelectItem>();
		list.add(a);
		list.add(b);
		list.add(c);
		
		
		return list;
	}
	public  void sizes() {
		String domain = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		DomainGserviceaccount dg = DBConsults.getServiceAccount(domain, domainId);
		if(dg != null){
			if(dg.getSize()!= null) occupied = dg.getSize();
			else occupied = 0.0;
			if(dg.getLimit() != null) free = (long) (dg.getLimit() - occupied);
			else free= 10000;
		}
		else {
			occupied = 0.0;
			free= 10000;
		}
		if (free <0) free=0;
	}
	
	public Boolean getPieChart(){ return pieChart;}
	
	public void setPieChart(boolean pieChart){ this.pieChart = pieChart;}
	
	public Boolean getColumnChart(){ return columnChart;}
	
	public void setColumnChart(boolean columnChart){ this.columnChart = columnChart;}

	public Boolean getBubbleChart(){ return bubbleChart;}

	public void setBubbleChart(Boolean bubbleChart){ this.bubbleChart = bubbleChart;}
	
	public Boolean getTotalSize() throws SQLException{ 
		sizes();
		return totalsize;}

	public void setTotalSize(Boolean total){ this.totalsize = total;}

	public Double getOccupied(){ return occupied;}

	public void setOccupied(long total){ 
		this.occupied = (double) total;}
	
	public long getFree(){ return free;}

	public void setFree(long total){ this.free = total;}
	
	public Integer getGraficSelection(){return graficSelection;}
	
	public void setGraficSelection(Integer graficSelection){this.graficSelection = graficSelection;}
	
	
	/* ***************************************************************** */
	/* ******************  PAYROLL PORTAL  *******************************/
	
	
	private static Integer year=2014;
	private Integer option; 
	private static Map<Integer, HashMap<String, DashboardPayrollPortal>> salaries;
	private Boolean meses = true;
	private Boolean años= false;
	
	
	public Integer getOption() {
		return option;
	}

	public void setOption(Integer option) {
		this.option = option;
	}

	public Boolean getMeses() {
		return meses;
	}

	public void setMeses(Boolean meses) {
		this.meses = meses;
	}

	public Boolean getAños() {
		return años;
	}

	public void setAños(Boolean años) {
		this.años = años;
	}

	public Map<Integer, HashMap<String, DashboardPayrollPortal>> getSalaries() {
		return salaries;
	}

	public void setSalaries(Map<Integer, HashMap<String, DashboardPayrollPortal>> salaries) {
		this.salaries = salaries;
	}

	public Integer getYear() {
		return year;
	}

	public void setYear(Integer year) {
		this.year = year;
	}
	
	public void onYearChanged(ActionEvent event) {
		this.list = null;
		this.listAno = null;
	}
	
	public void onOptionChanged(ActionEvent event) {
		if (option.equals(1)){
			meses=true;
			años= false;
		}
		if (option.equals(2)){
			meses=false;
			años=true;
		}
		
	}
	
	public  List<SelectItem> getOptions(){
		List<SelectItem> list = new Vector<SelectItem>();
		SelectItem si = new SelectItem(1, "meses");
		SelectItem si2 = new SelectItem(2, "años");
		
		list.add(si);
		list.add(si2);
		return list;
	}
	
	public  List<SelectItem> getYears(){
		List<SelectItem> list = new Vector<SelectItem>();
		boolean found = false;
		for (Integer salary: salaries.keySet()) {
			SelectItem si = new SelectItem(salary, salary.toString());
			if (salary!=null && this.year != null && salary.intValue() ==  this.year.intValue() ) {
				found = true;		
			}
			list.add(si);
		}
		
		if (!found && list.size() > 0 ) {
			try {
				this.year = salaries.keySet().iterator().next();	
			} catch ( Throwable t) {
				
			}
		}
		return list;
	}
	
	List<DashboardPayrollPortal> list;
	List<DashboardPayrollPortal> listAno;
	
	public   List<DashboardPayrollPortal> getPayroll() throws SQLException{
		if (list== null){
			Locale locale = AonUtil.getCurrentLocale();
			Map<Integer, HashMap<String, DashboardPayrollPortal>> a =getPayrollBD();
			list = new Vector<DashboardPayrollPortal>();
			for (int i = 0; i<12 ; i++) {
				String m = Month.getMonthByValue(i).getName(locale); 

				if (a.get(year) != null && a.get(year).get(m)!=null)
					list.add(a.get(year).get(m));
				else list.add(new DashboardPayrollPortal(0,0,0,0,0,m,0));
			}	
		}
		return list;
	}

	
	public   List<DashboardPayrollPortal> getPayrollAno() throws SQLException{
		if (listAno == null){
		Locale locale = AonUtil.getCurrentLocale();
		Map<Integer, HashMap<String, DashboardPayrollPortal>> a =getPayrollBD();
		listAno = new Vector<DashboardPayrollPortal>();
		
		for (Integer key : a.keySet()) {
			DashboardPayrollPortal b = new DashboardPayrollPortal(0,0,0,0,0,null, key); 
			b.setAno(key);
			
			for (int i =0; i<12 ; i++){
				String m = Month.getMonthByValue(i).getName(locale); 
				if (a.get(key).get(m)!=null){
					b.setIrpf(b.getIrpf()+a.get(key).get(m).getIrpf());
					b.setNeto(b.getNeto()+a.get(key).get(m).getNeto());
					b.setSs(b.getSs()+a.get(key).get(m).getSs());
					b.setOtros(b.getOtros()+a.get(key).get(m).getOtros());;
				}
				
			}
			listAno.add(b);
		}
		}
		return listAno;
	}
	
	public   Map<Integer, HashMap<String, DashboardPayrollPortal>> getPayrollBD() throws SQLException {
		Locale locale = AonUtil.getCurrentLocale();
		Domain domain = getDomain();
		com.esferalia.aon.occam.api.model.security.User user = getUser();
		AONContext ctx = null;
		try {
			ctx = AONContext.getAONContext(domain.getName(), domain.getId(), user.getLogin());			
			//Result<Record6<java.sql.Date, Double, Double, Double, Double, Double>> data ;

			
 			Result<Record6<java.sql.Date, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal>> data ;

			data =  ctx.getDslContext().select(SALARY.CHARGE_DATE,DSL.sum(SALARY.TOTAL_LIQUID),DSL.sum(SALARY.TOTAL_DEDUCTION),DSL.sum(SALARY.TOTAL_IRPF),DSL.sum(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS),DSL.sum(SALARY.TOTAL_ENTERPRISE))
					.from(SALARY)
					.where(SALARY.DOMAIN.eq(domain.getId()))
					.groupBy(SALARY.CHARGE_DATE).fetch();
			
			/*data=dslContext.select(SALARY.CHARGE_DATE,SALARY.TOTAL_LIQUID,SALARY.TOTAL_DEDUCTION,SALARY.TOTAL_IRPF,SALARY.SOCIAL_SECURITY_CONTRIBUTIONS,SALARY.TOTAL_ENTERPRISE)
					.from(SALARY).join(DOMAIN).on(SALARY.DOMAIN.eq(DOMAIN.ID))
					.where(DOMAIN.NAME.eq(domain)).fetch();
				*/	
			Map<Integer, HashMap<String, DashboardPayrollPortal>>  map = new HashMap<Integer,HashMap<String, DashboardPayrollPortal>>();
			
			for (Record6<java.sql.Date, BigDecimal, BigDecimal, BigDecimal, BigDecimal, BigDecimal> record : data) {
				HashMap<String, DashboardPayrollPortal> dpps = new HashMap<String, DashboardPayrollPortal>();
				DashboardPayrollPortal dpp = new DashboardPayrollPortal();
				dpp.setDeduction(0.00);dpp.setIrpf(0.00);
				int auxMes = Integer.parseInt((record.value1().toString().substring(5, 7)))-1;
				LOGGER.debug((record.value1().toString().substring(5, 7)));
				int ano= Integer.parseInt((record.value1().toString().substring(0, 4)));
				
				String mes = Month.getMonthByValue(auxMes).getName(locale);
				if(!map.containsKey(ano)){ 
					dpp.setMes(mes);
					dpp.setAno(ano);
					dpp.setIrpf(twoDecimal(record.value4().doubleValue()));
					dpp.setNeto(twoDecimal(record.value2().doubleValue()));
					dpp.setDeduction(twoDecimal(record.value3().doubleValue()));
					dpp.setSs(twoDecimal(record.value5().doubleValue()+record.value6().doubleValue()));
					Double otros= dpp.getDeduction()-(dpp.getIrpf()+record.value5().doubleValue());
					if (otros >=0) dpp.setOtros(twoDecimal(otros));
					else dpp.setOtros(0.00);
					view(dpp);
					dpps.put(mes, dpp);
					map.put(ano, dpps);	
				
				}
				else{
					if (!map.get(ano).containsKey(mes)){
						dpp.setMes(mes);
						dpp.setAno(ano);
						dpp.setIrpf(twoDecimal(record.value4().doubleValue()));
						dpp.setNeto(twoDecimal(record.value2().doubleValue()));
						dpp.setDeduction(twoDecimal(record.value3().doubleValue()));
						dpp.setSs(twoDecimal(record.value5().doubleValue()+record.value6().doubleValue()));
						Double otros= dpp.getDeduction()-(dpp.getIrpf()+record.value5().doubleValue());
						if (otros >=0) dpp.setOtros(twoDecimal(otros));
						else dpp.setOtros(0.00);
						view(dpp);

						map.get(ano).put(mes, dpp);
					}
					else{
						DashboardPayrollPortal d = map.get(ano).get(mes);
						d.setIrpf(twoDecimal(d.getIrpf()+record.value4().doubleValue()));
						d.setNeto(twoDecimal(d.getNeto()+record.value2().doubleValue()));
						d.setDeduction(twoDecimal(d.getDeduction()+record.value3().doubleValue()));
						d.setSs(twoDecimal(d.getSs()+record.value5().doubleValue()+record.value6().doubleValue()));
						Double otros= dpp.getDeduction()-(dpp.getIrpf()+record.value5().doubleValue());
						if (otros >=0) d.setOtros(twoDecimal(d.getOtros()+otros));
						
						view(d);

					}		
				}
			}
			salaries = map;
			return map;
		}
		finally{
			if (ctx != null)
				ctx.close();
		}
	}
	private  void view(DashboardPayrollPortal d) {
		LOGGER.debug("AÑO:  "+ d.getAno());
		LOGGER.debug("MES:  "+d.getMes());
		LOGGER.debug("SS:   "+d.getSs());
		LOGGER.debug("OTROS:  "+d.getOtros());
		LOGGER.debug("NETO:  "+d.getNeto());
		LOGGER.debug("IRPF:  "+d.getIrpf());
		LOGGER.debug("");
	}
	private Double twoDecimal(Double valor){
		String val = valor+"";
	    BigDecimal big = new BigDecimal(val);
	    big = big.setScale(2, RoundingMode.HALF_UP);
	    return big.doubleValue();
	}
	public static void main(String[] args) throws SQLException, AonConnectionException {
		//DashboardDocs dd = getTypesCatBD(1607);
		//System.out.println(dd.category+ " "  +dd.size );
		
		//System.out.println(dd.category+ " "  +dd.size );

		System.out.println( ""  );
		System.out.println( ""  );
		List<DashboardDocs> a = null;//getTypesCat();
		for (DashboardDocs d : a) {
			System.out.println(d.category +" "+ d.size);
			//view(d);
		}
		
	}

	public Vector<DashboardRecentFiles> getRecentFiles() {
		return recentFiles;
	}

	public void setRecentFiles(Vector<DashboardRecentFiles> recentFiles) {
		this.recentFiles = recentFiles;
	}

	public void setTypes(Vector<DashboardDocs> types) {
		this.types = types;
	} 
	
	
	
	private Domain getDomain(){
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		return new Domain().setName(domainName).setId(domainId);
	}
	
	private Domain getDomain(String domainName){
		Integer domainId = DomainManager.getCurrentDomain();
		return new Domain().setName(domainName).setId(domainId);
	}
	
	private com.esferalia.aon.occam.api.model.security.User getUser(){
		User user =  UserUtils.getInstance().getLoggedUser();
		return new com.esferalia.aon.occam.api.model.security.User()
				.setId(user.getId())
				.setLogin(user.getLogin())
				.setDomain(user.getDomain());
	}
}
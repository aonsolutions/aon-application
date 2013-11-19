package com.code.aon.aio.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.modules.account.entry.EmptyAccountEntryCheck;
import com.code.aon.ui.accounting.check.modules.account.entry.UnbalancedAccountEntryCheck;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.fiscal.controller.FiscalParametersController;
import com.code.aon.ui.util.AonUtil;

public class DashboardController {

	private final static Logger LOGGER = LoggerFactory.getLogger(DashboardController.class);

	private static final String WHERE = " WHERE ";

	private List<DashboardMessage> messages;
	private DashboardEntry[] periodEntriesCount;
	private List<DashboardFiscalStatus> fiscalStatus;
	private static String[] MODELS = new String[]{"111","115","123","130","131","310","303","349","347"};

	private com.code.aon.accounting.Period accountingPeriod;
	private Integer fiscalYear;
	
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
					IManagerBean periodBean = BeanManager.getManagerBean(com.code.aon.accounting.Period.class);
					List<ITransferObject> list = periodBean.getList(null);
					if (list != null && list.size() > 0) {
						accountingPeriod = (com.code.aon.accounting.Period) list.get(0);
					}
				} catch (ManagerBeanException e) {
					LOGGER.debug(e.getMessage(), e);
				}
			}
		}
		return accountingPeriod;
	}
	public void setAccountingPeriod(com.code.aon.accounting.Period accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}
	
	public void onAccountingPeriodChanged(ActionEvent event) {
		periodEntriesCount = null;
		messages=null;
	}
	public void onFiscalPeriodChanged(ActionEvent event) {
		fiscalStatus = null;
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
		this.fiscalStatus = null;
	}
	
	public DashboardEntry[] getPeriodEntriesCount() throws ManagerBeanException {
		if (periodEntriesCount == null && getAccountingPeriod() != null) {
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection c = null;
			periodEntriesCount = new DashboardEntry[12];
			try {
				Locale locale = AonUtil.getCurrentLocale();
				for (int i = 0; i < 12; i++) {
					DashboardEntry de = new DashboardEntry();
					de.setName( Month.getMonthByValue(i).getName(locale) );
					de.setValue(0);
					periodEntriesCount[i] = de;
				}
				String select = "SELECT " + " COUNT(*)"
						+ ", MONTH(ae.entry_date)"
						+ " FROM account_entry ae"
						+ WHERE + DomainManager.getSQLWhereClause("ae.domain")
						+ " AND ae.account_period = ?"
						+ " GROUP BY MONTH(ae.entry_date)";
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				ps.setInt(1, getAccountingPeriod().getId());
				rs = ps.executeQuery();
				while (rs.next()) {
					int count = rs.getInt(1);
					int month = rs.getInt(2) - 1;
					periodEntriesCount[month].setValue(count);
				}
			} catch (SQLException e) {
				e.printStackTrace();
				// Nothing. Se mostrara array vacio.
			} catch (AonConnectionException e) {
				e.printStackTrace();
				// Nothing. Se mostrara array vacio.
			} finally {
				DatabaseUtil.closeQuietly(rs);
				DatabaseUtil.closeQuietly(ps);
				DatabaseUtil.closeQuietly(c);
			}
		}
		return periodEntriesCount;
	}

	public List<DashboardFiscalStatus> getFiscalStatus() throws ManagerBeanException {
		if (fiscalStatus == null) {
			fiscalStatus = new LinkedList<DashboardFiscalStatus>();
			String select =
				"SELECT fm.model,fm.period,ELT(fm.status  + 1,0,1,1,1)"
					+" FROM fs_model fm"
					+WHERE + DomainManager.getSQLWhereClause("fm.domain")
					+" AND fm.year = ?"
				+" UNION " 
				+"SELECT '303',vat.period,ELT(vatdec.status + 1,1,1)"
					+" FROM fs_vat vat,fs_vat_declaration vatdec"
					+WHERE + DomainManager.getSQLWhereClause("vat.domain")
					+" AND vatdec.fs_vat = vat.id"
					+" AND vat.year = ?"
				+" UNION " 
				+"SELECT '349',m349.period,ELT(m349.status + 1,0,1)"
					+" FROM fs_mod349 m349"
					+WHERE + DomainManager.getSQLWhereClause("m349.domain")
					+" AND m349.year = ?"
				+" UNION " 
				+"SELECT '347',16,ELT(m347.status + 1,0,1)"
					+" FROM fs_mod347 m347"
					+WHERE + DomainManager.getSQLWhereClause("m347.domain")
					+" AND m347.year = (? - 1)";
			PreparedStatement ps = null;
			ResultSet rs = null;
			Connection c = null;
			try {
				for (Period p :  Period.values()) {
					if (p.getStartMonth() != p.getDueMonth()) {
						DashboardFiscalStatus de = new DashboardFiscalStatus();
						de.setPeriod(p);
						de.setModels( new HashMap<String, Integer>());
						for (String model: getModels()) {
							de.getModels().put(model, -1);
						}
						fiscalStatus.add(de);
					}
				}
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
						ResultSet.CONCUR_READ_ONLY);
				System.out.println(getFiscalYear());
				ps.setInt(1, getFiscalYear());
				ps.setInt(2, getFiscalYear());
				ps.setInt(3, getFiscalYear());
				ps.setInt(4, getFiscalYear());
				rs = ps.executeQuery();
				while (rs.next()) {
					String model = rs.getString(1);
					Period period = Period.values()[rs.getInt(2)];
					int exists = rs.getInt(3);
					if (rs.wasNull() ) {
						exists = -1;
					}
					for (DashboardFiscalStatus de : fiscalStatus) {
						if (de.getPeriod() == period) {
							de.getModels().put(model, exists);
						}
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
				// Nothing. Se mostrara array vacio.
			} catch (AonConnectionException e) {
				e.printStackTrace();
				// Nothing. Se mostrara array vacio.
			} finally {
				DatabaseUtil.closeQuietly(rs);
				DatabaseUtil.closeQuietly(ps);
				DatabaseUtil.closeQuietly(c);
			}
			
		}		
		return fiscalStatus;
	}
	public String[] getModels() {
		return MODELS;
	}
	
	public List<DashboardMessage> getMessages() {
		Connection c = null;
		if (messages == null && getAccountingPeriod() != null) {
			messages = new LinkedList<DashboardMessage>();
			try {
				if (getAccountingPeriod() != null) {
					CheckParams params = new CheckParams();
					params.setPeriod( getAccountingPeriod() );
					
					UnbalancedAccountEntryCheck uc = new UnbalancedAccountEntryCheck();
					uc.onExecute( params) ;
					List<ICheckEntry> list = uc.getCheckList();
					if (list != null && list.size() > 0) {
						DashboardMessage msg = new DashboardMessage();
						msg.setCategory("CONTABILIDAD");
						msg.setLevel("red");
						if (list.size() >1 ) {
							msg.setMessage( "Existen " + list.size() + " apuntes descuadrados. Verifique la integridad de la contabilidad." );
						} else {
							msg.setMessage( "Existe 1 apunte descuadrado. Verifique la integridad de la contabilidad." );
						}
						messages.add(msg);
					}
	
					EmptyAccountEntryCheck ec = new EmptyAccountEntryCheck();
					ec.onExecute( params) ;
					list = ec.getCheckList();
					if (list != null && list.size() > 0) {
						DashboardMessage msg = new DashboardMessage();
						msg.setCategory("CONTABILIDAD");
						msg.setLevel("orange");
						if (list.size() >1 ) {
							msg.setMessage( "Existen " + list.size() + " apuntes sin líneas. Verifique la integridad de la contabilidad." );
						} else {
							msg.setMessage( "Existe 1 apunte sin líneas. Verifique la integridad de la contabilidad." );
						}
						messages.add(msg);
					}
				}
				c = DatabaseUtil.getConnection(AonUtil.getDomainName());
				messages.addAll( getInvestmentInvoices(c) );
				messages.addAll( getIntracommunitaryInvoices(c) );
				messages.addAll( getRetentionInvoices(c) );
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
						+" FROM invoice_tax it"
						+" INNER JOIN invoice_detail id ON it.invoice_detail = id.id"
						+" INNER JOIN invoice i ON id.invoice = i.id"
						+WHERE + DomainManager.getSQLWhereClause("it.domain")
						+" AND it.tax_type = 2"
						+" AND i.issue_date BETWEEN ? AND ?"
						+" GROUP BY it.withholding_type";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DashboardMessage> msgs = new LinkedList<DashboardMessage>();
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(getAccountingPeriod().getInitiationDate().getTime()));
			ps.setDate(2, new java.sql.Date(getAccountingPeriod().getDeadline().getTime()));
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
				msg.setMessage("Existen facturas con retención de " + type + " ( " +  i +  " ).");
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

	private Collection<DashboardMessage> getIntracommunitaryInvoices(Connection c) {
		String select = "SELECT count(i.id)"
				+" FROM invoice i"
				+WHERE + DomainManager.getSQLWhereClause("i.domain")
				+" AND i.transaction = 1"
				+" AND i.issue_date BETWEEN ? AND ?";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DashboardMessage> msgs = new LinkedList<DashboardMessage>();
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(getAccountingPeriod().getInitiationDate().getTime()));
			ps.setDate(2, new java.sql.Date(getAccountingPeriod().getDeadline().getTime()));
			rs = ps.executeQuery();
			if (rs.next()) {
				int i = rs.getInt(1);
				if (i > 0) {
					DashboardMessage msg = new DashboardMessage();
					msg.setCategory("GESTIÓN");
					msg.setLevel("green");
					msg.setMessage("Existen facturas intracomunitarias ( " +  i +  " ).");
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
		String select = "SELECT count(i.id)"
				+" FROM invoice i"
				+WHERE + DomainManager.getSQLWhereClause("i.domain")
				+" AND i.issue_date BETWEEN ? AND ?"
				+" AND i.investment = 1"
				+" AND i.id NOT IN (SELECT invoice FROM amortization_invoice)";
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<DashboardMessage> msgs = new LinkedList<DashboardMessage>();
		try {
			ps = c.prepareStatement(select, ResultSet.TYPE_FORWARD_ONLY,
					ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, new java.sql.Date(getAccountingPeriod().getInitiationDate().getTime()));
			ps.setDate(2, new java.sql.Date(getAccountingPeriod().getDeadline().getTime()));
			rs = ps.executeQuery();
			if (rs.next()) {
				int i = rs.getInt(1);
				if (i > 0) {
					DashboardMessage msg = new DashboardMessage();
					msg.setCategory("GESTIÓN");
					msg.setLevel("red");
					msg.setMessage("Existen facturas de inversión sin ficha de amortización ( " +  i +  " ).");
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
}

package com.code.aon.fiscal.mod111;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.code.aon.AonVersion;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.AppParam;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.WithholdingType;
import com.code.aon.config.util.AppParamUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod111Key;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod111Manager extends FiscalModelManager {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String DOCUMENT = "document";
	private static final String WITHHOLDING_TYPE = "withholding_type";
	private static final String TAXABLE_BASE = "taxable_base";
	private static final String QUOTA = "quota";

	private static final String ISSUE_DATE = "salary_issue_date";
	private static final String EMPLOYEE_DOCUMENT = "employee_document";
	private static final String IRPF_BASE = "irpf_base";
	private static final String MONEY_IRPF_BASE = "money_irpf_base";
	private static final String INKIND_IRPF_BASE = "inkind_irpf_base";
	private static final String TOTAL_IRPF = "total_irpf";
	
	//  Se deben tener en cuenta las retenciones PROFESSIONAL, que van a una casilla
	//	y luego las de FARMER y TRANSPORT_OPERATOR, que van a otra juntas.
	//  Se usa ELT para agrupar correctamente.
	//			0 --> PROFESSIONAL 
	//			1 --> RENTING
	//			2 --> MOVABLE_CAPITAL
	//			3 --> FARMER
	//			4 --> TRANSPORT_OPERATOR
	private static String SELECT = "SELECT it.withholding_type " + WITHHOLDING_TYPE 
		+" ,i.rdocument " + DOCUMENT
		+" ,SUM( it.base )" + TAXABLE_BASE
		+" ,SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) " + QUOTA
		+" FROM invoice_tax it "
		+" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)" 
		+" INNER JOIN invoice i ON (id.invoice = i.id)"
		+" WHERE " + DomainManager.getStaticSQLWhereClause("i.domain")
		+" AND i.type != 1 " 			// No Ventas
		+" AND it.tax_type = 2" 		// IRPF
		+" AND it.withholding_type IN (0,3,4)" // IRPF de profesionales,agricultura y transporte.  
		+" AND i.tax_date >= ?"
		+" AND i.tax_date <= ?"
		+" GROUP BY " + WITHHOLDING_TYPE + "," + DOCUMENT;
	
	private static String SELECT_ACCOUNT = "SELECT " 
			+" aed.account,aed.debit,aed.credit "
			+" FROM account_entry ae " 
			+" INNER JOIN account_entry_detail aed ON ae.id = aed.account_entry " 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("ae.domain") 
			+" AND ae.entry_type = " + AccountEntryType.SALARY.ordinal()
			+" AND ae.entry_date >= ?"
			+" AND ae.entry_date <= ?"
			+" ORDER BY ae.id";	
	
	private static String PAYROLL_SELECT = "SELECT " 
			+"  s.employee_document " + EMPLOYEE_DOCUMENT
			+" ,s.issue_date " + ISSUE_DATE
			+" ,s.irpf_base " + IRPF_BASE
			+" ,s.money_irpf_base " + MONEY_IRPF_BASE
			+" ,s.inkind_irpf_base " + INKIND_IRPF_BASE
			+" ,s.total_irpf " + TOTAL_IRPF
			+" FROM salary s"
			+" WHERE s.domain =? "
			+" AND s.issue_date>=?"
			+" AND s.issue_date<=?";

	private String domainName;
	
	public Mod111Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M111;
	}

	@Override
	public Mod111 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod111 mod111 = new Mod111();
		mod111.setFiscalModel(fiscalModel);
		return mod111;
	}
	
	@Override
	public Mod111 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(getDomainName());
			Mod111 mod111 = (Mod111) declaration;
			FiscalModel fiscalModel = mod111.getHeader();
			mod111.initializeDetails();
			Mod111CalculatorFactory factory = new Mod111CalculatorFactory();
			int year = fiscalModel.getYear();
			Administration admin = fiscalModel.getAdministration(); 
			IMod111Calculator calculator = factory.getCalculator( year , admin );
			searchInvoices(conn, mod111,calculator);
			if (fiscalModel.isReadRetentionFromAccount()) {
				searchAccountEntries(conn, mod111,calculator);
			} else {
				searchSalaries(conn, mod111,calculator);
			}
			super.fillDeclaredData(mod111);
			mod111.calculate();
			return mod111;
		} catch (AonConnectionException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
	}

	private void searchInvoices(Connection conn, Mod111 mod111, IMod111Calculator calculator) throws ManagerBeanException {
		FiscalModel fiscalModel = mod111.getHeader();
		Date dateFrom = getInitialDate(fiscalModel);	
		Date dateTo = getDueDate(fiscalModel);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(SELECT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			Map<Mod111Key,Map<String,Integer>> receivers = new HashMap<Mod111Key, Map<String,Integer>>(); 
			while (rs.next()) {
				WithholdingType type = WithholdingType.values()[rs.getInt(WITHHOLDING_TYPE)];
				double base = rs.getDouble( TAXABLE_BASE );
				double retention = rs.getDouble( QUOTA );
				String doc = rs.getString( DOCUMENT );
				if (type == WithholdingType.FARMER ) {
					addReceiver(receivers,calculator.getKeyForFarmerReceivers(),doc);
					addAccumulatedAmont(mod111,calculator.getKeyForFarmerPerception(), base);
					addAccumulatedAmont(mod111,calculator.getKeyForFarmerWitholding(), retention);
				} else {
					addReceiver(receivers,calculator.getKeyForInvoiceReceivers(),doc);
					addAccumulatedAmont(mod111,calculator.getKeyForInvoicePerception(), base);
					addAccumulatedAmont(mod111,calculator.getKeyForInvoiceWitholding(), retention);
				}
			}
			for (Mod111Key key : receivers.keySet() ) {
				mod111.ensureDetail(key).addAccumulatedAmount(receivers.get(key).size());
			}
		} catch (NumberFormatException e) {
			//Nothing
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	private void addAccumulatedAmont(Mod111 mod111, Mod111Key key, double amount) {
		if (key != null && amount != 0) {
			mod111.ensureDetail(key).addAccumulatedAmount(amount);	
		}
	}

	private void addReceiver(Map<Mod111Key, Map<String,Integer>> receivers, Mod111Key key, String document) {
		if (key != null) {
			if (!receivers.containsKey(key)) {
				receivers.put(key, new HashMap<String, Integer>());
			}
			Map<String, Integer> map = receivers.get(key);
			if (!receivers.containsKey(document)) {
				map.put(document, 0);
			}
		}
	}

	private void searchAccountEntries(Connection conn, Mod111 mod111, IMod111Calculator calculator) throws ManagerBeanException {
		FiscalModel fiscalModel = mod111.getHeader();
		Date dateFrom = getInitialDate(fiscalModel);	
		Date dateTo = getDueDate(fiscalModel);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Integer retentionInKindAccount = AppParamUtil.getValueAsInteger(AppParam.ACC_SALARY_CHARGED_RET_IK_ACC);
			Integer salaryInKindAccount = AppParamUtil.getValueAsInteger(AppParam.ACC_DEFAULT_SALARY_IK_ACC);
			Integer retentionAccount = AppParamUtil.getValueAsInteger(AppParam.ACC_SALARY_CHARGED_RET_ACC);
			if ( retentionAccount != null ) {
				Integer salaryAccount = AppParamUtil.getValueAsInteger(AppParam.ACC_DEFAULT_SALARY_ACC);
				if ( salaryAccount != null ) {
					ps = conn.prepareStatement(SELECT_ACCOUNT, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					int i = 0;
					int filled = DomainManager.fillHostVariables(ps, 1);
					i = i + filled;
					ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
					ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
					rs = ps.executeQuery();
					boolean found = false;
					boolean inKindFound = false;
					while (rs.next()) {
						int acc = rs.getInt(1);
						double deb = rs.getDouble(2);
						double cre = rs.getDouble(3);
						if (acc == retentionAccount) {
							found = true;
							addAccumulatedAmont(mod111, calculator.getKeyForWorkWitholding(), cre);
						} else if (acc == salaryAccount) {
							found = true;
							addAccumulatedAmont(mod111, calculator.getKeyForWorkPerception(), deb);
						} else if (retentionInKindAccount != null && acc == retentionInKindAccount) {
							inKindFound = true;
							addAccumulatedAmont(mod111, calculator.getKeyForWorkInKindWitholding(), cre);
						} else if (salaryInKindAccount != null && acc == salaryInKindAccount) {
							inKindFound = true;
							addAccumulatedAmont(mod111, calculator.getKeyForWorkInKindPerception(), deb);
						}
					}
					if (found) {
						addAccumulatedAmont(mod111, calculator.getKeyForWorkReceivers(), fiscalModel.getReceiverCount());
					}
					if (inKindFound) {
						addAccumulatedAmont(mod111, calculator.getKeyForWorkInKindReceivers(), fiscalModel.getReceiverInKindCount());
					}
					
				}
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod111 mod111 = new Mod111();
		mod111.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod111.addDetail(detail);
		}
		return mod111;
	}
	

	private void searchSalaries(Connection conn, Mod111 mod111, IMod111Calculator calculator) throws ManagerBeanException {
		FiscalModel fiscalModel = mod111.getHeader();
		Date dateFrom = getInitialDate(fiscalModel);	
		Date dateTo = getDueDate(fiscalModel);
		Date periodFrom = fiscalModel.getPeriod().getStartDate(fiscalModel.getYear());	
		Date periodTo = fiscalModel.getPeriod().getDueDate(fiscalModel.getYear());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			ps = conn.prepareStatement(PAYROLL_SELECT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i,fiscalModel.getDomain());
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			int receivers = 0;
			int inKindReceivers = 0;
			double moneyBaseAccum = 0;
			double moneyQuotaAccum = 0;
			double inKindBaseAccum = 0;
			double inKindQuotaAccum = 0;
			Set<String> documents = new HashSet<String>();
			while (rs.next()) {
				String doc = rs.getString(EMPLOYEE_DOCUMENT);
				Date issueDate = rs.getDate(ISSUE_DATE);
				
				double base = rs.getDouble(IRPF_BASE);
				double quota = rs.getDouble(TOTAL_IRPF);
				 
				double moneyBase = base;
				double moneyQuota = quota;
				double inKindBase = rs.getDouble(INKIND_IRPF_BASE);
				double inKindQuota = 0;
				if (inKindBase != 0) {
					moneyBase = rs.getDouble(MONEY_IRPF_BASE);
					moneyQuota = CommonUtil.round( moneyBase * quota  / base ); 	
					inKindQuota = CommonUtil.round( quota - moneyQuota);
				} else {
					moneyBase = base;
					moneyQuota = quota;
				}
				// Se contabilizan los perceptores cuyas nóminas pertenezcan al periodo fiscal.
				if ( !issueDate.before(periodFrom) && !issueDate.after(periodTo) && !documents.contains(doc)) { 
					documents.add(doc);
					receivers = receivers + (moneyQuota !=0 || moneyBase!=0?1:0);
					inKindReceivers = inKindReceivers + (inKindQuota !=0 || inKindBase!=0?1:0);
				}
				moneyBaseAccum = moneyBaseAccum + moneyBase;
				moneyQuotaAccum = moneyQuotaAccum + moneyQuota;
				inKindBaseAccum = inKindBaseAccum + inKindBase;
				inKindQuotaAccum = inKindQuotaAccum + inKindQuota;

			}
			addAccumulatedAmont(mod111, calculator.getKeyForWorkReceivers(), receivers);	
			addAccumulatedAmont(mod111, calculator.getKeyForWorkPerception(), moneyBaseAccum);
			addAccumulatedAmont(mod111, calculator.getKeyForWorkWitholding(), moneyQuotaAccum);
			addAccumulatedAmont(mod111, calculator.getKeyForWorkInKindReceivers(), inKindReceivers);	
			addAccumulatedAmont(mod111, calculator.getKeyForWorkInKindPerception(), inKindBaseAccum);
			addAccumulatedAmont(mod111, calculator.getKeyForWorkInKindWitholding(), inKindQuotaAccum);
			
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
				}
			}
			if (ps != null) {
				try {
					ps.close();
				} catch (SQLException e) {
				}
			}
		}
	}

}

package com.code.aon.fiscal.mod111;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.aon.accounting.IDefaultAccounts;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.ApplicationParameter;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod111Manager extends FiscalModelManager {
	
	private static String SELECT = "SELECT " 
		+"i.type,it.percentage,i.rdocument,i.rname,"
		+" SUM( id.taxable_base),"
		+" SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) RET "
		+" FROM invoice_tax it "
		+" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)" 
		+" INNER JOIN invoice i ON (id.invoice = i.id)"
		+" WHERE " + DomainManager.getStaticSQLWhereClause("i.domain")
		+" AND i.type != 1 " 			// No Ventas
		+" AND it.tax_type = 2" 		// IRPF
		+" AND it.withholding_type = 0" // IRPF de profesionales
		+" AND i.tax_date >= ?"
		+" AND i.tax_date <= ?"
		+" GROUP BY i.type,it.percentage,i.rdocument,i.rname";
	
	private static String SELECT_ACCOUNT = "SELECT " 
			+" aed.account,aed.debit,aed.credit "
			+" FROM account_entry ae " 
			+" INNER JOIN account_entry_detail aed ON ae.id = aed.account_entry " 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("ae.domain") 
			+" AND ae.entry_type = " + AccountEntryType.SALARY.ordinal()
			+" AND ae.entry_date >= ?"
			+" AND ae.entry_date <= ?"
			+" ORDER BY ae.id";	
	
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
		List<String> receiverDocuments = new ArrayList<String>();
		try {
			ps = conn.prepareStatement(SELECT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			while (rs.next()) {
				
				double rentingAmount = rs.getDouble(5);
				double retention = rs.getDouble(6);
				String document = rs.getString(3);
				if (!receiverDocuments.contains(document) ) {
					receiverDocuments.add(document);
					mod111.ensureDetail(calculator.getKeyForInvoiceReceivers()).addAccumulatedAmount(1);
				}
				
				mod111.ensureDetail(calculator.getKeyForInvoicePerception()).addAccumulatedAmount(rentingAmount);
				mod111.ensureDetail(calculator.getKeyForInvoiceWitholding()).addAccumulatedAmount(retention);
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

	private void searchAccountEntries(Connection conn, Mod111 mod111, IMod111Calculator calculator) throws ManagerBeanException {
		FiscalModel fiscalModel = mod111.getHeader();
		Date dateFrom = getInitialDate(fiscalModel);	
		Date dateTo = getDueDate(fiscalModel);
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationParameter.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME),  IDefaultAccounts.SALARY_CHARGED_RETENTION_ACCOUNT );
			List<ITransferObject> list = bean.getList(criteria);
			if (list != null && list.size() > 0 ) {
				ApplicationParameter ap = (ApplicationParameter) list.get(0);
				int retentionAccount = Integer.parseInt(ap.getValue());
				criteria = new Criteria();
				criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_PARAMETER_NAME),  IDefaultAccounts.SALARY_ACCOUNT );
				list = bean.getList(criteria);
				if (list != null && list.size() > 0 ) {
					ap = (ApplicationParameter) list.get(0);
					int salaryAccount = Integer.parseInt(ap.getValue());	
					ps = conn.prepareStatement(SELECT_ACCOUNT, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
					int i = 0;
					int filled = DomainManager.fillHostVariables(ps, 1);
					i = i + filled;
					ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
					ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
					rs = ps.executeQuery();
					boolean found = false;
					while (rs.next()) {
						int acc = rs.getInt(1);
						double deb = rs.getDouble(2);
						double cre = rs.getDouble(3);
						if (acc == retentionAccount) {
							found = true;
							mod111.ensureDetail(calculator.getKeyForWorkWitholding()).addAccumulatedAmount(cre);
						}
						if (acc == salaryAccount) {
							found = true;
							mod111.ensureDetail(calculator.getKeyForWorkPerception()).addAccumulatedAmount(deb);
						}
					}
					if (found) {
						mod111.ensureDetail(calculator.getKeyForWorkReceivers()).addAccumulatedAmount( fiscalModel.getReceiverCount());
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
	
/*	
	private static String PAYROLL_SELECT = "SELECT " 
			+" s.employee_document doc"
			+" ,SUM(s.money_irpf_base) base"
			+" ,SUM(s.inkind_irpf_base) inKindBase"
			+" ,SUM(s.total_irpf) quota"
			+" FROM salary s"
			+" INNER JOIN contract c ON s.contract = c.id"
			+" INNER JOIN workplace w ON c.workplace = w.id"
			+" WHERE " + DomainManager.getStaticSQLWhereClause("s.domain")
			+" AND w.enterprise = ?"
			+" AND s.issue_date>=?"
			+" AND s.issue_date<=?"
			+" GROUP BY s.employee_document";
	

		try {
			Integer enterpriseId = null;
			IManagerBean bean = BeanManager.getManagerBean(Company.class);
			List<ITransferObject> list = bean.getList(null);
			for (ITransferObject to : list) {
				Company company = (Company) to;
				IManagerBean enterpriseBean = BeanManager.getManagerBean(Enterprise.class);
				Enterprise enterprise = (Enterprise) enterpriseBean.get(company.getId());
				if (enterprise != null) {
					enterpriseId = enterprise.getId();
				}
			}
			if (enterpriseId == null) {
				throw new AonException("No existe ninguna empresa (enterprise) definida en el dominio activo."); 
			}
			ps = conn.prepareStatement(PAYROLL_SELECT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setInt(++i, enterpriseId);
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			while (rs.next()) {
				double base = rs.getDouble(2);
				double inKindBase = rs.getDouble(3);
				double quota = rs.getDouble(4);
				String document = rs.getString(1);
				System.out.println("1.- "+ document  +mod111.getDetail(calculator.getKeyForReceivers()).getAccumulatedAmount());
				if (!receiverDocuments.contains(document) ) {
					receiverDocuments.add(document);
					mod111.ensureDetail(calculator.getKeyForReceivers()).addAccumulatedAmount(1);
				}
				mod111.ensureDetail(calculator.getKeyForPerception()).addAccumulatedAmount(base);
				mod111.ensureDetail(calculator.getKeyForWitholding()).addAccumulatedAmount(quota);
				mod111.ensureDetail(calculator.getKeyForInKindPerception()).addAccumulatedAmount(inKindBase);
				System.out.println("2.- "+ document +mod111.getDetail(calculator.getKeyForReceivers()).getAccumulatedAmount());
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



*/
}

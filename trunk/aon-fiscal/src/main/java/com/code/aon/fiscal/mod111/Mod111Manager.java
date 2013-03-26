package com.code.aon.fiscal.mod111;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.company.Company;
import com.code.aon.company.Enterprise;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
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
	

	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M111;
	}

	@Override
	public Mod111 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod111 mod111 = new Mod111();
		mod111.setFiscalModel(fiscalModel);
		mod111.initializeDetails();
		return mod111;
	}
	
	@Override
	public Mod111 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Mod111 mod111 = (Mod111) declaration;
		FiscalModel fiscalModel = mod111.getHeader();
		mod111.initializeDetails();
		Date dateFrom = getInitialDate(fiscalModel);	
		Date dateTo = getDueDate(fiscalModel);
		Mod111CalculatorFactory factory = new Mod111CalculatorFactory();
		int year = fiscalModel.getYear();
		Administration admin = fiscalModel.getAdministration(); 
		IMod111Calculator calculator = factory.getCalculator( year , admin );
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> receiverDocuments = new ArrayList<String>();
//		List<String> inKindReceiverDocuments = new ArrayList<String>();
		String sessionName = HibernateUtil.getSessionFactoryName( FiscalModel.class.getName() );
		try {
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
					mod111.ensureDetail(calculator.getKeyForReceivers()).addAccumulatedAmount(1);
				}
				mod111.ensureDetail(calculator.getKeyForPerception()).addAccumulatedAmount(rentingAmount);
				mod111.ensureDetail(calculator.getKeyForWitholding()).addAccumulatedAmount(retention);
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
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(PAYROLL_SELECT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
		super.fillDeclaredData(mod111);
		mod111.calculate();
		return mod111;
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
}

package com.code.aon.fiscal.mod115;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod115Key;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod115Manager extends FiscalModelManager {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String SELECT = "SELECT " 
			+"i.type,it.percentage,i.rdocument,i.rname,"
			+" SUM( it.base),"
			+" SUM( IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) ) ) RET "
			+" FROM invoice_tax it "
			+" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)" 
			+" INNER JOIN invoice i ON (id.invoice = i.id)"
			+" WHERE " + DomainManager.getStaticSQLWhereClause("i.domain")
			+" AND i.type != 1 " 			// No Ventas
			+" AND it.tax_type = 2" 		// IRPF
			+" AND it.withholding_type = 1" // IRPF de alquileres
			+" AND i.tax_date >= ?"
			+" AND i.tax_date <= ?"
			+" GROUP BY i.type,it.percentage,i.rdocument,i.rname";

	private String domainName;
	
	public Mod115Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	
	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M115;
	}

	@Override
	public Mod115 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod115 mod115 = new Mod115();
		mod115.setFiscalModel(fiscalModel);
		return mod115;
	}
	
	@Override
	public Mod115 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Mod115 mod115 = (Mod115) declaration;
			FiscalModel fiscalModel = mod115.getHeader();
			mod115.initializeDetails();
			Date dateFrom = getInitialDate(fiscalModel);	
			Date dateTo = getDueDate(fiscalModel);
			List<String> lessorDocuments = new ArrayList<String>();
			conn = DatabaseUtil.getConnection(getDomainName());
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
				if (!lessorDocuments.contains(document) ) {
					lessorDocuments.add(document);
					mod115.ensureDetail(Mod115Key.C01).addAccumulatedAmount(1);
				}
				mod115.ensureDetail(Mod115Key.C02).addAccumulatedAmount(rentingAmount);
				mod115.ensureDetail(Mod115Key.C03).addAccumulatedAmount(retention);
			}
			fillDeclaredData(mod115);
			mod115.calculate();			
			return mod115;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod115 mod115 = new Mod115();
		mod115.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod115.addDetail(detail);
		}
		return mod115;
	}
	
}

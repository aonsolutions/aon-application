package com.code.aon.fiscal.mod123;

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
import com.code.aon.fiscal.enumeration.Mod123Key;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod123Manager extends FiscalModelManager {
	
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
			+" AND it.withholding_type = 2" // IRPF de capital mobiliario
			+" AND i.tax_date >= ?"
			+" AND i.tax_date <= ?"
			+" GROUP BY i.type,it.percentage,i.rdocument,i.rname";

	private String domainName;
	
	public Mod123Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}
	
	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M123;
	}

	@Override
	public Mod123 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod123 mod123 = new Mod123();
		mod123.setFiscalModel(fiscalModel);
		return mod123;
	}
	
	@Override
	public Mod123 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			Mod123 mod123 = (Mod123) declaration;
			FiscalModel fiscalModel = mod123.getHeader();
			mod123.initializeDetails();
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
					mod123.ensureDetail(Mod123Key.C01).addAccumulatedAmount(1);
				}
				mod123.ensureDetail(Mod123Key.C02).addAccumulatedAmount(rentingAmount);
				mod123.ensureDetail(Mod123Key.C03).addAccumulatedAmount(retention);
			}
			fillDeclaredData(mod123);
			mod123.calculate();
			return mod123;
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
		Mod123 mod123 = new Mod123();
		mod123.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod123.addDetail(detail);
		}
		return mod123;
	}
	
}

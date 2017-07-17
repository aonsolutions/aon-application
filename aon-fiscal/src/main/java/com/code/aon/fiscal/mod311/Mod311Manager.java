package com.code.aon.fiscal.mod311;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.AonException;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod310Key;
import com.code.aon.fiscal.enumeration.Mod311Key;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.vat.tax.VatTaxManager;
import net.aonsolutions.core.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod311Manager extends FiscalModelManager {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String domainName;
	
	public Mod311Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M311;
	}

	@Override
	public Mod311 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod311 mod311 = new Mod311();
		mod311.setFiscalModel(fiscalModel);
		return mod311;
	}
	
	@Override
	public Mod311 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Mod311 mod311 = (Mod311) declaration;
		FiscalModel fiscalModel = mod311.getHeader();
		mod311.initializeDetails();
		
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		double c02 = 0.0;
		try {
			String SELECT = 
					"SELECT SUM(fs_model_detail.amount) "
					+ " FROM fs_model,fs_model_detail "
					+ " WHERE fs_model.model = '310' "
					+ " AND fs_model.domain = " + DomainManager.getCurrentDomain()
					+ " AND fs_model.year =  " + fiscalModel.getYear()
					+ " AND fs_model.id=fs_model_detail.fs_model "
					+ " AND fs_model_detail.type = '" + Mod310Key.C12.getValue() + "'";			
			conn = DatabaseUtil.getConnection(getDomainName());
			ps = conn.prepareStatement(SELECT,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
			rs = ps.executeQuery();
			while (rs.next()) {
				double d = rs.getDouble(1);
				c02 = CommonUtil.round(c02 + d );				
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
		mod311.ensureDetail( Mod311Key.C02).addAccumulatedAmount(CommonUtil.round(c02));
		
		Date fromDate = fiscalModel.getPeriod().getStartDate(  fiscalModel.getYear() );
		Date toDate = fiscalModel.getPeriod().getDueDate(  fiscalModel.getYear() );
		VatTaxManager taxManager = new VatTaxManager(getDomainName());
		List<VatTaxDetail> vatDetails = taxManager.getVatTax(fiscalModel.getDomain(),fromDate, toDate );
		double c04 = 0.0;
		double c06 = 0.0;
		double c08 = 0.0;
		for (VatTaxDetail vatDetail : vatDetails) {
			// Adquisiciones intracomunitarias de bienes corrientes
			if (vatDetail.getKey() == VatTaxKey.A3 ) {
				c04 = c04 + vatDetail.getQuotaAccumulated(); 
			}
			// Inversión de sujeto pasivo
			if (vatDetail.getKey() == VatTaxKey.A4 ) {
				c06 = c06 + vatDetail.getQuotaAccumulated(); 
			}
			// Adquisiciones o importacion de activos fijos
			if (vatDetail.getKey() == VatTaxKey.D2
			 || vatDetail.getKey() == VatTaxKey.C2) {
				c08 = c08 + vatDetail.getQuotaAccumulated(); 
			}
			
		}
		mod311.ensureDetail( Mod311Key.C04).addAccumulatedAmount(CommonUtil.round(c04));
		mod311.ensureDetail( Mod311Key.C06).addAccumulatedAmount(CommonUtil.round(c06));
		mod311.ensureDetail( Mod311Key.C08).addAccumulatedAmount(CommonUtil.round(c08));
		vatDetails = null;
		return mod311;
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod311 mod311 = new Mod311();
		mod311.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			mod311.addDetail(detail);
		}
		return mod311;
	}
}

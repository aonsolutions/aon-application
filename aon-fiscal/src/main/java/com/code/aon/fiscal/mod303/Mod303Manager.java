package com.code.aon.fiscal.mod303;

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
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.fiscal.FiscalModel;
import com.code.aon.fiscal.FiscalModelDetail;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.code.aon.fiscal.enumeration.Mod303Key;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.fiscal.model.FiscalModelManager;
import com.code.aon.fiscal.model.IFiscalDeclaration;
import com.code.aon.fiscal.vat.tax.VatTaxManager;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class Mod303Manager extends FiscalModelManager {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	// Ingresos a cuenta realizados en el ejercicio
	private static String SELECT_49 = "SELECT " 
			+" SUM(fmd.amount)"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fmd.type = ? "
			+" AND fmd.amount > 0 "
			+ "ORDER BY fm.period DESC";

	// Cuotas a compensar de periodos anteriores
	private static String SELECT_67 = "SELECT " 
			+" fmd.amount"
			+" FROM fs_model_detail fmd"
			+" INNER JOIN fs_model fm ON (fmd.fs_model = fm.id)" 
			+" WHERE " + DomainManager.getStaticSQLWhereClause("fmd.domain")
			+" AND fm.model = ? "
			+" AND fm.administration = ? "
			+" AND fm.year = ? "
			+" AND fm.period < ? "
			+" AND fmd.type = ? "
			+ "ORDER BY fm.period DESC";

	private String domainName;
	
	public Mod303Manager(String domainName) {
		this.domainName = domainName;
	}
	
	public String getDomainName() {
		return domainName;
	}

	@Override
	public boolean accept(FiscalModelType type) {
		return type == FiscalModelType.M303 || type == FiscalModelType.M303_AI;
	}

	@Override
	public Mod303 initializeFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod303 mod303 = new Mod303(domainName);
		mod303.setFiscalModel(fiscalModel);
		return mod303;
	}
	
	@Override
	public Mod303 initializeFiscalModelDetails(IFiscalDeclaration declaration) throws AonException {
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(getDomainName());

			Mod303 mod303 = (Mod303) declaration;
			FiscalModel fiscalModel = mod303.getHeader();
			mod303.initializeDetails();
			Date fromDate = fiscalModel.getPeriod().getStartDate(  fiscalModel.getYear() );
			Date toDate = fiscalModel.getPeriod().getDueDate(  fiscalModel.getYear() );
			VatTaxManager taxManager = new VatTaxManager(getDomainName());
			List<VatTaxDetail> vatDetails = taxManager.getVatTax(fiscalModel.getDomain(),fromDate, toDate );
			double c51 = 0.0;
			double c52 = 0.0;
			double c53 = 0.0;
			double c55 = 0.0;
			double cuotasSoportadas = 0.0;
			for (VatTaxDetail vatDetail : vatDetails) {
				// Adquisiciones intracomunitarias de bienes corrientes
				if (vatDetail.getKey() == VatTaxKey.A3 ) {
					c51 = c51 + vatDetail.getQuotaAccumulated(); 
				}
				// Inversión de sujeto pasivo
				if (vatDetail.getKey() == VatTaxKey.A4 ) {
					c53 = c53 + vatDetail.getQuotaAccumulated(); 
				}
				// Adquisiciones o importacion de activos fijos
				if (vatDetail.getKey() == VatTaxKey.B2
				 || vatDetail.getKey() == VatTaxKey.D2
				 || vatDetail.getKey() == VatTaxKey.C2) {
					c55 = c55 + vatDetail.getQuotaAccumulated(); 
				}
				if (vatDetail.getKey() == VatTaxKey.B1
				  || vatDetail.getKey() == VatTaxKey.B3
				  || vatDetail.getKey() == VatTaxKey.C1
				  || vatDetail.getKey() == VatTaxKey.D1
				  || vatDetail.getKey() == VatTaxKey.D3) {
					cuotasSoportadas = cuotasSoportadas + vatDetail.getQuotaAccumulated(); 
				}
				if (vatDetail.getKey() == VatTaxKey.EBI) {
					c52 = c52 + vatDetail.getQuotaAccumulated();
				}
				
			}
			if (mod303.isLastPeriod()) {
				double c49 = getPreviousAmount(conn, SELECT_49, fiscalModel, Mod303Key.C71);
				mod303.ensureDetail( Mod303Key.C49).addAccumulatedAmount(CommonUtil.round(c49));
			}
			mod303.ensureDetail( Mod303Key.C65).addAccumulatedAmount(100.0);
			mod303.ensureDetail( Mod303Key.C51).addAccumulatedAmount(CommonUtil.round(c51));
			mod303.ensureDetail( Mod303Key.C52).addAccumulatedAmount(CommonUtil.round(c52));
			mod303.ensureDetail( Mod303Key.C53).addAccumulatedAmount(CommonUtil.round(c53));
			mod303.ensureDetail( Mod303Key.C55).addAccumulatedAmount(CommonUtil.round(c55));
			double c67 = getPreviousAmount(conn, SELECT_67, fiscalModel, Mod303Key.C71);
			if (c67 < 0 ) {
				mod303.ensureDetail( Mod303Key.C67).addAccumulatedAmount(CommonUtil.round(c67 * (-1) ));
			}
			vatDetails = null;
			return mod303;
			
		} catch (AonConnectionException e) {
			throw new AonException(e.getMessage(),e);
		} finally {
			DatabaseUtil.closeQuietly(conn);
		}
	}

	@Override
	public IFiscalDeclaration loadFiscalModel(FiscalModel fiscalModel) throws AonException {
		Mod303 mod303 = new Mod303(domainName);
		mod303.setFiscalModel(fiscalModel);
		IManagerBean bean = BeanManager.getManagerBean(FiscalModelDetail.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName( IEntityAlias.FISCAL_MODEL_DETAIL_FISCAL_MODEL_ID );
		criteria.addEqualExpression(alias, fiscalModel.getId());
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to : list) {
			FiscalModelDetail detail = (FiscalModelDetail) to;
			if (detail.getKey() == Mod303Key.CAG1_V2 || detail.getKey() == Mod303Key.CAG2_V2) {
				detail.setAccumulatedAmount( CommonUtil.round(detail.getAccumulatedAmount() / 10000,5));
				detail.setAmount( CommonUtil.round(detail.getAmount() / 10000,5));
			}
			mod303.addDetail(detail);
		}
		return mod303;
	}
	
	private double getPreviousAmount(Connection c,String select,FiscalModel fiscalModel, Mod303Key key) throws AonException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		double value = 0;
		try {
			ps = c.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			int filled = DomainManager.fillHostVariables(ps, 1);
			i = i + filled;
			ps.setString(++i, FiscalModelType.M303.getValue());
			ps.setInt(++i, fiscalModel.getAdministration().ordinal());
			ps.setInt(++i, fiscalModel.getYear());
			ps.setInt(++i, fiscalModel.getPeriod().ordinal());
			ps.setString(++i, key.getValue());
			rs = ps.executeQuery();
			if (rs.next()) {
				value = rs.getDouble(1);
			}
			rs.close();
			ps.close();
			return value;
		} catch (SQLException e) {
			throw new AonException(e.getMessage(), e);
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


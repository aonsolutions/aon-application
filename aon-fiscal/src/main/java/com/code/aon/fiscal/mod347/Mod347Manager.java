package com.code.aon.fiscal.mod347;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.Province;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import net.aonsolutions.core.dbutils.DatabaseUtil;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.fiscal.enumeration.Mod347Type;
import net.aonsolutions.core.pool.AonConnectionException;

public class Mod347Manager {

	private static final String KEY_ALIAS = "mod347Key";
	private static final String INVOICE_ID_ALIAS = "id";
	private static final String REGISTRY_ALIAS = "registry";
	private static final String DOCUMENT_ALIAS = "document";
	private static final String NAME_ALIAS = "name";
	private static final String ISSUE_DATE_ALIAS = "issue_date";
	private static final String TAX_DATE_ALIAS = "tax_date";
	private static final String COUNTRY_ALIAS = "country";
	private static final String BASE_ALIAS = "base";
	private static final String QUOTA_ALIAS = "quota";
	private static final String RETENTION_QUOTA_ALIAS = "retention_quota";
	private static final String SURCHARGE_QUOTA_ALIAS = "surcharge_quota";
	private static final String VAT_ACCRUAL_PAYMENT_ALIAS = "vat_accrual_payment";
	private static final String FINANCE_AMOUNT_ALIAS = "finance_amount";
	private static final String TRANSACTION_ALIAS = "transaction";

	public Mod347 generateDetails(Mod347Parameters params) throws ManagerBeanException {
		int year = params.getMod347().getYear();
		Map<String,Mod347Detail> map = new TreeMap<String, Mod347Detail>();
		
		params.setPendingAccrualPayment(false);
		generateDetails(params,map,year);
		params.setPendingAccrualPayment(true);
		generateDetails(params,map,year-1);		
		
		IManagerBean bean = BeanManager.getManagerBean(Mod347Detail.class);
		double minAmount = params.getMod347().getMinimumAmount();
		for (Mod347Detail detail : map.values()) {
			double amount = detail.getAmount(); 
			double previousAmount = detail.getPreviousAmount();
			if ( (amount >= minAmount || amount <= ( minAmount * (-1))) 
				|| 
				 (detail.isPendingVatAccrual()
					&& (previousAmount >= minAmount || previousAmount <= ( minAmount * (-1)))
					&& CommonUtil.round(detail.getVatAccrualAmount()) != 0.0)) {
				bean.insert(detail);
			}
		}
		return params.getMod347();	
	}
	
	private void generateDetails(Mod347Parameters params, Map<String,Mod347Detail> map, int year) throws ManagerBeanException {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT ELT(i.type+1, 'A', 'B', 'A') ").append(KEY_ALIAS)
			.append(",i.id ").append(INVOICE_ID_ALIAS)
			.append(",i.registry ").append(REGISTRY_ALIAS)
			.append(",i.rdocument ").append(DOCUMENT_ALIAS)
			.append(",i.rname ").append(NAME_ALIAS)
			.append(",i.issue_date ").append(ISSUE_DATE_ALIAS)
			.append(",i.tax_date ").append(TAX_DATE_ALIAS)
			.append(",i.transaction ").append(TRANSACTION_ALIAS)
			.append(",i.retention_quota ").append(RETENTION_QUOTA_ALIAS)
			.append(",r.nationality ").append(COUNTRY_ALIAS)
			.append(",i.vat_accrual_payment ").append(VAT_ACCRUAL_PAYMENT_ALIAS)
			.append(",SUM(it.base) ").append(BASE_ALIAS)
			.append(",SUM( IF(it.quota=0, ROUND(it.percentage * id.taxable_base / 100,2) ,IF(it.quota is NULL,0,it.quota))) ").append(QUOTA_ALIAS)
			.append(",SUM( IF( it.surcharge_quota=0, ROUND(it.surcharge * id.taxable_base / 100,2) ,IF(it.surcharge_quota is NULL,0,it.surcharge_quota))) ").append(SURCHARGE_QUOTA_ALIAS)
			.append(" FROM invoice i ")
			.append(" INNER JOIN registry r ON (r.id = i.registry) ")
			.append(" INNER JOIN invoice_detail id ON (id.invoice = i.id) ")
			.append(" INNER JOIN invoice_tax it ON it.invoice_detail = id.id ")
			.append(" WHERE i.domain = ? ")
			.append(" AND ")
			.append(params.isTaxDateEnabled()?"i.tax_date":"i.issue_date")
			.append(" BETWEEN ? AND ? ")
			.append(" AND it.tax_type = 1 ");
		appendConditions(params, buf);
		buf.append(" GROUP BY ").append(INVOICE_ID_ALIAS);
		
		Connection conn = null;
		PreparedStatement ps = null; 
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		PreparedStatement ps2 = null;
		ResultSet rs2 = null;
		PreparedStatement ps3 = null;
		ResultSet rs3 = null;
		PreparedStatement ps180 = null;
		ResultSet rs180 = null;
		PreparedStatement ps190 = null;
		ResultSet rs190 = null;
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());
			ps180 = conn.prepareStatement(
					"SELECT count(*) FROM fs_model180 m180,fs_model180_detail m180d WHERE "
					+" m180.id = m180d.fs_model180 AND "
					+DomainManager.getSQLWhereClause("m180.domain")
					+" AND m180.year = ? "
					+" AND m180d.document = ? "
					);
			ps190 = conn.prepareStatement(
					"SELECT count(*) FROM fs_model190 m190,fs_model190_detail m190d WHERE "
					+" m190.id = m190d.fs_model190 AND "
					+DomainManager.getSQLWhereClause("m190.domain")
					+" AND m190.year = ? "
					+" AND m190d.document = ? "
					);
			ps1 = conn.prepareStatement(
					"SELECT geozone.code FROM raddress,geozone WHERE "
					+DomainManager.getSQLWhereClause("raddress.domain")
					+" AND raddress.geozone = geozone.id"
					+" AND raddress.registry = ? AND raddress.type = 0",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps2 = conn.prepareStatement(
					"SELECT  SUM( IF(ft.type=1, ft.amount,  -ft.amount )) " + FINANCE_AMOUNT_ALIAS
					+"  FROM finance f "
					+" INNER JOIN  finance_tracking ft ON (ft.finance = f.id) "
					+" WHERE f.invoice = ?"
					+" AND ft.tracking_date BETWEEN ? AND ? "
					+" AND ft.type IN (1,2) ",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps3 = conn.prepareStatement(
					"SELECT  SUM( f.amount ) " + FINANCE_AMOUNT_ALIAS
					+"  FROM finance f "
					+" WHERE f.invoice = ?"
					+" AND f.status IN (0,2) ",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			Mod347 mod347 = params.getMod347();
			ps = conn.prepareStatement(buf.toString(),
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, DomainManager.getCurrentDomain());
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearFirstDay(year).getTime()));
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearLastDay(year).getTime()));
			Calendar c = Calendar.getInstance(); 
			rs = ps.executeQuery();
			while (rs.next()) {
				Mod347Type key =  Mod347Type.valueOf(rs.getString( KEY_ALIAS ));
				int invoiceId= rs.getInt( INVOICE_ID_ALIAS );
				int registry = rs.getInt( REGISTRY_ALIAS );
				String document = rs.getString( DOCUMENT_ALIAS);
				String name = rs.getString(NAME_ALIAS);
				String country = rs.getString(COUNTRY_ALIAS);
				double base = rs.getDouble(BASE_ALIAS);
				double quota = rs.getDouble(QUOTA_ALIAS);
				double surchargeQuota = rs.getDouble(SURCHARGE_QUOTA_ALIAS);
				Date date = params.isTaxDateEnabled()?rs.getDate(TAX_DATE_ALIAS):rs.getDate(ISSUE_DATE_ALIAS);
				boolean vatAccrualPayment = rs.getBoolean(VAT_ACCRUAL_PAYMENT_ALIAS);
				
				// (Sólo el destinatario de la operación).
				// Se pondrá una "X" en este campo para identificar separadamente del resto las operaciones 
				// en las que el sujeto pasivo sea el destinatario de la operación de acuerdo con lo establecido 
				// en el artículo 84.Uno.2o de la Ley 37/1992, de 28 de diciembre.
				boolean isp = key == Mod347Type.A && (rs.getInt(TRANSACTION_ALIAS) == InvoiceTransactionType.OTHER_ISP.ordinal());
				
				
				boolean included = true;
				double retentionQuota = rs.getDouble(RETENTION_QUOTA_ALIAS);
				
				// Se excluyen los declarados en el modelo 180, si así se indica.
				if ( retentionQuota > 0 && params.isExcludeMod180Declared()) {
					ps180.setInt(1,year);
					ps180.setString(2,document);
					rs180 = ps180.executeQuery();
					int count = 0;
					if (rs180.next()) {
						count = rs180.getInt(1);
						included = (count == 0);
					}
					rs180.close();
				}
				
				// Se excluyen los declarados en el modelo 190, si así se indica.
				if ( included && retentionQuota > 0 && params.isExcludeMod190Declared()) {
					ps190.setInt(1,year);
					ps190.setString(2,document);
					rs190 = ps190.executeQuery();
					int count = 0;
					if (rs190.next()) {
						count = rs190.getInt(1);
						included = (count == 0);
					}
					rs190.close();
				}
				
				if (included) {
					String mapId = key+";"+document+";"+vatAccrualPayment+";"+isp;
					Mod347Detail detail = map.get(mapId);
					if (detail == null) {
						detail = new Mod347Detail();
						detail.setMod347(mod347);
						detail.setType(key);
						if (StringUtils.length(name) > 64) {
							name = StringUtils.substring(name, 0, 63);
						}
						detail.setName(name);
						detail.setVatAccrual(vatAccrualPayment);
						detail.setIsp(isp);
						Country cou = null;
						if (StringUtils.isNotBlank(country)) {
							cou = Country.valueOf( country);	
						}
						detail.setCountry( cou );
						Province province = null;
						if (cou == null || cou == Country.ES) {
							if (StringUtils.length(document) > 9) {
								detail.setDocument( StringUtils.substring(document, 0,9));
							} else {
								detail.setDocument(document);
							}
							ps1.setInt(1,registry);
							rs1 = ps1.executeQuery();
							if (rs1.next()) {
								String prov = rs1.getString(1);
								try {
									int p = Integer.parseInt(prov); 
									province = Province.values()[p];
								} catch (NumberFormatException e) {
									province = Province.DESCONOCIDO;
								} catch (ArrayIndexOutOfBoundsException e) {
									province = Province.DESCONOCIDO;
								}
							}
							rs1.close();
						} else {
							province = Province.NO_RESIDENTE;
							detail.setOperatorNif(cou.getValue() + document);
						}
						detail.setSheet("D");
						detail.setProvince( province );
						detail.setFirstQuarterAmount(0.0);
						detail.setSecondQuarterAmount(0.0);
						detail.setThirdQuarterAmount(0.0);
						detail.setFourthQuarterAmount(0.0);
						detail.setAmount(0.0);
						map.put(mapId, detail);
					}
					
					// Los empresarios profesionales que resulten ser sujetos pasivos por aplicación del 
					// artículo 84.Uno.2º de la Ley del IVA deberán consignar en el modelo 347 (declaración 
					// anual de operaciones con terceras personas) el importe de la contraprestación, 
					// IVA excluido, de las operaciones (compras) que deban ser incluidas en la referida declaración.
					double amount = !isp?(base + quota + surchargeQuota):base;
					
					if (!vatAccrualPayment) {
						c.setTime(date);
						int quarter = (c.get(Calendar.MONTH) / 3);
						if (quarter == 0) {
							detail.setFirstQuarterAmount(CommonUtil.round(detail.getFirstQuarterAmount() + amount));
						} else if (quarter == 1) {
							detail.setSecondQuarterAmount(CommonUtil.round(detail.getSecondQuarterAmount() + amount));
						} else if (quarter == 2) {
							detail.setThirdQuarterAmount(CommonUtil.round(detail.getThirdQuarterAmount() + amount));
						} else if (quarter == 3) {
							detail.setFourthQuarterAmount(CommonUtil.round(detail.getFourthQuarterAmount() + amount));
						}
					} else {
						if (!params.isPendingAccrualPayment()) {
							// Estamos en el año actual. Se buscan los vtos. pagados o devueltos.
							ps2.setInt(1,invoiceId);
							ps2.setDate(2, new java.sql.Date( CommonUtil.getYearFirstDay(year).getTime()));
							ps2.setDate(3, new java.sql.Date( CommonUtil.getYearLastDay(year).getTime()));
							rs2 = ps2.executeQuery();
							if (rs2.next()) {
								double financeAmount = rs2.getDouble(FINANCE_AMOUNT_ALIAS);
								detail.setVatAccrualAmount(CommonUtil.round(detail.getVatAccrualAmount() + financeAmount));
							}
							rs2.close();
						} else {
							// Estamos en el año anterior. Se buscan los vtos. pendientes.
							ps3.setInt(1,invoiceId);
							rs3 = ps3.executeQuery();
							if (rs3.next()) {
								double financeAmount = rs3.getDouble(FINANCE_AMOUNT_ALIAS);
								detail.setVatAccrualAmount(CommonUtil.round(detail.getVatAccrualAmount() + financeAmount));
								if (!detail.isPendingVatAccrual() && CommonUtil.round(financeAmount) != 0.0) {
									detail.setPendingVatAccrual( true );
								}
							}
							rs3.close();
							// Estamos en el año anterior. Se buscan los vtos. pagados o devueltos en el año de la declaración.
							ps2.setInt(1,invoiceId);
							ps2.setDate(2, new java.sql.Date( CommonUtil.getYearFirstDay(year+1).getTime()));
							ps2.setDate(3, new java.sql.Date( CommonUtil.getYearLastDay(year+1).getTime()));
							rs2 = ps2.executeQuery();
							if (rs2.next()) {
								double financeAmount = rs2.getDouble(FINANCE_AMOUNT_ALIAS);
								detail.setVatAccrualAmount(CommonUtil.round(detail.getVatAccrualAmount() + financeAmount));
								if (!detail.isPendingVatAccrual() && CommonUtil.round(financeAmount) != 0.0) {
									detail.setPendingVatAccrual( true );
								}
							}
							rs2.close();
						}
						
					}
					if (!params.isPendingAccrualPayment()) {
						detail.setAmount(CommonUtil.round(detail.getAmount() + amount));
					} else {
						detail.setPreviousAmount(CommonUtil.round(detail.getPreviousAmount() + amount));
					}
				}
			}
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs180);
			DatabaseUtil.closeQuietly(ps180);
			DatabaseUtil.closeQuietly(rs190);
			DatabaseUtil.closeQuietly(ps190);
			DatabaseUtil.closeQuietly(rs3);
			DatabaseUtil.closeQuietly(ps3);
			DatabaseUtil.closeQuietly(rs2);
			DatabaseUtil.closeQuietly(ps2);
			DatabaseUtil.closeQuietly(rs1);
			DatabaseUtil.closeQuietly(ps1);
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	private void appendConditions(Mod347Parameters params, StringBuilder buf) {
		if (params.isPendingAccrualPayment()) {
			buf.append(" AND (i.vat_accrual_payment = 1)");
		}
		buf.append(" AND ( (i.type = 1 AND ( (i.transaction IN (0,4) AND it.percentage != 0)");
		if (!params.isExcludeExports() ) {
			buf.append(" OR (i.transaction IN (2,3) AND i.service = 0)");
		}
		if (!params.isExcludeIntracommunitaryDeliveries() ) {
			buf.append(" OR (i.transaction = 1 AND i.service = 0)");
		}
		if (!params.isExcludeOutputExtracommunitaryServices() ) {
			buf.append(" OR (i.transaction IN (2,3) AND i.service = 1)");
		}
		if (!params.isExcludeOutputIntracommunitaryServices() ) {
			buf.append(" OR (i.transaction = 1 AND i.service = 1)");
		}
		if (!params.isExcludeOutputNationalZero() ) {
			buf.append(" OR (i.transaction IN (0,4) AND it.percentage = 0)");
		}
		buf.append(")) OR (i.type != 1 AND ( (i.transaction IN (0,4) AND it.percentage != 0)");
		if (!params.isExcludeImports() ) {
			buf.append("OR (i.transaction IN (2,3) AND i.service = 0)");
		}
        if (!params.isExcludeIntracommunitaryAdquisitions() ) {
        	buf.append("OR (i.transaction = 1 AND i.service = 0)");
		}
        if (!params.isExcludeInputExtracommunitaryServices() ) {
        	buf.append("OR ((i.transaction = 2 OR i.transaction = 3) AND i.service = 1)");
		}
        if (!params.isExcludeInputIntracommunitaryServices() ) {
        	buf.append("OR (i.transaction = 1 AND i.service = 1)");
		}
        if (!params.isExcludeInputNationalZero() ) {
        	buf.append("OR (i.transaction IN (0,4) AND it.percentage = 0)");
        }
        buf.append(")))");
	}
}





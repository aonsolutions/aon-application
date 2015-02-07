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
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.fiscal.enumeration.Mod347Type;
import com.code.aon.pool.AonConnectionException;

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
	private static final String SURCHARGE_QUOTA_ALIAS = "surcharge_quota";
	private static final String VAT_ACCRUAL_PAYMENT_ALIAS = "vat_accrual_payment";
	private static final String FINANCE_AMOUNT_ALIAS = "finance_amount";

	public Mod347 generateDetails(Mod347Parameters params) throws ManagerBeanException {
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT ELT(i.type+1, 'A', 'B', 'A') ").append(KEY_ALIAS)
			.append(",i.id ").append(INVOICE_ID_ALIAS)
			.append(",i.registry ").append(REGISTRY_ALIAS)
			.append(",i.rdocument ").append(DOCUMENT_ALIAS)
			.append(",i.rname ").append(NAME_ALIAS)
			.append(",i.issue_date ").append(ISSUE_DATE_ALIAS)
			.append(",i.tax_date ").append(TAX_DATE_ALIAS)
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
			.append(" AND i.tax_date BETWEEN ? AND ? ")
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
		try {
			conn = DatabaseUtil.getConnection(params.getDomainName());
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
					+" AND ft.type IN (1,2) ",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			Mod347 mod347 = params.getMod347();
			ps = conn.prepareStatement(buf.toString(),
				ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, DomainManager.getCurrentDomain());
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearFirstDay(mod347.getYear()).getTime()));
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearLastDay(mod347.getYear()).getTime()));
			Calendar c = Calendar.getInstance(); 
			rs = ps.executeQuery();
			Map<String,Mod347Detail> map = new TreeMap<String, Mod347Detail>();
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
				String mapId = key+";"+document+";"+vatAccrualPayment;
				Mod347Detail detail = map.get(mapId);
				if (detail == null) {
					detail = new Mod347Detail();
					detail.setMod347(mod347);
					detail.setType(key);
					detail.setDocument(document);
					detail.setName(name);
					detail.setVatAccrual(vatAccrualPayment);
					Country cou = null;
					if (StringUtils.isNotBlank(country)) {
						cou = Country.valueOf( country);	
					}
					detail.setCountry( cou );
					Province province = null;
					if (cou == Country.ES) {
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
				double amount = base + quota + surchargeQuota;
				if (!vatAccrualPayment) {
					c.setTime(date);
					int quarter = (c.get(Calendar.MONTH) % 3);
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
					ps2.setInt(1,invoiceId);
					rs2 = ps2.executeQuery();
					if (rs2.next()) {
						double financeAmount = rs2.getDouble(FINANCE_AMOUNT_ALIAS);
						detail.setVatAccrualAmount(CommonUtil.round(detail.getVatAccrualAmount() + financeAmount));
					}
					rs2.close();
					
				}
				detail.setAmount(CommonUtil.round(detail.getAmount() + amount));
			}
			IManagerBean bean = BeanManager.getManagerBean(Mod347Detail.class);
			for (Mod347Detail detail : map.values()) {
				if ( detail.getAmount() >= params.getMod347().getMinimumAmount() 
					|| detail.getAmount() <= (params.getMod347().getMinimumAmount()* (-1))) {
					bean.insert(detail);
				}
			}
			return mod347;	
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs2);
			DatabaseUtil.closeQuietly(ps2);
			DatabaseUtil.closeQuietly(rs1);
			DatabaseUtil.closeQuietly(ps1);
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}
/*
	public Mod347 OLDgenerateDetails(Mod347Parameters params) throws ManagerBeanException {
		Connection conn = null;
		PreparedStatement ps = null; 
		ResultSet rs = null;
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		try {
			Mod347 mod347 = params.getMod347();
			conn = DatabaseUtil.getConnection(params.getDomainName()); 
			ps1 = conn.prepareStatement(
					"SELECT geozone.code FROM raddress,geozone WHERE "
					+DomainManager.getSQLWhereClause("raddress.domain")
					+" AND raddress.geozone = geozone.id"
					+" AND raddress.registry = ? AND raddress.type = 0",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps = conn.prepareStatement(getSentence(params),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			getVatAccrualSelect(params);
			int i = 0;
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearFirstDay(mod347.getYear()).getTime()));
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearLastDay(mod347.getYear()).getTime()));
			ps.setDouble(++i, mod347.getMinimumAmount() );
			ps.setDouble(++i, CommonUtil.round( mod347.getMinimumAmount() * (-1)) );
			rs = ps.executeQuery();
			IManagerBean bean = BeanManager.getManagerBean(Mod347Detail.class);
			while (rs.next()) {
				Mod347Detail detail = new Mod347Detail();
				detail.setMod347(mod347);
				String type = rs.getString( KEY_ALIAS );
				detail.setType(Mod347Type.valueOf(type));
				detail.setDocument(StringUtils.upperCase(rs.getString( DOCUMENT_ALIAS)));
				if (StringUtils.length(detail.getDocument()) > 9) {
					detail.setDocument( StringUtils.substring(detail.getDocument(), 0,9));
				}
				int registry = rs.getInt( REGISTRY_ALIAS );
				detail.setRegistry(registry);
				String name = rs.getString(NAME_ALIAS);
				if (StringUtils.length(name) > 64) {
					name = StringUtils.substring(name, 0, 63);
				}
				detail.setName(StringUtils.upperCase( name ));
				
				String countryStr = rs.getString(COUNTRY_ALIAS);
				Country country = null;
				if (StringUtils.isNotBlank(countryStr)) {
					country = Country.valueOf( countryStr );	
				}
				detail.setCountry( country );

				Province province = null;
				if (country == Country.ES) {
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
				}
				detail.setSheet("D");
				detail.setProvince( province );				
				detail.setFirstQuarterAmount(CommonUtil.round(rs.getDouble(FIRST_QUARTER_ALIAS)));
				detail.setSecondQuarterAmount(CommonUtil.round(rs.getDouble(SECOND_QUARTER_ALIAS)));
				detail.setThirdQuarterAmount(CommonUtil.round(rs.getDouble(THIRD_QUARTER_ALIAS)));
				detail.setFourthQuarterAmount(CommonUtil.round(rs.getDouble(FOURTH_QUARTER_ALIAS)));
				detail.setAmount(CommonUtil.round(
							detail.getFirstQuarterAmount()
							+ detail.getSecondQuarterAmount()
							+detail.getThirdQuarterAmount()
							+detail.getFourthQuarterAmount()));
				bean.insert(detail);
			}
			// VAT ACCRUAL PAYMENT
			
			
			
			
			return mod347;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs1);
			DatabaseUtil.closeQuietly(ps1);
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}

	}

	private String getSentence(Mod347Parameters params) {
		String sumOp = " id.taxable_base + ( IF(it.quota=0, ROUND(it.percentage * id.taxable_base / 100,2) ,IF(it.quota is NULL,0,it.quota)) + IF( it.surcharge_quota=0, ROUND(it.surcharge * id.taxable_base / 100,2) ,IF(it.surcharge_quota is NULL,0,it.surcharge_quota)))";
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT ELT(i.type+1, 'A', 'B', 'A') ");
		buf.append(KEY_ALIAS);
		buf.append(",i.registry ");
		buf.append(REGISTRY_ALIAS);
		buf.append(",i.rdocument ");
		buf.append(DOCUMENT_ALIAS);
		buf.append(",MIN(i.rname) ");
		buf.append(NAME_ALIAS);
		buf.append(",MIN(r.nationality) ");
		buf.append(COUNTRY_ALIAS);
		buf.append(",SUM( " + sumOp + " ) ");
		buf.append(AMOUNT_ALIAS);
		buf.append(",SUM( IF(QUARTER(i.issue_date)=1,(" + sumOp + "),0)) ");
		buf.append(FIRST_QUARTER_ALIAS);
		buf.append(",SUM( IF(QUARTER(i.issue_date)=2,(" + sumOp + "),0)) ");
		buf.append(SECOND_QUARTER_ALIAS);
		buf.append(",SUM( IF(QUARTER(i.issue_date)=3,(" + sumOp + "),0)) ");
		buf.append(THIRD_QUARTER_ALIAS);
		buf.append(",SUM( IF(QUARTER(i.issue_date)=4,(" + sumOp + "),0)) ");
		buf.append(FOURTH_QUARTER_ALIAS);
		buf.append(" FROM invoice_detail id ");
		buf.append(" INNER JOIN invoice i ON (id.invoice = i.id) ");
		buf.append(" INNER JOIN registry r ON (r.id = i.registry) ");
		buf.append(" LEFT OUTER JOIN invoice_tax it ON it.invoice_detail = id.id ");
		buf.append(" WHERE i.id=i.id");
		buf.append(" AND " + DomainManager.getSQLWhereClause("i.domain"));
		buf.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") +" >= ?");
		buf.append(" AND " + (params.isTaxDateEnabled()?"i.tax_date":"i.issue_date") +" <= ?");
		buf.append(" AND i.vat_accrual_payment=0 ");
		buf.append(" AND it.tax_type=1 ");
		appendConditions(params,buf);
		buf.append(" HAVING amount > ? or amount < ?");
		buf.append(" ORDER BY key347,name,amount desc");
		return buf.toString();
	}
*/
	private void appendConditions(Mod347Parameters params, StringBuilder buf) {
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
/*
	private String getVatAccrualSelect(Mod347Parameters params) {
		String sumOp = " id.taxable_base + ( IF(it.quota=0, ROUND(it.percentage * id.taxable_base / 100,2) ,IF(it.quota is NULL,0,it.quota)) + IF( it.surcharge_quota=0, ROUND(it.surcharge * id.taxable_base / 100,2) ,IF(it.surcharge_quota is NULL,0,it.surcharge_quota)))";
		StringBuilder buf = new StringBuilder();
		buf.append("SELECT ELT(i.type+1, 'A', 'B', 'A') ");
		buf.append(KEY_ALIAS);
		buf.append(",i.registry ");
		buf.append(REGISTRY_ALIAS);
		buf.append(",i.rdocument ");
		buf.append(DOCUMENT_ALIAS);
		buf.append(",MIN(i.rname) ");
		buf.append(NAME_ALIAS);
		buf.append(",MIN(r.nationality) ");
		buf.append(COUNTRY_ALIAS);
		buf.append(",SUM( " + sumOp + " ) ");
		buf.append(AMOUNT_ALIAS);
		buf.append(	",SUM( IF(ft.type=1, ft.amount,  -ft.amount )) " + FINANCE_AMOUNT_ALIAS);
		buf.append("  FROM finance_tracking ft ");
		buf.append("  INNER JOIN finance f ON (ft.finance = f.id) ");
		buf.append("  INNER JOIN invoice i ON (f.invoice = i.id AND vat_accrual_payment = 1) ");
		buf.append("  INNER JOIN invoice_detail id ON (id.invoice = i.id) ");
		buf.append("  INNER JOIN invoice_tax it ON (it.invoice_detail = id.id) ");
		buf.append(" WHERE ft.domain = ?");
		buf.append(	" AND ft.tracking_date >= ?");
		buf.append(	" AND ft.tracking_date <= ?");
		buf.append(	" AND ft.type IN (1,2) ");
		buf.append(	" AND it.tax_type = 1");
		buf.append(	" AND i.vat_accrual_payment = 1");	// Criterio de Caja.
		appendConditions(params,buf);
		buf.append(" HAVING amount > ? or amount < ?");
		buf.append(" ORDER BY key347,name,amount desc");
		System.out.println(buf.toString());
		return buf.toString();		
	}
*/
}





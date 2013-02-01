package com.code.aon.fiscal.mod347;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.Country;
import com.code.aon.common.enumeration.Province;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.Mod347;
import com.code.aon.fiscal.Mod347Detail;
import com.code.aon.fiscal.enumeration.Mod347Type;

public class Mod347Manager {

	private static final String KEY_ALIAS = "key347";
	private static final String REGISTRY_ALIAS = "registry";
	private static final String DOCUMENT_ALIAS = "document";
	private static final String NAME_ALIAS = "name";
	private static final String COUNTRY_ALIAS = "country";
	private static final String AMOUNT_ALIAS = "amount";
	private static final String FIRST_QUARTER_ALIAS = "firstQuarter";
	private static final String SECOND_QUARTER_ALIAS = "secondQuarter";
	private static final String THIRD_QUARTER_ALIAS = "thirdQuarter";
	private static final String FOURTH_QUARTER_ALIAS = "fourthQuarter";

	public Mod347 generateDetails(Mod347Parameters params) throws ManagerBeanException {
		PreparedStatement ps = null; 
		ResultSet rs = null;
		
		PreparedStatement ps1 = null;
		ResultSet rs1 = null;
		
		try {
			Mod347 mod347 = params.getMod347();
			String sessionName = HibernateUtil.getSessionFactoryName(Mod347Detail.class.getName());
			ps1 = HibernateUtil.getSQLConnection(sessionName).prepareStatement(
					"SELECT geozone.code FROM raddress,geozone WHERE raddress.registry = ? AND raddress.type = 0",
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(getSentence(params),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
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
				detail.setDocument(rs.getString( DOCUMENT_ALIAS ));
				if (StringUtils.length(detail.getDocument()) > 9) {
					detail.setDocument( StringUtils.substring(detail.getDocument(), 0,9));
				}
				int registry = rs.getInt( REGISTRY_ALIAS );
				detail.setRegistry(registry);
				detail.setName(rs.getString(NAME_ALIAS));
				Country country = Country.valueOf( rs.getString(COUNTRY_ALIAS) ); 
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
				detail.setProvince( province );				
				detail.setAmount(CommonUtil.round(rs.getDouble(AMOUNT_ALIAS)));
				detail.setFirstQuarterAmount(CommonUtil.round(rs.getDouble(FIRST_QUARTER_ALIAS)));
				detail.setSecondQuarterAmount(CommonUtil.round(rs.getDouble(SECOND_QUARTER_ALIAS)));
				detail.setThirdQuarterAmount(CommonUtil.round(rs.getDouble(THIRD_QUARTER_ALIAS)));
				detail.setFourthQuarterAmount(CommonUtil.round(rs.getDouble(FOURTH_QUARTER_ALIAS)));
				bean.insert(detail);
			}
			return mod347;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			if (rs1 != null) {
				try {
					rs1.close();
				} catch (SQLException e) {
				}
			}
			if (ps1 != null) {
				try {
					ps1.close();
				} catch (SQLException e) {
				}
			}
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

	private String getSentence(Mod347Parameters params) {
		String sumOp = " id.taxable_base + ( IF(it.quota=0, ROUND(it.percentage * id.taxable_base / 100,2) ,IF(it.quota is NULL,0,it.quota)) + IF( it.surcharge_quota=0, ROUND(it.surcharge * id.taxable_base / 100,2) ,IF(it.surcharge_quota is NULL,0,it.surcharge_quota)))";
		StringBuffer buf = new StringBuffer();
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
		buf.append(" AND it.tax_type=1 ");
		
		// GRUPO DE VENTAS
		buf.append(" AND ( (i.type = 1 AND ( (i.transaction = 0 AND it.percentage != 0)");
		if (!params.isExcludeExports() ) {
			buf.append(" OR ((i.transaction = 2 OR i.transaction = 3) AND i.service = 0)");
		}
		if (!params.isExcludeIntracommunitaryDeliveries() ) {
			buf.append(" OR (i.transaction = 1 AND i.service = 0)");
		}
		if (!params.isExcludeOutputExtracommunitaryServices() ) {
			buf.append(" OR ((i.transaction = 2 OR i.transaction = 3) AND i.service = 1)");
		}
		if (!params.isExcludeOutputIntracommunitaryServices() ) {
			buf.append(" OR (i.transaction = 1 AND i.service = 1)");
		}
		if (!params.isExcludeOutputNationalZero() ) {
			buf.append(" OR (i.transaction = 0 AND it.percentage = 0)");
		}
		buf.append(")) OR (i.type != 1 AND (i.transaction = 0 AND it.percentage != 0)");
		if (!params.isExcludeImports() ) {
			buf.append("OR ((i.transaction = 2 OR i.transaction = 3) AND i.service = 0)");
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
        	buf.append("OR (i.transaction = 0 AND it.percentage = 0)");
        }
        buf.append("))");
        
		buf.append(" GROUP BY  key347");
		if (!params.isGroupedByNIF() ) {
			buf.append(",i.registry");
		}
		buf.append(",i.rdocument");
		buf.append(" HAVING amount > ? or amount < ?");
		buf.append(" ORDER BY key347,name,amount desc");
		
		System.out.println(buf.toString());
		
		return buf.toString();
	}

}





package com.code.aon.fiscal.mod347;



import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.enumeration.Country;
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
	private static final String PROVINCE_ALIAS = "province";
	private static final String AMOUNT_ALIAS = "amount";

	private static final String STMT =
		"SELECT ELT(i.type+1, 'A', 'B', 'A') "		+ KEY_ALIAS
		+ ",i.registry " 	 						+ REGISTRY_ALIAS
		+ ",i.rdocument "  							+ DOCUMENT_ALIAS
		+ ",MIN(i.rname) "	 						+ NAME_ALIAS
		+ ",MIN(r.nationality) "					+ COUNTRY_ALIAS
		+ ",IF(giz.id IS NOT null,giz.id,IF(gz.id IS NOT null,gz.id,gz2.id)) " 	+ PROVINCE_ALIAS
		+",SUM( id.taxable_base + ( IF(it.quota=0, ROUND(it.percentage * id.taxable_base / 100,2) ,IF(it.quota is NULL,0,it.quota)) + IF( it.surcharge_quota=0, ROUND(it.surcharge * id.taxable_base / 100,2) ,IF(it.surcharge_quota is NULL,0,it.surcharge_quota) ))) " + AMOUNT_ALIAS
		+" FROM invoice_detail id "
		+" INNER JOIN invoice i ON (id.invoice = i.id) "
		+" INNER JOIN registry r ON (r.id = i.registry) "
		+" LEFT OUTER JOIN invoice_tax it ON it.invoice_detail = id.id "
		
		// PRIORIDAD 1. Buscamos la provincia en las direcciones de la factura. 
		+" LEFT OUTER JOIN invoice_address ia ON ia.invoice = i.id "
		+" LEFT OUTER JOIN geozone giz ON ia.geozone = giz.id "

		// PRIORIDAD 2. Buscamos la provincia en la direccion de raddress asignada a la factura.		
		+" LEFT OUTER JOIN raddress ra ON ra.id = i.raddress  "
		+" LEFT OUTER JOIN geozone gz ON ra.geozone = gz.id "

		// PRIORIDAD 3. Buscamos la provincia en la direccion principal de raddress.		
		+" LEFT OUTER JOIN raddress ra2 ON ra2.registry = i.registry AND ra2.type = 0 "
		+" LEFT OUTER JOIN geozone gz2 ON ra2.geozone = gz2.id "
		
		+" WHERE i.id=i.id"
		+" AND it.tax_type=1 "
		+" AND i.issue_date >= ?"
		+" AND i.issue_date <= ?"
		+" GROUP BY  key347,i.rdocument,i.registry,province "
		+" HAVING amount > ? "
		+" ORDER BY key347,name,amount desc";
	
	public Mod347 generateDetails(Mod347 mod347) throws ManagerBeanException {
		PreparedStatement ps = null; 
		ResultSet rs = null;
		try {
			String sessionName = HibernateUtil.getSessionFactoryName(Mod347Detail.class.getName());
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(STMT,
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearFirstDay(mod347.getYear()).getTime()));
			ps.setDate(++i, new java.sql.Date( CommonUtil.getYearLastDay(mod347.getYear()).getTime()));
			ps.setDouble(++i, mod347.getMinimumAmount() );
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
				detail.setRegistry(rs.getInt( REGISTRY_ALIAS ));
				detail.setName(rs.getString(NAME_ALIAS));
				Country country = Country.valueOf( rs.getString(COUNTRY_ALIAS) ); 
				detail.setCountry( country );
				detail.setProvince( rs.getInt( PROVINCE_ALIAS ) );
				detail.setAmount(CommonUtil.round(rs.getDouble(AMOUNT_ALIAS)));
				bean.insert(detail);
			}
			return mod347;
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

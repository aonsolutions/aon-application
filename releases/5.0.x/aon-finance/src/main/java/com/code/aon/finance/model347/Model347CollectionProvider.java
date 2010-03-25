package com.code.aon.finance.model347;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.finance.enumeration.Model347ReportOrder;
import com.code.aon.finance.enumeration.Model347Type;
import com.code.aon.registry.RegistryDocument;

public class Model347CollectionProvider {

	public List<Model347> getList(Model347Parameters params) throws ManagerBeanException {
		PreparedStatement ps = null; 
		ResultSet rs = null;
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT ELT(i.type+1, 'A', 'B', 'A') key347 ");
			stmt.append(",i.registry ");
			stmt.append(",i.rdocument ");
			stmt.append(",i.rname ");
			stmt.append(",IF(giz.id IS NOT null,giz.id,gz.id) geozone ");
			stmt.append(",IF(giz.name IS NOT null,giz.name,gz.name) geozoneName ");
			stmt.append(",IF(giz2.id IS NOT null,giz2.id,gz2.id) country ");
			stmt.append(",IF(giz2.name IS NOT null,giz2.name,gz2.name) countryName ");
			stmt.append(",SUM( ");
			stmt.append("id.taxable_base ");
			stmt.append("+ ( ");
			stmt.append("IF(it.quota=0, ");
			stmt.append("ROUND(it.percentage * id.taxable_base / 100,2) ");
			stmt.append(",IF(it.quota is NULL,0,it.quota)) ");
			stmt.append("+ ");
			stmt.append("IF( ");
			stmt.append("it.surcharge_quota=0, ");
			stmt.append("ROUND(it.surcharge * id.taxable_base / 100,2) ");
			stmt.append(",IF(it.surcharge_quota is NULL,0,it.surcharge_quota) ");
			stmt.append(") ");
			stmt.append(") * IF(it.tax_type=2,-1,1) ");
			stmt.append(") total ");
			stmt.append("FROM invoice_detail id ");
			stmt.append("INNER JOIN invoice i ON (id.invoice = i.id) ");
			stmt.append("LEFT OUTER JOIN invoice_tax it ON it.invoice_detail = id.id ");
			stmt.append("LEFT OUTER JOIN invoice_address ia ON ia.invoice = i.id ");
			stmt.append("LEFT OUTER JOIN geozone giz ON ia.geozone = giz.id ");
			stmt.append("LEFT OUTER JOIN geotree git ON giz.id = git.child ");
			stmt.append("LEFT OUTER JOIN geozone giz2 ON git.parent = giz2.id ");
			stmt.append("LEFT OUTER JOIN raddress ra ON ra.registry = i.registry AND ra.type = 0 ");
			stmt.append("LEFT OUTER JOIN geozone gz ON ra.geozone = gz.id ");
			stmt.append("LEFT OUTER JOIN geotree gt ON gz.id = gt.child ");
			stmt.append("LEFT OUTER JOIN geozone gz2 ON gt.parent = gz2.id ");
			stmt.append("WHERE i.id=i.id");
			if (params.getType() == Model347Type.A_KEY ) {
				stmt.append(" AND i.type = 1");			
			}
			if (params.getType() == Model347Type.B_KEY ) {
				stmt.append(" AND (i.type = 0 OR i.type = 2)");
			}
			if (params.getFromDate() != null) {
				stmt.append(" AND i.issue_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append("AND i.issue_date <= ?");	
			}
			stmt.append("GROUP BY  key347,i.rdocument,i.registry,i.rname,geozone,geozoneName,country,countryName ");
			stmt.append("having total > ? ");
			if (params.getOrder() == null) {
				stmt.append(" ORDER BY key347,total desc");
			} else if (params.getOrder() == Model347ReportOrder.INVOICE_REGISTRY_DOCUMENT) {
				stmt.append(" ORDER BY key347,i.rdocument,total desc");
			} else if (params.getOrder() == Model347ReportOrder.INVOICE_REGISTRY_ID) {
				stmt.append(" ORDER BY key347,i.registry,total desc");
			} else if (params.getOrder() == Model347ReportOrder.INVOICE_REGISTRY_NAME) {
				stmt.append("ORDER BY key347,i.rname,total desc");
			}else if (params.getOrder() == Model347ReportOrder.INVOICE_TOTAL_AMOUNT) {
				stmt.append("ORDER BY key347,total desc");
			}
		
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getFromDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getFromDate().getTime()));
			}
			if (params.getToDate() != null) {
				ps.setDate(++i, new java.sql.Date( params.getToDate().getTime()));
			}
			ps.setDouble(++i, params.getMinimunAmount());
			rs = ps.executeQuery();
			List<Model347> m347s = new LinkedList<Model347>();
			while (rs.next()) {
				Model347 m347 = new Model347();
				String type = rs.getString(1);
				if ("A".equals(type)) {
					m347.setType(Model347Type.A_KEY);	
				} else if ("B".equals(type)) {
					m347.setType(Model347Type.B_KEY);
				}
				m347.setId(rs.getInt(2));
				RegistryDocument rd = new RegistryDocument( rs.getString(3) );
				m347.setDocument(rd);
				m347.setName(rs.getString(4));
				m347.setGeozone(rs.getInt(5));
				m347.setGeozoneName(rs.getString(6));
				m347.setCountry(rs.getInt(7));
				m347.setCountryName(rs.getString(8));
				m347.setTotal(rs.getDouble(9));
				m347s.add(m347);
			}
			return m347s;
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

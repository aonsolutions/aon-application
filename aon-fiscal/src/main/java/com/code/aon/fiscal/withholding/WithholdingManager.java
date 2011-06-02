package com.code.aon.fiscal.withholding;


import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;

public class WithholdingManager {
	
	public List<?> getList(WithholdingParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			Date dateFrom = params.getPeriod().getStartDate(params.getYear());
			Date dateTo = params.getPeriod().getDueDate(params.getYear());
			
			StringWriter stmt = new StringWriter();
			stmt.append(" SELECT COUNT(DISTINCT pr.document)");
			stmt.append(",r.id");
			stmt.append(",r.name");
			stmt.append(",ROUND(SUM(pr.taxable_base),2)");
			stmt.append(",ROUND(SUM(pr.quota),2)");
			stmt.append(" FROM fs_prof_retention pr");
			stmt.append(" INNER JOIN registry r ON (r.id = pr.enterprise)");
			stmt.append(" WHERE 1=1" );
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				stmt.append(" AND pr.enterprise=" + params.getEnterprise().getId() );	
			}
			if (dateFrom != null) {
				stmt.append(" AND pr.payment_date>=?");	
			}
			if (dateTo != null) {
				stmt.append(" AND pr.payment_date<=?");	
			}
			stmt.append(" GROUP BY pr.enterprise,r .name");
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (dateFrom != null) {
				ps.setDate(++i, new java.sql.Date(dateFrom.getTime()));	
			}
			if (dateTo != null) {
				ps.setDate(++i, new java.sql.Date(dateTo.getTime()));	
			}
			i = 0;
			rs = ps.executeQuery();
			List<EnterpriseWithholding> list = new LinkedList<EnterpriseWithholding>();
			while (rs.next()) {
				EnterpriseWithholding ew = new EnterpriseWithholding();
				ew.setCount(rs.getInt(1));
				ew.setEnterpriseId(rs.getInt(2));
				ew.setEnterprise(rs.getString(3));
				ew.setTaxableBase(rs.getDouble(4));
				ew.setQuota(rs.getDouble(5));
				list.add(ew);
			}
			return list;
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

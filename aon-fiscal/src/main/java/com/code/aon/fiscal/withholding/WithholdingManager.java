package com.code.aon.fiscal.withholding;


import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class WithholdingManager {
	
	public List<?> getList(WithholdingParameters params) throws ManagerBeanException {
		PreparedStatement salaryPs = null;
		ResultSet salaryRs = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			Date dateFrom = params.getPeriod().getStartDate(params.getYear());
			Date dateTo = params.getPeriod().getDueDate(params.getYear());
			
			StringWriter salary = new StringWriter();
			salary.append(" SELECT COUNT(DISTINCT s.employee_document),ROUND(SUM(s.irpf_base)),ROUND(SUM(sd.amount))");
			salary.append(" FROM salary_deduction sd");
			salary.append(" INNER JOIN salary s ON sd.salary = s.id");
			salary.append(" INNER JOIN contract c ON s.contract = c.id");
			salary.append(" INNER JOIN workplace w ON c.workplace = w.id");
			salary.append(" WHERE sd.deduction_concept = 'IRPF'");
			salary.append(" AND w.enterprise = ?");
			if (dateFrom != null) {
				salary.append(" AND s.issue_date>=?");	
			}
			if (dateTo != null) {
				salary.append(" AND s.issue_date<=?");	
			}
			String sessionName = HibernateUtil.getSessionFactoryName();
			salaryPs  = HibernateUtil.getSQLConnection(sessionName).prepareStatement(salary.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			
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
				salaryPs.setInt(1, ew.getEnterpriseId());
				int x = 1;
				if (dateFrom != null) {
					salaryPs.setDate(++x, new java.sql.Date(dateFrom.getTime()));	
				}
				if (dateTo != null) {
					salaryPs.setDate(++x, new java.sql.Date(dateTo.getTime()));	
				}
				salaryRs = salaryPs.executeQuery();
				if (salaryRs.next()) {
					ew.setWorkCount(salaryRs.getInt(1));
					ew.setWorkTaxableBase(salaryRs.getDouble(2));
					ew.setWorkQuota(salaryRs.getDouble(3));
					ew.setCount(ew.getCount() + ew.getWorkCount());
					ew.setTaxableBase(ew.getTaxableBase() + ew.getWorkTaxableBase());
					ew.setQuota(ew.getQuota() + ew.getWorkQuota());
				}
				salaryRs.close();
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
			if (salaryRs != null) {
				try {
					salaryRs.close();
				} catch (SQLException e) {
				}
			}
			if (salaryPs != null) {
				try {
					salaryPs.close();
				} catch (SQLException e) {
				}
			}
		}
	}
	
}

package com.code.aon.fiscal.withholding;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;

public class WithholdingManager {
	
	public List<?> getList(WithholdingParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			Date dateFrom = params.getPeriod().getStartDate(params.getYear());
			Date dateTo = params.getPeriod().getDueDate(params.getYear());
			
			StringBuffer select = new StringBuffer();
			select.append(" SELECT entID,MAX(entName),COUNT( DISTINCT doc),SUM(base),sum(quota)");
			select.append(" FROM (");
			select.append(" SELECT pr.enterprise entID,r.name entName,pr.document doc, ROUND(SUM(pr.taxable_base),2) base,ROUND(SUM(pr.quota),2)  quota");
			select.append(" FROM fs_prof_retention pr");
			select.append(" INNER JOIN registry r ON (r.id = pr.enterprise)");
			select.append(" WHERE " + DomainManager.getSQLWhereClause("pr.domain"));
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				select.append(" AND pr.enterprise = ? " );	
			}
			if (dateFrom != null) {
				select.append(" AND pr.payment_date>=?");	
			}
			if (dateTo != null) {
				select.append(" AND pr.payment_date<=?");	
			}
			select.append(" GROUP BY pr.enterprise,pr.document");
			select.append(" UNION");
			select.append(" SELECT w.enterprise entID,s.enterprise_name entName,s.employee_document doc,ROUND(SUM(s.irpf_base)) base,ROUND(SUM(sd.amount)) quota");
			select.append(" FROM salary_deduction sd");
			select.append(" INNER JOIN salary s ON sd.salary = s.id");
			select.append(" INNER JOIN contract c ON s.contract = c.id");
			select.append(" INNER JOIN workplace w ON c.workplace = w.id");
			select.append(" WHERE " + DomainManager.getSQLWhereClause("sd.domain"));
			select.append(" AND sd.deduction_concept = 'IRPF'");
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				select.append(" AND w.enterprise = ? " );	
			}
			if (dateFrom != null) {
				select.append(" AND s.issue_date>=?");	
			}
			if (dateTo != null) {
				select.append(" AND s.issue_date<=?");	
			}
			select.append(" GROUP BY w.enterprise,s.employee_document");
			select.append(" ) SLC");
			select.append(" GROUP BY SLC.entID");
			String sessionName = HibernateUtil.getSessionFactoryName(); 
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(select.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				ps.setInt(++i,params.getEnterprise().getId());
			}
			if (dateFrom != null) {
				ps.setDate(++i, new java.sql.Date(dateFrom.getTime()));	
			}
			if (dateTo != null) {
				ps.setDate(++i, new java.sql.Date(dateTo.getTime()));	
			}
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				ps.setInt(++i,params.getEnterprise().getId());
			}
			if (dateFrom != null) {
				ps.setDate(++i, new java.sql.Date(dateFrom.getTime()));	
			}
			if (dateTo != null) {
				ps.setDate(++i, new java.sql.Date(dateTo.getTime()));	
			}
			rs = ps.executeQuery();
			List<EnterpriseWithholding> list = new LinkedList<EnterpriseWithholding>();
			while (rs.next()) {
				EnterpriseWithholding ew = new EnterpriseWithholding();
				ew.setEnterpriseId(rs.getInt(1));
				ew.setEnterprise(rs.getString(2));
				ew.setCount(rs.getInt(3));
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
	
	public Model111 getModel111(WithholdingParameters params) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			
			Date dateFrom = params.getPeriod().getStartDate(params.getYear());
			Date dateTo = params.getPeriod().getDueDate(params.getYear());
			
			StringBuffer select = new StringBuffer();
			select.append(" SELECT ");
			select.append(" CASE");
				//I. Rendimientos del Trabajo
				select.append("	WHEN clave IN ('A','B','C','D','E','F') THEN (1 + inKind)");
				//II. Rendimientos de Actividades económicas
				select.append("	WHEN clave IN ('G','H','I') THEN (3 + inKind)");
				//III. Premios por la participación en juegos, concursos, rifas o combinaciones aleatorias.
				select.append("	WHEN clave = 'K' and subclave = 'K01' THEN (5 + inKind)");
				//IV. Ganancias patrimoniales derivadas de los aprovechamientos forestales de 
				//	  los vecinos en montes públicos.
				select.append("	WHEN clave = 'K' and subclave = 'K02' THEN (7 + inKind)");
				//V. Contraprestaciones por la cesión de derechos de imagen.
				select.append("	WHEN clave = 'J' THEN (9 + inKind)");
			select.append("	ELSE null");
			select.append(" END fila");
			select.append(" ,COUNT( DISTINCT doc),SUM(base),sum(quota)"); 
			select.append(" FROM ( ");
			select.append(" SELECT pr.withholding_key clave,pr.withholding_subkey subclave,pr.in_kind inKind");
			select.append("	,pr.document doc");
			select.append("	,ROUND(SUM(pr.taxable_base),2) base,ROUND(SUM(pr.quota),2)  quota"); 
			select.append(" FROM fs_prof_retention pr ");
			select.append(" WHERE " + DomainManager.getSQLWhereClause("pr.domain"));
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				select.append(" AND pr.enterprise = ? " );	
			}
			if (dateFrom != null) {
				select.append(" AND pr.payment_date>=?");	
			}
			if (dateTo != null) {
				select.append(" AND pr.payment_date<=?");	
			}
			select.append(" GROUP BY clave,subclave,doc,inKind");
			select.append(" UNION ");
			select.append(" SELECT 'A' clave,null subclave,0 inKind");
			select.append(" 	,s.employee_document doc");
			select.append(" 	,ROUND(SUM(s.irpf_base)) base,ROUND(SUM(sd.amount)) quota"); 
			select.append(" FROM salary_deduction sd ");
			select.append(" INNER JOIN salary s ON sd.salary = s.id"); 
			select.append(" INNER JOIN contract c ON s.contract = c.id ");
			select.append(" INNER JOIN workplace w ON c.workplace = w.id ");
			select.append(" WHERE " + DomainManager.getSQLWhereClause("sd.domain"));
			select.append(" AND sd.deduction_concept = 'IRPF' ");
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				select.append(" AND w.enterprise = ? " );	
			}
			if (dateFrom != null) {
				select.append(" AND s.issue_date>=?");	
			}
			if (dateTo != null) {
				select.append(" AND s.issue_date<=?");	
			}
			select.append(" GROUP BY clave,subclave,doc,inKind");
			select.append(" ) SLC GROUP BY fila");
			String sessionName = HibernateUtil.getSessionFactoryName(); 
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(select.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			Model111 model = new Model111();			
			int i = 0;
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				ps.setInt(++i,params.getEnterprise().getId());
			}
			if (dateFrom != null) {
				ps.setDate(++i, new java.sql.Date(dateFrom.getTime()));	
			}
			if (dateTo != null) {
				ps.setDate(++i, new java.sql.Date(dateTo.getTime()));	
			}
			if (params.getEnterprise() != null && params.getEnterprise().getId() != null) {
				ps.setInt(++i,params.getEnterprise().getId());
			}
			if (dateFrom != null) {
				ps.setDate(++i, new java.sql.Date(dateFrom.getTime()));	
			}
			if (dateTo != null) {
				ps.setDate(++i, new java.sql.Date(dateTo.getTime()));	
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				Integer row = rs.getInt(1);
				ModelDetail md = new ModelDetail();
				md.setCount(rs.getInt(2));
				md.setAmount(rs.getDouble(3));
				md.setQuota(rs.getDouble(4));
				model.getMap().put(row, md);
			}
			return model;
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

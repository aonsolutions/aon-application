package com.code.aon.fiscal.renting;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.Renting;
import com.code.aon.fiscal.RentingDetail;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.RentingDetailKind;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;

public class RentingProvider {

	public void initializeRentingDetail(Renting renting) throws ManagerBeanException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, renting.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		Date dateFrom = c.getTime();	
		Date dateTo = renting.getPeriod().getDueDate(renting.getYear());
		PreparedStatement ps = null;
		ResultSet rs = null;
		try {
			IManagerBean bean = BeanManager.getManagerBean(RentingDetail.class);
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT i.type,it.percentage,i.rdocument,i.rname,");
			stmt.append(" SUM( id.taxable_base),");
			stmt.append(" SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) RET ");
			stmt.append(" FROM invoice_tax it ");
			stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("i.domain"));
			stmt.append(" AND i.type != 1 "); // No Ventas
			stmt.append(" AND it.tax_type = 2"); // IRPF
			stmt.append(" AND it.withholding_type = 1"); // IRPF de alquileres
			stmt.append(" AND i.tax_date >= ?");
			stmt.append(" AND i.tax_date <= ?");
			stmt.append(" GROUP BY i.type,it.percentage,i.rdocument,i.rname");
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			List<RentingDetail> details = new LinkedList<RentingDetail>();
			while (rs.next()) {
				RentingDetail detail = new RentingDetail();
				detail.setRenting(renting);
				detail.setPaidReturns(rs.getDouble(5));
				detail.setAccountDeposit(rs.getDouble(6));
				detail.setPercent(rs.getDouble(2));
				detail.setDocument( rs.getString(3) );
				detail.setName( rs.getString(4) );
				detail.setType(RentingDetailKind.MONEY);
				detail.setAccrualPeriod(renting.getYear());
				details.add(detail);
			}
			for (RentingDetail detail: details) {
				bean.insert(detail);				
			}
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

	public Renting initializeRenting(Renting renting) throws ManagerBeanException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, renting.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		Date dateFrom = c.getTime();	
		Date dateTo = renting.getPeriod().getDueDate(renting.getYear());
		PreparedStatement ps = null;
		ResultSet rs = null;
		List<String> lessorDocuments = new ArrayList<String>();
		try {
			StringWriter stmt = new StringWriter();
			stmt.append("SELECT i.type,it.percentage,rdocument,i.rname,");
			stmt.append(" SUM( id.taxable_base),");
			stmt.append(" SUM( IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) ) ) RET ");
			stmt.append(" FROM invoice_tax it ");
			stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
			stmt.append(" WHERE " + DomainManager.getSQLWhereClause("i.domain"));
			stmt.append(" AND i.type != 1 "); // No Ventas
			stmt.append(" AND it.tax_type = 2"); // IRPF
			stmt.append(" AND it.withholding_type = 1"); // IRPF de alquileres
			stmt.append(" AND i.tax_date >= ?");
			stmt.append(" AND i.tax_date <= ?");
			stmt.append(" GROUP BY i.type,it.percentage,i.rdocument,i.rname");
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			renting.initializeAccumulatedAmounts();
			while (rs.next()) {
				double rentingAmount = rs.getDouble(5);
				double retention = rs.getDouble(6);
				String document = rs.getString(3);
				if (!lessorDocuments.contains(document) ) {
					lessorDocuments.add(document);
					renting.setLessorCountAccumulated( CommonUtil.round(renting.getLessorCountAccumulated() + 1 ) );	
				}
				renting.setRentingAmountAccumulated(CommonUtil.round(renting.getRentingAmountAccumulated() + rentingAmount ) );
				renting.setRetentionAccumulated( CommonUtil.round(renting.getRetentionAccumulated() + retention ) );
			}
			return renting;
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

	public void fillDeclared(Renting renting) throws ManagerBeanException {
		// Primera declaración del ejercicio, si no es complementaria, 
		// no se debe tener en cuenta lo almacenado en ese periodo.
		if ((renting.getPeriod() == Period.M01 || renting.getPeriod() == Period.T1) && !renting.isComplementary()) {
			return;  
		}
		IManagerBean bean = BeanManager.getManagerBean(Renting.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.RENTING_YEAR), renting.getYear());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.RENTING_ADMINISTRATION), renting.getAdministration());
		String periodAlias = bean.getFieldName(IEntityAlias.RENTING_PERIOD);

		int i = renting.getPeriod().ordinal(); 
		if (i == 0 ) {
			criteria.addEqualExpression(periodAlias, Period.M01);  // ENERO y COMPLENTARIA
		}
		if (i == 12 ) {
			criteria.addEqualExpression(periodAlias, Period.T1); // 1 TRIMESTRE y COMPLENTARIA
		}
		if (i >0 && i<12) {
			criteria.addBetweenExpression(periodAlias, Period.M01, Period.values()[renting.getPeriod().ordinal() -1 ]);
		}
		if (i >12 && i<16) {
			criteria.addBetweenExpression(periodAlias, Period.T1, Period.values()[renting.getPeriod().ordinal() -1 ]);
		}
		// Si params.getPeriod() == Period.YEAR Se saca todo lo del ejercicio, o sea, no se añaden filtros.
		if (renting.isExtraDeclaration() && renting.getPeriod() == Period.YEAR) {
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(periodAlias, Period.YEAR));
		}
		
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			Renting prev = (Renting) to;
			renting.setLessorCountDeclared( CommonUtil.round( renting.getLessorCountDeclared() + prev.getLessorCount() ));
			renting.setRentingAmountDeclared( CommonUtil.round( renting.getRentingAmountDeclared() + prev.getRentingAmount() ));
			renting.setRetentionDeclared( CommonUtil.round( renting.getRetentionDeclared() + prev.getRetention() ));
			renting.setLessorCountInKindDeclared( CommonUtil.round( renting.getLessorCountInKindDeclared() + prev.getLessorCountInKind() ));
			renting.setRemunerationInKindDeclared( CommonUtil.round( renting.getRemunerationInKindDeclared() + prev.getRemunerationInKind() ));
			renting.setAccountDepositDeclared( CommonUtil.round( renting.getAccountDepositDeclared() + prev.getAccountDeposit() ));
		}
	}


	
}

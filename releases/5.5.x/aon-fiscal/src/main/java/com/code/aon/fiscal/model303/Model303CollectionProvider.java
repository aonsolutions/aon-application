package com.code.aon.fiscal.model303;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.enumeration.Model303Column;
import com.code.aon.fiscal.enumeration.Model303Key;

public class Model303CollectionProvider {

	public List<Model303> getModel303(Model303Parameters params) throws ManagerBeanException {
		Map<Model303KeyEx,Model303> m303s = new Hashtable<Model303KeyEx,Model303>();
		PreparedStatement ps = null;
		ResultSet rs = null;
		Model303Column column = Model303Column.ACUMULADO; 
		try {
			StringWriter stmt = new StringWriter();
			String quotaStmt = "IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) )";
			stmt.append("SELECT i.type,it.percentage,it.surcharge,i.transaction,i.investment,");
			stmt.append(" SUM( id.taxable_base),");
			stmt.append(" SUM( ");
			stmt.append(quotaStmt);
			stmt.append(" ) IVA,");
			stmt.append(" SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(id.taxable_base * it.surcharge / 100, 2) ) ) RE,");
			stmt.append(" SUM( IF(it.deductible_quota != 0,it.deductible_quota,");
			stmt.append(quotaStmt);
			stmt.append(")) ");
			stmt.append(" FROM invoice_tax it ");
			stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
			stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
			stmt.append(" WHERE it.tax_type = 1");
			if (params.getFromDate() != null) {
				stmt.append(" AND i.tax_date >= ?");
			}
			if (params.getToDate() != null) {
				stmt.append(" AND i.tax_date <= ?");
			}
			if (params.getSecurityLevel() != null) {
				stmt.append(" AND i.security_level = " + params.getSecurityLevel().ordinal());
			}
			stmt.append(" GROUP BY i.type,it.percentage,it.surcharge,i.transaction,i.investment");
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
			rs = ps.executeQuery();
			initializeMap(m303s);
			while (rs.next()) {
				InvoiceType invoiceType = InvoiceType.values()[rs.getInt(1)];
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(4)];
				boolean investment = rs.getBoolean(5);
				double percent = rs.getDouble(2);
				Model303KeyEx[] keyExs = obtainModel303Type(invoiceType,transaction,investment,percent);
				double surchargePercent = rs.getDouble(3);
				double taxableBase = rs.getDouble(6);
				double quota = rs.getDouble(7);
				double surchargeQuota = rs.getDouble(8);
				double deductibleQuota = rs.getDouble(9);
				if (keyExs != null) {
					for (Model303KeyEx keyEx : keyExs) {
						manageKey(column,m303s,keyEx,taxableBase,quota,deductibleQuota);
						if (keyEx.getKey() == Model303Key.A1) {
							// En el caso especial del regimen general, se chequea si la linea tiene R.E.
							// para añadirlo en la casilla A2
							double surcharge = rs.getDouble(3);
							if (surcharge > 0) {
								Model303KeyEx sKeyEx = new Model303KeyEx(Model303Key.A2,surchargePercent);
								manageKey(column,m303s,sKeyEx,taxableBase,surchargeQuota,0.0);
							}
						}
					}
				}
			}
			// Para el caso especial de la diferencia
			Model303KeyEx difKey = new Model303KeyEx(Model303Key.DF);
			Model303KeyEx devKey = new Model303KeyEx(Model303Key.A12);
			Model303KeyEx dedKey = new Model303KeyEx(Model303Key.FT);
			Model303 dev = m303s.get(devKey);
			Model303 ded = m303s.get(dedKey);
			double dif = CommonUtil.round(dev.getDetailMap().get(column).getQuota() - ded.getDetailMap().get(column).getQuota());
			manageKey(column,m303s,difKey,0.0,dif,0.0);
			List<Model303> list = new LinkedList<Model303>();
			list.addAll( m303s.values() );
			Model303Comparator comparator = new Model303Comparator();
			Collections.sort(list, comparator);
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


	private void initializeMap(Map<Model303KeyEx, Model303> m303s) {
		for(Model303Key key: Model303Key.values() ) {
			m303s.put(new Model303KeyEx(key),new Model303(key) );	
		}
	}

	private void manageKey(Model303Column column,Map<Model303KeyEx,Model303> m303s, Model303KeyEx keyEx, double taxableBase, double quota, double deductibleQuota) throws SQLException {
		Model303 m303 = m303s.get(keyEx);
		if (m303 == null) {
			m303 = new Model303();
			m303.setKey( keyEx.getKey() );
			m303s.put(keyEx, m303);
		}
		if (keyEx.getKey().isDetailed()) {
			m303.getDetailMap().get(column).setTaxableBase(CommonUtil.round(m303.getDetailMap().get(column).getTaxableBase() + taxableBase));
		}
		if (keyEx.getKey().isPercentVisible()) {
			m303.setPercent(keyEx.getPercent());	
		}
		if (keyEx.getKey().isDeductibleQuotaVisible()) {
			m303.getDetailMap().get(column).setDeductibleQuota(CommonUtil.round(m303.getDetailMap().get(column).getDeductibleQuota() + deductibleQuota));	
		}
		m303.getDetailMap().get(column).setQuota(CommonUtil.round(m303.getDetailMap().get(column).getQuota() + quota));
	}

	private Model303KeyEx[] obtainModel303Type(InvoiceType invoiceType, InvoiceTransactionType transaction, boolean investment,double percent) {
		if (invoiceType == InvoiceType.SALES) {
			if (transaction == InvoiceTransactionType.NATIONAL || transaction == InvoiceTransactionType.CAN_CEU_MEL) {
				return new Model303KeyEx[]{new Model303KeyEx(Model303Key.A1,percent),new Model303KeyEx(Model303Key.A12),new Model303KeyEx(Model303Key.AT)};
			}
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return new Model303KeyEx[]{new Model303KeyEx(Model303Key.EI)};
			}
		} else if (invoiceType == InvoiceType.PURCHASE) {
			
			if (transaction == InvoiceTransactionType.NATIONAL ) {
				return investment?
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.B2),new Model303KeyEx(Model303Key.BT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.BI,percent),new Model303KeyEx(Model303Key.TD)}:
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.B1),new Model303KeyEx(Model303Key.BT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.CP,percent),new Model303KeyEx(Model303Key.TD)};
			}
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				return investment?
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.C2),new Model303KeyEx(Model303Key.CT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.BI,percent),new Model303KeyEx(Model303Key.TD)}:
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.C1),new Model303KeyEx(Model303Key.CT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.CP,percent),new Model303KeyEx(Model303Key.TD)};
			}
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return investment?
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.A3),new Model303KeyEx(Model303Key.D2),new Model303KeyEx(Model303Key.DT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.BI,percent),new Model303KeyEx(Model303Key.TD)}:
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.A3),new Model303KeyEx(Model303Key.D1),new Model303KeyEx(Model303Key.DT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.CP,percent),new Model303KeyEx(Model303Key.TD)};
			}
		} else if (invoiceType == InvoiceType.EXPENSES) {
			if (transaction == InvoiceTransactionType.NATIONAL ) {
				return investment?
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.B2),new Model303KeyEx(Model303Key.BT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.BI,percent),new Model303KeyEx(Model303Key.TD)}:
					new Model303KeyEx[]{new Model303KeyEx(Model303Key.B1),new Model303KeyEx(Model303Key.BT),new Model303KeyEx(Model303Key.FT),new Model303KeyEx(Model303Key.GT,percent),new Model303KeyEx(Model303Key.TD)};
			}
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY || transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				return new Model303KeyEx[]{new Model303KeyEx(Model303Key.A4),new Model303KeyEx(Model303Key.AT)};
			}
		}
		System.out.println( "No exite tipo de IVA para " + invoiceType+", "+transaction + ", " + investment);
		return null; 
	}
	
}

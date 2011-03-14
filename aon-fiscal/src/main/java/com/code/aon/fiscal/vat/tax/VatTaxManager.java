package com.code.aon.fiscal.vat.tax;

import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.VatTaxColumn;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;

public class VatTaxManager {

	public List<VatTaxDetail> getVatTax(VatTaxParameters params) throws ManagerBeanException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, params.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		Date dateFrom = c.getTime();	
		Date dateTo = params.getPeriod().getDueDate(params.getYear());
		PreparedStatement ps = null;
		ResultSet rs = null;
		VatTaxColumn column = VatTaxColumn.ACUMULADO; 
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
			stmt.append(" AND i.tax_date >= ?");
			stmt.append(" AND i.tax_date <= ?");
			if (params.getInvoiceStatus() == InvoiceStatus.SCORED) {
				stmt.append(" AND i.status = 1 ");
			}
			stmt.append(" GROUP BY i.type,it.percentage,it.surcharge,i.transaction,i.investment");
			String sessionName = HibernateUtil.getSessionFactoryName();
			ps = HibernateUtil.getSQLConnection(sessionName).prepareStatement(stmt.toString(),
					ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
			ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
			rs = ps.executeQuery();
			List<VatTaxDetail> list = new LinkedList<VatTaxDetail>();
			initializeList(list);
			while (rs.next()) {
				InvoiceType invoiceType = InvoiceType.values()[rs.getInt(1)];
				InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(4)];
				boolean investment = rs.getBoolean(5);
				double percent = rs.getDouble(2);
				VatTaxKeyEx[] keyExs = obtainModelAffectedKeys(invoiceType,transaction,investment,percent);
				double surchargePercent = rs.getDouble(3);
				double taxableBase = rs.getDouble(6);
				double quota = rs.getDouble(7);
				double surchargeQuota = rs.getDouble(8);
				double deductibleQuota = rs.getDouble(9);
				VatTaxAmount amount = new VatTaxAmount();
				amount.setTaxableBase(taxableBase);
				amount.setQuota(quota);
				amount.setDeductibleQuota(deductibleQuota);
				if (keyExs != null) {
					for (VatTaxKeyEx keyEx : keyExs) {
						manageKey(column,list,keyEx,amount);
						if (keyEx.getKey() == VatTaxKey.A1) {
							// En el caso especial del regimen general, se chequea si la linea 
							// tiene R.E. para añadirlo en la casilla A2
							if (surchargePercent > 0) {
								VatTaxKeyEx sKeyEx = new VatTaxKeyEx(VatTaxKey.A2,surchargePercent);
								VatTaxAmount surchargeAmount = new VatTaxAmount();
								surchargeAmount.setTaxableBase(taxableBase);
								surchargeAmount.setQuota(surchargeQuota);
								surchargeAmount.setDeductibleQuota(0.0);
								manageKey(column,list,sKeyEx,surchargeAmount);
							}
						}
					}
				}
			}
			VatTaxDetailComparator comparator = new VatTaxDetailComparator();
			Collections.sort(list, comparator);
			decorate(list);
			calculate(list);
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

	private void decorate(List<VatTaxDetail> list) {
		VatTaxKey pre = null;
		for (VatTaxDetail m: list) {
			m.setDescriptionDisabled(m.getKey() == pre );
			pre = m.getKey();
		}
	}

	private void initializeList(List<VatTaxDetail> list) {
		for(VatTaxKey key: VatTaxKey.values() ) {
			VatTaxDetail detail = new VatTaxDetail();
			detail.setKey(key);
			list.add(detail);
		}
	}

	private void manageKey(VatTaxColumn column,List<VatTaxDetail> list, 
			VatTaxKeyEx keyEx, VatTaxAmount amount) throws SQLException {
		VatTaxDetail  detail = null;
		for (VatTaxDetail d:list) {
			if (d.getKey() == keyEx.getKey() && d.getPercent() == keyEx.getPercent()) {
				detail = d;
				break;
			}
		}
		if (detail == null) {
			detail = new VatTaxDetail();
			detail.setKey( keyEx.getKey() );
			detail.setPercent(keyEx.getPercent());
			list.add(detail);
		}
		detail.add(column,amount);
	}

	private VatTaxKeyEx[] obtainModelAffectedKeys(InvoiceType invoiceType, InvoiceTransactionType transaction, boolean investment,double percent) {
		if (invoiceType == InvoiceType.SALES) {
			if (transaction == InvoiceTransactionType.NATIONAL) {
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A1,percent)};
			}
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EI)};
			}
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				// TODO tener en cuenta solo los PRODUCTOS.
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EX1)};
			}
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				// TODO tener en cuenta solo los PRODUCTOS.
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EX2)};
			}
			// TODO A5 --> Ventas Rectificativas Desglosado por porcentaje y totalizado, crear A5T ( total ).
		} else if (invoiceType == InvoiceType.PURCHASE) {
			
			if (transaction == InvoiceTransactionType.NATIONAL ) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
			}
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.C2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.C1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
			}
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A3),new VatTaxKeyEx(VatTaxKey.D2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A3),new VatTaxKeyEx(VatTaxKey.D1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
			}
		} else if (invoiceType == InvoiceType.EXPENSES) {
			if (transaction == InvoiceTransactionType.NATIONAL ) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent)};
			}
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A4)};
			}
			
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.D2),new VatTaxKeyEx(VatTaxKey.BI,percent),new VatTaxKeyEx(VatTaxKey.A4)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.D3),new VatTaxKeyEx(VatTaxKey.CP,percent),new VatTaxKeyEx(VatTaxKey.A4)};
			}
			
		}
		System.out.println( "No exite tipo de IVA para " + invoiceType+", "+transaction + ", " + investment);
		return null; 
	}
	
	public void calculate(List<VatTaxDetail> list) {
		for (VatTaxDetail detail:list){
			VatTaxKey[] positiveKeys = detail.getKey().getPositiveAffectedKeys();
			VatTaxKey[] negativeKeys = detail.getKey().getNegativeAffectedKeys();
			for (VatTaxDetail searchModel:list){
				if ( ArrayUtils.contains(positiveKeys, searchModel.getKey()) ) {
					detail.add( searchModel );
				}
				if ( ArrayUtils.contains(negativeKeys, searchModel.getKey()) ) {
					detail.subtract( searchModel );
				}
			}
		}
	}

	public void initializeTotals(List<VatTaxDetail> list) {
		for (VatTaxDetail detail:list){
			if (detail.getKey().isSubtotal() || detail.getKey().isTotal()) {
				detail.initialize();
			}
		}
	}

	public void fillDeclared(VatTaxParameters params, List<VatTaxDetail> summary) throws ManagerBeanException {
		VatTax vatTax = params.getVatTax();
		// Primera declaración del ejercicio, si no es complementaria, 
		// no se debe tener en cuenta lo almacenado en ese periodo.
		if ((vatTax.getPeriod() == Period.M01 || vatTax.getPeriod() == Period.T1) && !vatTax.isComplementary()) {
			return;  
		}
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_VAT_TAX_YEAR), params.getYear());
		String periodAlias = bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_VAT_TAX_PERIOD);

		int i = params.getPeriod().ordinal(); 
		if (i == 0 ) {
			criteria.addEqualExpression(periodAlias, Period.M01);  // ENERO y COMPLEMENTARIA
		}
		if (i == 12 ) {
			criteria.addEqualExpression(periodAlias, Period.T1); // 1 TRIMESTRE y COMPLEMENTARIA
		}
		if (i >0 && i<12) {
			criteria.addBetweenExpression(periodAlias, Period.M01, Period.values()[params.getPeriod().ordinal() -1 ]);
		}
		if (i >12 && i<16) {
			criteria.addBetweenExpression(periodAlias, Period.T1, Period.values()[params.getPeriod().ordinal() -1 ]);
		}
		// Si params.getPeriod() == Period.YEAR Se saca todo lo del ejercicio, o sea, no se añaden filtros.
		if (vatTax.isExtraDeclaration() && params.getPeriod() == Period.YEAR) {
			criteria.addExpression(ExpressionUtilities.getNotEqualExpression(periodAlias, Period.YEAR));
		}
		
		List<ITransferObject> list = bean.getList(criteria);
		for (ITransferObject to: list) {
			VatTaxDetail detail = (VatTaxDetail) to;
			if (!detail.getVatTax().isReplaced()) {
				VatTaxKey key = detail.getKey();
				double percent = detail.getPercent();
				boolean found = false;
				for (VatTaxDetail model: summary) {
					if (key == model.getKey() && (!key.isPercentVisible() || key.isPercentVisible() && model.getPercent() == percent)) {
						model.setTaxableBaseDeclared( CommonUtil.round( model.getQuotaDeclared() + detail.getTaxableBase() ));
						model.setQuotaDeclared( CommonUtil.round( model.getQuotaDeclared() + detail.getQuota() ));
						model.setDeductibleQuotaDeclared( CommonUtil.round( model.getDeductibleQuotaDeclared() + detail.getDeductibleQuota() ));
						found = true; 
						break;
					}
				}
				if (!found) {
					// Si hay algo declarado y no hay línea en esta declaracion. Ej:
					//	Una venta al 5% de IVA en el periodo anterior
					summary.add(detail);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public List<VatTaxDetail> getDetailList(VatTaxParameters params) throws ManagerBeanException {
		VatTaxDetailComparator comparator = new VatTaxDetailComparator();
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_VAT_TAX_ID), params.getVatTax().getId());
		List<?> list = bean.getList(criteria);
		List<VatTaxDetail> details = (List<VatTaxDetail>) list;
		Collections.sort(details, comparator);
		decorate(details);
		return details;
	}

	@SuppressWarnings("unchecked")
	public List<VatTaxDetail>  getPeriodDeclaredDetails(VatTaxDetail detail) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_VAT_TAX_YEAR), detail.getVatTax().getYear());
		criteria.addLessThanExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_VAT_TAX_PERIOD), detail.getVatTax().getPeriod());
		criteria.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_KEY), detail.getKey());
		if (detail.getKey().isPercentVisible()) {
			criteria.addEqualExpression(bean.getFieldName(IFiscalAlias.VAT_TAX_DETAIL_PERCENT), detail.getPercent());	
		}
		List<?> list = bean.getList(criteria);
		return (List<VatTaxDetail>) list;
	}
	
}

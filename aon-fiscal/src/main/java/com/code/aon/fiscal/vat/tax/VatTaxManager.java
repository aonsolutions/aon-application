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
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.dao.IFiscalAlias;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.TaxColumn;
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
		TaxColumn column = TaxColumn.ACUMULADO; 
		try {
			StringWriter stmt = new StringWriter();
			String quotaStmt = "IF(it.quota != 0,it.quota,ROUND(id.taxable_base * it.percentage / 100, 2) )";
			stmt.append("SELECT i.type,i.rectification_type,i.service,it.percentage,it.surcharge,it.vat_deduction_type,i.transaction,i.investment,");
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
			stmt.append(" GROUP BY i.type,i.rectification_type,i.service,it.percentage,it.surcharge,it.vat_deduction_type,i.transaction,i.investment");
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
				double surchargePercent = rs.getDouble(5);
				double taxableBase = rs.getDouble(9);
				double quota = rs.getDouble(10);
				double surchargeQuota = rs.getDouble(11);
				double deductibleQuota = rs.getDouble(12);
				
				VatTaxKeyEx[] keyExs = obtainModelAffectedKeys(rs);
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
			calculate(list);
			fillDeclared(params,list);		
			list = decorate(list);
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

	private List<VatTaxDetail> decorate(List<VatTaxDetail> list) {
		List<VatTaxDetail> newList = new LinkedList<VatTaxDetail>();
		for (VatTaxDetail detail : list) {
			if (detail.getKey().isPercentVisible()
				&& detail.getTaxableBaseAccumulated() == 0.0 && detail.getQuotaAccumulated() == 0.0
				&& detail.getDeductibleQuotaAccumulated() == 0.0 && detail.getTaxableBaseDeclared() == 0.0
				&& detail.getQuotaDeclared() == 0.0 && detail.getDeductibleQuotaDeclared() == 0.0
				&& detail.getTaxableBaseResult() == 0.0 && detail.getQuotaResult() == 0.0
				&& detail.getDeductibleQuotaResult() == 0.0 && detail.getTaxableBaseAdjust() == 0.0
				&& detail.getQuotaAdjust() == 0.0 && detail.getDeductibleQuotaAdjust() == 0.0
				&& detail.getTaxableBase() == 0.0 && detail.getQuota() == 0.0
				&& detail.getDeductibleQuota() == 0.0) {
					// nothing
			} else {
				newList.add(detail);	
			}
		}
		list = newList;

		VatTaxKey pre = null;
		for (VatTaxDetail m: list) {
			m.setDescriptionDisabled(m.getKey() == pre );
			pre = m.getKey();
		}
		return list;
	}

	private void initializeList(List<VatTaxDetail> list) {
		for(VatTaxKey key: VatTaxKey.values() ) {
			VatTaxDetail detail = new VatTaxDetail();
			detail.setKey(key);
			list.add(detail);
		}
	}

	private void manageKey(TaxColumn column,List<VatTaxDetail> list, 
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

	private VatTaxKeyEx[] obtainModelAffectedKeys(ResultSet rs) throws SQLException {
		InvoiceType invoiceType = InvoiceType.values()[rs.getInt(1)];
		boolean rectification = rs.getInt(2) == RectificationType.SPECIAL_RECTIFIER.ordinal();
		boolean service = rs.getInt(3) == 1;
		double percent = rs.getDouble(4);
		VatDeductionType vatDeductionType = VatDeductionType.values()[rs.getInt(6)];
		InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(7)];
		boolean investment = rs.getBoolean(8);

		
		if (invoiceType == InvoiceType.SALES) {
			List<VatTaxKeyEx> list = new LinkedList<VatTaxKeyEx>();
			if (transaction == InvoiceTransactionType.NATIONAL) {
				if (rectification) {
					return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A5,percent)};	
				} else {
					return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A1,percent)};	
				}
			} else {
				if (!service) {
					if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EI)};
					}
					if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EX1)};
					}
					if (transaction == InvoiceTransactionType.CAN_CEU_MEL) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EX2)};
					}
				} else {
					if (vatDeductionType == VatDeductionType.WITHOUT_RIGHT) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.OS)};
					} else {
						if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
							return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.OO)};
						} else if (transaction != InvoiceTransactionType.NATIONAL) {
							return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.OI)};
						}
					}
				}
			}
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
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A3,percent),new VatTaxKeyEx(VatTaxKey.D2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A3,percent),new VatTaxKeyEx(VatTaxKey.D1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
			}
		} else if (invoiceType == InvoiceType.EXPENSES) {
			if (transaction == InvoiceTransactionType.NATIONAL || transaction == InvoiceTransactionType.CAN_CEU_MEL) {
				//TODO	A la espera de saber si los gastos pueden ser Inversiones
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent)}; 
//				return investment?
//					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
//					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent)};
			}
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				//TODO	A la espera de saber si los gastos pueden ser Inversiones
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent),new VatTaxKeyEx(VatTaxKey.A4)};
//				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A4)};
			}
			
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent),new VatTaxKeyEx(VatTaxKey.A4)};
				//TODO	A la espera de saber si los gastos pueden ser Inversiones					
//				return investment?
//					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent),new VatTaxKeyEx(VatTaxKey.A4)}:
//					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.D3),new VatTaxKeyEx(VatTaxKey.CP,percent),new VatTaxKeyEx(VatTaxKey.A4)};
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

	private void fillDeclared(VatTaxParameters params, List<VatTaxDetail> summary) throws ManagerBeanException {
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
						model.setTaxableBaseDeclared( CommonUtil.round( model.getTaxableBaseDeclared() + detail.getTaxableBase() ));
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

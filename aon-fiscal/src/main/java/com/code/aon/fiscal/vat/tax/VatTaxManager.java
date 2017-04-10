package com.code.aon.fiscal.vat.tax;

import java.io.Serializable;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.config.enumeration.VatDeductionType;
import com.code.aon.dbutils.DatabaseUtil;
import com.code.aon.finance.enumeration.InvoiceStatus;
import com.code.aon.finance.enumeration.InvoiceType;
import com.code.aon.finance.enumeration.RectificationType;
import com.code.aon.fiscal.VatTax;
import com.code.aon.fiscal.VatTaxDetail;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.TaxColumn;
import com.code.aon.fiscal.enumeration.VatTaxKey;
import com.code.aon.pool.AonConnectionException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.watson.server.AonDateUtils;

public class VatTaxManager implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static String TYPE = "type";
	private static String RECTIFICATION_TYPE = "rectification_type";
	private static String SERVICE = "service";
	private static String PERCENTAGE = "percentage";
	private static String SURCHARGE_PERCENT = "surcharge";
	private static String VAT_DEDUCTION_TYPE = "vat_deduction_type";
	private static String TRANSACTION = "transaction";
	private static String INVESTMENT = "investment";
	private static String BASE = "base";
	private static String QUOTA = "quota";
	private static String SURCHARGE_QUOTA = "surcharge_quota"; 
	private static String DEDUCTIBLE_QUOTA = "deductible_quota";
	private static String WITHHOLDING_FARMER = "withholding_farmer";
//	private static String VAT_ACCRUAL_PAYMENT = "vat_accrual_payment";
	private static String FINANCE_AMOUNT = "finance_amount";
	private static String INVOICE_TOTAL = "invoice_total";
	private static String INVOICE_BASE = "invoice_base";
	private static String INVOICE_VAT = "invoice_vat";
	private static String INVOICE_RETENTION = "invoice_retention";
	
	private String domainName;
	
	public VatTaxManager(String domainName) {
		this.domainName = domainName;
	}
	public String getDomainName() {
		return domainName;
	}

	public List<VatTaxDetail> getVatTax(VatTaxParameters params) throws ManagerBeanException {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.YEAR, params.getYear());
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MONTH, 0);
		Date dateFrom = c.getTime();
		Date dateTo = params.getPeriod().getDueDate(params.getYear());
		List<VatTaxDetail> list = new LinkedList<VatTaxDetail>();
		if (params.isMod303AvailableByDifferenceDisabled() ) {
			initializeListForOnlyDeclaration(list);
		} else {
			initializeList(list);
			getVatTax(list, params.getDomain(),dateFrom, dateTo, params.getVatTax(), params.getInvoiceStatus());
			VatTaxDetailComparator comparator = new VatTaxDetailComparator();
			Collections.sort(list, comparator);
			calculate(list);
			if (params.getPeriod() != Period.YEAR) {
				fillDeclared(params,list);
			}
		}
		Collections.sort(list, new VatTaxDetailComparator());
		list = decorate(params,list);		
		return list;
	}

	private void initializeListForOnlyDeclaration(List<VatTaxDetail> list) {
		initializeList(list);
		VatTaxKey[] keys = new VatTaxKey[] {VatTaxKey.A1,VatTaxKey.A3,VatTaxKey.A31,VatTaxKey.A4,VatTaxKey.CP,VatTaxKey.GT,VatTaxKey.BI};
		double[] percents = new double[] {4,10,21};
		VatTaxDetail detail;
		for (VatTaxKey key : keys) {
			for (double percent : percents) {
				detail = new VatTaxDetail();
				detail.setKey(key);
				detail.setPercent(percent);
				list.add(detail);
			}
		}
		
		detail = new VatTaxDetail();
		detail.setKey(VatTaxKey.A2);
		detail.setPercent(0.5);
		list.add(detail);
		detail = new VatTaxDetail();
		detail.setKey(VatTaxKey.A2);
		detail.setPercent(1.4);
		list.add(detail);
		detail = new VatTaxDetail();
		detail.setKey(VatTaxKey.A2);
		detail.setPercent(5.2);
		list.add(detail);
	}
	
	public List<VatTaxDetail> getVatTax(int domain,Date dateFrom,Date dateTo) throws ManagerBeanException {
		List<VatTaxDetail> list = new LinkedList<VatTaxDetail>();
		initializeList(list);
		return getVatTax(list,domain,dateFrom, dateTo, null, null); 
	}
		
	public List<VatTaxDetail> getVatTax(List<VatTaxDetail> list,int domain,Date dateFrom,Date dateTo,VatTax vatTax,InvoiceStatus status) throws ManagerBeanException {
		getVatTaxINNER(list,domain,dateFrom,dateTo,vatTax,status,false,false);
		getVatTaxINNER(list,domain,dateFrom,dateTo,vatTax,status,true,false);
		// En el ultimo perido se debe declarar lo pendiente del año anterior de criterio de caja.
		if (vatTax != null && (vatTax.getPeriod() == Period.M12 || vatTax.getPeriod() == Period.T4)) {
			getVatTaxINNER(list,domain,dateFrom,dateTo,vatTax,status,true,true);
		}
		return list;
	}

	private List<VatTaxDetail> getVatTaxINNER(
			List<VatTaxDetail> list,
			int domain,
			Date dateFrom,
			Date dateTo,
			VatTax vatTax,
			InvoiceStatus status,
			boolean vatAccrualPayment,
			boolean lastPeriod) throws ManagerBeanException {
		PreparedStatement ps = null;
		ResultSet rs = null;
		TaxColumn column = TaxColumn.ACUMULADO; 
		Connection conn = null;
		try {
			conn = DatabaseUtil.getConnection(getDomainName());
			String select = null;
			if (vatAccrualPayment) {
				if (lastPeriod) {
					select = getLastPeriodVatAccrualSelect();	
				} else {
					select = getVatAccrualSelect();
				}
			} else {
				select = getSelect();
			}
			ps = conn.prepareStatement(select,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			int i = 0;
			ps.setInt(++i, domain);
			if (lastPeriod) {
				ps.setDate(++i, new java.sql.Date( AonDateUtils.getYearFirstDay(vatTax.getYear() - 1).getTime()) );	
				ps.setDate(++i, new java.sql.Date( AonDateUtils.getYearLastDay(vatTax.getYear() - 1).getTime()) );
			} else {
				ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
				ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
				if (vatAccrualPayment ){
					int prevYear = AonDateUtils.getYear(dateFrom) - 1;
					ps.setDate(++i, new java.sql.Date( AonDateUtils.getYearFirstDay(prevYear).getTime()) );	
				}
			}
			ps.setInt(++i, (status == InvoiceStatus.SCORED?InvoiceStatus.SCORED.ordinal():InvoiceStatus.PENDING.ordinal()));
			rs = ps.executeQuery();
			boolean hasVatAccrualPayment = false;
			while (rs.next()) {
				double surchargePercent = rs.getDouble(SURCHARGE_PERCENT);
				double taxableBase = rs.getDouble(BASE);
				double percent = rs.getDouble(PERCENTAGE);
				double quota = rs.getDouble(QUOTA);
				double surchargeQuota = rs.getDouble(SURCHARGE_QUOTA);
				double deductibleQuota = rs.getDouble(DEDUCTIBLE_QUOTA);

				if (vatAccrualPayment ){
					double invoiceBase = rs.getDouble(INVOICE_BASE);
					double invoiceVat  = rs.getDouble(INVOICE_VAT);
					double invoiceRetention = rs.getDouble(INVOICE_RETENTION);
					double invoiceTotal = rs.getDouble(INVOICE_TOTAL);
					double financeAmount = rs.getDouble(FINANCE_AMOUNT);
					
					invoiceTotal = CommonUtil.round(invoiceBase + invoiceVat - invoiceRetention);
					taxableBase = CommonUtil.round(financeAmount * taxableBase / invoiceTotal,4);
					quota = CommonUtil.round(taxableBase * percent / 100);
					deductibleQuota = quota; // TODO soporte a cuota deducible.
					hasVatAccrualPayment = true;
				}
				
				VatTaxKeyEx[] keyExs = obtainModelAffectedKeys(rs);
				VatTaxAmount amount = new VatTaxAmount();
				amount.setTaxableBase(taxableBase);
				amount.setQuota(quota);
				amount.setDeductibleQuota(deductibleQuota);
				if (keyExs != null) {
					for (VatTaxKeyEx keyEx : keyExs) {
						// En el caso de que las claves afectadas deban ser 
						// aminoradas debido a la existencia de prorrata.
						if (vatTax != null && vatTax.isProrataEnabled() && keyEx.getKey().isProrrataAware()) {
							double prorata = vatTax.getProrata();
							VatTaxAmount proratedAmount = new VatTaxAmount();
							proratedAmount.setTaxableBase(taxableBase);
							proratedAmount.setQuota(CommonUtil.round( quota * prorata / 100  ));
							proratedAmount.setDeductibleQuota(CommonUtil.round( deductibleQuota * prorata / 100  ));
							manageKey(column,list,keyEx,proratedAmount);
						} else {
							manageKey(column,list,keyEx,amount);	
						}
						if (keyEx.getKey() == VatTaxKey.A1 || keyEx.getKey() == VatTaxKey.A5) {
							// En el caso especial del regimen general, se chequea si la linea 
							// tiene R.E. para añadirlo en la casilla A2 (o A21 rectificativas)
							// Se ignora la prorrata, puesto que solo afecta al IVA soportado.
							if (surchargePercent > 0) {
								VatTaxKeyEx sKeyEx = null;
								if (keyEx.getKey() != VatTaxKey.A1) {
									sKeyEx = new VatTaxKeyEx(VatTaxKey.A21);	
								} else {
									sKeyEx = new VatTaxKeyEx(VatTaxKey.A2,surchargePercent);
								}
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
			rs.close();
			ps.close();
			
			if (hasVatAccrualPayment && !lastPeriod) {
				String sel ="SELECT i.type " + TYPE
						+ ",SUM( i.taxable_base)" + INVOICE_BASE
						+ ",SUM( i.vat_quota)" + INVOICE_VAT
						+ " FROM invoice i"
						+ " WHERE i.domain = ?"
						+ " AND i.vat_accrual_payment = 1"
						+ " AND i.tax_date >= ?"
						+ " AND i.tax_date <= ?"
						+ " AND i.status >= ? "
						+ " GROUP BY i.type"
						+ " HAVING " + INVOICE_VAT +" > 0";
				ps = conn.prepareStatement(sel,ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
				i = 0;
				ps.setInt(++i, domain);
				ps.setDate(++i, new java.sql.Date( dateFrom.getTime() ));
				ps.setDate(++i, new java.sql.Date( dateTo.getTime()));
				ps.setInt(++i, (status == InvoiceStatus.SCORED?InvoiceStatus.SCORED.ordinal():InvoiceStatus.PENDING.ordinal()));
				rs = ps.executeQuery();
				while (rs.next()) {
					InvoiceType type = InvoiceType.values()[rs.getInt(TYPE)];
					VatTaxKeyEx keyEx = new VatTaxKeyEx(type==InvoiceType.SALES?VatTaxKey.XO:VatTaxKey.XI);
					VatTaxAmount amount = new VatTaxAmount();
					amount.setTaxableBase(CommonUtil.round( rs.getDouble(INVOICE_BASE)));
					amount.setQuota(CommonUtil.round( rs.getDouble(INVOICE_VAT) ));
					amount.setDeductibleQuota(CommonUtil.round( rs.getDouble(INVOICE_VAT)));
					keyEx.getKey();
					manageKey(column,list,keyEx,amount);
				}
				rs.close();
				ps.close();
			}
			return list;
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (AonConnectionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} finally {
			DatabaseUtil.closeQuietly(rs);
			DatabaseUtil.closeQuietly(ps);
			DatabaseUtil.closeQuietly(conn);
		}
	}

	private List<VatTaxDetail> decorate(VatTaxParameters params, List<VatTaxDetail> list) {
		List<VatTaxDetail> newList = new LinkedList<VatTaxDetail>();
		for (VatTaxDetail detail : list) {
			if (detail.getKey().isPercentVisible() && detail.getPercent() == 0.0
				&& detail.getTaxableBaseAccumulated() == 0.0 && detail.getQuotaAccumulated() == 0.0
				&& detail.getDeductibleQuotaAccumulated() == 0.0 && detail.getTaxableBaseDeclared() == 0.0
				&& detail.getQuotaDeclared() == 0.0 && detail.getDeductibleQuotaDeclared() == 0.0
				&& detail.getTaxableBaseResult() == 0.0 && detail.getQuotaResult() == 0.0
				&& detail.getDeductibleQuotaResult() == 0.0 && detail.getTaxableBaseAdjust() == 0.0
				&& detail.getQuotaAdjust() == 0.0 && detail.getDeductibleQuotaAdjust() == 0.0
				&& detail.getTaxableBase() == 0.0 && detail.getQuota() == 0.0
				&& detail.getDeductibleQuota() == 0.0) {
				// Nothing 
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
		InvoiceType invoiceType = InvoiceType.values()[rs.getInt(TYPE)];
		boolean rectification = rs.getInt(RECTIFICATION_TYPE) == RectificationType.SPECIAL_RECTIFIER.ordinal()
				|| rs.getInt(RECTIFICATION_TYPE) == RectificationType.NORMAL_RECTIFIER.ordinal();
		boolean service = rs.getInt(SERVICE) == 1;
		double percent = rs.getDouble(PERCENTAGE);
		VatDeductionType vatDeductionType = VatDeductionType.values()[rs.getInt(VAT_DEDUCTION_TYPE)];
		InvoiceTransactionType transaction = InvoiceTransactionType.values()[rs.getInt(TRANSACTION)];
		boolean investment = rs.getBoolean(INVESTMENT);
		
		boolean farmerRegime = rs.getBoolean(WITHHOLDING_FARMER);
		

		if (invoiceType == InvoiceType.SALES) {
			if (transaction == InvoiceTransactionType.NATIONAL) {
				if (rectification) {
					if (investment) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A5), new VatTaxKeyEx(VatTaxKey.EBI)};	
					} else {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A5)};
					}
				} else {
					if (investment) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A1,percent), new VatTaxKeyEx(VatTaxKey.EBI)};	
					} else {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A1,percent)};
					}
				}
			} else {
				if (!service) {
					if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EI)};
					} else if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EX1)};
					} else if (transaction == InvoiceTransactionType.CAN_CEU_MEL) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.EX2)};
					} else if (transaction == InvoiceTransactionType.OTHER_ISP) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.OI)};
					}
				} else {
					if (vatDeductionType == VatDeductionType.WITHOUT_RIGHT) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.OS)};
					} else {
						if (transaction == InvoiceTransactionType.EXTRACOMMUNITY || transaction == InvoiceTransactionType.CAN_CEU_MEL) {
							return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.OO)};
						} else if (transaction == InvoiceTransactionType.OTHER_ISP) {
							return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.OI)};
						} else if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
							return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.PS)};
						}
					}
				}
			}
		} else if (invoiceType == InvoiceType.PURCHASE) {
			
			if (transaction == InvoiceTransactionType.NATIONAL) {
				if (farmerRegime) {
					return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.ET),new VatTaxKeyEx(VatTaxKey.CP,percent)};
				} else {
					if (investment) {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent)}; 
					} else {
						return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
					}
				}
			}
			if (transaction == InvoiceTransactionType.OTHER_ISP) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A4,percent),new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A4,percent),new VatTaxKeyEx(VatTaxKey.B1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
			}
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY || transaction == InvoiceTransactionType.CAN_CEU_MEL) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.C2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.C1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
			}
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A3,percent),new VatTaxKeyEx(VatTaxKey.D2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					service?
						new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A31,percent),new VatTaxKeyEx(VatTaxKey.D3),new VatTaxKeyEx(VatTaxKey.CP,percent)}:
						new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A3,percent),new VatTaxKeyEx(VatTaxKey.D1),new VatTaxKeyEx(VatTaxKey.CP,percent)};
			}
		} else if (invoiceType == InvoiceType.EXPENSES) {
			if (transaction == InvoiceTransactionType.NATIONAL) {
				return investment?
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
					new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent)};
			} 
			if (transaction == InvoiceTransactionType.CAN_CEU_MEL || transaction == InvoiceTransactionType.OTHER_ISP) {
					return investment?
						new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A4,percent),new VatTaxKeyEx(VatTaxKey.B2),new VatTaxKeyEx(VatTaxKey.BI,percent)}:
						new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.A4,percent),new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent)};
				} 
			if (transaction == InvoiceTransactionType.EXTRACOMMUNITY) {
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.B3),new VatTaxKeyEx(VatTaxKey.GT,percent),new VatTaxKeyEx(VatTaxKey.A4,percent)};
			}
			
			if (transaction == InvoiceTransactionType.INTRACOMMUNITY) {
				return new VatTaxKeyEx[]{new VatTaxKeyEx(VatTaxKey.D3),new VatTaxKeyEx(VatTaxKey.GT,percent),new VatTaxKeyEx(VatTaxKey.A31,percent)};
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
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_YEAR), params.getYear());
		String periodAlias = bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_PERIOD);

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
					// Una venta al 5% de IVA en el periodo anterior
					// Si no hay acumulado, es por que se borro la factura despues de declarar.
					
					detail.setTaxableBaseAccumulated(0);
					detail.setQuotaAccumulated(0);
					detail.setDeductibleQuotaAccumulated(0);
					
					detail.setTaxableBaseAdjust(0);
					detail.setQuotaAdjust(0);
					detail.setDeductibleQuotaAdjust(0);

					detail.setTaxableBaseDeclared(detail.getTaxableBase());
					detail.setQuotaDeclared(detail.getQuota());
					detail.setDeductibleQuotaDeclared(detail.getDeductibleQuota());
					
					detail.setId(null);
					
					detail.calculate();
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
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_ID), params.getVatTax().getId());
		List<?> list = bean.getList(criteria);
		List<VatTaxDetail> details = (List<VatTaxDetail>) list;
		Collections.sort(details, comparator);
		decorate(params,details);
		return details;
	}

	@SuppressWarnings("unchecked")
	public List<VatTaxDetail>  getPeriodDeclaredDetails(VatTaxDetail detail) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(VatTaxDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_YEAR), detail.getVatTax().getYear());
		criteria.addLessThanExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_VAT_TAX_PERIOD), detail.getVatTax().getPeriod());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_KEY), detail.getKey());
		if (detail.getKey().isPercentVisible()) {
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.VAT_TAX_DETAIL_PERCENT), detail.getPercent());	
		}
		List<?> list = bean.getList(criteria);
		return (List<VatTaxDetail>) list;
	}
	
	private String getSelect() {
		String quotaStmt = "IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 2) )";
		StringWriter stmt = new StringWriter();
		stmt.append("SELECT i.type " + TYPE);
		stmt.append(	",i.rectification_type " + RECTIFICATION_TYPE);
		stmt.append(	",i.service " + SERVICE);
		stmt.append(	",it.percentage " + PERCENTAGE);
		stmt.append(	",it.surcharge " + SURCHARGE_PERCENT);
		stmt.append(	",it.vat_deduction_type " + VAT_DEDUCTION_TYPE);
		stmt.append(	",i.transaction "+ TRANSACTION);
		stmt.append(	",i.investment " + INVESTMENT);
		stmt.append(	",i.withholding_farmer " + WITHHOLDING_FARMER);
		stmt.append(	",SUM( i.taxable_base)" + INVOICE_BASE);
		stmt.append(	",SUM( i.vat_quota)" + INVOICE_VAT);
		stmt.append(	",SUM( i.retention_quota)" + INVOICE_RETENTION);
		stmt.append(	",SUM( i.total) " + INVOICE_TOTAL);
		stmt.append(	",SUM( it.base) " + BASE);
		stmt.append(	",SUM( " + quotaStmt + " ) " + QUOTA);
		stmt.append(	",SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) ) " + SURCHARGE_QUOTA);
		stmt.append(	",SUM( IF(it.deductible_quota != 0,it.deductible_quota," + quotaStmt + ")) " + DEDUCTIBLE_QUOTA);
		stmt.append(" FROM invoice_tax it ");
		stmt.append(" INNER JOIN invoice_detail id ON (it.invoice_detail = id.id)"); 
		stmt.append(" INNER JOIN invoice i ON (id.invoice = i.id)"); 
		stmt.append(" WHERE it.domain = ?");
		stmt.append(	" AND it.tax_type = 1");
		stmt.append(	" AND i.vat_accrual_payment = 0");	// No Criterio de Caja.
		stmt.append(	" AND i.tax_date >= ?");
		stmt.append(	" AND i.tax_date <= ?");
		stmt.append(	" AND i.status >= ? ");
		stmt.append(" GROUP BY ");
		stmt.append(	TYPE);
		stmt.append(	"," + RECTIFICATION_TYPE);
		stmt.append(	"," + SERVICE);
		stmt.append(	"," + PERCENTAGE);
		stmt.append(	"," + SURCHARGE_PERCENT);
		stmt.append(	"," + VAT_DEDUCTION_TYPE);
		stmt.append(	"," + TRANSACTION);
		stmt.append(	"," + INVESTMENT);
		stmt.append(	"," + WITHHOLDING_FARMER);
		return stmt.toString();
	}
	
	private String getVatAccrualSelect() {
		String quotaStmt = "IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 4) )";
		StringWriter stmt = new StringWriter();
		stmt.append("SELECT it.id");
		stmt.append(	",i.type " + TYPE);
		stmt.append(	",i.rectification_type " + RECTIFICATION_TYPE);
		stmt.append(	",i.service " + SERVICE);
		stmt.append(	",it.percentage " + PERCENTAGE);
		stmt.append(	",it.surcharge " + SURCHARGE_PERCENT);
		stmt.append(	",it.vat_deduction_type " + VAT_DEDUCTION_TYPE);
		stmt.append(	",i.transaction "+ TRANSACTION);
		stmt.append(	",i.investment " + INVESTMENT);
		stmt.append(	",i.withholding_farmer " + WITHHOLDING_FARMER);
		stmt.append(	",i.taxable_base " + INVOICE_BASE);
		stmt.append(	",i.vat_quota " + INVOICE_VAT);
		stmt.append(	",i.retention_quota " + INVOICE_RETENTION);
		stmt.append(	",i.total " + INVOICE_TOTAL);
		stmt.append(	",it.base " + BASE);
		stmt.append(	"," + quotaStmt + " " + QUOTA);
		stmt.append(	",SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) ) " + SURCHARGE_QUOTA);
		stmt.append(	",SUM( IF(it.deductible_quota != 0,it.deductible_quota," + quotaStmt + ")) " + DEDUCTIBLE_QUOTA);
		stmt.append(	",SUM( IF(ft.type=1, ft.amount,  -ft.amount )) " + FINANCE_AMOUNT);
		stmt.append("  FROM finance_tracking ft ");
		stmt.append("  INNER JOIN finance f ON (ft.finance = f.id) ");
		stmt.append("  INNER JOIN invoice i ON (f.invoice = i.id AND vat_accrual_payment = 1) ");
		stmt.append("  INNER JOIN invoice_detail id ON (id.invoice = i.id) ");
		stmt.append("  INNER JOIN invoice_tax it ON (it.invoice_detail = id.id) ");
		stmt.append(" WHERE ft.domain = ?");
		stmt.append(	" AND ft.tracking_date >= ?");
		stmt.append(	" AND ft.tracking_date <= ?");
		stmt.append(	" AND ft.type IN (1,2) ");
		stmt.append(	" AND it.tax_type = 1");
		stmt.append(	" AND i.tax_date >= ?");
		stmt.append(	" AND i.vat_accrual_payment = 1");	// Criterio de Caja.
		stmt.append(	" AND i.status >= ? ");
		stmt.append(" GROUP BY it.id,"+PERCENTAGE +","+ SURCHARGE_PERCENT+","+VAT_DEDUCTION_TYPE);
		return stmt.toString();
	}
	
	private String getLastPeriodVatAccrualSelect() {
		String quotaStmt = "IF(it.quota != 0,it.quota,ROUND(it.base * it.percentage / 100, 4) )";
		StringWriter stmt = new StringWriter();
		stmt.append("SELECT it.id");
		stmt.append(	",i.type " + TYPE);
		stmt.append(	",i.rectification_type " + RECTIFICATION_TYPE);
		stmt.append(	",i.service " + SERVICE);
		stmt.append(	",it.percentage " + PERCENTAGE);
		stmt.append(	",it.surcharge " + SURCHARGE_PERCENT);
		stmt.append(	",it.vat_deduction_type " + VAT_DEDUCTION_TYPE);
		stmt.append(	",i.transaction "+ TRANSACTION);
		stmt.append(	",i.investment " + INVESTMENT);
		stmt.append(	",i.withholding_farmer " + WITHHOLDING_FARMER);
		stmt.append(	",i.taxable_base " + INVOICE_BASE);
		stmt.append(	",i.vat_quota " + INVOICE_VAT);
		stmt.append(	",i.retention_quota " + INVOICE_RETENTION);
		stmt.append(	",i.total " + INVOICE_TOTAL);
		stmt.append(	",it.base " + BASE);
		stmt.append(	"," + quotaStmt + " " + QUOTA);
		stmt.append(	",SUM( IF(it.surcharge_quota != 0,it.surcharge_quota,ROUND(it.base * it.surcharge / 100, 2) ) ) " + SURCHARGE_QUOTA);
		stmt.append(	",SUM( IF(it.deductible_quota != 0,it.deductible_quota," + quotaStmt + ")) " + DEDUCTIBLE_QUOTA);
		stmt.append(	",SUM( f.amount ) " + FINANCE_AMOUNT);
		stmt.append("  FROM finance f ");
		stmt.append("  INNER JOIN invoice i ON (f.invoice = i.id AND vat_accrual_payment = 1) ");
		stmt.append("  INNER JOIN invoice_detail id ON (id.invoice = i.id) ");
		stmt.append("  INNER JOIN invoice_tax it ON (it.invoice_detail = id.id) ");
		stmt.append(" WHERE f.domain = ?");
		stmt.append(	" AND f.status = 0 ");
		stmt.append(	" AND it.tax_type = 1");
		stmt.append(	" AND i.tax_date >= ?");
		stmt.append(	" AND i.tax_date <= ?");
		stmt.append(	" AND i.vat_accrual_payment = 1");	// Criterio de Caja.
		stmt.append(	" AND i.status >= ? ");
		stmt.append(" GROUP BY it.id,"+PERCENTAGE +","+ SURCHARGE_PERCENT+","+VAT_DEDUCTION_TYPE);
		return stmt.toString();
	}
}

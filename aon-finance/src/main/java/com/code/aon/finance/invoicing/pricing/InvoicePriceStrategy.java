package com.code.aon.finance.invoicing.pricing;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.code.aon.AonVersion;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.enumeration.TaxType;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.product.strategy.BasicPriceStrategy;
import com.code.aon.product.strategy.ICalculableContainer;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.product.strategy.TaxKey;
import com.code.aon.registry.ITaxInfo;

public class InvoicePriceStrategy extends BasicPriceStrategy {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public double getTaxableBase(ICalculableContainer icc) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getTaxableBase() != 0) ? invoice.getTaxableBase() : getCalculatedTaxableBase(icc);
	}

	public double getCalculatedTaxableBase(ICalculableContainer icc) {
		double taxableBase = 0;
		for (Object obj : icc.getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)obj;
			taxableBase = CommonUtil.round(taxableBase + invoiceDetail.getTaxableBase(), 4);
		}
		if (icc.getDiscountExpression().getDiscounts() != null) {
			for (int i = 0; i<icc.getDiscountExpression().getDiscounts().length; i++) {
				taxableBase = taxableBase * ( 1 - icc.getDiscountExpression().getDiscounts()[i] /100);
			}
		}
		return CommonUtil.round(taxableBase);
	}

	@Override
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti, boolean ignoreTaxFree) {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		Map<TaxKey, TaxBreakDown> map = new HashMap<TaxKey, TaxBreakDown>();
		for (Object obj : icc.getDetailList()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)obj;
			for (TaxBreakDown breakDown : invoiceDetail.getTaxBreakDowns()) {
				if (ignoreTaxFree || ((!breakDown.isVat() || !iti.isVatFree()) && (!breakDown.isRetention() || !iti.isRetentionFree()))) {
					setTaxBreakDownAddInfo(breakDown, invoiceDetail);
					TaxKey key = new TaxKey();
					key.setType(breakDown.getTaxType());
					key.setPercent(breakDown.getTaxPercent());
					TaxBreakDown mapBreakDown;
					if (map.containsKey(key)) {
						mapBreakDown = map.get(key);
						mapBreakDown.setBase(CommonUtil.round(mapBreakDown.getBase() + breakDown.getBase(), 4));
						mapBreakDown.setTaxQuota(CommonUtil.round(mapBreakDown.getTaxQuota() + breakDown.getTaxQuota()));
						mapBreakDown.setSurchargeQuota(CommonUtil.round(mapBreakDown.getSurchargeQuota() + breakDown.getSurchargeQuota()));
						if (mapBreakDown.isVat()) {
							if (breakDown.getDeductibleQuota() != 0) {
								mapBreakDown.setDeductibleQuota(CommonUtil.round(mapBreakDown.getDeductibleQuota() + breakDown.getDeductibleQuota()));
							} else {
								breakDown.setDeductibleQuota(obtainDeductibleQuota(breakDown));
								if (mapBreakDown.getDeductibleQuota() != 0) {
									mapBreakDown.setDeductibleQuota(CommonUtil.round(mapBreakDown.getDeductibleQuota() + breakDown.getDeductibleQuota()));
								} else {
									if (mapBreakDown.getDeductiblePercent() != breakDown.getDeductiblePercent()) {
										mapBreakDown.setDeductibleQuota(CommonUtil.round(obtainDeductibleQuota(mapBreakDown) + breakDown.getDeductibleQuota()));
										if (breakDown.getDeductiblePercent() != 100) {
											mapBreakDown.setDeductiblePercent(breakDown.getDeductiblePercent());
										}
									} else {
										mapBreakDown.setDeductibleBase(CommonUtil.round(mapBreakDown.getDeductibleBase() + breakDown.getDeductibleBase()));
									}
								}
							}
						}
					} else {
						mapBreakDown = breakDown;
					}
					map.put(key, mapBreakDown);
				}
			}
		}

		for (TaxBreakDown breakDown : map.values()) {
			if (breakDown.getTaxQuota() == 0) {
				breakDown.setTaxQuota(obtainQuota(breakDown.getBase(), breakDown.getTaxPercent()));
			}
			if (iti.isSurcharge()) {
				if (breakDown.getSurchargeQuota() == 0) {
					breakDown.setSurchargeQuota(obtainQuota(breakDown.getBase(), breakDown.getSurchargePercent()));
				}
			} else {
				breakDown.setSurchargeQuota(0.0);
				breakDown.setSurchargePercent(0.0);
			}
			if (breakDown.getDeductibleQuota() == 0) {
				breakDown.setDeductibleQuota(obtainDeductibleQuota(breakDown));
			}
			taxBreakDowns.add(breakDown);
		}
		return taxBreakDowns;
	}
	
	@Override
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti) {
		return getTaxBreakDowns(icc, iti, false);
	}

	protected void setTaxBreakDownAddInfo(TaxBreakDown breakDown, InvoiceDetail invoiceDetail) {
		//Para redefinir en los hijos.
	}

	@Override
	public double getTotalVatQuota(ICalculableContainer icc, ITaxInfo iti) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getVatQuota() != 0) ? invoice.getVatQuota() : getCalculatedTotalVatQuota(icc, iti);
	}

	public double getCalculatedTotalVatQuota(ICalculableContainer icc, ITaxInfo iti) {
		return super.getTotalVatQuota(icc, iti);
	}
	
	@Override
	public double getTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getRetentionQuota() != 0) ? invoice.getRetentionQuota() : getCalculatedTotalRetentionQuota(icc, iti);
	}

	public double getCalculatedTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti) {
		return super.getTotalRetentionQuota(icc, iti);
	}
	
	@Override
	public double getTotalPrice(ICalculableContainer icc, ITaxInfo iti) {
		Invoice invoice = (Invoice)icc;
		return (invoice.getTotal() != 0) ? invoice.getTotal() : super.getTotalPrice(icc, iti);
	}

	public double getVatPercent(ICalculableContainer icc, ITaxInfo iti) {
		double percent = 0;
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns(icc, iti)) {
			if (taxBreakDown.getTaxType().equals(TaxType.VAT)) {
				percent = taxBreakDown.getTaxPercent();
				break;
			}
		}
		return CommonUtil.round(percent);
	}

	public double getRetentionPercent(ICalculableContainer icc, ITaxInfo iti) {
		double percent = 0;
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns(icc, iti)) {
			if (taxBreakDown.getTaxType().equals(TaxType.RETENTION)) {
				percent = taxBreakDown.getTaxPercent();
				break;
			}
		}
		return CommonUtil.round(percent);
	}

}
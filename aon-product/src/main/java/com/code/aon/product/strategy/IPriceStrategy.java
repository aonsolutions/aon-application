package com.code.aon.product.strategy;

import java.util.Date;
import java.util.List;

import com.code.aon.config.Tariff;
import com.code.aon.registry.ITariffable;
import com.code.aon.registry.ITaxInfo;

public interface IPriceStrategy {
	
	public double getUnitPurchasePrice(ICalculable calc);
	
	public double getUnitPurchasePrice(ICalculable calc, Date date, ITariffable iTariffable);
	
	public double getUnitPurchasePrice(ICalculable calc, Date date, Tariff tariff);
	
	public double getUnitPrice(ICalculable calc);
	
	public double getUnitPrice(ICalculable calc, Date date, ITariffable iTariffable);
	
	public double getUnitPrice(ICalculable calc, Date date, Tariff tariff);
	
	public double getBasePrice(ICalculable calc);
	
	public double getBasePrice(ICalculable calc, boolean forceUnitPrice);
	
	public double getTaxableBase(ICalculableContainer icc);
	
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti, boolean ignoreTaxFree);
	
	public List<TaxBreakDown> getTaxBreakDowns(ICalculableContainer icc, ITaxInfo iti);

	public double getTotalVatQuota(ICalculableContainer icc, ITaxInfo iti);

	public double getTotalRetentionQuota(ICalculableContainer icc, ITaxInfo iti);

	public double getTotalPrice(ICalculableContainer icc, ITaxInfo iti);

}

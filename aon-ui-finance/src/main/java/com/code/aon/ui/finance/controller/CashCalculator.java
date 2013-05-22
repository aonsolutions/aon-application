package com.code.aon.ui.finance.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;

public class CashCalculator implements ICollectionProvider {

	private boolean showCalculatorWindow;
	private Integer[] amounts = new Integer[15];
	private double initialAmount;
	private ITransferObject recipient;
	private String headerLabel;
	private String footerLabel;

	public boolean isShowCalculatorWindow() {
		return showCalculatorWindow;
	}

	public void setShowCalculatorWindow(boolean showCalculatorWindow) {
		this.showCalculatorWindow = showCalculatorWindow;
	}

	public double getInitialAmount() {
		return initialAmount;
	}

	public void setInitialAmount(double initialAmount) {
		this.initialAmount = initialAmount;
	}

	public Integer[] getAmounts() {
		return amounts;
	}

	public void setAmounts(Integer[] amounts) {
		this.amounts = amounts;
	}

	public ITransferObject getRecipient() {
		return recipient;
	}

	public void setRecipient(ITransferObject recipient) {
		this.recipient = recipient;
	}

	public String getHeaderLabel() {
		return headerLabel;
	}

	public void setHeaderLabel(String headerLabel) {
		this.headerLabel = headerLabel;
	}

	public String getFooterLabel() {
		return footerLabel;
	}

	public void setFooterLabel(String footerLabel) {
		this.footerLabel = footerLabel;
	}

	public void initialize() {
		setAmounts(new Integer[15]);
		for (int i=0; i<amounts.length; amounts[i]=0,i++);
	}

	public Double getTotal() {
		Double total = 0.0;
		total += amounts[0] * 0.01;
		total += amounts[1] * 0.02;
		total += amounts[2] * 0.05;
		total += amounts[3] * 0.1;
		total += amounts[4] * 0.2;
		total += amounts[5] * 0.5;
		total += amounts[6] * 1;
		total += amounts[7] * 2;
		total += amounts[8] * 5;
		total += amounts[9] * 10;
		total += amounts[10] * 20;
		total += amounts[11] * 50;
		total += amounts[12] * 100;
		total += amounts[13] * 200;
		total += amounts[14] * 500;
		return total;
	}


	public Collection<CashCountUnit> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
	}

	public Collection<CashCountUnit> getCollection() {
		List<CashCountUnit> l = new LinkedList<CashCountUnit>();
		l.add(new CashCountUnit("0.01", amounts[0]));
		l.add(new CashCountUnit("0.02", amounts[1]));
		l.add(new CashCountUnit("0.05", amounts[2]));
		l.add(new CashCountUnit("0.10", amounts[3]));
		l.add(new CashCountUnit("0.20", amounts[4]));
		l.add(new CashCountUnit("0.50", amounts[5]));
		l.add(new CashCountUnit("1", amounts[6]));
		l.add(new CashCountUnit("2", amounts[7]));
		l.add(new CashCountUnit("5", amounts[8]));
		l.add(new CashCountUnit("10", amounts[9]));
		l.add(new CashCountUnit("20", amounts[10]));
		l.add(new CashCountUnit("50", amounts[11]));
		l.add(new CashCountUnit("100", amounts[12]));
		l.add(new CashCountUnit("200", amounts[13]));
		l.add(new CashCountUnit("500", amounts[14]));
		return l;
	}

	public class CashCountUnit {
		private String unit;
		private Integer amount;
		
		public String getUnit() {
			return unit;
		}

		public void setUnit(String unit) {
			this.unit = unit;
		}

		public Integer getAmount() {
			return amount;
		}

		public void setAmount(Integer amount) {
			this.amount = amount;
		}

		public CashCountUnit (String unit, Integer amount) {
			this.unit = unit;
			this.amount = amount;
		}

	}

}

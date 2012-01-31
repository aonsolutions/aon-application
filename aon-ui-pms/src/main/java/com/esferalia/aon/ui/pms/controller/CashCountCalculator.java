package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;



public class CashCountCalculator implements ICollectionProvider {
	
	private boolean showCalculatorWindow;
	private int[] amounts = new int[15];
	private Double cashAmount;
	
	public Double getCalcTotal() {
		Double calcTotal = 0.0;
		calcTotal += amounts[0]*0.01;
		calcTotal += amounts[1]*0.02;
		calcTotal += amounts[2]*0.05;
		calcTotal += amounts[3]*0.1;
		calcTotal += amounts[4]*0.2;
		calcTotal += amounts[5]*0.5;
		calcTotal += amounts[6]*1;
		calcTotal += amounts[7]*2;
		calcTotal += amounts[8]*5;
		calcTotal += amounts[9]*10;
		calcTotal += amounts[10]*20;
		calcTotal += amounts[11]*50;
		calcTotal += amounts[12]*100;
		calcTotal += amounts[13]*200;
		calcTotal += amounts[14]*500;
		return calcTotal;
	}
	public Double getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(Double cashAmount) {
		this.cashAmount = cashAmount;
	}
	public boolean isShowCalculatorWindow() {
		return showCalculatorWindow;
	}
	public void setShowCalculatorWindow(boolean showCalculatorWindow) {
		this.showCalculatorWindow = showCalculatorWindow;
	}
	public int[] getAmounts() {
		return amounts;
	}
	public void setAmounts(int[] amounts) {
		this.amounts = amounts;
	}
	
	/**
	 * Get a collection that contains current <code>ITransferObject</code>
	 * associated to controller. To use in reports.
	 * 
	 * @return Collection
	 */
	public Collection<CashCountUnit> getCollection() {
		List<CashCountUnit> l = new LinkedList<CashCountUnit>();
		
//		l.add(new CashCountUnit(unit, amount));
		l.add(new CashCountUnit(0.01, amounts[0]));
		l.add(new CashCountUnit(0.02, amounts[1]));
		l.add(new CashCountUnit(0.05, amounts[2]));
		l.add(new CashCountUnit(0.1, amounts[3]));
		l.add(new CashCountUnit(0.2, amounts[4]));
		l.add(new CashCountUnit(0.5, amounts[5]));
		l.add(new CashCountUnit(1.0, amounts[6]));
		l.add(new CashCountUnit(2.0, amounts[7]));
		l.add(new CashCountUnit(5.0, amounts[8]));
		l.add(new CashCountUnit(10.0, amounts[9]));
		l.add(new CashCountUnit(20.0, amounts[10]));
		l.add(new CashCountUnit(50.0, amounts[11]));
		l.add(new CashCountUnit(100.0, amounts[12]));
		l.add(new CashCountUnit(200.0, amounts[13]));
		l.add(new CashCountUnit(500.0, amounts[14]));
		
		
		
		
//		if (this.getTo() != null) {
//			List<ITransferObject> l = new LinkedList<ITransferObject>();
//			l.add(getTo());
//			return l;
//		}
		return l;
	}
	
	/**
	 * Get a collection that contains current <code>ITransferObject</code>
	 * associated to controller. To use in reports.
	 * 
	 * @return Collection
	 * @throws ManagerBeanException
	 */
	public Collection<CashCountUnit> getCollection(boolean forceRefresh) throws ManagerBeanException {
		return this.getCollection();
//		if (!forceRefresh) {
//		}
//		if (this.getTo() != null) {
//			List<ITransferObject> l = new LinkedList<ITransferObject>();
//			ITransferObject refreshed = getManagerBean().get(this.savedToId);
//			l.add(refreshed);
//			return l;
//		}
//		return null;
	}

//	/**
//	 * Get a collection that contains current <code>ITransferObject</code>
//	 * associated to controller. To use in reports.
//	 * 
//	 * @return Collection
//	 * @throws ManagerBeanException
//	 */
//	public Collection<ITransferObject> getCollection(boolean forceRefresh) throws ManagerBeanException {
//		if (!forceRefresh) {
//			return this.getCollection();
//		}
//		if (this.getTo() != null) {
//			List<ITransferObject> l = new LinkedList<ITransferObject>();
//			ITransferObject refreshed = getManagerBean().get(this.savedToId);
//			l.add(refreshed);
//			return l;
//		}
//		return null;
//	}
	
	public class CashCountUnit {
		private Double unit;
		private int amount;
		public CashCountUnit(Double unit, int amount){
			this.unit = unit;
			this.amount = amount;
			
		}
		public Double getUnit() {
			return unit;
		}
		public void setUnit(Double unit) {
			this.unit = unit;
		}
		public int getAmount() {
			return amount;
		}
		public void setAmount(int amount) {
			this.amount = amount;
		}
	}
			
}

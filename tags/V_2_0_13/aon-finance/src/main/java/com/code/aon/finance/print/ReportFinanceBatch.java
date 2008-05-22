package com.code.aon.finance.print;

import com.code.aon.common.ITransferObject;
import com.code.aon.finance.FinanceBatch;

public class ReportFinanceBatch implements ITransferObject {

	private FinanceBatch financeBatch;
	
	private Double total;

	public FinanceBatch getFinanceBatch() {
		return financeBatch;
	}

	public void setFinanceBatch(FinanceBatch batch) {
		financeBatch = batch;
	}

	public Double getTotal() {
		return total;
	}

	public void setTotal(Double total) {
		this.total = total;
	}
}
